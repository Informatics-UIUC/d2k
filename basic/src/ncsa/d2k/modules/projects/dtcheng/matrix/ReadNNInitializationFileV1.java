package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
import java.util.Date;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class ReadNNInitializationFileV1 extends InputModule {

	public String getModuleName() {
		return "ReadNNInitializationFile";
	}

	public String getModuleInfo() {
		return "This module is a highly specific, probably non-reusable module "
				+ "whose purpose is to read an initialization file and then kick out all the "
				+ "goods in the proper format to run my big nasty distribuatable neural net solver. "
				+ "There will be a single text file containing all the control parameters and this "
				+ "will read and interpret that.<p>"
				+ "Each thing listed in the output should be on a separate line in the same order "
				+ "as the outputs are listed. The \"HiddenLayerTable\" should have its elements "
				+ "separated by a single space with no trailing spaces. They should be listed in the "
				+ "order as if it were the transpose of the desired hidden layer table.<p>"
				+ "THERE IS NO IDIOT-PROOFING!!!";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "InitFileName";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "InitFileName: the path and name of the file with the goods";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = { "java.lang.String", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "nExplanatoryVariables";
		case 1:
			return "HiddenLayerTable";
		case 2:
			return "nOptionsMinusOne";
		case 3:
			return "nElementsThreshold";
		case 4:
			return "FractionInTraining";
		case 5:
			return "LearningRate";
		case 6:
			return "WeightSeedBaseName";
		case 7:
			return "BiasSeedBaseName";
		case 8:
			return "nEpochs";
		case 9:
			return "CheckNumber";
		case 10:
			return "WeightBaseName";
		case 11:
			return "BiasBaseName";
		case 12:
			return "GradientBaseName";
		case 13:
			return "HessianBaseName";
		case 14:
			return "ReportBaseName";
		case 15:
			return "Delimiter";
		case 16:
			return "aRandomSeed";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "nExplanatoryVariables";
		case 1:
			return "HiddenLayerTable";
		case 2:
			return "nOptionsMinusOne";
		case 3:
			return "nElementsThreshold";
		case 4:
			return "FractionInTraining";
		case 5:
			return "LearningRate";
		case 6:
			return "WeightSeedBaseName";
		case 7:
			return "BiasSeedBaseName";
		case 8:
			return "nEpochs";
		case 9:
			return "CheckNumber";
		case 10:
			return "WeightBaseName";
		case 11:
			return "BiasBaseName";
		case 12:
			return "GradientBaseName";
		case 13:
			return "HessianBaseName";
		case 14:
			return "ReportBaseName";
		case 15:
			return "Delimiter";
		case 16:
			return "aRandomSeed";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "java.lang.Long",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Long", "java.lang.Long", "java.lang.Double",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "java.lang.String", "java.lang.String",
				"java.lang.Long", "java.lang.Long", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.Long"

		};
		return types;
	}

	public void doit() throws Exception {

    System.out.println("[" + this.getAlias() + "] starting everything up at " +
        new Date() + " or millisecond " + System.currentTimeMillis());
		String InitFileName = (String) this.pullInput(0);

		// create the "File"
		File InitFileObject = new File(InitFileName);
		// create a character input stream (FileReader inherits from InputStreamReader
		FileReader InitStream = new FileReader(InitFileObject);
		// filter the input stream, buffers characters for efficiency
		BufferedReader InitReader = new BufferedReader(InitStream);

		// read the first line
		String LineContents = InitReader.readLine();
		long nExplanatoryVariables = Long.parseLong(LineContents);

		LineContents = InitReader.readLine();
		int previousEndIndex = 0;
		int currentEndIndex = 0;
		int currentColumn = 0;
		double valueToStore = 0.0;
		char lookForDelimiter = 'z';
		char delimiterChar = ' '; // Beware the MAGIC NUMBER!!! or in this case, character...

		// reset everything going in...
		int nElementsInRow = 0;
		currentColumn = 0;
		previousEndIndex = -1;

		int lengthOfRow = LineContents.length();
		// run through to check how many elements there are
		for (int charIndex = 0; charIndex < lengthOfRow; charIndex++) {
			if (charIndex != lengthOfRow - 1) { // we are NOT at the end of the row
				lookForDelimiter = LineContents.charAt(charIndex);
				if (lookForDelimiter == delimiterChar) {
					nElementsInRow++;
				}
			} else {
				nElementsInRow++;
			}
		}

		// now we go through and actually write them down
		MultiFormatMatrix HiddenLayerTable = new MultiFormatMatrix(1,
				new long[] { nElementsInRow, 1 });
		// reset everything going in...
		currentColumn = 0;
		previousEndIndex = -1;

		// run through to check how many elements there are
		for (int charIndex = 0; charIndex < lengthOfRow; charIndex++) {
			if (charIndex == lengthOfRow - 1) { // we are at the end of the row
				currentEndIndex = charIndex;
				valueToStore = Double.parseDouble(LineContents.substring(
						previousEndIndex + 1, currentEndIndex + 1));
				HiddenLayerTable.setValue(currentColumn, 0, valueToStore);
			} else {
				lookForDelimiter = LineContents.charAt(charIndex);
//				System.out.print("The char " + charIndex + " is [" + lookForDelimiter + "] ");
				if (lookForDelimiter == delimiterChar) {
					currentEndIndex = charIndex;
/*					System.out.print("The substring to parse is [" + LineContents.substring(
							previousEndIndex + 1, currentEndIndex) + "] ; its double value is [" +
							Double.parseDouble(LineContents.substring(
									previousEndIndex + 1, currentEndIndex)) + "] " +
									"the column is: " + currentColumn + " ");
*/					
					valueToStore = Double.parseDouble(LineContents.substring(
							previousEndIndex + 1, currentEndIndex));
					HiddenLayerTable.setValue(currentColumn, 0, valueToStore);
					previousEndIndex = currentEndIndex;
					currentColumn++;
				}
			}
		}
		LineContents = InitReader.readLine();
		long nOptionsMinusOne = Long.parseLong(LineContents);

		LineContents = InitReader.readLine();
		long nElementsThreshold = Long.parseLong(LineContents);

		LineContents = InitReader.readLine();
		double FractionInTraining = Double.parseDouble(LineContents);

		LineContents = InitReader.readLine();
		double LearningRateTemp = Double.parseDouble(LineContents);
		MultiFormatMatrix LearningRate = new MultiFormatMatrix(1, new long[] {1,1});
		LearningRate.setValue(0,0,LearningRateTemp);

		String WeightSeedBaseName = InitReader.readLine();
		String BiasSeedBaseName = InitReader.readLine();
		
		LineContents = InitReader.readLine();
		long nEpochs = Long.parseLong(LineContents);

		LineContents = InitReader.readLine();
		long CheckNumber = Long.parseLong(LineContents);
		
		String WeightBaseName = InitReader.readLine();
		String BiasBaseName = InitReader.readLine();
		String GradientBaseName = InitReader.readLine();
		String HessianBaseName = InitReader.readLine();
		String ReportBaseName = InitReader.readLine();
		String Delimiter = InitReader.readLine();
		
		long aRandomSeed = Long.parseLong(InitReader.readLine());

		InitReader.close();
		InitStream.close();

		this.pushOutput(new Long(nExplanatoryVariables), 0);
		this.pushOutput(HiddenLayerTable, 1);
		this.pushOutput(new Long(nOptionsMinusOne), 2);
		this.pushOutput(new Long(nElementsThreshold), 3);
		this.pushOutput(new Double(FractionInTraining), 4);
		this.pushOutput(LearningRate, 5);
		this.pushOutput(new String(WeightSeedBaseName), 6);
		this.pushOutput(new String(BiasSeedBaseName), 7);
		this.pushOutput(new Long(nEpochs), 8);
		this.pushOutput(new Long(CheckNumber), 9);
		this.pushOutput(new String(WeightBaseName), 10);
		this.pushOutput(new String(BiasBaseName), 11);
		this.pushOutput(new String(GradientBaseName), 12);
		this.pushOutput(new String(HessianBaseName), 13);
		this.pushOutput(new String(ReportBaseName), 14);
		this.pushOutput(new String(Delimiter), 15);
		this.pushOutput(new Long(aRandomSeed),16);
	}
}