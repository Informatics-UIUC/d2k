package ncsa.d2k.modules.projects.dtcheng.examples;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


public class ContinuousFloatExampleTable extends ContinuousExampleTable implements ExampleTable, TestTable, TchengExampleTable, java.io.Serializable {
  
  float[]   data;
  double[] predictionData;
  
  public ContinuousFloatExampleTable() {
  }
  
  
  public void initialize(float[] data, int [] group, int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {
    
    super.initialize(group, numExamples, numInputs, numOutputs, inputNames, outputNames);
    
    this.data = data;
  }
  
  
  public ContinuousFloatExampleTable(float[] data, int [] group, int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {
    initialize(data, group, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }
  
  public ContinuousFloatExampleTable(float[] data, int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {
    initialize(data, null, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }
  
  public ContinuousFloatExampleTable(int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {
    float[] data = new float[numExamples * (numInputs + numOutputs)];
    initialize(data, null, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }
  
  public ContinuousFloatExampleTable(int numExamples, boolean useGroups, String[] inputNames, String[] outputNames) {
    int numInputs  = inputNames.length;
    int numOutputs = outputNames.length;
    float[] data = new float[numExamples * (numInputs + numOutputs)];
    int [] groups = null;
    if (useGroups) {
      groups = new int[numExamples];
      for (int i = 0; i < numExamples; i++) {
        groups[i] = -1;
      }
    }
    initialize(data, groups, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }
  
  public Table copy() {
    
    ContinuousFloatExampleTable copy = new ContinuousFloatExampleTable();

    super.copy(copy);
    
    copy.data = (float[]) this.data.clone();

    return (Table) copy;
  }
  
  
  public Table copyByReference() {
    
    ContinuousFloatExampleTable copy = new ContinuousFloatExampleTable();
    
    super.copyByReference(copy);

    copy.data = this.data;
    
    return (Table) copy;
  }
  
  
  public double getInputDouble(int e, int i) {
    return (double) data[exampleIndices[e] * numFeatures + i];
  }
  
  public double getOutputDouble(int e, int i) {
    return (double) data[exampleIndices[e] * numFeatures + numInputs + i];
  }
  
  public void setInput(int e, int i, double value) {
    data[exampleIndices[e] * numFeatures + i] = (float) value;
  }
  
  public void setOutput(int e, int i, double value) {
    data[exampleIndices[e] * numFeatures + numInputs + i] = (float) value;
  }
  
  
  public ExampleTable getExampleTable() {
    return (ExampleTable) this.copy();
  }
  
  
  public Table getTestTable() {
    
    ContinuousDoubleExampleTable table = (ContinuousDoubleExampleTable) this.copy();
    
    int[] newExampleIndices = new int[testingSet.length];
    
    for (int i = 0; i < testingSet.length; i++) {
      newExampleIndices[i] = exampleIndices[testingSet[i]];
    }
    
    table.exampleIndices = newExampleIndices;
    table.numExamples = testingSet.length;
    
    return (TestTable) table;
  }
  
  
  public Table getTrainTable() {
    return (Table) this;
  }
  
  
  public PredictionTable toPredictionTable() {
    predictionData = new double[numOutputs * numExamples];
    return this;
  }
  
  
  public void setDoublePrediction(double prediction, int row, int predictionColIdx) {
    predictionData[row * numOutputs + predictionColIdx] = prediction;
  }
  
  
  public double getDoublePrediction(int row, int predictionColIdx) {
    return predictionData[row * numOutputs + predictionColIdx];
  }
  
  public void deleteInputs(boolean[] deleteFeatures) {
    
    int numFeaturesToDelete = 0;
    
    for (int i = 0; i < numInputs; i++) {
      if (deleteFeatures[i])
        numFeaturesToDelete++;
    }
    
    int NewNumInputs = numInputs - numFeaturesToDelete;
    int NewNumFeatures = NewNumInputs + numOutputs;
    int NewIndex;
    
    float[] NewData = new float[numExamples * NewNumFeatures];
    
    for (int e = 0; e < numExamples; e++) {
      NewIndex = 0;
      for (int i = 0; i < numInputs; i++) {
        if (!deleteFeatures[i]) {
          NewData[e * NewNumFeatures + NewIndex] = data[e * numFeatures + i];
          NewIndex++;
        }
      }
      for (int i = 0; i < numOutputs; i++) {
        NewData[e * NewNumFeatures + NewNumInputs + i] = data[e * numFeatures + numInputs + i];
      }
      
    }
    this.data = NewData;
   
     super.deleteInputs(deleteFeatures);
   
  }
  
  
}
