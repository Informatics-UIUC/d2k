package ncsa.d2k.modules.projects.aehmann;

import ncsa.d2k.core.modules.*;

public class GenerateTestDoubleArray
    extends InputModule {

  public String getModuleInfo() {
    return "GenerateTestDoubleArray";
  }

  public String getModuleName() {
    return "GenerateTestDoubleArray";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "TriggerObject";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "this input triggers firing of module";
      default:
        return "No such input";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[D"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "[D";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "[D";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    Object object = (Object)this.pullInput(0);

    double[] array = new double[5];

    for (int i = 0; i < array.length; i++) {
      array[i] = 2.0+(double)i;
      //if (i < 10000)
      //System.out.println(array[i]);
    }
    this.pushOutput(array, 0);
  }
}