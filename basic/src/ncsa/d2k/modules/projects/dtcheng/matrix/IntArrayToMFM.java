package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class IntArrayToMFM extends ComputeModule {

  public String getModuleName() {
    return "IntArrayToMFM";
  }


  public String getModuleInfo() {
    return "This module takes an Integer array and kicks out an MFM.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "IntArray";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "IntArray";
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

    int[][] X = (int[][])this.pullInput(0);

    long rowSizeX = X.length;
    long colSizeX = X[0].length;
  	
    MultiFormatMatrix NewArray = new MultiFormatMatrix(1,new long[] {rowSizeX,colSizeX});



    for (long rowIndex = 0; rowIndex < rowSizeX; rowIndex++) {
      for (long colIndex = 0; colIndex < colSizeX; colIndex++) {
        NewArray.setValue(rowIndex,colIndex,X[(int)rowIndex][(int)colIndex]);
      }
    }



    this.pushOutput(NewArray, 0);
  }

}


