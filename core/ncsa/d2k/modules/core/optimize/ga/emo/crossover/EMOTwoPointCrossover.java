package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.crossover.*;

/**
 * A special subclass of CrossoverModule that gets its properties
 * from the EMOPopulationParams.  This class implements the Crossover interface
 * so that the performCrossover() method can be called by EMOCrossover.
 *
 * This module can be placed in an itinerary or used by EMOCrossover.
 *
 * @author David Clutter
 * @version 1.0
 */
public class EMOTwoPointCrossover
    extends CrossoverModule implements Crossover {

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

/*  public void doit() {
    EMOPopulation pop = (EMOPopulation) pullInput(0);
    this.setCrossoverRate(pop.getPopulationInfo().crossoverRate);
    this.setGenerationGap(pop.getPopulationInfo().generationGap);
    this.performCrossover(pop);
    pushOutput(pop, 0);
  }*/

  public void performCrossover(Population p) {
    EMOPopulation pop = (EMOPopulation) p;
    this.setCrossoverRate(pop.getPopulationInfo().crossoverRate);
    this.setGenerationGap(pop.getPopulationInfo().generationGap);
    super.performCrossover(p);
  }
}