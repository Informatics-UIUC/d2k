/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.discovery.ruleassociation;

import ncsa.d2k.modules.core.datatype.table.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	ConvertVTToSets.java
*/
public class ConvertVTToSets extends ncsa.d2k.core.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This is the input table that will be converted into a set used in rule association.   ";
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
			case 0: return "  This is the itemset object representation.  ";
			case 1: return "      List of the names of the output attributes.  ";
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
		return "<html>  <head>      </head>  <body>    Read in a vertical table and convert it into an array of sets. I will do     this by prefixing the value found in the field with either the name of the     field if available, or a simple index. Either case, this is followed by a     &quot;^&quot; and then the value. This is sometimes required to make the attribute     value unique across fields.  </body></html>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		ItemSets iss = new ItemSets((Table)this.pullInput(0));
		if (iss.outputNames != null)
			this.pushOutput(iss.outputNames, 1);
		this.pushOutput(iss,0);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

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
				return "Sets";
			case 1:
				return "output names";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

