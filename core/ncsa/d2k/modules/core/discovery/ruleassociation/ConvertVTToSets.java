/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.discovery.ruleassociation;
import ncsa.d2k.util.datatype.*;
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
			"ncsa.d2k.util.datatype.VerticalTable"};
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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Sets\">    <Text>These are the item sets that can be used in rule association, for example, for basket analysis, one set might be {bread, butter, milk}. </Text>  </Info></D2K>";
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
			"[[Ljava.lang.String;"};
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
		VerticalTable vt = (VerticalTable) this.pullInput (0);
		int numColumns = vt.getNumColumns ();
		int numRows = vt.getNumRows ();

		// Allocate an array of string for each column prefix.
		String [] prefix = new String [numColumns];

		// Init each prefix, if there is no column label, use our own
		// home brew.
		for (int i = 0 ; i < numColumns ; i++) {
			String tmp = vt.getColumnLabel (i);
			if (tmp != null && tmp.length() > 0)
				prefix [i] = tmp+"^";
			else
				prefix [i] = "^"+Integer.toString (i)+"^";
		}

		String [][] sets = new String [numRows][numColumns];
		for (int i = 0 ; i < numRows ; i++) {
			for (int j = 0 ; j < numColumns ; j++) {
				sets[i][j] = prefix[j]+vt.getString (i, j);
			}
		}
		this.pushOutput (sets, 0);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
}

