package ncsa.d2k.modules.core.discovery.ruleassociation;




import ncsa.d2k.modules.core.datatype.table.*;

/**
	ConvertVTToSets.java
*/
public class ConvertTableToItemSets extends ncsa.d2k.core.modules.DataPrepModule
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "This is the input table that will be converted into an <i>ItemSet</i>     object used in rule association.";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "This is the itemset object representation.";
			case 1: return "This is a list of the names of the output attributes.";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.ItemSets","[Ljava.lang.String;"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "Overview: Read in a table and convert it into an <i>ItemSets</i> data     structure.    <p>"+
			"      Detailed Description: First, the unique items possible for all sets must       be determined."+
			" To do this, we take all the input and output names and       append each unique value for that"+
			" input or output name to the name to       define a unique id. With this done, each set consists"+
			" of the list of all       these value for each row. The resulting form representation of the"+
			" sets       is very efficient, but still requires an in memory representation for       each"+
			" row of the table. The names of the output columns is also passed       from this module.  "+
			"  </p>    <p>      Scalability: An representation of each row of the table must be stored  "+
			"     in memory. The representation is usually smaller than the originally       data.    </p>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		ItemSets iss = new ItemSets((Table)this.pullInput(0));
		if (iss.targetNames != null)
			this.pushOutput(iss.targetNames, 1);
		this.pushOutput(iss,0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Table To Sets";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
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
				return "Item Sets";
			case 1:
				return "Output Names";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

