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
 * <p>Title: </p> 
 * <p>Description: This class obtains a ResultSetTableModel 
 * 		for the query and uses it ot display the results of 
 * 		the query in a scrolling JTable component </p> 
 * <p>Copyright: Copyright (c) 2001</p> 
 * <p>Company: NCSA ALG </p> 
 * @author Dora Cai 
 * @version 1.0
 * 
 */
import ncsa.d2k.core.gui.JD2KFrame;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;


/**
 * Description of class BrowseTablesView.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class BrowseTablesView extends JD2KFrame implements ActionListener {

   //~ Instance fields *********************************************************

   /** Description of field aTable. */
   JTable aTable; // The table for displaying available database tables

   /** Description of field bt. */
   BrowseTables bt;

   /** Description of field chosenTableName. */
   String chosenTableName;
   // This class has two constructors. One is used for general queries, which
   // takes a query as the input. Another is used for listing table name or
   // column name, which takes a vector as the input.

   /** Description of field model. */
   TableModel model;

   /** Description of field msgBoard. */
   JOptionPane msgBoard = new JOptionPane();

   /** Description of field okBtn. */
   JButton okBtn; // Button for choosing a table

   /** Description of field selectedRow. */
   int selectedRow;

   //~ Constructors ************************************************************

   /**
    * Constructor.
    *
    * @param t     The BrowseTables object
    * @param query The SQL query to retrieve database table definition
    */
   public BrowseTablesView(BrowseTables t, String query) {

      // remember the BrowseTables object that was passed to us
      this.bt = t;

      // create the Swing components we'll be using
      aTable = new JTable();
      aTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      ListSelectionModel rowSM = aTable.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {

            /**
             * Detect a row has been selected.
             *
             * @param e The SelectionEvent listener
             */
            public void valueChanged(ListSelectionEvent e) {
               ListSelectionModel lsm = (ListSelectionModel) e.getSource();

               if (lsm.isSelectionEmpty()) {
                  selectedRow = -1;
                  System.out.println("No Selected Rows........");
               } else {
                  selectedRow = lsm.getMinSelectionIndex();
                  // System.out.println("Selected Row........"+selectedRow);
               }
            }
         });

      // Place the components within this window
      Container contentPane = getContentPane();
      contentPane.add(new JScrollPane(aTable), BorderLayout.CENTER);
      okBtn = new JButton("OK");
      okBtn.setSize(5, 5);
      okBtn.addActionListener(this);
      contentPane.add(okBtn, BorderLayout.SOUTH);
      displayQueryResults(query.toString());
   }

   /**
    * Constructor.
    *
    * @param t      The BrowseTables object
    * @param result The result set for meta queries, such as the list of tables
    *               or columns
    */
   public BrowseTablesView(BrowseTables t, Vector result) {

      // remember the BrowseTables object that was passed to us
      this.bt = t;

      // create the Swing components we'll be using
      aTable = new JTable();
      aTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      ListSelectionModel rowSM = aTable.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {

            /**
             * Detect a row has been selected.
             *
             * @param e The SelectionEvent listener
             */
            public void valueChanged(ListSelectionEvent e) {
               ListSelectionModel lsm = (ListSelectionModel) e.getSource();

               if (lsm.isSelectionEmpty()) {
                  selectedRow = -1;
                  System.out.println("No Selected Rows........");
               } else {
                  selectedRow = lsm.getMinSelectionIndex();
                  // System.out.println("Selected Row........"+selectedRow);
               }
            }
         });

      // Place the components within this window
      Container contentPane = getContentPane();
      contentPane.add(new JScrollPane(aTable), BorderLayout.CENTER);
      okBtn = new JButton("OK");
      okBtn.setSize(5, 5);
      okBtn.addActionListener(this);
      contentPane.add(okBtn, BorderLayout.SOUTH);
      displayQueryResults(result);
   }

   //~ Methods *****************************************************************

   /**
    * Event trigger.
    *
    * @param e The event detected
    */
   public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();

      if (src == okBtn) {
         dispose();
      }
   }

   /**
    * This method uses the supplied SQL query string, and the BrowseTables
    * object to create a TableModel that holds the results of the database
    * query. It passes that TableModel to the JTable component for display
    *
    * @param q The SQL query string to use
    */
   public void displayQueryResults(final String q) {

      try {

         // Use the BrowseTables object to obtain a TableModel object for
         // the query results and display that model in the JTable component.
         aTable.setModel(bt.getResultSetTableModel(q));
      } catch (SQLException ex) {
         JOptionPane.showMessageDialog(msgBoard,
                                       "SQL error: " + ex.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         System.out.println("SQL error in displayQueryResults for query.");
      }
   } /* end of displayQueryResult */

   /**
    * This method uses the supplied ResultSet, and the BrowseTables object to
    * create a TableModel that holds this ResultSet. It passes that TableModel
    * to the JTable component for display
    *
    * @param v The ResultSet to use
    */
   public void displayQueryResults(final Vector v) {

      // Use the BrowseTables object to obtain a TableModel object for
      // the query results and display that model in the JTable component.
      try {
         aTable.setModel(bt.getResultSetTableModel(v));
      } catch (SQLException ex) {
         JOptionPane.showMessageDialog(msgBoard,
                                       "SQL error: " + ex.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         System.out.println("SQL error in displayQueryResults for ResultSet.");
      }
   } /* end of displayQueryResult */

   /**
    * Get the table name a user has selected.
    *
    * @return The table name a user has selected
    */
   public String getChosenRow() {
      model = aTable.getModel();
      chosenTableName = model.getValueAt(selectedRow, 0).toString();

      return (chosenTableName);
   }

   /**
    * Get the index of the selected row.
    *
    * @return the row index
    */
   public int getSelectedRow() { return (this.selectedRow); }

} // end class BrowseTablesView
