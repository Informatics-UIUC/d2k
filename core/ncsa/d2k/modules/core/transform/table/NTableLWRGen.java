
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
/**
	NTableLWRGen.java
		This is a simplified version of a more complicated module
		that is not in use.  All this module does is output the
		the columns of its given VerticalTable one at a time as new
		VertialTables.  These are generally passed to an LWR model.
	@author talumbau
*/
public class NTableLWRGen extends ncsa.d2k.infrastructure.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"trainTable\">    <Text>This is the table of training data.  It is used to create the N tables of testing data for lines of best fit. </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"testTable\">    <Text>There are N output tables of test data for an LWRModel. </Text>  </Info></D2K>";
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
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"NTableLWRGen\">    <Text>The purpose of this module is to generate N tables of test data for an LWRModel.  For the 'ith' column of the xTable, a VerticalTable is created with those values at column i.  The other values in the table are copied from the row in the testTable that most closely matches the x value in column i. </Text>  </Info></D2K>";

	}

	/**
		PUT YOUR CODE HERE.
	*/
	Table xTable = null;
	Column predCol = null;
	int numFires = -1;
	int N = 0;
	int count = 0;
	public void doit() throws Exception {
		if (numFires == -1){
			xTable = (Table) pullInput(0);
			numFires = 0;
			N = xTable.getNumColumns()-1;
			predCol = xTable.getColumn(N-1).copy();
		}
		Column col = ((NumericColumn) xTable.getColumn(numFires)).copy();
		Column[] c = {col, predCol};
		Table t = TableFactory.createTable(c);
		pushOutput(t,0);
		numFires++;
	}

	public boolean isReady(){
		if (numFires < N){
			return true;
		}
		else
			return false;
	}
	public void beginExecution() {
		count = 0;
		N = 0;
		numFires = -1;
		xTable = null;
	}
}

