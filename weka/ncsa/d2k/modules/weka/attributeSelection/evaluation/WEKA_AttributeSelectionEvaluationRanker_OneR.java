package ncsa.d2k.modules.weka.attributeSelection.evaluation;
//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.OneRAttributeEval;

public class WEKA_AttributeSelectionEvaluationRanker_OneR extends ComputeModule  {

  //==============
  // Data Members
  //==============


  private OneRAttributeEval m_eval = null;

  //================
  // Constructor(s)
  //================

  public WEKA_AttributeSelectionEvaluationRanker_OneR() {
  }

  //================
  // Public Methods
  //================


  protected void doit() throws java.lang.Exception {
    try {
      m_eval = new OneRAttributeEval();
      pushOutput(m_eval, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_AttributeSelectionEvaluation_OneR");
      throw ex;
    }
  }

  public String getModuleInfo() {
    return "A module for supplying a OneR attribute rank evaluator to the attribute selector.  WARNING:For some reason, this evaluator generates" +
            " a null pointer exception when run on the hypothyroid.arff dataset.  So it may have problems with other datasets as well.  This is a" +
            " a problem in WEKA not D2K.";
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



}