package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

class StochasticUniversalSamplingWrapper extends Selection
  implements BinaryIndividualProcess, RealIndividualProcess {

  private StochasticUniversalSampling sus;

  StochasticUniversalSamplingWrapper() {
    sus = new StochasticUniversalSampling();
  }

  public void performSelection(Population p) {
    sus.performSelection(p);
  }

  public String getName() {
    return "Stochastic Universal Sampling";
  }
}
