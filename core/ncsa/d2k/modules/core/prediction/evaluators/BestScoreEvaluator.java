package ncsa.d2k.modules.core.prediction.evaluators;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

/**
 * Meant to be used in conjunction with NFoldTTables.  This module takes N
 * PredictionModelModules and N testing sets.  The testing set is passed to
 * the predict() method of each PredictionModelModule.  Only the model with
 * the highest number of correct predictions is passed as an output.
 */
public class BestScoreEvaluator extends ModelEvaluatorModule implements HasNames {

	public String getModuleInfo() {
		String s = "Meant to be used in conjunction with NFoldTTables.  ";
		s += "This module takes N PredictionModelModules and N testing sets.  ";
		s += "The testing set is passed to the predict() method of each ";
		s += "PredictionModelModule.  Only the model with the highest number of";
		s += "correct predictions is passed as an output.";
		return s;
	}

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.infrastructure.modules.PredictionModelModule",
			"ncsa.d2k.util.datatype.ExampleTable",
			"java.lang.Integer"};
		return in;
	}

	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.infrastructure.modules.PredictionModelModule"};
		return out;
	}

	public String getInputInfo(int i) {
		switch(i) {
			case 0:
				return "A PredictionModelModule.";
			case 1:
				return "A testing data set.";
			case 2:
				return "The number of folds.";
			default:
				return "No such input.";
		}
	}

	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "Model";
			case 1:
				return "TestSet";
			case 2:
				return "N";
			default:
				return "No such input.";
		}
	}

	public String getOutputInfo(int i) {
		return "The model with the highest number of correct predictions.";
	}

	public String getOutputName(int i) {
		return "BestModel";
	}

	public String getModuleName() {
		return "BestScoreEvaluator";
	}

	public void beginExecution() {
		bestScore = 0;
		numFolds = 0;
		numFires = 0;
		bestModel = null;
	}

	/** the number of folds, ie the number of models that will be created */
	private int numFolds = 0;
	/** the number of times this has fired so far */
	private int numFires = 0;
	/** the best score of the models so far */
	private double bestScore = 0;
	/** the most accurate prediction model so far */
	private PredictionModelModule bestModel;

	/**
	 * If we have not fired yet, we are ready when all 3 inputs are available.
	 * Otherwise we are ready when the first 2 inputs are available.
	 * @return true if this is ready to fire, false otherwise
	 */
	public boolean isReady() {
		if(numFires == 0)
			return super.isReady();
		else {
			// this is ready if there are inputs waiting on the first two pipes
			int[] flags = getFlags();
			return flags[0] > 0 && flags[1] > 0;
		}
	}

	public void doit() {
		// the first fire.  just call the predict method and keep this
		// model as the best
		if(numFires == 0) {
			PredictionModelModule pmm = (PredictionModelModule)pullInput(0);
			ExampleTable tbl = (ExampleTable)pullInput(1);
			Integer n = (Integer)pullInput(2);
			numFolds = n.intValue();

			PredictionTable pt = pmm.predict(tbl);
			int[] pred = pt.getPredictionSet();
			bestScore = score(pt);
			bestModel = pmm;
			printStats(numFires, bestScore, /*pmm.getTrainingSetSize()*/0, pt.getNumRows());
			numFires++;
		}
		// call the predict method and keep the model if it is the best one
		else {
			PredictionModelModule pmm = (PredictionModelModule)pullInput(0);
			ExampleTable tbl = (ExampleTable)pullInput(1);

			PredictionTable pt = pmm.predict(tbl);
			double thisscore = score(pt);
			printStats(numFires, thisscore, /*pmm.getTrainingSetSize()*/0, pt.getNumRows());
			if(thisscore > bestScore) {
				bestScore = thisscore;
				bestModel = pmm;
			}
			numFires++;
			if(numFires == numFolds) {
				pushOutput(bestModel, 0);
			}
		}
	}

	private static final String FOLD = "Fold: ";
	private static final String SCORE = "\tPercentage of correct predictions: ";
	private static final String TEST = "\tTesting set size: ";
	private static final String TRAIN = "\tTraining set size: ";

	private static final void printStats(int fld, double scre, int trainsize, int size) {
		System.out.println(FOLD+fld);
		System.out.println(TRAIN+trainsize);
		System.out.println(TEST+size);
		System.out.println(SCORE+scre*100);
	}

	/**
	 * Given a PredictionTable, compute the percentage of correct predictions.
	 * @param pt a prediction table
	 * @return the percentage of correct predictions
	 */
	private static final double score(PredictionTable pt) {
		double numCorrect = 0;
		int [] outs = pt.getOutputFeatures();
		int [] preds = pt.getPredictionSet();

		for(int row = 0; row < pt.getNumRows(); row++) {
			boolean ok = true;

			for(int col =0; col < outs.length; col++) {
				Column c = pt.getColumn(outs[col]);
				if(c instanceof NumericColumn) {
					double real = pt.getDouble(row, outs[col]);
					double pred = pt.getDouble(row, preds[col]);
					if(real != pred)
						ok = false;
				}
				else if(c instanceof TextualColumn) {
					String real = pt.getString(row, outs[col]);
					String pred = pt.getString(row, preds[col]);
					if(!real.equals(pred))
						ok = false;
				}
				else {
					Object real = pt.getObject(row, outs[col]);
					Object pred = pt.getObject(row, preds[col]);
					if(!real.equals(pred))
						ok = false;
				}
			}
			if(ok)
				numCorrect++;
		}
		double numRows = (double)pt.getNumRows();

		return numCorrect/numRows;
	}

	/**
	 * Return the model with the highest percentage of correct predictions.
	 * @return the model with the highest number of correct predictions.
	 */
	public ModelModule getModel() {
		return bestModel;
	}
}