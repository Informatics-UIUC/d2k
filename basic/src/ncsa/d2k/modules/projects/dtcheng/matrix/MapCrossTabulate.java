package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
import java.io.*;

public class MapCrossTabulate extends ComputeModule {
	
	public String getModuleName() {
		return "MapCrossTabulate";
	}
	
	
	public String getModuleInfo() {
		return "This module operates on two column MFM's which are considered to be " +
		"categories and should contain only integers. This is for comparing two maps " +
		"and is a special module that meets my dissertation needs but not necessarily other needs." +
		"<p>THERE IS NO IDIOT-PROOFING!!!";
	}
	
	
	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "PredictedMap";
		case 1:
			return "ActualMap";
		case 2:
			return "OutputFileName";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	
	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "PredictedMap";
		case 1:
			return "ActualMap";
		case 2:
			return "OutputFileName";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	
	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.String",
		};
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

		System.out.println("REGION AND MODEL NUMBERS AND INDICES MUST BE SINGLE DIGITS...");
		
		MultiFormatMatrix PredictedMap = (MultiFormatMatrix)this.pullInput(0);
		MultiFormatMatrix ActualMap = (MultiFormatMatrix)this.pullInput(1);
		String OutputFileName = (String)this.pullInput(2);
		
		// pull out the region number and the model number... oh my goodness, is this magicized...
		
		// the filename should be of the form: "Xtab_" + RegionNumber + "_" + modelIndex + ".tex"
		int regionPlaceInName = OutputFileName.indexOf("Xtab_");
//		int modelPlaceInName = OutputFileName.indexOf("_", regionPlaceInName + 1);
		String regionString = OutputFileName.substring(regionPlaceInName + 5,
				regionPlaceInName + 6);
		String modelString = OutputFileName.substring(regionPlaceInName + 7,
				regionPlaceInName + 8);
/*		System.out.println("regionString = [" + regionString + "], modelString = [" + modelString + "]");
*/
		int regionNumber = Integer.parseInt(regionString);
		int modelIndex = Integer.parseInt(modelString);

		String regionWord = new String();
		String modelWord = new String();
		String modelLongWords = new String();
		if (regionNumber == 1) {
			regionWord = new String("Region One");
		} else if (regionNumber == 2) {
			regionWord = new String("Region Two");
		} else if (regionNumber == 3) {
			regionWord = new String("Region Three");
		} else if (regionNumber == 4) {
			regionWord = new String("Region Four");
		}
		
		if (modelIndex == 0) {
			modelWord = new String("MNL2");
			modelLongWords = new String("MNL with 2");
		} else if (modelIndex == 1) {
			modelWord = new String("NN2");
			modelLongWords = new String("NN with 2");
		} else if (modelIndex == 2) {
			modelWord = new String("MNL4");
			modelLongWords = new String("MNL with 4");
		} else if (modelIndex == 3) {
			modelWord = new String("NN4");
			modelLongWords = new String("NN with 4");
		} else {
			modelWord = new String("Other");
			modelLongWords = new String("PUT_MODEL_NAME HERE with NUMBER_OF_CATEGORIES_HERE");
		}

		
		
		System.out.println("\\end{tabular}");
		System.out.println("\\caption[Cross tabulation: " + regionWord + ", " + modelWord + "]");
		System.out.println("{Cross tabulation: " + regionWord + ", " + modelWord +
				": A cross tabulation of the results for " + regionWord +
				" using a " + modelLongWords + " categories.}");
		System.out.println("\\label{table:CrossTab_" + regionNumber + "_" + modelIndex + "} ");
		System.out.println("\\end{table}");

		
		
		
		
/*		System.out.println("we are dealing with filename = [" + OutputFileName +
				"]\n\tthe conclusion is that the region number is [" + regionNumber +
				"] and the model is [" + modelIndex + "]");
*/		
		
		long rowSizePredicted = PredictedMap.getDimensions()[0];
		long colSizePredicted = PredictedMap.getDimensions()[1];
		
		long rowSizeActual = ActualMap.getDimensions()[0];
		long colSizeActual = ActualMap.getDimensions()[1];
		
		if (colSizePredicted != 1 || colSizeActual != 1) {
			System.out.println("(colSizePredicted [" + colSizePredicted +
					"] != 1 || colSizeActual [" + colSizeActual + "] != 1)");
			throw new Exception("(colSizePredicted [" + colSizePredicted +
					"] != 1 || colSizeActual [" + colSizeActual + "] != 1)");
		}
		if (rowSizePredicted != rowSizeActual) {
			System.out.println("(rowSizePredicted [" + rowSizePredicted +
					"] != rowSizeActual [" + rowSizeActual + "])");
			throw new Exception("(rowSizePredicted [" + rowSizePredicted +
					"] != rowSizeActual [" + rowSizeActual + "])");
		}
		
		System.out.println("Warning: Everything is hardcoded in here, so watch out...");
		
		
/*
 * 		ok, so first we'll need a translation table that says which categories correspond to
 * 		which. the predicted maps will always come numbered 0-nModelled whereas the actual
 * 		will just be a mess. so i'm gonna have an array where the index is PREDICTION CATEGORY
 * 		and the value is the ACTUAL CATEGORY
 * 
 * 		if you want to make this general, pass this in as an input. right now, i'm lazy.
*/		
		
		
		int[] transTable = { 	2,
								3,
								7,
								8};

		int nModelled = transTable.length;
		String[] tableActualLabels = new String[nModelled + 3];
		tableActualLabels[0] = "Forest";
		tableActualLabels[1] = "Shrub/Scrub";
		tableActualLabels[2] = "General Ag.";
		tableActualLabels[3] = "Rice";

