package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
   RotateVerticalTable.java
   @author David Clutter
*/
public class RotateVerticalTable extends DataPrepModule  {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Rotates a table so that the columns become the rows. The new table will     have an extra column. The labels of the columns in the original table     become the first column of the new table.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "rotate";
	}

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The table to rotate.";
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
				return "originalTable.";
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
			case 0: return "The rotated table.  The labels of the columns in the original table become the first column of the new table.  The data in column 0 becomes the data in row0, and so on.";
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
				return "rotatedTable";
			default: return "NO SUCH OUTPUT!";
		}
	}

    /**
       Perform the calculation.
    */
    public void doit() {

    	Table vt = (Table)pullInput(0);
		Table nt = rotateTable(vt);
		pushOutput(nt, 0);
	}

	/**
		whatever was in col1 becomes row1, etc
		the first column will be the labels from the original.
	*/
	Table rotateTable(Table orig) {
		int origRows = orig.getNumRows();
		int origCols = orig.getNumColumns();

		TableImpl newTable = (TableImpl)DefaultTableFactory.getInstance().createTable(origRows + 1);
		newTable.setComment(orig.getComment());

		StringColumn sc = new StringColumn(origCols);
                sc.setComment("ignore");
		sc.setLabel(new String("uid"));
		for(int i = 0; i < origCols; i++)
			sc.setString(orig.getColumnLabel(i), i);

		IntColumn []ic = new IntColumn[origRows];
		for(int i = 0; i < ic.length; i++) {
			ic[i] = new IntColumn(origCols);
			ic[i].setLabel(Integer.toString(i));

                        if (i == ic.length - 1)
                          ic[i].setComment("output");
                        else
                          ic[i].setComment("input");
		}

		// now fill the new table
		for(int i = 0; i < origRows; i++) {
			for(int j = 0; j < origCols; j++)
				ic[i].setInt(orig.getInt(i, j), j);
		}
		newTable.setColumn(sc, 0);
		for(int i = 0; i < ic.length; i++)
			newTable.setColumn(ic[i], i+1);
		return newTable;
	}
}
