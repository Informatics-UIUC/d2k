package ncsa.d2k.modules.core.datatype.table.sparse;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;

/**
 * This is a subset of the original table. It contains an array of the
 * indices of the rows of the original table to include in the subset, and
 * the accessor methods access only those rows.
 */
public class SparseSubsetTable extends SparseMutableTable {

  //==============
  // Data Members
  //==============
  protected int[] subset;

  //================
  // Constructor(s)
  //================

  /**
   * default constructor, creates a table with no columns or rows.
   */
  public SparseSubsetTable() {
    super();
    subset = new int[0];
  }

  /**
   * create a subset table with the given number of columns.
   * @param numColumns the number of columns to include.
   */
  SparseSubsetTable(int numColumns) {
    super(0, numColumns);
    subset = new int[0];
  }

//  /**
//   * We are given a table and the subset of the table to apply. The array
//   * of indices contains the indexes of the rows in the subset.
//   * @param table the table implementation.
//   * @param subset the integer subset.
//   */
//  public SparseSubsetTable(TableImpl table) {
//    this.columns = table.getColumns();
//    this.label = table.label;
//    this.comment = table.comment;
//    this.subset = new int[table.getNumRows()];
//    if (table instanceof SubsetTableImpl) {
//      int[] tmp = ( (SubsetTableImpl) table).getSubset();
//      for (int i = 0; i < this.subset.length; i++) {
//        this.subset[i] = tmp[i];
//      }
//    }
//    else
//      for (int i = 0; i < this.subset.length; i++) {
//        this.subset[i] = i;
//      }
//  }
//
//  /**
//   * We are given a table and the subset of the table to apply. The array
//   * of indices contains the indexes of the rows in the subset.
//   * @param table the table implementation.
//   * @param subset the integer subset.
//   */
//  public SparseSubsetTable(Column[] col, int[] subset) {
//    super(col);
//    this.subset = subset;
//  }
//
//  /**
//   * We are given a table and the subset of the table to apply. The array
//   * of indices contains the indexes of the rows in the subset.
//   * @param table the table implementation.
//   * @param subset the integer subset.
//   */
//  public SparseSubsetTable(Column[] col) {
//    super(col);
//    //ANCA added 2 lines below
//    int numRows = super.getNumRows();
//    this.subset = new int[numRows];
//    //ANCA took this out: this.subset = new int [this.getNumRows()];
//    for (int i = 0; i < this.getNumRows(); i++) {
//      subset[i] = i;
//    }
//  }

  /**
   * We are given a table and the subset of the table to apply. The array
   * of indices contains the indexes of the rows in the subset.
   * @param table the table implementation.
   * @param subset the integer subset.
   */
  public SparseSubsetTable(SparseTable table, int[] subset) {
    super(table);
    this.subset = subset;
  }

  /**
   * Set the subset.
   * @param ns the subset
   */
  final protected void setSubset(int[] ns) {
    this.subset = ns;
  }

  /**
   * return the integer array that defines the subset of the original table.
   * @return a subset.
   */
  final public int[] getSubset() {
    return subset;
  }

  /////////////////////////////
  // Table copies.
  //

  /**
   * Get the indices of the subset of the subset given a start
   * and a length.
   * @param start the first entry.
   * @param length the number of entries.
   * @return the new adjusted subset.
   */
  protected int[] resubset(int start, int length) {
    int[] tmp = new int[length];
    for (int i = 0; i < length; i++) {
      tmp[i] = subset[start + i];
    }
    return tmp;
  }

  /**
   * Get the subset of the subset given a list of row indices. This method
   * does not copy the array.
   * @param newset the new subset.
   * @return the adjusted newsubset.
   */
  protected int[] resubset(int[] newset) {
    for (int i = 0; i < newset.length; i++) {
      newset[i] = subset[newset[i]];
    }
    return newset;
  }

  /**
   * Gets a subset of this Table's rows, which is actually a shallow
   * copy which is subsetted..
   * @param pos the start position for the subset
   * @param len the length of the subset
   * @return a subset of this Table's rows
   */
  public Table getSubset(int pos, int len) {
    return new SparseSubsetTable(this, this.resubset(pos, len));
  }

