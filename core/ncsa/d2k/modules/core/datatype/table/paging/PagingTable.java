package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * <code>PagingTable</code> is an implementation of <code>Table</code> that can
 * store segments of itself on the hard disk rather than in memory. Each segment
 * is called a page.
 * <p>
 * Disk space used by the <code>PagingTable</code> will be reclaimed provided
 * that the virtual machine terminates normally.
 */
class PagingTable extends AbstractTable implements Serializable {

   protected Page[] pages;
   protected int[] offsets;
   protected PageManager manager;

   protected boolean[] columnIsNominal;

   protected int numPages, managerCapacity;

   private static final String PREFIX = "d2k-";

/******************************************************************************/
/* constructors                                                               */
/******************************************************************************/

   PagingTable() {
      pages = new Page[0];
      offsets = new int[0];
      manager = null;
      columnIsNominal = null;
      numPages = 0;
      managerCapacity = 0;
   }

   protected void addTable(Table newTable, boolean keepInMemory) {

      File newFile = null;
      try {
         newFile = File.createTempFile(PREFIX, null);
         newFile.deleteOnExit();
      }
      catch(IOException e) { e.printStackTrace(); }

      Page[] newPages = new Page[pages.length + 1];
      for (int i = 0; i < pages.length; i++)
         newPages[i] = pages[i];
      newPages[pages.length] = new Page(newFile, newTable, keepInMemory);

      pages = newPages;

      if (offsets.length == 0) {
         offsets = new int[1];
         offsets[0] = 0;
      }
      else {
         int[] newOffsets = new int[offsets.length + 1];
         for (int i = 0; i < offsets.length; i++)
            newOffsets[i] = offsets[i];
         // newOffsets[offsets.length] = offsets[offsets.length - 1] + newTable.getNumRows(); // <- logic error
         newOffsets[offsets.length] = offsets[offsets.length - 1] + pages[pages.length - 2].getNumRows();
         offsets = newOffsets;
      }

      numPages++;

      if (keepInMemory)
         managerCapacity++;

      if (columnIsNominal == null) {
         columnIsNominal = new boolean[newTable.getNumColumns()];
         for (int i = 0; i < columnIsNominal.length; i++)
            columnIsNominal[i] = newTable.isColumnNominal(i);
      }

      manager = new PageManager(pages, managerCapacity);

   }

   PagingTable(Table table, int totalPages, int pagesInMemory) {

      numPages = totalPages;
      managerCapacity = pagesInMemory;
      // System.out.println("constructing " + this + " with " + managerCapacity);

      int numRows = table.getNumRows();
      int numColumns = table.getNumColumns();

      offsets = new int[totalPages];

      columnIsNominal = new boolean[numColumns];
      for (int i = 0; i < numColumns; i++)
         columnIsNominal[i] = table.isColumnNominal(i);

      // create pages
      pages = new Page[totalPages];
      Page[] initialPages = new Page[pagesInMemory];

      int rowsPerPage = numRows/totalPages;
      int currentRow = 0;
      for (int i = 0; i < totalPages; i++) {

         offsets[i] = currentRow;

         Table subset;
         if (i < totalPages - 1)
            subset = (Table)table.getSubset(currentRow, rowsPerPage);
         else
            subset = (Table)table.getSubset(currentRow, numRows - currentRow);

            /*
         System.out.println("subset: " + subset.getColumnType(4));
         */

         /*System.out.println("---");
         for (int j = 0; j < numColumns; j++) {
            TableImpl ti = (TableImpl)subset;
            System.out.println(ti.isColumnNominal(j));
         }*/

         File file = null;
         try {
            file = File.createTempFile(PREFIX, null);
         }
         catch(Exception e) {
            System.err.println("PagingTable: error creating page file:");
            e.printStackTrace();
         }
         file.deleteOnExit();

         if (i < pagesInMemory) {
            pages[i] = new Page(file, subset, true);
            initialPages[i] = pages[i];
         }
         else
            pages[i] = new Page(file, subset, false);

         currentRow += rowsPerPage;

      }

      // create page manager
      manager = new PageManager(pages, initialPages);

   }

