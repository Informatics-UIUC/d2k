package ncsa.d2k.modules.projects.dtcheng.examples;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


public class ContinuousDoubleExampleTable extends ContinuousExampleTable implements ExampleTable, TestTable, TchengExampleTable, java.io.Serializable {
  
  double[] data;
  double[] predictionData;
  
  public ContinuousDoubleExampleTable() {
  }
  
  
  public void initialize(double[] data, int [] group, int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {
    
    super.initialize(group, numExamples, numInputs, numOutputs, inputNames, outputNames);
    
    this.data = data;
  }
  
  
  public ContinuousDoubleExampleTable(double[] data, int [] group, int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {
    initialize(data, group, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }
  
  public ContinuousDoubleExampleTable(double[] data, int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {
    initialize(data, null, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }
  
  public ContinuousDoubleExampleTable(int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {
    double[] data = new double[numExamples * (numInputs + numOutputs)];
    initialize(data, null, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }
  
  public ContinuousDoubleExampleTable(int numExamples, boolean useGroups, String[] inputNames, String[] outputNames) {
    int numInputs  = inputNames.length;
    int numOutputs = outputNames.length;
    double[] data = new double[numExamples * (numInputs + numOutputs)];
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
    
    ContinuousDoubleExampleTable copy = new ContinuousDoubleExampleTable();

    super.copy(copy);
    
    copy.data = (double[]) this.data.clone();

    return (Table) copy;
  }
  
  
  public Table copyByReference() {
    
    ContinuousDoubleExampleTable copy = new ContinuousDoubleExampleTable();
    
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
    data[exampleIndices[e] * numFeatures + i] = value;
  }
  
  public void setOutput(int e, int i, double value) {
    data[exampleIndices[e] * numFeatures + numInputs + i] = value;
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
    
    double[] NewData = new double[numExamples * NewNumFeatures];
    
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
