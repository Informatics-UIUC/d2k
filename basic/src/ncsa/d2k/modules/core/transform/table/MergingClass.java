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

import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.NumericColumn;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.TableFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Does the work of merging table rows for the MergeTableRows module.
 *
 * <p>Rows are merged if they have identical values for one or more key
 * attributes. A set of rows from the <i>Input Table</i> that has identical
 * values for the key attributes are called <i>matching rows</i>. One output row
 * is produced for each set of matching rows.</p>
 *
 * <p>The Control determines which row in each matching row set will be used as
 * the basis for the resulting merged row. For a set of matching rows, the row
 * with the maximum value for the Control attribute is the control row.</p>
 *
 * <p>The possible merge methods are <i>Sum</i>, <i>Average</i>, <i>Maximum</i>,
 * <i>Minimum</i>, and <i>Count</i>. For each of the Merge attributes selected,
 * the merge method will be applied to the attribute values of all matching rows
 * in a set and the result will appear in the output merged row.</p>
 *
 * <p>Each row in the <i>Output Table</i> will have the values of the control
 * row attributes for all string attributes and for the numeric attributes that
 * were not selected as Merge attributes. That is to say, all data that is not
 * merged using the merge method is simply copied from the control row for each
 * set of matching rows.</p>
 *
 * @author  $Author$
 * @version 1.0
 */
public class MergingClass {

   //~ Static fields/initializers **********************************************

   /** constant for Sum merge method. */
   static public final String SUM = "Sum";

   /** constant for Average merge method. */
   static public final String AVE = "Average";

   /** constant for Maximum merge method. */
   static public final String MAX = "Maximum";

   /** constant for Minimum merge method. */
   static public final String MIN = "Minimum";

   /** constant for Count merge method. */
   static public final String CNT = "Count";

   //~ Constructors ************************************************************

   /**
    * Constructor.
    */
   private MergingClass() { }

   //~ Methods *****************************************************************

   /**
    * Create an empty table with the same number of columns and column types as
    * the table argument, with the specified number of rows.
    *
    * @param  numRows number of rows
    * @param  table   original table
    *
    * @return a table with same types of columns as the table argument
    */
   static private MutableTable createTable(int numRows, Table table) {

      TableFactory factory = table.getTableFactory();
      MutableTable tbl = (MutableTable) factory.createTable();
      tbl.setLabel(table.getLabel());

      for (int i = 0; i < table.getNumColumns(); i++) {
         Column c = table.getColumn(i);
         Column newCol = null;

         int type = c.getType();

         newCol = factory.createColumn(type);
         newCol.setNumRows(numRows);
         newCol.setLabel(c.getLabel());

         tbl.addColumn(newCol);
      } // end for

      return tbl;
   } // createTable

   /**
    * Merge table rows using the avergage merge method.
    *
    * @param tbl       new table to put merged rows into
    * @param rowLoc    row
    * @param keys      key columns (not used)
    * @param mergeCols merge columns
    * @param control   control column
    * @param rows      rows of the table to use
    * @param table     original table
    */
   static private void mergeAve(MutableTable tbl,
                                int rowLoc,
                                int[] keys,
                                int[] mergeCols,
                                int control,
                                int[] rows,
                                Table table) {
      // find the maximum in the control column.  this row will be the one where
      // data is copied from

      int maxRow = rows[0];
      double maxVal;

      if (table.isValueMissing(rows[0], control)) {
         maxVal = Double.MIN_VALUE;
      } else {
         maxVal = table.getDouble(rows[0], control);
      }

      for (int i = 1; i < rows.length; i++) {

         if (
             !table.isValueMissing(rows[i], control) &&
                table.getDouble(rows[i], control) > maxVal) {
            maxVal = table.getDouble(rows[i], control);
            maxRow = rows[i];
         }
      }

      // copy all the row data in
      for (int i = 0; i < tbl.getNumColumns(); i++) {
         Column c = tbl.getColumn(i);

         if (c instanceof NumericColumn) {
            tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
         } else {
            tbl.setString(table.getString(maxRow, i), rowLoc, i);
         }

         tbl.setValueToMissing(table.isValueMissing(maxRow, i), rowLoc, i);
      }

      // now find the averages
      for (int i = 0; i < mergeCols.length; i++) {
         double sums = 0;
         int num = rows.length;

         for (int j = 0; j < rows.length; j++) {

            if (!table.isValueMissing(rows[j], mergeCols[i])) {
               sums += table.getDouble(rows[j], mergeCols[i]);
            } else {
               num--;
            }
         }

         tbl.setDouble(sums / (double) num, rowLoc, mergeCols[i]);
         tbl.setValueToMissing(false, rowLoc, mergeCols[i]);
      }
   } // end method mergeAve

