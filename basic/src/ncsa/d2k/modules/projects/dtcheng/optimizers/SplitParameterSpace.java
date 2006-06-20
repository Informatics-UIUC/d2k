package ncsa.d2k.modules.projects.dtcheng.optimizers;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class SplitParameterSpace extends
 ComputeModule {
  
  
  public String getModuleName () {
    return "SplitParameterSpace";
  }
  
  
  
  public String getModuleInfo () {
    return "This module splits a combined control space into two component control spaces.";
    
  }
  
  public String[] getInputTypes () {
    String[] out = {
       "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
      "java.lang.Integer",
    };
    return out;
  }
  
  public String[] getOutputTypes () {
    String[] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
    };
      return out;
  }
  public String getInputName (int i) {
    switch (i) {
      case 0:
        return "Combined Parameter Space";
      case 1:
        return "SplitPoint";
    }
    return "";
  }
  public String getOutputName (int i) {
    switch (i) {
      case 0:
        return "Parameter Space 1";
      case 1:
        return "Parameter Space 2";
    }
    return "";
  }
  
  public String getInputInfo (int i) {
    switch (i) {
      case 0:
        return "Parameter Space 1";
      case 1:
        return "SplitPoint";
    }
    return "";
  }
  public String getOutputInfo (int i) {
    switch (i) {
      case 0:
        return "Parameter Space 1";
      case 1:
        return "Parameter Space 2";
    }
    return "";
  }
  
  
  
  
  public void doit () throws Exception {
 
    ParameterSpace combinedSpace = (ParameterSpace) this.pullInput (0);
    int splitPoint = ((Integer) this.pullInput (1)).intValue ();

    int numCombinedControlParameters = combinedSpace.getNumParameters ();

    
    int numControlParameters1 = splitPoint;
    int numControlParameters2 = numCombinedControlParameters - splitPoint;
    
    
    double[] minControlValues1 = new double[numControlParameters1];
    double[] maxControlValues1 = new double[numControlParameters1];
    double[] defaults1 = new double[numControlParameters1];
    int[] numRegions1 = new int[numControlParameters1];
    int[] types1 = new int[numControlParameters1];
    String[] biasNames1 = new String[numControlParameters1];
    
    double[] minControlValues2 = new double[numControlParameters2];
    double[] maxControlValues2 = new double[numControlParameters2];
    double[] defaults2 = new double[numControlParameters2];
    int[] numRegions2 = new int[numControlParameters2];
    int[] types2 = new int[numControlParameters2];
    String[] biasNames2 = new String[numControlParameters2];
    
    
    
    int biasIndex = 0;
    
    for (int i = 0; i < numControlParameters1; i++) {
      
      biasNames1[i] = combinedSpace.getName (biasIndex);
      minControlValues1[i] = combinedSpace.getMinValue (biasIndex);
      maxControlValues1[i] = combinedSpace.getMaxValue (biasIndex);
      defaults1[i] = combinedSpace.getDefaultValue (biasIndex);
      numRegions1[i] = combinedSpace.getNumRegions (biasIndex);
      types1[i] = combinedSpace.getType (biasIndex);
      biasIndex++;
    }
    
    for (int i = 0; i < numControlParameters2; i++) {
      biasNames2[i] = combinedSpace.getName (biasIndex);
      minControlValues2[i] = combinedSpace.getMinValue (biasIndex);
      maxControlValues2[i] = combinedSpace.getMaxValue (biasIndex);
      defaults2[i] = combinedSpace.getDefaultValue (biasIndex);
      numRegions2[i] = combinedSpace.getNumRegions(biasIndex);
      types2[i] = combinedSpace.getType (biasIndex);
      biasIndex++;
    }
    
    
    
    ParameterSpace space1 = new ParameterSpaceImpl ();
    space1.createFromData (biasNames1, minControlValues1, maxControlValues1,
     defaults1, numRegions1, types1);
    
    
    ParameterSpace space2 = new ParameterSpaceImpl ();
    space2.createFromData (biasNames2, minControlValues2, maxControlValues2,
     defaults2, numRegions2, types2);
    
    
    this.pushOutput (space1, 0);
    this.pushOutput (space2, 1);
  }
}