package ncsa.d2k.modules.core.prediction;


import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.util.Random;
import ncsa.d2k.modules.core.prediction.evaluators.Utility;

public class ApplyFunctionInducerWithBagging
    extends OrderedReentrantModule {

  private int NumSubSampleExamples = 100;
  public void setNumSubSampleExamples(int value) {
    this.NumSubSampleExamples = value;
  }

  public int getNumSubSampleExamples() {
    return this.NumSubSampleExamples;
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
		return "<p>"+
"      Overview: This module applies a function inducer module to the given "+
"      example table using the given error function and with boosting to "+
"      produce a ensemble of models."+
"    </p>"+
"    <p>"+
"      Detailed Description: This module will generate several models using the "+
"      given function inducer and error function. The resulting models are then "+
"      combined into an ensemble model that can be used to provide a possibly "+
"      more accurate prediction. The &quot;Number Subsamples&quot; determines how many "+
"      times the data is subsampled. The &quot;Number Final Models&quot; determines how "+
"      many models will exist in the final ensemble model. Random set can be "+
"      set to some non-negative value to set the output of the random number "+
"      generator to the same sequence of values for each invocation."+
"    </p>" +
"	  <p>References:<A name=\"Breiman:1994:bagging\"></A>Leo Breiman. <A href=\"http://www.work.caltech.edu/cs156/01/papers/bagging.ps.gz\">" +
"     Bagging predictors</A>. Technical Report 421, Department of Statistics, University of California at Berkeley, September 1994." +
"		</p>";
	}

  public String getInputName(int i) {
		switch(i) {
			case 0:
				return "Function Inducer";
			case 1:
				return "Error Function";
			case 2:
				return "Examples";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The function inducer module used to generate the model.";
			case 1: return "The error function used to guide the function inducer.";
			case 2: return "The example table used for generating the model.";
			default: return "No such input";
		}
	}

  public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.FunctionInducerOpt","ncsa.d2k.modules.core.prediction.ErrorFunction","ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

  public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "Model";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The model generated from the example table and the error function.";
			default: return "No such output";
		}
	}

  public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.model.Model"};
		return types;
	}



  int[] RandomizedIndices = null;
  Random RandomNumberGenerator;

  public void doit() throws Exception {

    ExampleTable OriginalExamples = null;
    ExampleTable Examples = null;

    FunctionInducerOpt FunctionInducer = (FunctionInducerOpt)this.pullInput(0);
    ErrorFunction ErrorFunction = (ErrorFunction)this.pullInput(1);

    OriginalExamples = (ExampleTable)this.pullInput(2);


    ////////////////////////////////////////////////////////////////////////
    // create copy of example set if destructive modification is required //
    ////////////////////////////////////////////////////////////////////////

    if (NumberOfModelsInEnsemble == 1)
      Examples = OriginalExamples;
    else
      Examples = (ExampleTable) OriginalExamples.copy();

    int NumExamples = Examples.getNumRows();




    //examples = OriginalExamples;

    //!!! do i need this?
    //exampleSet = (ExampleTable) exampleSet.copy();

    Model model = null;
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
      if (ExampleSetClass.equals("class ncsa.d2k.modules.projects.dtcheng.datatype.ContinuousDoubleExampleTable")) {
        CurrentTrainExamples = (ExampleTable) Examples.getSubset(SubSampleExampleIndicies);
      }
      else {
        System.out.println("Warning!  Inefficient example table.");
        Examples.setTrainingSet(SubSampleExampleIndicies);
        CurrentTrainExamples = (ExampleTable) Examples.getTrainTable();
      }

      ///////////////////////////////////
      // create next model in ensemble //
      ///////////////////////////////////

      models[i] = FunctionInducer.generateModel(CurrentTrainExamples, ErrorFunction);

     }


    this.pushOutput(new EnsembleModel(Examples, models, NumberOfModelsInEnsemble, EnsembleModel.AVERAGE), 0);
  }
  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
	PropertyDescription[] pds = new PropertyDescription[3];
	pds[0] = new PropertyDescription("numSubSampleExamples",
									 "Number Subsamples",
									 "This determins the number of subsamples that will be taken to produce the models.");
	pds[1] = new PropertyDescription("numberOfModelsInEnsemble",
									 "Number Final Models",
									 "This is the number of models that will be included in the final ensemble.");
	pds[2] = new PropertyDescription("randomSeed",
									 "Random Seed",
									 "If not -1, will generate the same sequence of values at each run.");
	return pds;
  }


 }
