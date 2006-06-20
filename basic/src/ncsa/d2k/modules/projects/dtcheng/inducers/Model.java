package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.*;
public class Model extends PredictionModelModule implements java.io.Serializable {

	public Model [] subModels;
  
  public int getNumSubModels() {
	return subModels.length;
	}

  public Model [] getSubModels() {
	return subModels;
	}

  public Model getSubModel(int index) {
	return subModels[index];
	}

  protected Model(ExampleTable examples) {
	super(examples);  //??? needed?
  }
  
  protected Model(
   int trainingSetSize,
   String[] inputColumnLabels,
   String[] outputColumnLabels,
   int[] inputFeatureTypes,
   int[] outputFeatureTypes) {
    super(trainingSetSize, inputColumnLabels, outputColumnLabels, inputFeatureTypes, outputFeatureTypes);
  }

  public void evaluate(double [] inputs, double [] outputs) throws Exception {
	System.out.println("must override evaluate method: evaluate(double [] inputs, double [] outputs)");
	throw new Exception();
  }

  public double [] evaluate(ExampleTable exampleSet, int e) throws Exception {
	System.out.println("must override evaluate method: evaluate(ExampleTable exampleSet, int e)");
	throw new Exception();
  }

  public void evaluate(ExampleTable exampleSet, int e, double [] outputs) throws Exception {
	int numOutputs = exampleSet.getNumOutputFeatures();
	double [] internalOutputs = evaluate(exampleSet, e);
	for (int i = 0; i < numOutputs; i++) {
	  outputs[i] = internalOutputs[i];
	}
  }

  public double evaluateLogLikelihood(ExampleTable exampleSet, int e) throws Exception {
	System.out.println("must override evaluate method: evaluateLikelihood(ExampleTable exampleSet, int e)");
	throw new Exception();
  }

  public void print(ModelPrintOptions options, int indent) throws Exception {
	System.out.println("must override print method");
	throw new Exception();
  }
  
  public String toString() {
    
    
    try {
      ModelPrintOptions options = new ModelPrintOptions();
      
      this.print(options, 0);
    } catch(Exception e) {};
    
    return "Model";
  }

  public int getNumInputFeatures() {
	return getInputColumnLabels().length;
  }

  //renaming

  public String[] getInputFeatureNames() {
	return getInputColumnLabels();
	}

  public String getInputFeatureName(int i) {
	return this.getInputColumnLabels()[i];
  }

  public int getNumOutputFeatures() {
	return getOutputColumnLabels().length;
  }

  public String[] getOutputFeatureNames() {
	return getOutputColumnLabels();
  }

  public String getOutputFeatureName(int i) {
	return this.getOutputColumnLabels()[i];
  }

  
  public void makePredictions(PredictionTable pt) {
    int numOutputs = pt.getNumOutputFeatures();
    double [] predictions = new double[numOutputs];
    try {
      for (int i = 0 ; i < pt.getNumRows() ; i++) {
        this.evaluate(pt, i, predictions);
        for(int j = 0 ; j < numOutputs; j++) {
          pt.setDoublePrediction(predictions[j], i, j);
        }
      }
    } catch (Exception e) {
      System.out.println("makePredictions(PredictionTable pt) failed");

    }
  }

  
  public double error(ExampleTable examples, ErrorFunction errorFunction) throws Exception {
    
    int numExamples = examples.getNumRows();
    
    double errorSum = 0.0;
    for (int e = 0; e < numExamples; e++) {
      double [] predictedOutputs = evaluate(examples, e);
      errorSum += errorFunction.evaluate(examples, e, predictedOutputs, this);
    }
    return errorSum / numExamples;
  }
  
  public void indent(int level) {
    for (int i = 0; i < level; i++) {
      System.out.print(" ");
    }
  }

}
