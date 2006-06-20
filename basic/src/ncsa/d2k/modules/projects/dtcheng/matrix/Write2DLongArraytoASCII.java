package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class Write2DLongArraytoASCII extends OutputModule {

	public String getModuleName() {
		return "Write2DLongArraytoASCII";
	}

	public String getModuleInfo() {
		return "This module attempts to write a 2-d long array to disk, hopefully as ASCII. " +
		"The matrix will be stored in a file that starts with BaseFileName " +
		"and followed by .txt . Additionally, some info stored in a file called " +
		"BaseFileName followed by .info.txt .";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "LongArray";
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
			return "LongArray";
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
				"java.lang.Long",
				"java.lang.String", "java.lang.String", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "LongArray";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "LongArray";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "java.lang.Long", };
		return types;
	}

	public void doit() throws Exception {

		long[][] X = (long[][]) this.pullInput(0);
		String BaseFileName = (String) this.pullInput(1);
		String DelimiterString = (String) this.pullInput(2);

		String FileName = BaseFileName + ".txt";
		String InfoFileName = BaseFileName + ".info.txt";

		
		int NumRows = X.length;
		int NumCols = X[0].length;

		//    int BufferSize = 1000000;


		try {
			// the info file...
//			System.out.println(" Writing an info file to " + InfoFileName + " ... ");
			File InfoFileToWrite = new File(InfoFileName);
			FileOutputStream outInfoStream = new FileOutputStream(InfoFileToWrite);
			PrintWriter outInfoWriterObject = new PrintWriter(outInfoStream);

			outInfoWriterObject.print(NumRows + "\t = Number of Rows\n");
			outInfoWriterObject.print(NumCols + "\t = Number of Columns\n");
			outInfoWriterObject.print((NumRows*NumCols) + "\t = Total Number of Elements\n");
			outInfoWriterObject.print(DelimiterString + "\t = The string used to delimit elements in the Rows");
			
			// finish cleaning up the mess...
			outInfoWriterObject.flush();
			outInfoWriterObject.close();
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
			for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
			for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
				if (ColIndex < NumCols - 1) {
					// not the last one in the row...
					outWriterObject.print(X[RowIndex][ColIndex] + DelimiterString);
				}
				else {
					outWriterObject.print(X[RowIndex][ColIndex] + "\n");
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