package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.gui.Constrain;

import java.awt.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
abstract public class AbstractParamSpaceGenerator extends ncsa.d2k.core.modules.DataPrepModule {

	protected ParameterSpace space;
	public ParameterSpace getCurrentSpace() { return space; }
	public void setCurrentSpace(ParameterSpace space) { this.space = space; }

	/**
	 * Returns a reference to the developer supplied defaults. These are
	 * like factory settings, absolute ranges and definitions that are not
	 * mutable.
	 * @return the factory settings space.
	 */
	abstract protected ParameterSpace getDefaultSpace();

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<p>      This is the parameter space that will be searched.    </p>";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Parameter Space";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterSpace"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "Overview: This module will produce a parameter space object that     constrains the search space"+
			" for parameter optimization.Detailed Description: For model builders to work, they must have"+
			" an input that     defines their control parameters. For example, a neural net will often  "+
			"   quire an integer that defines the number of hidden layers and an     activation value. The"+
			" optimal settings for these parameters for a given     dataset to model are not usually known"+
			" before the run begins. Therefor,     several models can often be built and evaluated to find"+
			" the best settings     for these control parameters.To automate this, we define an API that"+
			" allows the optimization of these     control parameters. The range of possible values for each"+
			" control     parameters must therefor be defined for the optimization to search the     space"+
			" of possible parameters. We use the <i>ParameterSpace</i> object to     define these spaces."+
			" This module defines those spaces.";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Parameter Space Generator";
	}

	/**
	 * All we have to do here is push the parameter space.
	 */
	public void doit() {
		if (space == null) space = this.getDefaultSpace();
		this.pushOutput(space, 0);
	}

	/**
	 * return a reference a custom property editor to select the percent test
	 * and train.
	 * @returna reference a custom property editor
	 */
	public CustomModuleEditor getPropertyEditor() {
		if (space == null) space = this.getDefaultSpace();
		return new SetParameterSpace(this);
	}

}
