package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

public abstract class EMOPopulation extends Population {

  public EMOPopulation(Range[] ranges) {
    super (ranges);
  }

  private EMOPopulationInfo popInfo;

  public void setPopulationInfo(EMOPopulationInfo popI) {
    popInfo = popI;
  }

  public EMOPopulationInfo getPopulationInfo() {
    return popInfo;
  }

}