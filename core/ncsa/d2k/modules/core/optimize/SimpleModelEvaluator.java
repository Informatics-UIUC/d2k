package ncsa.d2k.modules.core.optimize;
import ncsa.d2k.modules.core.transform.sort.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class SimpleModelEvaluator extends OrderedReentrantModule {

  private boolean PrintErrors            = false;
  public  void    setPrintErrors         (boolean value) {       this.PrintErrors        = value;}
  public  boolean getPrintErrors         ()              {return this.PrintErrors;}

  private boolean PrintPredictions       = false;
  public  void    setPrintPredictions    (boolean value) {       this.PrintPredictions   = value;}
  public  boolean getPrintPredictions    ()              {return this.PrintPredictions;}

  private boolean    FilterByOutputRank  = false;
  public  void    setFilterByOutputRank  (boolean value) {       this.FilterByOutputRank = value;}
  public  boolean getFilterByOutputRank  ()              {return this.FilterByOutputRank;}

  private double  FilterLowerFraction    = 0.2;
  public  void    setFilterLowerFraction (double value) {       this.FilterLowerFraction = value;}
  public  double  getFilterLowerFraction ()             {return this.FilterLowerFraction;}

  private double  FilterUpperFraction    = 0.2;
  public  void    setFilterUpperFraction (double value) {       this.FilterUpperFraction = value;}
  public  double  getFilterUpperFraction ()             {return this.FilterUpperFraction;}

  private String OutputLabel             = "SimpleModelEvaluator: ";
  public  void   setOutputLabel          (String value) {this.OutputLabel = value;}
  public  String getOutputLabel          ()             {return this.OutputLabel;}


  public String getModuleName() {
    return "SimpleModelEvaluator";
  }
  public String getModuleInfo() {
    return "SimpleModelEvaluator";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0: return "Error Function";
      case 1: return "Model";
      case 2: return "Example Table";
    }
    return "";
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Error Function";
      case 1: return "Model";
      case 2: return "Example Table";
    }
    return "";
  }
  public String[] getInputTypes() {
    String [] in = {
      "ncsa.d2k.modules.projects.dtcheng.ErrorFunction",
      "ncsa.d2k.modules.PredictionModelModule",
      "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Objective Scores";
    }
    return "";
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Objective Scores";
    }
    return "";
  }
  public String[] getOutputTypes() {
    String [] out = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return out;
  }

  public void doit() throws Exception {

    ErrorFunction errorFunction = (ErrorFunction) this.pullInput(0);
    Model         model         = (Model)         this.pullInput(1);
    ExampleTable  exampleTable  = (ExampleTable)  this.pullInput(2);

    ExampleTable examples = exampleTable;

    int numExamples = examples.getNumExamples();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();

    // change to call make prediction

    PredictionTable predictionTable = model.predict(exampleTable);

    for (int e = 0; e < numExamples; e++) {

      if (PrintPredictions) {
        for (int o = 0; o < numOutputs; o++) {
          synchronized( System.out ) {
          System.out.println(OutputLabel + "e = " + e + "  predicted = " + predictionTable.getDoublePrediction(e, o) +
          "  actual = " + exampleTable.getOutputDouble(e, o));
        }
        }
      }

    }


    // mark examples to be used for error calculation
    boolean [] useExamples = null;
    if (FilterByOutputRank) {

      useExamples = new boolean[numExamples];

      double [][] results  = new double[numExamples][2];
      for (int e = 0; e < numExamples; e++) {
        double outputSum = 0.0;
        for (int o = 0; o < numOutputs; o++) {
          outputSum += predictionTable.getDoublePrediction(e, 0);
        }
        double predicted = outputSum / numOutputs;

        results[e][0] = predicted;
        results[e][1] = e;
      }

      QuickSort.sort(results);

      int lowerNumExamples = (int) Math.round(FilterLowerFraction * numExamples);
      int upperNumExamples = (int) Math.round(FilterUpperFraction * numExamples);

      for (int e = 0; e < lowerNumExamples; e++) {
        useExamples[(int) results[e][1]] = true;
      }
      for (int e = 0; e < upperNumExamples; e++) {
        useExamples[(int) results[numExamples - 1 - e][1]] = true;
      }
    }



    double errorSum = 0.0;
    int    numPredictions = 0;
    for (int e = 0; e < numExamples; e++) {
      if (FilterByOutputRank && !useExamples[e])
        continue;

      errorSum += errorFunction.evaluate(examples, e, model);

      numPredictions++;
    }

    double error = errorSum / numPredictions;

    double [] utility = new double [1];
    utility[0] = error;

    if (PrintErrors) {
      synchronized( System.out ) {
        System.out.println(OutputLabel + ErrorFunction.getErrorFunctionName(errorFunction.errorFunctionIndex) + " = " + utility[0]);
      }
    }

    // push outputs //

    ParameterPointImpl objectivePoint = new  ParameterPointImpl();
    String [] names = new String[1];
    names[0] = ErrorFunction.getErrorFunctionName(errorFunction.errorFunctionIndex);
    objectivePoint.createFromData(names, utility);
    this.pushOutput(objectivePoint, 0);

  }
}