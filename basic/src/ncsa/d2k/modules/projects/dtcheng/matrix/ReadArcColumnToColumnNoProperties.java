package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.io.FlatFile;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
//import java.util.*;
//import java.text.*;


public class ReadArcColumnToColumnNoProperties extends InputModule {
	
	//////////////////
	//  PROPERTIES  //
	//////////////////
	
	
	public String getModuleName() {
		return "ReadArcColumnToColumnNoProperties";
	}
	
	
	public String getModuleInfo() {
		return "ReadArcColumnToColumn. This module reads in the rastor to ASCII export " +
		"from ARC stuff and dumps it into a MultiFormatMatrix. Specifically, this " +
		"reads in stuff from the GRASS export which can write it out as a single column. The Arc thing " +
		"generally kicks out a big table of numbers arranged as if they were " +
		"in the rastor. For statistical type analyses, it is desirable to " +
		"have everything strung out as a column. This module reads it " +
		"directly into a column. I think that this will read across the " +
		"rows. That is, the first element in the resulting matrix will be " +
		"the upper-left-hand element; the second element/row will be from " +
		"the original first row, second column." +
		"<p> It has been lately changed in order to be clever enough to read " +
		"data written by GRASS version 5.3 which puts only a single space between " +
		"the metadata descriptions and their values. Arc lines up the values at column #15." +
		"<p> <i>This assumes the -1 option has been used in GRASS to create the data.</i> <p>" +
		"This has been re-written to have no property thingees...";
	}
	
	
	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "MultiFormatMatrix";
		case 1:
			return "MetaDataMatrix";
		}
		return "";
	}
	
	
	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "MultiFormatMatrix";
		case 1:
			return "MetaDataMatrix. This will contain the ncols, " +
			"nrows, xllcorner, yllcorner, cellsize, NoDataValue " +
			"information as a single column 2-D MFM, in that order.";
		}
		return "";
	}
	
	
	public String[] getOutputTypes() {
		String[] out = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
		};
		return out;
	}
	
	
	public String getInputName(int index) {
		switch (index) {
		case 0:
			return "FileName";
		case 1:
			return "NumberOfElementsThreshold";
		default:
			return "No such input!";
		}
	}
	
	
	public String getInputInfo(int index) {
		switch (index) {
		case 0:
			return "FileName";
		case 1:
			return "NumberOfElementsThreshold";
		default:
			return "No such input!";
		}
	}
	
	
	public String[] getInputTypes() {
		String[] types = {
				"java.lang.String",
				"java.lang.Long",
		};
		return types;
	}
	
	
	////////////
	//  MAIN  //
	////////////
	
	public void doit() throws Exception {
		
		String FileName = (String)this.pullInput(0);
		long NumberOfElementsThreshold = ((Long)this.pullInput(1)).longValue();
		
		File inputFile = new File(FileName);
		if (!inputFile.exists()) {
			System.out.println("um, the file [" + inputFile + "] has failed to exist!");
			throw new Exception("um, the file [" + inputFile + "] has failed to exist!");
		}
		
		BufferedReader inputReader = new BufferedReader(new FileReader(inputFile));
		
		int spaceIndex = -3;
		
		long ncols;
		long nrows;
		double xllcorner;
		double yllcorner;
		double cellsize;
		double NoDataValue;
		
		double value;
		int ArcOffset = 14;
		int GrassOffset = 0;
		System.out.println("ReadArcMatrixToColumn filename = " + FileName);
		GrassOffset = 7 - 1;
		
		String lineContents = new String();
		String checkString = new String();
		
/*		ncols 1003
		nrows 1005
		xllcorner 97.918750
		yllcorner 2.786528
		cellsize 0.000278
		NODATA_value -9999
*/
	
		lineContents = inputReader.readLine();
		spaceIndex = lineContents.indexOf(" ") + 1; // Beware the MAGIC NUMBER!!!
		ncols = Long.parseLong(lineContents.substring(spaceIndex));
		
		lineContents = inputReader.readLine();
		spaceIndex = lineContents.indexOf(" ") + 1; // Beware the MAGIC NUMBER!!!
		nrows = Long.parseLong(lineContents.substring(spaceIndex));
		
		lineContents = inputReader.readLine();
		spaceIndex = lineContents.indexOf(" ") + 1; // Beware the MAGIC NUMBER!!!
		xllcorner = Double.parseDouble(lineContents.substring(spaceIndex));
		
		lineContents = inputReader.readLine();
		spaceIndex = lineContents.indexOf(" ") + 1; // Beware the MAGIC NUMBER!!!
		yllcorner = Double.parseDouble(lineContents.substring(spaceIndex));
		
		lineContents = inputReader.readLine();
		spaceIndex = lineContents.indexOf(" ") + 1; // Beware the MAGIC NUMBER!!!
		cellsize = Double.parseDouble(lineContents.substring(spaceIndex));
		
		lineContents = inputReader.readLine();
		spaceIndex = lineContents.indexOf(" ") + 1; // Beware the MAGIC NUMBER!!!
		NoDataValue = Double.parseDouble(lineContents.substring(spaceIndex));

		
		// record the metadata in an MFM
		// Beware the MAGIC NUMBER!!! the format will be SDIM because it is itty-bitty...
		MultiFormatMatrix MetaDataMatrix = new MultiFormatMatrix(1, new long[] {6,1});
		MetaDataMatrix.setValue(0,0,(double)ncols);
		MetaDataMatrix.setValue(0,1,(double)nrows);
		MetaDataMatrix.setValue(0,2,xllcorner);
		MetaDataMatrix.setValue(0,3,yllcorner);
		MetaDataMatrix.setValue(0,4,cellsize);
		MetaDataMatrix.setValue(0,5,NoDataValue);
		
		long numExamples = nrows;
		long numColumns = ncols;
		
		long numElements = nrows * ncols;
		int FormatIndex = -1;
		if (numElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
			FormatIndex = 1; // Beware the MAGIC NUMBER!!!
		}
		else { // not small means big, so go out of core; serialized blocks on disk are best
			FormatIndex = 3; // Beware the MAGIC NUMBER!!!
		}
		
		long[] dimensions = {numElements, 1};
		MultiFormatMatrix exampleSet = new MultiFormatMatrix(FormatIndex, dimensions);
		
		
		//    long StorageNumber = 0;
		for (int lineIndex = 0; lineIndex < numElements; lineIndex++) {
			
			lineContents = inputReader.readLine();
			
			value = Double.parseDouble(lineContents);
			if (value == NoDataValue) {
				value = java.lang.Double.NaN;
			}
			exampleSet.setValue(lineIndex, 0, value);
			
		}
		inputReader.close();
		
		this.pushOutput((MultiFormatMatrix) exampleSet, 0);
		this.pushOutput((MultiFormatMatrix) MetaDataMatrix, 1);
		
	}
	
}
