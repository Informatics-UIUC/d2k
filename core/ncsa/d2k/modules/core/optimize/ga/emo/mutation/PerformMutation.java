package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.mutation.*;

public class PerformMutation extends ComputeModule {

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
    mutation = null;
  }

  private transient EMOPopulation population;
  private transient Mutation mutation;

  public void doit() throws Exception {
    EMOPopulation pop = (EMOPopulation)pullInput(0);

    if(population != pop) {
      Parameters info = pop.getParameters();
      mutation = info.mutation;
      population = pop;
    }

    mutation.mutatePopulation((Population)pop);
    pushOutput(pop, 0);
System.out.println("mutate!");    
  }
}
