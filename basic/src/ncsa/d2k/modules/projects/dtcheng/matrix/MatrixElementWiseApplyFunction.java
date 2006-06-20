package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class MatrixElementWiseApplyFunction
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixElementWiseApplyFunction";
  }

  public String getModuleInfo() {
    return "This module takes a 2d matrix and applies the given function to each element of the matrix.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "2DMatrix";
      case 1:
        return "FunctionDefinition";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "2DMatrix";
      case 1:
        return "FunctionDefinition";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.FunctionDefinition",
        "java.lang.Long",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultMatix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultMatix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"
    };
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    FunctionDefinition FunctionDefinition = (FunctionDefinition)this.pullInput(1);
//    int FormatIndex = ( (Integer)this.pullInput(2)).intValue();
    long NumberOfElementsThreshold = ((Long)this.pullInput(2)).longValue();

    if ((FunctionDefinition.getNumInputVariables() != 1) || (FunctionDefinition.getNumOutputVariables() != 1)) {
      System.out.println("error!  (FunctionDefinition.getNumInputVariables() != 1) || (FunctionDefinition.getNumOutputVariables() != 1)");
    }

    long NumRows = X.getDimensions()[0];
    long NumCols = X.getDimensions()[1];

    long NumElements = NumRows * NumCols;

    int FormatIndex = -1;
    if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

    MultiFormatMatrix R = new MultiFormatMatrix(FormatIndex, new long [] {NumRows, NumCols});

    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
          R.setValue(RowIndex, ColIndex, FunctionDefinition.evaluate(X.getValue(RowIndex, ColIndex)));
      }
    }

    this.pushOutput(R, 0);
  }

}
