package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

import java.util.Random;

/**
	SampleVTRows.java
	Creates a sample of the given VerticalTable.  If the useFirst property is 
	set, then the first N rows of the table will be the sample.  Otherwise, 
	the sampled table will contain N random rows from the table.  The original
	table is left untouched.

   @author David Clutter
*/
public class SampleVTRows extends DataPrepModule implements HasNames, 
	java.io.Serializable {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("Creates a sample of the given VerticalTable.");
		sb.append(" If the useFirst property is set, then the first N rows of the table ");
		sb.append(" will be the sample.  Otherwise, the sampled table will contain N ");
		sb.append(" random rows from the table.  The original table is left untouched.");
		return sb.toString();
    }
    
    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "SampleVTRows";
    }

    /**
       Return a String array containing the datatypes the inputs to this 
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String []in = {"ncsa.d2k.util.datatype.VerticalTable"};
		return in;
    }
 
    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String []out = {"ncsa.d2k.util.datatype.VerticalTable"};
		return out;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		return "The VerticalTable to create a sample from.";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		return "original table";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		return "The sample table";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	return "sample table";
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

	public void setN(int i) {
		N = i;
	}

	public int getN() {
		return N;
	}

	public void setSeed(int i) {
		seed = i;
	}

	public int getSeed() {
		return seed;
	}

    /**
       Perform the calculation.
    */
    public void doit() {
		VerticalTable orig = (VerticalTable)pullInput(0);
		VerticalTable newTable = orig.getCopy();

		// only keep the first N rows
		if(useFirst) {
			for(int i = newTable.getNumRows()-1; i > N-1; i--) 
				newTable.removeRow(i);
		}
		else {
			Random r = new Random(seed);
			int numRows = newTable.getNumRows();
			int numRowsToDelete = numRows - N;
			System.out.println("numRows; "+numRowsToDelete);
			// for each rowToDelete, randomly pluck one out
			for(int i = 0; i < numRowsToDelete; i++) {
				numRows = newTable.getNumRows();
				newTable.removeRow(Math.abs(r.nextInt()) % numRows);	
			}
		}
		pushOutput(newTable, 0);
    }
}
