package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;

public abstract class Selection {

  private int tournamentSize;
  public void setTournamentSize(int ts) {
    tournamentSize = ts;
  }
  public int getTournamentSize() {
    return tournamentSize;
  }

  public abstract void performSelection(Population p);
}