package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class MutablePagingTable extends PagingTable implements MutableTable, Serializable {

	static final long serialVersionUID = 4882678549211681266L;

   public MutablePagingTable() {
      super();
   }

   public MutablePagingTable(Table table, int totalPages, int pagesInMemory) {
      super(table, totalPages, pagesInMemory);
   }

   MutablePagingTable(Page[] newPages, PageManager newManager) {
      super(newPages, newManager);
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

      /*
      System.out.println("writing capacity " + this + " " + managerCapacity);

      out.defaultWriteObject();
      */

   }

   public void addTable(Table newTable, boolean keepInMemory) {
       super.addTable(newTable, keepInMemory);
   }

/******************************************************************************/
/* interface Table                                                            */
/******************************************************************************/

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

      return new MutablePagingTable(newPages, new PageManager(newPages, newInitialPages));

   }

   public Table getSubset(int start, int len) {

      Table t = null;

      try{

         manager.globalLock.acquireRead();

         int page1 = getPageNumber(start),
             page2 = getPageNumber(start + len - 1);

         boolean correct;
         if (page1 == page2) { // easy...

            correct = false;
            do {

               Lock lock = manager.request(pages[page1]);

               lock.acquireRead();

                  correct = manager.check(pages[page1], lock);

                  if (correct) {

                     Page[] newPage = new Page[1];

                     File file = File.createTempFile("d2k-", null);
                     file.deleteOnExit();

                     newPage[0] = new Page(
                        file,
                        pages[page1].getSubset(start - offsets[page1], len).copy(),
                        true
                     );

                     t = new MutablePagingTable(newPage, new PageManager(newPage, newPage));

                  }

               lock.releaseRead();

            } while (!correct);

         }
         else { // hard(er)...

            Page[] newPages = new Page[page2 - page1 + 1];
            Page[] newInitialPages = new Page[manager.capacity];

            // get the part from page 1

            correct = false;
            do {

               Lock lock = manager.request(pages[page1]);

               lock.acquireRead();

                  correct = manager.check(pages[page1], lock);

                  if (correct) {

                     File file = File.createTempFile("d2k-", null);
                     file.deleteOnExit();

                     newPages[0] = new Page(
                        file,
                        pages[page1].getSubset(
                           start - offsets[page1],
                           pages[page1].getNumRows() - start + offsets[page1]
                        ),
                        true
                     );

                     newInitialPages[0] = newPages[0];

                  }

               lock.releaseRead();

            } while (!correct);

            // add any pages between page 1 and page 2

            for (int i = page1 + 1; i < page2; i++) {

               correct = false;
               do {

                  Lock lock = manager.request(pages[i]);

                  lock.acquireRead();

                     correct = manager.check(pages[i], lock);

                     if (correct) {

                        File file = File.createTempFile("d2k-", null);
                        file.deleteOnExit();

                        newPages[i - page1] = new Page(
                           file,
                           pages[i].getSubset(
                              0,
                              pages[i].getNumRows()
                           ),
                           i - page1 < newInitialPages.length
                        );

                        if (i - page1 < newInitialPages.length)
                           newInitialPages[i - page1] = newPages[i - page1];

                     }

                  lock.releaseRead();

               } while (!correct);

            }

            // get the part from page 2

            correct = false;
            do {

               Lock lock = manager.request(pages[page2]);

               lock.acquireRead();

                  correct = manager.check(pages[page2], lock);

                  if (correct) {

                     File file = File.createTempFile("d2k-", null);
                     file.deleteOnExit();

                     newPages[newPages.length - 1] = new Page(
                        file,
                        pages[page2].getSubset(
                           0,
                           (start + len - 1) - offsets[page2] + 1
                        ),
                        page2 - page1 < newInitialPages.length
                     );

                     if (page2 - page1 < newInitialPages.length)
                        newInitialPages[newInitialPages.length - 1] =
                           newPages[newPages.length - 1];

                  }

               lock.releaseRead();

            } while (!correct);

            // done!

            t = new MutablePagingTable(newPages, new PageManager(newPages, newInitialPages));

         }

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }
      catch(IOException e) { e.printStackTrace(); }

      return t;

   }

