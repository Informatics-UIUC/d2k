package ncsa.d2k.modules.core.io.sql;

import  java.awt.*;
import  java.awt.event.*;
import  java.io.*;
import  java.text.*;
import  java.util.*;
import  javax.swing.*;
import  javax.swing.event.*;
import  ncsa.d2k.core.modules.*;
import  ncsa.d2k.gui.*;
//import ncsa.d2k.modules.core.datatype.*;
import  ncsa.d2k.modules.core.datatype.table.*;
import  ncsa.d2k.modules.core.vis.widgets.*;
import  ncsa.d2k.userviews.swing.*;
import  ncsa.gui.*;

import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

import java.sql.*;
import oracle.sql.*;
import oracle.jdbc.driver.*;

/**
 * This module does binning for database data
 */
public class SQLBinColumns extends UIModule {
    private static final String EMPTY = "",
    COLON = " : ", COMMA = ",", DOTS = "...",
    OPEN_PAREN = "(", CLOSE_PAREN = ")", OPEN_BRACKET = "[", CLOSE_BRACKET = "]";
    JOptionPane msgBoard = new JOptionPane();

    private NumberFormat nf;

    /**
     * put your documentation comment here
     * @return
     */
    public String getModuleName () {
        return  "SQLBinColumns";
    }

    /**
     * Get module information
     * @return A description for the module
     */
    public String getModuleInfo () {
        return  "<html>  <head>      </head>  <body>    Allows the user to interactively bin data.  </body></html>";
    }

    /**
     * Get the data types for the input parameters
     * @return Connection Wrapper, list of columns chosed, table name, where clause
     */
    public String[] getInputTypes () {
        String[] types =  {
            "ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
            "[Ljava.lang.String;",
            "java.lang.String",
            "java.lang.String"
        };
        return  types;
    }

    /**
     * Get the data types for the output parameters
     * @return A object of class BinTransform
     */
    public String[] getOutputTypes () {
        String[] types =  {
            "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform"
        };
        return  types;
    }

    /**
     * Get input information
     * @param i is the index of the input parameter
     * @return A description of the input parameter
     */
    public String getInputInfo (int i) {
        switch (i) {
            case 0:
                return "ConnectionWrapper";
            case 1:
                return "Field names.";
            case 2:
                return "Table name";
            case 3:
                return "Where clause";
            default:
                return  "No such input";
        }
    }

    /**
     * Get the name of the output parameters
     * @param i is the index of the output parameter
     * @return Name of the output parameter
     */
    public String getOutputName (int i) {
        switch (i) {
            case 0:
                return "BinningTransformation";
            default:
                return  "no such output!";
        }
    }

    /**
     * Get the name of the input parameter
     * @param i is the index of the input parameter
     * @return Name of the input parameter
     */
    public String getInputName (int i) {
        switch (i) {
            case 0:
                return "ConnectionWrapper";
            case 1:
                return "FieldName";
            case 2:
                return "TableName";
            case 3:
                return "WhereClause";
            default:
                return  "No such input";
        }
    }

    /**
     * Get output information
     * @param i is the index of the output parameter
     * @return A description of the output parameter
     */
    public String getOutputInfo (int i) {
        switch (i) {
            case 0:
                return  "A Table with its columns binned up.";
            default:
                return  "No such output";
        }
    }

    /**
     * Create a GUI interface
     * @return An object of SQLBinColumnsView
     */
    protected UserView createUserView () {
        return  new SQLBinColumnsView();
    }

    /**
     * Get a field name mapping
     * @return null
     */
    public String[] getFieldNameMapping () {
        return  null;
    }

    /**
     * A subclass of BinCounts
     * This class get count information from a database table
     */
    private class SQLBinCounts implements BinCounts {
        String[] fieldNames;
        String tableName;
        String whereClause;
        ConnectionWrapper wrapper;
        Connection con;
        Statement stmt;
        String queryStr;
        DatabaseMetaData metadata = null;


      SQLBinCounts(String tn, String[] fn, String wc, ConnectionWrapper cw) {
        tableName = tn;
        fieldNames = fn;
        whereClause = wc;
        wrapper = cw;
      }

      public boolean isColumnNumeric(int i) {
        try {
          String colName = fieldNames[i];
          con = wrapper.getConnection();
          metadata = con.getMetaData();
          String[] names = {"TABLE"};
          ResultSet tableNames = metadata.getTables(null,"%", tableName, names);
          while (tableNames.next()) {
            ResultSet columns = metadata.getColumns(null, "%", tableNames.getString("TABLE_NAME"), "%");
            while (columns.next()) {
              String columnName = columns.getString("COLUMN_NAME");
              String datatype = columns.getString("TYPE_NAME");
              if (colName.equals(columnName)) {
                if (datatype.equals("NUMBER") ||
                  datatype.equals("INTEGER") ||
                  datatype.equals("FLOAT") ||
                  datatype.equals("NUMERIC")) {
                  return true;
                }
                else
                  return false;
              }
            }
            return false;
          }
          return false;
        }
        catch (Exception e) {
	  JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occoured in isColumnNumeric.");
          return false;
        }
      }

      public double getMin(int col) {
        try {
          String colName = fieldNames[col];
          con = wrapper.getConnection();
          queryStr = "select min(" + colName + ") from " + tableName;
          System.out.println("queryStr for min is " + queryStr);
          stmt = con.createStatement();
          ResultSet minSet = stmt.executeQuery(queryStr);
          minSet.next();
          double val = minSet.getDouble(1);
          stmt.close();
          //return (minSet.getDouble(1));
          return(val);
        }
        catch (Exception e) {
	  JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occoured in getMin.");
          return 0;
        }
      }


