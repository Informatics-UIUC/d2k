package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: </p>
 * <p>Description: This class obtains a ResultSetTableModel for the query
 * and uses it ot display the results of the query in a scrolling JTable
 * component </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.UserView;

import ncsa.d2k.controller.userviews.swing.*;

import ncsa.gui.Constrain;
import ncsa.d2k.gui.*;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;

public class BrowseTablesView extends JD2KFrame
   implements ActionListener
{
  BrowseTables bt;
  JTable table; // The table for displaying available database tables
  JButton okBtn; // Button for choosing a table
  int selectedRow;
  TableModel model;
  JOptionPane msgBoard = new JOptionPane();
  String choosedTableName;
  /**
   * Constructor
   * @param t The BrowseTables object
   * @param query The SQL query to retrieve database table definition
   */
  public BrowseTablesView(BrowseTables t, String query)
  {
    // remember the BrowseTables object that was passed to us
    this.bt = t;
    // create the Swing components we'll be using
    table = new JTable();
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel rowSM = table.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      /**
       * Detect a row has been selected
       * @param e The SelectionEvent listener
       */
      public void valueChanged(ListSelectionEvent e)
      {
        ListSelectionModel lsm =
                    (ListSelectionModel)e.getSource();
        if (lsm.isSelectionEmpty())
        {
          int selectedRow = -1;
          System.out.println("No Selected Rows........");
        }
        else
        {
          selectedRow = lsm.getMinSelectionIndex();
          System.out.println("Selected Row........"+selectedRow);
        }
      }
    });
    // Place the components within this window
    Container contentPane = getContentPane();
    contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
    okBtn = new JButton("OK");
    okBtn.setSize(5,5);
    okBtn.addActionListener(this);
    contentPane.add(okBtn,BorderLayout.SOUTH);
    displayQueryResults(query.toString());
  }

  /**
   * This method uses the supplied SQL query string, and the BrowseTables
   * object to create a TableModel that holds the results of the database query.
   * It passes that TableModel to the JTable component for display
   * @param q The SQL query string to use
  */
  public void displayQueryResults(final String q)
  {
      try {
      // Use the BrowseTables object to obtain a TableModel object for
      // the query results and display that model in the JTable component.
        table.setModel(bt.getResultSetTableModel(q));
      }
      catch (SQLException ex) {
        JOptionPane.showMessageDialog(msgBoard,
          "SQL error: " + ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
        System.out.println("SQL error");
      }
  } /* end of displayQueryResult */
  /**
   * Event trigger
   * @param e The event detected
   */
  public void actionPerformed(ActionEvent e)
  {
    Object src = e.getSource();
    if (src == okBtn)
    {
      System.out.println("OK button is pressed");
      dispose();
    }
  }
  /**
   * Get the table name a user has selected
   * @return The table name a user has selected
   */
  public String getChoosedTableName()
  {
      model = table.getModel();
      choosedTableName = model.getValueAt(selectedRow,0).toString();
      return (choosedTableName);
  }
}