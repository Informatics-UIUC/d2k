package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class FindMaxValueIndices extends ComputeModule {

  private int NumMaxValuesToFind = 10;

  public void setNumMaxValuesToFind(int value) {
    this.NumMaxValuesToFind = value;
  }

  public int getNumMaxValuesToFind() {
    return this.NumMaxValuesToFind;
  }

  public String getModuleInfo() {
    return "FindMaxValueIndices";
  }

  public String getModuleName() {
    return "FindMaxValueIndices";
  }

  public String[] getInputTypes() {
    String[] types = { "[D" };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = { "[I", "[D" };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "ValueArray";
    default:
      return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "ValueArray";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Index Array";
    case 1:
      return "Probability Array";
    default:
      return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Index Array";
    case 1:
      return "Probability Array";
    default:
      return "No such output";
    }
  }

  public void doit() {

    double[] data = (double[]) this.pullInput(0);

    if (data == null) {
      this.pushOutput(null, 0);
      return;
    }

    int count = data.length;

    double[] OutputValues = new double[NumMaxValuesToFind];

    int[] OutputIndices = new int[NumMaxValuesToFind];

    boolean[] FoundAlready = new boolean[data.length];

    
    for (int r = 0; r < NumMaxValuesToFind; r++) {

      double MaxValue = Double.NEGATIVE_INFINITY;
      int MaxValueIndex = -1;

      for (int i = 0; i < count; i++) {
        if (!FoundAlready[i]) {
          if (data[i] > MaxValue) {
            MaxValue = data[i];
            MaxValueIndex = i;
          }
        }
      }
      OutputIndices[r] = MaxValueIndex;
      OutputValues[r] = MaxValue;
      FoundAlready[MaxValueIndex] = true;
    }

    this.pushOutput(OutputIndices, 0);
    this.pushOutput(OutputValues, 1);
  }
}