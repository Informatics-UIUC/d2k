//package opie;
package ncsa.d2k.modules.core.prediction.markov;


import ncsa.d2k.modules.TransformationModule;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;

/**
	MarkovDataTransform.java
	Transforms a table of Strings to a Table of ints.  The transformation logic
	is saved with this object.
	@author David Clutter
*/
public class MarkovDataTransform extends TransformationModule
	 {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Transforms a table of Strings to a Table of ints. The transformation logic     is saved with this object.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "MarkovDataTransform";
	}

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The ExampleTable to transform.";
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
				return "untransformedTable";
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
			case 0: return "The new, transformed table.  This module is added to the list of transformations.";
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
				return "transformedTable";
			default: return "NO SUCH OUTPUT!";
		}
	}

    /**
       Perform the calculation.
    */
    public void doit() {
		ExampleTable et = (ExampleTable)pullInput(0);
		stringToIntLookup = new HashMap();
		intToStringLookup = new HashMap();

		int numItems = 0;
		// iterate through each column to find the unique items and
		// create a mapping in the idLookup table
		for(int i = 0; i  < et.getNumColumns(); i++) {
			for(int j = 0; j < et.getNumRows(); j++) {
				String item = et.getString(j, i);
				if(!stringToIntLookup.containsKey(item)) {
					stringToIntLookup.put(item, new Integer(numItems));
					intToStringLookup.put(new Integer(numItems), item);
				}
				numItems++;
			}
		}

		ExampleTable newTable = (ExampleTable)transform(et);
		et = null;
		pushOutput(newTable, 0);
    }

	HashMap stringToIntLookup;
	HashMap intToStringLookup;

	public Table transform(Table t) {
		IntColumn newCols[] = new IntColumn[t.getNumColumns()];
		int numRows = t.getNumRows();
		for(int i = 0; i < newCols.length; i++) {
			newCols[i] = new IntColumn(numRows);
			newCols[i].setLabel(((TableImpl)t).getColumn(i).getLabel());
		}

		for(int i = 0; i < newCols.length; i++) {
			for(int j = 0; j < numRows; j++)
				newCols[i].setInt(stringToInt(t.getString(j, i)), j);
		}
		TableImpl vt = (TableImpl)DefaultTableFactory.getInstance().createTable(newCols);
		newCols = null;
		ExampleTable et = vt.toExampleTable();
		if(t instanceof ExampleTable) {
			et.setInputFeatures( ((ExampleTable)t).getInputFeatures());
			et.setOutputFeatures( ((ExampleTable)t).getOutputFeatures());
		}
		et.addTransformation(this);
		return et;
	}

	public Table untransform(Table t) {
		StringColumn newCols[] = new StringColumn[t.getNumColumns()];
		int numRows = t.getNumRows();
		for(int i = 0; i < newCols.length; i++) {
			newCols[i] = new StringColumn(numRows);
			newCols[i].setLabel(((TableImpl)t).getColumn(i).getLabel());
		}

		for(int i = 0; i < newCols.length; i++) {
			for(int j = 0; j < numRows; j++)
				newCols[i].setString(intToString(t.getInt(j, i)), j);
		}
		TableImpl vt = (TableImpl)DefaultTableFactory.getInstance().createTable(newCols);
		newCols = null;
		ExampleTable et = vt.toExampleTable();
		if(t instanceof ExampleTable) {
			et.setInputFeatures( ((ExampleTable)t).getInputFeatures());
			et.setOutputFeatures( ((ExampleTable)t).getOutputFeatures());
		}
		et.addTransformation(this);
		return et;
	}

	public String intToString(int i) {
		Integer num = new Integer(i);
		if(intToStringLookup.containsKey(num))
			return (String)intToStringLookup.get(num);
		else
			return MISSING_STRING;
	}

	static String MISSING_STRING = ".";
	static int MISSING_INT = -1;

	public int stringToInt(String s) {
		if(stringToIntLookup.containsKey(s))
			return ((Integer)stringToIntLookup.get(s)).intValue();
		else
			return MISSING_INT;
	}
}
