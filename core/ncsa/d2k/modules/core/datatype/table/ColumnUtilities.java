package ncsa.d2k.modules.core.datatype.table;

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
		DoubleColumn dc = new DoubleColumn(sc.getCapacity());
		for(int i = 0; i < sc.getCapacity(); i++)
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
		IntColumn dc = new IntColumn(sc.getCapacity());
		for(int i = 0; i < sc.getCapacity(); i++)
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
		LongColumn dc = new LongColumn(sc.getCapacity());
		for(int i = 0; i < sc.getCapacity(); i++)
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
		ShortColumn dc = new ShortColumn(sc.getCapacity());
		for(int i = 0; i < sc.getCapacity(); i++)
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
		FloatColumn dc = new FloatColumn(sc.getCapacity());
		for(int i = 0; i < sc.getCapacity(); i++)
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
		BooleanColumn dc = new BooleanColumn(sc.getCapacity());
		for(int i = 0; i < sc.getCapacity(); i++)
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
		ObjectColumn dc = new ObjectColumn(sc.getCapacity());
		for(int i = 0; i < sc.getCapacity(); i++)
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
		StringColumn dc = new StringColumn(sc.getCapacity());
		for(int i = 0; i < sc.getCapacity(); i++)
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
    

}
