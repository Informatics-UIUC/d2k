package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class Get1DSArrayFrom2DSArray extends ComputeModule {

  private int ColumnNumber = 1;

  public void setColumnNumber(int value) {
    this.ColumnNumber = value;
  }

  public int getColumnNumber() {
    return this.ColumnNumber;
  }

  public String getModuleInfo() {
    return "Get1DSArrayFrom2DSArray";
  }

  public String getModuleName() {
    return "Get1DSArrayFrom2DSArray";
  }

  public String[] getInputTypes() {
    String[] types = { "[[Ljava.lang.String;" };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = { "[Ljava.lang.String;" };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "2DStringArray";
    default:
      return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "2DStringArray";
    default:
      return "No such input";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "1DStringArray";
    default:
      return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "1DStringArray";
    default:
      return "No such output";
    }
  }

  public void doit() {
    String[][] String2DArray = (String[][]) this.pullInput(0);

    this.pushOutput(String2DArray[ColumnNumber - 1], 0);
  }
}