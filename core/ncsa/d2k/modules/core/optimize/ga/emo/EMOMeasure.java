package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;

public class EMOMeasure extends MeasureModule {
  
  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
  }
  
  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation",
        "ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }
  
  public void doit() {
    EMOPopulation pop = (EMOPopulation)pullInput(0);  
    
    Population p = (Population)pop;
    if(p.getCurrentGeneration() == p.getMaxGenerations()-1) {
      pushOutput(new Parameters(), 1);
    }
    else
      pushOutput(pop, 0); 
  }
}