package ncsa.d2k.modules.weka.evaluation;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.infrastructure.modules.ModelSelectorModule;
import ncsa.d2k.infrastructure.modules.ModelModule;


public class WEKA_SimpleModelSelector extends ModelSelectorModule {

  //==============
  // Data Members
  //==============

  private ModelModule m_model = null;

  //================
  // Constructor(s)
  //================

  public WEKA_SimpleModelSelector() {
  }

  //================
  // Public methods
  //================

  public String getModuleInfo() {
    return "Simply selects the last model it has received for storage.";

  }

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.PredictionModelModule"};
    return in;
  }

  public String getInputInfo(int parm1) {
    return "A PredicitonModelModule.";
  }

  public String getOutputInfo(int parm1) {
    return "";
  }

  public String[] getOutputTypes() {
    return null;
  }

  protected void doit() throws java.lang.Exception {
    try {
      m_model = (ModelModule)this.pullInput(0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_SimpleModelSelector.doit()");
      throw ex;
    }


  }

  public ModelModule getModel() {
    return m_model;
  }

}