package ncsa.d2k.modules.core.io.sql;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.sql.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;
/**
 * Title: WriteVTToDB
 * Description: This module allows a user to save the data from an in-memory
 *  vertical table to a database table.
 *
 * @author  Dora Cai
 */

public class WriteVTToDB extends UIModule
        {
    /* Input holder for ConnectionWrapper */
    protected ConnectionWrapper cw;
    /* Input holder for VerticalTable */
    protected TableImpl vt;
    /* SQL Query String */
    protected String query;
    /* The resultset table model */
    protected BrowseTables bt;
    /* Popup window for the available table list */
    protected BrowseTablesView btw;
    /* Pane for msg display */
    JOptionPane msgBoard = new JOptionPane();

    /* variables for tableModel objects */
    MetaTableModel newModel;
    MetaTableModel dbModel;
    MetaTableModel vtModel;

    /* variables for JTextField objects */
    JTextField newTableName;
    JTextField chosenTableName;

    /* variables for JTable objects */
    JTable newTableDef;
    JTable dbTableDef;
    JTable vtTableDef;

    /* maximum number in vertical tables */
    int maxNumRow;
    /* character used to initialize tables */
    static String  NOTHING = "";

   /**
       Provide a description of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module takes an Oracle database Connection and a table as the input,     and save the data from the input table to a database table. There are two     options are provided. You can either create a new database table to save     the data, or append the data to an existing database table.  </body></html>";
	}
    /**
       Provide the module name.
       @return The name of this module.
    */
    public String getModuleName() {
      return "Write VT To DB";
    }
    /**
       Return a String array containing the datatypes the inputs to this module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}
    /**
        Return a String array containing the datatypes of the outputs of this
        module.
        @return The datatypes of the outputs.
    */
    public String [] getOutputTypes() {
		String[] types = {		};
		return types;
	}
    /**
        Return the info for a particular input.
        @param i The index of the input to get info about
        @return The information on the input
     */
    public String getInputInfo (int i) {
		switch (i) {
			case 0: return "JDBC data source to make database connection.";
			case 1: return "The input table to upload to database.";
			default: return "No such input";
		}
	}
    public String getInputName (int i) {
        return null;
    }
    /**
        Return the info for a particular output.
        @param i The index of the output to get info about
        @return The information on the output
     */
    public String getOutputInfo (int i) {
		switch (i) {
			default: return "No such output";
		}
	}
    public String getOutputName (int i) {
        return null;
    }

	    //QA Anca added this:
	    public PropertyDescription[] getPropertiesDescriptions() {
		// so that "WindowName" property is invisible
		return new PropertyDescription[0];
	    }


    /**
        Get the field name map for this module-view combination.
        @return The field name map.
     */
    protected String[] getFieldNameMapping () {
        return null;
    }
    /**
       Create the UserView object for this module-view combination.
       @return The UserView associated with this module.
    */
    protected UserView createUserView() {
        return new TableNameView();
    }

    /**
       Provides a simple user interface to get a table name and table
       definition.  The text values used in the Labels and textfields are
       properties of the module class.  If these properties
       are null, default values are used.
    */
    public class TableNameView extends JUserInputPane
	implements ActionListener {

        /* variables for createTable tab */
        JButton createTableBtn;
        JButton cancelAddBtn;

        /* variables for appendTable tab */
        JButton browseBtn;
        JButton appendTableBtn;
        JButton cancelAppendBtn;


	public Dimension getPreferredSize() {
            return new Dimension (500, 500);
        }
        /**
	   Perform initializations here.
	   @param mod The module that created this UserView
	*/
	public void initView(ViewModule mod) {
            removeAll();
            cw = (ConnectionWrapper)pullInput(0);
            vt = (TableImpl)pullInput(1);
            /* the number of rows in vt JTables is determined by inputed table */
            maxNumRow = vt.getNumColumns();

            System.out.println("enter initView");

            /* layout the createTable tab */
            JPanel createTablePanel = new JPanel();
            createTablePanel.setLayout (new GridBagLayout());

            /* Outline panel inside of createTable tab */
            JOutlinePanel newTableInfo = new JOutlinePanel("New Table Information");
            newTableInfo.setLayout (new GridBagLayout());

            /* Table with scrollbar for editing the new database table definition */
            /* the number of columns is determined by the number of columns in input vt */
            newModel = new MetaTableModel(maxNumRow,3,true);
            newTableDef = new JTable(newModel);
            JScrollPane pane = new JScrollPane(newTableDef);
            newTableDef.setPreferredScrollableViewportSize (new Dimension(250,300));

            /* Allow to select a single row only */
            newTableDef.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            /* Use input information on the table as the default column definition */
            setUpColumnDefault(newTableDef);

            /* The second column "Data Type" has comboBox for choosing data type */
            setUpDataTypeCombo(newTableDef.getColumnModel().getColumn(1));

            /* layout the outline panel inside of createTable tab */
            Constrain.setConstraints(newTableInfo, new JLabel("Table Name"),
                      0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(newTableInfo, newTableName = new JTextField(20),
                      1,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
            Constrain.setConstraints(newTableInfo, new JLabel("Table Columns"),
                      0,1,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(newTableInfo, pane,
                      0,2,2,5,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,1,1);

            /* Add outline panel and button into the createTable tab */
            Constrain.setConstraints(createTablePanel, newTableInfo,
                      0,0,3,3,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,1,1);
            Constrain.setConstraints(createTablePanel, cancelAddBtn = new JButton ("     Cancel     "),
                      1,3,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
            cancelAddBtn.addActionListener(this);
            Constrain.setConstraints(createTablePanel, createTableBtn = new JButton ("Create Table"),
                      2,3,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
            createTableBtn.addActionListener(this);

            /* layout the appendTable tab */
            JPanel appendTablePanel = new JPanel();
            appendTablePanel.setLayout (new GridBagLayout());

            /* first outline panel inside of appendTablePanel */
            JOutlinePanel chooseTable = new JOutlinePanel("Choose a Table");
            chooseTable.setLayout (new GridBagLayout());
            Constrain.setConstraints(chooseTable, chosenTableName = new JTextField(20),
                      0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
            chosenTableName.addActionListener(this);
            Constrain.setConstraints(chooseTable, browseBtn = new JButton("Browse"),
                      3,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,2,1);
            browseBtn.addActionListener(this);

            /* Second outline panel inside of appendTablePanel */
            JOutlinePanel dbTableInfo = new JOutlinePanel("DB Table Information");
            dbTableInfo.setLayout (new GridBagLayout());
            /* Table with scrollbar for viewing the existing table definition */
            /* the database table has up to 100 columns and not editable */
            dbModel = new MetaTableModel(900,3,false);
            dbTableDef = new JTable(dbModel);
            JScrollPane dbPane = new JScrollPane(dbTableDef);
            dbTableDef.setPreferredScrollableViewportSize(new Dimension(250,125));
            dbTableDef.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            Constrain.setConstraints(dbTableInfo, dbPane,
                      0,0,2,5,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,1,1);

            /* Third outline panel inside of appendTablePanel */
            JOutlinePanel vtTableInfo = new JOutlinePanel("VT Table Information");
            vtTableInfo.setLayout (new GridBagLayout());

            /* Table with scrollbar for viewing the VT table definition */
            /* the number of columns in VT table is determined by number of columns in vt input */
            vtModel = new MetaTableModel(maxNumRow,3,false);
            vtTableDef = new JTable(vtModel);
            JScrollPane vtPane = new JScrollPane(vtTableDef);
            vtTableDef.setPreferredScrollableViewportSize(new Dimension(250,125));
            vtTableDef.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setUpColumnDefault(vtTableDef);
            Constrain.setConstraints(vtTableInfo, vtPane,
                      0,0,2,5,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,1,1);

            /* Add outline panels and button into the appendTable tab */
            Constrain.setConstraints(appendTablePanel, chooseTable,
                      0,0,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(appendTablePanel, dbTableInfo,
                      0,1,3,7,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(appendTablePanel, vtTableInfo,
                      0,8,3,7,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(appendTablePanel, cancelAppendBtn = new JButton("     Cancel     "),
                      1,15,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1,1);
            cancelAppendBtn.addActionListener(this);
            Constrain.setConstraints(appendTablePanel, appendTableBtn = new JButton("Append Table"),
                      2,15,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
            appendTableBtn.addActionListener(this);

            /* Put 2 tabs into tabbedpane */
            JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);
            jtp.add(createTablePanel, "Create a New Table");
            jtp.add(appendTablePanel, "Append to a Table");

            setLayout(new BorderLayout());
            add(jtp, BorderLayout.CENTER);

	} /* end of initView */

        /** Fill up column information based on the input table
	    @param newTable The JTable to fill up the column information
        */
        protected void setUpColumnDefault(JTable newTable) {
            String sLength;
            for (int i = 0; i < vt.getNumColumns(); i++) {
                String sLabel = vt.getColumnLabel(i);
                String sType = getDataType(vt.getColumn(i));
                if (sType.equals("string")||
                    sType.equals("byte[]")||
                    sType.equals("char[]"))
                    // if data type is string-like, find the max length
                    sLength = getMaxLength(i);
                else
                    sLength = NOTHING;
                newTable.setValueAt(sLabel,i,0);
                newTable.setValueAt(sType,i,1);
                newTable.setValueAt(sLength.toString(),i,2);
            }
        }

        /** get data type of the column
            @param c The column of the table
            @return The data type of the column
        */
        public String getDataType(Column c) {
            if (c instanceof StringColumn)
                return "string";
            else if(c instanceof IntColumn)
		return "int";
            else if(c instanceof FloatColumn)
                return "float";
            else if(c instanceof DoubleColumn)
                return "double";
            else if(c instanceof LongColumn)
                return "long";
            else if(c instanceof ShortColumn)
		return "short";
	    else if(c instanceof BooleanColumn)
		return "boolean";
	    else if(c instanceof ObjectColumn)
		return "object";
	    else if(c instanceof ByteArrayColumn)
		return "byte[]";
	    else if(c instanceof CharArrayColumn)
		return "char[]";
	    else
		return "unknown";
        }

        /** get maximum length of the string column from the table
            @param c The index of the column.
            @return The maximum length of the column.
        */
        public String getMaxLength(int c) {
            int maxLength = 0;
            for (int i=0; i<vt.getNumRows(); i++) {
                int l = vt.getString(i,c).length();
                if (l > maxLength)
                    maxLength = l;
            }
            return (Integer.toString(maxLength));
        }

        /** set up combo box for the data type column
            @param typeColumn The table column to set up combo box
        */
        public void setUpDataTypeCombo(TableColumn typeColumn) {
            JComboBox comboBox = new JComboBox(new String[]{NOTHING,"string",
                "byte[]","char[]","int","float","double","long","short","boolean"});
            typeColumn.setCellEditor (new DefaultCellEditor (comboBox));

            /*Set up tool tips for the data type cell */
            DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
            renderer.setToolTipText ("Click for combo box");
            typeColumn.setCellRenderer(renderer);

            /* Set up tool tip for the datatype column header */
            TableCellRenderer headerRenderer = typeColumn.getHeaderRenderer();
            if (headerRenderer instanceof DefaultTableCellRenderer) {
                ((DefaultTableCellRenderer)headerRenderer).setToolTipText(
                    "Click Data Type to see a list of choices");
            }
        }

	/**
	   This method is called when inputs arrive to the
	   ViewModule.
	   @param input The input
	   @param index The index of the input
	*/
	public void setInput(Object input, int index) {
          if (index == 0) {
            cw = (ConnectionWrapper)input;
            System.out.println("the input 1 in setInput is " + cw.toString());
          }
          else if (index == 1) {
            vt = (TableImpl)input;
            System.out.println("the input 2 in setInput is " + vt.toString());
            newModel.initTableModel(maxNumRow,3);
            newModel.fireTableDataChanged();
            setUpColumnDefault(newTableDef);
            setUpDataTypeCombo(newTableDef.getColumnModel().getColumn(1));
            vtModel.initTableModel(maxNumRow,3);
            vtModel.fireTableDataChanged();
            setUpColumnDefault(vtTableDef);
          }
	}

        /* this method is fired when a button or enter key is pressed */
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == createTableBtn) {
                if (doCreateTable()) {
                  doInsertTable(newTableName.getText(), newTableDef);
                }
            }
            else if (src == appendTableBtn) {
              /* user may not hit Enter key in the field chosenTableName before
                 clicking appendTableBtn. We need to force firing getDBTableDef */
              dbModel.initTableModel(900,3);
              dbModel.fireTableDataChanged();
              getDBTableDef();
              boolean pass = doValidate(dbTableDef, vtTableDef);
              if (pass) {
                doInsertTable(chosenTableName.getText(), vtTableDef);
              }
            }
            else if (src == chosenTableName) {
              if (chosenTableName.getText()!= null && chosenTableName.getText()!=NOTHING)
              {
                /* wipe out previously loaded table definition */
                dbModel.initTableModel(900,3);
                dbModel.fireTableDataChanged();
                getDBTableDef();
              }
            }
            else if (src == browseBtn) {
              doBrowse();
            }
            else if (src == cancelAddBtn) {
              System.out.println("cancel add button is pressed");
              viewAbort();
            }
            else if (src == cancelAppendBtn) {
              System.out.println("cancel append button is pressed");
              viewAbort();
            }
        } /* end of actionPerformed */

      /** connect to a database and retrieve the list of available tables */
      protected void doBrowse() {
          query = "select table_name from all_tables where owner not like '%SYS%'";
          try {
              bt = new BrowseTables(cw, query);
              btw = new BrowseTablesView(bt, query);
              btw.setSize(250,200);
              btw.setTitle("Available Tables");
              btw.setLocation(200,250);
              btw.setVisible(true);
              btw.addWindowListener(new WindowAdapter() {
                  public void windowClosed(WindowEvent e)
                  {
                    chosenTableName.setText(btw.getChosenRow());
                    /* wipe out previously loaded table definition */
                    dbModel.initTableModel(900,3);
                    dbModel.fireTableDataChanged();
                    getDBTableDef();
                  }
              });
           }
           catch (Exception e){
             JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
             System.out.println("Error occoured in doBrowse.");
           }
      }
    } /* end of TableNameView */

    /** create a database table based on user's input */
    protected boolean doCreateTable () {
      try {
          System.out.println ("Entering doCreateTable.");
          System.out.println("TableName is: " + newTableName.getText());

          if (newTableName.getText().length() == 0 ||
              newTableName.getText().equals(NOTHING)) {
            JOptionPane.showMessageDialog(msgBoard,
            "Table name is missing. Table cannot be created", "Error",
            JOptionPane.ERROR_MESSAGE);
            return (false);
          }

          String sb = new String("create table " + newTableName.getText() +
                      " (");
          int i = 0;
          for (i=0; i<newTableDef.getRowCount(); i++) {
            /* s1 is column name */
            String s1 = newTableDef.getValueAt(i,0).toString();
            if (s1.length()>0) {
              if (i > 0) // add "," between columns definitions
                sb = sb + ",";
              sb = sb + (String) newTableDef.getValueAt(i,0) + " ";
              /* s2 is column type */
              String s2 = newTableDef.getValueAt(i,1).toString();
              if (s2.length()>0) {
                String len = newTableDef.getValueAt(i, 2).toString();
                if (s2.equals("string"))
                  sb = sb + "varchar2(" + len.toString()+")";
                else if (s2.equals("byte[]"))
                  sb = sb + "varchar2(" + len.toString()+")";
                else if (s2.equals("char[]"))
                  sb = sb + "varchar2(" + len.toString()+")";
                /* all numeric datatype set to Oracle default length 22 */
                else if (s2.equals("int"))
                  sb = sb + "number";
                else if (s2.equals("float"))
                  sb = sb + "number";
                else if (s2.equals("double"))
                  sb = sb + "number";
                else if (s2.equals("long"))
                  sb = sb + "number";
                else if (s2.equals("short"))
                  sb = sb + "number";
                /* boolean datatype is saved as Oracle varchar2 */
                else if (s2.equals("boolean"))
                  sb = sb + "varchar2(" + "5)";
                else {
                  JOptionPane.showMessageDialog(msgBoard,
                  "Invalid data type. Table cannot be created", "Error",
                  JOptionPane.ERROR_MESSAGE);
                  return (false);
                }
              } /* end of if (s2.length()>0)*/
              else /* s2.length()<=0 */ {
                JOptionPane.showMessageDialog(msgBoard,
                "Column length is not provided. Table cannot be created.", "Error",
                JOptionPane.ERROR_MESSAGE);
                return (false);
              }
            } /* end of if (s1.length() > 0) */
            else
              break;
          } /* end for */
          // add username and create_date columns to the query string
          sb = sb + ", username varchar2(30), create_date date)";
          Connection con = cw.getConnection ();
          Statement stmt = con.createStatement ();
          ResultSet result = stmt.executeQuery(sb);
          stmt.close();
          result.close();
          System.out.println("Table " + newTableName.getText() + " has been created");
       }
       catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occoured in doCreateTable.");
           return (false);
      }
      return (true);
    }

    /** insert data from an input table into a database table
        @param dTableName The name of the table to create in the database
        @param vTableDef The structure of the table to create in the database
    */
    protected void doInsertTable (String dTableName, JTable vTableDef ) {
      /* userName and dateStr are for columns username and create_date */
      String userName = System.getProperty("user.name");
      System.out.println("user name is " + userName);
      java.util.Date now = new java.util.Date();
      SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      String dateStr = df.format(now);
      int rowIdx = 0;
      int colIdx = 0;
      /* insert data */
      try {
        Connection con = cw.getConnection ();
        Statement stmt;
        for(rowIdx = 0; rowIdx < vt.getNumRows(); rowIdx++) {
          String sb = new String("insert into " + dTableName +
                        " values (");
          for(colIdx = 0; colIdx < vt.getNumColumns(); colIdx++) {
            /* find the data type */
            String colType = vTableDef.getValueAt(colIdx,1).toString();
            String s = vt.getString(rowIdx, colIdx);
            if (colIdx > 0) /* add "," between columns */
              sb = sb + ",";
            if (colType.equals("string") || colType.equals("byte[]") ||
                colType.equals("char[]") || colType.equals("boolean")) {
              sb = sb + "'" + s + "'";
            }
            else {
              sb = sb + s;
            }
          } /* end of for colIdx */
          /* add username and create_date to the inserted row */
          sb = sb + ", '" + userName + "', to_date('" + dateStr + "', 'yyyy/mm/dd hh24:mi:ss'))";
          //System.out.println("sb is " + sb);
          stmt = con.createStatement ();
          stmt.executeUpdate(sb);
          stmt.close();
        } /* end of for rowIdx */
        JOptionPane.showMessageDialog(msgBoard,
              "Data has been loaded.", "Information",
              JOptionPane.INFORMATION_MESSAGE);
        newTableName.setText(NOTHING);
        chosenTableName.setText(NOTHING);
        newModel.initTableModel(maxNumRow,3);
        dbModel.initTableModel(900,3);
        vtModel.initTableModel(maxNumRow,3);
        viewAbort();
      }
      catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error in row " + rowIdx ,
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occoured in doInsertTable.");
      }
    }

    /** get the structure of a database table
    */
    protected void getDBTableDef() {
        System.out.println("enter getDBTableDef");
        try {
          Connection con = cw.getConnection ();
          Statement stmt;
          String sb = new String("select column_name, data_type, data_length " +
            "from all_tab_columns where table_name = '" +
            chosenTableName.getText().toUpperCase() + "' order by column_id");
          stmt = con.createStatement ();
          ResultSet tableSet = stmt.executeQuery(sb);
          int rIdx = 0;
          while (tableSet.next()) {
            dbTableDef.setValueAt(tableSet.getString(1),rIdx,0);
            dbTableDef.setValueAt(tableSet.getString(2),rIdx,1);
            dbTableDef.setValueAt(tableSet.getString(3),rIdx,2);
            rIdx++;
          }
          /* if rIdx == 0, that indicate the tableSet is empty, the db table does not exist */
          if (rIdx == 0) {
            JOptionPane.showMessageDialog(msgBoard,
                "Table " + chosenTableName.getText() + " does not exist.", "Error",
                JOptionPane.ERROR_MESSAGE);
            System.out.println("table does not exist.");
          }
          stmt.close();
        }
        catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occoured in getDBTableDef.");
        }
    }

    /** verify the data type and length of the input table suitable to
        insert into database
        @param dbTable The JTable keeping the structure of the database table
        @param vtTable The JTable keeping the structure of the input table
        @return The validation result (pass or fail)
    */
    protected boolean doValidate(JTable dbTable, JTable vtTable) {
      int dbRowCnt;
      int vtRowCnt;
      for (dbRowCnt=0; dbRowCnt<dbTable.getRowCount(); dbRowCnt++) {
        if (dbTable.getValueAt(dbRowCnt,0).equals(NOTHING))
          break;
      };
      for (vtRowCnt=0; vtRowCnt<vtTable.getRowCount(); vtRowCnt++) {
        if (vtTable.getValueAt(vtRowCnt,0).equals(NOTHING))
          break;
      };
      /* database table has 2 extra columns to keep the create date and username */
      if (dbRowCnt != (vtRowCnt+2)) {
        JOptionPane.showMessageDialog(msgBoard,
                "The number of columns does not match. Data cannot be appended. ",
                "Error", JOptionPane.ERROR_MESSAGE);
        System.out.println("column number does not match.");
        return (false);
      }
      for (int i = 0; i < vtRowCnt; i++) {
        if (!isTypeMatch(dbTable.getValueAt(i,1),vtTable.getValueAt(i,1))) {
          JOptionPane.showMessageDialog(msgBoard,
                "The data type of column " + (i+1) +
                " does not match. Data cannot be " +
                "appended. ", "Error", JOptionPane.ERROR_MESSAGE);
          System.out.println("column data type does not match.");
          return (false);
        }
        if (!isLengthMatch(dbTable.getValueAt(i,2),vtTable.getValueAt(i,2))) {
          JOptionPane.showMessageDialog(msgBoard,
                "The column " + (i+1) +
                " in input table is too big. Data cannot be " +
                "appended. ", "Error", JOptionPane.ERROR_MESSAGE);
          System.out.println("column is too big.");
          return (false);
        }
        return (true);
      }
      return (true);
    }

    /** verify the input table's datatype matches the database table's
        @param type1 The database table's data type
        @param type2 The input table's data type
        @return Dose the data type match? (yes or no)
    */
    protected boolean isTypeMatch (Object type1, Object type2) {
      if (type1.equals("varchar2")) {
        if (type2.equals("string") || type2.equals("boolean") ||
            type2.equals("byte[]") || type2.equals("char[]"))
          return (true);
        else
          return (false);
      }
      else if (type1.equals("number")) {
        if (type2.equals("int") || type2.equals("float") ||
            type2.equals("double") || type2.equals("long") ||
            type2.equals("short"))
            return (true);
        else
            return (false);
      }
      return (false);
    }

    /** verify the input table's data length matches the database table's
        @param length1 The database table's data length
        @param length2 The input table's data length
        @return Dose the data length match? (yes or no)
    */
    protected boolean isLengthMatch (Object length1, Object length2) {
      /* numeric column always uses the maximum length in the database */
      if (length2.equals(NOTHING)) {
        return (true);
      }
      /* if the input column length > database column length, return false */
      else if (Integer.valueOf(length1.toString()).intValue()
                < Integer.valueOf(length2.toString()).intValue()) {
        return (false);
      }
      return(true);
    }

}


