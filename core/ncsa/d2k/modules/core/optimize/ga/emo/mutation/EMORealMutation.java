package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

public class EMORealMutation
    extends RealMutation implements Mutation {

  /** Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[0];
    return pds;
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return out;
  }

  public void mutatePopulation(Population p) {
    EMOPopulation pop = (EMOPopulation) p;
    this.setMutationRate(pop.getPopulationInfo().mutationRate);
    this.setN(pop.getPopulationInfo().n);
    super.mutatePopulation(p);
  }
}
