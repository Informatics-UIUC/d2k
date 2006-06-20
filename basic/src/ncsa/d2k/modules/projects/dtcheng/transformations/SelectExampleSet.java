package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.examples.ContinuousDoubleExampleTable;


public class SelectExampleSet extends ComputeModule {

  private int inputFeatureNumber = 10;

  public void setInputFeatureNumber(int value) {
    this.inputFeatureNumber = value;
  }


  public int getInputFeatureNumber() {
    return this.inputFeatureNumber;
  }


  private double inputFeatureValue = 1.0;

  public void setInputFeatureValue(double value) {
    this.inputFeatureValue = value;
  }


  public double getInputFeatureValue() {
    return this.inputFeatureValue;
  }


  public String getModuleInfo() {
    return "SelectExampleSet";
  }


  public String getModuleName() {
    return "SelectExampleSet";
  }


  public String[] getInputTypes() {
    String[] types = { "ncsa.d2k.modules.core.datatype.table.ExampleTable" };
    return types;
  }


  public String[] getOutputTypes() {
    String[] types = { "ncsa.d2k.modules.core.datatype.table.ExampleTable" };
    return types;
  }


  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    default:
      return "No such input";
    }
  }


  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    }
    return "";
  }


  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    default:
      return "No such output";
    }
  }


  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    }
    return "";
  }


  ////////////////
  /// WORK ///
  ////////////////
  public void doit() {

    ///////////////////////
    /// PULL INPUTS ///
    ///////////////////////

    ExampleTable originalExamples = (ExampleTable) this.pullInput(0);

    int numExamples = originalExamples.getNumRows();
    int numInputs = originalExamples.getNumInputFeatures();
    int numOutputs = originalExamples.getNumOutputFeatures();

    int selectedFeatureIndex = inputFeatureNumber - 1;

    // count number of times the selected input values occurs //

    int count = 0;
    for (int e = 0; e < numExamples; e++) {
      double value = originalExamples.getInputDouble(e, selectedFeatureIndex);
      if (value == inputFeatureValue)
        count++;
    }

    int transformedNumExamples = count;
    int numFeatures = numInputs + numOutputs;
    String[] inputNames = originalExamples.getInputNames();
    String[] outputNames = originalExamples.getOutputNames();

    double[] data = new double[numExamples * numFeatures];
    ContinuousDoubleExampleTable selectedExamples = new ContinuousDoubleExampleTable(data, transformedNumExamples, numInputs,
        numOutputs, inputNames, outputNames);


    count = 0;
    for (int e = 0; e < numExamples; e++) {

      double value = originalExamples.getInputDouble(e, selectedFeatureIndex);
      if (value == inputFeatureValue) {
        for (int v = 0; v < numInputs; v++) {
          selectedExamples.setInput(count, v, (originalExamples.getInputDouble(e, v)));
        }
        for (int v = 0; v < numOutputs; v++) {
          selectedExamples.setOutput(count, v, (originalExamples.getOutputDouble(e, v)));
        }
        count++;
      }
    }


    ////////////////////////
    /// PUSH OUTPUTS ///
    ////////////////////////

    this.pushOutput(selectedExamples, 0);
  }
}