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

import ncsa.d2k.core.modules.OutputModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;

import java.beans.PropertyVetoException;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;


/**
 * Description of class WriteDBToFile.
 *
 * @author  Hong Cheng
 * @version 1.0
 * @this    module writes the data from a table in database to a file
 */


public class WriteDBToFile extends OutputModule {

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 4648503544541416225L;
	
   //~ Instance fields *********************************************************

   /** Description of field delimChar C==comma. */
   String delimChar = "C";

   /** Description of field delimiter. */
   transient String delimiter;

   /** Description of field useColumnLabels. */
   boolean useColumnLabels = true;

   /** Description of field useDataTypes. */
   boolean useDataTypes = true;

   //~ Methods *****************************************************************

   /**
    * Write database table data to delimited file on filesystem, delimiters
    * 		can be [ comma, space, or tab ].  
    * Inputs pulled from 
    * 	this.pullInput(0) = ConnectionWrapper - open database connection
    * 	this.pullInput(1) = String[]          - fields to select 
    *	this.pullInput(2) = tableNames        - table name to extract data from
    *	this.pullInput(3) = whereClause       - filter clause for query 
    *	this.pullInput(4) = outputFileName    - name of file to write to. 
    *
    * Constructs SQL query statement to fetch data rows from table using the form:
    * 		Select Column [, ...] From table [Where ....] 
    * 	fetch the rows from table, then write the results to file placing delimiter
    * 	character between fileds terminating each row with end of line character.
    *  
    * @throws Exception Description of exception Exception.
    * 
    * TODO: Evaluate handling of non-numeric or character data-types, for instance
    * 		many databases support data-types like binary or other  complex-datatypes 
    * 		which maybe extracted as encoded values or serialized objects. Support for
    * 		these may not be required for this module. If not then restrictions should
    * 		be coded and exceptions potentially rasied.
    */
   public void doit() throws Exception {
      FileWriter fw;

      String newLine = "\n";
      delimiter = ","; // default to comma

      if (delimChar.equals("S")) {
         delimiter = " ";
      } 
      else if (delimChar.equals("T")) {
         delimiter = "\t";
      }

      // We need a connection wrapper
      ConnectionWrapper cw = (ConnectionWrapper) this.pullInput(0);
      Connection con = cw.getConnection();
      String[] fieldArray = (String[]) this.pullInput(1);

      // get the list of fields to read.
      StringBuffer fieldList = new StringBuffer(fieldArray[0]);

      for (int i = 1; i < fieldArray.length; i++) {
         fieldList.append(", " + fieldArray[i]);
      }

      // get the name of the table.
      String tableList = (String) this.pullInput(2);

      // get the query condition.
      String whereClause = "";

      if (isInputPipeConnected(3)) {
         whereClause = (String) pullInput(3);

         if (whereClause.length() == 0) {
            whereClause = null;
         }
      } else if (!isInputPipeConnected(3)) {
         whereClause = null;
      }

      // get the name of the file to write to
      String fileName = (String) this.pullInput(4);
      fw = new FileWriter(fileName);

      ///////////////////////////
      // Get the column types, and create the appropriate column
      // objects
      // construct the query to get clumn information.
      ///////////////////////////

      String query = "SELECT " + fieldList.toString() + " FROM " + tableList;

      if (whereClause != null && whereClause.length() > 1) {
         query += " WHERE " + whereClause;
      }

      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      ResultSetMetaData rsmd = rs.getMetaData();
      int numColumns = rsmd.getColumnCount();

      if (useColumnLabels) {

         for (int i = 0; i < numColumns; i++) {
            String s = rsmd.getColumnName(i + 1);

            if (s == null || s.length() == 0) {
               s = "column_" + i;
            }

            fw.write(s, 0, s.length());

            if (i != (rsmd.getColumnCount() - 1)) {
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }
         }

         fw.write(newLine.toCharArray(), 0, newLine.length());
      }

      if (useDataTypes) {
         String type;
         for (int i = 0; i < numColumns; i++) {
            type = rsmd.getColumnTypeName(i + 1);

            if (ColumnTypes.isContainNumeric(type)) {
               type = "double";
            } else if (type == null || type.length() == 0) {
               type = "Undefined";
            } else {
               type = "string";
            }

            fw.write(type, 0, type.length());

            if (i != (rsmd.getColumnCount() - 1)) {
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }
         }
         fw.write(newLine.toCharArray(), 0, newLine.length());
      }

      // Now compile a list of the datatypes.
      String[] types = new String[numColumns];

      for (int i = 0; i < numColumns; i++) {
         types[i] = rsmd.getColumnTypeName(i + 1);
      }

      // Now populate the table.
      for (int where = 0; rs.next(); where++) {

         for (int i = 0; i < numColumns; i++) {

            if (ColumnTypes.isContainNumeric(types[i])) {

               if (rs.getString(i + 1) == null) {
                  fw.write("");
               } else {
                  fw.write(rs.getString(i + 1));
               }
            } else {

               if (rs.getString(i + 1) == null) {
                  fw.write("");
               } else {
                  fw.write(rs.getString(i + 1));
               }
            }

            if (i != (numColumns - 1)) {
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }

         } // for i

         fw.write(newLine.toCharArray(), 0, newLine.length());
      } // end for

      fw.flush();
      fw.close();
   } 

