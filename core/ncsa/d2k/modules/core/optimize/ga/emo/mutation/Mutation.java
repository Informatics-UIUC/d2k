package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

public abstract class Mutation {

  public abstract void mutatePopulation(Population p);
  public abstract String getName();
  public abstract String getDescription();

  private double mutationRate;
  public void setMutationRate(double mr) {
    mutationRate = mr;
  }
  public double getMutationRate() {
    return mutationRate;
  }

  public Property[] getProperties() { return null;}
}