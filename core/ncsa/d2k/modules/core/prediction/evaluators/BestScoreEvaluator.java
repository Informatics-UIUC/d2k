package ncsa.d2k.modules.core.prediction.evaluators;

//import ncsa.d2k.core.modules.*;
//import ncsa.util.table.*;
import ncsa.d2k.infrastructure.modules.ModelEvaluatorModule;
import ncsa.d2k.infrastructure.modules.ModelModule;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Meant to be used in conjunction with NFoldTTables.  This module takes N
 * PredictionModelModules and N testing sets.  The testing set is passed to
 * the predict() method of each PredictionModelModule.  Only the model with
 * the highest number of correct predictions is passed as an output.
 */
public class BestScoreEvaluator extends ModelEvaluatorModule  {

	private boolean debug = false;
	public void setDebug (boolean dg) { this.debug = dg; }
	public boolean getDebug () { return this.debug; }

	public String getModuleInfo() {
		String s = "Meant to be used in conjunction with NFoldTTables.  ";
		s += "This module takes N PredictionModelModules and N testing sets.  ";
		s += "The testing set is passed to the predict() method of each ";
		s += "PredictionModelModule.  Only the model with the highest number of";
		s += "correct predictions is passed as an output.";
		return s;
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule",
			"ncsa.d2k.modules.core.datatype.table.ExampleTable","java.lang.Integer"};
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule",
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "A PredictionModelModule.";
			case 1: return "A testing data set.";
			case 2: return "The number of folds.";
			default: return "No such input";
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
			default: return "NO SUCH INPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The model with the highest number of correct predictions.";
			case 1: return "The <i>table</i> with the score for the best model.";
			default: return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "BestModel";
			case 1:
				return "Score Table";
			default: return "NO SUCH OUTPUT!";
		}
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
			// change so this takes a PredictionTable, not PredictionTableImpl
			bestScore = score((PredictionTableImpl)pt);
			bestModel = pmm;
			if (debug) printStats(numFires, bestScore, pmm.getTrainingSetSize(), pt.getNumRows());
			numFires++;
		}
		// call the predict method and keep the model if it is the best one
		else {
			PredictionModelModule pmm = (PredictionModelModule)pullInput(0);
			ExampleTable tbl = (ExampleTable)pullInput(1);

			PredictionTable pt = pmm.predict(tbl);
			// change so this takes a PredictionTable, not PredictionTableImpl
			double thisscore = score((PredictionTableImpl)pt);
			if (debug) printStats(numFires, thisscore, pmm.getTrainingSetSize(), pt.getNumRows());
			if(thisscore > bestScore) {
				bestScore = thisscore;
				bestModel = pmm;
			}
			numFires++;
			if(numFires == numFolds) {
				pushOutput(bestModel, 0);
				double[] scores = new double [1];
				scores[0] = bestScore;
				DoubleColumn dc = new DoubleColumn (scores);
				Column [] columns = new Column[1];
				columns[0] = dc;
				pushOutput (DefaultTableFactory.getInstance().createTable(columns), 1);

				// reset.
				this.beginExecution();
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
	private static final double score(PredictionTableImpl pt) {
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
					String pred = null;
					try {
						pred = pt.getString(row, preds[col]);
					} catch (Exception e) {
						e.printStackTrace ();
					}
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
