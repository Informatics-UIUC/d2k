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
import javax.swing.table.AbstractTableModel;


/**
 * <p>Title: MetaTableModel</p> 
 * <p>Description: This model create a table to display meta information 
 * 		for a database table</p> 
 * <p>Copyright: Copyright (c)  2002</p> 
 * <p>Company: NCSA ALG</p> 
 * 
 * @author Dora Cai @version 1.0
 */

public class MetaTableModel extends AbstractTableModel {

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 4648503544541416220L;
	
   //~ Static fields/initializers **********************************************

   /** Description of field NOTHING. */
   static String NOTHING = "";

   //~ Instance fields *********************************************************

   /** Description of field columnNames. */
   String[] columnNames = { "Column Name", "Data Type", "Column Length" };

   /** Description of field data. */
   Object[][] data;

   /** Description of field edit. */
   boolean edit;

   //~ Constructors ************************************************************

   /**
    * Constructor MetaTableModel().
    * 
    * Table Model for displaying meta data: 
    * 		column name, 
    * 		column type, and 
    * 		column length.
    *
    * @param maxRow   Maximum number of rows in the table
    * @param maxCol   Maximum number of columns in the table
    * @param editable Is this table editable?
    */
   public MetaTableModel(int maxRow, int maxCol, boolean editable) {
      edit = editable;
      data = new Object[maxRow][maxCol];
      initTableModel(maxRow, maxCol);
   }

   //~ Methods *****************************************************************

   /**
    * Method getColumnCount().
    * 
    * Get column count of the table.
    *
    * @return The number of columns in the table
    */
   public int getColumnCount() { 
	   return columnNames.length; 
   }
   
   /**
    * Method getColumnName()
    * 
    * Get the name of the column.
    *
    * @param  col The column index
    *
    * @return The name of the column
    */
   public String getColumnName(int col) { 
	   return columnNames[col]; 
   }

   /**
    * Method getRowCount().
    * 
    * Get row count of the table.
    *
    * @return The number of rows in the table
    */
   public int getRowCount() { 
	   return data.length; 
   }

   /**
    * Method getValueAt().
    * 
    * Get the value of a cell.
    *
    * @param  row The row index
    * @param  col The column index
    *
    * @return The object in a cell
    */
   public Object getValueAt(int row, int col) { 
	   return data[row][col]; 
   }

   /**
    * Method initTableModel()
    * 
    * Initialize the table model.
    *
    * @param maxR Maximum number of rows in the table
    * @param maxC Maximum number of columns in the table
    */
   public void initTableModel(int maxR, int maxC) {

      for (int iRow = 0; iRow < maxR; iRow++) {

         for (int iCol = 0; iCol < maxC; iCol++) {
            data[iRow][iCol] = NOTHING;
            fireTableCellUpdated(iRow, iCol);
         }
      }
   }

   /**
    * Method isCellEditable().
    * 
    * Is the cell editable?
    *
    * @param  row The row index
    * @param  col The column index
    *
    * @return true or false
    */
   public boolean isCellEditable(int row, int col) {

      if ((col == 0 || col == 1) && edit == true) {
         return true;
      } else if (
                 col == 2 &&
                    edit == true &&
                    (data[row][1].toString().equals("string") ||
                        data[row][1].toString().equals("byte[]") ||
                        data[row][1].toString().equals("char[]"))) {
         return true;
      } else { // Length column is not editable for numeric and boolean column
         return false;
      }
   }

   /**
    * Method setValueAt().
    * 
    * Set value at a cell.
    *
    * @param value The value to set
    * @param row   The row index
    * @param col   The column index
    */
   public void setValueAt(Object value, int row, int col) {
      boolean pass = validateData(value, row, col);

      if (pass) // pass the validation
      {

         if (col == 0) // first column value can't contain space and minus sign
         {
            value = squeezeSpace(value);

            // column name does not allow "-"
            value = value.toString().replace('-', '_');
         }

         data[row][col] = value.toString().toLowerCase();
         fireTableCellUpdated(row, col);
      }
   } /* end of setValueAt */

   /**
    * Method squeezeSpace().
    * 
    * Squeeze out spaces from the string value.
    *
    * @param  value The string value to edit
    *
    * @return The object after spaces are squeezed out
    */
   public Object squeezeSpace(Object value) {
      int j;
      String strValue = value.toString();
      String newStr = NOTHING;

      for (j = 0; j < value.toString().length(); j++) {

         if (strValue.charAt(j) != ' ') {
            newStr = newStr + strValue.charAt(j);
         }
      }

      value = (Object) newStr;

      return (value);
   }

   /**
    * Method validateData().
    *
    * @param  value The value to validate
    * @param  row   The row index
    * @param  col   The column index
    *
    * @return pass or fail the validation
    */
   public boolean validateData(Object value, int row, int col) {
      JOptionPane msgBoard = new JOptionPane();

      // Must fill the previous row before move to a new row
      if (row > 0) {
         int i;

         for (i = 0; i < 3; i++) {

            if (data[row - 1][i].toString().equals(NOTHING)) {

               switch (i) {
                  case 0:
                     JOptionPane.showMessageDialog(msgBoard,
                                                   "You must give a column name for the previous row.",
                                                   "Error",
                                                   JOptionPane.ERROR_MESSAGE);

                     return (false);
                  case 1:
                     JOptionPane.showMessageDialog(msgBoard,
                                                   "You must choose a data type for the previous row.",
                                                   "Error",
                                                   JOptionPane.ERROR_MESSAGE);

                     return (false);
                  case 2: // length must be specified for varchar datatype
                     if (data[row - 1][1].toString().equals("String")) {
                        JOptionPane.showMessageDialog(msgBoard,
                                                      "You must specify the length of String for the previous row.",
                                                      "Error",
                                                      JOptionPane.ERROR_MESSAGE);

                        return (false);
                     } else {
                        return (true);
                     }
               } /* end of switch */
            } /* end of if (data[row-1][i] == NOTHING */
         } /* end of for */
      } /* end of if (row > 0) */

      return (true);
   } /* end of validateData */
   
} /* end of MetaTableModel */
