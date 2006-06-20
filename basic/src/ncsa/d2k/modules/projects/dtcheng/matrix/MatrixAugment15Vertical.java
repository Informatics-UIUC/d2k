package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class MatrixAugment15Vertical extends ComputeModule {
	
	public String getModuleName() {
		return "MatrixAugment15Vertical";
	}
	
	public String getModuleInfo() {
		return "This module takes up to 15 matrices and makes them into one matrix where "
		+ "the matrices are placed top to bottom in the order of inputs (top to bottom). "
		+ "If fewer the 15 are needed, the rest should be passed as nulls and should appear at the bottom. "
		+ "<p>THERE IS NO IDIOT-PROOFING: the matrices will be treated as if they are all the same columnsize.";
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
		long nCols = ((MultiFormatMatrix) allOfThem[0]).getDimensions()[1];
		// assuming they are all the same row-size
		long[] nRows = new long[nFull];
		long totalRows = 0; // this actually needs to be zero
		
		for (int objectIndex = 0; objectIndex < nFull; objectIndex++) {
			nRows[objectIndex] = ((MultiFormatMatrix) allOfThem[objectIndex])
			.getDimensions()[0];
			totalRows += nRows[objectIndex];
		}
		
		long NumElements = nCols * totalRows;
		int FormatIndex = -1;
		if (NumElements < NumberOfElementsThreshold) {
			FormatIndex = 1; // Beware the MAGIC NUMBER!!!
		} else {
			FormatIndex = 3; // Beware the MAGIC NUMBER!!!
		}
		
		MultiFormatMatrix AugmentedMatrix = new MultiFormatMatrix(FormatIndex,
				new long[] { totalRows, nCols });
		
		//		System.out.println("AM is [" + AugmentedMatrix.getDimensions()[0] +
		// "]x[" + AugmentedMatrix.getDimensions()[1] + "]");
		
		double valueToStore = -5.3;
		long nRowsThis = -5;
		long storageRow = 0;
		for (int objectIndex = 0; objectIndex < nFull; objectIndex++) {
			nRowsThis = nRows[objectIndex];
			for (long rowIndex = 0; rowIndex < nRowsThis; rowIndex++) {
				for (long colIndex = 0; colIndex < nCols; colIndex++) {
					valueToStore = ((MultiFormatMatrix) allOfThem[objectIndex]).getValue(rowIndex, colIndex);
					/*
					 * System.out.println("rowIndex = " + rowIndex + ";
					 * objectIndex = " + objectIndex + "; storageRow = " +
					 * storageRow + "colIndex = " + colIndex+ "; valueToStore = " +
					 * valueToStore);
					 */
					
					AugmentedMatrix.setValue(storageRow, colIndex, valueToStore);
				}
				storageRow++;
			}
		}
		
		this.pushOutput(AugmentedMatrix, 0);
	}
	
}

