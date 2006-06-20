package ncsa.d2k.modules.projects.dtcheng.examples;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.core.modules.ComputeModule;

public class ExampleTableToParameterPointStream extends ComputeModule {
  
  public String getModuleInfo() {
    return "ExampleTableToParameterPointStream";
  }
  public String getModuleName() {
    return "ExampleTableToParameterPointStream";
  }
  
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
    return types;
  }
  
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
    "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Example Table";
      default: return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "Example Table";
      default: return "NO SUCH INPUT!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Parameter Point";
      default: return "No such output";
    }
  }
  
  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Parameter Point";
      default: return "NO SUCH OUTPUT!";
    }
  }
  
  int Count;
  int NumObjectsToPass;
  
  boolean InitialExecution;
  
  public void beginExecution() {
    InitialExecution = true;
    Count = 0;
    NumObjectsToPass = 0;
  }
  
  public boolean isReady() {
    
    if (InitialExecution && (this.getFlags()[0] > 0)) {
      return true;
    } else {
      if (Count < NumObjectsToPass) {
        return true;
      };
    }
    
    return false;
  }
  ExampleTable exampleTable;
  public void doit() {
    
    if (InitialExecution) {
      exampleTable = (ExampleTable) this.pullInput(0);
      InitialExecution = false;
      NumObjectsToPass = exampleTable.getNumRows();
      return;
    }
    
    int numExamples = exampleTable.getNumRows();
    
    
    String [] inputNames  = exampleTable.getInputNames();
    int       numInputs   = exampleTable.getNumInputFeatures();
    
    String [] outputNames = exampleTable.getOutputNames();
    int       numOutputs  = exampleTable.getNumOutputFeatures();
    
      
      double [] inputValues = new double[numInputs];
      for (int i = 0; i < numInputs; i++) {
        inputValues[i] = exampleTable.getInputDouble(Count, i);
      }
      double [] outputValues = new double[numOutputs];
      for (int i = 0; i < numOutputs; i++) {
        outputValues[i] = exampleTable.getOutputDouble(Count, i);
      }
      
      ParameterPoint controlParameterPoint = ParameterPointImpl.getParameterPoint(inputNames, inputValues);
      
      ParameterPoint utilityParameterPoint = ParameterPointImpl.getParameterPoint(outputNames, outputValues);
      
      this.pushOutput(controlParameterPoint, 0);
      this.pushOutput(utilityParameterPoint, 1);
      
      Count++;
      if (Count == NumObjectsToPass) {
        beginExecution();
      }
  }
}
