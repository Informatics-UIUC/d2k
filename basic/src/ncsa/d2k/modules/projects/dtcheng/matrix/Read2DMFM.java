package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class Read2DMFM extends InputModule {
	
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
		
		int nDimensions = -1;
		int BufferSize = -5;
		long NumRows = -2;
		long NumCols = -3;
		int FormatIndex = -4;
		long RowToWrite = -6;
		long ColToWrite = -7;
		int nBlocks = -8;
		int LastBlockIndex = -9;
		
		//RandomAccessFile file = new RandomAccessFile(FileName, "rw");
		//File file = new File(FileName);
		
		//file.writeInt(2);
		//file.writeInt(NumRows);
		//file.writeInt(NumCols);
		//byte [] bytes = {1,2,3,4,5,6,7,8};
		
//		System.out.println("trying to create file object...");
		File headerFile = new File(FileName + ".bin");
		if (!headerFile.exists()) {
			System.out.println("um, the header file [" + headerFile + "] has failed to exist...");
			throw new Exception("um, the header file [" + headerFile + "] has failed to exist...");
		}
		FileInputStream file = new FileInputStream(FileName + ".bin");
//		System.out.println("created file input stream [" + file + "]");
		ObjectInputStream ininin = new ObjectInputStream(file);
//		System.out.println("created object input stream [" + ininin + "]");
		nDimensions = ininin.readInt();
//		System.out.println("here is the number of dimensions [" + nDimensions + "]");
		BufferSize = ininin.readInt();
		LastBlockIndex = ininin.readInt();
		NumRows = (long) ininin.readInt();
		NumCols = (long) ininin.readInt();
		System.out.println("nDimensions = " + nDimensions + "; BufferSize = " + BufferSize +
				"; LastBlockIndex = " + LastBlockIndex + "; NumRows = " + NumRows + "; NumCols = " + NumCols);
		ininin.close();
//		System.out.println("closing the stream");
		file.close();
//		System.out.println("closing the file");
		
		//      out.flush();
		//      out.close();
		
		/*		if (nDimensions != 2) {
		 System.out
		 .println("The number of dimensions of the stored matrix must be 2, not "
		 + nDimensions);
		 throw new Exception();
		 }
		 */
		long NumElements = NumRows * NumCols;
		if (NumElements < NumberOfElementsThreshold) { // small means keep it in
			// core; single
			// dimensional in memory
			// is best
			FormatIndex = 1; // Beware the MAGIC NUMBER!!!
		} else { // not small means big, so go out of core; serialized blocks on
			// disk are best
			FormatIndex = 3; // Beware the MAGIC NUMBER!!!
		}
		
		MultiFormatMatrix X = new MultiFormatMatrix(FormatIndex, new long[] {
				NumRows, NumCols });
		
		// Calculate the number of elements in the final block
		int nElementsInFinalBlock = (int) (NumElements) % BufferSize;
		
		// Beware the MAGIC NUMBER!!!
		//    int BufferSize = 1000000;
		double[] DoubleBuffer = new double[BufferSize];
		double[] DoubleBuffer2 = new double[nElementsInFinalBlock];
		
		int i = 0;
		//    int BlockIndex = 0;
		int LookupNumber = 0;
		
		// keep track of where to store the goods. starting at the beginning...
		RowToWrite = 0;
		ColToWrite = 0;
		
		if (LastBlockIndex > 0) {
			nBlocks = LastBlockIndex;
			// read the full blocks
			for (int BlockIndex = 0; BlockIndex < nBlocks; BlockIndex++) {
				// read in the block as an object and double array
				System.out.println("Reading File: " + FileName + BlockIndex + ".bin ...");
				file = new FileInputStream(FileName
						+ BlockIndex + ".bin");
				ininin = new ObjectInputStream(file);
				DoubleBuffer = (double[]) ininin.readObject();
				// write it to the appropriate spots in the matrix
				for (int BufferIndex = 0; BufferIndex < BufferSize; BufferIndex++) {
					X.setValue(RowToWrite, ColToWrite,
							DoubleBuffer[BufferIndex]);
					if (ColToWrite < NumCols - 1) { // as long as the next
						// column isn't beyond the
						// last available column...
						ColToWrite++;
					} else {
						RowToWrite++;
						ColToWrite = 0;
					}
				}
				// close out the file...
				ininin.close();
			}
			
			// read the last block
			// read in the block as an object and double array
			if (RowToWrite < NumRows & ColToWrite < NumCols) {
				System.out.println("Reading File: " + FileName + nBlocks + ".bin ...");
				file = new FileInputStream(FileName + nBlocks
						+ ".bin");
				ininin = new ObjectInputStream(file);
				DoubleBuffer2 = (double[]) ininin.readObject();
				// write it to the appropriate spots in the matrix
				for (int BufferIndex = 0; BufferIndex < nElementsInFinalBlock; BufferIndex++) {
					X.setValue(RowToWrite, ColToWrite, DoubleBuffer2[BufferIndex]);
					if (ColToWrite < NumCols - 1) { // as long as the next column
						// isn't beyond the last
						// available column...
						ColToWrite++;
					} else {
						RowToWrite++;
						ColToWrite = 0;
					}
				}
				// close out the file...
				ininin.close();
			}
		} else {
			// read the last/only block
			// read in the block as an object and double array
			System.out.println("Reading File: " + FileName + LastBlockIndex + ".bin ...");
			
			file = new FileInputStream(FileName + LastBlockIndex
					+ ".bin");
			ininin = new ObjectInputStream(file);
			DoubleBuffer2 = (double[]) ininin.readObject();
			// write it to the appropriate spots in the matrix
			for (int BufferIndex = 0; BufferIndex < nElementsInFinalBlock; BufferIndex++) {
				X.setValue(RowToWrite, ColToWrite, DoubleBuffer2[BufferIndex]);
				if (ColToWrite < NumCols - 1) { // as long as the next column
					// isn't beyond the last
					// available column...
					ColToWrite++;
				} else {
					RowToWrite++;
					ColToWrite = 0;
				}
			}
			// close out the file...
			ininin.close();
			
		}
		
		this.pushOutput(X, 0);
		
	}
	
}