package ncsa.d2k.modules.core.prediction.mean.continuous;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;

public class MeanInducer extends FunctionInducer {
  //int NumBiasParameters = 0;

  public String getModuleInfo() {
    return "MeanInducer";
  }
  public String getModuleName() {
    return "MeanInducer";
  }

  public void instantiateBias(double [] bias) {
  }

  public void instantiateBiasFromProperties() {
    // Nothing to do in this case since properties are reference directly by the algorithm and no other control
    // parameters need be set.  This may not be the case in general so this stub is left open for future development.
  }


  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws Exception {

    int numExamples = examples.getNumExamples();
    int numOutputs  = examples.getNumOutputFeatures();

    double [] outputSums = new double[numOutputs];

    for (int e = 0; e < numExamples; e++) {
      for (int f = 0; f < numOutputs; f++) {
        outputSums[f] += examples.getOutputDouble(e, f);
      }
    }

    for (int f = 0; f < numOutputs; f++) {
      outputSums[f] /= numExamples;
    }


    MeanModel model = new MeanModel(examples, outputSums);

    return (Model) model;
  }
}