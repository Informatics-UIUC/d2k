package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;

/**
 This is a table which provides access to only the rows which are part of
 the test dataset. This is done is such a way as to be transparent to the
 accessing objects. Used to test the effectiveness of the model. From the
 constructor it is also evident that the input columns are all first, and
 the output columns last.
 */
public final class TestTableImpl extends PredictionTableImpl implements TestTable {

	//static final long serialVersionUID = -7408690070856124087L;
	static final long serialVersionUID = -3816781677534840220L;

   	private ExampleTable original;

   	private TestTableImpl(int i) {
      	super(i);
   	}

    /**
     Given an example table, copy it's input columns, and create new
     columns to hold the predicted values.
    @param ttt the prediction table that this test table is derived from
     */
    TestTableImpl (PredictionTableImpl ttt) {
        super(ttt);
      original = ttt;
    }

    /**
     Given an example table, copy it's input columns, and create new
     columns to hold the predicted values.
    @param ttt the prediction table that this test table is derived from
     */
    TestTableImpl (ExampleTableImpl ttt) {
        super(ttt);
      original = ttt;
        //predictions = new int[outputColumns.length];
        //Column []newColumns = new Column[columns.length + outputColumns.length];
        //int i = 0;
        // copy references to the original columns
        //for(; i < columns.length; i++)
        //	newColumns[i] = columns[i];
        // everything below is taken care of by the ExampleTable constructor
        /*this.testSet = new int [ttt.testSet.length];
         //testSet = ttt.testSet;
         System.arraycopy(ttt.testSet, 0, testSet, 0, ttt.testSet.length);
         // Copy the input and output features into our index arrays.
         int inLen = ttt.getInputFeatures ().length;
         inputColumns = new int [inLen];
         System.arraycopy (ttt.getInputFeatures (), 0, inputColumns, 0, inLen);
         int outLen = ttt.getOutputFeatures ().length;
         outputColumns = new int [outLen];
         System.arraycopy (ttt.getOutputFeatures (), 0, outputColumns, 0, outLen);
         columns = ttt.columns;
         columns = new Column [inLen + outLen];
         int i = 0;
         for (; i < inLen ; i++)
         {
         columns [i] = ttt.getColumn (inputColumns [i]);
         inputColumns[i] = i;
         }
         */
        // Create new columns which will contain the predicted values.
        /*for (int i2 = 0 ; i2 < outputColumns.length; i++, i2++)
         {
         Column col = ttt.getColumn (outputColumns [i2]);
         if (col instanceof DoubleColumn)
         col = new DoubleColumn (col.getNumRows ());
         else if (col instanceof FloatColumn)
         col = new FloatColumn (col.getNumRows ());
         else if (col instanceof StringColumn)
         col = new StringColumn (col.getNumRows ());
         else if (col instanceof ByteArrayColumn)
         col = new ByteArrayColumn (col.getNumRows ());
         else if (col instanceof BooleanColumn)
         col = new BooleanColumn (col.getNumRows ());
         else if (col instanceof CharArrayColumn)
         col = new CharArrayColumn (col.getNumRows ());
         else if (col instanceof LongColumn)
         col = new LongColumn (col.getNumRows ());
         else if (col instanceof IntColumn)
         col = new IntColumn (col.getNumRows ());
         else if (col instanceof ShortColumn)
         col = new ShortColumn (col.getNumRows ());
         else if (col instanceof ObjectColumn)
         col = new ObjectColumn (col.getNumRows ());
         newColumns [i] = col;
         //outputColumns [i2] = i;
         predictions[i2] = i;
         }
         columns = newColumns;
         */
    }

