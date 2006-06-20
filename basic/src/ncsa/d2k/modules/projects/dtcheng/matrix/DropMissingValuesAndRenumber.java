package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class DropMissingValuesAndRenumber extends ComputeModule {

  public String getModuleName() {
    return "DropMissingValuesAndRenumber";
  }


  public String getModuleInfo() {
    return "This module goes row by row and drops the row if it " +
        "contains a missing value. Additionally, it assumes that the " +
        "final column is a list of categories and renumbers them " +
        "AND will drop any category whose fractional representation " +
        "in the dataset is below a specified threshold.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "DropCategoryFractionalThreshold";
      case 2:
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
        return "DropCategoryFractionalThreshold";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Double",
        "java.lang.Long",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "DroppedMatrix";
      case 1:
        return "nValid";
      case 2:
        return "nDropped";
      case 3:
        return "RenumberKey";
//      case 4:
//        return "CategoryCounters";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "DroppedMatrix: the matrix with the missing values dropped";
      case 1:
        return "nValid: the number of rows in the DroppedMatrix; the number " +
            "of valid rows";
      case 2:
        return "nDropped: the number of rows with missing values or weakly " +
            "represented categories that were discarded";
      case 3:
        return "RenumberKey. This will be a column matrix where the row index " +
            "corresponds to the renumbered category and the value is the old " +
            "category number that is now represented by the row index.";
//      case 4:
//        return "CategoryCounters";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Long",
        "java.lang.Long",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
//        "[I",
    };
    return types;
  }


  public void doit() throws Exception {

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    double DropCategoryFractionalThreshold = ((Double)this.pullInput(1)).doubleValue();
    long NumberOfElementsThreshold = ((Long)this.pullInput(2)).longValue();

    // basic idiot-proofing...
    if (DropCategoryFractionalThreshold < 0.0 | DropCategoryFractionalThreshold > 1.0) {
      System.out.println("Error: (DropCategoryFractionalThreshold [" +
                         DropCategoryFractionalThreshold +
                         "] < 0.0 | DropCategoryFractionalThreshold > 1.0)");
      throw new Exception();
    }


    long RowSizeX = X.getDimensions()[0];
    long ColSizeX = X.getDimensions()[1];
    long LastColX = ColSizeX - 1;

    int length = 1;
    int[] CategoryCountersTemp = new int[length];
    int[] CategoryCounters = CategoryCountersTemp;

    // unfortunately, i think i have to do an exhaustive search.

    // Beware the MAGIC NUMBER!!! doing this in memory because i'm assuming that there will be a small number of categories...
    MultiFormatMatrix RenumberKeyTemp = new MultiFormatMatrix(1, new long[] {1, 1});
    MultiFormatMatrix RenumberKey = new MultiFormatMatrix(1, new long[] {1, 1});


    double CategoryUnderConsideration = -2;
    double NewCategoryNumberTemp = -3;

    double LastCategoryIndexBeforeThisExample = 0;
    boolean WeHaveAlreadySeenThisCategory = false;

    boolean ThisIsTheVeryFirstCategoryToBeRecorded = true;

    boolean RowIsGood = true; // good unless proven otherwise...

    for (long RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
      // reset the goods
      RowIsGood = true;
      WeHaveAlreadySeenThisCategory = false;
      for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
        if (java.lang.Double.isNaN(X.getValue(RowIndex, ColIndex))) {
          RowIsGood = false;
          break;
        }
      }
      if (RowIsGood) {
        // check if we have seen this category before
        if (ThisIsTheVeryFirstCategoryToBeRecorded) {
          // pull out the first known category and write it down in the Key Temp
          RenumberKeyTemp.setValue(0, 0, X.getValue(RowIndex, LastColX));
          RenumberKey = RenumberKeyTemp;

          ThisIsTheVeryFirstCategoryToBeRecorded = false;
        }
        CategoryUnderConsideration = X.getValue(RowIndex, LastColX);
        LastCategoryIndexBeforeThisExample = RenumberKey.getDimensions()[0] - 1;
// look through the list of known categories; if it is there, just recode it with the new number
        for (long TempIndex = 0; TempIndex < LastCategoryIndexBeforeThisExample + 1; TempIndex++) {
          if (CategoryUnderConsideration == RenumberKey.getValue(TempIndex, 0)) {
            WeHaveAlreadySeenThisCategory = true;
            NewCategoryNumberTemp = TempIndex;
            break;
          }
        }
// now we know if we have seen it before or not
        if (WeHaveAlreadySeenThisCategory) {
          // old program: just record the new value and move on

          // Beware the MAGIC ASSUMPTION!!! Danger! Danger! Overwriting an inputed matrix rather than making a copy!
          // a new copy of the final matrix will be outputed because we still have to drop the bad rows.
          // regardless: Danger! Danger!

          // new: now we will overwrite the entry in the X matrix
          X.setValue(RowIndex, LastColX, NewCategoryNumberTemp);
          // and bump up the counter...
          CategoryCounters[(int) NewCategoryNumberTemp] = CategoryCounters[(int) NewCategoryNumberTemp] + 1;
        }
        else {
          // we must expand the key and also record.
          RenumberKeyTemp = RenumberKey;
          CategoryCountersTemp = CategoryCounters;
          // (int)LastCategoryIndexBeforeThisExample + 1 + 1
          // because + 1 for the new index value and + 1 for a dimension rather than just an index
          RenumberKey = new MultiFormatMatrix(1,
                                              new long[] {(long) LastCategoryIndexBeforeThisExample + 1 + 1, 1});
          CategoryCounters = new int[(int) LastCategoryIndexBeforeThisExample + 1 + 1];

          // copy over the old stuff.
          for (long KeyIndex = 0; KeyIndex < RenumberKeyTemp.getDimensions()[0]; KeyIndex++) {
            // + 1 to get us the dimension, not the index. and this is to copy the old stuff
            RenumberKey.setValue(KeyIndex, 0, RenumberKeyTemp.getValue(KeyIndex, 0));
            CategoryCounters[(int) KeyIndex] = CategoryCountersTemp[(int) KeyIndex];
          }
          // and the new one...
          RenumberKey.setValue((long) LastCategoryIndexBeforeThisExample + 1, 0, CategoryUnderConsideration);
          CategoryCounters[(int) LastCategoryIndexBeforeThisExample + 1] = 1;

          // overwrite with the new value and move on
          X.setValue(RowIndex, LastColX, LastCategoryIndexBeforeThisExample + 1);

        }
      }
      /*
      if (!java.lang.Double.isNaN(X.getValue(RowIndex,LastColX)) & X.getValue(RowIndex,LastColX) > RenumberKey.getDimensions()[0])
System.out.println("Row " + RowIndex + "; New Category = " + X.getValue(RowIndex,LastColX) + "SeenBefore?" + WeHaveAlreadySeenThisCategory);
        for (int CCC = 0; CCC < ColSizeX; CCC++) {
          System.out.println("Col " + CCC + " = " + X.getValue(RowIndex,CCC));
        }
        */
    }

    // find the total number of good datapoints
    long TotalNotMissing = 0;
    for (int NewCategoryIndex = 0; NewCategoryIndex < CategoryCounters.length; NewCategoryIndex++) {
      TotalNotMissing += CategoryCounters[NewCategoryIndex];
    }
    // find the fractional representation for each category


    double[] FractionalRepresentation = new double[CategoryCounters.length];
    for (int NewCategoryIndex = 0; NewCategoryIndex < CategoryCounters.length; NewCategoryIndex++) {
      FractionalRepresentation[NewCategoryIndex] = ((double) CategoryCounters[NewCategoryIndex] /
          (double) TotalNotMissing);
      System.out.println("RenumedCategoryIndex = " + NewCategoryIndex +
                         "; OriginalCategoryIndex = " + (int)RenumberKey.getValue(NewCategoryIndex,0) +
                         "; total = " + CategoryCounters[NewCategoryIndex] +
                         "; fractional = " + FractionalRepresentation[NewCategoryIndex]);
    }
    // figure out how many rows will be kept
    long nValid = 0;
    long nFinalCategories = 0;
    for (int NewCategoryIndex = 0; NewCategoryIndex < CategoryCounters.length; NewCategoryIndex++) {
      if (FractionalRepresentation[NewCategoryIndex] > DropCategoryFractionalThreshold) {
        nValid += CategoryCounters[NewCategoryIndex];
        nFinalCategories++;
      }
    }

    long nDropped = RowSizeX - nValid;