/******************************************************************************/
/* interface MutableTable                                                     */
/******************************************************************************/

   public void addColumn(boolean[] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     boolean[] toAdd = new boolean[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(byte[] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     byte[] toAdd = new byte[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(byte[][] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     byte[][] toAdd = new byte[rows][];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(char[] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     char[] toAdd = new char[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(char[][] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     char[][] toAdd = new char[rows][];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(double[] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     double[] toAdd = new double[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(float[] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     float[] toAdd = new float[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(int[] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     int[] toAdd = new int[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(long[] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     long[] toAdd = new long[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(Object[] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     Object[] toAdd = new Object[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(short[] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     short[] toAdd = new short[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addColumn(String[] newEntry) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     String[] toAdd = new String[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].addColumn(toAdd);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(boolean[] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(byte[] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(byte[][] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(char[] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(char[][] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(double[] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(float[] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(int[] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(long[] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(Object[] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(short[] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void addRow(String[] newEntry) {

      try {

         manager.globalLock.acquireWrite();

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pages.length - 1]);

            lock.acquireWrite();

               correct = manager.check(pages[pages.length - 1], lock);

               if (correct)
                  pages[pages.length - 1].addRow(newEntry);

            lock.releaseWrite();

         } while (!correct);

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(boolean[] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     boolean[] toAdd = new boolean[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(byte[] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     byte[] toAdd = new byte[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(byte[][] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     byte[][] toAdd = new byte[rows][];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(char[] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     char[] toAdd = new char[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(char[][] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     char[][] toAdd = new char[rows][];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(double[] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     double[] toAdd = new double[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(float[] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     float[] toAdd = new float[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(int[] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     int[] toAdd = new int[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(long[] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     long[] toAdd = new long[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(Object[] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     Object[] toAdd = new Object[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(short[] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     short[] toAdd = new short[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertColumn(String[] newEntry, int position) {

      int progress = 0;

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct) {

                     int rows = pages[i].getNumRows();
                     String[] toAdd = new String[rows];

                     for (int j = 0; j < rows; j++)
                        toAdd[j] = newEntry[progress + j];

                     pages[i].insertColumn(toAdd, position);

                     progress += rows;

                  }

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(boolean[] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(byte[] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(byte[][] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(char[] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(char[][] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(double[] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(float[] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(int[] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(long[] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(Object[] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(short[] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void insertRow(String[] newEntry, int position) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(position);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].insertRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]++;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void removeColumn(int position) {

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].removeColumn(position);

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void removeColumns(int start, int len) {

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].removeColumns(start, len);

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void removeColumnsByFlag(boolean[] flags) {

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].removeColumnsByFlag(flags);

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void removeColumnsByIndex(int[] indices) {

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].removeColumnsByIndex(indices);

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void removeRow(int row) {

      try {

         manager.globalLock.acquireWrite();

         int pageNum = getPageNumber(row);

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].removeRow(row - offsets[pageNum]);

            lock.releaseWrite();

         } while (!correct);

         for (int i = pageNum + 1; i < pages.length; i++)
            offsets[i]--;

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void removeRows(int start, int len) {

      try {

         manager.globalLock.acquireWrite();

         int page1 = getPageNumber(start),
             page2 = getPageNumber(start + len - 1);

         if (page1 == page2) { // easy...

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[page1]);

               lock.acquireWrite();

                  correct = manager.check(pages[page1], lock);

                  if (correct)
                     pages[page1].removeRows(start - offsets[page1], len);

               lock.releaseWrite();

            } while (!correct);

            for (int i = page1 + 1; i < offsets.length; i++)
               offsets[i] -= len;

         }
         else { // hard...

            int removeFromPage2 = start + len - offsets[page2]; // need this later

            // remove rows from page 1

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[page1]);

               lock.acquireWrite();

                  correct = manager.check(pages[page1], lock);

                  if (correct)
                     pages[page1].removeRows(start - offsets[page1], offsets[page1 + 1] - start);

               lock.releaseWrite();

            } while (!correct);

            int difference = offsets[page1 + 1] - start;
            for (int i = page1 + 1; i < offsets.length; i++)
               offsets[i] -= difference;

            // remove all rows from any pages in between page 1 and page 2

            for (int i = page1 + 1; i < page2; i++) {

               int numRemoved = 0;
               correct = false;
               do {

                  Lock lock = manager.request(pages[i]);

                  lock.acquireWrite();

                     correct = manager.check(pages[i], lock);

                     if (correct) {
                        numRemoved = pages[i].getNumRows();
                        pages[i].removeRows(0, numRemoved);
                     }

                  lock.releaseWrite();

               } while (!correct);

               for (int count = i + 1; count < offsets.length; count++)
                  offsets[count] -= numRemoved;

            }

            // remove rows from page 2

            correct = false;
            do {

               Lock lock = manager.request(pages[page2]);

               lock.acquireWrite();

                  correct = manager.check(pages[page2], lock);

                  if (correct)
                     pages[page2].removeRows(0, removeFromPage2);

               lock.releaseWrite();

            } while (!correct);

            for (int i = page2 + 1; i < offsets.length; i++)
               offsets[i] -= removeFromPage2;

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void removeRowsByFlag(boolean[] flags) {

      try {

         manager.globalLock.acquireWrite();

         boolean[][] splitFlags = new boolean[pages.length][];

         int[] numRemoved = new int[pages.length];
         for (int i = 0; i < pages.length; i++)
            numRemoved[i] = 0;

         // split flags appropriately

         int progress = 0;
         for (int i = 0; i < pages.length; i++) {

            splitFlags[i] = new boolean[pages[i].getNumRows()];

            for (int j = 0; j < splitFlags[i].length; j++) {
               splitFlags[i][j] = flags[progress++];
               if (splitFlags[i][j])
                  numRemoved[i]++;
            }

         }

         // remove and update offsets

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].removeRowsByFlag(splitFlags[i]);

               lock.releaseWrite();

            } while (!correct);

            for (int j = i + 1; j < offsets.length; j++)
               offsets[j] -= numRemoved[i];

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void removeRowsByIndex(int[] indices) {

      Arrays.sort(indices);

      try {

         manager.globalLock.acquireWrite();

         int pageNum, offset = 0;
         boolean correct;
         for (int i = 0; i < indices.length; i++) {

            pageNum = getPageNumber(indices[i] - offset);

            correct = false;
            do {

               Lock lock = manager.request(pages[pageNum]);

               lock.acquireWrite();

                  correct = manager.check(pages[pageNum], lock);

                  if (correct)
                     pages[pageNum].removeRow(indices[i] - offset++ - offsets[pageNum]);

               lock.releaseWrite();

            } while (!correct);

            for (int j = pageNum + 1; j < offsets.length; j++)
               offsets[j]--;

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public Table reorderColumns(int[] newOrder) {
      MutablePagingTable mpt = (MutablePagingTable)this.copy();
      mpt.internalReorderColumns(newOrder);
      return mpt;
   }

   void internalReorderColumns(int[] newOrder) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].reorderColumns(newOrder);

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public Table reorderRows(int[] newOrder) {

      MutablePagingTable mpt = null;

      try {

         manager.globalLock.acquireRead();

            mpt = (MutablePagingTable)this.copy();
            int numColumns = mpt.getNumColumns();

         mpt.manager.globalLock.acquireRead();

            Object[] swap = new Object[numColumns];
            for (int i = 0; i < newOrder.length; i++) {

               getRow(swap, newOrder[i]);
               mpt.setRow(swap, i);

            }

         mpt.manager.globalLock.releaseRead();
         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

      return mpt;

   }

   public void setBoolean(boolean data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setBoolean(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setByte(byte data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setByte(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setBytes(byte[] data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setBytes(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setChar(char data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setChar(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setChars(char[] data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setChars(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setColumn(boolean[] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setBoolean(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(byte[] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setByte(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(byte[][] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setBytes(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(char[] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setChar(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(char[][] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setChars(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(double[] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setDouble(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(float[] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setFloat(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(int[] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setInt(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(long[] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setLong(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(Object[] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setObject(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(short[] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setShort(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumn(String[] newEntry, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < newEntry.length; i++)
            setString(newEntry[i], i, position);

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumnComment(String comment, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].setColumnComment(comment, position);

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setColumnLabel(String comment, int position) {

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].setColumnLabel(comment, position);

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setDouble(double data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setDouble(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setFloat(float data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setFloat(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setInt(int data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setInt(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setLong(long data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setLong(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setNumColumns(int numColumns) {

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].setNumColumns(numColumns);

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setNumRows(int newCapacity) {

      try {

         manager.globalLock.acquireWrite();

         int numRows = 0;
         for (int i = 0; i < pages.length; i++)
            numRows += pages[i].getNumRows();

         if (newCapacity > numRows) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[pages.length - 1]);

               lock.acquireWrite();

                  correct = manager.check(pages[pages.length - 1], lock);

                  if (correct)
                     pages[pages.length - 1].setNumRows(
                        pages[pages.length - 1].getNumRows() + newCapacity
                        - numRows);

               lock.releaseWrite();

            } while (!correct);

            manager.globalLock.releaseWrite();

         }
         else if (newCapacity < numRows) {

            manager.globalLock.releaseWrite();

            removeRows(newCapacity, numRows - newCapacity);

         }

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setObject(Object data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setObject(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(boolean[] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(byte[] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(byte[][] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(char[] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(char[][] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(double[] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(float[] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(int[] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(long[] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(Object[] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(short[] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setRow(String[] newEntry, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setRow(newEntry, position - offsets[pageNum]);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setShort(short data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setShort(data, row - offsets[pageNum], column);

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void setString(String data, int row, int column) {

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct) {
                  pages[pageNum].setString(data, row - offsets[pageNum], column);

                  /*if (row == 20) {
                     System.out.println("20:");
                     System.out.println(row);
                     System.out.println(pageNum);
                     System.out.println(offsets[pageNum]);
                     System.out.println(pages[pageNum] + " " + pages[pageNum].numRows + " " + pages[pageNum].numColumns);
                     System.out.println(pages[pageNum].table + " " + ((TableImpl)pages[pageNum].table).getColumn(column).getNumRows());
                     System.out.println(data);
                     pages[pageNum].getString(row - offsets[pageNum], column);
                  }*/

               }

            lock.releaseWrite();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public void sortByColumn(int col) {
      sortByColumn(col, 0, getNumRows() - 1);
   }

   public void sortByColumn(int col, int begin, int end) {

      try {

         manager.globalLock.acquireRead();

         int page1 = getPageNumber(begin),
             page2 = getPageNumber(end);

         if (page1 == page2) { // easy...

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[page1]);

               lock.acquireWrite();

                  correct = manager.check(pages[page1], lock);

                  if (correct)
                     pages[page1].sortByColumn(col, begin - offsets[page1], end - offsets[page1]);

               lock.releaseWrite();

            } while (!correct);

         }
         else { // ...much harder :(

            // sort each segment in place...

               // page1:

               boolean correct = false;
               do {

                  Lock lock = manager.request(pages[page1]);

                  lock.acquireWrite();

                     correct = manager.check(pages[page1], lock);

                     if (correct)
                        pages[page1].sortByColumn(col, begin - offsets[page1], pages[page1].getNumRows() - 1);

                  lock.releaseWrite();

               } while (!correct);

               // any between page1 and page2:

               for (int i = page1 + 1; i < page2; i++) {

                  correct = false;
                  do {

                     Lock lock = manager.request(pages[i]);

                     lock.acquireWrite();

                        correct = manager.check(pages[i], lock);

                        if (correct)
                           pages[i].sortByColumn(col, 0, pages[i].getNumRows() - 1);

                     lock.releaseWrite();

                  } while (!correct);

               }

               // page2:

               correct = false;
               do {

                  Lock lock = manager.request(pages[page2]);

                  lock.acquireWrite();

                     correct = manager.check(pages[page2], lock);

                     if (correct)
                        pages[page2].sortByColumn(col, 0, end - offsets[page2]);

                  lock.releaseWrite();

               } while (!correct);

            // ...and merge

               // one "position" per page:
               int[] positions = new int[page2 - page1 + 1],
                     initialPositions = new int[positions.length],
                     finalPositions = new int[positions.length];

               positions[0] = begin - offsets[page1];
               initialPositions[0] = positions[0];
               finalPositions[0] = pages[page1].getNumRows() - 1;

               for (int i = 1; i < positions.length; i++) {
                  positions[i] = 0;
                  initialPositions[i] = 0;
                  finalPositions[i] = pages[page1 + i].getNumRows() - 1;
               }

               finalPositions[positions.length - 1] = end - offsets[page2];

               int leftmostUnsorted = 0, minimumIndex = 0;

               // new Object-comparison sort:

               Comparable[] values = new Comparable[positions.length];

               while (true) {

                  for (int i = leftmostUnsorted; i < positions.length; i++)
                     values[i] = (Comparable)getObject(offsets[page1 + i] + positions[i], col);

                  Comparable minimum = values[leftmostUnsorted];
                  minimumIndex = leftmostUnsorted;

                  for (int i = leftmostUnsorted + 1; i < values.length; i++) {
                     if (values[i].compareTo(minimum) < 0) {
                        minimum = values[i];
                        minimumIndex = i;
                     }
                  }

                  if (minimumIndex != leftmostUnsorted) {

                     this.swapRows(
                        offsets[page1 + minimumIndex] + positions[minimumIndex],
                        offsets[page1 + leftmostUnsorted] + positions[leftmostUnsorted]
                     );

                     // bubbling is bad... :|

                     correct = false;
                     do {

                        Lock lock = manager.request(pages[page1 + minimumIndex]);

                        lock.acquireWrite();

                           correct = manager.check(pages[page1 + minimumIndex], lock);

                           if (correct) {

                              for (int i = positions[minimumIndex]; i < finalPositions[minimumIndex]; i++)
                                 if (((Comparable)(pages[page1 + minimumIndex].getObject(i, col))).compareTo(
                                    pages[page1 + minimumIndex].getObject(i + 1, col)) > 0)
                                       pages[page1 + minimumIndex].swapRows(i, i + 1);

                           }

                        lock.releaseWrite();

                     } while (!correct);

                  }

                  // almost done...

                  if (++positions[leftmostUnsorted] > finalPositions[leftmostUnsorted])
                     leftmostUnsorted++;

                  if (page1 + leftmostUnsorted >= page2) {

                     correct = false;
                     do {

                        Lock lock = manager.request(pages[page2]);

                        lock.acquireWrite();

                           correct = manager.check(pages[page2], lock);

                           if (correct)
                              pages[page2].sortByColumn(col, 0, end - offsets[page2]);

                        lock.releaseWrite();

                     } while (!correct);

                     // ...and voila!
                     break;

                  }

               }

            }

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void swapColumns(int pos1, int pos2) {

      try {

         manager.globalLock.acquireWrite();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].swapColumns(pos1, pos2);

               lock.releaseWrite();

            } while (!correct);

         }

         manager.globalLock.releaseWrite();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void swapRows(int pos1, int pos2) {

      try {

         manager.globalLock.acquireRead();

         int page1 = getPageNumber(pos1),
             page2 = getPageNumber(pos2);

         // easy if they're on the same page...

         if (page1 == page2) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[page1]);

               lock.acquireWrite();

                  correct = manager.check(pages[page1], lock);

                  if (correct)
                     pages[page1].swapRows(pos1 - offsets[page1], pos2 - offsets[page1]);

               lock.releaseWrite();

            } while (!correct);

         }

         // ...if not...

         else {

            Object[] row1 = new Object[getNumColumns()],
                     row2 = new Object[getNumColumns()];

            getRow(row1, pos1);
            getRow(row2, pos2);

            setRow(row2, pos1);
            setRow(row1, pos2);

         }

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

/*   public void setScalarMissingValue(Number val, int col) {

      try {

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].setScalarMissingValue(val, col);

               lock.releaseWrite();

            } while (!correct);

         }

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setScalarEmptyValue(Number val, int col) {

      try {

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].setScalarEmptyValue(val, col);

               lock.releaseWrite();

            } while (!correct);

         }

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setNominalMissingValue(String val, int col) {

      try {

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].setNominalMissingValue(val, col);

               lock.releaseWrite();

            } while (!correct);

         }

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setNominalEmptyValue(String val, int col) {

      try {

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireWrite();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     pages[i].setNominalEmptyValue(val, col);

               lock.releaseWrite();

            } while (!correct);

         }

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }*/

   public void setValueToMissing(boolean b, int row, int col) {

      int pageNum = getPageNumber(row);

      try {

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setValueToMissing(b, row - offsets[pageNum], col);

            lock.releaseWrite();

         } while (!correct);

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public void setValueToEmpty(boolean b, int row, int col) {

      int pageNum = getPageNumber(row);

      try {

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireWrite();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].setValueToEmpty(b, row - offsets[pageNum], col);

            lock.releaseWrite();

         } while (!correct);

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   ArrayList transformations = new ArrayList();

   public void addTransformation(Transformation tm) {
      transformations.add(tm);
   }

   public List getTransformations() {
      return transformations;
   }

}
