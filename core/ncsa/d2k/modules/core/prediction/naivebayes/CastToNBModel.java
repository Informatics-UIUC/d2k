package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.infrastructure.modules.*;

/**
*/
public class CastToNBModel extends DataPrepModule
{

	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "DecisionTreeModel";
			default: return "No such input";
		}

	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;

	}

	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "A viewable DTModel.";
			default: return "No such output.";
		}

	}

	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
		return types;

	}

	public String getModuleInfo() {
		return "Casts a PredictionModelModule to a ViewableDTModel.";
	}


	public void doit() throws Exception {
		PredictionModelModule pmm = (PredictionModelModule)pullInput(0);
		if(pmm instanceof NaiveBayesModel)
			pushOutput((NaiveBayesModel)pmm, 0);
	}
}

