package ncsa.d2k.modules.core.optimize.random;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.modules.core.datatype.table.continuous.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.Random;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;


public class UniformSampling extends ComputeModule implements java.io.Serializable {

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[6];

    pds[0] = new PropertyDescription(
        "objectiveScoreOutputFeatureNumber",
        "Objective Score Output Feature Number",
        "Selects which example output feature is used to denote the objective score of the Parameter Point.  ");

    pds[1] = new PropertyDescription(
        "objectiveScoreDirection",
        "Objective Score Direction",
        "Determines whether the objective score is to be minimized (-1) or maximized (1).  ");

    pds[2] = new PropertyDescription(
        "stopObjectiveScoreThreshold",
        "Stop Utility Threshold",
        "Optimization halts when an example is generated with an objective score which is greater or less than threshold depending on Objective Score Direction.  ");

    pds[3] = new PropertyDescription(
        "maxNumIterations",
        "Maximum Number of Iterations",
        "Optimization halts when this limit on the number of iterations is exceeded.  ");

    pds[4] = new PropertyDescription(
        "randomSeed",
        "Random Number Generator Initial Seed",
        "This integer is use to seed the random number generator which is used to select points in parameter space.  ");

    pds[5] = new PropertyDescription(
        "trace",
        "Trace",
        "Report extra information during execution to trace the modules execution.  ");

