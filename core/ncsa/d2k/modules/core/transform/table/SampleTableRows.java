package ncsa.d2k.modules.core.transform.table;





import java.util.*;
import java.beans.PropertyVetoException;

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

    	  String s = "<p>Overview: ";
    	  s += "This module samples the input <i>Table</i> and chooses a certain number of rows to copy ";
    	  s += "to a new <i>Sample Table</i>.  The number of rows and sampling method are determined by the ";
    	  s += "module properties. ";

    	  s += "</p><p>Detailed Description: ";
    	  s += "This module creates a new <i>Sample Table</i> by sampling rows of the input <i>Table</i>.  If <i>Use First Rows</i> ";
    	  s += "is set, the first <i>Sample Size</i> rows in the input table are copied to the new table.  If it is not ";
    	  s += "set, <i>Sample Size</i> rows are selected randomly from the input table, using the <i>Random Seed</i> ";
    	  s += "to seed the random number generator.  If the same seed is used across runs with the same input table, ";
    	  s += "the sample tables produced by the module will be identical. ";

    	  s += "</p><p>";
    	  s += "If the input table has <i>Sample Size</i> or fewer rows, an exception will be raised. ";

    	  s += "</p><p>";
    	  s += "An <i>OPT</i>, optimizable, version of this module that uses control ";
    	  s += "parameters encapsulated in a <i>Parameter Point</i> instead of properties is also available. ";

    	  s += "</p><p>Data Handling: ";
    	  s += "The input table is not changed. ";

    	  s += "</p><p>Scalability: ";
    	  s += "This module should scale very well. There must be memory to accomodate both the input table ";
    	  s += "and the resulting sample table. ";

    	  return s;

	}

	/**
	   Return the name of this module.
	   @return The name of this module.
	*/
	public String getModuleName() {
		return "Sample Table Rows";
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
			case 0: return "The table that will be sampled.";
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
				return "Table";
			default: return "No such input";
		}
	}

	/**
	   Return the description of a specific output.
	   @param i The index of the output.
	   @return The description of the output.
	*/
	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "A new table containing a sample of rows from the original table.";
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
			default: return "No such output";
		}
	}

	/** the number of rows to sample */
	int N = 1;
	/** true if the first N rows should be the sample, false if the sample
		should be random rows from the table */
	boolean useFirst = false;
	/** the seed for the random number generator */
	int seed = 0;

	public void setUseFirst(boolean b) {
		useFirst = b;
	}

	public boolean getUseFirst() {
		return useFirst;
	}

	public void setSampleSize(int i) throws PropertyVetoException {
                if ( i < 1 ) {
		    throw new PropertyVetoException ( " Value must be > 0. ", null);
		} else {
		  N = i;
                }
	}

	public int getSampleSize() {
		return N;
	}

	public void setSeed(int i) throws PropertyVetoException {
                if ( i < 0 ) {
		    throw new PropertyVetoException ( " Value must be >= 0. ", null);
		} else {
		  seed = i;
                }
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
		pds[0] = new PropertyDescription ("sampleSize",
						  "Sample Size",
			"The number of rows to include in the resulting table. " +
			"It must be greater than 0 and less than the number of rows in the input table. " );
		pds[1] = new PropertyDescription ("seed",
						  "Random Seed",
                        "The seed for the random number generator used to select the sample set of " +
                        "<i>Sample Size</i> rows.  It must be greater than or equal to 0. " +
                        "The seed is not used if the <i>Use First Rows</i> option is selected. " );
		pds[2] = new PropertyDescription ("useFirst",
						  "Use First Rows",
			"If this option is selected, the first entries in the original table will be used as the sample.");
		return pds;
	}


	/**
	   Perform the calculation.
	*/
	public void doit() throws Exception {

          Table orig = (Table) pullInput(0);
          Table newTable = null;

          if (N > (orig.getNumRows()-1)){
                int numRows = orig.getNumRows() - 1;
                throw new Exception( getAlias()  +
			   ": Sample size (" + N +
			   ") is >= the number of rows in the table (" + numRows +
			   "). \n" +
 			   "Use a smaller sample size.");
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
// Start QA Comments
// 4/8/03 - Ruth updated Module Info and properties to match the OPT version and also
//          to add a comment that it exists.  Also added Exceptions of Sample Size < 1
//          or seed < 0.
// End QA Comments.
