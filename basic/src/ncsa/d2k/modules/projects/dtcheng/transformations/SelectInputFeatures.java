package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.examples.*;


public class SelectInputFeatures extends ComputeModule {


   public String getModuleInfo() {
    return "This module implements the input feature subset example table transformation.";
  }


  public String getModuleName() {
    return "SelectInputFeatures";
  }
  
  
  public String[] getInputTypes () {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
     "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
     return types;
  }


  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
      case 1:
        return "parameter point";
      default:
        return "no such input";
    }
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
      case 1:
        return "parameter point";
      default:
        return "no such input";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
      default:
        return "No such output";
    }
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
      default:
        return "NO SUCH OUTPUT!";
    }
  }


  public void doit() throws Exception {
    ExampleTable   exampleSet = (ExampleTable)this.pullInput(0);
    ParameterPoint point      = (ParameterPoint)this.pullInput(1);
    
    
    exampleSet = (ExampleTable) exampleSet.copy();
    
    int numInputs = exampleSet.getNumInputFeatures();
    
    
    int numInputFeaturesSelected = point.getNumParameters ();
    
    if (numInputFeaturesSelected >  numInputs) {
      System.out.println("Error:  number of input features selected is greater than the number of input features");
    }

    boolean[] deleteFeatures = new boolean[numInputs];
    for (int i = 0; i < numInputs; i++) {
        deleteFeatures[i] = true;
    }
    
    for (int i = 0; i < numInputFeaturesSelected; i++) {
        deleteFeatures[(int) point.getValue (i) - 1] = false;
    }

    ((TchengExampleTable) exampleSet).deleteInputs(deleteFeatures);

    this.pushOutput(exampleSet, 0);
  }
}
