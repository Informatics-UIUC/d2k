package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

class TournamentWithReplacementWrapper extends Selection {

  private TournamentWithReplacement twr;

  TournamentWithReplacementWrapper() {
    twr = new TournamentWithReplacement();
  }

  public void setTournamentSize(int ts) {
    super.setTournamentSize(ts);
    twr.setTournamentSize(ts);
  }

  public void performSelection(Population p) {
    twr.performSelection(p);
  }
}
