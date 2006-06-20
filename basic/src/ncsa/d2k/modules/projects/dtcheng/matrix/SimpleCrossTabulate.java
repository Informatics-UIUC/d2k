package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class SimpleCrossTabulate extends ComputeModule {

  public String getModuleName() {
    return "SimpleCrossTabulate";
  }


  public String getModuleInfo() {
    return "This module operates on two column MFM's which are considered to be " +
        "categories and should contain only integers. The output is a long matrix " +
        "of dimension (max category number in first MFM + 1) x (max category " +
        "number in second MFM + 1). The ROW = category from the first input matrix " +
        "and the COL = category from the second input matrix and the ELEMENT " +
        "VALUE = number of examples in which the two categories are assigned to the " +
        "same point." +
        "<p>" +
        "The point of this thing is to do a simple thing to see how the categories match " +
        "up between the 1km data and the 30m data in Sumatra. Thus, the 1km data will be " +
        "oversampled to the same resolution as the 30m data. Then the two datasets can be " +
        "compared point by point. The result will be a table showing how many of each of " +
        "the 30m categories were present within each 1km category." +
        "<p>" +
        "I am also going to try to make this so that it can accumulate the sums over multiple" +
        "datasets by inputing the previously calculated stuff and the number of previous datasets accumulated." +
        "<p>" +
        "Watch out that you pick the MAGIC NUMBER correctly for the Category Number to Use for Missing. " +
        "It needs to be larger than the highest category number being used. I would use zero except that " +
        "often the categories are indexed beginning at zero.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "FirstMatrix";
      case 1:
        return "SecondMatrix";
      case 2:
        return "PreviousAccumulatedTable";
      case 3:
        return "NumberOfPreviousDatasets";
      case 4:
        return "CategoryNumberToUseForMissing";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "FirstMatrix (i.e. coarser data)";
      case 1:
        return "SecondMatrix (i.e. finer data)";
      case 2:
        return "PreviousAccumulatedTable (previously accumulated cross-tabulation)";
      case 3:
        return "NumberOfPreviousDatasets";
      case 4:
        return "CategoryNumberToUseForMissing (should be a number greater " +
            "than the highest numbered category in either dataset)";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Long",
        "java.lang.Integer",
        "java.lang.Integer",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "AccumulatedTable";
      case 1:
        return "NumberOfDatasets";
      case 2:
        return "CategoryNumberToUseForMissing";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "AccumulatedTable (with the updates from the current dataset)";
      case 1:
        return "NumberOfDatasets (previous + 1)";
      case 2:
        return "CategoryNumberToUseForMissing (the one used in this " +
            "computation and hence to be used for future ones " +
            "accumulating on top of this table)";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long",
        "java.lang.Integer",
        "java.lang.Integer",
    };
    return types;
  }


  public void doit() throws Exception {

    MultiFormatMatrix CoarseMatrix = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix FineMatrix = (MultiFormatMatrix)this.pullInput(1);

    Object OldTableObject = this.pullInput(2);
    long[][] OldTable = new long[1][1];
    if (OldTableObject != null) {
      OldTable = (long[][]) OldTableObject;
    }

    Integer OldNumberOfDatasets = (Integer)this.pullInput(3);
    int CategoryNumberToUseForMissing = ((Integer)this.pullInput(4)).intValue();

    long RowSizeCoarse = CoarseMatrix.getDimensions()[0];
    long ColSizeCoarse = CoarseMatrix.getDimensions()[1];

    long RowSizeFine = FineMatrix.getDimensions()[0];
    long ColSizeFine = FineMatrix.getDimensions()[1];

    if (ColSizeCoarse > 1 | ColSizeFine > 1) {
      System.out.println("(ColSizeCoarse [" + ColSizeCoarse +
                         "] > 1 | ColSizeFine [" + ColSizeFine + "] > 1)");
      throw new Exception();
    }

    if (RowSizeCoarse != RowSizeFine) {
      System.out.println("(RowSizeCoarse [" + RowSizeCoarse +
                         "] != RowSizeFine [" + RowSizeFine + "] )");
      throw new Exception();
    }


    long[][] NewTable = OldTable;
//    long[][] NewTableTemp = NewTable;
    double TableRowDouble = -3.0;
    double TableColDouble = -4.0;
    int TableRow = -1;
    int TableCol = -2;

    // check if the old table has an entry for the missing values category
    if ((OldTable.length < CategoryNumberToUseForMissing + 1) &
        (OldTable[0].length < CategoryNumberToUseForMissing + 1)) {
      // this means that the table is smaller than the missing-value designation,
      // so it is either mismatched or brand new. we will dump a warning to the screen
      // and then proceed to create a new blank one of the proper size
      System.out.println("Previous table is smaller than size determined by the " +
                         "category chosen for missing values. A new table is being created...");
      NewTable = new long[(int)CategoryNumberToUseForMissing + 1][(int)CategoryNumberToUseForMissing + 1];
    }
    else if ((OldTable.length != CategoryNumberToUseForMissing + 1) &
             (OldTable[0].length != CategoryNumberToUseForMissing + 1)) {
        // we have a mismatched table and it is not worth continuing execution...
        System.out.println("Table [" + OldTable.length + "]x[" + OldTable[0].length +
                           "] is mismatched to category chosen for " +
                           "missing values [" + CategoryNumberToUseForMissing + "] .");
        throw new Exception();
    }

    for (long RowIndex = 0; RowIndex < RowSizeCoarse; RowIndex++) {

      TableRowDouble = CoarseMatrix.getValue(RowIndex, 0);
      TableColDouble = FineMatrix.getValue(RowIndex, 0);

      // both are valid observations...
      if (!java.lang.Double.isNaN(TableRowDouble) & !java.lang.Double.isNaN(TableColDouble)) {

        TableRow = (int) TableRowDouble;
        TableCol = (int) TableColDouble;

        NewTable[TableRow][TableCol]++;
      }

      // the row is missing but the column is good
      else if (java.lang.Double.isNaN(TableRowDouble) & !java.lang.Double.isNaN(TableColDouble)) {
        TableRow = CategoryNumberToUseForMissing;
        TableCol = (int) TableColDouble;
        NewTable[TableRow][TableCol]++;
      }

      // the row is good but the column is missing
      else if (!java.lang.Double.isNaN(TableRowDouble) & java.lang.Double.isNaN(TableColDouble)) {
        TableRow = (int) TableRowDouble;
        TableCol = CategoryNumberToUseForMissing;
        NewTable[TableRow][TableCol]++;
      }
    }

    // then we kick it out
    this.pushOutput(NewTable, 0);
    this.pushOutput(new Integer(OldNumberOfDatasets.intValue() + 1), 1);
    this.pushOutput(new Integer(CategoryNumberToUseForMissing), 2);
  }

}


