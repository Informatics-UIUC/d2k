package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

class StandardMutationWrapper
    extends Mutation {

  private MutateModule mm;

  StandardMutationWrapper() {
    mm = new MutateModule();
  }

  public void setMutationRate(double mr) {
    super.setMutationRate(mr);
    mm.setMutationRate(mr);
  }

  public void mutatePopulation(Population p) {
    mm.mutatePopulation(p);
  }
}
