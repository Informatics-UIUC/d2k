package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.controller.userviews.widgits.*;
import ncsa.d2k.util.*;
import ncsa.d2k.util.datatype.*;
import ncsa.gui.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
	MultipleSort.java

	Bradley Berkin
	5/01
*/
public class SortTable extends ncsa.d2k.infrastructure.modules.UIModule
{

	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"unsorted\">    <Text>Unsorted table </Text>  </Info></D2K>";
			default: return "No such input";
		}
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.util.datatype.Table"};
		return types;
	}

	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"sorted\">    <Text>Sorted table </Text>  </Info></D2K>";
			default: return "No such output";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.util.datatype.Table"};
		return types;
	}

	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"MultipleSort\">    <Text>Sorts the rows of the table by the first specified column. Finds equivalent rows and sorts by the second specified column. Repeats for specified number of columns. </Text>  </Info></D2K>";
	}

	protected UserView createUserView() {
		return new SortView();
	}

	public String[] getFieldNameMapping() {
		return null;

	}

	int numsort = 5;

	public int getNumsort() {
		return numsort;
	}

	public void setNumsort(int sort) {
		numsort = sort;
	}

	public void Done(Table table) {
		pushOutput(table, 0);
		viewDone("Done");
	}

	boolean reorder = false;

	public boolean getReorder() {
		return reorder;
	}

	public void setReorder(boolean order) {
		reorder = order;
	}

}


/**
	SortView
*/
class SortView extends ncsa.d2k.controller.userviews.swing.JUserPane implements ActionListener {
	SortTable module;

	Table table;
	int columns;
	int rows;
	int numsort;

	int[] sortorder;
	int sortsize;

	QuickQueue queue, lastqueue;

	JLabel[] sortlabels;
	JComboBox[] sortchoices;

	JButton done, abort;

	public void initView(ViewModule viewmod) {
		module = (SortTable) viewmod;
	}

	public void setInput(Object object, int inputindex) {
		table = (Table) object;
		columns = table.getNumColumns();
		rows = table.getNumRows();
		numsort = module.getNumsort();

		if (numsort > columns)
			numsort = columns;

		sortlabels = new JLabel[numsort];
		for(int index=0; index < numsort; index++) {
			JLabel label = new JLabel((index+1) + ". Sort by: ");
			sortlabels[index] = label;
		}

		String[] columnlabels = new String[columns+1];
		columnlabels[0] = "none";
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
		abort.addActionListener(this);
		done.addActionListener(this);
		buttonpanel.add(abort);
		buttonpanel.add(done);
		add(buttonpanel, BorderLayout.SOUTH);
	}

	public Dimension getPreferredSize() {
		return new Dimension(300, 200);
	}

	public void getSortOrder() {
		sortorder = new int[numsort];
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
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == done) {
			getSortOrder();
			sort();
		}
		if (source == abort)
			module.viewAbort();
	}


	public void sort() {
		Column column;
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
			table = (Table) table.reOrderRows(sorted);

			if (module.getReorder()) {
				reorder();
			}

		}
		module.Done(table);
	}

	// Reorder columns
	public void reorder() {
		//Column[] internal = (Column[]) table.getInternal();
		Column[] internal = new Column[table.getNumColumns()];
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
	}

	public void firstCollect(Column column, int[] sorted) {
		int previous = 0;
		int current = 0;
		int compare = 0;

		while (current < rows) {
			compare = ((SimpleColumn) column).compareRows(sorted[previous], sorted[current]);
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

	public void collect(Column column, int[] sorted) {
		int previous = 0;
		int current = 0;
		int compare = 0;

		while (!lastqueue.isEmpty()) {
			Run run = (Run) lastqueue.pop();
			current = run.start;
			previous = run.start;
			while (current <= run.end) {
				compare = ((SimpleColumn) column).compareRows(sorted[previous], sorted[current]);
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

	public void initialize(int[] array) {
		for (int index=0; index < array.length; index++)
			array[index] = index;
	}

	public void mergesort(Column column, int[] sorted, int start, int end) {
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
				int compare = ((SimpleColumn) column).compareRows(sorted[lowerindex], sorted[upperindex]);

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
}

