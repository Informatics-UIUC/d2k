package ncsa.d2k.modules.core.datatype.model;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.*;
public class Model extends PredictionModelModule implements java.io.Serializable {


  protected Model(ExampleTable examples) {
    super(examples);
  }

  protected Model(int trainingSetSize, String[] inputColumnLabels, String[] outputColumnLabels,
                  int[] inputFeatureTypes, int[] outputFeatureTypes) {
    super(trainingSetSize, inputColumnLabels, outputColumnLabels, inputFeatureTypes, outputFeatureTypes);
  }

  public double [] Evaluate(ExampleTable exampleSet, int e) throws Exception {
    System.out.println("must override this method");
    throw new Exception();
  }

  public void Evaluate(ExampleTable exampleSet, int e, double [] outputs) throws Exception {
    int numOutputs = exampleSet.getNumOutputFeatures();
    double [] internalOutputs = Evaluate(exampleSet, e);
    for (int i = 0; i < numOutputs; i++) {
      outputs[i] = internalOutputs[i];
    }
  }

  public void print(ModelPrintOptions options) throws Exception {
    System.out.println("must override this method");
    throw new Exception();
  }

  public int getNumInputs() {
    return getInputColumnLabels().length;
  }

  //renaming

  public String[] getInputFeatureNames() {
    return getInputColumnLabels();
    }

  public String getInputFeatureName(int i) {
    return this.getInputColumnLabels()[i];
  }

  public int getNumOutputs() {
    return getOutputColumnLabels().length;
  }

  public String[] getOutputFeatureNames() {
    return getOutputColumnLabels();
  }

  public String getOutputFeatureName(int i) {
    return this.getOutputColumnLabels()[i];
  }


  public void makePredictions(PredictionTable pt) {
  }

  protected void makePrediction(ExampleTable example, int row, double [] predictedOutputs) {
  }

}