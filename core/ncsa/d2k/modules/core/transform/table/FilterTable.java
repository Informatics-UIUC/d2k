package ncsa.d2k.modules.core.transform.table;

import  ncsa.d2k.infrastructure.modules.*;
import  ncsa.d2k.infrastructure.views.*;
import  ncsa.d2k.controller.userviews.swing.*;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Filter rows from a Table
 */
public class FilterTable extends UIModule {

	private static final String GREATER_THAN = ">";
	private static final String LESS_THAN = "<";
	private static final String GREATER_THAN_EQUAL_TO = ">=";
	private static final String LESS_THAN_EQUAL_TO = "<=";
	private static final String NOT_EQUAL_TO = "!=";
	private static final String EQUAL_TO = "==";

    /**
     This pair returns the description of the various inputs.
     @return the description of the indexed input.
     */
    public String getInputInfo (int index) {
        switch (index) {
            case 0:
                return  "A Table to filter.";
            default:
                return  "No such input";
        }
    }

    /**
     This pair returns an array of strings that contains the data types for the inputs.
     @return the data types of all inputs.
     */
    public String[] getInputTypes () {
        String[] types =  { "ncsa.d2k.modules.core.datatype.table.Table" };
        return  types;
    }

    /**
     This pair returns the description of the outputs.
     @return the description of the indexed output.
     */
    public String getOutputInfo (int index) {
        switch (index) {
            case 0:
                return  "A filtered table.";
            default:
                return  "No such output";
        }
    }

    /**
     This pair returns an array of strings that contains the data types for the outputs.
     @return the data types of all outputs.
     */
    public String[] getOutputTypes () {
        String[] types =  { "ncsa.d2k.modules.core.datatype.table.Table" };
        return  types;
    }

    /**
     This pair returns the description of the module.
     @return the description of the module.
     */
    public String getModuleInfo () {
        return  "Filter rows out of a Table.";
    }

    /**
     This pair is called by D2K to get the UserView for this module.
     @return the UserView.
     */
    protected UserView createUserView () {
        return new Filter();
    }

    /**
     This pair returns an array with the names of each DSComponent in the UserView
     that has a value.  These DSComponents are then used as the outputs of this module.
     */
    public String[] getFieldNameMapping () {
        return null;
    }

    /**
     * Filtering lines.
     */
    class Filter extends JUserPane implements ActionListener {
		Table table;
		boolean []linemap;

        HashMap numericColumnLookup;
        HashMap stringColumnLookup;
        JComboBox numColumns;
        JComboBox strColumns;
        JComboBox numOps;
        JComboBox strOps;
        JTextField numValue;
        JTextField strValue;
        JButton numAdd;
        JButton strAdd;
        JButton cancel;
        JButton done;
        JButton update;
        JList filterList;
        JButton remove;
        DefaultListModel listModel;

        /**
         * put your documentation comment here
         * @param         MainArea m
         */
		 public void initView(ViewModule m) {
		 }

