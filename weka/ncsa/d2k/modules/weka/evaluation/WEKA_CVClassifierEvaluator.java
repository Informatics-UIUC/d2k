package ncsa.d2k.modules.weka.evaluation;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============

import ncsa.d2k.modules.weka.classifier.WEKA_ModelDelegator;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.core.modules.ModelEvaluatorModule;
import weka.core.*;
import weka.classifiers.*;

public class WEKA_CVClassifierEvaluator extends ModelEvaluatorModule {

  //==============
  // Data Members
  //==============

  private int m_folds = 10;

  //================
  // Constructor(s)
  //================

  public WEKA_CVClassifierEvaluator() {
  }

  //================
  // Public Methods
  //================

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "PredictionModelModule";
			default: return "No such output";
		}
	}

  public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module executes classifier generic performance tests on WEKA     classifier models.  </body></html>";
	}

  public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule","weka.core.Instances"};
		return types;
	}

  public String getInputInfo(int i) {
		switch (i) {
			case 0: return "A weka classifier model.";
			case 1: return "A weka.core.Instances instance.";
			default: return "No such input";
		}
	}

  protected void doit() throws java.lang.Exception {

    try {
      PredictionModelModule pmm = (PredictionModelModule)this.pullInput(0);

      if (!(pmm instanceof ncsa.d2k.modules.weka.classifier.WEKA_ModelDelegator)){
        System.out.println("Input is not a ncsa.d2k.modules.weka.classifier.WEKA_ModelDelegator instance.");
        return;
      }

      Classifier classify = ((WEKA_ModelDelegator)pmm).getDelegate();

      Instances instances = (Instances)this.pullInput(1);

      Evaluation eval = new Evaluation(instances);

      System.out.println("\n\n##########################");
      System.out.println("Beginning Cross Validation");
      System.out.println("##########################\n");

      // Make a copy of the data we can reorder
      Instances data = new Instances(instances);
      if (data.classAttribute().isNominal()) {
        data.stratify(m_folds);
      }
      // Do the folds
      for (int i = 0; i < m_folds; i++) {
        System.out.println("Evaluating model for fold: " + (i+1));
        Instances train = data.trainCV(m_folds, i);
        eval.setPriors(train);
        classify.buildClassifier(train);
        Instances test = data.testCV(m_folds, i);
        eval.evaluateModel(classify, test);
      }

      System.out.println("\n\n##################");
      System.out.println("Evaluation Results");
      System.out.println("##################\n");

      System.out.println(eval.toSummaryString(true));

      if (instances.classAttribute().isNominal()) {
        System.out.println("\n\n" + eval.toMatrixString("ConfusionMatrix"));
      }

      this.pushOutput(pmm, 0);

    } catch(Exception exc){
      exc.printStackTrace();
      System.out.println("Exception in WEKA_ClassifierEvaluator doit: " + exc.getMessage());
      throw exc;
    }

  }

  public int getCrossValidationFolds(){
    return m_folds;
  }

  public void setCrossValidationFolds(int folds){
    if (folds < 2){
      System.out.println("error: invalid fold value, must be > 1");
      return;
    }
    m_folds = folds;
  }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_CVClassifierEvaluator";
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
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
