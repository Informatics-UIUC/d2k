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
class PagingExampleTable extends PagingTable implements ExampleTable {
	static final long serialVersionUID = 3690312945290834403L;

   protected int[] inputFeatures;
   protected int[] outputFeatures;

   protected int[] testSet;
   protected int[] trainSet;

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
   }

   public void setOutputFeatures(int[] outputs) {
      outputFeatures = outputs;
   }

   public void setTestingSet(int[] testingSet) {
      testSet = testingSet;
      Arrays.sort(testSet);
   }

   public void setTrainingSet(int[] trainingSet) {
      trainSet = trainingSet;
      Arrays.sort(trainSet);
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

}
