package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class LongCountUp extends OrderedReentrantModule {

	public String getModuleName() {
		return "LongCountUp";
	}

	public String getModuleInfo() {
		return "LongCountUp: counts from zero up to the input; kicks out a done flag.";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "EndIndex";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "EndIndex";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = { "java.lang.Long", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "DoneFlag";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "DoneFlag";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "java.lang.Boolean", };
		return types;
	}

	public void doit() {

		long StartIndex = 0;
		long EndIndex = ((Long) this.pullInput(0)).longValue();
		double j = -18.0;
		for (long i = StartIndex; i <= EndIndex; i++) {
			j = Math.log(i);
		}
	    this.pushOutput(new Boolean(true), 0);
	}

}