package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class GetIntegerFrom1DIArray extends ComputeModule {

  private int ElementNumber = 1;

  public void setElementNumber(int value) {
    this.ElementNumber = value;
  }

  public int getElementNumber() {
    return this.ElementNumber;
  }

  public String getModuleInfo() {
    return "GetIntegerFrom1DIArray";
  }

  public String getModuleName() {
    return "GetIntegerFrom1DIArray";
  }

  public String[] getInputTypes() {
    String[] types = { "[I" };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = { "java.lang.Integer" };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "1DIntArray";
    default:
      return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "1DIntArray";
    default:
      return "No such input";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Integer";
    default:
      return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Integer";
    default:
      return "No such output";
    }
  }

  public void doit() {
    
    int[] data = (int[]) this.pullInput(0);

    this.pushOutput(new Integer(data[ElementNumber - 1]), 0);
  }
}