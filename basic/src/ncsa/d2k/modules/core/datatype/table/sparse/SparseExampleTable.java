package ncsa.d2k.modules.core.datatype.table.sparse;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.sparse.examples.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

//==============
// Java Imports
//==============

import java.io.*;
import java.util.ArrayList;
import java.util.*;

/**
 * SparseExampleTable is identical to SparseTable with a few addtions:
 * Sparse Example Table holds 4 sets of integers, 2 of which define which are
 * the input columns and which are the output columns. the other 2 define which
 * are the test rows and which are the train rows.
 * A row in an ExampleTable is an example.
 */

public class SparseExampleTable
    extends SparseSubsetTable
    implements ExampleTable {

  //==============
  // Data Members
  //==============

  /** the indicies of the records in the various training sets. */
  protected int[] trainSet = new int[0];

  /** the indicies of the records in the various test sets. */
  protected int[] testSet = new int[0];

  /**the indicies of the attributes that are inputs (to the model). */
  protected int[] inputColumns = new int[0];

  /**the indicies of the attributes that are inputs (to the model). */
  protected int[] outputColumns = new int[0];

  protected String[] outputNames = new String[0];
  protected String[] inputNames = new String[0];

  //================
  // Constructor(s)
  //================

  /* Creates an empty table */
  public SparseExampleTable() {
    super();
//    inputColumns = null;
//    outputColumns = null;
//    testSet = null;
//    trainSet = null;
  }

//  /* Creates an empty table with the specified size */
//  public SparseExampleTable(int numRows, int numColumns) {
//    super(numRows, numColumns);
//    inputColumns = null;
//    outputColumns = null;
//    testSet = null;
//    trainSet = null;
//  }

  /* Instantiate this table with the content of <codE>table</code>*/
  public SparseExampleTable(SparseTable table) {
    super(table);
    if (table instanceof SparseExampleTable) {
      copyArrays( (SparseExampleTable) table);
    }
    else {
//      inputColumns = null;
//      outputColumns = null;
//      testSet = null;
//      trainSet = null;
    }
  }

  /* Instantiate this table with the content of <codE>table</code>*/
  public SparseExampleTable(int numcols) {
    super(numcols);
//    inputColumns = null;
//    outputColumns = null;
//    testSet = null;
//    trainSet = null;
  }

  /* Instantiate this table with the content of <codE>table</code>*/
  public SparseExampleTable(Column[] cols) {
    super(cols);
//    inputColumns = null;
//    outputColumns = null;
//    testSet = null;
//    trainSet = null;
  }

  /* Instantiate this table with the content of <codE>table</code>*/
  public SparseExampleTable(SparseTable table, int[] ss) {
    super(table, ss);
    if (table instanceof SparseExampleTable) {
      copyArrays( (SparseExampleTable) table);
    }
    else {
//      inputColumns = null;
//      outputColumns = null;
//      testSet = null;
//      trainSet = null;
    }
  }

  /* Instantiate this table with the content of <codE>table</code>*/
  public SparseExampleTable(Column[] cols, int[] ss) {
    super(cols, ss);
//    inputColumns = null;
//    outputColumns = null;
//    testSet = null;
//    trainSet = null;
  }

  //===================================================================
  //===================================================================

  protected void copyArrays(SparseExampleTable table) {
    if (table.inputColumns != null) {
      inputColumns = copyArray(table.inputColumns);
    }
    if (table.outputColumns != null) {
      outputColumns = copyArray(table.outputColumns);
    }
    if (table.testSet != null) {
      testSet = copyArray(table.testSet);
    }
    if (table.trainSet != null) {
      trainSet = copyArray(table.trainSet);
    }
  }

  protected int[] copyArray(int[] arr) {
    int[] retVal = new int[arr.length];
    System.arraycopy(arr, 0, retVal, 0, retVal.length);
    return retVal;
  }

  //=========================================================================
  //                      Example Table Interface
  //=========================================================================

  /**
   * Returns a SparsePredictionTable with the content of this table
   */
  public PredictionTable toPredictionTable() {
    return new SparsePredictionTable(this);
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
   * Returns the total number of input columns.
   *
   * @retuen     the total number of input features.
   */
  public int getNumInputFeatures() {
    if (inputColumns == null) {
      return 0;
    }
    return inputColumns.length;
  }

  /**
   * Returns the number of rows that are training examples.
   * @return    number of rows in this table that serve as training examples.
   */
  public int getNumTrainExamples() {
    if (trainSet == null) {
      return 0;
    }
    return trainSet.length;
  }

  /**
   * Returns the number of rows that are testing examples.
   * @return    number of rows in this table that serve as testing examples.
   */
  public int getNumTestExamples() {
    if (testSet == null) {
      return 0;
    }
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
    if (outputColumns == null) {
      return 0;
    }
    return outputColumns.length;
  }

  /**
   * Sets the input feature to be as specified by <code> inputs</code>
       * @param inputs    an int array holding valid indices of columns in this table
   *                  to be the input columns.
   */
  public void setInputFeatures(int[] inputs) {
    inputColumns = inputs;
    inputNames = new String[inputs.length];
    for (int i = 0; i < inputNames.length; i++) {
      inputNames[i] = getColumnLabel(inputs[i]);

    }
  }

  /**
   * Sets the output feature to be as specified by <code> outs</code>
   * @param outs    an int array holding valid indices of columns in this table
   *                  to be the output columns.
   */
  public void setOutputFeatures(int[] outs) {

    outputColumns = outs;
    outputNames = new String[outs.length];
    for (int i = 0; i < outputNames.length; i++) {
      outputNames[i] = getColumnLabel(outs[i]);

    }
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
   * Decrement any items in test or train that are greater than position
   * Also remove position from either set if it exists
   * @param position
   */
  protected void decrementInOut(int position) {
    boolean containsPos = false;
    int idx = -1;

    if (inputColumns != null) {
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

      //adjust input/output array values to account for column indice shifts -- DDS
      int[] newin = this.getInputFeatures();
      for (int i = 0, n = newin.length; i < n; i++) {
        if (newin[i] > position) {
          newin[i]--;
        }
      }
      this.setInputFeatures(newin);
    }

    containsPos = false;
    idx = -1;

    if (outputColumns != null) {

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

      //adjust input/output array values to account for column indice shifts -- DDS
      int[] newout = this.getOutputFeatures();
      for (int i = 0, n = newout.length; i < n; i++) {
        if (newout[i] > position) {
          newout[i]--;
        }
      }
      this.setOutputFeatures(newout);
    }

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
    for (int i = 0; i < len; i++) {
      removeColumn(start);
    }
  }

  /**
   * Returns a SparseExampleTable containing rows no. <code>start</code>
   * through </codE>start+len</code> from this table.
   *
   * @param start row number at which the subset starts.
   * @param len   number of consequentinve rows in the retrieved subset.
   * @return      SparseExampleTable with data from rows no.
   * 			  <code>start</code> through </codE>start+len</code>
   * 			  from this table.
   */
  public Table getSubset(int pos, int len) {
    SparseExampleTable eti = (SparseExampleTable)this.shallowCopy();
    int[] sample = new int[len];
    for (int i = 0; i < len; i++) {
      sample[i] = subset[pos + i];
    }
    eti.setSubset(sample);
    return eti;

//    SparseExampleTable retVal = (SparseExampleTable) ( (SparseMutableTable)
//        SparseMutableTable.getSubset(start, len, this)).toExampleTable();
//
//    // copy the training and testing sets
//    retVal.getSubArrays(this, start, len);
//
//    // copy the input and output columns
//    retVal.inputColumns = this.copyArray(inputColumns);
//    retVal.outputColumns = this.copyArray(outputColumns);
//
//    return retVal;
  }

  public Table getSubset(int[] rows) {
    SparseExampleTable eti = (SparseExampleTable)this.shallowCopy();
    for (int i = 0; i < rows.length; i++) {
      rows[i] = subset[rows[i]];
    }
    eti.setSubset(rows);
    return eti;

//    SparseExampleTable retVal = (SparseExampleTable)
//        ( (SparseMutableTable) SparseMutableTable.getSubset(rows, this)).
//        toExampleTable();
//
//    // copy the training and testing sets
//    retVal.getSubArrays(this, rows);
//
//    // copy the input and output columns
//    retVal.inputColumns = this.copyArray(inputColumns);
//    retVal.outputColumns = this.copyArray(outputColumns);
//
//    return retVal;
  }

//  protected void getSubArrays(SparseExampleTable srcTable, int start, int len) {
//    testSet = getSubArray(srcTable.testSet, start, len);
//    trainSet = getSubArray(srcTable.trainSet, start, len);
//  }
//
//  protected void getSubArrays(SparseExampleTable srcTable, int[] rows) {
//    testSet = getSubArray(srcTable.testSet, rows);
//    trainSet = getSubArray(srcTable.trainSet, rows);
//  }

//  protected int[] getSubArray(int[] arr, int start, int len) {
//
//    // Xiaolei
//    if (arr == null) {
//      return new int[0];
//    }
//
//    int[] tempSet = new int[len];
//    int j = 0;
//    for (int i = 0; i < arr.length; i++) {
//      //if (arr[i] >= start && arr[i] <= start + len) {
//      //XIAOLEI fixed <= to <
//      if (arr[i] >= start && arr[i] < start + len) {
//        tempSet[j] = arr[i];
//        j++;
//      } //if
//    } //for i
//
//    int[] retVal = new int[j];
//    System.arraycopy(tempSet, 0, retVal, 0, j);
//    return retVal;
//
//  }
//
//  /**
//   * Make a subset of the train or test set.  The subset will only
//   * contain the indices that are included in rows.  The indices in the
//   * returned value are numbered so that zero corresponds to rows[0].
//   *
//   * @param ts
//   * @param rows
//   * @return
//   */
//  protected static int[] getSubArray(int[] ts, int[] rows) {
//
//    // Xiaolei
//    if (ts == null) {
//      return new int[0];
//    }
//
//    // put all the indices of ts into a set
//    HashSet oldset = new HashSet();
//    for (int i = 0; i < ts.length; i++)
//      oldset.add(new Integer(ts[i]));
//
//      // create a list to hold the new indices
//    List newset = new ArrayList();
//    // for each row
//    for (int i = 0; i < rows.length; i++) {
//      // look up the value of the row in oldset
//      Integer ii = new Integer(rows[i]);
//      if (oldset.contains(ii)) {
//        // if it was contained, add i to the newset
//        newset.add(new Integer(i));
//      }
//    }
//
//    // copy all the values into an int array
//    int[] retVal = new int[newset.size()];
//    for (int i = 0; i < retVal.length; i++) {
//      Integer ii = (Integer) newset.get(i);
//      retVal[i] = ii.intValue();
//    }
//
//    return retVal;
//  }

//============================================================================
//============================= COPY =========================================

//  /**
//   * Returns a deep copy of this column.
//   *
//   * @return a deep copy of this SparseExampleTable
//   */
//  public Table copy() {
//    SparseExampleTable retVal;
//    try {
//      ByteArrayOutputStream baos = new ByteArrayOutputStream();
//      ObjectOutputStream oos = new ObjectOutputStream(baos);
//      oos.writeObject(this);
//      byte buf[] = baos.toByteArray();
//      oos.close();
//      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
//      ObjectInputStream ois = new ObjectInputStream(bais);
//      retVal = (SparseExampleTable) ois.readObject();
//      ois.close();
//      return retVal;
//    }
//    catch (Exception e) {
//      retVal = new SparseExampleTable(this);
//
//      return retVal;
//    }
//  }

  /**
   * Return a copy of this Table.
   * @todo handle case of an empty example table
   * @return A new Table with a copy of the contents of this table.
   */
  public Table copy() {
    SparseTable vt;

    // Copy failed, maybe objects in a column that are not serializable.
    Column[] cols = new Column[this.getNumColumns()];
    Column[] oldcols = this.getColumns();
    for (int i = 0; i < cols.length; i++) {
      cols[i] = oldcols[i].copy();
    }

    // Copy the subset, the inputs set, the output set, and the test and train sets.
//          int[] newsubset = new int[subset.length];
//          System.arraycopy(subset, 0, newsubset, 0, subset.length);
    int[] newins = new int[inputColumns.length];
    System.arraycopy(inputColumns, 0, newins, 0, inputColumns.length);
    int[] newouts = new int[outputColumns.length];
    System.arraycopy(outputColumns, 0, newouts, 0, outputColumns.length);
    int[] newtest = new int[testSet.length];
    System.arraycopy(testSet, 0, newtest, 0, testSet.length);
    int[] newtrain = new int[trainSet.length];
    System.arraycopy(trainSet, 0, newtrain, 0, trainSet.length);

    SparseExampleTable mti = new SparseExampleTable(cols);
//          mti.subset = newsubset;
    mti.setInputFeatures(newins);
    mti.setOutputFeatures(newouts);
    mti.setTestingSet(newtest);
    mti.setTrainingSet(newtrain);
    mti.setLabel(this.getLabel());
    mti.setComment(this.getComment());

    //copy the transformations
    try {
      transformations = (ArrayList) ( (ArrayList)this.getTransformations()).
          clone();
    }
    catch (Exception e) {
      e.printStackTrace();
      transformations = null;
    }

    return mti;
  }

  /**
   * Make a deep copy of the table, include length rows begining at start
   * @param start the first row to include in the copy
   * @param length the number of rows to include
   * @return a new copy of the table.
   */
  public Table copy(int start, int length) {
//          int[] newsubset = this.resubset(start, length);

    // Copy failed, maybe objects in a column that are not serializable.
    Column[] cols = new Column[this.getNumColumns()];
    Column[] oldcols = this.getColumns();
    for (int i = 0; i < cols.length; i++) {
      cols[i] = oldcols[i].getSubset(start, length);
    }

    // Copy the subset, the inputs set, the output set, and the test and train sets.
    int len = inputColumns == null ? 0 : inputColumns.length;
    int[] newins = new int[len];
    if (len > 0)
      System.arraycopy(inputColumns, 0, newins, 0, inputColumns.length);

    len = outputColumns == null ? 0 : outputColumns.length;
    int[] newouts = new int[len];
    if (len > 0)
      System.arraycopy(outputColumns, 0, newouts, 0, outputColumns.length);

//    len = testSet == null ? 0 : testSet.length;
//    int[] newtest = new int[len];
//    if (len > 0)
//      System.arraycopy(testSet, 0, newtest, 0, testSet.length);
//
//    len = trainSet == null ? 0 : trainSet.length;
//    int[] newtrain = new int[len];
//    if (len > 0)
//      System.arraycopy(trainSet, 0, newtrain, 0, trainSet.length);

    SparseExampleTable mti = new SparseExampleTable(cols);
//          int [] ns = new int [newsubset.length];
//          for (int i = 0 ; i < ns.length ; i++)
//                  ns [i] = i;
//          mti.subset = ns;
    mti.setInputFeatures(newins);
    mti.setOutputFeatures(newouts);

    // LAM-tlr wrong, subset the subsets.
    mti.setTestingSet(new int[0]);
    mti.setTrainingSet(new int[0]);
    mti.setLabel(this.getLabel());
    mti.setComment(this.getComment());

    //copy the transformations
    try {
      transformations = (ArrayList) ( (ArrayList)this.
                                     getTransformations()).clone();
    }
    catch (Exception e) {
      e.printStackTrace();
      transformations = null;
    }

    return mti;
  }

  /**
   * Make a deep copy of the table, include length rows begining at start
   * @param start the first row to include in the copy
   * @param length the number of rows to include
   * @return a new copy of the table.
   */
  public Table copy(int[] subset) {
//          TableImpl vt;
//          int[] newsubset = this.resubset(subset);

    // Copy failed, maybe objects in a column that are not serializable.
    Column[] cols = new Column[this.getNumColumns()];
    //Column[] oldcols = this.getColumns();

    for (int i = 0; i < cols.length; i++) {
      //System.out.println(i);
      cols[i] = getColumn(i).getSubset(subset);
    }

    // Copy the subset, the inputs set, the output set, and the test and train sets.
    int len = inputColumns == null ? 0 : inputColumns.length;
    int[] newins = new int[len];
    if (len > 0)
      System.arraycopy(inputColumns, 0, newins, 0, inputColumns.length);

    len = outputColumns == null ? 0 : outputColumns.length;
    int[] newouts = new int[len];
    if (len > 0)
      System.arraycopy(outputColumns, 0, newouts, 0, outputColumns.length);

//    len = testSet == null ? 0 : testSet.length;
//    int[] newtest = new int[len];
//    if (len > 0)
//      System.arraycopy(testSet, 0, newtest, 0, testSet.length);
//
//    len = trainSet == null ? 0 : trainSet.length;
//    int[] newtrain = new int[len];
//    if (len > 0)
//      System.arraycopy(trainSet, 0, newtrain, 0, trainSet.length);

    SparseExampleTable mti = new SparseExampleTable(cols);
//          int [] ns = new int [newsubset.length];
//          for (int i = 0 ; i < ns.length ; i++) ns [i] = i;
//          mti.subset = ns;
    mti.setInputFeatures(newins);
    mti.setOutputFeatures(newouts);

    // LAM-tlr, this is wrong, we need to subset the test and train sets here.
    mti.setTestingSet(new int[0]);
    mti.setTrainingSet(new int[0]);
    mti.setLabel(this.getLabel());
    mti.setComment(this.getComment());

    //copy the transformations
    try {
      transformations = (ArrayList) ( (ArrayList)this.
                                     getTransformations()).clone();
    }
    catch (Exception e) {
      e.printStackTrace();
      transformations = null;
    }

    return mti;
  }

  /**
   * Do a shallow copy on the data by creating a new instance of a MutableTable,
   * and initialize all it's fields from this one.
   * @return a shallow copy of the table.
   */
  public Table shallowCopy() {
    SparseTable tab = (SparseTable)super.shallowCopy();
    SparseExampleTable set = new SparseExampleTable(tab);

//    eti.setSubset(copyIntArray(this.getSubset()));
    set.setTrainingSet(copyArray(this.getTrainingSet()));
    set.setTestingSet(copyArray(this.getTestingSet()));
    set.setInputFeatures(copyArray(this.getInputFeatures()));
    set.setOutputFeatures(copyArray(this.getOutputFeatures()));
//    set.setLabel(this.getLabel());
//    set.setComment(this.getComment());
    set.transformations = this.transformations;
    return set;
  }


  /**
   * This class provides transparent access to the test data only. The testSets
   * field of the TrainTest table is used to reference only the test data, yet
   * the getter methods look exactly the same as they do for any other table.
   * @return a reference to a table referencing only the testing data
   */
  public Table getTestTable() {
    if (testSet == null) {
      return null;
    }
    SparseExampleTable eti = (SparseExampleTable)this.shallowCopy();
    eti.subset = testSet;
    return eti;
  }

  /**
     Return a reference to a Table referencing only the training data.
     @return a reference to a Table referencing only the training data.
   */
  public Table getTrainTable() {
    if (trainSet == null) {
      return null;
    }
    SparseExampleTable eti = (SparseExampleTable)this.shallowCopy();
    eti.subset = trainSet;
    return eti;
  }

//  /**
//   * Returns a SparseTestTable with the content of this table
//   */
//  public Table getTestTable() {
//    return new SparseTestTable(this);
//  }
//
//  /**
//   * Returns a SparseTrainTable with the content of this table
//   */
//  public Table getTrainTable() {
//    return new SparseTrainTable(this);
//  }


  /**
   * return a row object used to access each row, this instance is also an
   * Example object providing access to input and output columns within a row
   * specifically.
   * @return a row accessor object.
   */
  public Row getRow() {
          return new SparseExample(this);
  }


  //========================================================================
  //        DATA ACCESSOR METHODS FOR ExampleTable INTERFACE
  //========================================================================

  /**
   * Returns a boolean representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
   * This method is the same as <code>getBoolean(row, getInputFeatures()[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
       * @return       a boolean representation of the value at the specified location.
   */
  public boolean getInputBoolean(int row, int index) {
    return getBoolean(row, inputColumns[index]);
  }

  /**
   * Returns a byte representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
   * This method is the same as <code>getByte(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
       * @return       a byte representation of the value at the specified location.
   */
  public byte getInputByte(int row, int index) {
    return getByte(row, inputColumns[index]);
  }

  /**
   * Returns a char representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
   * This method is the same as <code>getChar(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
       * @return       a char representation of the value at the specified location.
   */
  public char getInputChar(int row, int index) {
    return getChar(row, inputColumns[index]);
  }

  /**
       * Returns a byte array representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
       * This method is the same as <code>getBytes(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
   * @return       a byte array representation of the value at the specified location.
   */
  public byte[] getInputBytes(int row, int index) {
    return getBytes(row, inputColumns[index]);
  }

  /**
       * Returns a char array representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
       * This method is the same as <code>getChars(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
   * @return       a char array representation of the value at the specified location.
   */
  public char[] getInputChars(int row, int index) {
    return getChars(row, inputColumns[index]);
  }

  /**
   * Returns a double representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
       * This method is the same as <code>getDouble(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
       * @return       a double representation of the value at the specified location.
   */
  public double getInputDouble(int row, int index) {
    return getDouble(row, inputColumns[index]);
  }

  /**
   * Returns an int representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
   * This method is the same as <code>getInt(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
       * @return       an int representation of the value at the specified location.
   */
  public int getInputInt(int row, int index) {
    return getInt(row, inputColumns[index]);
  }

  /**
   * Returns a long representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
   * This method is the same as <code>getLong(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
       * @return       a long representation of the value at the specified location.
   */
  public long getInputLong(int row, int index) {
    return getLong(row, inputColumns[index]);
  }

  /**
   * Returns a float representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
       * This method is the same as <code>getFloat(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
       * @return       a float representation of the value at the specified location.
   */
  public float getInputFloat(int row, int index) {
    return getFloat(row, inputColumns[index]);
  }

  /**
   * Returns a short  representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
       * This method is the same as <code>getShort(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
       * @return       a short representation of the value at the specified location.
   */
  public short getInputShort(int row, int index) {
    return getShort(row, inputColumns[index]);
  }

  /**
   * Returns an Object representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
       * This method is the same as <code>getObject(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
       * @return       an Objectrepresentation of the value at the specified location.
   */
  public Object getInputObject(int row, int index) {
    return getObject(row, inputColumns[index]);
  }

  /**
   * Returns a String  representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the input set.
       * This method is the same as <code>getString(row, inputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the input set, indicating the column from which
   *               the data is retrieved.
       * @return       a String representation of the value at the specified location.
   */
  public String getInputString(int row, int index) {
    return getString(row, inputColumns[index]);
  }


  /**
   * Returns a float representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getFloat(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
       * @return       a float representation of the value at the specified location.
   */
  public float getOutputFloat(int row, int index) {
    return getFloat(row, outputColumns[index]);
  }

  /**
   * Returns a boolean representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getBoolean(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
       * @return       a boolean representation of the value at the specified location.
   */
  public boolean getOutputBoolean(int row, int index) {
    return getBoolean(row, outputColumns[index]);
  }

  /**
   * Returns a byte representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getByte(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
       * @return       a byte representation of the value at the specified location.
   */
  public byte getOutputByte(int row, int index) {
    return getByte(row, outputColumns[index]);
  }

  /**
   * Returns a char representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getChar(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
       * @return       a char representation of the value at the specified location.
   */
  public char getOutputChar(int row, int index) {
    return getChar(row, outputColumns[index]);
  }

  /**
       * Returns a byte array representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getBytes(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
   * @return       a byte array representation of the value at the specified location.
   */
  public byte[] getOutputBytes(int row, int index) {
    return getBytes(row, outputColumns[index]);
  }

  /**
       * Returns a char array representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getChars(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
   * @return       a char array representation of the value at the specified location.
   */
  public char[] getOutputChars(int row, int index) {
    return getChars(row, outputColumns[index]);
  }

  /**
   * Returns a double representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getDouble(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
       * @return       a double representation of the value at the specified location.
   */
  public double getOutputDouble(int row, int index) {
    return getDouble(row, outputColumns[index]);
  }

  /**
   * Returns an int representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
   * This method is the same as <code>getInt(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
       * @return       an int representation of the value at the specified location.
   */
  public int getOutputInt(int row, int index) {
    return getInt(row, outputColumns[index]);
  }

  /**
   * Returns a long representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getLong(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
       * @return       a long representation of the value at the specified location.
   */
  public long getOutputLong(int row, int index) {
    return getByte(row, outputColumns[index]);
  }

  /**
   * Returns a short  representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getShort(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
       * @return       a short representation of the value at the specified location.
   */
  public short getOutputShort(int row, int index) {
    return getShort(row, outputColumns[index]);
  }

  /**
   * Returns an Object representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getObject(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
       * @return       an Objectrepresentation of the value at the specified location.
   */
  public Object getOutputObject(int row, int index) {
    return getObject(row, outputColumns[index]);
  }

  /**
   * Returns a String  representation of the data at row no. <codE>row</code>
   * and column with index no. <codE>index</code> into the output set.
       * This method is the same as <code>getString(row, outputColumns[index])</code>.
   *
   * @param row    the row number from which to retrieve the data.
       * @param index  an index into the output set, indicating the column from which
   *               the data is retrieved.
       * @return       a String representation of the value at the specified location.
   */
  public String getOutputString(int row, int index) {
    return getString(row, outputColumns[index]);
  }


  // END DATA ACCESSORS =====================================================


  /**
   * @return number of input columns
   */
  public int getNumInputs() {
    if (inputColumns == null) {
      return 0;
    }
    return inputColumns.length;
  }

  /**
   * @return number of output columns
   */
  public int getNumOutputs() {
    if (outputColumns == null) {
      return 0;
    }
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
  public String getInputName(int inputIndex) {
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
  public String getOutputName(int outputIndex) {
    return getColumnLabel(outputColumns[outputIndex]);
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
       * Return true if the any of the input or output columns contains missing values.
   * @return true if the any of the input or output columns contains missing values.
   */
  public boolean hasMissingInputsOutputs() {
    for (int i = 0; i < inputColumns.length; i++) {
      if (this.hasMissingValues(inputColumns[i]))
        return true;
    }
    for (int i = 0; i < outputColumns.length; i++) {
      if (this.hasMissingValues(outputColumns[i]))
        return true;
    }
    return false;
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
  public boolean isInputNominal(int inputIndex) {
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
  public boolean isInputScalar(int inputIndex) {
    return isColumnScalar(inputColumns[inputIndex]);
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
  public boolean isOutputNominal(int outputIndex) {
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
  public boolean isOutputScalar(int outputIndex) {
    return isColumnScalar(outputColumns[outputIndex]);
  }

  public String[] getInputNames() {
    return inputNames;
  }

  public String[] getOutputNames() {
    return outputNames;
  }

  /**
   * Returns a reference to this table.
   */
  public ExampleTable toExampleTable() {
    return this;
  }


//  /**
//   * Returns a single row SparseExampleTable, containing data from row
//   * no. <codE>i</code>.
//   */
//  public Example getExample(int i) {
//    return new SparseExample(this, i);
//  }
//
//  public Example getShallowExample(int i) {
//    return new SparseShallowExample(this, i);
//  }


  //=========================================================================
  //============= SPARSE SPECIFIC METHODS ===================================
  //=========================================================================

  /**
   * returns the valid output indices of row no. <codE>row</code> in a
   * sorted array.
   *
   * @param row      the row number to retrieve its output features.
   * @return            a sorted integer array with the valid output indices
   *                  of row no. <codE>row</code>.
   */
  public int[] getOutputFeatures(int row) {
    int[] retVal = new int[0];
    if (!rows.containsKey(row)) {
      return retVal;
    }

    VIntHashSet temp = (VIntHashSet) rows.get(row);
    int[] allIdx = temp.toArray();
   for (int i=0; i<allIdx.length; i++){
     allIdx[i] = columnRef.get(allIdx[i]);
   }
   temp = new VIntHashSet(allIdx);
    int[] tempArr = new int[outputColumns.length];
    int j = 0;
    for (int i = 0; i < outputColumns.length; i++) {
      if (temp.contains(outputColumns[i])) {
        tempArr[j] = outputColumns[i];
        j++;
      }
    }

    retVal = new int[j];
    System.arraycopy(tempArr, 0, retVal, 0, j);

    return retVal;

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
    if (!rows.containsKey(row)) {
      return retVal;
    }

    VIntHashSet temp = (VIntHashSet) rows.get(row);
    int[] allIdx = temp.toArray();
    for (int i=0; i<allIdx.length; i++){
      allIdx[i] = columnRef.get(allIdx[i]);
    }
    //now allIdx holds the real indices of the columns.
    temp = new VIntHashSet(allIdx);

    int[] tempArr =    new int[inputColumns.length];

    int j = 0;
    for (int i = 0; i < inputColumns.length; i++) {
      if (temp.contains(inputColumns[i])) {
        tempArr[j] = inputColumns[i];
        j++;
      }
    }
    retVal = new int[j];
    System.arraycopy(tempArr, 0, retVal, 0, j);
    return retVal;

  }

  /**
   * Returns number of output columns in row no. <code>row</code>
   *
   * @param row     row index
   * @return        number of output columns in row no. <code>row</code>
   */
  public int getNumOutputs(int row) {
    return getOutputFeatures(row).length;
  }

  /**
   * Returns number of input columns in row no. <code>row</code>
   *
   * @param row     row index
   * @return        number of input columns in row no. <code>row</code>
   */
  public int getNumInputs(int row) {
    return getInputFeatures(row).length;
  }


  //=========================================================================
  //                     END ExampleTable INTERFACE
  //=========================================================================

  //=========================================================================
  //                         Table Overrides
  //=========================================================================



//  /**
//       * Returns the total number of examples in this table, meaning the total number
//   * of rows.
//   * @retuen     the total number of examples.
//   */
//  public int getNumExamples() {
//    return getNumRows();
//  }




//  /**
//   * Copies the content of <code>srcTable</code> into this table
//   *
//   */
//  public void copy(SparseTable srcTable) {
//    if (srcTable instanceof SparseExampleTable) {
//      copyArrays( (SparseExampleTable) srcTable);
//
//    }
//    super.copy(srcTable);
//  }



//  public Table getSubsetByReference(int pos, int len) {
//    Table t = super.getSubsetByReference(pos, len);
//    ExampleTable et = t.toExampleTable();
//
//    int[] newin = new int[inputColumns.length];
//    System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
//    int[] newout = new int[outputColumns.length];
//    System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);
//
//    et.setInputFeatures(newin);
//    et.setOutputFeatures(newout);
//
//    // now figure out the test and train sets
//    int[] traincpy = new int[trainSet.length];
//    System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
//    int[] testcpy = new int[testSet.length];
//    System.arraycopy(testSet, 0, testcpy, 0, testSet.length);
//
//    int[] newtrain = getSubArray(traincpy, pos, len);
//    int[] newtest = getSubArray(testcpy, pos, len);
//
//    et.setTrainingSet(newtrain);
//    et.setTestingSet(newtest);
//
//    return et;
//  }
//
//  public Table getSubsetByReference(int[] rows) {
//    Table t = super.getSubsetByReference(rows);
//    ExampleTable et = t.toExampleTable();
//    int[] newin = new int[inputColumns.length];
//    System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
//    int[] newout = new int[outputColumns.length];
//    System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);
//
//    et.setInputFeatures(newin);
//    et.setOutputFeatures(newout);
//
//    // now figure out the test and train sets
//    int[] traincpy = new int[trainSet.length];
//    System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
//    int[] testcpy = new int[testSet.length];
//    System.arraycopy(testSet, 0, testcpy, 0, testSet.length);
//
//    int[] newtrain = getSubArray(traincpy, rows);
//    int[] newtest = getSubArray(testcpy, rows);
//
//    et.setTrainingSet(newtrain);
//    et.setTestingSet(newtest);
//    return et;
//  }


//  /**
//   * Returns a TestTable or a TrainTable with data from row index no.
//   * <code> start</code> in the test/train set through row index no.
//   * <code>start+len</code> in the test/train set.
//   *
//   * @param start       index number into the test/train set of the row at which begins
//   *                    the subset.
//   * @param len         number of consequetive rows to include in the subset.
//   * @param test        if true - the returned value is a TestTable. else - the
//   *                    returned value is a TrainTable
//       * @return            a TestTable (if <code>test</code> is true) or a TrainTable
//   *                    (if <code>test</code> is false) with data from row index no.
//       *                    <code>start</code> in the test/train set through row index
//   *                    no. <code>start+len</code> in the test/train set.
//   */
//  protected Table getSubset(int start, int len, boolean test) {
//
//    //initializing the returned value
//    SparseExampleTable retVal;
//    if (test) {
//      retVal = new SparseTestTable();
//    }
//    else {
//      retVal = new SparseTrainTable();
//
//      //retrieving the test rows indices
//    }
//    int[] rowIdx;
//    if (test) {
//      rowIdx = testSet;
//    }
//    else {
//      rowIdx = trainSet;
//
//      //if the start index is not in the range of the test rows
//      //or if len is zero - return an empty table
//    }
//    if (start >= rowIdx.length || len == 0) {
//      return retVal;
//    }
//
//    //calculating the true rows dimension of the returned table
//    //int size = len;
//    if (start + len > getNumRows()) {
//      len = getNumRows() - start;
//
//      //retrieving the indices to include in the sub set.
//    }
//    int[] indices = new int[len];
//    for (int i = 0; i < len; i++) {
//      indices[i] = rowIdx[start + i];
//
//      //retrieving a subset from the example part of this table
//    }
//    SparseMutableTable tempTbl = (SparseMutableTable) SparseMutableTable.
//        getSubset(indices, this);
//
//    //getting a test table from tempTbl. setting the test set.
//    //getting a subset from the prediction columns
//    if (test) {
//      retVal = (SparseTestTable) tempTbl.toExampleTable().getTestTable();
//      ( (SparseTestTable) retVal).predictionColumns = (SparseMutableTable)
//          SparseMutableTable.getSubset(indices,
//                                       ( (SparseTestTable)this).
//                                       predictionColumns);
//      retVal.setTestingSet(indices);
//    }
//    //getting a train table from tempTbl. setting the train set.
//    else {
//      retVal = (SparseTrainTable) tempTbl.toExampleTable().getTrainTable();
//      retVal.setTrainingSet(indices);
//
//    }
//
//    //setting input and output feature
//    retVal.setInputFeatures(getInputFeatures());
//    retVal.setOutputFeatures(outputColumns);
//
//    return retVal;
//  }




  /**
   * *****************************
   * General Get Methods
   * *****************************
   */


  // MutableTable support

//  /**
//   * Insert a new row into this Table, initialized with integer data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(int[] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with float data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(float[] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with double data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(double[] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with long data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(long[] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with short data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(short[] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with boolean data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(boolean[] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with String data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(String[] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with char[] data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(char[][] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with byte[] data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(byte[][] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with Object data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(Object[] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with byte data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(byte[] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }
//
//  /**
//   * Insert a new row into this Table, initialized with char data.
//   * @param newEntry the data to put into the inserted row.
//   * @param position the position to insert the new row
//   */
//  public void insertRow(char[] newEntry, int position) {
//    //insertTraining(trainSet[position]);
//    if (position < 0 || position >= numRows)
//      super.addRow(newEntry);
//    else {
//      incrementTrainTest(position);
//      super.insertRow(newEntry, trainSet[position]);
//    }
//  }


  /**
   * Insert a column in the table.
   * @param col the column to add.
   * @param where position were the column will be inserted.
   */
  public void insertColumn(Column col, int where) {
     // expand the column
     super.insertColumn(col, where);
     this.incrementInOut(where);
  }

  /**
   * Insert columns in the table.
   * @param datatype the columns to add.
   * @param where the number of columns to add.
   */
  public void insertColumns(Column [] datatype, int where){
     for (int i = 0 ; i < datatype.length ; i++) {
       super.insertColumn(datatype[i], where+i);
       this.incrementInOut(where+i);
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
    for (int i = 0; i < len; i++) {
      removeRow(start);
    }
  }

  /**
   Get a copy of this Table reordered based on the input array of indexes.
   Does not overwrite this Table.
   @param newOrder an array of indices indicating a new order
   @return a copy of this column with the rows reordered
   */
  public Table reorderColumns(int[] newOrder) {
    SparseMutableTable tab = (SparseMutableTable)super.copy();
    tab = (SparseMutableTable)tab.reorderColumns(newOrder);
    SparseExampleTable etab = new SparseExampleTable(tab);
    etab.setTestingSet(this.getTrainingSet());
    etab.setTrainingSet(this.getTrainingSet());

    gnu.trove.TIntIntHashMap map = new gnu.trove.TIntIntHashMap();
    for (int i = 0, n = newOrder.length; i < n; i++){
      map.put(newOrder[i], i);
    }

    int[] iset = this.getInputFeatures();
    int[] oset = this.getOutputFeatures();
    int[] newiset = new int[iset.length];
    int[] newoset = new int[oset.length];
    for (int i = 0, n = iset.length; i < n; i++){
      newiset[i] = map.get(iset[i]);
    }
    etab.setInputFeatures(newiset);
    for (int i = 0, n = oset.length; i < n; i++){
      newoset[i] = map.get(oset[i]);
    }
    etab.setOutputFeatures(newoset);
    return etab;
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

//  /**
//   * Insert a new column into this Table, initialized with integer data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(int[] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with float data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(float[] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with double data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(double[] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with long data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(long[] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with short data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(short[] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with boolean data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(boolean[] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with String data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(String[] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with char[] data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(char[][] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with byte[] data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(byte[][] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with Object data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(Object[] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with byte data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(byte[] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }
//
//  /**
//   * Insert a new column into this Table, initialized with char data.
//   * @param newEntry the initial values of the new column.
//   * @param position the position to insert the new row
//   */
//  public void insertColumn(char[] newEntry, int position) {
//    this.incrementInOut(position);
//    super.insertColumn(newEntry, position);
//  }



//  /**
//   * Remove rows from this Table using a boolean map.
//   * @param flags an array of booleans to map to this Table's rows.  Those
//   * with a true will be removed, all others will not be removed
//   */
//  public void removeRowsByFlag(boolean[] flags) {
//    int numRemoved = 0;
//    for (int i = 0; i < flags.length; i++) {
//      if (flags[i]) {
//        removeRow(i - numRemoved++);
//      }
//    }
//  }
//
//  /**
//   * Remove rows from this Table using a boolean map.
//   * @param flags an array of booleans to map to this Table's rows.  Those
//   * with a true will be removed, all others will not be removed
//   */
//  public void removeColumnsByFlag(boolean[] flags) {
//    int numRemoved = 0;
//    for (int i = 0; i < flags.length; i++) {
//      if (flags[i]) {
//        removeColumn(i - numRemoved);
//      }
//    }
//  }
//
//  /**
//   * Remove rows from this Table by index.
//   * @param indices a list of the rows to remove
//   */
//  public void removeRowsByIndex(int[] indices) {
//    for (int i = 0; i < indices.length; i++) {
//      removeRow(indices[i] - i);
//    }
//  }
//
//  /**
//   * Remove rows from this Table by index.
//   * @param indices a list of the rows to remove
//   */
//  public void removeColumnsByIndex(int[] indices) {
//    for (int i = 0; i < indices.length; i++) {
//      removeColumn(indices[i] - i);
//    }
//  }

//  /**
//   Get a copy of this Table reordered based on the input array of indexes.
//   Does not overwrite this Table.
//   @param newOrder an array of indices indicating a new order
//   @return a copy of this column with the rows reordered
//   */
//  public Table reorderRows(int[] newOrder) {
//    return super.reorderRows(newOrder);
//  }


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

    if (testSet != null) {
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
    }

    if (trainSet != null) {
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
//VERED: added this method, for testing purposes
  public boolean equals(Object set) {
   SparseExampleTable _set = (SparseExampleTable) set;
   super.equals(_set);
   int[] _inputs = _set.getInputFeatures();
   int[] _outputs = _set.getOutputFeatures();
   if (!Arrays.equals(_inputs, this.inputColumns)){
     System.out.println("incompatibility of input features sets");
     return false;
   }
   if (!Arrays.equals(_outputs, this.outputColumns)) {
     System.out.println("incompatibility of output features sets");
     return false;
   }


   for (int i=0; i<getNumRows(); i++){
     int[] thisInputs = this.getInputFeatures(i);
     _inputs = _set.getInputFeatures(i);
     int[] thisOutputs = this.getOutputFeatures(i);
     _outputs = _set.getOutputFeatures(i);
     if (!Arrays.equals(_inputs, thisInputs)) {
       System.out.println("incompatibility of input features sets for row # " +
                          i);
       return false;
   }

   if (!Arrays.equals(_outputs, thisOutputs)) {
     System.out.println("incompatibility of output features sets for row # " +
                        i);
     return false;
   }

   }//for

   return true;
 }//equals

}