package ncsa.d2k.modules.core.transform.filters.evaluation;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;

/**
 * CFS attribute subset evaluator.
 * For more information see: <p>
 *
 * Hall, M. A. (1998). Correlation-based Feature Subset Selection for Machine
 * Learning. Thesis submitted in partial fulfilment of the requirements of the
 * degree of Doctor of Philosophy at the University of Waikato. <p>
 *
 * Valid options are:
 *
 * -M <br>
 * Treat missing values as a seperate value. <p>
 *
 * -L <br>
 * Include locally predictive attributes. <p>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author wrapped in a d2k module by Anca Suvaiala
 * @version $Revision$
 */

public class SubsetEvalCFS extends ComputeModule {

	//==============
	// Data Members
	//==============
	/** Treat missing values as seperate values */
	private boolean m_missingSeperate;
	/** Include locally predicitive attributes */
	private boolean m_locallyPredictive;

	private CfsSubsetEval m_eval = null;

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			default :
				return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch (index) {
			default :
				return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {
		};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0 :
				return "Subset Evaluator to be used by the Attribute Selector with various search strategies";
			default :
				return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch (index) {
			case 0 :
				return "CFS Subset Evaluator";
			default :
				return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types =
			{ "ncsa.d2k.modules.projects.anca.attributeSelection.evaluation.ASEvaluation" };
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */

	public String getModuleInfo() {
		String s = "<p> Overview: ";
		s += "A module for supplying CFS Subset evaluator for the Attribute Selector module ";
		s += "</p>";
		s += "<p> Detailed Description: ";
		s += " CFS Attribute Subset Evaluator: Evaluates the worth of a subset of attributes ";
		s += "by considering the individual predictive ability of each feature ";
		s += "along with the degree of redundancy between them.";
		s += "Subsets of features that are highly correlated with the class ";
		s += "while having low intercorrelation are preferred.";
		s += " </p>";
		s += "<p> Data Type Restrictions: ";
		s += " The class attribute can be numeric or nominal and but all data must be in numeric form. ";
		s += " Records that have missing values for the class attribute must be removed. (use RemoveRowsWithMissing). ";
		s += " All nominal data must be converted to integers ( use ReplaceNominalWithInts). </p>";
		s += "<p> Data Handling: ";
		s += "The module does modify its input data: ";
		s += "it discretizes all numeric attributes.";
		return s;
		
	}

	
	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "CFS Subset Evaluator";
	}

	protected void doit() throws java.lang.Exception {
		try {
			m_eval = new CfsSubsetEval();
			m_eval.setOptions(getOptions());
			pushOutput(m_eval, 0);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println("ERROR: SubsetEvalCFS ");
			throw ex;
		}
	}

	/**
	 * Include locally predictive attributes
	 *
	 * @param b true or false
	 */
	public void setLocallyPredictive(boolean b) {
		m_locallyPredictive = b;
	}

	/**
	 * Return true if including locally predictive attributes
	 *
	 * @return true if locally predictive attributes are to be used
	 */
	public boolean getLocallyPredictive() {
		return m_locallyPredictive;
	}

	/**
	 * Treat missing as a seperate value
	 *
	 * @param b true or false
	 */
	public void setMissingSeperate(boolean b) {
		m_missingSeperate = b;
	}

	/**
	 * Return true is missing is treated as a seperate value
	 *
	 * @return true if missing is to be treated as a seperate value
	 */
	public boolean getMissingSeperate() {
		return m_missingSeperate;
	}

	/**
	 * Gets the current settings of CfsSubsetEval
	 *
	 * @return an array of strings suitable for passing to setOptions()
	 */
	public String[] getOptions() {
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

		return options;
	}

	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] pds = new PropertyDescription[2];
		pds[0] =
			new PropertyDescription(
				"missingSeperate",
				"Separate missing values",
				"Treat missing as a separate value. Otherwise, counts for missing "
					+ "values are distributed across other values in proportion to their "
					+ "frequency.");

		pds[1] =
			new PropertyDescription(
				"locallyPredictive",
				"Indentify locally predictive attributes",
				"Iteratively adds attributes with the highest correlation with the class as long "
					+ "as there is not already an attribute in the subset that has a "
					+ "higher correlation with the attribute in question");
		return pds;
	}

}
