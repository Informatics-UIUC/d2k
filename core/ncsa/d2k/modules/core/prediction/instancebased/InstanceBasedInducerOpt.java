package ncsa.d2k.modules.core.prediction.instancebased;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.core.modules.*;

public class InstanceBasedInducerOpt extends FunctionInducerOpt {

  int        NeighborhoodSize       = 1;
  double     DistanceWeightingPower = 0.0;
  double     ZeroDistanceValue      = 1E-9;


  public String getModuleName() {
    return "InstanceBasedInducerOpt";
  }
  public String getModuleInfo() {
    return "InstanceBasedInducerOpt";
  }

  public void setControlParameters(ParameterPoint parameterPoint) {

    NeighborhoodSize       = (int) parameterPoint.getValue(0);
    DistanceWeightingPower =       parameterPoint.getValue(1);
    ZeroDistanceValue      =       parameterPoint.getValue(2);

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

    //QA Anca added this:
    public PropertyDescription[] getPropertiesDescriptions() { 
        // so that "ordered and _trace" property are invisible 
        return new PropertyDescription[0];  
    }

}
