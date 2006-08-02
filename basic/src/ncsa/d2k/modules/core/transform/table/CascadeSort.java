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
package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Table;

import java.util.ArrayList;


/**
 * Does a cascading sort on a table in place.
 *
 * @author  anca
 * @version $Revision$, $Date$
 */
public class CascadeSort {

   //~ Instance fields *********************************************************

   /** true if sorting the first column */
   private boolean first = true;

   /** a run is a collection of
    * similar values in a column */
   private int[] runs = null;

   /** table to sort */
   private MutableTable table;

   //~ Constructors ************************************************************


   /**
    * Creates a new CascadeSort object.
    *
    * @param toSort table to sort
    */
   public CascadeSort(Table toSort) { table = (MutableTable) toSort; }

   //~ Methods *****************************************************************

   /**
    * Find the indices where a run ends in a column. A run is a collection of
    * similar values in a column. The column is assumed to be sorted. Returns an
    * array whose values are the indices of the rows that end a run.
    *
    * @param  col the column of interest
    *
    * @return find the indices where a run ends in a column. A run is a
    *         collection of similar values in a column. The column is assumed to
    *         be sorted. Returns an array whose values are the indices of the
    *         rows that end a run.
    */
   private int[] findRuns(int col) {

      if (first) {

         // System.out.println("FindRuns: "+table.getColumnLabel(col));
         ArrayList runList = new ArrayList();

         if (table.isColumnScalar(col)) {
            double currentVal = table.getDouble(0, col);

            for (int i = 1; i < table.getNumRows(); i++) {
               double rowVal = table.getDouble(i, col);

               if (rowVal != currentVal) {
                  runList.add(new Integer(i - 1));
                  currentVal = rowVal;
               }
            }
         } else {
            String currentVal = table.getString(0, col);

            for (int i = 1; i < table.getNumRows(); i++) {
               String rowVal = table.getString(i, col);

               if (!rowVal.equals(currentVal)) {
                  runList.add(new Integer(i - 1));
                  currentVal = rowVal;
               }
            }
         }

         runList.add(new Integer(table.getNumRows() - 1));

         int[] retVal = new int[runList.size()];

         for (int i = 0; i < retVal.length; i++) {
            retVal[i] = ((Integer) runList.get(i)).intValue();
            // System.out.println("Run["+i+"]: "+retVal[i]);
         }

         return retVal;
      } else {

         // Sort through the runs
         ArrayList runList = new ArrayList();

         if (table.isColumnScalar(col)) {
            double currentVal = table.getDouble(0, col);

            for (int i = 0; i <= runs[0]; i++) {
               double rowVal = table.getDouble(i, col);

               if (rowVal != currentVal) {
                  runList.add(new Integer(i - 1));
                  currentVal = rowVal;
               }
            }

            for (int j = 1; j < runs.length; j++) {
               runList.add(new Integer(runs[j - 1]));

               for (int i = runs[j - 1] + 1; i <= runs[j]; i++) {
                  double rowVal = table.getDouble(i, col);

                  if (rowVal != currentVal) {
                     runList.add(new Integer(i - 1));
                     currentVal = rowVal;
                  }
               }
            }
         } else {
            String currentVal = table.getString(0, col);

            for (int i = 0; i <= runs[0]; i++) {
               String rowVal = table.getString(i, col);

               if (!rowVal.equals(currentVal)) {
                  runList.add(new Integer(i - 1));
                  currentVal = rowVal;
               }
            }

            for (int j = 1; j < runs.length; j++) {
               runList.add(new Integer(runs[j - 1]));

               for (int i = runs[j - 1] + 1; i <= runs[j]; i++) {
                  String rowVal = table.getString(i, col);

                  if (!rowVal.equals(currentVal)) {
                     runList.add(new Integer(i - 1));
                     currentVal = rowVal;
                  }
               }
            }
         }

         runList.add(new Integer(table.getNumRows() - 1));

         int[] retVal = new int[runList.size()];

         for (int i = 0; i < retVal.length; i++) {
            retVal[i] = ((Integer) runList.get(i)).intValue();
            // System.out.println("Run["+i+"]: "+retVal[i]);
         }

         return retVal;
      } // end if
   } // end method findRuns

   /**
    * Get the default order for columns to sort.
    *
    * @return default column sort order
    */
   public int[] getDefaultSortOrder() {
      int[] sortorder = new int[table.getNumColumns()];

      for (int i = 0; i < sortorder.length; i++) {
         sortorder[i] = i;
      }

      return sortorder;
   }

   /**
    * Reorder the columns of the table based on sortorder
    * @param sortorder new column order
    */
   public void reorder(int[] sortorder) {
// VG - fixed the bug with reordering the columns. [May first, 1006]

      // an arrayof the old indices and how we are swapping among them.
      int[] indirection = new int[table.getNumColumns()];


      for (int i = 0; i < indirection.length; i++) {
         indirection[i] = i;
      }


      for (int i = 0; i < sortorder.length; i++) {

         if (indirection[i] == sortorder[i]) {
            continue;
         }

         table.swapColumns(i, indirection[sortorder[i]]);

         int tmp = indirection[i];
         indirection[i] = indirection[sortorder[i]];
         indirection[sortorder[i]] = tmp;

      }

   } // end method reorder


   /**
    * Sort the table for each column selected.
    *
    * @param sortorder Description of parameter sortorder.
    */
   public void sort(int[] sortorder) {

      if (sortorder.length > 0) {
         table.sortByColumn(sortorder[0]);

         for (int i = 1; i < sortorder.length; i++) {

            // Now find the runs in the (i-1)th column and do a
            // table.sortByColumn(col, begin, end) for each run
            runs = findRuns(sortorder[i - 1]);
            first = false;


            // Do the first sort outside the loop
            table.sortByColumn(sortorder[i], 0, runs[0]);

            for (int j = 1; j < runs.length; j++) {

               // Now sort from the end of the last run to the end
               // of the current run
               table.sortByColumn(sortorder[i], runs[j - 1] + 1, runs[j]);
            }
         }
      }
   } // end method sort


} // end class CascadeSort
