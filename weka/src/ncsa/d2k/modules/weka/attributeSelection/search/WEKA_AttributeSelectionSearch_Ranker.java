package ncsa.d2k.modules.weka.attributeSelection.search;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import weka.attributeSelection.Ranker;

public class WEKA_AttributeSelectionSearch_Ranker extends ComputeModule {

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

  public WEKA_AttributeSelectionSearch_Ranker() {
  }

  //================
  // Public Methods
  //================

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "WEKA Search Module";
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
      System.out.println("ERROR: WEKA_AttributeSelectionSearch_Ranker");
      throw ex;
    }
  }

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This modules provides a ranking mechanism in conjunction with attribute     evaluators.  </body></html>";
	}

  public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes() {
		String[] types = {"WEKA Search Module"};
		return types;
	}

  public String getInputInfo(int parm1) {
		switch (parm1) {
			default: return "No such input";
		}
	}

  // Options

  /**
   * Gets the current settings of ReliefFAttributeEval.
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
    return m_startRange;
  }

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_AttributeSelectionSearch_Ranker";
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
				return "ASSearch";
			default: return "NO SUCH OUTPUT!";
		}
	}

   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[3];

      pds[0] = new PropertyDescription(
         "startSet",
         "Start Set",
         "The start set as a range.");

      pds[1] = new PropertyDescription(
         "threshold",
         "Threshold",
         "The threshold by which to discard attributes.");

      pds[2] = new PropertyDescription(
         "numToSelect",
         "Number to Select",
         "The number of attributes to select. -1 indicates that all attributes are to be retained. This takes precedence over the <i>Threshold</i> property.");

      return pds;

   }

}
