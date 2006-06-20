package ncsa.d2k.modules.projects.dtcheng.optimizers;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class SingleIntegerParameterSpaceGenerator extends
 ComputeModule {
  
  public String getModuleName() {
    return "SingleIntegerParameterSpaceGenerator";
  }
  public String getModuleInfo() {
    return "This module creates a one dimensional control space from the giving integer ";
    
  }
  
  public String[] getInputTypes () {
    String[] types = { "java.lang.Integer"};
     return types;
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Integer";
    }
    return "";
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Integer";
    }
    return "";
  }
  
  
  
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Parameter Space";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Control Parameter Space for Decision Tree Inducer";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace"};
       return out;
  }
  
  
  public void doit() throws Exception {
    
    Integer integer = (Integer) this.pullInput (0);
    int numControlParameters = 1;
    int maxValue = integer.intValue ();
    
    double[] minControlValues = new double[numControlParameters];
    double[] maxControlValues = new double[numControlParameters];
    double[] defaults = new double[numControlParameters];
    int[] resolutions = new int[numControlParameters];
    int[] types = new int[numControlParameters];
    String[] biasNames = new String[numControlParameters];
    
    int biasIndex = 0;
    
    biasNames[biasIndex] = "InputFeatureNumberSelected";
    minControlValues[biasIndex] = 1;
    maxControlValues[biasIndex] = maxValue;
    defaults[biasIndex] = (int) ((1 + maxValue) / 2);
    resolutions[biasIndex] = maxValue;
    types[biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;
    
    
    ParameterSpace space = new ParameterSpaceImpl();
    space.createFromData(biasNames, minControlValues, maxControlValues,
     defaults, resolutions, types);
    
    
    this.pushOutput (space, 0);
  }
}