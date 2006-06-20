package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class Read2DMFMfromASCII extends InputModule {

	public String getModuleName() {
		return "Read2DMFMfromASCII";
	}

	public String getModuleInfo() {
		return "This module attempts to read a 2-d MFM that has been written to disk "
				+ " using the Write2DMFMtoASCII module. "
				+ "The matrix should have been stored in a file that starts with BaseFileName "
				+ "and followed by .txt . Additionally, some info should stored in a file called "
				+ "BaseFileName followed by .info.txt . <p>"
				+ "The info file should have the following information: <p>"
				+ "#(tab)= Number of Rows<br>"
				+ "#(tab)= Number of Columns<br>"
				+ "#(tab)= Total Number of Elements<br>"
				+ "#(tab)= The MultiFormatMatrix format the matrix was stored in<br>"
				+ "(char)(tab)= The string used to delimit elements in the Rows<p>"
				+ "The format to store it in will be determined by the 3rd line of the info file. "
				+ "If you don't like that, just use a text editor to change it before "
				+ "trying to load it. <p> There exist magic numbers....";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "BaseFileName";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "BaseFileName: base name of where to store matrix (no suffix)";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = { "java.lang.String", };
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

		String BaseFileName = (String) this.pullInput(0);

		String FileName = BaseFileName + ".txt";
		String InfoFileName = BaseFileName + ".info.txt";

		// create the "File"
		File InfoFileObject = new File(InfoFileName);
		// create a character input stream (FileReader inherits from
		// InputStreamReader
		FileReader InfoStream = new FileReader(InfoFileObject);
		// filter the input stream, buffers characters for efficiency
		BufferedReader InfoReader = new BufferedReader(InfoStream);
		// read the first line
		String LineContents = InfoReader.readLine();
		// Beware the MAGIC NUMBER!!! i am only going to read, at most, a pile
		// of characters on each line
//		int MaxNumCharacters = 50;
		int indexOfEnd = 0;

		indexOfEnd = LineContents.indexOf("=") - 2;
		long nRows = Long.parseLong(LineContents.substring(0, indexOfEnd));

		// read the second line
		LineContents = InfoReader.readLine();
		indexOfEnd = LineContents.indexOf("=") - 2;
		long nCols = Long.parseLong(LineContents
				.substring(0, indexOfEnd));

		// read the third line (who cares?)
		LineContents = InfoReader.readLine();
		// read the fourth line
		LineContents = InfoReader.readLine();
		indexOfEnd = LineContents.indexOf("=") - 2;
		int FormatIndex = Integer.parseInt(LineContents.substring(0,
				indexOfEnd));

		// read the fifth line
		LineContents = InfoReader.readLine();
		String delimiterString = LineContents.substring(0, indexOfEnd);

		// close up shop on that part...
		InfoReader.close();
		InfoStream.close();

		MultiFormatMatrix X = new MultiFormatMatrix(FormatIndex, new long[] {
				nRows, nCols });

		// create the "File"
		File DataFileObject = new File(FileName);
		// create a character input stream (FileReader inherits from
		// InputStreamReader
		FileReader DataStream = new FileReader(DataFileObject);
		// filter the input stream, buffers characters for efficiency
		BufferedReader DataReader = new BufferedReader(DataStream);

		// actually try to read in the goodies and crank it out and write it
		// down...

//		boolean stayOnThisLineFlag = true;
		int previousEndIndex = 0;
		int currentEndIndex = 0;
		long currentColumn = 0;
		double valueToStore = 0.0;
		String tempString = new String();

		for (int rowIndex = 0; rowIndex < nRows; rowIndex++) {
			// read it in...
			LineContents = DataReader.readLine();
			// reset everything going in...
			currentColumn = 0;
			previousEndIndex = -1;
			currentEndIndex = 0;

			while (currentColumn < nCols) {
				currentEndIndex = LineContents.indexOf(delimiterString,
						previousEndIndex + 1);
				if ((currentEndIndex != previousEndIndex)
						&& (currentEndIndex != -1)) {
					// we are in the interior of the line...
					tempString = LineContents.substring(previousEndIndex + 1,
							currentEndIndex);
				} else {
					tempString = LineContents.substring(previousEndIndex + 1);
				}
//				System.out.println("tempString = [" + tempString + "]");
				valueToStore = Double.parseDouble(tempString);

				X.setValue(rowIndex, currentColumn, valueToStore);
				previousEndIndex = currentEndIndex;
				currentColumn++;

			}

		}

		DataReader.close();
		DataStream.close();

		this.pushOutput(X, 0);

	}
}