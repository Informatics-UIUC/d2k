package ncsa.d2k.modules.weka.classifier;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

import weka.classifiers.kstar.*;
import weka.core.Instances;

public class WEKA_KStarModelProducer extends ModelProducerModule implements HasNames, KStarConstants {

  //==============
  // Data Members
  //==============

  /** missing value treatment */
  protected String m_MissingMode = "a";

  /** 0 = use specified blend, 1 = entropic blend setting */
  protected int m_BlendMethod = B_SPHERE;

  /** default sphere of influence blend setting */
  protected int m_GlobalBlend = 20;


  //====== OPTIONS ==========


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
    StringBuffer sb = new StringBuffer("Class for generating a K* classification model. ");
   return sb.toString();

  }

  /**
   * Return the name of this module.
   * @return The name of this module.
   */
  public String getModuleName() {
    return "WEKA_KStarModelGen";
  }

  /**
   * Return a String array containing the datatypes the inputs to this
   * module.
   * @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    return null;
  }

  /**
   * Return a String array containing the datatypes of the outputs of this
   * module.
   * @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String [] out = {"ncsa.d2k.infrastructure.modules.PredictionModelModule"};
    return out;
  }

  /**
   * Return a description of a specific input.
   * @param i The index of the input
   * @return The description of the input
   */
  public String getInputInfo(int i) {
    return "";
  }

  /**
   * Return the name of a specific input.
   * @param i The index of the input.
   * @return The name of the input
   */
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Instances";
      default: return "no such input";
    }
  }

  /**
   * Return the description of a specific output.
   * @param i The index of the output.
   * @return The description of the output.
   */
  public String getOutputInfo(int i) {
    if(i == 0) {
      return "ncsa.d2k.infrastructure.modules.PredictionModelModule: A WEKA_KStarModel module.";
    } else {
      return "no such output";
    }
  }

  /**
   * Return the name of a specific output.
   * @param i The index of the output.
   * @return The name of the output
   */
  public String getOutputName(int i) {
    switch(i) {
      case 0: return "WEKA_KStarModel";
      default: return "no such output";
    }
  }

  /**
   * Create the model and push it out.
   */
  public void doit() throws java.lang.Exception {
    try {
      WEKA_KStarModel m_model = new WEKA_KStarModel();
       m_model.setOptions(getOptions());
      pushOutput(m_model, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_KStarModelProducer.doit()");
      throw ex;
    }
  }


  //======= OPTIONS ==========

  /**
   * Gets the current settings of K*.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String [] getOptions() {
    // -B <num> -E -M <char>
    String [] options = new String [ 5 ];
    int itr = 0;
    options[itr++] = "-B";
    options[itr++] = "" + m_GlobalBlend;

    if (getEntropicAutoBlend()) {
      options[itr++] = "-E";
    }

    options[itr++] = "-M";
    options[itr++] = "" + m_MissingMode.trim();

    while (itr < options.length) {
      options[itr++] = "";
    }
    return options;
  }

  /**
   * Set the global blend parameter
   * @param b the value for global blending
   */
  public void setGlobalBlend(int b) {
     m_GlobalBlend = b;
      if ( m_GlobalBlend > 100 ) {
	m_GlobalBlend = 100;
      }
      if ( m_GlobalBlend < 0 ) {
	m_GlobalBlend = 0;
      }
  }

  /**
   * Get the value of the global blend parameter
   * @return the value of the global blend parameter
   */
  public int getGlobalBlend() {
    return m_GlobalBlend;
  }

  /**
   * Set whether entropic blending is to be used.
   * @param e true if entropic blending is to be used
   */
  public void setEntropicAutoBlend(boolean e) {
    if (e) {
      m_BlendMethod = B_ENTROPY;
    } else {
      m_BlendMethod = B_SPHERE;
    }
  }

  /**
   * Get whether entropic blending being used
   * @return true if entropic blending is used
   */
  public boolean getEntropicAutoBlend() {
    if (m_BlendMethod == B_ENTROPY) {
      return true;
    }

    return false;
  }

    /**
   * Gets the method to use for handling missing values. Will be one of
   * M_NORMAL, M_AVERAGE, M_MAXDIFF or M_DELETE.
   *
   * @return the method used for handling missing values.
   */
  public String getMissingMode() {
    return m_MissingMode;
  }

  /**
   * Sets the method to use for handling missing values. Values other than
   * M_NORMAL, M_AVERAGE, M_MAXDIFF and M_DELETE will be ignored.
   *
   * @param newMode the method to use for handling missing values.
   */
  public void setMissingMode(String newMode) {
      newMode = newMode.toLowerCase();
      if (newMode.equals("a") || newMode.equals("m") || newMode.equals("n") || newMode.equals("d")) {
        m_MissingMode = newMode;
      } else {
        m_MissingMode = "a";
      }
  }


}