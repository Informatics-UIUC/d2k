package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;

//import ncsa.d2k.modules.*;

/**
 * A default implementation of ExampleTable.
 */
public class ExampleTableImpl
    extends MutableTableImpl
    implements ExampleTable {

  static final long serialVersionUID = -3828377409585479094L;

  /** the indicies of the records in the various training sets. */
  protected int trainSet[];

  /** the indicies of the records in the various test sets. */
  protected int testSet[];

  /**the indicies of the attributes that are inputs (to the model). */
  protected int inputColumns[];

  /**the indicies of the attributes that are inputs (to the model). */
  protected int outputColumns[];

  private String[] inputNames;
  private String[] outputNames;

  /**
   * Create a new ExampleTableImpl
   */
  public ExampleTableImpl() {
    super();
  }

  /**
   * Create a new ExampleTableImpl given the number of columns
   * @param numColumns the number of columns
   */
  public ExampleTableImpl(int numColumns) {
    super(numColumns);
  }

  /**
   * Create a new ExampleTableImpl given a StaticDocument representation of the data.
   * @param sd the StaticDocument to fill the table with
   */
  /*public ExampleTableImpl (StaticDocument sd) {
      super(sd);
       }*/

  /**
   Given a TableImpl to represent, replicate its
   contents in an ExampleTable.
   @param table the table to replicate.
   */
  public ExampleTableImpl(TableImpl table) {
    int numColumns = table.getNumColumns();
    columns = new Column[numColumns];
    for (int i = 0; i < numColumns; i++) {
      columns[i] = table.getColumn(i);
    }
    setLabel(table.getLabel());
    setComment(table.getComment());

    inputNames = new String[0];
    outputNames = new String[0];

    //setType(table.getType());
    if (table instanceof ExampleTableImpl) {
      // Make sure we get the input / output definitions.
      ExampleTableImpl tt = (ExampleTableImpl) table;
      // make a copy of the input features
      int[] origInputFeatures = tt.getInputFeatures();
      if (origInputFeatures != null) {
        int inLen = origInputFeatures.length;
        this.inputColumns = new int[inLen];
        System.arraycopy(origInputFeatures, 0, inputColumns, 0, inLen);
        setInputFeatures(inputColumns);
      }
      else
        setInputFeatures(new int[0]);
      // make a copy of the output features
      int[] origOutputFeatures = tt.getOutputFeatures();
      if (origOutputFeatures != null) {
        int outLen = origOutputFeatures.length;
        this.outputColumns = new int[outLen];
        System.arraycopy(origOutputFeatures, 0, outputColumns, 0, outLen);
        setOutputFeatures(outputColumns);
      }
      else
        setOutputFeatures(new int[0]);
      // make a copy of the test set
      int[] origTestSet = tt.getTestingSet();
      if (origTestSet != null) {
        int testLen = origTestSet.length;
        this.testSet = new int[testLen];
        System.arraycopy(origTestSet, 0, testSet, 0, testLen);
      }
      // make a copy of the train set
      int[] origTrainSet = tt.getTrainingSet();
      if (origTrainSet != null) {
        int trainLen = origTrainSet.length;
        this.trainSet = new int[trainLen];
        System.arraycopy(origTrainSet, 0, trainSet, 0, trainLen);
      }

      //copy the transformations
      try {
        transformations = (ArrayList) ( (ArrayList) ( (MutableTable) tt).
                                       getTransformations()).clone();
      }
      catch (Exception e) {
        transformations = null;
      }
    }
  }

  /**
   Return an exact copy of this Table.  A deep copy is attempted, but if
     it fails a new Table will be created, initialized with the same data as
     this column.
   @return A new Table with a copy of the contents of this Table.
   */
  public Table copy() {
    ExampleTableImpl vt;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      vt = (ExampleTableImpl) ois.readObject();
      ois.close();
      return vt;
    }
    catch (Exception e) {
      vt = new ExampleTableImpl(getNumColumns());
      vt.setKeyColumn(getKeyColumn());
      for (int i = 0; i < getNumColumns(); i++) {
        vt.setColumn(getColumn(i).copy(), i);
      }
      vt.setLabel(getLabel());
      vt.setComment(getComment());
      //vt.setType(getType());
      vt.setInputFeatures(getInputFeatures());
      vt.setOutputFeatures(getOutputFeatures());
      vt.transformations = (ArrayList) transformations.clone();
      vt.setTestingSet(getTestingSet());
      vt.setTrainingSet(getTrainingSet());
      return vt;
    }
  }

  //////////////  Input, output, test and train. ///////////////
  /**
   Returns an array of ints, the indices of the input columns.
   @return an array of ints, the indices of the input columns.
   */
  public int[] getInputFeatures() {
    return inputColumns;
  }

  /**
   Returns the number of input features.
   @returns the number of input features.
   */
  public int getNumInputFeatures() {
    return inputColumns.length;
  }

  /**
   Returns the number of example rows.
   @returns the number of example rows.
   */
  public int getNumExamples() {
    return this.getNumRows();
  }

  /**
   Return the number of examples in the training set.
   @returns the number of examples in the training set.
   */
  public int getNumTrainExamples() {
    return this.trainSet.length;
  }

  /**
   Return the number of examples in the testing set.
   @returns the number of examples in the testing set.
   */
  public int getNumTestExamples() {
    return this.testSet.length;
  }

  /**
   Get the number of output features.
   @returns the number of output features.
   */
  public int[] getOutputFeatures() {
    return outputColumns;
  }

  /**
   Get the number of output features.
   @returns the number of output features.
   */
  public int getNumOutputFeatures() {
    return outputColumns.length;
  }

  /**
   Set the input features.
   @param inputs the indexes of the columns to be used as input features.
   */
  public void setInputFeatures(int[] inputs) {
    inputColumns = inputs;
    /*if (outputColumns == null) {
        // So these are the inputs, we can surmise the outputs.
        int numOutputs = this.getNumColumns() - inputs.length;
        outputColumns = new int[numOutputs];
        boolean[] isInput = new boolean[this.getNumColumns()];
        // Set the bit for each column that is an input.
        for (int j = 0; j < inputs.length; j++)
            isInput[inputs[j]] = true;
        // So, each column that is not an input must be an output.
        for (int i = 0, outputIndex = 0; i < this.getNumColumns(); i++)
            if (!isInput[i])
                outputColumns[outputIndex++] = i;
             }*/
    inputNames = new String[inputs.length];
    for (int i = 0; i < inputNames.length; i++) {
      inputNames[i] = getColumnLabel(inputs[i]);
    }
  }

  /**
   Set the output features.
   @param outs the indexes of the columns to be used as output features.
   */
  public void setOutputFeatures(int[] outs) {
    outputColumns = outs;
    outputNames = new String[outs.length];
    for (int i = 0; i < outputNames.length; i++) {
      outputNames[i] = getColumnLabel(outs[i]);
    }
  }

  /**
   Set the indexes of the rows in the training set.
   @param trainingSet the indexes of the items to be used to train the model.
   */
  public void setTrainingSet(int[] trainingSet) {
    trainSet = trainingSet;
  }

  /**
   Get the training set.
   @return the indices of the rows of the training set.
   */
  public int[] getTrainingSet() {
    return trainSet;
  }

  /**
   Set the indexes of the rows in the testing set.
   @param testingSet the indexes of the items to be used to test the model.
   */
  public void setTestingSet(int[] testingSet) {
    testSet = testingSet;
  }

  /**
   Get the testing set.
   @return the indices of the rows of the testing set.
   */
  public int[] getTestingSet() {
    return testSet;
  }

  ///////////////////// ACCESSOR METHODS ///////////////////
  /**
   * Get an int from the table at (testSet[row], outputColumns[column])
   * @param row the row of the test set
   * @param column the index into outputColumns
   * @return the int in the table at (testSet[row], outputColumns[column])
   * @deprecated Use TestTable
   */
  public int getTestOutputInt(int row, int column) {
    return columns[outputColumns[column]].getInt(testSet[row]);
  }

  /**
   * Get a short from the table at (testSet[row], outputColumns[column])
   * @param row the row of the test set
   * @param column the index into outputColumns
   * @return the short in the table at (testSet[row], outputColumns[column])
   * @deprecated Use TestTable
   */
  public short getTestOutputShort(int row, int column) {
    return columns[outputColumns[column]].getShort(testSet[row]);
  }

  /**
   * Get a long from the table at (testSet[row], outputColumns[column])
   * @param row the row of the test set
   * @param column the index into outputColumns
   * @return the long in the table at (testSet[row], outputColumns[column])
   * @deprecated Use TestTable
   */
  public long getTestOutputLong(int row, int column) {
    return columns[outputColumns[column]].getLong(testSet[row]);
  }

  /**
   * Get a float from the table at (testSet[row], outputColumns[column])
   * @param row the row of the test set
   * @param column the index into outputColumns
   * @return the float in the table at (testSet[row], outputColumns[column])
   * @deprecated Use TestTable
   */
  public float getTestOutputFloat(int row, int column) {
    return columns[outputColumns[column]].getFloat(testSet[row]);
  }

  /**
   * Get a double from the table at (testSet[row], outputColumns[column])
   * @param row the row of the test set
   * @param column the index into outputColumns
   * @return the double in the table at (testSet[row], outputColumns[column])
   * @deprecated Use TestTable
   */
  public double getTestOutputDouble(int row, int column) {
    return columns[outputColumns[column]].getDouble(testSet[row]);
  }

  /**
   * Get a String from the table at (testSet[row], outputColumns[column])
   * @param row the row of the test set
   * @param column the index into outputColumns
   * @return the String in the table at (testSet[row], outputColumns[column])
   * @deprecated Use TestTable
   */
  public String getTestOutputString(int row, int column) {
    return columns[outputColumns[column]].getString(testSet[row]);
  }

  /**
   * Get a value from the table at (testSet[row], outputColumns[column]) as a byte[]
   * @param row the row of the test set
   * @param column the index into outputColumns
   * @return the value in the table at (testSet[row], outputColumns[column]) as a byte[]
   * @deprecated Use TestTable
   */
  public byte[] getTestOutputBytes(int row, int column) {
    return columns[outputColumns[column]].getBytes(testSet[row]);
  }

  /**
   * Get a value from the table at (testSet[row], outputColumns[column]) as a char[]
   * @param row the row of the test set
   * @param column the index into outputColumns
   * @return the value in the table at (testSet[row], outputColumns[column]) as a char[]
   * @deprecated Use TestTable
   */
  public char[] getTestOutputChars(int row, int column) {
    return columns[outputColumns[column]].getChars(testSet[row]);
  }

  /**
   * Get a boolean from the table at (testSet[row], outputColumns[column])
   * @param row the row of the test set
   * @param column the index into outputColumns
   * @return the boolean in the table at (testSet[row], outputColumns[column])
   * @deprecated Use TestTable
   */
  public boolean getTestOutputBoolean(int row, int column) {
    return columns[outputColumns[column]].getBoolean(testSet[row]);
  }

  /**
   * Get an int from the table at (testSet[row], inputColumns[column])
   * @param row the row of the test set
   * @param column the index into inputColumns
   * @return the int in the table at (testSet[row], inputColumns[column])
   * @deprecated Use TestTable
   */
  public int getTestInputInt(int row, int column) {
    return columns[inputColumns[column]].getInt(testSet[row]);
  }

  /**
   * Get a short from the table at (testSet[row], inputColumns[column])
   * @param row the row of the test set
   * @param column the index into inputColumns
   * @return the short in the table at (testSet[row], inputColumns[column])
   * @deprecated Use TestTable
   */
  public short getTestInputShort(int row, int column) {
    return columns[inputColumns[column]].getShort(testSet[row]);
  }

  /**
   * Get a long from the table at (testSet[row], inputColumns[column])
   * @param row the row of the test set
   * @param column the index into inputColumns
   * @return the long in the table at (testSet[row], inputColumns[column])
   * @deprecated Use TestTable
   */
  public long getTestInputLong(int row, int column) {
    return columns[inputColumns[column]].getLong(testSet[row]);
  }

  /**
   * Get a flaot from the table at (testSet[row], inputColumns[column])
   * @param row the row of the test set
   * @param column the index into inputColumns
   * @return the float in the table at (testSet[row], inputColumns[column])
   * @deprecated Use TestTable
   */
  public float getTestInputFloat(int row, int column) {
    return columns[inputColumns[column]].getFloat(testSet[row]);
  }

  /**
   * Get a double from the table at (testSet[row], inputColumns[column])
   * @param row the row of the test set
   * @param column the index into inputColumns
   * @return the double in the table at (testSet[row], inputColumns[column])
   * @deprecated Use TestTable
   */
  public double getTestInputDouble(int row, int column) {
    return columns[inputColumns[column]].getDouble(testSet[row]);
  }

  /**
   * Get a String from the table at (testSet[row], inputColumns[column])
   * @param row the row of the test set
   * @param column the index into inputColumns
   * @return the String in the table at (testSet[row], inputColumns[column])
   * @deprecated Use TestTable
   */
  public String getTestInputString(int row, int column) {
    return columns[inputColumns[column]].getString(testSet[row]);
  }

  /**
       * Get a value from the table at (testSet[row], inputColumns[column]) as a byte[]
   * @param row the row of the test set
   * @param column the index into inputColumns
   * @return the value in the table at (testSet[row], inputColumns[column]) as a byte[]
   * @deprecated Use TestTable
   */
  public byte[] getTestInputBytes(int row, int column) {
    return columns[inputColumns[column]].getBytes(testSet[row]);
  }

  /**
       * Get a value from the table at (testSet[row], inputColumns[column]) as a char[]
   * @param row the row of the test set
   * @param column the index into inputColumns
   * @return the value in the table at (testSet[row], inputColumns[column]) as a char[]
   * @deprecated Use TestTable
   */
  public char[] getTestInputChars(int row, int column) {
    return columns[inputColumns[column]].getChars(testSet[row]);
  }

  /**
   * Get a boolean from the table at (testSet[row], inputColumns[column])
   * @param row the row of the test set
   * @param column the index into inputColumns
   * @return the boolean in the table at (testSet[row], inputColumns[column])
   * @deprecated Use TestTable
   */
  public boolean getTestInputBoolean(int row, int column) {
    return columns[inputColumns[column]].getBoolean(testSet[row]);
  }

  /**
   * Get an int from the table at (trainSet[row], outputColumns[column])
   * @param row the row of the train set
   * @param column the index into outputColumns
   * @return the int in the table at (trainSet[row], outputColumns[column])
   * @deprecated Use TrainTable
   */
  public int getTrainOutputInt(int row, int column) {
    return columns[outputColumns[column]].getInt(trainSet[row]);
  }

  /**
   * Get a short from the table at (trainSet[row], outputColumns[column])
   * @param row the row of the train set
   * @param column the index into outputColumns
   * @return the short in the table at (trainSet[row], outputColumns[column])
   * @deprecated Use TrainTable
   */
  public short getTrainOutputShort(int row, int column) {
    return columns[outputColumns[column]].getShort(trainSet[row]);
  }

  /**
   * Get a long from the table at (trainSet[row], outputColumns[column])
   * @param row the row of the train set
   * @param column the index into outputColumns
   * @return the long in the table at (trainSet[row], outputColumns[column])
   * @deprecated Use TrainTable
   */
  public long getTrainOutputLong(int row, int column) {
    return columns[outputColumns[column]].getLong(trainSet[row]);
  }

  /**
   * Get a float from the table at (trainSet[row], outputColumns[column])
   * @param row the row of the train set
   * @param column the index into outputColumns
   * @return the float in the table at (trainSet[row], outputColumns[column])
   * @deprecated Use TrainTable
   */
  public float getTrainOutputFloat(int row, int column) {
    return columns[outputColumns[column]].getFloat(trainSet[row]);
  }

  /**
   * Get a double from the table at (trainSet[row], outputColumns[column])
   * @param row the row of the train set
   * @param column the index into outputColumns
   * @return the double in the table at (trainSet[row], outputColumns[column])
   * @deprecated Use TrainTable
   */
  public double getTrainOutputDouble(int row, int column) {
    return columns[outputColumns[column]].getDouble(trainSet[row]);
  }

  /**
   * Get a String from the table at (trainSet[row], outputColumns[column])
   * @param row the row of the train set
   * @param column the index into outputColumns
   * @return the String in the table at (trainSet[row], outputColumns[column])
   * @deprecated Use TrainTable
   */
  public String getTrainOutputString(int row, int column) {
    return columns[outputColumns[column]].getString(trainSet[row]);
  }

  /**
   * Get a value from the table at (trainSet[row], outputColumns[column]) as a byte[]
   * @param row the row of the train set
   * @param column the index into outputColumns
   * @return the value in the table at (trainSet[row], outputColumns[column]) as a byte[]
   * @deprecated Use TrainTable
   */
  public byte[] getTrainOutputBytes(int row, int column) {
    return columns[outputColumns[column]].getBytes(trainSet[row]);
  }

  /**
   * Get a value from the table at (trainSet[row], outputColumns[column]) as a char[]
   * @param row the row of the train set
   * @param column the index into outputColumns
   * @return the value in the table at (trainSet[row], outputColumns[column]) as a char[]
   * @deprecated Use TrainTable
   */
  public char[] getTrainOutputChars(int row, int column) {
    return columns[outputColumns[column]].getChars(trainSet[row]);
  }

  /**
   * Get a boolean from the table at (trainSet[row], outputColumns[column])
   * @param row the row of the train set
   * @param column the index into outputColumns
   * @return the boolean in the table at (trainSet[row], outputColumns[column])
   * @deprecated Use TrainTable
   */
  public boolean getTrainOutputBoolean(int row, int column) {
    return columns[outputColumns[column]].getBoolean(trainSet[row]);
  }

  /**
   * Get an int from the table at (trainSet[row], inputColumns[column])
   * @param row the row of the train set
   * @param column the index into intputColumns
   * @return the int in the table at (trainSet[row], inputColumns[column])
   * @deprecated Use TrainTable
   */
  public int getTrainInputInt(int row, int column) {
    return columns[inputColumns[column]].getInt(trainSet[row]);
  }

  /**
   * Get a short from the table at (trainSet[row], inputColumns[column])
   * @param row the row of the train set
   * @param column the index into intputColumns
   * @return the short in the table at (trainSet[row], inputColumns[column])
   * @deprecated Use TrainTable
   */
  public short getTrainInputShort(int row, int column) {
    return columns[inputColumns[column]].getShort(trainSet[row]);
  }

  /**
   * Get a long from the table at (trainSet[row], inputColumns[column])
   * @param row the row of the train set
   * @param column the index into intputColumns
   * @return the long in the table at (trainSet[row], inputColumns[column])
   * @deprecated Use TrainTable
   */
  public long getTrainInputLong(int row, int column) {
    return columns[inputColumns[column]].getLong(trainSet[row]);
  }

  /**
   * Get a float from the table at (trainSet[row], inputColumns[column])
   * @param row the row of the train set
   * @param column the index into intputColumns
   * @return the float in the table at (trainSet[row], inputColumns[column])
   * @deprecated Use TrainTable
   */
  public float getTrainInputFloat(int row, int column) {
    return columns[inputColumns[column]].getFloat(trainSet[row]);
  }

  /**
   * Get a double from the table at (trainSet[row], inputColumns[column])
   * @param row the row of the train set
   * @param column the index into intputColumns
   * @return the double in the table at (trainSet[row], inputColumns[column])
   * @deprecated Use TrainTable
   */
  public double getTrainInputDouble(int row, int column) {
    return columns[inputColumns[column]].getDouble(trainSet[row]);
  }

  /**
   * Get a String from the table at (trainSet[row], inputColumns[column])
   * @param row the row of the train set
   * @param column the index into intputColumns
   * @return the String in the table at (trainSet[row], inputColumns[column])
   * @deprecated Use TrainTable
   */
  public String getTrainInputString(int row, int column) {
    return columns[inputColumns[column]].getString(trainSet[row]);
  }

  /**
   * Get a value from the table at (trainSet[row], inputColumns[column]) as a byte[]
   * @param row the row of the train set
   * @param column the index into intputColumns
   * @return the value in the table at (trainSet[row], inputColumns[column]) as a byte[]
   * @deprecated Use TrainTable
   */
  public byte[] getTrainInputBytes(int row, int column) {
    return columns[inputColumns[column]].getBytes(trainSet[row]);
  }

  /**
   * Get a value from the table at (trainSet[row], inputColumns[column]) as a char[]
   * @param row the row of the train set
   * @param column the index into intputColumns
   * @return the value in the table at (trainSet[row], inputColumns[column]) as a char[]
   * @deprecated Use TrainTable
   */
  public char[] getTrainInputChars(int row, int column) {
    return columns[inputColumns[column]].getChars(trainSet[row]);
  }

  /**
   * Get a boolean from the table at (trainSet[row], inputColumns[column])
   * @param row the row of the train set
   * @param column the index into intputColumns
   * @return the boolean in the table at (trainSet[row], inputColumns[column])
   * @deprecated Use TrainTable
   */
  public boolean getTrainInputBoolean(int row, int column) {
    return columns[inputColumns[column]].getBoolean(trainSet[row]);
  }

  //////////////// Access the test data ///////////////////
  /**
   This class provides transparent access to the test data only. The testSets
   field of the TrainTest table is used to reference only the test data, yet
   the getter methods look exactly the same as they do for any other table.
     @return a reference to a table referencing only the testing data
   */
  public TestTable getTestTable() {
    if (testSet == null) {
      return null;
    }
    return new TestTableImpl(this);
  }

  /**
   Return a reference to a Table referencing only the training data.
   @return a reference to a Table referencing only the training data.
   */
  public TrainTable getTrainTable() {
    if (trainSet == null) {
      return null;
    }
    return new TrainTableImpl(this);
  }

  public PredictionTable toPredictionTable() {
    return new PredictionTableImpl(this);
  }

  //*******************

   public double getInputDouble(int e, int i) {
     return getDouble(e, inputColumns[i]);
   }

  public double getOutputDouble(int e, int o) {
    return getDouble(e, outputColumns[o]);
  }

  public String getInputString(int e, int i) {
    return getString(e, inputColumns[i]);
  }

  public String getOutputString(int e, int o) {
    return getString(e, outputColumns[o]);
  }

  public int getInputInt(int e, int i) {
    return getInt(e, inputColumns[i]);
  }

  public int getOutputInt(int e, int o) {
    return getInt(e, outputColumns[o]);
  }

  public float getInputFloat(int e, int i) {
    return getFloat(e, inputColumns[i]);
  }

  public float getOutputFloat(int e, int o) {
    return getFloat(e, outputColumns[o]);
  }

  public short getInputShort(int e, int i) {
    return getShort(e, inputColumns[i]);
  }

  public short getOutputShort(int e, int o) {
    return getShort(e, outputColumns[o]);
  }

  public long getInputLong(int e, int i) {
    return getLong(e, inputColumns[i]);
  }

  public long getOutputLong(int e, int o) {
    return getLong(e, outputColumns[o]);
  }

  public byte getInputByte(int e, int i) {
    return getByte(e, inputColumns[i]);
  }

  public byte getOutputByte(int e, int o) {
    return getByte(e, outputColumns[o]);
  }

  public Object getInputObject(int e, int i) {
    return getObject(e, inputColumns[i]);
  }

  public Object getOutputObject(int e, int o) {
    return getObject(e, outputColumns[o]);
  }

  public char getInputChar(int e, int i) {
    return getChar(e, inputColumns[i]);
  }

  public char getOutputChar(int e, int o) {
    return getChar(e, outputColumns[o]);
  }

  public char[] getInputChars(int e, int i) {
    return getChars(e, inputColumns[i]);
  }

  public char[] getOutputChars(int e, int o) {
    return getChars(e, inputColumns[o]);
  }

  public byte[] getInputBytes(int e, int i) {
    return getBytes(e, inputColumns[i]);
  }

  public byte[] getOutputBytes(int e, int o) {
    return getBytes(e, outputColumns[o]);
  }

  public boolean getInputBoolean(int e, int i) {
    return getBoolean(e, inputColumns[i]);
  }

  public boolean getOutputBoolean(int e, int o) {
    return getBoolean(e, outputColumns[o]);
  }

  public int getNumInputs(int e) {
    return inputColumns.length;
  }

  public int getNumOutputs(int e) {
    return outputColumns.length;
  }

  public Example getExample(int i) {
    return null;
  }

  public String getInputName(int i) {
    return getColumnLabel(inputColumns[i]);
  }

  public String getOutputName(int o) {
    return getColumnLabel(outputColumns[o]);
  }

  public int getInputType(int i) {
    return getColumnType(inputColumns[i]);
  }

  public int getOutputType(int o) {
    return getColumnType(outputColumns[o]);
  }

  public boolean isInputNominal(int i) {
    return isColumnNominal(inputColumns[i]);
  }

  public boolean isOutputNominal(int o) {
    return isColumnNominal(outputColumns[o]);
  }

  public boolean isInputScalar(int i) {
    return isColumnScalar(inputColumns[i]);
  }

  public boolean isOutputScalar(int o) {
    return isColumnScalar(outputColumns[o]);
  }

  public String[] getInputNames() {
    return inputNames;
  }

  public String[] getOutputNames() {
    return outputNames;
  }

  // MutableTable support

  /**
   * Insert a new row into this Table, initialized with integer data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(int[] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with float data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(float[] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with double data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(double[] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with long data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(long[] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with short data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(short[] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with boolean data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(boolean[] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with String data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(String[] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with char[] data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(char[][] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with byte[] data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(byte[][] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with Object data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(Object[] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with byte data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(byte[] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new row into this Table, initialized with char data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(char[] newEntry, int position) {
    //insertTraining(trainSet[position]);
    incrementTrainTest(position);
    super.insertRow(newEntry, trainSet[position]);
  }

  /**
   * Insert a new column into this Table, initialized with integer data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(int[] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with float data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(float[] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with double data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(double[] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with long data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(long[] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with short data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(short[] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with boolean data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(boolean[] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with String data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(String[] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with char[] data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(char[][] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with byte[] data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(byte[][] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with Object data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(Object[] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with byte data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(byte[] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
   * Insert a new column into this Table, initialized with char data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(char[] newEntry, int position) {
    this.incrementInOut(position);
    super.insertColumn(newEntry, position);
  }

  /**
       Remove a column from the table.
       @param position the position of the Column to remove
   */
  public void removeColumn(int position) {
    decrementInOut(position);
    super.removeColumn(position);
  }

  /**
   Remove a range of columns from the table.
   @param start the start position of the range to remove
   @param len the number to remove-the length of the range
   */
  public void removeColumns(int start, int len) {
    for (int i = start; i < len; i++) {
      removeColumn(i);
    }
  }

  /**
   * Remove a row from this Table.
   * @param row the row to remove
   */
  public void removeRow(int row) {
    decrementTrainTest(row);
    super.removeRow(row);
  }

  /**
   Remove a range of rows from the table.
   @param start the start position of the range to remove
   @param len the number to remove-the length of the range
   */
  public void removeRows(int start, int len) {
    for (int i = start; i < len; i++) {
      removeRow(i);
    }
  }

  /**
   * Remove rows from this Table using a boolean map.
   * @param flags an array of booleans to map to this Table's rows.  Those
   * with a true will be removed, all others will not be removed
   */
  public void removeRowsByFlag(boolean[] flags) {
    int numRemoved = 0;
    for (int i = 0; i < flags.length; i++) {
      if (flags[i]) {
        removeRow(i - numRemoved);
      }
    }
  }

  /**
   * Remove rows from this Table using a boolean map.
   * @param flags an array of booleans to map to this Table's rows.  Those
   * with a true will be removed, all others will not be removed
   */
  public void removeColumnsByFlag(boolean[] flags) {
    int numRemoved = 0;
    for (int i = 0; i < flags.length; i++) {
      if (flags[i]) {
        removeColumn(i - numRemoved);
      }
    }
  }

  /**
   * Remove rows from this Table by index.
   * @param indices a list of the rows to remove
   */
  public void removeRowsByIndex(int[] indices) {
    for (int i = 0; i < indices.length; i++) {
      removeRow(indices[i] - i);
    }
  }

  /**
   * Remove rows from this Table by index.
   * @param indices a list of the rows to remove
   */
  public void removeColumnsByIndex(int[] indices) {
    for (int i = 0; i < indices.length; i++) {
      removeColumn(indices[i] - i);
    }
  }

  /**
   Get a copy of this Table reordered based on the input array of indexes.
   Does not overwrite this Table.
   @param newOrder an array of indices indicating a new order
   @return a copy of this column with the rows reordered
   */
  public Table reorderRows(int[] newOrder) {
    return super.reorderRows(newOrder);
  }

  /**
   Get a copy of this Table reordered based on the input array of indexes.
   Does not overwrite this Table.
   @param newOrder an array of indices indicating a new order
   @return a copy of this column with the rows reordered
   */
  public Table reorderColumns(int[] newOrder) {
    return super.reorderColumns(newOrder);
  }

  /**
   Swap the positions of two rows.
   @param pos1 the first row to swap
   @param pos2 the second row to swap
   */
  public void swapRows(int pos1, int pos2) {
    super.swapRows(pos1, pos2);
    this.swapTestTrain(pos1, pos2);
  }

  /**
   Swap the positions of two columns.
   @param pos1 the first column to swap
   @param pos2 the second column to swap
   */
  public void swapColumns(int pos1, int pos2) {
    super.swapColumns(pos1, pos2);
    this.swapInOut(pos1, pos2);
  }

  /**
   Set a specified element in the table.  If an element exists at this
       position already, it will be replaced.  If the position is beyond the capacity
   of this table then an ArrayIndexOutOfBounds will be thrown.
   @param element the new element to be set in the table
   @param row the row to be changed in the table
   @param column the Column to be set in the given row
    /
    public void setObject(Object element, int row, int column) {
         columns[column].setObject(element, trainSet[row]);
     }
     /**
    * Set an int value in the table.
    * @param data the value to set
    * @param row the row of the table
    * @param column the column of the table
          /
        public void setInt(int data, int row, int column) {
             columns[column].setInt(data, trainSet[row]);
         }
         /**
     * Set a short value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
              /
            public void setShort(short data, int row, int column) {
                 columns[column].setShort(data, trainSet[row]);
             }
             /**
      * Set a float value in the table.
      * @param data the value to set
      * @param row the row of the table
      * @param column the column of the table
                  /
                public void setFloat(float data, int row, int column) {
                     columns[column].setFloat(data, trainSet[row]);
                 }
                 /**
       * Set an double value in the table.
       * @param data the value to set
       * @param row the row of the table
       * @param column the column of the table
                      /
                    public void setDouble(double data, int row, int column) {
                         columns[column].setDouble(data, trainSet[row]);
                     }
                     /**
        * Set a long value in the table.
        * @param data the value to set
        * @param row the row of the table
        * @param column the column of the table
                          /
                        public void setLong(long data, int row, int column) {
                             columns[column].setLong(data, trainSet[row]);
                         }
                         /**
         * Set a String value in the table.
         * @param data the value to set
         * @param row the row of the table
         * @param column the column of the table
                              /
             public void setString(String data, int row, int column) {
             columns[column].setString(data, trainSet[row]);
                             }
                             /**
          * Set a byte[] value in the table.
          * @param data the value to set
          * @param row the row of the table
          * @param column the column of the table
                                  /
                     public void setBytes(byte[] data, int row, int column) {
              columns[column].setBytes(data, trainSet[row]);
                                 }
                                 /**
           * Set a boolean value in the table.
           * @param data the value to set
           * @param row the row of the table
           * @param column the column of the table
                                      /
               public void setBoolean(boolean data, int row, int column) {
                       columns[column].setBoolean(data, trainSet[row]);
                                     }
                                     /**
            * Set a char[] value in the table.
            * @param data the value to set
            * @param row the row of the table
            * @param column the column of the table
                                          /
                public void setChars(char[] data, int row, int column) {
                         columns[column].setChars(data, trainSet[row]);
                                         }
                                         /**
             * Set a byte value in the table.
             * @param data the value to set
             * @param row the row of the table
             * @param column the column of the table
                                              /
                 public void setByte(byte data, int row, int column) {
                           columns[column].setByte(data, trainSet[row]);
                                             }
                                             /**
              * Set a char value in the table.
              * @param data the value to set
              * @param row the row of the table
              * @param column the column of the table
                                                  /
                  public void setChar(char data, int row, int column) {
                             columns[column].setChar(data, trainSet[row]);
                                                 }
                                                /**
                   Set the name associated with a column.
                   @param label the new column label
                               @param position the index of the column to set
                                                    /
                   public void setColumnLabel(String label, int position);
                                                    /**
                                 Set the comment associated with a column.
                    @param comment the new column comment
                                 @param position the index of the column to set
                                                        /
                    public void setColumnComment(String comment, int position);
                                                         /**
                     Set the number of columns this Table can hold.
                     @param numColumns the number of columns this Table can hold
                 */
                public void setNumColumns(int numColumns) {
                  dropInOut(numColumns);
                  super.setNumColumns(numColumns);
                }

  /**
   Sort the specified column and rearrange the rows of the table to
   correspond to the sorted column.
   @param col the column to sort by
   /
     public void sortByColumn(int col)
     /**
        Sort the elements in this column starting with row 'begin' up to row 'end',
          @param col the index of the column to sort
        @param begin the row no. which marks the beginnig of the  column segment to be sorted
        @param end the row no. which marks the end of the column segment to be sorted
      /
        public void sortByColumn(int col, int begin, int end);
        /**
         Sets a new capacity for this Table.  The capacity is its potential
         maximum number of entries.  If numEntries is greater than newCapacity,
         then the Table may be truncated.
         @param newCapacity a new capacity
     */
    public void setNumRows(int newCapacity) {
      dropTestTrain(newCapacity);
      super.setNumRows(newCapacity);
    }

  /////////// Collect the transformations that were performed. /////////
  /**
   Add the transformation to the list.
   @param tm the Transformation that performed the reversable transform.
   /
       public void addTransformation (Transformation tm);
       /**
       Returns the list of all reversable transformations there were performed
       on the original dataset.
        @returns an ArrayList containing the Transformation which transformed the data.
       /
           public List getTransformations ();
        /**
     * Set the value at (row, col) to be missing or not missing.
     * @param b true if the value should be set as missing, false otherwise
     * @param row the row index
     * @param col the column index
             /
            public void setValueToMissing(boolean b, int row, int col) {
              columns[col].setValueToMissing(b, trainSet[row]);
               }
            /**
      * Set the value at (row, col) to be empty or not empty.
      * @param b true if the value should be set as empty, false otherwise
      * @param row the row index
      * @param col the column index
                 /
                public void setValueToEmpty(boolean b, int row, int col) {
                  columns[col].setValueToEmpty(b, trainSet[row]);
                   }

      /**
       * Increment all in and out indices greater than position
       */
      protected void incrementInOut(int position) {
        for (int i = 0; i < this.inputColumns.length; i++) {
          if (inputColumns[i] > position) {
            inputColumns[i]++;
          }
        }
        setInputFeatures(inputColumns);
        for (int i = 0; i < this.outputColumns.length; i++) {
          if (outputColumns[i] > position) {
            outputColumns[i]++;
          }
        }
        setOutputFeatures(outputColumns);
      }

  /**
   * Increment all test and train indices greater than position
   */
  protected void incrementTrainTest(int position) {
    for (int i = 0; i < this.trainSet.length; i++) {
      if (trainSet[i] > position) {
        trainSet[i]++;
      }
    }
    setTrainingSet(trainSet);
    for (int i = 0; i < this.testSet.length; i++) {
      if (testSet[i] > position) {
        testSet[i]++;
      }
    }
    setTestingSet(testSet);
  }

  /**
   * Decrement any items in test or train that are greater than position
   * Also remove position from either set if it exists
   * @param position
   */
  protected void decrementTrainTest(int position) {
    boolean containsPos = false;
    int idx = -1;
    for (int i = 0; i < testSet.length; i++) {
      if (testSet[i] == position) {
        containsPos = true;
        idx = i;
      }
      if (containsPos) {
        break;
      }
    }

    // if the test set contained pos, remove the item
    if (containsPos) {
      int[] newtest = new int[testSet.length - 1];
      int idd = 0;
      for (int i = 0; i < testSet.length; i++) {
        if (i != idx) {
          newtest[idd] = testSet[i];
          idd++;
        }
      }
      setTestingSet(newtest);
    }

    containsPos = false;
    idx = -1;

    for (int i = 0; i < trainSet.length; i++) {
      if (trainSet[i] == position) {
        containsPos = true;
        idx = i;
      }
      if (containsPos) {
        break;
      }
    }

    // if the test set contained pos, remove the item
    if (containsPos) {
      int[] newttrain = new int[trainSet.length - 1];
      int idd = 0;
      for (int i = 0; i < trainSet.length; i++) {
        if (i != idx) {
          newttrain[idd] = trainSet[i];
          idd++;
        }
      }
      setTrainingSet(newttrain);
    }
  }

  /**
   * Decrement any items in test or train that are greater than position
   * Also remove position from either set if it exists
   * @param position
   */
  protected void decrementInOut(int position) {
    boolean containsPos = false;
    int idx = -1;
    for (int i = 0; i < inputColumns.length; i++) {
      if (inputColumns[i] == position) {
        containsPos = true;
        idx = i;
      }
      if (containsPos) {
        break;
      }
    }

    // if the test set contained pos, remove the item
    if (containsPos) {
      int[] newin = new int[inputColumns.length - 1];
      int idd = 0;
      for (int i = 0; i < inputColumns.length; i++) {
        if (i != idx) {
          newin[idd] = inputColumns[i];
          idd++;
        }
      }
      setInputFeatures(newin);
    }

    containsPos = false;
    idx = -1;

    for (int i = 0; i < outputColumns.length; i++) {
      if (outputColumns[i] == position) {
        containsPos = true;
        idx = i;
      }
      if (containsPos) {
        break;
      }
    }

    // if the test set contained pos, remove the item
    if (containsPos) {
      int[] newout = new int[outputColumns.length - 1];
      int idd = 0;
      for (int i = 0; i < outputColumns.length; i++) {
        if (i != idx) {
          newout[idd] = outputColumns[i];
          idd++;
        }
      }
      setOutputFeatures(newout);
    }
  }

  /**
   * For every p1 in test/train, put in p2.
   * For every p2 in test/train, put in p1.
   */
  protected void swapTestTrain(int p1, int p2) {
    for (int i = 0; i < trainSet.length; i++) {
      if (trainSet[i] == p1) {
        trainSet[i] = p2;
      }
      else if (trainSet[i] == p2) {
        trainSet[i] = p1;
      }
    }
    for (int i = 0; i < testSet.length; i++) {
      if (testSet[i] == p1) {
        testSet[i] = p2;
      }
      else if (testSet[i] == p2) {
        testSet[i] = p1;
      }
    }
  }

  /**
   * For every p1 in test/train, put in p2.
   * For every p2 in test/train, put in p1.
   */
  protected void swapInOut(int p1, int p2) {
    for (int i = 0; i < inputColumns.length; i++) {
      if (inputColumns[i] == p1) {
        inputColumns[i] = p2;
      }
      else if (inputColumns[i] == p2) {
        inputColumns[i] = p1;
      }
    }
    for (int i = 0; i < outputColumns.length; i++) {
      if (outputColumns[i] == p1) {
        outputColumns[i] = p2;
      }
      else if (outputColumns[i] == p2) {
        outputColumns[i] = p1;
      }
    }
  }

  /**
   * Drop any input/output columns greater than pos
   */
  protected void dropInOut(int pos) {
    int numOk = 0;
    for (int i = 0; i < inputColumns.length; i++) {
      if (inputColumns[i] < pos) {
        numOk++;
      }
    }
    if (numOk != inputColumns.length) {
      int[] newin = new int[numOk];
      int idx = 0;
      for (int i = 0; i < inputColumns.length; i++) {
        int num = inputColumns[i];
        if (num < pos) {
          newin[idx] = num;
          idx++;
        }
      }
      setInputFeatures(newin);
    }

    numOk = 0;
    for (int i = 0; i < outputColumns.length; i++) {
      if (outputColumns[i] < pos) {
        numOk++;
      }
    }
    if (numOk != outputColumns.length) {
      int[] newout = new int[numOk];
      int idx = 0;
      for (int i = 0; i < outputColumns.length; i++) {
        int num = outputColumns[i];
        if (num < pos) {
          newout[idx] = num;
          idx++;
        }
      }
      setOutputFeatures(newout);
    }
  }

  /**
   * Drop any input/output columns greater than pos
   */
  protected void dropTestTrain(int pos) {
    int numOk = 0;
    for (int i = 0; i < testSet.length; i++) {
      if (testSet[i] < pos) {
        numOk++;
      }
    }
    if (numOk != testSet.length) {
      int[] newtest = new int[numOk];
      int idx = 0;
      for (int i = 0; i < testSet.length; i++) {
        int num = testSet[i];
        if (num < pos) {
          newtest[idx] = num;
          idx++;
        }
      }
      setTestingSet(newtest);
    }

    numOk = 0;
    for (int i = 0; i < trainSet.length; i++) {
      if (trainSet[i] < pos) {
        numOk++;
      }
    }
    if (numOk != trainSet.length) {
      int[] newtrain = new int[numOk];
      int idx = 0;
      for (int i = 0; i < trainSet.length; i++) {
        int num = trainSet[i];
        if (num < pos) {
          newtrain[idx] = num;
          idx++;
        }
      }
      setTrainingSet(newtrain);
    }
  }
}