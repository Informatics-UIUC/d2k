package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.modules.core.optimize.ga.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

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
    extends UniformCrossoverObj implements BinaryIndividualFunction {

  public String getName() {
    return "Uniform Crossover";
  }
  
  public String getDescription() {
    return "";
  }
  
  public Property[] getProperties() {
    return null;  
  }
}
