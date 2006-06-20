package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class WriteColToArc extends OutputModule {

	public String getModuleName() {
		return "WriteColToArc";
	}

	public String getModuleInfo() {
		return "This module attempts to write a column MFM to an Arc ASCII export "
				+ "formatted file. This should perform the opposite operation as ReadArcMatrixToColumn";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "ColumnMatrix";
		case 1:
			return "MetaDataMatrix";
		case 2:
			return "BaseFileName";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "ColumnMatrix";
		case 1:
			return "MetaDataMatrix. This will contain the ncols, "
					+ "nrows, xllcorner, yllcorner, cellsize, NoDataValue "
					+ "information as a single column 2-D MFM, in that order.";
		case 2:
			return "BaseFileName: base name of where to store matrix (no suffix)";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.String", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "MetaDataMatrix";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "MetaDataMatrix";
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

		MultiFormatMatrix ColumnMatrix = (MultiFormatMatrix) this.pullInput(0);
		MultiFormatMatrix MetaDataMatrix = (MultiFormatMatrix) this
				.pullInput(1);
		String BaseFileName = (String) this.pullInput(2);

		String ArcFileName = BaseFileName + ".txt";
		//		String InfoFileName = BaseFileName + ".info.txt";

		long NumRowsColumn = ColumnMatrix.getDimensions()[0];
		long NumColsColumn = ColumnMatrix.getDimensions()[1];
		long NumRowsMeta = MetaDataMatrix.getDimensions()[0];
		long NumColsMeta = MetaDataMatrix.getDimensions()[1];
		long NumColumnsStatedInMeta = (long) MetaDataMatrix.getValue(0, 0);
		double NoDataValue = MetaDataMatrix.getValue(5, 0);

		// a bunch of idiot-proofing...
		if (NumColsColumn != 1) {
			System.out.println("NumColsColumn [" + NumColsColumn
					+ "] != 1: not a column matrix");
			throw new Exception();
		}
		if (NumColsMeta != 1) {
			System.out.println("NumColsMeta [" + NumColsMeta
					+ "] != 1: metadata not a single column");
			throw new Exception();
		}
		if (NumRowsMeta != 6) {
			System.out.println("NumRowsMeta [" + NumRowsMeta
					+ "] != 6: metadata not correct length (6)");
			throw new Exception();
		}

		long NumElements = (long) MetaDataMatrix.getValue(0, 0)
				* (long) MetaDataMatrix.getValue(1, 0);
		if (NumRowsColumn != NumElements) {
			System.out.println("NumRowsColumn [" + NumRowsColumn
					+ "] != NumElements [" + NumElements
					+ "]: metadata do not match real data");
			throw new Exception();
		}

		//    int BufferSize = 1000000;

		try {
			// the info file...
			System.out.println(" Writing to file " + ArcFileName + " ... ");
			File ArcFileToWrite = new File(ArcFileName);
			FileOutputStream outArcStream = new FileOutputStream(ArcFileToWrite);
			PrintWriter outArcWriterObject = new PrintWriter(outArcStream);

			// write the headers
			outArcWriterObject.print("ncols         "
					+ (int) MetaDataMatrix.getValue(0, 0) + "\n");
			outArcWriterObject.print("nrows         "
					+ (int) MetaDataMatrix.getValue(1, 0) + "\n");
			outArcWriterObject.print("xllcorner     "
					+ MetaDataMatrix.getValue(2, 0) + "\n");
			outArcWriterObject.print("yllcorner     "
					+ MetaDataMatrix.getValue(3, 0) + "\n");
			outArcWriterObject.print("cellsize      "
					+ MetaDataMatrix.getValue(4, 0) + "\n");
			outArcWriterObject.print("NODATA_value  "
					+ (int)MetaDataMatrix.getValue(5, 0) + "\n");

			// write the data out in the right order...
			double ValueToStore = 0;
			for (long RowIndex = 0; RowIndex < NumRowsColumn; RowIndex++) {
				ValueToStore = ColumnMatrix.getValue(RowIndex, 0);
				if ((RowIndex + 1) % NumColumnsStatedInMeta != 0) {
					if (Double.isNaN(ValueToStore)) {
						outArcWriterObject.print(NoDataValue + " ");
					} else {
						outArcWriterObject.print(ValueToStore + " ");
					}
				} else {
					if (Double.isNaN(ValueToStore)) {
						outArcWriterObject.print(NoDataValue + "\n");
					} else {
						outArcWriterObject.print(ValueToStore + "\n");
					}
				}

			}

			// finish cleaning up the mess...
			outArcWriterObject.flush();
			outArcWriterObject.close();
			System.out.println("Done writing file " + ArcFileName
					+ " ... ");
		} catch (IOException SomethingWrong) {
			System.out.println("Something went wrong trying to write "
					+ ArcFileName);
		}

		this.pushOutput(ColumnMatrix, 0);
		this.pushOutput(MetaDataMatrix, 1);

	}
}