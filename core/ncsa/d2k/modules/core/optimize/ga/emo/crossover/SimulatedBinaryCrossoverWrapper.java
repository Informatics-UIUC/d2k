package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.modules.core.optimize.ga.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

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
    extends SimulatedBinaryCrossoverObj implements RealIndividualFunction {

  private NProp n;
  
  SimulatedBinaryCrossoverWrapper() {
    n = new NProp();
  }

  public String getName() {
    return "Simulated Binary Crossover";
  }
  
  public String getDescription() {
    return "";
  }
  
  public Property[] getProperties() {
    return new Property[] {n};
  }

  private class NProp extends Property {
    private boolean isDirty = false;

    NProp() {
      super(Property.DOUBLE, "n", "don't know", new Double(2));
    }

    public void setValue(Object val) {
      super.setValue(val);
      isDirty = true;
    }
  }
}
