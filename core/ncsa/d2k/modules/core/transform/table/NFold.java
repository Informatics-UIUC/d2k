package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

/**
   	NFold.java
   	Creates test and training tables from a VerticalTable.
	@author David Clutter
*/
public class NFold extends DataPrepModule
	implements HasNames, HasProperties, java.io.Serializable {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
    	StringBuffer sb = new StringBuffer("Creates test and training ");
		sb.append("tables from a VerticalTable.  The value of N determines ");
		sb.append("the size of the tables.  The test table will have (1/N)* ");
		sb.append("(number of columns in original table) ");
		sb.append("columns, and the training table will have (N-1)/N * ");
		sb.append("(number of columns in original table) ");
		sb.append("columns.  This module will fire its outputs N times, ");
		sb.append("once for each test and training table set.");
		return sb.toString();
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "n-fold";
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
		String []out = {"ncsa.d2k.util.datatype.VerticalTable",
                                "ncsa.d2k.util.datatype.VerticalTable",
                                "java.lang.Object",
                                "java.lang.Integer"};
		return out;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		if(i == 0)
			return "A VerticalTable.";
		return "no such input!";
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		if(i ==0)
			return "table";
		return "no such input!";
	}

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		if(i == 0) {
			StringBuffer sb = new StringBuffer("The test table.  This table ");
			sb.append("has (1/N)*(number of columns in original table) ");
			sb.append("columns.  This output will be fired N times!");
			return sb.toString();
		}
		else if(i == 1) {
			StringBuffer sb = new StringBuffer("The training table.  This ");
			sb.append("has (N-1)/N * (number of columns in original table) ");
			sb.append("columns.  This output will be fired N times!");
			return sb.toString();
		}
		else if(i == 2) {
			StringBuffer sb = new StringBuffer("trigger");
			return sb.toString();
		}
		else if(i == 3) {
			StringBuffer sb = new StringBuffer("numFolds");
			return sb.toString();
		}
		return "no such output";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	if(i == 0)
			return "testingTable";
		else if(i == 1)
			return "trainingTable";
		else if(i == 2)
			return "trigger";
		else if(i == 3)
			return "numFolds";
		return "no such output.";
    }

	int N;

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

    /**
       Perform the calculation.
    */
    public void doit() {
		VerticalTable vt = (VerticalTable)pullInput(0);
		if(N <= 1)
			pushOutput(vt, 0);
		else {	
			int[] tableBreaks = getTableBreaks(vt);

			for(int i = 0; i <= tableBreaks.length; i++) {
				VerticalTable []tables = breakTables(vt, tableBreaks, i);
				// 0 = test
				pushOutput(tables[0], 0);
				// 1 = train
				pushOutput(tables[1], 1);
				pushOutput(new Boolean(true), 2);
                        	pushOutput(new Integer(N), 3);
			}		
		}
	}

	// num = the part that is test
	// everything else will be train
	VerticalTable[] breakTables(VerticalTable orig, int[] breaks, int num) {
		int testColumnEnd;
		int testColumnBegin;

		if(num == 0) {
			testColumnBegin = 0;
			testColumnEnd = breaks[num];
		}
		else if(num < breaks.length) {
			testColumnBegin = breaks[num-1]+1;
			testColumnEnd = breaks[num];
		}
		else {
			testColumnBegin = breaks[breaks.length-1]+1;
			testColumnEnd = orig.getNumColumns()-1;
		}

		VerticalTable test = new VerticalTable(testColumnEnd-testColumnBegin+1);
		VerticalTable train = new VerticalTable(
			orig.getNumColumns()-(testColumnEnd-testColumnBegin+1));

		int trainIndex = 0;
		int testIndex = 0;

		for(int i = 0; i < orig.getNumColumns(); i++) {
			if( (i >= testColumnBegin) && (i <= testColumnEnd) ) {
				test.setColumn(orig.getColumn(i), testIndex);
				testIndex++;
			}
			else {
				train.setColumn(orig.getColumn(i), trainIndex);
				trainIndex++;
			}
		}

		VerticalTable []retVal = new VerticalTable[2];
		retVal[0] = test;
		retVal[1] = train;
		return retVal;
	}

	int []getTableBreaks(VerticalTable orig) {
		int[] tableBreaks = new int[N-1];
		double numCols = (double)orig.getNumColumns();
		double n = (double)N;

		for(int i = 0; i < N-1; i++)
			tableBreaks[i] = (int) (((double)(i+1)/n)*numCols);

		return tableBreaks;
	}
}
