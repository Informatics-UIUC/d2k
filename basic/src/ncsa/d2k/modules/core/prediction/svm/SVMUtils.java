package ncsa.d2k.modules.core.prediction.svm;
import ncsa.d2k.modules.core.datatype.table.*;
import cern.colt.matrix.impl.*;
import cern.colt.matrix.*;
import cern.colt.matrix.linalg.Algebra;
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

 /**
     * returns a 2D double matrix with the data of A and an additional column
     * all with -1.
     * @param A - data set
     * @return - the dataset labeled -1 in an additional column
     */

  public static DoubleMatrix2D getE(DoubleMatrix2D A) {
    DoubleMatrix2D E = new SparseDoubleMatrix2D(A.rows(), A.columns() + 1);
    int i, j;

    for (i = 0; i < A.rows(); i++) {
      for (j = 0; j < A.columns(); j++) {
        E.set(i, j, A.get(i, j));
      }
      E.set(i, j, -1.0);
    }
    return E;
  }

  /**
     * creates the an Identity matrix with <codE>col</code> by <codE>col</code>
     * dimenssions.
     * @param col - number of columns and rows in the returned matrix
     * @return - an Identity matrix.
     */

  public static DoubleMatrix2D getI(int col) {
    DoubleMatrix2D I = new SparseDoubleMatrix2D(col, col);
    for (int i = 0; i < col; i++)
      I.set(i, i, 1);
    return I;
  }

  public static Algebra Alg;


  /**
   * Prints a cern.colt matrix.
   * @param A - supposed to be either DoubleMatrix1D or DoubleMatrix2D
   */
  public static void printMatrix(Object A) {
    int i, j;

    if (A.getClass().getName().endsWith("1D")) {
      for (i = 0; i < ( (DoubleMatrix1D) A).size(); i++) {
        System.out.print( ( (DoubleMatrix1D) A).get(i) + " ");
      }
      System.out.println();
      System.out.println();
    }
    else if (A.getClass().getName().endsWith("2D")) {
      for (i = 0; i < ( (DoubleMatrix2D) A).rows(); i++) {
        for (j = 0; j < ( (DoubleMatrix2D) A).columns(); j++) {
          System.out.print( ( (DoubleMatrix2D) A).get(i, j) + " ");
        }
        System.out.println();
      }
      System.out.println();
    }
  }





}