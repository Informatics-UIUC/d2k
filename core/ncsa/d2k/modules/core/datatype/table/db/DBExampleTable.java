package ncsa.d2k.modules.core.datatype.table.db;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
import ncsa.d2k.modules.core.io.sql.*;
/**
 * <p>Title: DBExampleTable </p>
 * <p>Description: Example Table Implementation for a Database </p>
 * <p>Copyright: NCSA (c) 2002</p>
 * <p>Company: </p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 */

class DBExampleTable extends DBTable implements ExampleTable{

    /** the indicies of the records in the various training sets. */
    protected int trainSet[];

    /** the indicies of the records in the various test sets. */
    protected int testSet[];

    /**the indicies of the attributes that are inputs (to the model). */
    protected int inputColumns[];

    /**the indicies of the attributes that are inputs (to the model). */
    protected int outputColumns[];

    DBExampleTable(DBTable orig, DBDataSource _dbdatasource, DBConnection _dbconnection) {
        super(_dbdatasource, _dbconnection);

        if (orig instanceof ExampleTable) {
            if (((ExampleTable)orig).getTestingSet() != null) {
                testSet = new int[((ExampleTable)orig).getTestingSet().length];
                for (int i=0; i<((ExampleTable)orig).getTestingSet().length; i++) {
                    testSet[i] = ((ExampleTable)orig).getTestingSet()[i];
                }
            }

            if (((ExampleTable)orig).getTrainingSet() != null) {
                trainSet = new int[((ExampleTable)orig).getTrainingSet().length];
                for (int i=0; i<((ExampleTable)orig).getTrainingSet().length; i++) {
                    trainSet[i] = ((ExampleTable)orig).getTrainingSet()[i];
                }
            }

            if (((ExampleTable)orig).getInputFeatures() != null) {
                this.inputColumns = new int[((ExampleTable)orig).getInputFeatures().length];
                for (int i=0; i<((ExampleTable)orig).getInputFeatures().length; i++) {
                    inputColumns[i] = ((ExampleTable)orig).getInputFeatures()[i];
                }
            }

            if (((ExampleTable)orig).getOutputFeatures() != null) {
                this.outputColumns = new int[((ExampleTable)orig).getOutputFeatures().length];
                for (int i=0; i<((ExampleTable)orig).getOutputFeatures().length; i++) {
                    outputColumns[i] = ((ExampleTable)orig).getOutputFeatures()[i];
                }
            }
        }
        isNominal = new boolean[orig.isNominal.length];
        for(int i = 0; i < isNominal.length; i++)
            isNominal[i] = orig.isNominal[i];
    }

    public ExampleTable toExampleTable() {
        return this;
    }

    //////////////  Input, output, test and train. ///////////////
    /**	Returns an array of ints, the indices of the input columns.
    	@return an array of ints, the indices of the input columns.
     */
    public int[] getInputFeatures () {
        return  inputColumns;
    }

    /**
    	Returns the number of input features.
    	@returns the number of input features.
     */
    public int getNumInputFeatures () {
        return  inputColumns.length;
    }

    /**
    	Returns the number of example rows.
    	@returns the number of example rows.
     */
    public int getNumExamples () {
        return  this.getNumRows();
    }

    /**
    	Return the number of examples in the training set.
    	@returns the number of examples in the training set.
     */
    public int getNumTrainExamples () {
        return  this.trainSet.length;
    }

    /**
    	Return the number of examples in the testing set.
    	@returns the number of examples in the testing set.
     */
    public int getNumTestExamples () {
        return  this.testSet.length;
    }

    /**
    	Get the number of output features.
    	@returns the number of output features.
     */
    public int[] getOutputFeatures () {
        return  outputColumns;
    }

    /**
    	Get the number of output features.
    	@returns the number of output features.
     */
    public int getNumOutputFeatures () {
        return  outputColumns.length;
    }

    /**
    	Set the input features.
    	@param inputs the indexes of the columns to be used as input features.
     */
    public void setInputFeatures (int[] inputs) {
        inputColumns = inputs;
    }

    /**
    	Set the output features.
    	@param outs the indexes of the columns to be used as output features.
     */
    public void setOutputFeatures (int[] outs) {
        outputColumns = outs;
    }

    /**
    	Set the indexes of the rows in the training set.
    	@param trainingSet the indexes of the items to be used to train the model.
     */
    public void setTrainingSet (int[] trainingSet) {
        trainSet = trainingSet;
        Arrays.sort(trainSet);
    }

    /**
    	Get the training set.
    	@return the indices of the rows of the training set.
     */
    public int[] getTrainingSet () {
        return  trainSet;
    }

    /**
    	Set the indexes of the rows in the testing set.
    	@param testingSet the indexes of the items to be used to test the model.
     */
    public void setTestingSet (int[] testingSet) {
        testSet = testingSet;
        Arrays.sort(testSet);
    }

    /**
    	Get the testing set.
    	@return the indices of the rows of the testing set.
     */
    public int[] getTestingSet () {
        return  testSet;
    }


    //////////////// Access the test data ///////////////////
    /**
    	This class provides transparent access to the test data only. The testSets
    	field of the TrainTest table is used to reference only the test data, yet
    	the getter methods look exactly the same as they do for any other table.
		@return a reference to a table referencing only the testing data
     */
    public TestTable getTestTable () {
        if (testSet == null)
            return  null;
        return new LocalDBTestTable(this, dataSource.copy());
    }

    /**
    	Return a reference to a Table referencing only the training data.
    	@return a reference to a Table referencing only the training data.
     */
    public TrainTable getTrainTable () {
        if (trainSet == null)
            return  null;
        return new DBTrainTable (this, dataSource.copy(), dbConnection);
    }

    public PredictionTable toPredictionTable() {
            return new LocalDBPredictionTable(this, dataSource.copy());
    }

} //DBExampleTable
