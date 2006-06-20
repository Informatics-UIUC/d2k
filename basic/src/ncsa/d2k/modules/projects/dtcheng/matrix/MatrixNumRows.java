
package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class MatrixNumRows
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixNumRows";
  }

  public String getModuleInfo() {
    return "This module returns the number of rows in the matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "NumberOfRows";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "NumberOfRows";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long"};
    return types;
  }

  public void doit() {
    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    this.pushOutput(new Long(X.getDimensions()[0]), 0);
  }

}
