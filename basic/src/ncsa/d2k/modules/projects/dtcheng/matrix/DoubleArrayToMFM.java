package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

//import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class DoubleArrayToMFM extends ComputeModule {

	public String getModuleName() {
		return "DoubleArrayToMFM";
	}

	public String getModuleInfo() {
		return "This module takes a Double array and kicks out an MFM array "
				+ "whose elements are the same. This is "
				+ "because I am having trouble with the networking..."
				+ "oh, and it should be 2-dimensional... and it will be stored "
				+ " in memory since if it was a double array, it should fit. ";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "DoubleArray";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "DoubleArray";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = { "[[D", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "MultiFormatMatrix";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "MultiFormatMatrix";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
		return types;
	}

	public void doit() throws Exception {

		double[][] X = (double[][])this.pullInput(0);

		long rowSizeX = X.length;
		long colSizeX = X[0].length;

		MultiFormatMatrix NewArray = new MultiFormatMatrix(1,new long[] {rowSizeX,colSizeX});

		for (long rowIndex = 0; rowIndex < rowSizeX; rowIndex++) {
			for (long colIndex = 0; colIndex < colSizeX; colIndex++) {
				NewArray.setValue(rowIndex,colIndex,X[(int)rowIndex][(int)colIndex]);
			}
		}

		this.pushOutput(NewArray, 0);
	}

}

