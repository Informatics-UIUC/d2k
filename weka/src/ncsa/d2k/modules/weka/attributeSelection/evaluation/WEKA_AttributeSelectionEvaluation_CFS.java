package ncsa.d2k.modules.weka.attributeSelection.evaluation;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
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
		return "<html>  <head>      </head>  <body>    A module for supplying a CFS attribute subset evaluator to the attribute     selector.  </body></html>";
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


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_AttributeSelectionEvaluation_CFS";
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
