package ncsa.d2k.modules.projects.dtcheng.models;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.inducers.Model;

import ncsa.d2k.core.modules.*;

public class ApplyModelArrayToDoubleArray extends ComputeModule /* , Reentrant */
{
  private String ErrorFunctionName = "AbsoluteError";

  public void setErrorFunctionName(String value) {
    this.ErrorFunctionName = value;
  }

  public String getErrorFunctionName() {
    return this.ErrorFunctionName;
  }

  private boolean PrintErrors = false;

  public void setPrintErrors(boolean value) {
    this.PrintErrors = value;
  }

  public boolean getPrintErrors() {
    return this.PrintErrors;
  }

  private boolean PrintPredictions = false;

  public void setPrintPredictions(boolean value) {
    this.PrintPredictions = value;
  }

  public boolean getPrintPredictions() {
    return this.PrintPredictions;
  }

  private boolean RecycleExamples = false;

  public void setRecycleExamples(boolean value) {
    this.RecycleExamples = value;
  }

  public boolean getRecycleExamples() {
    return this.RecycleExamples;
  }

  private boolean CreatePredictionVTArrays = false;

  public void setCreatePredictionVTArrays(boolean value) {
    this.CreatePredictionVTArrays = value;
  }

  public boolean getCreatePredictionVTArrays() {
    return this.CreatePredictionVTArrays;
  }

  boolean InitialExecution = true;

  public boolean isReady() {
    boolean value = false;

    if (InitialExecution || (!RecycleExamples)) {
      value = (getFlags()[0] > 0) && (getFlags()[1] > 0);
    } else {
      value = (getFlags()[0] > 0);
    }
    return value;
  }

  public String getModuleInfo() {
    return "ApplyModelArrayToDoubleArray";
  }

  public String getModuleName() {
    return "ApplyModelArrayToDoubleArray";
  }

  public String[] getInputTypes() {
    String[] types = { "[D", "[Ljava.lang.Object;"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = { "[D" };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Inputs";
    case 1:
      return "ModelObjectArray";
    default:
      return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Inputs";
    case 1:
      return "ModelObjectArray";
    default:
      return "No such input";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "CombinedOutputArray";
    default:
      return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "CombinedOutputArray";
    default:
      return "No such output";
    }
  }

  ExampleTable exampleSet = null;

  public void doit() throws Exception {

    Object[] ModelArray = (Object[]) this.pullInput(1);
    int NumModels = ModelArray.length;
    int NumOutputs = ((Model)ModelArray[0]).getNumOutputFeatures();

    double[] ExampleInputs = (double[]) this.pullInput(0);
    double[] ExampleOutputs = new double[NumOutputs];

    double[] Results = new double[NumOutputs * NumModels];

    int index = 0;
    for (int i = 0; i < NumModels; i++) {
      
      ((Model)ModelArray[i]).evaluate(ExampleInputs, ExampleOutputs);

      for (int j = 0; j < ExampleOutputs.length; j++) {
        Results[index++] = ExampleOutputs[j];
      }

    }

    this.pushOutput(Results, 0);
  }
}