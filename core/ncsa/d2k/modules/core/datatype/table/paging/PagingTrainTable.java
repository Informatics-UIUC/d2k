package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;

class PagingTrainTable extends PagingExampleTable implements TrainTable {
	static final long serialVersionUID = -7138942752181423830L;

   protected PagingExampleTable original;

   //PagingTrainTable(PagingExampleTable pet) {
   PagingTrainTable(PagingExampleTable pet, Page[] pages, PageManager pm) {
	   super(pages, pm);

	  original = pet;
	  inputFeatures = copyIntArray(original.getInputFeatures());
	  //inputFeatures = original.inputFeatures;    // make copies?
	  //outputFeatures = original.outputFeatures;
	  outputFeatures = copyIntArray(original.getOutputFeatures());

	  //trainSet = original.trainSet;
	  trainSet = copyIntArray(original.getTrainingSet());
		testSet = copyIntArray(original.getTestingSet());
   }

/******************************************************************************/

   private void readObject(ObjectInputStream in)
	  throws IOException, ClassNotFoundException {

	  numPages = in.readInt();
	  managerCapacity = in.readInt();

	  pages = new Page[numPages];

	  for (int i = 0; i < numPages; i++) {
		 pages[i] = (Page)in.readObject();
		 if (i >= managerCapacity)
			pages[i].free();
	  }

	  offsets = (int[])in.readObject();
	  manager = (PageManager)in.readObject();
	  columnIsNominal = (boolean[])in.readObject();

	  manager.clearWorkingSet(pages, managerCapacity);

	  inputFeatures = (int[])in.readObject();
	  outputFeatures = (int[])in.readObject();
	  testSet = (int[])in.readObject();
	  trainSet = (int[])in.readObject();

	  original = (PagingExampleTable)in.readObject();

	  /*
	  in.defaultReadObject();

	  System.out.println("--\n" + manager + " " + manager.workingSet);
	  for (int i = 0; i < pages.length; i++)
		 System.out.println(pages[i]);
	  System.out.println(managerCapacity);

	  manager.clearWorkingSet(pages, managerCapacity);

	  System.out.println(manager + " " + manager.workingSet);
	  */

   }

   private void writeObject(ObjectOutputStream out) throws IOException {

	  out.writeInt(numPages);
	  out.writeInt(managerCapacity);

	  try {

		 for (int i = 0; i < pages.length; i++) {

			boolean correct = false;
			do {

			   Lock lock = manager.request(pages[i]);

			   lock.acquireWrite();

				  correct = manager.check(pages[i], lock);

				  if (correct)
					 out.writeObject(pages[i]);

			   lock.releaseWrite();

			} while (!correct);

		 }

	  }
	  catch(InterruptedException e) { e.printStackTrace(); }

	  out.writeObject(offsets);
	  out.writeObject(manager);
	  out.writeObject(columnIsNominal);

	  out.writeObject(inputFeatures);
	  out.writeObject(outputFeatures);
	  out.writeObject(testSet);
	  out.writeObject(trainSet);

	  out.writeObject(original);

	  /*
	  System.out.println("writing capacity " + this + " " + managerCapacity);

	  out.defaultWriteObject();
	  */

   }

   public Table getSubset(int pos, int len) {
	 ExampleTable et = (ExampleTable)original.getSubset(pos, len);
//    ExampleTable et  = t.toExampleTable();

 /*    int[] newin = new int[inputColumns.length];
	 System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
	 int[] newout = new int[outputColumns.length];
	 System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);

	 et.setInputFeatures(newin);
	 et.setOutputFeatures(newout);

	 // now figure out the test and train sets
	 int[] traincpy = new int[trainSet.length];
	 System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
	 int[] testcpy = new int[testSet.length];
	 System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

	 int[] newtrain = subsetTrainOrTest(traincpy, pos, len);
	 System.out.println("NEW TRAIN: "+newtrain.length+" OLD: "+traincpy.length);
	 int[] newtest = subsetTrainOrTest(testcpy, pos, len);

	 et.setTrainingSet(newtrain);
	 et.setTestingSet(newtest);
	 */

	 return et.getTrainTable();
   }

