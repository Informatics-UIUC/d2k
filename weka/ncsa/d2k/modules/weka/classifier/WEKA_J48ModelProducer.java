package ncsa.d2k.modules.weka.classifier;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

import weka.classifiers.j48.J48;
import weka.core.Instances;

/**
 * ModelGen wrapper around WEKA implementation of C4.5
 * @author D. Searsmith
 */
public class WEKA_J48ModelProducer extends ModelProducerModule implements HasNames {

  //==============
  // Data Members
  //==============


  //====== OPTIONS ==========

  /** Unpruned tree? */
  private boolean m_unpruned = false;

  /** Confidence level */
  private float m_CF = 0.25f;

  /** Minimum number of instances */
  private int m_minNumObj = 2;

  /** Determines whether probabilities are smoothed using
      Laplace correction when predictions are generated */
  private boolean m_useLaplace = false;

  /** Use reduced error pruning? */
  private boolean m_reducedErrorPruning = false;

  /** Number of folds for reduced error pruning. */
  private int m_numFolds = 3;

  /** Binary splits on nominal attributes? */
  private boolean m_binarySplits = false;

  /** Subtree raising to be performed? */
  private boolean m_subtreeRaising = true;

  /** Cleanup after the tree has been built. */
  boolean m_noCleanup = false;

  //================
  // Constructor(s)
  //================

  public WEKA_J48ModelProducer(){
  }

  //==================
  // Public Functions
  //==================

  /**
   * Return a description of the function of this module.
   * @return A description of this module.
   */
  public String getModuleInfo() {
    StringBuffer sb = new StringBuffer("Class for generating an unpruned or a pruned C4.5 decision tree. ");
    sb = sb.append("For more information, see \n\n");

    sb = sb.append("Ross Quinlan (1993). C4.5: Programs for Machine Learning,");
    sb = sb.append("Morgan Kaufmann Publishers, San Mateo, CA. \n\n\n");

    sb = sb.append("Valid options are: \n\n");

    sb = sb.append("unpruned \n");
    sb = sb.append("Use unpruned tree. \n\n");

    sb = sb.append("confidenceFactor \n");
    sb = sb.append("Set confidence threshold for pruning. (Default: 0.25) \n\n");

    sb = sb.append("minNumObj \n");
    sb = sb.append("Set minimum number of instances per leaf. (Default: 2) \n\n");

    sb = sb.append("reducedErrorPruning \n");
    sb = sb.append("Use reduced error pruning. No subtree raising is performed. \n\n");

    sb = sb.append("numFolds \n");
    sb = sb.append("Set number of folds for reduced error pruning. One fold is ");
    sb = sb.append("used as the pruning set. (Default: 3) \n\n");

    sb = sb.append("binarySplits \n");
    sb = sb.append("Use binary splits for nominal attributes. \n\n");

    sb = sb.append("subtreeRaising \n");
    sb = sb.append("Don't perform subtree raising. \n\n");

    sb = sb.append(" saveInstances -- FIXED TO FALSE \n");
    sb = sb.append("Do not clean up after the tree has been built. \n");

    sb = sb.append("useLaplace \n");
    sb = sb.append("If set, Laplace smoothing is used for predicted probabilites. \n\n\n");

    sb = sb.append("@author Eibe Frank (eibe@cs.waikato.ac.nz)");
    return sb.toString();

  }

  /**
   * Return the name of this module.
   * @return The name of this module.
   */
  public String getModuleName() {
    return "WEKA_J48ModelGen";
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
    return "";
  }

  /**
   * Return the description of a specific output.
   * @param i The index of the output.
   * @return The description of the output.
   */
  public String getOutputInfo(int i) {
    if(i == 0) {
      return "ncsa.d2k.infrastructure.modules.PredictionModelModule: A WEKA_J48Model module.";
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
      case 0: return "WEKA_J48Model";
      default: return "no such output";
    }
  }

