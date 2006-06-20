package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class GetSubmatrix
    extends ComputeModule {

  public String getModuleName() {
    return "GetSubmatrix";
  }

  public String getModuleInfo() {
    return "This module pulls out a submatrix from a 2-d MultiFormatMatrix. " +
        "Numbering starts at zero. The NumberOfElementsThreshold is an input " +
        "that determines when to use in-core memory and when to use " +
        "out-of-core memory. If the resulting matrix will be larger than " +
        "NumberOfElementsThreshold, then out-of-core will be used. " +
        "Furthermore, if the Finish Index is -1, then " +
        "all the rows or columns will be taken from the Start Index to the end. " +
        "This makes it easier for " +
        "pulling out certain rows or columns.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "RowStartIndex";
      case 2:
        return "RowEndIndex";
      case 3:
        return "ColumnStartIndex";
      case 4:
        return "ColumnEndIndex";
      case 5:
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
        return "RowStartIndex";
      case 2:
        return "RowEndIndex";
      case 3:
        return "ColumnStartIndex";
      case 4:
        return "ColumnEndIndex";
      case 5:
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
        "java.lang.Long",
        "java.lang.Long",
        "java.lang.Long",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Submatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Submatrix";
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
    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    long RowStartIndex = ((Long)this.pullInput(1)).longValue();
    long RowEndIndex = ((Long)this.pullInput(2)).longValue();
    long ColStartIndex = ((Long)this.pullInput(3)).longValue();
    long ColEndIndex = ((Long)this.pullInput(4)).longValue();
    long NumberOfElementsThreshold = ((Long) this.pullInput(5)).longValue();

// pull out the last available indices
    long LastRowIndexX = X.getDimensions()[0] - 1;
    long LastColIndexX = X.getDimensions()[1] - 1;
// preprocessing on the "-1"s...
    if (RowEndIndex == -1) {
      RowEndIndex = LastRowIndexX;
    }
    if (ColEndIndex == -1) {
      ColEndIndex = LastColIndexX;
    }

// idiot proofing
    if ((RowStartIndex > RowEndIndex)) {
      System.out.println("(RowStartIndex [" + RowStartIndex + "] > RowEndIndex [" + RowEndIndex + "])");
      throw new Exception();
    }
    if (ColStartIndex > ColEndIndex) {
      System.out.println("(ColStartIndex > ColEndIndex)");
      throw new Exception();
    }
    if (RowEndIndex > LastRowIndexX) {
      System.out.println("(RowEndIndex > LastRowIndexX)");
      throw new Exception();
    }
    if (ColEndIndex > LastColIndexX) {
      System.out.println("(ColEndIndex > LastColIndexX)");
      throw new Exception();
    }
    if (RowStartIndex < 0 || RowEndIndex < 0 ||
        ColStartIndex < 0 || ColEndIndex < 0) {
      System.out.println("Bad indices: less than -1 ...");
      throw new Exception();
    }

// prepare and calculate the size of the outgoing matrix
    long NumRows = RowEndIndex - RowStartIndex + 1;
    long NumCols = ColEndIndex - ColStartIndex + 1;
    long NumElements = NumRows * NumCols;

    int FormatIndex = -1; // initialize
    if (NumElements < NumberOfElementsThreshold) {
      // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so write it out of core; serialized blocks
      // on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

    MultiFormatMatrix SubMatrix = new MultiFormatMatrix(FormatIndex, new long[] {NumRows, NumCols});
    for (long RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (long ColIndex = 0; ColIndex < NumCols; ColIndex++) {
        SubMatrix.setValue(RowIndex, ColIndex, X.getValue(RowIndex + RowStartIndex, ColIndex + ColStartIndex));
      }
    }

this.pushOutput(SubMatrix, 0);
  }

}
