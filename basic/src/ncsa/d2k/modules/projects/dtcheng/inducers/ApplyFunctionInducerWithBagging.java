package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.Utility;

import ncsa.d2k.core.modules.*;
import java.util.Random;
import ncsa.d2k.modules.projects.dtcheng.examples.TchengExampleTable;
import ncsa.d2k.modules.projects.dtcheng.models.EnsembleModel;


public class ApplyFunctionInducerWithBagging
    extends OrderedReentrantModule {

  private double FractionSubSampleExamples = 0.9;
  public void setFractionSubSampleExamples(double value) {
    this.FractionSubSampleExamples = value;
  }

  public double getFractionSubSampleExamples() {
    return this.FractionSubSampleExamples;
  }

  private int RandomSeed = 123;
  public void setRandomSeed(int value) {
    this.RandomSeed = value;
  }

  public int getRandomSeed() {
    return this.RandomSeed;
  }

  private int NumberOfModelsInEnsemble = 10;
  public void setNumberOfModelsInEnsemble(int value) {
    this.NumberOfModelsInEnsemble = value;
  }

  public int getNumberOfModelsInEnsemble() {
    return this.NumberOfModelsInEnsemble;
  }

  public String getModuleName() {
    return "Apply Function Inducer With Bagging";
  }

  public String getModuleInfo() {
    return
        "This module applies a function inducer module to the given example table using the given error function and with boosting to produce a model";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Function Inducer";
      case 1:
        return "Error Function";
      case 2:
        return "Examples";
      default:
        return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The function inducer module used to generate the model";
      case 1:
        return "The error function used to guide the function inducer";
      case 2:
        return "The example table used for generating the model";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.prediction.FunctionInducerOpt",
        "ncsa.d2k.modules.core.prediction.ErrorFunction",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Model";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The model generated from the example table and the error function";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }



  int[] RandomizedIndices = null;
  Random RandomNumberGenerator;

  public void doit() throws Exception {
    

    TchengExampleTable OriginalExamples = null;
    TchengExampleTable Examples        = null;

    FunctionInducerOpt FunctionInducer = (FunctionInducerOpt)this.pullInput(0);
    ErrorFunction      ErrorFunction   =      (ErrorFunction)this.pullInput(1);
    OriginalExamples                   = (TchengExampleTable)this.pullInput(2);


    ////////////////////////////////////////////////////////////////////////
    // create copy of example set if destructive modification is required //
    ////////////////////////////////////////////////////////////////////////

    if (NumberOfModelsInEnsemble == 1)
      Examples = OriginalExamples;
    else
      Examples = (TchengExampleTable) OriginalExamples.copy();

    int NumExamples = Examples.getNumRows();

    
    int NumSubSampleExamples = (int) (FractionSubSampleExamples * NumExamples);
    
    if  (FractionSubSampleExamples > 1.0) {
      throw new Exception("FractionSubSampleExamples > 1.0");
    }
    if  (FractionSubSampleExamples < 0.0) {
      throw new Exception("FractionSubSampleExamples < 0.0");
    }

    //examples = OriginalExamples;

    //!!! do i need this?
    //exampleSet = (ExampleTable) exampleSet.copy();

    Model[] models = new Model[NumberOfModelsInEnsemble];

    RandomNumberGenerator = new Random(RandomSeed);

    if ( (RandomizedIndices == null) || (RandomizedIndices.length != NumExamples)) {
      RandomizedIndices = new int[NumExamples];
    }


    for (int i = 0; i < NumberOfModelsInEnsemble; i++) {

      //System.out.println("Round number " + (i + 1));

      ///////////////////////////////////////////
      // create random subsample to learn from //
      ///////////////////////////////////////////

      //Utility.randomizeIntArray(RandomNumberGenerator, RandomizedIndices, NumExamples);
      for (int e = 0; e < NumExamples; e++) {
        RandomizedIndices[e] = e;
      }
      Utility.randomizeIntArray(RandomNumberGenerator, RandomizedIndices, NumExamples);
      int[] SubSampleExampleIndicies = new int[NumSubSampleExamples];
      for (int e = 0; e < NumSubSampleExamples; e++) {
        SubSampleExampleIndicies[e] = RandomizedIndices[e];
        //System.out.println(SubSampleExampleIndicies[e]);
      }

      ExampleTable CurrentTrainExamples;
      String ExampleSetClass = Examples.getClass().toString();
        CurrentTrainExamples = (ExampleTable) Examples.getSubset(SubSampleExampleIndicies);

      ///////////////////////////////////
      // create next model in ensemble //
      ///////////////////////////////////

      models[i] = FunctionInducer.generateModel(CurrentTrainExamples, ErrorFunction);

     }


    this.pushOutput(new EnsembleModel(Examples, models, NumberOfModelsInEnsemble, EnsembleModel.AVERAGE), 0);
  }

 }
