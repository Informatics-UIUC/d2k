package ncsa.d2k.modules.core.datatype.table.sparse;

//import ncsa.d2k.modules.projects.vered.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
//import ncsa.d2k.modules.projects.vered.sparse.example.SparseExample;
import ncsa.d2k.modules.core.datatype.table.sparse.examples.SparseExample;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.TestTable;
import ncsa.d2k.modules.core.datatype.table.TrainTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.Example;
import ncsa.d2k.modules.core.datatype.table.TableFactory;
import java.io.*;
import java.util.ArrayList;
/**
 * SparseExampleTable is identical to SparseTable with a few addtions:
 * Sparse Example Table holds 4 sets of integers, 2 of which define which are the
 * input columns and which are the output columns. the other 2 define which are
 * the test rows and which are the train rows.
 * A row in an ExampleTable is an example.
 */

public class SparseExampleTable extends SparseTable implements ExampleTable {


   /** the indicies of the records in the various training sets. */
    protected int[] trainSet;

    /** the indicies of the records in the various test sets. */
    protected int[] testSet;

    /**the indicies of the attributes that are inputs (to the model). */
    protected int[] inputColumns;

    /**the indicies of the attributes that are inputs (to the model). */
    protected int[] outputColumns;


/* Creates an empty table */
  public SparseExampleTable() {
    super();
    inputColumns = null;
    outputColumns = null;
    testSet =  null;
    trainSet =  null;
  }

/* Creates an empty table with the specified size */
   public SparseExampleTable(int numRows, int numColumns){
    super(numRows, numColumns);
     inputColumns = null;
    outputColumns = null;
    testSet =  null;
    trainSet =  null;

   }

   /* Instantiate this table with the content of <codE>table</code>*/
  public SparseExampleTable(SparseTable table){
    super(table);
    if(table instanceof SparseExampleTable){
      copyArrays((SparseExampleTable)table);
    }
  }

  protected void copyArrays(SparseExampleTable table){
      inputColumns = copyArray(table.inputColumns);
      outputColumns = copyArray(table.outputColumns);
        testSet = copyArray(table.testSet);
      trainSet = copyArray(table.trainSet);
    }

    protected int[] copyArray(int[] arr){
       int[] retVal = new int[arr.length];
      System.arraycopy(arr, 0, retVal, 0, retVal.length);
      return retVal;
    }

    /**
     * returns an int array containing indices of input columns of this table.
     *
     * @return  int array with indices of columns that hold the input features
     *          of this example table
     */
  public int[] getInputFeatures() {
       return inputColumns;
  }

   /**
   * returns the valid input indices of row no. <codE>row</code> in a
   * sorted array.
   *
   * @param row      the row number to retrieve its input features.
   * @return            a sorted integer array with the valid input indices
   *                  of row no. <codE>row</code>.
   */

  public int[] getInputFeatures(int row) {
    int[] retVal = new int[0];
    if(!rows.containsKey(row))
      return retVal;
    VIntHashSet temp =   (VIntHashSet) rows.get(row);
    int[] tempArr = new int[temp.size()];
    int j=0;
    for(int i=0; i<inputColumns.length; i++)
      if(temp.contains(inputColumns[i])){
	tempArr[j] = inputColumns[i];
	j++;
      }
     retVal = new int [j];
    System.arraycopy(tempArr, 0, retVal, 0, j);
    return retVal;

  }

  /**
   * Returns the total number of input columns.
   *
   * @retuen     the total number of input features.
   */
  public int getNumInputFeatures() {
    if (inputColumns == null) return 0;
    return inputColumns.length;
  }


  /**
   * Returns the total number of examples in this table, meaning the total number
   * of rows.
   * @retuen     the total number of examples.
   */
  public int getNumExamples() {
    return getNumRows();
  }

  /**
   * Returns the number of rows that are training examples.
   * @return    number of rows in this table that serve as training examples.
   */
  public int getNumTrainExamples() {
    if (trainSet == null) return 0;
      return trainSet.length;
  }

   /**
   * Returns the number of rows that are testing examples.
   * @return    number of rows in this table that serve as testing examples.
   */
  public int getNumTestExamples() {
     if (testSet == null) return 0;
      return testSet.length;
  }

  /**
   * returns an int array containing indices of output features of this table.
   *
   * @return  int array with indices of columns that hold the output features
   *          of this example table
   */
  public int[] getOutputFeatures() {

    return outputColumns;
  }



