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
    Mutation mut = MutationFactory.createMutationOptions()[1];
    mut.setMutationRate(0.75);
    params.mutation = mut;

    Crossover cross = CrossoverFactory.createCrossoverOptions()[2];
    params.crossover = cross;
    cross.setCrossoverRate(0.1);
    cross.setGenerationGap(1);

    Selection selection = SelectionFactory.createSelectionOptions()[4];
    params.selection = selection;
    selection.setTournamentSize(4);

    params.populationSize = 250;
    params.maxGenerations = 100;

    pushOutput(params, 0);
  }
}