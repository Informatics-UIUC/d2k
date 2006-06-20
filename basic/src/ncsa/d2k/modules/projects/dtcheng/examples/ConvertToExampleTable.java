// !!!obsolete
package ncsa.d2k.modules.projects.dtcheng.examples;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.examples.ContinuousDoubleExampleTable;

public class ConvertToExampleTable extends ComputeModule {

  public String getModuleInfo() {
    return "Convert2DDToExampleTable";
  }

  public String getModuleName() {
    return "Convert2DDToExampleTable";
  }

  public String getInputName(int index) {
    switch (index) {
    case 0:
      return "Data";
    case 1:
      return "FeatureNames";
    case 2:
      return "OutputFeatureIndex";
    default:
      return "Error! No such input.  ";
    }
  }

  public String getInputInfo(int index) {
    switch (index) {
    case 0:
      return "Data";
    case 1:
      return "FeatureNames";
    case 2:
      return "OutputFeatureIndex";
    default:
      return "Error! No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "[[I", "[Ljava.lang.String;", "java.lang.Integer" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "ContinuousExampleSet";
    default:
      return "Error! No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "ExampleSet";
    default:
      return "Error! No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] out = { "ncsa.d2k.modules.core.datatype.table.ExampleTable" };
    return out;
  }

  public void doit() throws Exception {

    Object InputObject1 = this.pullInput(0);
    Object InputObject2 = this.pullInput(1);
    Object InputObject3 = this.pullInput(2);
    
    if ((InputObject1 == null) || (InputObject2 == null) || (InputObject3 == null)) {
      this.pushOutput(null, 0);
      return;
    }

    int[][] DataArray      = (int[][]) InputObject1;
    String[] FeatureNames  = (String[]) InputObject2;
    int OutputFeatureIndex = ((Integer) InputObject3).intValue();

    int numDataRows = DataArray.length;
    int numDataColumns = DataArray[0].length;

    if (false) {
      System.out.println("numDataRows    = " + numDataRows);
      System.out.println("numDataColumns = " + numDataColumns);
    }

    int numExamples = numDataRows - 1;

    int numInputs = 0;
    int numOutputs = 0;
    int[] inputColumnIndices = new int[numDataColumns];
    int[] outputColumnIndices = new int[numDataColumns];

    for (int i = 0; i < numDataColumns; i++) {
      if (i == OutputFeatureIndex) {
        outputColumnIndices[numOutputs++] = i;
      } else {
        inputColumnIndices[numInputs++] = i;
      }
    }

    int numFeatures = numInputs + numOutputs;

    if (false) {
      System.out.println("numExamples........ " + numExamples);
      System.out.println("numFeatures... " + numFeatures);
      System.out.println("numInputs... " + numInputs);
      System.out.println("numOutputs.. " + numOutputs);
    }

    double[] data = new double[numExamples * numFeatures];

    double value = 0.0;

    int example_index = 0;

    String[] columnNames = new String[numDataColumns];
    for (int f = 0; f < numDataColumns; f++) {
      columnNames[f] = FeatureNames[f];
    }

    for (int e = 0; e < numExamples; e++) {

      for (int i = 0; i < numInputs; i++)
        data[e * numFeatures + i] = DataArray[e][inputColumnIndices[i]];

      for (int i = 0; i < numOutputs; i++)
        data[e * numFeatures + numInputs + i] = DataArray[e][outputColumnIndices[i]];

    }

    String[] inputNames = new String[numInputs];
    String[] outputNames = new String[numOutputs];

    for (int i = 0; i < numInputs; i++) {
      inputNames[i] = columnNames[inputColumnIndices[i]];
    }
    for (int i = 0; i < numOutputs; i++) {
      outputNames[i] = columnNames[outputColumnIndices[i]];
    }

    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(data, numExamples, numInputs, numOutputs, inputNames, outputNames);
    this.pushOutput((ExampleTable) exampleSet, 0);

  }

}