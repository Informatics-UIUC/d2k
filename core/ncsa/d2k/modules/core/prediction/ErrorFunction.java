package ncsa.d2k.modules.core.prediction;
import ncsa.d2k.modules.core.datatype.parameter.*;
import java.io.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
public class ErrorFunction implements java.io.Serializable {
  public int    errorFunctionIndex;
  public String errorFunctionObjectPathName;

  public static final int classificationErrorFunctionIndex     = 0;
  public static final int absoluteErrorFunctionIndex           = 1;
  public static final int varianceErrorFunctionIndex           = 2;
  public static final int likelihoodErrorFunctionIndex         = 3;
  public static final int negativeOutputSumErrorFunctionIndex  = 4;
  //public static final int allstateErrorFunctionIndex           = 5;
  public static final String [] errorFunctionNames = {
    "classification",
    "absolute",
    "variance",
    "likelihood",
    "negativeOutputSum",
    //"allstate"
  };

/*
  public ErrorFunction(int errorFunctionIndex, String errorFunctionObjectPathName) {
    this.errorFunctionIndex          = errorFunctionIndex;
    this.errorFunctionObjectPathName = errorFunctionObjectPathName;
  }
*/

  public ErrorFunction(int errorFunctionIndex) {
    this.errorFunctionIndex          = errorFunctionIndex;
  }

  public ErrorFunction(ParameterPoint parameterPoint) {
    this.errorFunctionIndex = (int)parameterPoint.getValue(0);
  }

  static public String [] getErrorFunctionNames() {
    return errorFunctionNames;
  }
  static public String getErrorFunctionName(int i) throws Exception {
    return errorFunctionNames[i];
  }
  static public int getErrorFunctionIndex(String name) throws Exception {
    int index = -1;
    for (int i = 0; i < errorFunctionNames.length; i++) {
      if (name.equalsIgnoreCase(errorFunctionNames[i])) {
        index = i;
        break;
      }
    }
    return index;
  }
  double [] outputsMemory = new double [0];
  double [] AllstateRatios;

  public double evaluate(ExampleTable examples, Model model) throws Exception {
    int numExamples = examples.getNumExamples();
    int numInputs = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();

    double errorSum = 0.0;
    for (int e = 0; e < numExamples; e++) {
      errorSum += evaluate(examples, e, model);
    }
    return errorSum / numExamples;
  }

  public double evaluate(ExampleTable examples, int exampleIndex, Model model) throws Exception {

    int numInputs  = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();

    double error = Double.NaN;

    // allocate temporary memory if necessary
    if (outputsMemory.length != numOutputs)
      outputsMemory = new double [numOutputs];

    switch (errorFunctionIndex) {

      case classificationErrorFunctionIndex: {
        double errorSum = 0.0;

        double [] outputs = outputsMemory;

        model.Evaluate(examples, exampleIndex, outputs);

        if (numOutputs == 1) {
          double predictedOutput = examples.getOutputDouble(exampleIndex, 0);
          double actualOutput    = outputs[0];
          if (Math.round(predictedOutput) != Math.round(actualOutput)) {
            errorSum++;
          }
        }
        else {
          double maxPredictedOutput = Double.NEGATIVE_INFINITY;
          double maxActualOutput    = Double.NEGATIVE_INFINITY;
          int maxPredictedOutputIndex = -1;
          int maxActualOutputIndex    = -1;
          for (int f = 0; f < numOutputs; f++) {
            double predictedOutput = examples.getOutputDouble(exampleIndex, f);
            double actualOutput    = outputs[f];

            if (predictedOutput > maxPredictedOutput) {
              maxPredictedOutput = predictedOutput;
              maxPredictedOutputIndex = f;
            }

            if (actualOutput > maxActualOutput) {
              maxActualOutput = actualOutput;
              maxActualOutputIndex = f;
            }
          }
          if (maxPredictedOutputIndex != maxActualOutputIndex) {
            errorSum++;
          }
        }

        error = errorSum;
      }
      break;

      // Absolute Error //
    case absoluteErrorFunctionIndex: {
      double errorSum = 0.0;

      double [] outputs = outputsMemory;
      model.Evaluate(examples, exampleIndex, outputs);
      for (int f = 0; f < numOutputs; f++) {
        double difference = outputs[f] - examples.getOutputDouble(exampleIndex, f);
        errorSum += Math.abs(difference);
      }

      error = errorSum;
    }
    break;

    // Variance Error //
  case varianceErrorFunctionIndex: {
    double errorSum = 0.0;

    double [] outputs = outputsMemory;
    model.Evaluate(examples, exampleIndex, outputs);
    for (int f = 0; f < numOutputs; f++) {
      double difference = outputs[f] - examples.getOutputDouble(exampleIndex, f);
      errorSum += difference * difference;
    }

    error = errorSum;
  }
  break;
case likelihoodErrorFunctionIndex: {
  double likelihoodSum = 0.0;

  double [] outputs = outputsMemory;

  model.Evaluate(examples, exampleIndex, outputs);
  for (int f = 0; f < numOutputs; f++) {
    int ActualOutputClass = (int) examples.getOutputDouble(exampleIndex, f);

    double predictedClassProbability = outputs[f];

    if (ActualOutputClass == 0)
      predictedClassProbability = 1.0 - predictedClassProbability;


    likelihoodSum += Math.log(predictedClassProbability);
  }

  error = - likelihoodSum;
}
break;
case negativeOutputSumErrorFunctionIndex: {
  error = 0.0;
  for (int f = 0; f < numOutputs; f++) {
    error += - examples.getOutputDouble(exampleIndex, 0);
  }
}
break;
/*
case allstateErrorFunctionIndex:
{


  if (AllstateRatios == null) {
    try {
      FileInputStream   file = new FileInputStream(errorFunctionObjectPathName);
      ObjectInputStream in   = new ObjectInputStream(file);

      try {
        AllstateRatios = (double []) in.readObject();
      }
      catch (java.lang.ClassNotFoundException IOE) {
        System.out.println("java.lang.ClassNotFoundException " + IOE);
      }

      in.close();

    }
    catch (java.io.IOException IOE) {
      IOE.printStackTrace();
      System.out.println("IOException!!! could not open " + errorFunctionObjectPathName);
    }
  }

  double errorSum = 0.0;
  double [] outputs = outputsMemory;
  model.Evaluate(examples, exampleIndex, outputs);
  for (int f = 0; f < numOutputs; f++) {
    double difference = outputs[f] - examples.getOutputDouble(exampleIndex, f);
    errorSum += AllstateRatios[f] * (difference * difference);
  }

  error = errorSum;
  break;
}
*/
default: {
  System.out.println("errorFunctionIndex (" + errorFunctionIndex + ") not recognized");
  error = Double.NaN;
}
break;
    }
    return error;
  }
}