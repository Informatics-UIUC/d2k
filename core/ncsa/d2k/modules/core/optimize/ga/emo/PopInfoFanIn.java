package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;

public class PopInfoFanIn extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo",
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
  }

  public String[] getOutputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  public boolean isReady() {
    return getInputPipeSize(0) > 0 || getInputPipeSize(1) > 0;
  }

  public void doit() {
    if(getInputPipeSize(0) > 0)  {
      Object o = pullInput(0);
      pushOutput(o, 0);
    }
    else {
      Object o = pullInput(1);
      pushOutput(o, 0);
    }
  }
}