  /**
   * Get a subset of this table.
   * @param rows the rows to include in the subset.
   * @return a subset table.
   */
  public Table getSubset(int[] rows) {
    return new SparseSubsetTable(this, this.resubset(rows));
  }

  ///////////////////////////////////////////
  // Subsetting is done by reordering the subset array, rather than sorting the
  // column.
  //
  /**
          Sort the specified column and rearrange the rows of the table to
          correspond to the sorted column.
          @param col the column to sort by
   */
  public void sortByColumn(int col) {

    int[] tmp = new int[this.subset.length];
    System.arraycopy(this.subset, 0, tmp, 0, this.subset.length);
    this.doSort(this.getColumn(col), tmp, 0, this.getNumRows() - 1, 0);
  }

  /**
       Sort the elements in this column starting with row 'begin' up to row 'end',
     @param col the index of the column to sort
     @param begin the row no. which marks the beginnig of the  column segment to be sorted
       @param end the row no. which marks the end of the column segment to be sorted
   */
  public void sortByColumn(int col, int begin, int end) {
    int[] neworder = new int[end - begin + 1];
    for (int i = begin; i <= end; i++)
      neworder[i - begin] = this.subset[i];
    this.doSort(this.getColumn(col), neworder, 0, neworder.length - 1, begin);
  }

  /**
   Implement the quicksort algorithm.  Partition the array and
   recursively call doSort.
   @param A the array to sort
   @param p the beginning index
   @param r the ending index
   @param t the Table to swap rows for
   @return a sorted array of doubles
   */
  private void doSort(Column A, int[] i, int p, int r, int begin) { //double[] A, int p, int r, MutableTable t) {
    if (p < r) {
      int q = partition(A, i, p, r, begin);
      doSort(A, i, p, q, begin);
      doSort(A, i, q + 1, r, begin);
    }
  }

  /**
   Rearrange the subarray A[p..r] in place.
   @param A the array to rearrange
   @param p the beginning index
   @param r the ending index
   @param t the Table to swap rows for
   @return the partition point
   */
  private int partition(Column A, int[] ix, int p, int r, int begin) {
    int i = p - 1;
    int j = r + 1;
    boolean isMissing = A.isValueMissing(ix[p]);
    while (true) {
      if (isMissing) {
        j--;
        do {
          i++;
        }
        while (!A.isValueMissing(ix[i]));
      }
      else {

        // find the first entry [j] <= entry [p].
        do {
          j--;
        }
        while (A.isValueMissing(ix[j]) || A.compareRows(ix[j], ix[p]) > 0);

        // now find the first entry [i] >= entry [p].
        do {
          i++;
        }
        while (!A.isValueMissing(ix[i]) && A.compareRows(ix[i], ix[p]) < 0);
      }

      if (i < j) {
        this.swapRows(i + begin, j + begin);
        int tmp = ix[i];
        ix[i] = ix[j];
        ix[j] = tmp;
      }
      else
        return j;
    }
  }

  /**
   * Swap the table rows. We do this by simply swaping the indices in the subset array.
   * @param pos1 the first row to swap
   * @param pos2 the second row to swap
   */
  public void swapRows(int pos1, int pos2) {
    int swap = this.subset[pos1];
    this.subset[pos1] = this.subset[pos2];
    this.subset[pos2] = swap;
  }


  /**
   * Get a Column from the table. The columns must be compressed to provide
   * a consistent view of the data.
   * @param pos the position of the Column to get from table
   * @return the Column at in the table at pos
   */
  Column [] getColumns () {
     Column copyColumns [] = new Column [this.getNumColumns()];
     for (int i = 0 ; i < this.getNumColumns() ; i++)
        copyColumns[i] = this.compressColumn(i);
     return copyColumns;
  }

