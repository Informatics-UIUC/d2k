package ncsa.d2k.modules.core.transform.binning;



import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	BinnedTable.java
*/

public class BinTable extends ncsa.d2k.core.modules.DataPrepModule 
{
	boolean rmvCol;
	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This is the bin tree.  ";
			case 1: return "       The example table.  ";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.BinTree","ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "       The resulting table.";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module will use a module tree to bin the input columns of the example     table passed in. If removeColumns is true, the old unbinned columns will     not be included in the resulting table.  </body></html>";
	}

	// Method : getRemoveColumns
	// Purpose: to figure out whether you would like to
	//			remove the unbinned numerical columns
	public boolean getRemoveColumns() {
		return rmvCol;
	}

	// Method : setRemoveColumns
	// Purpose: to set the variable that controls whether
	//			the user would like to remove the unbinned
	//			numberical columns
	public void setRemoveColumns(boolean b) {
		rmvCol = b;
	}

	// Method : doit
	// Purpose: to place numerical data in bins and place
	//			the bins into an example table
	public void doit() throws Exception {
		// Pulls the first input which is a Bin Tree
		BinTree BT = (BinTree) pullInput(0);
		// Pulls the second input which is an Example Tree
		ExampleTableImpl ET = (ExampleTableImpl) pullInput(1);

		// Gets the "In Variables"
		int[] ins = ET.getInputFeatures();
		// Gets the "Out Variable" NOTICE: There can only be one out variable
		int[] outs = ET.getOutputFeatures();

		// Create the array of new columns
		Column[] newCols;

		// Would you like to remove the unbinned columns?
		if (rmvCol) {
			newCols = new Column[ins.length + outs.length];
		} else {
			newCols = new Column[(2 * ins.length) + outs.length];
		}

		int currCol = 0;
		for (int i = 0; i < ins.length; i++) {
				Column Columnworking = ET.getColumn(ins[i]);
                                int numRows = Columnworking.getNumRows();

				StringColumn names = new StringColumn(numRows);
				for (int j = 0; j < numRows; j++) {
					if (Columnworking instanceof NumericColumn) {
						double d = ((NumericColumn)Columnworking).getDouble(j);
						String str = BT.getBinNameForValue(ET.getString(j, outs[0]), ET.getColumnLabel(ins[i]), d);
						if(str != null)
							names.setString(str, j);
						else
							names.setString(Double.toString(d), j);
					} else {
						String str2 = ((StringColumn)Columnworking).getString(j);
						String str3 = BT.getBinNameForValue(ET.getString(j, outs[0]), ET.getColumnLabel(ins[i]), str2);
						if(str3 != null)
							names.setString(str3, j);
						else
							names.setString(str2, j);
					}
				}
				if (!rmvCol) {
					newCols[currCol++] = Columnworking;
				}
				newCols[currCol++] = names;
				names.setLabel(ET.getColumnLabel(ins[i]) + "_Bin");
				ins[i] = currCol-1;
		}
		newCols[currCol] = ET.getColumn(outs[0]);
		outs[0] = currCol;
		ET.setColumns(newCols);
		pushOutput(ET, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Bin Table";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "BT";
			case 1:
				return "ET";
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
				return "ET";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
