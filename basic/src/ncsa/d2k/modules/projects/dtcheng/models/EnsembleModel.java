package ncsa.d2k.modules.projects.dtcheng.models;

import java.text.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.inducers.Model;
import ncsa.d2k.modules.projects.dtcheng.inducers.ModelPrintOptions;

public class EnsembleModel extends Model implements java.io.Serializable {
  int numModels = 0;
  
  Model[] models;
  
  int CombinationMethod = 0; // 0 = sum, 1 = average
  
  public static final int SUM = 0;
  
  public static final int AVERAGE = 1;
  
  public EnsembleModel(ExampleTable examples, Model[] models, int NumModels, int CombinationMethod) throws Exception {
    super(examples);
    
    this.numModels         = NumModels;
    this.CombinationMethod = CombinationMethod;
    
    Model[] ModelsCopy = new Model[NumModels];
    for (int i = 0; i < NumModels; i++) {
      ModelsCopy[i] = (Model) models[i].clone();
    }
    this.models = ModelsCopy;
  }
  
  public void evaluate(double[] inputs, double[] outputs)
  throws Exception {
    
    int numOutputs = outputs.length;
    double[] subModelOutputs = new double[numOutputs];
    double[] combinedOutputs = new double[numOutputs];
    
    for (int m = 0; m < numModels; m++) {
      this.models[m].evaluate(inputs, subModelOutputs);
      for (int v = 0; v < numOutputs; v++) {
        combinedOutputs[v] += subModelOutputs[v];
      }
    }
    
    if (this.CombinationMethod == AVERAGE) {
      for (int v = 0; v < numOutputs; v++) {
        outputs[v] = combinedOutputs[v] / numModels;
      }
    }
    
  }
  
  public double[] evaluate(ExampleTable exampleSet, int e) throws Exception {
    double[] outputs;
    double[] combinedOutputs = new double[exampleSet.getNumOutputFeatures()];
    
    for (int m = 0; m < numModels; m++) {
      outputs = this.models[m].evaluate(exampleSet, e);
      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
        combinedOutputs[v] += outputs[v];
      }
    }
    
    if (this.CombinationMethod == EnsembleModel.AVERAGE) {
      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
        combinedOutputs[v] /= numModels;
      }
    }
    return combinedOutputs;
  }
  
  public void Evaluate(ExampleTable exampleSet, int e,
   double[] combinedOutputs) throws Exception {
    double[] outputs;
    
    for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
      combinedOutputs[v] = 0.0;
    }
    
    for (int m = 0; m < numModels; m++) {
      outputs = this.models[m].evaluate(exampleSet, e);
      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
        combinedOutputs[v] += outputs[v];
      }
    }
    
    if (this.CombinationMethod == EnsembleModel.AVERAGE) {
      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
        combinedOutputs[v] /= numModels;
      }
    }
    
  }
  
  DecimalFormat Format = new DecimalFormat();
  
  public void print(ModelPrintOptions options, int indent) throws Exception {
    
    Format.setMaximumFractionDigits(options.MaximumFractionDigits);
    System.out.println("Ensemble with " + numModels + " models");
    
    for (int m = 0; m < numModels; m++) {
      this.models[m].print(options, indent);
      
      System.out.println();
    }
    
  }
  
}