package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
public class AppendTables extends ncsa.d2k.core.modules.DataPrepModule {
	//begin setting Properties
	String fillerString = new String("*");
	int fillerNumeric = 0;
	boolean fillerBool = false;
	byte[] fillerByte = new byte[1];
	char[] fillerChar = new char[1];
	byte fillerSingleByte = '\000';
	char fillerSingleChar = '\000';

	public void setFillerString(String x){
		fillerString = x;
	}
	public String getFillerString(){
		return fillerString;
	}

	public void setFillerNumeric(int b){
		fillerNumeric = b;
	}
	public int getFillerNumeric(){
		return fillerNumeric;
	}

	public void setFillerBool(boolean a){
		fillerBool = a;
	}
	public boolean getFillerBool(){
		return fillerBool;
	}

	public void setFillerBytes(byte[] c){
		fillerByte = c;
	}
	public byte[] getFillerBytes(){
		return fillerByte;
	}

	public void setFillerChars(char[] d){
		fillerChar = d;
	}
	public char[] getFillerChars(){
		return fillerChar;
	}

	public void setFillerChar(char d){
		fillerSingleChar = d;
	}
	public char getFillerChar(){
		return fillerSingleChar;
	}

	public void setFillerByte(byte d){
		fillerSingleByte = d;
	}
	public byte getFillerByte(){
		return fillerSingleByte;
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [3];
		pds[0] = new PropertyDescription ("fillerBool", "Boolean Filler", "The value used to fill boolean columns.");
		pds[1] = new PropertyDescription ("fillerString", "String Filler", "The value used to fill string columns.");
		pds[2] = new PropertyDescription ("fillerNumeric", "Numeric Filler", "The value used to fill numeric columns.");
		return pds;
	}

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<p>First of two tables that will be combined.</p>";
			case 1: return "<p>Second of two tables that will be combined.</p>";
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "First Table";
			case 1:
				return "Second Table";
			default: return "No such input";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table","ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<p>Newly created table containing contents of both original tables.</p>";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0: return "Result Table";
			default: return "No such output";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
            String s = "<p>Overview: "+
            "This module takes two tables and creates a new table containing the contents of both. ";

	    s += "</p><p>Detailed Description: " +
            "This module combines two tables to create a single table that contains all of the data in the " +
            "original tables.  The data from the <i>First Table</i> appears first in the new table, "+
            "with the data from the <i>Second Table</i> appended to it.  The number of rows in the "+
            "<i>Result</i> table equals the number of rows in the <i>First Table</i> plus the number of "+
            "rows in the <i>Second Table</i>. "+

            "</p><p> "+
            "When an attribute name (column label) is common across the two input tables, a single "+
            "column with that name is created in the result table. "+
            "Columns are also created in the result table for attributes that appear in the "+
            "<i>First Table</i> or in the <i>Second Table</i> but not in both. " +
            "When the result table is populated, rows for table one are inserted first.  For these rows, "+
            "data values in columns that appear in the result table but not in the first table are set "+
            "to the <i>String</i>, <i>Boolean</i>, or <i>Numeric Filler</i> values specified "+
            "in the module properties.  Rows from table two follow, and again filler values are used "+
            "in result table columns that do not appear in the original table. ";

            s += "</p><p>Data Type Restrictions: "+
	    "If input table columns have the same name but different data types, the data type "+
            "from the <i>First Table</i> is used in the result table, and an attempt is made to "+
            "convert the data values from the <i>Second Table</i> to that type. "+
            "This conversion may result in unexpected values in the output table. In some cases, "+
            "such as when a string cannot be converted to a numeric, an exception will be raised. "+
            "The user is discouraged from appending tables containing attributes with the same name "+
            "whose types differ.  For some conversions, for example when an integer is converted "+
	    "to a double, there may be no loss of data, but the user should verify the result table "+
            "has the expected values. ";

             s += "</p><p>Data Handling: "+
             "This module does not modify either of the original tables. ";

