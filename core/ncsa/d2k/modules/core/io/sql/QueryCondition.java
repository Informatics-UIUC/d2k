/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.io.sql;
import ncsa.d2k.infrastructure.modules.*;
import java.io.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	QueryCondition.java
	
*/
public class QueryCondition extends ncsa.d2k.infrastructure.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
implements Serializable {

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
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
};
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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Query Condition\">    <Text>This is the search criterion for the search. If the string is blank therre will be no where clause in the database, all records will be retrieved. </Text>  </Info></D2K>";
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
			"java.lang.String"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Query Condition\">    <Text>This module pushes a string containing the condition for the where clause of the sql query. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
	}
	
	String query = "";
	public void setCondition (String cond) {
		query = cond;
	}
	public String getCondition () {
		return query;
	}
	
	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		this.pushOutput (query, 0);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
}

