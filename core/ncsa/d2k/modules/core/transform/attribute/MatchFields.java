package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
/**
	MatchFields.java

	 Takes two Tables, sets the second's inputFeatures and output
	 features to that of the first (which must be an Example Table)

	 @author Peter Groves
*/
public class MatchFields extends ncsa.d2k.infrastructure.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"FieldSourceTable\">    <Text>The table that contains the input and output columns already set </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"FieldSetTable\">    <Text>The table that will become a new ExampleTable (shallow copy) with the input and output columns set to be the same as the other input table </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 1: return "The table with the columns of the second and the input and output features of the first";
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
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"FieldMatch\">    <Text>Takes two Tables, sets the second's inputFeatures and output features to that of the first (which must be an Example Table) </Text>  </Info></D2K>";

	}

	/**
	*/
	public void doit() throws Exception {
		ExampleTable et=(ExampleTable)pullInput(0);
		Table tt=(Table)pullInput(1);

		ExampleTable et2=TableFactory.createExampleTable(tt);

		int[] ins=new int[et.getNumInputFeatures()];
		int[] outs=new int[et.getNumOutputFeatures()];

		System.arraycopy(et.getInputFeatures(), 0, ins, 0, ins.length);
		System.arraycopy(et.getOutputFeatures(), 0, outs, 0, outs.length);

		et2.setInputFeatures(ins);
		et2.setOutputFeatures(outs);

		pushOutput(et2, 0);
	}

}

