package ncsa.d2k.modules.core.prediction.LWR;

import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
	Model2VT.java
		This module takes an LWRModel module, and outputs the model's
		finaltable.  This is a Table containing the predicted
		values of each query along with the original test data.
	@talumbau
*/
public class Model2VT extends ncsa.d2k.infrastructure.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"model\">    <Text>the generated model </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.LWR.LWRModel"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"table\">    <Text>the table of predictions </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"\">    <Text> </Text>  </Info></D2K>";

	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
		LWRModel mdl = (LWRModel) pullInput(0);
		pushOutput(mdl.finaltable, 0);
	}

}

