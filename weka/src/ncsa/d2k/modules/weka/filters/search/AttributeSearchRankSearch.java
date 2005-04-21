package ncsa.d2k.modules.weka.filters.search;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.weka.filters.*;
import ncsa.d2k.modules.weka.filters.evaluation.*;


/**
 * Class for evaluating a attribute ranking (given by a specified
 * evaluator) using a specified subset evaluator. <p>
 *
 * Valid options are: <p>
 *
 * -A <attribute/subset evaluator> <br>
 * Specify the attribute/subset evaluator to be used for generating the
 * ranking. If a subset evaluator is specified then a forward selection
 * search is used to produce a ranked list of attributes.<p>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @version $Revision$
 * @author wrraped into a d2k module  - Anca Suvaiala
*/


public class AttributeSearchRankSearch extends ComputeModule {

  //==============
  // Data Members
  //==============

  /** the attribute evaluator to use for generating the ranking */
  private ASEvaluation m_ASEval ;

  private RankSearch m_search = null;

  //================
  // Constructor(s)
  //================

  public AttributeSearchRankSearch() {
  }

  //================
  // Public Methods
  //================

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "Search strategy to be used by the Attribute Selector";
			default: return "No such output";
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
					return "Rank Search";
				default: return "NO SUCH OUTPUT!";
			}
		}

  protected void doit() throws java.lang.Exception {
    try {
      m_ASEval = (ASEvaluation)pullInput(0);
      m_search = new RankSearch();
      m_search.setAttributeEvaluator(m_ASEval);
      pushOutput(m_search, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: AttributeSearchRankSearch");
      throw ex;
    }
  }

  public String getModuleInfo() {
	String s = "<p> Overview: ";
	s +="RankSearch : search strategy over the attribute /feature space";
	s += " </p>";
				s += "<p> Detailed Description: ";
		  s +="Uses an attribute/subset evaluator to rank all attributes. ";
		 s +="If a subset evaluator is specified, then a forward selection ";
		  s +="search is used to generate a ranked list. From the ranked ";
		  s +="list of attributes, subsets of increasing size are evaluated, ie. ";
		  s +="The best attribute, the best attribute plus the next best attribute, ";
		  s +="etc.... The best attribute set is reported. RankSearch is linear in ";
		  s +="the number of attributes if a simple attribute evaluator is used ";
		  s +="such as GainRatioAttributeEval.";
			 s += " </p>";
			 s += "<p> Data Type Restrictions: ";
			 s += " </p>";
			 s += "<p> Data Handling: ";
			 s += "The module does not destroy or modify the input data. ";
          s += "</p><p> Acknowledgement: ";
          s += "This module is a wrapper for functionality in the Weka project, available under ";
          s += "the GNU General Public License. See http://www.cs.waikato.ac.nz/ml/weka/.</p>";

			 return s;
	}

  public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.projects.anca.attributeSelection.evaluation.ASEvaluation"};
		return types;
	}

  public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.projects.anca.attributeSelection.search.ASSearch"};
		return types;
	}

  public String getInputInfo(int parm1) {
		switch (parm1) {
			default: return "Attribute Evaluator to be used in conjunction with  Rank Search";
		}
	}

	/**
		 * Return the human readable name of the indexed output.
		 * @param index the index of the output.
		 * @return the human readable name of the indexed output.
		 */
		public String getInputName(int index) {
			switch(index) {
				case 0:
					return "Attribute Evaluator";
				default: return "NO SUCH OUTPUT!";
			}
		}



}