   public Table getSubset(int[] rows) {
	 ExampleTable et = (ExampleTable)original.getSubset(rows);
//    ExampleTable et = t.toExampleTable();

 /*    int[] newin = new int[inputColumns.length];
	 System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
	 int[] newout = new int[outputColumns.length];
	 System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);

	 et.setInputFeatures(newin);
	 et.setOutputFeatures(newout);

	 // now figure out the test and train sets
	 int[] traincpy = new int[trainSet.length];
	 System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
	 int[] testcpy = new int[testSet.length];
	 System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

	 int[] newtrain = subsetTrainOrTest(traincpy, rows);
	 int[] newtest = subsetTrainOrTest(testcpy, rows);

	 et.setTrainingSet(newtrain);
	 et.setTestingSet(newtest);
	 */

	 return et.getTrainTable();
   }

   /**
	* Given a starting postion and a number of rows, make a subset table.
	* @param pos the first row to copy.
	* @param len the number of rows.
	* @return a table containing the subset.
	*/
   public Table getSubsetByReference(int pos, int len) {

	   // Make a copy of the example table
	   ExampleTable et  = this.toExampleTable();

	   // now figure out the test and train sets
	   int[] traincpy = new int [len];
	   System.arraycopy(trainSet, pos, traincpy, 0, len);
	   et.setTrainingSet(traincpy);
	   return et.getTrainTable();
   }

   /**
	* Given an array of the rows to be in a subset table, make the subset.
	* @param rows the array of row indices to copy.
	* @return the subset table.
	*/
   public Table getSubsetByReference(int[] rows) {
	 ExampleTable et = this.toExampleTable();

	 // now figure out the test and train sets
	 int[] traincpy = new int[rows.length];
	 for (int i = 0 ; i < traincpy.length ; i++) {
		 traincpy [i] = this.testSet[rows[i]];
	 }
	 et.setTrainingSet(traincpy);
	 return et.getTrainTable();
   }

/*
   public Table getSubsetByReference(int pos, int len) {
	 ExampleTable et = (ExampleTable)original.getSubsetByReference(pos, len);
	 return et.getTrainTable();
   }

   public Table getSubsetByReference(int[] rows) {
	 ExampleTable et = (ExampleTable)original.getSubsetByReference(rows);
	 return et.getTrainTable();
   }*/


   /**
	* Get the example table from which this table was derived.
	* @return the example table from which this table was derived
	*/
   public ExampleTable getExampleTable () {
	  return original;
   }

   /**
	* Get an int value from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the int at (row, column)
	*/
   public int getInt (int row, int column) {
	  return super.getInt(trainSet[row], column);
   }

   /**
	* Get a short value from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the short at (row, column)
	*/
   public short getShort (int row, int column) {
	  return super.getShort(trainSet[row], column);
   }

   /**
	* Get a long value from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the long at (row, column)
	*/
   public long getLong (int row, int column) {
	  return super.getLong(trainSet[row], column);
   }

   /**
	* Get a float value from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the float at (row, column)
	*/
   public float getFloat (int row, int column) {
	  return super.getFloat(trainSet[row], column);
   }

   /**
	* Get a double value from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the double at (row, column)
	*/
   public double getDouble (int row, int column) {
	  return super.getDouble(trainSet[row], column);
   }

   /**
	* Get a String from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the String at (row, column)
	*/
   public String getString (int row, int column) {
	  return super.getString(trainSet[row], column);
   }

   /**
	* Get an array of bytes from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the value at (row, column) as an array of bytes
	*/
   public byte[] getBytes (int row, int column) {
	  return super.getBytes(trainSet[row], column);
   }

