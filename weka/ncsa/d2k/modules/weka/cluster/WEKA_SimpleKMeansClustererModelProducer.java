package ncsa.d2k.modules.weka.cluster;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import weka.clusterers.*;
import weka.core.Instances;

/**
 * ModelGen wrapper around WEKA implementation of SimpleKMeans Clusterer
 * @author D. Searsmith
 */

public class WEKA_SimpleKMeansClustererModelProducer extends ModelProducerModule  {

  //==============
  // Data Members
  //==============


  //====== OPTIONS ==========

  /**
   * number of clusters to generate
   */
  private int m_NumClusters = 2;

  /**
   * random seed
   */
  private int m_Seed = 10;

  //================
  // Constructor(s)
  //================

  public WEKA_SimpleKMeansClustererModelProducer(){
  }

  //==================
  // Public Functions
  //==================

  /**
   * Return a description of the function of this module.
   * @return A description of this module.
   */
  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Module for generating a SimpleKMeans Clusterer Model.  </body></html>";
	}

  /**
   * Return the name of this module.
   * @return The name of this module.
   */
  public String getModuleName() {
		return "WEKA_SimpleKMeansClustererModelProducer";
	}

  /**
   * Return a String array containing the datatypes the inputs to this
   * module.
   * @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

  /**
   * Return a String array containing the datatypes of the outputs of this
   * module.
   * @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.core.modules.PredictionModelModule"};
		return types;
	}

  /**
   * Return a description of a specific input.
   * @param i The index of the input
   * @return The description of the input
   */
  public String getInputInfo(int i) {
		switch (i) {
			default: return "No such input";
		}
	}

  /**
   * Return the name of a specific input.
   * @param i The index of the input.
   * @return The name of the input
   */
  public String getInputName(int i) {
		switch(i) {
			default: return "NO SUCH INPUT!";
		}
	}

  /**
   * Return the description of a specific output.
   * @param i The index of the output.
   * @return The description of the output.
   */
  public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "ncsa.d2k.infrastructure.modules.PredictionModelModule: A SimpleKMeansClustererModelProducer module.";
			default: return "No such output";
		}
	}

  /**
   * Return the name of a specific output.
   * @param i The index of the output.
   * @return The name of the output
   */
  public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "SimpleKMeansClustererModel";
			default: return "NO SUCH OUTPUT!";
		}
	}

  /**
   * Create the model and push it out.
   */
  public void doit() throws java.lang.Exception {
    try {
      WEKA_SimpleKMeansClustererModel mod = new WEKA_SimpleKMeansClustererModel();
      mod.setOptions(getOptions());
      pushOutput(mod, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_SimpleKMeansClustererModelProducer");
      throw ex;
    }
  }


  //======= OPTIONS ==========

  /**
   * Gets the current settings of SimpleKMeans
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[4];
    int current = 0;

    options[current++] = "-N";
    options[current++] = "" + getNumClusters();
    options[current++] = "-S";
    options[current++] = "" + getSeed();

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
  }

  /**
   * set the number of clusters to generate
   *
   * @param n the number of clusters to generate
   */
  public void setNumClusters(int n) {
    m_NumClusters = n;
  }

  /**
   * gets the number of clusters to generate
   *
   * @return the number of clusters to generate
   */
  public int getNumClusters() {
    return m_NumClusters;
  }


  /**
   * Set the random number seed
   *
   * @param s the seed
   */
  public void setSeed (int s) {
    m_Seed = s;
  }


  /**
   * Get the random number seed
   *
   * @return the seed
   */
  public int getSeed () {
    return  m_Seed;
  }

}
