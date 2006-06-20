package ncsa.d2k.modules.projects.dtcheng.models;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.inducers.Model;

import ncsa.d2k.core.modules.*;

public class ApplyModelToDoubleArray extends ComputeModule  /*, Reentrant*/
{
  private String     ErrorFunctionName = "AbsoluteError";
  public  void    setErrorFunctionName(String value) {       this.ErrorFunctionName = value;}
  public  String  getErrorFunctionName()          {return this.ErrorFunctionName;}
  
  private boolean PrintErrors          = false;
  public  void    setPrintErrors       (boolean value) {       this.PrintErrors       = value;}
  public  boolean getPrintErrors       ()              {return this.PrintErrors;}
  
  private boolean PrintPredictions          = false;
  public  void    setPrintPredictions       (boolean value) {       this.PrintPredictions       = value;}
  public  boolean getPrintPredictions       ()              {return this.PrintPredictions;}
  
  private boolean    RecycleExamples = false;
  public  void    setRecycleExamples(boolean value) {       this.RecycleExamples = value;}
  public  boolean getRecycleExamples()              {return this.RecycleExamples;}
  
  private boolean CreatePredictionVTArrays     = false;
  public  void    setCreatePredictionVTArrays(boolean value) {       this.CreatePredictionVTArrays       = value;}
  public  boolean getCreatePredictionVTArrays()              {return this.CreatePredictionVTArrays;}
  
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
    return "ApplyModelToDoubleArray";
  }
  public String getModuleName() {
    return "ApplyModelToDoubleArray";
  }
  
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.projects.dtcheng.Model","[D"};
    return types;
  }
  
  public String[] getOutputTypes() {
    String[] types = {"[D"};
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      case 1: return "Double Array";
      default: return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "Model";
      case 1:
        return "Actual Inputs";
      default: return "NO SUCH INPUT!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Double Array";
      default: return "No such output";
    }
  }
  
  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Predicted Outputs";
      default: return "NO SUCH OUTPUT!";
    }
  }
  
  
  
  ExampleTable exampleSet = null;
  
  public void doit() throws Exception {
    
    Model model = (Model) this.pullInput(0);
    int NumOutputs = model.getNumOutputFeatures();
    
    double [] ExampleInputs  = (double []) this.pullInput(1);
    double [] ExampleOutputs = new double[NumOutputs];
    
    model.evaluate(ExampleInputs, ExampleOutputs);
    
    
    this.pushOutput(ExampleOutputs, 0);
  }
}
