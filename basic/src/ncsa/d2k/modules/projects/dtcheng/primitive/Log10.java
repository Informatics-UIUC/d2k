package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class Log10 extends ComputeModule {

	private double LogZeroValue = -10.0;

	public void setLogZeroValue(double value) {
		this.LogZeroValue = value;
	}

	public double getNumBins() {
		return this.LogZeroValue;
	}

	public String getModuleInfo() {
		return "Log10";
	}

	public String getModuleName() {
		return "Log10";
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
			return "Array";
		default:
			return "NO SUCH INPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "Array";
		default:
			return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "Array";
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
		
		double [] OutputData = new double[count];
		
		for (int i = 0; i < count; i++) {
		  double value = data[i];
		  if (value == 0.0) 
			OutputData[i] = LogZeroValue;
		  else
		    OutputData[i] = Math.log(value);
		}

		this.pushOutput(OutputData, 0);
	}
}