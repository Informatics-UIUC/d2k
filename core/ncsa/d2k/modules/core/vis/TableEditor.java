package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import ncsa.gui.ErrorDialog;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.vis.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
 Displays a Table and allows the cells to be changed
 @author David Clutter
 */
public class TableEditor extends TableViewer {

  public String getModuleInfo() {
    String info = "<p>Overview: ";
    info += "This module displays a table and allows the cells to be changed. ";
    info += "The first row of the table contains combo boxes that change the datatype associated with each column. ";
    info += "</p><p>Detailed Description: ";
    info += "This module can change the value of a cell and the datatype of the cells in a column. ";
    info += "If an incongruous value is entered in a cell, a message is displayed and the cell is not changed. ";
    info += "If the datatype is changed then the values of the cells are immediately converted. ";
    info += "If the conversion fails, a message is displayed and the cells are not changed. ";
    info += "The rows of the table can also be removed.";
    info += "</p><p>Data Handling: ";
    info += "The data of the input table is obviously changed. The changed table can also be saved to a file.";
    return info;
  }

  public String getModuleName() {
    return "Edit Table Data";
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
      case 0: return "The MutableTable to edit.";
      default: return "No such input";
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
        return "MutableTable";
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
        return "MutableTable";
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
      case 0: return "The MutableTable with any changes that were made.";
      default: return "No such output";
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
  public class TableEditorView extends TableView implements ItemListener {

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
        model = new TableEditorModel((MutableTable) table, types);
        matrix = new TableMatrix(model);

        TableColumnModel tc = matrix.getJTable().getColumnModel();
        // Set the DefaultCellEditor to be the JComboBox
        for(int i = 0; i < types.length; i++)
          tc.getColumn(i).setCellEditor(new VTCellEditor(types[i]));

        // Add everything to this
        add(matrix, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        //addKeyListener(this);
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
        //return editor.isCellEditable(anEvent);
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
      //Column c;
      types = new JComboBox[table.getNumColumns()];
      indices = new int[table.getNumColumns()];
      for(int i = 0; i < types.length; i++) {
        types[i] = new JComboBox(typeList);
        map.put(types[i], new Integer(i));

        /*
        c = table.getColumn(i);
        if(c instanceof ByteArrayColumn)
        types[i].setSelectedItem(BYTE_ARRAY_TYPE);
        else if(c instanceof DoubleColumn)
        types[i].setSelectedItem(DOUBLE_TYPE);
        else if(c instanceof IntColumn)
        types[i].setSelectedItem(INT_TYPE);
        else if(c instanceof StringColumn)
        types[i].setSelectedItem(STRING_TYPE);
        else if(c instanceof BooleanColumn)
        types[i].setSelectedItem(BOOLEAN_TYPE);
        else if(c instanceof CharArrayColumn)
        types[i].setSelectedItem(CHAR_ARRAY_TYPE);
        else if(c instanceof FloatColumn)
        types[i].setSelectedItem(FLOAT_TYPE);
        else if(c instanceof LongColumn)
        types[i].setSelectedItem(LONG_TYPE);
        else if(c instanceof ShortColumn)
        types[i].setSelectedItem(SHORT_TYPE);
        */

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
        types[i].addItemListener(this);
      }
    }

    /**
     Loop through each column and convert it to a new
     type.
     */
    protected void convertTable() {
      //Column newCol;
      //Column origCol;
      for(int i = 0; i < types.length; i++)
        convertColumn(i);
    }

    protected void convertColumn(int i) {
      String selected = (String) types[i].getSelectedItem();
      //newCol = null;
      //origCol = table.getColumn(i);

      try {
        if(selected.equals(DOUBLE_TYPE)) {
          //	if(!(table.getColumn(i) instanceof DoubleColumn)) {
          //		newCol = ColumnUtilities.toDoubleColumn(origCol);
          //	}
          if(!(table.getColumnType(i) == ColumnTypes.DOUBLE)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            double[] buffer = new double[table.getNumRows()];
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);

          }
        }
        else if(selected.equals(INT_TYPE)) {
          //	if(!(table.getColumn(i) instanceof IntColumn)) {
          //		newCol = ColumnUtilities.toIntColumn(origCol);
          //	}
          if(!(table.getColumnType(i) == ColumnTypes.INTEGER)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            int[] buffer = new int[table.getNumRows()];
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);
          }

        }
        else if(selected.equals(STRING_TYPE)) {
          //	if(!(table.getColumn(i) instanceof StringColumn)) {
          //		newCol = ColumnUtilities.toStringColumn(origCol);
          //	}
          if(!(table.getColumnType(i) == ColumnTypes.STRING)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            String[] buffer = new String[table.getNumRows()];
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++) {
               if (missing[j]) {
                  mt.setString("?", j, i);
                  mt.setValueToMissing(true, j, i);
               }
            }

          }
        }
        else if(selected.equals(BYTE_ARRAY_TYPE)) {
          //	if(!(table.getColumn(i) instanceof ByteArrayColumn)) {
          //		newCol = ColumnUtilities.toByteArrayColumn(origCol);
          //	}
          if(!(table.getColumnType(i) == ColumnTypes.BYTE_ARRAY)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            byte[][] buffer = new byte[table.getNumRows()][];
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);

          }
        }
        else if(selected.equals(CHAR_ARRAY_TYPE)) {
          //	if(!(table.getColumn(i) instanceof CharArrayColumn)) {
          //		newCol = ColumnUtilities.toCharArrayColumn(origCol);
          //	}
          if(!(table.getColumnType(i) == ColumnTypes.CHAR_ARRAY)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            char[][] buffer = new char[table.getNumRows()][];
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j]) {
                  mt.setString("?", j, i);
                  mt.setValueToMissing(true, j, i);
               }

          }
        }
        else if(selected.equals(FLOAT_TYPE)) {
          //	if(!(table.getColumn(i) instanceof FloatColumn)) {
          //		newCol = ColumnUtilities.toFloatColumn(origCol);
          //	}
          if(!(table.getColumnType(i) == ColumnTypes.FLOAT)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            float[] buffer = new float[table.getNumRows()];
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);

          }
        }
        else if(selected.equals(LONG_TYPE)) {
          //	if(!(table.getColumn(i) instanceof LongColumn)) {
          //		newCol = ColumnUtilities.toLongColumn(origCol);
          //	}
          if(!(table.getColumnType(i) == ColumnTypes.LONG)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            long[] buffer = new long[table.getNumRows()];
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);

          }
        }
        else if(selected.equals(SHORT_TYPE)) {
          //	if(!(table.getColumn(i) instanceof ShortColumn)) {
          //		newCol = ColumnUtilities.toShortColumn(origCol);
          //	}
          if(!(table.getColumnType(i) == ColumnTypes.SHORT)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            short[] buffer = new short[table.getNumRows()];
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++)
               if (missing[j])
                  mt.setValueToMissing(true, j, i);

          }
        }
        else if(selected.equals(BOOLEAN_TYPE)) {
          //	if(!(table.getColumn(i) instanceof BooleanColumn)) {
          //		newCol = ColumnUtilities.toBooleanColumn(origCol);
          //	}
          if(!(table.getColumnType(i) == ColumnTypes.BOOLEAN)) {

            String lbl = table.getColumnLabel(i);
            String comm = table.getColumnComment(i);

            // get data
            boolean[] buffer = new boolean[table.getNumRows()];
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
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
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
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
            byte[] buffer = new byte[table.getNumRows()];
            table.getColumn(buffer, i);

            // get missing values
            boolean[] missing = new boolean[table.getNumRows()];
            for (int j = 0; j < missing.length; j++)
               missing[j] = table.isValueMissing(j, i);

            MutableTable mt = (MutableTable)table;

            // set data
            mt.setColumn(buffer, i);
            mt.setColumnLabel(lbl, i);
            mt.setColumnComment(comm, i);

            // set missing values
            for (int j = 0; j < missing.length; j++) {
               if (missing[j]) {
                  mt.setChar('?', j, i);
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

      /*if(newCol != null)
      table.setColumn(newCol, i);
      */
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
      else
        super.actionPerformed(event);
    }

    // Listen for events from combo boxes
    public void itemStateChanged(ItemEvent event) {
      Object source = event.getSource();

      if (map.containsKey(source)) {
        Integer integer = (Integer) map.get(source);
        convertColumn(integer.intValue());
        model.fireTableDataChanged();
      }
    }

    protected void finishUp() {
      convertTable();
      super.finishUp();
    }

    /*public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_DELETE) {
    int[] rows = matrix.getSelectedRows();
    int[] cols = matrix.getSelectedColumns();
    for(int i = 0; i < rows.length; i++)
    System.out.println(rows[i]);
    }
    }
    public void keyReleased(KeyEvent e) {}
    */
  }
}