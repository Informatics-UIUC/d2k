package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class DownSampleArray extends ComputeModule {

	private int NumBins = 10;

	public void setNumBins(int value) {
		this.NumBins = value;
	}

	public int getNumBins() {
		return this.NumBins;
	}

	public String getModuleInfo() {
		return "DownSampleArray";
	}

	public String getModuleName() {
		return "DownSampleArray";
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
		int samplesPerBin = count / NumBins;
		
		double [] OutputData = new double[NumBins];
		
		for (int i = 0; i < count; i++) {
			int index = i / samplesPerBin;
			if (index >= NumBins) {
				index = NumBins - 1;
			}
			OutputData[index] += data[i];
		}
		for (int i = 0; i < NumBins; i++) {
			OutputData[i] /= samplesPerBin;
		}

		this.pushOutput(OutputData, 0);
	}
}