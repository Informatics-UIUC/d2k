package ncsa.d2k.modules.core.datatype.table.sparse.test;

import ncsa.d2k.modules.core.datatype.sort.*;
import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.modules.core.datatype.table.sparse.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;

import ncsa.d2k.modules.core.datatype.table.basic.test.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SparseSubsetTableTest
    extends SparseMutableTableTest {

  /**
   * Constructor for SubsetTableImplTest.
   * @param arg0
   */
  public SparseSubsetTableTest(String arg0) {
    super(arg0);

  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(SparseSubsetTableTest.class);
  }

  SparseSubsetTable stFull;
  SparseSubsetTable stEmpty;
  int[] subset;

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();

    stEmpty = new SparseSubsetTable();
    stFull = new SparseSubsetTable(columns);
    subset = new int[2];
    subset[0] = 1;
    subset[1] = 3;
  }

  public Table getFullTable() {
    return stFull;
  }

  public Table getEmptyTable() {
    return stEmpty;
  }

  public int[] getSubset() {
    return subset;
  }

  public Table getEmptyMutableTable() {
    return new SparseMutableTable();
  }

  public Column[] getColumns() {
    return columns;
  }

  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception {

    stFull = null;
    stEmpty = null;

  }

  public void testGetNumRows() {
    int numRows = stEmpty.getNumRows();
    assertEquals(0, numRows);
    numRows = stFull.getNumRows();
    //int [] subset = getSubset();
    assertEquals(4, numRows);
    // does it make sense to add rows to a subset ????
    int toAdd = 3;
    stFull.addRows(toAdd);
    assertEquals(numRows + toAdd, stFull.getNumRows());
  }

  public void testShallowCopy() {
    SparseSubsetTable sbt = (SparseSubsetTable) getFullTable();
    Table scopy = sbt.shallowCopy();
    sbt.setInt(7, 0, 0);
    assertEquals(sbt.getNumRows(), scopy.getNumRows());
    assertEquals(sbt.getNumColumns(), scopy.getNumColumns());
    assertEquals(sbt.getInt(0, 0), scopy.getInt(0, 0));
  }

  /*
   * Test for void sortByColumn(int)
   */
  /*	public void testSortByColumnint() {
                  SubsetTableImpl mtFull = (SubsetTableImpl) getFullTable();
                                  int numRows = mtFull.getNumRows();
                  for (int i = 0; i < numColumns; i++) {
                          mtFull.sortByColumn(i);
                          int subset[] = mtFull.getSubset();
                          for (int j = 0; j < subset.length -1; j++)
                                  assertTrue(
                                          "sort failed for column " + i,
       mtFull.getColumn(i).compareRows(subset[j],subset[j + 1]) <= 0);
                  }
          }
   */

  /*
   * Test for void sortByColumn(int, int, int)
   */
  /*	public void testSortByColumnintintint() {
           SubsetTableImpl mtFull = (SubsetTableImpl) getFullTable();
                  int pos = 1;
                  int len = 3;
                  for (int i = 0; i < numColumns; i++) {
                          mtFull.sortByColumn(i, pos, len);
                          int subset[] = mtFull.getSubset();
                          for (int j = pos; j < len - 1; j++)
                                  assertTrue(
                                          "sort failed",
       mtFull.getColumn(i).compareRows(subset[j], subset[j + 1]) <= 0);
                  }
          }
   */
  /*
   * Test for void SubsetTableImpl(int)
   */
  public void testSparseSubsetTableint() {
    int noColumns = 5;
    //SubsetTableImpl sbt = new SubsetTableImpl(noColumns);
    //assertEquals(noColumns, sbt.getNumColumns());
  }

  /*
   * Test for void SubsetTableImpl(TableImpl)
   */
  public void testSparseSubsetTableSparseTable() {
    SparseMutableTable tbl = new SparseMutableTable();
    tbl.addColumns(getColumns());
    int numRows = tbl.getNumRows();
    SparseSubsetTable sbt = new SparseSubsetTable(tbl);
    assertEquals(numRows, sbt.getNumRows());
    tbl = null;
    sbt = null;
  }

  /*
   * Test for void SubsetTableImpl(Column[], int[])
   */
  public void testSparseSubsetTableColumnArrayintArray() {
    int[] subset = {1, 3};
    SparseSubsetTable sbt = new SparseSubsetTable(getColumns(), subset);
    assertEquals(subset.length, sbt.getNumRows());
    sbt = null;
  }

  /*
   * Test for void SubsetTableImpl(TableImpl, int[])
   */
  public void testSubsetTableImplTableImplintArray() {
    SparseMutableTable tbl = new SparseMutableTable();
    tbl.addColumns(getColumns());
    int[] subset = {1, 3};
    SparseSubsetTable sbt = new SparseSubsetTable(tbl, subset);
    assertEquals(subset.length, sbt.getNumRows());
    tbl = null;
    sbt = null;
  }

  public void testRemoveRows() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    int toRemove = 2;
    int numRows = mtFull.getNumRows();
    mtFull.removeRows(0, toRemove);
    assertEquals(numRows - toRemove, mtFull.getNumRows());
    try {
      mtEmpty.removeRows(1, 2);
    }
    catch (NegativeArraySizeException e) {
      return;
    }
    fail("Should raise an NegativeArraySizeException");
  }

  public void testRemoveRow() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    int numRows = mtFull.getNumRows();
    mtFull.removeRow(1);
    assertEquals(numRows - 1, mtFull.getNumRows());
    try {
      mtEmpty.removeRow(1);
    }
    catch (NegativeArraySizeException e) {
      return;
    }
    fail("Should raise a NegativeArraySizeException");

  }

  public void testSetSubset() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable expected = new SparseSubsetTable();
    expected.addColumns(getColumns());
    expected.setSubset(subset);
    mtFull.setSubset(subset);
    assertEquals(mtFull, expected);
  }

  /*
   * Test for int[] getSubset()
   */
  public void testGetSubset() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    int[] subset = {
        1, 3};
    mtFull.setSubset(subset);
    int[] expected = mtFull.getSubset();
    assertEquals(subset, expected);

  }

  public void testGetColumns() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    Column[] cols = mtFull.getColumns();
    Column[] expected = getColumns();
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], cols[i]);
    }
    int[] subset = {1, 3};
    mtFull.setSubset(subset);
    cols = mtFull.getColumns();
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      for (int j = 0; j < cols.length; j++) {
        assertEquals(cols[j].getString(i), expected[j].getString(subset[i]));

      }
    }
  }

  public void testGetColumn() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    int colNum = 1;
    Column col = mtFull.getColumn(colNum);
    assertEquals(col, columns[colNum]);

    int[] subset = {
        1, 3};
    mtFull.setSubset(subset);
    col = mtFull.getColumn(colNum);
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      assertEquals(col.getString(i), columns[colNum].getString(subset[i]));

    }
  }

  public void testSetColumn() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmptyAdd = new SparseSubsetTable();
    SparseSubsetTable mtEmptySet = (SparseSubsetTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtEmptyAdd.addColumn(columns[0]);
    mtEmptySet.addColumn(columns[1]);
    mtEmptySet.setColumn(columns[0], 0);
    assertEquals(mtEmptyAdd.getNumColumns(), mtEmptySet.getNumColumns());
    assertEquals(mtEmptyAdd, mtEmptySet);
    mtEmptyAdd.addColumn(columns[0]);
    mtEmptyAdd.addColumn(columns[2]);
    mtEmptyAdd.addColumn(columns[3]);
    mtEmptyAdd.addColumn(columns[4]);
    mtEmptyAdd.setColumn(columns[1], 1);
    assertEquals(mtEmptyAdd, mtFull);

    mtFull.setSubset(getSubset());
    SparseSubsetTable mtEmpty = new SparseSubsetTable();
    //mtEmpty.setSubset(getSubset());
    mtEmpty.addColumn(columns[0]);
    mtEmpty.addColumn(columns[0]);
    mtEmpty.addColumn(columns[2]);
    mtEmpty.addColumn(columns[3]);
    mtEmpty.addColumn(columns[4]);
    mtEmpty.setSubset(getSubset());
    mtEmpty.setColumn(columns[1], 1);
    // assertEquals(mtEmpty,mtFull);
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      for (int j = 0; j < mtFull.getNumColumns(); j++) {
        //	System.out.println("i "  + i + " j " + j +" copy " + mtEmpty.getString(i,j) + " mtFull " +mtFull.getString(i,j));
        assertEquals(mtEmpty.getString(i, j), mtFull.getString(i, j));
      }
    }

    //	for (int i =0; i < result.getNumRows(); i++)
    //		assertEquals(result.getString(i), mtFull.getString(i, numColumns));
  }

  public void testSetColumns() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmptySet = (SparseSubsetTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    //initialize the table because setColumn does not work on an empty table
    for (int i = 0; i < numColumns; i++) {
      mtEmptySet.addColumn(columns[0]);
    }
    mtEmptySet.setColumns(columns);
    //assertEquals(mtEmptySet,mtFull);
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      for (int j = 0; j < mtFull.getNumColumns(); j++) {
        String one = mtEmptySet.getString(i, j);
        String two = mtFull.getString(i, j);
        //System.out.println("i "  + i + " j " + j +" copy " + mtEmptySet.getString(i,j) + " mtFull " +mtFull.getString(i,j));
        assertEquals(mtEmptySet.getString(i, j), mtFull.getString(i, j));
      }
    }

    mtFull.setSubset(getSubset());
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    mtEmpty.setSubset(getSubset());
    mtEmpty.setColumns(columns);
    assertEquals(mtEmpty, mtFull);
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      for (int j = 0; j < mtFull.getNumColumns(); j++) {
        //	System.out.println("i "  + i + " j " + j +" copy " + mtEmpty.getString(i,j) + " mtFull " +mtFull.getString(i,j));
        assertEquals(mtEmpty.getString(i, j), mtFull.getString(i, j));
      }
    }

    //	for (int i =0; i < result.getNumRows(); i++)
    //		assertEquals(result.getString(i), mtFull.getString(i, numColumns));
  }

  public void testSwapRows() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    int row1 = 0;
    int row2 = 3;
    int numRows = mtFull.getNumRows();
    //mtEmpty.addRows(numRows);
    for (int j = 0; j < mtFull.getNumColumns(); j++) {
      mtEmpty.addColumn(mtFull.getColumn(j));
    }
