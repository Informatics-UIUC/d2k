package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.crossover.*;

/**
 * A special subclass of SimulatedBinaryCrossover that gets its properties
 * from the EMOPopulationParams.  This class implements the Crossover interface
 * so that the performCrossover() method can be called by EMOCrossover.
 *
 * This module can be placed in an itinerary or used by EMOCrossover.
 *
 * @author David Clutter
 * @version 1.0
 */
class SimulatedBinaryCrossoverWrapper
    extends Crossover {

  private SimulatedBinaryCrossover sbc;

  SimulatedBinaryCrossoverWrapper() {
    sbc = new SimulatedBinaryCrossover();
  }

  public void setCrossoverRate(double cr) {
    super.setCrossoverRate(cr);
    sbc.setCrossoverRate(cr);
  }

  public void setGenerationGap(double gg) {
    super.setGenerationGap(gg);
    sbc.setGenerationGap(gg);
  }

  public void performCrossover(Population p) {
    sbc.performCrossover(p);
  }
}
