package ncsa.d2k.modules.core.prediction.decisiontree.rainforest;

import ncsa.d2k.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;
import java.io.Serializable;
import java.util.*;

/**
	Encapsulates a decision tree built from a database cube.  Takes an ExampleTable as input
	and uses the decision tree to predict the outcome for each row
	in the data set.

        This module is created based on DecisionTreeModel written by David Clutter

        @author Dora Cai

*/
public class DecisionForestModel extends PredictionModelModule
	implements Serializable, ViewableDTModel {

	static final long serialVersionUID = 6788778863299676465L;

	// The root of the decision tree
	private DecisionForestNode root;

	// The table that is passed to the tree to perform predictions
	private transient ExampleTable table;

	// The unique values in the output column of the table
	//private String[] uniqueOutputs;

	//private String[][] uniqueInputs;

	// The number of examples in the data set
	private int numExamples;

	private int[] inputFeatures;
	private int[] outputFeatures;

	private int trainingSetSize;

	private String[] inputColumnNames;
	private String[] outputColumnNames;

	private String[] classNames;

        private boolean[] inputIsScalar;
        private boolean[] outputIsScalar;

	/**
		Constructor
		@param rt the root of the decision tree
	*/
	public DecisionForestModel(DecisionForestNode rt, ExampleTable table, int totalRow, String[] classValues) {
		setName("DecisionForestModel");
		root = rt;
		inputFeatures = table.getInputFeatures();
		outputFeatures = table.getOutputFeatures();
		trainingSetSize = totalRow;
                numExamples = totalRow;
                classNames = classValues;

		inputColumnNames = new String[inputFeatures.length];
                inputIsScalar = new boolean[inputFeatures.length];
		for(int i = 0; i < inputFeatures.length; i++) {
			inputColumnNames[i] = table.getColumnLabel(inputFeatures[i]);
			if(table.isColumnScalar(inputFeatures[i]))
                                inputIsScalar[i] = true;
			else
                                inputIsScalar[i] = false;
		}

		outputColumnNames = new String[outputFeatures.length];
                outputIsScalar = new boolean[outputFeatures.length];
		for(int i = 0; i < outputFeatures.length; i++) {
			outputColumnNames[i] = table.getColumnLabel(outputFeatures[i]);
                        if(table.isColumnScalar(outputFeatures[i]))
                                outputIsScalar[i] = true;
			else
                                outputIsScalar[i] = false;
		}
/*
		//classNames = uniqueValues(table, outputFeatures[0]);

		uniqueInputs = new String[inputFeatures.length][];
		for (int index = 0; index < inputFeatures.length; index++)
			uniqueInputs[index] = uniqueValues(table, inputFeatures[index]);

		// do unique outputs here
		uniqueOutputs = uniqueValues(table, outputFeatures[0]); */
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
		String[] in = {"ncsa.d2k.modules.core.prediction.decisiontree.rainforest.DecisionForestNode","ncsa.d2k.modules.core.datatype.table.ExampleTable","java.lang.int"};
		return in;
	}

	// change to prediction table
	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.prediction.decisiontree.rainforest.DecisionForestModel"};
		return out;
	}

	public String getInputInfo(int i) {
          switch (i) {
            case 0: return "DecisionForestNode.";
            case 1: return "ExampleTable.";
            case 2: return "Total number of rows.";
            default: return "No such input.";
          }
	}

    public String getInputName(int i) {
          switch (i) {
            case 0: return "DecisionForestNode.";
            case 1: return "ExampleTable.";
            case 2: return "Total number of rows.";
            default: return "No such input.";
          }
    }

	public String getOutputInfo(int i) {
		    return "Decision tree model";
	}

    public String getOutputName(int i) {
		    return "Decision tree model";
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

	public String[] getInputFeatureTypes() {
                String[] retVal = new String[inputIsScalar.length];
                for(int i = 0; i < inputIsScalar.length; i++) {
                    if(inputIsScalar[i])
                        retVal[i] = "Scalar";
                    else
                        retVal[i] = "Nominal";
                }
                return retVal;
	}

	public String[] getOutputFeatureTypes() {
                String[] retVal = new String[outputIsScalar.length];
                for(int i = 0; i < outputIsScalar.length; i++) {
                    if(outputIsScalar[i])
                        retVal[i] = "Scalar";
                    else
                        retVal[i] = "Nominal";
                }
                return retVal;
	}

	/**
		Get the class names.
		@return the class names
	*/
        /*
  	public String[] getClassNames() {
  	//public final String[] getClassNames() {
		return classNames;
	} */

	/**
		Pull in the table and pass it to predict.
	*/
	public void doit() {
		ExampleTable et = (ExampleTable)pullInput(0);
		pushOutput(this, 0);
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
          return classNames;
		//return uniqueOutputs;
	}

	/**
		Get the unique values in a column of a Table
		@param vt the Table
		@param col the column we are interested in
		@return a String array containing the unique values of the column
	*/
	public static String[] uniqueValues(Table vt, int col) {
		int numRows = vt.getNumRows();

		// count the number of unique items in this column
		HashSet set = new HashSet();
		for(int i = 0; i < numRows; i++) {
			String s = vt.getString(i, col);
			if(!set.contains(s))
				set.add(s);
		}

		String[] retVal = new String[set.size()];
		int idx = 0;
		Iterator it = set.iterator();
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
	public DecisionForestNode getRoot() {
		return root;
	}

	public void setRoot(DecisionForestNode newRoot) {
		root = newRoot;
	}

	/**
		Get the Viewable root of this decision tree.
		@return the root of the tree
	*/
	public ViewableDTNode getViewableRoot() {
		return root;
	}

	public String[] getInputs() {
		return getInputColumnLabels();
	}

	public String[] getUniqueInputValues(int index) {
          return classNames;
		//return uniqueInputs[index];
	}

	public boolean scalarInput(int index) {
            return inputIsScalar[index];
	}

        public PredictionTable predict(ExampleTable val) {
          return null;
        }

}
