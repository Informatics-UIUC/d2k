package ncsa.d2k.modules.weka.association;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import weka.associations.Apriori;
import weka.core.*;

public class WEKA_Apriori extends ComputeModule {
 /**
   * This will return a string describing the classifier.
   * @return The string.
   */
  public String globalInfo() {
    return "Using Apriori algorithm, build rule associations for a dataset.";
  }

  //==============
  // Data Members
  //==============

  /**
   * Keep a reference to the model so that it can be
   * used in the getModel() method
   */
  Apriori m_delegate = null;

  //====== OPTIONS ==========

  /** The upper bound on the support */
  protected double m_upperBoundMinSupport = 1.0;

  /** The lower bound for the minimum support. */
  protected double m_lowerBoundMinSupport = 0.1;

  /** Metric types. */
  protected static final int CONFIDENCE = 0;
  protected static final int LIFT = 1;
  protected static final int LEVERAGE = 2;
  protected static final int CONVICTION = 3;
  public static final Tag [] TAGS_SELECTION = {
    new Tag(CONFIDENCE, "Confidence"),
    new Tag(LIFT, "Lift"),
    new Tag(LEVERAGE, "Leverage"),
    new Tag(CONVICTION, "Conviction")
      };

  /** The selected metric type. */
  protected int m_metricType = CONFIDENCE;

  /** The minimum metric score. */
  protected double m_minMetric = 0.90;

  /** The maximum number of rules that are output. */
  protected int m_numRules = 10;

  /** Delta by which m_minSupport is decreased in each iteration. */
  protected double m_delta = 0.05;

  /** Significance level for optional significance test. */
  protected double m_significanceLevel = -1;

  /** Output itemsets found? */
  protected boolean m_outputItemSets = false;

  protected boolean m_removeMissingCols = false;

  protected boolean m_verbose = false;

  //================
  // Constructor(s)
  //================

  public WEKA_Apriori(){
    m_delegate = new Apriori();
    m_delegate.resetOptions();
  }

  //==================
  // Public Functions
  //==================

  /**
   * Resets the options to the default values.
   */
  public void resetOptions() {

    m_removeMissingCols = false;
    m_verbose = false;
    m_delta = 0.05;
    m_minMetric = 0.90;
    m_numRules = 10;
    m_lowerBoundMinSupport = 0.1;
    m_upperBoundMinSupport = 1.0;
    m_significanceLevel = -1;
    m_outputItemSets = false;
  }


  /**
   * Return a description of the function of this module.
   * @return A description of this module.
   */
  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    WEKA Apriori implmentation.  </body></html>";
	}

  /**
   * Return the name of this module.
   * @return The name of this module.
   */
  public String getModuleName() {
    return "WEKA_Apriori";
  }

  /**
   * Return a String array containing the datatypes the inputs to this
   * module.
   * @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
		String[] types = {"weka.core.Instances"};
		return types;
	}

  /**
   * Return a String array containing the datatypes of the outputs of this
   * module.
   * @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

  /**
   * Return a description of a specific input.
   * @param i The index of the input
   * @return The description of the input
   */
  public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The Instances object";
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
      case 0: return "WEKA Instance Set";
      default: return "no such input";
    }
  }

  /**
   * Return the description of a specific output.
   * @param i The index of the output.
   * @return The description of the output.
   */
  public String getOutputInfo(int i) {
		switch (i) {
			default: return "No such output";
		}
	}

  /**
   * Return the name of a specific output.
   * @param i The index of the output.
   * @return The name of the output
   */
  public String getOutputName(int i) {
    return "";
  }


  /**
   * Create the model and push it out.
   */
  public void doit() throws java.lang.Exception {
    try {
      Instances instances = (Instances)pullInput(0);
      //System.out.println("Here 1");
      m_delegate.setOptions(getOptions());
      //System.out.println("Here 2");
      m_delegate.buildAssociations(instances);
      //System.out.println("Here 3");
      System.out.println(m_delegate);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_Apriori.doit()");
      throw ex;
    }
  }


  //======= OPTIONS ==========

  /**
   * Gets the current settings of the Apriori object.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {

    String [] options = new String [16];
    int current = 0;

    if (m_outputItemSets) {
      options[current++] = "-I";
    }

    if (getRemoveAllMissingCols()) {
      options[current++] = "-R";
    }

    options[current++] = "-N"; options[current++] = "" + m_numRules;
    options[current++] = "-T"; options[current++] = "" + m_metricType;
    options[current++] = "-C"; options[current++] = "" + m_minMetric;
    options[current++] = "-D"; options[current++] = "" + m_delta;
    options[current++] = "-U"; options[current++] = ""+m_upperBoundMinSupport;
    options[current++] = "-M"; options[current++] = ""+m_lowerBoundMinSupport;
    options[current++] = "-S"; options[current++] = "" + m_significanceLevel;

    while (current < options.length) {
      options[current++] = "";
    }
    return options;
  }

  /**
   * Get the value of significanceLevel.
   *
   * @return Value of significanceLevel.
   */
  public double getSignificanceLevel() {

    return m_significanceLevel;
  }

  /**
   * Set the value of significanceLevel.
   *
   * @param v  Value to assign to significanceLevel.
   */
  public void setSignificanceLevel(double v) {

    m_significanceLevel = v;
  }

  /**
   * Get the value of upperBoundMinSupport.
   *
   * @return Value of upperBoundMinSupport.
   */
  public double getUpperBoundMinSupport() {

    return m_upperBoundMinSupport;
  }

  /**
   * Set the value of upperBoundMinSupport.
   *
   * @param v  Value to assign to upperBoundMinSupport.
   */
  public void setUpperBoundMinSupport(double v) {

    m_upperBoundMinSupport = v;
  }

  /**
   * Get the value of lowerBoundMinSupport.
   *
   * @return Value of lowerBoundMinSupport.
   */
  public double getLowerBoundMinSupport() {

    return m_lowerBoundMinSupport;
  }

  /**
   * Set the value of lowerBoundMinSupport.
   *
   * @param v  Value to assign to lowerBoundMinSupport.
   */
  public void setLowerBoundMinSupport(double v) {

    m_lowerBoundMinSupport = v;
  }


  /**
   * Get the value of numRules.
   *
   * @return Value of numRules.
   */
  public int getNumRules() {

    return m_numRules;
  }

  /**
   * Set the value of numRules.
   *
   * @param v  Value to assign to numRules.
   */
  public void setNumRules(int v) {

    m_numRules = v;
  }

  /**
   * Get the metric type
   *
   * @return the type of metric to use for ranking rules
   */
  public int getMetricType() {
    return m_metricType;
  }

  /**
   * Get the metric type
   *
   * @return the type of metric to use for ranking rules
   */
