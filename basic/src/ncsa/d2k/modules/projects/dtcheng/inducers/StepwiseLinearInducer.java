package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.beans.PropertyVetoException;
//import Jama.Matrix;


public class StepwiseLinearInducer extends StepwiseLinearInducerOpt {

  int numBiasDimensions = 3;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    pds[0] = new PropertyDescription(
        "useStepwise",
        "Use Stepwise",
        "When true, a stepwise regression procedure is followed, otherwise normal regression is used on all features.");

    pds[1] = new PropertyDescription(
        "direction",
        "Direction of Search",
        "When set to 1, step up regression is used, and when set to -1 step down regression is done.  ");

    pds[2] = new PropertyDescription(
        "numRounds",
        "Number of Feature Selection Rounds",
        "The number of features to add (step up) or remove (step down) from the initial feature subset.  ");


    return pds;
  }

  //private boolean UseStepwise = false;
  public void setUseStepwise(boolean value) throws PropertyVetoException {
    this.UseStepwise = value;
  }

  public boolean getUseStepwise() {
    return this.UseStepwise;
  }

  //private int Direction = 1;
  public void setDirection(int value) throws PropertyVetoException {
    if (!((value == -1) || (value == 1))) {
      throw new PropertyVetoException(" must be -1 or 1", null);
    }
    this.Direction = value;
  }
  public int getDirection() {
    return this.Direction;
  }

  //private int NumRounds = 1;
  public void setNumRounds(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.NumRounds = value;
  }
  public int getNumRounds() {
    return this.NumRounds;
  }


  public String getModuleName() {
    return "Stepwise Linear Inducer";
  }

  public String getInputName(int i) {
    switch(i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.prediction.ErrorFunction"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }
  public void doit() throws Exception {

    ExampleTable  exampleSet      = (ExampleTable)  this.pullInput(0);
    ErrorFunction errorFunction   = (ErrorFunction) this.pullInput(1);

    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }


  }
