package ncsa.d2k.modules.weka.attributeSelection;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import weka.core.Instances;
import weka.attributeSelection.*;


public class WEKA_AttributeSelector extends ComputeModule {

  //==============
  // Data Members
  //==============

  private AttributeSelection m_attSel = null;
  private int m_numFolds = 0;
  private int m_seed = 1;

  //================
  // Constructor(s)
  //================
  public WEKA_AttributeSelector() {
  }

  //================
  // Public Methods
  //================

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			default: return "No such output";
		}
	}

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module will implement attribute selection using the input training     set, search method, and evaluation metric.  </body></html>";
	}

  public String[] getInputTypes() {
		String[] types = {"weka.core.Instances","weka.attributeSelection.ASSearch","weka.attributeSelection.ASEvaluation"};
		return types;
	}

  public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

  public String getInputInfo(int parm1) {
		switch (parm1) {
			case 0: return "WEKA instance set";
			case 1: return "WEKA search module";
			case 2: return "WEKA evaluation module";
			default: return "No such input";
		}
	}

  protected void doit() throws java.lang.Exception {
    try {
      m_attSel = new AttributeSelection();
      Instances instances = (Instances)this.pullInput(0);
      ASSearch search = (ASSearch)this.pullInput(1);
      ASEvaluation eval = (ASEvaluation)this.pullInput(2);

      if (m_numFolds != 0) {
        m_attSel.setFolds(m_numFolds);
        m_attSel.setXval(true);
        m_attSel.setSeed(m_seed);
      } else {
        m_attSel.setXval(false);
      }

      //set the search
      m_attSel.setSearch(search);

      // set the attribute evaluator
      m_attSel.setEvaluator(eval);

      // do the attribute selection
      m_attSel.SelectAttributes(instances);

      // return the results string
      System.out.println(m_attSel.toResultsString());

    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      System.out.println("ERROR: WEKA_AttributeSelector");
      throw e;
    }

  }

  /**
   * set the seed for use in cross validation
   * @param s the seed
   */
  public void setSeed (int s) {
    m_seed = s;
  }

  public int getSeed() {
    return m_seed;
  }

  /**
   * set the number of folds for cross validation
   * @param folds the number of folds
   */
  public void setFolds (int folds) {
    m_numFolds = folds;
  }

  public int getFolds() {
    return m_numFolds;
  }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_AttributeSelector";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			case 1:
				return "input1";
			case 2:
				return "input2";
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
			default: return "NO SUCH OUTPUT!";
		}
	}
}
