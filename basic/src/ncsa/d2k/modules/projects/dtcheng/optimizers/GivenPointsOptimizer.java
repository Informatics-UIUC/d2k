package ncsa.d2k.modules.projects.dtcheng.optimizers;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

//import ncsa.d2k.modules.core.datatype.table.continuous.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.Random;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class GivenPointsOptimizer
 extends ComputeModule
 implements java.io.Serializable {
  
  public PropertyDescription[] getPropertiesDescriptions() {
    
    PropertyDescription[] pds = new PropertyDescription[4];
    int i = 0;
    pds[i++] = new PropertyDescription(
     "objectiveFeatureName",
     "Objective Feature Name",
     "The name given to the objective feature.");
    
    pds[i++] = new PropertyDescription(
     "objectiveScoreOutputFeatureNumber",
     "Objective Score Output Feature Number",
     "Selects which example output feature is used to denote the objective score of the Parameter Point.");
    
    pds[i++] = new PropertyDescription(
     "objectiveScoreDirection",
     "Objective Score Direction",
     "Determines whether the objective score is to be minimized (-1) or maximized (1).");
    
    pds[i++] = new PropertyDescription(
     "trace",
     "Trace",
     "Report extra information during execution to trace the modules execution.");
    
    return pds;
  }
  
  String objectiveFeatureName = "Score";
  public void setObjectiveFeatureName(String value) throws
   PropertyVetoException {
    if (value.length() < 1) {
      throw new PropertyVetoException("length of objectiveFeatureName was less than 1", null);
    }
    this.objectiveFeatureName = value;
  }
  public String getObjectiveFeatureName() {
    return this.objectiveFeatureName;
  }
  
  
  int objectiveScoreOutputFeatureNumber = 1;
  public void setObjectiveScoreOutputFeatureNumber(int value) throws
   PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.objectiveScoreOutputFeatureNumber = value;
  }
  public int getObjectiveScoreOutputFeatureNumber() {
    return this.objectiveScoreOutputFeatureNumber;
  }
  
  
  int objectiveScoreDirection = -1;
  public void setObjectiveScoreDirection(int value) throws
   PropertyVetoException {
    if (! ( (value == -1) || (value == 1))) {
      throw new PropertyVetoException(" must be -1 or 1", null);
    }
    this.objectiveScoreDirection = value;
  }
  public int getObjectiveScoreDirection() {
    return this.objectiveScoreDirection;
  }
  
  
  boolean trace = false;
  public void setTrace(boolean value) {
    this.trace = value;
  }
  
  public boolean getTrace() {
    return this.trace;
  }
  
  public String getModuleName() {
    return "GivenPointsOptimizer";
  }
  
  public String getModuleInfo() {
    return "This module implements an optimizer which selects points according to a given set of " +
     "points to try contained in a user specified table.  ";
    
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Control Parameter Space";
      case 1:
        return "Parameter Point Table";
      case 2:
        return "Example";
    }
    return "";
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The Control Parameter Space to search";
      case 1:
        return "Parameter Point Table";
      case 2:
        return
         "The Example created by combining the Parameter Point and the objective scores";
    }
    return "";
  }
  
  public String[] getInputTypes() {
    String[] in = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
       "ncsa.d2k.modules.core.datatype.table.Table",
       "ncsa.d2k.modules.core.datatype.table.Example"
    };
    return in;
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Parameter Point";
      case 1:
        return "Optimal Example Table";
      case 2:
        return "Complete Example Table";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The next Parameter Point selected for evaluation";
      case 1:
        return "An example table consisting of only the Optimal Example(s)";
      case 2:
        return
         "An example table consisting of all Examples generated during optimization";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
       "ncsa.d2k.modules.core.datatype.table.ExampleTable",
       "ncsa.d2k.modules.core.datatype.table.ExampleTable"
    };
    return out;
  }
  
  private boolean InitialExecution = true;
  private Random randomNumberGenerator = null;
  
  public void beginExecution() {
    
    InitialExecution = true;
    exampleData = null;
    
    if (objectiveScoreDirection == 1) {
      bestObjectiveScore = Double.NEGATIVE_INFINITY;
    } else {
      bestObjectiveScore = Double.POSITIVE_INFINITY;
    }
    bestExampleIndex = Integer.MIN_VALUE;
  }
  
  public boolean isReady() {
    boolean value = false;
    
    if (InitialExecution) {
      value = (this.getFlags()[0] > 0 && this.getFlags()[1] > 0);
    } else {
      value = (this.getFlags()[2] > 0);
    }
    
    return value;
  }
  
  int NumExperimentsCompleted = 0;
  
  Table parameterPointTable;
  int   numParameterPoints;
  int   numParameters;
  ParameterSpace parameterSpace;
  double[] bias;
  double[][][] initialExampleSet;
  int initialNumExamples;
  
  int        numExamplesToCreate;
  int        numExamplesCreated;
  double[][] exampleData;
  
  int []    inputs;
  int []    outputs;
  int       numInputs;
  int       numOutputs;
  String [] inputNames;
  String [] outputNames;
  
  double bestObjectiveScore = 0;
  int bestExampleIndex = Integer.MIN_VALUE;
  
  public void doit() {
    
    if (InitialExecution) {
      
      parameterSpace      = (ParameterSpace)this.pullInput(0);
      parameterPointTable = (Table)         this.pullInput(1);
      
      if (parameterSpace.getNumParameters() != parameterPointTable.getNumColumns()) {
        System.out.println("Error parameterSpace and parameterPointTable dimensions do not agree");
      }
      
      numParameters = parameterPointTable.getNumColumns();
      
      numParameterPoints = parameterPointTable.getNumRows();
      
      numInputs  = numParameters;
      numOutputs = 1;
      int numVars = numInputs + numOutputs;
      
      
      numExamplesToCreate = numParameterPoints;
      numExamplesCreated = 0;
      
      exampleData = new double [numVars][numExamplesToCreate];
      inputs      = new int [numInputs];
      outputs     = new int [numOutputs];
      
      int index = 0;
      for (; index < inputs.length ; index++)
        inputs[index] = index;
      for (int i = 0 ; i < outputs.length ; index++, i++)
        outputs[i] = index;
      
      inputNames = new String[parameterSpace.getNumParameters()];
      for (int i = 0; i < parameterSpace.getNumParameters(); i++) {
        inputNames[i] = parameterSpace.getName(i);
      }
      
      outputNames = new String[numOutputs];
      for (int i = 0; i < numOutputs; i++) {
        outputNames[i] = objectiveFeatureName;
      }
      
      
      
      
      
      InitialExecution = false;
      
      
    } else {
      
      Example example = (Example)this.pullInput(2);
      
      // add example to set
      int index = 0;
      for (int i = 0; i < numInputs; i++) {
        exampleData[index++][numExamplesCreated] = example.getInputDouble(i);
      }
      for (int i = 0; i < numOutputs; i++) {
        exampleData[index++][numExamplesCreated] = example.getOutputDouble(i);
      }
      numExamplesCreated++;
      
      // update best solution so far
        double utility = example.getOutputDouble(objectiveScoreOutputFeatureNumber - 1);
        if (objectiveScoreDirection == 1) {
          if (utility > bestObjectiveScore) {
            bestObjectiveScore = utility;
            bestExampleIndex = numExamplesCreated - 1;
          }
        } else {
          if (utility < bestObjectiveScore) {
            bestObjectiveScore = utility;
            bestExampleIndex = numExamplesCreated - 1;
          }
        }
      
    }
    
    /////////////////////////////////////////
    // quit when necessary and push result //
    /////////////////////////////////////////
    if (numExamplesCreated == numExamplesToCreate) {
      
      if (trace) {
        
        System.out.println("Optimization Completed");
        System.out.println("  numExamplesCreated = " + numExamplesCreated);
        
        System.out.println("  objectiveScoreDirection = " + objectiveScoreDirection);
        System.out.println("  bestObjectiveScore      = " + bestObjectiveScore);
        System.out.println("  bestExampleIndex        = " + (bestExampleIndex + 1));
      }
      
      // add example to set
      double[][] data = new double[exampleData.length][1];
      int index = 0;
      for (int i = 0; i < exampleData.length; i++) {
        data[index++][0] = exampleData[i][bestExampleIndex];
      }
      //ANCA: was this.getTable()
      ExampleTable optimalExampleSet = Utility.getTable(data, inputNames, outputNames,
       inputs, outputs, 1);
      ExampleTable exampleSet = Utility.getTable(exampleData, inputNames, outputNames,
       inputs, outputs, numExamplesCreated);
      this.pushOutput(optimalExampleSet, 1);
      this.pushOutput(exampleSet, 2);
      beginExecution();
      return;
    }
    
    //////////////////////////////////////////////
    // generate next point in bias space to try //
    //////////////////////////////////////////////
    
    String[] names = new String[parameterSpace.getNumParameters()];
    double[] point = new double[parameterSpace.getNumParameters()];
    for (int i = 0; i < parameterSpace.getNumParameters(); i++) {
      names[i] = parameterSpace.getName(i);
      point[i] = parameterPointTable.getDouble(numExamplesCreated, i);
    }
    ParameterPoint parameterPoint = ParameterPointImpl.getParameterPoint(names, point);
    this.pushOutput(parameterPoint, 0);
    
  }
  
  
  
  
}
