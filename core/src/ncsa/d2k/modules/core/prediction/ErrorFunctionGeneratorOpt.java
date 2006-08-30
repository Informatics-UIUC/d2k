package ncsa.d2k.modules.core.prediction;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.core.modules.ComputeModule;


public class ErrorFunctionGeneratorOpt extends ComputeModule {

  //private String DataFilePath = "c:d2k/misc/dtcheng/allstate/ratios.ser";
  //public  void   setDataFilePath (String value) {this.DataFilePath = value;}
  //public  String getDataFilePath ()          {return this.DataFilePath;}

  //private String     ErrorFunctionName = "Absolute";
  //public  void    setErrorFunctionName (String value) {       this.ErrorFunctionName = value;}
  //public  String     getErrorFunctionName ()          {return this.ErrorFunctionName;}


  public String getModuleInfo() {
    return "ErrorFunctionGeneratorOpt";
  }
  public String getModuleName() {
    return "ErrorFunctionGeneratorOpt";
  }

  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Control Parameters";
      default: return "No such input";
    }
  }

  public String getInputName(int i) {
    switch(i) {
      case 0: return "Control Parameters";
      default: return "NO SUCH INPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.prediction.ErrorFunction"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Error Function";
      default: return "No such output!";
    }
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0: return "Error Function";
      default: return "No such output!";
    }
  }

  public void doit() throws Exception {

    ParameterPoint parameterPoint = (ParameterPoint)pullInput(0);

    ErrorFunction errorFunction = new ErrorFunction(parameterPoint);

    this.pushOutput(errorFunction, 0);

    }
}