package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
public class AppendTables extends ncsa.d2k.core.modules.DataPrepModule {

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
			" a new table containing the contents of both.    </p>    <p>      Detailed Description: This"+
			" module first determines how may columns both       tables contain. If the tables do not contain"+
			" the same number of columns,       or if the columns do not share the same names, they will"+
			" not be       appended, and this module will throw an exception. If the new table can      "+
			" be constructed, this module will construct the new table in memory, and       then use it to"+
			" initialize another new table of the same type as the       second input table.    </p>    <p>"+
			"      Scalability: All the data from the original tables is copied into a new       table memory"+
			" based table, so this module will require an amount of       memory sufficient to contain the"+
			" entire contents of the two input tables.    </p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Append Table";
	}
	public void doit() throws Exception {
		Table t1 = (Table)this.pullInput(0);
		Table t2 = (Table)this.pullInput(1);
		if (t1.getNumColumns() != t2.getNumColumns()) {
			throw new Exception ("To append tables, they must have the same number of columns.");
		}

		// First, hash the column names with the contents of that column.
		HashMap colMap1 = new HashMap();
		for (int i = 0 ; i < t1.getNumColumns() ; i++) {
			colMap1.put(t1.getColumnLabel(i),new Integer(i));
		}

		// Now, append each column of the first table to each column of the second
		// table to create a new column.
		Column[] cols = new Column[t1.getNumColumns()];
		int combinedSize = t1.getNumRows() + t2.getNumRows();

		for (int i = 0 ; i < cols.length; i++) {
			cols[i] = ColumnUtilities.createColumn(t2.getColumnType(i), combinedSize);
			String label = t2.getColumnLabel(i);
			Integer tmp = (Integer) colMap1.get(label);
			if (tmp == null)
				throw new Exception("Two tables to append must have columns sharing the same names. The second table had no column named "+label);
			int otherCol = tmp.intValue();

			// set the values from table one.
			int row = 0;;
			switch (t2.getColumnType(i)) {
				case ColumnTypes.INTEGER:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setInt(t2.getInt(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setInt(t1.getInt(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.FLOAT:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setFloat(t2.getFloat(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setFloat(t1.getFloat(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.DOUBLE:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setDouble(t2.getDouble(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setDouble(t1.getDouble(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.SHORT:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setShort(t2.getShort(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setShort(t1.getShort(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.LONG:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setLong(t2.getLong(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setLong(t1.getLong(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.STRING:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setString(t2.getString(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setString(t1.getString(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.CHAR_ARRAY:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setChars(t2.getChars(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setChars(t1.getChars(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.BYTE_ARRAY:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setBytes(t2.getBytes(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setBytes(t1.getBytes(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.BOOLEAN:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setBoolean(t2.getBoolean(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setBoolean(t1.getBoolean(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.OBJECT:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setObject(t2.getObject(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setObject(t1.getObject(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.BYTE:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setByte(t2.getByte(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setByte(t1.getByte(newrow, otherCol), row);
					}
					break;
				case ColumnTypes.CHAR:
					for (; row < t2.getNumRows(); row++) {
						cols[i].setChar(t2.getChar(row, i), row);
					}
					for (int newrow = 0 ; newrow < t1.getNumRows(); row++, newrow++) {
						cols[i].setChar(t1.getChar(newrow, otherCol), row);
					}
					break;
				default: throw new Exception("Datatype was not recognized when appending tables.");
			}
		}
		TableImpl mti = new TableImpl(cols);
		this.pushOutput(t2.getTableFactory().createExampleTable(mti), 0);
	}
}
