package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

class TournamentWithoutReplacementWrapper extends Selection
  implements BinaryIndividualProcess, RealIndividualProcess {

  private TournamentWithoutReplacement twr;

  TournamentWithoutReplacementWrapper() {
    twr = new TournamentWithoutReplacement();
  }

  public void setTournamentSize(int ts) {
    super.setTournamentSize(ts);
    twr.setTournamentSize(ts);
  }

  public void performSelection(Population p) {
    twr.performSelection(p);
  }

  public String getName() {
    return "Tournament Without Replacement";
  }
}
