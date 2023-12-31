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
package ncsa.d2k.modules.core.vis;

import ncsa.d2k.core.gui.ErrorDialog;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.ColumnUtilities;
import ncsa.d2k.modules.core.vis.widgets.TableEditorModel;
import ncsa.d2k.modules.core.vis.widgets.TableMatrix;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.HashMap;


/**
 * Displays a Table and allows the cells to be changed.
 *
 * @author  David Clutter
 * @version $Revision$, $Date$
 */
public class TableEditor extends TableViewer {

   //~ Static fields/initializers **********************************************

   /** Description of field STRING_TYPE. */
   static private final String STRING_TYPE = "String";

   /** Description of field DOUBLE_TYPE. */
   static private final String DOUBLE_TYPE = "double";

   /** Description of field INT_TYPE. */
   static private final String INT_TYPE = "int";

   /** Description of field BYTE_ARRAY_TYPE. */
   static private final String BYTE_ARRAY_TYPE = "byte[]";

   /** Description of field CHAR_ARRAY_TYPE. */
   static private final String CHAR_ARRAY_TYPE = "char[]";

   /** Description of field BOOLEAN_TYPE. */
   static private final String BOOLEAN_TYPE = "boolean";

   /** Description of field FLOAT_TYPE. */
   static private final String FLOAT_TYPE = "float";

   /** Description of field LONG_TYPE. */
   static private final String LONG_TYPE = "long";

   /** Description of field SHORT_TYPE. */
   static private final String SHORT_TYPE = "short";

   /** Description of field BYTE_TYPE. */
   static private final String BYTE_TYPE = "byte";

   /** Description of field CHAR_TYPE. */
   static private final String CHAR_TYPE = "char";

   /** Description of field OBJECT_TYPE. */
   static private final String OBJECT_TYPE = "Object";

   //~ Methods *****************************************************************

   /**
    * Override the superclass' implementation. Return a TableEditorView instead
    * of a VerticalTableView.
    *
    * @return The UserView that we display
    */
   public UserView createUserView() { return new TableEditorView(); }

   /**
    * Return a description of a specific input.
    *
    * @param  i The index of the input
    *
    * @return The description of the input
    */
   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "The table to edit.";

