package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.datatype.*;

/**
   BinningModule.java
   @author David Clutter
*/
public class Binning extends DataPrepModule implements HasNames {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "Takes a BinTree and an ExampleTable and classifies all input variables.";
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
		String []in = {"ncsa.d2k.modules.core.datatype.BinTree",
				"ncsa.d2k.util.datatype.ExampleTable"};
		return in;
    }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String []out = {"ncsa.d2k.modules.core.datatype.BinTree",
			"ncsa.d2k.util.datatype.ExampleTable"};
		return out;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		if(i == 0)
			return "An empty BinTree.";
		else if(i == 1)
			return "An ExampleTable.";
		else
			return "No such input";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		if(i == 0)
			return "BinTree";
		else if(i == 1)
			return "ExampleTable.";
		else
			return "No such input";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		if(i == 0)
			return "The full BinTree.";
		else if(i == 1)
			return "The ExampleTable, unchanged";
		else
			return "No such input";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
		if(i == 0)
			return "BinTree";
		else if(i == 1)
			return "ExampleTable";
		else
			return "No such input";
    }

    /**
       Perform the calculation.
    */
    public void doit() {
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
			SimpleColumn sc = (SimpleColumn)vt.getColumn(ins[i]);

			// numeric columns
			if(sc instanceof NumericColumn) {
				for(int j = 0; j < numRows; j++) {
					bt.classify(vt.getString(j, classColumn),
						sc.getLabel(), vt.getDouble(j, ins[i]));
					}
			}

			// everything else is treated as textual columns
			else {
				for(int j = 0; j < numRows; j++)
					bt.classify(vt.getString(j, classColumn),
						sc.getLabel(), vt.getString(j, ins[i]));
			}
		}

		//long endTime = System.currentTimeMillis();
		//System.out.println ( "time in msec " + (endTime-startTime));
		pushOutput(bt, 0);
		pushOutput(vt, 1);
    }
}
