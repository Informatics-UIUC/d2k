package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

class EMOTournamentWithReplacement extends TournamentWithReplacementObj
  implements BinaryIndividualFunction, RealIndividualFunction, java.io.Serializable {

  public String getDescription() {
    return "";
  }

  public String getName() {
    return "Tournament With Replacement";
  }

  public Property[] getProperties() {
    return null;
  }
}
