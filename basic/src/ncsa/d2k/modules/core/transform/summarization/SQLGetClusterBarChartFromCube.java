package ncsa.d2k.modules.core.transform.summarization;

/**
 * <p>Title: SQLGetClusterBarChartFromCube </p>
 * <p>Description: Extract rule association from a cube table </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */


import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

public class SQLGetClusterBarChartFromCube extends UIModule {
  JOptionPane msgBoard = new JOptionPane();
  ConnectionWrapper cw;
  String cube;
  Connection con;
  static String NOTHING = "";

  // variables for codebook
  TableImpl codeTable;
  SQLCodeBook aBook;
  JPanel codeBookPanel;
  CardLayout codeBookLayout;
  JTextField bookName;
  JLabel bookLabel;
  Checkbox useCodeBook;
  JButton bookBrowseBtn;
  private static String BLANK = "NoCodeBook";
  private static String FILLED = "WithCodeBook";


  JTextField cubeTableName;
  JList selectedFields;
  JList possibleFields;
  DefaultListModel possibleModel;
  DefaultListModel selectedModel;
  JButton cancelBtn;
  JButton displayBtn;

  BrowseTables bt;
  BrowseTablesView btw;
  int featureIdx = -1;
  int colCnt;
  TableImpl data;

  public SQLGetClusterBarChartFromCube() {
  }

  public String getOutputInfo (int i) {
                switch (i) {
                        case 0: return "A table to visualize.";
                        default: return "No such output";
                }
        }

  public String getInputInfo (int i) {
                switch (i) {
                        case 0: return "JDBC data source to make database connection.";
                        case 1: return "The name of the cube table which stores the data statistics.";
                        default: return "No such input";
                }
        }

  public String getModuleInfo () {
        String s = "<p> Overview: ";
          s += "This module displays a cluster bar chart for data in a cube table. </p>";
          s += "<p> Detailed Description: ";
          s += "This module first makes a connection to a database and retrieves the ";
          s += "data from two user-selected columns in a cube table, then displays the statistic data ";
          s += "using a cluster bar chart. The displayed data not only can be labeled in predefined ";
          s += "codes, but also in detailed descriptions by choosing the 'Use Code Book' ";
          s += "option and specifying a code book for use. ";
          s += "<p> Restrictions: ";
          s += "We currently only support Oracle and SQLServer databases.";
          return s;
  }

  public String[] getInputTypes () {
                String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.lang.String"};
                return types;
        }

  public String[] getOutputTypes () {
                String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
                return types;
  }

  protected String[] getFieldNameMapping () {
    return null;
  }

  /**
    Create the UserView object for this module-view combination.
    @return The UserView associated with this module.
  */
  protected UserView createUserView() {
    return new GetStatView();
  }

