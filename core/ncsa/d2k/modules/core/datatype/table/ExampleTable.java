package ncsa.d2k.modules.core.datatype.table;

import java.util.*;
import ncsa.d2k.infrastructure.modules.*;

/**
 This is a table with some additional features designed to support
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
 It is also typical that a dataset undergo some transformations to before the
 model-building algorithm can operate on it. These transformations need to be reversed
 before the final result can actually be ascertained. Therefore, we include a list
 of reversable transformations that have been performed on the dataset in this
 table.
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
     Get the number of output features.
     @returns the number of output features.
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

    ///////////////////// ACCESSOR METHODS ///////////////////
    /**
	 * Get an int from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the int in the table at (testSet[row], outputColumns[column])
     */
    //public int getTestOutputInt (int row, int column);

    /**
	 * Get a short from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the short in the table at (testSet[row], outputColumns[column])
     */
    //public short getTestOutputShort (int row, int column);

    /**
	 * Get a long from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the long in the table at (testSet[row], outputColumns[column])
     */
    //public long getTestOutputLong (int row, int column);

    /**
	 * Get a float from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the float in the table at (testSet[row], outputColumns[column])
     */
    //public float getTestOutputFloat (int row, int column);

    /**
	 * Get a double from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the double in the table at (testSet[row], outputColumns[column])
     */
    //public double getTestOutputDouble (int row, int column);

    /**
	 * Get a String from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the String in the table at (testSet[row], outputColumns[column])
     */
    //public String getTestOutputString (int row, int column);

    /**
	 * Get a value from the table at (testSet[row], outputColumns[column]) as a byte[]
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the value in the table at (testSet[row], outputColumns[column]) as a byte[]
     */
    //public byte[] getTestOutputBytes (int row, int column);

    /**
	 * Get a value from the table at (testSet[row], outputColumns[column]) as a char[]
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the value in the table at (testSet[row], outputColumns[column]) as a char[]
     */
    //public char[] getTestOutputChars (int row, int column);

    /**
	 * Get a boolean from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the boolean in the table at (testSet[row], outputColumns[column])
     */
    //public boolean getTestOutputBoolean (int row, int column);

    /**
	 * Get an int from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the int in the table at (testSet[row], inputColumns[column])
     */
    //public int getTestInputInt (int row, int column);

    /**
	 * Get a short from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the short in the table at (testSet[row], inputColumns[column])
     */
    //public short getTestInputShort (int row, int column);

    /**
	 * Get a long from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the long in the table at (testSet[row], inputColumns[column])
     */
    //public long getTestInputLong (int row, int column);

    /**
	 * Get a flaot from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the float in the table at (testSet[row], inputColumns[column])
     */
    //public float getTestInputFloat (int row, int column);

    /**
	 * Get a double from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the double in the table at (testSet[row], inputColumns[column])
     */
    //public double getTestInputDouble (int row, int column);

    /**
	 * Get a String from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the String in the table at (testSet[row], inputColumns[column])
     */
    //public String getTestInputString (int row, int column);

    /**
	 * Get a value from the table at (testSet[row], inputColumns[column]) as a byte[]
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the value in the table at (testSet[row], inputColumns[column]) as a byte[]
     */
    //public byte[] getTestInputBytes (int row, int column);

    /**
	 * Get a value from the table at (testSet[row], inputColumns[column]) as a char[]
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the value in the table at (testSet[row], inputColumns[column]) as a char[]
     */
    //public char[] getTestInputChars (int row, int column);

    /**
	 * Get a boolean from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the boolean in the table at (testSet[row], inputColumns[column])
     */
    //public boolean getTestInputBoolean (int row, int column);

    /**
	 * Get an int from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the int in the table at (trainSet[row], outputColumns[column])
     */
    //public int getTrainOutputInt (int row, int column);

    /**
	 * Get a short from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the short in the table at (trainSet[row], outputColumns[column])
     */
    //public short getTrainOutputShort (int row, int column);

    /**
	 * Get a long from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the long in the table at (trainSet[row], outputColumns[column])
     */
    //public long getTrainOutputLong (int row, int column);

    /**
	 * Get a float from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the float in the table at (trainSet[row], outputColumns[column])
     */
    //public float getTrainOutputFloat (int row, int column);

    /**
	 * Get a double from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the double in the table at (trainSet[row], outputColumns[column])
     */
    //public double getTrainOutputDouble (int row, int column);

    /**
	 * Get a String from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the String in the table at (trainSet[row], outputColumns[column])
     */
    //public String getTrainOutputString (int row, int column);

    /**
	 * Get a value from the table at (trainSet[row], outputColumns[column]) as a byte[]
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the value in the table at (trainSet[row], outputColumns[column]) as a byte[]
     */
    //public byte[] getTrainOutputBytes (int row, int column);

    /**
	 * Get a value from the table at (trainSet[row], outputColumns[column]) as a char[]
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the value in the table at (trainSet[row], outputColumns[column]) as a char[]
     */
    //public char[] getTrainOutputChars (int row, int column);

    /**
	 * Get a boolean from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the boolean in the table at (trainSet[row], outputColumns[column])
     */
    //public boolean getTrainOutputBoolean (int row, int column);

    /**
	 * Get an int from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the int in the table at (trainSet[row], inputColumns[column])
     */
    //public int getTrainInputInt (int row, int column);

    /**
	 * Get a short from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the short in the table at (trainSet[row], inputColumns[column])
     */
    //public short getTrainInputShort (int row, int column);

    /**
	 * Get a long from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the long in the table at (trainSet[row], inputColumns[column])
     */
    //public long getTrainInputLong (int row, int column);

    /**
	 * Get a float from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the float in the table at (trainSet[row], inputColumns[column])
     */
    //public float getTrainInputFloat (int row, int column);

    /**
	 * Get a double from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the double in the table at (trainSet[row], inputColumns[column])
     */
    //public double getTrainInputDouble (int row, int column);

    /**
	 * Get a String from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the String in the table at (trainSet[row], inputColumns[column])
     */
    //public String getTrainInputString (int row, int column);

    /**
	 * Get a value from the table at (trainSet[row], inputColumns[column]) as a byte[]
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the value in the table at (trainSet[row], inputColumns[column]) as a byte[]
     */
    //public byte[] getTrainInputBytes (int row, int column);

    /**
	 * Get a value from the table at (trainSet[row], inputColumns[column]) as a char[]
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the value in the table at (trainSet[row], inputColumns[column]) as a char[]
     */
    //public char[] getTrainInputChars (int row, int column);

    /**
	 * Get a boolean from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the boolean in the table at (trainSet[row], inputColumns[column])
     */
    //public boolean getTrainInputBoolean (int row, int column);

    //////////////// Access the test data ///////////////////
    /**
     This class provides transparent access to the test data only. The testSets
     field of the TrainTest table is used to reference only the test data, yet
     the getter methods look exactly the same as they do for any other vertical table.
	 @return a reference to a vertical table referencing only the testing data
     */
    public TestTable getTestTable ();

    /**
     Return a reference to a vertical table referencing only the training data.
     @return a reference to a vertical table referencing only the training data.
     */
    public TrainTable getTrainTable ();
}