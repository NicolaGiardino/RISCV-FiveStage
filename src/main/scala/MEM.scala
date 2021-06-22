package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class MemoryFetch() extends MultiIOModule {


  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val DMEMsetup      = Input(new DMEMsetupSignals)
      val DMEMpeek       = Output(UInt(32.W))

      val testUpdates    = Output(new MemUpdates)
    })

  val io = IO(
    new Bundle {
      val writeEnable   = Input(UInt(1.W))
      val readEnable    = Input(UInt(1.W))
      val dataAddress   = Input(UInt(32.W))
      val dataIn        = Input(UInt(32.W))
      val readData      = Output(UInt(32.W))
    })


  val DMEM = Module(new DMEM)


  /**
    * Setup. You should not change this code
    */
  DMEM.testHarness.setup  := testHarness.DMEMsetup
  testHarness.DMEMpeek    := DMEM.io.dataOut
  testHarness.testUpdates := DMEM.testHarness.testUpdates


  /**
    * Your code here.
    */
  DMEM.io.dataIn          := io.dataIn
  DMEM.io.dataAddress     := io.dataAddress
  DMEM.io.writeEnable     := io.writeEnable
  DMEM.io.readEnable      := io.readEnable

  io.readData             := DMEM.io.dataOut
}
