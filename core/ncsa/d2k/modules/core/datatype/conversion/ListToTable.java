package ncsa.d2k.modules.core.datatype.conversion;


import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	ListToVT.java
*/
public class ListToTable extends ncsa.d2k.core.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "Objects to be put in the verticaltable ";
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
			case 0: return "Contains the objects from the list in single column ";
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
		return "<html>  <head>      </head>  <body>    For each item in the list, create a row in the table. The table will only     have one column.  </body></html>";
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

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "ListToVT";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "list";
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
				return "table";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
