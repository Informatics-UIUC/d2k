package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * The <code>Page</code> class encapsulates a <code>Table</code> that can be
 * paged to and from disk.
 *
 * @author gpape
 */
class Page implements Serializable {

   private volatile boolean dirty = false; // modified since last write to disk?

   private long timestamp;                 // time of last reference that might
                                           // exhibit spatial locality

   File file;
   Table table;

   // In practice, getNumRows() and getNumColumns() are called so frequently
   // that we really don't want to require the table paged in before we can
   // compute them. Thus, we keep them here, and ensure in the method calls
   // that they are kept consistent with the table.

   int numRows, numColumns;

   /**
    * Constructs a new <code>Page</code> that can read the given
    * <code>Table</code> from (and write it to) the given <code>File</code>.
    * The <code>Table</code> itself will be retained in memory if and only if
    * the <code>keepInMemory</code> flag is <code>true</code>.
    *
    * @param file            a file on disk
    * @param table           the <code>Table</code> to store
    * @param keepInMemory    keep the <code>Table</code> in memory after
    *                        construction?
    */
   Page(File file, Table table, boolean keepInMemory) {

      this.file = file;
      this.table = table;

      numRows = table.getNumRows();
      numColumns = table.getNumColumns();

      pageOut();

      if (!keepInMemory)
         this.table = null;

      timestamp = System.currentTimeMillis();

   }

   /**
    * Reads the table in from disk.
    * <p>
    * <b>WARNING:</b> This method must only be called while the resource is
    * externally write-locked!
    */
   void pageIn() {

      try {
         ObjectInputStream I = new ObjectInputStream(new FileInputStream(file));
         table = (Table)I.readObject();
         I.close();
      }
      catch(Exception e) {
         System.err.println("Page " + file + ": exception on pageIn:");
         e.printStackTrace(System.err);
      }
      finally {
         dirty = false;
      }

   }

   /**
    * Writes the table out to disk.
    * <p>
    * <b>WARNING:</b> This method must only be called while the resource is
    * externally write-locked!
    */
   void pageOut() {

      try {
         ObjectOutputStream O = new ObjectOutputStream(new FileOutputStream(file));
         O.writeObject(table);
         O.flush();
         O.close();
      }
      catch(Exception e) {
         System.err.println("Page " + file + ": exception on pageOut:");
         e.printStackTrace(System.err);
      }
      finally {
         dirty = false;
      }

   }

   /**
    * Writes the table out to disk, if it has been modified, and frees the table
    * for garbage collection.
    * <p>
    * <b>WARNING:</b> This method must only be called while the resource is
    * externally write-locked!
    */
   void free() {

      if (dirty)
         pageOut();

      table = null;

   }

   /**
    * Time-stamps this page, and optionally marks it as dirty (modified).
    * <p>
    * <b>NOTE:</b> This method <i>should</i> only be called while the resource
    * is externally locked (long assignment is not actually an atomic operation
    * in Java). Failure to do so should not cause serious problems, however.
    *
    * @param modified        does this reference represent a modification of the
    *                        underlying <code>Table</code>?
    */
   void mark(boolean modified) {
      if (modified)    // if no modification, dirty should not be changed
         dirty = true;
      timestamp = System.currentTimeMillis();
   }

   /**
    * Returns the <code>long</code> time stamp at which this page was last
    * referenced (read from or written to).
    *
    * @return                this page's reference timestamp
    */
   long time() {
      return timestamp;
   }

   /**
    * Returns an exact copy of this <code>Page</code>, with its underlying
    * <code>Table</code> optionally retained in memory.
    * <p>
    * <b>WARNING:</b> This method must only be called while the resource is
    * externally write-locked (and <i>is paged in</i>)!
    *
    * @param keepInMemory    keep the <code>Table</code> in memory after
    *                        construction?
    *
    * @return                the new <code>Page</code> copy
    */
   Page copy(boolean keepInMemory) {

      File newFile = null;

      try {
         newFile = File.createTempFile("d2k-", null);
         newFile.deleteOnExit();
      }
      catch(IOException e) { e.printStackTrace(); }

      Page newPage = new Page(newFile, table.copy(), false);

      if (keepInMemory)
         newPage.pageIn();

      newPage.mark(false);

      newPage.numRows = numRows;
      newPage.numColumns = numColumns;

      return newPage;

   }

   /*
   public String toString() {
      if (file == null)
         return null;
      else
         return file.toString();
   }
   */

/******************************************************************************/

   // resource must be write-locked!

   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {

      table = (Table)in.readObject();
      // in.close();

      numRows = table.getNumRows();
      numColumns = table.getNumColumns();

      file = File.createTempFile("d2k-", null);
      file.deleteOnExit();

      pageOut();
      mark(false);

   }

