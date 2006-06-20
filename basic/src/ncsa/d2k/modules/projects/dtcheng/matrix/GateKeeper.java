package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class GateKeeper extends ComputeModule {
  
  public String getModuleName() {
    return "GateKeeper";
  }
  
  public String getModuleInfo() {
    return "This module pulls in an object and two longs. " + "If the two longs match, the object is passed out. "
    + "Otherwise execution ends and nothing is pushed out.";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Object";
    case 1:
      return "FirstLong";
    case 2:
      return "SecondLong";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Object";
    case 1:
      return "FirstLong";
    case 2:
      return "SecondLong";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Object", "java.lang.Long", "java.lang.Long" };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Object";
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Object";
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object", };
    return types;
  }
  
  public void doit() throws Exception {
    Object OriginalObject = this.pullInput(0);
    long FirstLong = ((Long)this.pullInput(1)).longValue();
    long SecondLong = ((Long)this.pullInput(2)).longValue();
    
    if (FirstLong == SecondLong) {
      this.pushOutput(OriginalObject, 0);
    }
  }
}