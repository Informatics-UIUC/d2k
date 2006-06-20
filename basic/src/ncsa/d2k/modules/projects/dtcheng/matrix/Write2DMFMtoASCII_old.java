package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class Write2DMFMtoASCII_old extends OutputModule {

	public String getModuleName() {
		return "Write2DMFMtoASCII_old";
	}

	public String getModuleInfo() {
		return "This module attempts to write a 2-d MFM to disk, hopefully as ASCII. " +
		"The matrix will be stored in a file that starts with BaseFileName " +
		"and followed by .txt . Additionally, some info stored in a file called " +
		"BaseFileName followed by .info.txt .<p>This is the orignal writer which is apparently not Linux compatible...";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "BaseFileName";
		case 2:
			return "DelimiterString";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "BaseFileName: base name of where to store matrix (no suffix)";
		case 2:
			return "DelimiterString: a string to go between the numbers in a row";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.String", "java.lang.String", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
		return types;
	}

	public void doit() throws Exception {

		MultiFormatMatrix X = (MultiFormatMatrix) this.pullInput(0);
		String BaseFileName = (String) this.pullInput(1);
		String DelimiterString = (String) this.pullInput(2);

		String FileName = BaseFileName + ".txt";
		String InfoFileName = BaseFileName + ".info.txt";

		
		long NumRows = X.getDimensions()[0];
		long NumCols = X.getDimensions()[1];
		int FormatIndex = X.dataFormat;

		//    int BufferSize = 1000000;


		try {
			// the info file...
//			System.out.println(" Writing an info file to " + InfoFileName + " ... ");
			File InfoFileToWrite = new File(InfoFileName);
//			System.out.println("info: created file object");
			FileOutputStream outInfoStream = new FileOutputStream(InfoFileToWrite);
//			System.out.println("info: created output stream object");
			PrintWriter outInfoWriterObject = new PrintWriter(outInfoStream);
//			System.out.println("info: created output printwriter object");

			outInfoWriterObject.print(NumRows + "\t = Number of Rows\n");
			outInfoWriterObject.print(NumCols + "\t = Number of Columns\n");
			outInfoWriterObject.print((NumRows*NumCols) + "\t = Total Number of Elements\n");
			outInfoWriterObject.print(FormatIndex + "\t = The MultiFormatMatrix format the matrix was stored in\n");
			outInfoWriterObject.print(DelimiterString + "\t = The string used to delimit elements in the Rows");

//			System.out.println("info: sent some output");
			
			// finish cleaning up the mess...
			outInfoWriterObject.flush();
//			System.out.println("info: successfully flushed");
			outInfoWriterObject.close();
//			System.out.println("info: successfully closed");
//			System.out.println("Done writing info file " + InfoFileName + " ... ");

			// the actual goods
//			System.out.println(" Starting to write data file: " + FileName);
			// Create a File
			File FileToWrite = new File(FileName);
			// Create an Output Stream
			FileOutputStream outStream = new FileOutputStream(FileToWrite);
			// Filter bytes to ASCII
			PrintWriter outWriterObject = new PrintWriter(outStream);

			// Here we actually write to file
			for (long RowIndex = 0; RowIndex < NumRows; RowIndex++) {
			for (long ColIndex = 0; ColIndex < NumCols; ColIndex++) {
				if (ColIndex < NumCols - 1) {
					// not the last one in the row...
					outWriterObject.print(X.getValue(RowIndex,ColIndex) + DelimiterString);
				}
				else {
					outWriterObject.print(X.getValue(RowIndex,ColIndex) + "\n");
				}
			}
			}
			
			// clean up the mess...
			outWriterObject.flush();
			outWriterObject.close();
//			System.out.println("Done writing file: " + FileName);
		} catch (IOException SomethingWrong) {
			System.out.println("Something went wrong trying to write "
					+ InfoFileName + " or " + FileName);
		}

		this.pushOutput(X, 0);

	}
}