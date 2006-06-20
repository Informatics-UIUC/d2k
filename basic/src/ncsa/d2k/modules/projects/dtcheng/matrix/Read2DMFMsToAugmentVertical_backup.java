package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class Read2DMFMsToAugmentVertical_backup extends InputModule {

	public String getModuleName() {
		return "Read2DMFMsToAugmentVertical_backup";
	}

	public String getModuleInfo() {
		return "This module attempts to read a whole stack of 2-d MFMs from disk that were written by Write2DMFM "
				+ "and then stacks them up vertically. You should feed in filenames in the first contiguous block of inputs "
				+ " and nulls in the unused ones. This is to put together large blocks in a memory efficient manner. "
				+ "The files should have the suffix .bin . Write2DMFM write it out as a series of blocks "
				+ "and so only the base name should be given to this module... <p> THERE IS NO IDIOT-PROOFING!!! It "
				+ "also assumes that it is too big for memory, otherwise, why would you use this?";
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
		int BufferSize = ((Integer) this.pullInput(10)).intValue();
		String OutputName = (String) this.pullInput(11);
//		long NumberOfElementsThreshold = ((Long) this.pullInput(12))
//				.longValue();

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

//		String inputFileName = new String();
//		String outputFileName = new String();


		// first, we will read in all the header files and figure out the total number of rows we need

		int myFiguredLastBlock = -5;
		
		long totalRows = 0; // this actually needs to be zero...
		try {
			for (int objectIndex = 0; objectIndex < nFull; objectIndex++) {
				String inputFileName = (String) allOfThem[objectIndex];

				System.out.println("trying to create file object...");
				FileInputStream inputFile = new FileInputStream(inputFileName + ".bin");
				System.out.println("created file input stream [" + inputFile + "]");
				ObjectInputStream ininin = new ObjectInputStream(inputFile);
				System.out.println("created object input stream [" + ininin
						+ "]");
				nDimensions = ininin.readInt();
				System.out.println("here is the number of dimensions ["
						+ nDimensions + "]");
				bufferSize[objectIndex] = ininin.readInt();
				lastBlockIndex[objectIndex] = ininin.readInt();
				nRows[objectIndex] = (long) ininin.readInt();
				nCols[objectIndex] = (long) ininin.readInt();
			
				if (nRows[objectIndex]*nCols[objectIndex] % bufferSize[objectIndex] == 0) {
					myFiguredLastBlock = (int)(nRows[objectIndex]*nCols[objectIndex] / bufferSize[objectIndex] - 1);
				} else {
					myFiguredLastBlock = (int)(nRows[objectIndex]*nCols[objectIndex] / bufferSize[objectIndex] + 1) - 1;
				}
//				System.out.println("object index " + objectIndex + "; myFiguredLastBlock = " + myFiguredLastBlock);
				
				if (myFiguredLastBlock == lastBlockIndex[objectIndex]) {
//					System.out.println("bumping up for object index " + objectIndex);
					lastBlockIndex[objectIndex]++;
				} else {
//					System.out.println("NOT bumping up for object index " + objectIndex);
				}

				System.out.println("nDimensions = " + nDimensions
						+ "; BufferSize = " + bufferSize[objectIndex]
						+ "; LastBlockIndex = " + lastBlockIndex[objectIndex]
						+ "; NumRows = " + nRows[objectIndex] + "; NumCols = " + nCols[objectIndex]);
				ininin.close();
				System.out.println("closing the stream");
				inputFile.close();
				System.out.println("closing the file");

				totalRows += nRows[objectIndex];
			}

		} catch (java.io.IOException IOE) {
			System.out.println("IOException reading header files");
		}

		long nElements = totalRows * nCols[0];
		int nOutputBlocks = -3;
		if (nElements % BufferSize == 0) {
//			nOutputBlocks = (int)(nElements / BufferSize) - 1;
			nOutputBlocks = (int)(nElements / BufferSize);
		} else {
			nOutputBlocks = (int)(nElements / BufferSize + 1) - 1;
		}
		
//		nOutputBlocks = (int) (nElements / BufferSize + 1);
		System.out.println("The total number of rows is a staggering ["
				+ totalRows + "] and nCols is [" + nCols[0] + "] requiring "
				+ nOutputBlocks + " blocks of " + BufferSize);

		long elementCounter = -222;
		long pseudoCol = -333;
		double[] columnSums = new double[(int)nCols[0]];
		double[] columnSumSquares = new double [(int)nCols[0]];
		
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

//				firstUnmovedInputIndex = 0;
				
				for (int blockIndex = 0; blockIndex < lastBlockIndex[objectIndex]; blockIndex++) {
						firstUnmovedInputIndex = 0;

					// open up the file to read from
					String inputFileName = (String) allOfThem[objectIndex];
//					System.out.println("    objectIndex = " + objectIndex + "; blockIndex = " + blockIndex);

					System.out.println("     trying to create file object for [" + inputFileName + blockIndex + ".bin" + "]...");
					FileInputStream inputFile = new FileInputStream(inputFileName + blockIndex + ".bin");
//					System.out.println("        created file input stream [" + inputFile + "]");
					ObjectInputStream ininin = new ObjectInputStream(inputFile);
//					System.out.println("        created object input stream [" + ininin + "]");
					
					// read in the inputbuffer
					inputBuffer = (double[]) ininin.readObject();
//					System.out.println("        the inputBuffer is " + inputBuffer);
					
					// move some of it into the output buffer until the output buffer is full
					// calculate the number of open spots in the outputBuffer
					nSpotsLeft = BufferSize - firstOpenSpotIndex;
					remainingToBeMoved = inputBuffer.length;
					if (remainingToBeMoved < nSpotsLeft) {
//						System.out.println("objectIndex = " + objectIndex + "; blockIndex = " + blockIndex + "; we went top -> false");
						nToMove = remainingToBeMoved;
						thereRemainUnwrittenInputs = false;
					} else {
//						System.out.println("objectIndex = " + objectIndex + "; blockIndex = " + blockIndex + "; we went bot -> true");
						nToMove = nSpotsLeft;
						thereRemainUnwrittenInputs = true;
					}
					
//					System.out.println("objectIndex = " + objectIndex + "; blockIndex = " + blockIndex + "; nBlocks this = " + lastBlockIndex[objectIndex] +
//							"; BufferSize = " + BufferSize + "; firstOpenSpotIndex = " + firstOpenSpotIndex + "; nSpotsLeft = " + nSpotsLeft + "; nToMove = " +
//							nToMove + "; there remain = " + thereRemainUnwrittenInputs);
					
					for (int moveIndex = 0; moveIndex < nToMove; moveIndex++) {
//						System.out.println("  to move index [" + moveIndex + "] from [" + (moveIndex + firstUnmovedInputIndex) + "]");
						outputBuffer[firstOpenSpotIndex + moveIndex] = inputBuffer[moveIndex + firstUnmovedInputIndex];
//						System.out.println("  moved to out index [" + (firstOpenSpotIndex + moveIndex) + "] outputvalue = "
//								+ outputBuffer[moveIndex + firstUnmovedInputIndex]);
					}
					
					firstOpenSpotIndex += nToMove;
					firstUnmovedInputIndex += nToMove;
//					System.out.println("    the new firstOpenSpotIndex is " + firstOpenSpotIndex + " while the BufferSize is " + BufferSize +
//							" thereRemainUnwrittenInputs = " + thereRemainUnwrittenInputs);
					if (firstOpenSpotIndex > finalBufferIndex) {
						// write out what we had in the output buffer
/*						for (int bufInd = 0; bufInd < BufferSize; bufInd++) {
							System.out.println(outputBuffer[bufInd] + " [" + bufInd + "]");
						}
*/						
						// we have filled the output buffer and need to write it out
			            System.out.println("Writing File: " + OutputName + outputBlockIndex + ".bin ...");
			            outputFile = new FileOutputStream(OutputName + outputBlockIndex + ".bin");
			            out = new ObjectOutputStream(outputFile);
			            out.writeObject(outputBuffer);
			            out.flush();
			            out.close();
			            
			            outputBlockIndex++;
						firstOpenSpotIndex = 0;
					}
					
					// now we need to reset all the thingeed
						// this also means we have just written a block (i think) so we fill in the outputBuffer and write it out until the inputBuffer is empty
					while (thereRemainUnwrittenInputs) {
//						System.out.println("--> top of while loop: break = " + thereRemainUnwrittenInputs);
						// calculate the number of open spots in the outputBuffer
						nSpotsLeft = BufferSize - firstOpenSpotIndex;
						remainingToBeMoved = inputBuffer.length - firstUnmovedInputIndex;
						if (remainingToBeMoved < nSpotsLeft) {
							nToMove = remainingToBeMoved;
							thereRemainUnwrittenInputs = false;
						} else {
							nToMove = nSpotsLeft;
							thereRemainUnwrittenInputs = true;
						}
//						System.out.println("--> mid 1 of while loop: break = " + thereRemainUnwrittenInputs);
						
						for (int moveIndex = 0; moveIndex < nToMove; moveIndex++) {
							outputBuffer[firstOpenSpotIndex + moveIndex] = inputBuffer[moveIndex + firstUnmovedInputIndex];
						}

//						System.out.println("--> mid 2 of while loop: break = " + thereRemainUnwrittenInputs);

						firstOpenSpotIndex += nToMove;
						firstUnmovedInputIndex += nToMove;
						if (firstOpenSpotIndex > finalBufferIndex) {
/*							for (int bufInd = 0; bufInd < BufferSize; bufInd++) {
								System.out.println(outputBuffer[bufInd] + " [" + bufInd + "]");
							}
*/						// we have filled the output buffer and need to write it out
				            System.out.println("Writing File: " + OutputName + outputBlockIndex + ".bin ...");
				            outputFile = new FileOutputStream(OutputName + outputBlockIndex + ".bin");
				            out = new ObjectOutputStream(outputFile);
				            out.writeObject(outputBuffer);
				            out.flush();
				            out.close();
				            
				            outputBlockIndex++;
							firstOpenSpotIndex = 0;
						}
//						System.out.println("--> bottom of while loop: break = " + thereRemainUnwrittenInputs);

					}
					// check if the buffer is empty or not...
//					System.out.println("remaining to be moved = " + remainingToBeMoved + "; blockIndex = " + blockIndex + " of " + lastBlockIndex[objectIndex]);
					if (blockIndex == lastBlockIndex[objectIndex] - 1) {
						// need to read the final block...
						finalOutputBuffer = new double[firstOpenSpotIndex];
//						System.out.println("firstOpenSpotIndex = " + firstOpenSpotIndex + "; length of finalOutputBuffer = " + finalOutputBuffer.length);
						for (int moveIndex = 0; moveIndex < firstOpenSpotIndex ; moveIndex++) {
							finalOutputBuffer[moveIndex] = outputBuffer[moveIndex];
//							System.out.println("fOB[" + moveIndex + "] = " + finalOutputBuffer[moveIndex]);
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

		} catch (IOException IOE) {
			System.out.println("IOException: we got some trouble reading and writing...");
		}

//		this.pushOutput(X, 0);

	}

}