         default:
            return "No such input.";
      }
   }

   /**
    * Return the human readable name of the indexed input.
    *
    * @param  index the index of the input.
    *
    * @return the human readable name of the indexed input.
    */
   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Mutable Table";

         default:
            return "No such input";
      }
   }

   /**
    * Return a String array containing the datatypes the inputs to this module.
    *
    * @return The datatypes of the inputs.
    */
   public String[] getInputTypes() {
      String[] types = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String info = "<p>Overview: ";
      info += "This module displays a table and allows it to be edited. ";
      info += "</p>";

      info += "<p>Detailed Description: ";
      info += "This module can be used to edit a table in a variety of ways. ";
      info +=
         "The value of an individual cell can be edited directly by clicking on the cell, updating value, ";
      info += "and hitting the Enter key. ";
      info +=
         "The datatype of an attribute (column) can be changed by clicking on the column's datatype and using the ";
      info += "combo box that appears to select a new datatype. ";
      info +=
         "One or more rows can be deleted from the table by highlighting the rows that are to be removed, and then ";
      info += "selecting the menu bar option <i>Edit->Remove Rows</i>. ";
      info += "</p>";

      info += "<p>";
      info +=
         "The module performs sanity checks when a cell value is changed, or when a column datatype is changed, to ";
      info += "make sure that the new selection is valid.  ";
      info +=
         "In particular, if an incongruous value is entered in a cell, a message is displayed and the cell is not changed. ";
      info +=
         "If a column datatype is changed, and the conversion fails, a message is displayed and the datatypes of the cells in the ";
      info += "column are not updated. ";
      info += "</p>";

      info += "<p>";
      info += "Should you wish to limit the number of decimal digits displayed ";
      info +=
         "in the table view, please see the <i>maximum fraction digits</i> ";
      info +=
         "property. The underlying data will not be affected <i>unless</i> ";
      info +=
         "you click on a cell in the table (which will be interpreted as an ";
      info += "edit operation).";
      info += "</p>";

      info += "<p>";
      info += "The <i>File</i> pull-down menu offers a <i>Save</i> option to ";
      info += "save the displayed table to a tab-delimited file. ";
      info += "A file browser window pops up, allowing the user to select ";
      info += "where the table should be saved. ";
      info += "Missing values in the table appear as blanks in the saved file. ";
      info += "</p>";

      info += "<p>Known Limitations in Current Release: ";
      info +=
         "This module was designed to work with a single input table per itinerary run. ";
      info +=
         "It will not work properly if it receives multiple inputs per run. ";
      info +=
         "If you accidently direct multiple inputs to the module, it may be necessary ";
      info +=
         "to resize the Table Editor Window before the table contents and <i>Abort</i> ";
      info +=
         "and <i>Done</i> buttons are visible and/or operational.   Until you resize, it may ";
      info +=
         "seem that you have no way to stop the itinerary and correct the problem. ";
      info += "</p>";

      info += "<p>Data Handling: ";
      info +=
         "The data in the input <i>Mutable Table</i> is changed during the edit process.";
      info += "</p>";

      return info;
   } // end method getModuleInfo

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleName() { return "Table Editor"; }

   /**
    * Return the description of a specific output.
    *
    * @param  i The index of the output.
    *
    * @return The description of the output.
    */
   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      switch (i) {

         case 0:
            return "The table after the edits.";

         default:
            return "No such output.";
      }
   }

   /**
    * Return the human readable name of the indexed output.
    *
    * @param  index the index of the output.
    *
    * @return the human readable name of the indexed output.
    */
   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Edited Table";

         default:
            return "No such output";
      }
   }

   /**
    * Return a String array containing the datatypes of the outputs of this
    * module.
    *
    * @return The datatypes of the outputs.
    */
   public String[] getOutputTypes() {
      String[] types = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

      return types;
   }

   //~ Inner Classes ***********************************************************

   /**
    * A specialized TableView that uses a TableEditorModel for the table model.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   public class TableEditorView
      extends TableView /* implements ItemListener */ {

      /** Use serialVersionUID for interoperability. */
      static private final long serialVersionUID = 4657497754395369036L;

      JMenuItem rows;
      protected int[] indices;
      protected HashMap map;
      protected TableEditorModel model;

      protected String[] typeList =
      {
         STRING_TYPE,
         DOUBLE_TYPE,
         INT_TYPE,
         BYTE_TYPE,
         CHAR_ARRAY_TYPE,
         BOOLEAN_TYPE,
         FLOAT_TYPE,
         LONG_TYPE,
         SHORT_TYPE,
         OBJECT_TYPE
      };

      protected JComboBox[] types;

      protected void convertColumn(int i) {
         String selected = (String) types[i].getSelectedItem();

         try {

            if (selected.equals(DOUBLE_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.DOUBLE)) {

                  // get data
                  double[] buffer = new double[table.getNumRows()];
                  boolean[] missing = new boolean[table.getNumRows()];

                  for (int row = 0; row < buffer.length; row++) {
                     missing[row] = table.isValueMissing(row, i);

                     if (missing[row]) {
                        buffer[row] = table.getMissingDouble();
                     } else {
                        buffer[row] = table.getDouble(row, i);
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column =
                     ColumnUtilities.toDoubleColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setDouble(buffer[j], j, i);
                     }
                  }
               } // end if
            } else if (selected.equals(INT_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.INTEGER)) {

                  // get data
                  int[] buffer = new int[table.getNumRows()];
                  boolean[] missing = new boolean[table.getNumRows()];

                  try {

                     for (int row = 0; row < buffer.length; row++) {
                        missing[row] = table.isValueMissing(row, i);

                        if (missing[row]) {
                           buffer[row] = table.getMissingInt();
                        } else {
                           buffer[row] = table.getInt(row, i);
                        }
                     }
                  } catch (NumberFormatException nfe) {

                     // It is possible the column is represented as doubles,
                     // we got a number format exception trying to parse the
                     // chars as an int, let's try getting them as doubles
                     // then casting to int.
                     for (int row = 0; row < buffer.length; row++) {
                        missing[row] = table.isValueMissing(row, i);

                        if (missing[row]) {
                           buffer[row] = table.getMissingInt();
                        } else {
                           buffer[row] = (int) table.getDouble(row, i);
                        }
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column = ColumnUtilities.toIntColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setInt(buffer[j], j, i);
                     }
                  }
               } // end if
            } else if (selected.equals(STRING_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.STRING)) {

                  // get data
                  String[] buffer = new String[table.getNumRows()];
                  boolean[] missing = new boolean[table.getNumRows()];

                  for (int row = 0; row < buffer.length; row++) {
                     missing[row] = table.isValueMissing(row, i);

                     if (missing[row]) {
                        buffer[row] = table.getMissingString();
                     } else {
                        buffer[row] = table.getString(row, i);
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column =
                     ColumnUtilities.toStringColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setString(buffer[j], j, i);
                     }
                  }
               } // end if
            } else if (selected.equals(BYTE_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.BYTE)) {

                  // get data
                  byte[] buffer = new byte[table.getNumRows()];
                  boolean[] missing = new boolean[table.getNumRows()];

                  try {

                     for (int row = 0; row < buffer.length; row++) {
                        missing[row] = table.isValueMissing(row, i);

                        if (missing[row]) {
                           buffer[row] = (byte) table.getMissingInt();
                        } else {
                           buffer[row] = table.getByte(row, i);
                        }
                     }
                  } catch (NumberFormatException nfe) {

                     // It is possible the column is represented as doubles,
                     // we got a number format exception trying to parse the
                     // chars as an int, let's try getting them as doubles
                     // then casting to int.
                     for (int row = 0; row < buffer.length; row++) {
                        missing[row] = table.isValueMissing(row, i);

                        if (missing[row]) {
                           buffer[row] = (byte) table.getMissingInt();
                        } else {
                           buffer[row] = (byte) table.getDouble(row, i);
                        }
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column = ColumnUtilities.toByteColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setByte(buffer[j], j, i);
                     }
                  }
               } // end if
            } else if (selected.equals(CHAR_ARRAY_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.CHAR_ARRAY)) {

                  // get data
                  char[][] buffer = new char[table.getNumRows()][];
                  boolean[] missing = new boolean[table.getNumRows()];

                  for (int row = 0; row < buffer.length; row++) {
                     missing[row] = table.isValueMissing(row, i);

                     if (missing[row]) {
                        buffer[row] = table.getMissingString().toCharArray();
                     } else {
                        buffer[row] = table.getChars(row, i);
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column =
                     ColumnUtilities.toCharArrayColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setChars(buffer[j], j, i);
                     }
                  }
               } // end if
            } else if (selected.equals(FLOAT_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.FLOAT)) {

                  // get data
                  float[] buffer = new float[table.getNumRows()];
                  boolean[] missing = new boolean[table.getNumRows()];

                  for (int row = 0; row < buffer.length; row++) {
                     missing[row] = table.isValueMissing(row, i);

                     if (missing[row]) {
                        buffer[row] = (float) table.getMissingDouble();
                     } else {
                        buffer[row] = table.getFloat(row, i);
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column =
                     ColumnUtilities.toFloatColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setFloat(buffer[j], j, i);
                     }
                  }
               } // end if
            } else if (selected.equals(LONG_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.LONG)) {

                  // get data
                  long[] buffer = new long[table.getNumRows()];
                  boolean[] missing = new boolean[table.getNumRows()];

                  try {

                     for (int row = 0; row < buffer.length; row++) {
                        missing[row] = table.isValueMissing(row, i);

                        if (missing[row]) {
                           buffer[row] = table.getMissingInt();
                        } else {
                           buffer[row] = table.getLong(row, i);
                        }
                     }
                  } catch (NumberFormatException nfe) {

                     // It is possible the column is represented as doubles,
                     // we got a number format exception trying to parse the
                     // chars as an int, let's try getting them as doubles
                     // then casting to int.
                     for (int row = 0; row < buffer.length; row++) {
                        missing[row] = table.isValueMissing(row, i);

                        if (missing[row]) {
                           buffer[row] = table.getMissingInt();
                        } else {
                           buffer[row] = (long) table.getDouble(row, i);
                        }
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column = ColumnUtilities.toLongColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setLong(buffer[j], j, i);
                     }
                  }
               } // end if
            } else if (selected.equals(SHORT_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.SHORT)) {

                  // get data
                  short[] buffer = new short[table.getNumRows()];
                  boolean[] missing = new boolean[table.getNumRows()];

                  try {

                     for (int row = 0; row < buffer.length; row++) {
                        missing[row] = table.isValueMissing(row, i);

                        if (missing[row]) {
                           buffer[row] = (short) table.getMissingInt();
                        } else {
                           buffer[row] = table.getShort(row, i);
                        }
                     }
                  } catch (NumberFormatException nfe) {

                     // It is possible the column is represented as doubles,
                     // we got a number format exception trying to parse the
                     // chars as an int, let's try getting them as doubles
                     // then casting to int.
                     for (int row = 0; row < buffer.length; row++) {
                        missing[row] = table.isValueMissing(row, i);

                        if (missing[row]) {
                           buffer[row] = (short) table.getMissingInt();
                        } else {
                           buffer[row] = (short) table.getDouble(row, i);
                        }
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column = ColumnUtilities.toIntColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setShort(buffer[j], j, i);
                     }
                  }
               } // end if
            } else if (selected.equals(BOOLEAN_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.BOOLEAN)) {

                  // get data
                  boolean[] buffer = new boolean[table.getNumRows()];
                  boolean[] missing = new boolean[table.getNumRows()];

                  for (int row = 0; row < buffer.length; row++) {
                     missing[row] = table.isValueMissing(row, i);

                     if (missing[row]) {
                        buffer[row] = table.getMissingBoolean();
                     } else {
                        buffer[row] = table.getBoolean(row, i);
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column =
                     ColumnUtilities.toBooleanColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setBoolean(buffer[j], j, i);
                     }
                  }
               } // end if
            } else if (selected.equals(BYTE_ARRAY_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.BYTE_ARRAY)) {

                  // get data
                  byte[][] buffer = new byte[table.getNumRows()][];
                  boolean[] missing = new boolean[table.getNumRows()];

                  for (int row = 0; row < buffer.length; row++) {
                     missing[row] = table.isValueMissing(row, i);

                     if (missing[row]) {
                        buffer[row] = table.getMissingString().getBytes();
                     } else {
                        buffer[row] = table.getBytes(row, i);
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column = ColumnUtilities.toByteColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setBytes(buffer[j], j, i);
                     }
                  }
               } // end if
            } else if (selected.equals(CHAR_TYPE)) {

               if (!(table.getColumnType(i) == ColumnTypes.CHAR)) {

                  // get data
                  char[] buffer = new char[table.getNumRows()];
                  boolean[] missing = new boolean[table.getNumRows()];

                  for (int row = 0; row < buffer.length; row++) {
                     missing[row] = table.isValueMissing(row, i);

                     if (missing[row]) {
                        buffer[row] = table.getMissingChar();
                     } else {
                        buffer[row] = table.getChar(row, i);
                     }
                  }

                  // Clone the column.
                  MutableTable mt = (MutableTable) table;
                  Column column = ColumnUtilities.toCharColumn(mt.getColumn(i));
                  mt.setColumn(column, i);

                  // set missing values
                  for (int j = 0; j < missing.length; j++) {

                     if (missing[j]) {
                        mt.setValueToMissing(true, j, i);
                     } else {
                        mt.setChar(buffer[j], j, i);
                     }
                  }
               } // end if
            }
            // Added by DC 6.9.2004
            else if (selected.equals(OBJECT_TYPE)) {
               Object[] buffer = new Object[table.getNumRows()];
               boolean[] missing = new boolean[table.getNumRows()];

               for (int row = 0; row < buffer.length; row++) {
                  missing[row] = table.isValueMissing(row, i);

                  if (missing[row]) {

                     // what goes in here if it is an object column
                     buffer[row] = null;
                  } else {
                     buffer[row] = table.getObject(row, i);
                  }
               }

               // Clone the column.
               MutableTable mt = (MutableTable) table;
               Column column = ColumnUtilities.toObjectColumn(mt.getColumn(i));
               mt.setColumn(column, i);

               // set missing values
               for (int j = 0; j < missing.length; j++) {

                  if (missing[j]) {
                     mt.setValueToMissing(true, j, i);
                  } else {
                     mt.setObject(buffer[j], j, i);
                  }
               }
            } // end if-else

            indices[i] = types[i].getSelectedIndex();
         } catch (Exception exception) {
            types[i].setSelectedIndex(indices[i]);

            // Change combo box selection to previous index
            ErrorDialog.showDialog("Could not convert column type",
                                   "Conversion Error");
         }

      } // end method convertColumn

      /**
       * Loop through each column and convert it to a new type.
       */
      protected void convertTable() {

         for (int i = 0; i < types.length; i++) {
            convertColumn(i);
         }
      }

      protected void finishUp() {
         convertTable();
         super.finishUp();
      }

      protected void removeRows() {
         int offset = table.getNumRows() - matrix.getJTable().getRowCount();
         int[] rows = matrix.getJTable().getSelectedRows();

         for (int row = 0; row < rows.length; row++) {

            if (rows[row] != 0) {
               ((MutableTable) table).removeRow(rows[row] + offset);
               offset--;
            }
         }

         model.fireTableDataChanged();
      }

      /**
       * Setup the types column.
       */
      protected void setupTypes() {
         types = new JComboBox[table.getNumColumns()];
         indices = new int[table.getNumColumns()];

         for (int i = 0; i < types.length; i++) {
            types[i] = new JComboBox(typeList);
            map.put(types[i], new Integer(i));

            switch (table.getColumnType(i)) {

               case ColumnTypes.INTEGER:
                  types[i].setSelectedItem(INT_TYPE);

                  break;

               case ColumnTypes.FLOAT:
                  types[i].setSelectedItem(FLOAT_TYPE);

                  break;

               case ColumnTypes.DOUBLE:
                  types[i].setSelectedItem(DOUBLE_TYPE);

                  break;

               case ColumnTypes.SHORT:
                  types[i].setSelectedItem(SHORT_TYPE);

                  break;

               case ColumnTypes.LONG:
                  types[i].setSelectedItem(LONG_TYPE);

                  break;

               case ColumnTypes.STRING:
                  types[i].setSelectedItem(STRING_TYPE);

                  break;

               case ColumnTypes.CHAR_ARRAY:
                  types[i].setSelectedItem(CHAR_ARRAY_TYPE);

                  break;

               case ColumnTypes.BYTE_ARRAY:
                  types[i].setSelectedItem(BYTE_ARRAY_TYPE);

                  break;

               case ColumnTypes.BOOLEAN:
                  types[i].setSelectedItem(BOOLEAN_TYPE);

                  break;

               case ColumnTypes.OBJECT:
                  types[i].setSelectedItem(OBJECT_TYPE);

                  break;

               case ColumnTypes.BYTE:
                  types[i].setSelectedItem(BYTE_TYPE);

                  break;

               case ColumnTypes.CHAR:
                  types[i].setSelectedItem(CHAR_TYPE);

                  break;

               default:
                  types[i].setSelectedItem(STRING_TYPE);
            }

            indices[i] = types[i].getSelectedIndex();

            // types[i].addItemListener(this);
            types[i].addActionListener(this);
         } // end for
      } // end method setupTypes

      // Listen for events from edit menu
      public void actionPerformed(ActionEvent event) {
         Object source = event.getSource();

         if (source == rows) {
            removeRows();
         }
         // listen for events from combo boxes
         else if (map.containsKey(source)) {
            Integer integer = (Integer) map.get(source);
            convertColumn(integer.intValue());
            model.fireTableDataChanged();
         } else {
            super.actionPerformed(event);
         }
      }

      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param module The module this view is associated with.
       */
      public void initView(ViewModule module) {
         super.initView(module);

         JMenu edit = new JMenu("Edit");
         rows = new JMenuItem("Remove rows");
         rows.addActionListener(this);
         edit.add(rows);
         menuBar.add(edit);
      }

      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param input The object that has been input.
       * @param idx   The index of the module input that been received.
       */
      public void setInput(Object input, int idx) {

         if (idx == 0) {
            removeAll();
            table = (MutableTable) input;
            map = new HashMap();

            // Panel to put the buttons on
            JPanel buttonPanel = new JPanel();
            ok = new JButton("Done");
            ok.addActionListener(this);
            cancel = new JButton("Abort");
            cancel.addActionListener(this);
            buttonPanel.add(cancel);
            buttonPanel.add(ok);
            setupTypes();

            // Create the matrix
            if (maxFractionDigits >= 0) {
               model =
                  new TableEditorModel((MutableTable) table,
                                       types,
                                       maxFractionDigits);
            } else {
               model = new TableEditorModel((MutableTable) table, types);
            }

            matrix = new TableMatrix(model);

            TableColumnModel tc = matrix.getJTable().getColumnModel();

            // Set the DefaultCellEditor to be the JComboBox
            for (int i = 0; i < types.length; i++) {
               tc.getColumn(i).setCellEditor(new VTCellEditor(types[i]));
            }

            // Add everything to this
            add(matrix, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
         } // end if
      } // end method setInput

      /**
       * Uses a Combo box on the first row and a text field for any other row as
       * the cell editor.
       *
       * @author  $Author$
       * @version $Revision$, $Date$
       */
      class VTCellEditor implements TableCellEditor, ActionListener {
         JComboBox box;

         TableCellEditor editor;
         TableCellEditor defaultEditor;

         VTCellEditor(JComboBox comboBox) {
            defaultEditor = new DefaultCellEditor(new JTextField());
            box = comboBox;
         }

         public void actionPerformed(ActionEvent event) {

            if (event.getSource() == rows) {
               cancelCellEditing();
            }
         }

         public void addCellEditorListener(CellEditorListener l) {
            editor.addCellEditorListener(l);
         }

         public void cancelCellEditing() { editor.cancelCellEditing(); }

         public Object getCellEditorValue() {
            return editor.getCellEditorValue();
         }

         public Component getTableCellEditorComponent(JTable tbl,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int column) {

            if (row == 0) {
               editor = new DefaultCellEditor(box);
            } else {
               editor = defaultEditor;
               rows.addActionListener(this);
            }

            return editor.getTableCellEditorComponent(tbl,
                                                      value,
                                                      isSelected,
                                                      row,
                                                      column);
         }

         public boolean isCellEditable(EventObject anEvent) { return true; }

         public void removeCellEditorListener(CellEditorListener l) {
            editor.removeCellEditorListener(l);
         }

         public boolean shouldSelectCell(EventObject anEvent) {
            return editor.shouldSelectCell(anEvent);
         }

         public boolean stopCellEditing() { return editor.stopCellEditing(); }
      } // end class VTCellEditor

   } // end class TableEditorView
} // end class TableEditorURL


