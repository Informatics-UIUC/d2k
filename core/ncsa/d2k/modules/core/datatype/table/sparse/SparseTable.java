package ncsa.d2k.modules.core.datatype.table.sparse;

//import ncsa.d2k.modules.projects.vered.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;


//import ncsa.d2k.modules.projects.vered.sparse.column.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import gnu.trove.*;
import java.io.*;
import java.util.Arrays;

/**
 * SparseTable is a type of Table that is sparsely populated.
 * Thus the internal representation of SparseTable is as following:
 *  each column that actually has elements in it (hereinafter "valid column") is
 *  represented by a hashmap ( an int to primitive type or object hashmap).
 *
 *  the entries of the sparse table are the values of such hashmap and they are mapped
 *  to their row number (the key).
 *
 *  all the columns are been held in a nother int to object hashmap.
 *  the keys are integers - the column number and the values are the hashmaps.
 *
 * SparseTable holds another int to object hashmap which represents the valid
 * rows (rows that have elements in them).
 *  each row is represented by a Set of integers, which are the valid columns number
 *  of that specific row.
 *  each row (a Set object) is mapped to an int, the row number, in the hashmap.
 */

public abstract class SparseTable  extends AbstractTable implements Serializable{


 protected VIntObjectHashMap columns;    //ints (keys - the column number) mapped to
				      //int hashmaps (values - the columns)
 protected VIntObjectHashMap rows;      //ints (keys - row number) mapped to Sets
				      //(values - holds the valid columns in the row)

  protected int numRows;
  protected int numColumns;

protected static SparseTableFactory factory = new SparseTableFactory();
/**
 * creates a sparse table with hashmaps with default capacity load factor.
 */
  public SparseTable(){
    this(0, 0);
  }

  /**
   * Creates a SparseTable with column hashmap with <code>numCols</code> capacity, and rows
   * hashmap with <code>numRows</code> capacity.
   *
   * @param numRows   number of rows, also the initial capacity of the rows hashmap
   * @param numCols   number of columns, also the initial capacity of the columns
   *                  hashmap
   */
  public SparseTable(int numRows, int numCols){
    if (numCols == 0)
      columns = new VIntObjectHashMap();
    else
     columns = new VIntObjectHashMap(numCols);

    if (numRows == 0)
      rows = new VIntObjectHashMap();
    else
      rows = new VIntObjectHashMap(numRows);
    setKeyColumn(0);
    numRows = 0;
    numColumns = 0;
  }

  /**
   * instantiate this table with the content of <code>T</codE>
   * creates a shallow copy of <codE>T</code>
   */
   public SparseTable(SparseTable T){

   //making a shallow copy of rows.
    rows = new VIntObjectHashMap(T.getNumRows());

    //retrieving valid rows numbers
    int[] rKeys;

    if(T instanceof TestTable)
      rKeys = ((SparseExampleTable)T).testSet;
    else if(T instanceof TrainTable)
      rKeys = ((SparseExampleTable)T).trainSet;
    else rKeys =  T.rows.keys();


    for (int i=0; i<rKeys.length; i++)
      rows.put(rKeys[i], T.rows.get(rKeys[i]));

    //making a shallow copy of columns
    columns = new VIntObjectHashMap(T.columns.size());
    int[] cKeys = T.columns.keys();
    for (int i=0; i<cKeys.length; i++)
      columns.put(cKeys[i], T.columns.get(cKeys[i]));

    numRows = T.numRows;
    numColumns = T.numColumns;
    copyAttributes(T);
  }


  /**
   * Copies content of <code> srcTable</code> into this table.
   *
   *
   */
 protected void copy(SparseTable srcTable){
  copyAttributes(srcTable);

  columns = srcTable.columns.copy();
  rows = srcTable.rows.copy();
  numColumns = srcTable.numColumns;
  numRows = srcTable.numRows;

 }


