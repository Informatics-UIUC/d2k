package ncsa.d2k.modules.projects.dtcheng.matrix;


//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;


public class MatrixStackToColumn extends ComputeModule {

  public String getModuleName() {
    return "MatrixStackToColumn";
  }


  public String getModuleInfo() {
    return "This module takes a matrix and stacks the columns on each other to form a long column with the first column at the top.  ";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
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
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Long",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "StackedMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "StackedMatrix";
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

    MultiFormatMatrix InputMatrix = (MultiFormatMatrix)this.pullInput(0);
    long NumberOfElementsThreshold = ((Long)this.pullInput(1)).longValue();

    long NumRows = InputMatrix.getDimensions()[0];
    long NumCols = InputMatrix.getDimensions()[1];

    long NumElements = NumRows * NumCols;
    int FormatIndex = -1;

    if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

    MultiFormatMatrix StackedMatrix = new MultiFormatMatrix(FormatIndex, new long[] {NumElements, 1});

    long r = 0;
    for (long ColIndex = 0; ColIndex < NumCols; ColIndex++) {
      for (long RowIndex = 0; RowIndex < NumRows; RowIndex++) {
        StackedMatrix.setValue(r++, 0, InputMatrix.getValue(RowIndex, ColIndex));
        //  [r++][0] = InputMatrix[RowIndex][ColIndex];
      }
    }

    this.pushOutput(StackedMatrix, 0);
  }

}