             s += "</p><p>Scalability: This module performs its operations on a table that is of "+
             "the same type as the <i>First Table</i>.  The scalability of the module "+
             "therefore depends on the scalability properties of that Table implementation. "+
             "The module requires that the data for a complete column must be able to fit "+
             "into memory, regardless of the Table implementation being used. The module is "+
	     "optimized for Tables whose underlying implementation is column-based. </p>";

             return s;
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Append Tables";
	}

	/**
	 * append table one to table two.
	 * @throws Exception
	 */
	public void doit() throws Exception {
		Table t1 = (Table)this.pullInput(0);
		Table t2 = (Table)this.pullInput(1);

		// Hash the column names of the second table.
		HashMap colMap2 = new HashMap();
		for (int i = 0 ; i < t2.getNumColumns() ; i++) {
			colMap2.put(t2.getColumnLabel(i),new Integer(i));
		}

		// Now, create the new table of same type as table 1 and popluate it
		// column by column, starting with the columns in table 1.  After all the
		// columns labelled in table 1 are in the result table, work on any remaining
		// columns from table 2.
		// Note:  This implementation is designed to speed up operations for tables whose
		// underlying implemenation is column based, but may be poor choice for those
		// that have other implementations (row-based for example).   Since most
		// D2K are column-based, good choice for now.

		MutableTable result = (MutableTable) t1.createTable();

 		int t1Size = t1.getNumRows();
 		int t2Size = t2.getNumRows();
		int combinedSize = t1Size + t2Size;

		int resultCol = 0;
		int t1numColumns = t1.getNumColumns();
		int t2numColumns = t2.getNumColumns();
                String label = null;

		try {
		   for (int i = 0; i < t1numColumns; i++) {
                        int t2Col = -1;

			label = t1.getColumnLabel(i);
			Integer tmp = (Integer) colMap2.get(label);
			if (tmp != null) {  		// col in both
				t2Col = tmp.intValue();
				colMap2.remove(label);
			}

			// set the column values from table one.
			int row = 0;
			switch (t1.getColumnType(i)) {
				case ColumnTypes.INTEGER: {

					int [] vals = new int [combinedSize];

					// get the data from table 1, put it first.
					System.arraycopy((int[]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((int[]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
 					// or, append filler values for table 2 entries
					} else {
						int [] s2 = new int [t2Size];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = this.getFillerNumeric();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new IntColumn(vals));
					break;
				}
				case ColumnTypes.FLOAT: {

					float [] vals = new float [combinedSize];

					// get the data from table 1, put it first.
					System.arraycopy((float[]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((float[]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
 					// or, append filler values for table 2 entries
					} else {
						float [] s2 = new float [t2Size];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = (float)this.getFillerNumeric();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new FloatColumn(vals));
					break;
				}
				case ColumnTypes.DOUBLE: {

					double [] vals = new double [combinedSize];

					// get the data from table 1, put it first.
					System.arraycopy((double[]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((double[]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
					// or, append filler values for table 2 entries
					} else {
						double [] s2 = new double [t2Size];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = (double)this.getFillerNumeric();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new DoubleColumn(vals));
					break;
				}
				case ColumnTypes.SHORT: {

					short [] vals = new short [combinedSize];

					// get the data from table 1, put it first.
					System.arraycopy((short[]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((short[]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
 					// or, append filler values for table 2 entries
					} else {
						short [] s2 = new short [t2Size];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = (short)this.getFillerNumeric();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new ShortColumn(vals));
					break;
				}
				case ColumnTypes.LONG: {

					long [] vals = new long [combinedSize];

					// get the data from table 1, put it first.
					System.arraycopy((long[]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((long[]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
 					// or, append filler values for table 2 entries
					} else {
						long [] s2 = new long [t2Size];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = (long)this.getFillerNumeric();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new LongColumn(vals));
					break;
				}

				case ColumnTypes.STRING: {

					String [] vals = new String [combinedSize];

					// get the data from table 1, put it first.
					System.arraycopy((String[]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((String[]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
					// or, append filler values for table 2 entries
					} else {
						String [] s2 = new String [t2Size];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = this.getFillerString();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new StringColumn(vals));
					break;
				}
				case ColumnTypes.CHAR_ARRAY: {

					char [][] vals = new char [combinedSize][];

					// get the data from table 1, put it first.
					System.arraycopy((char[][]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((char[][]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
 					// or, append filler values for table 2 entries
					} else {
						char [][] s2 = new char [t2Size][];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = this.getFillerChars();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new CharArrayColumn(vals));
					break;
				}
				case ColumnTypes.BYTE_ARRAY: {

					byte [][] vals = new byte [combinedSize][];

					// get the data from table 1, put it first.
					System.arraycopy((byte[][]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((byte[][]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
 					// or, append filler values for table 2 entries
					} else {
						byte [][] s2 = new byte [t2Size][];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = this.getFillerBytes();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new ByteArrayColumn(vals));
					break;
				}
				case ColumnTypes.BOOLEAN: {

					boolean [] vals = new boolean [combinedSize];

					// get the data from table 1, put it first.
					System.arraycopy((boolean[]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((boolean[]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
 					// or, append filler values for table 2 entries
					} else {
						boolean [] s2 = new boolean [t2Size];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = this.getFillerBool();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new BooleanColumn(vals));
					break;
				}
				case ColumnTypes.OBJECT: {

					Object [] vals = new Object [combinedSize];

					// get the data from table 1, put it first.
					System.arraycopy((Object []) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((Object []) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
 					// or, append filler values for table 2 entries
					} else {
						Object [] s2 = new Object [t2Size];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = null;
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new ObjectColumn(vals));
					break;
				}
				case ColumnTypes.BYTE: {

					byte [] vals = new byte [combinedSize];

					// get the data from table 1, put it first.
					System.arraycopy((byte[]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((byte[]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
 					// or, append filler values for table 2 entries
					} else {
						byte [] s2 = new byte [t2Size];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = this.getFillerByte();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new ByteColumn(vals));
					break;
				}

				case ColumnTypes.CHAR: {

					char [] vals = new char [combinedSize];

					// get the data from table 1, put it first.
					System.arraycopy((char[]) t1.getColumn(i).getInternal(),
								0, vals, 0, t1Size);

					// get the data from table 2, append it
					if (t2Col != -1) {
						System.arraycopy((char[]) t2.getColumn(t2Col).getInternal(),
								0, vals, t1Size, t2Size);
 					// or, append filler values for table 2 entries
					} else {
						char [] s2 = new char [t2Size];
						for (int j = 0; j < t2Size; j++) {
						    s2[j] = this.getFillerChar();
						}
						System.arraycopy(s2, 0, vals, t1Size, t2Size);
					}

					// add the column
					result.addColumn(new CharColumn(vals));
					break;
				}
				default: {
					throw new Exception("Datatype for Table 1 column named '" +
					    label +
					    "' was not recognized when appending tables.");
  				}
			}
			result.setColumnLabel(label, resultCol++);
		    }
                } catch (NumberFormatException e) {
           		throw new NumberFormatException(
				getAlias() +
                    		": Unable to convert data for Second Table column \"" +
                                label + "\" to numeric type" +
				"\n" + e );
                }

		// Now for any columns in table 2 that are not yet accounted for,
                // fill them in for table 1 rows and add the data from table 2 at the end.
		// Retain their original order in the result table.

		tbl2cols: for (int i = 0; i < t2numColumns; i++) {

			label = t2.getColumnLabel(i);
			if (colMap2.get(label) == null) { 	// col already processed so go to next one
				continue tbl2cols;
			}

			switch (t2.getColumnType(i)) {

				case ColumnTypes.INTEGER: {

					int [] vals = new int [combinedSize];

					// use filler values for table 1 entries
					int [] s1 = new int [t1Size];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = (int)this.getFillerNumeric();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((int[])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new IntColumn(vals));
					break;
				}

				case ColumnTypes.FLOAT: {

					float [] vals = new float [combinedSize];

					// use filler values for table 1 entries
					float [] s1 = new float [t1Size];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = (float)this.getFillerNumeric();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((float[])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new FloatColumn(vals));
					break;
				}
				case ColumnTypes.DOUBLE: {

					double [] vals = new double [combinedSize];

					// use filler values for table 1 entries
					double [] s1 = new double [t1Size];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = (double)this.getFillerNumeric();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((double[])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new DoubleColumn(vals));
					break;
				}
				case ColumnTypes.SHORT: {

					short [] vals = new short [combinedSize];

					// use filler values for table 1 entries
					short [] s1 = new short [t1Size];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = (short)this.getFillerNumeric();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((short[])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new ShortColumn(vals));
					break;
				}
				case ColumnTypes.LONG: {

					long [] vals = new long [combinedSize];

					// use filler values for table 1 entries
					long [] s1 = new long [t1Size];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = (long)this.getFillerNumeric();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((short[])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new LongColumn(vals));
					break;
				}
				case ColumnTypes.STRING: {

					String [] vals = new String [combinedSize];

					// use filler values for table 1 entries
					String [] s1 = new String [t1Size];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = this.getFillerString();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((String[])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new StringColumn(vals));
					break;
				}
				case ColumnTypes.CHAR_ARRAY: {

					char [][] vals = new char [combinedSize][];

					// use filler values for table 1 entries
					char [][] s1 = new char [t1Size][];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = this.getFillerChars();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((char[][])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new CharArrayColumn(vals));
					break;
				}

				case ColumnTypes.BYTE_ARRAY: {

					byte [][] vals = new byte [combinedSize][];

					// use filler values for table 1 entries
					byte [][] s1 = new byte [t1Size][];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = this.getFillerBytes();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((byte[][])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new ByteArrayColumn(vals));
					break;
				}
				case ColumnTypes.BOOLEAN: {

					boolean [] vals = new boolean [combinedSize];

					// use filler values for table 1 entries
					boolean [] s1 = new boolean [t1Size];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = this.getFillerBool();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((boolean[])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new BooleanColumn(vals));
					break;
				}

				case ColumnTypes.OBJECT: {

					Object [] vals = new Object [combinedSize];

					// use filler values for table 1 entries
					Object [] s1 = new Object [t1Size];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = null;
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((Object[])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new ObjectColumn(vals));
					break;
				}
				case ColumnTypes.BYTE: {

					byte [] vals = new byte [combinedSize];

					// use filler values for table 1 entries
					byte [] s1 = new byte [t1Size];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = this.getFillerByte();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((byte[])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new ByteColumn(vals));
					break;
				}
				case ColumnTypes.CHAR: {

					char [] vals = new char [combinedSize];

					// use filler values for table 1 entries
					char [] s1 = new char [t1Size];
					for (int j = 0; j < t1Size; j++) {
					    s1[j] = this.getFillerChar();
					}
					System.arraycopy(s1, 0, vals, 0, t1Size);
					s1 = null;   // hint to gc

					// get the data from table 2, append it
					System.arraycopy((char[])t2.getColumn(i).getInternal(),
									 0, vals, t1Size, t2Size);

					// add the column
					result.addColumn(new CharColumn(vals));
					break;
				}
				default: {
					throw new Exception("Datatype for Table 2 column named '" +
					    label +
					    "' was not recognized when appending tables.");
	  				}
			}
			result.setColumnLabel(label, resultCol++);
		}

		// Done!
		this.pushOutput(result, 0);
	}
}

// QA comments
// 3/3/03 - Handed to QA by Tom;  Ruth starts QA
//        - Fixed some typos in documentation
// 3/4/03 - Emailed Tom;  Expect Table1 then Table2 in result - both in rows and columns. And,
//          don't expect order of columns to be shuffled.  Waiting for feedback.
//        - Heard back and okay to change.  Updated description and reordered.
// 3/5/03 - Added exception handling to catch string->numeric conversions that fail.
//        - committed to Basic.
//	  - WISH:  At some point may want implementation that's efficient for Tables that aren't
//          column-oriented.
