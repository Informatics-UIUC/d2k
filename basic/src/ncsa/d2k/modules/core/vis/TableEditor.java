package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import ncsa.gui.ErrorDialog;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
 Displays a Table and allows the cells to be changed
 @author David Clutter
 */
public class TableEditor extends TableViewer {

  public String getModuleInfo() {
    String info = "<p>Overview: ";
    info += "This module displays a table and allows it to be edited. ";
    info += "</p>";

    info += "<p>Detailed Description: ";
    info += "This module can be used to edit a table in a variety of ways. ";
    info += "The value of an individual cell can be edited directly by clicking on the cell, updating value, ";
    info += "and hitting the Enter key. ";
    info += "The datatype of an attribute (column) can be changed by clicking on the column's datatype and using the ";
    info += "combo box that appears to select a new datatype. ";
    info += "One or more rows can be deleted from the table by highlighting the rows that are to be removed, and then ";
    info += "selecting the menu bar option <i>Edit->Remove Rows</i>. ";
    info += "</p>";

    info += "<p>";
    info += "The module performs sanity checks when a cell value is changed, or when a column datatype is changed, to ";
    info += "make sure that the new selection is valid.  ";
    info += "In particular, if an incongruous value is entered in a cell, a message is displayed and the cell is not changed. ";
    info += "If a column datatype is changed, and the conversion fails, a message is displayed and the datatypes of the cells in the ";
    info += "column are not updated. ";
    info += "</p>";

    info += "<p>";
    info += "Should you wish to limit the number of decimal digits displayed ";
    info += "in the table view, please see the <i>maximum fraction digits</i> ";
    info += "property. The underlying data will not be affected <i>unless</i> ";
    info += "you click on a cell in the table (which will be interpreted as an ";
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
    info += "This module was designed to work with a single input table per itinerary run. ";
    info += "It will not work properly if it receives multiple inputs per run. ";
    info += "If you accidently direct multiple inputs to the module, it may be necessary ";
    info += "to resize the Table Editor Window before the table contents and <i>Abort</i> ";
    info += "and <i>Done</i> buttons are visible and/or operational.   Until you resize, it may ";
    info += "seem that you have no way to stop the itinerary and correct the problem. ";
    info += "</p>";

    info += "<p>Data Handling: ";
    info += "The data in the input <i>Mutable Table</i> is changed during the edit process.";
    info += "</p>";
    return info;
  }

  public String getModuleName() {
    return "Table Editor";
  }

