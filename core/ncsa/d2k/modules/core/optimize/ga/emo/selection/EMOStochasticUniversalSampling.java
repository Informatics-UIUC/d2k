package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

class EMOStochasticUniversalSampling extends StochasticUniversalSamplingObj
  implements BinaryIndividualFunction, RealIndividualFunction {

  public String getDescription() {
    return "";
  }

  public String getName() {
    return "Stochastic Universal Sampling";
  }
  
  public Property[] getProperties() {
    return null;
  }
}
