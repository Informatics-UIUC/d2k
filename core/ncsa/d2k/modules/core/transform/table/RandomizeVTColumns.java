package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import java.util.*;
import java.io.Serializable;

/**
   RandomizeColumns takes a VerticalTable and randomly rearranges the
   columns.
   @author David Clutter
*/
public class RandomizeVTColumns extends DataPrepModule implements Serializable {


  //////////////////
  //  PROPERTIES  //
  //////////////////

  private int  Seed     = 123;
  public  void setSeed  (int value) {       this.Seed = value;}
  public  int  getSeed  ()             {return this.Seed;}



    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("Randomly rearranges the columns ");
		sb.append(" of a VerticalTable.  A Column is chosen at random and ");
		sb.append(" inserted into a new table, until all Columns have been ");
		sb.append(" inserted.");
    	return sb.toString();
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "RandomizeColumns";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String [] in = {"ncsa.d2k.util.datatype.VerticalTable"};
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
		return "The Table to randomize.";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		return "table";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		return "The randomized table.";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	return "randomizedTable";
    }

    /**
       Perform the calculation.
    */
    public void doit() {
    	VerticalTable vt = (VerticalTable)pullInput(0);
		int columns = vt.getNumColumns();

		// create a new, empty table
		VerticalTable newVt = new VerticalTable(vt.getNumColumns());
		newVt.setLabel(vt.getLabel());
		newVt.setComment(vt.getComment());
		int colNum = 0;

                Random r = new Random(Seed);
		while(vt.getNumColumns() > 0) {
			// choose a random column
			int col = r.nextInt(vt.getNumColumns());
			Column c = vt.getColumn(col);
			// remove the column from the original table
			vt.removeColumn(col);
			// put the column into the new table
			newVt.setColumn(c, colNum);
			colNum++;
		}
		pushOutput(newVt, 0);
	}
}
