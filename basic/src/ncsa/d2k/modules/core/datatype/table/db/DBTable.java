package ncsa.d2k.modules.core.datatype.table.db;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.io.sql.*;

/**
 * <p>Title: DBTable </p>
 * <p>Description: An implementation of the Table Interface for Database access </p>
 * <p>Copyright: NCSA (c) 2002</p>
 * <p>Company: </p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 */

public class DBTable extends AbstractTable implements Table {

    /* A reference for the DBTableConnection functionality */
    protected DBDataSource dataSource;
    protected DBConnection dbConnection;

    protected boolean[] isNominal;

    /**
     * Construct a DBTable with the specified DBDataSource.
     * @param _dbdatasource
     */
    public DBTable (DBDataSource _dbdatasource) {
        dataSource = _dbdatasource;
        dbConnection = null;
        isNominal = new boolean[dataSource.getNumDistinctColumns()];

        for(int i = 0; i < dataSource.getNumDistinctColumns(); i++) {
            int type = dataSource.getColumnType(i);
            if ( (type == ColumnTypes.DOUBLE) || (type == ColumnTypes.INTEGER) ||
                     (type == ColumnTypes.FLOAT) || (type == ColumnTypes.LONG) ||
                     (type == ColumnTypes.SHORT) || (type == ColumnTypes.BYTE) )
                isNominal[i] = false;
            else
                isNominal[i] = true;
        }
    }

    /**
     * @param _dbdatasource
     * @param _dbconnection
     */
    public DBTable (DBDataSource _dbdatasource, DBConnection _dbconnection) {
        dataSource = _dbdatasource;
        dbConnection = _dbconnection;

       isNominal = new boolean[dataSource.getNumDistinctColumns()];
        for(int i = 0; i < dataSource.getNumDistinctColumns(); i++) {
            int type = dataSource.getColumnType(i);
            if ( (type == ColumnTypes.DOUBLE) || (type == ColumnTypes.INTEGER) ||
                     (type == ColumnTypes.FLOAT) || (type == ColumnTypes.LONG) ||
                     (type == ColumnTypes.SHORT) || (type == ColumnTypes.BYTE) )
                isNominal[i] = false;
            else
                isNominal[i] = true;
        }
    }

    /**
	 * Get an Object from the database table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Object at (row, column)
     */
    public Object getObject(int row, int column){
        return (Object)dataSource.getObjectData(row, column).toString();
    }

    /**
	 * Get an int value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the int at (row, column)
     */
    public int getInt(int row, int column) {
        return (int)dataSource.getNumericData(row, column);
    }


    /**
	 * Get a short value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the short at (row, column)
     */
    public short getShort(int row, int column) {
        return (short)dataSource.getNumericData(row, column);
    }

    /**
	 * Get a float value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the float at (row, column)
     */
    public float getFloat(int row, int column) {
        return (float)dataSource.getNumericData(row, column);
    }

    /**
	 * Get a double value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the double at (row, column)
     */
    public double getDouble(int row, int column) {
        return (double)dataSource.getNumericData(row, column);
    }

    /**
	 * Get a long value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the long at (row, column)
     */
     public long getLong(int row, int column){
        return (long)dataSource.getNumericData(row, column);
    }

    /**
	 * Get a String value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the String at (row, column)
     */
    public String getString(int row, int column) {
        return dataSource.getTextData(row, column);
    }

    /**
	 * Get a value from the table as an array of bytes.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of bytes
     */
    public byte[] getBytes(int row, int column) {
        return dataSource.getTextData(row, column).getBytes();
    }

    /**
	 * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public boolean getBoolean(int row, int column){
        return new Boolean(dataSource.getTextData(row, column)).booleanValue();
    }

    /**
	 * Get a value from the table as an array of chars.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of chars
     */
    public char[] getChars(int row, int column){
        return dataSource.getTextData(row, column).toCharArray();
    }

    /**
	 * Get a byte value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the byte value at (row, column)
     */
    public byte getByte(int row, int column){
        return dataSource.getTextData(row, column).getBytes()[0];
    }

    /**
	 * Get a char value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the char value at (row, column)
     */
    public char getChar(int row, int column){
        return dataSource.getTextData(row, column).toCharArray()[0];
    }
	//// Accessing Table Metadata
	/**
		Returns the name associated with the column.
		@param position the index of the Column name to get.
		@returns the name associated with the column.
	*/
	public String getColumnLabel(int position){
            return dataSource.getColumnLabel(position);
        }

