package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
 */
public abstract class MOPopulation extends Population {

  public MOPopulation(Range[] ranges) {
    super (ranges);
  }

  private EMOPopulationParams popInfo;

  public void setPopulationInfo(EMOPopulationParams popI) {
    popInfo = popI;
  }

  public EMOPopulationParams getPopulationInfo() {
    return popInfo;
  }
}
