package ncsa.d2k.modules.core.vis;

import ncsa.d2k.modules.core.vis.widgets.TableEditorModel;
import ncsa.d2k.modules.core.vis.widgets.VerticalTableMatrix;

import ncsa.d2k.infrastructure.views.UserView;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.util.ErrorDialog;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.EventObject;

/**
   Displays a VerticalTable and allows editing of the cells.
   TODO: allow the columns and rows to be moved and deleted.
   @author David Clutter
*/
public class VTEditor extends VTViewer {

	public String getModuleInfo() {
		StringBuffer b = new StringBuffer("A table editor.  This displays");
		b.append(" the contents of a VerticalTable and allows the editing");
		b.append(" of the table.  The first row contains combo boxes to ");
		b.append(" change the datatype of the associated column.");
		return b.toString();
	}

	public String getModuleName() {
		return "TableEditor";
	}

    /**
       Override the superclass' implementation.  Return a TableEditorView
       instead of a VerticalTableView.
       @return The UserView that we display
    */
    public UserView createUserView() {
		return new TableEditorView();
    }

	static final String STRING_TYPE = "String";
	static final String DOUBLE_TYPE = "double";
	static final String INT_TYPE = "int";
	static final String BYTE_ARRAY_TYPE = "byte[]";
	static final String CHAR_ARRAY_TYPE = "char[]";
	static final String BOOLEAN_TYPE = "boolean";
	static final String FLOAT_TYPE = "float";
	static final String LONG_TYPE = "long";
	static final String SHORT_TYPE = "short";

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
				table = (VerticalTable)input;
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
				matrix = new VerticalTableMatrix(
					new TableEditorModel(table, types));

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
			Column c;
			types = new JComboBox[table.getNumColumns()];
			for(int i = 0; i < types.length; i++) {
				types[i] = new JComboBox(typeList);

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
	    	}
		}

		/**
			Loop through each column and convert it to a new
			type.
		*/
		protected void convertTable() {
			Column newCol;
			SimpleColumn origCol;

			for(int i = 0; i < types.length; i++) {
				String selected = (String)types[i].getSelectedItem();
				newCol = null;
				origCol = (SimpleColumn)table.getColumn(i);

				if(selected.equals(DOUBLE_TYPE)) {
					if(!(table.getColumn(i) instanceof DoubleColumn)) {
						newCol = ColumnUtilities.toDoubleColumn(origCol);
					}
				}
				if(selected.equals(INT_TYPE)) {
					if(!(table.getColumn(i) instanceof IntColumn)) {
						newCol = ColumnUtilities.toIntColumn(origCol);
					}
				}
				if(selected.equals(STRING_TYPE)) {
					if(!(table.getColumn(i) instanceof StringColumn)) {
 						newCol = ColumnUtilities.toStringColumn(origCol);
					}
				}
				if(selected.equals(BYTE_ARRAY_TYPE)) {
					if(!(table.getColumn(i) instanceof ByteArrayColumn)) {
						newCol = ColumnUtilities.toByteArrayColumn(origCol);
					}
				}
				if(selected.equals(CHAR_ARRAY_TYPE)) {
					if(!(table.getColumn(i) instanceof CharArrayColumn)) {
						newCol = ColumnUtilities.toCharArrayColumn(origCol);
					}
				}
				if(selected.equals(FLOAT_TYPE)) {
					if(!(table.getColumn(i) instanceof FloatColumn)) {
						newCol = ColumnUtilities.toFloatColumn(origCol);
					}
				}
				if(selected.equals(LONG_TYPE)) {
					if(!(table.getColumn(i) instanceof LongColumn)) {
						newCol = ColumnUtilities.toLongColumn(origCol);
					}
				}
				if(selected.equals(SHORT_TYPE)) {
					if(!(table.getColumn(i) instanceof ShortColumn)) {
						newCol = ColumnUtilities.toShortColumn(origCol);
					}
				}
				if(selected.equals(BOOLEAN_TYPE)) {
					if(!(table.getColumn(i) instanceof BooleanColumn)) {
						newCol = ColumnUtilities.toBooleanColumn(origCol);
					}
				}
				if(newCol != null)
					table.setColumn(newCol, i);
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
