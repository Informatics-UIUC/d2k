package ncsa.d2k.modules.core.transform.table;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.*;

/*
	Filter rows from a table
	Two filters can be combined to form a boolean expression using replace
	Remaining filters are combined using logical and
*/
public class FilterTable extends UIModule {

	private static final String GREATER_THAN = ">";
	private static final String LESS_THAN = "<";
	private static final String GREATER_THAN_EQUAL_TO = ">=";
	private static final String LESS_THAN_EQUAL_TO = "<=";
	private static final String NOT_EQUAL_TO = "!=";
	private static final String EQUAL_TO = "==";

	private static final String AND = "&&";
	private static final String OR = "||";

   public String getInputInfo (int index) {
        switch (index) {
            case 0:
                return "Table to filter";
            default:
                return "No such input";
        }
    }

    public String[] getInputTypes () {
        String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return types;
    }

    public String getOutputInfo (int index) {
        switch (index) {
            case 0:
                return "Filtered table";
            default:
                return "No such output";
        }
    }

    public String[] getOutputTypes () {
        String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return types;
    }

    public String getModuleInfo () {
        return "Filter rows out of a table";
    }

    protected UserView createUserView () {
        return new Filter();
    }

	public String[] getFieldNameMapping () {
        return null;
    }

    class Filter extends JUserPane implements ActionListener {
		MutableTable table;
		boolean[] linemap;

        HashMap numColumnLookup;
        HashMap strColumnLookup;
		HashMap strValueLookup;

        JComboBox numColumns;
        JComboBox strColumns;
        JComboBox numOps;
        JComboBox strOps;

		JTextField numValue;
		JComboBox strValue;

        JButton numAdd;
        JButton strAdd;
        JButton replace;
        JButton remove;

        JButton cancel;
        JButton done;

        JComboBox operators;

        JList filterList;
        DefaultListModel listModel;

		public void initView(ViewModule module) {
		}