      public double getMax(int col) {
        try {
          String colName = fieldNames[col];
          con = wrapper.getConnection();
          queryStr = "select max(" + colName + ") from " + tableName;
          stmt = con.createStatement();
          ResultSet maxSet = stmt.executeQuery(queryStr);
          maxSet.next();
          double val = maxSet.getDouble(1);
          stmt.close();
          //return (maxSet.getDouble(1));
          return (val);
        }
        catch (Exception e) {
	  JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occoured in getMax.");
          return 0;
        }
      }


      public int getNumRows() {
        try {
          String colName = fieldNames[0];
          con = wrapper.getConnection();
          queryStr = "select count(" + colName + ") from " + tableName;
          stmt = con.createStatement();
          ResultSet cntSet = stmt.executeQuery(queryStr);
          cntSet.next();
          int val = cntSet.getInt(1);
          stmt.close();
          //return (cntSet.getInt(1));
          return(val);
        }
        catch (Exception e) {
	  JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occoured in getNumRows.");
          return 0;
        }
      }

      public int[] getCounts(int col, double[] borders) {
        System.out.println("col is " + col);
        for(int z = 0; z < borders.length; z++)
            System.out.println("b["+z+"]: "+borders[z]);
        int[] counts = new int[borders.length+1];
        String colName = fieldNames[col];
        double low = 0;
        double high;
        try {
          con = wrapper.getConnection();
          for (int i = 0; i < (borders.length-1); i++) {
            high = borders[i];
            queryStr = "select count(" + colName + ") from " + tableName +
                       " where " + colName + " > " + low + " and " + colName +
                       " <= " + high;
            System.out.println("queryStr is " + queryStr);
            stmt = con.createStatement();
            ResultSet cntSet = stmt.executeQuery(queryStr);
            cntSet.next();
            counts[i] = cntSet.getInt(1);
            low = high;
            stmt.close();
          }
          queryStr = "select count(" + colName + ") from " + tableName +
                       " where " + colName + " > " + low;
          System.out.println("queryStr is " + queryStr);
          stmt = con.createStatement();
          ResultSet cntSet = stmt.executeQuery(queryStr);
          cntSet.next();
          counts[borders.length-1] = cntSet.getInt(1);
          stmt.close();

          for (int i=0; i<counts.length; i++) {
            System.out.println(i + " item is " + counts[i]);
          }
          return counts;
        }
        catch (Exception e) {
	  JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occoured in getCounts.");
          return counts;
        }
      }

      public double getTotal(int col) {
        try {
          String colName = fieldNames[col];
          con = wrapper.getConnection();
          queryStr = "select sum(" + colName + ") from " + tableName;
          stmt = con.createStatement();
          ResultSet totalSet = stmt.executeQuery(queryStr);
          totalSet.next();
          double val = totalSet.getDouble(1);
          stmt.close();
          //return (totalSet.getDouble(1));
          return(val);
        }
        catch (Exception e) {
	  JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occoured in getTotal.");
          return 0;
        }
      }
    }

    private class SQLBinColumnsView extends JUserPane {
        private boolean setup_complete;
        private BinDescriptor currentSelectedBin;
        private HashMap columnLookup;
        private HashSet[] uniqueColumnValues;
        private JList numericColumnLabels, textualColumnLabels, currentBins;
        private DefaultListModel binListModel;
        //private Table tbl;

        /* numeric text fields */
        private JTextField uRangeField, specRangeField, intervalField, weightField;

        /* textual lists */
        private JList textUniqueVals, textCurrentGroup;
        private DefaultListModel textUniqueModel, textCurrentModel;

        /* textual text field */
        private JTextField textBinName;

        /* current selection fields */
        private JTextField curSelName;
        private JList currentSelectionItems;
        private DefaultListModel currentSelectionModel;
        private JButton abort, done;
        private JCheckBox createInNewColumn;

        /**
         * GUI interface for binning data
         */
       private SQLBinColumnsView () {
            setup_complete = false;
            nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(3);
        }

        private int numArrived = 0;
        private ConnectionWrapper connectionWrapper;
        private String[] fieldNames;
        private String tableName;
        private String whereClause;
        private SQLBinCounts binCounts;
        private Connection con;
        private Statement stmt;
        private String queryStr;

        /**
         * put your documentation comment here
         * @param o
         * @param id
         */
        public void setInput (Object o, int id) {
            //tbl = (Table)o;

            if(id == 0) {
                connectionWrapper = (ConnectionWrapper)o;
                numArrived++;
            }
            if(id == 1) {
                fieldNames = (String[])o;
                numArrived++;
            }
            if(id == 2) {
                tableName = (String)o;
                numArrived++;
            }
            if(id == 3) {
                whereClause = (String)o;
                numArrived++;
            }

            if(numArrived == 4) {

                binCounts = new SQLBinCounts(tableName, fieldNames, whereClause, connectionWrapper);

            // clear all text fields and lists...
            curSelName.setText(EMPTY);
            textBinName.setText(EMPTY);
            uRangeField.setText(EMPTY);
            specRangeField.setText(EMPTY);
            intervalField.setText(EMPTY);
            weightField.setText(EMPTY);
            columnLookup = new HashMap();
            //uniqueColumnValues = new HashSet[tbl.getNumColumns()];
            uniqueColumnValues = new HashSet[fieldNames.length];
            binListModel.removeAllElements();
            DefaultListModel numModel = (DefaultListModel)numericColumnLabels.getModel(),
            txtModel = (DefaultListModel)textualColumnLabels.getModel();
            numModel.removeAllElements();
            txtModel.removeAllElements();
            //for (int i = 0; i < tbl.getNumColumns(); i++) {
            for (int i = 0; i < fieldNames.length; i++) {

                //columnLookup.put(tbl.getColumnLabel(i), new Integer(i));
                columnLookup.put(fieldNames[i], new Integer(i));
                //if (table.getColumn(i) instanceof NumericColumn)

                if (binCounts.isColumnNumeric(i))
                    //numModel.addElement(tbl.getColumnLabel(i));
                    numModel.addElement((String)fieldNames[i]);
                else {          //if (table.getColumn(i) instanceof TextualColumn) {
                    //txtModel.addElement(tbl.getColumnLabel(i));
                    txtModel.addElement((String)fieldNames[i]);
                    uniqueColumnValues[i] = uniqueValues(i);
                }
            }
            // finished...
            setup_complete = true;
            }
        }


