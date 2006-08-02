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


/**
 * <p>Title:</p>
 *
 * <p>Description: creates a copy of the input Table. The user may choose
 * between a shallow copy or a deep copy.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company:</p>
 *
 * @author  vered goren
 * @version 1.0
 */
public class CopyTable extends DataPrepModule {

   //~ Instance fields *********************************************************

   /**
    * When this property is true <i>Copied Table</i> is a deep copy of <i>Input
    * Table</i> .
    */
   private boolean deepCopy;

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   protected void doit() throws Exception {
      Table t = (Table) pullInput(0);

      if (deepCopy) {
         pushOutput(t.copy(), 0);
      } else {
         pushOutput(t.shallowCopy(), 0);
      }

      pushOutput(t, 1);

   }

   /**
    * Get deepCopy
    *
    * @return deep copy
    */
   public boolean getDeepCopy() { return deepCopy; }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "<p>      a Table to be copied.    </p>";

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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: This module creates a copy of the input table.</p>" +
             "<p>Data Handling: If 'Create Deep Copy' is set to true <i>Copied Table</i> is " +
             "a deep copy of <i>Original Table</i>.</p>";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Copy Table"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "<p>     Copy of <i>Input Table</i>    </p>";

         case 1:
            return "<p>     The original <i>Input Table</i>    </p>";

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
            return "Copied Table";

         case 1:
            return "Original Table";

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
      String[] types =
      {
         "ncsa.d2k.modules.core.datatype.table.Table",
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

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
      PropertyDescription[] retVal = new PropertyDescription[1];
      retVal[0] =
         new PropertyDescription("deepCopy", "Create Deep Copy",
                                 "<P>When this property is true <i>Copied Table</i> " +
                                 "is a deep copy of <i>Input Table</i></P>");

      return retVal;
   }

   /**
    * Set deepCopy
    *
    * @param b new deepCopy
    */
   public void setDeepCopy(boolean b) { deepCopy = b; }
} // end class CopyTable
