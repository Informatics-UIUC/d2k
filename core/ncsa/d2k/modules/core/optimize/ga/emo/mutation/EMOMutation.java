package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.*;

public class EMOMutation extends ComputeModule {

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
    mutateModule = null;
  }

  private EMOPopulation population;
  private Mutation mutateModule;

  public void doit() throws Exception {
    EMOPopulation pop = (EMOPopulation)pullInput(0);

    if(population != pop) {
      EMOParams info = pop.getParameters();
      mutateModule = info.mutation;
    }

    mutateModule.mutatePopulation((Population)pop);
    pushOutput(pop, 0);
  }
}
