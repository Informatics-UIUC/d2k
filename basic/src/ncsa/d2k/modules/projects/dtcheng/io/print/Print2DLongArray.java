package ncsa.d2k.modules.projects.dtcheng.io.print;

import ncsa.d2k.core.modules.*;

public class Print2DLongArray
    extends OutputModule {

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
    return "Print2DLongArray";
  }

  public String getModuleInfo() {
    return "This module prints a 2D long array.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Long2DArray";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Long2DArray";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[J"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Long2DArray";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Double2DArray";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[J"};
    return types;
  }

  public void doit() {

    long[][] long2DArray = (long[][])this.pullInput(0);
    
    if (long2DArray == null) {
      this.pushOutput(null, 0);
      return;
    }
    int dim1Size = long2DArray.length;
    synchronized (System.out)
    {

      if (PrintDimensions) {
        int d1 = long2DArray.length;
        int d2 = long2DArray[0].length;
        
        System.out.println(Label + " size = [" + d1 + "][" + d2 + "]");
      }
      
      if (PrintValues) {
      for (int d1 = 0; d1 < dim1Size; d1++) {
        int dim2Size = long2DArray[d1].length;
        for (int d2 = 0; d2 < dim2Size; d2++) {
          System.out.println(Label + "[" + (d1+1) + "][" + (d2+1) + "] = " + long2DArray[d1][d2]);
        }
      }
      }
    }

    this.pushOutput(long2DArray, 0);
  }

}