package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule


/**
  * This module is already done. Have one on me
  */
class DMEM extends MultiIOModule {


  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val setup = Input(new DMEMsetupSignals)
      val testUpdates = Output(new MemUpdates)
    })


  val io = IO(
    new Bundle {
      val writeEnable = Input(UInt(1.W))
      val dataIn      = Input(UInt(32.W))
      val dataAddress = Input(UInt(12.W))
      val readEnable  = Input(UInt(1.W))
      val dataOut     = Output(UInt(32.W))
    })

  val data = SyncReadMem(4096, UInt(32.W))

  val addressSource = Wire(UInt(32.W))
  val dataSource = Wire(UInt(32.W))
  val writeEnableSource = Wire(UInt(1.W))

  // For loading data
  when(testHarness.setup.setup){
    addressSource     := testHarness.setup.dataAddress
    dataSource        := testHarness.setup.dataIn
    writeEnableSource := testHarness.setup.writeEnable
  }.otherwise {
    addressSource     := io.dataAddress
    dataSource        := io.dataIn
    writeEnableSource := io.writeEnable
    readEnableSource  := io.readEnable
  }

  testHarness.testUpdates.writeEnable  := writeEnableSource
  testHarness.testUpdates.writeData    := dataSource
  testHarness.testUpdates.writeAddress := addressSource

  when(readEnableSource == 1.U){
    io.dataOut := data(addressSource)
  }
  when(writeEnableSource == 1.U){
    data(addressSource) := dataSource
  }
}
