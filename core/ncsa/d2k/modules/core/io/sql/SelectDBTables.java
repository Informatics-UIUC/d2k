package ncsa.d2k.modules.projects.smathur.io.sql;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.Constrain;

import ncsa.d2k.modules.projects.smathur.io.sql.*;

public class QueryDB extends UIModule {

    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.projects.smathur.io.sql.DBConnection"};
//       String[] in = {"ncsa.d2k.modules.projects.smathur.io.sql.TestGui"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.projects.smathur.io.sql.DBTable"};
//        String[] out = null;
        return out;
    }

    public String getInputInfo(int i) {
        return "";
    }

    public String getInputName(int i) {
        return "";
    }

    public String getOutputInfo(int i) {
        return "";
    }

    public String getOutputName(int i) {
        return "";
    }

    public String getModuleInfo() {
        return "";
    }

    public String getModuleName() {
        return "";
    }

    protected UserView createUserView() {
        return new QueryDBView();
    }

    public String[] getFieldNameMapping() {
        return null;
    }

    private class QueryDBView extends JUserPane {
        private JList allTablesList;                  // all tables
        private DefaultListModel allTablesModel;

        private JList availTablesList;                // available tables
        private DefaultListModel availTablesModel;
        private JList availColumnsList;               // available columns
        private DefaultListModel availColumnsModel;

        private JList selectedTablesList;             // selected tables
        private DefaultListModel selectedTablesModel;

        private JComboBox allTables1; //for join
        private JComboBox allTables2; //for join
        private JComboBox allColumns1; //for join
        private JComboBox allColumns2; //for join
        private JComboBox operations12; // for join   : ==, !=, >, <, >=, <=
        private JButton moveToJoin;
        private JButton removeJoin;

//        private JComboBox joins;   //................
        private JList joinsList;      //................
        private DefaultListModel joinsModel;
        private JScrollPane jspJoins;

        private String[] ops = {"  ", "==", ">", "<", "!=", ">=", "<="}; //........


        private JComboBox   allTables;      // for row filter       //...............
        private JComboBox   allColumns;     // for row filter
        private JComboBox   operations;     // for row filter   : ==, !=, >, <, >=, <=
        private JTextField  value;          // for row filter
        private JButton     moveToFilter;   // for row filter
        private JButton     removeFilter;   // for row filter

//        private JComboBox filter;         // for row filter
        private JList             filterList;      //................
        private DefaultListModel  filterModel;
        private JScrollPane       jspFilter;

        private JLabel            option;         //................
        private JTextField        optionalSQL;    //for optional sql
        private JButton           moveToOption;   // for optional sql
        private JButton           removeOption;   // for optional sql
        private JList             optionList;      //................
        private DefaultListModel  optionModel;
        private JScrollPane       jspOption;

        private JButton next;             // common buttons
        private JButton previous;
        private JButton check;
        private JButton abort;
        private JButton done;

        private JPanel mainArea;
        private CardLayout cardLayout;

        private HashMap tableNameToSelectedColsLookup;   // table selected <-> columns selected


        private DBConnection dbc;     // the input
        private DBDataSource dbds;    // got from the input

//        private TestGui dbc;

        private String current = "one";

        private class TableSelectionListener implements ListSelectionListener {
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting() && !removingTableName) {
                    String val = (String)availTablesList.getSelectedValue();

                    availColumnsModel.clear();
                    // now get the list of columns
                    String[] columns = dbc.getColumnNames(val);

                    // now add them to the availColumnsModel
                    for(int i = 0; i < columns.length; i++) {
                        availColumnsModel.addElement(columns[i]);
                    }
                }
            }
        }

        private String getWhereClause() {
            StringBuffer sb = new StringBuffer();
            int size = joinsModel.size() + filterModel.size() + optionModel.size();
            String[] all = new String[size];
            int allCount = 0;

            for (int i=0; i<joinsModel.size(); i++) {
                all[allCount] = joinsModel.elementAt(i).toString();
                allCount++;
            }
            for (int i=0; i<filterModel.size(); i++) {
                all[allCount] = filterModel.elementAt(i).toString();
                allCount++;
            }
            for (int i=0; i<optionModel.size(); i++) {
                all[allCount] = optionModel.elementAt(i).toString();
                allCount++;
            }
            for (int i=0; i<all.length; i++) {
                sb.append(all[i]);
                if (i<all.length-1)
                    sb.append(" AND ");
            }
            return sb.toString();
        }


        public void initView(ViewModule vm) {
            tableNameToSelectedColsLookup = new HashMap();
//......................................................................................//
            // setup one
            availTablesList = new JList((availTablesModel = new DefaultListModel()));
            availTablesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            availTablesList.addListSelectionListener(new TableSelectionListener());

            availColumnsList = new JList((availColumnsModel = new DefaultListModel()));
            availColumnsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            selectedTablesList = new JList((selectedTablesModel = new DefaultListModel()));
            selectedTablesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane jsp0 = new JScrollPane(availTablesList);
            jsp0.setColumnHeaderView(new JLabel("Table Names"));
            JScrollPane jsp1 = new JScrollPane(availColumnsList);
            jsp1.setColumnHeaderView(new JLabel("Column Names"));


            JButton moveToSelectedTables = new JButton("Add");
            JButton removeSelectedTables = new JButton("Remove");
            moveToSelectedTables.addActionListener(new AddToSelectedTables());
            removeSelectedTables.addActionListener(new RemoveSelectedTables());
//            JPanel buttons = new JPanel();
//            buttons.setLayout(new GridLayout(2, 1));
//            buttons.add(moveToSelectedTables);
//            buttons.add(removeSelectedTables);
//            one.add(buttons);

            JScrollPane jsp2 = new JScrollPane(selectedTablesList);
            jsp2.setColumnHeaderView(new JLabel("Selected Tables"));
//            one.add(jsp2);


            JPanel p0 = new JPanel();
            p0.setLayout(new GridBagLayout());

            Constrain.setConstraints(p0, jsp0, 0, 0, 1, 4,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p0, jsp1, 1, 0, 1, 4,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);

            Constrain.setConstraints(p0, moveToSelectedTables, 2, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p0, removeSelectedTables, 2, 2, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);

            Constrain.setConstraints(p0, jsp2, 3, 0, 1, 4,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);


            JPanel one = new JPanel();
            one.add(p0);


//......................................................................................//
            // now two
            JPanel p1 = new JPanel();
            p1.setLayout(new GridBagLayout());

            allTables1 = new JComboBox();
            allTables2 = new JComboBox();
            allTables1.addActionListener(new ShowSelectedColumns1()); //............
            allTables2.addActionListener(new ShowSelectedColumns2());

            allColumns1 = new JComboBox();
            allColumns2 = new JComboBox();

            operations12 = new JComboBox(ops); //...............

            moveToJoin = new JButton("Add");         //............
            removeJoin = new JButton("Remove");
            moveToJoin.addActionListener(new AddJoin());
            removeJoin.addActionListener(new RemoveJoin());


            Constrain.setConstraints(p1, new JLabel("Table 1", JLabel.CENTER), 0, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p1, new JPanel(), 1, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p1, new JLabel("Table 2", JLabel.CENTER), 2, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);

            Constrain.setConstraints(p1, allTables1, 0, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p1, new JLabel("JOIN", JLabel.CENTER), 1, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p1, allTables2, 2, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p1, moveToJoin, 3, 1, 1, 1,  //..............
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);

            Constrain.setConstraints(p1, new JLabel("Column", JLabel.CENTER), 0, 2, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p1, new JLabel("Op", JLabel.CENTER), 1, 2, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p1, new JLabel("Column", JLabel.CENTER), 2, 2, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p1, removeJoin, 3, 2, 1, 1,  //..............
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);

            Constrain.setConstraints(p1, allColumns1, 0, 3, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p1, operations12, 1, 3, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(p1, allColumns2, 2, 3, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);


            allTablesList = new JList((allTablesModel = new DefaultListModel()));

//            joins = new JComboBox();   //...............
            joinsList = new JList((joinsModel = new DefaultListModel()));   //-----------
            joinsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane jspJoins = new JScrollPane(joinsList);
            jspJoins.setColumnHeaderView(new JLabel("Joins"));

            JPanel two = new JPanel();
            two.setLayout(new BorderLayout());
            two.add(p1, BorderLayout.CENTER);

//            two.add(joins, BorderLayout.EAST);
            two.add(jspJoins, BorderLayout.EAST);

//......................................................................................//
            // now three
            JPanel three = new JPanel();
            JPanel p2 = new JPanel();
            p2.setLayout(new GridLayout(1, 4));
            allTables = new JComboBox();
            allTables.addActionListener(new ShowSelectedColumns()); //............
            allColumns = new JComboBox();
            operations = new JComboBox(ops); //...............
            value = new javax.swing.JTextField();
            moveToFilter = new JButton("Add");         //............
            removeFilter = new JButton("Remove");
            moveToFilter.addActionListener(new AddFilter());
            removeFilter.addActionListener(new RemoveFilter());

//            filter = new JComboBox();
            filterList = new JList((filterModel = new DefaultListModel()));   //-----------
            filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane jspFilter = new JScrollPane(filterList);
            jspFilter.setColumnHeaderView(new JLabel("Row Filter"));

            p2.add(allTables);
            p2.add(allColumns);
            p2.add(operations);
            p2.add(value);
            three.add(p2);

            JPanel pBut = new JPanel();
            pBut.setLayout(new GridLayout(2,1));
            pBut.add(moveToFilter);
            pBut.add(removeFilter);
            three.add(pBut);

//            three.add(filter);
            three.add(jspFilter);

//......................................................................................//
            // now four
            option = new JLabel("Optional, Additional SQL:");
            optionalSQL = new JTextField("");

            JPanel pOpt = new JPanel();
            pOpt.setLayout(new GridLayout(2,1));
            pOpt.add(option);
            pOpt.add(optionalSQL);

            JPanel four = new JPanel();
            four.add(pOpt);

            moveToOption = new JButton("Add");         //............
            removeOption = new JButton("Remove");
            moveToOption.addActionListener(new AddOption());
            removeOption.addActionListener(new RemoveOption());

            JPanel pBut2 = new JPanel();
            pBut2.setLayout(new GridLayout(2,1));
            pBut2.add(moveToOption);
            pBut2.add(removeOption);
            four.add(pBut2);

            optionList = new JList((optionModel = new DefaultListModel()));   //-----------
            optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane jspOption = new JScrollPane(optionList);
            jspOption.setColumnHeaderView(new JLabel("Optional SQL"));

            four.add(jspOption);

//......................................................................................//

            // now the buttons
            JPanel buttonPanel = new JPanel();
            next = new JButton("Next");
            next.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    next.setEnabled(false);
                    if (current == "three") {
                        cardLayout.show(mainArea, "four");
                        next.setEnabled(false);
                        previous.setEnabled(true);
                        current = "four";
                    }
                    else if (current == "two") {
                        cardLayout.show(mainArea, "three");
                        next.setEnabled(true);
                        previous.setEnabled(true);
                        current = "three";
                    }
                    else if (current == "one") {
                        cardLayout.show(mainArea, "two");
                        next.setEnabled(true);
                        previous.setEnabled(true);
                        current = "two";
                    }
                }

            });
            previous = new JButton("Previous");
            previous.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    previous.setEnabled(false);
                    if (current == "two"){
                        cardLayout.show(mainArea, "one");
                        previous.setEnabled(false);
                        next.setEnabled(true);
                        current = "one";
                    }
                    else if (current == "three"){
                        cardLayout.show(mainArea, "two");
                        previous.setEnabled(true);
                        next.setEnabled(true);
                        current = "two";
                    }
                    else if (current == "four"){
                        cardLayout.show(mainArea, "three");
                        previous.setEnabled(true);
                        next.setEnabled(true);
                        current = "three";
                    }
                }
            });
            previous.setEnabled(false);
            next.setEnabled(true);

