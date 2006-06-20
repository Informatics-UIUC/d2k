package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class ErrorFunction implements java.io.Serializable {
  
  public int index;
  
  public String errorFunctionObjectPathName;
  
  public static final int classificationErrorFunctionIndex = 0;
  public static final int absoluteErrorFunctionIndex       = 1;
  public static final int varianceErrorFunctionIndex       = 2;
  public static final int likelihoodErrorFunctionIndex     = 3;
  public static final int relativeErrorFunctionIndex       = 4;
  public static final int neuropace1ErrorFunctionIndex     = 5;
  public static final int neuropace2ErrorFunctionIndex     = 6;
  
  public static final String[] errorFunctionNames = { 
    "classification",
  "absolute", 
    "variance", 
    "likelihood", 
    "relative", 
    "neuropace1", 
    "neuropace2", 
  };
  
  public ErrorFunction(int errorFunctionIndex) {
    this.index = errorFunctionIndex;
  }
  
  public ErrorFunction(ParameterPoint parameterPoint) {
    this.index = (int) parameterPoint.getValue(0);
  }
  
  static public String[] getErrorFunctionNames() {
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
  
  public double evaluate(ExampleTable examples, PredictionTable predictedExamples, Model model) throws Exception {
    int numExamples = examples.getNumRows();
    
    double [] workOutputs = new double[examples.getNumOutputFeatures()];
    
    double errorSum = 0.0;
    for (int e = 0; e < numExamples; e++) {
      errorSum += evaluate(examples, e, predictedExamples, workOutputs, model);
    }
    return errorSum / numExamples;
  }
  
  public double evaluate(ExampleTable actualExamples, int exampleIndex, PredictionTable predictedExamples, double [] predictedOutputs, Model model) throws Exception {
    
    int numOutputFeatures = actualExamples.getNumOutputFeatures();
    
    for (int f = 0; f < actualExamples.getNumOutputFeatures(); f++) {
      predictedOutputs[f] = predictedExamples.getDoublePrediction(exampleIndex, f);
    }
    
    return evaluate(actualExamples, exampleIndex, predictedOutputs, model);
  }
  
  public double evaluate(ExampleTable actualExamples, int exampleIndex, double [] predictedOutputs, Model model) throws Exception {
    
    int numInputs = actualExamples.getNumInputFeatures();
    int numOutputs = actualExamples.getNumOutputFeatures();
    
    double error = Double.NaN;
    
    
    switch (index) {
      
      case classificationErrorFunctionIndex: {
        double errorSum = 0.0;
        
        //double[] outputs = outputsMemory;
        
        //model.evaluate(examples, exampleIndex, outputs);
        
        if (numOutputs == 1) {
          if (Math.round(actualExamples.getOutputDouble(exampleIndex, 0)) != Math.round(predictedOutputs[0])) {
            errorSum++;
          }
        } else {
          double maxPredictedOutput = Double.NEGATIVE_INFINITY;
          double maxActualOutput = Double.NEGATIVE_INFINITY;
          int maxPredictedOutputIndex = -1;
          int maxActualOutputIndex = -1;
          for (int f = 0; f < numOutputs; f++) {
            double predictedOutput = predictedOutputs[f];
            double actualOutput = actualExamples.getOutputDouble(exampleIndex, f);
            
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
        
        for (int f = 0; f < numOutputs; f++) {
          double difference = actualExamples.getOutputDouble(exampleIndex, f) - predictedOutputs[f];
          errorSum += Math.abs(difference);
        }
        
        error = errorSum;
      }
      break;
      
      // Variance Error //
      case varianceErrorFunctionIndex: {
        double errorSum = 0.0;
        
        for (int f = 0; f < numOutputs; f++) {
          double difference = actualExamples.getOutputDouble(exampleIndex, f) - predictedOutputs[f];
          errorSum += difference * difference;
        }
        
        error = errorSum;
      }
      break;
      
      // Likelihood Error //
      case likelihoodErrorFunctionIndex: {
        double logLikelihood = model.evaluateLogLikelihood(actualExamples, exampleIndex);
        error = -logLikelihood;
      }
      break;

      // Relative Error //
      case relativeErrorFunctionIndex: {
        double errorSum = 0.0;
        
        for (int f = 0; f < numOutputs; f++) {
          double actualValue = actualExamples.getOutputDouble(exampleIndex, f);
          double predictedValue = predictedOutputs[f];
          
          double difference = actualValue - predictedValue;
          errorSum += Math.abs(difference) / actualValue;
        }
        
        error = errorSum;
      }
      break;
      
      // Neuropace 1 Error //
      case neuropace1ErrorFunctionIndex: {
        
        double actual     =    actualExamples.getOutputDouble    (exampleIndex, 0);
        double prediction = predictedOutputs[0];
        
        error = Math.abs(prediction - actual) / (100 + Math.sqrt(Math.abs(prediction * actual)));

      }
      break;
      
      // Neuropace 2 Error //
      case neuropace2ErrorFunctionIndex: {
        
        double actual     =    actualExamples.getOutputDouble    (exampleIndex, 0);
        double prediction = predictedOutputs[0];
        
        double product = prediction * actual;
        
        
        double sign = 0;
        
        if (product < 0)
          sign = -1;
        
        if (product > 0)
          sign = 1;
         
        error = 1.0 - sign;

      }
      break;
      
      default: {
        System.out.println("errorFunctionIndex (" + index
        + ") not recognized");
        error = Double.NaN;
      }
      break;
    }
    return error;
  }
  
}