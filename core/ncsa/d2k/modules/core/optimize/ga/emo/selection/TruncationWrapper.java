package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

class TruncationWrapper extends Selection {

  private Truncation truncation;

  TruncationWrapper() {
    truncation = new Truncation();
  }

  public void performSelection(Population p) {
    truncation.setTournamentSize(getTournamentSize());
    truncation.performSelection(p);
  }
}
