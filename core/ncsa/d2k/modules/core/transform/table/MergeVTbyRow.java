package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	MergeVTbyRow.java
*/
public class MergeVTbyRow extends ncsa.d2k.core.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "First of the two tables to merge.";
			case 1: return "The second of the two tables to merge.";
			case 2: return "This string is the label of the attribute to be used as the key.";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl","ncsa.d2k.modules.core.datatype.table.basic.TableImpl","java.lang.String"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "This is the merged table.";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "Overview: This module will merge two tables by, for each row, appending     the columns of one"+
			" table to the other.    <p>      Detailed Description: The key passed in is used to identify"+
			" the column       of both tables that contains the unique ID field. Then for each row in   "+
			"    the table, it is determined if there is row in the other table sharing       the same key"+
			" value from that key column. If there is, the columns from       one table row is appended to"+
			" the columns for the other table row. If       there is no match, the attribute values from"+
			" the table row that could       not be matched is placed into the resulting table, the other"+
			" attribute       values are set to filler values.    </p>    <p>      Data Type Restrictions:"+
			" Obviously, this module will not work if the       tables do not share an attribute named by"+
			" the unique ID which is used to       match the rows.    </p>";
	}

	/**
		PUT YOUR CODE HERE.
	*/

	TableImpl returnTable;
	int counter = 0;

	//begin setting Properties
	String fillerString = new String("*");
	int fillerNumeric = 0;
	boolean fillerBol = false;
	byte[] fillerByte = new byte[1];
	char[] fillerChar = new char[1];


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
	public void setFillerbyte(byte[] c){
		fillerByte = c;
	}
	public byte[] getFillerbyte(){
		return fillerByte;
	}
	public void setFillerChar(char[] d){
		fillerChar = d;
	}
	public char[] getFillerChar(){
		return fillerChar;
	}
	//end setting Properties
	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [2];
		pds[0] = new PropertyDescription ("fillerBol", "Boolean Column Filler", "This value will be used to fill boolean columns.");
		pds[1] = new PropertyDescription ("fillerString", "String Column Filler", "This string fills the string columns.");
		return pds;
	}


	MutableTableImpl table1, table2;
	Object[] fill;

	public void doit() throws Exception {
		counter = 0;
		table1 = (MutableTableImpl) pullInput(0);
		table2 = (MutableTableImpl) pullInput(1);
		String[] uid = (String[]) pullInput(2);

		//System.out.println(uid[0]);
		Column col1 = findColumn(uid[0], table1);
		Column col2 = findColumn(uid[0], table2);

		int index1 = findIndex(col1, table1);
		int index2 = findIndex(col2, table2);

		if ( (col1 == null) || (col2 == null) ) {
			//push empty table and exit
			System.out.println("col1 or 2 is null");
			returnTable = (TableImpl)DefaultTableFactory.getInstance().createTable();
			pushOutput(returnTable, 0);

		}
		else {

			boolean test = isOK(col1, col2);

			if (!test){
				System.out.println("the test was not ok");
				returnTable = (TableImpl)DefaultTableFactory.getInstance().createTable();
				pushOutput(returnTable, 0);
			}
			else {

				int numCol1 = table1.getNumColumns();
				int numCol2 = table2.getNumColumns();

				//returnTable = new VerticalTable(numCol1 + numCol2);

				//returnTable.suggestCapacity(table1.getNumRows() + table2.getNumRows());
				//System.out.println("#rows of returntable is "+returnTable.getNumRows());
				fill = new Object[numCol1 + numCol2];

				fillerByte[0] = 0;
				fillerChar[0] = '*';
				determineFillers();
				System.out.println("# columns in table1 is "+numCol1);
				System.out.println("# columns in table2 is "+numCol2);
				System.out.println("index 1 is "+index1);
				System.out.println("index 2 is "+index2);

				/*System.out.println("here is table1 in makeBigTable: ");
				table1.print();
				System.out.println("here is table2 in makeBigTable: ");
				table2.print();
				*/

				table1.sortByColumn(index1);
				table2.sortByColumn(index2);

				/*System.out.println("here is table1 in makeBigTable: ");
				table1.print();
				System.out.println("here is table2 in makeBigTable: ");
				table2.print();
				*/

				table1.swapColumns(numCol1-1, index1);
				table2.swapColumns(0, index2);

				Column[] colArray = new Column[numCol1+numCol2-1];
				for (int m=0; m<(numCol1); m++){
					colArray[m] = (Column) ((table1.getColumn(m)).getClass()).newInstance();
				}

				for (int h=0; h<(numCol2-1); h++){
					colArray[h+numCol1] = (Column) ((table2.getColumn(h+1)).getClass()).newInstance();
				}

				returnTable = (TableImpl)DefaultTableFactory.getInstance().createTable(colArray);

				for (int u=0; u<(numCol1); u++){
					returnTable.getColumn(u).setLabel(table1.getColumnLabel(u));
				}

				for (int v=0; v<(numCol2-1); v++){
					returnTable.getColumn(v+numCol1).setLabel(table2.getColumnLabel(v+1));
				}

				returnTable.setNumRows(table1.getNumRows());

				/*System.out.println("here is table1 in makeBigTable: ");
				table1.print();
				System.out.println("here is table2 in makeBigTable: ");
				table2.print();
				*/

				makeBigTable();

				pushOutput(returnTable, 0);
			}

		}
	}

	protected void makeBigTable(){
		boolean smallerOnLeft;
		TableImpl smallTable, largeTable;
		Column smallCol, largeCol;
		if ( table1.getNumRows() < table2.getNumRows() ) {
			smallTable = table1;
			smallCol = table1.getColumn(table1.getNumColumns()-1);
			largeTable = table2;
			largeCol = table2.getColumn(0);
			smallerOnLeft = true;

		}
		else {
			smallTable = table2;
			smallCol = table2.getColumn(0);
			largeTable = table1;
			largeCol = table1.getColumn(table1.getNumColumns()-1);
			smallerOnLeft = false;
		}

		/*System.out.println("here is table1 in makeBigTable: ");
		table1.print();
		System.out.println("here is table2 in makeBigTable: ");
		table2.print();
		*/

		int count = 0;
		boolean[] isDuplicated = new boolean[smallCol.getNumRows()];

		for (int g=0; g<(smallCol.getNumRows()); g++){
			Object ob1 = smallCol.getRow(g);
			boolean keepgoing = true;
			if (g != 0) {
					Object ob1a = smallCol.getRow(g-1);
					if ((ob1a.equals(ob1)) && (isDuplicated[g-1])){
						//found duplicate in smaller Column, go back one
						//from count and add that row into returntable
						System.out.println("entered conditional");
						addFullRow(smallTable, largeTable, smallerOnLeft, g, count-1);
						isDuplicated[g] = true;
						keepgoing = false;
					}
			}
			while (keepgoing) {
				//Obj ob2 = largeCol.getRow(count);
				/*comparing rows code*/
				System.out.println("count is "+count);
				Class clas1 = (largeCol).getClass();
				System.out.println("the class name is "+clas1.getName());
				int comp = compare(largeCol.getRow(count), smallCol.getRow(g), clas1);
				if (comp < 0){
					//largeCol row < smallCol row
					//add row of longTable to new table
					addHalfRow(largeTable, true, smallerOnLeft, count);
					if ((g == (smallCol.getNumRows()-1)) && (count == largeTable.getNumRows()-1))
						addHalfRow(smallTable, false, smallerOnLeft, g);
					count++;
				}
				else { if (comp == 0) {
							addFullRow(smallTable, largeTable, smallerOnLeft, g, count);
							count++;
							isDuplicated[g] = true;
						}
						else { //largeCol row > smallCol row
									keepgoing = false;
									if (!isDuplicated[g])
										addHalfRow(smallTable, false, smallerOnLeft, g);
									if ((g == (smallCol.getNumRows()-1)) && (count == largeTable.getNumRows()-1))
										addHalfRow(largeTable, true, smallerOnLeft, count);
								}
						}
				}
			}

		if (count < (largeTable.getNumRows()-1) ) {
			System.out.println("inside last conditional");
			for (int j=count; j<largeTable.getNumRows(); j++){
				addHalfRow(largeTable, true, smallerOnLeft, j);
			}
		}


	}
	protected boolean isOK(Column col1, Column col2){
		boolean test = true;
		Hashtable ht1 = new Hashtable();
		Hashtable ht2 = new Hashtable();
		Hashtable ht3 = new Hashtable();

		for (int i=0; i< col1.getNumRows(); i++){
			Object obj = col1.getRow(i);
			if (!ht1.contains(obj))
				ht1.put(obj.toString(), obj);
			else {
				if (!ht2.contains(obj))
					ht2.put(obj.toString(), obj);
			}
		}
		for (int j=0; j< col2.getNumRows(); j++){
			Object obj2 = col2.getRow(j);
			if (!ht3.contains(obj2))
				ht3.put(obj2.toString(), obj2);
			else {
				if (ht2.contains(obj2))
					test = false;
			}
		}
		return test;
	}

	protected Column findColumn(String uid, TableImpl table){
		Column col = null;
		System.out.println("the string here is "+uid);
		for (int i=0; i<(table.getNumColumns()); i++){
			System.out.println(i+"  "+table.getColumn(i).getLabel());
			if (table.getColumn(i).getLabel().equals(uid)){
				System.out.println("matching column label is "+table.getColumn(i).getLabel());
				col = table.getColumn(i);
			}
		}

		return col;
	}

	protected int findIndex(Column col, TableImpl table){
		int index = -1;
		for (int j=0; j<(table.getNumColumns()); j++){
			if (table.getColumn(j).equals(col)){
				System.out.println("match in FindIndex at j="+j);
				index = j;
			}
		}
		return index;
	}

	protected void addFullRow( TableImpl smallTable, TableImpl largeTable, boolean smallOnLeft, int rowNum1, int rowNum2){

		int smallNum = smallTable.getNumColumns();
		int largeNum = largeTable.getNumColumns();
		//System.out.println("counter in addFullRow is "+counter);
		//System.out.println("capacity is "+returnTable.getCapacity());
		if (counter >= returnTable.getNumRows()){
			System.out.println("counter is too big.  it is "+counter);
			System.out.println("capacity is "+returnTable.getNumRows());
			returnTable.setNumRows(counter+1);
		}
		if (smallOnLeft) {
			for (int j=0; j<smallTable.getNumColumns(); j++){
				returnTable.getColumn(j).setRow( smallTable.getColumn(j).getRow(rowNum1), counter);
			}
			for (int h=0; h<largeTable.getNumColumns(); h++){
				returnTable.getColumn(h+smallNum-1).setRow( largeTable.getColumn(h).getRow(rowNum2), counter);
			}
		}
		else {
			for (int j=0; j<largeTable.getNumColumns(); j++){
				returnTable.getColumn(j).setRow( largeTable.getColumn(j).getRow(rowNum2), counter);
			}
			for (int h=0; h<smallTable.getNumColumns(); h++){
				returnTable.getColumn(h+largeNum-1).setRow( smallTable.getColumn(h).getRow(rowNum1), counter);
			}
		}
		counter++;
		return;
	}

	protected void addHalfRow( TableImpl table, boolean isLarge, boolean smallOnLeft, int rowNum) {

		int cNum = table.getNumColumns();
		try {
			if (counter >= returnTable.getNumRows()){
				System.out.println("counter is too big.  it is "+counter);
				System.out.println("capacity is "+returnTable.getNumRows());
				returnTable.setNumRows(counter+1);
			}
		} catch (NullPointerException e){
			System.out.println("nullpointer exception");
			returnTable.setNumRows(2);}
		if (!(isLarge ^ smallOnLeft)){
			//add half row on the right
			for (int g= cNum-1; g<returnTable.getNumColumns();g++){
				returnTable.getColumn(g).setRow( table.getColumn(g-cNum+1).getRow(rowNum), counter);
			}
			//add fillers
			for (int i=0; i<(cNum-1); i++){
				returnTable.getColumn(i).setRow( fill[i], counter);
			}
		}
		else {	//add half row on the left
				for (int h=0; h< cNum; h++){
					returnTable.getColumn(h).setRow( table.getColumn(h).getRow(rowNum), counter);
				}
				//add fillers
				for (int j=cNum; j<returnTable.getNumColumns();j++){
					returnTable.getColumn(j).setRow( fill[j], counter);
				}
		}

		counter++;
		return;

	}

	protected void determineFillers(){

		int num1 = table1.getNumColumns();
		int num2 = table2.getNumColumns();
		for (int i=0; i<num1; i++){
			if (table1.getColumn(i) instanceof StringColumn)
				fill[i] = fillerString;
			else { if (table1.getColumn(i) instanceof NumericColumn)
						fill[i] = (Object) new Integer(fillerNumeric);
					else { if (table1.getColumn(i) instanceof BooleanColumn)
								fill[i] = (Object) new Boolean(fillerBol);
							else { if (table1.getColumn(i) instanceof ByteArrayColumn)
										fill[i] = fillerByte;
									else { if (table1.getColumn(i) instanceof CharArrayColumn)
												fill[i] = fillerChar;
											else {
													fill[i] = null;
											}
									}
							}
					}
			}
		}

		for (int j=num1; j<(num2+num1); j++){
			if (table2.getColumn(j-num1) instanceof StringColumn)
				fill[j] = fillerString;
			else { if (table2.getColumn(j-num1) instanceof NumericColumn)
						fill[j] = (Object) new Integer(fillerNumeric);
					else { if (table2.getColumn(j-num1) instanceof BooleanColumn)
								fill[j] = (Object) new Boolean(fillerBol);
							else { if (table2.getColumn(j-num1) instanceof ByteArrayColumn)
										fill[j] = fillerByte;
									else { if (table2.getColumn(j-num1) instanceof CharArrayColumn)
												fill[j] = fillerChar;
											else {
													fill[j] = null;
											}
									}
							}
					}
			}
		}
	}

	protected int compare( Object ob1, Object ob2, Class clas1 ) {
		Column col1;
		Object o;
		System.out.println("the class name is "+clas1.getName());
		try {
			o = clas1.newInstance();
		}
		catch(Exception e) {
			System.out.println("exception in compare method");
			o = new IntColumn();
		}
			if ( o instanceof StringColumn){
				col1 = new StringColumn(1);
				col1.setRow((String) ob1, 0);
				col1.setNumRows(2);
				col1.setRow((String) ob2, 1);
			}
			else { if ( o instanceof DoubleColumn){
						col1 = new DoubleColumn(1);
						col1.setRow((Double) ob1, 0);
						col1.setNumRows(2);
						col1.setRow((Double) ob2, 1);
					}
					else { if ( o instanceof FloatColumn){
								col1 = new FloatColumn(1);
								col1.setRow((Float) ob1, 0);
								col1.setNumRows(2);
								col1.setRow((Float) ob2, 1);
							}
							else { if ( o instanceof IntColumn){
										col1 = new IntColumn(1);
										col1.setRow((Integer) ob1, 0);
										col1.setNumRows(2);
										col1.setRow((Integer) ob2, 1);
									}
									else { if ( o instanceof ShortColumn){
												col1 = new ShortColumn(1);
												col1.setRow((Short) ob1, 0);
												col1.setNumRows(2);
												col1.setRow((Short) ob2, 1);
											}
											else { if ( o instanceof LongColumn){
														col1 = new LongColumn(1);
														col1.setRow((Long) ob1, 0);
														col1.setNumRows(2);
														col1.setRow((Long) ob2, 1);
													}
													else { if ( o instanceof BooleanColumn){
																col1 = new BooleanColumn(1);
																col1.setRow((Boolean) ob1, 0);
																col1.setNumRows(2);
																col1.setRow((Boolean) ob2, 1);
															}
															else { if ( o instanceof ByteArrayColumn){
																		col1 = new ByteArrayColumn(1);
																		col1.setRow((Byte[]) ob1, 0);
																		col1.setNumRows(2);
																		col1.setRow((Byte[]) ob2, 1);
																	}
																	else {
																			col1 = new CharArrayColumn(1);
																			col1.setRow((Character[]) ob1, 0);
																			col1.setNumRows(2);
																			col1.setRow((Character[]) ob2, 1);
																	}
															}
													}
											}
									}
							}
					}
			}

		col1.setRow(ob1, 0);
		col1.setRow(ob2, 1);
		int answer = col1.compareRows(0, 1);
		return answer;

	}



	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Merge By Row";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "First Table";
			case 1:
				return "Second Table";
			case 2:
				return "ID Field";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Result Table";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

