package ncsa.d2k.modules.projects.dtcheng.evaluators;

import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.examples.*;
import ncsa.d2k.core.modules.*;
import java.util.Random;
import java.util.Hashtable;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;
import ncsa.d2k.modules.projects.dtcheng.primitive.Utility;

public class BiasEvaluationController extends ComputeModule {
  
  int numProperties = 12;
  
  public PropertyDescription[] getPropertiesDescriptions () {
    
    PropertyDescription[] pds = new PropertyDescription[numProperties];
    
    int i = 0;
    
    pds[i++] = new PropertyDescription ("useNumbers", "Use Numbers",
     "Use integer numbers to define size of training and testing sets");
    
    pds[i++] = new PropertyDescription ("numTrainExamples", "Num Train Examples",
     "The number of examples in each training set generated and if set to -1 all but Num Test Examples are used");
    
    pds[i++] = new PropertyDescription ("numTestExamples", "Num Test Examples",
     "The number of examples in each testing set generated and if set to -1 all but Num Train Examples are used");
    
    pds[i++] = new PropertyDescription ("useFractions", "Use Fractions",
     "Use fractional units to define size of training and testing sets");
    
    pds[i++] = new PropertyDescription ("fractionTrainExamples", "Fraction Train Examples",
     "The fraction of examples in each training set generated and if set to -1 all but Num Test Examples are used");
    
    pds[i++] = new PropertyDescription ("fractionTestExamples", "Fraction Test Examples",
     "The fraction of examples in each testing set generated and if set to -1 all but Num Train Examples are used");
    
    pds[i++] = new PropertyDescription ("numRepetitions", "Num Repetitions", "Number of train/test set combinations to generate");
    
    
    pds[i++] = new PropertyDescription ("batchSize", "Batch Size",
     "The maximum number of parallel evaluations to allow and must be equal or less than Num Repetitions");
    
    pds[i++] = new PropertyDescription ("randomSeed", "Random Seed",
     "The initial seed to the random number generator used to randomly select examples for training and testing sets");
    
    pds[i++] = new PropertyDescription ("recycleExamples", "Recycle Examples",
     "If true, a single example set is used by the module repeatedly for all subsequent module firings, otherwise "
     + "a new example set is used for each module firing");
    
    pds[i++] = new PropertyDescription ("timeSeriesMode", "Time Series Mode",
     "If true, a single example sets are chosen such that the training and testing sets are contiguous in time");
    
    pds[i++] = new PropertyDescription ("groupedCrossValidationMode", "Grouped Cross Validation Mode",
     "If true, grouped cross validation will be used.  " +
     "The input feature specifed by GroupFeatureName will be used to define ");
    
    return pds;
  }
  
  
  private boolean UseNumbers = false;
  
  public void setUseNumbers (boolean value) {
    this.UseNumbers = value;
  }
  
  public boolean getUseNumbers () {
    return this.UseNumbers;
  }
  
  
  private int NumTrainExamples = -1;
  
  public void setNumTrainExamples (int value) throws PropertyVetoException {
    if ((value == -1) && (NumTestExamples == -1)) {
      throw new PropertyVetoException ("both Num Test Examples and Num Train Examples cannot be -1", null);
    }
    
    if (((value != -1) && (value < 1))) {
      throw new PropertyVetoException ("< 1", null);
    }
    
    this.NumTrainExamples = value;
  }
  
  public int getNumTrainExamples () {
    return this.NumTrainExamples;
  }
  
  private int NumTestExamples = 999999999;
  
  public void setNumTestExamples (int value) throws PropertyVetoException {
    if ((value == -1) && (NumTrainExamples == -1)) {
      throw new PropertyVetoException ("both Num Test Examples and Num Test Examples cannot be -1", null);
    }
    
    if (((value != -1) && (value < 1))) {
      throw new PropertyVetoException ("< 1", null);
    }
    this.NumTestExamples = value;
  }
  
  public int getNumTestExamples () {
    return this.NumTestExamples;
  }
  
  
  private boolean UseFractions = true;
  