  /**
   * Return a compressed representation of the column identified by the index
   * passed in..
   * @param colindex the column to compress
   * @return the expanded compress.
   */
  private Column compressColumn (int colindex) {

     // init our data objects, create a new column
     Column col = null;
     try {
       col = super.getColumn(colindex);
     } catch (Exception e){
       //should catch the VERY rare and starnge case when a column doesn't exist.
       return null;
     }
     String columnClass = (col.getClass()).getName();
     Column expandedColumn = null;
     try {
        expandedColumn = (Column)Class.forName(columnClass).newInstance();
     } catch (Exception e) {
        System.out.println(e);
     }

     // create a new column
     int numTableRows = this.getNumRows();
     expandedColumn.addRows(numTableRows);
     expandedColumn.setLabel(col.getLabel());
     expandedColumn.setComment(col.getComment());
     expandedColumn.setIsScalar(col.getIsScalar());
     expandedColumn.setIsNominal(col.getIsNominal());

     //set the elements of the column where appropriate as determined by subset
     for (int i = 0; i < this.getNumRows(); i++) {
        if (this.isValueMissing(i, colindex)) {
           expandedColumn.setValueToMissing(true, i);
        } else {
          if (!isValueDefault(i, colindex)){
            expandedColumn.setObject(this.getObject(i, colindex), i);
            // ANCA: replaced line: expandedColumn.setValueToMissing(false, subset[i]);
            expandedColumn.setValueToMissing(false, i);
          }
        }
     }
     return expandedColumn;
  }

  /**
   * Sets the reference to the internal representation of this Table.
   * @param newColumns a new internal representation for this Table
   */
  public void setColumns (Column[] newColumns) {
     for (int i = 0 ; i < this.getNumColumns() ; i++){
       this.setColumn(newColumns[i], i);
     }
  }

  /**
   *
   * Get a Column from the table.
   * @param pos the position of the Column to get from table
   * @return the Column at in the table at pos
   */
  public Column getColumn (int pos) {
     return this.compressColumn(pos);
  }


    /**
   *
   * Get a Column from the table.
   * @param pos the position of the Column to get from table
   * @return the Column at in the table at pos
   */
  public void setColumn (Column col, int where) {
     super.setColumn(expandColumn(col), where);
  }

  /**
   * Column may or may not be the correct size for this table, it may only
   * be the size of the subset. If it is the size of the subset, create a
   * new column of the correct size, then assign the data from the original
   * column to the new column.
   * @param col the column to expand
   * @return the expanded column.
   */
  protected Column expandColumn (Column col) {
     String columnClass = (col.getClass()).getName();
     Column expandedColumn = null;

     //if col is the first column in the table add it as is and initialize subset
     int numRows = super.getNumRows();
     if (columns.size() == 0 && subset.length == 0) {

        // This is the first column added. Set the subset to include everything
        // and submist the column unmodified.
        numRows = col.getNumRows();
        subset = new int[numRows];
        for (int i = 0; i < this.getNumRows(); i++) {
           subset[i] = i;
        }
        expandedColumn = col;
     } else if (numRows == col.getNumRows()) {
        expandedColumn = col;
     } else {

        // the column is not the correct size, resize it to size of the
        // other columns in the table.
        try {
           expandedColumn = (Column)Class.forName(columnClass).newInstance();
        } catch (Exception e) {
           System.out.println(e);
        }
        expandedColumn.addRows(numRows);
        expandedColumn.setLabel(col.getLabel());
        expandedColumn.setComment(col.getComment());
        expandedColumn.setIsScalar(col.getIsScalar());
        expandedColumn.setIsNominal(col.getIsNominal());

        //set the elements of the column where appropriate as determined by subset
        Object el;
        for (int i = 0; i < subset.length; i++) {
           if (col.isValueMissing(i)) {
              expandedColumn.setValueToMissing(true, subset[i]);
           } else {
             if (!((AbstractSparseColumn)col).isValueDefault(i)){
               el = col.getObject(i);
               expandedColumn.setObject(el, subset[i]);
               expandedColumn.setValueToMissing(false, subset[i]);
             }
           }
        }
     }
     return expandedColumn;
  }

