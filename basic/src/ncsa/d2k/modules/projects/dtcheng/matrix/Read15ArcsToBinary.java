package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class Read15ArcsToBinary extends InputModule {
	
	public String getModuleName() {
		return "Read15ArcsToBinary";
	}
	
	public String getModuleInfo() {
		return "This module attempts to read a bunch of GRASS outputed single column Arc ASCII format " +
		"maps and write them to disk in my cheesy binary format for quick reading into MFMs. I will " +
		"probably make a bunch of assumptions and skip the idiot proofing. Obviously, they should all " +
		"come from the same region with the same resolution. A flag will indicate whether pixels with " +
		"a missing value in any of the maps should be dropped. Furthermore, the variables will be " +
		"standardized as we go along. If you don't want this standardization, just feed in 0's for the " +
		"centering and 1's for the scaling values. Perhaps one day I will make is so that you can send " +
		"in a null and then it will skip that step but right now my time is precious and I want to keep " +
		"it simple so I can graduate. Anyways, connect up filenames for as many files as you want to put " +
		"together and nulls to the rest of them.<p>I'm gonna trust that you can find a way to remember " +
		"the meta-data for this stuff without me telling you it back... <p> The BufferSize will be changed " +
		"to be the next even multiple of the number of columns in the final matrix just to make things easier on " +
		"the writing to disk. I hope this ain't a big deal...";
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
			return "FileName";
		case 11:
			return "FileName";
		case 12:
			return "FileName";
		case 13:
			return "FileName";
		case 14:
			return "FileName";
		case 15:
			return "BufferSize";
		case 16:
			return "OutputFileName";
		case 17:
			return "DropMissingValuesFlag";
		case 18:
			return "CenteringValues";
		case 19:
			return "ScalingValues";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "FileName: the full name of the raster text file";
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
			return "FileName";
		case 11:
			return "FileName";
		case 12:
			return "FileName";
		case 13:
			return "FileName";
		case 14:
			return "FileName";
		case 15:
			return "BufferSize";
		case 16:
			return "OutputFileName";
		case 17:
			return "DropMissingValuesFlag";
		case 18:
			return "CenteringValues";
		case 19:
			return "ScalingValues";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String[] getInputTypes() {
		String[] types = { "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.Integer",
				"java.lang.String", "java.lang.Boolean", "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"};
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
		
		int nObjects = 15; // Beware the MAGIC NUMBER!!!
		Object[] allOfThem = new Object[nObjects];
		
		for (int objectIndex = 0; objectIndex < nObjects; objectIndex++) {
			allOfThem[objectIndex] = (String) this.pullInput(objectIndex);
		}
		int BufferSize = ((Integer)this.pullInput(15)).intValue();
		String OutputName = (String)this.pullInput(16);
		boolean DropMissingValuesFlag = ((Boolean)this.pullInput(17)).booleanValue();
		MultiFormatMatrix CenteringValues = (MultiFormatMatrix)this.pullInput(18);
		MultiFormatMatrix ScalingValues = (MultiFormatMatrix)this.pullInput(19);
		
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

		System.out.println("nFull = " + nFull);
		int nCols = nFull;
		
		String magicLogFile = new String("C:\\temp\\big_scratch\\errorlog.txt");

		// we will bump it up to the next even multiple of nCols
		int bufferSizeToUse = (BufferSize/nCols + 1) * nCols;
		int lastBufferIndex = bufferSizeToUse - 1;
		double[] outputBuffer = new double[bufferSizeToUse];
		double[] finalBuffer = null;
		double[] valuesTemp = new double[nCols];
		double[] scalingMultipliers = new double[nCols];
		for (long colIndex = 0; colIndex < nFull; colIndex++) {
			scalingMultipliers[(int)colIndex] = 1.0/ScalingValues.getValue(0,colIndex);
		}
		
		// first, we need to open up the text files and read the headers of the first one

		File[] fileArray = new File[nFull];
		FileReader[] fileReaderArray = new FileReader[nFull];
		BufferedReader[] mapReaderArray = new BufferedReader[nFull];
		
		double xllCorner;
		double yllCorner;
		double cellSize;
		double noDataValue;
		
		long nColsMap = -1;
		long nRowsMap = -2;
		long totalMapLines = -5; // this is how many lines it will take in the GRASS style output...
		
		String lineContents = new String();
		double valueToConsider = -124.8;
		int tempIndex = -2;
		boolean keepThisLine = true;
		int firstOpenSpot = -34;
		
		
		
		long lineCounter = 0; // this actually needs to be zero
		int blockCounter = 0; // this actually needs to be zero
		long nLinesKeptCounter = 0; // this actually needs to be zero
		// start up the file readers and stuff...
//			PrintWriter logFileOut = new PrintWriter(new FileWriter(magicLogFile));
			for (int mapIndex = 0; mapIndex < nFull; mapIndex++) {
				fileArray[mapIndex] = new File((String)allOfThem[mapIndex]);
				if (!fileArray[mapIndex].exists()) {
					System.out.println("The file [" + allOfThem[mapIndex] + "] does not exist...");
					throw new Exception("File not found... [" + allOfThem[mapIndex] + "]");
				}
				fileReaderArray[mapIndex] = new FileReader(fileArray[mapIndex]);
				mapReaderArray[mapIndex] = new BufferedReader(fileReaderArray[mapIndex]);
				System.out.println("The file [" + allOfThem[mapIndex] + "] exists and was setup. It is file index ["
						+ mapIndex + "]");
			}
			// read the header from just the first file...
			lineContents = mapReaderArray[0].readLine(); // should be ncols
			tempIndex = lineContents.lastIndexOf(" ");
			nColsMap = Long.parseLong(lineContents.substring(tempIndex + 1));
			lineContents = mapReaderArray[0].readLine(); // should be nrows
			tempIndex = lineContents.lastIndexOf(" ");
			nRowsMap = Long.parseLong(lineContents.substring(tempIndex + 1));
			lineContents = mapReaderArray[0].readLine(); // should be xllcorner, but i don't care
			lineContents = mapReaderArray[0].readLine(); // should be yllcorner, but i don't care
			lineContents = mapReaderArray[0].readLine(); // should be cell size, but i don't care
			lineContents = mapReaderArray[0].readLine(); // should be nrows
			tempIndex = lineContents.lastIndexOf(" ");
			noDataValue = Double.parseDouble(lineContents.substring(tempIndex + 1));
			
			System.out.println("nColsMap = " + nColsMap + "; nRowsMap = " + nRowsMap +
					"; noDataValue = " + noDataValue);

			totalMapLines = nRowsMap * nColsMap; // this is the total number of lines it will take GRASS to write everything out...
			
			// catch the other files up to this spot
			if (nFull > 0) {
				for (int mapIndex = 1; mapIndex < nFull; mapIndex++) {
					mapReaderArray[mapIndex].readLine();
					mapReaderArray[mapIndex].readLine();
					mapReaderArray[mapIndex].readLine();
					mapReaderArray[mapIndex].readLine();
					mapReaderArray[mapIndex].readLine();
					mapReaderArray[mapIndex].readLine();
				}
			}
			
			// now we need to read the goodies in...
			firstOpenSpot = 0; // start it out right concerning the state of the Buffer
			for (long lineIndex = 0; lineIndex < totalMapLines; lineIndex++) {
				// go through each file and pull out the elements
				keepThisLine = true; // innocent until proven guilty
				for (int mapIndex = 0; mapIndex < nFull; mapIndex++) {
					lineContents = mapReaderArray[mapIndex].readLine();
					valueToConsider = Double.parseDouble(lineContents);
/*					logFileOut.println("[" +
							lineContents + "] [" + valueToConsider + "] l " 
							+ lineIndex + " m " + mapIndex);
*/
					if (valueToConsider == noDataValue) {
						if (DropMissingValuesFlag) {
							keepThisLine = false;
						} else {
							valuesTemp[mapIndex] = Double.NaN;

						}
					} else {
						if (keepThisLine) {
						valuesTemp[mapIndex] = (valueToConsider
								- CenteringValues.getValue(0,mapIndex)) * scalingMultipliers[mapIndex];
/*						logFileOut.println("[" +
								lineContents + "], double value = [" + valuesTemp[mapIndex] + "] line = "
								+ lineIndex + "; map = " + mapIndex);
*/
						} else {
/*							logFileOut.println("skip "
									+ lineIndex);
*/
						}
					}
				}
				
				// now that line is completed, either put in the outputbuffer or skip it...
				if (keepThisLine) {
					for (int moveIndex = 0; moveIndex < nCols; moveIndex++) {
						outputBuffer[firstOpenSpot + moveIndex] = valuesTemp[moveIndex];
					}
					nLinesKeptCounter++;
					firstOpenSpot += nCols;
					if (firstOpenSpot > lastBufferIndex) {
						// we need to move this to a file
						System.out.println("Writing File: " + OutputName + blockCounter + ".bin ...");
			            FileOutputStream file = new FileOutputStream(OutputName + blockCounter + ".bin");
			            ObjectOutputStream out = new ObjectOutputStream(file);
			            out.writeObject(outputBuffer);
			            out.flush();
			            out.close();
			            // tend to the counters
			            blockCounter++;
			            firstOpenSpot = 0;
					}
				}
				lineCounter++;
			}
			
			// now we are done but there might be something left in the buffer...
			if (firstOpenSpot > 0) {
				// there is something there, we need to write it to file
				finalBuffer = new double[firstOpenSpot];
				for (int moveIndex = 0; moveIndex < firstOpenSpot; moveIndex++) {
					finalBuffer[moveIndex] = outputBuffer[moveIndex];
				}
				// we need to move this to a file
				System.out.println("Writing File: " + OutputName + blockCounter + ".bin ...");
	            FileOutputStream file = new FileOutputStream(OutputName + blockCounter + ".bin");
	            ObjectOutputStream out = new ObjectOutputStream(file);
//	            out.writeObject(outputBuffer);
	            out.writeObject(finalBuffer);
	            out.flush();
	            out.close();
			}
	
			// and now for the meta-data file
			System.out.println("Writing meta-data File: " + OutputName + ".bin ...");
            FileOutputStream file = new FileOutputStream(OutputName + ".bin");
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeInt(2);
            out.writeInt(bufferSizeToUse);
            out.writeInt(blockCounter);
            out.writeInt((int)nLinesKeptCounter);
            out.writeInt(nCols);
            out.flush();
            out.close();

            // close everything up
			for (int mapIndex = 0; mapIndex < nFull; mapIndex++) {
				mapReaderArray[mapIndex].close();
				fileReaderArray[mapIndex].close();
			}
            
            
	    
		System.out.println("Out of " + totalMapLines + ", " + nLinesKeptCounter
				+ " were kept meaning " + (totalMapLines - nLinesKeptCounter)
				+ " were dropped.");
		
		
		//		this.pushOutput(X, 0);
		
	}
	
}