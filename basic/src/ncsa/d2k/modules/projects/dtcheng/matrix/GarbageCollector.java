package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.ComputeModule;

public class GarbageCollector extends ComputeModule {

	public String getModuleInfo() {
		return "GarbageCollector: forces a system garbage collection. "
				+ "The object pulled in is passed back out unmolested, but a garbage collection is performed.";
	}

	public String getModuleName() {
		return "GarbageCollector:";
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

	public String[] getInputTypes() {
		String[] types = { "java.lang.Object" };
		return types;
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
			return "Object";
		default:
			return "NO SUCH OUTPUT!";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "java.lang.Object" };
		return types;
	}

	public void doit() {
		Object x = this.pullInput(0);

		System.gc();

		this.pushOutput(x, 0);
	}
}

