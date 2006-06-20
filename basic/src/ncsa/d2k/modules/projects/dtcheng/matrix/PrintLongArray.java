package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class PrintLongArray
    extends OutputModule {

  private boolean Disable = false;
  public void setDisable(boolean value) {
    this.Disable = value;
  }

  public boolean getDisable() {
    return this.Disable;
  }

  private String Label = "";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  public String getModuleName() {
    return "PrintLongArray";
  }

  public String getModuleInfo() {
    return "This module prints a 2-d long array.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "2DLongArray";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "2DLongArray";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "2DLongArray";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "2DLongArray";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long",
    };
    return types;
  }

  public void doit() throws Exception {

    long[][] LongArray = (long[][])this.pullInput(0);
    synchronized (System.out) {

      for (int PrintRow = 0; PrintRow < LongArray.length; PrintRow++) {
        for (int PrintCol = 0; PrintCol < LongArray[0].length; PrintCol++) {
          System.out.println(Label + "[" + PrintRow + "][" +
                             PrintCol + "] = " + LongArray[PrintRow][PrintCol]);
        }
      }

    }

    this.pushOutput(LongArray, 0);
  }

}
