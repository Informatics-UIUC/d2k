package ncsa.d2k.modules.projects.dtcheng.examples;

import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.util.Random;
import java.util.Hashtable;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;
import ncsa.d2k.modules.projects.dtcheng.primitive.Utility;

public class RandomizeExamples extends ComputeModule {
  
  public String getModuleName() {
    return "RandomizeExamples";
  }
  
  public String getModuleInfo() {
    return "This module randomizes an eample table.";
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Example Table";
      default:
        return "No such input!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Example Table";
      default:
        return "No such input!";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Example Table";
      default:
        return "No such output!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Example Table";
      default:
        return "No such output!";
    }
  }
  
  public String[] getOutputTypes() {
    
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }
  
  
  
  
  
  
  
  
  private int RandomSeed = -1;
  
  public void setRandomSeed (int value) throws PropertyVetoException {
    this.RandomSeed = value;
  }
  
  public int getRandomSeed () {
    return this.RandomSeed;
  }
  
  
  
  
  
  
  
  
  
  
  
  TchengExampleTable ExampleSet;
  
  int NumExamples;
  
  int NumInputs;
  
  int NumOutputs;
  
  
  Random RandomNumberGenerator;
  
  int[] RandomizedIndices = null;
  int[] RandomOffsets = null;
  
  int    NumGroups;
  int [] ExampleGroups;
  int [] GroupNumExamples;
  
  
  
  ParameterPoint objectivePoint;
  
  public void doit() throws Exception {
    
    
    ExampleSet = (TchengExampleTable) this.pullInput(0);
    
    NumExamples = ExampleSet.getNumRows();
    NumInputs = ExampleSet.getNumInputFeatures();
    NumOutputs = ExampleSet.getNumOutputFeatures();
    
    
    
    if ((RandomizedIndices == null) || (RandomizedIndices.length < NumExamples)) {
        RandomizedIndices = new int[NumExamples];
    }
    
    RandomNumberGenerator = new Random(RandomSeed);
    
    int[] randomizedExampleIndicies = new int[NumExamples];
      
      for (int e = 0; e < NumExamples; e++) {
        randomizedExampleIndicies[e] = e;
      }
      
      Utility.randomizeIntArray(RandomNumberGenerator, randomizedExampleIndicies, NumExamples);
      
    
    
    Table randomizedExampleSet = (ExampleTable) ExampleSet.getSubset(randomizedExampleIndicies);
    
    this.pushOutput(randomizedExampleSet, 0);
    
    
  }
}