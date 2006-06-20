package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class CollapseDoubleArrayStreamToAveraged extends ComputeModule {

  private int MaxStreamLength = 100;

  public void setMaxStreamLength(int value) {
    this.MaxStreamLength = value;
  }

  public int getMaxStreamLength() {
    return this.MaxStreamLength;
  }

  private int NumObjectsInStream = 10;

  public void setNumObjectsInStream(int value) {
    this.NumObjectsInStream = value;
  }

  public int getNumObjectsInStream() {
    return this.NumObjectsInStream;
  }

  public String getModuleName() {
    return "CollapseDoubleArrayStreamToAveraged";
  }

  public String getModuleInfo() {
    return "CollapseDoubleArrayStreamToAveraged";
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
    String[] types = { "[D", "[D", };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Means";
    case 1:
      return "Sums";
    default:
      return "No such output";
    }
  }

  int Count;

  public void beginExecution() {
    Count = 0;
  }

  /*
   * public boolean isReady() { if ((Count == 0) && (this.getFlags()[0] > 0)) {
   * return true; } else { if ((Count != 0) && (Count < NumTimes)) { return
   * true; } } return false; }
   */

  double[] data;

  int length;

  double[][] AllData = new double[MaxStreamLength][];

  int NumTimes;

  int NumFrames;

  public void doit() throws Exception {

    data = (double[]) this.pullInput(0);

    if (data != null) {
      length = data.length;
      AllData[Count] = data;
      Count++;
    }

    if (data == null) {

      if (Count == 0) {
        this.pushOutput(null, 0);
        this.pushOutput(null, 1);
        return;
      }

      double[] DataSum = new double[length];
      for (int c = 0; c < Count; c++) {
        for (int i = 0; i < length; i++) {
          DataSum[i] += AllData[c][i];
        }
      }

      double[] DataMean = new double[length];
      for (int i = 0; i < length; i++) {
        DataMean[i] = DataSum[i] / Count;
      }

      double[] DataVarianceSum = new double[length];
      for (int c = 0; c < Count; c++) {
        for (int i = 0; i < length; i++) {
          double difference = AllData[c][i] - DataMean[i];
          DataVarianceSum[i] += difference * difference;
        }
      }

      double[] DataVariance = new double[length];
      for (int i = 0; i < length; i++) {
        DataVariance[i] += DataVarianceSum[i] / Count;
      }

      this.pushOutput(DataMean, 0);
      this.pushOutput(DataVariance, 1);

      beginExecution();

      return;
    }

  }
}