package ncsa.d2k.modules.core.datatype.table.sparse;

//import ncsa.d2k.modules.projects.vered.sparse.example.SparsePredictionExample;
import ncsa.d2k.modules.core.datatype.table.sparse.examples.SparsePredictionExample;
import ncsa.d2k.modules.core.datatype.table.Example;
import ncsa.d2k.modules.core.datatype.table.*;
//import ncsa.d2k.modules.projects.vered.sparse.primitivehash.*;
//import ncsa.d2k.modules.projects.vered.sparse.column.*;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * SparseTestTable is the same as SparsePredictionTable with limited access to
 * the test set rows only.
 */

public class SparseTestTable  extends SparsePredictionTable implements TestTable {

  public SparseTestTable() {
    super();
  }


   public SparseTestTable(SparseExampleTable table) {
    super(table);
  }



  public ExampleTable getExampleTable() {
    return  new SparseExampleTable(this);
  }
/*
  public Example getExample(int index){
    return new SparsePredictionExample( (SparseTestTable) getSubset(index, 1));
  }
*/


  /**
   * Returns an int representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getInt(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          an int representation of the data at the specified position.
   */
  public int getInt(int row, int column) {
    return super.getInt(testSet[row], column);
  }


  /**
   * Returns a short representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getShort(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          a short representation of the data at the specified position.
   */
  public short getShort(int row, int column) {
    return super.getShort(testSet[row], column);
  }


  /**
   * Returns a long representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> ..
   *
   * This method is the same as getLong(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          a long representation of the data at the specified position.
   */
  public long getLong(int row, int column) {
    return super.getLong(testSet[row], column);
  }


  /**
   * Returns a float representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getFloat(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          a float representation of the data at the specified position.
   */
  public float getFloat(int row, int column) {
    return super.getFloat(testSet[row], column);
  }



  /**
   * Returns a double representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getDouble(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          a double representation of the data at the specified position.
   */
  public double getDouble(int row, int column) {
    return super.getDouble(testSet[row], column);
  }


  /**
   * Returns a String representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getString(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          a String representation of the data at the specified position.
   */
  public String getString(int row, int column) {
    return super.getString(testSet[row], column);
  }


  /**
   * Returns a byte array representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getBytes(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          a byte array representation of the data at the specified position.
   */
  public byte[] getBytes(int row, int column) {
    return super.getBytes(testSet[row], column);
  }


  /**
   * Returns a char array representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getChars(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          a char array representation of the data at the specified position.
   */
  public char[] getChars(int row, int column) {
    return super.getChars(testSet[row], column);
  }

  /**
   * Returns  boolean representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getInt(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          a boolean representation of the data at the specified position.
   */
  public boolean getBoolean(int row, int column) {
    return super.getBoolean(testSet[row], column);
  }

  /**
   * Returns a byte representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getByte(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          a byte representation of the data at the specified position.
   */
  public byte getByte(int row, int column) {
    return super.getByte(testSet[row], column);
  }

  /**
   * Returns a char representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getChar(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          a char representation of the data at the specified position.
   */
  public char getChar(int row, int column) {
    return super.getChar(testSet[row], column);
  }



  /* returns the actual number of rows in the test set. */
  public int getNumRows() {
      return testSet.length;
  }


public int getNumExamples() {
      return testSet.length;
   }


  /**
   * Returns an Object representation of the data at row no. <codE>row</code> in the
   * test set and column no. <code>column</codE> .
   *
   * This method is the same as getObject(testSet[row], column).
   *
   * @param row       the row index in the test set
   * @param column    the column index
   * @return          an Object representation of the data at the specified position.
   */
  public Object getObject(int row, int column) {
    return super.getObject(testSet[row], column);
  }





/* Returns the number od entries in the test set.*/
  public int getNumEntries() {

    int numEntries = 0;
    for (int i=0; i<testSet.length; i++)
      numEntries += this.getRowNumEntries(testSet[i]);

    return numEntries;
  }

  /**
   * Copies the content of row index <code>position</codE> in the test set into
   * <code>buffer</code>, and copies the valid columns of this row into <code>
   * indices</cdoe>.
   *
   * @param buffer    an array of some type into which the data is copied.
   * @param position  the row index into the test set, to retrieve its data
   * @param indices   an int array to hold the valid columns of row <codE>position</code>
   */
  public void getRow(Object buffer, int position, int[] indices) {
    super.getRow(buffer, testSet[position], indices);
  }



  /**
   * Returns a TestTable with data from row index no. <code> start</code> in the
   * test set through row index no. <code>start+len</code> in the test set.
   *
   * @param start       index number into the test set of the row at which begins
   *                    the subset.
   * @param len         number of consequetive rows to include in the subset.
   */
  public Table getSubset(int start, int len) {
     return getSubset(start, len, true);
  }








