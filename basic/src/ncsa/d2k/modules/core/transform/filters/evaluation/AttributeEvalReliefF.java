package ncsa.d2k.modules.core.transform.filters.evaluation;

import ncsa.d2k.core.modules.*;

import java.beans.PropertyVetoException;

public class AttributeEvalReliefF extends ComputeModule{

  //==============
  // Data Members
  //==============


  /**
   * The number of instances to sample when estimating attributes
   * default == -1, use all instances
   */
  private int m_sampleM = -1;

  /** Random number seed used for sampling instances */
  private int m_seed = 1;

  /** The number of nearest hits/misses */
  private int m_Knn = 10;

  /** sigma is a user defined parameter, default=20 */
  private int m_sigma = 2;

  /** Weight by distance rather than equal weights */
  private boolean m_weightByDistance = false;

  private ReliefFAttributeEval m_eval = null;

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "Attribute Evaluator for use with the Attribute Selector and Ranking search";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "ReliefFEvaluator";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.projects.anca.attributeSelection.evaluation.ASEvaluation"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	
	public String getModuleInfo() {
		String s = "<p> Overview: ";
		s += " A module that supplies a ReliefF attribute evaluator for the Attribute Selector ";
		s += " </p>";
		s += "<p> Detailed Description: ";
		s +=" Class for evaluating attributes individually using ReliefF. <p>";
		
		s += " For more information see: <p>";
		s += " Kira, K. and Rendell, L. A. (1992). A practical approach to feature";
		s += " selection. In D. Sleeman and P. Edwards, editors, <i>Proceedings of";
		s += " the International Conference on Machine Learning,</i> pages 249-256,";
		s += " Morgan Kaufmann. <p>";
		
		s += " Kononenko, I. (1994). Estimating attributes: analysis and extensions of";
		s += " Relief. In De Raedt, L. and Bergadano, F., editors, <i> Machine Learning:";
		s += " ECML-94, </i> pages 171-182. Springer Verlag. <p>";
		
		s += " Marko Robnik Sikonja, Igor Kononenko: An adaptation of Relief for attribute";
		s += " estimation on regression. In D.Fisher (ed.): <i> Machine Learning,";
		s += " Proceedings of 14th International Conference on Machine Learning ICML'97,";
		s += " </i> Nashville, TN, 1997. <p>";
		s += "Evaluates the worth of an attribute ";
		s += "by computing the value of the chi-squared statistic with respect to the class.</p>";
		s += "<p> Data Type Restrictions: ";
		s += " Class attribute can be nominal or numeric. All data must be in numeric form (use ReplaceNominalswithInts)</p>";
		s += "<p> Data Handling: ";
		s += "The module does not destroy or modify the input data. ";
		return s;
		//TODO -finish up
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "ReliefF Attribute Evaluator";
	}



   protected void doit() throws java.lang.Exception {
    try {
      m_eval = new ReliefFAttributeEval();
      m_eval.setOptions(getOptions());
      pushOutput(m_eval, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: AttributeEvalReliefF ");
      throw ex;
    }
  }


 /**
   *
   * Valid options are: <p>
   *
   * -M <number of instances> <br>
   * Specify the number of instances to sample when estimating attributes. <br>
   * If not specified then all instances will be used. <p>
   *
   * -D <seed> <br>
   * Seed for randomly sampling instances. <p>
   *
   * -K <number of neighbours> <br>
   * Number of nearest neighbours to use for estimating attributes. <br>
   * (Default is 10). <p>
   *
   * -W <br>
   * Weight nearest neighbours by distance. <p>
   *
   * -A <sigma> <br>
   * Specify sigma value (used in an exp function to control how quickly <br>
   * weights decrease for more distant instances). Use in conjunction with <br>
   * -W. Sensible values = 1/5 to 1/10 the number of nearest neighbours. <br>
   *
   *
   **/


  /**
   * Sets the sigma value.
   *
   * @param s the value of sigma (> 0)
   * @exception Exception if s is not positive
   */
  public void setSigma (int s)
    throws PropertyVetoException
  {
    if (s <= 0) {
      throw  new PropertyVetoException("value of sigma must be > 0!",null);
    }

    m_sigma = s;
  }


  /**
   * Get the value of sigma.
   *
   * @return the sigma value.
   */
  public int getSigma () {
    return  m_sigma;
  }


  /**
   * Set the number of nearest neighbours
   *
   * @param n the number of nearest neighbours.
   */
  public void setNumNeighbours (int n) {
    m_Knn = n;
  }

    /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String numNeighboursTipText() {
    return "Number of nearest neighbours for attribute estimation.";
  }


  /**
   * Get the number of nearest neighbours
   *
   * @return the number of nearest neighbours
   */
  public int getNumNeighbours () {
    return  m_Knn;
  }

  
  /**
   * Set the random number seed for randomly sampling instances.
   *
   * @param s the random number seed.
   */
  public void setSeed (int s) {
    m_seed = s;
  }


  /**
   * Get the seed used for randomly sampling instances.
   *
   * @return the random number seed.
   */
  public int getSeed () {
    return  m_seed;
  }

  

  /**
   * Set the number of instances to sample for attribute estimation
   *
   * @param s the number of instances to sample.
   */
  public void setSampleSize (int s) {
    m_sampleM = s;
  }


  /**
   * Get the number of instances used for estimating attributes
   *
   * @return the number of instances.
   */
  public int getSampleSize () {
    return  m_sampleM;
  }

  

  /**
   * Set the nearest neighbour weighting method
   *
   * @param b true nearest neighbours are to be weighted by distance.
   */
  public void setWeightByDistance (boolean b) {
    m_weightByDistance = b;
  }


  /**
   * Get whether nearest neighbours are being weighted by distance
   *
   * @return m_weightByDiffernce
   */
  public boolean getWeightByDistance () {
    return  m_weightByDistance;
  }


  /**
   * Gets the current settings of ReliefFAttributeEval.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[9];
    int current = 0;

    if (getWeightByDistance()) {
      options[current++] = "-W";
    }

    options[current++] = "-M";
    options[current++] = "" + getSampleSize();
    options[current++] = "-D";
    options[current++] = "" + getSeed();
    options[current++] = "-K";
    options[current++] = "" + getNumNeighbours();
    if ( getWeightByDistance()) {
      options[current++] = "-A";
      options[current++] = "" + getSigma();
    }
    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
  }

  public PropertyDescription [] getPropertiesDescriptions () {
  	PropertyDescription [] pds = new PropertyDescription [5];
  	pds[0] = new PropertyDescription ("weightByDistance",
  			"Weight nearest neighbours by distance",
  	"If true use distance to weight nearest neighbours");
  	pds[1] = new PropertyDescription ("sampleSize", "Sample size",
  			"Number of instances to sample. Default (-1) indicates that all "
  			+"instances will be used for attribute estimation.");
  	pds[2] = new PropertyDescription ("seed", "Random Number Seed", 
  	"Random seed for sampling instances.");
  	pds[3] = new PropertyDescription ("sigma", "Sigma",
  			"Sigma value (used in an exp function to control how quickly" +
			" weights decrease for more distant instances). Use in conjunction with "+
  	" Weight by distance. Sensible values = 1/5 to 1/10 the number of nearest neighbours. ");
  	pds [4] = new PropertyDescription("numNeighbours","Number of nearest neighboors",
  	"Number of nearest neighbours for attribute estimation.");
  	return pds;
  }
}
