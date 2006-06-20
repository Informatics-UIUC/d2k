package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class MatrixAugment15Horizontal extends ComputeModule {

	public String getModuleName() {
		return "MatrixAugment15Horizontal";
	}

	public String getModuleInfo() {
		return "This module takes up to 15 matrices and makes them into one matrix where "
				+ "the matrices are placed side by side in the order of inputs (top to bottom). "
				+ "If fewer the 15 are needed, the rest should be passed as nulls and should appear at the bottom. " +
				"<p>THERE IS NO IDIOT-PROOFING: the matrices will be treated as if they are all the same rowsize.";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "Matrix";
		case 2:
			return "Matrix";
		case 3:
			return "Matrix";
		case 4:
			return "Matrix";
		case 5:
			return "Matrix";
		case 6:
			return "Matrix";
		case 7:
			return "Matrix";
		case 8:
			return "Matrix";
		case 9:
			return "Matrix";
		case 10:
			return "Matrix";
		case 11:
			return "Matrix";
		case 12:
			return "Matrix";
		case 13:
			return "Matrix";
		case 14:
			return "Matrix";
		case 15:
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
			return "Matrix";
		case 2:
			return "Matrix";
		case 3:
			return "Matrix";
		case 4:
			return "Matrix";
		case 5:
			return "Matrix";
		case 6:
			return "Matrix";
		case 7:
			return "Matrix";
		case 8:
			return "Matrix";
		case 9:
			return "Matrix";
		case 10:
			return "Matrix";
		case 11:
			return "Matrix";
		case 12:
			return "Matrix";
		case 13:
			return "Matrix";
		case 14:
			return "Matrix";
		case 15:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Long", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "AugmentedMatrix";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "AugmentedMatrix";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
		return types;
	}

	public void doit() throws Exception {

		int nObjects = 15; // Beware the MAGIC NUMBER!!!
		Object[] allOfThem = new Object[nObjects];

		for (int objectIndex = 0; objectIndex < nObjects; objectIndex++) {
			allOfThem[objectIndex] = (MultiFormatMatrix) this
					.pullInput(objectIndex);
		}
		long NumberOfElementsThreshold = ((Long) this.pullInput(15))
				.longValue();

		// determine which ones are actually MFMs
		boolean[] fullFlags = new boolean[nObjects];
		int nFull = 0; // this actually needs to be zero

		for (int objectIndex = 0; objectIndex < nObjects; objectIndex++) {
			if ((allOfThem[objectIndex] == null)) {
				fullFlags[objectIndex] = false;
			} else {
				fullFlags[objectIndex] = true;
				nFull++;
			}
		}

		// figure out the total number of columns and such
		long nRows = ((MultiFormatMatrix)allOfThem[0]).getDimensions()[0];
		// assuming they are all the same row-size
		long[] nCols = new long[nFull];
		long totalCols = 0; // this actually needs to be zero

		for (int objectIndex = 0; objectIndex < nFull; objectIndex++) {
			nCols[objectIndex] = ((MultiFormatMatrix)allOfThem[objectIndex]).getDimensions()[1];
			totalCols += nCols[objectIndex];
		}
		
		long NumElements = nRows * totalCols;
		int FormatIndex = -1;
		if (NumElements < NumberOfElementsThreshold) {
			FormatIndex = 1; // Beware the MAGIC NUMBER!!!
		} else {
			FormatIndex = 3; // Beware the MAGIC NUMBER!!!
		}

		MultiFormatMatrix AugmentedMatrix = new MultiFormatMatrix(FormatIndex, new long[] { nRows, totalCols });

		
		double valueToStore = -5.3;
		long storageColumn = 0;
		for (long rowIndex = 0; rowIndex < nRows; rowIndex++) {
			storageColumn = 0;
			for (int objectIndex = 0; objectIndex < nFull; objectIndex++) {
				for (long insideColIndex = 0; insideColIndex < nCols[objectIndex]; insideColIndex++) {
				valueToStore = ((MultiFormatMatrix)allOfThem[objectIndex]).getValue(rowIndex,insideColIndex);
/*				System.out.println("rowIndex = " + rowIndex + "; objectIndex = " + objectIndex +
						"; storageColumn = " + storageColumn + "insideColIndex = " +  insideColIndex+
						"; valueToStore = " + valueToStore);
*/				AugmentedMatrix.setValue(rowIndex,storageColumn,valueToStore);
				storageColumn++;
				}
			}
		}


		this.pushOutput(AugmentedMatrix, 0);
	}

} 

