   protected PagingTable(Page[] pages, PageManager manager) {

      this.pages = pages;
      this.manager = manager;

      numPages = pages.length;
      managerCapacity = manager.capacity;

      offsets = new int[pages.length];
      offsets[0] = 0;
      for (int i = 1; i < pages.length; i++)
         offsets[i] = offsets[i - 1] + pages[i - 1].getNumRows();

      columnIsNominal = new boolean[pages[0].getNumColumns()];
      try {

         boolean correct = false;
         do {

            Lock lock = manager.request(pages[0]);

            lock.acquireRead();

               correct = manager.check(pages[0], lock);

               if (correct)
                  for (int i = 0; i < columnIsNominal.length; i++)
                     columnIsNominal[i] = pages[0].isColumnNominal(i);

            lock.releaseRead();

         } while (!correct);

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public PagingTable shallowCopy() {
      return new PagingTable(pages, manager);
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

/******************************************************************************/
/* indirection mechanisms                                                     */
/******************************************************************************/

   int getPageNumber(int row) {

      for (int i = 1; i < pages.length; i++)
         if (row < offsets[i])
            return i - 1;
      return pages.length - 1;

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

      return new PagingTable(newPages, new PageManager(newPages, newInitialPages));

   }

   public boolean getBoolean(int row, int column) {

      boolean b = false;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  b = pages[pageNum].getBoolean(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return b;

   }

   public byte getByte(int row, int column) {

      byte b = 0;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  b = pages[pageNum].getByte(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return b;

   }

   public byte[] getBytes(int row, int column) {

      byte[] b = null;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  b = pages[pageNum].getBytes(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return b;

   }

   public char getChar(int row, int column) {

      char c = '\0';

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  c = pages[pageNum].getChar(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return c;

   }

   public char[] getChars(int row, int column) {

      char[] c = null;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  c = pages[pageNum].getChars(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return c;

   }

   public void getColumn(Object buffer, int position) {

      try {

         manager.globalLock.acquireRead();

         int numRows = 0;
         for (int i = 0; i < pages.length; i++)
            numRows += pages[i].getNumRows();

         if (buffer instanceof boolean[]) {
            boolean[] b = (boolean[])buffer;
            for (int i = 0; i < b.length; i++)
               b[i] = getBoolean(i, position);
         }
         else if (buffer instanceof byte[]) {
            byte[] b = (byte[])buffer;
            for (int i = 0; i < b.length; i++)
               b[i] = getByte(i, position);
         }
         else if (buffer instanceof byte[][]) {
            byte[][] b = (byte[][])buffer;
            for (int i = 0; i < b.length; i++)
               b[i] = getBytes(i, position);
         }
         else if (buffer instanceof char[]) {
            char[] c = (char[])buffer;
            for (int i = 0; i < c.length; i++)
               c[i] = getChar(i, position);
         }
         else if (buffer instanceof char[][]) {
            char[][] c = (char[][])buffer;
            for (int i = 0; i < c.length; i++)
               c[i] = getChars(i, position);
         }
         else if (buffer instanceof double[]) {
            double[] d = (double[])buffer;
            for (int i = 0; i < d.length; i++)
               d[i] = getDouble(i, position);
         }
         else if (buffer instanceof float[]) {
            float[] f = (float[])buffer;
            for (int i = 0; i < f.length; i++)
               f[i] = getFloat(i, position);
         }
         else if (buffer instanceof int[]) {
            int[] I = (int[])buffer;
            for (int i = 0; i < I.length; i++)
               I[i] = getInt(i, position);
         }
         else if (buffer instanceof long[]) {
            long[] l = (long[])buffer;
            for (int i = 0; i < l.length; i++)
               l[i] = getLong(i, position);
         }
         else if (buffer instanceof Object[]) {
            Object[] o = (Object[])buffer;
            for (int i = 0; i < o.length; i++)
               o[i] = getObject(i, position);
         }
         else if (buffer instanceof short[]) {
            short[] s = (short[])buffer;
            for (int i = 0; i < s.length; i++)
               s[i] = getShort(i, position);
         }
         else if (buffer instanceof String[]) {
            String[] s = (String[])buffer;
            for (int i = 0; i < s.length; i++)
               s[i] = getString(i, position);
         }

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

   }

   public String getColumnComment(int position) {

      String s = null;

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[0]);

            lock.acquireRead();

               correct = manager.check(pages[0], lock);

               if (correct)
                  s = pages[0].getColumnComment(position);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return s;

   }

   public String getColumnLabel(int position) {

      String s = null;

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[0]);

            lock.acquireRead();

               correct = manager.check(pages[0], lock);

               if (correct)
                  s = pages[0].getColumnLabel(position);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return s;

   }

   public int getColumnType(int position) {

      int i = -1;

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[0]);

            lock.acquireRead();

               correct = manager.check(pages[0], lock);

               if (correct)
                  i = pages[0].getColumnType(position);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return i;

   }

   public double getDouble(int row, int column) {

      double d = Double.NaN;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  d = pages[pageNum].getDouble(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return d;

   }

   public float getFloat(int row, int column) {

      float f = Float.NaN;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  f = pages[pageNum].getFloat(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return f;

   }

   public int getInt(int row, int column) {

      int i = 0;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  i = pages[pageNum].getInt(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return i;

   }

   public long getLong(int row, int column) {

      long l = 0;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  l = pages[pageNum].getLong(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return l;

   }

   public int getNumColumns() {

      int numColumns = 0;

      try {

         manager.globalLock.acquireRead();

         numColumns = pages[0].getNumColumns();

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

      return numColumns;

   }

   public int getNumEntries() {

      int numEntries = 0;

      try {

         manager.globalLock.acquireRead();

         for (int i = 0; i < pages.length; i++) {

            boolean correct = false;
            do {

               Lock lock = manager.request(pages[i]);

               lock.acquireRead();

                  correct = manager.check(pages[i], lock);

                  if (correct)
                     numEntries += pages[i].getNumEntries();

               lock.releaseRead();

            } while (!correct);

         }

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

      return numEntries;

   }

   public int getNumRows() {

      int numRows = 0;

      try {

         manager.globalLock.acquireRead();

            for (int i = 0; i < pages.length; i++)
               numRows += pages[i].getNumRows();

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }

      return numRows;

   }

   public Object getObject(int row, int column) {

      Object o = null;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  o = pages[pageNum].getObject(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return o;

   }

   public void getRow(Object buffer, int position) {

      int pageNum = getPageNumber(position);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  pages[pageNum].getRow(buffer, position - offsets[pageNum]);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

   }

   public short getShort(int row, int column) {

      short s = 0;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  s = pages[pageNum].getShort(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return s;

   }

   public String getString(int row, int column) {

      String s = null;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  s = pages[pageNum].getString(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return s;

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

                     File file = File.createTempFile(PREFIX, null);
                     file.deleteOnExit();

                     newPage[0] = new Page(
                        file,
                        pages[page1].getSubset(start - offsets[page1], len).copy(),
                        true
                     );

                     t = new PagingTable(newPage, new PageManager(newPage, newPage));

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

                     File file = File.createTempFile(PREFIX, null);
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

                        File file = File.createTempFile(PREFIX, null);
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

                     File file = File.createTempFile(PREFIX, null);
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

            t = new PagingTable(newPages, new PageManager(newPages, newInitialPages));

         }

         manager.globalLock.releaseRead();

      }
      catch(InterruptedException e) { e.printStackTrace(); }
      catch(IOException e) { e.printStackTrace(); }

      return t;

   }

   public TableFactory getTableFactory() {
      return new PagingTableFactory();
   }

   public boolean isColumnNominal(int position) {
      return columnIsNominal[position];
   }

   public boolean isColumnScalar(int position) {
      return !columnIsNominal[position];
   }

   public boolean isColumnNumeric(int position) {

      boolean numeric = false;

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[0]);

            lock.acquireRead();

               correct = manager.check(pages[0], lock);

               if (correct)
                  numeric = pages[0].isColumnNumeric(position);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return numeric;

   }

   public void setColumnIsNominal(boolean value, int position) {
      columnIsNominal[position] = value;
   }

   public void setColumnIsScalar(boolean value, int position) {
      columnIsNominal[position] = !value;
   }

   public ExampleTable toExampleTable() {
      //PagingTable pt = (PagingTable)this.copy();
      return new PagingExampleTable(pages, manager);
   }

   public boolean isValueMissing(int row, int column) {

      boolean missing = false;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  missing = pages[pageNum].isValueMissing(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return missing;

   }

   public boolean isValueEmpty(int row, int column) {

      boolean empty = false;

      int pageNum = getPageNumber(row);

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[pageNum]);

            lock.acquireRead();

               correct = manager.check(pages[pageNum], lock);

               if (correct)
                  empty = pages[pageNum].isValueEmpty(row - offsets[pageNum], column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return empty;

   }

   public Number getScalarMissingValue(int column) {

      Number n = null;

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[0]);

            lock.acquireRead();

               correct = manager.check(pages[0], lock);

               if (correct)
                  n = pages[0].getScalarMissingValue(column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return n;

   }

   public String getNominalMissingValue(int column) {

      String s = null;

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[0]);

            lock.acquireRead();

               correct = manager.check(pages[0], lock);

               if (correct)
                  s = pages[0].getNominalMissingValue(column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return s;

   }

   public Number getScalarEmptyValue(int column) {

      Number n = null;

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[0]);

            lock.acquireRead();

               correct = manager.check(pages[0], lock);

               if (correct)
                  n = pages[0].getScalarEmptyValue(column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return n;

   }

   public String getNominalEmptyValue(int column) {

      String s = null;

      boolean correct = false;
      do {

         try {

            Lock lock = manager.request(pages[0]);

            lock.acquireRead();

               correct = manager.check(pages[0], lock);

               if (correct)
                  s = pages[0].getNominalEmptyValue(column);

            lock.releaseRead();

         }
         catch(InterruptedException e) { e.printStackTrace(); }

      } while (!correct);

      return s;

   }

}
