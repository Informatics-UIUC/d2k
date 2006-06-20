package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class Write2DMFMagain
extends OutputModule {
	
	public String getModuleName() {
		return "Write2DMFMagain";
	}
	
	public String getModuleInfo() {
		return "This module attempts to write a 2-d MFM to disk. but i am changing it up...";
	}
	
	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "FileName";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "FileName: base name of where to store matrix";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.String",
		};
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
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
		};
		return types;
	}
	
	public void doit() throws Exception {
		
		MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
		String FileName = (String)this.pullInput(1);
		
		long nRows = X.getDimensions()[0];
		long nCols = X.getDimensions()[1];
		
		//RandomAccessFile file = new RandomAccessFile(FileName, "rw");
		//File file = new File(FileName);
		
		//file.writeInt(2);
		//file.writeInt(NumRows);
		//file.writeInt(NumCols);
		//byte [] bytes = {1,2,3,4,5,6,7,8};
		
		
		
		//    int BufferSize = 1000000;
		//    double [] DoubleBuffer = new double[BufferSize];
		
		//    int elementCounter = 0;
//		int nBlocks = (int) (nRows*nCols / BufferSize) + 1;
		//    int BlockIndex = 0;
//		long elementCounter = 0;
//		int blockIndex = 0;
//		boolean stayOnThisBlock = true;
		
		try {

//			blockIndex = 0;
//			elementCounter = 0;
			System.out.println("Writing File: " + FileName + ".bin ...");
			FileOutputStream file = new FileOutputStream(FileName + ".bin");
			ObjectOutputStream out = new ObjectOutputStream(file);

			// write some meta-data
			
			out.writeLong(nRows);
			out.writeLong(nCols);

			for (long rowIndex = 0; rowIndex < nRows; rowIndex++) {
				for (long colIndex = 0; colIndex < nCols; colIndex++) {
						out.writeDouble(X.getValue(rowIndex,colIndex));
				}
			}
			// close out this file and stream
			out.flush();
			out.close();
			file.close();
			
		}
		catch (java.io.IOException IOE) {
			System.out.println("IOException: writing data...");
		}
				
		
		this.pushOutput(X, 0);
		
	}
	
}
