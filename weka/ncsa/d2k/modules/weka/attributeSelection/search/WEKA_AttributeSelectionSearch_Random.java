package ncsa.d2k.modules.weka.attributeSelection.search;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import weka.attributeSelection.RandomSearch;

public class WEKA_AttributeSelectionSearch_Random extends ComputeModule {

  //==============
  // Data Members
  //==============

  /** holds the start set as a range */
  private String m_startRange = "";

  /** percentage of the search space to consider */
  private double m_searchSize = 25;

  /** output new best subsets as the search progresses */
  private boolean m_verbose = false;

  private RandomSearch m_search = null;

  //================
  // Constructor(s)
  //================

  public WEKA_AttributeSelectionSearch_Random() {
  }

  //================
  // Public Methods
  //================

  public String getOutputInfo(int parm1) {
    return "weka.attributeSelection.ASSearch";
  }

  protected void doit() throws java.lang.Exception {
    try {
      m_search = new RandomSearch();
      m_search.setOptions(getOptions());
      pushOutput(m_search, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_AttributeSelectionSearch_Random");
      throw ex;
    }
  }

  public String getModuleInfo() {
    return "This modules provides a random search startegy to the attribute selector.";
  }

  public String[] getInputTypes() {
    return null;
  }

  public String[] getOutputTypes() {
    String[] out = {"weka.attributeSelection.ASSearch"};
    return out;
  }

  public String getInputInfo(int parm1) {
    return "";
  }

  // Options

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
      options[current++] = "" + m_startRange;
    }

    options[current++] = "-F";
    options[current++] = "" + m_searchSize;

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
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
    m_searchSize = p;
  }

  /**
   * get the percentage of the search space to consider
   * @return the percent of the search space explored
   */
  public double getSearchPercent() {
    return m_searchSize;
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

}