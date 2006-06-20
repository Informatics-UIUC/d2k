package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class ProcessArray extends OrderedReentrantModule {

	private int NumPasses = 10;

	public void setNumPasses(int value) {
		this.NumPasses = value;
	}

	public int getNumPasses() {
		return this.NumPasses;
	}

	public String getModuleInfo() {
		return "ProcessArray";
	}

	public String getModuleName() {
		return "ProcessArray";
	}

	public String[] getInputTypes() {
		String[] types = { "java.lang.Object" };
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = { "java.lang.Object" };
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "Object";
		default:
			return "No such input";
		}
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Object";
		default:
			return "NO SUCH INPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "Object";
		default:
			return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "Object1";
		default:
			return "NO SUCH OUTPUT!";
		}
	}

	public void doit() {

		double[] data = (double[]) this.pullInput(0);

		if (data == null) {
			this.pushOutput(null, 0);
			return;
		}

		int count = data.length;

		for (int ii = 0; ii < NumPasses; ii++) {
			for (int i = 0; i < count; i++) {
				data[i] = data[i] + Math.sin(data[i]);
			}
		}

		this.pushOutput(data, 0);
	}
}