   /**
    * Method getDelimChar().
    *
    * @return java.lang.String containing the delimiter character.
    */
   public String getDelimChar() { 
	   return delimChar; 
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
            return "      This manages the sql database connection object.   ";
         case 1:
            return "      The names of the fields needed from within the table.   ";
         case 2:
            return "      The name of the table containing the fields.   ";
         case 3:
            return "      Contains the where clause for the sql query (Optional).   ";
         case 4:
            return "      The name of the file to write to.   ";
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
            return "Selected Fields";
         case 2:
            return "Selected Table";
         case 3:
            return "Query Condition (Optional)";
         case 4:
            return "File Name";
         default:
            return "NO SUCH INPUT!";
      }
   }

   /**
    * This method returns an array of strings that contains the data types for
    * the inputs.
    *
    * @return the data types of all inputs.
    */
   public String[] getInputTypes() {
      String[] types =
      {
         "ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
         "[Ljava.lang.String;", "java.lang.String",
         "java.lang.String", "java.lang.String"
      };
      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s += "This module constructs a SQL statement, and retrieves data from a ";
      s += "database and writes to a file. </p>";
      s += "<p>Detailed Description: ";
      s += "This module constructs a SQL query based on 5 inputs: the database ";
      s += "connection object, the selected table, the selected attributes, ";
      s += "the query condition (optional), and the name of the file to write to. ";
      s += "This module then executes the query and retrieves ";
      s += "the data from the specified database and outputs ";
      s += "database data to a specified file. </p>";
      s += "<p>Restrictions: ";
      s += "We currently only support Oracle, SqlServer, DB2, Postgres and MySql databases. </p>";

      return s;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleName() { 
	   return "Write DB To File"; 
   }

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
         default:
            return "No such output";
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
         default:
            return "NO SUCH OUTPUT!";
      }
   }

   /**
    * This method returns an array of strings that contains the data types for
    * the outputs.
    *
    * @return the data types of all outputs.
    */
   public String[] getOutputTypes() {
      String[] types = {};
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

      PropertyDescription[] descriptions = new PropertyDescription[3];

      descriptions[0] =
         new PropertyDescription("delimChar",
                                 "Delimiter Character (C=comma, S=space, T=tab)",
                                 "Selects the delimiter character used to separate columns in the file.  " +
                                 "Enter C for comma, S for space, or T for tab.");

      descriptions[1] =
         new PropertyDescription("useColumnLabels",
                                 "Write Column Labels",
                                 "Controls whether the column labels should be written to the file.");

      descriptions[2] =
         new PropertyDescription("useDataTypes",
                                 "Write Data Types",
                                 "Controls whether the column data types should be written to the file.");

      return descriptions;

   }

   /**
    * Method getUseColumnLabels().
    *
    * @return java.lang.boolean of variable UseColumnLabels.
    */
   public boolean getUseColumnLabels() { 
	   return useColumnLabels; 
   }

   /**
    * Method getUseDataTypes().
    *
    * @return java.lang.boolean of variable UseDataTypes.
    */
   public boolean getUseDataTypes() { return useDataTypes; }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {

      if (!isInputPipeConnected(3)) {
         return (getInputPipeSize(0) > 0 &&
                    getInputPipeSize(1) > 0 &&
                    getInputPipeSize(2) > 0 &&
                    getInputPipeSize(4) > 0);
      }

      return super.isReady();
   }

   /**
    * Method setDelimChar().
    *
    * @param  c <code>String</code> maybe one of;
    * 			C = comma
    * 			S = space 
    * 			T = tab
    *
    * @throws PropertyVetoException Description of exception
    *                               PropertyVetoException.
    */
   public void setDelimChar(String c) throws PropertyVetoException {

      // here we check for valid entries and save as upper case
      if (c.equalsIgnoreCase("C")) {
         delimChar = "C";
      } else if (c.equalsIgnoreCase("S")) {
         delimChar = "S";
      } else if (c.equalsIgnoreCase("T")) {
         delimChar = "T";
      } else {
         throw new PropertyVetoException("An invalid Delimiter Character was entered. " +
                                         "Enter C for comma, S for space, or T for tab.",
                                         null);
      }
   }

   /**
    * Method setUseColumnLabels().
    *
    * @param b <code>boolean</code>.
    */
   public void setUseColumnLabels(boolean b) { 
	   useColumnLabels = b; 
   }

   /**
    * Method setUseDataTypes().
    *
    * @param b <code>boolean</code>
    */
   public void setUseDataTypes(boolean b) { useDataTypes = b; }
} 
