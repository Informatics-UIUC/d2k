package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

class StochasticUniversalSamplingWrapper extends Selection {

  private StochasticUniversalSampling sus;

  StochasticUniversalSamplingWrapper() {
    sus = new StochasticUniversalSampling();
  }

  public void performSelection(Population p) {
    sus.performSelection(p);
  }

}
