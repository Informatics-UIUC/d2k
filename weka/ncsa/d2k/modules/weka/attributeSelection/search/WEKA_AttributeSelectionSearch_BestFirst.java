package ncsa.d2k.modules.weka.attributeSelection.search;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import weka.attributeSelection.BestFirst;

public class WEKA_AttributeSelectionSearch_BestFirst extends ComputeModule  {

  //==============
  // Data Members
  //==============

  /** holds the start set as a range */
  private String m_startRange = "";

   // member variables
  /** maximum number of stale nodes before terminating search */
  private int m_maxStale = 5;

  /** 0 == backward search, 1 == forward search, 2 == bidirectional */
  private int m_searchDirection = 1;

  private BestFirst m_search = null;

  //================
  // Constructor(s)
  //================

  public WEKA_AttributeSelectionSearch_BestFirst() {
  }

  //================
  // Public Methods
  //================

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "weka.attributeSelection.ASSearch";
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
      System.out.println("ERROR: WEKA_AttributeSelectionSearch_BestFirst");
      throw ex;
    }
  }

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This modules provides a best first search startegy to the attribute     selector.  </body></html>";
	}

  public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes() {
		String[] types = {"weka.attributeSelection.ASSearch"};
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
      options[current++] = "" + getStartSet();
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
   * Set the numnber of non-improving nodes to consider before terminating
   * search.
   *
   * @param t the number of non-improving nodes
   * @exception Exception if t is less than 1
   */
  public void setSearchTermination (int t) throws Exception
  {
    if (t < 1) {
      throw  new Exception("Value of -N must be > 0.");
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
    return m_startRange;
  }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_AttributeSelectionSearch_BestFirst";
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
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
