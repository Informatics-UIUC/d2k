/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.discovery.ruleassociation.apriori;

import java.util.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	StringBinModule.java

*/
public class StringBinModule extends ncsa.d2k.core.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This is an array of purchases, each purchase consisting of a list of the item names of the items purchased.   ";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"[[Ljava.lang.String;"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      This hashtable is keyed against the distinct items found, containing as the value an     <I>Integer containing the index </I>            ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"java.util.Hashtable"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module will take as input a list of purchases, each purchase     consisting of an array of item names of items purchased. It will produce a     hashtable contain each of the distinct words in the column of the original     table at the column indicated by the index. The value of the hashtable     will be the index of the associated distinct item.  </body></html>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		String [][] purchases = (String [][]) this.pullInput (0);
		Hashtable unique = new Hashtable ();

		// First identify all unique descriptions and count their
		// occurences
		int counter = 0;
		for (int i = 0; i < purchases.length; i++ ) {
			for (int j = 0; j < purchases[i].length; j++) {
				String item_desc = purchases[i][j];
				int [] cnt_and_id = (int []) unique.get (item_desc);
				if (cnt_and_id == null) {
					cnt_and_id = new int [2];
					cnt_and_id[0] = 1;
					cnt_and_id[1] = counter++;
					unique.put (item_desc, cnt_and_id);
				} else
					cnt_and_id[0]++;
			}
		}

		// We will now sort the unique items on the basis of their frequency distribution
		// with those items with the highest frequency distribution having the highest indices.
		// This will allow some optimization fo the search space.
		Hashtable result = new Hashtable ();
		int numUnique = unique.size ();
		for (int i = 0 ; i < numUnique ;i++) {
			Enumeration enum = unique.elements ();
			Enumeration enum2 = unique.keys ();
			int smallestFrequency = purchases.length;
			String smallestName = null;
			int [] smallestInts = null;
			while (enum.hasMoreElements ()) {
				int [] tmp = (int[]) enum.nextElement ();
				String tmpName = (String) enum2.nextElement ();
				if (tmp[0] <= smallestFrequency) {
					smallestName = tmpName;
					smallestInts = tmp;
					smallestFrequency = tmp[0];
				}
			}
			smallestInts[1] = i;
			result.put (smallestName, smallestInts);
			unique.remove (smallestName);
		}
		this.pushOutput (result, 0);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Bin Text Items";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "purchase array";
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
				return "item map";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

