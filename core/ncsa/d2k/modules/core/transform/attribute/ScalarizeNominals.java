package ncsa.d2k.modules.core.transform.attribute;
import ncsa.d2k.infrastructure.modules.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
	ScalarizeNominals.java

*/
public class ScalarizeNominals extends ncsa.d2k.infrastructure.modules.DataPrepModule
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Example table\">    <Text>This is the input example table with the inputs and outputs selected. </Text>  </Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Scalarized Table\">    <Text>This is an output example table inputs and outputs containing nominal values represented by additional columns containing scalars. </Text>  </Info></D2K>";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Scalarize Nominals\">    <Text>This module will examine each input column and output column, and if they contain nominal values, it will convert them to multiple colunms (of booleans) one for each possible value of the attribute. </Text>  </Info></D2K>";
	}

	/**
		convert the column containing nominal values to scalar values.
		@param Column the column containing the data.
	*/
	public Column [] getScalarColumns (Column col, String label) {

		// First find all the unique values of the column.
		int how = col.getNumRows ();
		HashMap vals = new HashMap ();
		int tmp = 0;
		for (int i = 0 ; i < how ; i++) {
			String tt = col.getString (i);
			if (vals.get (tt) == null)
				vals.put (col.getString (i), new Integer (tmp++));
		}

		// Make an array of the different values for this field. Basically
		// here we just ascertain the order of the keys.
		int numDiffValues = vals.size ();
		String [] possibleValues = new String [numDiffValues];
		Object [] keys = (Object []) vals.keySet().toArray ();
		for (int i = 0 ; i < numDiffValues; i++) {
			String ck = (String) keys[i];
			possibleValues [((Integer) vals.get (ck)).intValue ()] = ck;
		}
		// the size of the hashtable is also the size
		int numvals = vals.size ();
		boolean [][] sclr = new boolean [numvals][how];
		for (int i = 0 ; i < how ; i++)
			sclr [((Integer)vals.get (col.getString (i))).intValue ()][i] = true;

		// Now convert them to BooleanColumns;
		BooleanColumn [] bc = new BooleanColumn [numvals];
		for (int i = 0 ; i < numvals ; i++) {
			bc[i] = new BooleanColumn (sclr [i]);
			String lbl =label+"="+possibleValues[i];
System.out.println ("LABEL : "+lbl);
			bc[i].setLabel (lbl);
		}
		return bc;
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		TableImpl et = (TableImpl) this.pullInput (0);
		TableImpl tmpTbl = (TableImpl)DefaultTableFactory.getInstance().createTable();
		ExampleTableImpl newet = (ExampleTableImpl)tmpTbl.toExampleTable();
		int [] inputs = null;
		int [] outputs = null;
		if (et instanceof ExampleTable) {
			inputs = ((ExampleTable)et).getInputFeatures ();
			outputs = ((ExampleTable)et).getOutputFeatures ();
		}
		ArrayList newInputs = new ArrayList ();
		ArrayList newOutputs = new ArrayList ();
		int iIndex = 0, oIndex = 0;

		for (int i = 0 ; i < et.getNumColumns () ; i++) {
			Column col = et.getColumn (i);
			if (inputs == null) {

				// Is the column contain scalars?
				if (col instanceof StringColumn || col instanceof ByteArrayColumn ||  col instanceof CharArrayColumn) {

					// This column is non scalar. We will convert it to scalar
					Column [] addCols = this.getScalarColumns (col, et.getColumnLabel (i));
					for (int j = 0 ; j < addCols.length ; j++) {
						newet.addColumn (addCols[j]);
					}
				} else {
					newet.addColumn (col);
				}
			} else if (iIndex < inputs.length && i == inputs[iIndex]) {
				iIndex++;

				// Is the column contain scalars?
				if (col instanceof StringColumn || col instanceof ByteArrayColumn ||  col instanceof CharArrayColumn) {

					// This column is non scalar. We will convert it to scalar
					Column [] addCols = this.getScalarColumns (col, et.getColumnLabel (i));
					for (int j = 0 ; j < addCols.length ; j++) {
						newet.addColumn (addCols[j]);
						newInputs.add (new Integer (newet.getNumColumns ()-1));
					}
				} else {
					newet.addColumn (col);
					newInputs.add (new Integer (newet.getNumColumns ()-1));
				}
			} else if (oIndex < outputs.length && i == outputs[oIndex]) {
				oIndex++;

				// Is the column contain scalars?
				if (col instanceof StringColumn || col instanceof ByteArrayColumn ||  col instanceof CharArrayColumn) {

					// This column is non scalar. We will convert it to scalar
					Column [] addCols = this.getScalarColumns (col,et.getColumnLabel (i));
					for (int j = 0 ; j < addCols.length ; j++) {
						newet.addColumn (addCols[j]);
						newOutputs.add (new Integer (newet.getNumColumns ()-1));
					}
				} else {
					newet.addColumn (col);
					newOutputs.add (new Integer (newet.getNumColumns ()-1));
				}
			} else
				newet.addColumn (et.getColumn (i));
		}

		// reconstruct the inputs and outputs.
		int howMany = newInputs.size ();
		if (howMany > 0) {
			int [] newinputs = new int [howMany];
			for (int i = 0 ; i < howMany ; i++) {
	System.out.println ("Input column : "+((Integer)newInputs.get (i)).intValue ());
				newinputs[i] = ((Integer)newInputs.get (i)).intValue ();
			}
			newet.setInputFeatures (newinputs);
		}

		howMany = newOutputs.size ();
		if (howMany > 0) {
			int [] newoutputs = new int [howMany];
			for (int i = 0 ; i < howMany ; i++) {
	System.out.println ("Output column : "+((Integer)newOutputs.get (i)).intValue ());
				newoutputs[i] = ((Integer)newOutputs.get (i)).intValue ();
			}
			newet.setOutputFeatures (newoutputs);
		}
		this.pushOutput (newet, 0);
	}
}

