package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.modules.core.optimize.ga.*;

public abstract class Mutation {

  public abstract void mutatePopulation(Population p);

  private double mutationRate;
  public void setMutationRate(double mr) {
    mutationRate = mr;
  }
  public double getMutationRate() {
    return mutationRate;
  }
}