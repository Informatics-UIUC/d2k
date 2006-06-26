package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.ModelProducerModule;
import ncsa.d2k.core.modules.PropertyDescription;

/**
 * Produces an IsvmModel. This module generates a new IsvmModel and initializes
 * it according to the properties, as they were set by the user (Nu parameter
 * and number of input features). Please note that if number of input features
 * does not match the future input Tables of the output model, an exception will
 * be thrown.
 * 
 * @author vered goren
 */

public class CreateIsvmModel extends ModelProducerModule {

	// properties
	protected double nu;

	public double getNu() {
		return nu;
	}

	public void setNu(double dbl) {
		nu = dbl;
	}

	protected int numAttributes;

	public void setNumAttributes(int num) {
		numAttributes = num;
	}

	public int getNumAttributes() {
		return numAttributes;
	}

	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] pds = new PropertyDescription[2];
		pds[0] = new PropertyDescription("nu", "Nu", "SVM nu Parameter");
		pds[1] = new PropertyDescription("numAttributes",
				"Number Input Features",
				"Number of input features in the SVM problem");
		return pds;
	}

	public String getInputInfo(int index) {
		switch (index) {

		default:
			return "No such input";
		}
	}

	public String getInputName(int index) {
		switch (index) {

		default:
			return "No such input";
		}
	}

	public String[] getInputTypes() {
		String[] in = {};
		return in;
	}

	public String getModuleInfo() {
		return "<P>Overview: Produces an initialized IsvmModel.</P>"
				+ "<P>Detailed Description: This module generates a new IsvmModel and initializes it "
				+ "according to the properties, as they are set by the user."
				+ "Please note that if <I>Number Input Features</I> does not match the future"
				+ " input Tables of the output model, an exception will be thrown when this model attempts "
				+ " to train or predict.</P>";

	}

	public String getModuleName() {
		return "Create Isvm Model";
	}

	public String getOutputInfo(int index) {
		switch (index) {
		case 0:
			return "Initialized Isvm prediction model.";
		default:
			return "no such output";
		}
	}

	public String getOutputName(int index) {
		switch (index) {
		case 0:
			return "SVM Predictor";
		default:
			return "no such output";
		}
	}

	public String[] getOutputTypes() {
		String[] out = { "ncsa.d2k.modules.PredictionModelModule" };
		return out;
	}

	public void beginExecution() {
	}

	public void endExecution() {
		super.endExecution();
	}

	protected void doit() throws Exception {

		IsvmModel d2k_model = new IsvmModel(numAttributes);
		d2k_model.setNu(nu);
		d2k_model.init();

		pushOutput(d2k_model, 0);

	}
}
