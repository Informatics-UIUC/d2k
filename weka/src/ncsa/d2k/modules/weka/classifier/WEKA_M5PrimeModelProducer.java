package ncsa.d2k.modules.weka.classifier;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import weka.classifiers.m5.*;
import weka.core.Instances;

public class WEKA_M5PrimeModelProducer extends ModelProducerModule  {

  //==============
  // Data Members
  //==============

  //====== OPTIONS ==========

  /** No smoothing? */
  private boolean m_UseUnsmoothed = false;

  /** Pruning factor */
  private double m_PruningFactor = 2;

  /** Type of model */
  private String m_ModelType = "m";

  /** Verbosity */
  private int m_Verbosity = 0;

  //================
  // Constructor(s)
  //================

  //==================
  // Public Functions
  //==================

  /**
   * Return a description of the function of this module.
   * @return A description of this module.
   */
  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Class for generating a model tree model (M5Prime)  </body></html>";
	}

  /**
   * Return the name of this module.
   * @return The name of this module.
   */
  public String getModuleName() {
		return "WEKA_M5PrimeModelProducer";
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
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
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
			case 0: return "ncsa.d2k.infrastructure.modules.PredictionModelModule: A WEKA_M5PrimeModel module.";
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
				return "WEKA_M5PrimeModel";
			default: return "NO SUCH OUTPUT!";
		}
	}

  /**
   * Create the model and push it out.
   */
  public void doit() throws java.lang.Exception {
    try {
      WEKA_M5PrimeModel m_model = new WEKA_M5PrimeModel();
      m_model.setOptions(getOptions());
      pushOutput(m_model, 0);
   } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_M5PrimeModelProducer.doit()");
      throw ex;
    }
  }


  //======= OPTIONS ==========

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {

    String [] options = new String [7];
    int current = 0;

    if (m_ModelType.equals("m")){
      options[current++] = "-O"; options[current++] = "m";
      if (m_UseUnsmoothed) {
	options[current++] = "-U";
      }
    } else {
      options[current++] = "-O"; options[current++] = m_ModelType;
    }
    options[current++] = "-F"; options[current++] = "" + m_PruningFactor;
    options[current++] = "-V"; options[current++] = "" + m_Verbosity;

    while (current < options.length) {
      options[current++] = "";
    }
    return options;
  }

  /**
   * Get the value of UseUnsmoothed.
   *
   * @return Value of UseUnsmoothed.
   */
  public boolean getUseUnsmoothed() {

    return m_UseUnsmoothed;
  }

  /**
   * Set the value of UseUnsmoothed.
   *
   * @param v  Value to assign to UseUnsmoothed.
   */
  public void setUseUnsmoothed(boolean v) {

    m_UseUnsmoothed = v;
  }

  /**
   * Get the value of PruningFactor.
   *
   * @return Value of PruningFactor.
   */
  public double getPruningFactor() {

    return m_PruningFactor;
  }

  /**
   * Set the value of PruningFactor.
   *
   * @param v  Value to assign to PruningFactor.
   */
  public void setPruningFactor(double v) {

    m_PruningFactor = v;
  }

  /**
   * Get the value of Model.
   *
   * @return Value of Model.
   */
  public String getModelType() {
    return m_ModelType;
  }

  /**
   * Set the value of Model.
   *
   * @param v  Value to assign to Model.
   */
  public void setModelType(String newMethod) {
    if (newMethod.equals("m") || newMethod.equals("r") || newMethod.equals("l")) {
      m_ModelType = newMethod;
    }
  }

  /**
   * Get the value of Verbosity.
   *
   * @return Value of Verbosity.
   */
  public int getVerbosity() {

    return m_Verbosity;
  }

  /**
   * Set the value of Verbosity.
   *
   * @param v  Value to assign to Verbosity.
   */
  public void setVerbosity(int v) {

    m_Verbosity = v;
  }

}
