package ncsa.d2k.modules.core.transform.filters.search;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.transform.filters.Range;


/**
 * Class for performing an exhaustive search. <p>
 *
 * Valid options are: <p>
 *
 * -P <start set> <br>
 * Specify a starting set of attributes. Eg 1,4,7-9. <p>
 *
 * -V <br>
 * Verbose output. Output new best subsets as the search progresses. <p>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author wrapped in a d2k module by Anca Suvaiala
 * @version $Revision$
 */


public class AttributeSearchExhaustiveSearch extends ComputeModule {

  //==============
  // Data Members
  //==============

  /** the start set as a Range */
  private Range m_startRange = new Range();


  /** if true, then ouput new best subsets as the search progresses */
  private boolean m_verbose;


  private ExhaustiveSearch m_search = null;

  //================
  // Constructor(s)
  //================

  public AttributeSearchExhaustiveSearch() {
  }

  //================
  // Public Methods
  //================

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "Search strategy to be used by the Attribute Selector with attribute subset selectors";
			default: return "No such output";
		}
	}

  protected void doit() throws java.lang.Exception {
    try {
      m_search = new ExhaustiveSearch();
      m_search.setOptions(getOptions());
      pushOutput(m_search, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: AttributeSearchExhaustiveSearch");
      throw ex;
    }
  }
  
  public String getModuleInfo () {
			String s = "<p> Overview: ";
			s += " Exhaustive Search stragtegy to be used in conjuction with Subset Evaluation.</p>";
			s += "<p> Detailed Description: ";
			s += " Performs an exhaustive search through ";
      s +="the space of attribute subsets starting from the empty set of ";
      s +="attributes. Reports the best subset found. If a start set is ";
      s +="supplied, the algorithm searches backward from the start point ";
      s +="and reports the smallest subset with as good or better evaluation ";
      s +="as the start point.";
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
	
	/**
		 * Return the human readable name of the indexed output.
		 * @param index the index of the output.
		 * @return the human readable name of the indexed output.
		 */
		public String getOutputName(int index) {
			switch(index) {
				case 0:
					return "Exhaustive Search";
				default: return "NO SUCH OUTPUT!";
			}
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
   * eg. 1,2,6,10-15. "" indicates no start set.
   * If a start point is supplied, Exhaustive search stops after finding
   * the smallest possible subset with merit as good as or better than the
   * start set. Otherwise, the search space is explored FULLY, and the
   * best subset returned.
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
   * Gets the current settings of ExhaustiveSearch.
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[3];
    int current = 0;

    if (!(getStartSet().equals(""))) {
      options[current++] = "-P";
      options[current++] = ""+m_startRange.getRanges();
    }

    if (m_verbose) {
      options[current++] = "-V";
    }

    while (current < options.length) {
      options[current++] = "";
    }
    return  options;
  }
  
  
  public PropertyDescription [] getPropertiesDescriptions () {
		  PropertyDescription [] pds = new PropertyDescription [2];
		  pds[0] = new PropertyDescription ("verbose", "Verbose", "Set whether or not to output new best subsets as the search proceeds.");
		  pds[1] = new PropertyDescription ("startSet", "Search start set", " A string containing a list of attributes (and or ranges)" +""
	+ "eg. 1,2,6,10-15." + "If a start point is supplied, Exhaustive search stops after finding" +
	" the smallest possible subset with merit as good as or better than the " +
	" start set. Otherwise, the search space is explored FULLY, and the" +
	" best subset returned");
		  return pds;
		}

  /**
   * converts the array of starting attributes to a string. This is
   * used by getOptions to return the actual attributes specified
   * as the starting set. This is better than using m_startRanges.getRanges()
   * as the same start set can be specified in different ways from the
   * command line---eg 1,2,3 == 1-3. This is to ensure that stuff that
   * is stored in a database is comparable.
   * @return a comma seperated list of individual attribute numbers as a String
   */
 /* private String startSetToString() {
    StringBuffer FString = new StringBuffer();
    boolean didPrint;

    if (m_starting == null) {
      return getStartSet();
    }

    for (int i = 0; i < m_starting.length; i++) {
      didPrint = false;

      if ((m_hasClass == false) ||
	  (m_hasClass == true && i != m_classIndex)) {
	FString.append((m_starting[i] + 1));
	didPrint = true;
      }

      if (i == (m_starting.length - 1)) {
	FString.append("");
      }
      else {
	if (didPrint) {
	  FString.append(",");
	  }
      }
    }

    return FString.toString();
  }

*/
}