 /**
  * Copies attributes of <code>srcTable</code> to this table.
  */
 protected void copyAttributes(SparseTable srcTable){
  setComment(srcTable.getComment());
  setKeyColumn(srcTable.getKeyColumn());
  setLabel(srcTable.getLabel());
 }


 /**
  * ***********************************************************
  * GET TYPE METHODS
  * ***********************************************************
  */



  /**
   * Returns a boolean representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a boolean
   *                returns false if such column does not exist.
   */
  public boolean getBoolean(int row, int column){
    if (columns.containsKey(column))
      return ((Column)columns.get(column)).getBoolean(row);
    else return SparseBooleanColumn.NOT_EXIST;
  }


    /**
   * Returns a char representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a char.
   *                returns a value signifying the position is empty, as defined
   *                by SparseCharColumn if such column does not exist.
   */
  public char getChar(int row, int column){
    if (columns.containsKey(column))
      return ((Column)columns.get(column)).getChar(row);
    else return SparseCharColumn.NOT_EXIST;
  }


 /**
 * Returns a char array representation of the data held at (row,column) in this
 * table
 *
 * @param row     the row number from which to retrieve the data
 * @param column  the column number from which to retrieve the data
 * @return        the data at position (row, column) represented by a char array.
 *                returns null if such column does not exist.
 */
  public char[] getChars(int row, int column){
    if (columns.containsKey(column))
      return ((Column)columns.get(column)).getChars(row);
    else return null;
  }



  /**
   * Returns a double olean representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a double
   *                if such column does not exist returns a value signifying the
   *                position is empty, as defined by SparseDoubleColumn.
   */
  public double getDouble(int row, int column){
     if (columns.containsKey(column))
        return ((Column)columns.get(column)).getDouble(row);
    else return SparseDoubleColumn.NOT_EXIST;
  }


    /**
   * Returns a float representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a float
   *                returns Float.NEGATIVE_INFINITY if such column does not exist.
   */
  public float getFloat(int row, int column){
    if (columns.containsKey(column))
       return ((Column)columns.get(column)).getFloat(row);
    else return SparseFloatColumn.NOT_EXIST;
  }


  /**
   * Returns a byte array representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a byte array
   *                returns null if such column does not exist.
   */
  public byte[] getBytes(int row, int column){
    if (columns.containsKey(column))
      return ((Column)columns.get(column)).getBytes(row);
    else return null;
  }


   /**
   * Returns a byte representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a byte
   *                returns a value signifying the position is empty, as defined
   *                by SparseByteColumn if such column does not exist.
   */
  public byte getByte(int row, int column){
    if (columns.containsKey(column))
      return ((Column)columns.get(column)).getByte(row);
    else return SparseByteColumn.NOT_EXIST;
  }


   /**
   * Returns an int representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by an int
   *                if such column does not exist.returns a value signifying the
   *                position is empty, as defined by SparseIntColumn.
   */
  public int getInt(int row, int column){
    if (columns.containsKey(column))
      return ((Column)columns.get(column)).getInt(row);

    else return SparseIntColumn.NOT_EXIST;
  }



    /**
   * Returns a long representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a long
   *                if such column does not exist returns a value signifying the
   *                position is empty, as defined by SparseLongColumn.
   */
  public long getLong(int row, int column){
   if (columns.containsKey(column))
      return ((Column)columns.get(column)).getLong(row);
   else return SparseLongColumn.NOT_EXIST;
  }



    /**
   * Returns a short representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a short
   *                returns a value signifying the position is empty,
   *                as defined by SparseShortColumn, if such column does not exist.
   */
  public short getShort(int row, int column){
    if (columns.containsKey(column))
      return ((Column)columns.get(column)).getShort(row);
    else return SparseShortColumn.NOT_EXIST;
  }



    /**
   * Returns a String representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a String
   *                returns null if such column does not exist.
   */
  public String getString(int row, int column){
    if (columns.containsKey(column))
      return ((Column)columns.get(column)).getString(row);
    else return null;
  }


