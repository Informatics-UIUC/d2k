package ncsa.d2k.modules.projects.dtcheng.streams;

import ncsa.d2k.core.modules.*;

public class DoubleArrayToSubArrayStream extends ComputeModule {

  private int FrameSize = 44100;

  public void setFrameSize(int value) {
    this.FrameSize = value;
  }

  public int getFrameSize() {
    return this.FrameSize;
  }

  private int IncrementSize = 44100;

  public void setIncrementSize(int value) {
    this.IncrementSize = value;
  }

  public int getIncrementSize() {
    return this.IncrementSize;
  }

  public String getModuleName() {
    return "DoubleArrayToSubArrayStream";
  }

  public String getModuleInfo() {
    return "DoubleArrayToSubArrayStream";
  }

  public String getInputName(int i) {
    switch (i) {
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] types = { "[D" };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "DoubleArrayStream";
    default:
      return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "DoubleArrayStream";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] types = { "[D", };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "DoubleArrayStream";
    default:
      return "No such output";
    }
  }

  int Count;

  public void beginExecution() {
    Count = 0;
  }

  public boolean isReady() {
 
    if ((Count == 0) && (this.getFlags()[0] > 0)) {
      return true;
    }

    if ((Count != 0) && (Count < NumTimes)) {
      return true;
    }

    return false;
  }

  double[] data;

  int TotalLength;

  int NumTimes;

  int NumFrames;

  public void doit() throws Exception {

    if (Count == 0) {

      data = (double[]) this.pullInput(0);
      
      if (data == null) {

        this.pushOutput(null, 0);
        beginExecution();

        return;
      }

      TotalLength = data.length;
      NumFrames = (TotalLength - FrameSize) / IncrementSize + 1;
      NumTimes = NumFrames;

        //System.out.println("TotalLength = " + TotalLength);
        //System.out.println("NumFrames   = " + NumFrames);
        //System.out.println("NumTimes       = " + NumTimes);

    }

    if (Count < NumTimes) {

      double[] FrameData = new double[FrameSize];

      for (int i = 0; i < FrameSize; i++) {
        FrameData[i] = data[IncrementSize * Count + i];
      }

      this.pushOutput(FrameData, 0);
      Count++;

      if (Count == NumTimes) {
        this.pushOutput(null, 0);
        beginExecution();
      }
    }

  }
}