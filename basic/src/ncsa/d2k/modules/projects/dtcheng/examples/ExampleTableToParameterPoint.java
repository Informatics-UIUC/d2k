package ncsa.d2k.modules.projects.dtcheng.examples;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.core.modules.ComputeModule;

public class ExampleTableToParameterPoint extends ComputeModule {
  
  
  private int ExampleNumber = 1;
  
  public void setExampleNumber(int value) {
    this.ExampleNumber = value;
  }
  
  public int getExampleNumber() {
    return this.ExampleNumber;
  }
  
  
  public String getModuleInfo() {
    return "ExampleTableToParameterPoint";
  }
  public String getModuleName() {
    return "ExampleTableToParameterPoint";
  }
  
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
    return types;
  }
  
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
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
  
  public void doit() {
    ExampleTable exampleTable = (ExampleTable) this.pullInput(0);
    
    String [] inputNames = exampleTable.getInputNames();
    int numInputs = exampleTable.getNumInputFeatures();
    double [] inputValues = new double[numInputs];
    
    for (int i = 0; i < numInputs; i++) {
      inputValues[i] = exampleTable.getInputDouble(ExampleNumber - 1, i);
    }
    
    ParameterPoint paramterPoint = ParameterPointImpl.getParameterPoint(inputNames, inputValues);
    
    this.pushOutput(paramterPoint, 0);
  }
}
