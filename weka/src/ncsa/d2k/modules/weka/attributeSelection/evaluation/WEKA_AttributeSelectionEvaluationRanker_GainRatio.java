package ncsa.d2k.modules.weka.attributeSelection.evaluation;
//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
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
		return "<html>  <head>      </head>  <body>    A module for supplying a GainRatio attribute ranker to the attribute     selector.  </body></html>";
	}

  public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

  public String getInputInfo(int parm1) {
		switch (parm1) {
			default: return "No such input";
		}
	}

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "ASEvaluation";
			default: return "No such output";
		}
	}

  public String[] getOutputTypes() {
		String[] types = {"weka.attributeSelection.ASEvaluation"};
		return types;
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

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_AttributeSelectionEvaluationRanker_GainRatio";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
