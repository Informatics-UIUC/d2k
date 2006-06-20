package ncsa.d2k.modules.projects.dtcheng.models;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.inducers.Model;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.examples.TchengExampleTable;

public class ApplyModelToExampleSet extends ComputeModule {
  private boolean PrintPredictions = false;

  public void setPrintPredictions(boolean value) {
    this.PrintPredictions = value;
  }

  public boolean getPrintPredictions() {
    return this.PrintPredictions;
  }

  private boolean CacheModel = false;

  public void setCacheModel(boolean value) {
    this.CacheModel = value;
  }

  public boolean getCacheModel() {
    return this.CacheModel;
  }

  private boolean SumModelPredictions = false;

  public void setSumModelPredictions(boolean value) {
    this.SumModelPredictions = value;
  }

  public boolean getSumModelPredictions() {
    return this.SumModelPredictions;
  }

  public String getModuleInfo() {
    return "ApplyModelToExampleSet";
  }

  public String getModuleName() {
    return "ApplyModelToExampleSet";
  }

  public String[] getInputTypes() {
    String[] in = { "ncsa.d2k.modules.projects.dtcheng.Model", "ncsa.d2k.modules.projects.dtcheng.examples.TchengExampleTable" };
    return in;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Model";
    case 1:
      return "ExampleSet";
    }
    return "";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Model";
    case 1:
      return "ExampleSet";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = { "ncsa.d2k.modules.core.datatype.table.ExampleTable" };
    return out;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "ExampleSet";
    }
    return "";
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "ExampleSet";
    }
    return "";
  }


  boolean InitialExecution;

  public void beginExecution() {
    InitialExecution = true;
  }


  public boolean isReady() {

    boolean value = false;

    if (InitialExecution) {
      value = (getFlags()[0] > 0) && (getFlags()[1] > 0);
    } else {
      if (!CacheModel)
        value = (getFlags()[0] > 0) || (getFlags()[1] > 0);
      else
        value = (getFlags()[1] > 0);
    }

    return value;
  }


  Model model = null;

  public void doit() throws Exception {

    if (InitialExecution || !CacheModel)
      model = (Model) this.pullInput(0);
    
    TchengExampleTable originalExampleSet = (TchengExampleTable) this.pullInput(1);
    
    int numInputs = originalExampleSet.getNumInputFeatures();
    int numOutputs = originalExampleSet.getNumOutputFeatures();


    if (originalExampleSet == null) {
      this.pushOutput(null, 0);
    } else {

      int numExamples = originalExampleSet.getNumRows();
      int predictionTableNumOutputs = originalExampleSet.getNumOutputFeatures();

      double predictionSum = 0.0;
      
      for (int e = 0; e < numExamples; e++) {
        
        // evaluate model on current examples
        double[] outputs = model.evaluate(originalExampleSet, e);
        for (int o = 0; o < numOutputs; o++) {
          originalExampleSet.setOutput(e, o, outputs[o]);
        }
        
        
        if (PrintPredictions) {
          for (int i = 0; i < numInputs; i++) {
            if (i != 0)
              System.out.print("\t");
            System.out.print(originalExampleSet.getInputDouble(e, i));
          }
          for (int o = 0; o < numOutputs; o++) {
            System.out.print("\t");
            System.out.print(outputs[o]);
          }
          System.out.println();
        }
          
        
        if (SumModelPredictions) {
          for (int o = 0; o < numOutputs; o++) {
            predictionSum += outputs[o];
          }
        }
          
        
      }
      
      if (SumModelPredictions) {
        System.out.println("predictionSum = " + predictionSum);
        System.out.println("numExamples   = " + numExamples);
        System.out.println("avgOutput     = " + predictionSum / numExamples / numOutputs);
      }
      this.pushOutput(originalExampleSet, 0);
    }

    InitialExecution = false;

  }
}
