package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

//import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class StandardizeColumnsNoDropping extends ComputeModule {

	public String getModuleName() {
		return "StandardizeColumnsNoDropping";
	}

	public String getModuleInfo() {
		return "This only takes pre-determined centering and scaling values and applies "
				+ "them to a matrix. <p> I am getting rid of the indicator flags. Just make sure "
				+ "that the mean is 0 and the scaler is 1 for that column.";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "CenteringValuesIn";
		case 2:
			return "ScalingValuesIn";
		case 3:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "CenteringValuesIn";
		case 2:
			return "ScalingValuesIn";
		case 3:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Long", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "StandardizedMatrix";
		case 1:
			return "CenteringValues";
		case 2:
			return "ScalingValues";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "StandardizedMatrix: the matrix whose columns are now mean 0, "
					+ "variance 1.";
		case 1:
			return "CenteringValues: the values to subtract from the raw data; that "
					+ "is, the sample means of the columns.";
		case 2:
			return "ScalingValues: the values to scale the raw data by; that is, "
					+ "the sample standard deviations of the columns.";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
		return types;
	}

	public void doit() throws Exception {

		MultiFormatMatrix X = (MultiFormatMatrix) this.pullInput(0);
		MultiFormatMatrix CenteringValuesIn = (MultiFormatMatrix) this
				.pullInput(1);
		MultiFormatMatrix ScalingValuesIn = (MultiFormatMatrix) this
				.pullInput(2);
		long NumberOfElementsThreshold = ((Long) this.pullInput(3)).longValue();

		long nRowsX = X.getDimensions()[0];
		long nColsX = X.getDimensions()[1];

		// regardless, we rescale the data
		long nElementsBig = nRowsX * nColsX;
		int FormatIndexBig = -1; // initialize
		if (nElementsBig < NumberOfElementsThreshold) {
			FormatIndexBig = 1; // Beware the MAGIC NUMBER!!!
		} else {
			FormatIndexBig = 3; // Beware the MAGIC NUMBER!!!
		}

		double CenteringNumber = -6.0;
		double ScaleNumber = -7.0;

		double CurrentElement = -15.0;

		MultiFormatMatrix StandardizedMatrix = new MultiFormatMatrix(
				FormatIndexBig, new long[] { nRowsX, nColsX });

		for (long rowIndex = 0; rowIndex < nRowsX; rowIndex++) {
			for (long colIndex = 0; colIndex < nColsX; colIndex++) {
				CurrentElement = X.getValue(rowIndex, colIndex);
				// we center and rescale...
				StandardizedMatrix.setValue(rowIndex, colIndex,
						(CurrentElement - CenteringValuesIn.getValue(0,
								colIndex))
								/ ScalingValuesIn.getValue(0, colIndex));
			}
		}

		// then we kick it out
		this.pushOutput(StandardizedMatrix, 0);
		this.pushOutput(CenteringValuesIn, 1);
		this.pushOutput(ScalingValuesIn, 2);
	}

}