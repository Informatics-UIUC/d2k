package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.BinTree;

public class CloneBinTree extends DataPrepModule {

	public String getModuleInfo() {
		return "";
	}

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.datatype.BinTree",
			"java.lang.Integer"};
		return in;
	}

	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.datatype.BinTree",
			"java.lang.Integer"};
		return out;
	}

	public String getInputInfo(int i) {
		return "";
	}

	public String getOutputInfo(int i) {
		return "";
	}

	public void doit() {
		BinTree bt = (BinTree)pullInput(0);
		Integer n = (Integer)pullInput(1);

		pushOutput(n, 1);

		int val = n.intValue();
		for(int i = 0; i < val; i++) {
			try {
				BinTree clone = (BinTree)bt.clone();
				pushOutput(clone, 0);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}