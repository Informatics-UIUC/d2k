package ncsa.d2k.modules.core.transform.filters.search;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.transform.filters.*;


/**
 * Class for performing a forward selection hill climbing search. <p>
 *
 * Valid options are: <p>
 *
 * -P <start set> <br>
 * Specify a starting set of attributes. Eg 1,4,7-9. <p>
 *
 * -R <br>
 * Produce a ranked list of attributes. <p>
 *
 * -T <threshold> <br>
 * Specify a threshold by which the AttributeSelection module can. <br>
 * discard attributes. Use in conjunction with -R <p>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author wrapped in a d2k module by Anca Suvaiala
 * @version $Revision$
 */

public class AttributeSearchForwardSelection extends ComputeModule {

  //==============
  // Data Members
  //==============

  /** true if the user has requested a ranked list of attributes */
  private boolean m_rankingRequested;

  /**
   * A threshold by which to discard attributes---used by the
   * AttributeSelection module
   */
  private double m_threshold;

  /** The number of attributes to select. -1 indicates that all attributes
      are to be retained. Has precedence over m_threshold */
  private int m_numToSelect = -1;

  private int m_calculatedNumToSelect;

  /** holds the start set for the search as a Range */
  private Range m_startRange;

  /** holds an array of starting attributes */
  private int [] m_starting;


  private ForwardSelection m_search = null;

  //================
  // Constructor(s)
  //================

  public AttributeSearchForwardSelection() {
  }

  //================
  // Public Methods
  //================

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "Search strategy to be used by the Attribute Selector with attribute subset evaluators";
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
					return "Forward Selection Search";
				default: return "NO SUCH OUTPUT!";
			}
		}

  protected void doit() throws java.lang.Exception {
    try {
      m_search = new ForwardSelection();
      m_search.setOptions(getOptions());
      pushOutput(m_search, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: AttributeSearchForwardSelection");
      throw ex;
    }
  }

  public String getModuleInfo() {
	String s = "<p> Overview: ";
				s += " This modules provides a ranking mechanism to be used in conjunction with attribute evaluators. </p>";
				s += "<p> Detailed Description: ";
	s +="ForwardSelection: Performs a greedy forward search through ";
	s +="the space of attribute subsets. May start with no attributes or from ";
	s +="an arbitrary point in the space. Stops when the addition of any ";
	s +="remaining attributes results in a decrease in evaluation. ";
	s +="Can also produce a ranked list of ";
	s +="attributes by traversing the space from one side to the other and ";
	s +="recording the order that attributes are selected.";
			s += " </p>";
				s += "<p> Data Type Restrictions: ";
				s += " </p>";
				s += "<p> Data Handling: ";
				s += "The module does not destroy or modify the input data. ";
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
   * Specify the number of attributes to select from the ranked list
   * (if generating a ranking). -1
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
   * Gets the calculated number of attributes to retain. This is the
   * actual number of attributes to retain. This is the same as
   * getNumToSelect if the user specifies a number which is not less
   * than zero. Otherwise it should be the number of attributes in the
   * (potentially transformed) data.
   */
  public int getCalculatedNumToSelect() {
    if (m_numToSelect >= 0) {
      m_calculatedNumToSelect = m_numToSelect;
    }
    return m_calculatedNumToSelect;
  }

  
  /**
   * Records whether the user has requested a ranked list of attributes.
   * @param doRank true if ranking is requested
   */
  public void setGenerateRanking(boolean doRank) {
    m_rankingRequested = doRank;
  }

  /**
   * Gets whether ranking has been requested. This is used by the
   * AttributeSelection module to determine if rankedAttributes()
   * should be called.
   * @return true if ranking has been requested.
   */
  public boolean getGenerateRanking() {
    return m_rankingRequested;
  }

  

  /**
   * Sets a starting set of attributes for the search. It is the
   * search method's responsibility to report this start set (if any)
   * in its toString() method.
   * @param startSet a string containing a list of attributes (and or ranges),
   * eg. 1,2,6,10-15.
   * @exception Exception if start set can't be set.
   */
  public void setStartSet (String startSet) throws Exception {
    m_startRange.setRanges(startSet);
  }

  /**
   * Returns a list of attributes (and or attribute ranges) as a String
   * @return a list of attributes (and or attribute ranges)
   */
  public String getStartSet () {
     if (m_startRange == null)
      return "";
    else
    return m_startRange.getRanges();
  }

  /**
   * Gets the current settings of ReliefFAttributeEval.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[7];
    int current = 0;

    if (!(getStartSet().equals(""))) {
      options[current++] = "-P";
      options[current++] = ""+m_startRange.getRanges();
          }

    if (getGenerateRanking()) {
      options[current++] = "-R";
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

  public PropertyDescription [] getPropertiesDescriptions () {
		   PropertyDescription [] pds = new PropertyDescription [4];
		   pds[0] = new PropertyDescription ("startSet", "Search start set", 
	"Set the start point for the search. This is specified as a comma "
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
	pds[3] = new PropertyDescription ("generateRanking", "Generate Ranking", "Set to true if a ranked list is required.");
		   return pds;
		 }


}
