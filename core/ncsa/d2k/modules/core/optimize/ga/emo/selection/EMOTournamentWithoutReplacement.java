package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

public class EMOTournamentWithoutReplacement extends TournamentWithoutReplacement implements Selection {

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

  /*protected void compute (Population population) {
    EMOPopulation pop = (EMOPopulation)population;
    this.tsize = pop.getPopulationInfo().tournamentSize;
    super.compute(population);
  }*/

  public void performSelection(Population p) {
    EMOPopulation pop = (EMOPopulation)p;
    this.tsize = pop.getPopulationInfo().tournamentSize;
    super.performSelection(p);
  }

}