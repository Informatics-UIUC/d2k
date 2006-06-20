package ncsa.d2k.modules.projects.dtcheng.models;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.inducers.Model;

public class CreateCombined2Model extends ComputeModule {

	private boolean Trace = false;
	public void setTrace(boolean value) {
		this.Trace = value;
	}
	public boolean getTrace() {
		return this.Trace;
	}

	private String ConstructedFeatureName = "ModelPrediction";
	public void setConstructedFeatureName(String value) {
		this.ConstructedFeatureName = value;
	}
	public String getConstructedFeatureName() {
		return this.ConstructedFeatureName;
	}

	private double Model1Weight = 0.5;
	public void setModel1Weight(double value) {
		this.Model1Weight = value;
	}
	public double getModel1Weight() {
		return this.Model1Weight;
	}

	private double Model2Weight = 0.5;
	public void setModel2Weight(double value) {
		this.Model2Weight = value;
	}
	public double getModel2Weight() {
		return this.Model2Weight;
	}

	public String getModuleInfo() {
		return "CreateCombined2Model";
	}
	public String getModuleName() {
		return "CreateCombined2Model";
	}

	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "Examples";
			case 1 :
				return "Model1";
			case 2 :
				return "Model2";
		}
		return "";
	}
	public String[] getInputTypes() {
		String[] types =
			{
				"ncsa.d2k.modules.core.datatype.table.ExampleTable",
				"ncsa.d2k.modules.projects.dtcheng.Model",
				"ncsa.d2k.modules.projects.dtcheng.Model" };
		return types;
	}
	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "Examples";
			case 1 :
				return "Model";
			case 2 :
				return "Model";
			default :
				return "No such input";
		}
	}

	public String getOutputName(int i) {
		switch (i) {
			case 0 :
				return "Model";
		}
		return "";
	}
	public String[] getOutputTypes() {
		String[] types = { "ncsa.d2k.modules.projects.dtcheng.Model" };
		return types;
	}
	public String getOutputInfo(int i) {
		switch (i) {
			case 0 :
				return "Model";
			default :
				return "No such output";
		}
	}

	public void doit() throws Exception {
		ExampleTable examples = (ExampleTable) this.pullInput(0);
		Model model1 = (Model) this.pullInput(1);
		Model model2 = (Model) this.pullInput(2);

		CombinedModel combinedModel = new CombinedModel(examples, model1, model2, Model1Weight, Model2Weight);

		this.pushOutput(combinedModel, 0);

	}

}
