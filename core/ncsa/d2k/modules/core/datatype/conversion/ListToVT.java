package ncsa.d2k.modules.core.datatype.conversion;

import ncsa.d2k.infrastructure.modules.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	ListToVT.java
*/
public class ListToVT extends ncsa.d2k.infrastructure.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"list\"><Text>Objects to be put in the verticaltable </Text></Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"java.util.List"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"table\"><Text>Contains the objects from the list in single column </Text></Info></D2K>";
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
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"ListToVT\"><Text>For each item in the list, create a row in the table. The table will only have one column. </Text></Info></D2K>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
		int position = 0;
		List list = (List)pullInput(0);
		int listSize = list.size();
		ObjectColumn col = new ObjectColumn(listSize);

		// put data that is in list into col
		while (position < listSize) {
			col.setRow(list.get(position), position);
			position ++;
		}
		Column [] cc = new Column[1];
		cc[0] = col;
		Table table = DefaultTableFactory.getInstance().createTable(cc);
		pushOutput(table, 0);
	}
}
