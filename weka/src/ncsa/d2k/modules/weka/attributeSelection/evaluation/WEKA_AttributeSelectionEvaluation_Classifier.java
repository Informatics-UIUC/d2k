package ncsa.d2k.modules.weka.attributeSelection.evaluation;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.weka.classifier.WEKA_ModelDelegator;
import weka.core.Utils;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ClassifierSubsetEval;
import weka.classifiers.Classifier;
import weka.core.OptionHandler;

public class WEKA_AttributeSelectionEvaluation_Classifier extends ComputeModule {

  //==============
  // Data Members
  //==============

  //** holds the options string for the classifier to be used for error estimates**/
  private String m_ClassifierOptions = "";

  /** holds the classifier to use for error estimates */
  private Classifier m_Classifier = null;

  /** the file that containts hold out/test instances */
  private String m_holdOutFile = "";

  /** evaluate on training data rather than seperate hold out/test set */
  private boolean m_useTraining = true;

  private ClassifierSubsetEval m_eval = null;

  //================
  // Constructor(s)
  //================

  public WEKA_AttributeSelectionEvaluation_Classifier() {
  }

  //================
  // Public Methods
  //================

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "WEKA Evaluation Module";
			default: return "No such output";
		}
	}

  protected void doit() throws java.lang.Exception {
    try {
      m_Classifier = ((WEKA_ModelDelegator)pullInput(0)).getDelegate();
      m_eval = new ClassifierSubsetEval();
      m_eval.setOptions(getOptions());
      pushOutput(m_eval, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_AttributeSelectionEvaluation_Classifier");
      throw ex;
    }
  }
  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    A module for supplying a classification attribute subset evaluator to the     attribute selector.  </body></html>";
	}
  public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}
  public String[] getOutputTypes() {
		String[] types = {"weka.attributeSelection.ASEvaluation"};
		return types;
	}
  public String getInputInfo(int parm1) {
		switch (parm1) {
			case 0: return "ncsa.d2k.infrastructure.modules.PredictionModelModule";
			default: return "No such input";
		}
	}

  //Properties

  /**
   * Gets the current settings of ClassifierSubsetEval
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {

    String[] classifierOptions = new String[0];

    if ((m_Classifier != null) &&
	(m_Classifier instanceof OptionHandler)) {
      classifierOptions = ((OptionHandler)m_Classifier).getOptions();
    }

    String[] options = new String[6 + classifierOptions.length];
    int current = 0;

    if (getClassifier() != null) {
      options[current++] = "-B";
      options[current++] = getClassifier().getClass().getName();;
    }

    if (getUseTraining()) {
      options[current++] = "-T";
    }
    options[current++] = "-H";
    options[current++] = getHoldOutFile();
    options[current++] = "--";
    System.arraycopy(classifierOptions, 0, options, current, classifierOptions.length);
    current += classifierOptions.length;
        while (current < options.length) {
      options[current++] = "";
    }

    return  options;
  }

  /**
   * Gets the file that holds hold out/test instances.
   * @return File that contains hold out instances
   */
  public String getHoldOutFile() {
    return m_holdOutFile;
  }


  /**
   * Set the file that contains hold out/test instances
   * @param h the hold out file
   */
  public void setHoldOutFile(String h) {
    m_holdOutFile = h;
  }

  /**
   * Get if training data is to be used instead of hold out/test data
   * @return true if training data is to be used instead of hold out data
   */
  public boolean getUseTraining() {
    return m_useTraining;
  }

  /**
   * Set if training data is to be used instead of hold out/test data
   * @return true if training data is to be used instead of hold out data
   */
  public void setUseTraining(boolean t) {
    m_useTraining = t;
  }

  /**
   * Set the classifier to use for accuracy estimation
   *
   * @param newClassifier the Classifier to use.
   */
  public void setClassifier (Classifier newClassifier) {
    m_Classifier = newClassifier;
  }


  /**
   * Get the classifier used as the base learner.
   *
   * @return the classifier used as the classifier
   */
  public Classifier getClassifier () {
    return  m_Classifier;
  }



	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_AttributeSelectionEvaluation_Classifier";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "PredictionModelModule";
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

      PropertyDescription[] pds = new PropertyDescription[2];

      pds[0] = new PropertyDescription(
         "holdOutFile",
         "Hold Out File",
         "The file that contains hold out/test instances.");

      pds[1] = new PropertyDescription(
         "useTraining",
         "Use Training Data",
         "Evaluate on training data rather than separate hold out/test set?");

      return pds;

   }

}
