package ncsa.d2k.modules.core.datatype.table;

import java.util.*;
import ncsa.d2k.modules.TransformationModule;

/**
 This is a Table with some additional features designed to support
 the model-building process in a standard and interchangable way. Encapsulated
 here is some definition of a train and test set, in this case taking the form
 of two integer arrays. These integer arrays contain the indices of the rows
 containing the train data and the test data respectively.
 <p>
 In two additional arrays the input features and the output features are defined.
 The output features are the ones we would like to predict, the input features are
 those that we can use to predict them.
 <p>
 There are several additional methods defined in this class to provide
 transparent access to the various sets.  A TestTable and a TrainTable can
 be created from the test and train sets, respectively.
 <p>
 It is also typical that a dataset undergo some transformations before the
 model-building algorithm can operate on it. These transformations need to be reversed
 before the final result can actually be ascertained. Therefore, we include a list
 of reversable transformations that have been performed on the dataset in this
 Table.
 */
public interface ExampleTable extends Table {

    /////////// Collect the transformations that were performed. /////////
    /**
     Add the transformation to the list.
     @param tm the TransformationModule that performed the reversable transform.
     */
    public void addTransformation (TransformationModule tm);

    /**
     Returns the list of all reversable transformations there were performed
     on the original dataset.
     @returns an ArrayList containing the TransformationModules which transformed the data.
     */
    public ArrayList getTransformations ();

    //////////////  Input, output, test and train. ///////////////
    /**
     Returns an array of ints, the indices of the input columns.
     @return an array of ints, the indices of the input columns.
     */
    public int[] getInputFeatures () ;

    /**
     Returns the number of input features.
     @returns the number of input features.
     */
    public int getNumInputFeatures () ;

    /**
     Returns the number of example rows.
     @returns the number of example rows.
     */
    public int getNumExamples ();

    /**
     Return the number of examples in the training set.
     @returns the number of examples in the training set.
     */
    public int getNumTrainExamples ();

    /**
     Return the number of examples in the testing set.
     @returns the number of examples in the testing set.
     */
    public int getNumTestExamples ();

    /**
     Returns an array of ints, the indices of the output columns.
     @return an array of ints, the indices of the output columns.
     */
    public int[] getOutputFeatures ();

    /**
     Get the number of output features.
     @returns the number of output features.
     */
    public int getNumOutputFeatures ();

    /**
     Set the input features.
     @param inputs the indexes of the columns to be used as input features.
     */
    public void setInputFeatures (int[] inputs);

    /**
     Set the output features.
     @param outs the indexes of the columns to be used as output features.
     */
    public void setOutputFeatures (int[] outs);

    /**
     Set the indexes of the rows in the training set.
     @param trainingSet the indexes of the items to be used to train the model.
     */
    public void setTrainingSet (int[] trainingSet);

    /**
     Get the training set.
     @return the indices of the rows of the training set.
     */
    public int[] getTrainingSet ();

    /**
     Set the indexes of the rows in the testing set.
     @param testingSet the indexes of the items to be used to test the model.
     */
    public void setTestingSet (int[] testingSet);

    /**
     Get the testing set.
     @return the indices of the rows of the testing set.
     */
    public int[] getTestingSet ();

    /**
	 * Return a reference to a Table referencing only the testing data.
	 @return a reference to a Table referencing only the testing data
     */
    public TestTable getTestTable ();

    /**
     Return a reference to a Table referencing only the training data.
     @return a reference to a Table referencing only the training data.
     */
    public TrainTable getTrainTable ();

	/**
	 * Return this ExampleTable as a PredictionTable.
	 * @return This object as a PredictionTable
	 */
	public PredictionTable toPredictionTable();
}
