package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class SplitOffLastColumn extends ComputeModule {

	public String getModuleName() {
		return "SplitOffLastColumn";
	}

	public String getModuleInfo() {
		return "This module will pull in a matrix and kick out the "
				+ "last column as one matrix and all the rest as another.";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Long", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "MainBody";
		case 1:
			return "LastColumn";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "MainBody";
		case 1:
			return "LastColumn";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
		return types;
	}

	public void doit() throws Exception {
		MultiFormatMatrix X = (MultiFormatMatrix) this.pullInput(0);
		long NumberOfElementsThreshold = ((Long) this.pullInput(1)).longValue();

		long nRows = X.getDimensions()[0];
		long nCols = X.getDimensions()[1];
		long lastCol = nCols - 1;

		int FormatIndexLast = -1; // initialize
		int FormatIndexBody = -1; // initialize
		long NumElements = -1;

		NumElements = nRows * (nCols - 1);
		if (NumElements < NumberOfElementsThreshold) {
			FormatIndexBody = 1; // Beware the MAGIC NUMBER!!!
		} else {
			FormatIndexBody = 3; // Beware the MAGIC NUMBER!!!
		}

		NumElements = nRows;
		if (NumElements < NumberOfElementsThreshold) {
			FormatIndexLast = 1; // Beware the MAGIC NUMBER!!!
		} else {
			FormatIndexLast = 3; // Beware the MAGIC NUMBER!!!
		}

		MultiFormatMatrix Body = new MultiFormatMatrix(FormatIndexBody,
				new long[] { nRows, nCols - 1 });
		MultiFormatMatrix LastColumn = new MultiFormatMatrix(FormatIndexLast,
				new long[] { nRows, 1 });

		for (long rowIndex = 0; rowIndex < nRows; rowIndex++) {
			for (long colIndex = 0; colIndex < nCols - 1; colIndex++) {
/*				System.out.println("rowIndex = " + rowIndex + "; nRows = " + nRows +
						"; colIndex = " + colIndex + ";nCols - 1 = " + (nCols - 1) );
*/				Body.setValue(rowIndex, colIndex, X
						.getValue(rowIndex, colIndex));
			}
			LastColumn.setValue(rowIndex, 0, X.getValue(rowIndex, lastCol));
		}

		this.pushOutput(Body, 0);
		this.pushOutput(LastColumn, 1);
	}
}