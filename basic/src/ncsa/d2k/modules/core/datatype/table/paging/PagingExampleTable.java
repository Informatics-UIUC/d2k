package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;

// This class must be package-protected, as the constructor requires an array of
// Pages and a PageManager, constructs which are only available to this package.

/**
 * <code>PagingExampleTable</code> provides a functionally paging
 * <code>ExampleTable</code> implementation.
 */
class PagingExampleTable extends MutablePagingTable implements ExampleTable {
	static final long serialVersionUID = 3690312945290834403L;

   protected int[] inputFeatures;
   protected int[] outputFeatures;

   protected int[] testSet;
   protected int[] trainSet;

   private String[] inputNames;
   private String[] outputNames;

   //ArrayList transformations; // is this going to stay?...

   //protected PagingExampleTable() { }

   /**
	* Constructs a <code>PagingExampleTable</code> that shares an existing
	* paging infrastructure (including the same data on disk).
	*
	* @param pages           existing <code>Page</code>s
	* @param manager         an existing <code>PageManager</code>
	*/
   PagingExampleTable(Page[] pages, PageManager manager) {
	  super(pages, manager);
	//  transformations = new ArrayList();
   }

   protected int[] copyIntArray(int[] ar) {
	  if (ar == null)
		 return null;
	int[] retVal = new int[ar.length];
	for(int i = 0; i < retVal.length; i++)
		retVal[i] = ar[i];
	return retVal;
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

	  /*
	  System.out.println("writing capacity " + this + " " + managerCapacity);

	  out.defaultWriteObject();
	  */

   }

   public Table copy() {

	  Page[] newPages = new Page[pages.length],
			 newInitialPages = new Page[manager.capacity];

	  try {

		 manager.globalLock.acquireRead();

		 for (int i = 0; i < pages.length; i++) {

			boolean correct = false;
			do {

			   Lock lock = manager.request(pages[i]);

			   lock.acquireWrite();

				  correct = manager.check(pages[i], lock);

				  if (correct) {

					 if (i < newInitialPages.length) {
						newPages[i] = pages[i].copy(true);
						newInitialPages[i] = newPages[i];
					 }
					 else {
						newPages[i] = pages[i].copy(false);
					 }

				  }

			   lock.releaseWrite();

			} while (!correct);

		 }

		 manager.globalLock.releaseRead();

	  }
	  catch(InterruptedException e) { e.printStackTrace(); }

	  PagingExampleTable pet = new PagingExampleTable(newPages, new PageManager(newPages, newInitialPages));
	  int[] ar = copyIntArray(getInputFeatures());
	  if(ar != null)
		pet.setInputFeatures(ar);
	  else
		pet.setInputFeatures(new int[0]);
	  ar = copyIntArray(getOutputFeatures());
	  if(ar != null)
		pet.setOutputFeatures(ar);
	  else
		pet.setOutputFeatures(new int[0]);
	  ar = copyIntArray(getTestingSet());
	  if(ar != null)
		pet.setTestingSet(ar);
	  else
		pet.setTestingSet(new int[0]);
	  ar = copyIntArray(getTrainingSet());
	  if(ar != null)
		pet.setTrainingSet(ar);
	  else
		pet.setTrainingSet(new int[0]);
	  return pet;
   }


  public Table getSubset(int pos, int len) {
	Table t = super.getSubset(pos, len);
	ExampleTable et  = t.toExampleTable();

	int[] newin = new int[inputFeatures.length];
	System.arraycopy(inputFeatures, 0, newin, 0, inputFeatures.length);
	int[] newout = new int[outputFeatures.length];
	System.arraycopy(outputFeatures, 0, newout, 0, outputFeatures.length);

	et.setInputFeatures(newin);
	et.setOutputFeatures(newout);

	// now figure out the test and train sets
	int[] traincpy = new int[trainSet.length];
	System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
	int[] testcpy = new int[testSet.length];
	System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

	int[] newtrain = subsetTrainOrTest(traincpy, pos, len);
	int[] newtest = subsetTrainOrTest(testcpy, pos, len);

	et.setTrainingSet(newtrain);
	et.setTestingSet(newtest);

	return et;
  }


