package ncsa.d2k.modules.weka.attributeSelection.evaluation;
//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.GainRatioAttributeEval;

public class WEKA_AttributeSelectionEvaluationRanker_GainRatio extends ComputeModule  {
  //==============
  // Data Members
  //==============

  /** Treat missing values as a seperate value */
  private boolean m_missing_merge;

  private GainRatioAttributeEval m_eval = null;

  //================
  // Constructor(s)
  //================

  public WEKA_AttributeSelectionEvaluationRanker_GainRatio() {
  }

  //================
  // Public Methods
  //================


  protected void doit() throws java.lang.Exception {
    try {
      m_eval = new GainRatioAttributeEval();
      m_eval.setOptions(getOptions());
      pushOutput(m_eval, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_AttributeSelectionEvaluation_GainRatio");
      throw ex;
    }
  }

  public String getModuleInfo() {
    return "A module for supplying a GainRatio attribute ranker to the attribute selector.";
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

  public String[] getOptions () {
    String[] options = new String[1];
    int current = 0;

    if (!getMissingMerge()) {
      options[current++] = "-M";
    }

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
  }


  /**
   * distribute the counts for missing values across observed values
   *
   * @param b true=distribute missing values.
   */
  public void setMissingMerge (boolean b) {
    m_missing_merge = b;
  }


  /**
   * get whether missing values are being distributed or not
   *
   * @return true if missing values are being distributed.
   */
  public boolean getMissingMerge () {
    return  m_missing_merge;
  }
}