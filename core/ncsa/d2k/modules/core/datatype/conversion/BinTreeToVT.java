package ncsa.d2k.modules.core.datatype.conversion;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.transform.attribute.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
   BinTreeToVT takes the data in a BinTree and inserts it into a
   VerticalTable, so that it can be viewed.  A column is created for each bin.
   The first row contains class names, the second row attribute names, the
   third row is the bin name, and the fourth row is the tally for that
   particular bin.  The column label is set to the name of the bin.
   @author David Clutter
*/
public class BinTreeToVT extends DataPrepModule  {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Create a Table from a BinTree. Each column corresponds to a bin. The first     row contains the class name. The second row contains the attribute name.     The third row contains the bin name. The tally is in the fourth row. Each     columnlabel is the bin name.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "bt2vt";
	}

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.BinTree"};
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
			case 0: return "The BinTree.";
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
				return "binTree";
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
			case 0: return "The Table.";
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
				return "table";
			default: return "NO SUCH OUTPUT!";
		}
	}

    /**
       Perform the calculation.
    */
    public void doit() {
    	BinTree bt = (BinTree)pullInput(0);
		TableImpl vt = toTable(bt);
		pushOutput(vt, 0);
	}

	/**
		Take the data out of the BinTree and put it in a Table
	*/
	private TableImpl toTable(BinTree bt) {
		LinkedList columns = new LinkedList();

		String []classNames = bt.getClassNames();
		String []attributeNames = bt.getAttributeNames();

		// loop through class names
		for(int i = 0; i < classNames.length; i++) {
			for(int j = 0; j < attributeNames.length; j++) {
				String []bn = bt.getBinNames(classNames[i], attributeNames[j]);
				for(int k = 0; k < bn.length; k++) {
					StringColumn sc = new StringColumn(4);
					sc.setRow(classNames[i], 0);
					sc.setRow(attributeNames[j], 1);
					sc.setRow(bn[k], 2);
					sc.setRow(Integer.toString(bt.getTally(classNames[i],
						attributeNames[j], bn[k])), 3);
					sc.setLabel(bn[k]);
					columns.add(sc);
				}
			}
		}

		Object []cols = columns.toArray();
		Column []co = new Column[cols.length];
		for(int i = 0; i < cols.length; i++)
			co[i] = (Column)cols[i];
		return (TableImpl)DefaultTableFactory.getInstance().createTable(co);
	}
}