   /**
	* Get an array of chars from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the value at (row, column) as an array of chars
	*/
   public char[] getChars (int row, int column) {
	  return super.getChars(trainSet[row], column);
   }

   /**
	* Get a boolean value from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the boolean value at (row, column)
	*/
   public boolean getBoolean (int row, int column) {
	  return super.getBoolean(trainSet[row], column);
   }

   /**
	* Get a byte value from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the byte value at (row, column)
	*/
   public byte getByte (int row, int column) {
	  return super.getByte(trainSet[row], column);
   }

   /**
	* Get a char value from the table.
	* @param row the row of the table
	* @param column the column of the table
	* @return the char value at (row, column)
	*/
   public char getChar (int row, int column) {
	  return super.getChar(trainSet[row], column);
   }

   /**
	* Get the number of entries in the train set.
	* @return the size of the train set
	*/
   public int getNumRows () {
	  if (trainSet == null)
		 return 0;
	  return trainSet.length;
   }

   // MutableTable support

   /**
	* Add a row to the end of this Table, initialized with integer data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(int[] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with float data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(float[] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with double data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(double[] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with long data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(long[] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with short data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(short[] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with boolean data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(boolean[] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with String data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(String[] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with char[] data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(char[][] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with byte[] data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(byte[][] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with Object[] data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(Object[] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with byte data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(byte[] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Add a row to the end of this Table, initialized with char data.
	* @param newEntry the data to put into the new row.
	*/
   public void addRow(char[] newEntry) {
	 addTraining();
	 super.addRow(newEntry);
   }

   /**
	* Insert a new row into this Table, initialized with integer data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(int[] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with float data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(float[] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with double data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(double[] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 //incrementTrainTest(position);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with long data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(long[] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 //incrementTrainTest(position);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with short data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(short[] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 //incrementTrainTest(position);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with boolean data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(boolean[] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 //incrementTrainTest(position);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with String data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(String[] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 //incrementTrainTest(position);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with char[] data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(char[][] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 //incrementTrainTest(position);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with byte[] data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(byte[][] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 //incrementTrainTest(position);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with Object data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(Object[] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 //incrementTrainTest(position);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with byte data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(byte[] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 //incrementTrainTest(position);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Insert a new row into this Table, initialized with char data.
	* @param newEntry the data to put into the inserted row.
	* @param position the position to insert the new row
	*/
   public void insertRow(char[] newEntry, int position) {
	 //insertTraining(trainSet[position]);
	 //incrementTrainTest(position);
	 super.insertRow(newEntry, trainSet[position]);
	 addTraining(position);
   }

   /**
	* Set the row at the given position to an array of int data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(int[] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of float data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(float[] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of double data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(double[] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of long data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(long[] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of short data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(short[] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of boolean data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(boolean[] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of String data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(String[] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of char[] data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(char[][] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of byte[] data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(byte[][] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of Object data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(Object[] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of byte data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(byte[] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Set the row at the given position to an array of char data.
	*	@param newEntry the new values of the row.
	*	@param position the position to set
	*/
   public void setRow(char[] newEntry, int position) {
	 super.setRow(newEntry, trainSet[position]);
   }

   /**
	* Remove a column from the table.
	* @param position the position of the Column to remove
	/
   public void removeColumn(int position) {
	 decrementInOut(position);
	 super.removeColumn(position);
   }*/

   /**
	Remove a range of columns from the table.
	@param start the start position of the range to remove
	@param len the number to remove-the length of the range
	/
   public void removeColumns(int start, int len) {
	 for (int i = start; i < len; i++) {
	   removeColumn(i);
	 }
   }*/

   /**
	* Remove a row from this Table.
	* @param row the row to remove
	*/
   public void removeRow(int row) {
	 //decrementTrainTest(row);
	 super.removeRow(trainSet[row]);
   }

