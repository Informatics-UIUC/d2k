package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

class RankSelectionWrapper extends Selection {

  private RankSelection rs;

  RankSelectionWrapper() {
    rs = new RankSelection();
  }

  private double selectionPressure;
  public void setSelectionPressure(double sp) {
    selectionPressure = sp;
    rs.setSelectivePressure(sp);
  }
  public double getSelectionPressure() {
    return selectionPressure;
  }

  public void performSelection(Population p) {
    rs.performSelection(p);
  }
}