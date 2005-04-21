package ncsa.d2k.modules.weka.filters.evaluation;

import ncsa.d2k.core.modules.*;

public class AttributeEvalGainRatio extends ComputeModule{

  //==============
  // Data Members
  //==============

 /** Merge missing values */
  private boolean m_missing_merge = true;

  private GainRatioAttributeEval m_eval = null;

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "Attribute Evaluator for use with the Attribute Selector and Ranker search";
						default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Gain Ratio Evaluator";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.projects.anca.attributeSelection.evaluation.ASEvaluation"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */

	public String getModuleInfo() {
		String s = "<p> Overview: ";
		s += " A module for supplying a GainRatio attribute evaluator to the Attribute  Selection module.  ";
		s += "</p>";
		s += "<p> Detailed Description: ";
		s += "GainRatio Attribute Evaluator : Evaluates the worth of an attribute ";
		s += "by measuring the gain ratio with respect to the class.\n";
		s += "GainR(Class, Attribute) = (H(Class) - H(Class | Attribute)) / ";
		s += "H(Attribute). ";
		s += " </p>";
		s += "<p> Data Type Restrictions: ";
		s += " The class attribute must be nominal and all data must be in numeric form. ";
		s += " All nominal data must be converted to integers ( use ReplaceNominalWithInts). </p>";
		s += "<p> Data Handling: ";
		s += "The module does modify its input data ";
		s += " it discretizes all numeric attributes.";
      s += "</p><p> Acknowledgement: ";
      s += "This module is a wrapper for functionality in the Weka project, available under ";
      s += "the GNU General Public License. See http://www.cs.waikato.ac.nz/ml/weka/.</p>";
		return s;

	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "GainRatio Attribute Evaluator";
	}



   protected void doit() throws java.lang.Exception {
    try {
      m_eval = new GainRatioAttributeEval();
      m_eval.setOptions(getOptions());
      pushOutput(m_eval, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: AttributeEvalGainRatio ");
      throw ex;
    }
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
   * Gets the current settings of WrapperSubsetEval.
   * @return an array of strings suitable for passing to setOptions()
   */
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




  public PropertyDescription [] getPropertiesDescriptions () {
		 PropertyDescription [] pds = new PropertyDescription [1];
		 pds[0] = new PropertyDescription ("missingMerge", "Merge missing values",
			 " If True distribute counts for missing values. Counts are distributed "
		 +"across other values in proportion to their frequency. Otherwise, "
		 +"missing is treated as a separate value.");

		 		 return pds;
	   }
}
