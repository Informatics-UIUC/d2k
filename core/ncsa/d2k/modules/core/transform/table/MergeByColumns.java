package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
/**
	MergeByColumns.java
*/
public class MergeByColumns extends ncsa.d2k.infrastructure.modules.DataPrepModule
{
	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"VerticalTable\"><Text>These verticaltable will be merged into one larger verticaltable.</Text></Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table",
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"table\"><Text>This verticaltable contains the contents of the two verticaltables, merged by column.</Text></Info></D2K>";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"MergeByColumns\"><Text>Two verticaltables are merged by similar columns into one larger one.  Each column in the final verticaltable contains the contents of the two input tables.  If there is no matching column, the column is added with filler values.</Text></Info></D2K>";
	}

	//fill two arrays with filler properties that corespond to columns of two input verticaltables
	//these arrays will be used to fill rows in the final verticaltable
		protected void 	determineFillers(Table table1, Table table2){
		int num1 = table1.getNumColumns();
		int num2 = table2.getNumColumns();
		for (int i=0; i<num1; i++){
			if (table1.getColumn(i) instanceof StringColumn)
				fill[i] = fillerString;
			else { if (table1.getColumn(i) instanceof NumericColumn)
						fill[i] = new Integer(fillerNumeric);
					else { if (table1.getColumn(i) instanceof BooleanColumn)
								fill[i] = new Boolean(fillerBol);
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
		for (int j=0; j<(num2); j++){
			if (table2.getColumn(j) instanceof StringColumn)
				fill2[j] = fillerString;
			else { if (table2.getColumn(j) instanceof NumericColumn)
						fill2[j] = new Integer(fillerNumeric);
					else { if (table2.getColumn(j) instanceof BooleanColumn)
								fill2[j] = new Boolean(fillerBol);
							else { if (table2.getColumn(j) instanceof ByteArrayColumn)
										fill2[j] = fillerByte;
									else { if (table2.getColumn(j) instanceof CharArrayColumn)
												fill2[j] = fillerChar;
											else {
													fill2[j] = null;
											}
									}
							}
					}
			}
		}
	}

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
		PUT YOUR CODE HERE.
	*/
	Object[] fill;
	Object[] fill2;
	public void doit() throws Exception {
		Table vt1 = (Table)pullInput(0);
		Table vt2 = (Table)pullInput(1);
		int numcol1 = vt1.getNumColumns();
		int numcol2 = vt2.getNumColumns();
		int numrows1 = vt1.getNumRows();
		int numrows2 = vt2.getNumRows();
		ObjectColumn col;
		fill = new Object[numcol1];
		fill2 = new Object[numcol2];
		determineFillers(vt1, vt2);
		int i = 0;
		int i2 = 0;
		Column col1;
		Column col2;
		Object type;
		String label1;
		String label2;
		int r = 0;

		//fill in the rows in vt1 & vt2 with filler data
		for (i=0; i<numcol1; i++) {
			col1 = vt1.getColumn(i);
			col = new ObjectColumn(numrows1+numrows2);
			for (r=0; r<numrows1; r++) {
				col.setRow(col1.getRow(r), r);
			}
			for (r=0; r<numrows2; r++) {
				col.setRow(fill[i], r+numrows1);
			}
			col.setLabel(vt1.getColumnLabel(i));
			vt1.setColumn(col, i);
		}
		for (i=0; i<numcol2; i++) {
			col2 = vt2.getColumn(i);
			col = new ObjectColumn(numrows1+numrows2);
			for (r=0; r<numrows1; r++) {
				col.setRow(fill2[i], r);
			}
			for (r=0; r<numrows2; r++) {
				col.setRow(col2.getRow(r), r+numrows1);
			}
			col.setLabel(vt2.getColumnLabel(i));
			vt2.setColumn(col, i);
		}
		//check for and merge columns with same label
		for (i=0; i<numcol1; i++) {
			col1 = vt1.getColumn(i);
			label1 = col1.getLabel();
			for(i2=0; i2<numcol2; i2++) {
				col2 = vt2.getColumn(i2);
				label2 = col2.getLabel();
				if (label2.equalsIgnoreCase(label1)) {
					col = new ObjectColumn(numrows1+numrows2);
					for(r=0; r<numrows1; r++) {
						col.setRow(col1.getRow(r), r);
					}
					for(r=r; r<numrows1+numrows2; r++) {
						col.setRow(col2.getRow(r), r);
					}
					col.setLabel(label1);
					vt1.setColumn(col, i);
					vt2.setColumnLabel("*", i2);
				}
			}
		}
		//add columns from vt2 that do not match columns from vt1
		for(i=0; i<numcol2; i++) {
			col2 = vt2.getColumn(i);
			label2 = col2.getLabel();
			if (label2 != "*") {
				vt1.addColumn(col2);
			}
		}
		pushOutput(vt1, 0);
	}
}