// Start QA comments 4/5/03 - Ruth resumed QA after what Vered started
// initially.          Updated module name and info;   2 problems reported to
// Greg.          1) if you edit a cell and then hit "Done" before clicking on
// another cell the update          of the cell value doesn't occur.    2) If
// you change a column type to an illegal value          you get the Exception
// box "Could not convert column type" two times.  Haven't          yet tested
// w/ 'other' table types.  Vered had reported the types of conversions
// allowed weren't consistent across types. 4/10/03 - Greg fixed 2.  Said 1 was
// standard behavior.  Updated info to say "hit Enter".         - Ready for
// Basic End QA comments. End QA comments.

/**
 * basic 4 QA comments
 * 01-04-04:
 * Vered started qa process
 *
 * bug 196 -  preservation of double values when converting from double
 * to byte[] type and then back to double. all values are being zeroed.
 * fixed
 *
 * Bug 197 - cannot convert from String type to a numeric type when a column
 * holds missing values. same problem is with converting char[] to a numeric type.
 * fixed
 *
 * 01-06-04:
 * bug 196 is fixed. though this problem actually occures with all conversions from
 * byte array to a numeric type.
 * bug 197 is also fixed.
 *
 * 01-12-04:
 * bug 223 - Exception with subset table when trying to change the type of a column. (fixed)
 *
 * 01-29-04:
 * bug 200 is fixed.
 *
 * bug 238 - cannot convert from int/long/short to double/float then to char[] and
 * then back to int/short/long. last conversion fails. (fixed)
 *
 * 01-30-04:  ready for basic 4.
*/