   private void writeObject(ObjectOutputStream out) throws IOException {

      if (table == null)
         pageIn();

      // System.out.println("writing a table " + table);

      out.writeObject(table);
      // out.flush();
      // out.close();

   }

/******************************************************************************/
/* relevant Table methods.                                                    */
/* WARNING: these must only be called while the resource is read-locked!      */
/******************************************************************************/

   boolean getBoolean(int row, int column) {
      mark(false);
      return table.getBoolean(row, column);
   }

   byte getByte(int row, int column) {
      mark(false);
      return table.getByte(row, column);
   }

   byte[] getBytes(int row, int column) {
      mark(false);
      return table.getBytes(row, column);
   }

   char getChar(int row, int column) {
      mark(false);
      return table.getChar(row, column);
   }

   char[] getChars(int row, int column) {
      mark(false);
      return table.getChars(row, column);
   }

   String getColumnComment(int position) {
      mark(false);
      return table.getColumnComment(position);
   }

   String getColumnLabel(int position) {
      mark(false);
      return table.getColumnLabel(position);
   }

   int getColumnType(int position) {
      mark(false);
      return table.getColumnType(position);
   }

   double getDouble(int row, int column) {
      mark(false);
      return table.getDouble(row, column);
   }

   float getFloat(int row, int column) {
      mark(false);
      return table.getFloat(row, column);
   }

   int getInt(int row, int column) {
      mark(false);
      return table.getInt(row, column);
   }

   long getLong(int row, int column) {
      mark(false);
      return table.getLong(row, column);
   }

   int getNumColumns() {
      return numColumns;
   }

   int getNumEntries() {
      return table.getNumEntries();
   }

   int getNumRows() {
      return numRows;
   }

   Object getObject(int row, int column) {
      mark(false);
      return table.getObject(row, column);
   }

   void getRow(Object buffer, int position) {
      mark(false);
      table.getRow(buffer, position);
   }

   short getShort(int row, int column) {
      mark(false);
      return table.getShort(row, column);
   }

   String getString(int row, int column) {
      mark(false);
      return table.getString(row, column);
   }

   Table getSubset(int start, int len) {
      return table.getSubset(start, len);
   }

   boolean isColumnNominal(int position) {
      mark(false);
      return table.isColumnNominal(position);
   }

   boolean isColumnNumeric(int position) {
      mark(false);
      return table.isColumnNumeric(position);
   }

   boolean isValueMissing(int row, int column) {
      mark(false);
      return table.isValueMissing(row, column);
   }

   boolean isValueEmpty(int row, int column) {
      mark(false);
      return table.isValueEmpty(row, column);
   }

   Number getScalarMissingValue(int column) {
      mark(false);
      return table.getScalarMissingValue(column);
   }

   String getNominalMissingValue(int column) {
      mark(false);
      return table.getNominalMissingValue(column);
   }

   Number getScalarEmptyValue(int column) {
      mark(false);
      return table.getScalarEmptyValue(column);
   }

   String getNominalEmptyValue(int column) {
      mark(false);
      return table.getNominalEmptyValue(column);
   }

/******************************************************************************/
/* relevant MutableTable methods.                                             */
/* WARNING: these must only be called while the resource is write-locked!     */
/******************************************************************************/

