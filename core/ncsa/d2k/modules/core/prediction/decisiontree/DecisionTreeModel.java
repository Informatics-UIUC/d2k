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
*/
public class DecisionTreeModel extends PredictionModelModule
	implements Serializable, ViewableDTModel, HasNames {

	/** The root of the decision tree */
	private DecisionTreeNode root;
	/** The table that is passed to the tree to perform predictions */
	private transient ExampleTable table;
	/** The unique values in the output column of the table */
	private String[] uniqueOutputs;
	/** The number of examples in the data set */
	private int numExamples;

	private int[] inputFeatures;
	private int[] outputFeatures;

	private int trainingSetSize;

	private String[] inputColumnNames;
	private String[] outputColumnNames;

	/**
		Constructor
		@param rt the root of the decision tree
	*/
	DecisionTreeModel(DecisionTreeNode rt, ExampleTable table) {
		setName("DecisionTreeModel");
		root = rt;
		inputFeatures = table.getInputFeatures();
		outputFeatures = table.getOutputFeatures();
		trainingSetSize = table.getNumRows();

		inputColumnNames = new String[inputFeatures.length];
		for(int i = 0; i < inputFeatures.length; i++) {
			inputColumnNames[i] = table.getColumnLabel(inputFeatures[i]);
		}

		outputColumnNames = new String[outputFeatures.length];
		for(int i = 0; i < outputFeatures.length; i++)
			outputColumnNames[i] = table.getColumnLabel(outputFeatures[i]);
	}

	public String getModuleInfo() {
		String s = "Encapsulates a decision tree.  Takes an ";
		s += "ExampleTable as input and uses the decision tree to ";
		s += "predict the outcome for each row in the data set.";
		return s;
	}

    public String getModuleName() {
        return "DTModel";
    }

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.util.datatype.ExampleTable"};
		return in;
	}

	// change to prediction table
	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.util.datatype.PredictionTable",
            "ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return out;
	}

	public String getInputInfo(int i) {
		return "The data set to predict the outcomes for.";
	}

    public String getInputName(int i) {
        return "Dataset";
    }

	public String getOutputInfo(int i) {
        if(i == 0)
		    return "The original data set with an extra column of predictions.";
        else
            return "A reference to this model.";
	}

    public String getOutputName(int i) {
        if(i == 0)
            return "Predictions";
        else
            return "DTModel";
    }

	public int getTrainingSetSize() {
		return trainingSetSize;
	}

	public String[] getInputColumnLabels() {
		return inputColumnNames;
	}

	public String[] getOutputColumnLabels() {
		return outputColumnNames;
	}

	public int[] getInputFeatureIndicies() {
		return inputFeatures;
	}

	public int[] getOutputFeatureIndices() {
		return outputFeatures;
	}

	/**
		Pull in the table and pass it to predict.
	*/
	public void doit() {
		ExampleTable et = (ExampleTable)pullInput(0);
		PredictionTable retVal = predict(et);
		pushOutput(retVal, 0);
        pushOutput(this, 1);
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

		if(preds.length == 0) {
			StringColumn sc = new StringColumn(pt.getNumRows());
			pt.addPredictionColumn(sc);
			preds = pt.getPredictionSet();
		}

		for(int i = 0; i < pt.getNumRows(); i++) {
			String pred = (String)root.evaluate(pt, i);
			pt.setString(pred, i, preds[0]);
		}
		uniqueOutputs = uniqueValues(pt, outputs[0]);
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
