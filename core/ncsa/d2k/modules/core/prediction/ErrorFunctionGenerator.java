package ncsa.d2k.modules.core.prediction;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.core.modules.ComputeModule;


public class ErrorFunctionGenerator extends ComputeModule {

  //private String DataFilePath = "c:d2k/misc/dtcheng/allstate/ratios.ser";
  //public  void   setDataFilePath (String value) {this.DataFilePath = value;}
  //public  String getDataFilePath ()          {return this.DataFilePath;}

  private String     ErrorFunctionName = "Absolute";
  public  void    setErrorFunctionName (String value) {       this.ErrorFunctionName = value;}
  public  String     getErrorFunctionName ()          {return this.ErrorFunctionName;}


  public String getModuleName() {
    return "ErrorFunctionGenerator";
  }
  public String getModuleInfo() {
    return "ErrorFunctionGenerator";
  }

  public String getInputName(int i) {
    switch(i) {
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      default: return "No such input";
    }
  }
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }



  public String getOutputName(int i) {
    switch(i) {
      case 0: return "Error Function";
      default: return "No such output!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Error Function";
      default: return "No such output!";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.prediction.ErrorFunction"};
    return types;
  }


  public void doit() throws Exception {

    ErrorFunction errorFunction = new ErrorFunction(ErrorFunction.getErrorFunctionIndex(ErrorFunctionName));

    this.pushOutput(errorFunction, 0);

    }
}