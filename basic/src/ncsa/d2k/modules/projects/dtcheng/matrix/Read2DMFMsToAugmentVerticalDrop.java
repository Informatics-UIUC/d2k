package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class Read2DMFMsToAugmentVerticalDrop extends InputModule {
	
	public String getModuleName() {
		return "Read2DMFMsToAugmentVerticalDrop";
	}
	
	public String getModuleInfo() {
		return "This module attempts to read a whole stack of 2-d MFMs from disk that were written by Write2DMFM "
		+ "and then stacks them up vertically. You should feed in filenames in the first contiguous block of inputs "
		+ " and nulls in the unused ones. This is to put together large blocks in a memory efficient manner. "
		+ "The files should have the suffix .bin . Write2DMFM write it out as a series of blocks "
		+ "and so only the base name should be given to this module... <p> THERE IS NO IDIOT-PROOFING!!! It "
		+ "also assumes that it is too big for memory, otherwise, why would you use this? <p> Additionally, " +
		"a keep list can be specified to keep only certain categories. The categories should be listed in " +
		"a text file with each category on a separate line with no extra spaces or lines. If no such dropping " +
		"is desired, then a null should be fed in. The final column of the matrices will be assumed to be the " +
		"category. Further, they will be renumbered from 0 to whatever in the same order they appear in the drop list. " +
		"This <i>also assumes the blocks were created intelligently with no rows split between blocks. This will " +
		"not always be true, but is for the stuff i am putting together. Eventually, everything should be changed " +
		"to this style. But alas. I think it will get all messed up if you don't have happy unsplit rows.</i>";
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
		case 12:
			return "KeepListName";
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
		case 12:
			return "KeepListName";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String[] getInputTypes() {
		String[] types = { "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.Integer",
				"java.lang.String", "java.lang.String", };
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
		String KeepListName = (String) this.pullInput(12);
		
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
		
		String lineContents = new String();
		double[] categoriesToKeep = null;
		int largeInt = 500000; // Beware the MAGIC NUMBER!!! for resetting the
		// keep list...
		int nToKeep = 0; // this actually needs to be zero
		// determine if we are dropping anything or not
		boolean weAreBeingSelective = false; // innocent until proven guilty
		if (KeepListName != null) {
			weAreBeingSelective = true;
			File keepListFile = new File(KeepListName);
			if (!keepListFile.exists()) {
				throw new Exception("File [" + keepListFile + "] does not exist!");
			}
			BufferedReader keepListReader = new BufferedReader(
					new FileReader(keepListFile));
			keepListReader.mark(largeInt);
			// figure out how many categories there are...
			while (true) {
				lineContents = keepListReader.readLine();
				if (lineContents != null) {
					nToKeep++;
				} else {
					break;
				}
			}
			keepListReader.reset();
			// now actually record them
			categoriesToKeep = new double[nToKeep];
			for (int lineIndex = 0; lineIndex < nToKeep; lineIndex++) {
				lineContents = keepListReader.readLine();
				categoriesToKeep[lineIndex] = Double.parseDouble(lineContents);
			}
			keepListReader.close();
		}
		
		int nDimensions = -1;
		int bufferSize[] = new int[nFull];
		long nRows[] = new long[nFull];
		long nCols[] = new long[nFull];
		int lastBlockIndex[] = new int[nFull];
		
		
		// first, we will read in all the header files
		
		int myFiguredLastBlock = -5;
		
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
				//					System.out.println("bumping up for object index " +
				// objectIndex);
				lastBlockIndex[objectIndex]++;
			} else {
				//					System.out.println("NOT bumping up for object index " +
				// objectIndex);
			}
			if (bufferSize[objectIndex] < nCols[objectIndex]) {
				System.out.println("remind me why you're using this? your input buffersize is pitiful...");
				throw new Exception("yo, get a bigger buffersize; specifically, larger than your col size for file [" + objectIndex + "]");
			}
			
			System.out.println("nDimensions = " + nDimensions + "; BufferSize = " + bufferSize[objectIndex]
																							   + "; LastBlockIndex = " + lastBlockIndex[objectIndex] + "; NumRows = "
																							   + nRows[objectIndex] + "; NumCols = " + nCols[objectIndex]);
			ininin.close();
			inputFile.close();
			
		}
		int nColsToUse = (int)nCols[0];
		
		
		double valueToStore = -23.5;
		
		int bufferSizeToUse = (BufferSize/nColsToUse + 1) * nColsToUse;
		System.out.println("output buffersize = " + bufferSizeToUse);
		
		double[] singleRowTemp = new double[nColsToUse];
		
		double[] outputBuffer = new double[bufferSizeToUse];
		double[] inputBuffer = null;
		double[] finalOutputBuffer = null;
		
		
		int firstOpenSpotIndex = 0; // this actually needs to be zero
		int outputBlockIndex = 0; // this actually needs to be zero
		
		int lastBufferIndex = bufferSizeToUse - 1;
		
		FileOutputStream outputFile = null;
		ObjectOutputStream out = null;
		
		
		int currentInputStartIndex = 0;
		double candidateCategory = -12.5;
		double newCategory = -21.53;
		boolean keepThisRow = false;
		int nRowsThisInput = -532;
		int rowsReadCounter = -55555;
		
		int firstEmptyOutputIndex = 0; // this actually needs to be zero
		int blockCounter = 0; // this actually needs to be zero
		int rowCounter = 0; // this actually needs to be zero
		int nDropped = 0; // this actually needs to be zero
		
		for (int objectIndex = 0; objectIndex < nFull; objectIndex++) {
			
			rowsReadCounter = 0; // this is to make sure we don't read the bad parts of final buffers...
			
			for (int blockIndex = 0; blockIndex < lastBlockIndex[objectIndex]; blockIndex++) {
				currentInputStartIndex = 0;
				
				// open up the file to read from
				String inputFileName = (String) allOfThem[objectIndex];
				
				System.out.println("     preparing to read [" + inputFileName + blockIndex + ".bin" + "]...");
				FileInputStream inputFile = new FileInputStream(inputFileName + blockIndex + ".bin");
				ObjectInputStream ininin = new ObjectInputStream(inputFile);
				
				// read in the inputbuffer
				inputBuffer = (double[]) ininin.readObject();
				nRowsThisInput = inputBuffer.length/nColsToUse;
				// now we step through it nColsToUse at a time...
				for (int inputRow = 0; inputRow < nRowsThisInput; inputRow++) {
					// if we are trying to read the bad part of a final buffer, just break out...
/*					System.out.println("the inputRow is " + inputRow);
*/
					if (rowsReadCounter == (int)nRows[objectIndex]) {
						break;
					}
					// look at the category associated with this row and decide if we want it
					candidateCategory = inputBuffer[currentInputStartIndex + nColsToUse - 1];
//					System.out.println("  candidateCategory = " + candidateCategory);
					if (weAreBeingSelective) {
						keepThisRow = false; // guilty until proven innocent
						for (int keepIndex = 0; keepIndex < nToKeep; keepIndex++) {
							if (categoriesToKeep[keepIndex] == candidateCategory) {
								keepThisRow = true;
								newCategory = (double)keepIndex;
								break;
							}
						}
					} else {
						keepThisRow = true;
						newCategory = candidateCategory;
					}
					// if we keep it, store it, otherwise just bump up the counter...
					if (keepThisRow) {
						// do all but the category
						for (int toOutputIndex = 0; toOutputIndex < nColsToUse - 1; toOutputIndex++) {
							outputBuffer[firstEmptyOutputIndex + toOutputIndex] =
								inputBuffer[currentInputStartIndex + toOutputIndex];
/*							System.out.println("    moved value = " + inputBuffer[currentInputStartIndex + toOutputIndex] + 
									" to [" + (firstEmptyOutputIndex + toOutputIndex) + "] from [" +
									(currentInputStartIndex + toOutputIndex) + "]");
*/
							}
						// now do the category
						outputBuffer[firstEmptyOutputIndex + nColsToUse - 1] = newCategory;
/*						System.out.println("    moved value = " + newCategory + " to [" +
								(firstEmptyOutputIndex + nColsToUse - 1) + "] (final column)");
*/						
						firstEmptyOutputIndex += nColsToUse;
						currentInputStartIndex += nColsToUse;
						rowCounter++;
/*						System.out.println("firstEmptyOutputIndex [" + firstEmptyOutputIndex + "], currentInputStartIndex [" +
								currentInputStartIndex + "], rowCounter [" + rowCounter + "], blockCounter [" + blockCounter + "]");
*/						
						// check to see if the output buffer is full
						if (firstEmptyOutputIndex > lastBufferIndex) {
/*							System.out.println("about to write... firstEmptyOutputIndex = " + firstEmptyOutputIndex +
									", bufferSizeToUse = " + bufferSizeToUse);
*/
							// we need to move this to a file
							System.out.println("internal Writing File: " + OutputName + blockCounter + ".bin ...");
							FileOutputStream file = new FileOutputStream(OutputName + blockCounter + ".bin");
							out = new ObjectOutputStream(file);
							out.writeObject(outputBuffer);
							out.flush();
							out.close();
							// tend to the counters
							blockCounter++;
							firstEmptyOutputIndex = 0;
						}
					} else {
						currentInputStartIndex += nColsToUse;
						nDropped++;
/*						System.out.println("Dropping; firstEmptyOutputIndex [" + firstEmptyOutputIndex + "], currentInputStartIndex [" +
								currentInputStartIndex + "], rowCounter [" + rowCounter + "], blockCounter [" + blockCounter + "]");
*/
						}
					rowsReadCounter++;
/*					System.out.println("finished reading row " + rowsReadCounter + " from objectIndex " + objectIndex);
*/
				}
			}
		}
		
		// now we are done but there might be something left in the buffer...
		if (firstEmptyOutputIndex > 0) {
			// there is something there, we need to write it to file
			/*						finalBuffer = new double[firstOpenSpot];
			 for (int moveIndex = 0; moveIndex < firstOpenSpot; moveIndex++) {
			 finalBuffer[moveIndex] = outputBuffer[moveIndex];
			 }
			 */						// we need to move this to a file
			System.out.println("There are " + firstEmptyOutputIndex + " good elements left in the output buffer.");
			System.out.println("final Writing File: " + OutputName + blockCounter + ".bin ...");
			FileOutputStream file = new FileOutputStream(OutputName + blockCounter + ".bin");
			out = new ObjectOutputStream(file);
			out.writeObject(outputBuffer);
			out.flush();
			out.close();
		}
		
		// create the new output meta-data file
		outputFile = new FileOutputStream(OutputName + ".bin");
		out = new ObjectOutputStream(outputFile);
		out.writeInt(2);
		out.writeInt(bufferSizeToUse);
		out.writeInt(blockCounter);
		out.writeInt(rowCounter);
		out.writeInt((int)nCols[0]);
		out.flush();
		out.close();
		
		System.out.println("A total of [" + rowCounter + "] rows were kept and [" + nDropped + "] were dropped.");
		//		this.pushOutput(X, 0);
		
	}
	
}