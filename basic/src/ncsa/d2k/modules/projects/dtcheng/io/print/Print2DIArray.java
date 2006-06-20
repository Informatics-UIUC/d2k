package ncsa.d2k.modules.projects.dtcheng.io.print;

import ncsa.d2k.core.modules.*;

public class Print2DIArray extends OutputModule {

  private boolean CSV = false;

  public void setCSV(boolean value) {
    this.CSV = value;
  }

  public boolean getCSV() {
    return this.CSV;
  }

  private boolean PrintDimensions = true;

  public void setPrintDimensions(boolean value) {
    this.PrintDimensions = value;
  }

  public boolean getPrintDimensions() {
    return this.PrintDimensions;
  }

  private boolean PrintValues = true;

  public void setPrintValues(boolean value) {
    this.PrintValues = value;
  }

  public boolean getPrintValues() {
    return this.PrintValues;
  }

  private boolean Disable = false;

  public void setDisable(boolean value) {
    this.Disable = value;
  }

  public boolean getDisable() {
    return this.Disable;
  }

  private String Label = "label = ";

  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  public String getModuleName() {
    return "Print2DIArray";
  }

  public String getModuleInfo() {
    return "This module prints a 2D int array.  ";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Int2DArray";
    default:
      return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Int2DArray";
    default:
      return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "[[I" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Int2DArray";
    default:
      return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Int2DArray";
    default:
      return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[[D" };
    return types;
  }

  public void doit() {

    int[][] int2DArray = (int[][]) this.pullInput(0);

    if (int2DArray == null) {
      this.pushOutput(null, 0);
      return;
    }
    int dim1Size = int2DArray.length;
    synchronized (System.out) {

      if (PrintDimensions) {
        int d1 = int2DArray.length;
        int d2 = int2DArray[0].length;

        System.out.println(Label + " size = [" + d1 + "][" + d2 + "]");
      }

      if (PrintValues) {
        for (int d1 = 0; d1 < dim1Size; d1++) {
          
          int dim2Size = int2DArray[d1].length;

          for (int d2 = 0; d2 < dim2Size; d2++) {

            if (CSV) {
              if (d2 != 0)
                System.out.print(",");
              System.out.print(int2DArray[d1][d2]);
              if (d2 + 1 == dim2Size)
                System.out.println();
            } else {
              System.out.println(Label + "[" + (d1+1) + "][" + (d2+1) + "] = " + int2DArray[d1][d2]);
            }
          }
        }

      }
    }

    this.pushOutput(int2DArray, 0);
  }

}