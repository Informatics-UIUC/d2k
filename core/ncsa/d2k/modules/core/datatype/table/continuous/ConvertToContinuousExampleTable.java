package ncsa.d2k.modules.core.datatype.table.continuous;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class ConvertToContinuousExampleTable extends ComputeModule {

  public String getModuleName() {
    return "ConvertToContinuousExampleTable";
  }
  public String getModuleInfo() {
    return "ConvertToContinuousExampleTable";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0: return "ExampleTable";
    }
    return "";
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "ExampleTable";
    }
    return "";
  }
  public String[] getInputTypes() {
    String [] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "ContinuousExampleTable";
    }
    return "";
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "ContinuousExampleTable";
    }
    return "";
  }
  public String[] getOutputTypes() {
    String [] out = {"ncsa.d2k.modules.core.datatype.table.continuous.ContinuousExampleTable"};
    return out;
  }


  public void doit() {

    ExampleTable exampleSet = (ExampleTable) this.pullInput(0);

    int       numExamples = exampleSet.getNumExamples();
    int       numInputs   = exampleSet.getNumInputFeatures();
    int       numOutputs  = exampleSet.getNumOutputFeatures();
    int       numFeatures = numInputs + numOutputs;
    String [] inputNames  = exampleSet.getInputNames();
    String [] outputNames = exampleSet.getOutputNames();

    ContinuousExampleTable continuousExampleSet = new ContinuousExampleTable(
        numExamples,
        numInputs,
        numOutputs,
        inputNames,
        outputNames);


    double [] data = new double[numExamples * numFeatures];

    for (int e = 0; e < numExamples; e++) {

      for (int i = 0; i < numInputs; i++)
        data[e * numFeatures             + i] = exampleSet.getInputDouble(e, i);

      for (int i = 0; i < numOutputs; i++)
        data[e * numFeatures + numInputs + i] = exampleSet.getOutputDouble(e, i);

    }

    continuousExampleSet.data = data;

    this.pushOutput(continuousExampleSet, 0);
  }
}