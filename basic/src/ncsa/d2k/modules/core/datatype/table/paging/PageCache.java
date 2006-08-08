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
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


/**
 * <p>Handles the caching of pages for paging tables. It contains an array list
 * with one entry for each page. A page is kind of a serialized table, but data
 * only. For each page, a count of the number of threads referencing the page is
 * stored. When a count goes to zero, the table can be discarded, paged out.
 * When the table implementations gets a page fault, which manifests itself in
 * the form of an array index out of bounds exception, the table implementation
 * will request the appropriate page from the page manager. The page is read in
 * if neceessary, the referer count is incremented, and the offset to the start
 * of the page is returned to the table.</p>
 *
 * <p>This object also stores metadata about the columns. This include the
 * column labels, comments, if they are nominal or scalar.</p>
 *
 * <p>There are also methods provided that manipulate the form of the tables.
 * There are methods to add column, insert and remove columns. Rows can also be
 * added and removed. Copy and subset methods are also provided here.</p>
 *
 * @author  redman
 * @version $Revision$, $Date$
 */
public class PageCache {

   //~ Instance fields *********************************************************

   /** Column comments. */
   private String[] columnComments;

   /** Column labels. */
   private String[] columnLabels;

   /** Stores whether or not each column is nominal. */
   private boolean[] columnNominal;

   /** Stores whether or not each column is scalar. */
   private boolean[] columnScalar;

   /**
    * Empty <code>Page</code> instance that is used instead of returning null in
    * some cases.
    */
   private Page nonPage = null;

   /** Total number of rows. */
   private int numRows;

   /** The offset of each table. */
   private int[] offsets;

   /** The array of pages. */
   private Page[] pages;

   /** Count of the number of threads referencing each page. */
   private int[] references;

   /** Default size for each page. */
   int defaultPageSize = 50000;

   //~ Constructors ************************************************************

   /**
    * Creates a new <code>PageCache</code> given the pages, offsets, and the
    * page size.
    *
    * @param pgs             The pages
    * @param offsets         The offsets of each page
    * @param defaultPageSize The default size of a page
    */
   public PageCache(Page[] pgs, int[] offsets, int defaultPageSize) {
      this.pages = pgs;
      this.offsets = offsets;
      this.references = new int[pgs.length];
      this.defaultPageSize = defaultPageSize;

      try {
         nonPage = new Page(new MutableTableImpl(), true);
      } catch (IOException e) {
         e.printStackTrace();
      }

      for (int i = 0; i < references.length; i++) {
         references[i] = 0;
      }

      // Get the table from the first page, to get the column labels and
      // comments.
      int nc;
      MutableTable t = null;

      if (pgs.length > 0) {
         t = (MutableTable) pgs[0].getTable();
         nc = t.getNumColumns();
      } else {
         nc = 0;
      }

      columnLabels = new String[nc];
      columnComments = new String[nc];
      columnNominal = new boolean[nc];
      columnScalar = new boolean[nc];

      for (int i = 0; i < nc; i++) {
         columnLabels[i] = t.getColumnLabel(i);
         columnComments[i] = t.getColumnComment(i);
         columnNominal[i] = t.isColumnNominal(i);
         columnScalar[i] = t.isColumnScalar(i);
      }
   }

   //~ Methods *****************************************************************

   /**
    * Creates a temporary file.
    *
    * @return Temporary file
    *
    * @throws IOException If there was a problem creating the temp file
    */
   static private final File createTempFile() throws IOException {
      return File.createTempFile("page-", ".ser");
   }

   /**
    * Updates all the information associated with the columns when a new one is
    * added.
    *
    * @param col The new column
    */
   private void columnAdded(Column col) {

      // add a label
      int oldcount = columnLabels.length;
      String[] newlabels = new String[oldcount + 1];
      System.arraycopy(columnLabels, 0, newlabels, 0, oldcount);
      newlabels[oldcount] = col.getLabel();
      columnLabels = newlabels;

      // add a label
      newlabels = new String[oldcount + 1];
      System.arraycopy(columnComments, 0, newlabels, 0, oldcount);
      newlabels[oldcount] = col.getComment();
      columnComments = newlabels;

      // add a label
      boolean[] nb = new boolean[oldcount + 1];
      System.arraycopy(columnScalar, 0, nb, 0, oldcount);
      nb[oldcount] = col.getIsScalar();
      columnScalar = nb;

      // add a label
      nb = new boolean[oldcount + 1];
      System.arraycopy(columnNominal, 0, nb, 0, oldcount);
      nb[oldcount] = col.getIsNominal();
      columnNominal = nb;
   } // end method columnAdded


