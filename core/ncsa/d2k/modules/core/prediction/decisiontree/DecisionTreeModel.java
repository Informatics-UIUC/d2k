package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
	Encapsulates a decision tree.  Takes an ExampleTable as input
	and uses the decision tree to predict the outcome for each row
	in the data set.
	TODO: change to PredictionTable
*/
public class DecisionTreeModel extends PredictionModelModule
	implements Serializable, ViewableDTModel {

	/** The root of the decision tree */
	DecisionTreeNode root;
	/** The table that is passed to the tree to perform predictions */
	transient ExampleTable table;
	/** The unique values in the output column of the table */
	String[] uniqueOutputs;
	/** The number of examples in the data set */
	int numExamples;


	/**
		Constructor
		@param rt the root of the decision tree
	*/
	DecisionTreeModel(DecisionTreeNode rt) {
		root = rt;
	}

	static String INFO = "Encapsulates a decision tree.  Takes an "
		+"ExampleTable as input and uses the decision tree to "
		+"predict the outcome for each row in the data set.";
	static String[] IN = {"ncsa.d2k.util.datatype.ExampleTable"};
	static String[] OUT = {"ncsa.d2k.util.datatype.PredictionTable"};
	static String IN0 = "The data set to predict the outcomes for.";
	static String OUT0 = "The original data set with an extra column "
		+"of predictions.";

	public String getModuleInfo() {
		return INFO;
	}

	public String[] getInputTypes() {
		return IN;
	}

	// change to prediction table
	public String[] getOutputTypes() {
		return OUT;
	}

	public String getInputInfo(int i) {
		return IN0;
	}

	public String getOutputInfo(int i) {
		return OUT0;
	}

	/**
		Pull in the table and pass it to predict.
	*/
	public void doit() {
		ExampleTable et = (ExampleTable)pullInput(0);
		PredictionTable retVal = predict(et);
		pushOutput(retVal, 0);
	}

	/**
		Predict an outcome for each row of the table using the
		decision tree.
		@param val the table
		@return the table with an extra column of predictions at the end
	*/
	public PredictionTable predict(ExampleTable val) {
		table = (ExampleTable)val;
		numExamples = table.getNumRows();

		PredictionTable pt = null;
		if (table instanceof PredictionTable)
			pt = (PredictionTable) table;
		else
			pt = new PredictionTable(table);

		int[] outputs = pt.getOutputFeatures();
		int[] preds = pt.getPredictionSet();

		//StringColumn sc = new StringColumn(table.getNumRows());
		//StringColumn sc = pt.getColumn(preds[0]);

		for(int i = 0; i < pt.getNumRows(); i++) {
			String pred = (String)root.evaluate(pt, i, outputs[0]);
			//sc.setString(pred, i);
			pt.setString(pred, i, preds[0]);
		}
		//table.addColumn(sc);
		uniqueOutputs = uniqueValues(pt, outputs[0]);
		//return table;
		return pt;
	}

	/**
	 * Get the number of examples from the data set passed to
	 * the predict method.
	 * @return the number of examples.
	 */
	public int getNumExamples() {
		return numExamples;
	}

	/**
		Get the unique values of the output column.
		@param the unique items of the output column
	*/
	public String[] getUniqueOutputValues() {
		return uniqueOutputs;
	}

	/**
		Get the unique values in a column of a VerticalTable
		@param vt the VerticalTable
		@param col the column we are interested in
		@return a String array containing the unique values of the column
	*/
	public static String[] uniqueValues(VerticalTable vt, int col) {
		int numRows = vt.getNumRows();

		// count the number of unique items in this column
		HashMap map = new HashMap();
		for(int i = 0; i < numRows; i++) {
			String s = vt.getString(i, col);
			if(!map.containsKey(s))
				map.put(s, s);
		}

		String[] retVal = new String[map.size()];
		int idx = 0;
		Iterator it = map.keySet().iterator();
		while(it.hasNext()) {
			retVal[idx] = (String)it.next();
			idx++;
		}
		return retVal;
	}

	/**
		Get the root of this decision tree.
		@return the root of the tree
	*/
	public DecisionTreeNode getRoot() {
		return root;
	}
	/**
		Get the Viewable root of this decision tree.
		@return the root of the tree
	*/
	public ViewableDTNode getViewableRoot() {
		return root;
	}



}
