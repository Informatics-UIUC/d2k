package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
 * A special crossover module for EMO that can invoke any of the crossover methods
 * enumerated in the Crossover interface.  An object is then created and the
 * performCrossover method is called on that object.
 */
public class PerformCrossover extends ComputeModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
  }

  public String getInputName(int i) {
    return "Population";
  }

  public String[] getOutputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
  }

  public String getOutputName(int i) {
    return "Population";
  }

  public String getInputInfo(int i) {
    return "The population before crossover.";
  }

  public String getOutputInfo(int i) {
    return "The population after the crossover.";
  }

  public String getModuleInfo() {
    return "";
  }

  public void beginExecution() {
    population = null;
  }

  public void endExecution() {
    population = null;
    crossoverModule = null;
  }

  private EMOPopulation population;
  private Crossover crossoverModule;

  public void doit() throws Exception {
    EMOPopulation pop = (EMOPopulation)pullInput(0);

    // if the population has changed or the crossoverType has not been set,
    // create a Crossover object
    if(population != pop) {
      EMOParams info = pop.getParameters();
      crossoverModule = info.crossover;
      population = pop;
    }

    // do the crossover
    crossoverModule.performCrossover((Population)pop);

    // run the module
    pushOutput(pop, 0);
  }
}