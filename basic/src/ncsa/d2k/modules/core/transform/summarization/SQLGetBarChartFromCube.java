package ncsa.d2k.modules.core.transform.summarization;

/**
 * <p>Title: SQLGetBarChartFromCube </p>
 * <p>Description: Extract rule association from a cube table </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UIModule;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.ObjectColumn;
import ncsa.d2k.modules.core.datatype.table.basic.TableImpl;
import ncsa.d2k.modules.core.io.sql.BrowseTables;
import ncsa.d2k.modules.core.io.sql.BrowseTablesView;
import ncsa.d2k.modules.core.io.sql.ConnectionWrapper;
import ncsa.d2k.modules.core.transform.attribute.SQLCodeBook;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.gui.Constrain;

public class SQLGetBarChartFromCube extends UIModule {
  JOptionPane msgBoard = new JOptionPane();
  ConnectionWrapper cw;
  Connection con;
  String cube;
  static String NOTHING = "";

  JTextField cubeTableName;
  JList selectedFields;
  JList possibleFields;
  DefaultListModel possibleModel;
  DefaultListModel selectedModel;
  JButton cancelBtn;
  JButton displayBtn;

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

  BrowseTables bt;
  BrowseTablesView btw;
  int featureIdx = -1;
  int colCnt;
  MutableTableImpl data;

  public SQLGetBarChartFromCube() {
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
    s += "This module prepares cube table data for display in one or more bar chart visualizations. </p>";
    s += "<p> Detailed Description: ";
    s += "This module first makes a connection to a database and retrieves the ";
    s += "data for the user-selected attributes in the specified cube table. ";
    s += "It formats the statistical data and makes it available on the output port for use by ";
    s += "a 2D Bar Chart module which generates the actual visualizations. ";
    s += "A bar chart is generated for each selected attribute. ";
    s += "The displayed data not only can be labeled in predefined ";
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


    //QA Anca added this:
    public PropertyDescription[] getPropertiesDescriptions() {
        // so that "WindowName" property is invisible
        return new PropertyDescription[0];
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
      jsp.setColumnHeaderView(new JLabel("Possible Attributes"));
      JScrollPane jsp1 = new JScrollPane(selectedFields);
      jsp1.setColumnHeaderView(new JLabel("Selected Attributes"));

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
        else if (cubeTableName.getText().length()>0 &&
            retVal.length > 0 &&
            cubeTableName.getText().toString().indexOf("_CUBE")>=0) {
          if (useCodeBook.getState()) {
            aBook = new SQLCodeBook(cw, bookName.getText().toString());
            codeTable = aBook.codeBook;
          }
          for (int idx = 0; idx < selectedModel.size(); idx++) {
            // if code book is required and the code book is not retrieved yet, then get it
            if (create1ItemDataTable(idx)) {
              if (useCodeBook.getState()) {
                replaceCode(data);
              }
              pushOutput(data, 0);
            }
          }
          closeIt();
        }
        else if (cubeTableName.getText().length()<=0) { // The user has not chosen a table yet
          JOptionPane.showMessageDialog(msgBoard,
          "There is no cube table selected.", "Error",
          JOptionPane.ERROR_MESSAGE);
          System.out.println("There is no table selected.");
        }
        else if (retVal.length <= 0) { // The user has not chosen any features
          JOptionPane.showMessageDialog(msgBoard,
          "You must select some attributes.", "Error",
          JOptionPane.ERROR_MESSAGE);
          System.out.println("There are no attributes selected.");
        }
        else if (cubeTableName.getText().toString().indexOf("_CUBE")<0) {
          JOptionPane.showMessageDialog(msgBoard,
          "To display a chart, you must select a cube table rather than a data table.", "Error",
          JOptionPane.ERROR_MESSAGE);
          System.out.println("A cube table is selected instead of a data table.");
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
      System.out.println("Error occurred in doColumnBrowse.");
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
      System.out.println("Error occurred in doBookBrowse.");
    }
  }

  public boolean create1ItemDataTable(int col) {
    int rowCnt = 0;
    try {
      con = cw.getConnection();
      // only pick up the cube records that have values in the selected
      // columns and is a 1-item set
      String cntQry = new String("select count(*) from " + cubeTableName.getText() +
                                 " where ");
      for (int idx=0; idx<selectedModel.size(); idx++) {
        if (idx == col) {
          cntQry = cntQry + selectedModel.get(idx) + " is not null ";
        }
      }
      cntQry = cntQry + " AND set_size = 1";
      //System.out.println("cntQry is " + cntQry);
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
      System.out.println("Error occurred in create1ItemDataTable.");
      return false;
    }
    // data table will contains one of the columns the user selected and one
    // extra column for count
    Column[] cols = new Column[2];
    cols[0] = new ObjectColumn(rowCnt);
    cols[0].setLabel(selectedModel.get(col).toString());
    cols[1] = new ObjectColumn(rowCnt);
    cols[1].setLabel("COUNT");
    data = new MutableTableImpl (cols);

    try {
      con = cw.getConnection();
      String dataQry = new String("select ");
      dataQry = dataQry + selectedModel.get(col) + ", ";
      dataQry = dataQry + "CNT from " + cubeTableName.getText() + " where ";
      dataQry = dataQry + selectedModel.get(col) + " is not null ";
      dataQry = dataQry + " AND set_size = 1";
      //dataQry = dataQry + " AND set_size = 1 order by " + selectedModel.get(col);
      //System.out.println("dataQry is " + dataQry);
      Statement dataStmt = con.createStatement();
      ResultSet dataSet = dataStmt.executeQuery(dataQry);
      int rowIdx = 0;
      while (dataSet.next()) {
        data.setString(dataSet.getString(1),rowIdx,0);
        data.setInt(dataSet.getInt(2),rowIdx,1);
        rowIdx ++;
      }
      dataStmt.close();
      return true;
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
          e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occurred in createDataTable.");
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
                return "SQL Get Bar Chart From Cube";
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
