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

import ncsa.d2k.modules.core.datatype.table.AbstractTable;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.NumericColumn;
import ncsa.d2k.modules.core.datatype.table.Row;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.TableFactory;
import ncsa.d2k.modules.core.datatype.table.Transformation;
import ncsa.d2k.modules.core.datatype.table.basic.IntColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.SubsetTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.TableImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>A paging table is a collection of tabular data where not all the data
 * resides in memory simultaneously. The data is segmented into pages of data,
 * each of approximately equal size. The number of table rows each page contains
 * is user configurable. As the data is being accessed, only those pages that
 * are being accessed are in memory.</p>
 *
 * <p>There are several caveats with this table implementation:</p>
 *
 * <ul>
 *   <li>Paging tables are slow, since the data must be read and re-read from
 *     disk. This impacts average access time significantly, since periodically
 *     page faults will be encountered, and the next page of data must be read
 *     in.</li>
 *   <li>Paging tables are designed for sequential access, not random access.
 *     Random access to paging tables will result in frequent page faults and
 *     disk thrashing.</li>
 *   <li>Only single threaded write access is supported. Multithreaded writes
 *     may result in corrupted data.</li>
 *   <li>Limitations on the volumn of data that can be addressed does still
 *     exist. Currently subset must be initialized by a single array, and that
 *     single array must fit in memory. However, as the array is parsed, it is
 *     spooled off to disk along with the pages of data so that it is not
 *     required to keep it in memory. However, when it is first declared it must
 *     fit in memory. There are also limitations in that the table data accessor
 *     methods take an integer index which is 32 bits.</li>
 *   <li>Although the PageCache object can be shared among many PagingTables, a
 *     single SubsetPagingTable is not intended for multithreaded access.
 *     However, shallow copies can be used in a multi-threaded fashion.</li>
 * </ul>
 *
 * <p>A paging table contains a reference to a PageCache object. This object
 * contains an array of Page objects. Pages are responsible for reading and
 * writing the pages to the disk. It will contain a reference to the table and
 * subset array as long as they are needed. When the PageCache determins that
 * the page is no longer needed, it will tell the Page to purge it's data. The
 * SubsetPagingTable has a reference to the Page currently being referenced.
 * From the page, it gets the table, subset and columns currently being accessed
 * by it's accessor methods.</p>
 *
 * @author  redman
 * @version $Revision$, $Date$
 */
public class SubsetPagingTable extends AbstractTable implements MutableTable {

   //~ Static fields/initializers **********************************************

   /** Default page size. */
   static final int DEFAULT_PAGESIZE = 50000;

   //~ Instance fields *********************************************************

   /** Cache for the page. */
   PageCache cache;

   /** The current page. */
   Page currentPage;

   /** Offset of this page. */
   int offset = -1;

   /** The subset. */
   int[] subset;

   /** Page we are looking at. */
   Table table;

   /** List of transformations performed. */
   ArrayList transformations = new ArrayList();

   /** These are the columns included here only for efficiency. */
   protected Column[] columns;

   //~ Constructors ************************************************************

   /**
    * Creates an empty paging table.
    */
   public SubsetPagingTable() {
      Page[] pages = new Page[1];
      int[] offsets = new int[1];
      offsets[0] = 0;

      try {
         pages[0] = new Page(new MutableTableImpl(), true);
      } catch (IOException e) {
         e.printStackTrace();
      }

      cache = new PageCache(pages, offsets, DEFAULT_PAGESIZE);
      this.offset = -1;
   }

   /**
    * Constructor taking only a <code>PageCache</code>. The first data access
    * will cause a page fault.
    *
    * @param pager <code>PageCache</code> to use fo rthis paging table
    */
   public SubsetPagingTable(PageCache pager) {
      this.cache = pager;
      this.offset = -1;
      this.getPage(0);
   }

   //~ Methods *****************************************************************

