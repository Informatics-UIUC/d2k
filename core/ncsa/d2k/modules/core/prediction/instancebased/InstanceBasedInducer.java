package ncsa.d2k.modules.core.prediction.instancebased;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.ComputeModule;

public class InstanceBasedInducer extends InstanceBasedInducerOpt {

  public  void    setNeighborhoodSize (int value) {       this.NeighborhoodSize = value;}
  public  int     getNeighborhoodSize ()          {return this.NeighborhoodSize;}

  public  void    setDistanceWeightingPower (double value) {    this.DistanceWeightingPower = value;}
  public  double  getDistanceWeightingPower ()          {return this.DistanceWeightingPower;}

  public  void    setZeroDistanceValue (double value) {    this.ZeroDistanceValue = value;}
  public  double  getZeroDistanceValue ()          {return this.ZeroDistanceValue;}

  public String getModuleInfo() {
    return "InstanceBasedInducer";
  }
  public String getModuleName() {
    return "InstanceBasedInducer";
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
  public void instantiateBiasFromProperties() {
    // Nothing to do in this case since properties are reference directly by the algorithm and no other control
    // parameters need be set.  This may not be the case in general so this stub is left open for future development.
  }

  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) {

    int numExamples = examples.getNumExamples();
    int numInputs   = examples.getNumInputFeatures();
    double [] inputMins   = new double[numInputs];
    double [] inputMaxs   = new double[numInputs];
    double [] inputRanges = new double[numInputs];

    for (int v = 0; v < numInputs; v++) {
      inputMins[v] = Double.POSITIVE_INFINITY;
      inputMaxs[v] = Double.NEGATIVE_INFINITY;
    }

    for (int e = 0; e < numExamples; e++) {
      for (int v = 0; v < numInputs; v++) {
        double value = examples.getInputDouble(e, v);
        if (value < inputMins[v])
          inputMins[v] = value;
        if (value > inputMaxs[v])
          inputMaxs[v] = value;
      }
    }

    for (int v = 0; v < numInputs; v++) {
      inputRanges[v] = inputMaxs[v] - inputMins[v];
    }

    InstanceBasedModel model = new InstanceBasedModel(
        examples,
        inputRanges,
        NeighborhoodSize,
        DistanceWeightingPower,
        ZeroDistanceValue,
        examples);

    return (Model) model;
  }

  public void doit() throws Exception {

    ExampleTable  exampleSet      = (ExampleTable)  this.pullInput(0);
    ErrorFunction errorFunction   = (ErrorFunction) this.pullInput(1);

    instantiateBiasFromProperties();

    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }

}