package ncsa.d2k.modules.core.transform.table;



import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
public class AppendTables extends ncsa.d2k.core.modules.DataPrepModule {
	//begin setting Properties
	String fillerString = new String("*");
	int fillerNumeric = 0;
	boolean fillerBol = false;
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

	public void setFillerBol(boolean a){
		fillerBol = a;
	}
	public boolean getFillerBol(){
		return fillerBol;
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
		pds[0] = new PropertyDescription ("fillerBol", "Boolean Column Filler", "This value fills boolean columns.");
		pds[1] = new PropertyDescription ("fillerString", "String Column Filler", "This string fills the string columns.");
		pds[2] = new PropertyDescription ("fillerNumeric", "Numeric Column Filler", "This value fills any numeric columns.");
		return pds;
	}

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<p>      First of two tables to append together.    </p>";
			case 1: return "<p>      Second of two tables to append together.    </p>";
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
			default: return "NO SUCH INPUT!";
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
			case 0: return "<p>      Newly created table containing contents of both original tables.    </p>";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Result Table";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>      Overview: This module takes two tables appending one to the other to       produce"+
			" a new table containing the contents of both.    </p>    <p>      Detailed Description: The"+
			" new table is constructed column by column. The       second tables columns are added first."+
			" For each column in the second       table, the first table is examined to determine if it contains"+
			" a column       with the same label (column name). If it does, the contents of the first   "+
			"    table are appended to the contents to the second table to construct the       new column."+
			" If the first table does not contain a column with the same       name, the filler for the data"+
			" types of the original column will be       appended to the contents of the second tables column."+
			" When we are done,       there may be columns in the first table that were not matched against"+
			"       any columns in the second table. To construct columns for this data, we       append"+
			" the the contents of the first tables column to filler. The filler       represents the rows"+
			" of the second table that did not contain a column by       the same name. The filler values"+
			" for various data types is defined in       the properties. This module does not modify either"+
			" of the original       tables.    </p>    <p>      Scalability: This module will operate on"+
			" a table that is the same type       as the first input table. How scalable this module is will"+
			" depend on the       scalability of the table implementation. On the other hand, that data "+
			"      for each complete column must fit in memory.    </p>";
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

		// First, hash the column names with the contents of that column.
		HashMap colMap1 = new HashMap();
		for (int i = 0 ; i < t1.getNumColumns() ; i++) {
			colMap1.put(t1.getColumnLabel(i),new Integer(i));
		}

		// Hash the column names with the contents of the second table.
		HashMap colMap2 = new HashMap();
		for (int i = 0 ; i < t2.getNumColumns() ; i++) {
			colMap2.put(t2.getColumnLabel(i),new Integer(i));
		}

