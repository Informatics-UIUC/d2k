package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

class TruncationWrapper extends Selection
  implements BinaryIndividualProcess, RealIndividualProcess {

  private Truncation truncation;

  TruncationWrapper() {
    truncation = new Truncation();
  }

  public void performSelection(Population p) {
    truncation.setTournamentSize(getTournamentSize());
    truncation.performSelection(p);
  }
  
  public String getDescription() {
    return truncation.getModuleInfo();
  }

  public String getName() {
    return "Truncation";
  }
}
