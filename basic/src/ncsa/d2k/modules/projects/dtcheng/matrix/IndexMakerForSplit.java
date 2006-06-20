package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class IndexMakerForSplit
    extends ComputeModule {

  public String getModuleName() {
    return "IndexMakerForSplit";
  }

  public String getModuleInfo() {
    return "This module does several things in preparation for using the " +
        "data for the neural net. The inputs are the total number of " +
        "examples, the fraction that should be used for training, and the epoch " +
        "interval between reporting progress. " +
        "The outputs are the number of examples in the training " +
        "set minus one (that is, the index of the last training example), " +
        "the number of examples in the validation set minus one.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "nExamples";
      case 1:
        return "FractionInTrainingSet";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "nExamples";
      case 1:
        return "FractionInTrainingSet";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
        "java.lang.Double",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "nTrainingMinusOne";
      case 1:
        return "nValidationMinusOne";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "nTrainingMinusOne";
      case 1:
        return "nValidationMinusOne";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long",
        "java.lang.Long",
    };
    return types;
  }

  public void doit() throws Exception {


    Long nExamples = (Long)this.pullInput(0);
    Double FractionInTrainingSet = (Double)this.pullInput(1);

//    Double tempnumber = new Double (java.lang.Math.floor(nExamples.doubleValue()*FractionInTrainingSet.doubleValue()) - 1);
//    Integer n_TrainingMinusOne = new Integer (tempnumber.intValue());

    Long nTrainingMinusOne = new Long
        ((new Double
          (java.lang.Math.floor(nExamples.doubleValue() * FractionInTrainingSet.doubleValue())
           - 1.0)).longValue());

    this.pushOutput(nTrainingMinusOne, 0);
    this.pushOutput(new Long(nExamples.longValue() - nTrainingMinusOne.longValue() - 2), 1);
  }

}
