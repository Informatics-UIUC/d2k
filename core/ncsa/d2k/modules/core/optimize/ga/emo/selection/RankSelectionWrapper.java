package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

class RankSelectionWrapper extends RankSelectionObj
    implements BinaryIndividualFunction, RealIndividualFunction {

  private PressureProp pp;
  
  RankSelectionWrapper() {
    pp = new PressureProp();
  }

  public String getName() {
    return "Rank Selection";
  }
  
  public String getDescription() {
    return "";
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