package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class CPU extends MultiIOModule {

  val testHarness = IO(
    new Bundle {
      val setupSignals = Input(new SetupSignals)
      val testReadouts = Output(new TestReadouts)
      val regUpdates   = Output(new RegisterUpdates)
      val memUpdates   = Output(new MemUpdates)
      val currentPC    = Output(UInt(32.W))
    }
  )

  /**
    You need to create the classes for these yourself
    */
  val IFBarrier  = Module(new IFBarrier).io
  val IDBarrier  = Module(new IDBarrier).io
  val EXBarrier  = Module(new EXBarrier).io
  val MEMBarrier = Module(new MEMBarrier).io

  val ID  = Module(new InstructionDecode)
  val IF  = Module(new InstructionFetch)
  val EX  = Module(new Execute)
  val MEM = Module(new MemoryFetch)
  // val WB  = Module(new Execute) (You may not need this one?)


  /**
    * Setup. You should not change this code
    */
  IF.testHarness.IMEMsetup     := testHarness.setupSignals.IMEMsignals
  ID.testHarness.registerSetup := testHarness.setupSignals.registerSignals
  MEM.testHarness.DMEMsetup    := testHarness.setupSignals.DMEMsignals

  testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
  testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek

  /**
    spying stuff
    */
  testHarness.regUpdates := ID.testHarness.testUpdates
  testHarness.memUpdates := MEM.testHarness.testUpdates
  testHarness.currentPC  := IF.testHarness.PC


  /**
    TODO: Your code here
    */
  IF.io.PC_jump               := EXBarrier.PC_out
  IF.io.JorB                  := EXBarrier.controlSignals.controlSignals.branch || EXBarrier.io.controlSignals.controlSignals.jump
  IFBarrier.instruction_in    := IF.io.instruction
  IFBarrier.PC_in             := IF.io.PC

  ID.io.instruction           := IFBarrier.instruction_out
  IDBarrier.PC_in             := IFBarrier.PC_out
  IDBarrier.instruction_in    := IFBarrier.instruction_out
  IDBarrier.imm_in            := ID.io.imm
  IDBarrier.regA_in           := ID.io.regA
  IDBarrier.regB_in           := ID.io.regB
  IDBarrier.controlSignals_in := ID.controlSignals

  EX.io.PC                    := IDBarrier.PC_out
  EX.io.imm                   := IDBarrier.imm_out
  EX.io.regA                  := IDBarrier.regA_out
  EX.io.regB                  := IDBarrier.regB_out
  EX.io.controlSignals        := IDBarrier.controlSignals_out
  EX.io.regA                  := IDBarrier.regA_out
  EXBarrier.PC_in             := EX.io.PC_next
  when(EXBarrier.controlSignals_out.controlSignals.op2Select == Op2Select.regB){
    EXBarrier.regB_in         := IDBarrier.regB_out
  }.elsewhen{
    EXBarrier.regB_in         := IDBarrier.imm_out
  }.otherwise{
    EXBarrier.regB_in         := 0.U
  }
  EXBarrier.controlSignals_in := IDBarrier.controlSignals_out
  EXBarrier.ALUOut_in         := EX.io.ALUOut
  EXBarrier.instruction_in    := IDBarrier.instruction_out

  MEM.io.writeEnable            := EXBarrier.controlSignals_out.controlSignals.memWrite
  MEM.io.readEnable             := EXBarrier.controlSignals_out.controlSignals.memRead
  MEM.io.dataAddress            := EXBarrier.ALUOut_out
  MEM.io.dataIn                 := EXBarrier.regB_out
  MEMBarrier.controlSignals_in  := EXBarrier.controlSignals_out
  MEMBarrier.ALUOut_in          := EXBarrier.ALUOut_out
  MEMBarrier.readData_in        := MEM.io.readData
  MEMBarrier.instruction_in     := EXBarrier.instruction_out

  ID.io.WBDat                   := MEMBarrier.ALUOut_out
  ID.io.writeEnable             := MEMBarrier.controlSignals_out.controlSignals.regWrite
  ID.io.rd                      := MEMBarrier.instruction_out.registerRd

}
