package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.emo.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.mutation.*;

public class TestParams extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.EMOParams"};
  }

  public String[] getOutputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.EMOParams"};
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

  public void doit() {
    EMOParams params = (EMOParams)pullInput(0);

    params.createBinaryIndividuals = false;
/*    params.tournamentSize = 4;
    params.crossoverRate = 0.25;
    params.generationGap = 0.6;
    params.nSimulatedBinaryCrossover = 2;
    params.mutationRate = 0.1;
    params.nRealMutation = 2.0;
*/
    params.populationSize = 250;
    params.maxGenerations = 100;

    pushOutput(params, 0);
  }
}