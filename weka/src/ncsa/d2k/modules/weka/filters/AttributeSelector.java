package ncsa.d2k.modules.weka.filters;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============

import java.beans.PropertyVetoException;
import java.io.*;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.weka.filters.evaluation.ASEvaluation;
import ncsa.d2k.modules.weka.filters.evaluation.AttributeSelection;
import ncsa.d2k.modules.weka.filters.search.ASSearch;


public class AttributeSelector extends ComputeModule {

  //==============
  // Data Members
  //==============

//  private AttributeSelection m_attSel = null;
  private int m_numFolds = 10;
  private int m_seed = 1;

  //================
  // Constructor(s)
  //================
  public AttributeSelector() {

  }

  //================
  // Public Methods
  //================


  public String getModuleInfo() {
	String s = "<p> Overview: ";
	s += " This module will implement attribute selection using the input data set, ";
	s += " search method, and evaluation metric (attribute or subset based).  </p>";
	s += "<p> Detailed Description: ";
	s += "For each the selected input attributes of the data set a score is computed using the";
    s +=" evaluation metric provided. The input attributes space ";
    s +=" is then scanned using the seach strategy implemented by the search module for ";
    s += "the input attribute set that best satisfies the search parameters. NOTE: Attribute evaluators";
    s += " can only be used with the AttributeSearchRanker, subset evaluators can be used with any search module.  </p>";
	s += "<p> Data Type Restrictions: The data must be numeric, no nominal attributes";
	s += " can be processed. If the dataset contains nominal attributes, they should be ";
    s += " transformed into numeric attributes using ReplaceNominalWithInts transformation. ";
    s += "If the transformation is not done by the user, the evaluating modules will do it when ";
    s += "necessary without consulting the user.";
	s += " </p>";
	s += "<p> Data Handling: ";
	s += "The module might modify the input data. Check the evaluator documentation. ";
   s += "</p><p> Acknowledgement: ";
   s += "This module is a wrapper for functionality in the Weka project, available under ";
   s += "the GNU General Public License. See http://www.cs.waikato.ac.nz/ml/weka/.</p>";

	return s;

			}

  public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.Table","ncsa.d2k.modules.projects.anca.attributeSelection.search.ASSearch",
				"ncsa.d2k.modules.projects.anca.attributeSelection.evaluation.ASEvaluation","java.lang.String"};
		return types;
	}

  public String[] getOutputTypes() {
		String[] types = {"[I"};
		return types;
	}

  public String getInputInfo(int parm1) {
		switch (parm1) {
			case 0: return "Table containing the input training set";
			case 1: return  "Search module - searches over the feature space";
			case 2: return  "Evaluation module - evaluates the features";
			case 3: return  "Results file name";
			default: return "No such input";
		}
	}

  public boolean isReady() {
    if (!isInputPipeConnected(3)) {
            return (getInputPipeSize(0)>0 &&
                          getInputPipeSize(1)>0 &&
                          getInputPipeSize(2)>0 );
    }
     return super.isReady();
  }


  protected void doit() throws java.lang.Exception {

      ExampleTable instances = (ExampleTable)this.pullInput(0);
      ASSearch search = (ASSearch)this.pullInput(1);
      ASEvaluation eval = (ASEvaluation)this.pullInput(2);
      String resultsFile = null;
      if (isInputPipeConnected(3)) {
        resultsFile = (String)pullInput(3);
      }
      int [] selectedAttributes;
      double [][] rankedAttributes;

      AttributeSelection m_attSel = new AttributeSelection();
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

      selectedAttributes = m_attSel.selectedAttributes();
     // rankedAttributes = m_attSel.rankedAttributes();
      if (resultsFile !=null) {
      	FileWriter fw = new FileWriter(resultsFile,true);
      	fw.write(instances.getColumnLabel((int)selectedAttributes[0]));
      	String score = " " + selectedAttributes[0] + "\n";
      	fw.write( score);
      	fw.flush();
      	fw.close();
      }
      //TODO - what is the real output of this module ???
      this.pushOutput(selectedAttributes,0);


  }

  /**
   * set the seed for use in cross validation
   * @param s the seed
   */
  public void setSeed (int s)  {
    m_seed = s;
  }

  public int getSeed() {
    return m_seed;
  }

  /**
   * set the number of folds for cross validation
   * @param folds the number of folds
   */
  public void setFolds (int folds)  throws PropertyVetoException {
	 if (folds < 0 ) throw new PropertyVetoException("number of folds must be greater than 0 ", null);
    m_numFolds = folds;

  }

  public int getFolds() {
    return m_numFolds;
  }


  public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [2];
		pds[0] = new PropertyDescription ("folds", "Number of folds", "How many folds will be used in the validation process");
		pds[1] = new PropertyDescription ("seed", "Random number seed", "Seed for use in cross validation");

		return pds;
	  }



	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Attribute Selector";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			case 1:
				return "Search";
			case 2:
				return "Evaluator";
			case 3:
				return "Results File Name";
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
                    case 0: return "Selected attributes";
			default: return "NO SUCH OUTPUT!";
		}
	}

	public String getOutputInfo(int parm1) {
			switch (parm1) {
					  case 0: return "Array of integers containing the selected attributes indexes";
				default: return "No such output";
			}
		}



}