  public Table getSubset(int[] rows) {
	Table t = super.getSubset(rows);
	ExampleTable et = t.toExampleTable();

	int[] newin = new int[inputFeatures.length];
	System.arraycopy(inputFeatures, 0, newin, 0, inputFeatures.length);
	int[] newout = new int[outputFeatures.length];
	System.arraycopy(outputFeatures, 0, newout, 0, outputFeatures.length);

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

	return et;

  }

  public Table getSubsetByReference(int pos, int len) {
	Table t = super.getSubsetByReference(pos, len);
	ExampleTable et  = t.toExampleTable();

	int[] newin = new int[inputFeatures.length];
	System.arraycopy(inputFeatures, 0, newin, 0, inputFeatures.length);
	int[] newout = new int[outputFeatures.length];
	System.arraycopy(outputFeatures, 0, newout, 0, outputFeatures.length);

	et.setInputFeatures(newin);
	et.setOutputFeatures(newout);

	// now figure out the test and train sets
	int[] traincpy = new int[trainSet.length];
	System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
	int[] testcpy = new int[testSet.length];
	System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

	int[] newtrain = subsetTrainOrTest(traincpy, pos, len);
	int[] newtest = subsetTrainOrTest(testcpy, pos, len);

	et.setTrainingSet(newtrain);
	et.setTestingSet(newtest);

	return et;
  }

  public Table getSubsetByReference(int[] rows) {
	Table t = super.getSubsetByReference(rows);
	ExampleTable et = t.toExampleTable();
	int[] newin = new int[inputFeatures.length];
	System.arraycopy(inputFeatures, 0, newin, 0, inputFeatures.length);
	int[] newout = new int[outputFeatures.length];
	System.arraycopy(outputFeatures, 0, newout, 0, outputFeatures.length);

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
	return et;
  }

  /**
   * Make a subset of the train or test set.  The subset will only contain
   * the indices that are included in rows.  The indices in the returned value
   * are numbered so that zero corresponds to rows[0].
   *
   * @param ts
   * @param rows
   * @return
   */
  protected static int[] subsetTrainOrTest(int[] ts, int[] rows) {
	// put all the indices of ts into a set
	HashSet oldset = new HashSet();
	for(int i = 0; i < ts.length; i++)
	  oldset.add(new Integer(ts[i]));

	// create a list to hold the new indices
	List newset = new ArrayList();
	// for each row
	for(int i = 0; i < rows.length; i++) {
	  // look up the value of the row in oldset
	  Integer ii = new Integer(rows[i]);
	  if(oldset.contains(ii)) {
		// if it was contained, add i to the newset
		newset.add(new Integer(i));
	  }
	}

	// copy all the values into an int array
	int[] retVal = new int[newset.size()];
	for(int i = 0; i < retVal.length; i++) {
	  Integer ii = (Integer)newset.get(i);
	  retVal[i] = ii.intValue();
	}

	return retVal;
  }

  /**
   * Make a subset of the train or test set.  The subset will only contain
   * the indices that are between start and len.  The value returned has the
   * indices scaled so that start corresponds to row zero.
   *
   * @param ts
   * @param start
   * @param len
   * @return
   */
  protected static int[] subsetTrainOrTest(int[] ts, int start, int len) {

/*    System.out.println("SUBSET: "+start+" "+len);

	Arrays.sort(ts);
	int startidx;
	int endidx;

	// find the beginning
	int ctr = 0;
	while(ts[ctr] < start)
	  ctr++;

	// find the end
	startidx = ctr;
	while( (ctr < ts.length) && (ts[ctr] < (start+len-1)) ) {
	  ctr++;
	}

	// there were 0 valid entries in this set
	if(ctr == startidx) {
	  return new int[0];
	}

	// ctr is incremented one too many times
	ctr--;

	endidx = ctr;
	if(endidx < startidx)
	  endidx = startidx;

	System.out.println("SI: "+startidx+" EI: "+endidx);

	// copy
	int length = endidx-startidx+1;
	int[] newSet = new int[length];
	System.arraycopy(ts, startidx, newSet, 0, length);

	// subtract start from each entry
	for(int i = 0; i < newSet.length; i++)
	  newSet[i] -= start;
	  */

	HashSet oldset = new HashSet();
	for(int i = 0; i< ts.length; i++)
		oldset.add(new Integer(ts[i]));

	List newset = new ArrayList();
	for(int i = start; i < start+len; i++) {
		if(oldset.contains(new Integer(i))) {
			newset.add(new Integer(i-start));
		}
	}

	// copy all the values into an int array
	int[] retVal = new int[newset.size()];
	for(int i = 0; i < retVal.length; i++) {
	  Integer ii = (Integer)newset.get(i);
	  retVal[i] = ii.intValue();
	}

	return retVal;
  }


/******************************************************************************/
/* interface ExampleTable                                                     */
/******************************************************************************/

