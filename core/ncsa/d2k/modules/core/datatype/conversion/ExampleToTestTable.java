package ncsa.d2k.modules.core.datatype.conversion;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.d2k.modules.core.datatype.table.*;

import java.util.*;

/**
   This module simple creates a <code>PredictionTable</code> from an
   <code>ExampleTable</code>. It uses the same columns and the such from
   the original table.
*/
public class ExampleToTestTable extends DataPrepModule {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "This module will simple convert an ExampleTable to a TestTable.";
	}

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
    	String []in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return in;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
    	String []out = {"ncsa.d2k.modules.core.datatype.table.TestTable"};
		return out;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		return "The input ExampleTable.";
	}

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
    	return "The test table";
	}

    /**
       Perform the calculation.
    */
    public void doit () {
		Object obj = pullInput (0);
    	ExampleTable et = (ExampleTable) obj;
		pushOutput (et.getTestTable (), 0);
	}

}
