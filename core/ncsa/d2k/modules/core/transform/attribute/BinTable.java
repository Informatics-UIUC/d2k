package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.util.*;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.util.datatype.ExampleTable;
import ncsa.d2k.modules.core.datatype.*;

/**
	BinnedTable.java
*/

public class BinTable extends ncsa.d2k.infrastructure.modules.DataPrepModule implements Serializable
{
	boolean rmvCol;
	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"BT\">    <Text>This is the bin tree.</Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"ET\">    <Text> The example table.</Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.BinTree","ncsa.d2k.util.datatype.ExampleTable"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"ET\">    <Text> </Text>The resulting table.</Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.util.datatype.ExampleTable"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Bin Table\">    <Text>This module will use a module tree to bin the input columns of the example table passed in. If removeColumns is true, the old unbinned columns will not be included in the resulting table.</Text>  </Info></D2K>";

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
		ExampleTable ET = (ExampleTable) pullInput(1);

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
				ins[i] = currCol;
				newCols[currCol++] = names;
				names.setLabel(ET.getColumnLabel(ins[i]) + "_Bin");
		}
		newCols[currCol] = ET.getColumn(outs[0]);
		outs[0] = currCol;
		ET.setInternal(newCols);
		pushOutput(ET, 0);
	}
}
