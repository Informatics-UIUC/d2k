package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.infrastructure.modules.*;
//import ncsa.d2k.modules.core.datatype.table.*;

/**
 * A very simple ModelSelector that takes a model as input and returns it in the
 * getModel() method.  The model is passed as output, unchanged.
 */
public class BasicModelSelector extends ModelSelectorModule implements HasNames {

	public String getModuleInfo() {
		String s = "A simple ModelSelector that takes a model and returns it. ";
        s += "The model is passed as output, unchanged.";
		return s;
	}

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.infrastructure.modules.PredictionModelModule"};
		return in;
	}

	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.infrastructure.modules.PredictionModelModule"};
		return out;
	}

	public String getInputInfo(int i) {
		return "A PredictionModelModule.";
	}

	public String getInputName(int i) {
		return "Model";
	}

	public String getOutputInfo(int i) {
		return "The model that was passed in, unchanged.";
	}

	public String getOutputName(int i) {
		return "Model";
	}

	public String getModuleName() {
		return "BasicModelSelector";
	}

	public void beginExecution() {
		theModel = null;
	}

	/** the most accurate prediction model so far */
	private ModelModule theModel;

	public void doit() {
		ModelModule mm = (ModelModule)pullInput(0);
		theModel = mm;
		pushOutput(mm, 0);
	}

	/**
     * Return the model that was passed in.
	 * @return the model that was passed in.
	 */
	public ModelModule getModel() {
        ModelModule mod = theModel;
        theModel = null;
		return mod;
	}
}