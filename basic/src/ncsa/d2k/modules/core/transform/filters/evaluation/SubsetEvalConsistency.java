package ncsa.d2k.modules.core.transform.filters.evaluation;

import ncsa.d2k.core.modules.ComputeModule;


/**
 * Consistency attribute subset evaluator. <p>
 *
 * For more information see: <br>
 * Liu, H., and Setiono, R., (1996). A probabilistic approach to feature
 * selection - A filter solution. In 13th International Conference on
 * Machine Learning (ICML'96), July 1996, pp. 319-327. Bari, Italy.
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author wrapped in a d2k module by Anca Suvaiala
 * @version $Revision$
 */

public class SubsetEvalConsistency extends ComputeModule{

  //==============
  // Data Members
  //==============


  private ConsistencySubsetEval m_eval = null;

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
			case 0: return "Subset Evaluator for use with the Attribute Selector and various search strategies";
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
				return "Consistency Subset Evaluator";
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
		s += " A module for supplying Consistency Subset evaluator for the Attribute Selector module </p>";
		s += "<p> Detailed Description: ";
		s += "Consistency Attribute Subset Evaluator :Evaluates the worth of a subset of ";
		s +="attributes by the level of consistency in the class values when the ";
		s +="training instances are projected onto the subset of attributes. ";
		s +="Consistency of any subset can never be lower than that of the ";
		s +="full set of attributes, hence the usual practice is to use this ";
		s +="subset evaluator in conjunction with a Random or Exhaustive search ";
		s +="which looks for the smallest subset with consistency equal to that ";
		s +="of the full set of attributes. \n" ;
		s +="For more information see: " ;
		s += "Liu, H., and Setiono, R., (1996). A probabilistic approach to feature ";
		s += " selection - A filter solution. In 13th International Conference on ";
		s += "Machine Learning (ICML'96), July 1996, pp. 319-327. Bari, Italy. </p>";
		s += "<p> Data Type Restrictions: ";
		s += " The class attribute can be numeric or nominal and but all data must be in numeric form. ";
		s += " Records that have missing values for the class attribute must be removed. (use RemoveRowsWithMissing). ";
		s += " All nominal data must be converted to integers ( use ReplaceNominalWithInts). </p>";
		s += "<p> Data Handling: ";
		s += "The module does modify its input data:  ";
		s += "it discretizes all numeric attributes.";
		
		return s;
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Consistency Subset Evaluator";
	}



   protected void doit() throws java.lang.Exception {
    try {
      m_eval = new ConsistencySubsetEval();
      //m_eval.setOptions(getOptions());
      pushOutput(m_eval, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: SubsetEvalConsistency ");
      throw ex;
    }
  }


}
