package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

/**
	Useful methods that are used on Columns.
*/
final public class ColumnUtilities {

	/**
		Create a new DoubleColumn with a copy of the data from
		sc.
		@param sc the original column
		@return a DoubleColumn initialized with the data from sc
	*/
	public static DoubleColumn toDoubleColumn(Column sc) {
		DoubleColumn dc = new DoubleColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setDouble(sc.getDouble(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		//dc.setType(new Double(0));
		return dc;
	}

	/**
		Create a new IntColumn with a copy of the data from
		sc.
		@param sc the original column
		@return an IntColumn initialized with the data from sc
	*/
	public static IntColumn toIntColumn(Column sc) {
		IntColumn dc = new IntColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setInt(sc.getInt(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		//dc.setType(new Integer(0));
		return dc;
	}

	/**
		Create a new LongColumn with a copy of the data from
		sc.
		@param sc the original column
		@return a LongColumn initialized with the data from sc
	*/
	public static LongColumn toLongColumn(Column sc) {
		LongColumn dc = new LongColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setLong(sc.getLong(i), i);
		dc.setLabel(sc.getLabel());
		//dc.setType(new Long(0));
		dc.setComment(sc.getComment());
		//dc.setType(new Long(0));
		return dc;
	}

	/**
		Create a new ShortColumn with a copy of the data from
		sc.
		@param sc the original column
		@return a ShortColumn initialized with the data from sc
	*/
	public static ShortColumn toShortColumn(Column sc) {
		ShortColumn dc = new ShortColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setShort(sc.getShort(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		//dc.setType(new Short((short)0));
		return dc;
	}

	/**
		Create a new FloatColumn with a copy of the data from
		sc.
		@param sc the original column
		@return a FloatColumn initialized with the data from sc
	*/
	public static FloatColumn toFloatColumn(Column sc) {
		FloatColumn dc = new FloatColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setFloat(sc.getFloat(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		//dc.setType(new Float(0));
		return dc;
	}

	/**
		Create a new BooleanColumn with a copy of the data from
		sc.
		@param sc the original column
		@return a BooleanColumn initialized with the data from sc
	*/
	public static BooleanColumn toBooleanColumn(Column sc) {
		BooleanColumn dc = new BooleanColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setBoolean(sc.getBoolean(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		//dc.setType(new Boolean(false));
		return dc;
	}

	/**
		Create a new CharArrayColumn with the data from sc.  The
		Objects in the new column are references to the Objects in
		the original column.
		@param sc the original column
		@return a CharArrayColumn initialized with the data from sc
	*/
	/*public static final CharArrayColumn toCharArrayColumn(Column sc) {
		CharArrayColumn dc = new CharArrayColumn(sc.getCapacity());
		for(int i = 0; i < sc.getCapacity(); i++)
			dc.setChars(sc.getChars(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		char[] ty = new char[0];
		dc.setType(ty);
		return dc;
	}*/

	/**
		Create a new ByteArrayColumn with the data from sc.  The
		Objects in the new column are references to the Objects in
		the original column.
		@param sc the original column
		@return a FloatColumn initialized with the data from sc
	*/
	/*public static final ByteArrayColumn toByteArrayColumn(AbstractColumn sc) {
		ByteArrayColumn dc = new ByteArrayColumn(sc.getCapacity());
		for(int i = 0; i < sc.getCapacity(); i++)
			dc.setBytes(sc.getBytes(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		byte[] ty = new byte[0];
		dc.setType(ty);
		return dc;
	}*/

	/**
		Create a new ObjectColumn with the data from sc.  The
		Objects in the new column are references to the Objects in
		the original column.
		@param sc the original column
		@return an ObjectColumn initialized with the data from sc
	*/
	public static ObjectColumn toObjectColumn(Column sc) {
		ObjectColumn dc = new ObjectColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setObject(sc.getObject(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		//dc.setType(new Object());
		return dc;
	}

	/**
		Create a new StringColumn with the data from sc.  The
		Objects in the new column are references to the Objects in
		the original column.
		@param sc the original column
		@return a StringColumn initialized with the data from sc
	*/
	public static StringColumn toStringColumn(Column sc) {
		StringColumn dc = new StringColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setString(sc.getString(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		//dc.setType(new String());
		return dc;
	}


    /**
       Loop through the items in column, if they can all be represented
       numerically, return true.  Otherwise return false.
       @param column the column to test
       @return true if column contains only numeric data, false otherwise
    */
    public static boolean isNumericColumn(Column column) {
        int numRows = column.getNumRows();
        for(int row = 0; row < numRows; row++) {
            try {
                Double d = Double.valueOf(column.getString(row));
            }
            catch(Exception e) {
                return false;
            }
        }
        return true;

    }


    /**
       Create a column given the type and size.
       @param type the type of column to create
       @param size the initial size of the column
       @return a new, empty column
    */
    public static Column createColumn(String type, int size) {
        if(type.compareToIgnoreCase("String") == 0)
            return new StringColumn(size);
        else if(type.compareToIgnoreCase("float") == 0)
            return new FloatColumn(size);
        else if(type.compareToIgnoreCase("double") == 0)
            return new DoubleColumn(size);
        else if(type.compareToIgnoreCase("int") == 0)
            return new IntColumn(size);
        else if(type.compareToIgnoreCase("boolean") == 0)
            return new BooleanColumn(size);
        else if(type.compareToIgnoreCase("char[]") == 0)
            return new ContinuousCharArrayColumn(size);
        else if(type.compareToIgnoreCase("byte[]") == 0)
            return new ContinuousByteArrayColumn(size);
        else if(type.compareToIgnoreCase("long") == 0)
            return new LongColumn(size);
        else if(type.compareToIgnoreCase("short") == 0)
            return new ShortColumn(size);
	else
	    return new StringColumn(size);
    }

	/**
		CopyColumn
		
		make a copy of the column data in a Table (the interface kind of Table) and return
		it as a Column (The package 'basic' kind of Column)

		@param sourceTable the table to copy the column out of
		@param colIndex which column in the table to copy
		@author pgroves
	*/
	public static Column copyColumn(Table sourceTable, int colIndex){
		int type=sourceTable.getColumnType(colIndex);
		int numRows=sourceTable.getNumRows();
		Column c;
		switch(type){
			case (ColumnTypes.INTEGER) : {
				int[] dat=new int[numRows];
				sourceTable.getColumn(dat, colIndex);
				c= new IntColumn(dat);
				break;
			}
			case (ColumnTypes.FLOAT) : {
				float[] dat=new float[numRows];	
				sourceTable.getColumn(dat, colIndex);
				c= new FloatColumn(dat);
				break;
			}
			case (ColumnTypes.SHORT) : {
				short[] dat=new short[numRows];
				sourceTable.getColumn(dat, colIndex);
				c= new ShortColumn(dat);
				break;
			}
			case (ColumnTypes.LONG) : {
				long[] dat=new long[numRows];	
				sourceTable.getColumn(dat, colIndex);
				c= new LongColumn(dat);
				break;
			}
			case (ColumnTypes.STRING) : {
				String[] dat=new String[numRows];	
				sourceTable.getColumn(dat, colIndex);
				c= new StringColumn(dat);
				break;
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				char[][] dat=new char[numRows][];
				sourceTable.getColumn(dat, colIndex);
				c= new CharArrayColumn(dat);
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				byte[][] dat=new byte[numRows][];
				sourceTable.getColumn(dat, colIndex);
				c= new ByteArrayColumn(dat);
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				boolean[] dat=new boolean[numRows];
				sourceTable.getColumn(dat, colIndex);
				c= new BooleanColumn(dat);
				break;
			}
			case (ColumnTypes.OBJECT) : {
				Object[] dat=new Object[numRows];	
				sourceTable.getColumn(dat, colIndex);
				c= new ObjectColumn(dat);
				break;
			}
			case (ColumnTypes.BYTE) : {
				byte[] dat=new byte[numRows];
				sourceTable.getColumn(dat, colIndex);
				c= new ByteColumn(dat);
				break;
			}
			case (ColumnTypes.CHAR) : {
				char[] dat=new char[numRows];	
				sourceTable.getColumn(dat, colIndex);
				c= new CharColumn(dat);
				break;
			}
			case (ColumnTypes.DOUBLE) : {
				double[] dat=new double[numRows];
				sourceTable.getColumn(dat, colIndex);
				c= new DoubleColumn(dat);
				break;

			}
			default : {
				System.err.println("ColumnUtilities:CopyColumn: Invalid Column Type");
				c= new ObjectColumn(numRows);
			}
		}
		c.setLabel(sourceTable.getColumnLabel(colIndex));
		c.setComment(sourceTable.getColumnComment(colIndex));
		return c;
	}
	/**
       Create a column given the type and size.
       @param type the type of column to create
       @param size the initial size of the column
       @return a new, empty column
    */
    public static Column createColumn(int type, int size) {
		Column c;
		switch(type){
			case (ColumnTypes.INTEGER) : {
				c= new IntColumn(size);
				break;
			}
			case (ColumnTypes.FLOAT) : {
				c= new FloatColumn(size);
				break;
			}
			case (ColumnTypes.SHORT) : {
				c= new ShortColumn(size);
				break;
			}
			case (ColumnTypes.LONG) : {
				c= new LongColumn(size);
				break;
			}
			case (ColumnTypes.STRING) : {
				c= new StringColumn(size);
				break;
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				c= new CharArrayColumn(size);
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				c= new ByteArrayColumn(size);
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				c= new BooleanColumn(size);
				break;
			}
			case (ColumnTypes.OBJECT) : {
				c= new ObjectColumn(size);
				break;
			}
			case (ColumnTypes.BYTE) : {
				c= new ByteColumn(size);
				break;
			}
			case (ColumnTypes.CHAR) : {
				c= new CharColumn(size);
				break;
			}
			case (ColumnTypes.DOUBLE) : {
				c= new DoubleColumn(size);
				break;

			}
			default : {
				System.err.println(	"ColumnUtilities:CopyColumn"+
									": Invalid Column Type");
				c= new ObjectColumn();
			}
		}
		c.setLabel("");
		return c;
	}

	/**
		This is for creating a subset from a Table interface object
		and putting it into a TableImpl object

		@param tbl the original table 
		@param colIndex which column to make a subset of
		@param subset the indices of the rows from the original 
						column to put in the new column
		@return a new Column object of the same datatype as the
				original column of tbl w/ the entries being the
				subset values				

		@author pgroves 5/30/02		
	*/
	public static Column createColumnSubset(Table tbl, int colIndex, 
											int[] subset){
		int type=tbl.getColumnType(colIndex);
		int size=subset.length;
		
		Column col=ColumnUtilities.createColumn(type,size);
		col.setLabel(tbl.getColumnLabel(colIndex));
		col.setComment(tbl.getColumnLabel(colIndex));
		switch(type){
			case (ColumnTypes.DOUBLE) : {
				for(int i=0; i<size;i++){
					col.setDouble(tbl.getDouble(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.INTEGER) : {
				for(int i=0; i<size;i++){
					col.setInt(tbl.getInt(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.FLOAT) : {
				for(int i=0; i<size;i++){
					col.setFloat(tbl.getFloat(subset[i],colIndex), i);
				}
				break;
			}
			case (ColumnTypes.SHORT) : {
				for(int i=0; i<size;i++){
					col.setShort(tbl.getShort(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.LONG) : {
				for(int i=0; i<size;i++){
					col.setLong(tbl.getLong(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.STRING) : {
				for(int i=0; i<size;i++){
					col.setString(tbl.getString(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				for(int i=0; i<size;i++){
					col.setChars(tbl.getChars(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				for(int i=0; i<size;i++){
					col.setBytes(tbl.getBytes(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				for(int i=0; i<size;i++){
					col.setBoolean(tbl.getBoolean(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.OBJECT) : {
				for(int i=0; i<size;i++){
					col.setObject(tbl.getObject(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.BYTE) : {
				for(int i=0; i<size;i++){
					col.setByte(tbl.getByte(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.CHAR) : {
				for(int i=0; i<size;i++){
					col.setChar(tbl.getChar(subset[i],colIndex),i);
				}
				break;
			}
			default : {
			}
		}
		return col;


	}
	/* DONT DELETE THIS! every function needs to cut and
		paste this switch
	
		switch(type){
			case (ColumnTypes.DOUBLE) : {
				break;
			}
			case (ColumnTypes.INTEGER) : {
				break;
			}
			case (ColumnTypes.FLOAT) : {
				break;
			}
			case (ColumnTypes.SHORT) : {
				break;
			}
			case (ColumnTypes.LONG) : {
				break;
			}
			case (ColumnTypes.STRING) : {
				break;
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				break;
			}
			case (ColumnTypes.OBJECT) : {
				break;
			}
			case (ColumnTypes.BYTE) : {
				break;
			}
			case (ColumnTypes.CHAR) : {
				break;
			}
			default : {
			}
		}
	*/
}