        /**
         * Create all of the components and add them to the view.
         */
        public void initView (ViewModule m) {
            currentBins = new JList();
            binListModel = new DefaultListModel();
            currentBins.setModel(binListModel);
            currentBins.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            currentBins.addListSelectionListener(new CurrentListener());
            // set up the numeric tab
            numericColumnLabels = new JList();
            numericColumnLabels.setModel(new DefaultListModel());
            // uniform range
            JOutlinePanel urangepnl = new JOutlinePanel("Uniform Range");
            uRangeField = new JTextField(5);
            JButton addURange = new JButton("Add");
            addURange.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    addUniRange();
                    uRangeField.setText(EMPTY);
                }
            });
            JButton showURange = new JButton("Show");
            showURange.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    HashMap colLook = new HashMap();
                    for (int i = 0; i < /*tbl.getNumColumns()*/fieldNames.length; i++) {
                        if(/*tbl.*/binCounts.isColumnNumeric(i)) {
                            colLook.put(/*tbl.getColumnLabel(i)*/(String)fieldNames[i], new Integer(i));
                        }
                    }

                    String txt = uRangeField.getText();
                    if(txt != null && txt.length() != 0) {
                    String col = (String)numericColumnLabels.getSelectedValue();
                    final Histogram H = new UniformHistogram(binCounts,
                            uRangeField.getText(), colLook, col);
                    JD2KFrame frame = new JD2KFrame("Uniform Range");
                    frame.getContentPane().setLayout(new GridBagLayout());
                    Constrain.setConstraints(frame.getContentPane(), H, 0,
                            0, 3, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                            1, 1);
                    final JButton uniformAdd = new JButton("Add");
                    Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                            0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST,
                            .33, 0);
                    Constrain.setConstraints(frame.getContentPane(), uniformAdd,
                            1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                            .34, 0);
                    Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                            2, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHEAST,
                            .33, 0);
                    uniformAdd.addActionListener(new AbstractAction() {
                        final JSlider uniformSlider = H.getSlider();

                        public void actionPerformed (ActionEvent e) {
                            uRangeField.setText(Integer.toString(uniformSlider.getValue()));
                            numericColumnLabels.clearSelection();
                            setSelectedNumericIndex(H.getSelection());
                            addUniRange();
                            uRangeField.setText(EMPTY);
                        }
                    });
                    frame.pack();
                    frame.setVisible(true);
                    }
                    else {
                        // message dialog...must specify range
                        ErrorDialog.showDialog("Must specify range.", "Error");
                    }
                    /*
                     final JSlider uniformSlider = H.getSlider();
                     uniformSlider.addChangeListener(new ChangeListener() {
                     public void stateChanged(ChangeEvent e) {
                     uRangeField.setText(Integer.toString(uniformSlider.getValue()));
                     }
                     });
                     */

                }
            });
            urangepnl.setLayout(new GridBagLayout());
            Constrain.setConstraints(urangepnl, new JLabel("Number of Bins"),
                                     0, 0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            Box b = new Box(BoxLayout.X_AXIS);
            b.add(uRangeField);
            b.add(addURange);
            b.add(showURange);
            Constrain.setConstraints(urangepnl, b, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // specified range
            JOutlinePanel specrangepnl = new JOutlinePanel("Specified Range");
            specrangepnl.setLayout(new GridBagLayout());
            Constrain.setConstraints(specrangepnl, new JLabel("Range"), 0,
                                     0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            specRangeField = new JTextField(5);
            JButton addSpecRange = new JButton("Add");
            addSpecRange.addActionListener(new AbstractAction() {
                public void actionPerformed (ActionEvent e) {
                    addSpecifiedRange();
                    specRangeField.setText(EMPTY);
                }
            });
            JButton showSpecRange = new JButton("Show");
            showSpecRange.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    String txt = specRangeField.getText();
                    if(txt == null || txt.length() == 0) {
                        // show message dialog
                        ErrorDialog.showDialog("Must specify range.", "Error");
                        return;
                    }

                    HashMap colLook = new HashMap();
                    for (int i = 0; i < /*tbl.getNumColumns()*/fieldNames.length; i++) {
                        if(/*tbl.*/binCounts.isColumnNumeric(i)) {
                            colLook.put(/*tbl.getColumnLabel(i)*/(String)fieldNames[i], new Integer(i));
                        }
                    }
                    JD2KFrame frame = new JD2KFrame("Specified Range");
                    String col = (String)numericColumnLabels.getSelectedValue();
                    frame.getContentPane().add(new RangeHistogram(binCounts,
                            /*Histogram.HISTOGRAM_RANGE,*/ specRangeField.getText(), colLook, col));
                             frame.pack();
                             frame.setVisible(true);
                }
            });
            Box b1 = new Box(BoxLayout.X_AXIS);
            b1.add(specRangeField);
            b1.add(addSpecRange);
            b1.add(showSpecRange);
            Constrain.setConstraints(specrangepnl, b1, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // interval
            JOutlinePanel intervalpnl = new JOutlinePanel("Bin Interval");
            intervalpnl.setLayout(new GridBagLayout());
            intervalField = new JTextField(5);
            JButton addInterval = new JButton("Add");
            addInterval.addActionListener(new AbstractAction() {
                public void actionPerformed (ActionEvent e) {
                    addFromInterval();
                    intervalField.setText(EMPTY);
                }
            });
            JButton showInterval = new JButton("Show");
            showInterval.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    HashMap colLook = new HashMap();
                    for (int i = 0; i < /*tbl.getNumColumns()*/fieldNames.length; i++) {
                        if(/*tbl.*/binCounts.isColumnNumeric(i)) {
                            colLook.put(/*tbl.getColumnLabel(i)*/(String)fieldNames[i], new Integer(i));
                        }
                    }
                    String txt = intervalField.getText();
                    if(txt != null && txt.length() != 0) {
                        String col = (String)numericColumnLabels.getSelectedValue();
                    final Histogram H = new IntervalHistogram(binCounts,
                            /*Histogram.HISTOGRAM_INTERVAL,*/ intervalField.getText(), colLook, col);
                             JD2KFrame frame = new JD2KFrame("Bin Interval");
                             frame.getContentPane().setLayout(new GridBagLayout());
                             Constrain.setConstraints(frame.getContentPane(), H, 0,
                                     0, 3, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                                     1, 1);
                             final JButton intervalAdd = new JButton("Add");
                             Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                                     0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST,
                                     .33, 0);
                             Constrain.setConstraints(frame.getContentPane(), intervalAdd,
                                     1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                                     .34, 0);
                             Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                                     2, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHEAST,
                                     .33, 0);
                             intervalAdd.addActionListener(new AbstractAction() {
                                 final JSlider intervalSlider = H.getSlider();

                                 /**
                                  * put your documentation comment here
                                  * @param e
                                  */
                                 public void actionPerformed (ActionEvent e) {
                                     int sel = H.getSelection();
                                     numericColumnLabels.clearSelection();
                                     setSelectedNumericIndex(sel);
                                     double interval = getInterval();
                                     intervalField.setText(Double.toString(H.getPercentage()*interval));
                                     addFromInterval();
                                     intervalField.setText(EMPTY);
                                 }
                             });
                             frame.pack();
                             frame.setVisible(true);
                    }
                    else {
                        // message dialog...you must specify an interval
                        ErrorDialog.showDialog("Must specify interval.", "Error");
                    }
                             /*
                     final JSlider intervalSlider = H.getSlider();
                     intervalSlider.addChangeListener(new ChangeListener() {
                     public void stateChanged(ChangeEvent e) {
                     intervalField.setText(Integer.toString(intervalSlider.getValue()));
                     }
                     });
                     */

                }
            });
            Constrain.setConstraints(intervalpnl, new JLabel("Interval"), 0,
                                     0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            Box b2 = new Box(BoxLayout.X_AXIS);
            b2.add(intervalField);
            b2.add(addInterval);
            b2.add(showInterval);
            Constrain.setConstraints(intervalpnl, b2, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // uniform weight
            JOutlinePanel weightpnl = new JOutlinePanel("Uniform Weight");
            weightpnl.setLayout(new GridBagLayout());
            weightField = new JTextField(5);
            JButton addWeight = new JButton("Add");
            addWeight.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    addFromWeight();
                    weightField.setText(EMPTY);
                }
            });
            JButton showWeight = new JButton("Show");
            showWeight.setEnabled(false);
            Constrain.setConstraints(weightpnl, new JLabel("Number in each bin"),
                                     0, 0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            Box b3 = new Box(BoxLayout.X_AXIS);
            b3.add(weightField);
            b3.add(addWeight);
            b3.add(showWeight);
            Constrain.setConstraints(weightpnl, b3, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // add all numeric components
            JPanel numpnl = new JPanel();
            numpnl.setLayout(new GridBagLayout());
            JScrollPane jsp = new JScrollPane(numericColumnLabels);
            //jsp.setColumnHeaderView(new JLabel("Numeric Columns"));
            Constrain.setConstraints(numpnl, jsp, 0, 0, 4, 1, GridBagConstraints.BOTH,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, urangepnl, 0, 1, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, specrangepnl, 0, 2, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, intervalpnl, 0, 3, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, weightpnl, 0, 4, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            // textual bins
            JPanel txtpnl = new JPanel();
            txtpnl.setLayout(new GridBagLayout());
            textualColumnLabels = new JList();
            textualColumnLabels.addListSelectionListener(new TextualListener());
            textualColumnLabels.setModel(new DefaultListModel());
            textUniqueVals = new JList();
            textUniqueModel = new DefaultListModel();
            textUniqueVals.setModel(textUniqueModel);
            textUniqueVals.setFixedCellWidth(100);
            textCurrentGroup = new JList();
            textCurrentGroup.setFixedCellWidth(100);
            textCurrentModel = new DefaultListModel();
            textCurrentGroup.setModel(textCurrentModel);
            JButton addTextToGroup = new JButton(">");
            addTextToGroup.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    Object[] sel = textUniqueVals.getSelectedValues();
                    for (int i = 0; i < sel.length; i++) {
                        textUniqueModel.removeElement(sel[i]);
                        textCurrentModel.addElement(sel[i]);
                    }
                }
            });
            JButton removeTextFromGroup = new JButton("<");
            removeTextFromGroup.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    Object[] sel = textCurrentGroup.getSelectedValues();
                    for (int i = 0; i < sel.length; i++) {
                        textCurrentModel.removeElement(sel[i]);
                        textUniqueModel.addElement(sel[i]);
                    }
                }
            });
            JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);
            jtp.add(numpnl, "Scalar");
            jtp.add(txtpnl, "Nominal");
            Box bx = new Box(BoxLayout.Y_AXIS);
            bx.add(Box.createGlue());
            bx.add(addTextToGroup);
            bx.add(removeTextFromGroup);
            bx.add(Box.createGlue());
            Box bx1 = new Box(BoxLayout.X_AXIS);
            JScrollPane jp1 = new JScrollPane(textUniqueVals);
            jp1.setColumnHeaderView(new JLabel("Unique Values"));
            bx1.add(jp1);
            bx1.add(Box.createGlue());
            bx1.add(bx);
            bx1.add(Box.createGlue());
            JScrollPane jp2 = new JScrollPane(textCurrentGroup);
            jp2.setColumnHeaderView(new JLabel("Current Group"));
            bx1.add(jp2);
            textBinName = new JTextField(10);
            JButton addTextBin = new JButton("Add");
            addTextBin.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    Object[] sel = textCurrentModel.toArray();
                    Object val = textualColumnLabels.getSelectedValue();
                    int idx = ((Integer)columnLookup.get(val)).intValue();
                    BinDescriptor bd = createTextualBin(idx, textBinName.getText(),
                            sel);
                    HashSet set = uniqueColumnValues[idx];
                    for (int i = 0; i < sel.length; i++) {
                        textCurrentModel.removeElement(sel[i]);
                        set.remove(sel[i]);
                    }
                    addItemToBinList(bd);
                    textBinName.setText(EMPTY);
                }
            });
            JOutlinePanel jop = new JOutlinePanel("Group");
            JPanel pp = new JPanel();
            pp.add(new JLabel("Name"));
            pp.add(textBinName);
            pp.add(addTextBin);
            jop.setLayout(new BoxLayout(jop, BoxLayout.Y_AXIS));
            jop.add(bx1);
            jop.add(pp);
            JScrollPane jp3 = new JScrollPane(textualColumnLabels);
            Constrain.setConstraints(txtpnl, jp3, 0, 0, 4, 1, GridBagConstraints.BOTH,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(txtpnl, jop, 0, 1, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            // now add everything
            JPanel pq = new JPanel();
            pq.setLayout(new BorderLayout());
            JScrollPane jp4 = new JScrollPane(currentBins);
            jp4.setColumnHeaderView(new JLabel("Current Bins"));
            pq.add(jp4, BorderLayout.CENTER);
            JOutlinePanel jop5 = new JOutlinePanel("Current Selection");
            currentSelectionItems = new JList();
            currentSelectionItems.setVisibleRowCount(4);
            currentSelectionItems.setEnabled(false);
            currentSelectionModel = new DefaultListModel();
            currentSelectionItems.setModel(currentSelectionModel);
            JPanel pt = new JPanel();
            curSelName = new JTextField(10);
            pt.add(new JLabel("Name"));
            pt.add(curSelName);
            JButton updateCurrent = new JButton("Update");
            updateCurrent.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    if (currentSelectedBin != null) {
                        currentSelectedBin.name = curSelName.getText();
                        currentBins.repaint();
                    }
                }
            });
            JButton removeBin = new JButton("Remove Bin");
            removeBin.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    if (currentSelectedBin != null) {
                        int col = currentSelectedBin.column_number;
                        if (currentSelectedBin instanceof TextualBinDescriptor)
                            uniqueColumnValues[col].addAll(((TextualBinDescriptor)currentSelectedBin).vals);
                        binListModel.removeElement(currentSelectedBin);
                        currentSelectionModel.removeAllElements();
                        curSelName.setText(EMPTY);
                        // update the group
                        Object lbl = textualColumnLabels.getSelectedValue();
                        // gpape:
                        if (lbl != null) {
                            int idx = ((Integer)columnLookup.get(lbl)).intValue();
                            HashSet unique = uniqueColumnValues[idx];
                            textUniqueModel.removeAllElements();
                            textCurrentModel.removeAllElements();
                            Iterator i = unique.iterator();
                            while (i.hasNext())
                                textUniqueModel.addElement(i.next());
                        }
                    }
                }
            });
            // gpape:
            JButton removeAllBins = new JButton("Remove All");
            removeAllBins.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    binListModel.removeAllElements();
                    currentSelectionModel.removeAllElements();
                    curSelName.setText(EMPTY);
                }
            });
            // gpape:
            createInNewColumn = new JCheckBox("Create in new column", false);
            Box pg = new Box(BoxLayout.X_AXIS);
            pg.add(updateCurrent);
            //pg.add(removeItems);
            pg.add(removeBin);
            pg.add(removeAllBins);
            // gpape:
            Box pg2 = new Box(BoxLayout.X_AXIS);
            pg2.add(createInNewColumn);
            jop5.setLayout(new BoxLayout(jop5, BoxLayout.Y_AXIS));
            jop5.add(pt);
            JScrollPane pane = new JScrollPane(currentSelectionItems);
            pane.setColumnHeaderView(new JLabel("Items"));
            jop5.add(pane);
            jop5.add(pg);
            jop5.add(pg2);
            JPanel bgpnl = new JPanel();
            bgpnl.setLayout(new BorderLayout());
            bgpnl.add(jp4, BorderLayout.CENTER);
            bgpnl.add(jop5, BorderLayout.SOUTH);
            // finally add everything to this
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            Box bxl = new Box(BoxLayout.X_AXIS);
            bxl.add(jtp);
            bxl.add(bgpnl);
            JPanel buttonPanel = new JPanel();
            abort = new JButton("Abort");
            abort.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    viewCancel();
                }
            });
            done = new JButton("Done");
            done.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    //binIt(createInNewColumn.isSelected());
                    Object[] tmp = binListModel.toArray();
                    BinDescriptor[] bins = new BinDescriptor[tmp.length];
                    for (int i = 0; i < bins.length; i++)
                        bins[i] = (BinDescriptor)tmp[i];
                    BinTransform bt = new BinTransform(bins, createInNewColumn.isSelected());
                    pushOutput(bt, 0);
                    //pushOutput(tbl, 1);
                    viewDone("Done");
                }
            });
            JButton helpButton = new JButton("Help");
            helpButton.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    HelpWindow help = new HelpWindow();
                    help.setVisible(true);
                }
            });
            buttonPanel.add(abort);
            buttonPanel.add(done);
            //buttonPanel.add(showTable);
            buttonPanel.add(helpButton);
            setLayout(new BorderLayout());
            add(bxl, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        /*private void binIt(boolean new_column) {
        // boolean replace = true;
         Object[] tmp = binListModel.toArray();
         BinDescriptor[] bins = new BinDescriptor[tmp.length];
         for (int i = 0; i < bins.length; i++)
         bins[i] = (BinDescriptor)tmp[i];
        // need to figure out which columns have been binned:
         boolean[] binRelevant = new boolean[table.getNumColumns()];
         for (int i = 0; i < binRelevant.length; i++)
         binRelevant[i] = false;
         for (int i = 0; i < bins.length; i++)
         binRelevant[bins[i].column_number] = true;
        // if (replace) {
        //Column[] c = new Column[table.getNumColumns()];
        //for(int i = 0; i < c.length; i++)
        //  c[i] = table.getColumn(i);
         String[][] newcols =
         new String[table.getNumColumns()][table.getNumRows()];
         for(int i = 0; i < table.getNumColumns(); i++) {
         if (binRelevant[i])
         for(int j = 0; j < table.getNumRows(); j++) {
        // find the correct bin for this column
         boolean binfound = false;
         for(int k = 0; k < bins.length; k++) {
         if(bins[k].column_number == i) {
        // this has the correct column index
        //if(c[i] instanceof NumericColumn) {
         if(table.isColumnNumeric(i)) {
         if(bins[k].eval(table.getDouble(j, i))) {
         newcols[i][j] = bins[k].name;
         binfound = true;
         }
         }
         else {
         if(bins[k].eval(table.getString(j, i))) {
         newcols[i][j] = bins[k].name;
         binfound = true;
         }
         }
         }
         if(binfound) {
         binRelevant[i] = true;
         break;
         }
         }
         if(!binfound)
         newcols[i][j] = "Unknown";
         }
         }
        //StringColumn[] sc = new StringColumn[table.getNumColumns()];
         for(int i = 0; i < table.getNumColumns(); i++) {
         if (binRelevant[i]) {
        //sc[i] = new StringColumn(newcols[i]);
        //sc[i].setComment(table.getColumn(i).getComment());
         if (new_column) {
         if (binRelevant[i]) {
        //sc[i].setLabel(table.getColumnLabel(i) + " bin");
         table.addColumn(newcols[i]);
         table.setColumnLabel(table.getColumnLabel(i)+" bin", table.getNumColumns()-1);
         }
         }
         else {
         String oldLabel = table.getColumnLabel(i);
         table.setColumn(newcols[i], i);
         table.setColumnLabel(oldLabel, i);
         }
         }
         }
        // }
         }*/

        private HashSet uniqueValues (int col) {
            // count the number of unique items in this column
            HashSet set = new HashSet();
            /*for (int i = 0; i < tbl.getNumRows(); i++) {
                String s = tbl.getString(i, col);
                if (!set.contains(s))
                    set.add(s);
            }*/
          try {
            String colName = fieldNames[col];
            con = connectionWrapper.getConnection();
            queryStr = "select distinct " + colName + " from " + tableName;
            stmt = con.createStatement();
            ResultSet distinctSet = stmt.executeQuery(queryStr);
            while (distinctSet.next()) {
              set.add(distinctSet.getString(1));
            }
            stmt.close();
            return set;
          }
          catch (Exception e) {
	    JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occoured in uniqueValues.");
            return null;
          }
        }

        /**
         * Get the column indices of the selected numeric columns.
         */
        private int[] getSelectedNumericIndices () {
            Object[] setVals = numericColumnLabels.getSelectedValues();
            int[] colIdx = new int[setVals.length];
            for (int i = 0; i < colIdx.length; i++)
                colIdx[i] = ((Integer)columnLookup.get(setVals[i])).intValue();
            return  colIdx;
        }

        /**
         * put your documentation comment here
         * @param index
         */
        private void setSelectedNumericIndex (int index) {
            numericColumnLabels.setSelectedIndex(index);
        }

        /**
         * Get the range of the first selected column.
         */
        private double getInterval () {
            int colIdx = numericColumnLabels.getSelectedIndex();
            /*double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
            for (int i = 0; i < tbl.getNumRows(); i++) {
                double d = tbl.getDouble(i, colIdx);
                if (d < min)
                    min = d;
                if (d > max)
                    max = d;
            }*/
            double max = binCounts.getMax(colIdx);
            double min = binCounts.getMin(colIdx);
            return  max - min;
        }

        /**
         * Add uniform range bins
         */
        private void addUniRange () {
            int[] colIdx = getSelectedNumericIndices();

            // uniform range is the number of bins...
            String txt = uRangeField.getText();
            int num;
            // ...get this number
            try {
                num = Integer.parseInt(txt);
            } catch (NumberFormatException e) {
                return;
            }
            double[] maxes = new double[fieldNames.length];
            double[] mins = new double[fieldNames.length];
            for (int i = 0; i < fieldNames.length; i++) {
              for (int j = 0; j < colIdx.length; j++) {
                // if the column is selected, then find the max and min
                if (i == colIdx[j]) {
                  maxes[i] = binCounts.getMax(i);
                  mins[i] = binCounts.getMin(i);
                }
              }
            }
            for (int i = 0; i < fieldNames.length; i++) {
              for (int j = 0; j < colIdx.length; j++) {
                if (i == colIdx[j]) {
                  double[] binMaxes = new double[num - 1];
                  double interval = (maxes[i] - mins[i])/(double)num;
                  // add the first bin manually
                  binMaxes[0] = mins[i] + interval;
                  BinDescriptor nbd = createMinNumericBinDescriptor(i,
                        binMaxes[0]);
                  addItemToBinList(nbd);
                  for (int k = 1; k < binMaxes.length; k++) {
                    binMaxes[k] = binMaxes[k - 1] + interval;
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(i, binMaxes[k -1], binMaxes[k]);
                    addItemToBinList(nbd);
                  }
                  nbd = createMaxNumericBinDescriptor(i, binMaxes[binMaxes.length
                        - 1]);
                  addItemToBinList(nbd);
                }
              }
            }
        }

        /**
         * Add bins from a user-specified range
         */
        private void addSpecifiedRange () {
            int[] colIdx = getSelectedNumericIndices();
            // specified range is a comma-separated list of bin maxes
            String txt = specRangeField.getText();
            ArrayList al = new ArrayList();
            StringTokenizer strTok = new StringTokenizer(txt, COMMA);
            double[] binMaxes = new double[strTok.countTokens()];
            int idx = 0;
            try {
                while (strTok.hasMoreElements()) {
                    String s = (String)strTok.nextElement();
                    binMaxes[idx++] = Double.parseDouble(s);
                }
            } catch (NumberFormatException e) {
                return;
            }
            // now create and add the bins
            for (int i = 0; i < fieldNames.length; i++) {
              for (int j = 0; j < colIdx.length; j++) {
                if (i == colIdx[j]) {
                  BinDescriptor nbd = createMinNumericBinDescriptor(i,
                      binMaxes[0]);
                  addItemToBinList(nbd);
                  for (int k = 1; k < binMaxes.length; k++) {
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(i, binMaxes[k - 1], binMaxes[k]);
                    addItemToBinList(nbd);
                }
                nbd = createMaxNumericBinDescriptor(i, binMaxes[binMaxes.length
                        - 1]);
                addItemToBinList(nbd);
                }
              }
            }
        }

        /**
         * Add bins from an interval - the width of each bin
         */
        private void addFromInterval () {
            int[] colIdx = getSelectedNumericIndices();
            // the interval is the width
            String txt = intervalField.getText();
            double intrval;
            try {
                intrval = Double.parseDouble(txt);
            } catch (NumberFormatException e) {
                return;
            }

            // find the mins and maxes
            double[] maxes = new double[fieldNames.length];
            double[] mins = new double[fieldNames.length];

            for (int i = 0; i < fieldNames.length; i++) {
              for (int j = 0; j < colIdx.length; j++) {
                // if the column is selected, then find the max and min
                if (i == colIdx[j]) {
                  maxes[i] = binCounts.getMax(i);
                  mins[i] = binCounts.getMin(i);
                }
              }
            }
            for (int i = 0; i < fieldNames.length; i++) {
              for (int j = 0; j < colIdx.length; j++) {
                if (i == colIdx[j]) {
                  // the number of bins is (max - min) / (bin width)
                  int num = (int)Math.ceil((maxes[i] - mins[i])/intrval);
                  double[] binMaxes = new double[num-1];
                  binMaxes[0] = mins[i] + intrval;
                  // add the first bin manually
                  BinDescriptor nbd = createMinNumericBinDescriptor(i,
                        binMaxes[0]);
                  addItemToBinList(nbd);
                  for (int k = 1; k < binMaxes.length; k++) {
                    binMaxes[k] = binMaxes[k - 1] + intrval;
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(i, binMaxes[k - 1], binMaxes[k]);
                    addItemToBinList(nbd);
                  }
                  nbd = createMaxNumericBinDescriptor(i, binMaxes[binMaxes.length
                        - 1]);
                  addItemToBinList(nbd);
                }
              }
            }
        }

        /**
         * Add bins given a weight. This will attempt to make bins with an
         * equal number of tallies in each.
         *
         */
        private void addFromWeight () {
            int[] colIdx = getSelectedNumericIndices();
            // the weight is the number of items in each bin
            String txt = weightField.getText();
            int weight;
            try {
                weight = Integer.parseInt(txt);
            }
            catch (NumberFormatException e) {
                return;
            }
            for (int i = 0; i < colIdx.length; i++) {
              try {
                Double db1 = null;
                ArrayList list = new ArrayList();
                int aColIdx = colIdx[i];
                String colName = fieldNames[aColIdx];
                con = connectionWrapper.getConnection();
                queryStr = "select " + colName + ", count(" + colName + ") from " +
                           tableName + " group by " + colName;
                stmt = con.createStatement();
                ResultSet groupSet = stmt.executeQuery(queryStr);
                int itemCnt = 0;
                while (groupSet.next()) {
                  itemCnt += groupSet.getInt(2);
                  db1 = new Double(groupSet.getDouble(1));
                  if (itemCnt >= (weight - 1)) {
                    // itemCnt >= specified weight, add the value to the list
                    list.add(db1);
                    // reset itemCnt
                    itemCnt = 0;
                  }
                }
                // put anything left in a bin
                if (itemCnt > 0)
                  list.add(db1);

                double[] binMaxes = new double[list.size()];
                for (int j = 0; j < binMaxes.length; j++)
                    binMaxes[j] = ((Double)list.get(j)).doubleValue();
                // add the first bin manually
                BinDescriptor nbd = createMinNumericBinDescriptor(colIdx[i],
                        binMaxes[0]);
                addItemToBinList(nbd);
                for (int j = 1; j < binMaxes.length; j++) {
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(colIdx[i], binMaxes[j -
                    1], binMaxes[j]);
                    addItemToBinList(nbd);
                }
                stmt.close();
              }
              catch (Exception e) {
	          JOptionPane.showMessageDialog(msgBoard,
                    e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
                  System.out.println("Error occoured in addFromWeight.");
              }
            }
        }

        /**
         * put your documentation comment here
         * @param idx
         * @param name
         * @param sel
         * @return
         */
        private BinDescriptor createTextualBin (int idx, String name, Object[] sel) {
            String[] vals = new String[sel.length];
            for (int i = 0; i < vals.length; i++)
                vals[i] = sel[i].toString();
            return  new TextualBinDescriptor(idx, name, vals, /*tbl.getColumnLabel(idx)*/(String)fieldNames[idx]);
        }

        /**
         * Create a numeric bin that goes from min to max.
         */
        private BinDescriptor createNumericBinDescriptor (int col, double min,
                double max) {
            StringBuffer nameBuffer = new StringBuffer();
            nameBuffer.append(OPEN_PAREN);
            nameBuffer.append(nf.format(min));
            nameBuffer.append(COLON);
            nameBuffer.append(nf.format(max));
            nameBuffer.append(CLOSE_BRACKET);
            BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
                    min, max, /*tbl.getColumnLabel(col)*/(String)fieldNames[col]);
            return  nb;
        }

        /**
         * Create a numeric bin that goes from Double.MIN_VALUE to max
         */
        private BinDescriptor createMinNumericBinDescriptor (int col, double max) {
            StringBuffer nameBuffer = new StringBuffer();
            nameBuffer.append(OPEN_BRACKET);
            nameBuffer.append(DOTS);
            nameBuffer.append(COLON);
            nameBuffer.append(nf.format(max));
            nameBuffer.append(CLOSE_BRACKET);
            BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
                    Double.MIN_VALUE, max, /*tbl.getColumnLabel(col)*/(String)fieldNames[col]);
            return  nb;
        }

        /**
         * Create a numeric bin that goes from min to Double.MAX_VALUE
         */
        private BinDescriptor createMaxNumericBinDescriptor (int col, double min) {
            StringBuffer nameBuffer = new StringBuffer();
            nameBuffer.append(OPEN_PAREN);
            nameBuffer.append(nf.format(min));
            nameBuffer.append(COLON);
            nameBuffer.append(DOTS);
            nameBuffer.append(CLOSE_BRACKET);
            BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
                    min, Double.MAX_VALUE, /*tbl.getColumnLabel(col)*/(String)fieldNames[col]);
            return  nb;
        }

        /**
         * put your documentation comment here
         * @param bd
         */
        private void addItemToBinList (BinDescriptor bd) {
            binListModel.addElement(bd);
        }

        private class CurrentListener
                implements ListSelectionListener {

            /**
             * put your documentation comment here
             * @param e
             */
            public void valueChanged (ListSelectionEvent e) {
                if (!setup_complete)
                    return;
                if (!e.getValueIsAdjusting()) {
                    currentSelectedBin = (BinDescriptor)currentBins.getSelectedValue();
                    if (currentSelectedBin == null) {
                        currentSelectionModel.removeAllElements();
                        curSelName.setText(EMPTY);
                        return;
                    }
                    curSelName.setText(currentSelectedBin.name);
                    if (currentSelectedBin instanceof NumericBinDescriptor) {
                        currentSelectionModel.removeAllElements();
                        currentSelectionModel.addElement(currentSelectedBin.name);
                    }
                    else {
                        currentSelectionModel.removeAllElements();
                        HashSet hs = (HashSet)((TextualBinDescriptor)currentSelectedBin).vals;
                        Iterator i = hs.iterator();
                        while (i.hasNext())
                            currentSelectionModel.addElement(i.next());
                    }
                }
            }
        }       // BinColumnsView$CurrentListener

        private class TextualListener
                implements ListSelectionListener {

            /**
             * put your documentation comment here
             * @param e
             */
            public void valueChanged (ListSelectionEvent e) {
                if (!setup_complete)
                    return;
                if (!e.getValueIsAdjusting()) {
                    Object lbl = textualColumnLabels.getSelectedValue();
                    if (lbl != null) {
                        int idx = ((Integer)columnLookup.get(lbl)).intValue();
                        HashSet unique = uniqueColumnValues[idx];
                        textUniqueModel.removeAllElements();
                        textCurrentModel.removeAllElements();
                        Iterator i = unique.iterator();
                        while (i.hasNext())
                            textUniqueModel.addElement(i.next());
                    }
                }
            }
        }       // BinColumnsView$TextualListener

        private class HelpWindow extends JD2KFrame {

            /**
             * put your documentation comment here
             */
            public HelpWindow () {
                super("About BinColumns");
                JEditorPane ep = new JEditorPane("text/html", getHelpString());
                ep.setCaretPosition(0);
                getContentPane().add(new JScrollPane(ep));
                setSize(400, 400);
            }
        }

        /**
         * put your documentation comment here
         * @return
         */
        private String getHelpString () {
            StringBuffer sb = new StringBuffer();
            sb.append("<html><body><h2>BinColumns</h2>");
            sb.append("This module allows a user to interactively bin data from a table. ");
            sb.append("Numeric data can be binned in four ways:<ul>");
            sb.append("<li><b>Uniform range</b><br>");
            sb.append("Enter a positive integer value for the number of bins. BinColumns will ");
            sb.append("divide the binning range evenly over these bins.");
            sb.append("<br><li><b>Specified range</b>:<br>");
            sb.append("Enter a comma-separated sequence of integer or floating-point values ");
            sb.append("for the endpoints of each bin.");
            sb.append("<br><li><b>Bin Interval</b>:<br>");
            sb.append("Enter an integer or floating-point value for the width of each bin.");
            sb.append("<br><li><b>Uniform Weight</b>:<br>");
            sb.append("Enter a positive integer value for even binning with that number in each bin.");
            sb.append("</ul>");
            sb.append("To bin numeric data, select columns from the ``Numeric Columns'' ");
            sb.append("selection area (top left) and select a binning method by entering a value ");
            sb.append("in the corresponding text field and clicking ``Add''. Data can ");
            sb.append("alternately be previewed in histogram form by clicking the corresponding ");
            sb.append("``Show'' button (this accepts, but does not require, a value in the text field). ");
            sb.append("Uniform weight binning has no histogram (it would always look the same).");
            sb.append("<br><br>To bin textual data, click the ``Textual'' tab (top left) to bring ");
            sb.append("up the ``Textual Columns'' selection area. Click on a column to show a list ");
            sb.append("of unique textual values in that column in the ``Unique Values'' area below. ");
            sb.append("Select one or more of these values and click the right arrow button to group ");
            sb.append("these values. They can then be assigned a collective name as before.");
            sb.append("<br><br>To assign a name to a particular bin, select that bin in ");
            sb.append("the ``Current Bins'' selection area (top right), enter a name in ");
            sb.append("the ``Name'' field below, and click ``Update''. To bin the data and ");
            sb.append("output the new table, click ``Done''.");
            sb.append("</body></html>");
            return  sb.toString();
        }
    }           // BinColumnsView
}
