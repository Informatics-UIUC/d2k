package ncsa.d2k.modules.core.transform.binning;


import ncsa.d2k.core.modules.*;

/**
 * Clones a BinTree.
 */
public class CloneBinTree extends DataPrepModule  {

	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Clones a BinTree N times.  </body></html>";
	}

    public String getModuleName() {
		return "Clone BinTree";
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.transform.binning.BinTree","java.lang.Integer"};
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.transform.binning.BinTree","java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The BinTree to clone.";
			case 1: return "The number of times to clone the bin tree.";
			default: return "No such input";
		}
	}

    public String getInputName(int i) {
		switch(i) {
			case 0:
				return "BinTree";
			case 1:
				return "N";
			default: return "NO SUCH INPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "A cloned BinTree.";
			case 1: return "The number of times to clone the bin tree.";
			default: return "No such output";
		}
	}

    public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "Cloned Bin Tree";
			case 1:
				return "N";
			default: return "NO SUCH OUTPUT!";
		}
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
