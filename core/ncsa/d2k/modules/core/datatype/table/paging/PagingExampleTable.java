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
class PagingExampleTable extends PagingTable
   implements ExampleTable {

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
   public PagingExampleTable(Page[] pages, PageManager manager) {
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

   public ExampleTable toExampleTable() {
    return this;
   }

}