   /**
    * Updates all the information associated with the columns when a new one is
    * inserted.
    *
    * @param col   The new column
    * @param where Where the new column was inserted
    */
   private void columnInserted(Column col, int where) {

      // add a label
      int oldcount = columnLabels.length;
      String[] newlabels = new String[oldcount + 1];
      System.arraycopy(columnLabels, 0, newlabels, 0, where);
      newlabels[where] = col.getLabel();
      System.arraycopy(columnLabels, where, newlabels, where + 1,
                       newlabels.length - (where + 1));
      columnLabels = newlabels;

      // add a label
      newlabels = new String[oldcount + 1];
      System.arraycopy(columnComments, 0, newlabels, 0, where);
      newlabels[where] = col.getComment();
      System.arraycopy(columnComments, where, newlabels, where + 1,
                       newlabels.length - (where + 1));
      columnComments = newlabels;

      // add a label
      boolean[] nb = new boolean[oldcount + 1];
      System.arraycopy(columnScalar, 0, nb, 0, where);
      nb[where] = col.getIsScalar();
      System.arraycopy(columnScalar, where, nb, where + 1,
                       newlabels.length - (where + 1));
      columnScalar = nb;

      // add a label
      nb = new boolean[oldcount + 1];
      System.arraycopy(columnNominal, 0, nb, 0, where);
      nb[where] = col.getIsNominal();
      System.arraycopy(columnNominal, where, nb, where + 1,
                       newlabels.length - (where + 1));
      columnNominal = nb;
   } // end method columnInserted

   /**
    * Updates all the information associated with the columns when a new one is
    * removed.
    *
    * @param index Index of the removed column
    */
   private void columnRemoved(int index) {

      // add a label
      int oldcount = columnLabels.length;
      String[] newlabels = new String[oldcount - 1];
      System.arraycopy(columnLabels, 0, newlabels, 0, index);
      System.arraycopy(columnLabels, index + 1, newlabels, index,
                       newlabels.length - index);
      columnLabels = newlabels;

      // remove a commment
      newlabels = new String[oldcount - 1];
      System.arraycopy(columnComments, 0, newlabels, 0, index);
      System.arraycopy(columnComments, index + 1, newlabels, index,
                       newlabels.length - index);
      columnComments = newlabels;

      // remove a scalar flag
      boolean[] nb = new boolean[oldcount - 1];
      System.arraycopy(columnScalar, 0, nb, 0, index);
      System.arraycopy(columnScalar, index + 1, nb, index, nb.length - index);
      columnScalar = nb;

      // remove a nominal flag
      nb = new boolean[oldcount - 1];
      System.arraycopy(columnNominal, 0, nb, 0, index);
      System.arraycopy(columnNominal, index + 1, nb, index, nb.length - index);
      columnNominal = nb;
   } // end method columnRemoved

   /**
    * Frees a <code>Page</code> if there are no references to it.
    *
    * @param whichPage <code>Page</code> to free
    */
   private void freeIfUnreferenced(int whichPage) {

      if (references[whichPage] == 0) {
         pages[whichPage].free();
      }
   }

   /**
    * Returns the index of the first item in the array greater than last. If
    * there are no entries greater than last, it returns the array size.
    *
    * @param  last   The last index
    * @param  values Values to compare
    *
    * @return The index
    */
   private int getLastEntry(int last, int[] values) {
      int i = values.length;

      while (values[i - 1] > last) {
         i--;
      }

      return i;
   }

   /**
    * Gets the index of the page at the given offset.
    *
    * @param  row Row we search for
    *
    * @return get Index of the page at the given offset
    */
   private final int getPageIndex(int row) {

      int[] o = offsets;
      int low = 1;

      // We search for two entries where row is between them, so
      // if it is less than the offset for the second entry,
      // it must be the first entry.
      if (row > o[o.length - 1]) {
         return o.length - 1;
      }

      if (o.length < 2 || row < o[1]) {
         return 0;
      }

      int high = o.length - 1;
      int middle;

      while (low <= high) {
         middle = (low + high) / 2;

         if (row == o[middle]) {
            return middle;
         } else if (row < o[middle]) {

            if (row >= o[middle - 1]) {

               // We look for the first entry that is greater than or equal to.
               return middle - 1;
            } else {
               high = middle - 1;
            }
         } else {
            low = middle + 1;
         }
      }

      return 0;
   } // end method getPageIndex

   /**
    * Gets the index of the page at the given offset.
    *
    * @param  row  Row we search for
    * @param  hint Suggestion of where to start looking
    *
    * @return Index of the page.
    */
   private final int getPageIndexWithHint(int row, int hint) {

      if (
          offsets.length > (hint + 1) &&
             row >= offsets[hint] &&
             row < offsets[hint + 1]) {
         return hint;
      } else {
         return getPageIndex(row);
      }
   }