  /**
   Return a String array containing the datatypes the inputs to this
   module.
   @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
    return types;
  }

  /**
   Return a description of a specific input.
   @param i The index of the input
   @return The description of the input
   */
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "The table to edit.";
      default: return "No such input.";
    }
  }

  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch(index) {
      case 0:
        return "Mutable Table";
      default: return "No such input";
    }
  }

  /**
   * Return the human readable name of the indexed output.
   * @param index the index of the output.
   * @return the human readable name of the indexed output.
   */
  public String getOutputName(int index) {
    switch(index) {
      case 0:
        return "Edited Table";
      default: return "No such output";
    }
  }

  /**
   Return a String array containing the datatypes of the outputs of this
   module.
   @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
    return types;
  }

  /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
       */
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "The table after the edits.";
      default: return "No such output.";
    }
  }

  /**
   Override the superclass' implementation.  Return a TableEditorView
   instead of a VerticalTableView.
   @return The UserView that we display
   */
  public UserView createUserView() {
    return new TableEditorView();
  }

  private static final String STRING_TYPE = "String";
  private static final String DOUBLE_TYPE = "double";
  private static final String INT_TYPE = "int";
  private static final String BYTE_ARRAY_TYPE = "byte[]";
  private static final String CHAR_ARRAY_TYPE = "char[]";
  private static final String BOOLEAN_TYPE = "boolean";
  private static final String FLOAT_TYPE = "float";
  private static final String LONG_TYPE = "long";
  private static final String SHORT_TYPE = "short";
  private static final String BYTE_TYPE = "byte";
  private static final String CHAR_TYPE = "char";
  private static final String OBJECT_TYPE = "Object";

  /**
     A specialized TableView that uses a TableEditorModel for the
     table model.
     */
  public class TableEditorView extends TableView /* implements ItemListener */ {

    protected JComboBox[] types;
    protected int[] indices;
    protected HashMap map;
    protected TableEditorModel model;

    JMenuItem rows;

    String[] typeList = {STRING_TYPE, DOUBLE_TYPE, INT_TYPE,
      BYTE_ARRAY_TYPE, CHAR_ARRAY_TYPE,
      BOOLEAN_TYPE, FLOAT_TYPE, LONG_TYPE,
      SHORT_TYPE};

    public void initView(ViewModule module) {
      super.initView(module);

      JMenu edit = new JMenu("Edit");
      rows = new JMenuItem("Remove rows");
      rows.addActionListener(this);
      edit.add(rows);
      menuBar.add(edit);
    }

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
           model = new TableEditorModel((MutableTable) table, types, maxFractionDigits);
        }
        else {
           model = new TableEditorModel((MutableTable) table, types);
        }
        matrix = new TableMatrix(model);

        TableColumnModel tc = matrix.getJTable().getColumnModel();
        // Set the DefaultCellEditor to be the JComboBox
        for(int i = 0; i < types.length; i++)
          tc.getColumn(i).setCellEditor(new VTCellEditor(types[i]));

        // Add everything to this
        add(matrix, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
      }
    }

    /**
     Uses a Combo box on the first row and a text field for
     any other row as the cell editor.
     */
    class VTCellEditor implements TableCellEditor, ActionListener {

      TableCellEditor editor, defaultEditor;
      JComboBox box;

      VTCellEditor(JComboBox comboBox) {
        defaultEditor = new DefaultCellEditor(new JTextField());
        box = comboBox;
      }

      public Component getTableCellEditorComponent(JTable tbl,
          Object value, boolean isSelected, int row, int column) {

        if(row == 0)
          editor = new DefaultCellEditor(box);
        else {
          editor = defaultEditor;
          rows.addActionListener(this);
        }
        return editor.getTableCellEditorComponent(tbl, value,
            isSelected, row, column);
      }

      public Object getCellEditorValue() {
        return editor.getCellEditorValue();
      }

      public boolean stopCellEditing() {
        return editor.stopCellEditing();
      }

      public void cancelCellEditing() {
        editor.cancelCellEditing();
      }

      public boolean isCellEditable(EventObject anEvent) {
        return true;
      }

      public void addCellEditorListener(CellEditorListener l) {
        editor.addCellEditorListener(l);
      }

      public void removeCellEditorListener(CellEditorListener l) {
        editor.removeCellEditorListener(l);
      }

      public boolean shouldSelectCell(EventObject anEvent) {
        return editor.shouldSelectCell(anEvent);
      }

      public void actionPerformed(ActionEvent event) {
        if (event.getSource() == rows)
          cancelCellEditing();
      }
    }

    /**
     Setup the types column.
     */
    protected void setupTypes() {
      types = new JComboBox[table.getNumColumns()];
      indices = new int[table.getNumColumns()];
      for(int i = 0; i < types.length; i++) {
        types[i] = new JComboBox(typeList);
        map.put(types[i], new Integer(i));

        switch(table.getColumnType(i)) {
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
      }
    }

    /**
     Loop through each column and convert it to a new
     type.
     */
    protected void convertTable() {
      for(int i = 0; i < types.length; i++)
        convertColumn(i);
    }

    protected void convertColumn(int i) {
      String selected = (String) types[i].getSelectedItem();

      try {
        if(selected.equals(DOUBLE_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.DOUBLE)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            double[] buffer = new double[table.getNumRows()];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
		      missing[row] = table.isValueMissing(row, i);
		      if (missing[row])
		      	buffer[row] = table.getMissingDouble();
		      else
                buffer[row] = table.getDouble(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new DoubleColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);
          }
        }
        else if(selected.equals(INT_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.INTEGER)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            int[] buffer = new int[table.getNumRows()];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
			  missing[row] = table.isValueMissing(row, i);
			  if (missing[row])
				buffer[row] = table.getMissingInt();
			  else
				buffer[row] = table.getInt(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new IntColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);
          }

        }
        else if(selected.equals(STRING_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.STRING)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            String[] buffer = new String[table.getNumRows()];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
			  missing[row] = table.isValueMissing(row, i);
			  if (missing[row])
				buffer[row] = table.getMissingString();
			  else
				buffer[row] = table.getString(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new StringColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++) {
               if (missing[j]) {
                   mt.setValueToMissing(true, j, i);
               }
            }

          }
        }
        else if(selected.equals(BYTE_ARRAY_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.BYTE_ARRAY)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            byte[][] buffer = new byte[table.getNumRows()][];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
			  missing[row] = table.isValueMissing(row, i);
			  if (missing[row])
				buffer[row] = table.getMissingString().getBytes();
			  else
				buffer[row] = table.getBytes(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new ByteArrayColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);
          }
        }
        else if(selected.equals(CHAR_ARRAY_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.CHAR_ARRAY)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            char[][] buffer = new char[table.getNumRows()][];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
			  missing[row] = table.isValueMissing(row, i);
			  if (missing[row])
				buffer[row] = table.getMissingString().toCharArray();
			  else
				buffer[row] = table.getChars(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new CharArrayColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j]) {
                  mt.setValueToMissing(true, j, i);
               }

          }
        }
        else if(selected.equals(FLOAT_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.FLOAT)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            float[] buffer = new float[table.getNumRows()];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
			  missing[row] = table.isValueMissing(row, i);
			  if (missing[row])
				buffer[row] = (float)table.getMissingDouble();
			  else
				buffer[row] = table.getFloat(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new FloatColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);
          }
        }
        else if(selected.equals(LONG_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.LONG)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            long[] buffer = new long[table.getNumRows()];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
			  missing[row] = table.isValueMissing(row, i);
			  if (missing[row])
				buffer[row] = table.getMissingInt();
			  else
				buffer[row] = table.getLong(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new LongColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);
          }
        }
        else if(selected.equals(SHORT_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.SHORT)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            short[] buffer = new short[table.getNumRows()];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
			  missing[row] = table.isValueMissing(row, i);
			  if (missing[row])
				buffer[row] = (short) table.getMissingInt();
			  else
				buffer[row] = table.getShort(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new ShortColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);

          }
        }
        else if(selected.equals(BOOLEAN_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.BOOLEAN)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            boolean[] buffer = new boolean[table.getNumRows()];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
			  missing[row] = table.isValueMissing(row, i);
			  if (missing[row])
				buffer[row] = table.getMissingBoolean();
			  else
				buffer[row] = table.getBoolean(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new BooleanColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);
          }
        }
        else if(selected.equals(BYTE_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.BYTE)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            byte[] buffer = new byte[table.getNumRows()];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
			  missing[row] = table.isValueMissing(row, i);
			  if (missing[row])
				buffer[row] = table.getMissingByte();
			  else
				buffer[row] = table.getByte(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new ByteColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);
          }
        }
        else if(selected.equals(CHAR_TYPE)) {
          if(!(table.getColumnType(i) == ColumnTypes.CHAR)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            char[] buffer = new char[table.getNumRows()];
			boolean[] missing = new boolean[table.getNumRows()];
			for (int row = 0 ; row < buffer.length; row++) {
			  missing[row] = table.isValueMissing(row, i);
			  if (missing[row])
				buffer[row] = table.getMissingChar();
			  else
				buffer[row] = table.getChar(row, i);
			}

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(new CharColumn(buffer), i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++) {
               if (missing[j]) {
                  mt.setValueToMissing(true, j, i);
               }
            }

          }
        }
        indices[i] = types[i].getSelectedIndex();
      } catch (Exception exception) {
          types[i].setSelectedIndex(indices[i]); // Change combo box selection to previous index
          ErrorDialog.showDialog("Could not convert column type", "Conversion Error");
      }

    }

    protected void removeRows() {
      int offset = table.getNumRows() - matrix.getJTable().getRowCount();
      int[] rows = matrix.getJTable().getSelectedRows();
      for (int row=0; row < rows.length; row++) {
        if (rows[row] != 0) {
          ((MutableTable) table).removeRow(rows[row]+offset);
          offset--;
        }
      }
      model.fireTableDataChanged();
    }

    // Listen for events from edit menu
    public void actionPerformed(ActionEvent event) {
      Object source = event.getSource();

      if (source == rows)
        removeRows();
      // listen for events from combo boxes
      else if (map.containsKey(source)) {
         Integer integer = (Integer) map.get(source);
         convertColumn(integer.intValue());
         model.fireTableDataChanged();
      }
      else
        super.actionPerformed(event);
    }

    // Listen for events from combo boxes (moved to actionPerformed so it
    // would only fire once)
    /*
    public void itemStateChanged(ItemEvent event) {
      Object source = event.getSource();

      if (map.containsKey(source)) {
        Integer integer = (Integer) map.get(source);
        convertColumn(integer.intValue());
        model.fireTableDataChanged();
      }
    }
    */

    protected void finishUp() {
      convertTable();
      super.finishUp();
    }

  }
}

// Start QA comments
// 4/5/03 - Ruth resumed QA after what Vered started initially.
//          Updated module name and info;   2 problems reported to Greg.
//          1) if you edit a cell and then hit "Done" before clicking on another cell the update
//          of the cell value doesn't occur.    2) If you change a column type to an illegal value
//          you get the Exception box "Could not convert column type" two times.  Haven't
//          yet tested w/ 'other' table types.  Vered had reported the types of conversions
//          allowed weren't consistent across types.
// 4/10/03 - Greg fixed 2.  Said 1 was standard behavior.  Updated info to say "hit Enter".
//         - Ready for Basic
// End QA comments.
// End QA comments.


/**
 * basic 4 QA comments
 * 01-04-04:
 * Vered started qa process
 *
 * bug 196 -  preservation of double values when converting from double
 * to byte[] type and then back to double. all values are being zeroed.
 *
 * Bug 197 - cannot convert from String type to a numeric type when a column
 * holds missing values. same problem is with converting char[] to a numeric type.
*/