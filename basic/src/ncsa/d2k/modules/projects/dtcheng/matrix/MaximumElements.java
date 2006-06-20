package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class MaximumElements
    extends ComputeModule {

  public String getModuleName() {
    return "MaximumElements";
  }

  public String getModuleInfo() {
    return "This module takes a 2-d MultiFormatMatrix and based on the " +
        "specified dimension, pulls out the maximum element for each " +
        "row/column. The <i>dimension</i> input specifies the dimension " +
        "to maximize along. That is, if 0 is specified, it will maximize " +
        "along the rows; that is, the output will be a column matrix whose " +
        "elements are the maximum elements in each row. A second output " +
        "matrix will contain the indices of which elements were pulled out.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "DimensionToMaximize";
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
        return "DimensionToMaximize";
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
        return "MaximumElementsMatrix";
      case 1:
        return "IndexMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "MaximumElementsMatrix";
      case 1:
        return "IndexMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

  public void doit() throws Exception {
    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    long DimensionToMaximize = ((Long)this.pullInput(1)).longValue();
    long NumberOfElementsThreshold = ((Long)this.pullInput(2)).longValue();

// idiot-proofing
    long IndexOfFinalDimension = X.getNumDimensions() - 1;
    if (DimensionToMaximize > IndexOfFinalDimension){
      System.out.println("(DimensionToMaximize > IndexOfFinalDimension)");
      throw new Exception();
    }
    if (DimensionToMaximize < 0){
      System.out.println("(DimensionToMaximize < 0)");
      throw new Exception();
    }
    if (DimensionToMaximize > 1){
      System.out.println("(DimensionToMaximize > 1) 2-d matrices only...");
      throw new Exception();
    }
    if (IndexOfFinalDimension != 1){
      System.out.println("(IndexOfFinalDimension != 1) 2-d matrices only...");
      throw new Exception();
    }

    long NumElements = 0;
    if (DimensionToMaximize == 0) {
      NumElements = X.getDimensions()[1];
    }
    else {
      NumElements = X.getDimensions()[0];
    }
// choose the proper format
    int FormatIndex = -1; // initialize
    if (NumElements < NumberOfElementsThreshold) {
      // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so write it out of core; serialized blocks
      // on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

// the exhaustive search
    // suppose we are maximizing ALONG the rows...
    MultiFormatMatrix MaximumElementsMatrix = new MultiFormatMatrix(FormatIndex, new long[] {0, 0});
    MultiFormatMatrix IndexMatrix = new MultiFormatMatrix(FormatIndex, new long[] {0, 0});
    double MaxTemp = java.lang.Double.NEGATIVE_INFINITY;
    long MaxIndexTemp = 0;

    if (DimensionToMaximize == 1) {
      MaximumElementsMatrix = new MultiFormatMatrix(FormatIndex, new long[] {NumElements, 1});
      IndexMatrix = new MultiFormatMatrix(FormatIndex, new long[] {NumElements, 1});
      for (long RowIndex = 0; RowIndex < X.getDimensions()[0]; RowIndex++) {
        for (long ColIndex = 0; ColIndex < X.getDimensions()[1]; ColIndex++) {
          if (X.getValue(RowIndex, ColIndex) > MaxTemp) {
            MaxTemp = X.getValue(RowIndex, ColIndex);
            MaxIndexTemp = ColIndex;
          }
        }
        if (MaxTemp > Double.NEGATIVE_INFINITY) {
        	MaximumElementsMatrix.setValue(RowIndex, 0, MaxTemp);
        	IndexMatrix.setValue(RowIndex, 0, MaxIndexTemp);
        } else {
        	MaximumElementsMatrix.setValue(RowIndex,0,Double.NaN);
        	IndexMatrix.setValue(RowIndex,0,Double.NaN);
        }
        MaxTemp = java.lang.Double.NEGATIVE_INFINITY;
        MaxIndexTemp = 0;
      }
    }
    else {
      MaximumElementsMatrix = new MultiFormatMatrix(FormatIndex, new long[] {1, NumElements});
      IndexMatrix = new MultiFormatMatrix(FormatIndex, new long[] {1, NumElements});
      for (long ColIndex = 0; ColIndex < X.getDimensions()[1]; ColIndex++) {
        for (long RowIndex = 0; RowIndex < X.getDimensions()[0]; RowIndex++) {
          if (X.getValue(RowIndex, ColIndex) > MaxTemp) {
            MaxTemp = X.getValue(RowIndex, ColIndex);
            MaxIndexTemp = RowIndex;
          }
        }
        if (MaxTemp > Double.NEGATIVE_INFINITY) {
        	MaximumElementsMatrix.setValue(0,ColIndex,MaxTemp);
        	IndexMatrix.setValue(0,ColIndex,MaxIndexTemp);
        } else {
        	MaximumElementsMatrix.setValue(0,ColIndex,Double.NaN);
        	IndexMatrix.setValue(0,ColIndex,Double.NaN);
        }

        MaxTemp = java.lang.Double.NEGATIVE_INFINITY;
        MaxIndexTemp = 0;
      }

    }

    this.pushOutput(MaximumElementsMatrix, 0);
    this.pushOutput(IndexMatrix, 1);
  }

}
