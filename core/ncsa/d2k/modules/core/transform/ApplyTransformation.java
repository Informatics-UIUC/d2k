package ncsa.d2k.modules.core.transform;

import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Apply a Transformation to a MutableTable.
 */
public class ApplyTransformation extends DataPrepModule {

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.datatype.table.Transformation",
			"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return in;
	}

	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.datatype.table.Transformation",
			"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return out;
	}

	public String getInputInfo(int i) {
		return "";
	}

	public String getInputName(int i) {
		return "";
	}

	public String getOutputInfo(int i) {
		return "";
	}

	public String getOutputName(int i) {
		return "";
	}

	public String getModuleInfo() {
		return "";
	}

	public void doit() throws Exception {
		Transformation t = (Transformation)pullInput(0);
		MutableTable mt = (MutableTable)pullInput(1);

		boolean ok = t.transform(mt);
		if(!ok)
			throw new Exception("Transformation failed.");
		pushOutput(t, 0);
		pushOutput(mt, 1);
	}
}
