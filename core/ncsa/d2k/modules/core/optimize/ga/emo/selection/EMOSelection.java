package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.*;

public class EMOSelection extends ComputeModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
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

  public void beginExecution() {
    population = null;
  }

  public void endExecution() {
    population = null;
    selectionModule = null;
  }

  private EMOPopulation population;
  private Selection selectionModule;

  public void doit() throws Exception {
    EMOPopulation pop = (EMOPopulation)pullInput(0);

    if(population != pop) {
      EMOParams info = pop.getParameters();
      selectionModule = info.selection;
    }

    selectionModule.performSelection((Population)pop);
    pushOutput(pop, 0);
  }
}
