package ncsa.d2k.modules.core.io.sql;
/**
 * <p>Title: BrowseTables
 * <p>Description: This class takes a JDBC ResultSet object and implements
 * the TableModel interface in terms of it so that a Swing JTable component
 * can display the contents of the ResultSet. </p>
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

public class ResultSetTableModel implements TableModel{
    ResultSet results;             // The ResultSet to interpret
    ResultSetMetaData metadata;    // Additional information about the results
    int numcols, numrows;          // How many rows and columns in the table

    /**
     * This constructor creates a TableModel from a ResultSet.
     * @param results The result set to display
     * @throws SQLException
     **/
    ResultSetTableModel(ResultSet results) throws SQLException {
	this.results = results;                 // Save the results
	metadata = results.getMetaData();       // Get metadata on them
	numcols = metadata.getColumnCount();    // How many columns?
	results.last();                         // Move to last row
	numrows = results.getRow();             // How many rows?
    }

    /**
     * Call this when done with the table model.  It closes the ResultSet and
     * the Statement object used to create it.
     **/
    public void close() {
	try { results.getStatement().close(); }
	catch(SQLException e) {};
    }

    /** Automatically close when we're garbage collected */
    protected void finalize() { close(); }

    // These two TableModel methods return the size of the table
    public int getColumnCount() { return numcols; }
    public int getRowCount() { return numrows; }

    // This TableModel method returns columns names from the ResultSetMetaData
    public String getColumnName(int column) {
	try {
	    return metadata.getColumnLabel(column+1);
	} catch (SQLException e) { return e.toString(); }
    }

    // This TableModel method specifies the data type for each column.
    // We could map SQL types to Java types, but for this example, we'll just
    // convert all the returned data to strings.
    public Class getColumnClass(int column) { return String.class; }

    /**
     * This is the key method of TableModel: it returns the value at each cell
     * of the table.  We use strings in this case.  If anything goes wrong, we
     * return the exception as a string, so it will be displayed in the table.
     * Note that SQL row and column numbers start at 1, but TableModel column
     * numbers start at 0.
     * @param row The row index
     * @param column The column index
     * @return A object in the specified cell
     **/
    public Object getValueAt(int row, int column) {
	try {
	    results.absolute(row+1);                // Go to the specified row
	    Object o = results.getObject(column+1); // Get value of the column
	    if (o == null) return null;
	    else return o.toString();               // Convert it to a string
	} catch (SQLException e) { return e.toString(); }
    }

    // Our table isn't editable
    public boolean isCellEditable(int row, int column) { return false; }

    // Since its not editable, we don't need to implement these methods
    public void setValueAt(Object value, int row, int column) {}
    public void addTableModelListener(TableModelListener l) {}
    public void removeTableModelListener(TableModelListener l) {}
}