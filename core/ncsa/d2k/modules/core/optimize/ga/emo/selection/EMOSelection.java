package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.*;

public class EMOSelection extends ComputeModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  public void beginExecution() {
    population = null;
    selectionType = -1;
  }

  public void endExecution() {
    population = null;
    selectionModule = null;
  }

  private int selectionType = -1;
  private EMOPopulation population;
  private Selection selectionModule;

  public void doit() throws Exception {
    EMOPopulation pop = (EMOPopulation)pullInput(0);

    if(selectionType == -1 || population != pop) {
      EMOPopulationParams info = pop.getPopulationInfo();
      selectionType = info.selectionType;

      String classname = Selection.CLASSES[selectionType];

      population = pop;

      // create the module
      Class moduleClass;
      try {
        moduleClass = Class.forName(classname);
        selectionModule = (Selection)moduleClass.newInstance();
      }
      catch(ClassNotFoundException nfe) {
        throw nfe;
      }
    }

    selectionModule.performSelection((Population)pop);
    pushOutput(pop, 0);
  }
}