   void addColumn(boolean[] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addColumn(byte[] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addColumn(byte[][] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addColumn(char[] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addColumn(double[] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addColumn(float[] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addColumn(int[] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addColumn(long[] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addColumn(Object[] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addColumn(short[] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addColumn(String[] newEntry) {
      ((MutableTable)table).addColumn(newEntry);
      numColumns++;
      if (newEntry.length > numRows)
         numRows = newEntry.length;
      mark(true);
   }

   void addRow(boolean[] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void addRow(byte[] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void addRow(byte[][] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void addRow(char[] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void addRow(double[] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void addRow(float[] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void addRow(int[] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void addRow(long[] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void addRow(Object[] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void addRow(short[] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void addRow(String[] newEntry) {
      ((MutableTable)table).addRow(newEntry);
      numRows++;
      mark(true);
   }

   void insertColumn(boolean[] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(byte[] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(byte[][] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(char[] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(char[][] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(double[] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(float[] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(int[] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(long[] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(Object[] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(short[] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertColumn(String[] newEntry, int position) {
      ((MutableTable)table).insertColumn(newEntry, position);
      numColumns++;
      mark(true);
   }

   void insertRow(boolean[] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(byte[] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(byte[][] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(char[] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(char[][] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(double[] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(float[] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(int[] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(long[] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(Object[] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(short[] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void insertRow(String[] newEntry, int position) {
      ((MutableTable)table).insertRow(newEntry, position);
      numRows++;
      mark(true);
   }

   void removeColumn(int position) {
      ((MutableTable)table).removeColumn(position);
      numColumns--;
      mark(true);
   }

   void removeColumns(int start, int len) {
      ((MutableTable)table).removeColumns(start, len);
      numColumns -= len;
      mark(true);
   }

   void removeColumnsByFlag(boolean[] flags) {
      int offset = 0;
      for (int i = 0; i < flags.length; i++)
         if (flags[i])
            removeColumn(i - offset++);
      mark(true);
   }

   void removeColumnsByIndex(int[] indices) {
      Arrays.sort(indices); // important!
      int offset = 0;
      for (int i = 0; i < indices.length; i++)
         removeColumn(indices[i] - offset++);
      mark(true);
   }

   void removeRow(int row) {
      ((MutableTable)table).removeRow(row);
      numRows--;
      mark(true);
   }

   void removeRows(int start, int len) {
      ((MutableTable)table).removeRows(start, len);
      numRows -= len;
      mark(true);
   }

   void removeRowsByFlag(boolean[] flags) {

      ((MutableTable)table).removeRowsByFlag(flags);

      for (int i = 0; i < flags.length; i++)
         if (flags[i])
            numRows--;

      mark(true);

   }

   void removeRowsByIndex(int[] indices) {
      ((MutableTable)table).removeRowsByIndex(indices);
      numRows -= indices.length;
      mark(true);
   }

   void reorderColumns(int[] newOrder) {
      ((MutableTable)table).reorderColumns(newOrder);
      dirty = true;
   }

   void setBoolean(boolean data, int row, int column) {
      ((MutableTable)table).setBoolean(data, row, column);
      mark(true);
   }

   void setByte(byte data, int row, int column) {
      ((MutableTable)table).setByte(data, row, column);
      mark(true);
   }

   void setBytes(byte[] data, int row, int column) {
      ((MutableTable)table).setBytes(data, row, column);
      mark(true);
   }

   void setChar(char data, int row, int column) {
      ((MutableTable)table).setChar(data, row, column);
      mark(true);
   }

   void setChars(char[] data, int row, int column) {
      ((MutableTable)table).setChars(data, row, column);
      mark(true);
   }

   void setDouble(double data, int row, int column) {
      ((MutableTable)table).setDouble(data, row, column);
      mark(true);
   }

   void setFloat(float data, int row, int column) {
      ((MutableTable)table).setFloat(data, row, column);
      mark(true);
   }

   void setInt(int data, int row, int column) {
      ((MutableTable)table).setInt(data, row, column);
      mark(true);
   }

   void setLong(long data, int row, int column) {
      ((MutableTable)table).setLong(data, row, column);
      mark(true);
   }

   void setNumColumns(int numColumns) {
      ((MutableTable)table).setNumColumns(numColumns);
      this.numColumns = numColumns;
      dirty = true;
   }

   void setNumRows(int numRows) {
      ((MutableTable)table).setNumRows(numRows);
      this.numRows = numRows;
      dirty = true;
   }

   void setObject(Object data, int row, int column) {
      ((MutableTable)table).setObject(data, row, column);
      mark(true);
   }

   void setRow(boolean[] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(byte[] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(byte[][] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(char[] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(char[][] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(double[] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(float[] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(int[] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(long[] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(Object[] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(short[] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setRow(String[] newEntry, int position) {
      ((MutableTable)table).setRow(newEntry, position);
      mark(true);
   }

   void setShort(short data, int row, int column) {
      ((MutableTable)table).setShort(data, row, column);
      mark(true);
   }

   void setString(String data, int row, int column) {
      ((MutableTable)table).setString(data, row, column);
      mark(true);
   }

   void sortByColumn(int col) {
      ((MutableTable)table).sortByColumn(col);
      mark(true);
   }

   void sortByColumn(int col, int begin, int end) {
      ((MutableTable)table).sortByColumn(col, begin, end);
      mark(true);
   }

   void swapColumns(int pos1, int pos2) {
      ((MutableTable)table).swapColumns(pos1, pos2);
      mark(true);
   }

   void swapRows(int pos1, int pos2) {
      ((MutableTable)table).swapRows(pos1, pos2);
      mark(true);
   }

   void setScalarMissingValue(Number val, int col) {
      ((MutableTable)table).setScalarMissingValue(val, col);
      dirty = true;
   }

   void setScalarEmptyValue(Number val, int col) {
      ((MutableTable)table).setScalarEmptyValue(val, col);
      dirty = true;
   }

   void setNominalMissingValue(String val, int col) {
      ((MutableTable)table).setNominalMissingValue(val, col);
      dirty = true;
   }

   void setNominalEmptyValue(String val, int col) {
      ((MutableTable)table).setNominalEmptyValue(val, col);
      dirty = true;
   }

   void setValueToMissing(int row, int col) {
      ((MutableTable)table).setValueToMissing(row, col);
      mark(true);
   }

   void setValueToEmpty(int row, int col) {
      ((MutableTable)table).setValueToEmpty(row, col);
      mark(true);
   }

   void setColumnComment(String comment, int position) {
      ((MutableTable)table).setColumnComment(comment, position);
      dirty = true;
   }

   void setColumnLabel(String comment, int position) {
      ((MutableTable)table).setColumnLabel(comment, position);
      dirty = true;
   }

}