    /**
   * Returns an Object encapsulating the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) encapsulated in an Object
   *                returns null if such column does not exist.
   */
  public Object getObject(int row, int column){
    if (columns.containsKey(column))
      return ((Column)columns.get(column)).getObject(row);
    else return null;
  }




  /**
   * ***********************************************************
   * GENERAL GET METHODS
   * ***********************************************************
   */


  /**
   * Returns the number of columns in this talbe. Since this is a Sparse Table
   * returns the maximal column number plus 1 (counting starts from zero)
   *
   * @return    the maximal column number plus 1.
   */
  public int getNumColumns(){
    return numColumns;
  }


  /**
   * Returns the total number of entries in this table.
   *
   * @return    the total number of valid entries in this table
   */
  public int getNumEntries(){
    int numEntries = 0;     //the returned value

    int[] columnNumbers = columns.keys();     //retrieving the column number

    //for each colum
    for (int i=0; i<columnNumbers.length; i++)
      //add its number of entries to the returned value
      numEntries += getColumnNumEntries(columnNumbers[i]);

    return numEntries;
  }




  /**
   * Returns the comment associated with column #<code>position</code>
   *
   * @param position    the column number to retrieve its comment.
   * @return    the associated comment with column #<codE>position</code> or
   *            null if such column does not exit.
   */
  public String getColumnComment(int position){
    if (columns.containsKey(position))
      return ((Column)columns.get(position)).getComment();
    else return null;
  }


    /**
   * Returns the label associated with column #<code>position</code>
   *
   * @param position    the column number to retrieve its label.
   * @return        the associated label with column #<codE>position</code> or
   *                null if such column does not exit.
   */
  public String getColumnLabel(int position){
     if (columns.containsKey(position))
       return ((Column)columns.get(position)).getLabel();
    else return null;
  }




  /**
   * Retruns true if column #<code>position</code> holds nominal values. otherwise
   * returns false (also if such column does not exist).
   *
   * @param position    the column number its data type is varified
   * @return            true if the data t column #<code>position</code> is
   *                    nominal. if else (also if column does not exist) returns
   *                    false.
   */
  public boolean isColumnNominal(int position){
     if (columns.containsKey(position))
       return ((Column)columns.get(position)).getIsNominal();
    else return false;
  }



  /**
   * Retruns true if column #<code>position</code> holds scalar values. otherwise
   * returns false (also if such column does not exist).
   *
   * @param position    the column number its data type is varified
   * @return            true if the data t column #<code>position</code> is
   *                    scalar. if else (also if column does not exist) returns
   *                    false.
   */
  public boolean isColumnScalar(int position){
     if (columns.containsKey(position))
       return ((Column)columns.get(position)).getIsScalar();
     else return false;
  }

  /**
   * marks the falg attributes of column #<code>position</code> according to
   * <code>values</code>: sets isNominal to <code>value</code> and sets isScalar
   * to <code>!value</code>
   *
   * @param value   a flag signifies whether a column holds nominal values
   * @param position  the column number to have its attributes set
   *
   */
  public void setColumnIsNominal(boolean value, int position){
    if(columns.containsKey(position))
      ((Column)columns.get(position)).setIsNominal(value);
  }


   /**
   * marks the falg attributes of column #<code>position</code> according to
   * <code>values</code>: sets isScalar to <code>value</code> and sets isNominal
   * to <code>!value</code>
   *
   * @param value   a flag signifies whether a column holds scalar values
   * @param position  the column number to have its attributes set
   *
   */
  public void setColumnIsScalar(boolean value, int position){
    if(columns.containsKey(position))
      ((Column)columns.get(position)).setIsScalar(value);
  }


  /**
   * Returns true if column #<c0ode>position</code> holds numeric data or data
   * that numeric values can be parsed from. otherwise return false (also if the
   * column does not exist).
   *
   * @param position    the column number which its data type is verified.
   * @return            true if the data at column #<code>position</code> is
   *                    numeric or that numeric values can be parsed from it.
   *                    returns false if otherwise (also if column does not exit).
   */
  public boolean isColumnNumeric(int position){
   AbstractSparseColumn tempCol = ((AbstractSparseColumn)columns.get(position));
   return tempCol.isNumeric();
  }


