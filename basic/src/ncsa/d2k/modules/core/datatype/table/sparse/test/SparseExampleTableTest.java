package ncsa.d2k.modules.core.datatype.table.sparse.test;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.sparse.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;
import ncsa.d2k.modules.core.datatype.table.sparse.examples.*;

/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SparseExampleTableTest
    extends SparseSubsetTableTest {

  /**
   * Constructor for ExampleTableImplTest.
   * @param arg0
   */
  public SparseExampleTableTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(SparseExampleTableTest.class);
  }

  SparseExampleTable etEmpty, etFull;

  protected int[] vali = {
      0, 1, 2, 3};
  protected double[] vald = {
      3, 2, 1, 0};
  protected short[] vals = {
      1, 2, 3, 0};
  protected float[] valf = {
      2, 3, 0, 1};
  protected long[] vall = {
      3, 0, 1, 2};

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();

    etEmpty = new SparseExampleTable();
    /*columns = new Column[numColumns];
                     int[] vali = { 0, 1, 2, 3 };
                     //		columns[0] = new IntColumn(vali);
                     double[] vald = { 3, 2, 1, 0 };
                     //		columns[1] = new DoubleColumn(vald);
                     short[] vals = { 1, 2, 3, 0 };
                     //		columns[2] = new ShortColumn(vals);
                     float[] valf = { 2, 3, 0, 1 };
                     //		columns[3] = new FloatColumn(valf);
                     long[] vall = { 3, 0, 1, 2 };
                     //		columns[4] = new LongColumn(vall); */
    //SparseMutableTable mtb = new SparseMutableTable(columns);
    etFull = new SparseExampleTable(stFull);
    int inputs[] = {
        0, 1, 2, 3, 4};
    int outputs[] = {
        0, 1, 2, 3, 4};
    int[] train = {
        0, 1};
    int[] test = {
        2, 3};

    etFull.setInputFeatures(inputs);
    etFull.setOutputFeatures(outputs);
    etFull.setTrainingSet(train);
    etFull.setTestingSet(test);

  }

  public Table getFullTable() {
    return etFull;
  }

  public Table getEmptyTable() {
    return etEmpty;
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
    super.tearDown();
  }

//        public void testGetRow() {
//                // test getRow on an empty table
//                Row emptyRow = getEmptyTable().getRow();
//                Row expected  = new SparseExample();
//                assertEquals(emptyRow, expected);
//                Table mFull = getFullTable();
//                int numColumns = mFull.getNumColumns();
//                Row fullRow = mFull.getRow();
//                int numRows = mFull.getNumRows();
//           for (int j =0; j < numRows; j ++) {
//                   fullRow.setIndex(j);
//                            for (int i = 0; i < numColumns ; i ++) {
//                                    assertEquals(fullRow.getString(i),mFull.getString(j,i));
//                               }
//           }
//        }

