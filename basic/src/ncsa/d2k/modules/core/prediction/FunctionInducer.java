package ncsa.d2k.modules.core.prediction;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class FunctionInducer extends OrderedReentrantModule implements Cloneable
{
  //int NumBiasParameters = 0;

  double [] BiasParameters;

  public boolean _Trace     = false;
  public void    set_Trace (boolean value) {       this._Trace       = value;}
  public boolean get_Trace ()              {return this._Trace;}

  public String getModuleInfo() {
    return "FunctionInducer";
  }
  public String getModuleName()
  {
    return "FunctionInducer";
  }


  public String[] getInputTypes() {
    String[] types = {
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
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "No such input";
    }
  }

  public String getInputName(int i) {
    switch(i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
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

  public void instantiateBiasFromProperties() throws Exception {
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

    ExampleTable  exampleSet      = (ExampleTable)  this.pullInput(0);
    ErrorFunction errorFunction   = (ErrorFunction) this.pullInput(1);
    
 
    instantiateBiasFromProperties();

    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
