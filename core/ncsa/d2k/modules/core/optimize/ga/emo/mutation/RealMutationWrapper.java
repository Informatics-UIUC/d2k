package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

class RealMutationWrapper
    extends Mutation implements RealIndividualProcess {

  private RealMutation rm;
  private NProp n;

  RealMutationWrapper() {
    rm = new RealMutation();
    n = new NProp();
  }

  public void setMutationRate(double mr) {
    super.setMutationRate(mr);
    rm.setMutationRate(mr);
  }

  public void mutatePopulation(Population p) {
    if(n.isDirty) {
      Double d = (Double)n.getValue();
      rm.setN(d.doubleValue());
      n.isDirty = false;
    }
    rm.mutatePopulation(p);
  }

  public String getName() {
    return "Real Mutation";
  }

  public Property[] getProperties() {
    return new Property[] {n};
  }

  private class NProp extends Property {
    boolean isDirty = false;

    NProp() {
      super(Property.DOUBLE, "n", "don't know", new Double(2));
    }

    public void setValue(Object v) {
      super.setValue(v);
      isDirty = true;
    }
  }
}