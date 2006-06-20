package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.core.datatype.table.*;
//import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;

//import ncsa.d2k.modules.core.prediction.*;

public class ConvertMatrixToMaxColumnIndices extends ComputeModule {

	public String getModuleInfo() {
		return "This returns the index of the column containing the maximum element of each row";

	}

	public String getModuleName() {
		return "ConvertMatrixToNominalVector";
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "IndicatorMatrix";
		case 1:
			return "NumberOfElementsThreshold";
		default:
			return "No such input";
		}
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "IndicatorMatrix";
		case 1:
			return "NumberOfElementsThreshold";
		default:
			return "No such input";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Long", };
		return types;
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "NominalVector";
		default:
			return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "NominalVector";
		default:
			return "No such output";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
		return types;
	}

	public void doit() throws Exception {

		MultiFormatMatrix InputMatrix = (MultiFormatMatrix) this.pullInput(0);
		long NumberOfElementsThreshold = ((Long) this.pullInput(1)).longValue();

		long NumRows = InputMatrix.getDimensions()[0];
		long NumCols = InputMatrix.getDimensions()[1];

		long NumElements = NumRows * NumCols;
		int FormatIndex = -1;
		if (NumElements < NumberOfElementsThreshold) { // small means keep it in
			// core; single
			// dimensional in memory
			// is best
			FormatIndex = 1; // Beware the MAGIC NUMBER!!!
		} else { // not small means big, so go out of core; serialized blocks on
			// disk are best
			FormatIndex = 3; // Beware the MAGIC NUMBER!!!
		}

		MultiFormatMatrix NominalVector = new MultiFormatMatrix(FormatIndex,
				new long[] { NumRows, 1 });

		double TempValue = -58.0;

		for (long RowIndex = 0; RowIndex < NumRows; RowIndex++) {
			double MaxValue = Double.NEGATIVE_INFINITY;
			long MaxValueColIndex = 0;

			for (long ColIndex = 0; ColIndex < NumCols; ColIndex++) {
				TempValue = InputMatrix.getValue(RowIndex, ColIndex);
				if (Double.isNaN(TempValue)) {
					NominalVector.setValue(RowIndex, 0, Double.NaN);
					break;
				} else if (TempValue > MaxValue) {
					MaxValue = TempValue;
					MaxValueColIndex = ColIndex;
				}
			}
			if (!Double.isNaN(TempValue)) {
				NominalVector.setValue(RowIndex, 0, MaxValueColIndex);
			}
		}

		this.pushOutput(NominalVector, 0);
	}
}