   /**
    * Merge table rows using the count merge method.
    *
    * @param tbl       new table to put merged rows into
    * @param rowLoc    row
    * @param keys      key columns (not used)
    * @param mergeCols merge columns
    * @param control   control column
    * @param rows      rows of the table to use
    * @param table     original table
    */
   static private void mergeCnt(MutableTable tbl,
                                int rowLoc,
                                int[] keys,
                                int[] mergeCols,
                                int control,
                                int[] rows,
                                Table table) {
      // find the maximum in the control column.  this row will be the one where
      // data is copied from

      int maxRow = rows[0];
      double maxVal;

      if (table.isValueMissing(rows[0], control)) {
         maxVal = Double.MIN_VALUE;
      } else {
         maxVal = table.getDouble(rows[0], control);
      }

      for (int i = 1; i < rows.length; i++) {

         if (
             !table.isValueMissing(rows[i], control) &&
                table.getDouble(rows[i], control) > maxVal) {
            maxVal = table.getDouble(rows[i], control);
            maxRow = rows[i];
         }
      }

      // copy all the row data in
      for (int i = 0; i < tbl.getNumColumns(); i++) {
         Column c = tbl.getColumn(i);

         if (c instanceof NumericColumn) {
            tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
         } else {
            tbl.setString(table.getString(maxRow, i), rowLoc, i);
         }

         tbl.setValueToMissing(table.isValueMissing(maxRow, i), rowLoc, i);
      }

      // the count is the number of rows - write than in each column
      // that's being merged.
      int cnt = rows.length;

      for (int i = 0; i < mergeCols.length; i++) {
         tbl.setDouble(cnt, rowLoc, mergeCols[i]);
         tbl.setValueToMissing(false, rowLoc, mergeCols[i]);
      }
   } // end method mergeCnt

   /**
    * Merge table rows using the maximum merge method.
    *
    * @param tbl       new table to put merged rows into
    * @param rowLoc    row
    * @param keys      key columns (not used)
    * @param mergeCols merge columns (not used)
    * @param control   control column
    * @param rows      rows of the table to use
    * @param table     original table
    */
   static private void mergeMax(MutableTable tbl,
                                int rowLoc,
                                int[] keys,
                                int[] mergeCols,
                                int control,
                                int[] rows,
                                Table table) {
      // find the maximum in the control column.  this row will be the one where
      // data is copied from

      int maxRow = rows[0];
      double maxVal;

      if (table.isValueMissing(rows[0], control)) {
         maxVal = Double.MIN_VALUE;
      } else {
         maxVal = table.getDouble(rows[0], control);
      }

      for (int i = 1; i < rows.length; i++) {

         if (
             !table.isValueMissing(rows[i], control) &&
                table.getDouble(rows[i], control) > maxVal) {
            maxVal = table.getDouble(rows[i], control);
            maxRow = rows[i];
         }
      }

      // copy all the row data in
      for (int i = 0; i < tbl.getNumColumns(); i++) {
         Column c = tbl.getColumn(i);

         if (c instanceof NumericColumn) {
            tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
         } else {
            tbl.setString(table.getString(maxRow, i), rowLoc, i);
         }

         tbl.setValueToMissing(table.isValueMissing(maxRow, i), rowLoc, i);
      }
   } // end method mergeMax

   /**
    * Merge table rows using the minimum merge method.
    *
    * @param tbl       new table to put merged rows into
    * @param rowLoc    row
    * @param keys      key columns (not used)
    * @param mergeCols merge columns
    * @param control   control column
    * @param rows      rows of the table to use
    * @param table     original table
    */
   static private void mergeMin(MutableTable tbl,
                                int rowLoc,
                                int[] keys,
                                int[] mergeCols,
                                int control,
                                int[] rows,
                                Table table) {
      // find the maximum in the control column.  this row will be the one where
      // data is copied from

      int maxRow = rows[0];
      double maxVal;

      if (table.isValueMissing(rows[0], control)) {
         maxVal = Double.MIN_VALUE;
      } else {
         maxVal = table.getDouble(rows[0], control);
      }

      for (int i = 1; i < rows.length; i++) {

         if (
             !table.isValueMissing(rows[i], control) &&
                table.getDouble(rows[i], control) > maxVal) {
            maxVal = table.getDouble(rows[i], control);
            maxRow = rows[i];
         }
      }

      // copy all the row data in
      for (int i = 0; i < tbl.getNumColumns(); i++) {
         Column c = tbl.getColumn(i);

         if (c instanceof NumericColumn) {
            tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
         } else {
            tbl.setString(table.getString(maxRow, i), rowLoc, i);
         }

         tbl.setValueToMissing(table.isValueMissing(maxRow, i), rowLoc, i);
      }

      // now find the minimum
      for (int i = 0; i < mergeCols.length; i++) {
         double minimum = 0;

         for (int j = 0; j < rows.length; j++) {

            if (!table.isValueMissing(rows[j], mergeCols[i])) {

               if (j == 0) {
                  minimum = table.getDouble(rows[j], mergeCols[i]);
               } else {
                  double testVal = table.getDouble(rows[j], mergeCols[i]);

                  if (testVal < minimum) {
                     minimum = testVal;
                  }
               }
            }
         }

         tbl.setDouble(minimum, rowLoc, mergeCols[i]);
         tbl.setValueToMissing(false, rowLoc, mergeCols[i]);
      }
   } // end method mergeMin

