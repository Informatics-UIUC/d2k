package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;
import java.util.Random;

public class MagicDataGenerator extends ComputeModule {

  public String getModuleName() {
    return "MagicDataGenerator";
  }

  public String getModuleInfo() {
    return "This module magically creates fake data for use in testing algorithms...";
  }

  public String getInputName(int i) {
    switch (i) {
    // case 0:
    // return "NumRows";
    // case 1:
    // return "NumCols";
    // case 2:
    // return "Seed";
    // case 3:
    // return "NumberOfElementsThreshold";
    // case 4:
    // return "FormatIndex";
    default:
      return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    // case 0:
    // return "NumRows";
    // case 1:
    // return "NumCols";
    // case 2:
    // return "Seed";
    // case 3:
    // return "NumberOfElementsThreshold";
    // case 4:
    // return "FormatIndex";
    default:
      return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
    // "java.lang.Long",
    // "java.lang.Long",
    // "java.lang.Long",
    // "java.lang.Long",
    // "java.lang.Integer"
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
    String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix" };
    return types;
  }

  public void doit() throws Exception {

    long nRows = 10000;
    long nXs = 1;
    double xMin = -5;
    double xMax = 5;
    

    
    long randomSeed = 528;
    long nElementsThreshold = 100000000;
    long nCols = nXs + 1;
    double xRange = xMax - xMin;
    
    int formatIndex = -1; // initialize

    double valueToStore = Double.NaN;
    
    // determine the proper format
    long nElements = nRows * nCols;

    if (nElements < nElementsThreshold) {
      // small means keep it in core; single dimensional in memory is best
      formatIndex = 1; // Beware the MAGIC NUMBER!!!
    } else { // not small means big, so write it out of core; serialized blocks
      // on disk are best
      formatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

    // prepare the random number generator
    Random randomNumberGenerator = new Random(randomSeed);

    MultiFormatMatrix fakeDataMatrix = new MultiFormatMatrix(formatIndex,
        new long[] { nRows, nCols });

    for (long rowIndex = 0; rowIndex < nRows; rowIndex++) {
      for (long colIndex = 0; colIndex < nXs; colIndex++) {
        valueToStore = randomNumberGenerator.nextDouble() * xRange + xMin;
        fakeDataMatrix.setValue(rowIndex, colIndex, valueToStore);
      }
      
      // the magic formula goes here...
      valueToStore = Math.sin(fakeDataMatrix.getValue(new long[] {rowIndex,0} ) );
      
      // put it away...
      fakeDataMatrix.setValue(rowIndex, nXs, valueToStore);
    }

    this.pushOutput(fakeDataMatrix, 0);
  }

}
