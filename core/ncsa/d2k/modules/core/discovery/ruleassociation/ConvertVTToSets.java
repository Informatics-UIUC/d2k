/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.discovery.ruleassociation;
import ncsa.d2k.modules.core.datatype.table.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	ConvertVTToSets.java
*/
public class ConvertVTToSets extends ncsa.d2k.infrastructure.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>This is the input table that will be converted into a set used in rule association. </Text>  </Info></D2K>";
			default: return "No such input";
		}
/*#end^3 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
/*&%^4 Do not modify this section. */
		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
/*#end^4 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
/*&%^5 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Sets\"><Text>This is the itemset object representation.</Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"output names\">    <Text>List of the names of the output attributes.</Text>  </Info></D2K>";
			default: return "No such output";
		}
/*#end^5 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
/*&%^6 Do not modify this section. */
		String [] types =  {
			"ncsa.d2k.modules.core.discovery.ruleassociation.ItemSets",
			"[Ljava.lang.String;"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table To Sets\">    <Text>Read in a vertical table and convert it into an array of sets. I will do this by prefixing the value found in the field with either the name of the field if available, or a simple index. Either case, this is followed by a \"^\" and then the value. This is sometimes required to make the attribute value unique across fields. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
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
}

