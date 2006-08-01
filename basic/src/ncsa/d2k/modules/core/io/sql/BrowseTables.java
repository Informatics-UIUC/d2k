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

/**
 * <p>Title: BrowseTables <p>
 * Description: Connect to database and get the table list from the database</p> 
 * <p>Copyright: Copyright (c) 2001</p> 
 * <p>Company: NCSA ALG</p> 
 * @author Dora Cai 
 * @version 1.0
 * 
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;


public class BrowseTables {

   //~ Instance fields *********************************************************

   /** Description of field con. */
   Connection con;

   /** Description of field cw. */
   protected ConnectionWrapper cw;

   //~ Constructors ************************************************************

   // This class has two constructors. One is used for general queries, which
   // takes a query as the input. Another is used for listing table name or
   // column name, which takes a vector as the input.
   /**
    * Constructor.
    *
    * @param  cw    Description of parameter $param.name$.
    * @param  query Description of parameter $param.name$.
    *
    * @throws ClassNotFoundException
    * @throws SQLException
    * @throws InstantiationException
    * @throws IllegalAccessException
    */
   public BrowseTables(ConnectionWrapper cw, String query)
      throws ClassNotFoundException, SQLException, InstantiationException,
             IllegalAccessException { con = cw.getConnection(); }

   /**
    * Constructor.
    *
    * @param  cw     Description of parameter $param.name$.
    * @param  result This constructor used for meta queries, such as get table
    *                list or column list
    *
    * @throws ClassNotFoundException
    * @throws SQLException
    * @throws InstantiationException
    * @throws IllegalAccessException
    */
   public BrowseTables(ConnectionWrapper cw, Vector result)
      throws ClassNotFoundException, SQLException, InstantiationException,
             IllegalAccessException { con = cw.getConnection(); }

   //~ Methods *****************************************************************

   /**
    * Automatically close the connection when we're garbage collected.
    */
   protected void finalize() { } // Don't close the session. More trans are
                                 // following

   /**
    * Close database connection.
    */
   public void close() {

      try {
         con.close();
      } // Try to close the connection
      catch (Exception e) { } // Do nothing on error. At least we tried

      con = null;
   }

   /**
    * Table model for query results.
    *
    * @param  query The SQL query to use
    *
    * @return The table model for displaying result set
    *
    * @throws SQLException
    * @throws IllegalStateException Description of exception
    *                               IllegalStateException.
    */
   public ResultSetTableModel getResultSetTableModel(String query)
      throws SQLException {

      // If we've called close(), then we can't call this method
      if (con == null) {
         throw new IllegalStateException("Connection already closed");
      }

      // Create a Statement object that will be used to excecute the query.
      // The arguments specify that the returned ResultSet will be
      // scrollable, read-only, and insensitive to changes in the db.
      Statement stmt =
         con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                             ResultSet.CONCUR_READ_ONLY);

      // Run the query, creating a ResultSet
      ResultSet tableSet = stmt.executeQuery(query);

      // Create and return a TableModel for the ResultSet
      return new ResultSetTableModel(tableSet);
   }

   /**
    * Table model for query results.
    *
    * @param  tableSet The result set for meta queries, such as the list of
    *                  tables, columns
    *
    * @return The table model for displaying result set
    *
    * @throws SQLException
    * @throws IllegalStateException Description of exception
    *                               IllegalStateException.
    */
   public ResultSetTableModel getResultSetTableModel(Vector tableSet)
      throws SQLException {

      // If we've called close(), then we can't call this method
      if (con == null) {
         throw new IllegalStateException("Connection already closed");
         // Create a Statement object that will be used to excecute the query.
         // The arguments specify that the returned ResultSet will be
         // scrollable, read-only, and insensitive to changes in the db.
      }

      return new ResultSetTableModel(tableSet);
   }
} // end class BrowseTables