  /**
   * Returns the total number of output features.
   * @retuen     the total number of output features.
   */
  public int getNumOutputFeatures() {
    if(outputColumns == null) return 0;
    return outputColumns.length;
  }

  /**
   * Sets the input feature to be as specified by <code> inputs</code>
   * @param inputs    an int array holding valid indices of columns in this table
   *                  to be the input columns.
   */
  public void setInputFeatures(int[] inputs) {
    inputColumns = inputs;
  }



  /**
   * Sets the output feature to be as specified by <code> outs</code>
   * @param outs    an int array holding valid indices of columns in this table
   *                  to be the output columns.
   */
  public void setOutputFeatures(int[] outs) {

    outputColumns = outs;
  }


  /**
   * Sets the training set of indices to be as specified by <code>trainingSet</code>
   * @param trainingSet    an int array holding valid indices of rows in this table
   *                        to be the training examples.
   */
  public void setTrainingSet(int[] trainingSet) {

    trainSet = trainingSet;
  }

  /**
   * Returns an int array holding the indices of rows that serve as training
   * examples.
   *
   * @return    int array holding the indices of rows that serve as training
   *            examples.
   */
  public int[] getTrainingSet() {
    return trainSet;
  }



  /**
   * Sets the testing set of indices to be as specified by <code>testingSet</code>
   * @param testingSet    an int array holding valid indices of rows in this table
   *                        to be the testing examples.
   */
  public void setTestingSet(int[] testingSet) {
    testSet = testingSet;
  }

  /**
   * Returns an int array holding the indices of rows that serve as testing
   * examples.
   *
   * @return    int array holding the indices of rows that serve as testing
   *            examples.
   */
  public int[] getTestingSet() {
    return testSet;
  }


  /**
   * Returns a SparseTestTable with the content of this table
   */
  public TestTable getTestTable() {
    return new SparseTestTable(this);
  }



  /**
   * Returns a SparseTrainTable with the content of this table
   */
  public TrainTable getTrainTable() {
    return new SparseTrainTable(this);
  }



  /**
   * Returns a SparsePredictionTable with the content of this table
   */
  public PredictionTable toPredictionTable() {
     return new SparsePredictionTable(this);
  }


  /**
   * Copies the content of <code>srcTable</code> into this table
   *
   */
  protected void copy(SparseTable srcTable){
    if(srcTable instanceof SparseExampleTable)
      copyArrays((SparseExampleTable)srcTable);

    super.copy(srcTable);
  }


