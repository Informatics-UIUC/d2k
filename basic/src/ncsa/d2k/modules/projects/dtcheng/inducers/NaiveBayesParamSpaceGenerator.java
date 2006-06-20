package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class NaiveBayesParamSpaceGenerator extends AbstractParamSpaceGenerator {
  
  
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Parameter Space";
      case 1:
        return "Function Inducer Class";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Control Parameter Space";
      case 1:
        return "Function Inducer Class";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
       "java.lang.Class" };
       return out;
  }
  
  int numControlParameters = 2;
  protected ParameterSpace getDefaultSpace() {
    
    double[] minControlValues = new double[numControlParameters];
    double[] maxControlValues = new double[numControlParameters];
    double[] defaults = new double[numControlParameters];
    int[] resolutions = new int[numControlParameters];
    int[] types = new int[numControlParameters];
    String[] biasNames = new String[numControlParameters];
    
    int biasIndex = 0;
    
    biasNames       [biasIndex] = "NumRounds";
    minControlValues[biasIndex] = 1;
    maxControlValues[biasIndex] = 20;
    defaults        [biasIndex] = 0;
    resolutions     [biasIndex] = (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] + 1;
    types[biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;
    biasNames       [biasIndex] = "Direction";
    minControlValues[biasIndex] = -1;
    maxControlValues[biasIndex] = 1;
    defaults        [biasIndex] = 0;
    resolutions     [biasIndex] = (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] + 1;
    types[biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;

    
    ParameterSpace psi = new ParameterSpaceImpl();
    psi.createFromData(biasNames, minControlValues, maxControlValues,
     defaults, resolutions, types);
    return psi;
    
  }
  
  /**
   * REturn a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "Naive Bayes Param Space Generator";
  }
  
  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  
  public PropertyDescription[] getPropertiesDescriptions() {
    
    PropertyDescription[] pds = new PropertyDescription[numControlParameters];
    
    int i = 0;
    pds[i++] = new PropertyDescription("NumRounds",
     "NumRounds",
     "NumRounds");
    
    pds[i++] = new PropertyDescription("Direction",
     "Direction",
     "Direction");
    
    
    return pds;
  }
  
  /**
   * All we have to do here is push the parameter space and function inducer class.
   */
  public void doit() throws Exception {
    
    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.projects.dtcheng.inducers.NaiveBayesInducerOpt");
    } catch (Exception e) {
      //System.out.println("could not find class");
      //throw new Exception();
      throw new Exception(getAlias()
      + ": could not find class NaiveBayesInducerOpt ");
    }
    
    if (space == null)
      space = this.getDefaultSpace();
    
    this.pushOutput(space, 0);
    this.pushOutput(functionInducerClass, 1);
  }
}