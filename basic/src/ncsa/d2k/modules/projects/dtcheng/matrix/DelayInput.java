package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class DelayInput
    extends InputModule {

  public String getModuleInfo() {
    return "DelayInput";
  }

  public String getModuleName() {
    return "DelayInput";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return this.getAlias();
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return this.getAlias();
      default:
        return "NO SUCH OUTPUT!";
    }
  }
  private long SleepTime = 1000;
  
  public void setSleepTime(long value) {
    this.SleepTime = value;
  }
  
  public long getSleepTime() {
    return this.SleepTime;
  }

  public void doit() throws Exception {
    Thread.sleep(SleepTime);
    this.pushOutput(this.pullInput(0), 0);
  }
}