package ncsa.d2k.modules.core.datatype.table;

/**
 * <p>Title: Sparse</p>
 * <p>Description: An interface class that can be used to
 * identify sparse implementations of tables.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: NCSA</p>
 * @author searsmith
 * @version 1.0
 */


public interface Sparse {

  /**
   * Number of non-default values in a column.
   * @param position int Column position.
   * @return int Count of non-default values.
   */
  public int getColumnNumEntries (int position);

  /**
   * Get sorted array of ints that are row indices for this
   * column for non-default values only.
   * @param columnNumber int Column position.
   * @return int[] Array of index values.
   */
  public int[] getColumnIndices (int columnNumber);

  /**
   * Get sorted array of ints that are row indices for this
   * row for non-default values only.
   * @param rowNumber int Row index.
   * @return int[] Array of index values.
   */
  public int[] getRowIndices (int rowNumber);

  /**
  /**
   * Get unsorted array of ints that are row indices for this
   * row for non-default values only.  In most implementations
   * this method should do less work than the sorted variation.
   * @param rowNumber int Row index.
   * @return int[] Array of index values.
   */
  public int[] getRowIndicesUnsorted (int rowNumber);

  /**
   * Number of non-default values in a row.
   * @param position int Row position.
   * @return int Count of non-default values in row.
   */
  public int getRowNumEntries (int position);

  /**
   * Returns true if there is data stored at (row, col) in this table
   *
   * This should not be confused however with missing values which are data
   * that we don't know the value of.  In this the case of this method we know the value is the
   * default value but it is not missing it just isn't stored.
   *
   * @param row     the row number
   * @param col     the column number
   * @return        true if there is data at position (row, col), otherwise
   *                return false.
   */
  public boolean doesValueExist (int row, int col);

  /**
   * Return a factory object for this table implementation.
   * @return
   */
  public TableFactory getTableFactory();

}