  public void setUseFractions (boolean value) {
    this.UseFractions = value;
  }
  
  public boolean getUseFractions () {
    return this.UseFractions;
  }
  
  
  private double FractionTrainExamples = 0.9;
  
  public void setFractionTrainExamples (double value) throws PropertyVetoException {
    
    if (value < 0) {
      throw new PropertyVetoException ("< 0", null);
    }
    if (value > 1) {
      throw new PropertyVetoException ("> 1", null);
    }
    
    this.FractionTrainExamples = value;
  }
  
  public double getFractionTrainExamples () {
    return this.FractionTrainExamples;
  }
  
  private double FractionTestExamples = 0.1;
  
  public void setFractionTestExamples (double value) throws PropertyVetoException {
    if (value < 0) {
      throw new PropertyVetoException ("< 0", null);
    }
    if (value > 1) {
      throw new PropertyVetoException ("> 1", null);
    }
    this.FractionTestExamples = value;
  }
  
  public double getFractionTestExamples () {
    return this.FractionTestExamples;
  }
  
  
  
  private int NumRepetitions = 10;
  
  public void setNumRepetitions (int value) throws PropertyVetoException {
    if (!(value >= 1)) {
      throw new PropertyVetoException ("< 1", null);
    }
    this.NumRepetitions = value;
  }
  
  public int getNumRepetitions () {
    return this.NumRepetitions;
  }
  
  private int RandomSeed = 123;
  
  public void setRandomSeed (int value) {
    this.RandomSeed = value;
  }
  
  public int getRandomSeed () {
    return this.RandomSeed;
  }
  
  private int BatchSize = 1;
  
  public void setBatchSize (int value) throws PropertyVetoException {
    if (!(value >= 1)) {
      throw new PropertyVetoException ("< 1", null);
    }
    this.BatchSize = value;
  }
  
  public int getBatchSize () {
    return this.BatchSize;
  }
  
  private boolean RecycleExamples = false;
  
  public void setRecycleExamples (boolean value) {
    this.RecycleExamples = value;
  }
  
  public boolean getRecycleExamples () {
    return this.RecycleExamples;
  }
  
  private boolean TimeSeriesMode = false;
  
  public void setTimeSeriesMode (boolean value) {
    this.TimeSeriesMode = value;
  }
  
  public boolean getTimeSeriesMode () {
    return this.TimeSeriesMode;
  }
  
  
  private boolean GroupedCrossValidationMode = false;
  
  public void setGroupedCrossValidationMode (boolean value) {
    this.GroupedCrossValidationMode = value;
  }
  
  public boolean getGroupedCrossValidationMode () {
    return this.GroupedCrossValidationMode;
  }
  
  
  
  public String getModuleName () {
    return "BiasEvaluationController";
  }
  
  public String getModuleInfo () {
    return "This module is the control center for evaluating a control space point that involves the generation of training and "
     + "testing example tables.  Random sampling is performed without replacement.  "
     + "The sum of training and testing examples can be less than the total number of examples but not more.";
  }
  
  public String getInputName (int i) {
    switch (i) {
      case 0:
        return "Control Parameter Point";
      case 1:
        return "Example Table";
      case 2:
        return "Individual Objective Space Point";
      default:
        return "No such input!";
    }
  }
  
  public String getInputInfo (int i) {
    switch (i) {
      case 0:
        return "The point in control space which is currently being evaluated";
      case 1:
        return "The example table used to generate training and testing example tables";
      case 2:
        return "The point in objective space resulting from evaluating the last pair of train and test tables";
      default:
        return "No such input!";
    }
  }
  
  public String[] getInputTypes () {
    String[] types = { "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
     "ncsa.d2k.modules.core.datatype.table.ExampleTable", "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint" };
     return types;
  }
  
  public String getOutputName (int i) {
    switch (i) {
      case 0:
        return "Control Parameter Point";
      case 1:
        return "Train Example Table";
      case 2:
        return "Test Example Table";
      case 3:
        return "Averaged Objective Space Point";
      case 4:
        return "All Objective Space Points";
      default:
        return "No such output!";
    }
  }
  
