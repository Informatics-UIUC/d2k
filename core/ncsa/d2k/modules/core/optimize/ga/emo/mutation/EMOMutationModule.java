package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.*;

public class EMOMutationModule extends ComputeModule {

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
    mutationType = -1;
  }

  public void endExecution() {
    population = null;
    mutateModule = null;
  }

  private int mutationType = -1;
  private EMOPopulation population;
  private Mutation mutateModule;

  public void doit() throws Exception {
    EMOPopulation pop = (EMOPopulation)pullInput(0);

    if(mutationType == -1 || population != pop) {
      EMOPopulationParams info = pop.getPopulationInfo();
      mutationType = info.mutationType;

      String classname = Mutation.CLASSES[mutationType];
      population = pop;

      // create the module
      Class moduleClass;
      try {
        moduleClass = Class.forName(classname);
        mutateModule = (Mutation)moduleClass.newInstance();
      }
      catch(ClassNotFoundException nfe) {
        throw nfe;
      }
    }

    mutateModule.mutatePopulation((Population)pop);
    pushOutput(pop, 0);
  }
}
