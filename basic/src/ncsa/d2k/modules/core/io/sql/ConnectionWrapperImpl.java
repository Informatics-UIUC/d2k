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


import javax.swing.JOptionPane;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;


/**
 * ConnectionWrapper is a wrapper to java.sql.Connection It is passed around
 * between the modules in the ncsa.d2k.modules.sql package. If the modules are
 * all running on the same machine, the connection is shared. If the
 * ConnectionWrapper gets passed to another machine, a new connection is
 * established using the info contained herein.
 *
 */

public class ConnectionWrapperImpl implements ConnectionWrapper,
                                              java.io.Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -2094547863581955832L;

   //~ Instance fields *********************************************************

   /** The connection. */
   private transient Connection connection;

   /**
    * The fully qualified classname of the JDBC driver needs to be known if the
    * vm doesn't have the class loaded when attempting to establish the
    * connecion.
    */
   private String driver;

   /** Description of field password. */
   private String password;

   /** The url identifies the data source when establishing the connection. */
   private String url;

   /**
    * If username and password are not null the version of
    * Driver.getConnection() that takes username and password arguments will be
    * used.
    */
   private String username;

   //~ Constructors ************************************************************

   /**
    * Creates a new ConnectionWrapperImpl object.
    *
    * @param _url      Description of parameter _url.
    * @param _driver   Description of parameter _driver.
    * @param _username Description of parameter _username.
    * @param _password Description of parameter _password.
    */
   public ConnectionWrapperImpl(String _url, String _driver, String _username,
                                String _password) {
      url = _url;
      driver = _driver;
      username = _username;
      password = _password;
   } 

   //~ Methods *****************************************************************

   /**
    * Description of method finalize.
    */
   protected void finalize() {

      try {

         if (connection != null) {
            connection.close();
         }
      } catch (Exception e) { }
   }

   /**
    * Method getConnection().
    *
    * @return connection 
    */
   public Connection getConnection() {

      try {

         // System.out.println ("Entering getConnection");
         if (connection == null) {

            // Class classNm = Class.forName (driver);
            DriverManager.registerDriver((Driver) Class.forName(driver)
                                                       .newInstance());

            // make the connection
            if (username == null) {
               connection = DriverManager.getConnection(url);
            } else {

               // System.out.println ("URL is "+url);
               connection =
                  DriverManager.getConnection(url, username, password);
            }
         }

         // System.out.println("Successful Connection");
         return connection;
      } catch (Exception e) {
         JOptionPane msgBoard = new JOptionPane();
         JOptionPane.showMessageDialog(msgBoard,
                                       "Cannot connect to the database. Please check your 'Connect To DB' " +
                                       "properties and verify that the appropriate driver is installed. ",
                                       "Error",
                                       JOptionPane.ERROR_MESSAGE);
         System.out.println("Error occurred when connecting to a database.");
         e.printStackTrace();

         return null;
      }
   } // end method getConnection

} /* ConnectionWrapper */
