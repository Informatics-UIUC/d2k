package ncsa.d2k.modules.core.vis;

import ncsa.d2k.modules.core.vis.widgets.TableEditorModel;
import ncsa.d2k.modules.core.vis.widgets.TableMatrix;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.infrastructure.views.UserView;
import ncsa.d2k.util.ErrorDialog;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.EventObject;

/**
   Displays a Table and allows editing of the cells.
   TODO: allow the columns and rows to be moved and deleted.
   @author David Clutter
*/
public class TableEditor extends TableViewer {

	public String getModuleInfo() {
		StringBuffer b = new StringBuffer("A table editor.  This displays");
		b.append(" the contents of a Table and allows the editing");
		b.append(" of the table.  The first row contains combo boxes to ");
		b.append(" change the datatype of the associated column.");
		return b.toString();
	}

	public String getModuleName() {
		return "TableEditor";
	}

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return in;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		if(i == 0)
	    	return "The MutableTable to edit.";
		else
	    	return "No such input!";
    }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return out;
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		if(i == 0) {
	    	StringBuffer b = new StringBuffer("The Table, with any changes ");
			b.append("that were made.");
	    	return b.toString();
		}
		else
	    	return "No such output!";
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
    public class TableEditorView extends TableView {

		protected JComboBox []types;

		String []typeList = {STRING_TYPE, DOUBLE_TYPE, INT_TYPE,
			     BYTE_ARRAY_TYPE, CHAR_ARRAY_TYPE,
			     BOOLEAN_TYPE, FLOAT_TYPE, LONG_TYPE,
			     SHORT_TYPE};

		public void setInput(Object input, int idx) {
			if(idx == 0) {
				removeAll();
				table = (MutableTable)input;
				// a panel to put the buttons on
				JPanel buttonPanel = new JPanel();
				ok = new JButton("Done");
				ok.addActionListener(this);
				cancel = new JButton("Abort");
				cancel.addActionListener(this);
				buttonPanel.add(cancel);
				buttonPanel.add(ok);
				setupTypes();

				// create the matrix
				matrix = new TableMatrix(
					new TableEditorModel((MutableTable)table, types));

				TableColumnModel tc = matrix.getJTable().getColumnModel();
				// set the DefaultCellEditor to be the JComboBox
				for(int i = 0; i < types.length; i++) {
					tc.getColumn(i).setCellEditor(new VTCellEditor(types[i]));
				}

				// add everything to this
				add(matrix, BorderLayout.CENTER);
				add(buttonPanel, BorderLayout.SOUTH);
				//addKeyListener(this);
			}
		}

		/**
			Uses a Combo box on the first row and a text field for
			any other row as the cell editor.
		*/
		class VTCellEditor implements TableCellEditor {

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
				else
					editor = defaultEditor;
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
		}

		/**
			Setup the types column.
		*/
		protected void setupTypes() {
			//Column c;
			types = new JComboBox[table.getNumColumns()];
			for(int i = 0; i < types.length; i++) {
				types[i] = new JComboBox(typeList);

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
	    	}
		}

		/**
			Loop through each column and convert it to a new
			type.
		*/
		protected void convertTable() {
			//Column newCol;
			//Column origCol;

			for(int i = 0; i < types.length; i++) {
				String selected = (String)types[i].getSelectedItem();
				//newCol = null;
				//origCol = table.getColumn(i);


				if(selected.equals(DOUBLE_TYPE)) {
				//	if(!(table.getColumn(i) instanceof DoubleColumn)) {
				//		newCol = ColumnUtilities.toDoubleColumn(origCol);
				//	}
					if(!(table.getColumnType(i) == ColumnTypes.DOUBLE)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						double[] buffer = new double[table.getNumRows()];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				else if(selected.equals(INT_TYPE)) {
				//	if(!(table.getColumn(i) instanceof IntColumn)) {
				//		newCol = ColumnUtilities.toIntColumn(origCol);
				//	}
					if(!(table.getColumnType(i) == ColumnTypes.INTEGER)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						int[] buffer = new int[table.getNumRows()];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				else if(selected.equals(STRING_TYPE)) {
				//	if(!(table.getColumn(i) instanceof StringColumn)) {
 				//		newCol = ColumnUtilities.toStringColumn(origCol);
				//	}
					if(!(table.getColumnType(i) == ColumnTypes.STRING)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						String[] buffer = new String[table.getNumRows()];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				else if(selected.equals(BYTE_ARRAY_TYPE)) {
				//	if(!(table.getColumn(i) instanceof ByteArrayColumn)) {
				//		newCol = ColumnUtilities.toByteArrayColumn(origCol);
				//	}
					if(!(table.getColumnType(i) == ColumnTypes.BYTE_ARRAY)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						byte[][] buffer = new byte[table.getNumRows()][];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				else if(selected.equals(CHAR_ARRAY_TYPE)) {
				//	if(!(table.getColumn(i) instanceof CharArrayColumn)) {
				//		newCol = ColumnUtilities.toCharArrayColumn(origCol);
				//	}
					if(!(table.getColumnType(i) == ColumnTypes.CHAR_ARRAY)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						char[][] buffer = new char[table.getNumRows()][];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				else if(selected.equals(FLOAT_TYPE)) {
				//	if(!(table.getColumn(i) instanceof FloatColumn)) {
				//		newCol = ColumnUtilities.toFloatColumn(origCol);
				//	}
					if(!(table.getColumnType(i) == ColumnTypes.FLOAT)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						float[] buffer = new float[table.getNumRows()];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				else if(selected.equals(LONG_TYPE)) {
				//	if(!(table.getColumn(i) instanceof LongColumn)) {
				//		newCol = ColumnUtilities.toLongColumn(origCol);
				//	}
					if(!(table.getColumnType(i) == ColumnTypes.LONG)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						long[] buffer = new long[table.getNumRows()];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				else if(selected.equals(SHORT_TYPE)) {
				//	if(!(table.getColumn(i) instanceof ShortColumn)) {
				//		newCol = ColumnUtilities.toShortColumn(origCol);
				//	}
					if(!(table.getColumnType(i) == ColumnTypes.SHORT)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						short[] buffer = new short[table.getNumRows()];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				else if(selected.equals(BOOLEAN_TYPE)) {
				//	if(!(table.getColumn(i) instanceof BooleanColumn)) {
				//		newCol = ColumnUtilities.toBooleanColumn(origCol);
				//	}
					if(!(table.getColumnType(i) == ColumnTypes.BOOLEAN)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						boolean[] buffer = new boolean[table.getNumRows()];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				else if(selected.equals(BYTE_TYPE)) {
					if(!(table.getColumnType(i) == ColumnTypes.BYTE)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						byte[] buffer = new byte[table.getNumRows()];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				else if(selected.equals(CHAR_TYPE)) {
					if(!(table.getColumnType(i) == ColumnTypes.CHAR)) {
						String lbl = table.getColumnLabel(i);
						String comm = table.getColumnComment(i);
						byte[] buffer = new byte[table.getNumRows()];
						table.getColumn(buffer, i);
						((MutableTable)table).setColumn(buffer, i);
						((MutableTable)table).setColumnLabel(lbl, i);
						((MutableTable)table).setColumnComment(comm, i);
					}
				}
				/*if(newCol != null)
					table.setColumn(newCol, i);
				*/
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
