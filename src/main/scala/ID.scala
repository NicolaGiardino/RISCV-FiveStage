package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule


class InstructionDecode extends MultiIOModule {

  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val registerSetup = Input(new RegisterSetupSignals)
      val registerPeek  = Output(UInt(32.W))

      val testUpdates   = Output(new RegisterUpdates)
    })


  val io = IO(
    new Bundle {
      val instruction     = Input(new Instruction)
      val WBDat           = Input(UInt(32.W))
      val writeEnable     = Input(Bool())
      val rd              = Input(UInt(5.W))
      val controlSignals  = Output((new Decoder).io)
      val regA            = Output(UInt(32.W))
      val regB            = Output(UInt(32.W))
      val imm             = Output(UInt(32.W))
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io


  /**
    * Setup. You should not change this code
    */
  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates


  /**
    * TODO: Your code here.
    */
  registers.io.readAddress1 := io.instruction.registerRs1
  registers.io.readAddress2 := io.instruction.registerRs2
  registers.io.writeEnable  := io.writeEnable
  registers.io.writeAddress := io.rd
  registers.io.writeData    := io.WBDat

  decoder.instruction       := io.instruction.asTypeOf(new Instruction)

  io.controlSignals         := decoder
  io.regA                   := registers.io.readData1
  io.regB                   := registers.io.readData2
  io.imm                    := io.instruction.imm(decoder.immType)

}
