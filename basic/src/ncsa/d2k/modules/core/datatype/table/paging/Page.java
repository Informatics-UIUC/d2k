/*
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 *
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 *
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
package ncsa.d2k.modules.core.datatype.table.paging;

import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.basic.BooleanColumn;
import ncsa.d2k.modules.core.datatype.table.basic.ByteArrayColumn;
import ncsa.d2k.modules.core.datatype.table.basic.ByteColumn;
import ncsa.d2k.modules.core.datatype.table.basic.CharArrayColumn;
import ncsa.d2k.modules.core.datatype.table.basic.CharColumn;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.FloatColumn;
import ncsa.d2k.modules.core.datatype.table.basic.IntColumn;
import ncsa.d2k.modules.core.datatype.table.basic.LongColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MissingValuesColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.ObjectColumn;
import ncsa.d2k.modules.core.datatype.table.basic.ShortColumn;
import ncsa.d2k.modules.core.datatype.table.basic.StringColumn;
import ncsa.d2k.modules.core.datatype.table.basic.TableImpl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Encapsulates a <code>Table</code> that can be paged to and from disk. Pages
 * also handle subset arrays, but this implementation does not page those out to
 * disk. They remain resident in memory.
 *
 * @author  suvalala
 * @author  pape
 * @author  redman
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class Page {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static final long serialVersionUID = -3452910203130295662L;

   /** Prefix used in the naming of the temp files. */
   static protected final String PAGE_PREFIX = "page-";

   /** Suffix used in the naming of the temp files. */
   static protected final String SUFFIX = ".ser";

   /** Description of field standardRead. */
   static final boolean standardRead = true;

   //~ Instance fields *********************************************************

   /** Number of milliseconds it took to deserialize the file. */
   long deserializeTime = 0;


   /** Number of milliseconds it took to read the file. */
   long fileReadTime = 0;

   /** Flag for when we change the data. */
   protected volatile boolean dirty = false;

   /**
    * Number of columns in the table. Same issue applies as for the one noted in
    * <code>numRows</code>.
    */
   protected int numColumns;

   /**
    * Number of rows in the table. In practice, getNumRows() is called so
    * frequently that we really don't want to require the <code>Table</code> be
    * paged in before the number of rows can be computed.
    */
   protected int numRows;

   /** The file we saved the <code>Table</code> in. */
   protected File pageFile;

   /** The subset array for the subsetted <code>Table</code>. */
   protected int[] subset;

   /** The <code>Table</code> we are paging in and out. */
   protected Table table;

   /** The time of last reference. */
   protected long timestamp;

   //~ Constructors ************************************************************

   /**
    * Creates a new Page object. Package private.
    */
   Page() { }

   /**
    * Constructs a new <code>Page</code> that can read the given <code>
    * Table</code> from (and write it to) the given <code>File</code>. The
    * <code>Table</code> itself will be retained in memory if and only if the
    * <code>keepInMemory</code> flag is <code>true</code>.
    *
    * @param  table        <code>Table</code> to store
    * @param  subset       File on disk
    * @param  keepInMemory Whether or not to keep the <code>Table</code> in
    *                      memory after construction
    *
    * @throws IOException If a problem occurs while creating this instance
    */
   Page(Table tbl, int[] sbst, boolean keepInMemory) throws IOException {
      pageFile = File.createTempFile(PAGE_PREFIX, SUFFIX);
      pageFile.deleteOnExit();
      table = tbl;
      numRows = table.getNumRows();
      numColumns = table.getNumColumns();
      subset = sbst;
      pageOut();

      if (!keepInMemory) {
         this.table = null;
      }

      timestamp = System.currentTimeMillis();
   }


   /**
    * If we are creating a new subset of the <code>Table</code>, the page for
    * the data already exists, we just need to create a new subset. The <code>
    * Table</code> has already been written to memory since it is already part
    * of another paging <code>Table</code>.
    *
    * @param  pp    File on disk containing the data
    * @param  table The <code>Table</code> to store
    * @param  s     The subset data.
    *
    * @throws IOException If a problem occurs while creating this instance
    */
   Page(File pp, Table tbl, int[] s) throws IOException {
      pageFile = pp;
      table = null;
      numRows = tbl.getNumRows();
      numColumns = tbl.getNumColumns();
      subset = s;
      timestamp = System.currentTimeMillis();
   }

   /**
    * If we are creating a new subset of the <code>Table</code>, the page for
    * the data already exists, we just need to create a new subset. The <code>
    * Table</code> has already been written to memory since it is already part
    * of another paging <code>Table</code>.
    *
    * @param  pp   File on disk containing the data
    * @param  rows <code>Table</code> to store
    * @param  cols Whether or not to keep the<code>Table</code> in memory after
    *              construction
    * @param  s    Subset data
    *
    * @throws IOException Description of exception IOException.
    */
   Page(File pp, int rows, int cols, int[] s) throws IOException {
      pageFile = pp;
      table = null;
      numRows = rows;
      numColumns = cols;
      subset = s;
      timestamp = System.currentTimeMillis();
   }

   /**
    * Constructs a new <code>Page</code> that can read the given <code>
    * Table</code> from (and write it to) the given <code>File</code>. The
    * <code>Table</code> itself will be retained in memory if and only if the
    * <code>keepInMemory</code> flag is <code>true</code>.
    *
    * @param  table        the <code>Table</code> to store
    * @param  keepInMemory keep the <code>Table</code> in memory after
    *                      construction?
    *
    * @throws IOException Description of exception IOException.
    */
   public Page(Table tbl, boolean keepInMemory) throws IOException {
      pageFile = File.createTempFile(PAGE_PREFIX, SUFFIX);
      pageFile.deleteOnExit();
      table = tbl;
      numRows = table.getNumRows();
      numColumns = table.getNumColumns();
      subset = new int[numRows];

      for (int i = 0; i < numRows; i++) {
         subset[i] = i;
      }

      pageOut();

      if (!keepInMemory) {
         this.table = null;
      }

      timestamp = System.currentTimeMillis();
   }

   //~ Methods *****************************************************************

   /**
    * The table must be paged in for this to work, it will update the count of
    * the number of rows and columns in the table.
    */
   private void updateCounts() {

      if (table == null) {
         pageIn();
      }

      numRows = table.getNumRows();
      numColumns = table.getNumColumns();
   }

   /**
    * Returns an exact copy of this <code>Page</code>, with its underlying
    * <code>Table</code> optionally retained in memory.
    *
    * @param  keepInMemory keep the <code>Table</code> in memory after
    *                      construction?
    *
    * @return the new <code>Page</code> copy
    */
   Page copy(boolean keepInMemory) {
      File newFile = null;

      try {
         newFile = File.createTempFile("d2k-", null);
         newFile.deleteOnExit();
      } catch (IOException e) {
         e.printStackTrace();
      }

      Page newPage;

      try {
         newPage = new Page(table.copy(), false);
      } catch (IOException e1) {
         e1.printStackTrace();

         return null;
      }

      if (keepInMemory) {
         newPage.pageIn();
      }

      newPage.mark(false);
      newPage.numRows = numRows;
      newPage.numColumns = numColumns;

      return newPage;
   } // end method copy

   /**
    * Writes the table out to disk, if it has been modified, and frees the table
    * for garbage collection.
    *
    * <p><b>WARNING:</b> This method must only be called while the resource is
    * externally write-locked!</p>
    */
   void free() {

      if (dirty) {
         pageOut();
      }

      table = null;
   }

   /**
    * Return the table subset, if it is spooled off to disk read it in.
    *
    * @return the table.
    */
   int[] getSubset() { return subset; }

   /**
    * Return the table, if it is spooled off to disk read it in.
    *
    * @return the table.
    */
   synchronized Table getTable() {

      if (table == null) {
         pageIn();
      }

      return table;
   }

   /**
    * returns true if the table is loaded.
    *
    * @return true if the table is loaded.
    */
   boolean hasTable() { return table != null; }

   /**
    * Time-stamps this page, and optionally marks it as dirty (modified).
    *
    * <p><b>NOTE:</b> This method <i>should</i> only be called while the
    * resource is externally locked (long assignment is not actually an atomic
    * operation in Java). Failure to do so should not cause serious problems,
    * however.</p>
    *
    * @param modified does this reference represent a modification of the
    *                 underlying <code>Table</code>?
    */
   void mark(boolean modified) {

      if (modified) { // if no modification, dirty should not be changed
         dirty = true;
      }

      this.updateCounts();
      timestamp = System.currentTimeMillis();
   }

   /**
    * Reads the <code>Table</code> in from disk.
    *
    * <p><b>WARNING:</b> This method must only be called while the resource is
    * externally write-locked!</p>
    */
   void pageIn() {

      if (pageFile == null) {
         return;
      }

      try {
         long start;
         long start2;
         long end;

         if (standardRead) {
            start = System.currentTimeMillis();

            ObjectInputStream I =
               new ObjectInputStream(new BufferedInputStream(new FileInputStream(pageFile)));
            Column[] cols = new Column[numColumns];

            for (int i = 0; i < numColumns; i++) {
               int type = I.readInt();

               switch (type) {

                  case ColumnTypes.BOOLEAN:
                     cols[i] = new BooleanColumn((boolean[]) I.readObject());

                     break;

                  case ColumnTypes.BYTE:
                     cols[i] = new ByteColumn((byte[]) I.readObject());

                     break;

                  case ColumnTypes.CHAR:
                     cols[i] = new CharColumn((char[]) I.readObject());

                     break;

                  case ColumnTypes.CHAR_ARRAY:
                     cols[i] = new CharArrayColumn((char[][]) I.readObject());

                     break;

                  case ColumnTypes.BYTE_ARRAY:
                     cols[i] = new ByteArrayColumn((byte[][]) I.readObject());

                     break;

                  case ColumnTypes.STRING:
                     cols[i] = new StringColumn((String[]) I.readObject());

                     break;

                  case ColumnTypes.SHORT:
                     cols[i] = new ShortColumn((short[]) I.readObject());

                     break;

                  case ColumnTypes.INTEGER:
                     cols[i] = new IntColumn((int[]) I.readObject());

                     break;

                  case ColumnTypes.LONG:
                     cols[i] = new LongColumn((long[]) I.readObject());

                     break;

                  case ColumnTypes.FLOAT:
                     cols[i] = new FloatColumn((float[]) I.readObject());

                     break;

                  case ColumnTypes.DOUBLE:
                     cols[i] = new DoubleColumn((double[]) I.readObject());

                     break;

                  case ColumnTypes.OBJECT:
                     cols[i] = new ObjectColumn((Object[]) I.readObject());

                     break;

                  default:
                     cols[i] = (Column) I.readObject();

                     break;
               }

               boolean[] mv = (boolean[]) I.readObject();
               ((MissingValuesColumn) cols[i]).setMissingValues(mv);
            } // end for

            table = new MutableTableImpl(cols);
            I.close();
            start2 = end = System.currentTimeMillis();
         } // end if

         fileReadTime += (start2 - start);
         deserializeTime += (end - start2);
      } catch (Exception e) {
         System.err.println("Page " + pageFile + ": exception on pageIn:");
         e.printStackTrace(System.err);
      } finally {
         dirty = false;
      }

   } // end method pageIn

   /**
    * Returns the <code>long</code> time stamp at which this page was last
    * referenced (read from or written to).
    *
    * @return The reference timestamp for this page
    */
   long time() { return timestamp; }

   /**
    * Writes the <code>Table</code> out to disk.
    *
    * <p><b>WARNING:</b> This method must only be called while the resource is
    * externally write-locked!</p>
    */
   protected void pageOut() {

      if (pageFile == null) {
         return;
      }

      try {

         // This will create a new file, if and only if the file does not
         // already exist.
         if (!pageFile.getParentFile().exists()) {
            pageFile.getParentFile().mkdirs();
         }

         // If we are writing, it is possible that the table
         // has changed, let's make sure we have the right number of
         // rows and columns.
         this.updateCounts();

         // Write the data to an object output stream.
         ObjectOutputStream O =
            new ObjectOutputStream(new FileOutputStream(pageFile));
         Column[] cols = ((TableImpl) table).getRawColumns();

         for (int i = 0; i < numColumns; i++) {
            int type = cols[i].getType();
            O.writeInt(type);

            switch (type) {

               case ColumnTypes.BOOLEAN:
               case ColumnTypes.BYTE:
               case ColumnTypes.CHAR:
               case ColumnTypes.CHAR_ARRAY:
               case ColumnTypes.BYTE_ARRAY:
               case ColumnTypes.STRING:
               case ColumnTypes.SHORT:
               case ColumnTypes.INTEGER:
               case ColumnTypes.LONG:
               case ColumnTypes.FLOAT:
               case ColumnTypes.DOUBLE:
               case ColumnTypes.OBJECT:
                  O.writeObject(cols[i].getInternal());

                  break;

               default:
                  O.writeObject(cols[i]);

                  break;
            }

            O.writeObject(((MissingValuesColumn) cols[i]).getMissingValues());
         } // end for

         O.close();
      } catch (Exception e) {
         System.err.println("Page " + pageFile + ": exception on pageOut:");
         e.printStackTrace(System.err);
      } finally {
         dirty = false;
      }

   } // end method pageOut
} // end class Page