  /**
   * Returns an int representing the type of data held at column  #<code>position</code>.
   *
   * @param position  the oclumn number its data type is being varified.
   * @return          an integer value representing the type of the data.
   *                  return -1 if such column does not exist.
   */
  public int getColumnType(int position){
     if(columns.containsKey(position))
      return ((Column)columns.get(position)).getType();

    else return -1;
  }








  /**
   * Copies the data of column #<code>pos</code> into <code>buffer</code>.
   * Assuming <code>buffer</code> is an array of some type, the data at the column
   * will be converted into that type as needed.
   *
   * If column #<coe>pos</code> does not exist - the method does nothing.
   *
   * Note - that since this is a Sparse Table if value V resides at buffer[i] at the
   * end of getColumn process - it does not mean V resides at (i, pos) in this table.
   * It is highly recommended to use getColumn(Object, int, int[]) in order to
   * receive a better mapping of vlaues to row numbers.
   *
   * @param buffer  an array to hold the data at column #<code>pos</code>
   * @param pos     the column number to retrieve the data from
   *
   */
   public void getColumn (Object buffer, int pos) {
    if(columns.containsKey(pos))
      ((AbstractSparseColumn)columns.get(pos)).getData(buffer);

   }


   /**
    * Copies the values that are stored in column #<code>pos</code> into
    * <code>valuesBuffer</code> according to order of row numbers. also
    * copies the valid row numbers into <code>rowNumbers</code>, such that when
    * getColumn returns <code>valuesBuffer[i]</code> is the value stored at row
    * #<code>rowNumbers[i]<code> and column #<code>pos</code>.
    *
    * If column #<coe>pos</code> does not exist - the method does nothing.
    *
    * to use this method efficiently: valuesBuffer and rowNumbers should be only
    * as big as the value <code>getColumnNumEntries(pos)</code> returns.
    *
    * @param valuesBuffer   must be an array of some type. at the end of the process
    *                       of this method it will hold the values of column
    *                       pos. the values will be converted into the type of the
    *                       array.
    * @param pos            the column number from which the data is retrieved
    * @param rowNumber      an int array to hold the valid row numbers in
    *                       column #<code>pos</code>.

    */
   public  void getColumn(Object valuesBuffer, int pos, int[] rowNumbers){
    if (!columns.containsKey(pos))
        return;
    getColumn(valuesBuffer, pos);
    int [] arr = getColumnIndices(pos);
    System.arraycopy(arr, 0, rowNumbers, 0, arr.length);
   }


   /**
    * Returns the number of entries in column #<code>position</code>.
    *
    * @param position     the column number to retrieve its total number of entries.
    * @return             the number of entries in column #<code>position</code>
    *                      return zero if no such column exists.
    */
   public int getColumnNumEntries(int position){
    if (columns.containsKey(position))
       return ((AbstractSparseColumn)columns.get(position)).getNumEntries();
    else return 0;
   }





