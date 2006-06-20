package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import java.io.*;

import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class SplitBinariesToRowChunks extends ComputeModule {

	public String getModuleName() {
		return "SplitBinariesToRowChunks";
	}

	public String getModuleInfo() {
		return "This module will pull in a matrix and then slice and dice it. " +
		"into a specified number of chunks. That is, the columns will be " +
		"kept together, but the rows will be pushed out in blocks along with " +
		"a numbered string and such. This is meant for cutting up datasets into " +
		"chunks for parallel processing and use with Write2DMFM. The chunks will " +
		"be sequentially pushed out of the output pipes.<p>This is now being adapted " +
		"to work with my silly binary representations so as to not have to be in " +
		"memory all at once... It may be the case that the input buffersize should be " +
		"an even multiple of the number of columns...";
	}

	public String getInputName(int i) {
		switch (i) {
	      case 0:
	        return "BigFileName";
	      case 1:
	      	return "nChunks";
	      case 2:
	      	return "BufferSize";
	      case 3:
	        return "FileName";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
	      case 0:
	        return "BigFileName";
	      case 1:
	      	return "nChunks";
	      case 2:
	      	return "BufferSize";
	      case 3:
	        return "FileName";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"java.lang.String",
				"java.lang.Integer", "java.lang.Integer", "java.lang.String", };
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
		String BigFileName = (String)this.pullInput(0);
		int nChunks = ((Integer)this.pullInput(1)).intValue();
		int BufferSize = ((Integer)this.pullInput(2)).intValue();
		String BaseFileName = (String)this.pullInput(3);

		// ok, we're gonna need to be reading in the meta-data file...
		File metaDataFile = new File(BigFileName + ".bin");
		if (!metaDataFile.exists()) {
			System.out.println("um, your file ain't there [" + metaDataFile + "]");
			throw new Exception("File [" + metaDataFile + "] does not exist, could not read data.");
		}
		FileInputStream inputFile = new FileInputStream(metaDataFile);
		ObjectInputStream ininin = new ObjectInputStream(inputFile);
		int nDimensions = ininin.readInt();
		int bufferSizeInput = ininin.readInt();
		int lastInputBufferIndex = bufferSizeInput - 1;
		int lastBlockIndex = ininin.readInt();
		int nRows = ininin.readInt();
		int nCols = ininin.readInt();
		
		if (bufferSizeInput % nCols != 0) {
			System.out.println("i'm gonna make you have the buffersize of the read blocks [" +
					bufferSizeInput + "] be an even multiple of the number of columns [" + nCols);
			throw new Exception("yo, make the buffersize of the read blocks be an even multiple of the number of columns...");
		}
		
		if (nRows < nChunks) {
			System.out.println("Insufficient rows [" + nRows + "] to spread among that many chunks [" +
					nChunks + "] ...");
			throw new Exception("Too many chunks [" + nChunks + "], not enough rows [" + nRows + "]");
		}
		
		System.out.println("nDimensions = " + nDimensions + "; bufferSizeInput = " + bufferSizeInput
							+ "; LastBlockIndex = " + lastBlockIndex + "; NumRows = "
							+ nRows + "; NumCols = " + nCols);
		ininin.close();
		inputFile.close();
		
		int bufferSizeToUse = (BufferSize/nCols + 1) * nCols;
		int lastOutputIndex = bufferSizeToUse - 1;
		System.out.println("output buffersize = " + bufferSizeToUse);
		
		// do a modulus to figure out how bad the remainder is
		int leftOver = -9;
		int nRowsChunk = -5;
		int nRowsRemaining = -6;
		String thisChunkFileName = new String();
		double[] inputBuffer = new double[bufferSizeInput];
		double[] outputBuffer = new double[bufferSizeToUse];
		double[] finalBuffer = null;
		
		File inputBufferFile = null;
		FileInputStream inputStream = null;
		ObjectInputStream inputReader = null;
		FileOutputStream outputStream = null;
		ObjectOutputStream outputWriter = null;
		FileOutputStream metaDataStream = null;
		ObjectOutputStream metaDataWriter = null;
		
		int inputBlockIndex = 0; // this actually needs to be zero
		int outputBlockIndex = 0; // this actually needs to be zero
		int currentInputIndex = Integer.MAX_VALUE; // this actually needs to be huge
		int firstOpenSpot = 0; // this actually needs to be zero
		nRowsRemaining = nRows;
		
		for (int chunkIndex = 0; chunkIndex < nChunks; chunkIndex++){
			outputBlockIndex = 0; // starting over for each chunk...
			firstOpenSpot = 0;

			leftOver = nRowsRemaining % (nChunks - chunkIndex);
			if (leftOver == 0) {
				// it's all good, chunk away
				nRowsChunk = nRows / nChunks;
			} else {
				nRowsChunk = nRows/ nChunks + 1;
			}

			thisChunkFileName = new String(BaseFileName + chunkIndex + "_");

			// let's do this the long way and read it in one row at a time and write it back out the same way...
			for (int readRowIndex = 0; readRowIndex < nRowsChunk; readRowIndex++) {
				// if we are done with the previous input buffer, read in the next one...
				if (currentInputIndex > lastInputBufferIndex) {
					inputBufferFile = new File(BigFileName + inputBlockIndex + ".bin");
					inputStream = new FileInputStream(inputBufferFile);
					inputReader = new ObjectInputStream(inputStream);
					
					inputBuffer = (double[])inputReader.readObject();
					
					inputReader.close();
					inputStream.close();
					
					inputBlockIndex++;
					currentInputIndex = 0;
				}
				
				// put a row in the output buffer
				for (int moveIndex = 0; moveIndex < nCols; moveIndex++) {
					outputBuffer[firstOpenSpot + moveIndex] = inputBuffer[currentInputIndex + moveIndex];
/*					System.out.println("just moved [" + outputBuffer[firstOpenSpot + moveIndex]
							+ "] from [" + (currentInputIndex + moveIndex) + "] to [" +
							(firstOpenSpot + moveIndex) + "]");
*/
				}
				firstOpenSpot += nCols;
				currentInputIndex += nCols;

				// if the outputbuffer is full, write it to disk
				if (firstOpenSpot > lastOutputIndex) {
					// we need to move this to a file, but how about a small one:
					System.out.println("internal Writing File: " + thisChunkFileName + outputBlockIndex + ".bin ...");
					outputStream = new FileOutputStream(thisChunkFileName + outputBlockIndex + ".bin");
					outputWriter = new ObjectOutputStream(outputStream);
					outputWriter.writeObject(outputBuffer);
					outputWriter.flush();
					outputWriter.close();
					// tend to the counters
					outputBlockIndex++;
					firstOpenSpot = 0;

				}
				
			}
			
			// we need to write out the last block and the meta-data
			if (firstOpenSpot > 0) {
				finalBuffer = new double[firstOpenSpot];
				for (int finalMoveIndex = 0; finalMoveIndex < firstOpenSpot; finalMoveIndex++) {
					finalBuffer[finalMoveIndex] = outputBuffer[finalMoveIndex];
				}
				System.out.println("final in chunk Writing File: " + thisChunkFileName + outputBlockIndex + ".bin ...");
				outputStream = new FileOutputStream(thisChunkFileName + outputBlockIndex + ".bin");
				outputWriter = new ObjectOutputStream(outputStream);
				outputWriter.writeObject(finalBuffer);
				outputWriter.flush();
				outputWriter.close();
			}

			// now for the meta-data
			// create the new output meta-data file
			metaDataStream = new FileOutputStream(thisChunkFileName + ".bin");
			metaDataWriter = new ObjectOutputStream(metaDataStream);
			metaDataWriter.writeInt(2);
			metaDataWriter.writeInt(bufferSizeToUse);
			metaDataWriter.writeInt(outputBlockIndex);
			metaDataWriter.writeInt(nRowsChunk);
			metaDataWriter.writeInt(nCols);
			metaDataWriter.flush();
			metaDataWriter.close();
			
			nRowsRemaining -= nRowsChunk;

/*			System.out.println("done transcribing chunk [" + chunkIndex +
					"] nRowsRemaining = " + nRowsRemaining);
*/
		}
	}
}