//            check = new JButton("Check");
            abort = new JButton("Abort");
            abort.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    viewCancel();
                }
            });
            done = new JButton("Done");
            done.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    Object[] selectedTables =  selectedTablesModel.toArray();

                    String[] tabs = new String[selectedTables.length];
                    String[][] cols = new String[selectedTables.length][];
                    String where = getWhereClause();

System.out.println("---------->where clause :  " + where);

                    for(int i = 0; i < selectedTables.length; i++) {
                        //SelectedTable st = (SelectedTable)selectedTables[i];
                        String tn = (String)selectedTables[i];
                        SelectedTable st = (SelectedTable)tableNameToSelectedColsLookup.get(tn);

                        tabs[i] = st.tableName;
                        cols[i] = st.selectedColumnNames;
                    }

//                    DBTable table = new DBTable( (DBTableConnection)dbi, tabs, cols,
//                                                  "", 0);

                    dbds = new ResultSetDataSource(dbc, tabs, cols, where);
                    DBTable table = new DBTable(dbds, dbc);

                    pushOutput(table, 0);

                    // push output here
                    viewDone("Done");
                }
            });

            buttonPanel.add(next);
            buttonPanel.add(previous);
//            buttonPanel.add(check);
            buttonPanel.add(abort);
            buttonPanel.add(done);

            // now the main area
            mainArea = new JPanel();
            mainArea.setLayout((cardLayout = new CardLayout()));
            mainArea.add(one, "one");
            mainArea.add(two, "two");
            mainArea.add(three, "three");       //............
            mainArea.add(four, "four");

            setLayout(new BorderLayout());
            add(mainArea, BorderLayout.CENTER);
            current = "one";                    //............
            cardLayout.show(mainArea, "one");
            add(buttonPanel, BorderLayout.SOUTH);
        }//initiview



