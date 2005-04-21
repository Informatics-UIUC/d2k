package ncsa.d2k.modules.weka.filters.search;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;

/**
 * Class for ranking the attributes evaluated by a AttributeEvaluator
 *
 * Valid options are: <p>
 *
 * -P <start set> <br>
 * Specify a starting set of attributes. Eg 1,4,7-9. <p>
 *
 * -T <threshold> <br>
 * Specify a threshold by which the AttributeSelection module can. <br>
 * discard attributes. <p>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author wrapped in a d2k module by Anca Suvaiala
 * @version $Revision$
 */

public class AttributeSearchRanker extends ComputeModule {

  //==============
  // Data Members
  //==============

  /** holds the start set as a range */
  private String m_startRange = "";

  /**
   * A threshold by which to discard attributes---used by the
   * AttributeSelection module
   */
  private double m_threshold = -Double.MAX_VALUE;

  /** The number of attributes to select. -1 indicates that all attributes
      are to be retained. Has precedence over m_threshold */
  private int m_numToSelect = -1;

  private Ranker m_search = null;

  //================
  // Constructor(s)
  //================

  public AttributeSearchRanker() {
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

  protected void doit() throws java.lang.Exception {
    try {
      m_search = new Ranker();
      m_search.setOptions(getOptions());
      pushOutput(m_search, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: AttributeSearchRanker");
      throw ex;
    }
  }

  public String getModuleInfo () {
			String s = "<p> Overview: ";
			s += " This modules provides a ranking mechanism to be used in conjunction with attribute evaluators. </p>";
			s += "<p> Detailed Description: ";
			s += " Ranks attributes by their individual evaluations. ";
	      s +="Use in conjunction with attribute evaluators (ReliefF, GainRatio, ";
    	  s +=" etc)  Ranker is only capable of generating attribute rankings.";
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
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.projects.anca.attributeSelection.search.ASSearch"};
		return types;
	}

  public String getInputInfo(int parm1) {
		switch (parm1) {
			default: return "No such input";
		}
	}

  // Options

  /**
   * Gets the current settings of Ranker
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[6];
    int current = 0;

    if (!(getStartSet().equals(""))) {
      options[current++] = "-P";
      options[current++] = "" + getStartSet();
    }

    options[current++] = "-T";
    options[current++] = "" + getThreshold();

    options[current++] = "-N";
    options[current++] = ""+getNumToSelect();

    while (current < options.length) {
      options[current++] = "";
    }
    return  options;
  }


  /**
   * Set the threshold by which the AttributeSelection module can discard
   * attributes.
   * @param threshold the threshold.
   */
  public void setThreshold(double threshold) {
    m_threshold = threshold;
  }

  /**
   * Returns the threshold so that the AttributeSelection module can
   * discard attributes from the ranking.
   */
  public double getThreshold() {
    return m_threshold;
  }

  /**
   * Specify the number of attributes to select from the ranked list. -1
   * indicates that all attributes are to be retained.
   * @param n the number of attributes to retain
   */
  public void setNumToSelect(int n) {
    m_numToSelect = n;
  }

  /**
   * Gets the number of attributes to be retained.
   * @return the number of attributes to retain
   */
  public int getNumToSelect() {
    return m_numToSelect;
  }

  /**
   * Sets a starting set of attributes for the search. It is the
   * search method's responsibility to report this start set (if any)
   * in its toString() method.
   * @param startSet a string containing a list of attributes (and or ranges),
   * eg. 1,2,6,10-15. "" indicates no start point.
   * If a start point is supplied, random search evaluates the
   * start point and then looks for subsets that are as good as or better
   * than the start point with the same or lower cardinality.
   * @exception Exception if start set can't be set.
   */
  public void setStartSet (String startSet) throws Exception {
    m_startRange = startSet;
  }

  /**
   * Returns a list of attributes (and or attribute ranges) as a String
   * @return a list of attributes (and or attribute ranges)
   */
  public String getStartSet () {
     if (m_startRange == null)
      return "";
    else
    return m_startRange;
  }

  public PropertyDescription [] getPropertiesDescriptions () {
			PropertyDescription [] pds = new PropertyDescription [3];

			pds[0] = new PropertyDescription ("startSet", "Search start set", "Specify a set of attributes to ignore. "
					+" When generating the ranking, Ranker will not evaluate the attributes "
					+" in this list. "
					+"This is specified as a comma "
					+"seperated list off attribute indexes starting at 1. It can include "
					+"ranges. Eg. 1,2,5-9,17.");
	pds[1] = new PropertyDescription ("threshold", "Threshold",
	 "Set threshold by which attributes can be discarded. Default value "
	+ "results in no attributes being discarded. Use either this option or "
	+"numToSelect to reduce the attribute set.");
	pds[2] = new PropertyDescription ("numToSelect", "Number To Select",
	"Specify the number of attributes to select from the ranked list.Default ( -1)"
   +" indicates that all attributes are to be retained.  Use either "
		 +"this option or a threshold to reduce the attribute set.");

			return pds;
		  }

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Attribute Search Ranker";
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
				return "Ranker Search";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
