package ncsa.d2k.modules.core.prediction.evaluators;


import ncsa.d2k.core.modules.ModelEvaluatorModule;
import ncsa.d2k.core.modules.ModelModule;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.*;

import java.util.*;

/**
 * Meant to be used in conjunction with NFoldTTables.  This module takes N
 * PredictionModelModules and N testing sets.  The testing set is passed to
 * the predict() method of each PredictionModelModule.  Only the model with
 * the highest number of correct predictions is passed as an output.
 */
public class ClassificationErrorEvaluator extends ModelEvaluatorModule  {

	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Meant to be used in conjunction with NFoldTTables. This module takes N     PredictionModelModules and N testing sets. The testing set is passed to     the predict() method of each PredictionModelModule. Only the model with     the highest number ofcorrect predictions is passed as an output.  </body></html>";
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.PredictionTable","java.lang.Integer"};
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = {"java.util.ArrayList","java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "A PredictionModelModule to evaluate";
			case 1: return "A PredictionTable to test the model with.";
			case 2: return "The number of folds.";
			default: return "No such input";
		}
	}

	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "Model";
			case 1:
				return "TestTable";
			case 2:
				return "N";
			default: return "NO SUCH INPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The statistics about each fold.";
			case 1: return "The number of folds.";
			default: return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "Stats";
			case 1:
				return "N";
			default: return "NO SUCH OUTPUT!";
		}
	}

	public String getModuleName() {
		return "ClassificationErrorEvaluator";
	}

	public void beginExecution() {
		numFolds = 0;
		numFires = 0;
	}

	/** the number of folds, ie the number of models that will be created */
	private int numFolds = 0;
	/** the number of times this has fired so far */
	private int numFires = 0;

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
			return getInputPipeSize(0) > 0;
		}
	}

	ArrayList tables;

	public void doit() {
		// the first fire.  just call the predict method and keep this
		// model as the best
		if(numFires == 0) {
			PredictionTable pt = (PredictionTable)pullInput(0);
			Integer n = (Integer)pullInput(1);
			numFolds = n.intValue();

			/*int[] pred = pt.getPredictionSet();
			Pair p = score(pt);
			double score = p.percent;
			stats.addFold(pmm.getTrainingSetSize(), pt.getNumRows(), p.correct, score, pt);
			*/
			tables = new ArrayList();
			tables.add(pt);
			numFires++;
		}
		// call the predict method and keep the model if it is the best one
		else {
			PredictionTable pt = (PredictionTable)pullInput(0);
			tables.add(pt);
			numFires++;

			/*Pair thisscore = score(pt);
			stats.addFold(pmm.getTrainingSetSize(), pt.getNumRows(), thisscore.correct, thisscore.percent, pt);
			numFires++;
			if(numFires == numFolds) {
				pushOutput(stats, 0);
				pushOutput(new Integer(numFolds), 1);
			}*/
			if(numFires == numFolds) {
				pushOutput(tables, 0);
				pushOutput(new Integer(numFolds), 1);
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
	private static final Pair score(PredictionTable pt) {
		double numCorrect = 0;
		int [] outs = pt.getOutputFeatures();
		int [] preds = pt.getPredictionSet();

		for(int row = 0; row < pt.getNumRows(); row++) {
			for(int col =0; col < outs.length; col++) {
				//Column c = pt.getColumn(outs[col]);
				if(pt.isColumnScalar(outs[col])) {
					double real = pt.getDouble(row, outs[col]);
					double pred = pt.getDoublePrediction(row, col);
					if(real != pred)
						numCorrect++;
				}
				else {
					String real = pt.getString(row, outs[col]);
					String pred = pt.getStringPrediction(row, col);
					if(!real.equals(pred))
						numCorrect++;
				}
			}
		}
		double numRows = (double)pt.getNumRows();

		Pair p = new Pair();
		p.correct = (int)numCorrect;
		p.percent = numCorrect/numRows;

		//return numCorrect/numRows;
		return p;
	}

	private static final class Pair {
		int correct;
		double percent;
	}
}