/*

      if (!java.lang.Double.isNaN(TableRowDouble) & !java.lang.Double.isNaN(TableColDouble)) {

        TableRow = (int) TableRowDouble;
        TableCol = (int) TableColDouble;

        // if both are new categories
        if ((TableRow > NewTable.length - 1) & (TableCol > NewTable[0].length - 1)) {
          NewTableTemp = NewTable;
          NewTable = new long[TableRow + 1][TableCol + 1];
          // re-write the old table
          for (int TempRow = 0; TempRow < NewTableTemp.length; TempRow++) {
            for (int TempCol = 0; TempCol < NewTableTemp[0].length; TempCol++) {
              NewTable[TempRow][TempCol] = NewTableTemp[TempRow][TempCol];
            }
          }
          // write in the new entry with the single new example...
          NewTable[TableRow][TableCol] = 1;
        }

        // if only the row is a new category
        else if (TableRow > NewTable.length - 1) {
          NewTableTemp = NewTable;
          NewTable = new long[TableRow + 1][NewTable[0].length];
          // re-write the old table
          for (int TempRow = 0; TempRow < NewTableTemp.length; TempRow++) {
            for (int TempCol = 0; TempCol < NewTableTemp[0].length; TempCol++) {
              NewTable[TempRow][TempCol] = NewTableTemp[TempRow][TempCol];
            }
          }
          // write in the new entry with the single new example...
          NewTable[TableRow][TableCol] = 1;
        }

        // if only the column is a new category
        else if (TableCol > NewTable[0].length - 1) {
          NewTableTemp = NewTable;
          NewTable = new long[NewTable.length][TableCol + 1];
          // re-write the old table
          for (int TempRow = 0; TempRow < NewTableTemp.length; TempRow++) {
            for (int TempCol = 0; TempCol < NewTableTemp[0].length; TempCol++) {
              NewTable[TempRow][TempCol] = NewTableTemp[TempRow][TempCol];
            }
          }
          // write in the new entry with the single new example...
          NewTable[TableRow][TableCol] = 1;
        }

        // if we have seen both categories before...
        else {
          NewTable[TableRow][TableCol]++;
        }
      }


*/
