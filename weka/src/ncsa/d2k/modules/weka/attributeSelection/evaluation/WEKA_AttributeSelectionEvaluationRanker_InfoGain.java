package ncsa.d2k.modules.weka.attributeSelection.evaluation;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.InfoGainAttributeEval;

public class WEKA_AttributeSelectionEvaluationRanker_InfoGain extends ComputeModule  {
  //==============
  // Data Members
  //==============

  /** Treat missing values as a seperate value */
  private boolean m_missing_merge;

  /** Just binarize numeric attributes */
  private boolean m_Binarize;

  private InfoGainAttributeEval m_eval = null;

  //================
  // Constructor(s)
  //================

  public WEKA_AttributeSelectionEvaluationRanker_InfoGain() {
  }

  //================
  // Public Methods
  //================


  protected void doit() throws java.lang.Exception {
    try {
      m_eval = new InfoGainAttributeEval();
      m_eval.setOptions(getOptions());
      pushOutput(m_eval, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_AttributeSelectionEvaluationRanker_InfoGain");
      throw ex;
    }
  }

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    A module for supplying a InfoGain attribute rank evaluator to the     attribute selector.  </body></html>";
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
			case 0: return "WEKA Evaluation Module";
			default: return "No such output";
		}
	}

  public String[] getOutputTypes() {
		String[] types = {"weka.attributeSelection.ASEvaluation"};
		return types;
	}


  //Properties

  /**
   * Gets the current settings of WrapperSubsetEval.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[2];
    int current = 0;

    if (!getMissingMerge()) {
      options[current++] = "-M";
    }
    if (getBinarizeNumericAttributes()) {
      options[current++] = "-B";
    }

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
  }


  /**
   * Binarize numeric attributes.
   *
   * @param b true=binarize numeric attributes
   */
  public void setBinarizeNumericAttributes (boolean b) {
    m_Binarize = b;
  }


  /**
   * get whether numeric attributes are just being binarized.
   *
   * @return true if missing values are being distributed.
   */
  public boolean getBinarizeNumericAttributes () {
    return  m_Binarize;
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
		return "WEKA_AttributeSelectionEvaluationRanker_InfoGain";
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
				return "WEKA Evaluation Module";
			default: return "NO SUCH OUTPUT!";
		}
	}

   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[2];

      pds[0] = new PropertyDescription(
         "missingMerge",
         "Missing as Separate",
         "Treat missing values as a separate value?");

      pds[1] = new PropertyDescription(
         "binarizeNumericAttributes",
         "Binarize Numeric Attributes",
         "Just binarize numeric attributes?");

      return pds;

   }

}
