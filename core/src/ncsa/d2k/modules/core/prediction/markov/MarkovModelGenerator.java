//package working.click;
//package opie;
package ncsa.d2k.modules.core.prediction.markov;


import ncsa.d2k.core.modules.*;
import java.util.HashMap;


/**
   MarkovModelGenerator.java
   @author David Clutter
*/
public class MarkovModelGenerator extends ModelGeneratorModule
	 {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    MarkovModelGenerator  </body></html>";
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
		String[] types = {"ncsa.d2k.modules.core.prediction.markov.MarkovModel"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.markov.MarkovModel"};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "";
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
				return "";
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
			case 0: return "";
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
				return "";
			default: return "NO SUCH OUTPUT!";
		}
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
