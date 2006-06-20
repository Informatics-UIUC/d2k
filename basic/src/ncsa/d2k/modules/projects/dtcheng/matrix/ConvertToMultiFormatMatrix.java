package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class ConvertToMultiFormatMatrix
    extends ComputeModule {

  public String getModuleName() {
    return "ConvertToMultiFormatMatrix";
  }

  public String getModuleInfo() {
    return "This module creates an MFM copy of a [[D matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "DoubleMatrix";
      case 1:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "DoubleMatrix";
      case 1:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

  public void doit() throws Exception {

// not really using "long"'s because a double array can only be numbered up to int...
    double [][] X = (double [][])this.pullInput(0);
//    int[] dimensionsTemp =  {X.length, X[0].length};
    int RowSizeX = X.length;
    int ColSizeX = X[0].length;
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    MultiFormatMatrix XCopy = new MultiFormatMatrix(FormatIndex, new long[] {RowSizeX,ColSizeX});

    for (int d1 = 0; d1 < RowSizeX; d1++) {
      for (int d2 = 0; d2 < ColSizeX; d2++) {
        XCopy.setValue(d1, d2, X[d1][d2]);
      }
    }

    this.pushOutput(XCopy, 0);
  }

}