   /**
    * This is the initial test for paging tables, but it can also serve an
    * example of how to create and use paging tables.
    *
    * @param args The first arg is the table size, second is the number of
    *             pages, and the last, if present is the location of the pages.
    */
   static public void main(String[] args) {
      int tableSize = Integer.parseInt(args[0]);
      int numTables = Integer.parseInt(args[1]);

      // Now let's do a regular table to compare time.
      Column[] cols = new Column[4];
      cols[0] = new IntColumn(tableSize);
      cols[1] = new IntColumn(tableSize);
      cols[2] = new IntColumn(tableSize);
      cols[3] = new IntColumn(tableSize);

      MutableTableImpl mti = new MutableTableImpl(cols);

      for (int i = 0; i < tableSize; i++) {
         mti.setInt(i, i, 0);
         mti.setInt(i, i, 1);
         mti.setInt(i, i, 2);
         mti.setInt(i, i, 3);
      }

      System.out.println();
      System.out.println("--------------- Doing a mutable table ----------------");

      long start = System.currentTimeMillis();

      for (int whichTable = 0; whichTable < numTables; whichTable++) {

         for (int i = 0; i < tableSize; i++) {
            int value = mti.getInt(i, 0);

            if (value != i) {
               System.out.println("Data wrong, row = " + i +
                                  " column = 0 value = " + value);
            }

            value = mti.getInt(i, 1);

            if (value != i) {
               System.out.println("Data wrong, row = " + i +
                                  " column = 1 value = " + value);
            }

            value = mti.getInt(i, 2);

            if (value != i) {
               System.out.println("Data wrong, row = " + i +
                                  " column = 2 value = " + value);
            }

            value = mti.getInt(i, 3);

            if (value != i) {
               System.out.println("Data wrong, row = " + i +
                                  " column = 3 value = " + value);
            }
         } // end for
      } // end for

      System.out.println("Total access for mutable table took " +
                         (System.currentTimeMillis() - start));
      mti = null;

      // Now let's do a subset table to compare time.
      cols = new Column[4];
      cols[0] = new IntColumn(tableSize);
      cols[1] = new IntColumn(tableSize);
      cols[2] = new IntColumn(tableSize);
      cols[3] = new IntColumn(tableSize);

      SubsetTableImpl sti = new SubsetTableImpl(cols);

      for (int i = 0; i < tableSize; i++) {
         sti.setInt(i, i, 0);
         sti.setInt(i, i, 1);
         sti.setInt(i, i, 2);
         sti.setInt(i, i, 3);
      }

      System.out.println();
      System.out.println("--------------- Doing a subset table ----------------");
      start = System.currentTimeMillis();

      for (int whichTable = 0; whichTable < numTables; whichTable++) {

         for (int i = 0; i < tableSize; i++) {
            int value = sti.getInt(i, 0);

            if (value != i) {
               System.out.println("Data wrong, row = " + i +
                                  " column = 0 value = " + value);
            }

            value = sti.getInt(i, 1);

            if (value != i) {
               System.out.println("Data wrong, row = " + i +
                                  " column = 1 value = " + value);
            }

            value = sti.getInt(i, 2);

            if (value != i) {
               System.out.println("Data wrong, row = " + i +
                                  " column = 2 value = " + value);
            }

            value = sti.getInt(i, 3);

            if (value != i) {
               System.out.println("Data wrong, row = " + i +
                                  " column = 3 value = " + value);
            }
         } // end for
      } // end for

      System.out.println("Total access for subset table took " +
                         (System.currentTimeMillis() - start));
      sti = null;

      System.out.println("Constructing pages...");

      Page[] pages = new Page[numTables];
      int[] offset = new int[numTables];

      for (int whichTable = 0; whichTable < numTables; whichTable++) {
         cols = new Column[4];
         cols[0] = new IntColumn(tableSize);
         cols[1] = new IntColumn(tableSize);
         cols[2] = new IntColumn(tableSize);
         cols[3] = new IntColumn(tableSize);
         mti = new MutableTableImpl(cols);

         for (int i = 0; i < tableSize; i++) {
            int val = i + (tableSize * whichTable);
            mti.setInt(val, i, 0);
            mti.setInt(val, i, 1);
            mti.setInt(val, i, 2);
            mti.setInt(val, i, 3);
         }

         try {
            pages[whichTable] = new Page(mti, false);
         } catch (IOException e) {
            e.printStackTrace();

            return;
         }

         offset[whichTable] = whichTable * tableSize;
      }

      // Create a paging table, check it's performance.
      PageCache pc = new PageCache(pages, offset, tableSize);
      ExamplePagingTable spt = new ExamplePagingTable(pc);
      start = System.currentTimeMillis();
      System.out.println();
      System.out.println("TABLE SIZE = " + spt.getNumRows());

      int size = spt.getNumRows();

      for (int i = 0; i < size; i++) {
         int value = spt.getInt(i, 0);

         if (value != i) {
            System.out.println("Data wrong, row = " + i +
                               " column = 0 value = " + value);
            System.exit(0);
         }

         value = spt.getInt(i, 1);

         if (value != i) {
            System.out.println("Data wrong, row = " + i +
                               " column = 1 value = " + value);
            System.exit(0);
         }

         value = spt.getInt(i, 2);

         if (value != i) {
            System.out.println("Data wrong, row = " + i +
                               " column = 2 value = " + value);
            System.exit(0);
         }

         value = spt.getInt(i, 3);

         if (value != i) {
            System.out.println("Data wrong, row = " + i +
                               " column = 3 value = " + value);
            System.exit(0);
         }
      } // end for

      long total = System.currentTimeMillis() - start;

      System.out.println("Total access for paging took " + total);

      long sum = 0;

      for (int i = 0; i < pages.length; i++) {
         sum += pages[i].fileReadTime;
      }

      System.out.println("Time spent on Data access = " + (total - sum));
   } // end method main

