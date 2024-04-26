// This file is where all of the CPU components are assembled into the whole CPU

package dinocpu

import chisel3._
import chisel3.util._
import dinocpu.components._

/**
 * The main CPU definition that hooks up all of the other components.
 *
 * For more information, see section 4.4 of Patterson and Hennessy
 * This follows figure 4.21
 */
class SingleCycleCPU(implicit val conf: CPUConfig) extends BaseCPU {
  // All of the structures required
  val pc              = dontTouch(RegInit(0.U(64.W)))
  val control         = Module(new Control())
  val registers       = Module(new RegisterFile())
  val aluControl      = Module(new ALUControl())
  val alu             = Module(new ALU())
  val immGen          = Module(new ImmediateGenerator())
  val controlTransfer = Module(new ControlTransferUnit())
  val (cycleCount, _) = Counter(true.B, 1 << 30)

  // these fields are not required for R-type instructions
  immGen.io := DontCare
  controlTransfer.io := DontCare
  io.dmem <> DontCare

  //FETCH
  io.imem.address := pc
  io.imem.valid := true.B

  val instruction = Wire(UInt(32.W))
  when ((pc % 8.U) === 4.U) {
    instruction := io.imem.instruction(63, 32)
  } .otherwise {
    instruction := io.imem.instruction(31, 0)
  }

  //Your code goes here

  // increment PC to move the processor to the next instruction
  // for multi-cycle instructions
  val nextPC = pc + 4.U
  pc := nextPC

  control.io.opcode := instruction(6, 0)

  // create wire to connect aluop from control output to aluControl input
  val aluop = Wire(UInt(3.W))
  aluop := control.io.aluop

  registers.io.readreg1 := instruction(19, 15)
  registers.io.readreg2 := instruction(24, 20)
  registers.io.writereg := instruction(11, 7)
  // set the write enable bit to write the result back to destination reg
  registers.io.wen := 1.U
  
  // connect the other end of the aluop wire
  aluControl.io.aluop := aluop
  aluControl.io.funct7 := instruction(31, 25)
  aluControl.io.funct3 := instruction(14, 12)
  
  // connect operation from aluControl output to alu input
  val operation = Wire(UInt(5.W))
  operation := aluControl.io.operation
  alu.io.operation := operation

  // get operand 1 and operand 2 from registers
  val regData1 = Wire(UInt(64.W))
  val regData2 = Wire(UInt(64.W))
  regData1 := registers.io.readdata1
  regData2 := registers.io.readdata2

  alu.io.operand1 := regData1
  alu.io.operand2 := regData2

  // connect result from alu output to register write data input
  val result = Wire(UInt(64.W))
  result := alu.io.result

  // register x0 should always hold the value 0
  // so we only write the result when the destination reg is not 0
  when (registers.io.writereg === 0.U) {
    registers.io.writedata := 0.U
  }.otherwise {
    registers.io.writedata := result
  }

}

/*
 * Object to make it easier to print information about the CPU
 */
object SingleCycleCPUInfo {
  def getModules(): List[String] = {
    List(
      "dmem",
      "imem",
      "control",
      "registers",
      "csr",
      "aluControl",
      "alu",
      "immGen",
      "controlTransfer"
    )
  }
}