		public void setInput(Object object, int id) {
			removeAll();
			table = (MutableTable) object;
			linemap = new boolean[table.getNumRows()];

            numColumnLookup = new HashMap();
            strColumnLookup = new HashMap();
            strValueLookup = new HashMap();

            LinkedList numCols = new LinkedList();
            LinkedList strCols = new LinkedList();

            for (int index = 0; index < table.getNumColumns(); index++) {
                //Column column = table.getColumn(index);

                //if (column instanceof NumericColumn) {
				if(table.isColumnScalar(index)) {
                    numColumnLookup.put(table.getColumnLabel(index), new Integer(index));
                    numCols.add(table.getColumnLabel(index));
                }
                else {
                    strColumnLookup.put(table.getColumnLabel(index), new Integer(index));
                    strValueLookup.put(table.getColumnLabel(index), getUniqueValues(index));
                    strCols.add(table.getColumnLabel(index));
                }
            }

            // Numeric attributes
            JOutlinePanel numericpanel = new JOutlinePanel("Numeric");
            numericpanel.setLayout(new GridBagLayout());
            numColumns = new JComboBox();
            Iterator iterator = numCols.iterator();

            while (iterator.hasNext())
                numColumns.addItem(iterator.next());
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

            Constrain.setConstraints(numericpanel, numColumns, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numericpanel, numOps, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numericpanel, numValue, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numericpanel, numAdd, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);

            // String attributes
            JPanel stringpanel = new JOutlinePanel("String");
            stringpanel.setLayout(new GridBagLayout());
            strColumns = new JComboBox();
            iterator = strCols.iterator();

            while (iterator.hasNext())
                strColumns.addItem(iterator.next());
            strCols.clear();

            strOps = new JComboBox();
            strOps.addItem(EQUAL_TO);
            strOps.addItem(NOT_EQUAL_TO);

            if (strColumns.getItemCount() > 0) {
            	String column = (String) strColumns.getItemAt(0);
            	String[] value = (String[]) strValueLookup.get(column);
            	strValue = new JComboBox(new DefaultComboBoxModel(value));
			}
			else
				strValue = new JComboBox();

			strColumns.addActionListener(this);

            strAdd = new JButton("Add");
            strAdd.addActionListener(this);

            Constrain.setConstraints(stringpanel, strColumns, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(stringpanel, strOps, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(stringpanel, strValue, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(stringpanel, strAdd, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST, 1, 1);

            // Attribute panel
            JPanel attributepanel = new JPanel();
            attributepanel.setLayout(new GridBagLayout());

            Constrain.setConstraints(attributepanel, numericpanel, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
            Constrain.setConstraints(attributepanel, stringpanel, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.NORTHWEST, 1, 1, new Insets(5, 5, 5, 5));

            JScrollPane attributescroll = new JScrollPane(attributepanel);
            attributescroll.setMinimumSize(attributescroll.getPreferredSize());

            // Filters
            filterList = new JList();
            listModel = new DefaultListModel();
            JLabel filterlabel = new JLabel("Current Filters");
            filterList.setModel(listModel);

            JScrollPane filterscroll = new JScrollPane(filterList);
            JViewport viewport = new JViewport();
            viewport.setView(filterlabel);
            filterscroll.setColumnHeader(viewport);

			// Filter panel
            JPanel filterpanel = new JPanel();
            filterpanel.setLayout(new BorderLayout());
            filterpanel.add(filterscroll, BorderLayout.CENTER);

            remove = new JButton("Remove");
            remove.addActionListener(this);

            operators = new JComboBox();
			operators.addItem(AND);
			operators.addItem(OR);

            replace = new JButton("Replace");
            replace.addActionListener(this);

            JPanel filterbuttons = new JPanel();
            filterbuttons.setLayout(new GridBagLayout());

            Constrain.setConstraints(filterbuttons, remove, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
            	GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
            Constrain.setConstraints(filterbuttons, new JPanel(), 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 1, 1, new Insets(5, 5, 5, 5));
			Constrain.setConstraints(filterbuttons, operators, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL,
			    GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
			Constrain.setConstraints(filterbuttons, replace, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL,
			    GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));

			filterpanel.add(filterbuttons, BorderLayout.SOUTH);

            JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, attributescroll, filterpanel);

            cancel = new JButton("Cancel");
            cancel.addActionListener(this);

            done = new JButton("Done");
            done.addActionListener(this);

            JPanel buttonpanel = new JPanel();
            buttonpanel.add(cancel);
            buttonpanel.add(done);

            setLayout(new BorderLayout());
            add(splitpane, BorderLayout.CENTER);
            add(buttonpanel, BorderLayout.SOUTH);
		}

        String[] getUniqueValues(/*Column column*/int column) {
			HashMap map = new HashMap();

			for (int index = 0; index < table.getNumRows(); index++) {
				String key = table.getString(index, column);

				if (!map.containsKey(key))
					map.put(key, new Integer(index));
			}

			Set set = map.keySet();
			String[] keys = new String[set.size()];
			Iterator iterator = set.iterator();
			int index = 0;

			while (iterator.hasNext()) {
				keys[index] = (String) iterator.next();
				index++;
			}

			return keys;
		}

        void updateLineMap () {
            Object[] filters = listModel.toArray();

            if (filters.length == 0) {
                for (int row = 0; row < table.getNumRows(); row++)
                    linemap[row] = true;

                return;
            }

            FilterItem filteritem = (FilterItem) filters[0];

            if (filters.length > 1) {
				for (int index = 1; index < filters.length; index++)
					filteritem = new CompoundFilterItem(filteritem, (FilterItem) filters[index], AND);
			}

            if (filteritem instanceof CompoundFilterItem) {
				for (int row = 0; row < table.getNumRows(); row++)
					linemap[row] = filteritem.evaluate(filteritem, row);
			}
			else {
				int column = filteritem.column;
				boolean value;

				for (int row = 0; row < table.getNumRows(); row++) {

					//if (table.getColumn(column) instanceof NumericColumn)
					if(table.isColumnScalar(column))
						value = filteritem.evaluate(table.getDouble(row, column));
					else
						value = filteritem.evaluate(table.getString(row, column));

                	linemap[row] = value;
				}
            }
        }

        public void actionPerformed (ActionEvent event) {
            Object source = event.getSource();

            if (source == strColumns) {
				String column = (String) strColumns.getSelectedItem();
				String[] value = (String[]) strValueLookup.get(column);

				strValue.setModel(new DefaultComboBoxModel(value));
				strValue.revalidate();
				strValue.repaint();
			}

            if (source == strAdd) {
                String colLabel = strColumns.getSelectedItem().toString();
                String op = strOps.getSelectedItem().toString();
                String value = (String) strValue.getSelectedItem();
                int colNum = ((Integer) strColumnLookup.get(colLabel)).intValue();
                FilterItem fi = new StringFilterItem(colLabel, colNum, op, value);
                listModel.addElement(fi);
            }

            else if (source == numAdd) {
                String colLabel = numColumns.getSelectedItem().toString();
                String op = numOps.getSelectedItem().toString();
                String value = numValue.getText();
                int colNum = ((Integer) numColumnLookup.get(colLabel)).intValue();
                double d = 0;
                try {
                    d = Double.parseDouble(value);
                } catch (Exception exception) {}
                FilterItem fi = new NumericFilterItem(colLabel, colNum, op, d);
                listModel.addElement(fi);
                numValue.setText("");
            }

            else if (source == remove) {
                int selected = filterList.getSelectedIndex();
                if (selected != -1)
                    listModel.remove(selected);
            }

            else if (source == replace) {
				String operator = (String) operators.getSelectedItem();

				int[] indices = filterList.getSelectedIndices();
				if (indices.length < 2)
					return;

				FilterItem first = (FilterItem) listModel.getElementAt(indices[0]);
				FilterItem second = (FilterItem) listModel.getElementAt(indices[1]);
				FilterItem three = new CompoundFilterItem(first, second, operator);

				listModel.removeElementAt(indices[0]);
				listModel.removeElementAt(indices[1] - 1);
				listModel.add(0, three);
			}

            else if (source == cancel) {
                listModel.removeAllElements();
				viewCancel();
            }

            else if (source == done) {
                updateLineMap();

				for(int index = 0; index < linemap.length; index++)
					linemap[index] = !linemap[index];

				table.removeRowsByFlag(linemap);
				pushOutput(table, 0);
				viewDone("");
            }
		}

        /*
        	Base class for filters
        */
        class FilterItem {
            FilterItem first, second;

            String attribute;
            int column;
            String operator;

            boolean evaluate(String value) {
				return false;
			}

            boolean evaluate(double value) {
				return false;
			}

			boolean evaluate(FilterItem filteritem, int row) {
				return false;
			}
        }

        /*
        	Class for binary boolean expressions
        */
        class CompoundFilterItem extends FilterItem {

			CompoundFilterItem(FilterItem first, FilterItem second, String operator) {
				this.first = first;
				this.second = second;
				this.operator = operator;
			}

			boolean evaluate(String value) {
				return false;
			}

			boolean evaluate(double value) {
				return false;
			}

			boolean evaluate(FilterItem filteritem, int row) {
				boolean expression;

				if (filteritem instanceof CompoundFilterItem) {
					expression = evaluate(filteritem.first, row);

					if (filteritem.operator == AND)
						expression = expression && evaluate(filteritem.second, row);
					else if (filteritem.operator == OR)
						expression = expression || evaluate(filteritem.second, row);
				}
				else {
					int column = filteritem.column;

					//if (table.getColumn(column) instanceof NumericColumn)
					if(table.isColumnScalar(column))
						return filteritem.evaluate(table.getDouble(row, column));
					else
						return filteritem.evaluate(table.getString(row, column));
				}

				return expression;
			}

			public String toString() {
				return toString(this);
			}

			String toString(FilterItem filteritem) {
				String expression;

				if (filteritem instanceof CompoundFilterItem) {
					expression = "(" + toString(filteritem.first) + " " + filteritem.operator;
					expression = expression + " " + toString(filteritem.second) + ")";
				}
				else
					return filteritem.toString();

				return expression;
			}
		}

        class NumericFilterItem extends FilterItem {
            double value;

            NumericFilterItem(String attribute, int column, String operator, double value) {
                this.attribute = attribute;
                this.column = column;
                this.operator = operator;
                this.value = value;
            }

            boolean evaluate(String svalue) {
                double dvalue = 0;

                try {
                    dvalue = Double.parseDouble(svalue);
                } catch(Exception exception) {
                    return false;
                }

                return evaluate(dvalue);
            }

            boolean evaluate(double dvalue) {
                if (operator == GREATER_THAN)
                    return value < dvalue;

                else if (operator == GREATER_THAN_EQUAL_TO)
                    return value <= dvalue;

                else if (operator == LESS_THAN)
                    return value > dvalue;

                else if (operator == LESS_THAN_EQUAL_TO)
                    return value >= dvalue;

                else if (operator == EQUAL_TO)
                    return value == dvalue;

                else if (operator == NOT_EQUAL_TO)
                    return value != dvalue;

                return false;
            }

			public String toString() {
                return attribute + " " + operator + " " + value;
            }
        }

        class StringFilterItem extends FilterItem {
            String value;

            StringFilterItem(String attribute, int column, String operator, String value) {
                first = null;
                second = null;

                this.attribute = attribute;
                this.column = column;
                this.operator = operator;
                this.value = value;
            }

            boolean evaluate(String svalue) {
                if (operator == EQUAL_TO)
                    return value.trim().equals(svalue.trim());

                else if (operator == NOT_EQUAL_TO)
                    return !(value.trim().equals(svalue.trim()));

                return false;
            }

            boolean evaluate(double dvalue) {
                String svalue;

                try {
                    svalue = Double.toString(dvalue);
                } catch (Exception exception) {
                    return false;
                }

                return evaluate(svalue);
            }

            public String toString() {
                return attribute + " " + operator + " " + value;
            }
        }
    }
}
