package ncsa.d2k.modules.core.transform.filters.search;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.transform.filters.*;
import ncsa.d2k.modules.projects.anca.attributeSelection.*;

/**
 * Class for performing a genetic based search. <p>
 *
 * For more information see: <p>
 * David E. Goldberg (1989). Genetic algorithms in search, optimization and
 * machine learning. Addison-Wesley. <p>
 *
 * Valid options are: <p>
 *
 * -Z <size of the population> <br>
 * Sets the size of the population. (default = 20). <p>
 *
 * -G <number of generations> <br>
 * Sets the number of generations to perform.
 * (default = 5). <p>
 *
 * -C <probability of crossover> <br>
 * Sets the probability that crossover will occur.
 * (default = .6). <p>
 *
 * -M <probability of mutation> <br>
 * Sets the probability that a feature will be toggled on/off. <p>
 *
 * -R <report frequency> <br>
 * Sets how frequently reports will be generated. Eg, setting the value
 * to 5 will generate a report every 5th generation. <p>
 * (default = number of generations). <p>
 *
 * -S <seed> <br>
 * Sets the seed for random number generation. <p>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author wrapped in a d2k module by Anca Suvaiala
 * @version $Revision$

*/

public class AttributeSearchGeneticSearch extends ComputeModule {

  //==============
  // Data Members
  //==============


  /** holds the start set for the search as a Range */
  private Range m_startRange;

  /** the number of individual solutions */
  private int m_popSize =20;

  /** seed for random number generation */
  private int m_seed;

  /** the probability of crossover occuring */
  private double m_pCrossover = 0.6;

  /** the probability of mutation occuring */
  private double m_pMutation;

  /** the maximum number of generations to evaluate */
  private int m_maxGenerations =5;

  /** how often reports are generated */
  private int m_reportFrequency = m_maxGenerations;

  private GeneticSearch m_search = null;

  //================
  // Constructor(s)
  //================

  public AttributeSearchGeneticSearch() {
  }

  //================
  // Public Methods
  //================

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "Search strategy to be used by the Attribute Selectorin conjuction with subset evaluators";
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
					return "Genetic Search";
				default: return "NO SUCH OUTPUT!";
			}
		}

  protected void doit() throws java.lang.Exception {
    try {
      m_search = new GeneticSearch();
      m_search.setOptions(getOptions());
      pushOutput(m_search, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: AttributeSearchGeneticSearch");
      throw ex;
    }
  }

  public String getModuleInfo() {
	String s = "<p> Overview: ";
				 s += " This modules provides a search strategy to be used in conjunction with attribute subset evaluators. </p>";
		s += "<p> Detailed Description: Performs a search using the simple genetic ";
		  s +="algorithm described in Goldberg (1989).";
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
   * Gets the current settings of GeneticSearch
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[14];
    int current = 0;

    if (!(getStartSet().equals(""))) {
      options[current++] = "-P";
      options[current++] = ""+m_startRange.getRanges();
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

  public PropertyDescription [] getPropertiesDescriptions () {
			  PropertyDescription [] pds = new PropertyDescription [7];
			  pds[0] = new PropertyDescription ("seed", "Seed", "Set the random seed.");
			
			  pds[1] = new PropertyDescription ("startSet", "Search start set",
	"Set a start point for the search. This is specified as a comma "
		  +"seperated list off attribute indexes starting at 1. It can include "
		  +"ranges. Eg. 1,2,5-9,17. The start set becomes one of the population "
		  +"members of the initial population.");
			   
	  
	pds[2] = new PropertyDescription ("crossoverProb", "Crossover Probability",
	" Set the probability of crossover. This is the probability that "
	+"two population members will exchange genetic material.");
	
	pds[3] = new PropertyDescription ("maxGenerations", "Max Generations", "Set the number of generations to evaluate.");
	pds[4] = new PropertyDescription ("reportFrequency", "Report Frequency",
	"Set how frequently reports are generated. Default is equal to "
	  +"the number of generations meaning that a report will be printed for "
	  +"initial and final generations. Setting the value to 5 will result in "
	  +"a report being printed every 5 generations.");
	pds[5] = new PropertyDescription ("populationSize", "Population Size", 
	 "Set the population size. This is the number of individuals "
      +"(attribute sets) in the population.");
	pds[6] = new PropertyDescription ("mutationProb", "Mutation Probability", 
"Set the probability of mutation occuring.");
	
				  return pds;
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


}