//......................................................................................//

        private boolean removingTableName = false;

        private class AddToSelectedTables implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                // get the selected item from the tables list
                String sel = (String)availTablesList.getSelectedValue();
                // remove the selected item from the available tables model
                removingTableName = true;
                availTablesModel.removeElement(sel);
                removingTableName = false;

                // get the selected items from the columns list
                Object[] vals = availColumnsList.getSelectedValues();
                String[] cols = new String[vals.length];
                for(int i = 0; i < vals.length; i++)
                    cols[i] = (String)vals[i];

                // create the wrapper object
                SelectedTable st = new SelectedTable();
                st.tableName = sel;
                st.selectedColumnNames = cols;
                // add this to the selected tables list
                selectedTablesModel.addElement(sel);

                tableNameToSelectedColsLookup.put(sel, st);
                // add the table name to the combo boxes
                allTables1.addItem(sel);
                allTables2.addItem(sel);

                allTables.addItem(sel);
            }
        }

        private class RemoveSelectedTables implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                Object[] val = selectedTablesList.getSelectedValues();

                for(int i = 0; i < val.length; i++) {
                    SelectedTable st = (SelectedTable)val[i];
                    // remove from the selected tables model
                    selectedTablesModel.removeElement(st);
                    // remove the table from the hash map
                    tableNameToSelectedColsLookup.remove(st.tableName);

                    // remove the tables from each table combo box
                    allTables1.removeItem(st.tableName);
                    allTables2.removeItem(st.tableName);

                    allTables.removeItem(st.tableName);
                }
            }
        }

        private class SelectedTable {
            String tableName;
            String[] selectedColumnNames;

            public String toString() {
                return tableName;
            }
        }
