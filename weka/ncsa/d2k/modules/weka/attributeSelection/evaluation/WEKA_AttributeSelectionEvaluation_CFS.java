package ncsa.d2k.modules.weka.attributeSelection.evaluation;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.CfsSubsetEval;

public class WEKA_AttributeSelectionEvaluation_CFS extends ComputeModule {

  //==============
  // Data Members
  //==============

  /** Treat missing values as seperate values */
  private boolean m_missingSeperate;

  /** Include locally predicitive attributes */
  private boolean m_locallyPredictive;

  private CfsSubsetEval m_eval = null;

  //================
  // Constructor(s)
  //================

  public WEKA_AttributeSelectionEvaluation_CFS() {
  }

  //================
  // Public Methods
  //================


  protected void doit() throws java.lang.Exception {
    try {
      m_eval = new CfsSubsetEval();
      m_eval.setOptions(getOptions());
      pushOutput(m_eval, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_AttributeSelectionEvaluation_Cfs");
      throw ex;
    }
  }

  public String getModuleInfo() {
    return "A module for supplying a CFS attribute subset evaluator to the attribute selector.";
  }

  public String[] getInputTypes() {
    return null;
  }

  public String getInputInfo(int parm1) {
    return "";
  }

  public String getOutputInfo(int parm1) {
    return "ASEvaluation";
  }

  public String[] getOutputTypes() {
    String[] out = {"weka.attributeSelection.ASEvaluation"};
    return out;
  }


  //Properties

  /**
   * Gets the current settings of CfsSubsetEval
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[2];
    int current = 0;

    if (getMissingSeperate()) {
      options[current++] = "-M";
    }

    if (getLocallyPredictive()) {
      options[current++] = "-L";
    }

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
  }

  /**
   * Include locally predictive attributes
   *
   * @param b true or false
   */
  public void setLocallyPredictive (boolean b) {
    m_locallyPredictive = b;
  }


  /**
   * Return true if including locally predictive attributes
   *
   * @return true if locally predictive attributes are to be used
   */
  public boolean getLocallyPredictive () {
    return  m_locallyPredictive;
  }


  /**
   * Treat missing as a seperate value
   *
   * @param b true or false
   */
  public void setMissingSeperate (boolean b) {
    m_missingSeperate = b;
  }


  /**
   * Return true is missing is treated as a seperate value
   *
   * @return true if missing is to be treated as a seperate value
   */
  public boolean getMissingSeperate () {
    return  m_missingSeperate;
  }

}