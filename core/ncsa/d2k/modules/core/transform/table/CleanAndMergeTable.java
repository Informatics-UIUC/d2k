package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.swing.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;
/**
 * Clean the data in a Table.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class CleanAndMergeTable extends UIModule {

	public String getInputInfo(int i) {
		return "A Table to clean.";
	}

	public String getOutputInfo(int i) {
		if(i == 0)
			return "The original Table.";
		return "The cleaned and merged Table.";
	}

	public String [] getInputTypes() {
		String [] in = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return in;
	}

	public String [] getOutputTypes() {
		String [] out = {"ncsa.d2k.modules.core.datatype.table.Table",
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return out;
	}

	public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("Cleans a Table by merging duplicate records.  ");
		sb.append("The Key is the column that uniquely identifies rows.  This can be a combination ");
		sb.append("of columns.  The To Merge columns are the columns to merge together.  ");
		sb.append("The rows of these columns are merged into one row by taking the maximum value, ");
		sb.append("averaging the values, or summing the values together.  The Control is the ");
		sb.append("column that determines which record is the basis for the new row.  All ");
		sb.append("data that is not merged is simply copied from the record with the maximum value ");
		sb.append("in the Control column.");
		return sb.toString();
	}

	protected UserView createUserView() {
		return new CleanView();
	}

	public String [] getFieldNameMapping() {
		return null;
	}

	private static final String SUM = "Sum";
	private static final String AVE = "Average";
	private static final String MAX = "Max";

	private class CleanView extends JUserPane {
		JList keyAttributeList;
		DefaultListModel keyListModel;
		JList attributesToMerge;
		DefaultListModel mergeListModel;
		JList controlAttribute;
		DefaultListModel controlListModel;

		JComboBox mergeMethod;

		Table table;

		HashMap columnLookup;

		public void setInput(Object o, int id) {
			table = (Table)o;

			// clear all lists
			keyListModel.removeAllElements();
			mergeListModel.removeAllElements();
			controlListModel.removeAllElements();

			columnLookup = new HashMap(table.getNumColumns());
			String longest = "";

			// now add the column lables to keyListModel and controlListModel
			for(int i = 0; i < table.getNumColumns(); i++) {
				columnLookup.put(table.getColumnLabel(i), new Integer(i));
				keyListModel.addElement(table.getColumnLabel(i));
				if(table.getColumn(i) instanceof NumericColumn) {
					mergeListModel.addElement(table.getColumnLabel(i));
					controlListModel.addElement(table.getColumnLabel(i));
				}
				if(table.getColumnLabel(i).length() > longest.length())
					longest = table.getColumnLabel(i);
			}
			keyAttributeList.setPrototypeCellValue(longest);
			attributesToMerge.setPrototypeCellValue(longest);
			controlAttribute.setPrototypeCellValue(longest);
		}

		public void initView(ViewModule m) {
			keyAttributeList = new JList();
			keyListModel = new DefaultListModel();
			keyAttributeList.setModel(keyListModel);
			attributesToMerge = new JList();
			mergeListModel = new DefaultListModel();
			attributesToMerge.setModel(mergeListModel);
			controlAttribute = new JList();
			controlListModel = new DefaultListModel();
			controlAttribute.setModel(controlListModel);
			controlAttribute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			JScrollPane jsp1 = new JScrollPane(keyAttributeList);
			jsp1.setColumnHeaderView(new JLabel("Key"));
			JScrollPane jsp2 = new JScrollPane(attributesToMerge);
			jsp2.setColumnHeaderView(new JLabel("To Merge"));
			JScrollPane jsp3 = new JScrollPane(controlAttribute);
			jsp3.setColumnHeaderView(new JLabel("Control"));

			String [] methods = {SUM, AVE, MAX};
			mergeMethod = new JComboBox(methods);
			JPanel pnl = new JPanel();
			pnl.add(new JLabel("Merge Method"));
			JPanel pn2 = new JPanel();
			pn2.add(mergeMethod);

			Box b1 = new Box(BoxLayout.Y_AXIS);
			b1.add(jsp3);
			b1.add(pnl);
			b1.add(pn2);

			Box b2 = new Box(BoxLayout.X_AXIS);
			b2.add(jsp1);
			b2.add(jsp2);
			b2.add(b1);

			setLayout(new BorderLayout());
			add(b2, BorderLayout.CENTER);

			JPanel buttonPanel = new JPanel();
			JButton abort = new JButton("Abort");
			abort.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					viewCancel();
				}
			});
			JButton done = new JButton("Done");
			done.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					Object [] keys = keyAttributeList.getSelectedValues();
					Object [] merges = attributesToMerge.getSelectedValues();
					Object control = controlAttribute.getSelectedValue();
					final Object type = mergeMethod.getSelectedItem();

					final int [] ks = new int[keys.length];
					for(int i = 0; i < keys.length; i++)
						ks[i] = ((Integer)columnLookup.get(keys[i])).intValue();

					final int [] ms = new int[merges.length];
					for(int i = 0; i < merges.length; i++)
						ms[i] = ((Integer)columnLookup.get(merges[i])).intValue();

					final int ctrl = ((Integer)columnLookup.get(control)).intValue();

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							cleanTable(ks, ms, ctrl, (String)type);
						}
					});
				}
			});
			buttonPanel.add(abort);
			buttonPanel.add(done);
			add(buttonPanel, BorderLayout.SOUTH);
		}

		private void cleanTable(int[] keys, int[] merges, int control, String type) {
			HashMap keyLookup = new HashMap(20);
			// loop through table to find rows where all the key columns are identical
			for(int i = 0; i < table.getNumRows(); i++) {
				// get the keys for this row
				String [] kys = new String[keys.length];
				for(int j = 0; j < kys.length; j++)
					kys[j] = table.getString(i, keys[j]);
				KeySet set = new KeySet(kys);
				if(!keyLookup.containsKey(set)) {
					ArrayList list = new ArrayList();
					list.add(new Integer(i));
					keyLookup.put(set, list);
				}
				else {
					ArrayList list = (ArrayList)keyLookup.get(set);
					list.add(new Integer(i));
					// necessary?
					keyLookup.put(set, list);
				}
			}

			// create the table
			Table newTable = createTable(keyLookup.size());

			int curRow = 0;
			// now convert the array lists to int[]
			Iterator iter = keyLookup.keySet().iterator();
			while(iter.hasNext()) {
				Object key = iter.next();
				ArrayList list = (ArrayList)keyLookup.get(key);
				int [] array = new int[list.size()];
				for(int q = 0; q < list.size(); q++)
					array[q] = ((Integer)list.get(q)).intValue();
				//now go ahead and do the merging..
				if(type.equals(MAX))
					mergeMax(newTable, curRow, keys, merges, control, array);
				else if(type.equals(AVE))
					mergeAve(newTable, curRow, keys, merges, control, array);
				else if(type.equals(SUM))
					mergeSum(newTable, curRow, keys, merges, control, array);
				curRow++;
			}

			// the number of rows of the cleaned table is equal to the number of unique keys..
			pushOutput(table, 0);
			pushOutput(newTable, 1);
			viewDone("Done");
		}

		private void mergeMax(Table tbl, int rowLoc, int[] keys, int [] mergeCols, int control, int [] rows) {
			// find the maximum in the control column.  this row will be the one
			// where data is copied from

			int maxRow = rows[0];
			double maxVal = Double.MIN_VALUE;
			for(int i = 1; i < rows.length; i++) {
				if(table.getDouble(rows[i], control) > maxVal) {
					maxVal = table.getDouble(rows[i], control);
					maxRow = rows[i];
				}
			}

			// copy all the row data in
			for(int i = 0; i < tbl.getNumColumns(); i++) {
				Column c = tbl.getColumn(i);
				if(c instanceof NumericColumn)
					tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
				else
					tbl.setString(table.getString(maxRow, i), rowLoc, i);
			}
		}

		private void mergeAve(Table tbl, int rowLoc, int[] keys, int [] mergeCols, int control, int [] rows) {
			// find the maximum in the control column.  this row will be the one
			// where data is copied from

			int maxRow = rows[0];
			double maxVal = Double.MIN_VALUE;
			for(int i = 1; i < rows.length; i++) {
				if(table.getDouble(rows[i], control) > maxVal) {
					maxVal = table.getDouble(rows[i], control);
					maxRow = rows[i];
				}
			}

			// copy all the row data in
			for(int i = 0; i < tbl.getNumColumns(); i++) {
				Column c = tbl.getColumn(i);
				if(c instanceof NumericColumn)
					tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
				else
					tbl.setString(table.getString(maxRow, i), rowLoc, i);
			}

			// now find the averages
			// now find the sums
			for(int i = 0; i < mergeCols.length; i++) {
				double sums = 0;
				for(int j = 0; j < rows.length; j++) {
					sums += table.getDouble(rows[j], mergeCols[i]);
				}
				tbl.setDouble(sums/(double)rows.length, rowLoc, mergeCols[i]);
			}
		}

		private void mergeSum(Table tbl, int rowLoc, int[] keys, int [] mergeCols, int control, int [] rows) {
			// find the maximum in the control column.  this row will be the one
			// where data is copied from

			int maxRow = rows[0];
			double maxVal = Double.MIN_VALUE;
			for(int i = 1; i < rows.length; i++) {
				if(table.getDouble(rows[i], control) > maxVal) {
					maxVal = table.getDouble(rows[i], control);
					maxRow = rows[i];
				}
			}

			// copy all the row data in
			for(int i = 0; i < tbl.getNumColumns(); i++) {
				Column c = tbl.getColumn(i);
				if(c instanceof NumericColumn)
					tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
				else
					tbl.setString(table.getString(maxRow, i), rowLoc, i);
			}

			// now find the sums
			for(int i = 0; i < mergeCols.length; i++) {
				double sums = 0;
				for(int j = 0; j < rows.length; j++) {
					sums += table.getDouble(rows[j], mergeCols[i]);
				}
				tbl.setDouble(sums, rowLoc, mergeCols[i]);
			}
		}

		private Table createTable(int numRows) {
			Column[] cols = new Column[table.getNumColumns()];
			for(int i = 0; i < table.getNumColumns(); i++) {
				Column c = table.getColumn(i);
				Column newCol = null;
				if(c instanceof IntColumn)
					newCol = new IntColumn(numRows);
				else if(c instanceof StringColumn)
					newCol = new StringColumn(numRows);
				else if(c instanceof FloatColumn)
					newCol = new FloatColumn(numRows);
				else if(c instanceof LongColumn)
					newCol = new LongColumn(numRows);
				else if(c instanceof DoubleColumn)
					newCol = new DoubleColumn(numRows);
				else if(c instanceof BooleanColumn)
					newCol = new BooleanColumn(numRows);
				else if(c instanceof ContinuousCharArrayColumn)
					newCol = new ContinuousCharArrayColumn(numRows);
				else if(c instanceof ContinuousByteArrayColumn)
					newCol = new ContinuousByteArrayColumn(numRows);
				else if(c instanceof ShortColumn)
					newCol = new ShortColumn(numRows);
				else
					newCol = new StringColumn(numRows);
				newCol.setLabel(c.getLabel());
				cols[i] = newCol;
			}

			Table tbl = TableFactory.createTable(cols);
			tbl.setLabel(table.getLabel());
			return tbl;
		}

		private class KeySet {
			String[] keys;

			KeySet(String[] k) {
				keys = k;
			}

			public boolean equals(Object o) {
				KeySet other = (KeySet)o;
				String [] otherkeys = other.keys;

				if(otherkeys.length != keys.length)
					return false;

				for(int i = 0; i < keys.length; i++)
					if(!keys[i].equals(otherkeys[i]))
						return false;
				return true;
			}

			public String toString() {
				StringBuffer sb = new StringBuffer();
				for(int i = 0; i < keys.length; i++) {
					sb.append(keys[i]);
					sb.append(" ");
				}
				return sb.toString();
			}

			public int hashCode() {
				int result = 37;
				for(int i = 0; i < keys.length; i++)
					result *= keys[i].hashCode();
				return result;
			}
		}

		public Dimension getPreferredSize() {
			return new Dimension(400, 300);
		}
	}
}