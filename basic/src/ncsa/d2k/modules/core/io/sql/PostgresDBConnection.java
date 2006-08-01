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

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


/**
 * <p>Title:</p>
 *
 * <p>Description:</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company:</p>
 *
 * @author  not attributable
 * @version 1.0
 */
public class PostgresDBConnection extends SQLDBConnection {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -5216487378160032559L;

   //~ Constructors ************************************************************

   /**
    * Creates a new PostgresDBConnection object.
	*
    * @param _url      <code>String</code>JDBC URL .
    * @param _driver   <code>String</code>JDBC Driver.
    * @param _username <code>String</code>Database username.
    * @param _password <code>String</code>Database password.
    */
   public PostgresDBConnection(String _url, String _driver, String _username,
                               String _password) {
      super(_url, _driver, _username, _password);
   }

   //~ Methods *****************************************************************

   /**
    * Method getAllTableNamesQuery().
    * 
    * TODO: This method used getAllTableNamesQuery(tableName) that should be replaced and coded
    * 		to use the connect.metadata.getTables() method instead.  Once complete 
    * 		for all references the getAllTableNamesQuery should be depericated. 
	*
    * @return Description of return value.
    */
   protected String getAllTableNamesQuery() {
      String str =
         "select tablename from pg_tables where tablename not like 'pg_%';";

      return str;
   }

   /**
    * Method getFirstRowQuery() fetch one row from table.
    *
    * TODO: This method used getFirstRowQuery(tableName) that should be replaced and coded
    * 		to use the connect.metadata.getColumns() method instead.  Once complete 
    * 		for all references the getFirstRowQuery should be depericated. 

    * @param  tableName <code>String</code> name of table to foucs on.
    *
    * @return java.lang.String containing SQL Statement to fetch one row from table.
    */
   protected String getFirstRowQuery(String tableName) {
      String str = "SELECT * FROM " + tableName + " LIMIT 1";

      return str;
   }
   
