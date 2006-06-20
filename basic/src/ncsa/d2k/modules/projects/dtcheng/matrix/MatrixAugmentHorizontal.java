package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class MatrixAugmentHorizontal
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixAugmentHorizontal";
  }

  public String getModuleInfo() {
    return "This module takes two matrices and makes them into one matrix where " +
    		"the two matrices are placed side by side";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "FirstMatrix";
      case 1:
        return "SecondMatrix";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "FirstMatrix";
      case 1:
        return "SecondMatrix";
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
        return "AugmentedMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "AugmentedMatrix";
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

    MultiFormatMatrix FirstMatrix = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix SecondMatrix = null;
    try {
      SecondMatrix = (MultiFormatMatrix)this.pullInput(1);
    }
    catch (Exception ex) {
    }
    long NumberOfElementsThreshold = ((Long)this.pullInput(2)).longValue();

    long RowSizeFirstMatrix = FirstMatrix.getDimensions()[0];
    long ColSizeFirstMatrix = FirstMatrix.getDimensions()[1];

    long RowSizeSecondMatrix = SecondMatrix.getDimensions()[0];
    long ColSizeSecondMatrix = SecondMatrix.getDimensions()[1];

//    MultiFormatMatrix AugmentedMatrix = null;

    if (RowSizeFirstMatrix != RowSizeSecondMatrix) {
      System.out.println("Error!  RowSizeFirstMatrix != RowSizeSecondMatrix");
    }

    long NumElements = RowSizeFirstMatrix*(ColSizeFirstMatrix + ColSizeSecondMatrix);
    int FormatIndex = -1;
    if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

    MultiFormatMatrix AugmentedMatrix = new MultiFormatMatrix(FormatIndex, new long [] {RowSizeFirstMatrix, ColSizeFirstMatrix + ColSizeSecondMatrix});

    for (long OutputCol = 0; OutputCol < ColSizeFirstMatrix; OutputCol++) {
      for (long OutputRow = 0; OutputRow < RowSizeFirstMatrix; OutputRow++) {
        AugmentedMatrix.setValue(OutputRow, OutputCol, FirstMatrix.getValue(OutputRow, OutputCol));
      }
    }
    for (long OutputCol = 0; OutputCol < ColSizeSecondMatrix; OutputCol++) {
      for (long OutputRow = 0; OutputRow < RowSizeFirstMatrix; OutputRow++) {
        AugmentedMatrix.setValue(OutputRow, ColSizeFirstMatrix + OutputCol, SecondMatrix.getValue(OutputRow, OutputCol));
      }
    }

/*
    for (int OutputRow = 0; OutputRow < RowSizeFirstMatrix; OutputRow++) {
      for (int OutputCol = 0; OutputCol < ColSizeFirstMatrix; OutputCol++) {
        AugmentedMatrix.setValue(OutputRow, OutputCol, FirstMatrix.getValue(OutputRow, OutputCol));
      }
      for (int OutputCol = 0; OutputCol < ColSizeSecondMatrix; OutputCol++) {
        AugmentedMatrix.setValue(OutputRow, ColSizeFirstMatrix + OutputCol, SecondMatrix.getValue(OutputRow, OutputCol));
      }
    }
*/
    this.pushOutput(AugmentedMatrix, 0);
  }

}