/*
  public SelectedTag getMetricType() {
    return new SelectedTag(m_metricType, TAGS_SELECTION);
  }
*/

  /**
   * Set the metric type for ranking rules
   *
   * @param d the type of metric
   */
  public void setMetricType (int d) {

    m_metricType = d;

    if (m_significanceLevel != -1 && m_metricType != CONFIDENCE) {
      m_metricType = CONFIDENCE;
    }

    if (m_metricType == CONFIDENCE) {
      setMinMetric(0.9);
    }

    if (m_metricType == LIFT || m_metricType == CONVICTION) {
      setMinMetric(1.1);
    }

    if (m_metricType == LEVERAGE) {
      setMinMetric(0.1);
    }
  }

  /**
   * Set the metric type for ranking rules
   *
   * @param d the type of metric
   */
/*
  public void setMetricType (SelectedTag d) {

    if (d.getTags() == TAGS_SELECTION) {
      m_metricType = d.getSelectedTag().getID();
    }

    if (m_significanceLevel != -1 && m_metricType != CONFIDENCE) {
      m_metricType = CONFIDENCE;
    }

    if (m_metricType == CONFIDENCE) {
      setMinMetric(0.9);
    }

    if (m_metricType == LIFT || m_metricType == CONVICTION) {
      setMinMetric(1.1);
    }

    if (m_metricType == LEVERAGE) {
      setMinMetric(0.1);
    }
  }
*/

  /**
   * Remove columns containing all missing values.
   * @param r true if cols are to be removed.
   */
  public void setRemoveAllMissingCols(boolean r) {
    m_removeMissingCols = r;
  }

  /**
   * Returns whether columns containing all missing values are to be removed
   * @return true if columns are to be removed.
   */
  public boolean getRemoveAllMissingCols() {
    return m_removeMissingCols;
  }

  public void setOutputItemSets(boolean r) {
    m_outputItemSets = r;
  }

  public boolean getOutputItemSets() {
    return m_outputItemSets;
  }

  /**
   * Get the value of minConfidence.
   *
   * @return Value of minConfidence.
   */
  public double getMinMetric() {

    return m_minMetric;
  }

  /**
   * Set the value of minConfidence.
   *
   * @param v  Value to assign to minConfidence.
   */
  public void setMinMetric(double v) {

    m_minMetric = v;
  }

  /**
   * Get the value of delta.
   *
   * @return Value of delta.
   */
  public double getDelta() {

    return m_delta;
  }

  /**
   * Set the value of delta.
   *
   * @param v  Value to assign to delta.
   */
  public void setDelta(double v) {

    m_delta = v;
  }

  public void setVerbose(boolean r) {
    m_verbose = r;
  }

  public boolean getVerbose() {
    return m_verbose;
  }

  public PropertyDescription[] getPropertiesDescriptions() {

     PropertyDescription[] pds = new PropertyDescription[10];

     pds[0] = new PropertyDescription(
        "lowerBoundMinSupport",
        "Minimum Support Lower Bound",
        "The lower bound for the minimum support.");
     pds[1] = new PropertyDescription(
        "upperBoundMinSupport",
        "Minimum Support Upper Bound",
        "The upper bound for the minimum support.");
     pds[2] = new PropertyDescription(
        "metricType",
        "Metric Type",
        "The metric type.");
     pds[3] = new PropertyDescription(
        "minMetric",
        "Minimum Metric Score",
        "The minimum metric score.");
     pds[4] = new PropertyDescription(
        "numRules",
        "Number of Rules",
        "The maximum number of rules that are output.");
     pds[5] = new PropertyDescription(
        "delta",
        "Delta",
        "Delta by which minimum support is decreased in each iteration.");
     pds[6] = new PropertyDescription(
        "outputItemSets",
        "Output Item Sets",
        "Output itemsets found?");
     pds[7] = new PropertyDescription(
        "removeAllMissingCols",
        "Remove Missing Columns",
        "Remove missing columns?");
     pds[8] = new PropertyDescription(
        "verbose",
        "Verbose",
        "Verbose?");
     pds[9] = new PropertyDescription(
        "significanceLevel",
        "Significance Level",
        "The significance level.");

     return pds;

  }

}
