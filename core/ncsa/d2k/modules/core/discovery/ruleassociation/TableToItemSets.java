package ncsa.d2k.modules.core.discovery.ruleassociation;




import ncsa.d2k.modules.core.datatype.table.*;

/**
	TableToItemSets.java
*/
public class TableToItemSets extends ncsa.d2k.core.modules.DataPrepModule
{
	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Table To Item Sets";
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
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The table that will be converted to an <i>ItemSets</i> object.";
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
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "ItemSets";
			case 1:
				return "Target Attributes";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "The <i>ItemSets</i> object.";
			case 1: return "The names of the target attributes.";
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
                StringBuffer sb =  new StringBuffer("<p>Overview: ");
                sb.append( "This module reads a Table and converts it into an <i>ItemSets</i> data structure for use " );
                sb.append( "in mining association rules with the Apriori Algorithm. " );
                sb.append( "</p><p>Detailed Description: " );
                sb.append( "The module takes as input a Table or an Example Table, and creates an <i>ItemSets</i> object ");
		sb.append( "that encapsulates the information required by the Apriori Rule Association algorithm. ");
		sb.append( "In a typical itinerary, the <i>ItemSets</i> output port is connected to a <i>Multiple Outputs</i> ");
		sb.append( "module and then to <i>Apriori</i>, <i>Compute Confidence</i>, and <i>ItemSets To Rule Table</i> modules. ");
		sb.append( "</p><p>" );
		sb.append( "If a Table or an Example Table with no input or output attributes selected is the module input, ");
		sb.append( "all attributes (columns) will be used as possible targets for the association rules. " );
		sb.append( "If an Example Table with only input attributes or only output attributes is loaded, " );
		sb.append( "the chosen attributes will be used as possible rule antecedents and possible rule targets. " );
		sb.append( "If an Example Table with both input and output attributes is used, the inputs will be " );
		sb.append( "treated as possible rule antecedents, and the outputs as possible rule targets. " );
		sb.append( "The computational complexity of the Apriori algorithm depends upon ");
		sb.append( "the number of possible antecedents and targets, so narrowing the search prior to this step is ");
		sb.append( "highly recommended.   Use the module <i>Choose Attributes</i> to specify the subset of table ");
		sb.append( "attributes that are of interest prior to creating the ItemSets structure in this module. ");
		sb.append( "</p><p>" );
		sb.append( "The names of the target attributes the association rule builder will consider is output to the port ");
		sb.append( "<i>Target Attributes</i>,  which is connected to a <i>Compute Confidence</i> module in a typical ");
		sb.append( "itinerary. The <i>Apriori</i> module builds rules for targets individually.  That is, no ");
                sb.append( "rules will be built for combinations of the targets specified. ");
		sb.append( "</p><p>Data Handling: " );
		sb.append( "This module does not modify the input Table in any way. ");
		sb.append( "</p><p>Scalability: " );
		sb.append( "A representation of each row of the table is stored in memory. The representation is usually ");
		sb.append( "smaller than the original data.    </p>" );
		return sb.toString();
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

}

// Start QA Comments
// 2/28/03 Recv from Tom
// 3/11/03 Ruth starts QA;  Renamed TableToItemSets instead of ConvertTableToItemSets (class)
// and Table To Sets (module name).   Updated documentation.


