package ncsa.d2k.modules.core.datatype.table.weighted;
//package ncsa.d2k.modules.projects.clutter.weighted;

import java.io.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * <p>Title: WeightedTable </p>
 * <p>Description: WeightedTable</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Clutter, Sameer Mathur
 * @version 1.0
 */

public class WeightedTable implements Table {
    protected Table original;
    //protected int[] rowIndices; // array of rowIndex into the physical Table
    protected double[] rowWeights;

    protected WeightedTable() { }

    /**
     * Create and Initialize Weighted Table
     * Note: This constructor is for *local testing* purpose only
     * @param orig - The Original Table
     */
    public WeightedTable(Table orig) {
        original = orig;
        if(orig instanceof WeightedTable) {
            //rowIndices = ((WeightedTable)orig).rowIndices;
            rowWeights = ((WeightedTable)orig).rowWeights;
            original = ((WeightedTable)orig).original;
        }

        rowWeights = new double[orig.getNumRows()];
    }

    public double getRowWeight(int row) {
        return rowWeights[row];
    }

    public void setRowWeight(double weight, int row) {
        rowWeights[row] = weight;
    }

    public double[] getAllRowWeights() {
        return rowWeights;
    }

    public void setAllRowWeights(double[] weights) {
        rowWeights = weights;
    }

    /*public void setRowWeights(int [] weights) {
        int tot = 0;
        for(int i = 0; i < weights.length; i++) {
          tot += weights[i];
        }
        int[] indices = new int[tot];
        int ctr = 0;
        for(int i = 0; i < weights.length; i++) {
          int num = weights[i];
          for(int j = 0; j < num; j++) {
              indices[ctr] = i;
              ctr++;
          }
        }
        setRowIndices(indices);
    }*/

    /**
     * This initializer function will be called from outside to setup the array
     * row indices that reference the actual physical table
     * @param rows - the array of row indices
     */
    /*public void setRowIndices(int[] rows) {
        rowIndices = rows;
    }*/

    /**
     * Get Object from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Object value at (row, column)
     */
    public Object getObject(int row, int column) {
        return original.getObject(row, column);
    }

    /**
     * Get Int from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Int value at (row, column)
     */
    public int getInt(int row, int column) {
        return original.getInt(row, column);
    }

    /**
     * Get Short from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Short value at (row, column)
     */
    public short getShort(int row, int column) {
        return original.getShort(row, column);
    }

    /**
     * Get Float from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Float value at (row, column)
     */
    public float getFloat(int row, int column) {
        return original.getFloat(row, column);
    }

    /**
     * Get Double from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Double value at (row, column)
     */
    public double getDouble(int row, int column) {
        return original.getDouble(row, column);
    }

    /**
     * Get Long from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Long value at (row, column)
     */
    public long getLong(int row, int column) {
        return original.getLong(row, column);
    }

    /**
     * Get Strings from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the String value at (row, column)
     */
    public String getString(int row, int column) {
        return original.getString(row, column);
    }

    /**
     * Get Bytes from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the byte[] value at (row, column)
     */
    public byte[] getBytes(int row, int column) {
        return original.getBytes(row, column);
    }

    /**
     * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public boolean getBoolean(int row, int column) {
        return original.getBoolean(row, column);
    }

    /**
     * Get chars from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public char[] getChars(int row, int column) {
        return original.getChars(row, column);
    }

    /**
     * Get a Byte value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public byte getByte(int row, int column) {
        return original.getByte(row, column);
    }

    /**
     * Get a Char value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public char getChar(int row, int column) {
        return original.getChar(row, column);
    }

	//////////////////////////////////////
	//// Accessing Table Metadata

    /**
     * getKeyColumn
     * @return
     */
    public int getKeyColumn() {
        return original.getKeyColumn();
    }

    /**
     *
     * @param position
     */
    public void setKeyColumn(int position) {
        original.setKeyColumn(position);
    }

    /**
     *
     * @param position
     */
    public String getColumnLabel(int position) {
        return original.getColumnLabel(position);
    }

    /**
     *
     * @param position
     */
    public String getColumnComment(int position) {
        return original.getColumnComment(position);
    }

    /**
     *
     * @param position
     */
    public String getLabel() {
        return original.getLabel();
    }

    /**
     *
     * @param position
     */
    public void setLabel(String labl) {
        original.setLabel(labl);
    }

    /**
     *
     * @param position
     */
    public String getComment() {
        return original.getComment();
    }

    /**
     *
     * @param position
     */
    public void setComment(String comment) {
        original.setComment(comment);
    }

    /**
     *
     * @param position
     */
    public int getNumRows() {
        return original.getNumRows();
    }

    /**
     *
     * @param position
     */
    public int getNumEntries() {
        return getNumRows();
    }

    /**
     *
     * @param position
     */
    public int getNumColumns() {
        return original.getNumColumns();
    }

    /**
     *
     * @param position
     */
    public void getRow(Object buffer, int position) {
        original.getRow(buffer, position);
    }

