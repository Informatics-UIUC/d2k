package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;
import ncsa.d2k.modules.core.datatype.table.*;

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

}
