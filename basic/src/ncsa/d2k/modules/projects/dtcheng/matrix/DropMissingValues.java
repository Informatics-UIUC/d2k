package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

//import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class DropMissingValues extends ComputeModule {

	public String getModuleName() {
		return "DropMissingValues";
	}

	public String getModuleInfo() {
		return "This module goes row by row and drops the row if it "
				+ "contains a missing value. It also renumbers the final "
				+ "column which is considered to be a category and kicks out " +
				"a key for converting back to the original categories along with " +
				"the number of rows with that category. This allows the key to be " +
				"reused for numerous matrices with the same categories and still " +
				"end up with the same reclassified numbers. Furthermore, the counts " +
				"allow a subsequent matrix to sift through it again to drop poorly " +
				"represented categories without having to run entirely through the dataset.";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Matrix";
		case 1:
			return "PrevCategoryNamesCounts";
		case 2:
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
			return "NumberOfElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Long", "java.lang.Long", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "DroppedMatrix";
		case 1:
			return "CategoryNameCounts";
		case 2:
			return "nValid";
		case 3:
			return "nDropped";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "DroppedMatrix: the matrix with the missing values dropped";
		case 1:
			return "CategoryNameCounts: a Long array listing the categories "
					+ "in order of appearance and the number represented. Row = new category "
					+ "index (it will be from 0 up with no gaps), Col 0 = original category "
					+ "number, Col 1 = number present in this dataset, Col 2 = cumulative number "
					+ "present in this dataset and all others previously using this key.";
		case 2:
			return "nValid";
		case 3:
			return "nDropped";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Long", "java.lang.Long", "java.lang.Long", };
		return types;
	}

	public void doit() throws Exception {

		MultiFormatMatrix X = (MultiFormatMatrix) this.pullInput(0);
		long[][] ProvidedCategoryNameCounts = (long[][]) this.pullInput(1);
		long NumberOfElementsThreshold = ((Long) this.pullInput(2)).longValue();

		// set up the category key...
		boolean inputWasNull = true;
		long[][] CategoryNameCounts = new long[1][3];
		long[][] tempCategoryNameCounts = null;
		int currentNumberOfCategories = 1;
		if (ProvidedCategoryNameCounts != null) {
			inputWasNull = false;
			CategoryNameCounts = (long[][]) ProvidedCategoryNameCounts.clone();
			currentNumberOfCategories = CategoryNameCounts.length;
			// reset the "current dataset counts" to zeros
			for (int categoryIndex = 0; categoryIndex < currentNumberOfCategories; categoryIndex++) {
				CategoryNameCounts[categoryIndex][1] = 0;
			}
		}
		currentNumberOfCategories = CategoryNameCounts.length;

		long rowSizeX = X.getDimensions()[0];
		long colSizeX = X.getDimensions()[1];
		long lastColX = colSizeX - 1;


		// Beware the MAGIC NUMBER!!! format = 1
		boolean rowIsGood = true; // good unless proven otherwise...
		boolean weHaveSeenThisCategoryBefore = false;
		long thisCategory = -5;

		if (inputWasNull) {
			// this means the key is brand new
			// so, i should look up the first category and write it in...
			CategoryNameCounts[0][0] = (long) X.getValue(0,lastColX);
		}


		long nValid = 0;
		for (long rowIndex = 0; rowIndex < rowSizeX; rowIndex++) {
			rowIsGood = true;
			for (long colIndex = 0; colIndex < colSizeX; colIndex++) {
				if (java.lang.Double.isNaN(X.getValue(rowIndex, colIndex))) {
					rowIsGood = false;
					break;
				}
			}
			if (rowIsGood) {
				nValid++;
				// now i need to consult the key and/or update
				weHaveSeenThisCategoryBefore = false;
				thisCategory = (long) X.getValue(rowIndex,lastColX);
				for (int categoryIndex = 0; categoryIndex < currentNumberOfCategories; categoryIndex++) {
					if (CategoryNameCounts[categoryIndex][0] == thisCategory) {
						// up the counters;
						CategoryNameCounts[categoryIndex][1] ++;
						CategoryNameCounts[categoryIndex][2] ++;
						weHaveSeenThisCategoryBefore = true;
						break;
					}
				}
				if (!weHaveSeenThisCategoryBefore) {
					// we need to update the key
					tempCategoryNameCounts = CategoryNameCounts;
					CategoryNameCounts = new long[currentNumberOfCategories + 1][3];
					// copy the old stuff back in
					for (int keyRowIndex = 0; keyRowIndex < currentNumberOfCategories; keyRowIndex++) {
						for (int keyColIndex = 0; keyColIndex < 3; keyColIndex++) {
							CategoryNameCounts[keyRowIndex][keyColIndex] = tempCategoryNameCounts[keyRowIndex][keyColIndex];
						}
					}
					// put in an entry for the new one...
					CategoryNameCounts[currentNumberOfCategories][0] = thisCategory;
					CategoryNameCounts[currentNumberOfCategories][1] = 1;
					CategoryNameCounts[currentNumberOfCategories][2] = 1;
					// reset the number of categories to reflect the new reality
					currentNumberOfCategories = CategoryNameCounts.length;
				}
			}
		}

		long nDropped = rowSizeX - nValid;

		long nElementsBig = nValid * colSizeX;
		int FormatIndexBig = -2; // initialize
		if (nElementsBig < NumberOfElementsThreshold) {
			FormatIndexBig = 1; // Beware the MAGIC NUMBER!!!
		} else {
			FormatIndexBig = 3; // Beware the MAGIC NUMBER!!!
		}

		
		MultiFormatMatrix DroppedMatrix = new MultiFormatMatrix(FormatIndexBig,
				new long[] { nValid, colSizeX });

		double thisValue = -2.3;
		double theNewCategory = -5.2;
		
		long storageNumber = 0;
		for (long rowIndex = 0; rowIndex < rowSizeX; rowIndex++) {
			rowIsGood = true;
			for (long colIndex = 0; colIndex < colSizeX; colIndex++) {
				thisValue = X.getValue(rowIndex, colIndex);
				if (java.lang.Double.isNaN(thisValue)) {
					rowIsGood = false;
					break;
				} else {
					if (colIndex != lastColX) {
/*						System.out.println("rowIndex = " + rowIndex + "; colIndex = " + colIndex +
								"; storageNumber = " + storageNumber);
*/
						DroppedMatrix.setValue(storageNumber, colIndex, thisValue);
					} else {
						thisCategory = (long)thisValue;
						// look it up in the table;
						for (int keyRow = 0; keyRow < currentNumberOfCategories; keyRow++) {
							if (CategoryNameCounts[keyRow][0] == thisCategory) {
								theNewCategory = keyRow;
								break;
							}
						}
						DroppedMatrix.setValue(storageNumber, colIndex, theNewCategory);
					}
				}
			}
			if (rowIsGood) {
				storageNumber++;
			}
		}

		System.out.println("nValid = " + nValid + "; nDropped = " + nDropped);

		// then we kick it out
		this.pushOutput(DroppedMatrix, 0);
		this.pushOutput(CategoryNameCounts,1);
		this.pushOutput(new Long(nValid), 2);
		this.pushOutput(new Long(nDropped), 3);
	}

}