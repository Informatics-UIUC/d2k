
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
	RandomizeVTRows.java
*/
public class RandomizeVTRows extends ncsa.d2k.infrastructure.modules.DataPrepModule implements java.io.Serializable
{
	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"table\">    <Text>Table to be randomized </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"returnTable\">    <Text>Table that has been randomized </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"RandomizeVTRows\">    <Text>Randomizes the rows of a Table </Text>  </Info></D2K>";

	}

	/**
		PUT YOUR CODE HERE.
	*/

	/**
		Randomizes the rows of the Table using swapping
	*/
	public void doit() throws Exception {

		rand = new Random(seed);

		TableImpl table = (TableImpl) pullInput(0);
		int numRow = table.getNumRows();
		int j = 0;
		for (int i=0; i<numRow; i++){
			j = getRandomNumber(i, numRow-1);
			table.swapRows(i,j);
		}
		pushOutput(table, 0);

	}

	int seed;

	public void setSeed(int x){
		seed = x;
	}

	public int getSeed(){
		return seed;
	}

	transient Random rand;

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

}