//......................................................................................//

        private class ShowSelectedColumns1 implements ActionListener { //.............
            public void actionPerformed(ActionEvent e) {
                 JComboBox cb = (JComboBox)e.getSource();
                 String tab =  (String)cb.getSelectedItem();
                 SelectedTable st = (SelectedTable)tableNameToSelectedColsLookup.get(tab);
                 String[] colsSelected = st.selectedColumnNames;

                 allColumns1.removeAllItems();
                 for (int i=0; i<colsSelected.length; i++){
                     allColumns1.addItem(colsSelected[i]);
                 }
            }
        }

        private class ShowSelectedColumns2 implements ActionListener {//.............
            public void actionPerformed(ActionEvent e) {
                 JComboBox cb = (JComboBox)e.getSource();
                 String tab =  (String)cb.getSelectedItem();
                 SelectedTable st = (SelectedTable)tableNameToSelectedColsLookup.get(tab);
                 String[] colsSelected = st.selectedColumnNames;

                 allColumns2.removeAllItems();
                 for (int i=0; i<colsSelected.length; i++){
                     allColumns2.addItem(colsSelected[i]);
                 }
            }
        }

        private boolean removingJoin = false;
        // TODO
        private class AddJoin implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                // get the selected join columns and join operation
                String tab1 = (String)allTables1.getSelectedItem();
                String tab2 = (String)allTables2.getSelectedItem();
                String col1 = (String)allColumns1.getSelectedItem();
                String col2 = (String)allColumns2.getSelectedItem();
                String op   = (String)operations12.getSelectedItem();

                StringBuffer sb = new StringBuffer();
                sb.append(tab1);
                sb.append(".");
                sb.append(col1);
                sb.append(" ");
                sb.append(op);
                sb.append(" ");
                sb.append(tab2);
                sb.append(".");
                sb.append(col2);

