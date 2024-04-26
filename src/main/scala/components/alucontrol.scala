// This file contains ALU control logic.

package dinocpu.components

import chisel3._
import chisel3.util._

/**
 * The ALU control unit
 *
 * Input:  aluop        Specifying the type of instruction using ALU
 *                          . 0 for none of the below
 *                          . 1 for 64-bit R-type
 *                          . 2 for 64-bit I-type
 *                          . 3 for 32-bit R-type
 *                          . 4 for 32-bit I-type
 *                          . 5 for non-arithmetic instruction types that uses ALU (auipc/jal/jarl/Load/Store)
 * Input:  funct7       The most significant bits of the instruction.
 * Input:  funct3       The middle three bits of the instruction (12-14).
 *
 * Output: operation    What we want the ALU to do.
 *
 * For more information, see Section 4.4 and A.5 of Patterson and Hennessy.
 * This is loosely based on figure 4.12
 */
class ALUControl extends Module {
  val io = IO(new Bundle {
    val aluop     = Input(UInt(3.W))
    val funct7    = Input(UInt(7.W))
    val funct3    = Input(UInt(3.W))

    val operation = Output(UInt(5.W))
  })

  //Your code goes here

  io.operation := 63.U

  when (io.aluop === 1.U) { // 64 bit R-type instructions
    when (io.funct7 === 0.U) {
      when (io.funct3 === 0.U) { // add
        io.operation := 1.U
      }
      .elsewhen (io.funct3 === 1.U) { // sll
        io.operation := 18.U
      }
      .elsewhen (io.funct3 === 2.U) { // slt
        io.operation := 22.U
      }
      .elsewhen (io.funct3 === 3.U) { // sltu
        io.operation := 23.U
      }
      .elsewhen (io.funct3 === 4.U) { // xor
        io.operation := 15.U
      }
      .elsewhen (io.funct3 === 5.U) { // srl
        io.operation := 20.U
      }
      .elsewhen (io.funct3 === 6.U) { // or
        io.operation := 14.U
      }
      .elsewhen (io.funct3 === 7.U) { // and
        io.operation := 13.U
      }
    }
    .elsewhen (io.funct7 === 32.U) {
      when (io.funct3 === 0.U) { // sub
        io.operation := 4.U
      }
      .elsewhen (io.funct3 === 5.U) { // sra
        io.operation := 16.U
      }
    }
    .elsewhen (io.funct7 === 1.U) {
      when (io.funct3 === 0.U) { // mul
        io.operation := 6.U
      }
      .elsewhen (io.funct3 === 1.U) { // mulh
        io.operation := 7.U
      }
      .elsewhen (io.funct3 === 2.U) { // mulhsu
        io.operation := 24.U
      }
      .elsewhen (io.funct3 === 3.U) { // mulhu
        io.operation := 8.U
      }
      .elsewhen (io.funct3 === 4.U) { // div
        io.operation := 11.U
      }
      .elsewhen (io.funct3 === 5.U) { // divu
        io.operation := 10.U
      }
      .elsewhen (io.funct3 === 6.U) { // rem
        io.operation := 28.U
      }
      .elsewhen (io.funct3 === 7.U) { // remu
        io.operation := 27.U
      }
    }
  }
  .elsewhen (io.aluop === 3.U) { // 32 bit R-type instructions
    when (io.funct7 === 0.U) {
      when (io.funct3 === 0.U) { // addw
        io.operation := 0.U
      }
      .elsewhen (io.funct3 === 1.U) { // sllw
        io.operation := 19.U
      }
      .elsewhen (io.funct3 === 5.U) { // srlw
        io.operation := 21.U
      }
    }
    .elsewhen (io.funct7 === 32.U) {
      when (io.funct3 === 0.U) { // subw
        io.operation := 2.U
      }
      .elsewhen (io.funct3 === 5.U) { // sraw
        io.operation := 17.U
      }
    }
    .elsewhen (io.funct7 === 1.U) {
      when (io.funct3 === 0.U) { // mulw
        io.operation := 5.U
      }
      .elsewhen (io.funct3 === 4.U) { // divw
        io.operation := 9.U
      }
      .elsewhen (io.funct3 === 5.U) { // divuw
        io.operation := 12.U
      }
      .elsewhen (io.funct3 === 6.U) { // remw
        io.operation := 26.U
      }
      .elsewhen (io.funct3 === 7.U) { // remuw
        io.operation := 25.U
      }
    }
  }

}
