package ncsa.d2k.modules.core.prediction.naivebayes;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * 	CreateNBModel simply creates a NaiveBayesModel.
 *  	@author David Clutter
 */
public class CreateNBModel extends ModelProducerModule
	 {

    /**
       	Return a description of the function of this module.
       	@return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Generates a NaiveBayesModel from the given BinTree. The NaiveBayesModel     performs all necessary calculations.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "createNBModel";
	}

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.BinTree","ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The BinTree which contains counts.";
			case 1: return "The ExampleTable with the data in it.";
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
				return "BinTree";
			case 1:
				return "Table";
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
			case 0: return "A NaiveBayesModel module.";
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
				return "NBModel";
			default: return "NO SUCH OUTPUT!";
		}
	}

    /**
    	Create the model and push it out.
	*/
    public void doit() {
		BinTree bins = (BinTree)pullInput(0);
		ExampleTable et = (ExampleTable)pullInput(1);
		ModelModule mdl = new NaiveBayesModel(bins, et);
		pushOutput(mdl, 0);
	}
}