   /*public void addTransformation(Transformation tm) {
	  transformations.add(tm);
   }*/

   public int[] getInputFeatures() {
	  return inputFeatures;
   }

   public int getNumExamples() {
	  return getNumRows();
   }

   public int getNumInputFeatures() {
	  if (inputFeatures == null)
		 return 0;
	  return inputFeatures.length;
   }

   public int getNumOutputFeatures() {
	  if (outputFeatures == null)
		 return 0;
	  return outputFeatures.length;
   }

   public int getNumTestExamples() {
	  if (testSet == null)
		 return 0;
	  return testSet.length;
   }

   public int getNumTrainExamples() {
	  if (trainSet == null)
		 return 0;
	  return trainSet.length;
   }

   public int[] getOutputFeatures() {
	  return outputFeatures;
   }

   public int[] getTestingSet() {
	  return testSet;
   }

   public TestTable getTestTable() {
	  return new PagingTestTable(this, pages, manager);
   }

   public int[] getTrainingSet() {
	  return trainSet;
   }

   public TrainTable getTrainTable() {
	  return new PagingTrainTable(this, pages, manager);
   }

   /*public ArrayList getTransformations() {
	  return transformations;
   }*/

   public void setInputFeatures(int[] inputs) {
	  inputFeatures = inputs;
	  if(inputs != null) {
		inputNames = new String[inputs.length];
		for (int i = 0; i < inputNames.length; i++)
		  inputNames[i] = getColumnLabel(inputs[i]);
	  }
	  else
		inputNames = new String[0];
   }

   public void setOutputFeatures(int[] outputs) {
	  outputFeatures = outputs;
	  if(outputs != null) {
		outputNames = new String[outputs.length];
		for (int i = 0; i < outputNames.length; i++)
		  outputNames[i] = getColumnLabel(outputs[i]);
	  }
	  else
		outputNames = new String[0];
   }

   public void setTestingSet(int[] testingSet) {
	  testSet = testingSet;
	  if(testSet != null)
		Arrays.sort(testSet);
   }

   public void setTrainingSet(int[] trainingSet) {
	  trainSet = trainingSet;
	  if(trainSet != null)
		Arrays.sort(trainSet);
   }

   /**
	* This is an example table
	* @return the example table.
	*/
   public ExampleTable toExampleTable() {
	  return this;
   }
   public PredictionTable toPredictionTable() {
	  return new PagingPredictionTable(this, pages, manager);
   }

/*   public ExampleTable toExampleTable() {
	return this;
   }
   */
   public double getInputDouble(int e, int i) {
	   return getDouble(e, inputFeatures[i]);
   }

   public double getOutputDouble(int e, int o) {
	   return getDouble(e, outputFeatures[o]);
   }
   public String getInputString(int e, int i) {
	   return getString(e, inputFeatures[i]);
   }
   public String getOutputString(int e, int o) {
	   return getString(e, outputFeatures[o]);
   }
   public int getInputInt(int e, int i) {
	   return getInt(e, inputFeatures[i]);
   }
   public int getOutputInt(int e, int o) {
	   return getInt(e, outputFeatures[o]);
   }
   public float getInputFloat(int e, int i) {
	   return getFloat(e, inputFeatures[i]);
   }
   public float getOutputFloat(int e, int o) {
	   return getFloat(e, outputFeatures[o]);
   }
   public short getInputShort(int e, int i) {
	   return getShort(e, inputFeatures[i]);
   }
   public short getOutputShort(int e, int o) {
	   return getShort(e, outputFeatures[o]);
   }
   public long getInputLong(int e, int i) {
	   return getLong(e, inputFeatures[i]);
   }
   public long getOutputLong(int e, int o) {
	   return getLong(e, outputFeatures[o]);
   }
   public byte getInputByte(int e, int i) {
	   return getByte(e, inputFeatures[i]);
   }
   public byte getOutputByte(int e, int o) {
	   return getByte(e, outputFeatures[o]);
   }
   public Object getInputObject(int e, int i) {
	   return getObject(e, inputFeatures[i]);
   }
   public Object getOutputObject(int e, int o) {
	   return getObject(e, outputFeatures[o]);
   }
   public char getInputChar(int e, int i) {
	   return getChar(e, inputFeatures[i]);
   }
   public char getOutputChar(int e, int o) {
	   return getChar(e, outputFeatures[o]);
   }
   public char[] getInputChars(int e, int i) {
	   return getChars(e, inputFeatures[i]);
   }
   public char[] getOutputChars(int e, int o) {
	   return getChars(e, inputFeatures[o]);
   }
   public byte[] getInputBytes(int e, int i) {
	   return getBytes(e, inputFeatures[i]);
   }
   public byte[] getOutputBytes(int e, int o) {
	   return getBytes(e, outputFeatures[o]);
   }
   public boolean getInputBoolean(int e, int i) {
	   return getBoolean(e, inputFeatures[i]);
   }
   public boolean getOutputBoolean(int e, int o) {
	   return getBoolean(e, outputFeatures[o]);
   }
   public int getNumInputs(int e) {
	   return inputFeatures.length;
   }
   public int getNumOutputs(int e) {
	   return outputFeatures.length;
   }

