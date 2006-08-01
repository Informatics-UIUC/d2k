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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;


/**
 * <p>Title: SQLDBConnection</p>
 *
 * <p>Description: an abstract class with common functionality for Database
 * connections</p>
 *
 * <p>Copyright: NCSA (c) 2002</p>
 *
 * <p>Company:</p>
 *
 * @author  Sameer Mathur, David Clutter
 * @version 1.0
 * 
 * TODO: Noted changes to use JDBC metadata versus hardcoded database dictionary queries.
 * 
 */

public abstract class SQLDBConnection extends ConnectionWrapperImpl
   implements DBConnection {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 4648503544541416220L;

   //~ Constructors ************************************************************

   /**
    * Creates a new SQLDBConnection object.
    *
    * @param _url      <code>String</code>JDBC URL .
    * @param _driver   <code>String</code>JDBC Driver.
    * @param _username <code>String</code>Database username.
    * @param _password <code>String</code>Database password.
    */
   protected SQLDBConnection(String _url, String _driver, String _username,
                             String _password) {
      super(_url, _driver, _username, _password);
   }

   //~ Methods *****************************************************************

   /**
    * Abstract method getAllTableNamesQuery().
    * 
    * TODO: This method used getAllTableNamesQuery(tableName) that should be replaced and coded
    * 		to use the connect.metadata.getTables() method instead.  Once complete 
    * 		for all references the getAllTableNamesQuery should be depericated. 
	*
    * @return Description of return value.
    */
   protected abstract String getAllTableNamesQuery();

   /**
    * Abstract method getFirstRowQuery() fetch one row from table.
    *
    * TODO: This method used getFirstRowQuery(tableName) that should be replaced and coded
    * 		to use the connect.metadata.getColumns() method instead.  Once complete 
    * 		for all references the getFirstRowQuery should be depericated. 

    * @param  tableName <code>String</code> name of table to foucs on.
    *
    * @return java.lang.String containing SQL Statement to fetch one row from table.
    */
   protected abstract String getFirstRowQuery(String tableName);

   /**
    * Abstract method getTableQuery() creates a SQL Select Statement.
    *
    * @param  tables  <code>String[]</code> Array of tableNames to focus on.
    * @param  columns <code>String[][]</code> Array columnNames indexed by table.
    * @param  where   code>String</code> fully formatted where clause
    *
    * @return Description of return value.
    */
   protected abstract String getTableQuery(String[] tables, String[][] columns,
                                           String where);

   /**
    * Method addColumnQuery() creates a SQL Statement in the form of:
    * 		Alter Table add column [varchar(32) | Number]
    *
    * @param  tableName <code>String</code> name of the table to add column to.
    * @param  columnName <code>String</code> name of column to add.
    * @param  columnType <code>String</code> expects either number or varchar.
    *
    * @return java.lang.String containing the SQL Statement.
    */
   private String addColumnQuery(String tableName, String columnName,
                                 String columnType) {
      String str = "ALTER TABLE ";
      str += tableName;
      str += " ADD (";
      str += columnName;
      str += " ";
      str += columnType;

      if (columnType == "varchar") {
         str += "(32)";
      }

      return str;
   }

   /**
    * Method addColumn() create and excute a SQL Statement in the form of:
    * 		Alter Table add column [varchar(32) | Number]
    *		Uses: addColumnQuery()
    *
    * TODO: Evaluate why only varchar and number data-types only are expected, prehaps
    * 		there is a good reason.  Neither varchar or number are flexible in terms of 
    * 		data-lengths since there is no means of defining that value (??).
    * 
    * @param  tableName <code>String</code> name of the table to add column to.
    * @param  columnName <code>String</code> name of column to add.
    * @param  columnType <code>String</code> expects either number or varchar.
    */
   public void addColumn(String tableName, String columnName,
                         String columnType) {

      // 1. create a new column called 'columnName' in the table in the database
      try {
         Connection con = getConnection();
         Statement stmt =
            con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);

         String query = "";

         if (columnType == "number") {
            query = this.addColumnQuery(tableName, columnName, "number");
         } else { // (columnType == "varchar"
            query = this.addColumnQuery(tableName, columnName, "varchar");
         }

         stmt.executeUpdate(query);
         stmt.close();
      } catch (Exception s) {
         s.printStackTrace();
      }
   }
  
   /**
    * Method dropTable() creates and executes SQL Statement in the form:
    * 		Drop Table tablename
    *
    * @param tableName <code>String</code> name of the table to drop.
    */
   public void dropTable(String tableName) {

      try {
         Connection con = getConnection();
         Statement stmt = con.createStatement();
         StringBuffer query = new StringBuffer("DROP TABLE ");
         query.append(tableName);
         stmt.executeUpdate(query.toString());
         stmt.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Method getColumnLength() fetches the max data-length of a column of the named table.
    *
    * TODO: This method used getFirstRowQuery(tableName) that should be replaced and coded
    * 		to use the connect.metadata.getColumns() method instead.  Once complete 
    * 		for all references the getFirstRowQuery should be depericated. 
    * 
    * @param  tableName <code>String</code> name of the table to fetch data-type for.
    * @param  columnName <code>String</code> name of a single column to fetch data-type for.
    * @return int that describes the max data-length of table/column.
    */   
   public int getColumnLength(String tableName, String columnName) {

      try {
         Connection con = getConnection();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
         ResultSetMetaData rsmd = rs.getMetaData();
         int length = 0;

         for (int i = 1; i <= rsmd.getColumnCount(); i++) {

            if (rsmd.getColumnName(i) == columnName) {
               length = rsmd.getColumnDisplaySize(i);

               break;
            }
         }

         stmt.close();

         return length;
      } catch (Exception e) {
         e.printStackTrace();

         return 0;
      }
   } // end method getColumnLength

   /**
    * Method getColumnLengths() fetches the max data length of columns of named table.
    *
    * TODO: This method used getFirstRowQuery(tableName) that should be replaced and coded
    * 		to use the connect.metadata.getColumns() method instead.  Once complete 
    * 		for all references the getFirstRowQuery should be depericated. 
    * 
    * @param  tableName <code>String</code> name of the table to fetch data-type for.
    *
    * @return int[] that describes the max data length columns for the table.
    */
   public int[] getColumnLengths(String tableName) {

      try {
         Connection con = getConnection();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
         ResultSetMetaData rsmd = rs.getMetaData();
         int[] columnsLengths = new int[rsmd.getColumnCount()];

         for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columnsLengths[i - 1] = rsmd.getColumnDisplaySize(i);
         }

         stmt.close();

         return columnsLengths;
      } catch (Exception e) {
         e.printStackTrace();

         return new int[(0)];
      }
   }

   /**
    * Method getColumnNames() fetches the column names of the named table.
    *
    * TODO: This method used getFirstRowQuery(tableName) that should be replaced and coded
    * 		to use the connect.metadata.getColumns() method instead.  Once complete 
    * 		for all references the getFirstRowQuery should be depericated. 
    * 
    * @param  tableName <code>String</code> name of the table to fetch data-type for.
    *
    * @return String[] that describes the column names for the table.
    */
   public String[] getColumnNames(String tableName) {

      try {
         Connection con = getConnection();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
         ResultSetMetaData rsmd = rs.getMetaData();
         String[] columns = new String[rsmd.getColumnCount()];

         for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columns[i - 1] = rsmd.getColumnName(i);
         }

         stmt.close();

         return columns;
      } catch (Exception e) {
         e.printStackTrace();

         return null;
      }
   }

   /**
    * Method getColumnType() fetches the data-types of a column of the named table.
    *
    * TODO: This method used getFirstRowQuery(tableName) that should be replaced and coded
    * 		to use the connect.metadata.getColumns() method instead.  Once complete 
    * 		for all references the getFirstRowQuery should be depericated. 
    * 
    * @param  tableName <code>String</code> name of the table to fetch data-type for.
    * @param  columnName <code>String</code> name of a single column to fetch data-type for.
    * @return int that describes the data-types of table/column.
    */
   public int getColumnType(String tableName, String columnName) {

      try {
         Connection con = getConnection();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
         ResultSetMetaData rsmd = rs.getMetaData();
         int type = 0;

         for (int i = 1; i <= rsmd.getColumnCount(); i++) {

            if (rsmd.getColumnName(i) == columnName) {
               type = rsmd.getColumnType(i);

               break;
            }
         }

         stmt.close();

         return type;
      } catch (Exception e) {
         e.printStackTrace();

         return 0;
      }
   }
  
   /**
    * Method getColumnTypes() fetches the data-types of columns of the named table.
    *
    * TODO: This method used getFirstRowQuery(tableName) that should be replaced and coded
    * 		to use the connect.metadata.getColumns() method instead.  Once complete 
    * 		for all references the getFirstRowQuery should be depericated. 
    * 
    * @param  tableName <code>String</code> name of the table to fetch data-type for.
    *
    * @return int[] that describes the data-types of table columns.
    */
   public int[] getColumnTypes(String tableName) {

      try {
         Connection con = getConnection();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
         ResultSetMetaData rsmd = rs.getMetaData();
         int[] columnsTypes = new int[rsmd.getColumnCount()];

         for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columnsTypes[i - 1] = rsmd.getColumnType(i);
         }

         stmt.close();

         return columnsTypes;
      } catch (Exception e) {
         e.printStackTrace();

         return null;
      }
   }

   /**
    * Method getNumRecords() builds SQLStatement in the form: 
    * 				Select count(*) from Table
    *				Uses getNumRecordsQuery() method to construct query string.
    *
    * TODO: method decrements return value and in comments indicates this value is not 
    * 			is not correct nor can it be right or all count queries must be wrong ??
    *			Fix this inconsistency after making certain there is an issue.
    * 
    * @param  tableName   <code>String[]</code> table names to focused on.
    *
    * @return java.lang.int that is the number returned by query.  
    */
   public int getNumRecords(String tableName) {
      int numRecords = 0;

      try {
         Connection con = getConnection();
         String query = getNumRecordsQuery(tableName);
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(query);

         while (rs.next()) {
            numRecords = rs.getInt(1);
         }
         
         numRecords--; ///////////For some reason, numRecords is one more than it should be..
         stmt.close();

         return numRecords;
      } catch (Exception e) {
         e.printStackTrace();

         return numRecords;
      }
   }

   /**
    * Method getNumRecordsQuery() builds SQLStatement in the form: 
    * 				Select count(*) from Table
    *
    * @param  table   <code>String[]</code> Table name to focuse on.
    *
    * @return <code>String</code> containing the SQL Query string constructed.
    */
   public String getNumRecordsQuery(String tableName) {
      String str = "SELECT COUNT(*) FROM " + tableName;

      return str;
   }

   /**
    * Method getResultSet() builds SQLStatement in the for 
    * 				Select Column[, column ...] from Table [Where ...][Order by ...]
    *				Uses getTableQuery() method to construct query string. 
    *
    * @param  table   <code>String[]</code> Array of table names to focuse on.
    * @param  columns <code>String[][]</code> Array of column names to include.
    * @param  where   <code>String</code> full formatted where clause for Select,
    *
    * @return java.sql.ResultSet Scrollable,Concurrent Read-Only.  
    */
   public ResultSet getResultSet(String[] tables, String[][] columns,
                                 String where) {

      try {
         Connection con = getConnection();
         Statement stmt =
            con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_READ_ONLY);
         ResultSet rs =
            stmt.executeQuery(this.getTableQuery(tables, columns, where));

         return rs;
      } catch (Exception e) {
         e.printStackTrace();

         return null;
      }
   }

   /**
    * Method getTableNames() retrieves list of tablename available on database
 	* 		connection, used getAllTableNamesQuery().
 	* 
    * TODO: Convert method to use con.metadata.getTables to aviod hardcoded query
    * 		incorporated in getAllTableNamesQuery() and depericate it.
    *
    * @return Description of return value.
    */
   public String[] getTableNames() {
      String[] tables;
      Vector vec = new Vector();

      try {
         Connection con = getConnection();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(getAllTableNamesQuery());

         while (rs.next()) {
            vec.addElement((Object) rs.getString(1));
         }

         stmt.close();

         // copy over the tables in the Vector into an Array of Strings
         tables = new String[vec.size()];

         for (int i = 0; i < vec.size(); i++) {
            tables[i] = vec.elementAt(i).toString();
         }

         return tables;
      } catch (Exception e) {
         e.printStackTrace();

         return null;
      }
   } 

   /**
    * Method getUpdatableResultSet() builds SQLStatement in the for 
    * 				Select Column[, column ...] from Table [Where ...][Order by ...]
    *				Uses getTableQuery() method to construct query string. 
    *
    * @param  table   <code>String[]</code> Array of table names to focuse on.
    * @param  columns <code>String[][]</code> Array of column names to include.
    * @param  where   <code>String</code> full formatted where clause for Select,
    *
    * @return java.sql.ResultSet Scrollable,Concurrent Updatable.  
    */
   public ResultSet getUpdatableResultSet(String[] tables, String[][] columns,
                                          String where) {

      try {
         Connection con = getConnection();
         Statement stmt =
            con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
         ResultSet rs =
            stmt.executeQuery(this.getTableQuery(tables, columns, where));

         return rs;
      } catch (Exception e) {
         e.printStackTrace();

         return null;
      }
   }

   /**
    * Method getUpdatableResultSet() builds SQLStatement in the for 
    * 				Select Column[, column ...] from Table. 
    *
    * @param  table   <code>String</code> name of table focused on.
    * @param  columns <code>String[]</code> Array of column names to include.
    * @param  where   <code>String</code> full formatted where clause for Select,
    * 				** NOT USED: This parameter does not get used **
    *
    * @return java.sql.ResultSet Scrollable,Concurrent Updatable.  
    */
   public ResultSet getUpdatableResultSet(String table, String[] columns,
                                          String where) {

      try {
         Connection con = getConnection();
         Statement stmt =
            con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
         StringBuffer query = new StringBuffer();
         query.append("SELECT ");

         for (int i = 0; i < columns.length; i++) {
            query.append(columns[i]);

            if (i < columns.length - 1) {
               query.append(", ");
            } else {
               query.append(" ");
            }
         }

         query.append("FROM ");
         query.append(table);

         ResultSet rs = stmt.executeQuery(query.toString());

         return rs;
      } catch (Exception e) {
         e.printStackTrace();

         return null;
      }
   } 

} 
