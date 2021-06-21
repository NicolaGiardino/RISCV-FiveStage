package FiveStage
import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule

class IFBarrier extends MultiIOModule {

    val io = IO(
        new Bundle{
            val PC_in = Input(UInt(32.W))
            val instruction_in = Input(new Instruction)
            val PC_out = Output(UInt(32.W))
            val instruction_out = Output(new Instruction)
        }
    )

    val PC = RegInit(0.U(32.W))

    io.PC_out := PC
    PC := io.PC_in

    io.instruction_out := io.instruction_in

}

class IDBarrier extends MultiIOModule {

    val io = IO(
        new Bundle{
            val PC_in = Input(UInt(32.W))
            val instruction_in = Input(UInt(32.W))
            val imm_in = Input(UInt(32.W))
            val regA_in = Input(UInt(32.W))
            val regB_in = Input(UInt(32.W))
            val controlSignals_in = Input((new Decoder).io)
            val PC_out = Output(UInt(32.W))
            val instruction_out = Output(UInt(32.W))
            val imm_out = Output(UInt(32.W))
            val regA_out = Output(UInt(32.W))
            val regB_out = Output(UInt(32.W))
            val controlSignals_out = Output((new Decoder).io)
        }
    )

    val PC             = RegInit(0.U(32.W))
    val instruction    = RegInit(0.U(32.W))
    val imm            = RegInit(0.U(32.W))
    val regA           = RegInit(0.U(32.W))
    val regB           = RegInit(0.U(32.W))
    val controlSignals = Reg((new Decoder).io)

    io.PC_out := PC
    PC := io.PC_in
    
    io.imm_out  := imm
    imm         := io.imm_in

    io.regA_out := regA
    regA        := io.regA_in

    io.regB_out := regB
    regB        := io.regB_in

    io.instruction_out := instruction
    instruction        := io.instruction_in

    io.controlSignals_out := controlSignals
    controlSignals        := io.controlSignals_in

}

val EXBarrier extends MultiIOModule {

    val io = IO(new Bundle {
        val regB_in             = Input(UInt(32.W))
        val PC_in               = Input(UInt(32.W))
        val controlSignals_in   = Input((new Decoder).io)
        val ALUOut_in           = Input(UInt(32.W))
        val instruction_in      = Input(UInt(32.W))
        val regB_out            = Output(UInt(32.W))
        val PC_out              = Output(UInt(32.W))
        val controlSignals_out  = Output((new Decoder).io)
        val ALUOut_out          = Output(UInt(32.W))
        val instruction_out     = Output(UInt(32.W))
    })

    val regB            = RegInit(0.U(32.W))
    val PC              = RegInit(0.U(32.W))
    val controlSignals  = Reg((new Decoder).io)
    val ALUOut          = RegInit(0.U(32.W))
    val instruction     = RegInit(0.U(32.W))

    io.regB_out := regB
    regB        := io.regB_in

    io.instruction_out := instruction
    instruction        := io.instruction_in

    io.controlSignals_out := controlSignals
    controlSignals        := io.controlSignals_in

    io.PC_out := PC
    PC := io.PC_in

    io.ALUOut_out := ALUOut
    ALUOut        := io.ALUOut_in

}

class MEMBarrier extends MultiIOModule {

    val io = IO( new Bundle {
        val controlSignals_in   = Input((new Decoder).io)
        val ALUOut_in           = Input(UInt(32.W))     
        val readData_in         = Input(UInt(32.W))
        val instruction_in      = Input(UInt(32.W))
        val controlSignals_out  = Output((new Decoder).io)
        val ALUOut_out          = Output(UInt(32.W))     
        val readData_out        = Output(UInt(32.W))
        val instruction_out     = Output(UInt(32.W))
    })

    val controlSignals   = Reg((new Decoder).io)
    val ALUOut           = RegInit(0.U(32.W))
    val instruction      = RegInit(0.U(32.W))

    io.ALUOut_out := ALUOut
    ALUOut        := io.ALUOut_in

    io.readData_out := io.readData_in

    io.instruction_out := instruction
    instruction        := io.instruction_in

    io.controlSignals_out := controlSignals
    controlSignals        := io.controlSignals_in

}