   /**
    * Grabs the next page and inits the columns.
    *
    * @param where Row we are to access next
    */
   protected void getPage(int where) {
      currentPage = cache.getPageAt(where, offset);
      this.table = currentPage.getTable();
      this.subset = currentPage.getSubset();
      this.offset = cache.getOffsetAt(where);
      columns = ((TableImpl) table).getRawColumns();
   }

   /**
    * Refreshes the page we are on now. This is done when the table has changed,
    * and we need to update our fields.
    */
   protected void refresh() {

      if (currentPage == null) {
         this.getPage(0);
      }

      this.table = currentPage.getTable();
      this.subset = currentPage.getSubset();
      this.offset = cache.getOffsetAt(offset);
      columns = ((TableImpl) table).getRawColumns();
   }

   /**
    * Adds a column. The column passed in is expected to be only a template. It
    * should define the type and the label and comment, however, it is not
    * expected to actually contain the data, since the data would likely be much
    * to large to fit in memory. The column should be populated using the writer
    * methods of the table.
    *
    * @param datatype The new column.
    */
   public void addColumn(Column datatype) {
      cache.addColumn(datatype);
      this.refresh();
   }

   /**
    * Adds new columns. The columns passed in is expected to be only a template.
    * It should define the type and the label and comment, however, it is not
    * expected to actually contain the data, since the data would likely be much
    * to large to fit in memory. The columns should be populated using the
    * writer methods of the table.
    *
    * @param datatype The new columns.
    */
   public void addColumns(Column[] datatype) {
      cache.addColumns(datatype);
      this.refresh();
   }

   /**
    * Adds rows to the last page.
    *
    * @param howMany How many rows to add
    */
   public void addRows(int howMany) {
      cache.addRows(howMany);
      this.refresh();
   }

   /**
    * Adds a reversible transformation.
    *
    * @param tm The transformation
    */
   public void addTransformation(Transformation tm) { transformations.add(tm); }

   /**
    * Makes a deep copy of this table.
    *
    * @return Deep copy of this table.
    */
   public Table copy() { return new SubsetPagingTable(cache.copy(null)); }

   /**
    * Copies only the specified rows.
    *
    * @param  rows Rows to copy
    *
    * @return <code>Table</code> with the specified rows copied
    */
   public Table copy(int[] rows) {
      int[] newsubset = new int[rows.length];

      for (int i = 0; i < rows.length; i++) {
         newsubset[i] = rows[i];
      }

      return new SubsetPagingTable(cache.copy(newsubset));
   }

