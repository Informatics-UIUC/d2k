package ncsa.d2k.modules.core.prediction;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.*;

import ncsa.d2k.core.modules.*;

public class ApplyFunctionInducer extends OrderedReentrantModule {

  public boolean Trace     = false;
  public void    setTrace (boolean value) {       this.Trace       = value;}
  public boolean getTrace ()              {return this.Trace;}

  private boolean    RecycleExamples = false;
  public  void    setRecycleExamples (boolean value) {       this.RecycleExamples = value;}
  public  boolean getRecycleExamples ()              {return this.RecycleExamples;}

  public String getModuleInfo() {
    return "ApplyFunctionInducer";
  }
  public String getModuleName() {
    return "ApplyFunctionInducer";
  }

  public String getInputName(int i) {
    switch(i) {
      case 0: return "Function Inducer";
      case 1: return "Error Function";
      case 2: return "Examples";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Function Inducer";
      case 1: return "Error Function";
      case 2: return "Examples";
      default: return "No such input";
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
    switch(i) {
      case 0: return "Model";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      default: return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }



  public void doit() throws Exception {

    ExampleTable exampleSet = null;

    FunctionInducerOpt functionInducer = (FunctionInducerOpt) this.pullInput(0);
    ErrorFunction      errorFunction   = (ErrorFunction)   this.pullInput(1);

    functionInducer._Trace = Trace;

    exampleSet  = (ExampleTable) this.pullInput(2);

    //!!! do i need this?
    exampleSet = (ExampleTable) exampleSet.copy();

    Model model = functionInducer.generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }
}