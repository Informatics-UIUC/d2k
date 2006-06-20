package ncsa.d2k.modules.projects.dtcheng.matrix;
//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class MatrixElementWiseMultiplication
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixElementWiseMultiplication";
  }

  public String getModuleInfo() {
    return "This module computes element-wise multiplication of X*Y.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix X";
      case 1:
        return "Matrix Y";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix X";
      case 1:
        return "Matrix Y";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
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
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"};
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix Y = (MultiFormatMatrix)this.pullInput(1);
    long NumberOfElementsThreshold = ((Long)this.pullInput(2)).longValue();

    long RowSizeX = X.getDimensions()[0];
    long ColSizeX = X.getDimensions()[1];

    long RowSizeY = Y.getDimensions()[0];
    long ColSizeY = Y.getDimensions()[1];

    long NumElements = -1;
    int FormatIndex = -2; // initialize
//    double ScalarTemp = -3.0;

    boolean XIsScalar = false;
    boolean YIsScalar = false;

    if ((RowSizeX == 1) && (ColSizeX == 1)) {
      XIsScalar = true;
    }
    if ((RowSizeY == 1) && (ColSizeY == 1)) {
      YIsScalar = true;
    }
    MultiFormatMatrix ResultMatrix = null;

    if (XIsScalar && YIsScalar) {
      NumElements = 1;
      FormatIndex = 1; // small: keep it in core
      ResultMatrix = new MultiFormatMatrix(FormatIndex, new long [] {1, 1});
      ResultMatrix.setValue(0, 0, X.getValue(0, 0) * Y.getValue(0, 0));
    }
    else
    if (XIsScalar) {
      NumElements = RowSizeY * ColSizeY;
      if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
        FormatIndex = 1; // Beware the MAGIC NUMBER!!!
      }
      else { // not small means big, so go out of core; serialized blocks on disk are best
        FormatIndex = 3; // Beware the MAGIC NUMBER!!!
      }
//      ScalarTemp = X.getValue(0, 0);
      ResultMatrix = new MultiFormatMatrix(FormatIndex, new long[] {RowSizeY, ColSizeY});
      for (long OutputRow = 0; OutputRow < RowSizeY; OutputRow++) {
        for (long OutputCol = 0; OutputCol < ColSizeY; OutputCol++) {
          ResultMatrix.setValue(OutputRow, OutputCol, X.getValue(0, 0) * Y.getValue(OutputRow, OutputCol));
        }
      }
    }
    else
    if (YIsScalar) {
      NumElements = RowSizeX * ColSizeX;
      if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
        FormatIndex = 1; // Beware the MAGIC NUMBER!!!
      }
      else { // not small means big, so go out of core; serialized blocks on disk are best
        FormatIndex = 3; // Beware the MAGIC NUMBER!!!
      }
//      ScalarTemp = Y.getValue(0, 0);
      ResultMatrix = new MultiFormatMatrix(FormatIndex, new long[] {RowSizeX, ColSizeX});
      for (long OutputRow = 0; OutputRow < RowSizeX; OutputRow++) {
        for (long OutputCol = 0; OutputCol < ColSizeX; OutputCol++) {
          ResultMatrix.setValue(OutputRow, OutputCol, X.getValue(OutputRow,OutputCol) * Y.getValue(0,0));
        }
      }
    }
    else {
      if (ColSizeX != ColSizeY) {
        System.out.println("ColSizeX != ColSizeY (X and Y are nonconformable)");
        throw new Exception();
      }
      if (RowSizeX != RowSizeY) {
        System.out.println("RowSizeX != RowSizeY (X and Y are nonconformable)");
        throw new Exception();
      }
      NumElements = RowSizeX * ColSizeX;
      if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
        FormatIndex = 1; // Beware the MAGIC NUMBER!!!
      }
      else { // not small means big, so go out of core; serialized blocks on disk are best
        FormatIndex = 3; // Beware the MAGIC NUMBER!!!
      }

      ResultMatrix = new MultiFormatMatrix(FormatIndex, new long [] {RowSizeX,ColSizeX});

      for (long RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
        for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
          ResultMatrix.setValue(RowIndex, ColIndex, X.getValue(RowIndex, ColIndex) * Y.getValue(RowIndex, ColIndex));
        }
      }

    }

    this.pushOutput(ResultMatrix, 0);
  }

}
