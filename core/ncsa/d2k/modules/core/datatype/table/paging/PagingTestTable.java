package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;
import ncsa.d2k.modules.core.datatype.table.*;

class PagingTestTable extends PagingPredictionTable
   implements TestTable {

   PagingTestTable(PagingExampleTable pet, Page[] pgs, PageManager pm) {
      super(pet, pgs, pm);
     //testSet = pet.getTestingSet();
      //testSet = testSet;
      //testSet = copyIntArray(pet.getTestingSet());

      //inputFeatures = pet.getInputFeatures();
      //outputFeatures = pet.getOutputFeatures();
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

      predictionSet = (int[])in.readObject();
      predictionColumnsTable = (MutablePagingTable)in.readObject();
      indirection = (int[])in.readObject();
      prediction = (boolean[])in.readObject();
      original = (PagingTable)in.readObject();
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
      if (prediction[column])
         return predictionColumnsTable.getObject(row, indirection[column]);
      else
         // return original.getObject(testSet[row], indirection[column]);
         return super.getObject(row, indirection[column]);
   }

   public int getInt(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getInt(row, indirection[column]);
      else
         // return original.getInt(testSet[row], indirection[column]);
         return super.getInt(row, indirection[column]);
   }

   public short getShort(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getShort(row, indirection[column]);
      else
         // return original.getShort(testSet[row], indirection[column]);
         return super.getShort(row, indirection[column]);
   }

   public float getFloat(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getFloat(row, indirection[column]);
      else
         // return original.getFloat(testSet[row], indirection[column]);
         return super.getFloat(row, indirection[column]);
   }

   public double getDouble(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getDouble(row, indirection[column]);
      else
         // return original.getDouble(testSet[row], indirection[column]);
         return super.getDouble(row, indirection[column]);
   }

   public long getLong(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getLong(row, indirection[column]);
      else
         // return original.getLong(testSet[row], indirection[column]);
         return super.getLong(row, indirection[column]);
   }

   public String getString(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getString(row, indirection[column]);
      else
         // return original.getString(testSet[row], indirection[column]);
         return super.getString(row, indirection[column]);
   }

   public byte[] getBytes(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getBytes(row, indirection[column]);
      else
         // return original.getBytes(testSet[row], indirection[column]);
         return super.getBytes(row, indirection[column]);
   }

   public boolean getBoolean(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getBoolean(row, indirection[column]);
      else
         // return original.getBoolean(testSet[row], indirection[column]);
         return super.getBoolean(row, indirection[column]);
   }

   public char[] getChars(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getChars(row, indirection[column]);
      else
         // return original.getChars(testSet[row], indirection[column]);
         return super.getChars(row, indirection[column]);
   }

   public byte getByte(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getByte(row, indirection[column]);
      else
         // return original.getByte(testSet[row], indirection[column]);
         return super.getByte(row, indirection[column]);
   }

   public char getChar(int row, int column) {
      if (prediction[column])
         return predictionColumnsTable.getChar(row, indirection[column]);
      else
         // return original.getChar(testSet[row], indirection[column]);
         return super.getChar(row, indirection[column]);
   }

   public int getNumRows() {
      // return original.getNumRows();
      if (testSet == null) {
         return 0;
      }
      else
         return testSet.length;
   }

   public void getRow(Object buffer, int position) {
      original.getRow(buffer, testSet[position]);
   }

    public void getColumn(Object buffer, int position) {
      if (prediction[position])
         predictionColumnsTable.getColumn(buffer, indirection[position]);
      else
         original.getColumn(buffer, indirection[position]);
    }

   public Table getSubset(int start, int len) {
      PagingTestTable ptt =
         (PagingTestTable)((PagingExampleTable)((PagingTable)original.copy()).toExampleTable().getTestTable());
      ptt.predictionColumnsTable = (MutablePagingTable)predictionColumnsTable.copy();

      int[] newTestingSet = new int[len];
      for (int i = start; i < start + len; i++)
         newTestingSet[i - start] = testSet[i];

      ptt.testSet = newTestingSet;

      return ptt;
   }

   public Table copy() {
      PagingTestTable ptt =
         (PagingTestTable)((PagingExampleTable)((PagingTable)original.copy()).toExampleTable().getTestTable());
      ptt.predictionColumnsTable = (MutablePagingTable)predictionColumnsTable.copy();

      if (testSet != null) {
         int[] newTestingSet = new int[testSet.length];
         for (int i = 0; i < testSet.length; i++)
            newTestingSet[i] = testSet[i];

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

   public PredictionTable toPredictionTable() {
      //return (PagingTestTable)this.copy();
       return this;
   }

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
}