/*		for (int labelIndex = 0; labelIndex < nModelled; labelIndex++) {
			tableActualLabels[labelIndex] = new String(Integer.toString(transTable[labelIndex]));
		}
*/		
		tableActualLabels[nModelled] = new String("all others");
		tableActualLabels[nModelled + 1] = new String("missing");
		tableActualLabels[nModelled + 2] = new String("total");
		
		// Beware the MAGIC NUMBER!!! an alternate missing value indicator: zero for my real maps
		int altMissingValue = 0; // this really needs to be zero
		
		// create an empty cross tab table with "all others," "missing values," and "total" rows and "missing value" and "total" columns
		long[][] crossTabTable = new long[nModelled + 3][nModelled + 2]; // Beware the MAGIC NUMBER!!! the number of categories etc...
		
		double thePredictedCatRaw = -3;
		int thePredictedCat = -7;
		double theActualCatRaw = -4;
		int theActualCatTemp = -8;
		int theActualCat = -5;
		int notFoundValue = Integer.MIN_VALUE;
		int missingValueEquivalent = Integer.MAX_VALUE;
		
		for (long exampleIndex = 0; exampleIndex < rowSizePredicted; exampleIndex++) {
			thePredictedCatRaw = PredictedMap.getValue(exampleIndex,0);
			if (Double.isNaN(thePredictedCatRaw)) {
				thePredictedCat = missingValueEquivalent;
			} else {
				thePredictedCat = (int)thePredictedCatRaw;
			}
			theActualCatRaw = ActualMap.getValue(exampleIndex,0);
			if (Double.isNaN(theActualCatRaw)) {
				theActualCatTemp = missingValueEquivalent;
			} else if (theActualCatRaw == altMissingValue) { 
				theActualCatTemp = missingValueEquivalent;
			} else {
				theActualCatTemp = (int)theActualCatRaw;
			}

			// recode for missing values
			if (thePredictedCat == missingValueEquivalent) {
				thePredictedCat = nModelled;				
			}

			// invert the raw category to the same scheme as the predicted stuff
			theActualCat = notFoundValue;
			if (theActualCatTemp == missingValueEquivalent) {
				theActualCat = nModelled + 1;
			} else {
				for (int transIndex = 0; transIndex < nModelled; transIndex++) {
					if (theActualCatTemp == transTable[transIndex]) {
						theActualCat = transIndex;
						break;
					}
				}
			}
			
			if (theActualCat == notFoundValue) {
				theActualCat = nModelled;
			}
			
/*			System.out.println("PRaw = " + thePredictedCatRaw + "; PCat = " + thePredictedCat +
					"; ARaw = " + theActualCatRaw + "; ACatTemp = " + theActualCatTemp +
					"; ACat = " + theActualCat);
*/
			
			// up the counters

			// the cross tab part
			crossTabTable[theActualCat][thePredictedCat]++;
			// the totals
			crossTabTable[theActualCat][nModelled + 1]++;
			crossTabTable[nModelled + 2][thePredictedCat]++;
			// the total number of goodies
			crossTabTable[nModelled + 2][nModelled + 1]++;
			
		}
		
		// prepare the output
		
		File outputFileToWrite = new File(OutputFileName);
		FileWriter outfileWriter = new FileWriter(outputFileToWrite);
		PrintWriter outoutout = new PrintWriter(outfileWriter);

		System.out.println("Generating the EXTREMELY MAGICAL LaTeX output...");
		// first the headers:
		outoutout.println("\\begin{table}");
		// the name column for the row names
		outoutout.print("\\begin{tabular}{|r");
		// the categories proper
		for (int colIndex = 0; colIndex < nModelled; colIndex++) {
			outoutout.print("|r");
		}
		// the missing and total columns and close it out...
		outoutout.print("|r|r|} \n");
		outoutout.println("\\hline");

		
		// now we put in those labels...
		// the name column for the row names
		outoutout.print("real\\modelled & ");
		// the categories proper
		for (int colIndex = 0; colIndex < nModelled; colIndex++) {
			outoutout.print(tableActualLabels[colIndex] + " & ");
		}
		// the missing and total columns and close it out...
		outoutout.print(tableActualLabels[nModelled + 1] + " & " +
				tableActualLabels[nModelled + 2] + " \\\\ \\hline \n");

		// now for the actual data....
		for (int rowIndex = 0; rowIndex < crossTabTable.length; rowIndex++) {
			outoutout.print( tableActualLabels[rowIndex] + " & ");
			for (int colIndex = 0; colIndex < crossTabTable[0].length; colIndex++) {
				if (colIndex < crossTabTable[0].length - 1) {
					outoutout.print(crossTabTable[rowIndex][colIndex] + "  & ");
				} else {
					outoutout.print(crossTabTable[rowIndex][colIndex] + " \\\\ \n \\hline \n");
				}
			}
		}
		
		// close out the table
		outoutout.println("\\end{tabular}");
		outoutout.println("\\caption[Cross tabulation: " + regionWord + ", " + modelWord + "]");
		outoutout.println("{A cross tabulation of the results for " + regionWord +
				" using a " + modelLongWords + " categories. Rows are the observed categories " +
				"and columns are those predicted by the model.}");
		outoutout.println("\\label{table:CrossTab_" + regionNumber + "_" + modelIndex + "} ");
		outoutout.println("\\end{table}");
		
		outoutout.flush();
		outoutout.close();
	}
	
}





