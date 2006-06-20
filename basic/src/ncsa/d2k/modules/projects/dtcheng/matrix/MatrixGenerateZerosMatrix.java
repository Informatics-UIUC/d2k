package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class MatrixGenerateZerosMatrix
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGenerateZerosMatrix";
  }

  public String getModuleInfo() {
    return "This module generates a nxm matrix of zeros.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumRows";
      case 1:
        return "NumCols";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NumRows";
      case 1:
        return "NumCols";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
        "java.lang.Long",
        "java.lang.Long",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ZerosMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ZerosMatrix";
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

    long NumRows = ((Long)this.pullInput(0)).longValue();
    long NumCols = ((Long)this.pullInput(1)).longValue();
    long NumberOfElementsThreshold = ((Long)this.pullInput(2)).longValue();

    long NumElements = NumRows * NumCols;
    int FormatIndex = 1;
    if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }


    MultiFormatMatrix ZerosMatrix = new MultiFormatMatrix(FormatIndex, new long [] {NumRows,NumCols});

/*      for (long i = 0; i < NumRows; i++) {
        for (long j = 0; j < NumCols; j++) {
          ZerosMatrix.setValue(i, j, 0);
        }
      }
*/
    this.pushOutput(ZerosMatrix, 0);
  }

}
