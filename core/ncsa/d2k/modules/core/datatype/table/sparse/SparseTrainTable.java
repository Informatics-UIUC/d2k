package ncsa.d2k.modules.core.datatype.table.sparse;

//import ncsa.d2k.modules.projects.vered.sparse.primitivehash.VIntHashSet;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.VIntHashSet;

import ncsa.d2k.modules.core.datatype.table.TrainTable;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.TestTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.Table;
//import ncsa.d2k.modules.projects.vered.sparse.example.SparseExample;
import ncsa.d2k.modules.core.datatype.table.sparse.examples.SparseExample;
import ncsa.d2k.modules.core.datatype.table.Example;
import ncsa.d2k.modules.core.datatype.table.TableFactory;

/**
 * SparseTrainTable is the same as SparsePredictionTable with limited access to
 * the train set rows only.
 */

public class SparseTrainTable extends SparseExampleTable implements TrainTable {

  public SparseTrainTable() {
    super();
  }


  public SparseTrainTable(SparseExampleTable table) {
    super(table);
  }


  public ExampleTable getExampleTable() {
    return new SparseExampleTable(this);
  }



  public Example getExample(int index){
    return new SparseExample( (SparseTrainTable) getSubset(index, 1));
  }

  /**
   * Returns an int representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getInt(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          an int representation of the data at the specified position.
   */
  public int getInt(int row, int column) {
     return super.getInt(trainSet[row], column);
  }


  /**
   * Returns a short representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getShort(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a short representation of the data at the specified position.
   */
  public short getShort(int row, int column) {
     return super.getShort(trainSet[row], column);
  }


  /**
   * Returns a long representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getLong(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a long representation of the data at the specified position.
   */
  public long getLong(int row, int column) {
       return super.getLong(trainSet[row], column);
  }


  /**
   * Returns a float representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getFloat(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a float representation of the data at the specified position.
   */
  public float getFloat(int row, int column) {
       return super.getFloat(trainSet[row], column);
  }


  /**
   * Returns a double representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getDouble(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a double representation of the data at the specified position.
   */
  public double getDouble(int row, int column) {
       return super.getDouble(trainSet[row], column);
  }


  /**
   * Returns a String representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getString(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a String representation of the data at the specified position.
   */
  public String getString(int row, int column) {
       return super.getString(trainSet[row], column);
  }


  /**
   * Returns a byte array representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getBytes(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a byte array representation of the data at the specified position.
   */
  public byte[] getBytes(int row, int column) {
       return super.getBytes(trainSet[row], column);
  }


  /**
   * Returns a char array representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getChars(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a char array representation of the data at the specified position.
   */
  public char[] getChars(int row, int column) {
       return super.getChars(trainSet[row], column);
  }


  /**
   * Returns a boolean representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getBoolean(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a boolean representation of the data at the specified position.
   */
  public boolean getBoolean(int row, int column) {
       return super.getBoolean(trainSet[row], column);
  }


  /**
   * Returns a byte representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getByte(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a byte representation of the data at the specified position.
   */
  public byte getByte(int row, int column) {
     return super.getByte(trainSet[row], column);
  }


  /**
   * Returns a char representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getChar(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a char representation of the data at the specified position.
   */
  public char getChar(int row, int column) {
     return super.getChar(trainSet[row], column);
  }



  public int getNumRows() {
     return trainSet.length;
  }


  public int getNumExamples() {
     return trainSet.length;
  }

  /**
   * Returns an Object representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getObject(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          an Object representation of the data at the specified position.
   */
  public Object getObject(int row, int column) {
    return super.getObject(trainSet[row], column);
  }


  public int getNumEntries() {

    int numEntries = 0;
    for (int i=0; i<trainSet.length; i++)
      numEntries += getRowNumEntries(trainSet[i]);

    return numEntries;
  }



  public void getRow(Object buffer, int position, int[] indices) {
    super.getRow(buffer, trainSet[position], indices);
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
   return getSubset(start, len, false);
  }


  public Table copy() {

    return new SparseTrainTable((SparseExampleTable)super.copy());
  }


  public boolean doesValueExist(int row, int column){
    return super.doesValueExist(trainSet[row], column);
  }


     /**
     * Return the total number of entries in row index <code>position</code> in
     * the testing set.
     */
    public int getRowNumEntries(int position){
     return super.getRowNumEntries(trainSet[position]);

    }

    /**
     * Returns the valid column numbers of row index <code>position</code> in
     * the testing set.
     */
    public int[] getRowIndices(int position){
      return super.getRowIndices(trainSet[position]);
    }

}