package ncsa.d2k.modules.core.prediction;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.PredictionModelModule;

/**
 * A very simple ModelSelector that takes a model as input and returns it in the
 * getModel() method.  The model is passed as output, unchanged.
 */
public class SimpleModelSelector extends ModelSelectorModule  {

	public String getModuleInfo() {
		return "This has been REPLACED by CatchModel.   Stop using this module.";
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "A PredictionModelModule.";
			default: return "No such input";
		}
	}

	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "Model";
			default: return "NO SUCH INPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The model that was passed in, unchanged.";
			default: return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "Model";
			default: return "NO SUCH OUTPUT!";
		}
	}

	public String getModuleName() {
		return "REPLACED BY CatchModel - DISCONTINUE USING THIS!";
	}

	public void beginExecution() {
		theModel = null;
	}

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
