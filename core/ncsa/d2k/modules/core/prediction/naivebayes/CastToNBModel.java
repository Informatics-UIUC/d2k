package ncsa.d2k.modules.core.prediction.naivebayes;


import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.core.modules.*;

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
			default: return "No such output";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
		return types;
	}

	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Casts a PredictionModelModule to a ViewableDTModel.  </body></html>";
	}


	public void doit() throws Exception {
		PredictionModelModule pmm = (PredictionModelModule)pullInput(0);
		if(pmm instanceof NaiveBayesModel)
			pushOutput((NaiveBayesModel)pmm, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "CastToNBModel";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

