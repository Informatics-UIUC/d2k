package ncsa.d2k.modules.weka.filters.evaluation;

import ncsa.d2k.core.modules.*;


/**
 * Class for Evaluating attributes individually by measuring the
 * chi-squared statistic with respect to the class.
 *
 * Valid options are:<p>
 *
 * -M <br>
 * Treat missing values as a seperate value. <br>
 *
 * -B <br>
 * Just binarize numeric attributes instead of properly discretizing them. <br>
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author wrapped in a d2k module by Anca Suvaiala
 * @version $Revision$
 */


public class AttributeEvalChiSquared extends ComputeModule{

  //==============
  // Data Members
  //==============
  /** Treat missing values as a separate value */
  private boolean m_missing_merge = false;

  /** Just binarize numeric attributes */
  private boolean m_Binarize;

  private ChiSquaredAttributeEval m_eval = null;

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
			case 0: return "Attribute Evaluator for the Attribute Selector and Ranker search";
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
				return "ChiSquared Evaluator";
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

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "ChiSquared Attribute Evaluator";
	}

	public String getModuleInfo() {
			String s = "<p> Overview: ";
					 s += " A module that supplies a ChiSquared attribute evaluator for the Attribute Selector ";
					 s += " </p>";
					 s += "<p> Detailed Description: ";
					 s += "Evaluates the worth of an attribute ";
					 s += "by computing the value of the chi-squared statistic with respect to the class.</p>";
					 s += "<p> Data Type Restrictions: ";
					 s += " The class attribute must be nominal and all data must be in numeric form. ";
					 s += " All nominal data must be converted to integers ( use ReplaceNominalWithInts). </p>";
					 s += "<p> Data Handling: ";
					 s += "The module does modify its input data: removes rows with missing values for class, ";
					 s += "and discretizes all numeric attributes.";
                s += "</p><p> Acknowledgement: ";
                s += "This module is a wrapper for functionality in the Weka project, available under ";
                s += "the GNU General Public License. See http://www.cs.waikato.ac.nz/ml/weka/.</p>";
					 return s;

		}


   protected void doit() throws java.lang.Exception {
    try {
      m_eval = new ChiSquaredAttributeEval();
      m_eval.setOptions(getOptions());
      pushOutput(m_eval, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: AttributeEvalChiSquared ");
      throw ex;
    }
  }

  /**
   * Gets the current settings of ChiSquared attribute evaluation
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


  public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [2];
		pds[0] = new PropertyDescription ("missingMerge", "Merge missing values",
			" If True distribute counts for missing values. Counts are distributed "
	 	+"across other values in proportion to their frequency. Otherwise, "
		+"missing is treated as a separate value.");

		pds[1] = new PropertyDescription ("binarizeNumericAttributes", "Binarize numeric attributes",
		" If true , just binarize numeric attributes instead of properly discretizing them.");

		return pds;
	  }

}