  /**
           * Add a new Column after the last occupied position in this Table.
           * If this is the first column in the table it will be added as is.
           * If not, it will be expanded to match the other columns and corresponding subset
           * @param newColumn the Column to be added to the table
           */
  public void addColumn(Column col) {
     col = this.expandColumn(col);
     super.addColumn(col);
  }

  /**
   * Add a new Column after the last occupied position in this Table.
   * @param newColumn the Column to be added to the table
   */
  public void addColumns(Column[] cols) {
     // Expand the columns before adding them.
     for (int i = 0 ; i < cols.length ; i++) {
        addColumn(cols[i]);
     }
  }


  /**
   * Insert a column in the table.
   * @param col the column to add.
   * @param where position were the column will be inserted.
   */
  public void insertColumn(Column col, int where) {
     // expand the column
     col = this.expandColumn(col);
     super.insertColumn(col, where);
  }

  /**
   * Insert columns in the table.
   * @param datatype the columns to add.
   * @param where the number of columns to add.
   */
  public void insertColumns(Column [] datatype, int where){
     for (int i = 0 ; i < datatype.length ; i++) {
       insertColumn(datatype[i], where+i);
     }
  }












































//  /**
//   * Add a new Column after the last occupied position in this Table.
//   * If this is the first column in the table it will be added as is.
//   * If not, it will be expanded to match the other columns and corresponding subset
//   * @param newColumn the Column to be added to the table
//   */
//  public void addColumn(Column col) {
//    // Allocate a new array.
//    Column[] newColumns = new Column[columns.length + 1];
//
//    // copy current columns if any
//    System.arraycopy(columns, 0, newColumns, 0, columns.length);
//
//    //ANCA: need to expand the column
//    // old:		newColumns[newColumns.length - 1] = col;
//
//    String columnClass = (col.getClass()).getName();
//    Column expandedColumn = null;
//    try {
//      expandedColumn = (Column) Class.forName(columnClass).newInstance();
//    }
//    catch (Exception e) {
//      System.out.println(e);
//    }
//
//    //if col is the first column in the table add it as is and initialize subset
//    int numRows = super.getNumRows();
//    if (columns.length == 0) {
//      if (subset.length == 0) {
//        numRows = col.getNumRows();
//        subset = new int[numRows];
//        for (int i = 0; i < this.getNumRows(); i++) {
//          subset[i] = i;
//        }
//        expandedColumn = col;
//      }
//    }
//    else if (numRows == col.getNumRows()) {
//      expandedColumn = col;
//    }
//    else {
//      expandedColumn.addRows(numRows);
//      expandedColumn.setLabel(col.getLabel());
//      expandedColumn.setComment(col.getComment());
//      expandedColumn.setIsScalar(col.getIsScalar());
//      expandedColumn.setIsNominal(col.getIsNominal());
//
//      //initialize all values as missing for the beginning
//      for (int j = 0; j < numRows; j++)
//        expandedColumn.setValueToMissing(true, j);
//
//        //set the elements of the column where appropriate as determined by subset
//      Object el;
//      for (int i = 0; i < subset.length; i++) {
//        if (col.isValueMissing(i)) {
//          expandedColumn.setValueToMissing(true, i);
//        }
//        else {
//          el = col.getObject(i);
//          expandedColumn.setObject(el, subset[i]);
//          expandedColumn.setValueToMissing(false, subset[i]);
//        }
//      }
//    }
//    newColumns[newColumns.length - 1] = expandedColumn;
//    columns = newColumns;
//  }
//
//  /**
//   * Add a new Column after the last occupied position in this Table.
//   * @param newColumn the Column to be added to the table
//   */
//  public void addColumns(Column[] cols) {
//
//    //			Allocate a new array.
//    int number = cols.length;
//    int cnt = columns.length + number;
//    Column[] newColumns = new Column[cnt];
//
//    // copy current columns.
//    System.arraycopy(columns, 0, newColumns, 0, columns.length);
//
//    //			ANCA: need to expand the Column[] cols
//
//    for (int k = 0; k < cols.length; k++) {
//
//      String columnClass = (cols[k].getClass()).getName();
//      Column expandedColumn = null;
//
//      //if col is the first column in the table add it as is and initialize subset
//      int numRows = super.getNumRows();
//      if (columns.length == 0) {
//        if (subset.length == 0) {
//          numRows = cols[k].getNumRows();
//          subset = new int[numRows];
//          for (int i = 0; i < this.getNumRows(); i++) {
//            subset[i] = i;
//          }
//        }
//
//        // LAM-tlr, I moved this down from inside the above condition. If it is
//        // inside the condition above, after the first column is added, the rest of
//        // the columns are empty.
//        expandedColumn = cols[k];
//      }
//      else {
//        // LAM-tlr, I moved this down from above. If we are to use the column
//        // as is, we don't need to reallocate it.
//        try {
//          expandedColumn =
//              (Column) Class.forName(columnClass).newInstance();
//        }
//        catch (Exception e) {
//          System.out.println(e);
//        }
//        expandedColumn.addRows(numRows);
//        expandedColumn.setLabel(cols[k].getLabel());
//        expandedColumn.setComment(cols[k].getComment());
//        expandedColumn.setIsScalar(cols[k].getIsScalar());
//        expandedColumn.setIsNominal(cols[k].getIsNominal());
//
//        //initialize all values as missing for the beginning
//        for (int j = 0; j < numRows; j++)
//          expandedColumn.setValueToMissing(true, j);
//
//          //set the elements of the column where appropriate as determined by subset
//        Object el;
//        for (int i = 0; i < subset.length; i++) {
//          el = cols[k].getObject(i);
//          expandedColumn.setObject(el, subset[i]);
//          expandedColumn.setValueToMissing(false, subset[i]);
//        }
//      }
//      newColumns[columns.length + k] = expandedColumn;
//    }
//    columns = newColumns;
//
//  }
//
//  /**
//   * Return a copy of this Table.
//   * @return A new Table with a copy of the contents of this table.
//   */
//  public Table copy() {
//    TableImpl vt;
//
//    // Copy failed, maybe objects in a column that are not serializable.
//    Column[] cols = new Column[this.getNumColumns()];
//    Column[] oldcols = this.getColumns();
//    for (int i = 0; i < cols.length; i++) {
//      cols[i] = oldcols[i].copy();
//    }
//    int[] newsubset = new int[subset.length];
//    System.arraycopy(subset, 0, newsubset, 0, subset.length);
//    vt = new SubsetTableImpl(cols, newsubset);
//    vt.setLabel(this.getLabel());
//    vt.setComment(this.getComment());
//    return vt;
//  }
//
//  /**
//   * Make a deep copy of the table, include length rows begining at start
//   * @param start the first row to include in the copy
//   * @param length the number of rows to include
//   * @return a new copy of the table.
//   */
//  public Table copy(int start, int length) {
//    TableImpl vt;
//    int[] newsubset = this.resubset(start, length);
//
//    // Copy failed, maybe objects in a column that are not serializable.
//    Column[] cols = new Column[this.getNumColumns()];
//    Column[] oldcols = this.getColumns();
//    for (int i = 0; i < cols.length; i++) {
//      cols[i] = oldcols[i].getSubset(newsubset);
//    }
//
//    vt = new MutableTableImpl(cols);
//    vt.setLabel(this.getLabel());
//    vt.setComment(this.getComment());
//    return vt;
//  }
//
//  /**
//   * Make a deep copy of the table, include length rows begining at start
//   * @param start the first row to include in the copy
//   * @param length the number of rows to include
//   * @return a new copy of the table.
//   */
//  public Table copy(int[] subset) {
//    TableImpl vt;
//    int[] newsubset = this.resubset(subset);
//
//    // Copy failed, maybe objects in a column that are not serializable.
//    Column[] cols = new Column[this.getNumColumns()];
//    Column[] oldcols = this.getColumns();
//    for (int i = 0; i < cols.length; i++) {
//      cols[i] = oldcols[i].getSubset(newsubset);
//    }
//
//    vt = new MutableTableImpl(cols);
//    vt.setLabel(this.getLabel());
//    vt.setComment(this.getComment());
//    return vt;
//  }
//
//  /**
//       * Do a shallow copy on the data by creating a new instance of a MutableTable,
//   * and initialize all it's fields from this one.
//   * @return a shallow copy of the table.
//   */
//  public Table shallowCopy() {
//    SubsetTableImpl vt =
//        new SubsetTableImpl(this.getColumns(), this.subset);
//    vt.setLabel(getLabel());
//    vt.setComment(getComment());
//    return vt;
//  }
//
//  /**
//   * Insert the specified number of blank rows.
//   * @param howMany
//   */
//  public void addRows(int howMany) {
//    for (int i = 0; i < getNumColumns(); i++) {
//      getColumn(i).addRows(howMany);
//    }
//    int[] newsubset = new int[subset.length + howMany];
//    System.arraycopy(subset, 0, newsubset, 0, subset.length);
//    for (int i = subset.length; i < subset.length + howMany; i++)
//      newsubset[i] = i;
//
//    subset = newsubset;
//  }
//
//  /**
//   * Remove a row from this Table.
//   * @param pos the row to remove
//   */
//  public void removeRows(int pos, int cnt) {
//    //		ANCA: rows need not be removed from the columns if they are removed from the subset
//    //for (int i = 0; i < getNumColumns(); i++) {
//    //	getColumn(i).removeRows(pos, cnt);
//    //}
//    int[] newsubset = new int[subset.length - cnt];
//    System.arraycopy(subset, 0, newsubset, 0, pos);
//    System.arraycopy(
//        subset,
//        pos + cnt,
//        newsubset,
//        pos,
//        subset.length - pos - cnt);
//
//    subset = newsubset;
//  }
//
//  /**
//   * Remove a row from this Table.
//   * @param pos the row to remove
//   */
//  public void removeRow(int pos) {
//    //ANCA: rows need not be removed from the columns if they are removed from the subset
//    //for (int i = 0; i < getNumColumns(); i++) {
//    //	getColumn(i).removeRow(subset[pos]);
//    //}
//    int[] newsubset = new int[subset.length - 1];
//
//    System.arraycopy(subset, 0, newsubset, 0, pos);
//    System.arraycopy(
//        subset,
//        pos + 1,
//        newsubset,
//        pos,
//        subset.length - pos - 1);
//    subset = newsubset;
//
//  }
//
//  /**
//   * Returns the length of the table, defined by the size of the subset, not
//   * the table itself.
//   * @return the number of rows int he subset.
//   */
//  public int getNumRows() {
//    return this.subset.length;
//  }
//
  //////////////////////////////////////
  // getters.
  //
  /**
   * Get an Object from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the Object in the Table at (row, column)
   */
  public Object getObject(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getRow(subset[row]);
    } else {
      return SparseDefaultValues.getDefaultObject();
    }
  }

  /**
   * Get an int from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the int in the Table at (row, column)
   */
  public int getInt(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getInt(subset[row]);
    } else {
      return SparseDefaultValues.getDefaultInt();
    }
  }

  /**
   * Get a short from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the short in the Table at (row, column)
   */
  public short getShort(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getShort(subset[row]);
    } else {
      return (short)SparseDefaultValues.getDefaultInt();
    }
  }

  /**
   * Get a long from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the long in the Table at (row, column)
   */
  public long getLong(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getLong(subset[row]);
    } else {
      return (long)SparseDefaultValues.getDefaultInt();
    }
  }

  /**
   * Get a float from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the float in the Table at (row, column)
   */
  public float getFloat(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getFloat(subset[row]);
    } else {
      return (float)SparseDefaultValues.getDefaultDouble();
    }
  }

  /**
   * Get a double from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the float in the Table at (row, column)
   */
  public double getDouble(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getDouble(subset[row]);
    } else {
      return SparseDefaultValues.getDefaultDouble();
    }
  }

  /**
   * Get a String from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the String in the Table at (row, column)
   */
  public String getString(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getString(subset[row]);
    } else {
      return SparseDefaultValues.getDefaultString();
    }
  }

  /**
   * Get the bytes from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the bytes in the Table at (row, column)
   */
  public byte[] getBytes(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getBytes(subset[row]);
    } else {
      return SparseDefaultValues.getDefaultBytes();
    }
  }

  /**
   * Get a byte from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the byte in the Table at (row, column)
   */
  public byte getByte(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getByte(subset[row]);
    } else {
      return SparseDefaultValues.getDefaultByte();
    }
  }

  /**
   * Get a char[] from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the chars in the Table at (row, column)
   */
  public char[] getChars(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getChars(subset[row]);
    } else {
      return SparseDefaultValues.getDefaultChars();
    }
  }

  /**
   * Get a char from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the chars in the Table at (row, column)
   */
  public char getChar(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getChar(subset[row]);
    } else {
      return SparseDefaultValues.getDefaultChar();
    }
  }

  /**
   * Get a boolean from the Table.
   * @param row the position of the row to find the element
   * @param column the column of row to be returned
   * @return the boolean in the Table at (row, column)
   */
  public boolean getBoolean(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.getBoolean(subset[row]);
    } else {
      return SparseDefaultValues.getDefaultBoolean();
    }
  }

  ///////////////////////
  // Missing and empty values.
  //
  public boolean isValueMissing(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.isValueMissing(subset[row]);
    } else {
      return false;
    }
  }

  public boolean isValueEmpty(int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      return col.isValueEmpty(subset[row]);
    } else {
      return false;
    }
  }

  public void setValueToMissing(boolean b, int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      col.setValueToMissing(b, subset[row]);
    } else {
      throw new RuntimeException("ERROR: Column doesn't exist in table.");
    }
  }

  public void setValueToEmpty(boolean b, int row, int column) {
    //the index out of bounds check is performed in the getColumn call.
    Column col = getColumn(column);
    if (col != null) {
      col.setValueToEmpty(b, subset[row]);
    } else {
      throw new RuntimeException("ERROR: Column doesn't exist in table.");
    }
  }

