package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

/**
 * A very simple ModelSelector that takes a model as input and returns it in the
 * getModel() method.
 */
public class BasicModelSelector extends ModelSelectorModule implements HasNames {

	public String getModuleInfo() {
		String s = "A simple ModelSelector that takes a model and returns it.";
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
		return "model";
	}

	public String getOutputInfo(int i) {
		return "The model that was passed in, unchanged.";
	}

	public String getOutputName(int i) {
		return "model";
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
	 * Return the model with the highest percentage of correct predictions.
	 * @return the model with the highest number of correct predictions.
	 */
	public ModelModule getModel() {
		return theModel;
	}
}