		public void setInput(Object o, int id) {
			removeAll();
			table = (Table)o;
			linemap = new boolean[table.getCapacity()];

            numericColumnLookup = new HashMap();
            stringColumnLookup = new HashMap();
            LinkedList numCols = new LinkedList();
            LinkedList strCols = new LinkedList();
            for (int i = 0; i < table.getNumColumns(); i++) {
                Column c = table.getColumn(i);
                if (c instanceof NumericColumn) {
                    numericColumnLookup.put(c.getLabel(), new Integer(i));
                    numCols.add(c.getLabel());
                }
                else {
                    stringColumnLookup.put(c.getLabel(), new Integer(i));
                    strCols.add(c.getLabel());
                }
            }
            JOutlinePanel num = new JOutlinePanel("Numeric");
            //num.setLayout(new GridLayout(2, 3));
            num.setLayout(new GridBagLayout());
            numColumns = new JComboBox();
            Iterator i = numCols.iterator();
            while (i.hasNext())
                numColumns.addItem(i.next());
            numCols.clear();
            numOps = new JComboBox();
            numOps.addItem(GREATER_THAN);
            numOps.addItem(LESS_THAN);
            numOps.addItem(GREATER_THAN_EQUAL_TO);
            numOps.addItem(LESS_THAN_EQUAL_TO);
            numOps.addItem(EQUAL_TO);
            numOps.addItem(NOT_EQUAL_TO);
            numValue = new JTextField(5);
            numAdd = new JButton("Add");
            numAdd.addActionListener(this);
            /*num.add(numColumns);
             num.add(numOps);
             num.add(numValue);
             num.add(new JPanel());
             num.add(numAdd);
             num.add(new JPanel());
             */
            Constrain.setConstraints(num, numColumns, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(num, numOps, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(num, numValue, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(num, numAdd, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            JPanel str = new JOutlinePanel("String");
            //str.setLayout(new GridLayout(2, 3));
            str.setLayout(new GridBagLayout());
            strColumns = new JComboBox();
            i = strCols.iterator();
            while (i.hasNext())
                strColumns.addItem(i.next());
            strCols.clear();
            strOps = new JComboBox();
            strOps.addItem(EQUAL_TO);
            strOps.addItem(NOT_EQUAL_TO);
            strValue = new JTextField(5);
            strAdd = new JButton("Add");
            strAdd.addActionListener(this);
            /*str.add(strColumns);
             str.add(strOps);
             str.add(strValue);
             str.add(new JPanel());
             str.add(strAdd);
             str.add(new JPanel());
             */
            Constrain.setConstraints(str, strColumns, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(str, strOps, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(str, strValue, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(str, strAdd, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            JPanel one = new JPanel();
            //one.setLayout(new GridLayout(2, 1));
            /*one.setLayout(new BoxLayout(one, BoxLayout.Y_AXIS));
             one.add(num);
             one.add(Box.createGlue());
             one.add(str);
             */
            one.setLayout(new GridBagLayout());
            Constrain.setConstraints(one, num, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(one, new JPanel(), 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(one, str, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            JScrollPane jsp = new JScrollPane(one);
            jsp.setMinimumSize(jsp.getPreferredSize());
            //bg.setLayout(new BorderLayout());
            //bg.add(one, BorderLayout.CENTER);
            filterList = new JList();
            listModel = new DefaultListModel();
            JLabel lbl = new JLabel("Current Filters");
            Dimension d = lbl.getPreferredSize();
            //filterList.setFixedCellWidth(d.width);
            filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            filterList.setModel(listModel);
            JScrollPane jsp1 = new JScrollPane(filterList);
            JViewport jview = new JViewport();
            jview.setView(lbl);
            jsp1.setColumnHeader(jview);
            JPanel two = new JPanel();
            two.setLayout(new BorderLayout());
            two.add(jsp1, BorderLayout.CENTER);
            remove = new JButton("Remove");
            remove.addActionListener(this);
            JPanel rp = new JPanel();
            rp.add(remove);
            two.add(rp, BorderLayout.SOUTH);
            //bg.add(two, BorderLayout.EAST);
            JSplitPane bg = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            /*one*/
            jsp, two);
            cancel = new JButton("Cancel");
            cancel.addActionListener(this);
            done = new JButton("Done");
            done.addActionListener(this);
            //update = new JButton("Update");
            //update.addActionListener(this);
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(cancel);
            buttonPanel.add(done);
            //buttonPanel.add(update);
            JPanel whole = new JPanel();
            whole.setLayout(new BorderLayout());
            whole.add(bg, BorderLayout.CENTER);
            whole.add(buttonPanel, BorderLayout.SOUTH);
			setLayout(new BorderLayout());
			add(whole, BorderLayout.CENTER);
        }

        /**
         * Update the lines to show based on the current filteritems.
         */
        void updateLineMap () {
            Object[] filters = listModel.toArray();
            if (filters.length == 0) {
                for (int i = 0; i < table.getNumRows(); i++)
                    linemap[i] = true;
                return;
            }
            for (int i = 0; i < table.getNumRows(); i++) {
                boolean start = true;
                for (int j = 0; j < filters.length; j++) {
                    FilterItem fi = (FilterItem)filters[j];
                    int col = fi.colNum;
                    boolean retVal;
                    if (table.getColumn(col) instanceof NumericColumn)
                        retVal = fi.evaluate(table.getDouble(i, col));
                    else
                        retVal = fi.evaluate(table.getString(i, col));
                    start = start && retVal;
                }
                linemap[i] = start;
            }
        }

        /**
         * Listen for button presses.
         */
        public void actionPerformed (ActionEvent e) {
            Object src = e.getSource();
            if (src == strAdd) {
                String colLabel = strColumns.getSelectedItem().toString();
                String op = strOps.getSelectedItem().toString();
                String value = strValue.getText();
                int colNum = ((Integer)stringColumnLookup.get(colLabel)).intValue();
                FilterItem fi = new StringFilterItem(colLabel, colNum, op,
                        value);
                listModel.addElement(fi);
                strValue.setText("");
            }
            else if (src == numAdd) {
                String colLabel = numColumns.getSelectedItem().toString();
                String op = numOps.getSelectedItem().toString();
                String value = numValue.getText();
                int colNum = ((Integer)numericColumnLookup.get(colLabel)).intValue();
                double d = 0;
                try {
                    d = Double.parseDouble(value);
                } catch (Exception ex) {}
                FilterItem fi = new NumericFilterItem(colLabel, colNum, op,
                        d);
                listModel.addElement(fi);
                numValue.setText("");
            }
            else if (src == remove) {
                int selected = filterList.getSelectedIndex();
                if (selected != -1)
                    listModel.remove(selected);
            }
            else if (src == cancel) {
                listModel.removeAllElements();
				viewCancel();
                /*for (int i = 0; i < oldFilters.length; i++) {
                    listModel.addElement(oldFilters[i]);
                }
                oldFilters = null;
                setVisible(false);
				*/
            }
            else if (src == done) {
                updateLineMap();

				for(int i = 0; i < linemap.length; i++)
					linemap[i] = !linemap[i];

				table.removeByFlag(linemap);
				pushOutput(table, 0);
				viewDone("");

                //oldFilters = null;
                //setVisible(false);
            }
            /*else if (src == update) {
                updateLineMap();
                oldFilters = listModel.toArray();
            }*/
        }
        //Object[] oldFilters;

        /**
         * Base class for filters.
         */
        abstract class FilterItem {
            String label;
            int colNum;
            String op;

            /**
             * put your documentation comment here
             * @param s
             * @return
             */
            abstract boolean evaluate (String s);

            /**
             * put your documentation comment here
             * @param d
             * @return
             */
            abstract boolean evaluate (double d);
        }

        /**
         * Filter out items for numeric columns
         */
        class NumericFilterItem extends FilterItem {
            double value;

            /**
             * put your documentation comment here
             * @param             String l
             * @param             int c
             * @param             String o
             * @param             double v
             */
            NumericFilterItem (String l, int c, String o, double v) {
                label = l;
                colNum = c;
                op = o;
                value = v;
            }

            /**
             * put your documentation comment here
             * @param s
             * @return
             */
            boolean evaluate (String s) {
                double d = 0;
                try {
                    d = Double.parseDouble(s);
                } catch (Exception e) {
                    return  false;
                }
                return  evaluate(d);
            }

            /**
             * put your documentation comment here
             * @param d
             * @return
             */
            boolean evaluate (double d) {
                if (op == GREATER_THAN)
                    //return value > d;
                    return  value < d;
                else if (op == GREATER_THAN_EQUAL_TO)
                    //return value >= d;
                    return  value <= d;
                else if (op == LESS_THAN)
                    //return value < d;
                    return  value > d;
                else if (op == LESS_THAN_EQUAL_TO)
                    //return value <= d;
                    return  value >= d;
                else if (op == EQUAL_TO)
                    //return value == d;
                    return  value != d;
                else if (op == NOT_EQUAL_TO)
                    //return value != d;
                    return  value == d;
                return  false;
            }

            /**
             */
            public String toString () {
                return  label + " " + op + " " + value;
            }
        }

        /**
         * Filter out items for non-numeric columns.
         * String equality and inequality is used
         */
        class StringFilterItem extends FilterItem {
            String value;

            /**
             * put your documentation comment here
             * @param             String l
             * @param             int c
             * @param             String o
             * @param             String v
             */
            StringFilterItem (String l, int c, String o, String v) {
                label = l;
                colNum = c;
                op = o;
                value = v;
            }

            /**
             Return true if the item should be shown
             */
            boolean evaluate (String s) {
                if (op == EQUAL_TO)
                    return  value.trim().equals(s.trim());
                else if (op == NOT_EQUAL_TO)
                    return  !value.trim().equals(s.trim());
                return  false;
            }

            /**
             * put your documentation comment here
             * @param d
             * @return
             */
            boolean evaluate (double d) {
                String s;
                try {
                    s = Double.toString(d);
                } catch (Exception e) {
                    return  false;
                }
                return  evaluate(s);
            }

            /**
             * put your documentation comment here
             * @return
             */
            public String toString () {
                return  label + " " + op + " " + value;
            }
        }
    }
}



