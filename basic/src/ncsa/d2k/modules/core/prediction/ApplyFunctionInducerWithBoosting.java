package ncsa.d2k.modules.core.prediction;


import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class ApplyFunctionInducerWithBoosting
    extends OrderedReentrantModule {

  private int NumberOfRounds = 100;
  public void setNumberOfRounds(int value) {
    this.NumberOfRounds = value;
  }

  public int getNumberOfRounds() {
    return this.NumberOfRounds;
  }

  public String getModuleName() {
		return "Apply Function Inducer With Boosting";
	}

  public String getModuleInfo() {
		return "<p>"+
"      Overview: This module applies a function inducer module to the given "+
"      example table using the given error function and with boosting to "+
"      produce a model."+
"    </p>"+
"    <p>"+
"      Detailed Description: This module uses the given function inducer to "+
"      build an ordered series of different models. Each round, a new set of "+
"      examples is created by first forming a model with the current set of "+
"      examples, then using the model to predict each output, and then re;acing "+
"      the current output variable with the difference between the prediction "+
"      and actual output values. In this way, each model tries to predict the "+
"      different left over after applying the previous model in the series."+
"    </p>"+
"    <p>"+
"      References:"+
"    </p>";
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
			case 0: return "The function inducer module used to generate the model,";
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

  public void doit() throws Exception {

    ExampleTable OriginalExamples = null;
    ExampleTable examples = null;

    FunctionInducerOpt FunctionInducer = (FunctionInducerOpt)this.pullInput(0);
    ErrorFunction ErrorFunction = (ErrorFunction)this.pullInput(1);

    OriginalExamples = (ExampleTable)this.pullInput(2);

    if (NumberOfRounds == 1)
      examples = OriginalExamples;
    else
      examples = (ExampleTable) OriginalExamples.copy();

      //examples = OriginalExamples;

      //!!! do i need this?
      //exampleSet = (ExampleTable) exampleSet.copy();

    Model model = null;
    Model[] models = new Model[NumberOfRounds];

    Model BoostedModel = null;

    for (int i = 0; i < NumberOfRounds; i++) {

      System.out.println("Round number " + (i + 1));
      model = FunctionInducer.generateModel(examples, ErrorFunction);

      ///////////////////////////
      // add model to ensemble //
      ///////////////////////////

      models[i] = model;

      BoostedModel = new EnsembleModel(OriginalExamples, models, i + 1, EnsembleModel.SUM);

      ///////////////////////////////////////////
      // quit when last round has been reached //
      ///////////////////////////////////////////

      if (i == NumberOfRounds - 1) {
        break;
      }

      ////////////////////////////////
      // apply model to example set //
      ////////////////////////////////

      int NumExamples = examples.getNumRows();
      int numInputs = examples.getNumInputFeatures();
      int NumOutputs = examples.getNumOutputFeatures();

      //PredictionTable predictionTable = model.predict(examples.copy());
      PredictionTable predictionTable = examples.toPredictionTable();
      BoostedModel.predict(predictionTable);

      double errorSum = 0.0;
      int numPredictions = 0;
      for (int e = 0; e < NumExamples; e++) {

        //errorSum += errorFunction.evaluate(examples, e, model);

        errorSum += ErrorFunction.evaluate(OriginalExamples, e, predictionTable);

        numPredictions++;
      }

      double error = errorSum / numPredictions;

      double[] utility = new double[1];
      utility[0] = error;

      synchronized (System.out) {
        if (examples.getLabel() != null)
          System.out.println("(" + examples.getLabel() + ")" +
                             ErrorFunction.getErrorFunctionName(ErrorFunction.errorFunctionIndex) + " = " + utility[0]);
        else
          System.out.println(ErrorFunction.getErrorFunctionName(ErrorFunction.errorFunctionIndex) + " = " + utility[0]);
      }

      //////////////////////////
      // tranform example set //
      //////////////////////////
      int [] outputIndices = examples.getOutputFeatures();
      for (int e = 0; e < NumExamples; e++) {

        for (int o = 0; o < NumOutputs; o++) {

          double predicted = predictionTable.getDoublePrediction(e, o);
          double actual = OriginalExamples.getOutputDouble(e, o);
          double residual = actual - predicted;
          examples.setDouble(residual, e, outputIndices[o]);
        }
      }

    }

    this.pushOutput(BoostedModel, 0);
  }
}
