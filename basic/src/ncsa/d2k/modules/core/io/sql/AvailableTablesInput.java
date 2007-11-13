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
import ncsa.d2k.core.modules.PropertyDescription;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Vector;


public class AvailableTablesInput extends InputModule {
	

   //~ Instance fields *********************************************************

   /** Description of field dataCubeOnly. */
   protected boolean dataCubeOnly = false;

   /** controls what to list.* */
   /**
    * NOTE: These variable names imply that only one or the other can be*
    * retrieved, but in fact both can. We didn't update the variable names* when
    * we changed the logic because we wanted old itinieraries to continue* to
    * work. *
    */
   protected boolean dataTableOnly = true;

   //~ Methods *****************************************************************

   /**
    * Description of method doit.
    *
    * @throws Exception Description of exception Exception.
    */
   protected void doit() throws Exception {
      ConnectionWrapper cw = (ConnectionWrapper) this.pullInput(0);
      Connection con = cw.getConnection();
      Vector v = new Vector();

      DatabaseMetaData metadata = null;
      con = cw.getConnection();
      metadata = con.getMetaData();

      String[] types = { "TABLE" };
      ResultSet tableNames = metadata.getTables(null, "%", "%", types);

      while (tableNames.next()) {
         String aTable = tableNames.getString("TABLE_NAME");

         if (dataTableOnly) {

            if (aTable.toUpperCase().indexOf("CUBE") < 0) {
               v.addElement(aTable);
            }
         }

         if (dataCubeOnly) {

            if (aTable.toUpperCase().indexOf("CUBE") >= 0) {
               v.addElement(aTable);
            }
         }
      }

      this.pushOutput(cw, 0);
      this.pushOutput(v, 1);
   } // end method doit

   /**
    * Get the value of dataCubeOnly.
    *
    * @return true if data cubes should be listed. false otherwise
    */
   public boolean getDataCubeOnly() { return dataCubeOnly; }

   /**
    * Get the value of dataTableOnly.
    *
    * @return true if data tables should be listed. false otherwise
    */
   public boolean getDataTableOnly() { return dataTableOnly; }

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
            return "The database connection used to discover the available tables.";

         default:
            return "No such input.";
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
      String[] types = { "ncsa.d2k.modules.core.io.sql.ConnectionWrapper" };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s += "This module builds a list of available database tables. </p>";
      s += "<p>Detailed Description: ";
      s += "This module makes a connection to the database indicated by the ";
      s += "<i>Database Connection</i> input port and builds a ";
      s +=
         "list of available database tables. There are two types of tables in a ";
      s += "database, raw data tables and aggregated cube tables. The ";
      s +=
         "properties <i>List Data Tables</i> and <i>List Cube Tables</i> allow ";
      s +=
         "the user to control whether one or both types of tables will be included in the list.</p>";
      s += "<p>For security, ";
      s +=
         "users may only view the tables they have been granted permission to access. If you ";
      s += "cannot see the tables you are looking for, please report the ";
      s += "problems to your database administrator. </p>";
      s += "<p> Restrictions: ";
      s +=
         "Only Oracle, SQLServer, DB2 and MySql databases are currently supported.";

      return s;
   }


   /**
    * Describes the name of the module.
    *
    * @return <code>String</code> describing the name of the module.
    */
   public String getModuleName() { return "Available Tables Input"; }

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
            return "A list of available tables of the specified type(s).";

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
            return "Tables List";

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

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[2];
      pds[0] =
         new PropertyDescription("dataTableOnly", "List Data Tables",
                                 "Choose True if you want to list data tables.");
      pds[1] =
         new PropertyDescription("dataCubeOnly", "List Data Cubes",
                                 "Choose True if you want to list data cubes.");

      return pds;
   }

   /**
    * Set the the value of dataCubeOnly.
    *
    * @param  b true if data tables should be listed. false otherwise
    *
    * @throws PropertyVetoException Description of exception
    *                               PropertyVetoException.
    */
   public void setDataCubeOnly(boolean b) throws PropertyVetoException {
      dataCubeOnly = b;

      if (!dataTableOnly && !dataCubeOnly) {
         throw new PropertyVetoException("\nYou must set either List Data Tables or List Data Cubes to True.",
                                         null);
      }
   }

   /**
    * Set the the value of dataTableOnly.
    *
    * @param b true if data tables should be listed. false otherwise
    */
   public void setDataTableOnly(boolean b) { dataTableOnly = b; }


} // end class AvailableTablesInput