  public class GetStatView extends JUserPane
    implements ActionListener, ItemListener {

    public void setInput(Object input, int index) {
      if (index == 0) {
        cw = (ConnectionWrapper)input;
      }
      else if (index == 1) {
        cube = (String)input;
        doGUI();
        cubeTableName.setText((String)input);
      }
    }

    public Dimension getPreferredSize() {
      return new Dimension (450, 300);
    }

    public void initView(ViewModule mod) {
      removeAll();
    }

    public void doGUI() {
      removeAll();
      //cw = (ConnectionWrapper)pullInput (0);

      selectedFields = new JList ();
      possibleFields = new JList ();
      JButton add = new JButton ("Add");
      JButton remove = new JButton ("Remove");
      possibleModel = new DefaultListModel();
      selectedModel = new DefaultListModel();

      // Panel to hold outline panels
      JPanel getStatPanel = new JPanel();
      getStatPanel.setLayout (new GridBagLayout());

      // panel for table name
      JPanel options = new JPanel();
      options.setLayout (new GridBagLayout());
      Constrain.setConstraints(options, new JLabel("Table Name"),
        0,0,5,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(options, cubeTableName = new JTextField(10),
        5,0,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      //cubeTableName.setText((String)pullInput(1));
      cubeTableName.setText(cube);
      cubeTableName.setEditable(false);
      doColumnBrowse();

      JPanel buttons = new JPanel ();
      buttons.setLayout (new GridLayout(6,1));
      buttons.add (add);
      buttons.add (remove);

      JPanel b1 = new JPanel();
      b1.add(buttons);

      // the add button moves stuff from the possible list
      // to the selected list.
      add.addActionListener (new AbstractAction() {
        public void actionPerformed (ActionEvent e) {
          Object[] sel = possibleFields.getSelectedValues();
          for(int i = 0; i < sel.length; i++) {
            possibleModel.removeElement(sel[i]);
            selectedModel.addElement(sel[i]);
          }
        }
      });

      // the remove button moves stuff from the selected list
      // to the possible list.
      remove.addActionListener (new AbstractAction () {
        public void actionPerformed (ActionEvent e) {
          Object[] sel = selectedFields.getSelectedValues();
          for(int i = 0; i < sel.length; i++) {
            selectedModel.removeElement(sel[i]);
            possibleModel.addElement(sel[i]);
          }
        }
      });

      JPanel featureArea = new JPanel();
      featureArea.setLayout (new BorderLayout ());

      possibleFields.setFixedCellWidth(150);
      //possibleFields.setFixedCellHeight(100);
      selectedFields.setFixedCellWidth(150);
      //selectedFields.setFixedCellHeight(100);
      selectedFields.setModel(selectedModel);
      possibleFields.setModel(possibleModel);

      JScrollPane jsp = new JScrollPane(possibleFields);
      jsp.setColumnHeaderView(new JLabel("Possible Fields"));
      JScrollPane jsp1 = new JScrollPane(selectedFields);
      jsp1.setColumnHeaderView(new JLabel("Selected Fields"));

      featureArea.add (b1, BorderLayout.CENTER);
      featureArea.add (jsp, BorderLayout.WEST);
      featureArea.add (jsp1, BorderLayout.EAST);

      JPanel options2 = new JPanel();
      options2.setLayout (new GridBagLayout());
      useCodeBook = new Checkbox ( "Use Code Book", null, false);
      useCodeBook.addItemListener( this );
      Constrain.setConstraints(options2, useCodeBook,
        0,0,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1,1);

      codeBookPanel = new JPanel();
      codeBookLayout = new CardLayout();
      codeBookPanel.setLayout(codeBookLayout);

      JPanel filledPanel = new JPanel();
      filledPanel.setLayout (new GridBagLayout());
      Constrain.setConstraints(filledPanel, bookLabel = new JLabel("Code Book Name"),
        0,0,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(filledPanel, bookName = new JTextField(10),
        5,0,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      bookName.setText(NOTHING);
      bookName.addActionListener(this);
      Constrain.setConstraints(filledPanel, bookBrowseBtn = new JButton ("Browse"),
        15,0,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
      bookBrowseBtn.addActionListener(this);
      codeBookPanel.add(filledPanel, FILLED);

      JPanel blankPanel = new JPanel();
      blankPanel.setLayout (new GridBagLayout());
      Constrain.setConstraints(blankPanel, new JPanel(),
        0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      codeBookPanel.add(blankPanel, BLANK);
      codeBookLayout.show(codeBookPanel, BLANK);

      Constrain.setConstraints(options2, codeBookPanel,
        5,0,15,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1,1);

      /* Add the outline panel to getRulePanel */
      Constrain.setConstraints(getStatPanel, options,
        0,0,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(getStatPanel, featureArea,
        0,1,5,5,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(getStatPanel, options2,
        0,7,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(getStatPanel, cancelBtn = new JButton (" Abort "),
        2,8,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
      cancelBtn.addActionListener(this);
      Constrain.setConstraints(getStatPanel, displayBtn = new JButton ("Display"),
        3,8,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      displayBtn.addActionListener(this);
      setLayout (new BorderLayout());
      add(getStatPanel, BorderLayout.NORTH);
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == displayBtn) {
        Object[] values = selectedModel.toArray();
        String[] retVal = new String[values.length];
        if (useCodeBook.getState() && bookName.getText().length()<=0) {
          // The user has not chosen a code book yet
          JOptionPane.showMessageDialog(msgBoard,
            "You must choose a code book or deselect 'Use Code Book'.", "Error",
            JOptionPane.ERROR_MESSAGE);
          System.out.println("There is no code book selected.");
        }
        else if (retVal.length != 2) {
          JOptionPane.showMessageDialog(msgBoard,
          "You must choose two features from the selected table.", "Error",
          JOptionPane.ERROR_MESSAGE);
          System.out.println(retVal.length + " are selected. You must choose two features.");
        }
        else if (cubeTableName.getText().length()<=0) { // The user has not chosen a table yet
          JOptionPane.showMessageDialog(msgBoard,
          "There is no cube table selected.", "Error",
          JOptionPane.ERROR_MESSAGE);
          System.out.println("There is no table selected.");
        }
        else if (cubeTableName.getText().toString().indexOf("_CUBE")<0) {
          JOptionPane.showMessageDialog(msgBoard,
          "To display a chart, you must select a cube table rather than a data table.", "Error",
          JOptionPane.ERROR_MESSAGE);
          System.out.println("A cube table is selected instead of a data table.");
        }
        else if (cubeTableName.getText().length()>0 && retVal.length > 0) {
          // if code book is required and the code book is not retrieved yet, then get it
          if (useCodeBook.getState()) {
            aBook = new SQLCodeBook(cw, bookName.getText().toString());
            codeTable = aBook.codeBook;
          }
          if (create1ItemDataTable()) {
            if (useCodeBook.getState()) {
              replaceCode(data);
            }
              pushOutput(data, 0);
          }
          closeIt();
        }
      }
      else if (src == cancelBtn) {
        cubeTableName.setText(NOTHING);
        closeIt();
      }
      else if (src == bookBrowseBtn) {
        doBookBrowse();
      }
    }

    public void itemStateChanged(ItemEvent e) {
      if (e.getSource() == useCodeBook) {
        if (useCodeBook.getState()) {
          codeBookLayout.show(codeBookPanel, FILLED);
          bookName.setText(NOTHING);
        }
        else {
          codeBookLayout.show(codeBookPanel, BLANK);
          bookName.setText(NOTHING);
        }
      }
    }
  }

  /** connect to a database and retrieve the column list of the cube table
   *  @param colType the column type: 0 for condition column and 1 for target column
   */
  protected void doColumnBrowse() {
    possibleModel.removeAllElements();
    selectedModel.removeAllElements();
    DatabaseMetaData metadata = null;
    try {
      con = cw.getConnection();
      metadata = con.getMetaData();
      ResultSet columns = metadata.getColumns(null,"%",cubeTableName.getText(),"%");
      while (columns.next()) {
        String colName = columns.getString("COLUMN_NAME");
        if (!colName.equals("SET_SIZE") && !colName.equals("CNT")) {
          possibleModel.addElement(colName);
        }
      }
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in doColumnBrowse.");
    }
  }

  /** connect to a database and retrieve the list of available book code tables
   */
  protected void doBookBrowse() {
    Vector v = new Vector();
    try {
      DatabaseMetaData metadata = null;
      con = cw.getConnection();
      metadata = con.getMetaData();
      String[] types = {"TABLE"};
      ResultSet tableNames = metadata.getTables(null,"%","%_CODEBOOK%",types);
      while (tableNames.next()) {
        String aTable = tableNames.getString("TABLE_NAME");
        v.addElement(aTable);
      }
      if (v.size()<=0) {
        JOptionPane.showMessageDialog(msgBoard,
          "There is no any code book in the database", "Error",
          JOptionPane.ERROR_MESSAGE);
        System.out.println("There is no any code book in the database.");
      }
      else {
        bt = new BrowseTables(cw, v);
        btw = new BrowseTablesView(bt, v);
        btw.setSize(250,200);
        btw.setTitle("Available Code Book Tables");
        btw.setLocation(200,250);
        btw.setVisible(true);
        btw.addWindowListener(new WindowAdapter() {
          public void windowClosed(WindowEvent e)
          {
            bookName.setText(btw.getChosenRow());
          }
        });
      }
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in doBookBrowse.");
    }
  }

  public boolean create1ItemDataTable() {
    int rowCnt = 0;
    try {
      con = cw.getConnection();
      // only pick up the cube records that have values in the selected
      // columns and is a 1-item set
      String cntQry = new String("select count(*) from " + cubeTableName.getText() +
                                 " where ");
      for (int idx=0; idx<selectedModel.size(); idx++) {
        if (idx == 0) {
          cntQry = cntQry + selectedModel.get(idx) + " is not null ";
        }
        else {
          cntQry = cntQry + " and " + selectedModel.get(idx) + " is not null ";
        }
      }
      cntQry = cntQry + " AND set_size = 2";
      Statement cntStmt = con.createStatement();
      ResultSet cntSet = cntStmt.executeQuery(cntQry);
      while (cntSet.next()) {
        rowCnt = cntSet.getInt(1);
      }
      cntStmt.close();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
          e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in create1ItemDataTable.");
      return false;
    }
    // data table will contains one of the columns the user selected and one
    // extra column for count
    Column[] cols = new Column[3];
    cols[0] = new ObjectColumn(rowCnt);
    cols[0].setLabel(selectedModel.get(0).toString());
    cols[1] = new ObjectColumn(rowCnt);
    cols[1].setLabel(selectedModel.get(1).toString());
    cols[2] = new ObjectColumn(rowCnt);
    cols[2].setLabel("COUNT");
    data = (TableImpl)DefaultTableFactory.getInstance().createTable(cols);

    try {
      con = cw.getConnection();
      String dataQry = new String("select ");

      for (int idx=0; idx<selectedModel.size(); idx++) {
          dataQry = dataQry + selectedModel.get(idx) + ", ";
      }
      dataQry = dataQry + "CNT from " + cubeTableName.getText() + " where ";
      for (int idx=0; idx<selectedModel.size(); idx++) {
        if (idx == 0) {
          dataQry = dataQry + selectedModel.get(idx) + " is not null ";
        }
        else {
          dataQry = dataQry + " and " + selectedModel.get(idx) + " is not null ";
        }
      }
      dataQry = dataQry + " AND set_size = 2";
      Statement dataStmt = con.createStatement();
      ResultSet dataSet = dataStmt.executeQuery(dataQry);
      int rowIdx = 0;
      while (dataSet.next()) {
        data.setString(dataSet.getString(1),rowIdx,0);
        data.setString(dataSet.getString(2),rowIdx,1);
        data.setInt(dataSet.getInt(3),rowIdx,2);
        rowIdx ++;
      }
      dataStmt.close();
      return true;
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
          e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in createDataTable.");
      return false;
    }
  }

  protected void replaceCode(TableImpl origTbl) {
    MutableTable newTbl = (MutableTable) origTbl;
    for (int rowIdx = 0; rowIdx < newTbl.getNumRows(); rowIdx++) {
      for (int colIdx = 0; colIdx < 2; colIdx++) {
        String colName = newTbl.getColumnLabel(colIdx);
        String codeVal = newTbl.getString(rowIdx, colIdx);
        String textVal = aBook.getDescription(colName+"="+codeVal);
        if (textVal != null) {
          newTbl.setString(textVal,rowIdx,colIdx);
        }
      }
    }
    origTbl = (TableImpl) newTbl;
  }

  protected void closeIt() {
    executionManager.moduleDone(this);
  }


        /**
         * Return the human readable name of the module.
         * @return the human readable name of the module.
         */
        public String getModuleName() {
                return "SQLGetClusterBarChartFromCube";
        }

        /**
         * Return the human readable name of the indexed input.
         * @param index the index of the input.
         * @return the human readable name of the indexed input.
         */
        public String getInputName(int index) {
                switch(index) {
                        case 0:
                                return "Database Connection";
                        case 1:
                                return "Cube Table";
                        default: return "NO SUCH INPUT!";
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
                                return "Table";
                        default: return "NO SUCH OUTPUT!";
                }
        }

}