package ncsa.d2k.modules.core.io.sql;
/**
 * <p>Title: BrowseTables
 * <p>Description: Connect to database and get the table list from the database</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG</p>
 * @author Dora Cai
 * @version 1.0
 */

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.UserView;

import ncsa.d2k.controller.userviews.swing.*;

import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

//import ncsa.d2k.modules.core.datatype.table.*;

import java.sql.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.*;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class BrowseTables
{
  protected ConnectionWrapper cw;
  Connection con;

  /**
   * Constructor
   * @param cw
   * @param query
   * @throws ClassNotFoundException
   * @throws SQLException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public BrowseTables(ConnectionWrapper cw, String query)
     throws ClassNotFoundException, SQLException, InstantiationException,
            IllegalAccessException
  {
    con = cw.getConnection();
  }

  /**
   * Table model for query results
   * @param query The SQL query to use
   * @return The table model for displaying result set
   * @throws SQLException
   */
  public ResultSetTableModel getResultSetTableModel(String query)
    throws SQLException
  {
    // If we've called close(), then we can't call this method
    if (con == null)
      throw new IllegalStateException("Connection already closed");
    // Create a Statement object that will be used to excecute the query.
    // The arguments specify that the returned ResultSet will be
    // scrollable, read-only, and insensitive to changes in the db.
    Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                         ResultSet.CONCUR_READ_ONLY);
    // Run the query, creating a ResultSet
    ResultSet tableSet = stmt.executeQuery(query);
    // Create and return a TableModel for the ResultSet
    return new ResultSetTableModel(tableSet);
  }
    /**
     * Cose database connection
     */
    public void close()
    {
      try {con.close();} // Try to close the connection
      catch (Exception e) {} // Do nothing on error. At least we tried
      con = null;
    }

    /**
     *  Automatically close the connection when we're garbage collected
     */
    protected void finalize() {close();}
}