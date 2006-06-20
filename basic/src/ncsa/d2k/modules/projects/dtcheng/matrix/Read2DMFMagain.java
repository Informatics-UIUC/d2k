package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class Read2DMFMagain extends InputModule {
	
	public String getModuleName() {
		return "Read2DMFM";
	}
	
	public String getModuleInfo() {
		return "This module attempts to read a 2-d MFM from disk that was written by Write2DMFM."
		+ "The files should have the suffix .bin . Write2DMFM write it out as a series of blocks "
		+ "and so only the base name should be given to this module...";
	}
	
	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "FileName";
		case 1:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "FileName: the base name of the files without the .bin";
		case 1:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String[] getInputTypes() {
		String[] types = { "java.lang.String",
				"java.lang.Long", };
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
		
		String FileName = (String) this.pullInput(0);
		//		int LastBlockIndex = ((Integer) this.pullInput(1)).intValue();
		long NumberOfElementsThreshold = ((Long) this.pullInput(1)).longValue();
		
		long nRows = -2;
		long nCols = -3;
		int FormatIndex = -4;
		long nElements = -15;
		
		MultiFormatMatrix X = null;
		//RandomAccessFile file = new RandomAccessFile(FileName, "rw");
		//File file = new File(FileName);
		
		//file.writeInt(2);
		//file.writeInt(NumRows);
		//file.writeInt(NumCols);
		//byte [] bytes = {1,2,3,4,5,6,7,8};
		
		try {
			FileInputStream file = new FileInputStream(FileName + ".bin");
			System.out.println("created file object");
			ObjectInputStream ininin = new ObjectInputStream(file);
			System.out.println("created object input stream");
//			bufferSize = ininin.readLong();
//			System.out.println("bufferSize = " + bufferSize);
//			lastBlockIndex = ininin.readInt();
			nRows = ininin.readLong();
			nCols = ininin.readLong();
			System.out.println("read the metadata: dimensions= = [" + nRows + "][" + nCols + "]");

			nElements = nRows * nCols;
			if (nElements < NumberOfElementsThreshold) { // small means keep it in
				// core; single
				// dimensional in memory
				// is best
				FormatIndex = 1; // Beware the MAGIC NUMBER!!!
			} else { // not small means big, so go out of core; serialized blocks on
				// disk are best
				FormatIndex = 3; // Beware the MAGIC NUMBER!!!
			}
			
			X = new MultiFormatMatrix(FormatIndex, new long[] {
					nRows, nCols });
			double valueToStore = Double.NaN;

			for (long rowIndex = 0; rowIndex < nRows ; rowIndex++) {
				for (long colIndex = 0; colIndex < nCols; colIndex++) {
					valueToStore = ininin.readDouble();
					X.setValue(rowIndex,colIndex,valueToStore);
				}
			}
			ininin.close();
			System.out.println("closing the stream");
			file.close();
			System.out.println("closing the file");
			//      out.flush();
			//      out.close();
		} catch (java.io.IOException IOE) {
			System.out.println("IOException: the goods...");
		}
		
			

		
		this.pushOutput(X, 0);
		
	}
	
}