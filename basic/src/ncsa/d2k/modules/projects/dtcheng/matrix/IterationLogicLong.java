package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class IterationLogicLong
    extends ComputeModule {

  public String getModuleName() {
    return "IterationLogicLong";
  }

  public String getModuleInfo() {
    return "This module notifies when the number of iterations has " +
        "exceeded the limit. It operates on Longs rather than Integers.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "CurrentIterationNumber";
      case 1:
        return "MaximumNumberOfIterations";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "CurrentIterationNumber";
      case 1:
        return "MaximumNumberOfIterations";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
        "java.lang.Long",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "BailFlag";
      case 1:
        return "Reason";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "BailFlag";
      case 1:
        return "Reason";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Boolean",
        "java.lang.String",
    };
    return types;
  }

  public void doit() throws Exception {

    long CurrentIterationNumber = ((Long)this.pullInput(0)).longValue();
    long MaximumNumberOfIterations = ((Long)this.pullInput(1)).longValue();

    if (CurrentIterationNumber >= MaximumNumberOfIterations) {
      this.pushOutput(new Boolean(true), 0);
      this.pushOutput(new String("Stop: Max Iterations Exceeded"), 1);
    }
    else {
      this.pushOutput(new Boolean(false), 0);
      this.pushOutput(new String("Continuing..."), 1);
    }

  }

}