	/**
		Returns the comment associated with the column.
		@param position the index of the Column name to get.
		@returns the comment associated with the column.
	*/
	public String getColumnComment(int position){
            return dataSource.getColumnComment(position);
        }

	/**
	  	Get the number of rows in this Table.  Same as getCapacity().
		@return the number of rows in this Table.
	*/
	public int getNumRows(){
            return dataSource.getNumRows();
        }

	/**
		Get the number of entries this Table holds.
		@return this Table's number of entries
	*/
	public int getNumEntries(){
            return dataSource.getNumRows();
    }

	/**
		Return the number of columns this table holds.
		@return the number of columns in this table
	*/
	public int getNumColumns(){
            return dataSource.getNumDistinctColumns();
    }

	/**
	 * Get a row from the table at the specified position.  The table will
	 * copy the entries into the buffer, in a format that is appropriate for
	 * the buffer's data type.
	 * @param buffer a buffer to copy the data into
	 * @param position the position
	*/
	public void getRow(Object buffer, int pos) {
            if(buffer instanceof int[]) {
               int[] b1 = (int[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getInt(pos, i);
            }
            else if(buffer instanceof float[]) {
               float[] b1 = (float[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getFloat(pos, i);
            }
            else if(buffer instanceof double[]) {
               double[] b1 = (double[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getDouble(pos, i);
            }
            else if(buffer instanceof long[]) {
               long[] b1 = (long[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getLong(pos, i);
            }
            else if(buffer instanceof short[]) {
               short[] b1 = (short[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getShort(pos, i);
            }
            else if(buffer instanceof boolean[]) {
               boolean[] b1 = (boolean[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getBoolean(pos, i);
            }
            else if(buffer instanceof String[]) {
               String[] b1 = (String[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getString(pos, i);
            }
            else if(buffer instanceof char[][]) {
               char[][] b1 = (char[][])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getChars(pos, i);
            }
            else if(buffer instanceof byte[][]) {
               byte[][] b1 = (byte[][])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getBytes(pos, i);
            }
            else if(buffer instanceof Object[]) {
               Object[] b1 = (Object[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getObject(pos, i);
            }
            else if(buffer instanceof byte[]) {
               byte[] b1 = (byte[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getByte(pos, i);
            }
            else if(buffer instanceof char[]) {
               char[] b1 = (char[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getChar(pos, i);
      }
        }

	/**
	 * Get a copy of the data from a column from the Table at the specified
	 * position.  The Table will copy the entries into the buffer, in a format
	 * that is appropriate for the buffer's data type.  The buffer must be an
	 * array of data that corresponds to one of the types enumerated in
	 * ColumnTypes.
	 * @param buffer a buffer to copy the data into
	 * @param position the position
	 * @see ColumnTypes
	 */
	public void getColumn(Object buffer, int pos) {
            if(buffer instanceof int[]) {
               int[] b1 = (int[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getInt(i, pos);
            }
            else if(buffer instanceof float[]) {
               float[] b1 = (float[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getFloat(i, pos);
            }
            else if(buffer instanceof double[]) {
               double[] b1 = (double[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getDouble(i, pos);
            }
            else if(buffer instanceof long[]) {
               long[] b1 = (long[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getLong(i, pos);
            }
            else if(buffer instanceof short[]) {
               short[] b1 = (short[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getShort(i, pos);
            }
            else if(buffer instanceof boolean[]) {
               boolean[] b1 = (boolean[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getBoolean(i, pos);
            }
            else if(buffer instanceof String[]) {
               String[] b1 = (String[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getString(i, pos);
            }
            else if(buffer instanceof char[][]) {
               char[][] b1 = (char[][])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getChars(i, pos);
            }
            else if(buffer instanceof byte[][]) {
               byte[][] b1 = (byte[][])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getBytes(i, pos);
            }
            else if(buffer instanceof Object[]) {
               Object[] b1 = (Object[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getObject(i, pos);
            }
            else if(buffer instanceof byte[]) {
               byte[] b1 = (byte[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getByte(i, pos);
            }
            else if(buffer instanceof char[]) {
               char[] b1 = (char[])buffer;
               for(int i = 0; i < b1.length; i++)
                  b1[i] = getChar(i, pos);
            }
        }

	/**
		Get a subset of this Table, given a start position and length.  The
		subset will be a new Table.
		@param start the start position for the subset
		@param len the length of the subset
		@return a subset of this Table
	*/
	public Table getSubset(int start, int len){
            // here we will return a TrainDBTable.
            ExampleTable et = this.toExampleTable();
            int[] trSet = new int[len];
            int ctr = 0;
            for(int i = start; i < start+len; i++) {
                trSet[ctr] = i;
                ctr++;
            }
            et.setTrainingSet(trSet);
            TrainTable tt = et.getTrainTable();
            return tt;
        }

        public Table getSubset(int[] rows) {
            ExampleTable et = this.toExampleTable();
            et.setTrainingSet(rows);
            return et.getTrainTable();
        }

        public Table getSubsetByReference(int start, int len) {
          return getSubset(start, len);
        }

        public Table getSubsetByReference(int[] rows){
          return getSubset(rows);
        }

	/**
		Create a copy of this Table.
		@return a copy of this Table
	*/
	public Table copy(){
            DBTable retVal = new DBTable(dataSource.copy(), dbConnection);
            boolean[] retIsNom = new boolean[isNominal.length];
            for(int i = 0; i < retIsNom.length; i++)
                retIsNom[i] = isNominal[i];
            retVal.isNominal = retIsNom;
            return retVal;
        }

	/**
	 * Get a TableFactory for this Table.
	 * @return The appropriate TableFactory for this Table.
	 */
	 public TableFactory getTableFactory(){
            //return dbintf.getCache().table.getTableFactory();
             return null;
        }

	 /**
	  * Returns true if the column at position contains nominal data, false
	  * otherwise.
	  * @param position the index of the column
	  * @return true if the column contains nominal data, false otherwise.
	  */
	 public boolean isColumnNominal(int position){
            return isNominal[position];
        }

	 /**
	  * Returns true if the column at position contains scalar data, false
	  * otherwise
	  * @param position
	  * @return true if the column contains scalar data, false otherwise
	  */
	 public boolean isColumnScalar(int position){
             return !isNominal[position];
        }
	 /**
	  * Set whether the column at position contains nominal data or not.
	  * @param value true if the column at position holds nominal data, false otherwise
	  * @param position the index of the column
	  */
	 public void setColumnIsNominal(boolean value, int position){
             if(value)
                isNominal[position] = true;
             else
                isNominal[position] = false;
        }

	 /**
	  * Set whether the column at position contains scalar data or not.
	  * @param value true if the column at position holds scalar data, false otherwise
	  * @param position the index of the column
	  */
	 public void setColumnIsScalar(boolean value, int position){
            if(value)
                isNominal[position] = false;
            else
                isNominal[position] = true;
        }

	 /**
	  * Returns true if the column at position contains only numeric values,
	  * false otherwise.
	  * @param position the index of the column
	  * @return true if the column contains only numeric values, false otherwise
	  */
	 public boolean isColumnNumeric(int position){
        int type = getColumnType(position);
        switch(type) {
            case(ColumnTypes.BYTE):
            case(ColumnTypes.DOUBLE):
            case(ColumnTypes.FLOAT):
            case(ColumnTypes.INTEGER):
            case(ColumnTypes.LONG):
            case(ColumnTypes.SHORT):
                return true;
            default:
                return false;
        }
     }

	 /**
	  * Return the type of column located at the given position.
	  * @param position the index of the column
	  * @return the column type
	  * @see ColumnTypes
	  */
	 public int getColumnType(int position){
            return this.dataSource.getColumnType(position);
     }

	 /**
	  * Return this Table as an ExampleTable.
	  * @return This object as an ExampleTable
	  */
	public ExampleTable toExampleTable(){
            return new DBExampleTable(this, dataSource.copy(), dbConnection);
    }

    public boolean isValueMissing(int row, int col) {
	    if (this.getObject(row,col) == null)
                return true;
            else
        return false;
	}

	public boolean isValueEmpty(int row, int col) {
	    if (this.getObject(row,col) == null)
                return true;
            else
        	return false;
	}

/*	public Number getScalarMissingValue(int col) {
            return new Double(Double.NEGATIVE_INFINITY);
	}

	public String getNominalMissingValue(int col) {
            return "?";
	}

	public Number getScalarEmptyValue(int col) {
            return new Double(Double.POSITIVE_INFINITY);
	}

	public String getNominalEmptyValue(int col) {
            return ".";
	}*/

       /**
        * Return true if any value in this Table is missing.
        * @return true if there are any missing values, false if there are no missing values
        */
       public boolean hasMissingValues() {
        for(int i = 0; i < getNumColumns(); i++)
          for(int j = 0; j < getNumRows(); j++)
            if(isValueMissing(j, i))
              return true;
        return false;
       }

} //DBTable