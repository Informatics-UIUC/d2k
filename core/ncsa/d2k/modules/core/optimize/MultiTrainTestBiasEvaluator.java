package ncsa.d2k.modules.core.optimize;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.util.Random;

public class MultiTrainTestBiasEvaluator extends ComputeModule {

  private int  NumRepetitions = 10;
  public  void setNumRepetitions (int value) {       this.NumRepetitions = value;}
  public  int  getNumRepetitions ()          {return this.NumRepetitions;}

  private int  NumTrainExamples = -1;
  public  void setNumTrainExamples (int value) {       this.NumTrainExamples = value;}
  public  int  getNumTrainExamples ()          {return this.NumTrainExamples;}

  private int  NumTestExamples = 999999999;
  public  void setNumTestExamples (int value) {       this.NumTestExamples = value;}
  public  int  getNumTestExamples ()          {return this.NumTestExamples;}

  private int  RandomSeed = 123;
  public  void setRandomSeed (int value) {       this.RandomSeed = value;}
  public  int  getRandomSeed ()          {return this.RandomSeed;}



  private int     BatchSize = 1;
  public  void setBatchSize (int value) {       this.BatchSize = value;}
  public  int  getBatchSize ()          {return this.BatchSize;}

  private boolean    Trace = false;
  public  void    setTrace (boolean value) {       this.Trace = value;}
  public  boolean getTrace ()              {return this.Trace;}

  private boolean    RecycleExamples = false;
  public  void    setRecycleExamples (boolean value) {       this.RecycleExamples = value;}
  public  boolean getRecycleExamples ()              {return this.RecycleExamples;}



  public String getModuleName() {
    return "MultiTrainTestBiasEvaluator";
  }
  public String getModuleInfo() {
    return "This module is the control center for evaluating a bias that involves training and testing examples.  " +
           "Random sampling is performed without replacement.  " +
           "The sum of training and testing examples can be less than the total number of examples but not more.";
  }