   /**
	Remove a range of rows from the table.
	@param start the start position of the range to remove
	@param len the number to remove-the length of the range
	*/
   public void removeRows(int start, int len) {
	 for (int i = start; i < len; i++) {
	   removeRow(trainSet[i]);
	 }
   }

   /**
	* Remove rows from this Table using a boolean map.
	* @param flags an array of booleans to map to this Table's rows.  Those
	* with a true will be removed, all others will not be removed
	*/
   public void removeRowsByFlag(boolean[] flags) {
	 int numRemoved = 0;
	 for (int i = 0; i < flags.length; i++) {
	   if (flags[i]) {
		 removeRow(trainSet[i - numRemoved]);
	   }
	 }
   }

   /**
	* Remove rows from this Table using a boolean map.
	* @param flags an array of booleans to map to this Table's rows.  Those
	* with a true will be removed, all others will not be removed
	/
   public void removeColumnsByFlag(boolean[] flags) {
	 int numRemoved = 0;
	 for (int i = 0; i < flags.length; i++) {
	   if (flags[i]) {
		 removeColumn(i - numRemoved);
	   }
	 }
   }*/

   /**
	* Remove rows from this Table by index.
	* @param indices a list of the rows to remove
	*/
   public void removeRowsByIndex(int[] indices) {
	 for (int i = 0; i < indices.length; i++) {
	   removeRow(trainSet[indices[i] - i]);
	 }
   }

   /**
	* Remove rows from this Table by index.
	* @param indices a list of the rows to remove
	*/
   public void removeColumnsByIndex(int[] indices) {
	 for (int i = 0; i < indices.length; i++) {
	   removeColumn(trainSet[indices[i] - i]);
	 }
   }

   /**
	Get a copy of this Table reordered based on the input array of indexes.
	Does not overwrite this Table.
	@param newOrder an array of indices indicating a new order
	@return a copy of this column with the rows reordered
	*/
   public Table reorderRows(int[] newOrder) {
	 return super.reorderRows(newOrder);
   }