System.out.println("TotalNotMissing = " + TotalNotMissing + "; nValid = " + nValid +
                       "; nDropped = " + nDropped + "; nFinalCategories = " + nFinalCategories);

    long NumElementsBig = nValid * ColSizeX;
    int FormatIndexBig = -2; // initialize
    if (NumElementsBig < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndexBig = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndexBig = 3; // Beware the MAGIC NUMBER!!!
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    // create the final output matrix...
    /////////////////////////////////////////////////////////////////////////////////////////

    MultiFormatMatrix FinalRenumberKey = new MultiFormatMatrix(1, new long[] {nFinalCategories, 1});

    // reset the boolean for yet another recoding loop
    ThisIsTheVeryFirstCategoryToBeRecorded = true;

    double FinalCategoryUnderConsideration = -58;
    double OriginalCategoryNumber = -59;

    // btw: this is an actual negative one, not just a unique initialization...
    LastCategoryIndexBeforeThisExample = -1;

    MultiFormatMatrix DroppedMatrix = new MultiFormatMatrix(FormatIndexBig, new long[] {nValid, ColSizeX});
    long StorageNumber = 0;
    for (long RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
// System.out.println("Row = " + RowIndex + "; CategoryIndex = " + X.getValue(RowIndex,LastColX));
      RowIsGood = true;
      WeHaveAlreadySeenThisCategory = false;

      // Beware the MAGIC NUMBER!!! i am debugging, so i don't really care.
/*
      if (X.getValue(RowIndex,LastColX) > 20.0) {
        for (int RRR = -1; RRR < 2; RRR++) {
          for (int CCC = 0; CCC < ColSizeX; CCC++) {
            if (RRR == 0) {
              System.out.println("Base [" + (RowIndex+RRR) + "][" + CCC + "] = " + X.getValue(RowIndex+RRR,CCC));
            }
            else {
              System.out.println("Other[" + (RowIndex+RRR) + "][" + CCC + "] = " + X.getValue(RowIndex+RRR,CCC));
            }
          }
        }
      }

*/

      for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
//System.out.println("(before conditions) RowIndex = " + RowIndex + "; ColIndex = " + ColIndex + "; Value = " + X.getValue(RowIndex, ColIndex) + "; RowIsGood = " + RowIsGood);
        if (java.lang.Double.isNaN(X.getValue(RowIndex, ColIndex))) {
          RowIsGood = false;
//System.out.println("(inside if isNaN) RowIndex = " + RowIndex + "; ColIndex = " + ColIndex + "; Value = " + X.getValue(RowIndex, ColIndex) + "; RowIsGood = " + RowIsGood);
          break;
        }
        else if (RowIsGood & (ColIndex == ColSizeX - 1)) {
          if (FractionalRepresentation[(int) X.getValue(RowIndex, LastColX)] < DropCategoryFractionalThreshold) {
            RowIsGood = false;
//System.out.println("(inside else if Fractional Rep) RowIndex = " + RowIndex + "; RowIsGood = " + RowIsGood);
            break;
          }
        }
      }
      if (RowIsGood) {
//  System.out.println("Row " + RowIndex + " is good...");
        if (ThisIsTheVeryFirstCategoryToBeRecorded) {
          OriginalCategoryNumber = RenumberKey.getValue((long)X.getValue(RowIndex, LastColX),0);
          FinalRenumberKey.setValue(0,0, OriginalCategoryNumber);
          ThisIsTheVeryFirstCategoryToBeRecorded = false;
        }

        // check if we have seen this category before

        FinalCategoryUnderConsideration = X.getValue(RowIndex, LastColX);
//        LastCategoryIndexBeforeThisExample = FinalRenumberKey.getDimensions()[0] - 1;
// look through the list of known categories; if it is there, just recode it with the new number
        for (long TempIndex = 0; TempIndex < LastCategoryIndexBeforeThisExample + 1; TempIndex++) {
          // compare the original category from the X to the original category from the TempIndex...
          if ( RenumberKey.getValue((long)FinalCategoryUnderConsideration,0) == FinalRenumberKey.getValue(TempIndex, 0) ) {
            WeHaveAlreadySeenThisCategory = true;
            NewCategoryNumberTemp = TempIndex;
            break;
          }
        }
// now we know if we have seen it before or not
        if (WeHaveAlreadySeenThisCategory) {
          // old program: just record the new values and move on
          // the non-category values
          for (int ColIndex = 0; ColIndex < LastColX; ColIndex++) {
            DroppedMatrix.setValue(StorageNumber, ColIndex, X.getValue(RowIndex,ColIndex));
          }
          // and the new category value...
          DroppedMatrix.setValue(StorageNumber, LastColX, NewCategoryNumberTemp);
//  System.out.println("(if)LastCategoryIndexBeforeThisExample = " + LastCategoryIndexBeforeThisExample);
        }
        else {
          // we must expand the key and also record.
          OriginalCategoryNumber = RenumberKey.getValue((long)FinalCategoryUnderConsideration,0);
//   System.out.println("(else)LastCategoryIndexBeforeThisExample = " + LastCategoryIndexBeforeThisExample);
          FinalRenumberKey.setValue((long)LastCategoryIndexBeforeThisExample + 1, 0, OriginalCategoryNumber);

          // now record the goods.
          for (int ColIndex = 0; ColIndex < LastColX; ColIndex++) {
            DroppedMatrix.setValue(StorageNumber, ColIndex, X.getValue(RowIndex,ColIndex));
          }
          // and the new category value...
          DroppedMatrix.setValue(StorageNumber, LastColX, (long)LastCategoryIndexBeforeThisExample + 1);
          // and bump up the index for the "LastCategory" dealie-wop
          LastCategoryIndexBeforeThisExample++;
        }
        StorageNumber++;
      }
    }

// then we kick it out
    this.pushOutput(DroppedMatrix, 0);
    this.pushOutput(new Long(nValid), 1);
    this.pushOutput(new Long(nDropped), 2);
    this.pushOutput(FinalRenumberKey, 3);
//    this.pushOutput(CategoryCounters, 4);
  }

}
