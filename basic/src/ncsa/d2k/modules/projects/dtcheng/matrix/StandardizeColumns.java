package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class StandardizeColumns extends ComputeModule {

  public String getModuleName() {
    return "StandardizeColumns";
  }


  public String getModuleInfo() {
    return "Take care!!! THERE ARE SOME MAGIC doubleS BURIED IN HERE WHERE THEY " +
        "SHOULD BE LONGS!!! THIS WILL NEED MORE WORK TO MAKE IT TRUELY SCALABLE!!!" +
        "This module takes a 2-d MultiFormatMatrix and kicks out a new " +
        "one whose columns have been standardized to mean zero, variance one. " +
        "This aids in training Neural Nets because it keeps the weights within " +
        "reason. Right now, this does a brute force routine which might possibily " +
        "introduce rounding errors if extremely large datasets are used. Alas, " +
        "my module! The mean and variance are supplied so that future scenarios " +
        "data can be predicted using the determined parameters. The basic formula " +
        "will be (Raw - CenteringValue)/ScalingValue . The IndicatorFlags should " +
        "be a MFM with a single row: 0's mean the column is <i>not</i> an " +
        "indicator/dummy variable or more precisely that the column <i>should</i> " +
        "be standardized; 1's mean it is and thus <i>should not</i> be standardized." +
        "<p>" +
        "Also, one can feed in a set of CenteringValues and ScalingValues and it will " +
        "standardize the matrix according to those values (for example, useful for " +
        "validation sets or scenarios). If either of those inputs is a null, then the " +
        "automatic standardization is performed. " +
        "<p>" +
        "Now, this is updated so that <i>if</i> the CenteringValues or ScalingValues are " +
        "not seeded (aka, we need to <i>do</i> the standardization), then also drops any " +
        "rows which contain a missing value in <i>any</i> column in the process of calculating " +
        "the CenteringValues, ScalingValues, and finally the standardized columns.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "IndicatorFlags";
      case 2:
        return "CenteringValuesIn";
      case 3:
        return "ScalingValuesIn";
      case 4:
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
        return "IndicatorFlags";
      case 2:
        return "CenteringValuesIn <null> means calculate";
      case 3:
        return "ScalingValuesIn <null> means calculate";
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
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Long",
    };
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
        return "StandardizedMatrix: the matrix whose columns are now mean 0, " +
            "variance 1.";
      case 1:
        return "CenteringValues: the values to subtract from the raw data; that " +
            "is, the sample means of the columns.";
      case 2:
        return "ScalingValues: the values to scale the raw data by; that is, " +
            "the sample standard deviations of the columns.";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  public void doit() throws Exception {

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix IndicatorFlags = (MultiFormatMatrix)this.pullInput(1);
    Object CenteringValuesIn = this.pullInput(2);
    Object ScalingValuesIn = this.pullInput(3);
    long NumberOfElementsThreshold = ((Long)this.pullInput(4)).longValue();

    long RowSizeX = X.getDimensions()[0];
    long ColSizeX = X.getDimensions()[1];

// some quick idiot-proofing on the indicator flags...
    if (IndicatorFlags.getDimensions()[0] != 1) {
      System.out.println("(IndicatorFlags.getDimensions()[0] != 1) ; " +
                         IndicatorFlags.getDimensions()[0] + " != 1");
      throw new Exception();
    }
    if (IndicatorFlags.getDimensions()[1] != ColSizeX) {
      System.out.println("(IndicatorFlags.getDimensions()[1] != ColSizeX) ; " +
                         IndicatorFlags.getDimensions()[1] + " != " + ColSizeX);
      throw new Exception();
    }
    for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
      if ((IndicatorFlags.getValue(0, ColIndex) != 0.0) && (IndicatorFlags.getValue(0, ColIndex) != 1.0)) {
        System.out.println("ColIndex = " + ColIndex + "; IndicatorFlags.getValue(0,ColIndex) = " + IndicatorFlags.getValue(0,ColIndex)+ "; ((IndicatorFlags.getValue(0,ColIndex) != 0.0) && (IndicatorFlags.getValue(0,ColIndex) != 1.0))");
        throw new Exception();
      }
    }

    long NumElementsSmall = ColSizeX;
    long nValidRows = -4;
    int FormatIndexSmall = -2; // initialize

    if (NumElementsSmall < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndexSmall = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndexSmall = 3; // Beware the MAGIC NUMBER!!!
    }

//    MultiFormatMatrix StandardizedMatrix = new MultiFormatMatrix(FormatIndexBig, new long[] {RowSizeX, ColSizeX});
    MultiFormatMatrix StandardizedMatrix = null;
    MultiFormatMatrix CenteringValues = new MultiFormatMatrix(FormatIndexSmall, new long[] {1, ColSizeX});
    MultiFormatMatrix ScalingValues = new MultiFormatMatrix(FormatIndexSmall, new long[] {1, ColSizeX});
    MultiFormatMatrix TempValues = new MultiFormatMatrix(1, new long[] {1, X.getDimensions()[1]}); // Beware the MAGIC NUMBER!!! format = 1
    double MeanTemp = -5.0;
    double CurrentElement = -6.0;
    boolean RowIsGood = true; // good unless proven otherwise...

// if both seeds are non-null, then use them and check for their reasonability
    if (CenteringValuesIn != null && ScalingValuesIn != null) {
      CenteringValues = (MultiFormatMatrix) CenteringValuesIn;
      ScalingValues = (MultiFormatMatrix) ScalingValuesIn;

      if (CenteringValues.getDimensions()[0] != 1) {
        System.out.println("(CenteringValues.getDimensions()[0] != 1); " +
                           (CenteringValues.getDimensions()[0]) + " != 1");
        throw new Exception();
      }
      if (ScalingValues.getDimensions()[0] != 1) {
        System.out.println("(ScalingValues.getDimensions()[0] != 1); " +
                           (ScalingValues.getDimensions()[0]) + " != 1");
        throw new Exception();
      }
      if (CenteringValues.getDimensions()[1] != ColSizeX) {
        System.out.println("(CenteringValues.getDimensions()[1] != ColSizeX); " +
                           (CenteringValues.getDimensions()[1]) + " != " +
                           ColSizeX);
        throw new Exception();
      }
      if (ScalingValues.getDimensions()[1] != ColSizeX) {
        System.out.println("(ScalingValues.getDimensions()[1] != ColSizeX); " +
                           (ScalingValues.getDimensions()[1]) + " != " +
                           ColSizeX);
        throw new Exception();
      }
      nValidRows = RowSizeX;
    }
    else {
// if the goods are *not* supplied, then we must calculate them for ourselves...
// first we calculate the mean and variance for each column
      MultiFormatMatrix SumTempArray = new MultiFormatMatrix(1, new long[] {1, X.getDimensions()[1]}); // Beware the MAGIC NUMBER!!! format = 1
      MultiFormatMatrix SquareTempArray = new MultiFormatMatrix(1, new long[] {1, X.getDimensions()[1]}); // Beware the MAGIC NUMBER!!! format = 1

      // run through all the columns and write in the indicator ones to make it easy
      for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
        if (IndicatorFlags.getValue(0, ColIndex) == 1) {
          CenteringValues.setValue(0, ColIndex, 0);
          ScalingValues.setValue(0, ColIndex, 1);
        }
      }

      // now i have to run through the goods row by row to determine if the row is good or not, etc.
      RowIsGood = true; // good unless proven otherwise...
      nValidRows = 0;
      for (long RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
        for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
          if (IndicatorFlags.getValue(0, ColIndex) == 0) {
            CurrentElement = X.getValue(RowIndex, ColIndex);
            if (java.lang.Double.isNaN(CurrentElement)) {
              RowIsGood = false;
              break;
            }
            else {
              TempValues.setValue(0, ColIndex, CurrentElement);
            }
          }
        }

        if (RowIsGood) {
          nValidRows++; // bump up the number of good rows...
          for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
            if (IndicatorFlags.getValue(0, ColIndex) == 0) {
              SumTempArray.setValue(0, ColIndex,
                                    SumTempArray.getValue(0, ColIndex) + TempValues.getValue(0, ColIndex));
              SquareTempArray.setValue(0, ColIndex,
                                       SquareTempArray.getValue(0, ColIndex) + TempValues.getValue(0, ColIndex) *
                                       TempValues.getValue(0, ColIndex));
            }
          }
        }
        RowIsGood = true; // reset it to good.
      }
      for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
        if (IndicatorFlags.getValue(0, ColIndex) == 0) {
          MeanTemp = SumTempArray.getValue(0, ColIndex) / (double) nValidRows;
          CenteringValues.setValue(0, ColIndex, MeanTemp);
          ScalingValues.setValue(0, ColIndex,
                                 java.lang.Math.sqrt(SquareTempArray.getValue(0, ColIndex) /
              (double) nValidRows - MeanTemp * MeanTemp)
                                 );
        }
      }
