package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
   RotateVerticalTable.java
   @author David Clutter
*/
public class RotateVerticalTable extends DataPrepModule implements HasNames {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("Rotates a table so that ");
		sb.append("the columns become the rows.  The new table will have ");
		sb.append("an extra column.  The labels of the columns in the ");
		sb.append("original table become the first column of the new table.");
    	return sb.toString();
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
		String []in = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return in;
    }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String []out = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return out;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		if(i == 0)
			return "The table to rotate.";
		return "no such input";
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		if(i ==0)
			return "originalTable.";
		return "no such input.";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		if(i == 0) {
			StringBuffer sb = new StringBuffer("The rotated table.  The ");
			sb.append("labels of the columns in the original table become ");
			sb.append("the first column of the new table.  The data in ");
			sb.append("column 0 becomes the data in row0, and so on.");
			return sb.toString();
		}
		return "no such output.";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	return "rotatedTable";
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
