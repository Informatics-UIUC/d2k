
package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
	RandomizeVTRows.java
*/
public class RandomizeTableRows extends ncsa.d2k.core.modules.DataPrepModule 
{
	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      Table to be randomized   ";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "      Table that has been randomized   ";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Randomizes the rows of a Table  </body></html>";
	}

	/**
		Randomizes the rows of the Table using swapping
	*/
	public void doit() throws Exception {

		if(useSeed)
			rand = new Random(seed);
		else
			rand = new Random();

		MutableTable table = (MutableTable) pullInput(0);
		int numRow = table.getNumRows();
		int j = 0;
		for (int i=0; i<numRow; i++){
			j = getRandomNumber(i, numRow-1);
			table.swapRows(i,j);
		}
		pushOutput(table, 0);
	}

	private int seed = 345;

	public void setSeed(int x){
		seed = x;
	}

	public int getSeed(){
		return seed;
	}

	private boolean useSeed = true;

	public void setUseSeed(boolean b) {
		useSeed = b;
	}

	public boolean getUseSeed() {
		return useSeed;
	}

	private transient Random rand;

	/**
		Chooses a random integer between two integers (inclusive)
		@param int m - the lower integer
		@param int n - the higher integer
		@return the pseudorandom integer between m and n (inclusive)
		*/
	public int getRandomNumber(int m, int n){
		if (m == n)
			return m;
		else {
			double rnd = (Math.abs(rand.nextDouble()))*(n-m+1) + m;
			int theNum = (int) (rnd);
			return theNum;
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "RandomizeVTRows";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "table";
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
				return "returnTable";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
