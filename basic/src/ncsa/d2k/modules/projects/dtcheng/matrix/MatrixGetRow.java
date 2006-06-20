package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class MatrixGetRow
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGetRow";
  }

  public String getModuleInfo() {
    return "This module pulls out a particular row from a matrix (numbering starts at 0).  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "RowIndex";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "RowIndex";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Long",
        "java.lang.Long",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "RowMatix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "RowMatix";
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

//    double[][] X = (double[][])this.pullInput(0);
    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    long RowIndex = ((Long)this.pullInput(1)).longValue();
    long NumberOfElementsThreshold = ((Long)this.pullInput(2)).longValue();

    long RowSizeX = X.getDimensions()[0];
    long ColSizeX = X.getDimensions()[1];

    long NumElements = ColSizeX;
    int FormatIndex = -1;
    if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

    MultiFormatMatrix RowMatrix = new MultiFormatMatrix(FormatIndex, new long[] {RowSizeX,1});
//    int NumRows = X.length;
//    int NumCols = X[0].length;
//    double[][] ColumnMatrix = new double[NumRows][1];
//    ResultMatrix = null;N

    for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
//        ColumnMatrix[RowIndex][0] = X[RowIndex][ColumnIndex];
        RowMatrix.setValue(0,ColIndex,X.getValue(RowIndex, ColIndex));
    }

    this.pushOutput(RowMatrix, 0);
  }

}
