/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	ScaleDoubles.java

*/
public class ScaleDoubles extends ncsa.d2k.core.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This is the input example table with the inputs and outputs selected.   ";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      Scales all double and float columns to the range 0-1.   ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module will examine each input column and output column, and if they     contain nominal values, it will convert them to multiple colunms (of     booleans) one for each possible value of the attribute.  </body></html>";
	}

	private Column getScaledColumn (DoubleColumn col) {
		double [] tmp = col.getScaledDoubles ();
		return new DoubleColumn (tmp);
	}
	private Column getScaledColumn (FloatColumn col) {
		float [] tmp = col.getScaledFloats ();
		return new FloatColumn (tmp);
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		TableImpl et = (TableImpl) this.pullInput (0);
		int [] inputs = null;
		int [] outputs = null;
		int iIndex=0, oIndex=0;

		if (et instanceof ExampleTable) {
			inputs = ((ExampleTable)et).getInputFeatures ();
			outputs = ((ExampleTable)et).getOutputFeatures ();
		}

		for (int i = 0 ; i < et.getNumColumns () ; i++) {
			Column col = et.getColumn (i);
		System.out.println ("COLUMN TPE = "+col.getClass().getName());
			if (inputs == null) {

				// Is the column contain scalable stuff?
				if (col instanceof DoubleColumn) {
					// This column is non scalar. We will convert it to scalar
					Column  newCol = this.getScaledColumn ((DoubleColumn)col);
					et.setColumn (newCol, i);
				} else if (col instanceof FloatColumn) {
					// This column is non scalar. We will convert it to scalar
					Column  newCol = this.getScaledColumn ((FloatColumn)col);
					et.setColumn (newCol, i);
				}
			} else if (iIndex < inputs.length && i == inputs[iIndex]) {
				iIndex++;
				// Is the column contain scalable stuff?
				if (col instanceof DoubleColumn) {
					// This column is non scalar. We will convert it to scalar
					Column  newCol = this.getScaledColumn ((DoubleColumn)col);
					et.setColumn (newCol, i);
				} else if (col instanceof FloatColumn) {
					// This column is non scalar. We will convert it to scalar
					Column  newCol = this.getScaledColumn ((FloatColumn)col);
					et.setColumn (newCol, i);
				}
			} else if (oIndex < outputs.length && i == outputs[oIndex]) {
				oIndex++;
				// Is the column contain scalable stuff?
				if (col instanceof DoubleColumn) {
					// This column is non scalar. We will convert it to scalar
					Column  newCol = this.getScaledColumn ((DoubleColumn)col);
					et.setColumn (newCol, i);
				} else if (col instanceof FloatColumn) {
					// This column is non scalar. We will convert it to scalar
					Column  newCol = this.getScaledColumn ((FloatColumn)col);
					et.setColumn (newCol, i);
				}
			}
		}
		this.pushOutput (et, 0);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Scalarize Nominals";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Example table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Scalarized Table";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