    return pds;
  }


  private int ObjectiveScoreOutputFeatureNumber = 1;
  public void setObjectiveScoreOutputFeatureNumber(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.ObjectiveScoreOutputFeatureNumber = value;
    }
  public  int getObjectiveScoreOutputFeatureNumber () {
    return this.ObjectiveScoreOutputFeatureNumber;
    }

  private int ObjectiveScoreDirection = -1;
  public  void setObjectiveScoreDirection (int value) throws PropertyVetoException {
    if (!((value == -1) || (value == 1))) {
      throw new PropertyVetoException(" must be -1 or 1", null);
    }
    this.ObjectiveScoreDirection = value;
    }
  public int getObjectiveScoreDirection () {
    return this.ObjectiveScoreDirection;
    }

  private double StopObjectiveScoreThreshold = 0.0;
  public void setStopObjectiveScoreThreshold (double value)  {
    this.StopObjectiveScoreThreshold = value;
    }
  public double  getStopObjectiveScoreThreshold () {
    return this.StopObjectiveScoreThreshold;
    }

  private int MaxNumIterations = 10;
  public void setMaxNumIterations (int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.MaxNumIterations = value;
    }
  public int getMaxNumIterations () {
    return this.MaxNumIterations;
    }

  private int RandomSeed = 123;
  public void setRandomSeed (int value)  {
    this.RandomSeed = value;
    }
  public int getRandomSeed () {
    return this.RandomSeed;
    }

  private boolean Trace = false;
  public void setTrace (boolean value) {
    this.Trace = value;
    }
  public boolean getTrace () {
    return this.Trace;
    }


  public String getModuleName() {
    return "Random Optimizer";
  }
  public String getModuleInfo() {
    return "This module implements a simple random sampling optimizer which selects points according to a uniform " +
           "distribution over the parameter space.  Every point in the space as equal likelihood of being selected.  ";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0: return "Control Parameter Space";
      case 1: return "Example";
    }
    return "";
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "The Control Parameter Space to search";
      case 1: return "The Example created by combining the Parameter Point and the objective scores";
    }
    return "";
  }
  public String[] getInputTypes() {
    String [] in = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
      "ncsa.d2k.modules.core.datatype.table.Example"
    };
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Parameter Point";
      case 1: return "Optimal Example Table";
      case 2: return "Complete Example Table";
    }
    return "";
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "The next Parameter Point selected for evaluation";
      case 1: return "An example table consisting of only the Optimal Example(s)";
      case 2: return "An example table consisting of all Examples generated during optimization";
    }
    return "";
  }
  public String[] getOutputTypes() {
    String [] out = {
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
    ExampleSet = null;
    NumExamples = 0;

    if (ObjectiveScoreDirection == 1) {
      BestUtility = Double.NEGATIVE_INFINITY;
    }
    else {
      BestUtility = Double.POSITIVE_INFINITY;
    }
    BestExampleIndex = Integer.MIN_VALUE;
    randomNumberGenerator = new Random(RandomSeed);
  }

  public boolean isReady() {
    boolean value = false;

    if (InitialExecution) {
      value = (inputFlags[0] > 0);
    }
    else {
      value = (inputFlags[1] > 0);
    }

    return value;
  }


  int NumExperimentsCompleted = 0;

  ParameterSpace   BiasSpace;
  //String []     BiasSpaceDimensionNames;
  double []     Bias;
  //int           BiasSpaceNumDimensions;
  double [][][] InitialExampleSet;
  int           InitialNumExamples;
  ContinuousDoubleExampleTable ExampleSet;
  int           NumExamples;
  double [][]   ExampleData;

  double BestUtility      = 0;
  int    BestExampleIndex = Integer.MIN_VALUE;


  public void doit() {

    if (InitialExecution) {
      BiasSpace                    = (ParameterSpace) this.pullInput(0);
      InitialExecution             = false;
    }
    else {

      Example example = (Example) this.pullInput(1);

      if (ExampleSet == null) {

        String [] ControlSpaceDimensionNames = new String[BiasSpace.getNumParameters()];
        for (int i = 0; i < BiasSpace.getNumParameters(); i++) {
          ControlSpaceDimensionNames[i] = BiasSpace.getName(i);
        }

        String [] ObjectiveSpaceDimensionNames = new String[example.getNumOutputs()];
        for (int i = 0; i < example.getNumOutputs(); i++) {
          ObjectiveSpaceDimensionNames[i] = example.getOutputName(i);
        }

        ExampleSet = new ContinuousDoubleExampleTable(MaxNumIterations,
            BiasSpace.getNumParameters(),
            example.getNumOutputs(),
            ControlSpaceDimensionNames,
            ObjectiveSpaceDimensionNames);

        ExampleSet.setNumRows(0);

      }

      // add example to set
      double [] data = new double[example.getNumInputs() + example.getNumOutputs()];
      int index = 0;
      for (int i = 0; i < example.getNumInputs(); i++) {
        data[index++] = example.getInputDouble(i);
      }
      for (int i = 0; i < example.getNumOutputs(); i++) {
        data[index++] = example.getOutputDouble(i);
      }

      ExampleSet.addRow(data);
      NumExamples++;


      // update best solution so far


      for (int e = NumExamples - 1; e < NumExamples; e++) {

        double utility = ExampleSet.getExample(e).getOutputDouble(ObjectiveScoreOutputFeatureNumber - 1);

        if (ObjectiveScoreDirection == 1) {
          if (utility > BestUtility) {
            BestUtility      = utility;
            BestExampleIndex = e;
          }
        }
        else {
          if (utility < BestUtility) {
            BestUtility      = utility;
            BestExampleIndex = e;
          }
        }
      }

    }

    ////////////////////////////
    // test stopping criteria //
    ////////////////////////////

    boolean stop = false;

    if (NumExamples > 0) {
      if ((ObjectiveScoreDirection ==  1) && (BestUtility >= StopObjectiveScoreThreshold))
        stop = true;
      if ((ObjectiveScoreDirection == -1) && (BestUtility <= StopObjectiveScoreThreshold))
        stop = true;
      if (NumExamples >= MaxNumIterations)
        stop = true;
      if (BiasSpace.getNumParameters() == 0) {
        System.out.println("Halting execution of optimizer after on iteration because numParameters = 0.  ");
        stop = true;
      }
    }

    /////////////////////////////////////////
    // quit when necessary and push result //
    /////////////////////////////////////////
    if (stop) {

      if (Trace) {

        System.out.println("Optimization Completed");
        System.out.println("  Number of Experiments = " + NumExamples);

        System.out.println("NumExamples............ " + NumExamples);
        System.out.println("ObjectiveScoreDirection....... " + ObjectiveScoreDirection);
        System.out.println("BestUtility............ " + BestUtility);
        System.out.println("BestExampleNumber...... " + (BestExampleIndex + 1));
      }


      // add example to set
      double [] data = new double[ExampleSet.getNumInputFeatures() + ExampleSet.getNumOutputFeatures()];
      int index = 0;
      for (int i = 0; i < ExampleSet.getNumInputFeatures(); i++) {
        data[index++] = ExampleSet.getInputDouble(BestExampleIndex, i);
      }
      for (int i = 0; i < ExampleSet.getNumOutputFeatures(); i++) {
        data[index++] = ExampleSet.getOutputDouble(BestExampleIndex, i);
      }


      ContinuousDoubleExampleTable optimalExampleSet = new ContinuousDoubleExampleTable(
          data,
          1,
          ExampleSet.getNumInputFeatures(),
          ExampleSet.getNumOutputFeatures(),
          ExampleSet.getInputNames(),
          ExampleSet.getOutputNames()
          );

      this.pushOutput(optimalExampleSet, 1);
      this.pushOutput(ExampleSet,        2);

      beginExecution();

      return;
  }



    //////////////////////////////////////////////
    // generate next point in bias space to try //
    //////////////////////////////////////////////

    double [] point = new double[BiasSpace.getNumParameters()];

    // use uniform random sampling to constuct point
    for (int d = 0; d < BiasSpace.getNumParameters(); d++) {
      double range = BiasSpace.getMaxValue(d) - BiasSpace.getMinValue(d);
      point[d] = BiasSpace.getMinValue(d) + range * randomNumberGenerator.nextDouble();
    }

    ParameterPointImpl parameterPoint = new ParameterPointImpl();

    String [] names = new String[BiasSpace.getNumParameters()];
    for (int i = 0; i < BiasSpace.getNumParameters(); i++) {
      names[i] = BiasSpace.getName(i);
    }
    parameterPoint.createFromData(names, point);

    this.pushOutput(parameterPoint, 0);

  }
}