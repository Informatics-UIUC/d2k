package ncsa.d2k.modules.core.prediction.evaluators;

import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;

import java.util.*;

/**
 * Collects N PredictionTables and pushes them out in an ArrayList
 */
public class NFoldPTCollector extends DataPrepModule  {

	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body> Collects N PredictionTables and pushes out an ArrayList containing all N PredictionTables. </body></html>";
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
			case 1: return "A PredictionTable.";
			case 2: return "The number of folds.";
			default: return "No such input";
		}
	}

	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "PredictionTable";
			case 1:
				return "N";
			default: return "NO SUCH INPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "ArrayList of PredictionTables.";
			case 1: return "The number of folds.";
			default: return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "List";
			case 1:
				return "N";
			default: return "NO SUCH OUTPUT!";
		}
	}

	public String getModuleName() {
		return "NFoldPTCollector";
	}

	public void beginExecution() {
		numFolds = 0;
		numFires = 0;
		tables = null;
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

	private ArrayList tables;

	public void doit() {
		// the first fire.  just call the predict method and keep this
		// model as the best
		if(numFires == 0) {
			PredictionTable pt = (PredictionTable)pullInput(0);
			Integer n = (Integer)pullInput(1);
			numFolds = n.intValue();

			tables = new ArrayList();
			tables.add(pt);
			numFires++;
		}
		// call the predict method and keep the model if it is the best one
		else {
			PredictionTable pt = (PredictionTable)pullInput(0);
			tables.add(pt);
			numFires++;

			if(numFires == numFolds) {
				pushOutput(tables, 0);
				pushOutput(new Integer(numFolds), 1);
			}
		}
	}
}
