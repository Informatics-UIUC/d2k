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

	public Table getSubset(int pos, int len) {
	  Table t = super.getSubset(pos, len);
	  ExampleTable et  = t.toExampleTable();

	  int[] newin = new int[inputColumns.length];
	  System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
	  int[] newout = new int[outputColumns.length];
	  System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);
	  int[] newpred = new int[predictionSet.length];
	  System.arraycopy(predictionSet, 0, newpred, 0, predictionSet.length);

	  // now figure out the test and train sets
	  int[] traincpy = new int[trainSet.length];
	  System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
	  int[] testcpy = new int[testSet.length];
	  System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

	  int[] newtrain = subsetTrainOrTest(traincpy, pos, len);
	  int[] newtest = subsetTrainOrTest(testcpy, pos, len);

	  PredictionTableImpl pt = new PredictionTableImpl(et.getNumColumns());
	  pt.columns = ((ExampleTableImpl)et).columns;
	  pt.setPredictionSet(newpred);
	  pt.setInputFeatures(newin);
	  pt.setOutputFeatures(newout);
	  pt.setTrainingSet(newtrain);
	  pt.setTestingSet(newtest);
	  return pt;
	}

	public Table getSubset(int[] rows) {
	  Table t = super.getSubset(rows);
	  ExampleTable et = t.toExampleTable();

	  int[] newin = new int[inputColumns.length];
	  System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
	  int[] newout = new int[outputColumns.length];
	  System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);
	  int[] newpred = new int[predictionSet.length];
	  System.arraycopy(predictionSet, 0, newpred, 0, predictionSet.length);

	  // now figure out the test and train sets
	  int[] traincpy = new int[trainSet.length];
	  System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
	  int[] testcpy = new int[testSet.length];
	  System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

	  int[] newtrain = subsetTrainOrTest(traincpy, rows);
	  int[] newtest = subsetTrainOrTest(testcpy, rows);

	  //return et.getTrainTable();
	  //PredictionTable pt = et.toPredictionTable();
	  PredictionTableImpl pt = new PredictionTableImpl(et.getNumColumns());
	  pt.columns = ((ExampleTableImpl)et).columns;
	  pt.setPredictionSet(newpred);
	  pt.setInputFeatures(newin);
	  pt.setOutputFeatures(newout);
	  pt.setTrainingSet(newtrain);
	  pt.setTestingSet(newtest);
	  return pt;
	}

	/**
	 * Create a subset of a table give a position and a number of row to compose
	 * the subset.
	 * @param pos the first row to include in the new table.
	 * @param len the length of the new table.
	 * @return the resulting table
	 */
	public Table getSubsetByReference(int pos, int len) {

	  // Make a copy of the example table
	  ExampleTable et  = this.toExampleTable();

	  // Remove the prediction columns, since they will get readded
	  // when we create the test table.
	  for (int i = predictionSet.length - 1 ; i > -1; i--) {
		  et.removeColumn(predictionSet[i]);
	  }

	  // now figure out the test and train sets
	  int[] testcpy = new int [len];
	  System.arraycopy(testSet, pos, testcpy, 0, len);
	  et.setTestingSet(testcpy);
	  return et.getTestTable();
	}

	/**
	 * Given a list of the rows to include in the new table, make a subset
	 * table.
	 * @param rows array of indices of rows to include.
	 * @return the new table.
	 */
	public Table getSubsetByReference(int[] rows) {
	  ExampleTable et = this.toExampleTable();

	  // Remove the prediction columns, since they will get readded
	  // when we create the test table.
	  for (int i = this.predictionSet.length - 1 ; i > -1; i--) {
		  et.removeColumn(this.predictionSet[i]);
	  }

	  // now figure out the test and train sets
	  int[] testcpy = new int[rows.length];
	  for (int i = 0 ; i < testcpy.length ; i++) {
		  testcpy [i] = this.testSet[rows[i]];
	  }
	  et.setTestingSet(testcpy);
	  return et.getTestTable();
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

   /**
	* Set the row at the given position to an array of int data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(int[] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of float data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(float[] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of double data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(double[] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of long data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(long[] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of short data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(short[] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of boolean data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(boolean[] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of String data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(String[] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of char[] data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(char[][] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of byte[] data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(byte[][] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of Object data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(Object[] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of byte data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(byte[] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	* Set the row at the given position to an array of char data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(char[] newEntry, int position) {
	 super.setRow(newEntry, testSet[position]);
   }

   /**
	Swap the positions of two rows.
	@param pos1 the first row to swap
	@param pos2 the second row to swap
	*/
   public void swapRows(int pos1, int pos2) {
	 super.swapRows(trainSet[pos1], trainSet[pos2]);
	 this.swapTestTrain(pos1, pos2);
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

