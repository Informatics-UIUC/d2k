package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class Read2DMFMsToAugmentVertical extends InputModule {
	
	public String getModuleName() {
		return "Read2DMFMsToAugmentVertical";
	}
	
	public String getModuleInfo() {
		return "This module attempts to read a whole stack of 2-d MFMs from disk that were written by Write2DMFM "
		+ "and then stacks them up vertically. You should feed in filenames in the first contiguous block of inputs "
		+ " and nulls in the unused ones. This is to put together large blocks in a memory efficient manner. "
		+ "The files should have the suffix .bin . Write2DMFM write it out as a series of blocks "
		+ "and so only the base name should be given to this module... <p> THERE IS NO IDIOT-PROOFING!!! It "
		+ "also assumes that it is too big for memory, otherwise, why would you use this? <p> Additionally, " +
		"i'm gonna try to have this sum stuff as it goes so that i can standardize it more easily later... maybe...";
	}
	
	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "FileName";
		case 1:
			return "FileName";
		case 2:
			return "FileName";
		case 3:
			return "FileName";
		case 4:
			return "FileName";
		case 5:
			return "FileName";
		case 6:
			return "FileName";
		case 7:
			return "FileName";
		case 8:
			return "FileName";
		case 9:
			return "FileName";
		case 10:
			return "BufferSize";
		case 11:
			return "OutputFileName";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "FileName: the base name of the files without the .bin";
		case 1:
			return "FileName";
		case 2:
			return "FileName";
		case 3:
			return "FileName";
		case 4:
			return "FileName";
		case 5:
			return "FileName";
		case 6:
			return "FileName";
		case 7:
			return "FileName";
		case 8:
			return "FileName";
		case 9:
			return "FileName";
		case 10:
			return "BufferSize";
		case 11:
			return "OutputFileName";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String[] getInputTypes() {
		String[] types = { "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.Integer",
				"java.lang.String", };
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
		
		int nObjects = 10; // Beware the MAGIC NUMBER!!!
		Object[] allOfThem = new Object[nObjects];
		
		for (int objectIndex = 0; objectIndex < nObjects; objectIndex++) {
			allOfThem[objectIndex] = (String) this.pullInput(objectIndex);
		}
		int RawBufferSize = ((Integer) this.pullInput(10)).intValue();
		String OutputName = (String) this.pullInput(11);
		

		// determine which ones are actually full
		boolean[] fullFlags = new boolean[nObjects];
		int nFull = 0; // this actually needs to be zero
		
		for (int objectIndex = 0; objectIndex < nObjects; objectIndex++) {
			if ((allOfThem[objectIndex] == null)) {
				fullFlags[objectIndex] = false;
			} else {
				fullFlags[objectIndex] = true;
				nFull++;
			}
		}
		
		int nDimensions = -1;
		int bufferSize[] = new int[nFull];
		long nRows[] = new long[nFull];
		long nCols[] = new long[nFull];
		int lastBlockIndex[] = new int[nFull];
		
		
		// first, we will read in all the header files and figure out the total
		// number of rows we need
		
		int myFiguredLastBlock = -5;
		
		long totalRows = 0; // this actually needs to be zero...
		try {
			for (int objectIndex = 0; objectIndex < nFull; objectIndex++) {
				String inputFileName = (String) allOfThem[objectIndex];
				
				FileInputStream inputFile = new FileInputStream(inputFileName + ".bin");
				ObjectInputStream ininin = new ObjectInputStream(inputFile);
				nDimensions = ininin.readInt();
				bufferSize[objectIndex] = ininin.readInt();
				lastBlockIndex[objectIndex] = ininin.readInt();
				nRows[objectIndex] = (long) ininin.readInt();
				nCols[objectIndex] = (long) ininin.readInt();
				
				if (nRows[objectIndex]*nCols[objectIndex] % bufferSize[objectIndex] == 0) {
					myFiguredLastBlock = (int)(nRows[objectIndex]*nCols[objectIndex] / bufferSize[objectIndex] - 1);
				} else {
					myFiguredLastBlock = (int)(nRows[objectIndex]*nCols[objectIndex] / bufferSize[objectIndex] + 1) - 1;
				}
				
				if (myFiguredLastBlock == lastBlockIndex[objectIndex]) {
					//					System.out.println("bumping up for object index " + objectIndex);
					lastBlockIndex[objectIndex]++;
				} else {
					//					System.out.println("NOT bumping up for object index " + objectIndex);
				}
				
				System.out.println("nDimensions = " + nDimensions + "; BufferSize = " + bufferSize[objectIndex]
						+ "; LastBlockIndex = " + lastBlockIndex[objectIndex] + "; NumRows = "
						+ nRows[objectIndex] + "; NumCols = " + nCols[objectIndex]);
				ininin.close();
				inputFile.close();
				
				totalRows += nRows[objectIndex];
			}
		} catch (java.io.IOException IOE) {
			System.out.println("IOException reading header files");
		}

	    int BufferSize = (int)(((long)RawBufferSize/nCols[0] + 1) * nCols[0]);

		long nElements = totalRows * nCols[0];
		int nOutputBlocks = -3;
		if (nElements % BufferSize == 0) {
			nOutputBlocks = (int)(nElements / BufferSize);
		} else {
			nOutputBlocks = (int)(nElements / BufferSize + 1) - 1;
		}
		
		System.out.println("The total number of rows is a staggering ["
				+ totalRows + "] and nCols is [" + nCols[0] + "] requiring "
				+ nOutputBlocks + " blocks of " + BufferSize);
		
		long elementCounter = -222;
		long pseudoCol = -333;
		long nBaseCols = nCols[0];
		double[] columnSums = new double[(int)nBaseCols];
		double[] columnSumSquares = new double [(int)nBaseCols];
		double valueToStore = -23.5;
		
		double[] outputBuffer = new double[BufferSize];
		double[] inputBuffer = null;
		double[] finalOutputBuffer = null;
		int nBlocks = -8;
		int nSpotsLeft = -3;
		int nToMove = -3;
		int firstUnmovedInputIndex = -34;
		int remainingToBeMoved = -444;
		boolean thereRemainUnwrittenInputs = true;
		boolean weHaveWrittenAnOutputBlock = true;
		
		int firstOpenSpotIndex = 0; // this actually needs to be zero
		int outputBlockIndex = 0; // this actually needs to be zero
		
		int finalBufferIndex = BufferSize - 1;
		
		try {
			// create the new output meta-data file
			FileOutputStream outputFile = new FileOutputStream(OutputName + ".bin");
			ObjectOutputStream out = new ObjectOutputStream(outputFile);
			out.writeInt(2);
			out.writeInt(BufferSize);
			out.writeInt(nOutputBlocks);
			out.writeInt((int)totalRows);
			out.writeInt((int)nCols[0]);
			out.flush();
			out.close();
			
			elementCounter = 0;
			
			for (int objectIndex = 0; objectIndex < nFull; objectIndex++) {
				
				for (int blockIndex = 0; blockIndex < lastBlockIndex[objectIndex]; blockIndex++) {
					firstUnmovedInputIndex = 0;
					
					// open up the file to read from
					String inputFileName = (String) allOfThem[objectIndex];
					
					System.out.println("     preparing to read [" + inputFileName + blockIndex + ".bin" + "]...");
					FileInputStream inputFile = new FileInputStream(inputFileName + blockIndex + ".bin");
					ObjectInputStream ininin = new ObjectInputStream(inputFile);
					
					// read in the inputbuffer
					inputBuffer = (double[]) ininin.readObject();
					
					// move some of it into the output buffer until the output
					// buffer is full
					// calculate the number of open spots in the outputBuffer
					nSpotsLeft = BufferSize - firstOpenSpotIndex;
					remainingToBeMoved = inputBuffer.length;
					if (remainingToBeMoved < nSpotsLeft) {
						nToMove = remainingToBeMoved;
						thereRemainUnwrittenInputs = false;
					} else {
						nToMove = nSpotsLeft;
						thereRemainUnwrittenInputs = true;
					}
					
					for (int moveIndex = 0; moveIndex < nToMove; moveIndex++) {
						valueToStore = inputBuffer[moveIndex + firstUnmovedInputIndex];
						elementCounter++;
						pseudoCol = elementCounter % nBaseCols; // what column
						// the element
						// originally
						// came from
						columnSums[(int)pseudoCol] += valueToStore;
						columnSumSquares[(int)pseudoCol] += valueToStore*valueToStore;
						
						outputBuffer[firstOpenSpotIndex + moveIndex] = valueToStore;
					}
					
					firstOpenSpotIndex += nToMove;
					firstUnmovedInputIndex += nToMove;
					if (firstOpenSpotIndex > finalBufferIndex) {
						// we have filled the output buffer and need to write it
						// out
						System.out.println("Writing File: " + OutputName + outputBlockIndex + ".bin ...");
						outputFile = new FileOutputStream(OutputName + outputBlockIndex + ".bin");
						out = new ObjectOutputStream(outputFile);
						out.writeObject(outputBuffer);
						out.flush();
						out.close();
						
						outputBlockIndex++;
						firstOpenSpotIndex = 0;
					}
					
					// now we need to reset all the thingees
					// this also means we have just written a block (i
					// think) so we fill in the outputBuffer and write it
					// out until the inputBuffer is empty
					while (thereRemainUnwrittenInputs) {
						// calculate the number of open spots in the
						// outputBuffer
						nSpotsLeft = BufferSize - firstOpenSpotIndex;
						remainingToBeMoved = inputBuffer.length - firstUnmovedInputIndex;
						if (remainingToBeMoved < nSpotsLeft) {
							nToMove = remainingToBeMoved;
							thereRemainUnwrittenInputs = false;
						} else {
							nToMove = nSpotsLeft;
							thereRemainUnwrittenInputs = true;
						}
						
						for (int moveIndex = 0; moveIndex < nToMove; moveIndex++) {
							valueToStore = inputBuffer[moveIndex + firstUnmovedInputIndex];
							elementCounter++;

							pseudoCol = elementCounter % nBaseCols; // what
							// column
							// the
							// element
							// originally
							// came from
							columnSums[(int)pseudoCol] += valueToStore;
							columnSumSquares[(int)pseudoCol] += valueToStore*valueToStore;
							
							outputBuffer[firstOpenSpotIndex + moveIndex] = valueToStore;
						}
						
						firstOpenSpotIndex += nToMove;
						firstUnmovedInputIndex += nToMove;
						if (firstOpenSpotIndex > finalBufferIndex) {
							// we have filled the output buffer and need to write it
							// out
							System.out.println("Writing File: " + OutputName + outputBlockIndex + ".bin ...");
							outputFile = new FileOutputStream(OutputName + outputBlockIndex + ".bin");
							out = new ObjectOutputStream(outputFile);
							out.writeObject(outputBuffer);
							out.flush();
							out.close();
							
							outputBlockIndex++;
							firstOpenSpotIndex = 0;
						}
					}
					// check if the buffer is empty or not...
					if (blockIndex == lastBlockIndex[objectIndex] - 1) {
						// need to read the final block...
						finalOutputBuffer = new double[firstOpenSpotIndex];
						for (int moveIndex = 0; moveIndex < firstOpenSpotIndex ; moveIndex++) {
							valueToStore = outputBuffer[moveIndex];
/*							elementCounter++;
System.out.println("in final: elementCounter = " + elementCounter);
							pseudoCol = elementCounter % nBaseCols; // what
							// column
							// the
							// element
							// originally
							// came from
							columnSums[(int)pseudoCol] += valueToStore;
							columnSumSquares[(int)pseudoCol] += valueToStore*valueToStore;
*/							
							finalOutputBuffer[moveIndex] = valueToStore;
						}
						System.out.println("Writing File: " + OutputName + outputBlockIndex + ".bin ...");
						outputFile = new FileOutputStream(OutputName + outputBlockIndex + ".bin");
						out = new ObjectOutputStream(outputFile);
						out.writeObject(finalOutputBuffer);
						out.flush();
						out.close();
					}
				}
			}
			
			// write out the sums data...
			outputFile = new FileOutputStream(OutputName + "ColumnSums" + ".bin");
			out = new ObjectOutputStream(outputFile);
			out.writeInt(2);
			out.writeInt((int)nBaseCols);
			out.writeInt(1);
			out.writeInt(1);
			out.writeInt((int)nBaseCols);
			out.flush();
			out.close();
			System.out.println("Writing File: " + OutputName + "ColumnSums0.bin ...");
			outputFile = new FileOutputStream(OutputName + "ColumnSums0.bin");
			out = new ObjectOutputStream(outputFile);
			out.writeObject(columnSums);
			out.flush();
			out.close();
			
			outputFile = new FileOutputStream(OutputName + "ColumnSumSquares" + ".bin");
			out = new ObjectOutputStream(outputFile);
			out.writeInt(2);
			out.writeInt((int)nBaseCols);
			out.writeInt(1);
			out.writeInt(1);
			out.writeInt((int)nBaseCols);
			out.flush();
			out.close();
			System.out.println("Writing File: " + OutputName + "ColumnSumSquares0.bin ...");
			outputFile = new FileOutputStream(OutputName + "ColumnSumSquares0.bin");
			out = new ObjectOutputStream(outputFile);
			out.writeObject(columnSumSquares);
			out.flush();
			out.close();
			
			System.out.println("elementCounter ended up at: " + elementCounter);
			
		} catch (IOException IOE) {
			System.out.println("IOException: we got some trouble reading and writing...");
		}
		
		//		this.pushOutput(X, 0);
		
	}
	
}