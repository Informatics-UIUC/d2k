package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
 */
public abstract class MOPopulation extends Population implements EMOPopulation {

  public MOPopulation(Range[] ranges) {
    super (ranges);
  }

  private EMOParams popInfo;

  public void setParameters(EMOParams popI) {
    popInfo = popI;
  }

  public EMOParams getParameters() {
    return popInfo;
  }
}
