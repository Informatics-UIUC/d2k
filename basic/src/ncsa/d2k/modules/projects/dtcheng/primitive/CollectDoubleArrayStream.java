package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class CollectDoubleArrayStream extends ComputeModule {

  private long NumStreamsToCollect = 1;

  public void setNumStreamsToCollect(long value) {
    this.NumStreamsToCollect = value;
  }

  public long getNumStreamsToCollect() {
    return this.NumStreamsToCollect;
  }

  public String getModuleInfo() {
    return "This modules collects a stream of 1d double arrays and forms a 2d "
        + "representing the stream when the null (endof stream) is encountered.";
  }

  public String getModuleName() {
    return "CollectDoubleArrayStream";
  }

  public String[] getInputTypes() {
    String[] types = { "[D" };
    return types;
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "1D Double Array";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "1D Double Array";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[[D" };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "2D Double Array";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "2D Double Array";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  int StreamCount;

  int count = 0;

  int maxCount = 1;

  double[][] DoubleArrays;

  public void beginExecution() {
    StreamCount = 0;
    count = 0;
    DoubleArrays = new double[maxCount][];
  }

  public void endExecution() {
    DoubleArrays = null;
  }

  public void expandArray() {
    maxCount = maxCount * 2;
    double[][] newDoubleArrays = new double[maxCount][];
    for (int i = 0; i < count; i++) {
      newDoubleArrays[i] = DoubleArrays[i];
    }
    DoubleArrays = newDoubleArrays;
  }

  public void resetArray() {
    count = 0;
    maxCount = 1;
    StreamCount = 0;
    DoubleArrays = new double[maxCount][];
  }

  public void doit() {

    double[] object = (double[]) this.pullInput(0);

    if (object == null) {
      StreamCount++;

      if (StreamCount == NumStreamsToCollect) {
        double[][] newDoubleArrays = new double[count][0];
        for (int i = 0; i < count; i++) {
          newDoubleArrays[i] = DoubleArrays[i];
        }

        this.pushOutput(newDoubleArrays, 0);

        resetArray();
      
      }
      
      return;
    }

    if (count == maxCount) {
      expandArray();
    }

    DoubleArrays[count++] = object;
  }
}