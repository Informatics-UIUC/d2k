package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.core.modules.*;

public class MeasureEMOPopulation extends MeasureModule {

  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }

  public void doit() {
    Population p = (Population)pullInput(0);

    p.computeStatistics ();

    // set if we have hit the target fitness.
    if (p.isDone ()) {
      pushOutput(p, 1);
    }else
            this.pushOutput (p, 0);

  }

}