package ncsa.d2k.modules.weka.attributeSelection.search;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import weka.attributeSelection.GeneticSearch;

public class WEKA_AttributeSelectionSearch_Genetic extends ComputeModule {

  //==============
  // Data Members
  //==============

  /** holds the start set for the search as a Range */
  private String m_startRange = "";

  /** the number of individual solutions */
  private int m_popSize = 20;

  /** the maximum number of generations to evaluate */
  private int m_maxGenerations = 20;

  /** the probability of crossover occuring */
  private double m_pCrossover = 0.6;

  /** the probability of mutation occuring */
  private double m_pMutation = 0.033;

  /** how often reports are generated */
  private int m_reportFrequency = 20;

  /** seed for random number generation */
  private int m_seed = 1;

  private GeneticSearch m_search = null;

  //================
  // Constructor(s)
  //================

  public WEKA_AttributeSelectionSearch_Genetic() {
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
      m_search = new GeneticSearch();
      m_search.setOptions(getOptions());
      pushOutput(m_search, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_AttributeSelectionSearch_Genetic");
      throw ex;
    }
  }

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This modules provides a exhaustive search startegy to the attribute     selector.  </body></html>";
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
   * Gets the current settings of ReliefFAttributeEval.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[14];
    int current = 0;

    if (!(getStartSet().equals(""))) {
      options[current++] = "-P";
      options[current++] = ""+ getStartSet();
    }
    options[current++] = "-Z";
    options[current++] = "" + getPopulationSize();
    options[current++] = "-G";
    options[current++] = "" + getMaxGenerations();
    options[current++] = "-C";
    options[current++] = "" + getCrossoverProb();
    options[current++] = "-M";
    options[current++] = "" + getMutationProb();
    options[current++] = "-R";
    options[current++] = "" + getReportFrequency();
    options[current++] = "-S";
    options[current++] = "" + getSeed();

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
    m_startRange = startSet;
  }

  /**
   * Returns a list of attributes (and or attribute ranges) as a String
   * @return a list of attributes (and or attribute ranges)
   */
  public String getStartSet() {
    return m_startRange;
  }

  /**
   * set the population size
   * @param p the size of the population
   */
  public void setPopulationSize(int p) {
    m_popSize = p;
  }

  /**
   * get the size of the population
   * @return the population size
   */
  public int getPopulationSize() {
    return m_popSize;
  }

  /**
   * set the number of generations to evaluate
   * @param m the number of generations
   */
  public void setMaxGenerations(int m) {
    m_maxGenerations = m;
  }

  /**
   * get the number of generations
   * @return the maximum number of generations
   */
  public int getMaxGenerations() {
    return m_maxGenerations;
  }

  /**
   * set the probability of crossover
   * @param c the probability that two population members will exchange
   * genetic material
   */
  public void setCrossoverProb(double c) {
    m_pCrossover = c;
  }

  /**
   * get the probability of crossover
   * @return the probability of crossover
   */
  public double getCrossoverProb() {
    return m_pCrossover;
  }

  /**
   * set the probability of mutation
   * @param m the probability for mutation occuring
   */
  public void setMutationProb(double m) {
    m_pMutation = m;
  }

  /**
   * get the probability of mutation
   * @return the probability of mutation occuring
   */
  public double getMutationProb() {
    return m_pMutation;
  }

  /**
   * set how often reports are generated
   * @param f generate reports every f generations
   */
  public void setReportFrequency(int f) {
    m_reportFrequency = f;
  }

  /**
   * get how often repports are generated
   * @return how often reports are generated
   */
  public int getReportFrequency() {
    return m_reportFrequency;
  }

  /**
   * set the seed for random number generation
   * @param s seed value
   */
  public void setSeed(int s) {
    m_seed = s;
  }

  /**
   * get the value of the random number generator's seed
   * @return the seed for random number generation
   */
  public int getSeed() {
    return m_seed;
  }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_AttributeSelectionSearch_Genetic";
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
