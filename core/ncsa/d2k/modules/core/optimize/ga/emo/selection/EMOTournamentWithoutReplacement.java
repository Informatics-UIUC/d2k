package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

class EMOTournamentWithoutReplacement extends TournamentWithoutReplacementObj
  implements BinaryIndividualFunction, RealIndividualFunction {

  public String getDescription() {
    return "";
  }
  
  public String getName() {
    return "Tournament Without Replacement";
  }
    
  public Property[] getProperties() {
    return null;
  }
}
