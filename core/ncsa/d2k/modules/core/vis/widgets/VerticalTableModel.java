package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.util.datatype.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
	VerticalTableModel is the table model for the VerticalTableViewer.
	This is a very simple implementation.  All cells are uneditable in this
	model.  A subclass may allow cells in the table to be edited, however.
	@author David Clutter
*/
public class VerticalTableModel extends AbstractTableModel {
	/** The VerticalTable that holds the data */
	protected VerticalTable table;

	/**
		Constructor.
		@param vt The VerticalTable that we represent
	*/
	public VerticalTableModel(VerticalTable vt) {
		table = vt;
	}

	/**
		Return the name of a column in the table.  This returns the label
		of the column from the VerticalTable.
		@param col The column we are interested in.
	*/
	public String getColumnName(int col) {
		if(col == 0)
			return "";
		return table.getColumnLabel(col-1);
	}

	/**
		Return the number of rows in the table.
		@return the number of rows in the table.
	*/
	public int getRowCount() {
		return table.getNumRows();
	}

	/**
		Return the number of columns in the table.
		@return the number of columns in the table.
	*/
	public int getColumnCount() {
		return table.getNumColumns()+1;
	}

	/**
		Return the Object that goes in a particular spot in the table.
		@param row the row of the table to index
		@param column the column of the table to index
		@return the object at table[row][col]
	*/
	public Object getValueAt(int row, int col) {
		if(col == 0)
			return new Integer(row);
		return table.getString (row, col-1);
	}

	/**
		Return whether a particular cell is editable or not.
		Always returns false.
		@param row the row of the table to index
		@param col the column of the table to index
	*/
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
