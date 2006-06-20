package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

//import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class MFMToDoubleArray extends ComputeModule {

	public String getModuleName() {
		return "MFMToDoubleArray";
	}

	public String getModuleInfo() {
		return "This module takes an MFM and kicks out a Double array "
				+ "whose elements are the same. This is "
				+ "because I am having trouble with the networking..."
				+ "oh, and it should be 2-dimensional... and it shouldn't be obscenely large "
				+ "since double arrays are indexed by integers...";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "MultiFormatMatrix";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "MultiFormatMatrix";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "DoubleArray";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "DoubleArray";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "[[D", };
		return types;
	}

	public void doit() throws Exception {

		MultiFormatMatrix X = (MultiFormatMatrix) this.pullInput(0);

		int rowSizeX = (int) X.getDimensions()[0];
		int colSizeX = (int) X.getDimensions()[1];

		double[][] NewArray = new double[rowSizeX][colSizeX];

		for (int rowIndex = 0; rowIndex < rowSizeX; rowIndex++) {
			for (int colIndex = 0; colIndex < colSizeX; colIndex++) {
				NewArray[rowIndex][colIndex] = X.getValue(rowIndex,
						colIndex);
			}
		}

		this.pushOutput(NewArray, 0);
	}

}