   /**
    * Copies the data that is held at row #<code>position</code> into <code>
    * buffer</code>.
    * Assuming <code>buffer</code> is an array of some type, the data at the row
    * will be converted into that type as needed.
    *
    * If row #<coe>pos</code> does not exist - the method does nothing.
    *
    * Note - that since this is a Sparse Table it is very likely that when getRow
    * returns the value stored at <code>buffer[i]</code> is not stored at row
    * <code>position</code> and column <code>i</code> in this table.
    * For more accurate results use getRow(Object, int, int[]).
    *
    *  @param buffer  an array to hold the data at row #<code>position</code>
    *  @param pos     the row number from which the data is being retrieved.
    */
   public void getRow(Object buffer, int position){

      //validating the row number
      if (!rows.containsKey(position))
	return;

        //retrieving the valid columns in the given row.
	VIntHashSet row = ((VIntHashSet)rows.get(position));
	int[] colNumbers = row.toArray();
	Arrays.sort(colNumbers);
	int size = colNumbers.length;

	//converting the data in the given row as needed

	if(buffer instanceof int[]) {
         int[] b1 = (int[])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getInt(position,colNumbers[i]);
      }
      else if(buffer instanceof float[]) {
         float[] b1 = (float[])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getFloat(position,colNumbers[i]);
      }
      else if(buffer instanceof double[]) {
         double[] b1 = (double[])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getDouble(position,colNumbers[i]);
      }
      else if(buffer instanceof long[]) {
         long[] b1 = (long[])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getLong(position,colNumbers[i]);
      }
      else if(buffer instanceof short[]) {
         short[] b1 = (short[])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getShort(position,colNumbers[i]);
      }
      else if(buffer instanceof boolean[]) {
         boolean[] b1 = (boolean[])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getBoolean(position,colNumbers[i]);
      }
      else if(buffer instanceof String[]) {
         String[] b1 = (String[])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getString(position,colNumbers[i]);
      }
      else if(buffer instanceof char[][]) {
         char[][] b1 = (char[][])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getChars(position,colNumbers[i]);
      }
      else if(buffer instanceof byte[][]) {
         byte[][] b1 = (byte[][])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getBytes(position,colNumbers[i]);
      }
      else if(buffer instanceof Object[]) {
         Object[] b1 = (Object[])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getObject(position,colNumbers[i]);
      }
      else if(buffer instanceof byte[]) {
         byte[] b1 = (byte[])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getByte(position,colNumbers[i]);
      }
      else if(buffer instanceof char[]) {
         char[] b1 = (char[])buffer;
         for(int i = 0; i < b1.length && i < size; i++)
            b1[i] = getChar(position,colNumbers[i]);
      }


   }


   /**
    * Copies the value at row #<code>position</code> into <code>buffer</code>
    * and copies the number of valid columns in this row into <code>
    * columnsNumbers</code>, such that when this method returns the value
    * <code>buffer[i]</code> is the value that stored at row <code>position</code>
    * and column <codE>columnNumbers[i]</code> in this table.
    *
    * If row #<coe>pos</code> does not exist - the method does nothing.
    *
    * to use this method efficiently make sure that buffer and columnNumbers are
    * only and exactly as big as the value <code>getRowNumEntries(int)</code> returns.
    *
    * @param buffer     an array of some type, into which the data at row #<code>
    *                   position</code> is being copied. the values will be
    *                   converted into the type of the array as needed.
    * @param position   the row number from which to retrieve the data.
    * @param columnNumbers    an array of ints, into which the valid column
    *                         numbers of row #position will be copied.
    */
     public void getRow(Object buffer, int position, int[] columnNumbers){
       if (!rows.containsKey(position))
	  return;

      getRow(buffer, position);
      int[] arr = ((VIntHashSet)rows.get(position)).toArray();
      System.arraycopy(arr, 0, columnNumbers, 0, arr.length);
     }




     /**
    * Returns the number of entries in row #<code>position</code>.
    *
    * @param position     the row number to retrieve its total number of entries.
    * @return             the number of entries in row #<code>position</code>
    */
     public int getRowNumEntries(int position){
       if (rows.containsKey(position))
          return ((TIntHashSet)rows.get(position)).size();
      else return 0;
     }





    /**
     * Return true if the value at (row, col) is an empty value, false otherwise.
     * @param row the row index
     * @param col the column index
     * @return true if the value is empty, false otherwise
     */
    public boolean isValueEmpty(int row, int col){
      if (columns.containsKey(col))
	return ((Column)columns.get(col)).isValueEmpty(row);
      else return false;
    }


    /**
     * Returns true if there is data stored at (row, col) in this table
     *
     * @param row     the row number
     * @param col     the column number
     * @return        true if there is data at position (row, col), otherwise
     *                return false.
     */
    public boolean doesValueExist(int row, int col){
      if (columns.containsKey(col))
	return ((AbstractSparseColumn)columns.get(col)).doesValueExist(row);
      else return false;
    }