//      System.out.println("end of else, but still inside nValidRows = " + nValidRows);

    }

System.out.println("nValidRows = " + nValidRows);
    /*
          for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
            // if it is an indicator variable, we want to just write it down and say
            // that its center is Zero and its scale is One
            if (IndicatorFlags.getValue(0, ColIndex) == 1) {
              CenteringValues.setValue(0, ColIndex, 0);
              ScalingValues.setValue(0, ColIndex, 1);
            }
            else {
              SumTemp = 0.0;
              SquareTemp = 0.0;
              for (long RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
                CurrentElement = X.getValue(RowIndex,ColIndex);
                if (!java.lang.Double.isNaN(CurrentElement)) {
                  SumTemp += X.getValue(RowIndex, ColIndex);
                  SquareTemp += (X.getValue(RowIndex, ColIndex) * X.getValue(RowIndex, ColIndex));
                }
              }
              MeanTemp = SumTemp / (double) RowSizeX;
              CenteringValues.setValue(0, ColIndex, MeanTemp);
              ScalingValues.setValue(0, ColIndex,
                                     java.lang.Math.sqrt(SquareTemp /
                  (double) RowSizeX -
                  MeanTemp * MeanTemp)
                                     );
            }
          }
        }
     */


// regardless, we rescale the data
    long NumElementsBig = nValidRows * ColSizeX;
    int FormatIndexBig = -1; // initialize
    if (NumElementsBig < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndexBig = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndexBig = 3; // Beware the MAGIC NUMBER!!!
    }

    double CenteringNumber = -6.0;
    double ScaleNumber = -7.0;

