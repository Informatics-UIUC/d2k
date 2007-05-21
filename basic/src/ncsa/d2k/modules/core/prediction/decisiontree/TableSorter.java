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
package ncsa.d2k.modules.core.prediction.decisiontree;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Vector;
import ncsa.d2k.modules.core.util.*;


/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel)
 * and itself implements TableModel. TableSorter does not store or copy the data
 * in the TableModel, instead it maintains an array of integers which it keeps
 * the same size as the number of rows in its model. When the model changes it
 * notifies the sorter that something has changed eg. "rowsAdded" so that its
 * internal array of integers can be reallocated. As requests are made of the
 * sorter (like getValueAt(row, col) it redirects them to its model via the
 * mapping array. That way the TableSorter appears to hold another copy of the
 * table with the rows in a different order. The sorting algorthm used is stable
 * which means that it does not move around rows when its comparison function
 * returns 0 to denote that they are equivalent.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class TableSorter extends AbstractTableModel
   implements TableModelListener {

   //~ Instance fields *********************************************************

   /** true if sorting in ascending order */
   protected boolean ascending = false;

   /** number of comparisons made */
   protected int compares;

   /** used to indirectly access rows.  the original data is unmodified, its
    * rows are accessed through indexes */
   protected int[] indexes;

   /** List of column indexes to sort by */
   protected Vector sortingColumns = new Vector();

   /** table model */
   protected TableModel model;

   //~ Constructors ************************************************************

   /**
    * Creates a new TableSorter object.
    */
   public TableSorter() {
      indexes = new int[0]; // for consistency
   }

   /**
    * Creates a new TableSorter object.
    *
    * @param model table model
    */
   public TableSorter(TableModel model) { setModel(model); }

   //~ Methods *****************************************************************

   /**
    * There is no-where else to put this. Add a mouse listener to the Table to
    * trigger a table sort when a column heading is clicked in the JTable.
    *
    * @param table Description of parameter table.
    */
   public void addMouseListenerToHeaderInTable(JTable table) {
      final TableSorter sorter = this;
      final JTable tableView = table;
      tableView.setColumnSelectionAllowed(false);

      MouseAdapter listMouseListener =
         new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
               TableColumnModel columnModel = tableView.getColumnModel();
               int viewColumn = columnModel.getColumnIndexAtX(e.getX());
               int column = tableView.convertColumnIndexToModel(viewColumn);

               if (e.getClickCount() == 1 && column != -1) {

                  // System.out.println("Sorting ...");
                  int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
                  boolean ascending = (shiftPressed == 0);
                  sorter.sortByColumn(column, ascending);
               }
            }
         };

      JTableHeader th = tableView.getTableHeader();
      th.addMouseListener(listMouseListener);
   }
   
   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   /**
    * Check that the length of indexes is the same as the row count of the model
    */
   public void checkModel() {

      if (indexes.length != model.getRowCount()) {
    	  myLogger.error("Sorter not informed of a change in model.");
      }
   }

   /**
    * Compare two rows.
    *
    * @param  row1 first row
    * @param  row2 second row
    *
    * @return -1, 0, or 1 if row1 is less than, equal to, or greater than row2
    */
   public int compare(int row1, int row2) {
      compares++;

      for (int level = 0; level < sortingColumns.size(); level++) {
         Integer column = (Integer) sortingColumns.elementAt(level);
         int result = compareRowsByColumn(row1, row2, column.intValue());

         if (result != 0) {
            return ascending ? result : -result;
         }
      }

      return 0;
   }

   /**
    * Compare the values at (row1, column) and (row2, column)
    *
    * @param  row1   first row
    * @param  row2   second row
    * @param  column Description of parameter column.
    *
    * @return -1, 0, or 1 if row1 is less than, equal to, or greater than row2
    */
   public int compareRowsByColumn(int row1, int row2, int column) {
      Class type = model.getColumnClass(column);
      TableModel data = model;

      // Check for nulls.

      Object o1 = data.getValueAt(row1, column);
      Object o2 = data.getValueAt(row2, column);

      // If both values are null, return 0.
      if (o1 == null && o2 == null) {
         return 0;
      } else if (o1 == null) { // Define null less than everything.
         return -1;
      } else if (o2 == null) {
         return 1;
      }

      /*
       * We copy all returned values from the getValue call in case an optimised
       * model is reusing one object to return many values.  The Number
       * subclasses in the JDK are immutable and so will not be used in this way
       * but other subclasses of Number might want to do this to save space and
       * avoid unnecessary heap allocation.
       */

      if (type.getSuperclass() == java.lang.Number.class) {
         Number n1 = (Number) data.getValueAt(row1, column);
         double d1 = n1.doubleValue();
         Number n2 = (Number) data.getValueAt(row2, column);
         double d2 = n2.doubleValue();

         if (d1 < d2) {
            return -1;
         } else if (d1 > d2) {
            return 1;
         } else {
            return 0;
         }
      } else if (type == java.util.Date.class) {
         Date d1 = (Date) data.getValueAt(row1, column);
         long n1 = d1.getTime();
         Date d2 = (Date) data.getValueAt(row2, column);
         long n2 = d2.getTime();

         if (n1 < n2) {
            return -1;
         } else if (n1 > n2) {
            return 1;
         } else {
            return 0;
         }
      } else if (type == String.class) {
         String s1 = (String) data.getValueAt(row1, column);
         String s2 = (String) data.getValueAt(row2, column);
         int result = s1.compareTo(s2);

         if (result < 0) {
            return -1;
         } else if (result > 0) {
            return 1;
         } else {
            return 0;
         }
      } else if (type == Boolean.class) {
         Boolean bool1 = (Boolean) data.getValueAt(row1, column);
         boolean b1 = bool1.booleanValue();
         Boolean bool2 = (Boolean) data.getValueAt(row2, column);
         boolean b2 = bool2.booleanValue();

         if (b1 == b2) {
            return 0;
         } else if (b1) { // Define false < true
            return 1;
         } else {
            return -1;
         }
      } else {
         Object v1 = data.getValueAt(row1, column);
         String s1 = v1.toString();
         Object v2 = data.getValueAt(row2, column);
         String s2 = v2.toString();
         int result = s1.compareTo(s2);

         if (result < 0) {
            return -1;
         } else if (result > 0) {
            return 1;
         } else {
            return 0;
         }
      }
   } // end method compareRowsByColumn


    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
        return (model == null) ? 0 : model.getColumnCount();
    }

   /**
    * Get the TableModel
    *
    * @return table model
    */
   public TableModel getModel() { return model; }


    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        return (model == null) ? 0 : model.getRowCount();
    }

   /**
    * The mapping only affects the contents of the data rows. Pass all requests
    * to these rows through the mapping array: "indexes".
    *
    * @param  aRow    Description of parameter aRow.
    * @param  aColumn Description of parameter aColumn.
    *
    * @return Description of return value.
    */
   public Object getValueAt(int aRow, int aColumn) {
      checkModel();

      return model.getValueAt(indexes[aRow], aColumn);
   }

   /**
    * sorting method
    */
   public void n2sort() {

      for (int i = 0; i < getRowCount(); i++) {

         for (int j = i + 1; j < getRowCount(); j++) {

            if (compare(indexes[i], indexes[j]) == -1) {
               swap(i, j);
            }
         }
      }
   }

   /**
    * Reset the indexes array
    */
   public void reallocateIndexes() {
      int rowCount = model.getRowCount();

      // Set up a new array of indexes with the right number of elements
      // for the new data model.
      indexes = new int[rowCount];

      // Initialise with the identity mapping.
      for (int row = 0; row < rowCount; row++) {
         indexes[row] = row;
      }
   }

   /**
    * Set the table model
    *
    * @param model table model
    */
   public void setModel(TableModel model) {
      this.model = model;
      model.addTableModelListener(this);
      reallocateIndexes();
   }


    /**
     * This empty implementation is provided so users don't have to implement
     * this method if their data model is not editable.
     *
     * @param aValue      value to assign to cell
     * @param aRow    row of cell
     * @param aColumn column of cell
     */
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        checkModel();
        model.setValueAt(aValue, indexes[aRow], aColumn);
    }

   /**
    * This is a home-grown implementation which we have not had time to research
    * - it may perform poorly in some circumstances. It requires twice the space
    * of an in-place algorithm and makes NlogN assigments shuttling the values
    * between the two arrays. The number of compares appears to vary between N-1
    * and NlogN depending on the initial order but the main reason for using it
    * here is that, unlike qsort, it is stable.
    *
    * @param from Description of parameter from.
    * @param to   Description of parameter to.
    * @param low  Description of parameter low.
    * @param high Description of parameter high.
    */
   public void shuttlesort(int[] from, int[] to, int low, int high) {

      if (high - low < 2) {
         return;
      }

      int middle = (low + high) / 2;
      shuttlesort(to, from, low, middle);
      shuttlesort(to, from, middle, high);

      int p = low;
      int q = middle;

      /* This is an optional short-cut; at each recursive call,
       * check to see if the elements in this subset are already ordered.  If
       * so, no further comparisons are needed; the sub-array can just be
       * copied.  The array must be copied rather than assigned otherwise sister
       * calls in the recursion might get out of sinc.  When the number of
       * elements is three they are partitioned so that the first set, [low,
       * mid), has one element and and the second, [mid, high), has two. We skip
       * the optimisation when the number of elements is three or less as the
       * first compare in the normal merge will produce the same sequence of
       * steps. This optimisation seems to be worthwhile for partially ordered
       * lists but some analysis is needed to find out how the performance drops
       * to Nlog(N) as the initialorder diminishes - it may drop very quickly.
       */

      if (high - low >= 4 && compare(from[middle - 1], from[middle]) <= 0) {

         for (int i = low; i < high; i++) {
            to[i] = from[i];
         }

         return;
      }

      // A normal merge.

      for (int i = low; i < high; i++) {

         if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
            to[i] = from[p++];
         } else {
            to[i] = from[q++];
         }
      }
   } // end method shuttlesort

   /**
    * Sorting method.
    *
    * @param sender
    */
   public void sort(Object sender) {
      checkModel();

      compares = 0;
      n2sort();
      // qsort(0, indexes.length-1); shuttlesort((int[])indexes.clone(),
      // indexes, 0, indexes.length); System.out.println("Compares: "+compares);
   }

    /**
     * Sort by the specified column
     *
     * @param column column to sort by
     */
   public void sortByColumn(int column) { sortByColumn(column, true); }

   /**
    * Sort by the specified column
    *
    * @param column    column to sort by
    * @param ascending true if sorting should be in ascending order
    */
   public void sortByColumn(int column, boolean ascending) {
      this.ascending = ascending;
      sortingColumns.removeAllElements();
      sortingColumns.addElement(new Integer(column));
      sort(this);
      fireTableChanged(new TableModelEvent(this));
   }

   /**
    * Swap values in the indexes array
    *
    * @param i first location
    * @param j second location
    */
   public void swap(int i, int j) {
      int tmp = indexes[i];
      indexes[i] = indexes[j];
      indexes[j] = tmp;
   }


    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {

        // System.out.println("Sorter: tableChanged");
        reallocateIndexes();
        fireTableChanged(e);
    }
} // end class TableSorter
