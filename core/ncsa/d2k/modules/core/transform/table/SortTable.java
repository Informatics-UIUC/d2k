package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.controller.userviews.widgits.*;
import ncsa.d2k.util.*;
import ncsa.gui.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Perform a cascading Excel-style sort on a MutableTable.
*/
public class SortTable extends ncsa.d2k.infrastructure.modules.UIModule {

	public String getInputInfo(int index) {
		return "";
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	public String getOutputInfo(int index) {
		return "";
	}

	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	public String getModuleInfo() {
		return "Sort the columns of a Table.";
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

	public void done(Table table) {
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
	private class SortTableView extends ncsa.d2k.controller.userviews.swing.JUserPane implements ActionListener {

		MutableTable table;
		int columns;
		int rows;
		int numsort;
		int[] sortorder;
		int sortsize;
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
			/*sortorder = new int[numsort];
			initialize(sortorder);
			int choicesindex = 0;
			int index = sortchoices[choicesindex].getSelectedIndex();
			while ((index != 0) && (choicesindex < numsort)) {
				sortorder[choicesindex] = index-1;
				choicesindex++;
				if (choicesindex < numsort)
					index = sortchoices[choicesindex].getSelectedIndex();
			}
			sortsize = choicesindex;
			*/
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

		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			/*if (source == done) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						viewDone("Done");
						getSortOrder();
						sort();
					}
				});
			}*/
			if (source == abort)
				viewAbort();
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
					int[] runs = findRuns(sortorder[i-1]);

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
			pushOutput(table, 0);
		}
/*		Column column;
		int[] sorted;
		int sortindex = 0;

		if (sortsize > 0) {
			// Initial run
			queue = new QuickQueue();
			lastqueue = new QuickQueue();
			sorted = new int[rows];
			initialize(sorted);

			column = table.getColumn(sortorder[sortindex]);
			mergesort(column, sorted, 0, rows-1);
			firstCollect(column, sorted);
			sortindex++;

			while (sortindex < sortsize) {
				column = table.getColumn(sortorder[sortindex]);
				while (!queue.isEmpty()) {
					Run run = (Run) queue.pop();
					lastqueue.push(run);
					mergesort(column, sorted, run.start, run.end);
				}
				collect(column, sorted);
				sortindex++;
			}
			table = (TableImpl) table.reorderRows(sorted);

			if (module.getReorder()) {
				reorder();
			}

		}
		module.done(table);
*/

	// Reorder columns
	public void reorder() {
		//Column[] internal = (Column[]) table.getInternal();
/*		Column[] internal = new Column[table.getNumColumns()];
		for(int i = 0; i < internal.length; i++)
			internal[i] = table.getColumn(i);

		Column[] ordered = new Column[columns];
		Hashtable hashtable = new Hashtable(sortsize);

		int orderindex = 0;
		for (; orderindex < sortsize; orderindex++) {
			int value = sortorder[orderindex];
			hashtable.put(new Integer(orderindex), new Integer(value));
			ordered[orderindex] = internal[value];
		}

		int internalindex = 0;
		for (; orderindex < columns;) {
			if (!hashtable.containsValue(new Integer(internalindex))) {
				ordered[orderindex] = internal[internalindex];
				orderindex++;
			}
			internalindex++;
		}

		//table.setInternal(ordered);
		for(int i = 0; i < ordered.length; i++)
			table.setColumn(ordered[i], i);
*/
		for(int i = 0; i < sortorder.length; i++) {
			table.swapColumns(sortorder[i], i);
		}
	}
	}

/*	public void firstCollect(Column column, int[] sorted) {
		int previous = 0;
		int current = 0;
		int compare = 0;

		while (current < rows) {
			compare = column.compareRows(sorted[previous], sorted[current]);
			if (compare != 0) {
				if ((current-previous) >= 1) {
					queue.push(new Run(previous, current-1));
				}
				previous = current;
			}
			current++;
		}
		// Collect last run
		if ((current==rows) && (compare == 0)) {
			queue.push(new Run(previous, current-1));
		}
	}
	*/

/*	public void collect(Column column, int[] sorted) {
		int previous = 0;
		int current = 0;
		int compare = 0;

		while (!lastqueue.isEmpty()) {
			Run run = (Run) lastqueue.pop();
			current = run.start;
			previous = run.start;
			while (current <= run.end) {
				compare = column.compareRows(sorted[previous], sorted[current]);
				if (compare != 0) {
					if ((current-previous) >= 1) {
						queue.push(new Run(previous, current-1));
					}
					previous = current;
				}
				current++;
			}
			// Collect last run
			if ((current > run.end) && (compare == 0)) {
				queue.push(new Run(previous, current-1));
			}
		}
	}
*/

	public void initialize(int[] array) {
		for (int index=0; index < array.length; index++)
			array[index] = index;
	}

/*	public void mergesort(Column column, int[] sorted, int start, int end) {
		if (start < end) {
			int middle = (start+end)/2;
			mergesort(column, sorted, start, middle);
			mergesort(column, sorted, middle+1, end);
			merge(column, sorted, start, middle, end);
		}
	}

	public void merge(Column column, int[] sorted, int start, int middle, int end) {
		int[] copy = new int[end+1];

		int index = start;
		int lowerindex = start;
		int upperindex = middle+1;

		while (index <= end) {
			if ((lowerindex <= middle) && (upperindex <= end)) {
				int compare = column.compareRows(sorted[lowerindex], sorted[upperindex]);

				if (compare < 0) {
					copy[index] = sorted[lowerindex];
					lowerindex++;
					index++;
				}
				else if (compare == 0) {
					copy[index] = sorted[lowerindex];
					copy[index+1] = sorted[upperindex];
					lowerindex++;
					upperindex++;
					index += 2;
				}
				else {
					copy[index] = sorted[upperindex];
					upperindex++;
					index++;
				}
			}
			else if (lowerindex > middle) {
				while (upperindex <= end) {
					copy[index] = sorted[upperindex];
					upperindex++;
					index++;
				}
			}
			else if(upperindex > end) {
				while (lowerindex <= middle) {
					copy[index] = sorted[lowerindex];
					lowerindex++;
					index++;
				}
			}
		}
		for (index=start; index <= end; index++)
			sorted[index] = copy[index];
	}

	private final class Run {
		int start, end;

		Run(int start, int end) {
			this.start = start;
			this.end = end;
		}

		void print() {
			System.out.println(start + "-" + end);
		}
	}
*/
}