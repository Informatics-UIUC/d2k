package ncsa.d2k.modules.projects.dtcheng.optimizers;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class SplitParameterPoint extends
 ComputeModule {
  
  
  public String getModuleName () {
    return "SplitParameterPoint";
  }
  
  
  
  public String getModuleInfo () {
    return "This module splits a combined control point into two component control points.";
    
  }
  
  public String[] getInputTypes () {
    String[] out = {
       "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
      "java.lang.Integer",
    };
    return out;
  }
  
  public String[] getOutputTypes () {
    String[] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
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
        return "Parameter Point 1";
      case 1:
        return "Parameter Point 2";
    }
    return "";
  }
  
  public String getInputInfo (int i) {
    switch (i) {
      case 0:
        return "Parameter Point 1";
      case 1:
        return "SplitPoint";
    }
    return "";
  }
  public String getOutputInfo (int i) {
    switch (i) {
      case 0:
        return "Parameter Point 1";
      case 1:
        return "Parameter Point 2";
    }
    return "";
  }
  
  
  
  
  public void doit () throws Exception {
    
    ParameterPoint combinedPoint = (ParameterPoint) this.pullInput (0);
    int            splitPoint    = ((Integer)       this.pullInput (1)).intValue ();
    

    int numCombinedControlParameters = combinedPoint.getNumParameters ();
    
    int numControlParameters1 = splitPoint;
    int numControlParameters2 = numCombinedControlParameters - splitPoint;
    
    
    double[] pointValues1         = new double[numControlParameters1];
    String[] pointDimensionNames1 = new String[numControlParameters1];
    
    double[] pointValues2         = new double[numControlParameters2];
    String[] pointDimensionNames2 = new String[numControlParameters2];
    
    
    int index = 0;
    
    for (int i = 0; i < numControlParameters1; i++) {
      pointDimensionNames1[i] = combinedPoint.getName (index);
      pointValues1[i]         = combinedPoint.getValue (index);
      index++;
    }
    
    for (int i = 0; i < numControlParameters2; i++) {
      pointDimensionNames2[i] = combinedPoint.getName (index);
      pointValues2[i]         = combinedPoint.getValue (index);
      index++;
    }
    
    
    ParameterPoint paramterPoint1 = ParameterPointImpl.getParameterPoint(pointDimensionNames1, pointValues1);
    ParameterPoint paramterPoint2 = ParameterPointImpl.getParameterPoint(pointDimensionNames2, pointValues2);
    
    
    this.pushOutput (paramterPoint1, 0);
    this.pushOutput (paramterPoint2, 1);
  }
}