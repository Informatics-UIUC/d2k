package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
 * An EMOPopulation has an EMOPopulationParams.
 */
public interface EMOPopulation {
  public void setPopulationInfo(EMOPopulationParams popI);
  public EMOPopulationParams getPopulationInfo();
}