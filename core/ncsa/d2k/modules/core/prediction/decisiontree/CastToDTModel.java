package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.core.modules.*;

public class CastToDTModel extends ComputeModule {

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.PredictionModelModule"};
		return in;
	}

	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return out;
	}

	public String getInputInfo(int i) {
		return "";
	}

	public String getInputName(int i) {
		return "";
	}

	public String getOutputInfo(int i) {
		return "";
	}

	public String getOutputName(int i) {
		return "";
	}

	public String getModuleName() {
		return "CastToDTModel";
	}

	public String getModuleInfo() {
		return "";
	}

	public void doit() {
		Object o = pullInput(0);
		pushOutput(o, 0);
	}
}