		// Now, append each column of the first table to each column of the second
		// table to create a new column.
		MutableTable result = (MutableTable) t1.getTableFactory().createTable();
		int combinedSize = t1.getNumRows() + t2.getNumRows();
		int numColumns = t2.getNumColumns();
		int i = 0;
		for (; i < numColumns; i++) {
			String label = t2.getColumnLabel(i);
			Integer tmp = (Integer) colMap1.get(label);
			int otherCol = -1;
			if (tmp != null) {
				//throw new Exception("Two tables to append must have columns sharing the same names. The second table had no column named "+label);
				otherCol = tmp.intValue();
				colMap1.remove(label);
			}
			colMap2.remove(label);

			// set the values from table one.
			int row = 0;;
			switch (t2.getColumnType(i)) {
				case ColumnTypes.INTEGER: {

					// get the data from table 2, put it first.
					int [] vals = new int [combinedSize];
					{
						int [] s1 = new int [t2.getNumRows()];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						int [] s2 = new int [t1.getNumRows()];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						int [] s2 = new int [t1.getNumRows()];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = this.getFillerNumeric();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.FLOAT: {

					// get the data from table 2, put it first.
					float [] vals = new float [combinedSize];
					{
						float [] s1 = new float [t2.getNumRows()];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						float [] s2 = new float [t1.getNumRows()];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						float [] s2 = new float [t1.getNumRows()];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = (float)this.getFillerNumeric();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.DOUBLE: {

					// get the data from table 2, put it first.
					double [] vals = new double [combinedSize];
					{
						double [] s1 = new double [t2.getNumRows()];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						double [] s2 = new double [t1.getNumRows()];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						double [] s2 = new double [t1.getNumRows()];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = (double)this.getFillerNumeric();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.SHORT: {

					// get the data from table 2, put it first.
					short [] vals = new short [combinedSize];
					{
						short [] s1 = new short [t2.getNumRows()];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						short [] s2 = new short [t1.getNumRows()];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						short [] s2 = new short [t1.getNumRows()];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = (short) this.getFillerNumeric();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.LONG: {

					// get the data from table 2, put it first.
					long [] vals = new long [combinedSize];
					{
						long [] s1 = new long [t2.getNumRows()];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						long [] s2 = new long [t1.getNumRows()];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						long [] s2 = new long [t1.getNumRows()];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = (long)this.getFillerNumeric();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.STRING: {

					// get the data from table 2, put it first.
					String [] vals = new String [combinedSize];
					{
						String [] s1 = new String [t2.getNumRows()];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						String [] s2 = new String [t1.getNumRows()];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						String [] s2 = new String [t1.getNumRows()];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = this.getFillerString();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.CHAR_ARRAY: {

					// get the data from table 2, put it first.
					char [][] vals = new char [combinedSize][];
					{
						char [][] s1 = new char [t2.getNumRows()][];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						char [][] s2 = new char [t1.getNumRows()][];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						char [][] s2 = new char [t1.getNumRows()][];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = this.getFillerChars();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.BYTE_ARRAY: {

					// get the data from table 2, put it first.
					byte [][] vals = new byte [combinedSize][];
					{
						byte [][] s1 = new byte [t2.getNumRows()][];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						byte [][] s2 = new byte [t1.getNumRows()][];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						byte [][] s2 = new byte [t1.getNumRows()][];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = this.getFillerBytes();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.BOOLEAN: {

					// get the data from table 2, put it first.
					boolean [] vals = new boolean [combinedSize];
					{
						boolean [] s1 = new boolean [t2.getNumRows()];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						boolean [] s2 = new boolean [t1.getNumRows()];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						boolean [] s2 = new boolean [t1.getNumRows()];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = this.getFillerBol();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.OBJECT: {

					// get the data from table 2, put it first.
					Object [] vals = new Object [combinedSize];
					{
						Object [] s1 = new Object [t2.getNumRows()];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						Object [] s2 = new Object [t1.getNumRows()];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						Object [] s2 = new Object [t1.getNumRows()];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = null;
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.BYTE: {

					// get the data from table 2, put it first.
					byte [] vals = new byte [combinedSize];
					{
						byte [] s1 = new byte [t2.getNumRows()];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						byte [] s2 = new byte [t1.getNumRows()];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						byte [] s2 = new byte [t1.getNumRows()];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = this.getFillerByte();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					result.setColumnLabel(t1.getColumnLabel(i), i);
					break;
				}
				case ColumnTypes.CHAR: {

					// get the data from table 2, put it first.
					char [] vals = new char [combinedSize];
					{
						char [] s1 = new char [t2.getNumRows()];
						t2.getColumn(s1, i);
						System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					}

					// get the data from table 1, append it
					if (otherCol != -1) {
						char [] s2 = new char [t1.getNumRows()];
						t1.getColumn(s2, otherCol);
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					} else {
						char [] s2 = new char [t1.getNumRows()];
						for (int j = 0 ; j < t1.getNumRows(); j++) s2[j] = this.getFillerChar();
						System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());
					}

					// add the column
					result.addColumn(vals);
					break;
				}
				default: throw new Exception("Datatype was not recognized when appending tables.");
			}
			result.setColumnLabel(t2.getColumnLabel(i), i);
		}

		// Now for any columns in table 1 that are not accounted for, just fill them
		// and add the data from table 1 at the end.
		Iterator iter = colMap1.values().iterator();
		for ( ; iter.hasNext() ; i++) {
			int colIndex = ((Integer)iter.next()).intValue();
			switch (t1.getColumnType(colIndex)) {
				case ColumnTypes.INTEGER: {
					int [] vals = new int [combinedSize];
					int [] s1 = new int [t2.getNumRows()];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = this.getFillerNumeric();
					int [] s2 = new int [t1.getNumRows()];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}

				case ColumnTypes.FLOAT: {
					float [] vals = new float [combinedSize];
					float [] s1 = new float [t2.getNumRows()];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = (float)this.getFillerNumeric();
					float [] s2 = new float [t1.getNumRows()];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.DOUBLE: {
					double [] vals = new double [combinedSize];
					double [] s1 = new double [t2.getNumRows()];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = (double)this.getFillerNumeric();
					double [] s2 = new double [t1.getNumRows()];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.SHORT: {

					// get the data from table 2, put it first.
					short [] vals = new short [combinedSize];
					short [] s1 = new short [t2.getNumRows()];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = (short)this.getFillerNumeric();
					short [] s2 = new short [t1.getNumRows()];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.LONG: {

					// get the data from table 2, put it first.
					long [] vals = new long [combinedSize];
					long [] s1 = new long [t2.getNumRows()];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = (long)this.getFillerNumeric();
					long [] s2 = new long [t1.getNumRows()];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.STRING: {

					// get the data from table 2, put it first.
					String [] vals = new String [combinedSize];
					String [] s1 = new String [t2.getNumRows()];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = this.getFillerString();
					String [] s2 = new String [t1.getNumRows()];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.CHAR_ARRAY: {

					// get the data from table 2, put it first.
					char [][] vals = new char [combinedSize][];
					char [][] s1 = new char [t2.getNumRows()][];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = this.getFillerChars();
					char [][] s2 = new char [t1.getNumRows()][];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.BYTE_ARRAY: {

					// get the data from table 2, put it first.
					byte [][] vals = new byte [combinedSize][];
					byte [][] s1 = new byte [t2.getNumRows()][];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = this.getFillerBytes();
					byte [][] s2 = new byte [t1.getNumRows()][];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.BOOLEAN: {

					// get the data from table 2, put it first.
					boolean [] vals = new boolean [combinedSize];
					boolean [] s1 = new boolean [t2.getNumRows()];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = this.getFillerBol();
					boolean [] s2 = new boolean [t1.getNumRows()];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.OBJECT: {

					// get the data from table 2, put it first.
					Object [] vals = new Object [combinedSize];
					Object [] s1 = new Object [t2.getNumRows()];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = null;
					Object [] s2 = new Object [t1.getNumRows()];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.BYTE: {

					// get the data from table 2, put it first.
					byte [] vals = new byte [combinedSize];
					byte [] s1 = new byte [t2.getNumRows()];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = this.getFillerByte();
					byte [] s2 = new byte [t1.getNumRows()];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				case ColumnTypes.CHAR: {

					// get the data from table 2, put it first.
					char [] vals = new char [combinedSize];
					char [] s1 = new char [t2.getNumRows()];
					for(int j = 0 ; j < t2.getNumRows(); j++) s1[j] = this.getFillerChar();
					char [] s2 = new char [t1.getNumRows()];
					t1.getColumn(s2, colIndex);
					System.arraycopy(s1, 0, vals, 0, t2.getNumRows());
					System.arraycopy(s2, 0, vals, t2.getNumRows(), t1.getNumRows());

					// add the column
					result.addColumn(vals);
					break;
				}
				default: throw new Exception("Datatype was not recognized when appending tables.");
			}
			result.setColumnLabel(t1.getColumnLabel(colIndex), i);
		}
		this.pushOutput(result, 0);
	}
}

// QA comments
// 3/3/03 - Handed to QA by Tom;  Ruth starts QA
//        - Fixed some typos in documentation
// 3/4/03 - Emailed Tom;  Expect Table1 then Table2 in result - both in rows and columns. And,
//          don't expect order of columns to be shuffled.  Waiting for feedback.
