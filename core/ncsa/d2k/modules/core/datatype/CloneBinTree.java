package ncsa.d2k.modules.core.datatype;

import ncsa.d2k.infrastructure.modules.*;

/**
 * Clones a BinTree.
 */
public class CloneBinTree extends DataPrepModule implements HasNames {

	public String getModuleInfo() {
		return "Clones a BinTree N times.";
	}

    public String getModuleName() {
        return "Clone BinTree";
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
		if(i == 0)
			return "The BinTree to clone.";
		else
			return "The number of times to clone the bin tree.";
	}

    public String getInputName(int i) {
        if(i == 0)
            return "BinTree";
        else
            return "N";
    }

	public String getOutputInfo(int i) {
		if(i == 0)
			return "A cloned BinTree.";
		else
			return "The number of times to clone the bin tree.";
	}

    public String getOutputName(int i) {
        if(i == 0)
            return "Cloned Bin Tree";
        else
            return "N";
    }

	public void doit() {
		// pull in the inputs
		BinTree bt = (BinTree)pullInput(0);
		Integer n = (Integer)pullInput(1);

		// push N along
		pushOutput(n, 1);

		// push out N clones.
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