//    System.out.println("nValidRows = " + nValidRows + "; ColSizeX = " + ColSizeX);

    StandardizedMatrix = new MultiFormatMatrix(FormatIndexBig, new long[] {nValidRows, ColSizeX});

    long StorageRow = 0;
    RowIsGood = true; // reset it for the beginning of *this* loop
    for (long RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
      for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
        CurrentElement = X.getValue(RowIndex, ColIndex);
        if (java.lang.Double.isNaN(CurrentElement)) {
          RowIsGood = false;
          break;
        }
        else {
          TempValues.setValue(0, ColIndex, CurrentElement);
        }
      }
      if (RowIsGood) {
        for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
          if (IndicatorFlags.getValue(0, ColIndex) == 1) {
            // just write it down unaltered
            StandardizedMatrix.setValue(StorageRow, ColIndex, TempValues.getValue(0, ColIndex));
          }
          else {
            // we center and rescale...
//            CenteringNumber = CenteringValues.getValue(0, ColIndex);
//            ScaleNumber = ScalingValues.getValue(0, ColIndex);
            StandardizedMatrix.setValue(StorageRow, ColIndex,
                                        (TempValues.getValue(0, ColIndex) - CenteringValues.getValue(0, ColIndex)) /
                                        ScalingValues.getValue(0, ColIndex)
                                        );
          }
        }
        StorageRow++; // bump up the storage row...
      }
      RowIsGood = true;
    }

    /*
       for (long ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
         if (IndicatorFlags.getValue(0, ColIndex) == 1) {
           // just write it down unaltered
           for (long RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
             StandardizedMatrix.setValue(RowIndex, ColIndex, X.getValue(RowIndex, ColIndex));
           }
         }
         else {
           // we center and rescale...
           CenteringNumber = CenteringValues.getValue(0, ColIndex);
           ScaleNumber = ScalingValues.getValue(0, ColIndex);
           for (long RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
             StandardizedMatrix.setValue(RowIndex, ColIndex,
                                         (X.getValue(RowIndex, ColIndex) - CenteringNumber) /
                                         ScaleNumber
                                         );
           }
         }
       }

     */

// then we kick it out
    this.pushOutput(StandardizedMatrix, 0);
    this.pushOutput(CenteringValues, 1);
    this.pushOutput(ScalingValues, 2);
  }

}