  /**
   * Create the model and push it out.
   */
  public void doit() throws java.lang.Exception {
    try {
      WEKA_J48Model j48mod = new WEKA_J48Model();
      j48mod.setOptions(getOptions());
      pushOutput(j48mod, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_J48ModelProducer");
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

    String [] options = new String [10];
    int current = 0;

    if (m_noCleanup) {
      options[current++] = "-L";
    }
    if (m_unpruned) {
      options[current++] = "-U";
    } else {
      if (!m_subtreeRaising) {
	options[current++] = "-S";
      }
      if (m_reducedErrorPruning) {
	options[current++] = "-R";
	options[current++] = "-N"; options[current++] = "" + m_numFolds;
      } else {
	options[current++] = "-C"; options[current++] = "" + m_CF;
      }
    }
    if (m_binarySplits) {
      options[current++] = "-B";
    }
    options[current++] = "-M"; options[current++] = "" + m_minNumObj;
    if (m_useLaplace) {
      options[current++] = "-A";
    }

    while (current < options.length) {
      options[current++] = "";
    }
    return options;
  }

  /**
   * Get the value of useLaplace.
   *
   * @return Value of useLaplace.
   */
  public boolean getUseLaplace() {

    return m_useLaplace;
  }

  /**
   * Set the value of useLaplace.
   *
   * @param newuseLaplace Value to assign to useLaplace.
   */
  public void setUseLaplace(boolean newuseLaplace) {

    m_useLaplace = newuseLaplace;
  }

  /**
   * Get the value of unpruned.
   *
   * @return Value of unpruned.
   */
  public boolean getUnpruned() {

    return m_unpruned;
  }

  /**
   * Set the value of unpruned. Turns reduced-error pruning
   * off if set.
   * @param v  Value to assign to unpruned.
   */
  public void setUnpruned(boolean v) {

    if (v) {
      m_reducedErrorPruning = false;
    }
    m_unpruned = v;
  }

  /**
   * Get the value of CF.
   *
   * @return Value of CF.
   */
  public float getConfidenceFactor() {

    return m_CF;
  }

  /**
   * Set the value of CF.
   *
   * @param v  Value to assign to CF.
   */
  public void setConfidenceFactor(float v) {

    m_CF = v;
  }

  /**
   * Get the value of minNumObj.
   *
   * @return Value of minNumObj.
   */
  public int getMinNumObj() {

    return m_minNumObj;
  }

  /**
   * Set the value of minNumObj.
   *
   * @param v  Value to assign to minNumObj.
   */
  public void setMinNumObj(int v) {

    m_minNumObj = v;
  }

  /**
   * Get the value of reducedErrorPruning.
   *
   * @return Value of reducedErrorPruning.
   */
  public boolean getReducedErrorPruning() {

    return m_reducedErrorPruning;
  }

  /**
   * Set the value of reducedErrorPruning. Turns
   * unpruned trees off if set.
   *
   * @param v  Value to assign to reducedErrorPruning.
   */
  public void setReducedErrorPruning(boolean v) {

    if (v) {
      m_unpruned = false;
    }
    m_reducedErrorPruning = v;
  }

  /**
   * Get the value of numFolds.
   *
   * @return Value of numFolds.
   */
  public int getNumFolds() {

    return m_numFolds;
  }

  /**
   * Set the value of numFolds.
   *
   * @param v  Value to assign to numFolds.
   */
  public void setNumFolds(int v) {

    m_numFolds = v;
  }

  /**
   * Get the value of binarySplits.
   *
   * @return Value of binarySplits.
   */
  public boolean getBinarySplits() {

    return m_binarySplits;
  }

  /**
   * Set the value of binarySplits.
   *
   * @param v  Value to assign to binarySplits.
   */
  public void setBinarySplits(boolean v) {

    m_binarySplits = v;
  }

  /**
   * Get the value of subtreeRaising.
   *
   * @return Value of subtreeRaising.
   */
  public boolean getSubtreeRaising() {

    return m_subtreeRaising;
  }

  /**
   * Set the value of subtreeRaising.
   *
   * @param v  Value to assign to subtreeRaising.
   */
  public void setSubtreeRaising(boolean v) {

    m_subtreeRaising = v;
  }

   /**
   * Check whether instance data is to be saved.
   *
   * @return true if instance data is saved
   */
  public boolean getSaveInstanceData() {

    return m_noCleanup;
  }

  /**
   * Set whether instance data is to be saved.
   * @param v true if instance data is to be saved
   */
  public void setSaveInstanceData(boolean v) {

    m_noCleanup = v;
  }


}
