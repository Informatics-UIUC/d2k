package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class SumsToStandardizingValues extends ComputeModule {

	public String getModuleName() {
		return "SumsToStandardizingValues";
	}

	public String getModuleInfo() {
		return "This module will pull in two row matrices of the same dimension and " +
		"a set of indicator flags and use them to create a set of standardization " +
		"centering and scaling values that can be used with the StandardizeColumns " +
		"modules. <p> THERE IS NO IDIOT-PROOFING!!!";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "ColumnSums";
		case 1:
			return "ColumnSumSquares";
		case 2:
			return "IndicatorFlags";
		case 3:
			return "nRowsOriginalMatrix";
		case 4:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "ColumnSums";
		case 1:
			return "ColumnSumSquares";
		case 2:
			return "IndicatorFlags";
		case 3:
			return "nRowsOriginalMatrix";
		case 4:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "java.lang.Long",
				"java.lang.Long", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "CenteringValues";
		case 1:
			return "ScalingValues";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "CenteringValues";
		case 1:
			return "ScalingValues";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
		return types;
	}

	public void doit() throws Exception {
		MultiFormatMatrix ColumnSums  = (MultiFormatMatrix) this.pullInput(0);
		MultiFormatMatrix ColumnSumSquares  = (MultiFormatMatrix) this.pullInput(1);
		MultiFormatMatrix IndicatorFlags  = (MultiFormatMatrix) this.pullInput(2);
		long nRowsOriginal = ((Long) this.pullInput(3)).longValue();
		long NumberOfElementsThreshold = ((Long) this.pullInput(4)).longValue();
		
		double nRowsDouble = (double) nRowsOriginal;
		long nCols = ColumnSums.getDimensions()[1];

		double meanTemp = -234.5;

		// Beware the MAGIC NUMBER!!! the format is assumed to be in memory...
		MultiFormatMatrix CenteringValues = new MultiFormatMatrix(1,new long[] {1,nCols});
		MultiFormatMatrix ScalingValues = new MultiFormatMatrix(1,new long[] {1,nCols});
		
		for (long colIndex = 0; colIndex < nCols; colIndex++) {
			if (IndicatorFlags.getValue(0,colIndex) == 1) {
				CenteringValues.setValue(0,colIndex,0.0);
				ScalingValues.setValue(0,colIndex,1.0);
			} else {
				meanTemp = ColumnSums.getValue(0,colIndex) / nRowsDouble;
				CenteringValues.setValue(0,colIndex,meanTemp);
				ScalingValues.setValue(0,colIndex,
						Math.sqrt(ColumnSumSquares.getValue(0,colIndex)/nRowsDouble - meanTemp*meanTemp)
						);
			}
		}


		this.pushOutput(CenteringValues, 0);
		this.pushOutput(ScalingValues, 1);
	}
}