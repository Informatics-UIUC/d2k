package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class TwoObjectCoordinator extends ComputeModule {
  
  public String getModuleName() {
    return "TwoObjectCoordinator";
  }
  
  public String getModuleInfo() {
    return "This module pulls in two objects and passes them out along with a \"true\" in order to make sure everything arrives at the same time";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "FirstObject";
    case 1:
      return "SecondObject";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "FirstObject";
    case 1:
      return "SecondObject";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Object", "java.lang.Object", };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "FirstObject";
    case 1:
      return "SecondObject";
    case 2:
      return "True";
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "FirstObject";
    case 1:
      return "SecondObject";
    case 2:
      return "True";
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object", "java.lang.Object", "java.lang.Boolean",};
    return types;
  }
  
  public void doit() throws Exception {
    Object FirstObject = this.pullInput(0);
    Object SecondObject = this.pullInput(1);
    
    this.pushOutput(FirstObject, 0);
    this.pushOutput(SecondObject, 1);
    this.pushOutput(new Boolean(true), 2);
  }
}