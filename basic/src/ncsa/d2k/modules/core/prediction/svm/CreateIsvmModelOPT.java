package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;

/**
 * Produces an initialized IsvmModel. This modules generates a new IsvmModel and
 * initializes it according to the properties, as they are set by the input
 * parameter point. see CreateIsvmModel or IsvmParamSpaceGenerator for
 * information about the properties.
 * 
 * @author vered goren
 */

public class CreateIsvmModelOPT extends CreateIsvmModel {

	public String getInputInfo(int index) {
		switch (index) {
		case 0:
			return "Controll point in the parameter space";
		default:
			return "No such input";
		}
	}

	public String getInputName(int index) {
		switch (index) {
		case 0:
			return "Parameter Point";
		default:
			return "No such input";
		}
	}

	public String[] getInputTypes() {
		String[] in = { "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint" };
		return in;
	}

	public String getModuleInfo() {
		return "<p>Overview: Produces an initialized IsvmModel.</p>"
				+ "<p>Detailed Description: This modules generates a new IsvmModel and initializes it "
				+ "according to the properties, as they are set by the input parameter point."
				+ "See CreateIsvmModel or IsvmParamSpaceGenerator for information about the "
				+ "properties.</p>";

	}

	public String getModuleName() {
		return "Create Isvm Model Optimized";
	}

	/*
	 * public String getOutputInfo(int index) { switch (index) { case 0: return
	 * "Isvm prediction model."; default: return "no such output"; } }
	 * 
	 * public String getOutputName(int index) { switch (index) { case 0: return
	 * "SVM Predictor"; default: return "no such output"; } }
	 * 
	 * public String[] getOutputTypes() { String[] out =
	 * {"ncsa.d2k.modules.projects.vered.svm.IsvmModel"}; return out; }
	 */

	public void beginExecution() {
	}

	public void endExecution() {
		super.endExecution();
	}

	protected void doit() throws Exception {
		ParameterPoint pp = (ParameterPoint) pullInput(0);

		IsvmModel d2k_model = new IsvmModel(super.numAttributes);
		d2k_model.setNu(pp.getValue(IsvmParamSpaceGenerator.NU));

		d2k_model.init();
		pushOutput(d2k_model, 0);

	}

	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] pds = new PropertyDescription[1];
		pds[0] = super.getPropertiesDescriptions()[1];
		return pds;
	}

}
