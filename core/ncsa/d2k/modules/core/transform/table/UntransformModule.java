
package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.*;
import java.util.List;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.*;

/**
	UntransformModule.java

	runs all the untransforms of an exampletable on
	that table

	@author Peter Groves
	8/01/01
*/
public class UntransformModule extends ncsa.d2k.core.modules.ComputeModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      The example table to untransform   ";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "      The ET after the untransforms have been run   ";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Runs all the untransforms of an ExampleTable on itself  </body></html>";
	}

	/**
		Does it
	*/
	public void doit() throws Exception {
		MutableTable et=(MutableTable)pullInput(0);


		List transforms=et.getTransformations();

		//System.out.println(transforms.size());
		//make sure to untransform in reverse order
		for(int i=transforms.size()-1; i>=0; i--){
			((ReversibleTransformation)(transforms.get(i))).untransform(et);
		}
		pushOutput(et, 0);
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "TransformedET";
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
				return "untransformed";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

