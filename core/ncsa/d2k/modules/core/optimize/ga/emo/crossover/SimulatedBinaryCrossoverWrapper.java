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
    extends Crossover implements RealIndividualProcess {

  private SimulatedBinaryCrossover sbc;
  private NProp n;

  SimulatedBinaryCrossoverWrapper() {
    sbc = new SimulatedBinaryCrossover();
    n = new NProp();
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
    if(n.isDirty) {
      Double d = (Double)n.getValue();
      sbc.setN(d.doubleValue());
      n.isDirty = false;
    }
    sbc.performCrossover(p);
  }

  public String getName() {
    return "Simulated Binary Crossover";
  }
  
  public String getDescription() {
    return sbc.getModuleInfo();
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
