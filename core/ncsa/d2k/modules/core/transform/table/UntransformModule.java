
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import java.util.ArrayList;

import ncsa.d2k.modules.core.datatype.table.*;
/**
	UntransformModule.java

	runs all the untransforms of an exampletable on
	that table

	@author Peter Groves
	8/01/01
*/
public class UntransformModule extends ncsa.d2k.infrastructure.modules.ComputeModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"TransformedET\">    <Text>The example table to untransform </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"untransformed\">    <Text>The ET after the untransforms have been run </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"\">    <Text>Runs all the untransforms of an ExampleTable on itself </Text>  </Info></D2K>";

	}

	/**
		Does it
	*/
	public void doit() throws Exception {
		ExampleTable et=(ExampleTable)pullInput(0);


		ArrayList transforms=et.getTransformations();

		//System.out.println(transforms.size());
		//make sure to untransform in reverse order
		for(int i=transforms.size()-1; i>=0; i--){
			((ncsa.d2k.modules.TransformationModule)(transforms.get(i))).untransform(et);
		}
		pushOutput(et, 0);
	}

}

