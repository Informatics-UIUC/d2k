package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.core.modules.*;

public class ConvertDoubleArrayToStringArray extends OutputModule {

	private int Offset = 0;

	public void setOffset(int value) {
		this.Offset = value;
	}

	public int getOffset() {
		return this.Offset;
	}

	private int NumValues = 1;

	public void setNumValues(int value) {
		this.NumValues = value;
	}

	public int getNumValues() {
		return this.NumValues;
	}

	public String getModuleInfo() {
		return "ConvertDoubleArrayToByteArray";
	}

	public String getModuleName() {
		return "ConvertDoubleArrayToByteArray";
	}

	public String[] getInputTypes() {
		String[] types = { "[D" };
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = { "[Ljava.lang.String;" };
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "DoubleArray";
		default:
			return "No such input";
		}
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "DoubleArray";
		default:
			return "No such input";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "StringArray";
		default:
			return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "StringArray";
		default:
			return "No such output";
		}
	}

	public void doit() {

		Object object = (Object) this.pullInput(0);
		if (object == null)
			return;

		double[] doubleArray = (double[]) object;

		String[] stringArray = new String[NumValues];

		for (int i = 0; i < NumValues; i++) {
			stringArray[i] = Double.toString(doubleArray[i + Offset]);
		}

		this.pushOutput(stringArray, 0);

	}
}