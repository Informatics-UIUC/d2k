package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.infrastructure.modules.ComputeModule;
import ncsa.d2k.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**


=======
	ModelPredict

		takes in a model and a predictionTable
		and runs the model's predict function
		on the test table
*/
public class ModelPredict extends ncsa.d2k.infrastructure.modules.ComputeModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"TestTable\">    <Text>The test set</Text>  </Info></D2K>";
				case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"PredictionModel\">    <Text>The model</Text>  </Info></D2K>";

			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
						"ncsa.d2k.infrastructure.modules.PredictionModelModule"	};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"TTout\">    <Text>The PredictionTable with the prediction columns filled in by the model</Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"pmm\">    <Text>The Model.</Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.PredictionTable",
			"ncsa.d2k.infrastructure.modules.PredictionModelModule"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Predictor\">    <Text>takes in a model and a TestTable and runs the model's predict function on the test table </Text>  </Info></D2K>";
	}


	/**
	*/
	public void doit() throws Exception {
		ExampleTable tt= (ExampleTable)pullInput(0);
		PredictionModelModule pmm=(PredictionModelModule)pullInput(1);

		PredictionTable pt=pmm.predict(tt);
		pushOutput(pt, 0);
		pushOutput(pmm, 1);

		/*ExampleTable tt= (ExampleTable) pullInput (0);
		PredictionModelModule pmm = (PredictionModelModule) pullInput (1);
		tt = (PredictionTable) pmm.predict (tt);
		pushOutput(tt, 0);
		*/
	}
}

