package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;
import java.util.Random;

public class MatrixGenerateRandomNxM
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGenerateRandomNxM";
  }

  public String getModuleInfo() {
    return "This module generates a NxM (uniform) random matrix. The " +
        "storage format is determined by the number of elements and "+
        "the threshold provided for moving from memory to disk. There " +
		"will also be a format index that can be supplied which will take " +
		"precedence if it is not a null.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumRows";
      case 1:
        return "NumCols";
      case 2:
        return "Seed";
      case 3:
        return "NumberOfElementsThreshold";
      case 4:
      	return "FormatIndex";
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
        return "Seed";
      case 3:
        return "NumberOfElementsThreshold";
      case 4:
      	return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
        "java.lang.Long",
        "java.lang.Long",
        "java.lang.Long",
		"java.lang.Integer"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultMatrix";
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
    long Seed = ((Long)this.pullInput(2)).longValue();
    long NumberOfElementsThreshold = ((Long)this.pullInput(3)).longValue();
    Object ExternalFormatIndex = this.pullInput(4);

    
    int FormatIndex = -1; // initialize

    //  determine the proper format
    if (ExternalFormatIndex != null) {
    	FormatIndex = ((Integer)ExternalFormatIndex).intValue();
    }
    else {
        long NumElements = NumRows * NumCols;

        if (NumElements < NumberOfElementsThreshold) {
          // small means keep it in core; single dimensional in memory is best
          FormatIndex = 1; // Beware the MAGIC NUMBER!!!
        }
        else { // not small means big, so write it out of core; serialized blocks
          // on disk are best
          FormatIndex = 3; // Beware the MAGIC NUMBER!!!
        }
    }

// prepare the random number generator
    Random RandomNumberGenerator = new Random(Seed);


//    double[][] ResultMatrix = new double[NumRows.intValue()][NumCols.intValue()];
    MultiFormatMatrix ResultMatrix = new MultiFormatMatrix(FormatIndex,
        new long[] {NumRows, NumCols});

    for (long i = 0; i < NumRows; i++) {
      for (long j = 0; j < NumCols; j++) {
//        ResultMatrix[i][j] = RandomNumberGenerator.nextDouble();
        ResultMatrix.setValue(i, j, RandomNumberGenerator.nextDouble());
      }
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
