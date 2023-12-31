package ncsa.d2k.modules.core.datatype.conversion;


import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	MapToVT.java
*/
public class MapToTable extends ncsa.d2k.core.modules.DataPrepModule
{
	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The contents of this map will be put into a verticaltable.";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"java.util.Map"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "This verticaltable contains the data from the map.";
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
		return "<html>  <head>      </head>  <body>    For each key-value pair in the map, create a row in the VT. Two columns     will be in the VT, one for keys and one for values. The number of rows in     the VT will be the same as the size of the map.  </body></html>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
		Map map = (Map)pullInput(0);
		int mapSize = map.size();
		ObjectColumn col1 = new ObjectColumn(mapSize); // keys
		ObjectColumn col2 = new ObjectColumn(mapSize); // values

		Set keyVal = map.keySet();
		Set dataVal = map.entrySet();
		Iterator keyI = keyVal.iterator();
		Iterator iter = dataVal.iterator();
		int position = 0;

		// put the values from map into appropriate column
		while (position < mapSize) {
			Object o = keyI.next();
			col1.setRow(o, position);
			col2.setRow(map.get(o), position);
			position++;
		}

		Column [] cc = new Column[2];
		cc[0] = col1;
		cc[1] = col2;
		Table table = DefaultTableFactory.getInstance().createTable(cc);
		// add the two columns into table
		//table.setColumn(col1,0);
		//table.setColumn(col2,1);
		pushOutput(table, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "MapToVT";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "map";
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
