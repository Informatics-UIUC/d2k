package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class PutTXTonEnd
    extends ComputeModule {

  public String getModuleName() {
    return "PutTXTonEnd";
  }

  public String getModuleInfo() {
    return "This module takes a String and appends a \".txt\" to it.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "BaseString";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
        return "BaseString";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.String",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "NewString";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "NewString";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.String",
    };
    return types;
  }

  public void doit() throws Exception {
      String BaseString = (String)this.pullInput(0);
      
      String newString = new String(BaseString + ".txt");

      this.pushOutput(newString, 0);
  }
}
