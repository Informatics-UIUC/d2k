package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.mutation.*;

class EMOMutation
    extends Mutation implements BinaryIndividualFunction {

  public String getName() {
    return "Mutation";
  }
  
  public String getDescription() {
    return "";
  }
  
  public Property[] getProperties() {
    return null;
  }
}
