/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.transform.attribute;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import java.util.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	ScaleDoubles.java

*/
public class ScaleDoubles extends ncsa.d2k.infrastructure.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Example table\">    <Text>This is the input example table with the inputs and outputs selected. </Text>  </Info></D2K>";
			default: return "No such input";
		}
/*#end^3 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
/*&%^4 Do not modify this section. */
		String [] types =  {
			"ncsa.d2k.util.datatype.ExampleTable"};
		return types;
/*#end^4 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
/*&%^5 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Scalarized Table\">    <Text>Scales all double and float columns to the range 0-1. </Text>  </Info></D2K>";
			default: return "No such output";
		}
/*#end^5 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
/*&%^6 Do not modify this section. */
		String [] types =  {
			"ncsa.d2k.util.datatype.Table"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Scalarize Nominals\">    <Text>This module will examine each input column and output column, and if they contain nominal values, it will convert them to multiple colunms (of booleans) one for each possible value of the attribute. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
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
		Table et = (Table) this.pullInput (0);
		int [] inputs = null;
		int [] outputs = null;
		int iIndex=0, oIndex=0;

		if (et instanceof ExampleTable) {
			inputs = ((ExampleTable)et).getInputFeatures ();
			outputs = ((ExampleTable)et).getOutputFeatures ();
		}

		for (int i = 0 ; i < et.getNumColumns () ; i++) {
			SimpleColumn col = (SimpleColumn) et.getColumn (i);
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
}