   public Example getExample(int i) { return null; }

   public String getInputName(int i) {
	   return getColumnLabel(inputFeatures[i]);
   }
   public String getOutputName(int o) {
	   return getColumnLabel(outputFeatures[o]);
   }
   public int getInputType(int i) {
	   return getColumnType(inputFeatures[i]);
   }
   public int getOutputType(int o) {
	   return getColumnType(outputFeatures[o]);
   }
   public boolean isInputNominal(int i) {
	   return isColumnNominal(inputFeatures[i]);
   }
   public boolean isOutputNominal(int o) {
	   return isColumnNominal(outputFeatures[o]);
   }
   public boolean isInputScalar(int i) {
	   return isColumnScalar(inputFeatures[i]);
   }
   public boolean isOutputScalar(int o) {
	   return isColumnScalar(outputFeatures[o]);
	}

	public String[] getInputNames() {
	  return inputNames;
	}

	public String[] getOutputNames() {
	  return outputNames;
	}

	// MutableTable support

	/**
	 * Insert a new row into this Table, initialized with integer data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(int[] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with float data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(float[] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with double data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(double[] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with long data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(long[] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with short data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(short[] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with boolean data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(boolean[] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with String data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(String[] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with char[] data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(char[][] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with byte[] data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(byte[][] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with Object data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(Object[] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with byte data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(byte[] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new row into this Table, initialized with char data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(char[] newEntry, int position) {
	  //insertTraining(trainSet[position]);
	  incrementTrainTest(position);
	  super.insertRow(newEntry, trainSet[position]);
	}

	/**
	 * Insert a new column into this Table, initialized with integer data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(int[] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with float data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(float[] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with double data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(double[] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with long data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(long[] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with short data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(short[] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with boolean data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(boolean[] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with String data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(String[] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with char[] data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(char[][] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with byte[] data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(byte[][] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with Object data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(Object[] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with byte data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(byte[] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
	 * Insert a new column into this Table, initialized with char data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(char[] newEntry, int position) {
	  this.incrementInOut(position);
	  super.insertColumn(newEntry, position);
	}

	/**
		 Remove a column from the table.
		 @param position the position of the Column to remove
	 */
	public void removeColumn(int position) {
	  decrementInOut(position);
	  super.removeColumn(position);
	}

	/**
	 Remove a range of columns from the table.
	 @param start the start position of the range to remove
	 @param len the number to remove-the length of the range
	 */
	public void removeColumns(int start, int len) {
	  for (int i = start; i < len; i++) {
		removeColumn(i);
	  }
	}

	/**
	 * Remove a row from this Table.
	 * @param row the row to remove
	 */
	public void removeRow(int row) {
	  decrementTrainTest(row);
	  super.removeRow(row);
	}

