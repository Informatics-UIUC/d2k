package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.crossover.*;

/**
 * A special subclass of UniformCrossover that gets its properties
 * from the EMOPopulationParams.  This class implements the Crossover interface
 * so that the performCrossover() method can be called by EMOCrossover.
 *
 * This module can be placed in an itinerary or used by EMOCrossover.
 *
 * @author David Clutter
 * @version 1.0
 */
class UniformCrossoverWrapper
    extends Crossover implements BinaryIndividualProcess {

  private UniformCrossoverModule ucm;

  UniformCrossoverWrapper() {
    ucm = new UniformCrossoverModule();
  }

  public void setCrossoverRate(double cr) {
    super.setCrossoverRate(cr);
    ucm.setCrossoverRate(cr);
  }

  public void performCrossover(Population p) {
    ucm.performCrossover(p);
  }

  public String getName() {
    return "Uniform Crossover";
  }
  
  public String getDescription() {
    return ucm.getModuleInfo();
  }
}
