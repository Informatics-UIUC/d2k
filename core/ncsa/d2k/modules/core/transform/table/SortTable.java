package ncsa.d2k.modules.core.transform.table;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.*;

/**
 * Perform a cascading Excel-style sort on a MutableTable.
*/
public class SortTable extends ncsa.d2k.core.modules.UIModule {

	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "";
			default: return "No such input";
		}
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "";
			default: return "No such output";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Sort the columns of a Table.  </body></html>";
	}

	protected UserView createUserView() {
		return new SortTableView();
	}

	public String[] getFieldNameMapping() {
		return null;
	}

	private int numberOfSorts = 5;

	public int getNumberOfSorts() {
		return numberOfSorts;
	}

	public void setNumberOfSorts(int sort) {
		numberOfSorts = sort;
	}

	private void done(Table table) {
		pushOutput(table, 0);
		viewDone("Done");
	}

	boolean reorderColumns = false;

	public boolean getReorderColumns() {
		return reorderColumns;
	}

	public void setReorderColumns(boolean order) {
		reorderColumns = order;
	}

	private static final String NONE = "None";

	/**
		SortTableView
	*/
	private class SortTableView extends ncsa.d2k.userviews.swing.JUserPane {

		MutableTable table;
		int columns;
		int rows;
		int numsort;
		int[] sortorder;
		int sortsize;
		int[] runs = null;
		boolean first = true;
/*
	QuickQueue queue, lastqueue;
*/
		JLabel[] sortlabels;
		JComboBox[] sortchoices;

		JButton done, abort;

		public void initView(ViewModule viewmod) {
	//		module = (SortTable) viewmod;
		}

		public void setInput(Object object, int inputindex) {
			table = (MutableTable) object;
			columns = table.getNumColumns();
			rows = table.getNumRows();
			numsort = getNumberOfSorts();

			if (numsort > columns)
				numsort = columns;

			sortlabels = new JLabel[numsort];
			for(int index=0; index < numsort; index++) {
				JLabel label = new JLabel((index+1) + ". Sort by: ");
				sortlabels[index] = label;
			}

			String[] columnlabels = new String[columns+1];
			columnlabels[0] = NONE;
			for(int index=0; index < columns; index++) {
				String columnlabel = table.getColumnLabel(index);
				columnlabels[index+1] = columnlabel;
			}

			sortchoices = new JComboBox[numsort];
			for(int index=0; index < numsort; index++) {
				JComboBox combobox = new JComboBox(columnlabels);
				sortchoices[index] = combobox;
			}

			buildView();
		}

		public void buildView() {
			removeAll();

			setLayout(new BorderLayout());

			JPanel scrollpanel = new JPanel();
			scrollpanel.setLayout(new GridBagLayout());

			for(int index=0; index < numsort; index++) {
				Constrain.setConstraints(scrollpanel, sortlabels[index], 0, index, 1, 1, GridBagConstraints.NONE,
					GridBagConstraints.WEST, 0, 1);
				Constrain.setConstraints(scrollpanel, sortchoices[index], 1, index, 1, 1, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.WEST, 1, 0);
			}

			JScrollPane scrollpane = new JScrollPane(scrollpanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			add(scrollpane, BorderLayout.CENTER);

			JPanel buttonpanel = new JPanel();
			abort = new JButton("Abort");
			done = new JButton("Done");
			//abort.addActionListener(this);
			//done.addActionListener(this);
			abort.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					viewAbort();
				}
			});

			done.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					viewDone("Done");
					getSortOrder();
					sort();
				}
			});
			buttonpanel.add(abort);
			buttonpanel.add(done);
			add(buttonpanel, BorderLayout.SOUTH);
		}

		public Dimension getPreferredSize() {
			return new Dimension(300, 200);
		}

		public void getSortOrder() {
			ArrayList sortList = new ArrayList();
			for(int i = 0; i < sortchoices.length; i++) {
				int idx = sortchoices[i].getSelectedIndex();
				if(idx != 0)
					sortList.add(new Integer(idx-1));
				else
					break;
			}
			sortorder = new int[sortList.size()];
			for(int i = 0; i < sortList.size(); i++)
				sortorder[i] = ((Integer)sortList.get(i)).intValue();
			numsort = sortorder.length;
		}

		/**
		 * Find the indices where a run ends in a column.  A run is a block of
		 * equal values in the column.  The column is assumed to be sorted.
		 * This will return an array whose values are the indices of the
		 * rows that end a run.
		 * @param col the column of interest
		 * @return
		 */
		private int[] findRuns(int col) {
			if (first) {
				//System.out.println("FindRuns: "+table.getColumnLabel(col));
				ArrayList runList = new ArrayList();
				if(table.isColumnScalar(col)) {
					double currentVal = table.getDouble(0, col);
					for(int i = 1; i < table.getNumRows(); i++) {
						double rowVal = table.getDouble(i, col);
						if(rowVal != currentVal) {
							runList.add(new Integer(i-1));
							currentVal = rowVal;
						}
					}
				}
				else {
					String currentVal = table.getString(0, col);
					for(int i = 1; i < table.getNumRows(); i++) {
						String rowVal = table.getString(i, col);
						if(!rowVal.equals(currentVal)) {
							runList.add(new Integer(i-1));
							currentVal = rowVal;
						}
					}
				}
				runList.add(new Integer(table.getNumRows()-1));
				int[] retVal = new int[runList.size()];
				for(int i = 0; i < retVal.length; i++) {
					retVal[i] = ((Integer)runList.get(i)).intValue();
					//System.out.println("Run["+i+"]: "+retVal[i]);
				}
				return retVal;
			} else {
				//Sort through the runs
				ArrayList runList = new ArrayList();
				if(table.isColumnScalar(col)) {
					double currentVal = table.getDouble(0, col);
					for (int i = 0; i <= runs[0]; i++) {
						double rowVal = table.getDouble(i, col);
						if (rowVal != currentVal) {
							runList.add(new Integer(i-1));
							currentVal = rowVal;
						}
					}
					for (int j = 1; j < runs.length; j++) {
						runList.add(new Integer(runs[j-1]));
						for (int i = runs[j-1] + 1; i <= runs[j]; i++) {
							double rowVal = table.getDouble(i, col);
							if (rowVal != currentVal) {
								runList.add(new Integer(i-1));
								currentVal = rowVal;
							}
						}
					}
				} else {
					String currentVal = table.getString(0, col);
					for (int i = 0; i <= runs[0]; i++) {
						String rowVal = table.getString(i, col);
						if (!rowVal.equals(currentVal)) {
							runList.add(new Integer(i-1));
							currentVal = rowVal;
						}
					}
					for (int j = 1; j < runs.length; j++) {
						runList.add(new Integer(runs[j-1]));
						for (int i = runs[j-1] + 1; i <= runs[j]; i++) {
							String rowVal = table.getString(i, col);
							if (!rowVal.equals(currentVal)) {
								runList.add(new Integer(i-1));
								currentVal = rowVal;
							}
						}
					}
				}
				runList.add(new Integer(table.getNumRows()-1));
				int[] retVal = new int[runList.size()];
				for(int i = 0; i < retVal.length; i++) {
					retVal[i] = ((Integer)runList.get(i)).intValue();
					//System.out.println("Run["+i+"]: "+retVal[i]);
				}
				return retVal;
			}
		}

		/**
		 * Sort the table for each column selected.
		 */
		private void sort() {
			if(sortorder.length > 0) {
				table.sortByColumn(sortorder[0]);
				for(int i = 1; i < sortorder.length; i++) {
					// now, find the runs in the (i-1)th column and do a
					// table.sortByColumn(col, begin, end) for each run
					runs = findRuns(sortorder[i-1]);
					first = false;

					// do the first sort outside the loop
					table.sortByColumn(sortorder[i], 0, runs[0]);
					for(int j = 1; j < runs.length; j++) {
						// now sort from the end of the last run to the end
						// of the current run
						table.sortByColumn(sortorder[i], runs[j-1]+1, runs[j]);
					}
				}
			}
			if(getReorderColumns())
				reorder();
			//reset global variables.
			runs = null;
			sortorder = null;
			first = true;
			pushOutput(table, 0);
		}

	// Reorder columns
		public void reorder() {
			for(int i = 0; i < sortorder.length; i++) {
				table.swapColumns(sortorder[i], i);
			}
		}
	}

	public void initialize(int[] array) {
		for (int index=0; index < array.length; index++)
			array[index] = index;
	}
}
