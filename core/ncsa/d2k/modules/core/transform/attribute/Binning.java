package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
   BinningModule.java
   @author David Clutter
*/
public class Binning extends DataPrepModule  {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Takes a BinTree and an ExampleTable and classifies all input variables.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "BinningMod";
	}

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.BinTree","ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.BinTree","ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "An empty BinTree.";
			case 1: return "An ExampleTable.";
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
				return "BinTree";
			case 1:
				return "ExampleTable.";
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
			case 0: return "The full BinTree.";
			case 1: return "The ExampleTable, unchanged";
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
				return "BinTree";
			case 1:
				return "ExampleTable";
			default: return "NO SUCH OUTPUT!";
		}
	}

    /**
       Perform the calculation.
    */
    public void doit() throws Exception {
		BinTree bt = (BinTree)pullInput(0);
		ExampleTable vt = (ExampleTable)pullInput(1);

		int [] ins = vt.getInputFeatures();
		int [] out = vt.getOutputFeatures();

		// we only support one out variable..
		int classColumn = out[0];

        //                int numRows = sc.getNumRows();
		int numRows = vt.getNumRows();
		//long startTime = System.currentTimeMillis();
		for(int i = 0; i < ins.length; i++) {
			//Column sc = vt.getColumn(ins[i]);

			// numeric columns
			//if(sc instanceof NumericColumn) {
            if(vt.isColumnScalar(ins[i])) {
				for(int j = 0; j < numRows; j++) {
					bt.classify(vt.getString(j, classColumn),
						vt.getColumnLabel(ins[i]), vt.getDouble(j, ins[i]));
					}
			}

			// everything else is treated as textual columns
			else {
				for(int j = 0; j < numRows; j++)
					bt.classify(vt.getString(j, classColumn),
						vt.getColumnLabel(ins[i]), vt.getString(j, ins[i]));
			}
		}

		//long endTime = System.currentTimeMillis();
		//System.out.println ( "time in msec " + (endTime-startTime));
		pushOutput(bt, 0);
		pushOutput(vt, 1);
    }
}