    /**
     Copy method. Return an exact copy of this column.  A deep copy
     is attempted, but if it fails a new column will be created,
     initialized with the same data as this column.
     @return A new Column with a copy of the contents of this column.
     */
    public Table copy () {
         TestTableImpl vt;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            vt = (TestTableImpl)ois.readObject();
            ois.close();
            return  vt;
        } catch (Exception e) {
            vt = new TestTableImpl(getNumColumns());
            vt.setKeyColumn(getKeyColumn());
            for (int i = 0; i < getNumColumns(); i++)
                vt.setColumn(getColumn(i).copy(), i);
            vt.setLabel(getLabel());
            vt.setComment(getComment());
            //vt.setType(getType());
         vt.setInputFeatures(getInputFeatures());
         vt.setOutputFeatures(getOutputFeatures());
         vt.setPredictionSet(getPredictionSet());
         vt.transformations = (ArrayList)transformations.clone();
         vt.setTestingSet(getTestingSet());
         vt.setTrainingSet(getTrainingSet());
            return  vt;
        }
    }

    /**
    * Get an int value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the int at (row, column)
     */
    public int getInt (int row, int column) {
        return  columns[column].getInt(this.testSet[row]);
    }

    /**
    * Set an int value in the table.
    * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setInt (int data, int row, int column) {
        columns[column].setInt(data, this.testSet[row]);
    }

    /**
    * Get a short value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the short at (row, column)
     */
    public short getShort (int row, int column) {
        return  columns[column].getShort(this.testSet[row]);
    }

    /**
    * Set a short value in the table.
    * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setShort (short data, int row, int column) {
        columns[column].setShort(data, this.testSet[row]);
    }

    /**
    * Get a long value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the long at (row, column)
     */
    public long getLong (int row, int column) {
        return  columns[column].getLong(this.testSet[row]);
    }

    /**
    * Set a long value in the table.
    * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setLong (long data, int row, int column) {
        columns[column].setLong(data, this.testSet[row]);
    }

    /**
    * Get a float value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the float at (row, column)
     */
    public float getFloat (int row, int column) {
        return  columns[column].getFloat(this.testSet[row]);
    }

    /**
    * Set a float value in the table.
    * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setFloat (float data, int row, int column) {
        columns[column].setFloat(data, this.testSet[row]);
    }

    /**
    * Get a double value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the double at (row, column)
     */
    public double getDouble (int row, int column) {
        return  columns[column].getDouble(this.testSet[row]);
    }

    /**
    * Set a double value in the table.
    * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setDouble (double data, int row, int column) {
        columns[column].setDouble(data, this.testSet[row]);
    }

    /**
    * Get a String value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the String at (row, column)
     */
    public String getString (int row, int column) {
        return  columns[column].getString(this.testSet[row]);
    }

    /**
    * Set a String value in the table.
    * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setString (String data, int row, int column) {
        columns[column].setString(data, this.testSet[row]);
    }

    /**
    * Get a value from the table as an array of bytes.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of bytes
     */
    public byte[] getBytes (int row, int column) {
        return  columns[column].getBytes(this.testSet[row]);
    }

    /**
    * Set a byte[] value in the table.
    * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setBytes (byte[] data, int row, int column) {
        columns[column].setBytes(data, this.testSet[row]);
    }

    /**
    * Get a value from the table as an array of chars
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of chars
     */
    public char[] getChars (int row, int column) {
        return  columns[column].getChars(this.testSet[row]);
    }

    /**
    * Set a char[] value in the table.
    * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setChars (char[] data, int row, int column) {
        columns[column].setChars(data, this.testSet[row]);
    }

    /**
    * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public boolean getBoolean (int row, int column) {
        return  columns[column].getBoolean(this.testSet[row]);
    }

    /**
    * Set a boolean value in the table.
    * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setBoolean (boolean data, int row, int column) {
        columns[column].setBoolean(data, this.testSet[row]);
    }

    /**
     get the number of entries in the test set.
    @return the number of entries in the test set
     */
    public int getNumRows () {
        return  testSet.length;
    }

   public int getNumExamples() {
      return getNumRows();
   }

   public ExampleTable getExampleTable() {
      return original;
   }

   /*public PredictionTable toPredictionTable() {
      return this;
   }*/

   ////////////////////////////////////
   // now, TestTableImpl must override all methods that add, insert, and
   // remove rows in order to correctly keep track of its test set.

   public void addRow(int[] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(float[] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(double[] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(long[] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(short[] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(boolean[] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(String[] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(char[][] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(byte[][] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(Object[] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(byte[] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void addRow(char[] newEntry) {
      addTesting();
      super.addRow(newEntry);
   }

   public void insertRow(int[] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(float[] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(double[] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(long[] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(short[] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(boolean[] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(String[] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(char[][] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(byte[][] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(Object[] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(byte[] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void insertRow(char[] newEntry, int position) {
      insertTesting(testSet[position]);
      super.insertRow(newEntry, testSet[position]);
   }

   public void removeRow(int row) {
      super.removeRow(testSet[row]);
      removeTesting(row);
   }

   public void removeRows(int start, int len) {
      for (int i = 0; i < len; i++)
         removeRow(start);
   }

   public void removeRowsByFlag(boolean[] flags) {
      int offset = 0;
      for (int i = 0; i < flags.length; i++)
         if (flags[i])
            removeRow(i - offset++);
   }

   public void removeRowsByIndex(int[] indices) {
      int offset = 0;
      for (int i = 0; i < indices.length; i++)
         removeRow(indices[i] - offset++);
   }

   // maintenance methods:

   private void addTesting() {

      int[] newTestingSet = new int[testSet.length + 1];
      for (int i = 0; i < testSet.length; i++)
         newTestingSet[i] = testSet[i];
      newTestingSet[testSet.length] = super.getNumRows();

      testSet = newTestingSet;

   }

   private void insertTesting(int tablePosition) {

      for (int i = 0; i < testSet.length; i++)
         if (testSet[i] >= tablePosition)
            testSet[i]++;

      int[] newTestingSet = new int[testSet.length + 1];
      for (int i = 0; i < testSet.length; i++)
         newTestingSet[i] = testSet[i];
      newTestingSet[testSet.length] = tablePosition;

      Arrays.sort(newTestingSet);

      testSet = newTestingSet;

   }

   private void removeTesting(int testingPosition) {

      int[] newTestingSet = new int[testSet.length - 1];
      for (int i = 0; i < testingPosition; i++)
         newTestingSet[i] = testSet[i];

      for (int i = testingPosition + 1; i < testSet.length; i++)
         newTestingSet[i - 1] = testSet[i] - 1;

      testSet = newTestingSet;

   }

}

