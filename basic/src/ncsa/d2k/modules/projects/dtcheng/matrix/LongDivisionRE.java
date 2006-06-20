package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class LongDivisionRE
    extends OrderedReentrantModule {

  public String getModuleName() {
    return "LongDivisionRE";
  }

  public String getModuleInfo() {
    return "This module divides two long numbers producing a long number " +
	"output with the default truncation. Reentrant version.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "First Long";
      case 1:
        return "Second Long";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "First Long";
      case 1:
        return "Second Long";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
        "java.lang.Long"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultLong";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultLong";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long"};
    return types;
  }

  public void doit() throws Exception {

    long X = ((Long)this.pullInput(0)).longValue();
    long Y = ((Long)this.pullInput(1)).longValue();

    this.pushOutput(new Long(X / Y), 0);
  }

}
