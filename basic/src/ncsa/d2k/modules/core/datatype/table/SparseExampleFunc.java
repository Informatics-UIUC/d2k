package ncsa.d2k.modules.core.datatype.table;

/**
 * <p>Title: Sparse Example Table Functions</p>
 * <p>Description: Added functionality for Sparse Example
 * Tables. </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: RiverGlass, Inc.</p>
 * @author searsmith
 * @version 1.0
 */


public interface SparseExampleFunc {

  /**
   * returns the valid output indices of row no. <codE>row</code> in a
   * sorted array.
   *
   * @param row      the row number to retrieve its output features.
   * @return            a sorted integer array with the valid output indices
   *                  of row no. <codE>row</code>.
   */
  public int[] getOutputFeatures (int row);

  /**
   * returns the valid input indices of row no. <codE>row</code> in a
   * sorted array.
   *
   * @param row      the row number to retrieve its input features.
   * @return            a sorted integer array with the valid input indices
   *                  of row no. <codE>row</code>.
   */
  public int[] getInputFeatures (int row);

  /**
   * Returns number of output columns in row no. <code>row</code>
   *
   * @param row     row index
   * @return        number of output columns in row no. <code>row</code>
   */
  public int getNumOutputs (int row);

  /**
   * Returns number of input columns in row no. <code>row</code>
   *
   * @param row     row index
   * @return        number of input columns in row no. <code>row</code>
   */
  public int getNumInputs (int row);


}
