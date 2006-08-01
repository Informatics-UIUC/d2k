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
package ncsa.d2k.modules.core.io.sql;


import ncsa.d2k.core.modules.InputModule;

import javax.swing.JOptionPane;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Vector;


public class AvailableAttributesInput extends InputModule {

   //~ Instance fields *********************************************************

   /** Description of field msgBoard. */
   JOptionPane msgBoard = new JOptionPane();

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {
      String tableName = (String) this.pullInput(1);

      if (tableName == null || tableName.length() == 0) {

         /* JOptionPane.showMessageDialog(msgBoard,
          *         "No table is selected.", "Error",
          * JOptionPane.ERROR_MESSAGE);*/
         System.out.println("No table is selected");

      } else {
         ConnectionWrapper cw = (ConnectionWrapper) this.pullInput(0);
         Connection con = cw.getConnection();
         Vector v = new Vector();
         Vector c = new Vector();
         DatabaseMetaData dbmd = con.getMetaData();
         ResultSet tableSet = dbmd.getColumns(null, null, tableName, "%");

         while (tableSet.next()) {
            v.addElement(tableSet.getString("COLUMN_NAME"));
         }

         this.pushOutput(cw, 0);
         this.pushOutput(v, 1);
      }
   }

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
            return "The database connection.";

         case 1:
            return "The name of the selected table, as specified in a previous module.";

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
            return "Database Connection";

         case 1:
            return "Selected Table";

         default:
            return "No such input";
      }
   }

   /**
    * Description of method getInputTypes.
    *
    * @return Description of return value.
    */
   public String[] getInputTypes() {
      String[] types =
      { "ncsa.d2k.modules.io.input.sql.ConnectionWrapper", "java.lang.String" };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s +=
         "This module builds a list of attributes (fields) available in a specified database table. </p>";
      s += "<p>Detailed Description: ";
      s += "This module makes a connection to the database indicated by the ";
      s += "<i>Database Connection</i> input port and builds a ";
      s +=
         "list of available attributes for the database table indicated by the ";
      s += "<i>Selected Table</i> input port. ";
      s +=
         "The list is made available to other modules via the <i>Attributes List</i> output port. ";
      s += "<p>Restrictions: ";
      s +=
         "Only Oracle, SQLServer, DB2 and MySql databases are currently supported.";

      return s;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleName() { return "Available Attributes Input"; }

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
            return "The database connection, for use by the next module.";

         case 1:
            return "A list of the attributes available in the selected table.";

         default:
            return "No such output.";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Database Connection";

         case 1:
            return "Attributes List";

         default:
            return "No such output";
      }
   }

   /**
    * Description of method getOutputTypes.
    *
    * @return Description of return value.
    */
   public String[] getOutputTypes() {
      String[] types =
      { "ncsa.d2k.modules.core.io.sql.ConnectionWrapper", "java.util.Vector" };

      return types;
   }

} // end class AvailableAttributesInput