//  //////////////////////////////////////
//  // Setter methods.
//  //
//  /**
//   * Set an Object in the Table.
//   * @param element the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setObject(Object element, int row, int column) {
//    columns[column].setRow(element, subset[row]);
//  }
//
//  /**
//   * Set an int value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setInt(int data, int row, int column) {
//    columns[column].setInt(data, subset[row]);
//  }
//
//  /**
//   * Set a short value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setShort(short data, int row, int column) {
//    columns[column].setShort(data, subset[row]);
//  }
//
//  /**
//   * Set a long value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setLong(long data, int row, int column) {
//    columns[column].setLong(data, subset[row]);
//  }
//
//  /**
//   * Set a float value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setFloat(float data, int row, int column) {
//    columns[column].setFloat(data, subset[row]);
//  }
//
//  /**
//   * Set a double value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setDouble(double data, int row, int column) {
//    columns[column].setDouble(data, subset[row]);
//  }
//
//  /**
//   * Set a String value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setString(String data, int row, int column) {
//    columns[column].setString(data, subset[row]);
//  }
//
//  /**
//   * Set a byte[] value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setBytes(byte[] data, int row, int column) {
//    columns[column].setBytes(data, subset[row]);
//  }
//
//  /**
//   * Set a byte value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setByte(byte data, int row, int column) {
//    columns[column].setByte(data, subset[row]);
//  }
//
//  /**
//   * Set a char[] value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setChars(char[] data, int row, int column) {
//    columns[column].setChars(data, subset[row]);
//  }
//
//  /**
//   * Set a char value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setChar(char data, int row, int column) {
//    columns[column].setChar(data, subset[row]);
//  }
//
//  /**
//   * Set a boolean value in the Table.
//   * @param data the value to set
//   * @param row the row of the table
//   * @param column the column of the table
//   */
//  public void setBoolean(boolean data, int row, int column) {
//    columns[column].setBoolean(data, subset[row]);
//  }
}