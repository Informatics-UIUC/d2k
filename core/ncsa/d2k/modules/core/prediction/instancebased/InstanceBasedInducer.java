package ncsa.d2k.modules.core.prediction.instancebased;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.ComputeModule;

public class InstanceBasedInducer extends FunctionInducer
{
  //int NumBiasParameters = 3;

  private int        NeighborhoodSize = 20;
  public  void    setNeighborhoodSize (int value) {       this.NeighborhoodSize = value;}
  public  int     getNeighborhoodSize ()          {return this.NeighborhoodSize;}

  private double     DistanceWeightingPower = 0.0;
  public  void    setDistanceWeightingPower (double value) {    this.DistanceWeightingPower = value;}
  public  double  getDistanceWeightingPower ()          {return this.DistanceWeightingPower;}

  private double     ZeroDistanceValue = 1E-9;
  public  void    setZeroDistanceValue (double value) {    this.ZeroDistanceValue = value;}
  public  double  getZeroDistanceValue ()          {return this.ZeroDistanceValue;}

  public String getModuleInfo() {
    return "InstanceBasedInducer";
  }
  public String getModuleName() {
    return "InstanceBasedInducer";
  }

  public void instantiateBias(double [] bias) {
    NeighborhoodSize       = (int) bias[0];
    DistanceWeightingPower =       bias[1];
    ZeroDistanceValue      =       bias[2];
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

  public String getInputName(int index) {
    switch(index) {
      case 0: return "input0";
      case 1: return "input1";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputName(int index) {
    switch(index) {
      case 0:
        return "output0";
      default: return "NO SUCH OUTPUT!";
    }
  }
}