	/**
	 Remove a range of rows from the table.
	 @param start the start position of the range to remove
	 @param len the number to remove-the length of the range
	 */
	public void removeRows(int start, int len) {
	  for (int i = start; i < len; i++) {
		removeRow(i);
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
		  removeRow(i - numRemoved);
		}
	  }
	}

	/**
	 * Remove rows from this Table using a boolean map.
	 * @param flags an array of booleans to map to this Table's rows.  Those
	 * with a true will be removed, all others will not be removed
	 */
	public void removeColumnsByFlag(boolean[] flags) {
	  int numRemoved = 0;
	  for (int i = 0; i < flags.length; i++) {
		if (flags[i]) {
		  removeColumn(i - numRemoved);
		}
	  }
	}

	/**
	 * Remove rows from this Table by index.
	 * @param indices a list of the rows to remove
	 */
	public void removeRowsByIndex(int[] indices) {
	  for (int i = 0; i < indices.length; i++) {
		removeRow(indices[i] - i);
	  }
	}

	/**
	 * Remove rows from this Table by index.
	 * @param indices a list of the rows to remove
	 */
	public void removeColumnsByIndex(int[] indices) {
	  for (int i = 0; i < indices.length; i++) {
		removeColumn(indices[i] - i);
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
	  super.swapRows(pos1, pos2);
	  this.swapTestTrain(pos1, pos2);
	}

	/**
	 Swap the positions of two columns.
	 @param pos1 the first column to swap
	 @param pos2 the second column to swap
	 */
	public void swapColumns(int pos1, int pos2) {
	  super.swapColumns(pos1, pos2);
	  this.swapInOut(pos1, pos2);
	}

	/**
	 Set a specified element in the table.  If an element exists at this
		 position already, it will be replaced.  If the position is beyond the capacity
	 of this table then an ArrayIndexOutOfBounds will be thrown.
	 @param element the new element to be set in the table
	 @param row the row to be changed in the table
	 @param column the Column to be set in the given row
	  /
	  public void setObject(Object element, int row, int column) {
		   columns[column].setObject(element, trainSet[row]);
	   }
	   /**
	  * Set an int value in the table.
	  * @param data the value to set
	  * @param row the row of the table
	  * @param column the column of the table
			/
		  public void setInt(int data, int row, int column) {
			   columns[column].setInt(data, trainSet[row]);
		   }
		   /**
	   * Set a short value in the table.
	   * @param data the value to set
	   * @param row the row of the table
	   * @param column the column of the table
				/
			  public void setShort(short data, int row, int column) {
				   columns[column].setShort(data, trainSet[row]);
			   }
			   /**
		* Set a float value in the table.
		* @param data the value to set
		* @param row the row of the table
		* @param column the column of the table
					/
				  public void setFloat(float data, int row, int column) {
					   columns[column].setFloat(data, trainSet[row]);
				   }
				   /**
		 * Set an double value in the table.
		 * @param data the value to set
		 * @param row the row of the table
		 * @param column the column of the table
						/
					  public void setDouble(double data, int row, int column) {
						   columns[column].setDouble(data, trainSet[row]);
					   }
					   /**
		  * Set a long value in the table.
		  * @param data the value to set
		  * @param row the row of the table
		  * @param column the column of the table
							/
						  public void setLong(long data, int row, int column) {
							   columns[column].setLong(data, trainSet[row]);
						   }
						   /**
		   * Set a String value in the table.
		   * @param data the value to set
		   * @param row the row of the table
		   * @param column the column of the table
								/
			   public void setString(String data, int row, int column) {
			   columns[column].setString(data, trainSet[row]);
							   }
							   /**
			* Set a byte[] value in the table.
			* @param data the value to set
			* @param row the row of the table
			* @param column the column of the table
									/
					   public void setBytes(byte[] data, int row, int column) {
				columns[column].setBytes(data, trainSet[row]);
								   }
								   /**
			 * Set a boolean value in the table.
			 * @param data the value to set
			 * @param row the row of the table
			 * @param column the column of the table
										/
				 public void setBoolean(boolean data, int row, int column) {
						 columns[column].setBoolean(data, trainSet[row]);
									   }
									   /**
			  * Set a char[] value in the table.
			  * @param data the value to set
			  * @param row the row of the table
			  * @param column the column of the table
											/
				  public void setChars(char[] data, int row, int column) {
						   columns[column].setChars(data, trainSet[row]);
										   }
										   /**
			   * Set a byte value in the table.
			   * @param data the value to set
			   * @param row the row of the table
			   * @param column the column of the table
												/
				   public void setByte(byte data, int row, int column) {
							 columns[column].setByte(data, trainSet[row]);
											   }
											   /**
				* Set a char value in the table.
				* @param data the value to set
				* @param row the row of the table
				* @param column the column of the table
													/
					public void setChar(char data, int row, int column) {
							   columns[column].setChar(data, trainSet[row]);
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

	/////////// Collect the transformations that were performed. /////////
	/**
	 Add the transformation to the list.
	 @param tm the Transformation that performed the reversable transform.
	 /
		 public void addTransformation (Transformation tm);
		 /**
		 Returns the list of all reversable transformations there were performed
		 on the original dataset.
		  @returns an ArrayList containing the Transformation which transformed the data.
		 /
			 public List getTransformations ();
		  /**
	   * Set the value at (row, col) to be missing or not missing.
	   * @param b true if the value should be set as missing, false otherwise
	   * @param row the row index
	   * @param col the column index
			   /
			  public void setValueToMissing(boolean b, int row, int col) {
				columns[col].setValueToMissing(b, trainSet[row]);
				 }
			  /**
		* Set the value at (row, col) to be empty or not empty.
		* @param b true if the value should be set as empty, false otherwise
		* @param row the row index
		* @param col the column index
				   /
				  public void setValueToEmpty(boolean b, int row, int col) {
					columns[col].setValueToEmpty(b, trainSet[row]);
					 }

		/**
		 * Increment all in and out indices greater than position
		 */
		protected void incrementInOut(int position) {
		  for (int i = 0; i < this.inputFeatures.length; i++) {
			if (inputFeatures[i] > position) {
			  inputFeatures[i]++;
			}
		  }
		  setInputFeatures(inputFeatures);
		  for (int i = 0; i < this.outputFeatures.length; i++) {
			if (outputFeatures[i] > position) {
			  outputFeatures[i]++;
			}
		  }
		  setOutputFeatures(outputFeatures);
		}

	/**
	 * Increment all test and train indices greater than position
	 */
	protected void incrementTrainTest(int position) {
	  for (int i = 0; i < this.trainSet.length; i++) {
		if (trainSet[i] > position) {
		  trainSet[i]++;
		}
	  }
	  setTrainingSet(trainSet);
	  for (int i = 0; i < this.testSet.length; i++) {
		if (testSet[i] > position) {
		  testSet[i]++;
		}
	  }
	  setTestingSet(testSet);
	}

	/**
	 * Decrement any items in test or train that are greater than position
	 * Also remove position from either set if it exists
	 * @param position
	 */
	protected void decrementTrainTest(int position) {
	  boolean containsPos = false;
	  int idx = -1;
	  for (int i = 0; i < testSet.length; i++) {
		if (testSet[i] == position) {
		  containsPos = true;
		  idx = i;
		}
		if (containsPos) {
		  break;
		}
	  }

	  // if the test set contained pos, remove the item
	  if (containsPos) {
		int[] newtest = new int[testSet.length - 1];
		int idd = 0;
		for (int i = 0; i < testSet.length; i++) {
		  if (i != idx) {
			newtest[idd] = testSet[i];
			idd++;
		  }
		}
		setTestingSet(newtest);
	  }

	  containsPos = false;
	  idx = -1;

	  for (int i = 0; i < trainSet.length; i++) {
		if (trainSet[i] == position) {
		  containsPos = true;
		  idx = i;
		}
		if (containsPos) {
		  break;
		}
	  }

	  // if the test set contained pos, remove the item
	  if (containsPos) {
		int[] newttrain = new int[trainSet.length - 1];
		int idd = 0;
		for (int i = 0; i < trainSet.length; i++) {
		  if (i != idx) {
			newttrain[idd] = trainSet[i];
			idd++;
		  }
		}
		setTrainingSet(newttrain);
	  }
	}

	/**
	 * Decrement any items in test or train that are greater than position
	 * Also remove position from either set if it exists
	 * @param position
	 */
	protected void decrementInOut(int position) {
	  boolean containsPos = false;
	  int idx = -1;
	  for (int i = 0; i < inputFeatures.length; i++) {
		if (inputFeatures[i] == position) {
		  containsPos = true;
		  idx = i;
		}
		if (containsPos) {
		  break;
		}
	  }

	  // if the test set contained pos, remove the item
	  if (containsPos) {
		int[] newin = new int[inputFeatures.length - 1];
		int idd = 0;
		for (int i = 0; i < inputFeatures.length; i++) {
		  if (i != idx) {
			newin[idd] = inputFeatures[i];
			idd++;
		  }
		}
		setInputFeatures(newin);
	  }

	  containsPos = false;
	  idx = -1;

	  for (int i = 0; i < outputFeatures.length; i++) {
		if (outputFeatures[i] == position) {
		  containsPos = true;
		  idx = i;
		}
		if (containsPos) {
		  break;
		}
	  }

	  // if the test set contained pos, remove the item
	  if (containsPos) {
		int[] newout = new int[outputFeatures.length - 1];
		int idd = 0;
		for (int i = 0; i < outputFeatures.length; i++) {
		  if (i != idx) {
			newout[idd] = outputFeatures[i];
			idd++;
		  }
		}
		setOutputFeatures(newout);
	  }
	}

	/**
	 * For every p1 in test/train, put in p2.
	 * For every p2 in test/train, put in p1.
	 */
	protected void swapTestTrain(int p1, int p2) {
	  for (int i = 0; i < trainSet.length; i++) {
		if (trainSet[i] == p1) {
		  trainSet[i] = p2;
		}
		else if (trainSet[i] == p2) {
		  trainSet[i] = p1;
		}
	  }
	  for (int i = 0; i < testSet.length; i++) {
		if (testSet[i] == p1) {
		  testSet[i] = p2;
		}
		else if (testSet[i] == p2) {
		  testSet[i] = p1;
		}
	  }
	}

	/**
	 * For every p1 in test/train, put in p2.
	 * For every p2 in test/train, put in p1.
	 */
	protected void swapInOut(int p1, int p2) {
	  for (int i = 0; i < inputFeatures.length; i++) {
		if (inputFeatures[i] == p1) {
		  inputFeatures[i] = p2;
		}
		else if (inputFeatures[i] == p2) {
		  inputFeatures[i] = p1;
		}
	  }
	  for (int i = 0; i < outputFeatures.length; i++) {
		if (outputFeatures[i] == p1) {
		  outputFeatures[i] = p2;
		}
		else if (outputFeatures[i] == p2) {
		  outputFeatures[i] = p1;
		}
	  }
	}

	/**
	 * Drop any input/output columns greater than pos
	 */
	protected void dropInOut(int pos) {
	  int numOk = 0;
	  for (int i = 0; i < inputFeatures.length; i++) {
		if (inputFeatures[i] < pos) {
		  numOk++;
		}
	  }
	  if (numOk != inputFeatures.length) {
		int[] newin = new int[numOk];
		int idx = 0;
		for (int i = 0; i < inputFeatures.length; i++) {
		  int num = inputFeatures[i];
		  if (num < pos) {
			newin[idx] = num;
			idx++;
		  }
		}
		setInputFeatures(newin);
	  }

	  numOk = 0;
	  for (int i = 0; i < outputFeatures.length; i++) {
		if (outputFeatures[i] < pos) {
		  numOk++;
		}
	  }
	  if (numOk != outputFeatures.length) {
		int[] newout = new int[numOk];
		int idx = 0;
		for (int i = 0; i < outputFeatures.length; i++) {
		  int num = outputFeatures[i];
		  if (num < pos) {
			newout[idx] = num;
			idx++;
		  }
		}
		setOutputFeatures(newout);
	  }
	}

	/**
	 * Drop any input/output columns greater than pos
	 */
	protected void dropTestTrain(int pos) {
	  int numOk = 0;
	  for (int i = 0; i < testSet.length; i++) {
		if (testSet[i] < pos) {
		  numOk++;
		}
	  }
	  if (numOk != testSet.length) {
		int[] newtest = new int[numOk];
		int idx = 0;
		for (int i = 0; i < testSet.length; i++) {
		  int num = testSet[i];
		  if (num < pos) {
			newtest[idx] = num;
			idx++;
		  }
		}
		setTestingSet(newtest);
	  }

	  numOk = 0;
	  for (int i = 0; i < trainSet.length; i++) {
		if (trainSet[i] < pos) {
		  numOk++;
		}
	  }
	  if (numOk != trainSet.length) {
		int[] newtrain = new int[numOk];
		int idx = 0;
		for (int i = 0; i < trainSet.length; i++) {
		  int num = trainSet[i];
		  if (num < pos) {
			newtrain[idx] = num;
			idx++;
		  }
		}
		setTrainingSet(newtrain);
	  }
	}

}
