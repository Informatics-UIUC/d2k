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
package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.ObjectColumn;
import ncsa.d2k.modules.core.datatype.table.basic.TableImpl;
import ncsa.d2k.modules.core.io.sql.ConnectionWrapper;

import javax.swing.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;


/**
 * <p>Title: SQLcodeBook</p>
 *
 * <p>Description: Get a code book from a database table.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA ALG</p>
 *
 * @author  Dora Cai
 * @version 1.0
 */
public class SQLCodeBook {
   // JOptionPane msgBoard = new JOptionPane();

   //~ Instance fields *********************************************************

   /** database connection. */
   private Connection con;

   /** map code labels to their descriptions. */
   private HashMap map = new HashMap();

   /** the code book. */
   public TableImpl codeBook;

   //~ Constructors ************************************************************

   /**
    * Constructor.
    *
    * @param cw       connection wrapper
    * @param bookName name of codebook
    */
   public SQLCodeBook(ConnectionWrapper cw, String bookName) {
      codeBook = getCodeBook(cw, bookName);
   }

   //~ Methods *****************************************************************

   /**
    * Get codebook from database.
    *
    * @param  cw       connection wrapper
    * @param  bookName name of codebook
    *
    * @return Table containing the contents of codebook
    */
   public TableImpl getCodeBook(ConnectionWrapper cw, String bookName) {
      int codeCount = 0;
      MutableTableImpl book = null;

      try {
         con = cw.getConnection();

         String countQry = new String("select count(*) " + " from " + bookName);

         // System.out.println("countQry is " + countQry);
         Statement countStmt = con.createStatement();
         ResultSet countSet = countStmt.executeQuery(countQry);

         while (countSet.next()) {
            codeCount = countSet.getInt(1);
         }

         // System.out.println("codeCount is " + codeCount);
         countStmt.close();
      } catch (Exception e) {
         JOptionPane.showMessageDialog(null,
                                       e.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         System.out.println("Error to get count in getCodeBook.");
      }

      if (codeCount > 0) {
         Column[] cols = new Column[4];
         cols[0] = new ObjectColumn(codeCount);
         cols[1] = new ObjectColumn(codeCount);
         cols[2] = new ObjectColumn(codeCount);
         cols[3] = new ObjectColumn(codeCount);
         cols[0].setLabel("Attribute");
         cols[1].setLabel("Code");
         cols[2].setLabel("Description");
         cols[3].setLabel("ItemIdx");
         book = new MutableTableImpl(cols);

         try {
            String codeQry =
               new String("select attribute_name, code, description from " +
                          bookName);

            // System.out.println("codeQry is " + codeQry);
            Statement codeStmt = con.createStatement();
            ResultSet codeSet = codeStmt.executeQuery(codeQry);
            int row = 0;

            while (codeSet.next()) {
               book.setString(codeSet.getString(1), row, 0);
               book.setString(codeSet.getString(2), row, 1);
               book.setString(codeSet.getString(3), row, 2);

               String codeLabel =
                  codeSet.getString(1) + "=" + codeSet.getString(2);
               map.put(codeLabel, new Integer(row));
               row++;
            }
         } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                                          e.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.out.println("Error to get code in getCodeBook.");
         }
      } // end if

      return book;
   } // end method getCodeBook

   /**
    * Get the row count from the code table.
    *
    * @return number of rows
    */
   public int getCodeCount() { return (codeBook.getNumRows()); }

   /**
    * Get the description of a code.
    *
    * @param  codeLabel code label
    *
    * @return description of a code
    */
   public String getDescription(String codeLabel) {

      if (!map.containsKey(codeLabel)) {
         return null;
      } else {
         Integer ii = (Integer) map.get(codeLabel);

         return (codeBook.getString(ii.intValue(), 2));
      }
   }
} // end class SQLCodeBook
