package ncsa.d2k.modules;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * A subclass of ModelModule that defines a predict method.  The basic
 * functionality has been implemented, so that subclasses need only implement
 * the predict() method.  A subclass can still override the basic
 * functionality, if desired.
 */
abstract public class PredictionModelModule extends ModelModule {

	static final long serialVersionUID = -9161888039007606151L;

	/** A decription of Numeric inputs and outputs */
	public static final String NUMERIC = "Numeric";
	/** A description of Textual inputs and outputs */
	public static final String TEXT = "TEXT";

	/**
	 *	Describe the function of this module.
	 *	@return the description of this module.
	 */
	public String getModuleInfo() {
		return "Makes predictions on a set of examples.";
	}

	/**
	 *	Get the name of this module.
	 *	@return the name of this module.
	 */
	public String getModuleName() {
		return "PredModel";
	}

	/**
	 *	The input is an ExampleTable.
	 *	@return the input types
	 */
	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return in;
	}

	/**
	 *	The input is an ExampleTable.
	 *	@param i the index of the input
	 *	@return the description of the input
	 */
	public String getInputInfo(int i) {
		return "A set of examples to make predicitons on.";
	}

	/**
	 *	Get the name of the input.
	 *	@param i the index of the input
	 *	@return the name of the input
	 */
	public String getInputName(int i) {
		return "TrainingSet";
	}

	/**
	 *	The output is a PredictionTable.
	 *	@return the output types
	 */
	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
		return out;
	}

	/**
	 *	Describe the output.
	 *	@param i the index of the output
	 *	@return the description of the output
	 */
	public String getOutputInfo(int i) {
		String out = "The input set of examples, with extra columns of "
			+"predicitions added to the end.";
		return out;
	}

	/**
	 *	Get the name of the output.
	 *	@param i the index of the output
	 *	@return the name of the output
	 */
	public String getOutputName(int i) {
		return "PredictionSet";
	}

	/**
	 *	Pull in the ExampleTable, pass it to the predict() method,
	 *	and push out the PredictionTable returned by predict();
	 */
	public void doit() {
		ExampleTable et = (ExampleTable)pullInput(0);
		pushOutput(predict(et), 0);
	}

	/**
	 * Predict the outcomes given a set of examples.  The return value
	 * is a PredictionTable, which is the same as the input set with
	 * extra column(s) of predictions added to the end.
	 * @param value a table with a set of examples to predict on
	 * @return the input table, with extra columns for predictions
	 */
	abstract public PredictionTable predict (ExampleTable table);

	/**
	 * Get the size of the training set that built this model.
	 * @return the size of the training set used to build this model
	 */
	public int getTrainingSetSize() {
		return 0;
	}

	/**
	 * Get the labels of the input columns.
	 * @return the labels of the input columns
	 */
	public String[] getInputColumnLabels() {
		return null;
	}

	/**
	 * Get the labels of the output columns.
	 * @return the labels of the output columns
	 */
	public String[] getOutputColumnLabels() {
		return null;
	}

	/**
	 * Get the indicies of the input columns in the training table.
	 * @return the indicies of the input columns in the training table
	 */
	public int[] getInputFeatureIndicies() {
		return null;
	}

	/**
	 * Get the indices of the output columns in the training table.
	 * @return the indices of the output columns in the training table
	 */
	public int[] getOutputFeatureIndices() {
		return null;
	}

	/**
	 * Get the data types of the input columns in the training table.  Will
	 * be Numeric or Text.
	 * @return the datatypes of the input columns in the training table
	 */
	public String [] getInputFeatureTypes() {
		return null;
	}

	/**
	 * Get the data types of the output columns in the training table.  Will
	 * be Numeric or Text.
	 * @return the data types of the output columns in the training table
	 */
	public String [] getOutputFeatureTypes() {
		return null;
	}
}
