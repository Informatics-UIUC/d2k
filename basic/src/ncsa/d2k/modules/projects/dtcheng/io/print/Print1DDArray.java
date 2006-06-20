package ncsa.d2k.modules.projects.dtcheng.io.print;

import ncsa.d2k.core.modules.*;

public class Print1DDArray extends OutputModule {

	private boolean CSVFormat = false;

	public void setCSVFormat(boolean value) {
		this.CSVFormat = value;
	}

	public boolean getCSVFormat() {
		return this.CSVFormat;
	}

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
		return "Print1DDArray";
	}

	public String getModuleInfo() {
		return "This module prints a 1D double array.  ";
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
		double[] double1DArray = (double[]) this.pullInput(0);

		if (double1DArray == null) {
			System.out.println("null");
			this.pushOutput(null, 0);
			return;
		}

		int dim1Size = double1DArray.length;

		if (!Disable) {
			if (CSVFormat) {
				for (int d1 = 0; d1 < dim1Size; d1++) {
					if (d1 > 0) {
						System.out.print(",");
					}
					System.out.print(double1DArray[d1]);
				}
				System.out.println();
			} else {
				for (int d1 = 0; d1 < dim1Size; d1++) {
					System.out.println(Label + "[" + (d1+1) + "] = "
							+ double1DArray[d1]);
				}
			}
		}

		this.pushOutput(double1DArray, 0);
	}
}