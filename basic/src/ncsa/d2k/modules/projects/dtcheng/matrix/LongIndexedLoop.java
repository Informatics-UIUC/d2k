package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class LongIndexedLoop extends ComputeModule {

	public String getModuleName() {
		return "LongIndexedLoop";
	}

	public String getModuleInfo() {
		return "LongIndexedLoop";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "StartIndex";
		case 1:
			return "EndIndex";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "StartIndex";
		case 1:
			return "EndIndex";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = { "java.lang.Long", "java.lang.Long", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "SequentialLongs";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "SequentialLongs";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "java.lang.Long", };
		return types;
	}

	public void doit() {

		long StartIndex = ((Long) this.pullInput(0)).longValue();
		long EndIndex 	= ((Long) this.pullInput(1)).longValue();

		for (long i = StartIndex; i <= EndIndex; i++) {
			this.pushOutput(new Long(i), 0);
		}

	}

}