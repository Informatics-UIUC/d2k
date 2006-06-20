package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class CheckOnABlock extends InputModule {
	
	public String getModuleName() {
		return "CheckOnABlock";
	}
	
	public String getModuleInfo() {
		return "This module attempts to read a particular block from a 2-d MFM from disk that was written by Write2DMFM."
		+ "The files should have the suffix .bin . Write2DMFM write it out as a series of blocks "
		+ "and so only the base name should be given to this module..." +
		" If you just want to see the headers, pass in a null for the block number.";
	}
	
	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "FileName";
		case 1:
			return "OutputFileName";
		case 2:
			return "BlockToCheck";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "FileName: the base name of the files without the .bin";
		case 1:
			return "OutputFileName";
		case 2:
			return "BlockToCheck";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String[] getInputTypes() {
		String[] types = { "java.lang.String", "java.lang.String",
				"java.lang.Integer", };
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
		
		String FileName = (String) this.pullInput(0);
		String OutputFileName = (String) this.pullInput(1);
		Object blockTemp = this.pullInput(2);
		int BlockToCheck = -14;
		
		boolean onlyPrintHeaders = true;
		if (blockTemp == null) {
			onlyPrintHeaders = true;
		} else {
			BlockToCheck = ((Integer)blockTemp).intValue();
		}
		
		int nDimensions = -1;
		int bufferSize = -5;
		long nRows = -2;
		long nCols = -3;
//		int FormatIndex = -4;
//		long RowToWrite = -6;
//		long ColToWrite = -7;
//		int nBlocks = -8;
		int lastBlockIndex = -9;
		
		//RandomAccessFile file = new RandomAccessFile(FileName, "rw");
		//File file = new File(FileName);
		
		//file.writeInt(2);
		//file.writeInt(NumRows);
		//file.writeInt(NumCols);
		//byte [] bytes = {1,2,3,4,5,6,7,8};
		
		File theHeaderFile = new File(FileName + ".bin");
		
		if (!theHeaderFile.exists()) {
			System.out.println("The file [" + theHeaderFile + "] does not exist...");
			throw new Exception("File not found... [" + theHeaderFile + "]");
		}
		System.out.println("trying to create file object...");
		
		FileInputStream file = new FileInputStream(theHeaderFile);
		System.out.println("created file input stream [" + file + "]");
		ObjectInputStream ininin = new ObjectInputStream(file);
		System.out.println("created object input stream [" + ininin + "]");
		nDimensions = ininin.readInt();
		System.out.println("here is the number of dimensions [" + nDimensions + "]");
		bufferSize = ininin.readInt();
		lastBlockIndex = ininin.readInt();
		nRows = ininin.readInt();
		nCols = ininin.readInt();
		System.out.println("nDimensions = " + nDimensions + "; BufferSize = " + bufferSize +
				"; LastBlockIndex = " + lastBlockIndex + "; NumRows = " + nRows + "; NumCols = " + nCols);
		ininin.close();
		System.out.println("closing the stream");
		file.close();
		System.out.println("closing the file");
		
		//      out.flush();
		//      out.close();
		
		double[] theBlock = null;
		
		if (!onlyPrintHeaders) {
			// now read the block we're looking for
			File theBlockFile = new File(FileName + BlockToCheck + ".bin");
			
			if (!theBlockFile.exists()) {
				System.out.println("The file [" + theBlockFile + "] does not exist...");
				throw new Exception("File not found... [" + theBlockFile + "]");
			}
			System.out.println("trying to create file object...");
			
			file = new FileInputStream(theBlockFile);
			System.out.println("created file input stream [" + file + "]");
			ininin = new ObjectInputStream(file);
			System.out.println("created object input stream [" + ininin + "]");
			
			theBlock = (double[])ininin.readObject();
			
			ininin.close();
			System.out.println("closing the stream");
			file.close();
			System.out.println("closing the file");
			
			
			
			// calculate some simple stuff
			int nElementsHereToFore = (bufferSize * BlockToCheck);
			int firstColInBlock = nElementsHereToFore%(int)nCols;
			int lastColIndex = (int)nCols - 1;
			String delimiterString = new String(" "); // Beware the MAGIC
													  // NUMBER!!!
			
			int currentCol = firstColInBlock;
			// now, the idea is to write it out as a text file in the form it
			// was supposed to be in originally
			File theOutputFile = new File(OutputFileName);
			FileOutputStream theOutputStream = new FileOutputStream(theOutputFile);
			PrintWriter outoutout = new PrintWriter(theOutputStream);
			
			for (int emptyColIndex = 0; emptyColIndex < firstColInBlock; emptyColIndex++) {
				outoutout.print(Double.NaN + " ");
			}
			for (int elementIndex = 0; elementIndex < bufferSize; elementIndex++) {
				if (currentCol != lastColIndex) {
					outoutout.print(theBlock[elementIndex] + delimiterString);
					currentCol++;
				} else {
					// we are done with this row
					outoutout.print(theBlock[elementIndex] + "\n");
					currentCol = 0;
				}
			}
			
			
			outoutout.flush();
			outoutout.close();
			theOutputStream.close();
			
		}
		
		
		
		// this.pushOutput(X, 0);
		
	}
	
}