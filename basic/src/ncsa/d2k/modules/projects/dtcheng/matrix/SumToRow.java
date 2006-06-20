package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.core.datatype.table.*;
//import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.core.prediction.*;

public class SumToRow
    extends ComputeModule {

  public String getModuleInfo() {
    return "This collapses the columns of an (mxn) matrix to a (1xn) row vector by adding down the columns.  " +
        "Algebraically, this is the same as  a matrix (1xn) of 1's times X (mxn).  ";
  }

  public String getModuleName() {
    return "SumToRow";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "InputMatrix";
      case 1:
        return "NumberOfElementsThreshold";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "InputMatrix";
      case 1:
        return "NumberOfElementsThreshold";
      default:
        return "No such input";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Long",
    };
    return types;
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "RowMatrix";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "RowMatrix";
      default:
        return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

/*
  private String Format = "NativeArray";
  public void setFormat(String value) {
    this.Format = value;
  }

  public String getFormat() {
    return this.Format;
  }*/

  public void doit() throws Exception {

    MultiFormatMatrix InputMatrix = (MultiFormatMatrix)this.pullInput(0);
    long NumberOfElementsThreshold = ((Long)this.pullInput(1)).longValue();
//    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    long NumRows = InputMatrix.getDimensions()[0];
    long NumCols = InputMatrix.getDimensions()[1];

    long NumElements = NumCols;
    int FormatIndex = -1;
    if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

    MultiFormatMatrix RowMatrix = new MultiFormatMatrix(FormatIndex, new long[] {1, NumCols});

    for (long i = 0; i < NumRows; i++) {
      for (long j = 0; j < NumCols; j++) {
        RowMatrix.setValue(0, j,  RowMatrix.getValue(0, j) + InputMatrix.getValue(i, j));
      }
    }

    this.pushOutput(RowMatrix, 0);
  }
}