    /**
     * Return true if the value at (row, col) is a missing value, false otherwise.
     * @param row the row index
     * @param col the column index
     * @return true if the value is missing, false otherwise
     */
    public boolean isValueMissing(int row, int col){
       if (columns.containsKey(col))
	return ((Column)columns.get(col)).isValueMissing(row);
      else return false;
    }




   public TableFactory getTableFactory() {
      return factory;
   }




   /**
    * Returns the number of rows in this table. counting starts from 0;
    * @return     the maximal row number + 1.
    */
   public int getNumRows(){
     return numRows;
   }





  /**
   * Returns the numbers of the valid columns of row number <code>rowNumber</code>
   *
   * @param rowNumber   the index of the row which its valid column numbers are
   *                    to be retrieved.
   * @return            the valid column numbers in row no. <code>rowNumber</code>,
   *                    sorted.
   */
  public int[] getRowIndices(int rowNumber){
    int[] indices = null;
    if (rows.containsKey(rowNumber))
      indices =  ((VIntHashSet)rows.get(rowNumber)).toArray();
    if (indices == null)
      indices =  columns.keys();

    Arrays.sort(indices);

    return indices;

  }

  /**
   * Returns the numbers of all the valid columns in this table
   *
   * @return    the indices of all valid columns in this table, sorted
   */
  public  int[] getAllColumns(){
    return VHashService.getIndices(columns);
  }


  /**
   * Returns the numbers of all the valid rows in this table
   *
   * @return    the indices of all valid rows in this table, sorted
   */
  public   int[] getAllRows(){
    return VHashService.getIndices(rows);
  }



  /**
   * Returns the numbers of the valid rows of column no. <code>columnNumber</code>
   *
   * @param columnNumber    the index of the column which its valid row numbers
   *                        are to be retrieved
   * @return                the valid row numbers of column no. <code>columnNumber
   *                        </code>, sorted
   */
  public  int[] getColumnIndices(int columnNumber){
    int[] indices = null;
    if (columns.containsKey(columnNumber))
      return ((AbstractSparseColumn)columns.get(columnNumber)).getIndices();

    return indices;
  }


/**
 * Returns the Column at index <codE>pos</code>.
 *
 * @param pos   the index number of the returned Column
 * @return      the Column at index <code>pos</codE>
 */
  protected Column getColumn(int pos){
    return (Column)columns.get(pos);
  }

}//SparseTable





/*
/**
   * Creates a deep copy of this SparseTable.
   * @return  an exact copy of this SparseTable
   *
  public Table copy() {

    SparseTable st;
      try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(this);
          byte buf[] = baos.toByteArray();
          oos.close();
          ByteArrayInputStream bais = new ByteArrayInputStream(buf);
          ObjectInputStream ois = new ObjectInputStream(bais);
          st = (SparseTable)ois.readObject();
          ois.close();
          return  st;
      } catch (Exception e) {

	st = new SparseTable();

	st.copy(this);
          return  st;
      }
  }//copy


  /**
   * Returns a subset of this table, containing data from rows <code>start</code>
   * through <code>start+len</code>.
   *
   * @param start   row number from which the subset starts.
   * @param len     number of consequetive rows to be included in the subset
   * @return        a SparseTable containing rows <code>start</code> through
   *                <code>start+len</code>
   *
  public Table getSubset(int start, int len){
    SparseTable retVal = new SparseTable();

    //because a sparse table cannot be changed.
    SparseMutableTable temp = new SparseMutableTable();

    int[] columnNumbers = columns.keys();
    for (int i=0; i<columnNumbers.length; i++){
      Column subCol = ((Column)columns.get(columnNumbers[i])).getSubset(start, len);
      temp.setColumn(columnNumbers[i], (AbstractSparseColumn)subCol);
    }

    retVal.copy(temp);
    return retVal;

  }//getSubset



    public ExampleTable toExampleTable(){
      return new SparseExampleTable(this);
      }

*/