   /**
	Get a copy of this Table reordered based on the input array of indexes.
	Does not overwrite this Table.
	@param newOrder an array of indices indicating a new order
	@return a copy of this column with the rows reordered
	*/
   public Table reorderColumns(int[] newOrder) {
	 return super.reorderColumns(newOrder);
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

   /**
	Swap the positions of two columns.
	@param pos1 the first column to swap
	@param pos2 the second column to swap
	/
   public void swapColumns(int pos1, int pos2) {
	 super.swapColumns(pos1, pos2);
	 this.swapInOut(pos1, pos2);
   }*/

   /**
	Set a specified element in the table.  If an element exists at this
		position already, it will be replaced.  If the position is beyond the capacity
	of this table then an ArrayIndexOutOfBounds will be thrown.
	@param element the new element to be set in the table
	@param row the row to be changed in the table
	@param column the Column to be set in the given row
	*/
   public void setObject(Object element, int row, int column) {
	 super.setObject(element, trainSet[row], column);
   }

   /**
	* Set an int value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/
   public void setInt(int data, int row, int column) {
	 super.setInt(data, trainSet[row], column);
   }

   /**
	* Set a short value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/
   public void setShort(short data, int row, int column) {
	 super.setShort(data, trainSet[row], column);
   }

   /**
	* Set a float value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/
   public void setFloat(float data, int row, int column) {
	 super.setFloat(data, trainSet[row], column);
   }

   /**
	* Set an double value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/

   public void setDouble(double data, int row, int column) {
	 super.setDouble(data, trainSet[row], column);
   }

   /**
	* Set a long value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/
   public void setLong(long data, int row, int column) {
	 super.setLong(data, trainSet[row], column);
   }

   /**
	* Set a String value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/
   public void setString(String data, int row, int column) {
	 super.setString(data, trainSet[row], column);
   }

   /**
	* Set a byte[] value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/

   public void setBytes(byte[] data, int row, int column) {
	 super.setBytes(data, trainSet[row], column);
   }

   /**
	* Set a boolean value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/
   public void setBoolean(boolean data, int row, int column) {
	 super.setBoolean(data, trainSet[row], column);
   }

   /**
	* Set a char[] value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/

   public void setChars(char[] data, int row, int column) {
	 super.setChars(data, trainSet[row], column);
   }

   /**
	* Set a byte value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/
   public void setByte(byte data, int row, int column) {
	 super.setByte(data, trainSet[row], column);
   }

   /**
	* Set a char value in the table.
	* @param data the value to set
	* @param row the row of the table
	* @param column the column of the table
	*/
   public void setChar(char data, int row, int column) {
	 super.setChar(data, trainSet[row], column);
   }

   /**
								   Set the name associated with a column.
								   @param label the new column label
					   @param position the index of the column to set
		/
					   public void setColumnLabel(String label, int position);
		/**
						   Set the comment associated with a column.
										@param comment the new column comment
						   @param position the index of the column to set
				/
		   public void setColumnComment(String comment, int position);
	   /**
		   Set the number of columns this Table can hold.
		   @param numColumns the number of columns this Table can hold
	  */
	 public void setNumColumns(int numColumns) {
	   dropInOut(numColumns);
	   super.setNumColumns(numColumns);
	 }

   /**
	Sort the specified column and rearrange the rows of the table to
	correspond to the sorted column.
	@param col the column to sort by
	/
	  public void sortByColumn(int col)
	  /**
		 Sort the elements in this column starting with row 'begin' up to row 'end',
			   @param col the index of the column to sort
			 @param begin the row no. which marks the beginnig of the  column segment to be sorted
		 @param end the row no. which marks the end of the column segment to be sorted
		   /
			 public void sortByColumn(int col, int begin, int end);

	   /**
		   Sets a new capacity for this Table.  The capacity is its potential
		  maximum number of entries.  If numEntries is greater than newCapacity,
		   then the Table may be truncated.
		   @param newCapacity a new capacity
	  */
	 public void setNumRows(int newCapacity) {
	   dropTestTrain(newCapacity);
	   super.setNumRows(newCapacity);
	 }

   // maintenance methods:

   private void addTraining() {

	 int[] newTrainingSet = new int[trainSet.length + 1];
	 for (int i = 0; i < trainSet.length; i++) {
	   newTrainingSet[i] = trainSet[i];
	 }
	 newTrainingSet[trainSet.length] = super.getNumRows();

	 //trainSet = newTrainingSet;
	 setTrainingSet(newTrainingSet);

   }

   private void insertTraining(int tablePosition) {

	 for (int i = 0; i < trainSet.length; i++) {
	   if (trainSet[i] >= tablePosition) {
		 trainSet[i]++;

	   }
	 }
	 int[] newTrainingSet = new int[trainSet.length + 1];
	 for (int i = 0; i < trainSet.length; i++) {
	   newTrainingSet[i] = trainSet[i];
	 }
	 newTrainingSet[trainSet.length] = tablePosition;

	 Arrays.sort(newTrainingSet);

	 trainSet = newTrainingSet;

   }

   private void removeTraining(int trainingPosition) {

	 int[] newTrainingSet = new int[trainSet.length - 1];
	 for (int i = 0; i < trainingPosition; i++) {
	   newTrainingSet[i] = trainSet[i];

	 }
	 for (int i = trainingPosition + 1; i < trainSet.length; i++) {
	   newTrainingSet[i - 1] = trainSet[i] - 1;

	 }
	 trainSet = newTrainingSet;

   }

   private void addTraining(int tablePosition) {
	 int[] newTrainingSet = new int[trainSet.length + 1];
	 System.arraycopy(trainSet, 0, newTrainingSet, 0, trainSet.length);
	 newTrainingSet[newTrainingSet.length - 1] = tablePosition;
	 setTrainingSet(newTrainingSet);
   }

}
