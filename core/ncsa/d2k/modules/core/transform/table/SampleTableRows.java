package ncsa.d2k.modules.core.transform.table;





import java.util.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
	SampleTableRows.java
	Creates a sample of the given Table.  If the useFirst property is
	set, then the first N rows of the table will be the sample.  Otherwise,
	the sampled table will contain N random rows from the table.  The original
	table is left untouched.

   @author David Clutter
*/
public class SampleTableRows extends DataPrepModule  {

	/**
	   Return a description of the function of this module.
	   @return A description of this module.
	*/
	public String getModuleInfo() {
		return "Overview: Creates a sample of the given Table, by either randomly selecting     rows, or using"+
			" the first rows.<p>Detailed Description: This module will take the inputs table, and select <i>"+
			"     Sample Size</i> rows from it, where <i>Sample Size</i> is a property the     user can modify."+
			" The selected rows will then be placed in a different     table and passed as the output. If"+
			" the <i>Use First</i> property is set,     the first <i>Sample Size</i> entries from the file"+
			" will be placed in the     resulting table. Otherwise, they are selected randomly.<p>Scalability:"+
			" This module should scale very well. There must be memory to     accomodate both the input table"+
			" and the resulting sample table.";
	}

	/**
	   Return the name of this module.
	   @return The name of this module.
	*/
	public String getModuleName() {
		return "SampleTableRows";
	}

	/**
	   Return a String array containing the datatypes the inputs to this
	   module.
	   @return The datatypes of the inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	   Return a String array containing the datatypes of the outputs of this
	   module.
	   @return The datatypes of the outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	   Return a description of a specific input.
	   @param i The index of the input
	   @return The description of the input
	*/
	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "This is the original table that will be sampled.";
			default: return "No such input";
		}
	}

	/**
	   Return the name of a specific input.
	   @param i The index of the input.
	   @return The name of the input
	*/
	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "Original Table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	   Return the description of a specific output.
	   @param i The index of the output.
	   @return The description of the output.
	*/
	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "This table is the result of sampling the original table.";
			default: return "No such output";
		}
	}

	/**
	   Return the name of a specific output.
	   @param i The index of the output.
	   @return The name of the output
	*/
	public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "Sample Table";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/** the number of rows to sample */
	int N;
	/** true if the first N rows should be the sample, false if the sample
		should be random rows from the table */
	boolean useFirst;
	/** the seed for the random number generator */
	int seed;

	public void setUseFirst(boolean b) {
		useFirst = b;
	}

	public boolean getUseFirst() {
		return useFirst;
	}

	public void setSampleSize(int i) {
		N = i;
	}

	public int getSampleSize() {
		return N;
	}

	public void setSeed(int i) {
		seed = i;
	}

	public int getSeed() {
		return seed;
	}
	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [3];
		pds[0] = new PropertyDescription ("sampleSize", "Sample Size", "This number of examples in the resulting table.");
		pds[1] = new PropertyDescription ("seed", "Seed", "The seed for the random number generater used to select the random folds. If this value is set to the same value for different runs, the results be the exact same.");
		pds[2] = new PropertyDescription ("useFirst", "Use First", "If this option is selected, the first entries in the original table will be used as the sample.");
		return pds;
	}


	/**
	   Perform the calculation.
	*/
	public void doit() throws Exception {

          Table orig = (Table) pullInput(0);
          Table newTable = null;

          if (N > (orig.getNumRows()-1)){
            throw new Exception("SampleTableRows: Sample size is >= the number of table rows.  Use a smaller value.");
          }

          //System.out.println("Sampling " + N + " rows from a table of " +
          //                   orig.getNumRows() + " rows.");

          // only keep the first N rows
          if (useFirst) {
            newTable = (Table) orig.getSubset(0, N);
          } else {
            int numRows = orig.getNumRows();
            int[] keeps = new int[N];
            Random r = new Random(seed);
            if (N < (orig.getNumRows()/2)){
              ArrayList keepers = new ArrayList();
              for (int i = 0; i < N; i++) {
                int ind = Math.abs(r.nextInt()) % numRows;
                Integer indO = new Integer(ind);
                if (keepers.contains(indO)) {
                  i--;
                } else {
                  keeps[i] = ind;
                  keepers.add(indO);
                }
              }
            } else {
              ArrayList pickers = new ArrayList();
              for (int i = 0, n = numRows; i < n; i++) {
                pickers.add(new Integer(i));
              }
              for (int i = 0; i < N; i++) {
                int ind = Math.abs(r.nextInt()) % pickers.size();
                //System.out.println(pickers.size() + " " + ind);
                keeps[i] = ( (Integer) pickers.remove(ind)).intValue();
              }
            }
            newTable = orig.getSubset(keeps);
          }

          //System.out.println("Sampled table contains " +
          //newTable.getNumRows() + " rows.");

          pushOutput(newTable, 0);


//		MutableTable orig = (MutableTable)pullInput(0);
//		MutableTable newTable = (MutableTable)orig.copy();
//
//		// only keep the first N rows
//		if(useFirst) {
//			for(int i = newTable.getNumRows()-1; i > N-1; i--)
//				newTable.removeRow(i);
//		}
//		else {
//			Random r = new Random(seed);
//			int numRows = newTable.getNumRows();
//			int numRowsToDelete = numRows - N;
//			// for each rowToDelete, randomly pluck one out
//			for(int i = 0; i < numRowsToDelete; i++) {
//				numRows = newTable.getNumRows();
//				newTable.removeRow(Math.abs(r.nextInt()) % numRows);
//			}
//		}
//		pushOutput(newTable, 0);
	}
}