//    mtFull.print();
//    mtEmpty.print();
    for (int j = 0; j < mtFull.getNumColumns(); j++) {
      mtEmpty.setString(mtFull.getString(row1, j), row2, j);
      mtEmpty.setString(mtFull.getString(row2, j), row1, j);
    }
//    mtFull.print();
//    mtEmpty.print();
    mtFull.swapRows(row1, row2);
//    mtFull.print();
//    mtEmpty.print();
    assertEquals(mtFull, mtEmpty);

  }

  public void testIsValueMissing() {
    //TODO Implement isValueMissing().
  }

  public void testIsValueEmpty() {
    //TODO Implement isValueEmpty().
  }

  public void testSetValueToMissing() {
    //TODO Implement setValueToMissing().
  }

  public void testSetValueToEmpty() {
    //TODO Implement setValueToEmpty().
  }

  public void testInsertColumn() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    int[] subset = getSubset();
    mtFull.setSubset(getSubset());
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    //mtFull.print();
    mtFull.insertColumn(columns[0], 1);
    //mtFull.print();
    assertEquals(numColumns + 1, mtFull.getNumColumns());
    Column result = mtFull.getColumn(1);
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      assertEquals(result.getString(i), columns[0].getString(subset[i]));

//		mtEmpty.setSubset(getSubset());
    }
    mtEmpty.insertColumn(columns[0], 0);
    assertEquals(1, mtEmpty.getNumColumns());
    result = mtEmpty.getColumn(0);
    assertEquals(columns[0], result);

    try {
      mtFull.insertColumn(columns[0], numColumns + 2);
    }
    catch (IndexOutOfBoundsException e) {
      return;
    }
    fail("Should raise an ArrayIndexOutOfBoundsException");

  }

  public void testInsertColumns() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    int subset[] = getSubset();
    mtFull.setSubset(getSubset());
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtFull.insertColumns(columns, 0);
    assertEquals(numColumns + columns.length, mtFull.getNumColumns());
    Column result = mtFull.getColumn(1);
    //assertEquals(result, columns[1]);
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      assertEquals(result.getString(i), columns[1].getString(subset[i]));

    }
    result = mtFull.getColumn(4);
    //assertEquals(result, columns[4]);
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      assertEquals(result.getString(i), columns[4].getString(subset[i]));

    }
    result = mtFull.getColumn(5);
    //assertEquals(result, columns[0]);
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      assertEquals(result.getString(i), columns[0].getString(subset[i]));

    }
    mtEmpty.insertColumns(columns, 0);
    assertEquals(columns.length, mtEmpty.getNumColumns());
    result = mtEmpty.getColumn(0);
    assertEquals(result, columns[0]);
    result = mtEmpty.getColumn(1);
    assertEquals(result, columns[1]);
    result = mtEmpty.getColumn(4);
    assertEquals(result, columns[4]);
  }

  public void testAddColumn() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtEmpty.addColumn(columns[0]);
    assertEquals(mtEmpty.getNumColumns(), 1);
    Column result = mtEmpty.getColumn(0);
    assertEquals(result, columns[0]);

    mtFull.addColumn(columns[0]);
    assertEquals(mtFull.getNumColumns(), numColumns + 1);
    result = mtFull.getColumn(numColumns);
    for (int i = 0; i < result.getNumRows(); i++) {
      assertEquals(result.getString(i), mtFull.getString(i, numColumns));

    }
  }

  public void testAddColumns() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    mtFull.setSubset(getSubset());
    int[] subset = getSubset();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtEmpty.addColumns(columns);
    assertEquals(mtEmpty.getNumColumns(), columns.length);
    Column result = mtEmpty.getColumn(0);
    assertEquals(result, columns[0]);
    mtFull.addColumns(columns);
    assertEquals(mtFull.getNumColumns(), numColumns + columns.length);
    result = mtFull.getColumn(numColumns);
    //assertEquals(result, columns[0]);
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      assertEquals(result.getString(i), columns[0].getString(subset[i]));

    }
    result = mtFull.getColumn(numColumns + columns.length - 1);
    //assertEquals(result, columns[columns.length - 1]);
    for (int i = 0; i < mtFull.getNumRows(); i++) {
      assertEquals(result.getString(i),
                   columns[columns.length - 1].getString(subset[i]));

    }
  }

  public void testRemoveColumn() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtFull.removeColumn(0);
    assertEquals(numColumns - 1, mtFull.getNumColumns());
    Column result = mtFull.getColumn(0);
    int[] sub = mtFull.getSubset();
    for (int i = 0; i < result.getNumRows(); i++) {
      assertEquals(result.getString(i), columns[1].getString(sub[i]));
    }
    try {
      mtFull.removeColumn(numColumns);
      mtEmpty.removeColumn(0);
    }
    catch (IndexOutOfBoundsException e) {
      return;
    }
    fail("Should raise an ArrayIndexOutOfBoundsException");
  }

  public void testRemoveColumns() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtFull.removeColumns(1, 3);
    assertEquals(numColumns - 3, mtFull.getNumColumns());
    Column result = mtFull.getColumn(0);
    int[] sub = mtFull.getSubset();
    for (int i = 0; i < result.getNumRows(); i++) {
      assertEquals(result.getString(i), columns[0].getString(sub[i]));

    }
    result = mtFull.getColumn(1);
    for (int i = 0; i < result.getNumRows(); i++) {
      assertEquals(result.getString(i), columns[4].getString(sub[i]));

    }
    try {

      mtFull.removeColumns(1, 5);
      mtEmpty.removeColumns(0, 1);
    }
    catch (NegativeArraySizeException e) {
      return;
    }
    catch (IndexOutOfBoundsException eb) {
      return;
    }
    fail(
        "Should raise an NegativeArraySizeException or ArrayIndexOutOfBoundsException");

  }

  /*
   * Class to test for Table getSubset(int, int)
   */
  public void testGetSubsetintint() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    int[] subset = {
        1, 2, 3};
    mtFull.setSubset(subset);
    int len = mtFull.getNumRows();
    int pos = 1;
    Table subsetTable = mtFull.getSubset(pos, len - 1);
    for (int i = 0; i < subsetTable.getNumRows(); i++) {
      for (int j = 0; j < mtFull.getNumColumns(); j++) {
        //	String one =  mtFull.getString(pos+i,j);
        //	String two = subsetTable.getString(i,j);
        //	System.out.println("i j mtfull subset " + i + " "+ j + " " + one+ " " + two);
        assertEquals(mtFull.getString(pos + i, j), subsetTable.getString(i, j));
      }
    }
  }

  /*
   * Class to test for Table getSubset(int[])
   */
  public void testGetSubsetintArray() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    int[] subset = {
        1, 2, 3};
    mtFull.setSubset(subset);
    int[] newSubset = {
        0, 2};
    int[] stableSubset = {
        0, 2};
    //newSubset is changed in resubset function call . is that right ???
    Table subsetTable = mtFull.getSubset(newSubset);

    for (int i = 0; i < subsetTable.getNumRows(); i++) {
      for (int j = 0; j < mtFull.getNumColumns(); j++) {
        //String one =  mtFull.getString(stableSubset[i],j);
        //String two = subsetTable.getString(i,j);
        //System.out.println("i j mtfull subset " + i + " "+ j + " " + one+ " " + two);
        assertEquals(mtFull.getString(stableSubset[i], j),
                     subsetTable.getString(i, j));
      }
    }

  }

  /*
   * Class to test for Table copy()
   */
  public void testCopy() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    Table copy = getFullTable().copy();
    assertEquals(copy, mtFull);
    mtFull.setSubset(getSubset());
    SparseMutableTable subsetCopy = (SparseMutableTable) mtFull.copy();
    for (int i = 0; i < subsetCopy.getNumRows(); i++) {
      for (int j = 0; j < subsetCopy.getNumColumns(); j++) {
        String one = subsetCopy.getString(i, j);
        String two = mtFull.getString(i, j);
        //System.out.println("i " + i + " j " + j + " copy " + one + " mtFull " + two);
        assertEquals(subsetCopy.getString(i, j), mtFull.getString(i, j));
      }
    }
  }

  /*
   * Class to test for Table copy(int, int)
   */
  public void testCopyintint() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    int[] subset = {
        1, 2, 3};
    mtFull.setSubset(subset);
    int len = mtFull.getNumRows();
    int pos = 1;
    Table subsetTable = mtFull.copy(pos, len - 1);
    for (int i = 0; i < subsetTable.getNumRows(); i++) {
      for (int j = 0; j < mtFull.getNumColumns(); j++) {
        String one = mtFull.getString(pos + i, j);
        String two = subsetTable.getString(i, j);
        //System.out.println("i j mtfull subset " + i + " " + j + " " + one + " " +  two);
        assertEquals(mtFull.getString(pos + i, j), subsetTable.getString(i, j));
      }
    }

  }

  /*
   * Class to test for Table copy(int[])
   */
  public void testCopyintArray() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    //mtFull.print();
    int[] subset = {1, 2, 3};
    //mtFull.setSubset(subset);
    //mtFull.print();
    int[] newSubset = {0, 2};
    int[] stableSubset = {0, 2};
    Table subsetTable = mtFull.copy(newSubset);
    //((SparseMutableTable)subsetTable).print();
    //mtFull.print();

    for (int i = 0; i < subsetTable.getNumRows(); i++) {
      for (int j = 0; j < mtFull.getNumColumns(); j++) {
        String one = mtFull.getString(newSubset[i], j);
        String two = subsetTable.getString(i, j);
        //System.out.println("testCopyInt array: i j mtfull subset " + i + " " + j + " " + one + " " + two);
        assertEquals(mtFull.getString(stableSubset[i], j), subsetTable.getString(i, j));
      }
    }

  }

  public void testReorderColumns() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();

    int newOrder[] = new int[mtFull.getNumColumns()];
    for (int i = 0; i < newOrder.length; i++) {
      newOrder[i] = i;

    }
    int tmp = newOrder[3];
    newOrder[3] = newOrder[1];
    newOrder[1] = tmp;

    for (int i = 0; i < newOrder.length; i++) {
      mtEmpty.addColumn(mtFull.getColumn(newOrder[i]));
    }
    //mtFull.print();
    //mtEmpty.print();
    SparseMutableTable reordered = (SparseMutableTable) mtFull.reorderColumns(newOrder);
    //reordered.print();
    assertEquals(mtEmpty, reordered);

    mtFull.setSubset(getSubset());
    SparseSubsetTable mtEmptySubset = new SparseSubsetTable();
    for (int i = 0; i < newOrder.length; i++) {
      mtEmptySubset.addColumn(mtFull.getColumn(newOrder[i]));
    }
    //mtFull.print();
    SparseMutableTable reorderedSubset = (SparseMutableTable) mtFull.reorderColumns(
        newOrder);

    //mtEmptySubset.print();
    //reorderedSubset.print();
    assertEquals(mtEmptySubset, reorderedSubset);

  }

  public void testReorderRows() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    //SparseSubsetTable mtEmpty = (SparseSubsetTable) getEmptyTable();
    int newOrder[] = {
        0, 3, 2, 1};
    SparseMutableTable mtEmpty =  (SparseMutableTable)mtFull.copy();
    mtEmpty.removeRows(0, mtFull.getNumRows());
    mtEmpty.addRows(mtFull.getNumRows());
    for (int i = 0; i < newOrder.length; i++) {
      for (int j = 0; j < numColumns; j++) {
        mtEmpty.setString(mtFull.getString(newOrder[i], j), i, j);
      }
    }
    Table reordered = mtFull.copy(newOrder);
    //Table reordered = mtFull.reorderRows(newOrder);
    assertEquals(mtEmpty, reordered);

    mtFull.setSubset(getSubset());
    mtEmpty =  (SparseMutableTable)mtFull.copy();
    mtEmpty.removeRows(0, mtFull.getNumRows());
    mtEmpty.addRows(mtFull.getNumRows());
    int[] nOrder = {
        1, 0};
    for (int i = 0; i < nOrder.length; i++) {
      for (int j = 0; j < numColumns; j++) {
        mtEmpty.setString(mtFull.getString(nOrder[i], j), i, j);
        //reordered = mtFull.reorderRows(nOrder);

      }
    }
    reordered = mtFull.copy(nOrder);
    assertEquals(mtEmpty, reordered);

  }

  public void testAddRows() {
    SparseSubsetTable mtFull = (SparseSubsetTable) getFullTable();
    int rowsToAdd = 3;
    int numRows = mtFull.getNumRows();
    mtFull.addRows(rowsToAdd);
    assertEquals(numRows + rowsToAdd, mtFull.getNumRows());
    mtFull.setSubset(getSubset());
    numRows = mtFull.getNumRows();
    mtFull.addRows(rowsToAdd);
    assertEquals(numRows + rowsToAdd, mtFull.getNumRows());
  }

  /*
   * Class to test for int[] resubset(int, int)
   */
  public void testResubsetintint() {
    //TODO Implement resubset().
  }

  /*
   * Class to test for int[] resubset(int[])
   */
  public void testResubsetintArray() {
    //TODO Implement resubset().
  }

}