   /**
    * Makes a copy of the table data from start for len.
    *
    * @param  start First entry of the new table
    * @param  len   Number of entries tto copy
    *
    * @return Copy of the table data from start for len
    */
   public Table copy(int start, int len) {
      int[] newsubset = new int[len];

      for (int i = 0; i < len; i++) {
         newsubset[i] = start + i;
      }

      return new SubsetPagingTable(cache.copy(newsubset));
   }

   /**
    * Creates a new table.
    *
    * @return New <code>SubsetPagingTable</code>
    */
   public MutableTable createTable() {
      Page[] pg = new Page[0];
      int[] os = new int[0];

      return new SubsetPagingTable(new PageCache(pg, os,
                                                 this.cache.defaultPageSize));

   }

   /**
    * Tests for equality with another <code>SubsetPagingTable.</code>
    *
    * @param  mt Object for comparing with this
    *
    * @return True if the passed in object equals this
    */
   public boolean equals(Object mt) {
      Table mti = (Table) mt;
      int numColumns = mti.getNumColumns();
      int numRows = mti.getNumRows();

      if (getNumColumns() != numColumns) {
         return false;
      }

      if (getNumRows() != numRows) {
         return false;
      }

      for (int i = 0; i < numRows; i++) {

         for (int j = 0; j < numColumns; j++) {

            if (!getString(i, j).equals(mti.getString(i, j))) {
               return false;
            }
         }
      }

      return true;
   }

   /**
    * Gets the boolean at the given row and column indices, reading a new page
    * if necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The boolean representation of the data at the given row and
    *         column.
    */
   public boolean getBoolean(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getBoolean(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getBoolean(where);
      }
   }

   /**
    * Gets the byte at the given row and column indices, reading a new page if
    * necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The byte representation of the data at the given row and column.
    */
   public byte getByte(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getByte(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getByte(where);
      }
   }

   /**
    * Gets the byte at the given row and column indices, reading a new page if
    * necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The byte representation of the data at the given row and column.
    */
   public byte[] getBytes(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getBytes(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getBytes(where);
      }
   }

   /**
    * Gets the object at the given row and column indices, reading a new page if
    * necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The object representation of the data at the given row and column.
    */
   public char getChar(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getChar(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getChar(where);
      }
   }

   /**
    * Gets the character at the given row and column indices, reading a new page
    * if necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The character representation of the data at the given row and
    *         column.
    */
   public char[] getChars(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getChars(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getChars(where);
      }
   }

   /**
    * Returns the current column at the given location, but it will only
    * represent a portion of the data in the table. This table pages because the
    * data is big, so it makes no sense to return all the data for the row, it
    * likely would not fit in memory.
    *
    * @param  n Index of the column
    *
    * @return Column at the specified location
    */
   public Column getColumn(int n) { return columns[n]; }

   /**
    * Returns the column comment.
    *
    * @param  position Column index
    *
    * @return Column comment
    */
   public String getColumnComment(int position) {
      return cache.getColumnComment(position);
   }

   /**
    * Returns the column label.
    *
    * @param  position Column index
    *
    * @return Column label
    */
   public String getColumnLabel(int position) {
      return cache.getColumnLabel(position);
   }

   /**
    * Returns the datatype of the column.
    *
    * @param  position Column index.
    *
    * @return Column datatype
    */
   public int getColumnType(int position) {
      return columns[position].getType();
   }

   /**
    * Gets the double at the given row and column indices, reading a new page if
    * necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The double representation of the data at the given row and column.
    */
   public double getDouble(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getDouble(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getDouble(where);
      }
   }

   /**
    * Gets the float at the given row and column indices, reading a new page if
    * necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The float representation of the data at the given row and column.
    */
   public float getFloat(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getFloat(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getFloat(where);
      }
   }

   /**
    * Gets the int at the given row and column indices, reading a new page if
    * necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The int representation of the data at the given row and column.
    */
   public int getInt(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getInt(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getInt(where);
      }
   }