  public String getOutputInfo (int i) {
    switch (i) {
      case 0:
        return "The point in control space which is currently being evaluated and is replicated for each train test table set";
      case 1:
        return "The example table used for the training phase of the external evaluation process";
      case 2:
        return "The example table used for the testing phase of the external evaluation process";
      case 3:
        return "The point in objective space resulting from averaging (i.e., computing the centroid) of all the individual "
         + "train/test objective space points";
      case 4:
        return "An array of objective space points for each train/test pairing";
      default:
        return "No such output!";
    }
  }
  
  public String[] getOutputTypes () {
    String[] types = { "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
     "ncsa.d2k.modules.core.datatype.table.ExampleTable", "ncsa.d2k.modules.core.datatype.table.ExampleTable",
     "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint", "[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint;", };
     return types;
  }
  
  int PhaseIndex;
  
  int ExampleSetIndex;
  
  int UtilityIndex;
  
  boolean InitialExecution;
  
  public void reset () {
    PhaseIndex = 0;
    ExampleSetIndex = 0;
    UtilityIndex = 0;
  }
  
  public void beginExecution () {
    InitialExecution = true;
    reset ();
  }
  
  public boolean isReady () {
    boolean value = false;
    
    switch (PhaseIndex) {
      case 0:
        if (InitialExecution || (!RecycleExamples)) {
          value = (getFlags ()[0] > 0) && (getFlags ()[1] > 0);
        } else {
          value = (getFlags ()[0] > 0);
        }
        break;
        
      case 1:
        value = true;
        break;
        
      case 2:
        value = (getFlags ()[2] > 0);
        break;
        
      case 3:
        value = true;
        break;
    }
    
    return value;
  }
  
  ParameterPoint ControlPoint;
  
  Object ErrorFunction = null;
  
  TchengExampleTable ExampleSet;
  
  int NumExamples;
  
  int NumInputs;
  
  int NumOutputs;
  
  ParameterPoint[] UtilityValues;
  
  double[] UtilitySums;
  
  Random RandomNumberGenerator;
  
  int[] RandomizedIndices = null;
  int[] RandomOffsets = null;
  
  int    NumGroups;
  int [] ExampleGroups;
  int [] GroupNumExamples;
  
  
  
  
  public boolean modulePredictsOutputBehavior() {
    return true;
  }
  
  int [] outputBehavior = new int[5]; 
int numUtilities;
  
  public int[] predictOutputBehavior() {
    
    
    switch (PhaseIndex) {
      
      /*  phase #0, read control point */
      case 0:
      outputBehavior[0] = 0;
      outputBehavior[1] = 0;
      outputBehavior[2] = 0;
      outputBehavior[3] = 0;
      outputBehavior[4] = 0;
      break;
        //  phase #1  output control point(s) to be evaluated
        
      case 1:
      outputBehavior[0] = 1;
      outputBehavior[1] = 1;
      outputBehavior[2] = 1;
      outputBehavior[3] = 0;
      outputBehavior[4] = 0;
      break;
      
      
        // collect results
      case 2:
      outputBehavior[0] = 0;
      outputBehavior[1] = 0;
      outputBehavior[2] = 0;
      outputBehavior[3] = 0;
      outputBehavior[4] = 0;
      break;
      
        // average evaluation results
      
      case 3:
      outputBehavior[0] = 0;
      outputBehavior[1] = 0;
      outputBehavior[2] = 0;
      outputBehavior[3] = 0;
      outputBehavior[4] = 0;
      break;
    }
      
      
    return outputBehavior;
  }
  
  ParameterPoint objectivePoint;
  
