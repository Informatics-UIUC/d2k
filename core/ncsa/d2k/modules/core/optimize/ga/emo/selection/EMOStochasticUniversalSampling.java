package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

public class EMOStochasticUniversalSampling extends StochasticUniversalSampling implements Selection {

  /** Return an array with information on the properties the user may update.
    *  @return The PropertyDescriptions for properties the user may update.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
       PropertyDescription[] pds = new PropertyDescription [0];
       return pds;
   }

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return out;
  }

  public void performSelection(Population p) {
    super.performSelection(p);
  }

}
