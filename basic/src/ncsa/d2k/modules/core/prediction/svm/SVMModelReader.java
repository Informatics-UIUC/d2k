package ncsa.d2k.modules.core.prediction.svm;

import libsvm.svm;
import libsvm.svm_model;

import ncsa.d2k.core.modules.ModelProducerModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

/**
  Produces a SVMModel Predictor Model from an existing libsvm model file.

  @author Xiaolei Li
  */

public class SVMModelReader extends ModelProducerModule
{
	public String getInputInfo(int index)
	{
		switch (index) {
			case 0:
				return "File name of stored SVM Model.";
			case 1:
				return "Input data in an example table.";
			default:
				return "";
		}
	}

	public String getInputName(int index)
	{
		switch (index) {
			case 0:
				return "File Name";
			case 1:
				return "Example Table";
			default:
				return "";
		}
	}

	public String[] getInputTypes()
	{
		String[] in = {"java.lang.String",
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return in;
	}


        public String getModuleName()
       {
         return "SVM Model Reader";

       }


	public String getModuleInfo()
	{
		return "<p>Overview: Given a native libSVM model file, produces a D2K SVM prediction model.</p>";
	}

	public String getOutputInfo(int index)
	{
		switch (index) {
			case 0:
				return "SVM prediction model.";
			default:
				return "";
		}
	}

	public String getOutputName(int index)
	{
		switch (index) {
			case 0:
				return "SVM Predictor";
			default:
				return "";
		}
	}

	public String[] getOutputTypes()
	{
		String[] out = {"ncsa.d2k.modules.core.prediction.svm.SVMModel"};
		return out;
	}

	public void beginExecution()
	{
	}

	public void endExecution()
	{
		super.endExecution();
	}

	protected void doit() throws Exception
	{
		try {
			String file_name = (String) this.pullInput(0);
			ExampleTable et = (ExampleTable) this.pullInput(1);

			svm_model model = svm.svm_load_model(file_name);

			SVMModel d2k_model = new SVMModel(model, et);

			this.pushOutput(d2k_model, 0);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			System.out.println("ERROR: SVMModelReader.doit()");
			throw ex;
		}
	}
}
