package ncsa.d2k.modules.core.transform.filters.search;

//==============
// Java Imports
//==============

import java.beans.PropertyVetoException;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.transform.filters.Range;

/**
 * Class for performing a best first search. <p>
 *
 * Valid options are: <p>
 *
 * -P <start set> <br>
 * Specify a starting set of attributes. Eg 1,4,7-9. <p>
 *
 * -D <-1 = backward | 0 = bidirectional | 1 = forward> <br>
 * Direction of the search. (default = 1). <p>
 *
 * -N <num> <br>
 * Number of non improving nodes to consider before terminating search.
 * (default = 5). <p>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author wrapped in a d2k module by Anca Suvaiala
 * @version $Revision$
 */

public class AttributeSearchBestFirst extends ComputeModule {

  //==============
  // Data Members
  //==============

  private int m_maxStale=5;

  /** 0 == backward search, 1 == forward search, 2 == bidirectional */
  private int m_searchDirection;

  /** holds the start set for the search as a Range */
  private Range m_startRange;

  private BestFirst m_search = null;

  //================
  // Constructor(s)
  //================

  public AttributeSearchBestFirst() {
   m_startRange = new Range();
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
      m_search = new BestFirst();
      m_search.setOptions(getOptions());
      pushOutput(m_search, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: AttributeSearchBestFirst");
      throw ex;
    }
  }

  public String getModuleInfo () {
			String s = "<p> Overview: ";
			s += " Provides a search strategy over the attribute/feature space. </p>";
			s += "<p> Detailed Description: ";
			s += "Searches the space of attribute subsets by greedy hillclimbing ";
	s +="augmented with a backtracking facility. Setting the number of ";
	s +="consecutive non-improving nodes allowed controls the level of ";
	s +="backtracking done. Best first may start with the empty set of ";
	s +="attributes and search forward, or start with the full set of ";
	s +="attributes and search backward, or start at any point and search ";
	s +="in both directions (by considering all possible single attribute ";
	s +="additions and deletions at a given point).";
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
   * Gets the current settings of BestFirst.
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[6];
    int current = 0;

    if (!(getStartSet().equals(""))) {
      options[current++] = "-P";
      options[current++] = ""+m_startRange.getRanges();
    }
    options[current++] = "-D";
    options[current++] = "" + m_searchDirection;
    options[current++] = "-N";
    options[current++] = "" + m_maxStale;

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
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
   * Set the numnber of non-improving nodes to consider before terminating
   * search.
   *
   * @param t the number of non-improving nodes
   * @exception Exception if t is less than 1
   */
  public void setSearchTermination (int t)
    throws PropertyVetoException
  {
    if (t < 1) {
      throw  new PropertyVetoException("Value of N must be > 0.",null);
    }

    m_maxStale = t;
  }


  /**
   * Get the termination criterion (number of non-improving nodes).
   *
   * @return the number of non-improving nodes
   */
  public int getSearchTermination () {
    return  m_maxStale;
  }

  
  /**
   * Set the search direction
   *
   * @param d the direction of the search
   */
  public void setDirection (int d) {

      m_searchDirection = d;

  }


  /**
   * Get the search direction
   *
   * @return the direction of the search
   */
  public int getDirection () {

    return m_searchDirection;
  }


  public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [3];
		pds[0] = new PropertyDescription ("direction", "Search Direction", "-1 = backward | 0 = bidirectional | 1 = forward");
		pds[1] = new PropertyDescription ("searchTermination", "Number of non-improving nodes",
		 "Set the amount of backtracking. Specify the number of non-improving nodes before search is terminated");
	pds[2] = new PropertyDescription ("startSet", "Search start set", " A string containing a list of attributes (and or ranges)" +""
  + "eg. 1,2,6,10-15.");
		return pds;
	  }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Best First Search";
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
				return "Best First Search";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
