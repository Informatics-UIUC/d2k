package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.core.modules.*;

public class FunctionInducerOpt extends OrderedReentrantModule implements Cloneable {
  public int numInputs;
  public int numOutputs;
  public String [] inputNames;
  public String [] outputNames;

  //int NumBiasParameters = 0;

  double [] BiasParameters;

  public boolean _Trace     = false;
  public void    set_Trace (boolean value) {this._Trace  = value;}
  public boolean get_Trace ()              {return this._Trace;}

  public String getModuleInfo() {
    return "FunctionInducerOpt";
  }

  public String getModuleName()
  {
    return "FunctionInducerOpt";
  }


  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.prediction.ErrorFunction"
    };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Parameter Point containing control parameters values";
      case 1: return "Example Table";
      case 2: return "Error Function";
      default: return "No such input";
    }
  }

  public String getInputName(int i) {
    switch(i) {
      case 0: return "Parameter Point";
      case 1: return "Example Table";
      case 2: return "Error Function";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      default: return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Model";
      default: return "NO SUCH OUTPUT!";
    }
  }

  public void setControlParameters(ParameterPoint point) throws Exception {
    System.out.println("override this method");
    throw new Exception();
  }

    /*
  public Model generateModel(ExampleTable examples, double [] bias) throws Exception
    {
    instantiateBias(bias);
    return generateModel(examples);
    }
    */

  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws Exception {
    System.out.println("override this method");
    throw new Exception();
  }

  public void doit() throws Exception {

    ParameterPoint parameterPoint = (ParameterPoint) this.pullInput(0);
    ExampleTable   exampleSet     = (ExampleTable)   this.pullInput(1);
    ErrorFunction  errorFunction  = (ErrorFunction)  this.pullInput(2);

    setControlParameters(parameterPoint);

    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
