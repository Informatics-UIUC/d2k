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
 * Class for performing a random search. <p>
 *
 * Valid options are: <p>
 *
 * -P <start set> <br>
 * Specify a starting set of attributes. Eg 1,4,7-9. <p>
 *
 * -F <percent) <br>
 * Percentage of the search space to consider. (default = 25). <p>
 *
 * -V <br>
 * Verbose output. Output new best subsets as the search progresses. <p>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @version $Revision$
 * @author - wrapped in a d2k module by Anca Suvaiala
 */

public class AttributeSearchRandomSearch extends ComputeModule {

  //==============
  // Data Members
  //==============
  /** holds the start set as a range */
  private Range m_startRange = null;

  /** percentage of the search space to consider */
  private double m_searchSize = 0.25;

  /** output new best subsets as the search progresses */
  private boolean m_verbose = true;

  private RandomSearch m_search = null;

  //================
  // Constructor(s)
  //================

  public AttributeSearchRandomSearch() {
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
					return "Random Search";
				default: return "NO SUCH OUTPUT!";
			}
		}
  protected void doit() throws java.lang.Exception {
    try {
      m_search = new RandomSearch();
      m_search.setOptions(getOptions());
      pushOutput(m_search, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: AttributeSearchRandomSearch");
      throw ex;
    }
  }

  public String getModuleInfo() {
	String s = "<p> Overview: ";
			 s += " This modules provides a search strategy to be used in conjunction with attribute subset evaluators. </p>";
	s += "<p> Detailed Description: ";
	s += "RandomSearch : Performs a Random search in ";
    s  +="the space of attribute subsets. If no start set is supplied, Random ";
     s +="search starts from a random point and reports the best subset found. ";
     s +="If a start set is supplied, Random searches randomly for subsets ";
     s +="that are as good or better than the start point with the same or ";
     s +="or fewer attributes. Using RandomSearch in conjunction with a start ";
     s +="set containing all attributes equates to the LVF algorithm of Liu ";
     s +="and Setiono (ICML-96).";
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
    if(m_startRange == null ) m_startRange = new Range();
		if (startSet != null) m_startRange.setRanges(startSet);
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
   * set whether or not to output new best subsets as the search proceeds
   * @param v true if output is to be verbose
   */
  public void setVerbose(boolean v) {
    m_verbose = v;
  }

  /**
   * get whether or not output is verbose
   * @return true if output is set to verbose
   */
  public boolean getVerbose() {
    return m_verbose;
  }

  
  /**
   * set the percentage of the search space to consider
   * @param p percent of the search space ( 0 < p <= 100)
   */
  public void setSearchPercent(double p) {
    p = Math.abs(p);
    if (p == 0) {
      p = 25;
    }

    if (p > 100.0) {
      p = 100;
    }

    m_searchSize = (p/100.0);
  }

  /**
   * get the percentage of the search space to consider
   * @return the percent of the search space explored
   */
  public double getSearchPercent() {
    return m_searchSize;
  }

  /**
   * Gets the current settings of RandomSearch.
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[5];
    int current = 0;

    if (m_verbose) {
      options[current++] = "-V";
    }

    if (!(getStartSet().equals(""))) {
      options[current++] = "-P";
      options[current++] = ""+m_startRange.getRanges();
    }

    options[current++] = "-F";
    options[current++] = "" + m_searchSize;

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
  }

  public PropertyDescription [] getPropertiesDescriptions () {
			  PropertyDescription [] pds = new PropertyDescription [3];
			  pds[0] = new PropertyDescription ("verbose", "Verbose", 
			"Print progress information. Sends progress info to the terminal "	
      +"as the search progresses.");
			  pds[1] = new PropertyDescription ("startSet", "Search start set", " A string containing a list of attributes (and or ranges)" +""
		+ "eg. 1,2,6,10-15. " + "If a start point is supplied, Exhaustive search stops after finding" +
		" the smallest possible subset with merit as good as or better than the " +
		" start set. Otherwise, the search space is explored FULLY, and the" +
		" best subset returned");
	  
	pds[2] = new PropertyDescription ("searchPercent", "Search Percent", 
	"Percentage of the search space to explore.");
			  return pds;
			}


}
