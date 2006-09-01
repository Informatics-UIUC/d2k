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

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.modules.core.datatype.table.MutableTable;

import gnu.trove.TIntArrayList;

import java.util.HashSet;


/**
 * Remove duplicate rows from a MutableTable.
 *
 * @author  David Clutter, Tom Redman
 * @version 1.0
 */
public class RemoveDuplicateRows extends DataPrepModule {

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module. Remove duplicate rows from the
    * table.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      MutableTable mt = (MutableTable) pullInput(0);
      HashSet setOfUniqueRows = new HashSet();

      TIntArrayList rowsToRemove = new TIntArrayList();

      int numRows = mt.getNumRows();
      int numCols = mt.getNumColumns();

      for (int i = 0; i < numRows; i++) {
         String[] row = new String[numCols];

         for (int j = 0; j < numCols; j++) {

            if (mt.isValueMissing(i, j)) {
               row[j] = "XX_MISSING_VALUE_XX";
            } else {
               row[j] = mt.getString(i, j);
            }
         }

         RowSet rs = new RowSet(row);

         if (setOfUniqueRows.contains(rs)) {
            rowsToRemove.add(i);
         } else {
            setOfUniqueRows.add(rs);
         }
      }

      int[] toRem = rowsToRemove.toNativeArray();

      for (int i = toRem.length - 1; i > -1; i--) {
         mt.removeRow(toRem[i]);
      }

      pushOutput(mt, 0);
   } // end method doit

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "This is the table that will have duplication rows removed.";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Input Table";

         default:
            return "NO SUCH INPUT!";
      }
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] types = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>      Overview: This module will remove all duplicate rows. A row is a       duplicate if" +
             " each value for each attribute is the same as those in a       previously encountered row. " +
             "   </p>    <p>      Detailed Description: For each row in the table, we collect a vector of" +
             "       strings that is the text encoding. We then check a hash table to see if       an entry" +
             " with the same values has already been encountered. If it has,       we just mark the row as" +
             " a duplicate to delete when we are done. If it       hasn't been encountered, we add it to the" +
             " hash table. When we have       examined each row, we just trash the duplicates.    </p>   " +
             "<P>Data Handling: The input table is changed by this module.</P>" +
             " <p>      Scalability: If there are not a lot of duplicates, this hashtable can       get very" +
             " large. The entries in this hash table store a string       representation of each value in" +
             " each field in each unique row. Do the       math.    </p>";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Remove Duplicate Rows"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      switch (i) {

         case 0:
            return "The same table as the input table, with the duplicate rows removed.";

         default:
            return "No such output";
      }
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Result Table";

         default:
            return "NO SUCH OUTPUT!";
      }
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] types = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

      return types;
   }

   //~ Inner Classes ***********************************************************

   /**
    * Represents the values in a row.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class RowSet {

      /** the values. */
      String[] keys;

      /**
       * Constructor.
       *
       * @param k values
       */
      RowSet(String[] k) { keys = k; }

      /**
       * Return true iff all the elements of this.keys are equal to o.keys.
       *
       * @param  o other RowSet
       *
       * @return true iff all the elements of this.keys are equal to o.keys
       */
      public boolean equals(Object o) {
         RowSet other = (RowSet) o;
         String[] otherkeys = other.keys;

         if (otherkeys.length != keys.length) {
            return false;
         }

         for (int i = 0; i < keys.length; i++) {

            if (!keys[i].equals(otherkeys[i])) {
               return false;
            }
         }

         return true;
      }


      /**
       * Compute a hash code.
       *
       * @return hash code
       */
      public int hashCode() {
         int result = 37;

         for (int i = 0; i < keys.length; i++) {
            result *= keys[i].hashCode();
         }

         return result;
      }
   } // end class RowSet
} // end class RemoveDuplicateRows
