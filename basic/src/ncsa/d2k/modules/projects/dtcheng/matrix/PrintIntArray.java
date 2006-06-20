package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class PrintIntArray
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
    return "PrintIntArray";
  }

  public String getModuleInfo() {
    return "This module prints a 2-d int array.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "2DIntArray";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "2DIntArray";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[I",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "2DIntArray";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "2DIntArray";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[I",
    };
    return types;
  }

  public void doit() throws Exception {

//    int[][] IntArray = (int[][])((int[][])this.pullInput(0)).clone();
    int[][] IntArray = (int[][])this.pullInput(0);
    synchronized (System.out) {

      for (int PrintRow = 0; PrintRow < IntArray.length; PrintRow++) {
        for (int PrintCol = 0; PrintCol < IntArray[0].length; PrintCol++) {
          System.out.println(Label + "[" + PrintRow + "][" +
                             PrintCol + "] = " + IntArray[PrintRow][PrintCol]);
        }
      }

    }

    this.pushOutput(IntArray, 0);
  }

}
