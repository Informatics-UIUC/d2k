package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

class RealMutationWrapper
    extends Mutation {

  private RealMutation rm;

  RealMutationWrapper() {
    rm = new RealMutation();
  }

  private double n;
  public void setN(double newN) {
    n = newN;
    rm.setN(newN);
  }
  public double getN() {
    return n;
  }

  public void setMutationRate(double mr) {
    super.setMutationRate(mr);
    rm.setMutationRate(mr);
  }

  public void mutatePopulation(Population p) {
    rm.mutatePopulation(p);
  }
}
