package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class MatrixTranspose
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixTranspose";
  }

  public String getModuleInfo() {
    return "This module computes the transpose of the input matrix.  ";
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
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "TransposedMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "TransposedMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"};
    return types;
  }

  public void doit() throws Exception {

//    double[][] InputMatrix = (double[][])this.pullInput(0);
    MultiFormatMatrix InputMatrix = (MultiFormatMatrix)this.pullInput(0);

    long nRows = InputMatrix.getDimensions()[0];
    long nCols = InputMatrix.getDimensions()[1];

//    double[][] TransposedMatrix = new double[NumCols][NumRows];
    MultiFormatMatrix transposedMatrix = new MultiFormatMatrix(InputMatrix.dataFormat, new long[] {nCols, nRows});
    for (long rowIndex = 0; rowIndex < nRows; rowIndex++) {
      for (long colIndex = 0; colIndex < nCols; colIndex++) {
        transposedMatrix.setValue(colIndex,rowIndex, InputMatrix.getValue(rowIndex,colIndex));
//        [ColIndex][RowIndex] = InputMatrix[RowIndex][ColIndex];
      }
    }

    this.pushOutput(transposedMatrix, 0);
  }

}