  /**
   * Returns true if there is a value at row with index  <codE>row</code> and column
   * no. <code>column</code> in the test set.
   *
   * @param row   row index into the test set.
   * @param column  the iinspected column index
   * @return        true if there is a value at row index <code> row</codE> in the test
   *                set and column no. <code>column</code>.
   */
  public boolean doesValueExist(int row, int column){
    return super.doesValueExist(testSet[row], column);
  }


  /**
   * Returns a deep copy of this table.
   */
  public Table copy() {
   return new SparseTestTable((SparsePredictionTable)super.copy());
  }



   /**
     * Return the total number of entries in row index <code>position</code> in
     * the testing set.
     */
    public int getRowNumEntries(int position){
     return super.getRowNumEntries(testSet[position]);

    }

    /**
     * Returns the valid column numbers of row index <code>position</code> in
     * the testing set.
     */
    public int[] getRowIndices(int position){
      return super.getRowIndices(testSet[position]);
    }


/* **************************************************
   OVERRIDING SET TYPE PREDICTION METHODS
   **************************************************
*/


   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setStringPrediction(String prediction, int row, int predictionColIdx) {
    predictionColumns.setString(prediction, testSet[row], predictionColIdx);
  }




   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setBooleanPrediction(boolean prediction, int row, int predictionColIdx) {
    predictionColumns.setBoolean(prediction, testSet[row], predictionColIdx);
  }

   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setCharPrediction(char prediction, int row, int predictionColIdx) {
    predictionColumns.setChar(prediction, testSet[row], predictionColIdx);
  }




   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setBytePrediction(byte prediction, int row, int predictionColIdx) {
    predictionColumns.setByte(prediction, testSet[row], predictionColIdx);
  }


   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setFloatPrediction(float prediction, int row, int predictionColIdx) {
    predictionColumns.setFloat(prediction, testSet[row], predictionColIdx);
  }




   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setDoublePrediction(double prediction, int row, int predictionColIdx) {
    predictionColumns.setDouble(prediction, testSet[row], predictionColIdx);
  }

   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setCharsPrediction(char[] prediction, int row, int predictionColIdx) {
    predictionColumns.setChars(prediction, testSet[row], predictionColIdx);
  }




   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setBytesPrediction(byte[] prediction, int row, int predictionColIdx) {
    predictionColumns.setBytes(prediction, testSet[row], predictionColIdx);
  }






   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setObjectPrediction(Object prediction, int row, int predictionColIdx) {
    predictionColumns.setObject(prediction, testSet[row], predictionColIdx);
  }




   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setShortPrediction(short prediction, int row, int predictionColIdx) {
    predictionColumns.setShort(prediction, testSet[row], predictionColIdx);
  }

   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setLongPrediction(long prediction, int row, int predictionColIdx) {
    predictionColumns.setLong(prediction, testSet[row], predictionColIdx);
  }




   /**
   * sets the value <codE>prediction</code> to row index <codE>row</code> in the
   * testing set.
   * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   * @param prediction    the value to be set.
   * @param row           the row index into the testing set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setIntPrediction(int prediction, int row, int predictionColIdx) {
    predictionColumns.setInt(prediction, testSet[row], predictionColIdx);
  }







}//SparseTestTable




/*overriding the addRow(type[] arr, int[] indices) methods*/
/*
    public void addRow(int[] newEntry, int[] indices) {
      testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(float[] newEntry, int[] indices) {
      testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(double[] newEntry, int[] indices) {
      testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(long[] newEntry, int[] indices) {
      testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(short[] newEntry, int[] indices) {
      testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(boolean[] newEntry, int[] indices) {
       testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(String[] newEntry, int[] indices) {
      testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(char[][] newEntry, int[] indices) {
      testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(byte[][] newEntry, int[] indices) {
      testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(Object[] newEntry, int[] indices) {
      testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(byte[] newEntry, int[] indices) {
       testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }

   public void addRow(char[] newEntry, int[] indices) {
      testSet.add(super.getNumRows());
      super.addRow(newEntry, indices);
   }
*/
/* overrding insertRow(type[] arr, int pos, int[] indices) methods */

/*
  public void insertRow(int[] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(float[] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(double[] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(long[] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(short[] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(boolean[] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(String[] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(char[][] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(byte[][] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(Object[] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(byte[] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

   public void insertRow(char[] newEntry, int position, int[] indices) {
      incrementTrainTest(position);
      testSet.add(position);
      super.insertRow(newEntry, getTestingSet()[position], indices);
   }

*/
   /* overriding removeRow methods */
/*
    public void removeRow(int row) {
      super.removeRow(getTestingSet()[row]);
      testSet.remove(row);
   }

   public void removeRows(int start, int len) {
      for (int i=0; i<len; i++)
	super.removeRow(start+i);
   }

   public void removeRowsByFlag(boolean[] flags) {

      for (int i = 0; i < flags.length; i++)
         if (flags[i])
            removeRow(i);
   }

   public void removeRowsByIndex(int[] indices) {

      for (int i = 0; i < indices.length; i++)
         removeRow(indices[i]);
   }
*/



