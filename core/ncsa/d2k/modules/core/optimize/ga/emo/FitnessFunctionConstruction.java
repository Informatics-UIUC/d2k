package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.transformations.Construction;

public class FitnessFunctionConstruction extends Construction {

  private boolean isMinimizing = false;

  public FitnessFunctionConstruction(String label, String expression) {
    super(label, expression);
  }

  public boolean getIsMinimizing() {
    return isMinimizing;
  }

  public void setIsMinimizing(boolean b) {
    this.isMinimizing = b;
  }
}