package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

class TruncationWrapper extends TruncationObj
  implements BinaryIndividualFunction, RealIndividualFunction {

  public String getDescription() {
    return "";
  }

  public String getName() {
    return "Truncation";
  }
  
  public Property[] getProperties() {
    return null;
  }
}
