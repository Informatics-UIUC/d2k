package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class PassTopObject
    extends DataPrepModule {

  public String getModuleName() {
    return "PassTopObject";
  }

  public String getModuleInfo() {
    return "This module pulls in two inputs and passes the TOP one along.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ObjectToPass";
      case 1:
        return "ObjectToWaitFor";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "ObjectToPass";
    case 1:
      return "ObjectToWaitFor";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object",
        "java.lang.Object"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "TopInputObject";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "TopInputObject";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public void doit() throws Exception {

    Object ObjectToPass = this.pullInput(0);

    this.pushOutput(ObjectToPass, 0);
  }

}