//  public void testToPredictionTable() {
//    SparseExampleTable et = (SparseExampleTable) getFullTable();
//    SparsePredictionTable pt = new SparsePredictionTable(et);
//    SparsePredictionTable result = (SparsePredictionTable) et.toPredictionTable();
//    assertEquals(result, pt);
//  }

  public void testGetNumInputOuputFeatures() {
    SparseExampleTable etFull = (SparseExampleTable) getFullTable();
    SparseExampleTable etEmpty = (SparseExampleTable) getEmptyTable();
    int expected = etFull.getNumInputFeatures();
    assertEquals(expected, 5);
    assertEquals(etFull.getNumOutputFeatures(), 5);
    assertEquals(etEmpty.getNumInputFeatures(), 0);
    assertEquals(etEmpty.getNumOutputFeatures(), 0);
    int[] ins = {
        0, 1, 2};
    int[] outs = {
        3, 4};
    etFull.setInputFeatures(ins);
    assertEquals(etFull.getNumInputFeatures(), 3);
    etFull.setOutputFeatures(outs);
    assertEquals(etFull.getNumOutputFeatures(), 2);
    //	TODO setInput/OutputFeatures fail on an empty ExampleTable
    //etEmpty.setInputFeatures(ins);
  }

  public void testGetNumTrainTestExamples() {
    ExampleTable etFull = (ExampleTable) getFullTable();
    int expected = etFull.getNumTrainExamples();
    assertEquals(expected, 2);
    expected = etFull.getNumTestExamples();
    assertEquals(expected, 2);
    ExampleTable etEmtpy = (ExampleTable) getEmptyTable();
    etEmpty.addRows(4);
    int[] train = {
        0, 1};
    int[] test = {
        3};
    etEmpty.setTrainingSet(train);
    etEmpty.setTestingSet(test);

    expected = etEmpty.getNumTrainExamples();
    assertEquals(expected, 2);
    expected = etEmpty.getNumTestExamples();
    assertEquals(expected, 1);

  }

  public void testGetOutputFeatures() {
    //TODO Implement getOutputFeatures().
  }

  public void testGetTestTable() {
    ExampleTable etFull = (ExampleTable) getFullTable();
    ExampleTable tTable = (ExampleTable) etFull.getTestTable();
    Row row = etFull.getRow();
    int[] testSet = etFull.getTestingSet();
    for (int i = 0; i < testSet.length; i++) {
      row.setIndex(testSet[i]);
      for (int j = 0; j < etFull.getNumColumns(); j++) {
        assertEquals(tTable.getString(i, j), row.getString(j));
      }
    }
  }

  public void testGetTrainTable() {
    ExampleTable etFull = (ExampleTable) getFullTable();
    ExampleTable tTable = (ExampleTable) etFull.getTrainTable();
    Row row = etFull.getRow();
    int[] trainSet = etFull.getTrainingSet();
    for (int i = 0; i < trainSet.length; i++) {
      row.setIndex(trainSet[i]);
      for (int j = 0; j < etFull.getNumColumns(); j++) {
        assertEquals(tTable.getString(i, j), row.getString(j));
      }
    }
  }

  public void testGetInputDouble() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(vald[i], tbl.getInputDouble(i, 1), delta);
    }
  }

  public void testGetOutputDouble() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(vald[i], tbl.getOutputDouble(i, 1), delta);
    }
  }

  public void testGetInputString() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(
          (new Double(vald[i]).toString()),
          tbl.getInputString(i, 1));
    }
  }

  public void testGetOutputString() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(
          (new Double(vald[i]).toString()),
          tbl.getOutputString(i, 1));
    }
  }

  public void testGetInputInt() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(vali[i], tbl.getInputInt(i, 0));
    }
  }

  public void testGetOutputInt() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(vali[i], tbl.getOutputInt(i, 0));
    }
  }

  public void testGetInputFloat() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(valf[i], tbl.getInputFloat(i, 3), delta);
    }
  }

  public void testGetOutputFloat() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(valf[i], tbl.getOutputFloat(i, 3), delta);
    }
  }

  public void testGetInputShort() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(vals[i], tbl.getInputShort(i, 2));
    }
  }

  public void testGetOutputShort() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(vals[i], tbl.getOutputShort(i, 2));
    }
  }

  public void testGetInputLong() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(vall[i], tbl.getInputLong(i, 4));
    }
  }

  public void testGetOutputLong() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    for (int i = 0; i < tbl.getNumRows(); i++) {
      assertEquals(vall[i], tbl.getOutputLong(i, 4));
    }
  }

  public void testGetInputByte() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    //byte b = "3".getBytes()[0];
    assertEquals(3, tbl.getInputByte(3, 0));

  }

  public void testGetOutputByte() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    //byte b = "3".getBytes()[0];
    assertEquals(3, tbl.getOutputByte(3, 0));
  }

  public void testGetInputChar() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    //char b = "3".toCharArray()[0];
    assertEquals(3, tbl.getInputChar(3, 0));

  }

  public void testGetOutputChar() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    char b = "3".toCharArray()[0];
    assertEquals(3, tbl.getOutputChar(3, 0));
  }

  public void testGetInputChars() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    char[] b = "3".toCharArray();
    char[] result = tbl.getInputChars(3, 0);
    assertEquals(b.length, result.length);
    for (int i = 0; i < b.length; i++) {
      assertEquals(b[i], result[i]);

    }
  }

  public void testGetOutputChars() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    char[] b = "3".toCharArray();
    char[] result = tbl.getOutputChars(3, 0);
    assertEquals(b.length, result.length);
    for (int i = 0; i < b.length; i++) {
      assertEquals(b[i], result[i]);
    }
  }

  public void testGetInputBytes() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    byte[] b = "3".getBytes();
    byte[] result = tbl.getInputBytes(3, 0);
    assertEquals(b.length, result.length);
    for (int i = 0; i < b.length; i++) {
      assertEquals(b[i], result[i]);
    }
  }

  public void testGetOutputBytes() {
    ExampleTable tbl = (ExampleTable) getFullTable();
    byte[] b = "3".getBytes();
    byte[] result = tbl.getOutputBytes(3, 0);
    assertEquals(b.length, result.length);
    for (int i = 0; i < b.length; i++) {
      assertEquals(b[i], result[i]);
    }
  }

  public void testGetInputName() {
    //TODO Implement getInputName().
  }

  public void testGetOutputName() {
    //TODO Implement getOutputName().
  }

  public void testGetInputType() {
    //TODO Implement getInputType().
  }

  public void testGetOutputType() {
    //TODO Implement getOutputType().
  }

  public void testIsInputNominal() {
    SparseExampleTable etFull = (SparseExampleTable) getFullTable();
    boolean expected;
    for (int i = 0; i < etFull.getNumInputFeatures(); i++) {
      expected = etFull.isInputNominal(i);
      assertEquals(expected, false);
    }

  }

  public void testIsOutputNominal() {
    SparseExampleTable etFull = (SparseExampleTable) getFullTable();
    boolean expected;
    for (int i = 0; i < etFull.getNumOutputFeatures(); i++) {
      expected = etFull.isOutputNominal(i);
      assertEquals(expected, false);
    }

  }

  public void testIsInputScalar() {
    SparseExampleTable etFull = (SparseExampleTable) getFullTable();
    boolean expected;
    for (int i = 0; i < etFull.getNumInputFeatures(); i++) {
      expected = etFull.isInputScalar(i);
      assertEquals(expected, true);
    }
  }

  public void testIsOutputScalar() {
    SparseExampleTable etFull = (SparseExampleTable) getFullTable();
    boolean expected;
    for (int i = 0; i < etFull.getNumOutputFeatures(); i++) {
      expected = etFull.isOutputScalar(i);
      assertEquals(expected, true);
    }

  }

  public void testGetInputNames() {
    //TODO Implement getInputNames().
  }

  public void testGetOutputNames() {
    //TODO Implement getOutputNames().
  }



  public void testRemoveColumn() {
    SparseExampleTable mtFull = (SparseExampleTable) getFullTable();
    SparseExampleTable mtEmpty = (SparseExampleTable) getEmptyTable();
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
    SparseExampleTable mtFull = (SparseExampleTable) getFullTable();
    SparseExampleTable mtEmpty = (SparseExampleTable) getEmptyTable();
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

}