   /**
    * Gets the long at the given row and column indices, reading a new page if
    * necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The long representation of the data at the given row and column.
    */
   public long getLong(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getLong(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getLong(where);
      }
   }

   /**
    * Gets the object at the given row and column indices, reading a new page if
    * necessary.
    *
    * @return The object representation of the data at the given row and column.
    */
   public int getNumColumns() { return table.getNumColumns(); }

   /**
    * Returns the total number of rows.
    *
    * @return Number of rows.
    */
   public int getNumRows() { return cache.getNumRows(); }

   /**
    * Gets the object at the given row and column indices, reading a new page if
    * necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The object representation of the data at the given row and column.
    */
   public Object getObject(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getObject(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getObject(where);
      }
   }

   /**
    * Currently does nothing but return null.
    *
    * @return Null
    */
   public Row getRow() { return null; }

   /**
    * Gets the short at the given row and column indices, reading a new page if
    * necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The short representation of the data at the given row and column.
    */
   public short getShort(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getShort(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getShort(where);
      }
   }

   /**
    * Gets the string at the given row and column indices, reading a new page if
    * necessary.
    *
    * @param  row    Row index
    * @param  column Column index
    *
    * @return The string representation of the data at the given row and column.
    */
   public String getString(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return columns[column].getString(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[column].getString(where);
      }
   }

   /**
    * Gets a subset of the indexed rows.
    *
    * @param  rows The rows to include
    *
    * @return A subset of the indexed rows
    */
   public Table getSubset(int[] rows) {
      PageCache fudge;

      try {
         fudge = cache.subset(rows);
      } catch (IOException e) {
         e.printStackTrace();

         return null;
      }

      return new SubsetPagingTable(fudge);
   }

   /**
    * Returns a subset of the data. The paging table returned will actually
    * replicate the data, because the subset array is stored with the data in an
    * out-of-memory page, so we are required to use a different page.
    *
    * @param  start Row index
    * @param  len   Column index
    *
    * @return The object representation of the data at the given row and column.
    */
   public Table getSubset(int start, int len) {

      try {
         return new SubsetPagingTable(cache.subset(start, len));
      } catch (IOException e) {
         e.printStackTrace();

         return null;
      }
   }

   /**
    * Not implemented. Returns null.
    *
    * @return null
    */
   public TableFactory getTableFactory() { return null; }

   /**
    * Return the <code>List</code> of transformations.
    *
    * @return <code>List</code> of transformations.
    */
   public List getTransformations() { return transformations; }