    /**
     *
     * @param position
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
      * Get a subset of len rows of the Weighted Table,
      * beginning from row# start?
      * For every column in the Weighted Table :
      *   -Check the Data type
      *   -Initialize and Populate a new column
      * Finally return all the new columns as a new TableImpl
      * @param start
      * @param len
      * @return a new TableImpl
      */
    public Table getSubset(int start, int len) {
        Column[] subsetCols = new Column[this.getNumColumns()];
        for (int i = 0; i < subsetCols.length; i++) { // i : column #
            if (this.getColumnType(i) == ColumnTypes.BOOLEAN) {
                subsetCols[i] = new BooleanColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setBoolean(this.getBoolean(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.BYTE) {
                subsetCols[i] = new ByteColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setByte(this.getByte(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.BYTE_ARRAY) {
                subsetCols[i] = new ByteArrayColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setBytes(this.getBytes(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.CHAR) {
                subsetCols[i] = new CharColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setChar(this.getChar(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.CHAR_ARRAY) {
                subsetCols[i] = new CharColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setChars(this.getChars(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.DOUBLE) {
                subsetCols[i] = new DoubleColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setDouble(this.getDouble(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.FLOAT) {
                subsetCols[i] = new FloatColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setFloat(this.getFloat(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.INTEGER) {
                subsetCols[i] = new IntColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setInt(this.getInt(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.LONG) {
                subsetCols[i] = new LongColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setLong(this.getLong(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.OBJECT) {
                subsetCols[i] = new ObjectColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setObject(this.getObject(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.SHORT) {
                subsetCols[i] = new ShortColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setShort(this.getShort(j,i), j);
            }
            else if (this.getColumnType(i) == ColumnTypes.STRING) {
                subsetCols[i] = new StringColumn(len);
                subsetCols[i].setLabel(this.getColumnLabel(i));
                subsetCols[i].setComment(this.getColumnComment(i));
                for (int j = start; j < start+len; j++)
                    subsetCols[i].setString(this.getString(j,i), j);
            }
        } //end for loop
        return  new TableImpl(subsetCols);
    } //end Table getSubset(int start, int len)

    /**
     * Create a copy of the Weighted table
     * @return Table
     */
    public Table copy() {
        WeightedTable vt;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            vt = (WeightedTable)ois.readObject();
            ois.close();
            return  vt;
        } catch (Exception e) {
            vt = new WeightedTable(original.copy());
            //vt.setRowIndices(this.rowIndices);
            vt.setKeyColumn(this.getKeyColumn());
            vt.setLabel(getLabel());
            vt.setComment(getComment());
            return  vt;
        }
    } // end  Table copy()

    /**
     * Create a TableFactory from the Weighted Table?
     * @return
     */
    public TableFactory getTableFactory() {
        return original.getTableFactory();
    }

    /**
     *
     * @param
     */
    public boolean isColumnNominal(int position) {
        return original.isColumnNominal(position);
     }

    /**
     *
     * @param
     */
     public boolean isColumnScalar(int position) {
        return original.isColumnScalar(position);
     }

    /**
     *
     * @param
     */
     public void setColumnIsNominal(boolean value, int position) {
        original.setColumnIsNominal(value, position);
     }

    /**
     *
     * @param
     */
     public void setColumnIsScalar(boolean value, int position) {
        original.setColumnIsScalar(value, position);
     }

    /**
     *
     * @param
     */
     public boolean isColumnNumeric(int position) {
        return original.isColumnNumeric(position);
     }

    /**
     *
     * @param
     */
     public int getColumnType(int position) {
        return original.getColumnType(position);
     }

     /**
      * Create a ExampleTable from the Weighted Table?
      * @return a WeightedExampleTable
      */
    public ExampleTable toExampleTable() {
         return new WeightedExampleTable(this);
    }

	public boolean isValueMissing(int row, int col) {
		//return false;//return getColumn(col).isValueMissing(row);
        return original.isValueMissing(row, col);
	}
	public boolean isValueEmpty(int row, int col) {
		//return false;// return getColumn(col).isValueEmpty(row);
        return original.isValueEmpty(row, col);
	}

	/*public void setValueToMissing(int row, int col) {
		//getColumn(col).setValueToMissing(row);
	}*/

	/*public void setValueToEmpty(int row, int col) {
		//getColumn(col).setValueToEmpty(row);
	}*/

/*	public Number getScalarMissingValue(int col) {
		//return null;//return getColumn(col).getScalarMissingValue();
        return original.getScalarMissingValue(col);
	}
	public String getNominalMissingValue(int col) {
		//return "";//return getColumn(col).getNominalMissingValue();
        return original.getNominalMissingValue(col);
	}

	public Number getScalarEmptyValue(int col) {
		//return null;//return getColumn(col).getScalarEmptyValue();
        return original.getScalarEmptyValue(col);
	}
	public String getNominalEmptyValue(int col) {
		//return "";//return getColumn(col).getNominalEmptyValue();
        return original.getNominalEmptyValue(col);
	}
    */
}