package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

import ncsa.d2k.modules.core.datatype.BinTree;

import java.util.HashMap;


/**
  	NaiveBayesModelGen creates a NaiveBayesModel.
   	@author David Clutter
*/
public class NaiveBayesModelGen extends ModelGeneratorModule
	implements HasNames {

    /**
       	Return a description of the function of this module.
       	@return A description of this module.
    */
    public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("Generates a NaiveBayesModel ");
    	sb = sb.append("from the given BinTree.  The NaiveBayesModel ");
		sb = sb.append("performs all necessary calculations.");
		return sb.toString();
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "modelgen";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		/*String []in = {"ncsa.d2k.modules.dataprep.field.BinTree",
			"ncsa.d2k.modules.util.datatype.VerticalTable",
			"java.util.HashMap"};
		*/
		String []in = {"ncsa.d2k.modules.core.datatype.BinTree",
			"ncsa.d2k.modules.util.datatype.ExampleTable"};
		return in;
    }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String [] out = {"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
		return out;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch(i) {
	    	case 0: return "The BinTree which contains counts.";
			case 1: return "The ExampleTable with the data in it.";
	    	//case 2: return "Lookup table with the data from ChooseAttributes.";
			default: return "no such input";
    	}
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
	    switch(i) {
			case 0: return "BinTree";
			case 1: return "VerticalTable";
	    	//case 2: return "typesmap";
			default: return "no such input";
    	}
	}

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		if(i == 0)
	    	return "A NaiveBayesModel module.";
		else
	    	return "no such output";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
	    switch(i) {
			case 0: return "mdl";
			default: return "no such output";
    	}
	}

	/** Keep a reference to the model so that it can be
		used in the getModel() method */
    NaiveBayesModel mdl;

    /**
    	Create the model and push it out.
	*/
    public void doit() {
		BinTree bins = (BinTree)pullInput(0);
		/*VerticalTable vt = (VerticalTable)pullInput(1);
		HashMap types = (HashMap)pullInput(2);
		*/
		ExampleTable et = (ExampleTable)pullInput(1);
		//mdl = new NaiveBayesModel(bins, vt, types);
		mdl = new NaiveBayesModel(bins, et);
		pushOutput(mdl, 0);
	}

	public ModelModule getModel() {
		return mdl;
    }
}