  public void doit () throws Exception {
    
    switch (PhaseIndex) {
      
      /*  phase #0, read control point */
      
      case 0:
        
        ControlPoint = (ParameterPoint) this.pullInput (0);
        
        if (InitialExecution || (!RecycleExamples)) {
          
          ExampleSet = (TchengExampleTable) this.pullInput (1);
          
          NumExamples = ExampleSet.getNumRows ();
          NumInputs = ExampleSet.getNumInputFeatures ();
          NumOutputs = ExampleSet.getNumOutputFeatures ();
          
          
          
          if (UseFractions) {
            NumTrainExamples = (int) (NumExamples * FractionTrainExamples);
            NumTestExamples =  (int) (NumExamples * FractionTestExamples);
            NumTrainExamples = NumExamples - (int) (NumExamples * FractionTestExamples);
          }
          
          if (UseNumbers) {
            if (NumTrainExamples == -1) {
              NumTrainExamples = NumExamples - NumTestExamples;
            }
            
            if (NumTestExamples == -1) {
              NumTestExamples = NumExamples - NumTrainExamples;
            }
          }
          
          if (NumTrainExamples >= NumExamples) {
            System.out.println ("NumTrainExamples >= numExamples");
            throw new Exception ();
          }
          
          if (NumTrainExamples + NumTestExamples > NumExamples) {
            NumTestExamples = NumExamples - NumTrainExamples;
          }
          
          if ((RandomizedIndices == null) || (RandomizedIndices.length < NumExamples)) {
            if (!TimeSeriesMode)
              RandomizedIndices = new int[NumExamples];
            else
              RandomOffsets = new int[NumOutputs];
          }
          
          
          
          
          
          
          
          
          
          
          /* analyse groups if used grouped cross validation*/
          if (GroupedCrossValidationMode) {
            
            NumGroups = ExampleSet.getNumGroups ();
            System.out.println ("num groups = " + NumGroups);
            
            GroupNumExamples = new int[NumGroups];
            
            
            for (int i = 0; i < NumExamples; i++) {
              int groupIndex = ExampleSet.getGroup (i);
              GroupNumExamples[groupIndex]++;
            }
            
            NumRepetitions = NumGroups;
          }

          
          
          
          
          
          
          
          
          
          InitialExecution = false;
        }
        
        RandomNumberGenerator = new Random (RandomSeed);
        
        
        
        
        
        
        
        PhaseIndex = 1;
        break;
        
        
        //  phase #1  output control point(s) to be evaluated
        
      case 1:
        
        
        if ((ExampleSetIndex - UtilityIndex < BatchSize) && (ExampleSetIndex < NumRepetitions)) {
          
          int groupIndex = -1;
          
          if (GroupedCrossValidationMode) {
           groupIndex = ExampleSetIndex % NumGroups;
          }
          
          int[] trainSetIndicies = null;
          int[] testSetIndicies = null;
          
          if (!GroupedCrossValidationMode) {
            
            trainSetIndicies = new int[NumTrainExamples];
            testSetIndicies = new int[NumTestExamples];
            
          } else {
            
            trainSetIndicies = new int[NumExamples - GroupNumExamples[groupIndex]];
            testSetIndicies = new int[GroupNumExamples[groupIndex]];
          }

          if (!TimeSeriesMode) {
            
            
            if (GroupedCrossValidationMode) {
              
              int testIndex = 0;
              int trainIndex = 0;
              
              for (int e = 0; e < NumExamples; e++) {
                if ( ExampleSet.getGroup (e) == groupIndex) {
                testSetIndicies[testIndex++] = e;
                } else {
                trainSetIndicies[trainIndex++] = e;
                }
              
              }
              
            } else {
              
              for (int e = 0; e < NumExamples; e++) {
                RandomizedIndices[e] = e;
              }
              
              Utility.randomizeIntArray (RandomNumberGenerator, RandomizedIndices, NumExamples);
              
              for (int e = 0; e < NumTrainExamples; e++) {
                trainSetIndicies[e] = RandomizedIndices[e];
              }
              for (int e = 0; e < NumTestExamples; e++) {
                testSetIndicies[e] = RandomizedIndices[e + NumTrainExamples];
              }
            }
          } else {
            
            int NumClasses = NumOutputs;
            int TrainExampleIndex = 0;
            int TestExampleIndex = 0;
            int ExamplesPerClass = NumExamples / NumClasses;
            int NumTrainExamplesPerClass = NumTrainExamples / NumClasses;
            int NumTestExamplesPerClass = NumTestExamples / NumClasses;
            
            
            System.out.println ("NumClasses = " + NumClasses);
            
            if ((NumTrainExamplesPerClass + NumTestExamplesPerClass) % NumClasses != 0) {
              
              System.out.println ("Error! ((NumTrainExamplesPerClass + NumTestExamplesPerClass) % NumClasses != 0)");
              throw new Exception ();
            }
            
            
            
            for (int c = 0; c < NumClasses; c++) {
              RandomOffsets[c] = RandomNumberGenerator.nextInt (ExamplesPerClass);
            }
            
            for (int c = 0; c < NumClasses; c++) {
              int StartExampleIndex = ExamplesPerClass * c;
              int EndExampleIndex   = ExamplesPerClass * (c + 1);
              int CurrentExampleIndex = StartExampleIndex + RandomOffsets[c];
              
              for (int e = 0; e < NumTrainExamplesPerClass; e++) {
                trainSetIndicies[TrainExampleIndex++] = CurrentExampleIndex++;
                
                if (CurrentExampleIndex == EndExampleIndex)
                  CurrentExampleIndex = StartExampleIndex;
              }
              
              int skip = (NumExamples - NumTrainExamplesPerClass - NumTestExamplesPerClass) / 2;
              
              for (int i = 0; i < skip; i++) {
                CurrentExampleIndex++;
                if (CurrentExampleIndex == EndExampleIndex)
                  CurrentExampleIndex = StartExampleIndex;  // enforce circular buffer
              }
              
              for (int e = 0; e < NumTestExamplesPerClass; e++) {
                testSetIndicies[TestExampleIndex++] = CurrentExampleIndex++;
                
                if (CurrentExampleIndex == EndExampleIndex)
                  CurrentExampleIndex = StartExampleIndex;  // enforce circular buffer
              }
            }
          }
          
          Table currentTrainExampleSet, currentTestExampleSet;
          String ExampleSetClass = ExampleSet.getClass ().toString ();
          
          currentTrainExampleSet = (ExampleTable) ExampleSet.getSubset (trainSetIndicies);
          currentTestExampleSet = (ExampleTable) ExampleSet.getSubset (testSetIndicies);
          
          this.pushOutput (ControlPoint, 0);
          this.pushOutput (currentTrainExampleSet, 1);
          this.pushOutput (currentTestExampleSet, 2);
          
          ExampleSetIndex++;
        } else {
          PhaseIndex = 2;
        }
        break;
        
        
        // collect results
        
      case 2:
        
        objectivePoint = (ParameterPoint) this.pullInput (2);
        
        numUtilities = objectivePoint.getNumParameters ();
        
        if (UtilityIndex == 0) {
          UtilityValues = new ParameterPoint[NumRepetitions];
          UtilitySums = new double[numUtilities];
        }
        
        UtilityValues[UtilityIndex] = objectivePoint;
        for (int i = 0; i < numUtilities; i++) {
          UtilitySums[i] += objectivePoint.getValue (i);
        }
        
        UtilityIndex++;
        if (UtilityIndex == NumRepetitions) {
          PhaseIndex = 3;
          
        } else {
          PhaseIndex = 1;
        }
        break;
        
        // average evaluation results
        
      case 3:
        
          String[] names = new String[numUtilities];
          for (int i = 0; i < numUtilities; i++) {
            names[i] = objectivePoint.getName (i);
          }
          double[] meanUtilityArray = new double[numUtilities];
          for (int i = 0; i < numUtilities; i++) {
            meanUtilityArray[i] = UtilitySums[i] / NumRepetitions;
          }
          
          ParameterPoint meanObjectivePoint = ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl.getParameterPoint (
           names, meanUtilityArray);
          this.pushOutput (meanObjectivePoint, 3);
          this.pushOutput (UtilityValues, 4);
          
          reset ();
          
        break;
    }
    
  }
}