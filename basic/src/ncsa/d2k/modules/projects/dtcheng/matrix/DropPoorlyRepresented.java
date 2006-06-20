package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

//import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class DropPoorlyRepresented extends ComputeModule {

	public String getModuleName() {
		return "DropPoorlyRepresented";
	}

	public String getModuleInfo() {
		return "This module takes the output from DropMissingValues and "
				+ "drops the poorly represented categories. This requires yet another "
				+ "round of renumbering. Thus, two keys will be outputed: the first will "
				+ "be a Long array which will be of the format row = new category number; "
				+ "col 0 = original category; col 1 = category number from the key passed in. "
				+ "and an MFM of the format row = new category number, col 0 = original category.<p>"
				+ "The poorly-represented-ness will be based on the <i>totals for all datasets</i>, "
				+ "that is, the final column of the inputed key.";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "PrevCategoryNamesCounts";
		case 2:
			return "FractionalRepresentationThreshold";
		case 3:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "Matrix: a matrix of explanatory variables in the columns with the "
					+ "final column being a category number with no fractional part.";
		case 1:
			return "PrevCategoryNamesCounts: a Long array containing a list "
					+ "of categories in order of appearance and the number represented from "
					+ "a previous dataset. This will be expanded and outputed with the results "
					+ "from this matrix added on. If there is no previous key that should be used, "
					+ "then pass in a null.";
		case 2:
			return "FractionalRepresentationThreshold: the minimum fraction of the total dataset "
					+ "that should be in a category in order for it to be kept.";
		case 3:
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Long", "java.lang.Double", "java.lang.Long", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "DroppedMatrix";
		case 1:
			return "LongKey";
		case 2:
			return "MFMKey";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "DroppedMatrix: the matrix with the missing values dropped";
		case 1:
			return "LongKey: a Long array listing the retained categories "
					+ "Row = new category index (it will be from 0 up with no gaps), "
					+ "Col 0 = original category "
					+ "number, Col 1 = category index from key passed in.";
		case 2:
			return "MFMKey: a MultiFormatMatrix listing the retained categories where the "
					+ "row represents the new category index and the value in column 0 is the original "
					+ "category number.";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Long",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
		return types;
	}

	public void doit() throws Exception {

		MultiFormatMatrix X = (MultiFormatMatrix) this.pullInput(0);
		long[][] CategoryNameCounts = (long[][]) this.pullInput(1);
		double FracThresh = ((Double) this.pullInput(2)).doubleValue();
		long NumberOfElementsThreshold = ((Long) this.pullInput(3)).longValue();

		long rowSizeX = X.getDimensions()[0];
		long colSizeX = X.getDimensions()[1];
		long lastColX = colSizeX - 1;

		// figure out which categories we're gonna end up with
		int nCategoriesIn = CategoryNameCounts.length;
		int nKeptCategories = 0;
		long fewestAllowableExamples = (long) (FracThresh * rowSizeX);
		boolean[] keepCategory = new boolean[nCategoriesIn];
		for (int categoryIndex = 0; categoryIndex < nCategoriesIn; categoryIndex++) {
			if (CategoryNameCounts[categoryIndex][2] >= fewestAllowableExamples) {
				keepCategory[categoryIndex] = true;
				nKeptCategories++;
			} else {
				keepCategory[categoryIndex] = false;
			}
		}
		// make the new key
		long[][] LongKey = new long[nKeptCategories][2];
		MultiFormatMatrix MFMKey = new MultiFormatMatrix(1, new long[] {
				nKeptCategories, 1 });
		int storageNumber = 0; // this actually needs to be zero.
		for (int oldKeyIndex = 0; oldKeyIndex < nKeptCategories; oldKeyIndex++) {
			if (keepCategory[oldKeyIndex]) {
				LongKey[storageNumber][0] = CategoryNameCounts[oldKeyIndex][0];
				LongKey[storageNumber][1] = oldKeyIndex;

				MFMKey.setValue(storageNumber, 0,
						CategoryNameCounts[oldKeyIndex][0]);
				
				storageNumber++;
			}
		}

		long nKeptRows = 0;
		int thisCategory = -5;
		// now we must make a quick run through to figure out how many rows
		// we're keeping...
		for (int rowIndex = 0; rowIndex < rowSizeX; rowIndex++) {
			thisCategory = (int) X.getValue(rowIndex, lastColX);
			if (keepCategory[thisCategory]) {
				nKeptRows++;
			}
		}

		// now we can create the output matrix...
		long nElementsBig = nKeptRows * colSizeX;
		int FormatIndexBig = -2; // initialize
		if (nElementsBig < NumberOfElementsThreshold) {
			FormatIndexBig = 1; // Beware the MAGIC NUMBER!!!
		} else {
			FormatIndexBig = 3; // Beware the MAGIC NUMBER!!!
		}

		MultiFormatMatrix DroppedMatrix = new MultiFormatMatrix(FormatIndexBig,
				new long[] { nKeptRows, colSizeX });

//		double thisValue = -2.3;
		int theOldCategory = -52;
		double theNewCategory = -6.2;

		long storageNumberLong = 0;
		for (long rowIndex = 0; rowIndex < rowSizeX; rowIndex++) {
			theOldCategory = (int) X.getValue(rowIndex, lastColX);
			if (keepCategory[theOldCategory]) {
				// find the new category number
				for (int keyIndex = 0; keyIndex < nKeptCategories; keyIndex++) {
					if (LongKey[keyIndex][1] == theOldCategory) {
						theNewCategory = keyIndex;
						break;
					}
				}
				// write it down along with the others
				for (long colIndex = 0; colIndex < lastColX; colIndex++) {
					DroppedMatrix.setValue(storageNumberLong, colIndex, X
							.getValue(rowIndex, colIndex));
				}
				DroppedMatrix.setValue(storageNumberLong, lastColX,
						theNewCategory);
				storageNumberLong++;
			}
		}

		// then we kick it out
		this.pushOutput(DroppedMatrix, 0);
		this.pushOutput(LongKey, 1);
		this.pushOutput(MFMKey, 2);
	}

}