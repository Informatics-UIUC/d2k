package ncsa.d2k.modules.core.prediction.svm;
import ncsa.d2k.modules.core.datatype.table.*;
import cern.colt.matrix.impl.*;
import cern.colt.matrix.*;
//import cern.colt.matrix.linalg.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author vered
 * @version 1.0
 */

public class SVMUtils {
  public SVMUtils() {
  }



  /**
   * given an example table, creates a 2D double matrix with the same content,
   * as found in the input features columns.
   * @param tbl - data source.
   * @return - 2D double matrix with data from the inputs features in tbl.
   */
  public static SparseDoubleMatrix2D get2DMatrix(ExampleTable tbl){
     int numRows = tbl.getNumRows();
     int numInputs = tbl.getNumInputFeatures();
    SparseDoubleMatrix2D mtrx = new SparseDoubleMatrix2D(numRows, numInputs);
    for (int i = 0; i< numRows; i++)
      for (int j=0; j< numInputs; j++)
        mtrx.set(i, j, tbl.getInputDouble(i,j));




    return mtrx;

  }


  /**
    * given an example table, creates a vector of doubles with the content
    * of input features in row number <codE>index</code> in <codE>tbl</code>.
    * the last double in the returned vector is 1. (this is so because of computations
    * done by ISvmModel)
    * @param tbl - data source.
    * @param index - row index into tbl.
    * @return - vector of doubles with the content of input features in row
    * number <codE>index</code> in <codE>tbl</code>. and an additional double
    * with value 1, for computation done by Isvm class.

    */

  public static SparseDoubleMatrix1D getExampleMatrix(ExampleTable tbl, int index){
    int numInputs = tbl.getNumInputFeatures();
    SparseDoubleMatrix1D mtrx = new SparseDoubleMatrix1D(numInputs + 1);
    for (int i = 0; i< numInputs; i++){

      mtrx.set(i, tbl.getInputDouble(index, i));
    }

    mtrx.set(numInputs, 1);
    return mtrx;

  }


  /**
      * given an example table, creates a vector of doubles with the content
      * from the first output feature column in tbl. This output feature must be binary.
      * assuming the values are going to be 0 and 1, all zeros are transformed
      * into -1, to sute the Isvm class computation.
      * @param tbl - data source.
      * @return - vector of doubles with the content
      * from the first output feature column in tbl.
      */

  public static SparseDoubleMatrix1D getClassMatrix(ExampleTable tbl){
    int numRows = tbl.getNumRows();
    SparseDoubleMatrix1D mtrx = new SparseDoubleMatrix1D(numRows);
    for (int i = 0; i< numRows; i++){
      double dbl = tbl.getOutputDouble(i,0);
      if(dbl == 0) dbl = -1;
      mtrx.set(i, dbl);
    }

    return mtrx;

 }



}