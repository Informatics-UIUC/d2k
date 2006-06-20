package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class MFMToIntArray extends ComputeModule {

  public String getModuleName() {
    return "MFMToIntArray";
  }


  public String getModuleInfo() {
    return "This module takes an MFM and kicks out a Integer array " +
        "whose elements are the int casts of the doubles. This is " +
        "because I don't know how to write a reader, but I have an MFM reader..." +
        "oh, and it should be 2-dimensional... and it shouldn't be obscenely large " +
        "since int arrays are indexed by integers...";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "IntArray";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "IntArray";
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

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);

    int RowSizeX = (int)X.getDimensions()[0];
    int ColSizeX = (int)X.getDimensions()[1];

    int[][] NewArray = new int[RowSizeX][ColSizeX];

    for (int RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
      for (int ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
        NewArray[RowIndex][ColIndex] = (int)X.getValue(RowIndex,ColIndex);
      }
    }



    this.pushOutput(NewArray, 0);
  }

}


