package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.core.modules.*;

public class NaiveBayesInducerOpt extends FunctionInducerOpt {
  
  int        NumRounds      = 0;
  int        Direction      = 0;
  
  
  public String getModuleName() {
    return "Naive Bayes Optimizable";
  }
  
  public String getModuleInfo() {
    return "Naive Bayes Optimizable";
  }
  
  public void setControlParameters(ParameterPoint parameterPoint) {
    
    int i = 0;
    
    NumRounds        = (int) parameterPoint.getValue(i++);
    Direction        = (int) parameterPoint.getValue(i++);
    
  }
  
  
  
  double [] inputMeans = null;
  
  public Model generateModel(ExampleTable exampleSet, ErrorFunction errorFunction) throws Exception {
    
    int numExamples = exampleSet.getNumRows();
    int numInputs   = exampleSet.getNumInputFeatures();
    int numOutputs  = exampleSet.getNumOutputFeatures();
    
    inputMeans = new double[numInputs];
    
    

    System.out.println("calculating input means");
    
    for (int e = 0; e < numExamples; e++) {
      for (int i = 0; i < numInputs; i++) {
        
        inputMeans[i] += exampleSet.getInputDouble(e, i);
        
      }
    }
    
      for (int i = 0; i < numInputs; i++) {
          inputMeans[i] /= numExamples;
      }
    
    System.out.println("calculating all 1d models for each output feature");
    int [][][][] counts = new int[numOutputs][numInputs][2][2];
    int numPositiveExamples = 0;
    
    int input  = -1;
    
    for (int o = 0; o < numOutputs; o++) {
      for (int i = 0; i < numInputs; i++) {
        for (int e = 0; e < numExamples; e++) {
          
          double inputValue  = exampleSet.getInputDouble(e, i);
          
          if (inputValue > inputMeans[i]) {
            input = 1;
          } else {
            input = 0;
          }
          
          counts[o][i][input][(int) exampleSet.getOutputDouble(e, o)]++;
        }
      }
    }

    boolean [] selectedInputs = new boolean[numInputs];
    
    for (int i = 0; i < numInputs; i++) {
      if (Direction <= 0)
        selectedInputs[i] = true;
      else
        selectedInputs[i] = false;
    }
    
    NaiveBayesModel bestModel = null;
    
    
    if (Direction != 0) {
      
    System.out.println("entering stepwise phase");
    
      for (int roundIndex = 0; roundIndex < NumRounds; roundIndex++) {
        double bestError = Double.POSITIVE_INFINITY;
        int    bestV = -1;
        
        for (int v = 0; v < numInputs; v++) {
          if ((Direction == -1 && selectedInputs[v]) || (Direction == 1 && !selectedInputs[v])) {
            
            if (Direction == -1)
              selectedInputs[v] = false;
            else
              selectedInputs[v] = true;
            
            Object [] results = computeError(exampleSet, counts, selectedInputs);
            
            double error = ((Double) results[1]).doubleValue();
            
            if (error < bestError) {
              bestError = error;
              bestV     = v;
            }
            
            if (Direction == -1)
              selectedInputs[v] = true;
            else
              selectedInputs[v] = false;
          }
        }
        
        System.out.println("bestError = " + bestError);
        if (Direction == -1)
          selectedInputs[bestV] = false;
        else
          selectedInputs[bestV] = true;
        
      }
    }
    
    System.out.println("computing error");
    
    Object [] results = computeError(exampleSet, counts, selectedInputs);
    
    bestModel = (NaiveBayesModel) results[0];
    
    double finalError = ((Double) results[1]).doubleValue();
    System.out.println("finalError = " + finalError);
    
    return (Model) bestModel;
  }
  
  
  Object [] computeError(ExampleTable examples, int [][][][] counts, boolean [] selectedInputs) throws Exception {
    
    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();
    
    
    int        numSelectedInputs    = 0;
    int     [] selectedInputIndices = new int[numInputs];
    
    for (int i = 0; i < numInputs; i++) {
      if (selectedInputs[i] == true) {
        selectedInputIndices[numSelectedInputs] = i;
        numSelectedInputs++;
      }
    }
    
    
    
    NaiveBayesModel model = new NaiveBayesModel(examples, selectedInputs, counts, numExamples, inputMeans);
    
    ErrorFunction errorFunction = new ErrorFunction(ErrorFunction.varianceErrorFunctionIndex);
    
    //PredictionTable predictionTable = examples.toPredictionTable();
    //model.predict(predictionTable);

    double error = model.error(examples, errorFunction);

    //System.out.println("error = " + error);
    
    Object [] returnValues = new Object[2];
    
    Double errorObject = new Double(error);
    
    returnValues[0] = model;
    returnValues[1] = errorObject;
    
    return returnValues;
  }
  
}