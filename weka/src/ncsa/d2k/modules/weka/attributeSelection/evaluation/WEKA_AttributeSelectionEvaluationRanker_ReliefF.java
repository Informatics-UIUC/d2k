package ncsa.d2k.modules.weka.attributeSelection.evaluation;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ReliefFAttributeEval;

public class WEKA_AttributeSelectionEvaluationRanker_ReliefF extends ComputeModule  {

  //==============
  // Data Members
  //==============

  /** Weight by distance rather than equal weights */
  private boolean m_weightByDistance = false;

  private int m_sigma = 2;

  /** Random number seed used for sampling instances */
  private int m_seed = 1;

  /**
   * The number of instances to sample when estimating attributes
   * default == -1, use all instances
   */
  private int m_sampleM = -1;

  /** The number of nearest hits/misses */
  private int m_Knn = 10;

  private ReliefFAttributeEval m_eval = null;

  //================
  // Constructor(s)
  //================

  public WEKA_AttributeSelectionEvaluationRanker_ReliefF() {
  }

  //================
  // Public Methods
  //================


  protected void doit() throws java.lang.Exception {
    try {
      m_eval = new ReliefFAttributeEval();
      m_eval.setOptions(getOptions());
      pushOutput(m_eval, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_AttributeSelectionEvaluation_ReliefF");
      throw ex;
    }
  }

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    A module for supplying a ReliefF attribute rank evaluator to the attribute     selector. ReliefFAttributeEval : Evaluates the worth of an attribute by     repeatedly sampling an instance and considering the value of the given     attribute for the nearest instance of the same and different class. Can     operate on both discrete and continuous class data.  </body></html>";
	}

  public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

  public String getInputInfo(int parm1) {
		switch (parm1) {
			default: return "No such input";
		}
	}

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "WEKA Evaluation Module";
			default: return "No such output";
		}
	}

  public String[] getOutputTypes() {
		String[] types = {"weka.attributeSelection.ASEvaluation"};
		return types;
	}


  //Properties

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
    options[current++] = "-A";
    options[current++] = "" + getSigma();

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
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
   * Sets the sigma value.
   *
   * @param s the value of sigma (> 0)
   * @exception Exception if s is not positive
   */
  public void setSigma (int s)
    throws Exception
  {
    if (s <= 0) {
      throw  new Exception("value of sigma must be > 0!");
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
   * Get the number of nearest neighbours
   *
   * @return the number of nearest neighbours
   */
  public int getNumNeighbours () {
    return  m_Knn;
  }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_AttributeSelectionEvaluationRanker_ReliefF";
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
				return "WEKA Evaluation Module";
			default: return "NO SUCH OUTPUT!";
		}
	}

   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[5];

      pds[0] = new PropertyDescription(
         "weightByDistance",
         "Weight by Distance",
         "Weight by distance rather than equal weights?"
      );

      pds[1] = new PropertyDescription(
         "sigma",
         "Sigma",
         "The sigma value."
      );

      pds[2] = new PropertyDescription(
         "seed",
         "Seed",
         "The random number seed used for sampling instances."
      );

      pds[3] = new PropertyDescription(
         "sampleSize",
         "Sample Size",
         "The number of instances to sample when estimating attributes. The default, -1, uses all instances."
      );

      pds[4] = new PropertyDescription(
         "numNeighbours",
         "Number of Neighbours",
         "The number of nearest hits/misses."
      );

      return pds;

   }

}
