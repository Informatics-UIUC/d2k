//package working.click;
//package opie;
package ncsa.d2k.modules.core.prediction.markov;

import ncsa.d2k.infrastructure.modules.*;
import java.util.HashMap;


/**
   MarkovModelGenerator.java
   @author David Clutter
*/
public class MarkovModelGenerator extends ModelGeneratorModule
	implements HasNames {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "MarkovModelGenerator";
    }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String []in = {"ncsa.d2k.modules.core.prediction.markov.MarkovModel"};
		return in;
    }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String []out = {"ncsa.d2k.modules.core.prediction.markov.MarkovModel"};
		return out;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		return "";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		return "";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		return "";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	return "";
    }

	MarkovModel mdl;

    /**
       Perform the calculation.
    */
    public void doit() {
		mdl = (MarkovModel)pullInput(0);
		pushOutput(mdl, 0);
    }

	public ModelModule getModel() {
		return mdl;
	}
}
