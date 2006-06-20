package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class MFMToLongArray extends ComputeModule {

  public String getModuleName() {
    return "MFMToLongArray";
  }


  public String getModuleInfo() {
    return "This module takes an MFM and kicks out a Long array " +
        "whose elements are the long casts of the doubles. This is " +
        "because I don't know how to write a reader, but I have an MFM reader..." +
        "oh, and it should be 2-dimensional... and it shouldn't be obscenely large " +
        "since long arrays are indexed by integers...";
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
        return "LongArray";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "LongArray";
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

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);

    long RowSizeX = X.getDimensions()[0];
    long ColSizeX = X.getDimensions()[1];

    long[][] NewArray = new long[(int)RowSizeX][(int)ColSizeX];

    for (int RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
      for (int ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
        NewArray[RowIndex][ColIndex] = (long)X.getValue(RowIndex,ColIndex);
      }
    }



    this.pushOutput(NewArray, 0);
  }

}