   /**
    * Merge table rows using the sum merge method.
    *
    * @param tbl       new table to put merged rows into
    * @param rowLoc    row
    * @param keys      key columns (not used)
    * @param mergeCols merge columns
    * @param control   control column
    * @param rows      rows of the table to use
    * @param table     original table
    */
   static private void mergeSum(MutableTable tbl,
                                int rowLoc,
                                int[] keys,
                                int[] mergeCols,
                                int control,
                                int[] rows,
                                Table table) {
      // find the maximum in the control column.  this row will be the one where
      // data is copied from

      int maxRow = rows[0];
      double maxVal;

      if (table.isValueMissing(rows[0], control)) {
         maxVal = Double.MIN_VALUE;
      } else {
         maxVal = table.getDouble(rows[0], control);
      }

      for (int i = 1; i < rows.length; i++) {

         if (
             !table.isValueMissing(rows[i], control) &&
                table.getDouble(rows[i], control) > maxVal) {
            maxVal = table.getDouble(rows[i], control);
            maxRow = rows[i];
         }
      }

      // copy all the row data in
      for (int i = 0; i < tbl.getNumColumns(); i++) {
         Column c = tbl.getColumn(i);

         if (c instanceof NumericColumn) {
            tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
         } else {
            tbl.setString(table.getString(maxRow, i), rowLoc, i);
         }

         tbl.setValueToMissing(table.isValueMissing(maxRow, i), rowLoc, i);
      }

      // now find the sums
      for (int i = 0; i < mergeCols.length; i++) {
         double sums = 0;

         for (int j = 0; j < rows.length; j++) {

            if (!table.isValueMissing(rows[j], mergeCols[i])) {
               sums += table.getDouble(rows[j], mergeCols[i]);
            }
         }

         tbl.setDouble(sums, rowLoc, mergeCols[i]);
         tbl.setValueToMissing(false, rowLoc, mergeCols[i]);
      }
   } // end method mergeSum

   /**
    * Merge table rows.
    *
    * @param  keys    key columns
    * @param  merges  merge columns
    * @param  control control column
    * @param  type    type of merge to perform
    * @param  table   original table
    *
    * @return a new table with the appropriate rows merged
    */
   static public MutableTable mergeTable(int[] keys,
                                         int[] merges,
                                         int control,
                                         String type,
                                         Table table) {
      HashMap keyLookup = new HashMap(20);
      // loop through table to find rows where all the key columns are identical

      // for each row
      for (int i = 0; i < table.getNumRows(); i++) {
         // get the keys for this row

         // alocating an array for the keys
         String[] kys = new String[keys.length];

         // for each columns id in keys
         for (int j = 0; j < kys.length; j++) {

            // getting the value at row i
            if (table.isValueMissing(i, keys[j])) {
               kys[j] = "?";
            } else {
               kys[j] = table.getString(i, keys[j]);
            }
         }

         // cereating a set
         KeySet set = new KeySet(kys);

         // if the look up map does nto contain such set yet - adding it with
         // this row ID
         if (!keyLookup.containsKey(set)) {
            ArrayList list = new ArrayList();
            list.add(new Integer(i));
            keyLookup.put(set, list);
         } else {

            // if it does contain this set - reteiving its array list and
            // adding this row ID
            ArrayList list = (ArrayList) keyLookup.get(set);
            list.add(new Integer(i));

            // necessary?
            keyLookup.put(set, list);
         }
      } // for i

      // now KeyLookup contains all the unique keys and their respective row
      // Ids.

      // create the table
      MutableTable newTable = createTable(keyLookup.size(), table);

      int curRow = 0;

      // now convert the array lists to int[]
      Iterator iter = keyLookup.keySet().iterator();

      while (iter.hasNext()) {

         // for each set in keyLookup
         Object key = iter.next();

         // getting it's row IDs
         ArrayList list = (ArrayList) keyLookup.get(key);

         // converting the IDS into an array
         int[] array = new int[list.size()];

         for (int q = 0; q < list.size(); q++) {
            array[q] = ((Integer) list.get(q)).intValue();
         }

         // now go ahead and do the merging..
         if (type.equals(MAX)) {
            mergeMax(newTable, curRow, keys, merges, control, array, table);
         } else if (type.equals(MIN)) {
            mergeMin(newTable, curRow, keys, merges, control, array, table);
         } else if (type.equals(AVE)) {
            mergeAve(newTable, curRow, keys, merges, control, array, table);
         } else if (type.equals(SUM)) {
            mergeSum(newTable, curRow, keys, merges, control, array, table);
         } else if (type.equals(CNT)) {
            mergeCnt(newTable, curRow, keys, merges, control, array, table);
         }

         curRow++;
      } // end while

      // the number of rows of the cleaned table is equal to the number of
      // unique keys..
      return newTable;
   } // mergeTable

} // MergingClass
