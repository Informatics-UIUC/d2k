package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;

class PagingTestTable
	extends PagingPredictionTable
	implements TestTable {

  static final long serialVersionUID = -4264210069845688368L;

  PagingTestTable(PagingExampleTable pet, Page[] pgs, PageManager pm) {
	super(pet, pgs, pm);
	//testSet = pet.getTestingSet();
	//testSet = testSet;
	//testSet = copyIntArray(pet.getTestingSet());

	//inputFeatures = pet.getInputFeatures();
	//outputFeatures = pet.getOutputFeatures();
  }

	  /******************************************************************************/

  /**
  * This is a prediction table, return this.
  * @return this which is a prediction table
  */
  public PredictionTable toPredictionTable() {
	return this;
  }
  private void readObject(ObjectInputStream in) throws IOException,
	  ClassNotFoundException {

	numPages = in.readInt();
	managerCapacity = in.readInt();

	pages = new Page[numPages];

	for (int i = 0; i < numPages; i++) {
	  pages[i] = (Page) in.readObject();
	  if (i >= managerCapacity) {
		pages[i].free();
	  }
	}

	offsets = (int[]) in.readObject();
	manager = (PageManager) in.readObject();
	columnIsNominal = (boolean[]) in.readObject();

	manager.clearWorkingSet(pages, managerCapacity);

	inputFeatures = (int[]) in.readObject();
	outputFeatures = (int[]) in.readObject();
	testSet = (int[]) in.readObject();
	trainSet = (int[]) in.readObject();

	predictionSet = (int[]) in.readObject();
	predictionColumnsTable = (MutablePagingTable) in.readObject();
	indirection = (int[]) in.readObject();
	prediction = (boolean[]) in.readObject();
	original = (PagingTable) in.readObject();
	newTableHackVariable = in.readBoolean();

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

		  if (correct) {
			out.writeObject(pages[i]);

		  }
		  lock.releaseWrite();

		}
		while (!correct);

	  }

	}
	catch (InterruptedException e) {
	  e.printStackTrace();
	}

	out.writeObject(offsets);
	out.writeObject(manager);
	out.writeObject(columnIsNominal);

	out.writeObject(inputFeatures);
	out.writeObject(outputFeatures);
	out.writeObject(testSet);
	out.writeObject(trainSet);

	out.writeObject(predictionSet);
	out.writeObject(predictionColumnsTable);
	out.writeObject(indirection);
	out.writeObject(prediction);
	out.writeObject(original);
	out.writeBoolean(newTableHackVariable);

	/*
		 System.out.println("writing capacity " + this + " " + managerCapacity);
		   out.defaultWriteObject();
	 */

  }

  public Object getObject(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getObject(row, indirection[column]);
	}
	else {

	  // return original.getObject(testSet[row], indirection[column]);
	  return super.getObject(testSet[row], indirection[column]);
	}
  }
  public void setObject(Object v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setObject(v, row, indirection[column]);
	} else {
	  super.setObject(v, testSet[row], indirection[column]);
	}
  }

  public int getInt(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getInt(row, indirection[column]);
	}
	else {

	  // return original.getInt(testSet[row], indirection[column]);
	  return super.getInt(testSet[row], indirection[column]);
	}
  }
  public void setInt(int v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setInt(v, row, indirection[column]);
	} else {
	  super.setInt(v, testSet[row], indirection[column]);
	}
  }

  public short getShort(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getShort(row, indirection[column]);
	}
	else {

	  // return original.getShort(testSet[row], indirection[column]);
	  return super.getShort(testSet[row], indirection[column]);
	}
  }
  public void setShort(short v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setShort(v, row, indirection[column]);
	} else {
	  super.setShort(v, testSet[row], indirection[column]);
	}
  }

  public float getFloat(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getFloat(row, indirection[column]);
	}
	else {

	  // return original.getFloat(testSet[row], indirection[column]);
	  return super.getFloat(testSet[row], indirection[column]);
	}
  }
  public void setFloat(float v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setFloat(v, row, indirection[column]);
	} else {
	  super.setFloat(v, testSet[row], indirection[column]);
	}
  }

  public double getDouble(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getDouble(row, indirection[column]);
	}
	else {

	  // return original.getDouble(testSet[row], indirection[column]);
	  return super.getDouble(testSet[row], indirection[column]);
	}
  }
  public void setDouble(double v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setDouble(v, row, indirection[column]);
	} else {
	  super.setDouble(v, testSet[row], indirection[column]);
	}
  }

  public long getLong(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getLong(row, indirection[column]);
	}
	else {

	  // return original.getLong(testSet[row], indirection[column]);
	  return super.getLong(testSet[row], indirection[column]);
	}
  }
  public void setLong(long v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setLong(v, row, indirection[column]);
	} else {
	  super.setLong(v, testSet[row], indirection[column]);
	}
  }

  public String getString(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getString(row, indirection[column]);
	}
	else {

	  // return original.getString(testSet[row], indirection[column]);
	  return super.getString(testSet[row], indirection[column]);
	}
  }
  public void setString(String v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setString(v, row, indirection[column]);
	} else {
	  super.setString(v, testSet[row], indirection[column]);
	}
  }

  public byte[] getBytes(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getBytes(row, indirection[column]);
	}
	else {

	  // return original.getBytes(testSet[row], indirection[column]);
	  return super.getBytes(testSet[row], indirection[column]);
	}
  }
  public void setBytes(byte[] v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setBytes(v, row, indirection[column]);
	} else {
	  super.setBytes(v, testSet[row], indirection[column]);
	}
  }

  public boolean getBoolean(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getBoolean(row, indirection[column]);
	}
	else {

	  // return original.getBoolean(testSet[row], indirection[column]);
	  return super.getBoolean(testSet[row], indirection[column]);
	}
  }
  public void setBoolean(boolean v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setBoolean(v, row, indirection[column]);
	} else {
	  super.setBoolean(v, testSet[row], indirection[column]);
	}
  }

  public char[] getChars(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getChars(row, indirection[column]);
	}
	else {

	  // return original.getChars(testSet[row], indirection[column]);
	  return super.getChars(testSet[row], indirection[column]);
	}
  }
  public void setChars(char [] v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setChars(v, row, indirection[column]);
	} else {
	  super.setChars(v, testSet[row], indirection[column]);
	}
  }

  public byte getByte(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getByte(row, indirection[column]);
	}
	else {

	  // return original.getByte(testSet[row], indirection[column]);
	  return super.getByte(testSet[row], indirection[column]);
	}
  }
  public void setByte(byte v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setByte(v, row, indirection[column]);
	} else {
	  super.setByte(v, testSet[row], indirection[column]);
	}
  }

  public char getChar(int row, int column) {
	if (prediction[column]) {
	  return predictionColumnsTable.getChar(row, indirection[column]);
	}
	else {

	  // return original.getChar(testSet[row], indirection[column]);
	  return super.getChar(testSet[row], indirection[column]);
	}
  }
  public void setChar(char v, int row, int column) {
	if (prediction[column]) {
	  predictionColumnsTable.setChar(v, row, indirection[column]);
	} else {
	  super.setChar(v, testSet[row], indirection[column]);
	}
  }

  public int getNumRows() {
	// return original.getNumRows();
	if (testSet == null) {
	  return 0;
	}
	else {
	  return testSet.length;
	}
  }

  public void getRow(Object buffer, int position) {
	original.getRow(buffer, testSet[position]);
  }

  public void getColumn(Object buffer, int position) {
	if (prediction[position]) {
	  predictionColumnsTable.getColumn(buffer, indirection[position]);
	}
	else {
	  original.getColumn(buffer, indirection[position]);
	}
  }

  public boolean isValueMissing(int row, int column) {
	  if (prediction[column]) {
		return predictionColumnsTable.isValueMissing(row, indirection[column]);
	  } else {
		  return super.isValueMissing(testSet[row], indirection[column]);
	  }
  }


  public Table getSubset(int start, int len) {
	PagingTestTable ptt =
		(PagingTestTable) ( (PagingExampleTable) ( (PagingTable) original.copy()).
						   toExampleTable().getTestTable());
	ptt.predictionColumnsTable = (MutablePagingTable) predictionColumnsTable.
		copy();

	int[] newTestingSet = new int[len];
	for (int i = start; i < start + len; i++) {
	  newTestingSet[i - start] = testSet[i];
	}
	ptt.testSet = newTestingSet;
	return ptt;
  }

  public Table getSubset(int[] rows) {
	PagingTestTable ptt =
		(PagingTestTable) ( (PagingExampleTable) ( (PagingTable) original.copy()).
						   toExampleTable().getTestTable());
	ptt.predictionColumnsTable = (MutablePagingTable) predictionColumnsTable.
		copy();

	int[] newTestingSet = new int[rows.length];
	for (int i = 0; i < rows.length; i++) {
	  newTestingSet[i] = testSet[rows[i]];
	}
	ptt.testSet = newTestingSet;
	return ptt;
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

  public Table copy() {
	PagingTestTable ptt =
		(PagingTestTable) ( (PagingExampleTable) ( (PagingTable) original.copy()).
						   toExampleTable().getTestTable());
	ptt.predictionColumnsTable = (MutablePagingTable) predictionColumnsTable.
		copy();

	if (testSet != null) {
	  int[] newTestingSet = new int[testSet.length];
	  for (int i = 0; i < testSet.length; i++) {
		newTestingSet[i] = testSet[i];

	  }
	  ptt.testSet = newTestingSet;
	}

	return ptt;
  }

  public TableFactory getTableFactory() {
	return original.getTableFactory();
  }

  public ExampleTable getExampleTable() {
	//return (PagingTestTable)this.copy();
	return this;
  }

  /*public PredictionTable toPredictionTable() {
	 //return (PagingTestTable)this.copy();
	  return this;
	  }*/

///////////////////////////////////////////////////////////
  /*
	 public void setStringPrediction(String prediction, int row, int predictionColIdx) {
		// setString(prediction, row, predictionSet[predictionColIdx]);
		// predictionColumnsTable.setString(prediction, testSet[row], predictionColIdx);
		predictionColumnsTable.setString(prediction, row, predictionColIdx);
		//System.out.println("||| " + predictionColumnsTable.getString(row, predictionColIdx));
	 }
	 public String getStringPrediction(int row, int predictionColIdx) {
		// return getString(row, predictionSet[predictionColIdx]);
		// return predictionColumnsTable.getString(testSet[row], predictionColIdx);
		return predictionColumnsTable.getString(row, predictionColIdx);
	 }
   */

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
	for (int i = 0; i < len; i++) {
	  removeRow(start);
	}
  }

  public void removeRowsByFlag(boolean[] flags) {
	int offset = 0;
	for (int i = 0; i < flags.length; i++) {
	  if (flags[i]) {
		removeRow(i - offset++);
	  }
	}
  }

  public void removeRowsByIndex(int[] indices) {
	int offset = 0;
	for (int i = 0; i < indices.length; i++) {
	  removeRow(indices[i] - offset++);
	}
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
	for (int i = 0; i < testSet.length; i++) {
	  newTestingSet[i] = testSet[i];
	}
	newTestingSet[testSet.length] = super.getNumRows();

	testSet = newTestingSet;

  }

  private void insertTesting(int tablePosition) {

	for (int i = 0; i < testSet.length; i++) {
	  if (testSet[i] >= tablePosition) {
		testSet[i]++;

	  }
	}
	int[] newTestingSet = new int[testSet.length + 1];
	for (int i = 0; i < testSet.length; i++) {
	  newTestingSet[i] = testSet[i];
	}
	newTestingSet[testSet.length] = tablePosition;

	Arrays.sort(newTestingSet);

	testSet = newTestingSet;

  }

  private void removeTesting(int testingPosition) {

	int[] newTestingSet = new int[testSet.length - 1];
	for (int i = 0; i < testingPosition; i++) {
	  newTestingSet[i] = testSet[i];

	}
	for (int i = testingPosition + 1; i < testSet.length; i++) {
	  newTestingSet[i - 1] = testSet[i] - 1;

	}
	testSet = newTestingSet;

  }

  public void setColumn(char[] data, int pos) {
	 //CharColumn cc = new CharColumn(data);
	 //setColumn(cc, pos);
	 for(int i = 0; i < data.length; i++) {
	   setChar(data[i], i,  pos);
	 }
  }
  public void setColumn(byte[] data, int pos) {
	 //ByteColumn bc = new ByteColumn(data);
	 //setColumn(bc, pos);
	 for(int i = 0; i < data.length; i++) {
	   setByte(data[i], i,  pos);
	 }
  }

  public void setColumn(int[] data, int pos) {
	 //IntColumn ic = new IntColumn(data);
	 //setColumn(ic, pos);
	 for(int i = 0; i < data.length; i++) {
	   setInt(data[i], i,  pos);
	 }
  }
  public void setColumn(float[] data, int pos) {
	 //FloatColumn ic = new FloatColumn(data);
	 //setColumn(ic, pos);
	 for(int i = 0; i < data.length; i++) {
	   setFloat(data[i], i,  pos);
	 }
  }
  public void setColumn(double[] data, int pos) {
	 //DoubleColumn ic = new DoubleColumn(data);
	 //setColumn(ic, pos);
	 for(int i = 0; i < data.length; i++) {
	   setDouble(data[i], i,  pos);
	 }
  }
  public void setColumn(long[] data, int pos) {
	 //LongColumn ic = new LongColumn(data);
	 //setColumn(ic, pos);
	 for(int i = 0; i < data.length; i++) {
	   setDouble(data[i], i,  pos);
	 }
  }
  public void setColumn(short[] data, int pos) {
	 //ShortColumn ic = new ShortColumn(data);
	 //setColumn(ic, pos);
	 for(int i = 0; i < data.length; i++) {
	   setShort(data[i], i,  pos);
	 }
  }
  public void setColumn(String[] data, int pos) {
	 //StringColumn ic = new StringColumn(data);
	 //setColumn(ic, pos);
	 for(int i = 0; i < data.length; i++) {
	   setString(data[i], i,  pos);
	 }
  }
  public void setColumn(byte[][] data, int pos) {
	 //ContinuousByteArrayColumn bc = new ContinuousByteArrayColumn(data);
	 //setColumn(bc, pos);
	 for(int i = 0; i < data.length; i++) {
	   setBytes(data[i], i,  pos);
	 }
  }
  public void setColumn(char[][] data, int pos) {
	 //ContinuousCharArrayColumn cc = new ContinuousCharArrayColumn(data);
	 //setColumn(cc, pos);
	 for(int i = 0; i < data.length; i++) {
	   setChars(data[i], i,  pos);
	 }
  }
  public void setColumn(Object[] data, int pos) {
	 //ObjectColumn ic = new ObjectColumn(data);
	 //setColumn(ic, pos);
	 for(int i = 0; i < data.length; i++) {
	   setObject(data[i], i,  pos);
	 }
  }
  public void setColumn(boolean[] data, int pos) {
	 //BooleanColumn ic = new BooleanColumn(data);
	 //ic.setLabel(getColumnLabel(pos));
	 //ic.setComment(getColumnComment(pos));
	 //setColumn(ic, pos);
	 for(int i = 0; i < data.length; i++) {
	   setBoolean(data[i], i,  pos);
	 }
  }

}