   /**
    * If there are any missing values in the table, returns true.
    *
    * @return True if there are any missing values in the table
    */
   public boolean hasMissingValues() {

      for (int row = 0; row < this.getNumRows(); row++) {

         for (int col = 0; col < this.getNumColumns(); col++) {

            if (this.isValueMissing(row, col)) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * If there are any missing values in the specified column of the table,
    * returns true.
    *
    * @param  columnIndex Index of the column to check for missing values
    *
    * @return True if there are any missing values in the table column
    */
   public boolean hasMissingValues(int columnIndex) {

      for (int row = 0; row < this.getNumRows(); row++) {

         if (this.isValueMissing(row, columnIndex)) {
            return true;
         }
      }

      return false;
   }

   /**
    * Inserts a column. The column passed in is expected to be only a template.
    * It should define the type and the label and comment, however, it is not
    * expected to actually contain the data, since the data would likely be much
    * to large to fit in memory. The column should be populated using the writer
    * methods of the table.
    *
    * @param col   New column
    * @param where Index of the column to replace
    */
   public void insertColumn(Column col, int where) {
      cache.insertColumn(col, where);
      this.refresh();
   }

   /**
    * Inserts multiple columns. The columns passed in is expected to be only a
    * template. It should define the type and the label and comment, however, it
    * is not expected to actually contain the data, since the data would likely
    * be much to large to fit in memory. The columns should be populated using
    * the writer methods of the table.
    *
    * @param datatype New columns
    * @param where    Insertion index
    */
   public void insertColumns(Column[] datatype, int where) {
      cache.insertColumns(datatype, where);
      this.refresh();
   }

   /**
    * If the column contains nominal data, returns true, otherwise false.
    *
    * @param  position Column index
    *
    * @return True if the column is nominal
    */
   public boolean isColumnNominal(int position) {
      return cache.isColumnNominal(position);
   }

   /**
    * Returns true if the column is numeric.
    *
    * @param  position Column index
    *
    * @return True if the column is numeric
    */
   public boolean isColumnNumeric(int position) {

      if (columns[position] instanceof NumericColumn) {
         return true;
      }

      return false;
   }

   /**
    * If the column contains scalar data, returns true, otherwise false.
    *
    * @param  position Column index
    *
    * @return True if the column is scalar
    */
   public boolean isColumnScalar(int position) {
      return cache.isColumnScalar(position);
   }

   /**
    * Returns true if the value is empty.
    *
    * @param  row Row to test
    * @param  col Column to test
    *
    * @return true if the value is empty.
    */
   public boolean isValueEmpty(int row, int col) {
      int where;

      try {
         where = subset[row - offset];

         return columns[col].isValueEmpty(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[col].isValueEmpty(where);
      }
   }

   /**
    * Returns true if the value is missing.
    *
    * @param  row Row to test
    * @param  col Column to test
    *
    * @return True if the value is missing
    */
   public boolean isValueMissing(int row, int col) {
      int where;

      try {
         where = subset[row - offset];

         return columns[col].isValueMissing(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return columns[col].isValueMissing(where);
      }
   }

   /**
    * Removes the column from the table.
    *
    * @param position Index of the column to remove
    */
   public void removeColumn(int position) {
      cache.removeColumn(position);
      this.refresh();
   }

   /**
    * Removes len columns starting at start.
    *
    * @param start First column to delete
    * @param len   Number of columns to delete
    */
   public void removeColumns(int start, int len) {
      cache.removeColumns(start, len);
      this.refresh();
   }

   /**
    * Removes a row. Takes the row out of the table, and then decrements each of
    * the offsets for the subsequent tables offsets.
    *
    * @param row Remove the row at the given index
    */
   public void removeRow(int row) {
      cache.removeRow(row);
      this.refresh();
   }

   /**
    * Removes the rows starting at start for len indices
    *
    * @param start Where to start removing
    * @param len   Number to remove
    */
   public void removeRows(int start, int len) {
      cache.removeRows(start, len);
      this.refresh();
   }

   /**
    * Makes a copy of the table with the columns in a new order. This requires us
    * to make a copy of the PageCache object.
    *
    * @param  newOrder New order of the columns
    *
    * @return <code>Table</code> with the columns reordered
    */
   public Table reorderColumns(int[] newOrder) {
      PageCache pc = cache.reorderColumns(newOrder);
      this.refresh();

      return new SubsetPagingTable(pc);
   }

   /**
    * Sets the boolean value at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    The row
    * @param column The column
    */
   public void setBoolean(boolean data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setBoolean(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setBoolean(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the byte value at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    The row
    * @param column The column
    */
   public void setByte(byte data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setByte(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setByte(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the bytes at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    the row
    * @param column the column
    */
   public void setBytes(byte[] data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setBytes(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setBytes(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the char value at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    The row
    * @param column The column
    */
   public void setChar(char data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setChar(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setChar(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the character array value at the given row and column to the value
    * provided.
    *
    * @param data   Value to set the entry to
    * @param row    the row
    * @param column the column
    */
   public void setChars(char[] data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setChars(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setChars(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the column. The column passed in is expected to be only a template. It
    * should define the type and the label and comment, however, it is not
    * expected to actually contain the data, since the data would likely be much
    * to large to fit in memory. The column should be populated using the writer
    * methods of the table.
    *
    * @param col   New column
    * @param where Index of the column to replace
    */
   public void setColumn(Column col, int where) {
      this.cache.setColumn(col, where);
      this.refresh();
   }

   /**
    * Sets the column comment.
    *
    * @param comment  New column comment for the column
    * @param position Column index
    */
   public void setColumnComment(String comment, int position) {
      cache.setColumnComment(comment, position);
   }

   /**
    * Set the a flag that indicates if the column is treated as nominal or not.
    *
    * @param value    New nominal value flag
    * @param position Column position
    */
   public void setColumnIsNominal(boolean value, int position) {
      cache.setColumnIsNominal(value, position);
   }

   /**
    * Set the a flag that indicates if the column is treated as scalar or not.
    *
    * @param value    New scalar value flag
    * @param position Column position
    */
   public void setColumnIsScalar(boolean value, int position) {
      cache.setColumnIsScalar(value, position);
   }

   /**
    * Sets the column label for the given column.
    *
    * @param label    New column label
    * @param position Column position
    */
   public void setColumnLabel(String label, int position) {
      cache.setColumnLabel(label, position);
   }

   /**
    * Sets the double value at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    The row
    * @param column The column
    */
   public void setDouble(double data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setDouble(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setDouble(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the float value at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    The row
    * @param column The column
    */
   public void setFloat(float data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setFloat(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setFloat(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the int value at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    The row
    * @param column The column
    */
   public void setInt(int data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setInt(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setInt(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the value at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    The row
    * @param column The column
    */
   public void setLong(long data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setLong(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setLong(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the value at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    The row
    * @param column The column
    */
   public void setObject(Object data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setObject(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setObject(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the short value at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    The row
    * @param column The column
    */
   public void setShort(short data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setShort(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setShort(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the string at the given row and column to the value provided.
    *
    * @param data   Value to set the entry to
    * @param row    The row
    * @param column The column
    */
   public void setString(String data, int row, int column) {
      int where;

      try {
         where = subset[row - offset];
         columns[column].setString(data, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[column].setString(data, where);
      }

      currentPage.mark(true);
   }

   /**
    * Sets the empty flag.
    *
    * @param b   True if the cell is empty
    * @param row Row of the table
    * @param col Column of the table
    */
   public void setValueToEmpty(boolean b, int row, int col) {
      int where;

      try {
         where = subset[row - offset];
         columns[col].setValueToEmpty(b, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[col].setValueToEmpty(b, where);
      }
   }

   /**
    * Sets the missing value flag.
    *
    * @param b   True or false if missing or not
    * @param row Row of the data
    * @param col Column of the data
    */
   public void setValueToMissing(boolean b, int row, int col) {
      int where;

      try {
         where = subset[row - offset];
         columns[col].setValueToMissing(b, where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];
         columns[col].setValueToMissing(b, where);
      }
   }

   /**
    * Copies the table, but not its contents. The new table will share the same
    * data.
    *
    * @return Copied table
    */
   public Table shallowCopy() {
      SubsetPagingTable spt = new SubsetPagingTable(cache);
      spt.subset = this.subset;

      return spt;
   }

   /**
    * Does nothing yet.
    *
    * @param col Column to do nothing with
    */
   public void sortByColumn(int col) {}

   /**
    * Does nothing yet.
    *
    * @param col Column to do nothing with
    * @param begin Column to do nothing with
    * @param end Column to do nothing with
    */
   public void sortByColumn(int col, int begin, int end) {}

   /**
    * Swaps the position of two columns.
    *
    * @param pos1 Position of first column to swap
    * @param pos2 Position of second column to swap
    */
   public void swapColumns(int pos1, int pos2) {
      cache.swapColumns(pos1, pos2);
      this.refresh();
   }

   /**
    * Swap two rows. They can potentially be in different pages, but it doesn't
    * matter, we treat them the same either way.
    *
    * @param pos1 First position of a row to swap
    * @param pos2 Second postion of a row to swap
    */
   public void swapRows(int pos1, int pos2) { cache.swapRows(pos1, pos2); }

   /**
    * The example table is a shallow copy of this one, shares the same page
    * manager and all.
    *
    * @return <code>ExampleTable</code>
    */
   public ExampleTable toExampleTable() {
      return new ExamplePagingTable(cache);
   }

} // end class SubsetPagingTable
