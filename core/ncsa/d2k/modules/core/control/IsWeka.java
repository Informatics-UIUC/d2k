
package ncsa.d2k.modules.core.control;

import ncsa.d2k.infrastructure.modules.*;
import java.lang.*;

/*
** Class : IsWeka
** Purpose: To determine if the file is a Weka File or D2K data file
*/
public class IsWeka extends ncsa.d2k.infrastructure.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"The absolute path to the selected file\">    <Text> </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"java.lang.String"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"WEKA data\">    <Text> </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"D2K data\">    <Text> </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"java.lang.String","java.lang.String"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"\">    <Text> </Text>  </Info></D2K>";

	}

	// Method : doit
	// Purpose: to determine whether the input file is a Weka Data File or a D2K Data File
	public void doit() throws Exception {
		String str = (String) pullInput(0);
		//If it is a Weka File, then push that output
		if (str.endsWith(".arff")) {
			pushOutput(str, 0);
		//Otherwise it is a D2K file and push that output
		} else {
			pushOutput(str, 1);
		}
	}

}

