package ncsa.d2k.modules.weka.classifier;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

import weka.classifiers.j48.J48;
import weka.core.Instances;

/**
 * ModelGen wrapper around WEKA implementation of C4.5
 * @author D. Searsmith
 */
public class WEKA_NaiveBayesModelProducer extends ModelProducerModule implements HasNames {

  //==============
  // Data Members
  //==============

 //====== OPTIONS ==========

    /**
   * Whether to use kernel density estimator rather than normal distribution
   * for numeric attributes
   */
  private boolean m_UseKernelEstimator;

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

    String info =  "Class for a Naive Bayes classifier using estimator classes. Numeric"
                    + " estimator precision values are chosen based on analysis of the "
                    + " training data. For this reason, the classifier is not an "
                    + " UpdateableClassifier (which in typical usage are initialized with zero"
                    + " training instances) -- if you need the UpdateableClassifier functionality,"
                    + " Create an empty class such as the following: <p>"
                    + "  <pre><code>"
                    + " public class NaiveBayesUpdateable extends NaiveBayes"
                    + "      implements UpdateableClassifier {"
                    + ""
                    + "  }"
                    + " </code></pre>"
                    + " This classifier will use a default precision of 0.1 for numeric attributes"
                    + " when buildClassifier is called with zero training instances."
                    + " <p>"
                    + " For more information on Naive Bayes classifiers, see<p>"
                    + " "
                    + "  George H. John and Pat Langley (1995). <i>Estimating"
                    + " Continuous Distributions in Bayesian Classifiers</i>. Proceedings"
                    + " of the Eleventh Conference on Uncertainty in Artificial"
                    + " Intelligence. pp. 338-345. Morgan Kaufmann, San Mateo.<p>"
                    + ""
                    + " Valid options are:<p>"
                    + ""
                    + " -K <br>"
                    + " Use kernel estimation for modelling numeric attributes rather than"
                    + " a single normal distribution.<p>";


    return info;

  }

  /**
   * Return the name of this module.
   * @return The name of this module.
   */
  public String getModuleName() {
    return "WEKA_NaiveBayesianModelGen";
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
      return "ncsa.d2k.infrastructure.modules.PredictionModelModule: A WEKA_NaiveBayesModel module.";
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
      case 0: return "WEKA_NaiveBayesModel";
      default: return "no such output";
    }
  }

  /**
   * Create the model and push it out.
   */
  public void doit() throws java.lang.Exception {
    try {
      WEKA_NaiveBayesModel bmod = new WEKA_NaiveBayesModel();
      bmod.setOptions(getOptions());
      pushOutput(bmod, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_NaiveBayesModelProducer.doit()");
      throw ex;
    }
  }


  //======= OPTIONS ==========

  /**
   * Gets the current settings of the classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {

    String [] options = new String [1];
    int current = 0;

    if (m_UseKernelEstimator) {
      options[current++] = "-K";
    }

    while (current < options.length) {
      options[current++] = "";
    }
    return options;
  }


  /**
   * Gets if kernel estimator is being used.
   *
   * @return Value of m_UseKernelEstimatory.
   */
  public boolean getUseKernelEstimator() {

    return m_UseKernelEstimator;
  }

  /**
   * Sets if kernel estimator is to be used.
   *
   * @param v  Value to assign to m_UseKernelEstimatory.
   */
  public void setUseKernelEstimator(boolean v) {

    m_UseKernelEstimator = v;
  }


}