   /**
    * Copies and returns the page cache including only the given subset. If the
    * subset is null, everything is returned.
    *
    * @param  subset Subset to copy
    *
    * @return Make a copy of the page cache including only the given subset. If
    *         the subset is null, return everything.
    */
   PageCache copy(int[] subset) {

      if (subset == null) {

         // remove the column from each page
         int numPages = pages.length;
         Page[] newpages = new Page[numPages];

         for (int i = 0; i < numPages; i++) {

            // Make a deep copy of the table.
            MutableTable t = (MutableTable) pages[i].getTable().copy();
            this.freeIfUnreferenced(i);

            try {
               newpages[i] = new Page(t, false);
            } catch (IOException e) {
               e.printStackTrace();

               return null;
            }
         }

         int[] newoffsets = new int[offsets.length];
         System.arraycopy(offsets, 0, newoffsets, 0, offsets.length);

         return new PageCache(newpages, newoffsets, this.defaultPageSize);
      } else {

         // make a subset of the table.
         Table t = pages[0].getTable();
         int numColumns = t.getNumColumns();
         int numPages = subset.length / this.defaultPageSize;

         if ((numPages * this.defaultPageSize) != subset.length) {

            // Add another page to accomodate the remainder
            numPages++;
         }

         // create the new pages.
         Page[] newpages = new Page[numPages];
         int[] newoffsets = new int[numPages];
         int currentOffset = 0;

         for (int i = 0; i < newpages.length; i++) {
            t = this.createNewTable(defaultPageSize);

            try {
               newpages[i] = new Page(t, true);
            } catch (IOException e) {
               e.printStackTrace();

               return null;
            }

            newoffsets[i] = currentOffset;
            currentOffset += defaultPageSize;
         }

         // Create the new page cache.
         PageCache pc = new PageCache(newpages, newoffsets, defaultPageSize);

         // Get the tables we will start with.
         MutableTable newTable = (MutableTable) pc.getPageAt(0, -1).getTable();
         int newoffset = pc.getOffsetAt(0);
         MutableTable origTable =
            (MutableTable) this.getPageAt(0, -1).getTable();
         int myoffset = this.getOffsetAt(0);
         int[] coltypes = new int[numColumns];

         for (int ci = 0; ci < numColumns; ci++) {
            coltypes[ci] = origTable.getColumnType(ci);
         }

         // Now we have the data, transfer the data from one table to the other.
         // We really have to transfer the data in it's native format. We know
         // these tables are huge, and the alternative it to read the data as an
         // object, and then write it to the other table as an object. Man!
         // that's a lot of objects to allocate!!!
         for (int i = 0; i < subset.length; i++) {
            int newlocation = subset[i];

            for (int col = 0; col < numColumns; col++) {

               switch (coltypes[col]) {

                  case ColumnTypes.BOOLEAN: {

                     // get the old data.
                     boolean a;

                     try {
                        a = origTable.getBoolean(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getBoolean(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setBoolean(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setBoolean(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.BYTE: {

                     // get the old data.
                     byte a;

                     try {
                        a = origTable.getByte(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getByte(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setByte(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setByte(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.BYTE_ARRAY: {

                     // get the old data.
                     byte[] a;

                     try {
                        a = origTable.getBytes(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getBytes(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setBytes(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setBytes(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.CHAR: {

                     // get the old data.
                     char a;

                     try {
                        a = origTable.getChar(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getChar(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setChar(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setChar(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.CHAR_ARRAY: {

                     // get the old data.
                     char[] a;

                     try {
                        a = origTable.getChars(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getChars(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setChars(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setChars(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.DOUBLE: {

                     // get the old data.
                     double a;

                     try {
                        a = origTable.getDouble(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getDouble(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setDouble(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setDouble(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.FLOAT: {

                     // get the old data.
                     float a;

                     try {
                        a = origTable.getFloat(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getFloat(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setFloat(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setFloat(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.SHORT: {

                     // get the old data.
                     short a;

                     try {
                        a = origTable.getShort(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getShort(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setShort(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setShort(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.INTEGER: {

                     // get the old data.
                     int a;

                     try {
                        a = origTable.getInt(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getInt(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setInt(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setInt(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.LONG: {

                     // get the old data.
                     long a;

                     try {
                        a = origTable.getLong(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getLong(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setLong(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setLong(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.NOMINAL:
                  case ColumnTypes.STRING: {

                     // get the old data.
                     String a;

                     try {
                        a = origTable.getString(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getString(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setString(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setString(a, i - newoffset, col);
                     }

                     break;
                  }

                  case ColumnTypes.OBJECT: {

                     // get the old data.
                     Object a;

                     try {
                        a = origTable.getObject(subset[i] - myoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        origTable =
                           (MutableTable) this.getPageAt(subset[i], myoffset)
                              .getTable();
                        myoffset = this.getOffsetAt(subset[i]);
                        a = origTable.getObject(subset[i] - myoffset, col);
                     }

                     // set the new data.
                     try {
                        newTable.setObject(a, i - newoffset, col);
                     } catch (ArrayIndexOutOfBoundsException aiob) {
                        newTable =
                           (MutableTable) pc.getPageAt(i, newoffset).getTable();
                        newoffset = this.getOffsetAt(i);
                        newTable.setObject(a, i - newoffset, col);
                     }

                     break;
                  }
               }
            } // end for
         } // end for

         return new PageCache(newpages, newoffsets, this.defaultPageSize);
      } // end if
   } // end method copy

   /**
    * Creates a new <code>Table</code> for a <code>Page</code> of the given
    * size, usually the default page size.
    *
    * @param  size The number of rows in the resulting <code>Table</code>
    *
    * @return The new <code>Table</code>
    */
   Table createNewTable(int size) {
      Table t = pages[0].getTable();
      this.freeIfUnreferenced(0);

      Column[] columns = new Column[t.getNumColumns()];

      for (int i = 0; i < t.getNumColumns(); i++) {

         try {
            columns[i] = (Column) t.getColumn(i).getClass().newInstance();
         } catch (InstantiationException e) {
            e.printStackTrace();

            return null;
         } catch (IllegalAccessException e) {
            e.printStackTrace();

            return null;
         }

         columns[i].setNumRows(size);
      }

      return new MutableTableImpl(columns);
   }

   /**
    * Returns a reference to the column comment.
    *
    * @param  columnIndex Index of the column
    *
    * @return The column coment
    */
   String getColumnComment(int columnIndex) {
      return this.columnComments[columnIndex];
   }

   /**
    * Returns a reference to the column label.
    *
    * @param  columnIndex Index of the column
    *
    * @return The column label
    */
   String getColumnLabel(int columnIndex) {
      return this.columnLabels[columnIndex];
   }

   /**
    * Gets the number of rows in the entire cache.
    *
    * @return Number of rows in the entire cache
    */
   int getNumRows() {
      int last = offsets.length - 1;
      Table t = pages[last].getTable();
      int len = pages[last].getSubset().length;

      return offsets[last] + len;
   }

   /**
    * Returns the offset of the <code>Table</code> at the given index.
    *
    * @param  offset Index of the entry
    *
    * @return Offset of the <code>Table</code> at the given entry.
    */
   int getOffsetAt(int offset) {

      if (this.offsets.length == 0) {
         return 0;
      }

      return this.offsets[this.getPageIndex(offset)];
   }

   /**
    * Gets the <code>Page</code> at the given offset.
    *
    * @param  row            The row attempting to access
    * @param  previousOffset Offset of the previously accessed table
    *
    * @return <code>Page</code> at the given offset, or null if there is not
    *         such row.
    */
   Page getPageAt(int row, int previousOffset) {

      // If nobody is referencing the previous page, page it out.
      int previous = this.getPageIndex(previousOffset);
      int which;

      if (previous >= 0) {

         if ((--references[previous]) == 0) {
            pages[previous].free();
         }

         // If practical, use the previous offset as a hint where to start
         // searching.
         if (offsets.length > 2) {
            previous =
               previous < offsets.length - 2 ? previous + 1
                                             : offsets.length - 2;
            which = this.getPageIndexWithHint(row, previous);
         } else {
            which = this.getPageIndex(row);
         }
      } else {
         which = this.getPageIndex(row);
      }

      // check to see if the row even exists. If it does not
      // return null.
      if (which == -1) {
         return nonPage;
      }

      references[which]++;

      return pages[which];
   } // end method getPageAt

   /**
    * Gets the <code>Page</code> at the given offset.
    *
    * @param  row            The row attempting to access
    * @param  previousOffset Offset of the previously accessed table
    *
    * @return <code>Page</code> at the given offset, or null if there is not
    *         such row.
    */
   Page getPageAtAndPrefetch(int row, int previousOffset) {

      // If nobody is referencing the previous page, page it out.
      int previous = this.getPageIndex(previousOffset);

      if (previous >= 0 && (--references[previous]) == 0) {
         pages[previous].free();
      }

      // return the new page table.
      final int which = this.getPageIndex(row);

      // check to see if the row even exists. If it does not
      // return null.
      if (which == -1) {
         return nonPage;
      }

      references[which]++;

      // Do a prefetch, on a separate thread fetch the table.
      new Thread() {
            public void run() { pages[which + 1].getTable(); }
         }.start();

      return pages[which];
   } // end method getPageAtAndPrefetch

   /**
    * Returns true if the specified column is nominal.
    *
    * @param  columnIndex Column index of the column to check
    *
    * @return True if the column is nominal
    */
   boolean isColumnNominal(int columnIndex) {
      return this.columnNominal[columnIndex];
   }

   /**
    * Returns true if the specified column is scalar.
    *
    * @param  columnIndex Column index of the column to check
    *
    * @return True if the column is scalar
    */
   boolean isColumnScalar(int columnIndex) {
      return this.columnScalar[columnIndex];
   }

   /**
    * Given a subset array, this method will create a subset that represents the
    * subset. Starting with the value entry in rows specified by
    * firstIndexInRows, this method will determine how big the resulting subset
    * array is, it will allocate it and initialize its values. We will identify
    * the first entry in the original subset array to be included, the index of
    * the last entry to be included. We then allocate the array of the
    * appropriate size, and copy the entries from the original subset to the new
    * subset array. Also, the original subset array is offset by currentOffset,
    * so appropriate adjustments are made.
    *
    * @param  rows             Subset rows
    * @param  firstIndexInRows First index in rows
    * @param  current          Current
    * @param  currentOffset    Current offset
    *
    * @return Subset that represents the specified subset.
    */
   int[] populateSubset(int[] rows, int firstIndexInRows, int[] current,
                        int currentOffset) {
      int firstIndexInCurrent = rows[firstIndexInRows] - currentOffset;
      int lastValueInCurrent = (current.length - 1) + currentOffset;
      int lastIndexInRows = this.getLastEntry(lastValueInCurrent, rows);
      int numEntriesInRows = lastIndexInRows - firstIndexInRows;
      int[] newindices = new int[numEntriesInRows];

      // Now populate the new subset.
      for (int i = 0, ri = firstIndexInRows; i < numEntriesInRows;) {
         newindices[i++] = current[rows[firstIndexInRows] - currentOffset];
         firstIndexInRows++;

      }

      return newindices;
   }

   /**
    * Removes entries of a subset.
    *
    * @param  ss      The subset
    * @param  where   Where to remove
    * @param  howmany Number to remove
    *
    * @return The new subset
    */
   int[] removeSubsetEntries(int[] ss, int where, int howmany) {
      int remainder = ss.length - howmany;
      int[] newss = new int[remainder];
      System.arraycopy(ss, 0, newss, 0, where);
      System.arraycopy(ss, where + howmany, newss, where, remainder - where);

      return newss;
   }

   /**
    * Sets a column to nominal or not.
    *
    * @param value       True if the column is to be nominal
    * @param columnIndex The index of the column to set
    */
   void setColumnIsNominal(boolean value, int columnIndex) {
      this.columnNominal[columnIndex] = value;
   }

   /**
    * Set the column to scalar or not.
    *
    * @param value       true if the column is to be scalar.
    * @param columnIndex the index of the column to set up.
    */
   void setColumnIsScalar(boolean value, int columnIndex) {
      this.columnScalar[columnIndex] = value;
   }

   /**
    * When we subset a paging table, the offset arrays will reflect the table
    * view of the data, not the physical arrangement of the data. In this way,
    * the searches will be much faster.
    *
    * @param  rows Subset rows
    *
    * @return New PageCache
    *
    * @throws IOException If a problem occurs while creating the page cache
    */
   PageCache subset(int[] rows) throws IOException {

      // First, sort the array.
      Arrays.sort(rows);

      Page[] newpages = new Page[this.pages.length];
      int[] newoffsets = new int[this.pages.length];
      int newPageCount = 0;

      // Get the page at start, and it's offsets.
      int which = this.getPageIndex(rows[0]);
      Page currentPage = this.pages[which];
      int[] currentSubset = currentPage.getSubset();
      this.freeIfUnreferenced(which);

      int currentOffset = this.offsets[which];

      // Copy the subset of the subset into a new subset array.
      int[] newsubset =
         this.populateSubset(rows, 0, currentSubset, currentOffset);

      // We have the new subset, the old table and page, now
      // we create a new page sharing the same page data file but
      // a different offset file.
      newpages[newPageCount] =
         new Page(currentPage.pageFile, currentPage.numRows,
                  currentPage.numColumns, newsubset);
      newoffsets[newPageCount++] = 0;

      int total = newsubset.length;
      int len = rows.length;

      while (total < len) {
         which++;

         // Get the page at start, and it's offsets.
         currentPage = this.pages[which];
         currentSubset = currentPage.getSubset();
         this.freeIfUnreferenced(which);
         currentOffset = this.offsets[which];
         newsubset =
            this.populateSubset(rows, total, currentSubset, currentOffset);

         // We have the new subset, the old table and page, now
         // we create a new page sharing the same page data file but
         // a different offset file.
         newpages[newPageCount] =
            new Page(currentPage.pageFile, currentPage.numRows,
                     currentPage.numColumns, newsubset);
         newoffsets[newPageCount++] = total;
         total += newsubset.length;
      }

      // Create the accuratly sized pages and offsets array.
      Page[] tmpp = new Page[newPageCount];
      int[] tmpo = new int[newPageCount];
      System.arraycopy(newpages, 0, tmpp, 0, newPageCount);
      System.arraycopy(newoffsets, 0, tmpo, 0, newPageCount);

      return new PageCache(tmpp, tmpo, this.defaultPageSize);
   } // end method subset


   /**
    * When we subset a paging table, the offsets arrays will reflect the table
    * view of the data, not the physical arrangement of the data. In this way,
    * the searches will be much faster.
    *
    * @param  start Start of the subset
    * @param  len   Length of the subset
    *
    * @return New PageCache
    *
    * @throws IOException If a problem occurs while creating the page cache
    */
   PageCache subset(int start, int len) throws IOException {
      Page[] newpages = new Page[this.pages.length];
      int[] newoffsets = new int[this.pages.length];
      int newPageCount = 0;

      // Get the page at start, and it's offsets.
      int which = this.getPageIndex(start);
      Page currentPage = this.pages[which];
      Table currentTable = currentPage.getTable();
      int[] currentSubset = currentPage.getSubset();
      int currentOffset = this.offsets[which];
      this.freeIfUnreferenced(which);

      // Copy the subset of the subset into a new subset array.
      int offset = start - currentOffset;
      int amount = currentSubset.length - offset;
      amount = amount > len ? len : amount;

      int[] newsubset = new int[amount];
      System.arraycopy(currentSubset, offset, newsubset, 0, amount);

      // We have the new subset, the old table and page, now
      // we create a new page sharing the same page data file but
      // a different offset file.
      newpages[newPageCount] =
         new Page(currentPage.pageFile, currentTable, newsubset);
      newoffsets[newPageCount++] = 0;

      int total = amount;

      while (total < len) {
         which++;

         // Get the page at start, and it's offsets.
         currentPage = this.pages[which];
         currentTable = currentPage.getTable();
         currentSubset = currentPage.getSubset();
         amount = currentSubset.length;
         amount = amount > len - total ? len - total : amount;
         newsubset = new int[amount];
         System.arraycopy(currentSubset, 0, newsubset, 0, amount);

         // We have the new subset, the old table and page, now
         // we create a new page sharing the same page data file but
         // a different offset file.
         newpages[newPageCount] =
            new Page(currentPage.pageFile, currentTable, newsubset);
         newoffsets[newPageCount++] = total;
         total += amount;
         this.freeIfUnreferenced(which);
      }

      // Create the accuratly sized pages and offsets array.
      Page[] tmpp = new Page[newPageCount];
      int[] tmpo = new int[newPageCount];
      System.arraycopy(newpages, 0, tmpp, 0, newPageCount);
      System.arraycopy(newoffsets, 0, tmpo, 0, newPageCount);

      return new PageCache(tmpp, tmpo, this.defaultPageSize);
   } // end method subset

   /**
    * Adds a column. The column passed in is expected to be only a template. It
    * should define the type and the label and comment, however, it is not
    * expected to actually contain the data, since the data would likely be much
    * to large to fit in memory. The column should be populated using the writer
    * methods of the table.
    *
    * @param col The new column.
    */
   public void addColumn(Column col) {

      // Add the column to each page, set all values to missing.
      Class colClass = col.getClass();
      int len = pages.length;

      for (int i = 0; i < len; i++) {
         MutableTable t = (MutableTable) pages[i].getTable();

         try {
            col = (Column) colClass.newInstance();
         } catch (InstantiationException e) {
            e.printStackTrace();

            return;
         } catch (IllegalAccessException e) {
            e.printStackTrace();

            return;
         }

         col.setNumRows(t.getNumRows());
         t.addColumn(col);
         pages[i].mark(true);
         this.freeIfUnreferenced(i);
      }

      // Update the column info.
      this.columnAdded(col);
   } // end method addColumn


   /**
    * Adds multiple columns. The columns passed in is expected to be only a
    * template. It should define the type and the label and comment, however, it
    * is not expected to actually contain the data, since the data would likely
    * be much to large to fit in memory. The columns should be populated using
    * the writer methods of the table.
    *
    * @param cols The new columns.
    */
   public void addColumns(Column[] cols) {

      // Make an array of classes to add.
      Class[] colClasses = new Class[cols.length];

      for (int i = 0; i < colClasses.length; i++) {
         colClasses[i] = cols[i].getClass();
      }

      // For each page.
      int len = pages.length;

      for (int i = 0; i < len; i++) {
         MutableTable t = (MutableTable) pages[i].getTable();

         for (int j = 0; j < cols.length; j++) {

            try {
               cols[j] = (Column) colClasses[i].newInstance();
            } catch (InstantiationException e) {
               e.printStackTrace();

               return;
            } catch (IllegalAccessException e) {
               e.printStackTrace();

               return;
            }

            cols[j].setNumRows(t.getNumRows());
         }

         t.addColumns(cols);
         pages[i].mark(true);
         this.freeIfUnreferenced(i);
      }

      // update the info
      for (int i = 0; i < cols.length; i++) {
         this.columnAdded(cols[i]);
      }
   } // end method addColumns

   /**
    * Adds the rows to the last page.
    *
    * @param howMany Number of rows to add
    */
   public void addRows(int howMany) {
      int last = pages.length - 1;

      // add rows to the table.
      MutableTable t = (MutableTable) pages[last].getTable();
      int tablesize = t.getNumRows();
      t.addRows(howMany);

      // update the subset.
      int[] no = pages[last].getSubset();
      int[] newss = new int[no.length + howMany];
      System.arraycopy(no, 0, newss, 0, no.length);

      int old_len = no.length;

      for (int i = 0; i < howMany; i++) {
         newss[old_len + i] = tablesize + i;
      }

      pages[last].subset = newss;
      pages[last].mark(true);
      this.freeIfUnreferenced(last);
   }

   /**
    * Inserts a column. The column passed in is expected to be only a template.
    * It should define the type and the label and comment, however, it is not
    * expected to actually contain the data, since the data would likely be much
    * to large to fit in memory. The column should be populated using the writer
    * methods of the table.
    *
    * @param col   The new column
    * @param where The index of the column to replace
    */
   public void insertColumn(Column col, int where) {

      // Add the column to each page, set all values to missing.
      Class colClass = col.getClass();
      int len = pages.length;

      for (int i = 0; i < len; i++) {
         MutableTable t = (MutableTable) pages[i].getTable();

         try {
            col = (Column) colClass.newInstance();
         } catch (InstantiationException e) {
            e.printStackTrace();

            return;
         } catch (IllegalAccessException e) {
            e.printStackTrace();

            return;
         }

         col.setNumRows(t.getNumRows());
         t.insertColumn(col, where);
         pages[i].mark(true);
         this.freeIfUnreferenced(i);
      }

      this.columnInserted(col, where);
   } // end method insertColumn

   /**
    * Inserts multiple columns. The columns passed in is expected to be only a
    * template. It should define the type and the label and comment, however, it
    * is not expected to actually contain the data, since the data would likely
    * be much to large to fit in memory. The columns should be populated using
    * the writer methods of the table.
    *
    * @param cols  The new column
    * @param where Index of the column to replace
    */
   public void insertColumns(Column[] cols, int where) {

      // Make an array of classes to add.
      Class[] colClasses = new Class[cols.length];

      for (int i = 0; i < colClasses.length; i++) {
         colClasses[i] = cols[i].getClass();
      }

      // For each page.
      int len = pages.length;

      for (int i = 0; i < len; i++) {
         MutableTable t = (MutableTable) pages[i].getTable();

         for (int j = 0; j < cols.length; j++) {

            try {
               cols[j] = (Column) colClasses[i].newInstance();
            } catch (InstantiationException e) {
               e.printStackTrace();

               return;
            } catch (IllegalAccessException e) {
               e.printStackTrace();

               return;
            }

            cols[j].setNumRows(t.getNumRows());
         }

         t.insertColumns(cols, where);
         pages[i].mark(true);
         this.freeIfUnreferenced(i);
      }

      // update the info
      for (int i = 0; i < cols.length; i++) {
         this.columnInserted(cols[i], where + i);
      }
   } // end method insertColumns

   /**
    * Removes a column from each table.
    *
    * @param position Location the column should be removed
    */
   public void removeColumn(int position) {

      // remove the column from each page
      int len = pages.length;

      for (int i = 0; i < len; i++) {
         MutableTable t = (MutableTable) pages[i].getTable();
         t.removeColumn(position);
         pages[i].mark(true);
         this.freeIfUnreferenced(i);
      }

      this.columnRemoved(position);
   }

   /**
    * Remove len columns starting at start.
    *
    * @param start First column to delete.
    * @param len   Number of columns to delete.
    */
   public void removeColumns(int start, int len) {

      // remove the column from each page
      int numPages = pages.length;

      for (int i = 0; i < numPages; i++) {
         MutableTable t = (MutableTable) pages[i].getTable();
         t.removeColumns(start, len);
         pages[i].mark(true);
         this.freeIfUnreferenced(i);
      }

      // Remove the data for the columns removed.
      for (int i = 0; i < len; i++) {
         this.columnRemoved(start);
      }
   }

   /**
    * Removes a row. Takes the row out of the table, and then decrements each of
    * the offsets for the subsequent tables offsets.
    *
    * @param row Row to remove
    */
   public void removeRow(int row) {
      int whichPage = this.getPageIndex(row);
      MutableTable mt = (MutableTable) pages[whichPage].getTable();
      mt.removeRow(row);
      pages[whichPage].mark(true);
      this.freeIfUnreferenced(whichPage);

      // Update the offsets.
      for (int i = whichPage + 1; i < pages.length; i++) {
         offsets[i]--;
      }
   }

   /**
    * Removes the specified number of rows.
    *
    * @param start Index to start removing rows
    * @param len   Number of rows to remove
    */
   public void removeRows(int start, int len) {
      int currentPage = this.getPageIndex(start);

      // The first table, we remove starting from an offset, the
      // remaining rows we remove starting at zero.
      int offset = start - this.offsets[currentPage];
      MutableTable t = (MutableTable) pages[currentPage].getTable();

      if (t.getNumColumns() == 0) {
         return; // there is nothing to do if there are no columns.
      }

      int[] subset = pages[currentPage].getSubset();
      int total = subset.length - offset;
      int howmany = total < len ? total : len;
      subset = this.removeSubsetEntries(subset, offset, howmany);
      pages[currentPage].subset = subset;
      pages[currentPage].mark(true);
      this.freeIfUnreferenced(currentPage);

      // update the offset of the next page if we can.
      currentPage++;

      // Now compute how many rows remain to be removed.
      howmany = len - howmany;

      // remove rows from consecutive tables until we have
      // removed the right amount.
      while (currentPage < pages.length) {
         subset = pages[currentPage].getSubset();

         int numberRows = subset.length;

         if (howmany != 0) {

            if (numberRows > howmany) {
               subset = this.removeSubsetEntries(subset, 0, howmany);
               pages[currentPage].subset = subset;
               pages[currentPage].mark(true);
               this.freeIfUnreferenced(currentPage);
               offsets[currentPage] -= len - howmany;
               howmany = 0;
            } else {
               subset = this.removeSubsetEntries(subset, 0, numRows);
               pages[currentPage].subset = subset;
               pages[currentPage].mark(true);
               this.freeIfUnreferenced(currentPage);
               offsets[currentPage] -= len - howmany;
               howmany -= numberRows;
            }
         } else {
            offsets[currentPage] -= len;
         }

         currentPage++;
      } // end while
   } // end method removeRows

   /**
    * Makes a copy of the table with the columns in a new order. This requires
    * us to make a copy of the <code>PageCache</code> object.
    *
    * @param  newOrder Column indices in a new order
    *
    * @return Copy of the table with the columns in a new order
    */
   public PageCache reorderColumns(int[] newOrder) {

      // remove the column from each page
      int numPages = pages.length;
      Page[] newpages = new Page[pages.length];

      for (int i = 0; i < numPages; i++) {

         // Make a deep copy of the table.
         MutableTable t = (MutableTable) pages[i].getTable().copy();
         this.freeIfUnreferenced(i);
         t.reorderColumns(newOrder);

         try {
            newpages[i] = new Page(t, false);
         } catch (IOException e) {
            e.printStackTrace();

            return null;
         }
      }

      int[] newoffsets = new int[offsets.length];
      System.arraycopy(offsets, 0, newoffsets, 0, offsets.length);

      return new PageCache(newpages, newoffsets, this.defaultPageSize);
   } // end method reorderColumns

   /**
    * Sets a column. The column passed in is expected to be only a template. It
    * should define the type and the label and comment, however, it is not
    * expected to actually contain the data, since the data would likely be much
    * to large to fit in memory. The column should be populated using the writer
    * methods of the table.
    *
    * @param col   The new column
    * @param where The index of the column to replace
    */
   public void setColumn(Column col, int where) {

      // Add the column to each page, set all values to missing.
      Class colClass = col.getClass();
      int len = pages.length;

      for (int i = 0; i < len; i++) {
         MutableTable t = (MutableTable) pages[i].getTable();

         try {
            col = (Column) colClass.newInstance();
         } catch (InstantiationException e) {
            e.printStackTrace();

            return;
         } catch (IllegalAccessException e) {
            e.printStackTrace();

            return;
         }

         col.setNumRows(t.getNumRows());
         t.setColumn(col, where);
         pages[i].mark(true);
         this.freeIfUnreferenced(i);
      }

      // update the column info
      this.setColumnLabel(col.getLabel(), where);
      this.setColumnComment(col.getComment(), where);
      this.setColumnIsScalar(col.getIsScalar(), where);
      this.setColumnIsNominal(col.getIsNominal(), where);
   } // end method setColumn

   /**
    * Sets a column comment.
    *
    * @param com      New commment
    * @param position The position
    */
   public void setColumnComment(String com, int position) {
      this.columnComments[position] = com;
   }

   /**
    * Sets a column label.
    *
    * @param label    New label
    * @param position The position.
    */
   public void setColumnLabel(String label, int position) {
      this.columnLabels[position] = label;
   }

   /**
    * Swaps the position of two columns.
    *
    * @param pos1 Position of the first column to swap
    * @param pos2 Position of the second column to swap
    */
   public void swapColumns(int pos1, int pos2) {

      // remove the column from each page
      int len = pages.length;

      for (int i = 0; i < len; i++) {
         MutableTable t = (MutableTable) pages[i].getTable();
         t.swapColumns(pos1, pos2);

         // mark dirty and free them up
         pages[i].mark(true);
         this.freeIfUnreferenced(i);
      }
   }

   /**
    * Swaps the position of two rows. They can potentially be in different
    * pages, but it doesn't matter, we treat them the same either way.
    *
    * @param pos1 Position of the first row to swap
    * @param pos2 Position of the second row to swap
    */
   public void swapRows(int pos1, int pos2) {

      // Get the page indices, the tables, and the offsets of the positions in
      // those tables.
      int firstIndex = this.getPageIndex(pos1);
      int secondIndex = this.getPageIndex(pos2);
      MutableTable firstTable = (MutableTable) pages[firstIndex].getTable();
      MutableTable secondTable = (MutableTable) pages[secondIndex].getTable();
      int firstOffset = pos1 - offsets[firstIndex];
      int secondOffset = pos2 = offsets[secondIndex];

      int nc = firstTable.getNumColumns();

      for (int i = 0; i < nc; i++) {
         Object obj = firstTable.getObject(firstOffset, i);
         firstTable.setObject(secondTable.getObject(secondOffset, i),
                              firstOffset, i);
         secondTable.setObject(obj, secondOffset, i);

         // swap missing values.
         boolean missing1 = firstTable.isValueMissing(pos1, i);
         boolean missing2 = secondTable.isValueMissing(pos2, i);
         firstTable.setValueToMissing(missing2, pos1, i);
         secondTable.setValueToMissing(missing1, pos2, i);

      }

      // Dirty and free up.
      pages[firstIndex].mark(true);
      this.freeIfUnreferenced(firstIndex);
      pages[secondIndex].mark(true);
      this.freeIfUnreferenced(secondIndex);
   } // end method swapRows
} // end class PageCache
