package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule


class EX extends MultiIOModule {

    val io = IO(new Bundle {
        val PC              = Input(UInt(32.W))
        val imm             = Input(UInt(32.W))
        val regA            = Input(UInt(32.W))
        val regB            = Input(UInt(32.W))
        val controlSignals  = Input((new Decoder).io)
        val PC_next         = Output(UInt(32.W))
        val ALUOut          = Output(UInt(32.W))
    })

    val alu = Module(new ALU)

    alu.io.aluOp := io.controlSignals.ALUOp

    when(io.controlSignals.op2Select === Op2Select.rs2){
        alu.io.op2 := io.regB
    }.elsewhen(io.controlSignals.op2Select === Op2Select.imm){
        alu.io.op2 := io.imm
    }.otherwise{
        alu.io.op2 := 0.U
    }

    when(io.controlSignals.op1Select === Op1Select.rs1){
        alu.io.op1 := io.regA
    }.elsewhen(io.controlSignals.op1Select === Op1Select.PC){
        io.PC_next := io.PC + io.imm
        alu.io.op1 := 0.U
    }.otherwise{
        alu.io.op1 := 0.U
    }

    io.ALUOut := alu.io.aluResult

}