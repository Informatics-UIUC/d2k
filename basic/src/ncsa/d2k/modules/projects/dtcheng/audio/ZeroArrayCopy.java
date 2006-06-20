package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.*;

public class ZeroArrayCopy extends OrderedReentrantModule {

	private boolean Disable = false;

	public void setDisable(boolean value) {
		this.Disable = value;
	}

	public boolean getDisable() {
		return this.Disable;
	}

	private String Label = "label = ";

	public void setLabel(String value) {
		this.Label = value;
	}

	public String getLabel() {
		return this.Label;
	}

	public String getModuleName() {
		return "ZeroArrayCopy";
	}

	public String getModuleInfo() {
		return "This module creates a zeroed array of doubles of the same length as its input array.  ";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Double1DArray";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "Double1DArray";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = { "[D" };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "Double1DArray";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "Double1DArray";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "[D" };
		return types;
	}

	public void doit() {

		Object object = (Object) this.pullInput(0);

		if (object == null) {
			this.pushOutput(null, 0);
			return;
		}

		double[] double1DArray = (double[]) object;
		int dim1Size = double1DArray.length;

		double[] zeroCopy = new double[dim1Size];

		this.pushOutput(zeroCopy, 0);
	}
}