  /**
   * Returns a deep copy of this column.
   *
   * @return a deep copy of this SparseExampleTable
   */
  public Table copy() {
    SparseExampleTable retVal;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            retVal = (SparseExampleTable)ois.readObject();
            ois.close();
            return  retVal;
        } catch (Exception e) {
            retVal = new SparseExampleTable(this);

            return  retVal;
        }
  }





  /**
   * Returns a SparseExampleTable containing rows no. <code>start</code> through
   * </codE>start+len</code> from this table.
   *
   * @param start     row number at which the subset starts.
   * @param len       number of consequentinve rows in the retrieved subset.
   * @return          SparseExampleTable with data from rows no. <code>start</code>
   *                  through </codE>start+len</code> from this table.
   */
   public Table getSubset(int start, int len){
    SparseExampleTable retVal = (SparseExampleTable)((SparseMutableTable)SparseMutableTable.getSubset(start, len, this)).toExampleTable();

    int[] indices = new int[len];
    for (int i=0; i<len; i++)
      indices[i] = start + i;

      //getting sub set sof the testing and training sets
    retVal.getSubArrays(this, start, len);

    //copying hte input and output columns
     retVal.inputColumns = this.copyArray(inputColumns);

      retVal.outputColumns = this.copyArray(outputColumns);

    return retVal;
   }

   private void getSubArrays(SparseExampleTable srcTable, int start, int len){

       testSet = getSubArray(srcTable.testSet, start, len);
       trainSet = getSubArray(srcTable.trainSet, start, len);

   }

   private int[] getSubArray(int[] arr, int start, int len){
      int[] tempSet = new int[len];
      int j=0;
      for (int i=0; i<arr.length; i++){
	if (arr[i] >= start && arr[i] <= start + len){
	  tempSet[j] = arr[i];
	  j++;
	}//if
      }//for i

      int[] retVal = new int[j];
      System.arraycopy(tempSet, 0, retVal, 0, j);
      return retVal;

   }


    /**
   * Returns a TestTable or a TrainTable with data from row index no. <code>
   * start</code> in the test/train set through row index no. <code>start+len</code>
   * in the test/train set.
   *
   * @param start       index number into the test/train set of the row at which begins
   *                    the subset.
   * @param len         number of consequetive rows to include in the subset.
   * @param test        if true - the returned value is a TestTable. else - the
   *                    returned value is a TrainTable
   * @return            a TestTable (if <code>test</code> is true) or a TrainTable
   *                    (if <code>test</code> is false) with data from row index no.
   *                    <code>start</code> in the test/train set through row index
   *                    no. <code>start+len</code> in the test/train set.
   */
  protected Table getSubset(int start, int len, boolean test) {

    //initializing the returned value
    SparseExampleTable retVal;
    if(test)      retVal = new SparseTestTable();
    else          retVal = new SparseTrainTable();

    //retrieving the test rows indices
    int[] rowIdx;
    if(test)      rowIdx = testSet;
    else          rowIdx = trainSet;

    //if the start index is not in the range of the test rows
    //or if len is zero - return an empty table
    if(start >= rowIdx.length || len == 0)
      return retVal;

      //calculating the true rows dimension of the returned table
    //int size = len;
    if(start + len > getNumRows())
      len = getNumRows() - start;


    //retrieving the indices to include in the sub set.
    int[] indices = new int[len];
    for (int i=0; i<len; i++)
      indices[i] = rowIdx[start + i];

      //retrieving a subset from the example part of this table
     SparseMutableTable tempTbl = (SparseMutableTable) SparseMutableTable.getSubset(indices, this);

     //getting a test table from tempTbl. setting the test set.
     //getting a subset from the prediction columns
      if(test){
	retVal = (SparseTestTable) tempTbl.toExampleTable().getTestTable();
	((SparseTestTable)retVal).predictionColumns = (SparseMutableTable)
	    SparseMutableTable.getSubset(indices, ((SparseTestTable)this).predictionColumns);
	retVal.setTestingSet(indices);
      }
      //getting a train table from tempTbl. setting the train set.
      else{
	retVal = (SparseTrainTable) tempTbl.toExampleTable().getTrainTable();
	retVal.setTrainingSet(indices);

      }

      //setting input and output feature
      retVal.setInputFeatures(getInputFeatures());
      retVal.setOutputFeatures(outputColumns);

      return retVal;
  }


   /**
    * Returns a reference to this table.
    */
   public ExampleTable toExampleTable(){
    return this;
   }


   /**
    * ***********************************
    * GET INPUT TYPE METHODS
    * ***********************************
    */



    /**
    * Returns a boolean representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getBoolean(row, getInputFeatures()[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       a boolean representation of the value at the specified location.
    */
   public boolean getInputBoolean(int row, int index){
      return getBoolean(row, inputColumns[index]);
   }

    /**
    * Returns a byte representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getByte(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       a byte representation of the value at the specified location.
    */
   public byte getInputByte(int row, int index){
      return getByte(row, inputColumns[index]);
   }

   /**
    * Returns a char representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getChar(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       a char representation of the value at the specified location.
    */
   public char getInputChar(int row, int index){
      return getChar(row, inputColumns[index]);
   }



    /**
    * Returns a byte array representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getBytes(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       a byte array representation of the value at the specified location.
    */
   public byte[] getInputBytes(int row, int index){
      return getBytes(row, inputColumns[index]);
   }

   /**
    * Returns a char array representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getChars(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       a char array representation of the value at the specified location.
    */
   public char[] getInputChars(int row, int index){
      return getChars(row, inputColumns[index]);
   }

   /**
    * Returns a double representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getDouble(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       a double representation of the value at the specified location.
    */
   public double getInputDouble(int row, int index){
      return getDouble(row, inputColumns[index]);
   }



    /**
    * Returns an int representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getInt(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       an int representation of the value at the specified location.
    */
   public int getInputInt(int row, int index){
      return getInt(row, inputColumns[index]);
   }

    /**
    * Returns a long representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getLong(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       a long representation of the value at the specified location.
    */
   public long getInputLong(int row, int index){
      return getLong(row, inputColumns[index]);
   }



    /**
    * Returns a float representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getFloat(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       a float representation of the value at the specified location.
    */
   public float getInputFloat(int row, int index){
      return getFloat(row, inputColumns[index]);
   }


   /**
    * Returns a short  representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getShort(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       a short representation of the value at the specified location.
    */
   public short getInputShort(int row, int index){
      return getShort(row, inputColumns[index]);
   }



    /**
    * Returns an Object representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getObject(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       an Objectrepresentation of the value at the specified location.
    */
   public Object getInputObject(int row, int index){
      return getObject(row, inputColumns[index]);
   }

   /**
    * Returns a String  representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the input set.
    * This method is the same as <code>getString(row, inputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the input set, indicating the column from which
    *               the data is retrived.
    * @return       a String representation of the value at the specified location.
    */
   public String getInputString(int row, int index){
      return getString(row, inputColumns[index]);
   }


   /*
    * ********************************************
    * GET OUTPUT TYPES
    * *******************************************
    */



    /**
    * Returns a float representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getFloat(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       a float representation of the value at the specified location.
    */
   public float getOutputFloat(int row, int index){
      return getFloat(row, outputColumns[index]);
   }



       /**
    * Returns a boolean representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getBoolean(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       a boolean representation of the value at the specified location.
    */
   public boolean getOutputBoolean(int row, int index){
      return getBoolean(row, outputColumns[index]);
   }

    /**
    * Returns a byte representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getByte(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       a byte representation of the value at the specified location.
    */
   public byte getOutputByte(int row, int index){
      return getByte(row, outputColumns[index]);
   }

   /**
    * Returns a char representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getChar(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       a char representation of the value at the specified location.
    */
   public char getOutputChar(int row, int index){
      return getChar(row, outputColumns[index]);
   }



    /**
    * Returns a byte array representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getBytes(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       a byte array representation of the value at the specified location.
    */
   public byte[] getOutputBytes(int row, int index){
      return getBytes(row, outputColumns[index]);
   }

   /**
    * Returns a char array representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getChars(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       a char array representation of the value at the specified location.
    */
   public char[] getOutputChars(int row, int index){
      return getChars(row, outputColumns[index]);
   }

   /**
    * Returns a double representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getDouble(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       a double representation of the value at the specified location.
    */
   public double getOutputDouble(int row, int index){
      return getDouble(row, outputColumns[index]);
   }



    /**
    * Returns an int representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getInt(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       an int representation of the value at the specified location.
    */
   public int getOutputInt(int row, int index){
      return getInt(row, outputColumns[index]);
   }

    /**
    * Returns a long representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getLong(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       a long representation of the value at the specified location.
    */
   public long getOutputLong(int row, int index){
      return getByte(row, outputColumns[index]);
   }

   /**
    * Returns a short  representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getShort(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       a short representation of the value at the specified location.
    */
   public short getOutputShort(int row, int index){
      return getShort(row, outputColumns[index]);
   }



    /**
    * Returns an Object representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getObject(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       an Objectrepresentation of the value at the specified location.
    */
   public Object getOutputObject(int row, int index){
      return getObject(row, outputColumns[index]);
   }

   /**
    * Returns a String  representation of the data at row no. <codE>row</code>
    * and column with index no. <codE>index</code> into the output set.
    * This method is the same as <code>getString(row, outputColumns[index])</code>.
    *
    * @param row    the row number from which to retrieve the data.
    * @param index  an index into the output set, indicating the column from which
    *               the data is retrived.
    * @return       a String representation of the value at the specified location.
    */
   public String getOutputString(int row, int index){
      return getString(row, outputColumns[index]);
   }


   /**
    * *****************************
    * General Get Methods
    * *****************************
    */

    /**
     * @return number of input columns
     */
   public int getNumInputs(){
    if(inputColumns == null)
      return 0;
    return inputColumns.length;
    }


    /**
     * @return number of output columns
     */
   public int getNumOutputs()  {
    if(outputColumns == null)
      return 0;
    return outputColumns.length;
  }


   /**
    * Returns the label of the column with index <code>inputIndex</codE> into the
    * input set.
    * This method is the same as getColumnLabel(inputColumns[inputIndex])
    *
    * @param inputIndex    an index into the input set.
    * @return             the label of the column associated with index
    *                     <codE>inputIndex</code>.
    */
   public String getInputName(int inputIndex){
    return getColumnLabel(inputColumns[inputIndex]);
   }



   /**
    * Returns the label of the column with index <code>outputIndex</codE> into the
    * output set.
    * This method is the same as getColumnLabel(outputColumns[outputIndex])
    *
    * @param outputIndex    an index into the output set.
    * @return         the label of the column associated with index <codE>outputIndex</code>.
    */
    public String getOutputName(int outputIndex){
      return getColumnLabel(outputColumns[outputIndex]);
    }

    /**
     * Returns true if the column associated with index <codE>inputIndex</code>
     * into the input set is nominal. otherwise returns false.
     * This method is the same as isColumnNominal(inputColumns[inputIndex])
     *
     * @param inputIndex    an index into the intput set.
     * @return              true if the column associated with index <codE>inputIndex
     *                      </code> into the input set is nominal. otherwise
     *                      returns false.
     */
    public boolean isInputNominal(int inputIndex){
      return isColumnNominal(inputColumns[inputIndex]);
    }

     /**
     * Returns true if the column associated with index <codE>inputIndex</code>
     * into the input set is scalar. otherwise returns false.
     * This method is the same as isColumnScalar(inputColumns[inputIndex])
     *
     * @param inputIndex    an index into the intput set.
     * @return              true if the column associated with index <codE>inputIndex
     *                      </code> into the input set is scalar. otherwise
     *                      returns false.
     */
      public boolean isInputScalar(int inputIndex){
	return  isColumnScalar(inputColumns[inputIndex]);
      }



    /**
     * Returns an integer representing the type of the column associated with index
     * <codE>inputIndex</code> into the input set.
     * This method is the same as getColumnType(inputColumns[inputIndex])
     *
     * @param inputIndex    an index into the intput set.
     * @return              an integer representing the type of the column associated
     *                      with index <codE>inputIndex</code> into the input set.
     */
     public int getInputType(int inputIndex) {
      return getColumnType(inputColumns[inputIndex]);
     }

    /**
     * Returns true if the column associated with index <codE>outputIndex</code>
     * into the output set is nominal. otherwise returns false.
     * This method is the same as isColumnNominal(outputColumns[outputIndex])
     *
     * @param outputIndex    an index into the intput set.
     * @return              true if the column associated with index <codE>outputIndex
     *                      </code> into the input set is nominal. otherwise
     *                      returns false.
     */
    public boolean isOutputNominal(int outputIndex){
      return isColumnNominal(outputColumns[outputIndex]);
    }



    /**
     * Returns true if the column associated with index <codE>outputIndex</code>
     * into the output set is scalar. otherwise returns false.
     * This method is the same as isColumnScalar(outputColumns[outputIndex])
     *
     * @param outputIndex    an index into the intput set.
     * @return              true if the column associated with index <codE>outputIndex
     *                      </code> into the input set is scalar. otherwise
     *                      returns false.
     */
    public boolean isOutputScalar(int outputIndex){
      return isColumnScalar(outputColumns[outputIndex]);
    }

    /**
     * Returns an integer representing the type of the column associated with index
     * <codE>outputIndex</code> into the output set.
     * This method is the same as getColumnType(outputColumns[outputIndex])
     *
     * @param outputIndex    an index into the intput set.
     * @return              an integer representing the type of the column associated
     *                      with index <codE>outputIndex</code> into the output set.
     */
     public int getOutputType(int outputIndex) {
      return getColumnType(outputColumns[outputIndex]);
     }


    /**
     * Returns a single row SparseExampleTable, containing data from row
     * no. <codE>i</code>.
     */
      public Example getExample(int i) {
	return new SparseExample(this, i);
      }






      /**
       * returns the valid output indices of row no. <codE>row</code> in a
       * sorted array.
       *
       * @param row      the row number to retrieve its output features.
       * @return            a sorted integer array with the valid output indices
       *                  of row no. <codE>row</code>.
       */
      public int[] getOutputFeatures(int row){
	 int[] retVal = new int[0];
	  if(!rows.containsKey(row))
	    return retVal;

	  VIntHashSet temp =   (VIntHashSet) rows.get(row);
	  int[] tempArr  = new int[outputColumns.length];
	  int j=0;
	  for(int i=0; i<outputColumns.length; i++)
	    if(temp.contains(outputColumns[i])){
	      tempArr[j] = outputColumns[i];
	      j++;
	    }

	   retVal = new int [j];
	  System.arraycopy(tempArr, 0, retVal, 0, j);

	  return retVal;

      }

    /**
     * Returns number of output columns in row no. <code>row</code>
     *
     * @param row     row index
     * @return        number of output columns in row no. <code>row</code>
     */
    public int getNumOutputs(int row){
      return getOutputFeatures(row).length;
    }


    /**
     * Returns number of input columns in row no. <code>row</code>
     *
     * @param row     row index
     * @return        number of input columns in row no. <code>row</code>
     */
    public int getNumInputs(int row){
      return getInputFeatures(row).length;
    }




}