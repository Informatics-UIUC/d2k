package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

class RankSelectionWrapper extends Selection
    implements BinaryIndividualProcess, RealIndividualProcess {

  private RankSelection rs;
  private PressureProp pp;

  RankSelectionWrapper() {
    rs = new RankSelection();
    pp = new PressureProp();
  }

  public void performSelection(Population p) {
    if(pp.isDirty) {
      Double d = (Double)pp.getValue();
      rs.setSelectivePressure(d.doubleValue());
      pp.isDirty = false;
    }
    rs.performSelection(p);
  }

  public String getName() {
    return "Rank Selection";
  }

  public Property[] getProperties() {
    return new Property[] {pp};
  }

  private class PressureProp extends Property {
    boolean isDirty = false;

    PressureProp() {
      super(Property.DOUBLE, "selective pressure", "don't know", new Double(2));
    }

    public void setValue(Object val) {
      super.setValue(val);
      isDirty = true;
    }
  }
}