package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

class TournamentWithReplacementWrapper extends Selection
  implements BinaryIndividualProcess, RealIndividualProcess {

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
  
  public String getDescription() {
    return twr.getModuleInfo();
  }
  

  public String getName() {
    return "Tournament With Replacement";
  }
}
