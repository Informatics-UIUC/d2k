package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.gui.*;

/**
 * Merge rows in a table based on identical key attributes.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class MergeTableRows extends UIModule {

       ///////
       // variables and methods used to preserve settings between invocations
       ///////

	private String lastControl;
	public String getLastControl() {
		return lastControl;
	}
	public void setLastControl(String s) {
		lastControl = s;
	}

	private HashSet lastKeys;
	public HashSet getLastKeys() {
		return lastKeys;
	}
	public void setLastKeys(HashSet s) {
		lastKeys = s;
	}

	private HashSet lastToMerge;
	public HashSet getLastToMerge() {
		return lastToMerge;
	}
	public void setLastToMerge(HashSet s) {
		lastToMerge = s;
	}

	private String lastMergeMethod;
	public String getLastMergeMethod() {
		return lastMergeMethod;
	}
	public void setLastMergeMethod(String s) {
		lastMergeMethod = s;
	}

        ///////
        // standard info-related methods
        ///////

	/**
	 * Return a list of the property descriptions that a user may edit.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [0];
		return pds;
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Merge Table Rows";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Input Table";
			default:
				return "No such input.";
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
				return "Output Table";
			default:
				return "No such output.";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The table to be processed by the row merge operation.";
			default: return "No such input.";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The table that results from the row merge operation.";
			default: return "No such output.";
		}
	}

	public String [] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	public String [] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	public String getModuleInfo() {

      	String s = "<p>Overview: ";
      	  s += "This module merges rows in a table that have the same values for one or more key attributes.  ";
          s += "The user selects the key attribute(s) and specifies other information about how the rows should be merged. ";

      	  s += "</p><p>Detailed Description: ";
      	  s += "This module merges multiple rows from the <i>Input Table</i> into a single row in the <i>Output Table</i>. ";
          s += "Rows are merged if they have identical values for one or more key attributes. ";
          s += "A set of rows from the <i>Input Table</i> that has identical values for the key attributes are called ";
          s += "<i>matching rows</i>.   One output row is produced for each set of matching rows. ";
          s += "The module presents a dialog that allows selection of the key attribute(s) and control over additional ";
      	  s += "merge parameters. ";

      	  s += "</p><p>";
      	  s += "The module dialog lists all of the attributes in the <i>Input Table</i> and allows the user to select one or more ";
          s += "of them to be the <i>Key</i> for the merge. ";
          s += "The module merges table rows with identical values for all of the specified Key attributes. ";
          s += "The module dialog also lists all of the numeric attributes in the <i>Input Table</i> and allows the user to select ";
          s += "one of these as the <i>Control</i> attribute. ";
          s += "The Control determines which row in each matching row set will be used as the basis for the resulting merged row. ";
          s += "For a set of matching rows, the row with the maximum value for the Control attribute is the control row. ";

          s += "</p><p>";
          s += "The module dialog also lists the numeric attributes under the <i>Merge</i> heading, and allows the user to select ";
          s += "one or more of these attributes to be merged across matching rows using the operation specified via the ";
          s += "dialog's <i>Merge Method</i>. ";
          s += "The possible merge methods are <i>Sum</i>, <i>Average</i>, <i>Maximum</i>, and <i>Minimum</i>. ";
          s += "For each of the Merge attributes selected, the merge method will be applied to the attribute values of all ";
          s += "matching rows in a set and the result will appear in the output merged row. ";

          s += "</p><p>";
          s += "Each row in the <i>Output Table</i> will have the values of the control row attributes for all string attributes and ";
          s += "for the numeric attributes that were not selected as Merge attributes.    That is to say, all data that is not ";
          s += "merged using the merge method is simply copied from the control row for each set of matching rows. ";

          s += "</p><p>Data Type Restrictions: ";
          s += "The <i>Input Table</i> must contain at least one numeric attribute that can be used as the <i>Control</i>. ";
          s += "In addition, the Merge Method can only be applied to numeric attributes. ";

          s += "</p><p>Data Handling: ";
          s += "The <i>Input Table</i> is not modified.   The <i>Output Table</i> is created by the module. ";

	  s += "</p><p>Scalability: ";
          s += "This module should scale very well for tables where the key attribute has a limited number of unique values. When ";
          s += "that is not the case, in other words, if the key attribute selected is not nominal, the module will not scale ";
          s += "well.</p>";

          return s;
	}

        //////
        // the meat
        /////

	protected UserView createUserView() {
		return new CleanView();
	}

	public String [] getFieldNameMapping() {
		return null;
	}

	private static final String SUM = "Sum";
	private static final String AVE = "Average";
	private static final String MAX = "Max";
	private static final String MIN = "Min";

	private class CleanView extends JUserPane {
		JList keyAttributeList;
		DefaultListModel keyListModel;
		JList controlAttribute;
		DefaultListModel controlListModel;
		JList attributesToMerge;
		DefaultListModel mergeListModel;

		JComboBox mergeMethod;

		TableImpl table;

		HashMap columnLookup;

		public void setInput (Object o, int id) throws Exception {
			table = (TableImpl)o;

			// clear all lists
			keyListModel.removeAllElements();
			controlListModel.removeAllElements();
			mergeListModel.removeAllElements();

			columnLookup = new HashMap(table.getNumColumns());
			String longest = "";

			HashSet selectedKeys = new HashSet();
			HashSet selectedControls = new HashSet();
			HashSet selectedMerges = new HashSet();

			// now add the column labels
			// keyListModel entries can be string or numeric type columns
			// controlListModel and mergeListModel entries must be numeric type columns
			int ni = 0;		// index for numeric type selections
			for(int i = 0; i < table.getNumColumns(); i++) {
				columnLookup.put(table.getColumnLabel(i), new Integer(i));

				keyListModel.addElement(table.getColumnLabel(i));
				if(lastKeys != null && lastKeys.contains(table.getColumnLabel(i))) {
				   selectedKeys.add(new Integer(i));
                                }

				if(table.getColumn(i) instanceof NumericColumn) {
					controlListModel.addElement(table.getColumnLabel(i));
					if(lastControl != null && lastControl.equals(table.getColumnLabel(i))) {
						selectedControls.add(new Integer(ni));
					}

					mergeListModel.addElement(table.getColumnLabel(i));
					if(lastToMerge != null && lastToMerge.contains(table.getColumnLabel(i))) {
						selectedMerges.add(new Integer(ni));
   					}

					ni++;
				}
				if(table.getColumnLabel(i).length() > longest.length())
					longest = table.getColumnLabel(i);

			}

                        // Don't force user to Abort if table data is wrong - abort for them with message.
                        if (controlListModel.size() == 0 ) {
                            throw new Exception( getAlias() +
                               ": Input Table does not contain any numeric attributes - itinerary will be aborted" );
                        }

			keyAttributeList.setPrototypeCellValue(longest);
			controlAttribute.setPrototypeCellValue(longest);
			attributesToMerge.setPrototypeCellValue(longest);

			int[] selKeys = new int[selectedKeys.size()];
			int idx = 0;
			Iterator iter = selectedKeys.iterator();
			while(iter.hasNext()) {
				Integer num = (Integer)iter.next();
				selKeys[idx] = num.intValue();
				idx++;
			}

			int[] selControls = new int[selectedControls.size()];
			idx = 0;
			iter = selectedControls.iterator();
			while(iter.hasNext()) {
				Integer num = (Integer)iter.next();
				selControls[idx] = num.intValue();
				idx++;
			}

			int[] selMerge = new int[selectedMerges.size()];
			idx = 0;
			iter = selectedMerges.iterator();
			while(iter.hasNext()) {
				Integer num = (Integer)iter.next();
				selMerge[idx] = num.intValue();
				idx++;
			}

			keyAttributeList.setSelectedIndices(selKeys);
                        controlAttribute.setSelectedIndices(selControls);
			attributesToMerge.setSelectedIndices(selMerge);
			mergeMethod.setSelectedItem(lastMergeMethod);
                }

		public void initView(ViewModule m) {
			keyAttributeList = new JList();
			keyListModel = new DefaultListModel();
			keyAttributeList.setModel(keyListModel);
			controlAttribute = new JList();
			controlListModel = new DefaultListModel();
			controlAttribute.setModel(controlListModel);
                        controlAttribute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			attributesToMerge = new JList();
			mergeListModel = new DefaultListModel();
			attributesToMerge.setModel(mergeListModel);



			JScrollPane jsp1 = new JScrollPane(keyAttributeList);
			jsp1.setColumnHeaderView(new JLabel("Key"));
			JScrollPane jsp2 = new JScrollPane(controlAttribute);
			jsp2.setColumnHeaderView(new JLabel("Control"));
			JScrollPane jsp3 = new JScrollPane(attributesToMerge);
			jsp3.setColumnHeaderView(new JLabel("To Merge"));

			String [] methods = {SUM, AVE, MAX, MIN};
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
					Object control = controlAttribute.getSelectedValue();
					Object [] merges = attributesToMerge.getSelectedValues();
					final Object type = mergeMethod.getSelectedItem();

					if(keys == null || keys.length == 0) {
						ErrorDialog.showDialog(
							"You must select a key attribute.", "Error");
						return;
					}
					if(control == null) {
						ErrorDialog.showDialog(
							"You must select a control attribute.", "Error");
						return;
					}
					if(merges == null || merges.length == 0) {
						ErrorDialog.showDialog(
							"You must select one or more attributes to merge.", "Error");
						return;
					}
					if(type == null) {
						ErrorDialog.showDialog(
							"You must select a method to merge by.", "Error");
						return;
					}

					final int [] ks = new int[keys.length];
					for(int i = 0; i < keys.length; i++)
						ks[i] = ((Integer)columnLookup.get(keys[i])).intValue();

					final int [] ms = new int[merges.length];
					for(int i = 0; i < merges.length; i++)
						ms[i] = ((Integer)columnLookup.get(merges[i])).intValue();

					final int ctrl = ((Integer)columnLookup.get(control)).intValue();

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							mergeTable(ks, ms, ctrl, (String)type);
						}
					});
					HashSet usedKeys = new HashSet();
					for(int i = 0; i < keys.length; i++)
						usedKeys.add(keys[i]);

					HashSet usedMerges = new HashSet();
					for(int i = 0; i < merges.length; i++)
						usedMerges.add(merges[i]);

					setLastControl(control.toString());
					setLastKeys(usedKeys);
					setLastToMerge(usedMerges);
					setLastMergeMethod(type.toString());
				}
			});
			buttonPanel.add(abort);
			buttonPanel.add(done);
			add(buttonPanel, BorderLayout.SOUTH);
		}

		private void mergeTable(int[] keys, int[] merges, int control, String type) {
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
			TableImpl newTable = createTable(keyLookup.size());

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
				else if(type.equals(MIN))
					mergeMin(newTable, curRow, keys, merges, control, array);
				else if(type.equals(AVE))
					mergeAve(newTable, curRow, keys, merges, control, array);
				else if(type.equals(SUM))
					mergeSum(newTable, curRow, keys, merges, control, array);
				curRow++;
			}

			// the number of rows of the cleaned table is equal to the number of unique keys..
			pushOutput(newTable, 0);
			viewDone("Done");
		}

		private void mergeMax(TableImpl tbl, int rowLoc, int[] keys, int [] mergeCols, int control, int [] rows) {
			// find the maximum in the control column.  this row will be the one
			// where data is copied from

			int maxRow = rows[0];
			double maxVal = table.getDouble(rows[0], control);
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

		private void mergeMin(TableImpl tbl, int rowLoc, int[] keys, int [] mergeCols, int control, int [] rows) {
			// find the maximum in the control column.  this row will be the one
			// where data is copied from

			int maxRow = rows[0];
			double maxVal = table.getDouble(rows[0], control);
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

			// now find the minimum
			for(int i = 0; i < mergeCols.length; i++) {
				double minimum = 0;
				for(int j = 0; j < rows.length; j++) {
         			    if ( j == 0 ) {
					minimum = table.getDouble(rows[j], mergeCols[i]);
                                    } else {
					double testVal = table.getDouble(rows[j], mergeCols[i]);
					if ( testVal < minimum ) {
					    minimum = testVal;
					}
				    }
				}
				tbl.setDouble(minimum, rowLoc, mergeCols[i]);
			}
		}

		private void mergeAve(TableImpl tbl, int rowLoc, int[] keys, int [] mergeCols, int control, int [] rows) {
			// find the maximum in the control column.  this row will be the one
			// where data is copied from

			int maxRow = rows[0];
			double maxVal = table.getDouble(rows[0], control);
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
			for(int i = 0; i < mergeCols.length; i++) {
				double sums = 0;
				for(int j = 0; j < rows.length; j++) {
					sums += table.getDouble(rows[j], mergeCols[i]);
				}
				tbl.setDouble(sums/(double)rows.length, rowLoc, mergeCols[i]);
			}
		}

		private void mergeSum(TableImpl tbl, int rowLoc, int[] keys, int [] mergeCols, int control, int [] rows) {
			// find the maximum in the control column.  this row will be the one
			// where data is copied from

			int maxRow = rows[0];
			double maxVal = table.getDouble(rows[0], control);
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

		private TableImpl createTable(int numRows) {
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

			TableImpl tbl = (TableImpl)DefaultTableFactory.getInstance().createTable(cols);
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


// QA Comments
// 2/24/03 - Handed off to QA by Loretta - replaces CleanAndMergeTable.
// 2/25/03 - Ruth started QA process.
//           Updated documentation; Removed OutputPort that just copied thru
//           InputTable; Reordered Control and Merge columns in UI so that
//           Merge above MergeMethod;  Added Mininum MergeMethod option;
// 2/27/03 - Want to raise Exception if InputTable doesn't have numeric
//           attributes but not possible in UI module.  David C is looking into
//           fix.   Committing current version (w/ debug stments) to CVS core.
// 3/5/03  - A6 allows Exeception in setValue;  Commmited to Basic
//           WISH:  After discussion with Tom this should be reworked at
//           some point to use Tables, not TableImpls.
// END QA Comments
