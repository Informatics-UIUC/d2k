package ncsa.d2k.modules.projects.dtcheng.optimizers;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class CombineParameterSpaces extends
 ComputeModule {
  
  
  public String[] getInputTypes () {
    String[] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
       "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
    };
    return out;
  }
  
  public String[] getOutputTypes () {
    String[] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
       "java.lang.Integer",
    };
      return out;
  }
  public String getInputName (int i) {
    switch (i) {
      case 0:
        return "Parameter Space 1";
      case 1:
        return "Parameter Space 2";
    }
    return "";
  }
  public String getOutputName (int i) {
    switch (i) {
      case 0:
        return "Parameter Space";
      case 1:
        return "Split Index";
    }
    return "";
  }
  
  public String getInputInfo (int i) {
    switch (i) {
      case 0:
        return "Parameter Space 1";
      case 1:
        return "Parameter Space 2";
    }
    return "";
  }
  public String getOutputInfo (int i) {
    switch (i) {
      case 0:
        return "Combined Parameter Space";
      case 1:
        return "Split Index";
    }
    return "";
  }
  
  
  
  
  
  public String getModuleInfo () {
    return "This module creates a combined control space from to component control spaces.";
    
  }
  
  public String getModuleName () {
    return "CombineParameterSpaces";
  }
  
  public void doit () throws Exception {
    
    ParameterSpace space1 = (ParameterSpace) this.pullInput (0);
    ParameterSpace space2 = (ParameterSpace) this.pullInput (1);
    
    
    int numControlParameters1 = space1.getNumParameters ();
    int numControlParameters2 = space2.getNumParameters ();
    int numControlParameters = numControlParameters1 + numControlParameters2;
    
    double[] minControlValues = new double[numControlParameters];
    double[] maxControlValues = new double[numControlParameters];
    double[] defaults = new double[numControlParameters];
    int[] numRegions = new int[numControlParameters];
    int[] types = new int[numControlParameters];
    String[] biasNames = new String[numControlParameters];
    
    
    
    int biasIndex = 0;
    
    for (int i = 0; i < numControlParameters1; i++) {
      
      biasNames[biasIndex] = space1.getName (i);
      minControlValues[biasIndex] = space1.getMinValue (i);
      maxControlValues[biasIndex] = space1.getMaxValue (i);
      defaults[biasIndex] = space1.getDefaultValue (i);
      numRegions[biasIndex] = space1.getNumRegions (i);
      types[biasIndex] = space1.getType (i);
      biasIndex++;
    }
    for (int i = 0; i < numControlParameters2; i++) {
      
      biasNames[biasIndex] = space2.getName (i);
      minControlValues[biasIndex] = space2.getMinValue (i);
      maxControlValues[biasIndex] = space2.getMaxValue (i);
      defaults[biasIndex] = space2.getDefaultValue (i);
      numRegions[biasIndex] = space2.getNumRegions (i);
      types[biasIndex] = space2.getType (i);
      biasIndex++;
    }
    
    
    
    ParameterSpace space = new ParameterSpaceImpl ();
    space.createFromData (biasNames, minControlValues, maxControlValues,
     defaults, numRegions, types);
    
    
    this.pushOutput (space, 0);
    this.pushOutput (new Integer(numControlParameters1), 1);
  }
}