   /**
    * Method getTableQuery() creates a SQL Select Statement.
    *
    * @param  tables  <code>String[]</code> Array of tableNames to focus on.
    * @param  columns <code>String[][]</code> Array columnNames indexed by table.
    * @param  where   <code>String</code> fully formatted where clause
    *
    * @return Description of return value.
    * 
    * TODO: Method should be refactored to alias column names with table identifier 
    */
/**
 * Refactor sample ??
 *    
   public String getTableQuery(String[] tables, String[][]columns, String where) { 
	   StringBuffer query = new StringBuffer();
	   query.append("Select \n"); 
	   for(int cols=0; cols<columns.length;cols++){ 
		   for(int tabs=0; tabs<tables.length; tabs++){
	          query.append( tables[tabs]); 
	          query.append( "."); 
	          query.append(columns[cols]); 
	          if(cols== columns.length -1 ){ 
	        	  query.append(" \n"); 
	          } else { 
	        	  query.append(" , \n"); } 
	          } 
		   } 
	   query.append(" From \n"); 
	   for( int tabs=0; tabs< tables.length; tabs++) { 
		   query.append(tables[tabs]); 
		   if( tabs== tables.length ){ 
			   query.append( " \n" );
	       } else { 
	    	   query.append( " , \n"); 
	       } 
	   } 
	   if ((where != null) && (where.length() > 0)) { 
		   query.append(" WHERE \n");
	       query.append(where); 
	   } // Should not require blind Order by..
       query.append(" Order By "); 
       for(int cols=0;cols<columns.length;cols++){ 
    	   query.append(cols+1);
           if(cols==columns.length-1){ 
        	   query.append(" "); 
           } else {
               query.append(" , "); 
           } 
       } 
       return query.toString(); 
    }
**
*/
   protected String getTableQuery(String[] tables, String[][] columns,
                                  String where) {
      StringBuffer query = new StringBuffer();

      if (tables.length == 1) { // USER SELECTED ONLY 1 TABLE
         query.append("SELECT ");

         for (int tabl = 0; tabl < columns.length; tabl++) {

            for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
               query.append(columns[tabl][tablCol]);

               if (tablCol < columns[tabl].length - 1) {
                  query.append(", ");
               }
            }
         }

         query.append(" FROM ");
         query.append(tables[0]);

         if ((where != null) && (where.length() > 0)) {
            query.append(" WHERE ");
            query.append(where);
         }

         query.append(" ORDER BY ");

         for (int tabl = 0; tabl < columns.length; tabl++) {

            for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
               query.append(columns[tabl][tablCol]);

               if (tablCol < columns[tabl].length - 1) {
                  query.append(", ");
               }
            }
         }
         return query.toString();
      } else { // USER SELECTED >1 TABLES

// separate the columns into uniqueColumns and duplicateColumns
         int i = 0;
         Set uniques = new HashSet();
         Set dups = new HashSet();

         for (int tabl = 0; tabl < columns.length; tabl++) {

            for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {

               if (!uniques.add(columns[tabl][tablCol])) {
                  dups.add(columns[tabl][tablCol]);
               }
            }
         }

         uniques.removeAll(dups); // Destructive set-difference

         Vector uniqueVec = new Vector(uniques);
         Vector duplicateVec = new Vector(dups);
         // First : Create SELECT Clause
         query.append("SELECT ");

         for (int l = 0; l < duplicateVec.size(); l++) {

			// get the 2 tables that a duplicate column belongs to first find the two [x]
			// indices of columns[x][y] where the column is present next, use these indices
			// on the tables[] array to retrieve the tables
            int idx1 = 0;
            // int idx2 = 0;
            int table1;
            int table2;

            for (table1 = 0; table1 < columns.length; table1++) {

               for (
                    int tablCol = 0;
                       tablCol < columns[table1].length;
                       tablCol++) {

                  if (columns[table1][tablCol] == duplicateVec.elementAt(l)) {
                     idx1 = table1;

                     break;
                  }
               }
            }

            for (table2 = table1; table2 < columns.length; table2++) {

               for (
                    int tablCol = 0;
                       tablCol < columns[table2].length;
                       tablCol++) {

                  if (columns[table2][tablCol] == duplicateVec.elementAt(l)) {
                     // idx2 = table2;
                     break;
                  }
               }
            }
            // now append "<TableName1>.<DuplicateColumnName>"

            query.append(tables[idx1]);
            query.append(".");
            query.append(duplicateVec.elementAt(l));

            if (l < duplicateVec.size() - 1) {
               query.append(", ");
            }
         } // end for

         if ((duplicateVec.size() > 0) && (uniqueVec.size() > 0)) {
            query.append(", ");
         }

         int ct = 0;

         for (int tabl = 0; tabl < columns.length; tabl++) {

            for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {

               if (duplicateVec.contains(columns[tabl][tablCol])) {
                  continue;
               } else {
                  ct++;
                  query.append(tables[tabl]); // ..........
                  query.append("."); // ..........
                  query.append(columns[tabl][tablCol]);

                  if (ct < uniqueVec.size()) {
                     query.append(", ");
                  }
               }
            }
         }
         // Second : Create FROM Clause

         query.append(" FROM ");

         for (int k = 0; k < tables.length; k++) {
            query.append(tables[k]);

            if (k < tables.length - 1) {
               query.append(", ");
            }
         }
         // Thrid : Create WHERE Clause
         if ((where != null) && (where.length() > 0)) {
            query.append(" WHERE ");
            query.append(where);
         }

         query.append(" ORDER BY ");

         for (int l = 0; l < duplicateVec.size(); l++) {

			// get the 2 tables that a duplicate column belongs to first find the two [x]
			// indices of columns[x][y] where the column is present next, use these indices
			// on the tables[] array to retrieve the tables
            int idx1 = 0;
            // int idx2 = 0;
            int table1;
            int table2;

            for (table1 = 0; table1 < columns.length; table1++) {

               for (
                    int tablCol = 0;
                       tablCol < columns[table1].length;
                       tablCol++) {

                  if (columns[table1][tablCol] == duplicateVec.elementAt(l)) {
                     idx1 = table1;

                     break;
                  }
               }
            }

            for (table2 = table1; table2 < columns.length; table2++) {

               for (
                    int tablCol = 0;
                       tablCol < columns[table2].length;
                       tablCol++) {

                  if (columns[table2][tablCol] == duplicateVec.elementAt(l)) {
                     // idx2 = table2;

                     break;
                  }
               }
            }
            // now append "<TableName1>.<DuplicateColumnName>"

            query.append(tables[idx1]);
            query.append(".");
            query.append(duplicateVec.elementAt(l));

            if (l < duplicateVec.size() - 1) {
               query.append(", ");
            }
         } // end for

         if ((duplicateVec.size() > 0) && (uniqueVec.size() > 0)) {
            query.append(", ");
         }

         int ct2 = 0;

         for (int tabl = 0; tabl < columns.length; tabl++) {

            for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {

               if (duplicateVec.contains(columns[tabl][tablCol])) {
                  continue;
               } else {
                  ct2++;
                  query.append(tables[tabl]); // ..........
                  query.append("."); // ..........
                  query.append(columns[tabl][tablCol]);

                  if (ct2 < uniqueVec.size()) {
                     query.append(", ");
                  }
               }
            }
         }

         return query.toString();
      } // else
   } // getTableQuery()

   /**
    * Method createTable().
    *
    * Not implemented for Postgres 
    * 
    * @param  tableName       Description of parameter tableName.
    * @param  colNames        Description of parameter colNames.
    * @param  colTypeNames    Description of parameter colTypeNames.
    * @param  colDisplaySizes Description of parameter colDisplaySizes.
    *
    * @throws RuntimeException Description of exception RuntimeException.
    * 
    * TODO: Buid this code set, should there be Sequence/Non-Sequence versions ?
    * 
    */
   public void createTable(String tableName,
                           String[] colNames,
                           String[] colTypeNames,
                           int[] colDisplaySizes) {
      throw new RuntimeException("PostgresDBConnection : createTable not implemented.");
   }

   /**
    * Method createTable().
    *
    * Not implemented for Postgres 
    * 
    * @param  tableName       Description of parameter tableName.
    * @param  seqName         Description of parameter seqName.
    * @param  colNames        Description of parameter colNames.
    * @param  colTypeNames    Description of parameter colTypeNames.
    * @param  colDisplaySizes Description of parameter colDisplaySizes.
    * @param  numRows         Description of parameter numRows.
    *
    * @throws RuntimeException Description of exception RuntimeException.
    * 
    * TODO: Buid this code set, should there be Sequence/Non-Sequence versions ?
    * 
    */
   public void createTable(String tableName,
                           String seqName,
                           String[] colNames,
                           String[] colTypeNames,
                           int[] colDisplaySizes,
                           int numRows) {
      throw new RuntimeException("PostgresDBConnection : createTable not implemented.");
   }
} // DBConnection
