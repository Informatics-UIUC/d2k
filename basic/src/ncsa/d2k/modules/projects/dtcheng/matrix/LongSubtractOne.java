package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.ComputeModule;

public class LongSubtractOne
    extends ComputeModule {

  public String getModuleInfo() {
    return "LongSubtractOne: takes a Long and bumps the value down by one.";
  }

  public String getModuleName() {
    return "LongSubtractOne";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Long";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Long";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Long bumped down by one.";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Long";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    Long x = (Long)this.pullInput(0);
    this.pushOutput(new Long(x.longValue() - 1), 0);
  }
}
