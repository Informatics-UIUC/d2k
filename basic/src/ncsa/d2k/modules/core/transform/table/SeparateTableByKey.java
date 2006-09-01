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
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.util.HashMap;
import java.util.Iterator;


/**
 * This module will separate the input table into several subset tables based on
 * the value of the key field.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class SeparateTableByKey extends DataPrepModule {

   //~ Instance fields *********************************************************

   /** the name of the attribute to use to distinguish the tables by key. */
   protected String attributeName;

   /** this is the index of the next table to push. */
   protected int currentTable = 0;

   /** this property defines the max number of tables to produce. */
   protected int maxTables = 10000;

   /** this is the list of tables that were produced. */
   protected Table[] tables = null;

   //~ Methods *****************************************************************

   /**
    * Clear the tables field and the current table index.
    */
   public void beginExecution() {
      tables = null;
      currentTable = 0;
   }

   /**
    * First time through, we will seperate the table into n distinct tables
    * where n is the number of distinct values for the key field. Each table
    * will contains all the rows that shared the same value for the key.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {

      if (tables == null) {

         // Create the array of tables.
         HashMap subtableMap = new HashMap();
         MutableTableImpl mt = (MutableTableImpl) this.pullInput(0);

         // First find the index of the key column.
         int numCols = mt.getNumColumns();
         int keyColIndx = 0;

         for (; keyColIndx < numCols; keyColIndx++) {

            if (mt.getColumn(keyColIndx).getLabel().equals(attributeName)) {
               break;
            }
         }

         if (keyColIndx == numCols) {
            throw new Exception(this.getAlias() + " : The attribute named " +
                                attributeName + " was not found in the table.");
         }

         // Now create the subset identifying the row for each subtable..
         int numRows = mt.getNumRows();

         for (int i = 0; i < numRows; i++) {
            String keyValue = mt.getString(i, keyColIndx);
            ExtensibleIntArray eia =
               (ExtensibleIntArray) subtableMap.get(keyValue);

            if (eia == null) {
               eia = new ExtensibleIntArray(numRows);
               subtableMap.put(keyValue, eia);
            }

            eia.add(i);
         }

         // Now create the tables.
         int numtables =
            subtableMap.size() < this.maxTables ? subtableMap.size()
                                                : this.maxTables;
         tables = new Table[numtables];

         Iterator i1 = subtableMap.keySet().iterator();
         Iterator i2 = subtableMap.values().iterator();

         for (int i = 0; i < numtables; i++) {
            String label = (String) i1.next();
            ExtensibleIntArray ss = (ExtensibleIntArray) i2.next();
            Table t = mt.copy(ss.toIntArray());

            // Table t = mt.getSubset(ss.toIntArray ());
            t.setLabel(label);
            tables[i] = t;
         }
      } // end if

      this.pushOutput(tables[currentTable++], 0);

      if (currentTable == tables.length) {
         this.beginExecution();
      }
   } // end method doit

   /**
    * Clean up fields.
    */
   public void endExecution() {
      tables = null;
      currentTable = 0;
   }

   /**
    * Get the name of the key attribute field.
    *
    * @return the name of the key attribute field.
    */
   public String getAttributeName() { return attributeName; }

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
            return "This is the input table which will be subseted to get tables sharing a common value for the key column.";

         default:
            return "No such input";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  i Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int i) {

      switch (i) {

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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return types;
   }

   /**
    * Get the maximum number of tables to produce.
    *
    * @return the maximum number of tables to produce.
    */
   public int getMaxTables() { return maxTables; }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>      Overview: This module will separate the input table into several subset       tables" +
             " based on the value of the key field.    </p>    <p>      Detailed Description: The column named" +
             " in the property named <i>Key       Attribute</i> will be used to identify the contents of the" +
             " resulting       tables. Each table will contain all the entries that shared a common      " +
             " value of that attribute. The tables are pushed one per execution until       either the number" +
             " specified by the &quot;Maximum Number of Tables&quot; property       is reached, or all the" +
             " tables generated have been pushed.    </p>    <p>      Data Handling: Each table is a subset" +
             " table, so the data is not copied,       and the subset tables are no smaller than the original." +
             "    </p>";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "SeparateTableByKey"; }

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
            return "A new table containing all rows sharing the same value for the key attribute.";

         default:
            return "No such output";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {

      switch (i) {

         case 0:
            return "Subset Table";

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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return types;
   }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[2];
      descriptions[0] =
         new PropertyDescription("attributeName",
                                 "Key Attribute",
                                 "This is the name of the attribute which contains the key value.");
      descriptions[1] =
         new PropertyDescription("maxTables",
                                 "Maxmimum Number of Tables",
                                 "This is the maximum number of tables that will be pushed. This can push fewer, but never more.");

      return descriptions;
   }

   /**
    * We are ready to execute if we have more tables to push, or if we have a
    * table as input that is to be seperated by key.
    *
    * @return true if we are ready to execute.
    */
   public boolean isReady() {

      if (tables != null && currentTable < tables.length) {
         return true;
      } else if (this.getFlags()[0] > 0) {
         return true;
      }

      return false;
   }

   /**
    * Set the name of the key attribute field.
    *
    * @param name attribute name
    */
   public void setAttributeName(String name) { attributeName = name; }

   /**
    * Set the maximum number of tables to produce.
    *
    * @param mmt maximum number of tables to produce.
    */
   public void setMaxTables(int mmt) { this.maxTables = mmt; }

   //~ Inner Classes ***********************************************************

   /**
    * Wrapper class for an integer array where the ultimate size of the array is
    * not known, but the max size is. The integer array returned is compressed
    * to the right size.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   public class ExtensibleIntArray {

      /** the array. */
      private int[] intarray;

      /** index into array. */
      private int where;

      /**
       * Constructor.
       *
       * @param maxsize maximum size
       */
      public ExtensibleIntArray(int maxsize) { intarray = new int[maxsize]; }

      /**
       * Compress intarray to a size so all the items fit.
       */
      private void compress() {
         int[] newintarray = new int[where];
         System.arraycopy(intarray, 0, newintarray, 0, where);
         intarray = newintarray;
      }

      /**
       * Add a value to the array.
       *
       * @param newval new value
       */
      public void add(int newval) { intarray[where++] = newval; }

      /**
       * Return the contents as a compressed int[].
       *
       * @return contents compressed to the minimum size
       */
      public int[] toIntArray() {

         if (intarray.length != where) {
            this.compress();
         }

         return intarray;
      }
   } // end class ExtensibleIntArray
} // end class SeparateTableByKey
