package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class MatrixGenerateConstantMatrix
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGenerateConstantMatrix";
  }

  public String getModuleInfo() {
    return "This module generates a nxm constant matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumRows";
      case 1:
        return "NumCols";
      case 2:
        return "Constant";
      case 3:
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
      case 3:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
        "java.lang.Long",
        "java.lang.Double",
        "java.lang.Long",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ConstantMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ConstantMatrix";
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
    double Constant = ((Double)this.pullInput(2)).doubleValue();
    long NumberOfElementsThreshold = ((Long)this.pullInput(3)).longValue();

    long NumElements = NumRows * NumCols;
    int FormatIndex = 1;
    if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }


    MultiFormatMatrix ConstantMatrix = new MultiFormatMatrix(FormatIndex, new long [] {NumRows,NumCols});

    if (Constant != 0.0) {
      for (long i = 0; i < NumRows; i++) {
        for (long j = 0; j < NumCols; j++) {
          ConstantMatrix.setValue(i, j, Constant);
        }
      }
    }

    this.pushOutput(ConstantMatrix, 0);
  }

}
