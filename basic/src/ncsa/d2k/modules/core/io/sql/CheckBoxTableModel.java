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
 * <p>Title: CheckBoxTableModel</p> 
 * <p>Description: Table model for Checkbox in JTable</p> 
 * <p>Copyright: Copyright (c) 2003</p> 
 * <p>Company: NCSA</p> 
 * @Dora
 * @version 1.0
 */

import javax.swing.table.AbstractTableModel;

import java.util.Vector;


public class CheckBoxTableModel extends AbstractTableModel {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -2462694707492513472L;

   /** col names. */
   // protected String[] cols;
   static private final String[] cols =
      new String[] {
         "Selected Attributes", "Input", "Output"
      };

   //~ Instance fields *********************************************************

   /** vector to store the data in the columns. */
   protected Vector data;

   //~ Constructors ************************************************************

   /**
    * Constructor CheckBoxTableModel().
    * 
    * sets Vector data to a new Vector().
    * 
    */
   public CheckBoxTableModel() {
      super();
      data = new Vector();
   }

   //~ Methods *****************************************************************

   /**
    * Method addRow().
    * 
    * Add a row to the table... if the entry is null, stick an empty string in
    * it's place, otherwise the table throws NullPointers
    *
    * @param o an arrow of objects making 1 row of the table
    */
   public void addRow(Object[] o) {

      for (int i = 0; i < o.length; i++) {

         if (o[i] == null) {
            o[i] = new String();
         }
      }

      data.addElement(o);

      // entry added to the end, so fire an event to notify the table
      // to update itself
      fireTableRowsInserted(data.size() - 1, data.size() - 1);
   }

   /**
    * Method clear()
    * 
    * Clears all rows in the table.
    */
   public void clear() {
      data.removeAllElements();
      fireTableDataChanged();
   }

   /**
    * Method getColumnClass().
    * 
    * Returns the type of object in the column.
    *
    * @param  c the column
    *
    * @return the type of class
    */
   public Class getColumnClass(int c) { return getValueAt(0, c).getClass(); }

   /**
    * Method get ColumnCount().
    * 
    * Returns the total number of columns in the table.
    *
    * @return the # of cols
    */
   public int getColumnCount() { return cols.length; }

   /**
    * Method getColumnName
    * 
    * Returns the column name for a given column.
    *
    * @param  i the column
    *
    * @return the name of that column
    */
   public String getColumnName(int i) { return cols[i]; }

   /**
    * Method getRowCount().
    * 
    * Returns the total number of rows.
    *
    * @return the # of rows
    */
   public int getRowCount() { return data.size(); }

   /**
    * Method getValueAt().
    * 
    * Returns the object in the given coordinates may be overridden by methods
    * that extend this class.
    *
    * @param  row the row number
    * @param  col the column number
    *
    * @return the object in that row/col
    */
   public Object getValueAt(int row, int col) {
      Object[] o = (Object[]) data.elementAt(row);

      return o[col];
   }

   /**
    * Method isCellEditable()
    * 
    * Must override this function. This allows users to select the check in
    * column 1 and no other elements in the table
    *
    * @param  row the row to check if editable
    * @param  col the column to check if editable
    *
    * @return true/false whether we want the user to be able to modify that cell
    *         in the table
    */
   public boolean isCellEditable(int row, int col) {

      if (col == 1 || col == 2) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Method removeRow()
    * 
    * Removes a row in the table.
    *
    * @param i the row number
    */
   public void removeRow(int i) {
      data.removeElementAt(i);
      fireTableRowsDeleted(i, i);
   }

   /**
    * Method setValueAt().
    * 
    * Sets the value in our data model We know to expect only boolean/checkbox
    * values here.
    *
    * @param value the object to set
    * @param row   the row number to set the data
    * @param col   the column number to set the data
    */
   public void setValueAt(Object value, int row, int col) {

      // get the array for the proper row
      Object[] o = (Object[]) data.elementAt(row);

      // replace the boolean object that changed
      o[col] = value;

      // only either input or output can be set to true
      if (((Boolean) value).booleanValue()) {

         if (col == 1) {
            Object newValue = (Object) new Boolean(false);
            setValueAt(newValue, row, 2);
         } else if (col == 2) {
            Object newValue = (Object) new Boolean(false);
            setValueAt(newValue, row, 1);
         }

         fireTableDataChanged();
      }

   }
} // end class CheckBoxTableModel