  public String getInputName(int i) {
    switch(i) {
      case  0: return "Control Parameters";
      case  1: return "Error Function";
      case  2: return "Example Table";
      case  3: return "Objective Scores";
      default: return "No such input!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case  0: return "Control Parameters";
      case  1: return "ErrorFunctions";
      case  2: return "ExampleSet";
      case  3: return "Objective Scores";
      default: return "No such input!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
      "ncsa.d2k.modules.core.prediction.ErrorFunction",
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"
    };
    return types;
  }


  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Control Parameters";
      case  1: return "Error Function";
      case  2: return "Train Example Table";
      case  3: return "Test Example Table";
      case  4: return "Objective Scores";
      case  5: return "All Objective Scores";
      default: return "No such output!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Control Parameters";
      case 1: return "Error Function";
      case 2: return "Train Example Table";
      case 3: return "Test Example Table";
      case 4: return "Objective Scores";
      case 5: return "All Objective Scores";
      default: return "No such output!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
      "ncsa.d2k.modules.core.prediction.ErrorFunction",
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
      "[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint;",
    };
    return types;
  }

  /*****************************************************************************/
  /* This function returns a random integer between min and max (both          */
  /* inclusive).                                                               */
  /*****************************************************************************/

    int randomInt(int min, int max) {
      return (int) ((RandomNumberGenerator.nextDouble() * (max - min + 1)) + min);
    }

    void randomizeIntArray(int [] data, int numElements) throws Exception {
      int temp, rand_index;

      for (int i = 0; i < numElements - 1; i++) {
        rand_index       = randomInt(i + 1, numElements - 1);
        temp             = data[i];
        data[i]          = data[rand_index];
        data[rand_index] = temp;
      }
    }





  int PhaseIndex;
  int ExampleSetIndex;
  int UtilityIndex;
  boolean InitialExecution;

  public void reset() {
    PhaseIndex = 0;
    ExampleSetIndex = 0;
    UtilityIndex = 0;
  }

  public void beginExecution() {
    InitialExecution = true;
    reset();
  }


  public boolean isReady() {
    boolean value = false;

    switch (PhaseIndex) {
      case 0:
        if (InitialExecution || (!RecycleExamples)) {
          value = (inputFlags[0] > 0) && (inputFlags[2] > 0);
        }
        else {
          value = (inputFlags[0] > 0);
        }
        break;

      case 1:
        value = true;
        break;

      case 2:
        value = (inputFlags[3] > 0);
        break;
    }

    return value;
  }



  ParameterPoint ControlPoint;
  ErrorFunction   errorFunction = null;
  ExampleTable ExampleSet;
  int numExamples;
  int numInputs;
  int numOutputs;
  ParameterPoint [] UtilityValues;
  double []   UtilitySums;

  Random RandomNumberGenerator;
  int [] RandomizedIndices = null;

  public void doit() throws Exception {

    switch (PhaseIndex) {
      case 0:

        ControlPoint = (ParameterPoint) this.pullInput(0);

        if (InitialExecution)
          errorFunction = (ErrorFunction) this.pullInput(1);

        if (InitialExecution || (!RecycleExamples)) {

          ExampleSet   = (ExampleTable) this.pullInput(2);

          numExamples = ExampleSet.getNumExamples();
          numInputs = ExampleSet.getNumInputFeatures();
          numOutputs = ExampleSet.getNumOutputFeatures();

          if (NumTrainExamples == -1) {
            NumTrainExamples = numExamples - NumTestExamples;
          }

          if (NumTestExamples == -1) {
            NumTestExamples = numExamples - NumTrainExamples;
          }

          if (NumTrainExamples + 1 > numExamples) {
            System.out.println("NumTrainExamples + 1 > numExamples");
            throw new Exception();
          }

          if (NumTrainExamples + NumTestExamples > numExamples) {
            NumTestExamples = numExamples - NumTrainExamples;
          }


          if ((RandomizedIndices == null) || (RandomizedIndices.length < numExamples)) {
            RandomizedIndices = new int[numExamples];
          }



          InitialExecution = false;
        }

        RandomNumberGenerator = new Random(RandomSeed);

        PhaseIndex = 1;
        break;

      case 1:

        if (ExampleSetIndex - UtilityIndex < BatchSize && ExampleSetIndex < NumRepetitions) {


          for (int e = 0; e < numExamples; e++)
            RandomizedIndices[e] = e;

          randomizeIntArray(RandomizedIndices, numExamples);



          int [] trainSetIndicies = new int[NumTrainExamples];
          int []  testSetIndicies = new int[ NumTestExamples];



          for (int e = 0; e < NumTrainExamples; e++) {
            trainSetIndicies[e] = RandomizedIndices[e];
          }
          for (int e = 0; e < NumTestExamples; e++) {
            testSetIndicies[e] = RandomizedIndices[e + NumTrainExamples];
          }


         ExampleTable currentTrainExampleSet = (ExampleTable) ExampleSet.getSubsetByReference(trainSetIndicies);
         ExampleTable currentTestExampleSet = (ExampleTable) ExampleSet.getSubsetByReference(testSetIndicies);

          this.pushOutput(ControlPoint,            0);
          this.pushOutput(errorFunction,           1);
          this.pushOutput(currentTrainExampleSet,  2);
          this.pushOutput(currentTestExampleSet,   3);

          if (Trace)
            System.out.println("pushing outputs; ExampleSetIndex = " + ExampleSetIndex);

          ExampleSetIndex++;
        }
        else {
          PhaseIndex = 2;
        }
        break;

      case 2:

        ParameterPoint objectivePoint = (ParameterPoint) this.pullInput(3);
        //double [] utilityArray = (double []) this.pullInput(3);

        //double [] utilities = utilityArray;
        int    numUtilities = objectivePoint.getNumParameters();

        if (UtilityIndex == 0) {
          UtilityValues = new ParameterPoint[NumRepetitions];
          UtilitySums   = new double[numUtilities];
        }

        UtilityValues[UtilityIndex] = objectivePoint;
        for (int i = 0; i < numUtilities; i++) {
          UtilitySums[i] += objectivePoint.getValue(i);
        }

        UtilityIndex++;
        if (UtilityIndex == NumRepetitions) {

          String [] names = new String[numUtilities];
          for (int i = 0; i < numUtilities; i++) {
            names[i] = objectivePoint.getName(i);
          }
          double [] meanUtilityArray = new double[numUtilities];
          for (int i = 0; i < numUtilities; i++) {
            meanUtilityArray[i] = UtilitySums[i] / NumRepetitions;
          }

          ParameterPoint meanObjectivePoint = new ParameterPointImpl();
          meanObjectivePoint.createFromData(names, meanUtilityArray);
          this.pushOutput(meanObjectivePoint, 4);
          this.pushOutput(UtilityValues,      5);

          reset();
        }
        else {
          PhaseIndex = 1;
        }
        break;
    }

  }
}