package ncsa.d2k.modules.projects.dtcheng.matrix.reentrant;

import ncsa.d2k.core.modules.*;

public class LongToIntegerRE extends OrderedReentrantModule {

	public String getModuleName() {
		return "LongToIntegerRE";
	}

	public String getModuleInfo() {
		return "LongToInteger: changes a Long to an Integer in the default manner. Reentrant version.";
	}

	public String[] getInputTypes() {
		String[] types = { "java.lang.Long" };
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = { "java.lang.Integer" };
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "Long";
		default:
			return "No such input";
		}
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Long";
		default:
			return "NO SUCH INPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "Integer version of the Long.";
		default:
			return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "Integer";
		default:
			return "NO SUCH OUTPUT!";
		}
	}

	public void doit() {
		Long x = (Long) this.pullInput(0);
		this.pushOutput(new Integer(x.intValue()), 0);
		//    this.pushOutput(new Integer(((Long)this.pullInput(0)).intValue()),
		// 0);
	}
}