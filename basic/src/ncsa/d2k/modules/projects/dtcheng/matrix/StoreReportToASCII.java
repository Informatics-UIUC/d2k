package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class StoreReportToASCII extends OutputModule {

	public String getModuleName() {
		return "StoreReportToASCII";
	}

	public String getModuleInfo() {
		return "This module will input a Long and four Doubles and then "
				+ "append them to an ASCII file on a new line separated by spaces. ";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Long";
		case 1:
			return "Double0";
		case 2:
			return "Double1";
		case 3:
			return "Double2";
		case 4:
			return "Double3";
		case 5:
			return "BaseFileName";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "Long";
		case 1:
			return "Double0";
		case 2:
			return "Double1";
		case 3:
			return "Double2";
		case 4:
			return "Double3";
		case 5:
			return "BaseFileName: .txt will be appended";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = { "java.lang.Long","java.lang.Double", "java.lang.Double",
				"java.lang.Double", "java.lang.Double", "java.lang.String", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { , };
		return types;
	}

	public void doit() throws Exception {

		long Long0 = ((Long)this.pullInput(0)).longValue();
		double Double0 = ((Double)this.pullInput(1)).doubleValue(); 
		double Double1 = ((Double)this.pullInput(2)).doubleValue(); 
		double Double2 = ((Double)this.pullInput(3)).doubleValue(); 
		double Double3 = ((Double)this.pullInput(4)).doubleValue();
		String FileName = (String) (this.pullInput(5) + ".txt");
		// Beware the MAGIC NUMBER!!! above: appending .txt from the get go


		//    int BufferSize = 1000000;

		try {
			// the info file...
			//			System.out.println(" Writing an info file to " + InfoFileName + "
			// ... ");
			File FileToWrite = new File(FileName);
			FileOutputStream outInfoStream = new FileOutputStream(
					FileToWrite, true);
			PrintWriter outWriterObject = new PrintWriter(outInfoStream);

			outWriterObject.print(Long0 + "\t" + Double0 + "\t" + Double1 + "\t" + Double2 + "\t" + Double3 + "\n");

			// clean up the mess...
			outWriterObject.flush();
			outWriterObject.close();
			//			System.out.println("Done writing file: " + FileName);
		} catch (IOException SomethingWrong) {
			System.out.println("Something went wrong trying to write "
					+ FileName );
		}

	}
}