//                joins.addItem(sb.toString());
                joinsModel.addElement(sb.toString());

            }
        }



        private class RemoveJoin implements ActionListener {  //................
            public void actionPerformed(ActionEvent e) {
//                Object val = joins.getSelectedItem();
//                joins.removeItem(val); JFrame f;

                  Object val = joinsList.getSelectedValue();
                  joinsModel.removeElement(val.toString());

                //joins.repaint();
            }
        }

//......................................................................................//
// listeners for 'three'

        private class ShowSelectedColumns implements ActionListener { //.............
            public void actionPerformed(ActionEvent e) {
                 JComboBox cb = (JComboBox)e.getSource();
                 String tab =  (String)cb.getSelectedItem();
                 SelectedTable st = (SelectedTable)tableNameToSelectedColsLookup.get(tab);
                 String[] colsSelected = st.selectedColumnNames;

                 allColumns.removeAllItems();
                 for (int i=0; i<colsSelected.length; i++){
                     allColumns.addItem(colsSelected[i]);
                 }
            }
        }

        private class AddFilter implements ActionListener {  //................
            public void actionPerformed(ActionEvent e) {
                String tab = (String)allTables.getSelectedItem();
                String col = (String)allColumns.getSelectedItem();
                String op   = (String)operations.getSelectedItem();
                String val  = (String)value.getText();

                StringBuffer sb = new StringBuffer();
                sb.append(tab);
                sb.append(".");
                sb.append(col);
                sb.append(" ");
                sb.append(op);
                sb.append(" ");
                sb.append(val);

//                filter.addItem(sb.toString());
                filterModel.addElement(sb.toString());

            }
        }

        private class RemoveFilter implements ActionListener {  //................
            public void actionPerformed(ActionEvent e) {
//                Object val = filter.getSelectedItem();
//                filter.removeItem(val);

                Object val = filterList.getSelectedValue();
                filterModel.removeElement(val.toString());

           }
        }

//......................................................................................//

        private class AddOption implements ActionListener {  //................
            public void actionPerformed(ActionEvent e) {
                String opt = optionalSQL.getText();
                if (opt != ""){
                    optionModel.addElement(opt);
                    optionalSQL.setText("");
                }
            }
        }

        private class RemoveOption implements ActionListener {  //................
            public void actionPerformed(ActionEvent e) {
                Object val = optionList.getSelectedValue();
                optionModel.removeElement(val.toString());
           }
        }

//......................................................................................//

        public void setInput(Object o, int i) {
            tableNameToSelectedColsLookup = new HashMap();
            availTablesModel.clear();
            availColumnsModel.clear();
            selectedTablesModel.clear();
            joinsModel.clear();   //.................
            filterModel.clear();  //.................


            dbc = (DBConnection)o;

//            dbc = (TestGui)o;
            String[] tbls = dbc.getTableNames();
            for(int id = 0; id < tbls.length; id++)
                //System.out.println(tbls[id]);
                availTablesModel.addElement(tbls[id]);
        }
    }
}