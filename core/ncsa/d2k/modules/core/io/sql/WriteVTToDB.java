// this is the working version to create a new table and load data
// Next version will do:
// append data to an existing table

package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.UserView;

import ncsa.d2k.controller.userviews.swing.*;

import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

//import ncsa.d2k.util.datatype.VerticalTable;
//import ncsa.d2k.util.datatype.Table;
import ncsa.d2k.util.datatype.*;

import java.sql.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.*;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
/**
 * Title: WriteVTToDB
 * Description: This module allows a user to save the data from an in-memory
 *  vertical table to a database table.
 *
 * @author  Dora Cai
 */

public class WriteVTToDB extends UIModule
       implements java.io.Serializable, HasNames, HasProperties
{
    /** Input holder for ConnectionWrapper */
    protected ConnectionWrapper cw;
    /** Input holder for VerticalTable */
    protected VerticalTable vt;
    /** The name of the database table */
    protected String TableName;

   /**
       Provide a description of this module.
       @return A description of this module.
    */
    public String getModuleInfo()
    {
	return "This module allows a user to save the data from "+
	    "a vertical table to a database table.";
    }
    /**
       Provide the module name.
       @return The name of this module.
    */
    public String getModuleName()
    {
		return "WriteVTToDB";
    }
    /**
       Return a String array containing the datatypes the inputs to this module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes ()
    {
        String [] in = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
                        "ncsa.d2k.util.datatype.VerticalTable" };
        return in;
    }
    /**
        Return a String array containing the datatypes of the outputs of this
        module.
        @return The datatypes of the outputs.
    */
    public String [] getOutputTypes()
    {
        return null;
    }
    /**
        Return the info for a particular input.
        @param i The index of the input to get info about
     */
    public String getInputInfo (int i)
    {
        switch(i)
        {
          case 0: return "JDBC data source to make database connection.";
          case 1: return "The VerticalTable to upload to database.";
          default: return "No such input.";
        }
    }
    public String getInputName (int i)
    {
        return null;
    }
    /**
        Return the info for a particular output.
        @param i The index of the output to get info about
     */
    public String getOutputInfo (int i)
    {
        return null;
    }
    public String getOutputName (int i)
    {
        return null;
    }
    /**
        Get the field name map for this module-view combination.
        @return The field name map.
     */
    protected String[] getFieldNameMapping ()
    {
        return null;
    }
    /**
       Create the UserView object for this module-view combination.
       @return The UserView associated with this module.
    */
    protected UserView createUserView()
    {
        cw = (ConnectionWrapper)pullInput(0);
        vt = (VerticalTable)pullInput(1);
        System.out.println("the input in createUserView is " + cw.toString());
        return new TableNameView();
    }

    /**
       Provides a simple user interface to get a table name.  The
       text values used in the Labels and textfields are
       properties of the module class.  If these properties
       are null, default values are used.
    */
    public class TableNameView extends JUserInputPane
	implements ActionListener
    {
        /** The module that creates this view.  We need a
	    reference to it so we can get and set its properties. */
	WriteVTToDB parentModule;
        JTable newTable;

        /** variables for createTable tab */
        JTextField newTableName;
        JButton createTableBtn;

        /** variables for appendTable tab */
        JTextField choosedTableName;
        JButton browseBtn;
        JButton appendTableBtn;

	public Dimension getPreferredSize()
        {
            return new Dimension (500, 400);
        }
        /**
	   Perform initializations here.
	   @param mod The module that created this UserView
	*/
	public void initView(ViewModule mod)
        {
            removeAll();
            System.out.println("enter initView");
	    parentModule = (WriteVTToDB)mod;

            // layout the createTable tab
            JPanel createTablePanel = new JPanel();
            createTablePanel.setLayout (new GridBagLayout());

            // Outline panel inside of createTable tab
            JOutlinePanel tableInfo = new JOutlinePanel("New Table Information");
            tableInfo.setLayout (new GridBagLayout());

            // Table with scrollbar for editing the new database table definition
            EmptyTableModel emptyModel = new EmptyTableModel();
            JTable newTableDef = new JTable(emptyModel);
            JScrollPane pane = new JScrollPane(newTableDef);
            newTableDef.setPreferredScrollableViewportSize (new Dimension(250,200));
            newTableDef.setRowHeight(18);

            // Allow to select a single row only
            newTableDef.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            newTable = newTableDef;

            // Use VerticalTable Information as the default column definition
            setUpColumnDefault(newTableDef);

            // The second column "Data Type" has comboBox for choosing data type
            setUpDataTypeCombo(newTableDef.getColumnModel().getColumn(1));

            // layout the outline panel inside of createTable tab
            Constrain.setConstraints(tableInfo, new JLabel("Table Name"),
                      0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(tableInfo, newTableName = new JTextField(20),
                      1,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
            Constrain.setConstraints(tableInfo, new JLabel("Table Columns"),
                      0,1,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(tableInfo, pane,
                      0,2,2,5,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,1,1);

            // Add outline panel and button into the createTable tab
            Constrain.setConstraints(createTablePanel, tableInfo,
                      0,0,3,2,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,1,1);
            Constrain.setConstraints(createTablePanel, createTableBtn = new JButton ("Create Table"),
                      1,3,1,1,GridBagConstraints.NONE, GridBagConstraints.CENTER,2,1);
            createTableBtn.addActionListener(this);

            // layout the appendTable tab
            JPanel appendTablePanel = new JPanel();
            appendTablePanel.setLayout (new GridBagLayout());
            JOutlinePanel chooseTable = new JOutlinePanel("Choose a Table");
            chooseTable.setLayout (new GridBagLayout());
            Constrain.setConstraints(chooseTable, choosedTableName = new JTextField(20),
                      0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(chooseTable, browseBtn = new JButton("Browse"),
                      3,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,2,1);
            browseBtn.addActionListener(this);

            JOutlinePanel dbTableInfo = new JOutlinePanel("DB Table Information");
            dbTableInfo.setLayout (new GridBagLayout());
            JOutlinePanel vtTableInfo = new JOutlinePanel("VT Table Information");
            vtTableInfo.setLayout (new GridBagLayout());
            Constrain.setConstraints(appendTablePanel, chooseTable,
                      0,3,4,2,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,4,2);
            Constrain.setConstraints(appendTablePanel, dbTableInfo,
                      0,7,4,2,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,4,2);
            Constrain.setConstraints(appendTablePanel, vtTableInfo,
                      0,11,4,2,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,4,2);
            Constrain.setConstraints(appendTablePanel, appendTableBtn = new JButton("Append Table"),
                      1,13,1,1,GridBagConstraints.NONE,GridBagConstraints.CENTER,2,1);
            appendTableBtn.addActionListener(this);

            // Put 2 tabs into tabbedpane
            JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);
            jtp.add(createTablePanel, "Create a New Table");
            jtp.add(appendTablePanel, "Append to a Table");

            setLayout(new BorderLayout());
            add(jtp, BorderLayout.CENTER);
	} /* end of initView */

        public void setUpColumnDefault(JTable newTable)
        {
            String sLength;
            for (int i = 0; i < vt.getNumColumns(); i++)
            {
                String sLabel = vt.getColumnLabel(i);
                String sType = getDataType(vt.getColumn(i));
                System.out.println("column " + i + " sLabel is " + sLabel);
                System.out.println("column " + i + " sType is " + sType);
                if (sType.equals("String")||
                    sType.equals("byte[]")||
                    sType.equals("char[]"))
                    // if data type is string-like, find the max length
                    sLength = getMaxLength(i);
                else
                    sLength = null;
                System.out.println("column " + i + " sLength is " + sLength);
                newTable.setValueAt(sLabel,i,0);
                newTable.setValueAt(sType,i,1);
                if (sLength != null)
                    newTable.setValueAt(sLength.toString(),i,2);
            }
        } /* end of setUpColumnDefault */

        public String getDataType(Column c)
        {
            if (c instanceof StringColumn)
                return "String";
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
		return "Object";
	    else if(c instanceof ByteArrayColumn)
		return "byte[]";
	    else if(c instanceof CharArrayColumn)
		return "char[]";
	    else
		return "unknown";
        } /* end of getDataType */

        public String getMaxLength(int c)
        {
            int maxLength = 0;
            for (int i=0; i<vt.getNumRows(); i++)
            {
                int l = vt.getString(i,c).length();
                if (l > maxLength)
                    maxLength = l;
            }
            return (Integer.toString(maxLength));
        } /* end of getMaxLength */

        public void setUpDataTypeCombo(TableColumn typeColumn)
        {
            System.out.println("enter setUpDataTypeCombo");
            JComboBox comboBox = new JComboBox(new String[]{"","String",
                "byte[]","char[]","int","float","double","long","short","boolean"});
            typeColumn.setCellEditor (new DefaultCellEditor (comboBox));

            //Set up tool tips for the data type cell
            DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
            renderer.setToolTipText ("Click for combo box");
            typeColumn.setCellRenderer(renderer);

            //Set up tool tip for the sport column header
            TableCellRenderer headerRenderer = typeColumn.getHeaderRenderer();
            if (headerRenderer instanceof DefaultTableCellRenderer)
            {
                ((DefaultTableCellRenderer)headerRenderer).setToolTipText(
                    "Click Data Type to see a list of choices");
            }
        } /* end of setUpDataTypeCombo */

	/**
	   This method is called when inputs arrive to the
	   ViewModule.
	   @param input The input
	   @param index The index of the input
	*/
	public void setInput(Object input, int index)
        {
	}
        /* this method is only fired when hit browse button */
        public void actionPerformed(ActionEvent e)
        {
            Object src = e.getSource();
            if (src == createTableBtn)
            {
                System.out.println("in actionPerformed, create table button is pressed");
                parentModule.TableName = newTableName.getText();
                doCreateTable(newTable);
            }
            else if (src == appendTableBtn)
            {
                System.out.println("in actionPerformed, append to table button is pressed");
            }
            else if (src == browseBtn)
            {
                System.out.println("in actionPerformed, browse table button is pressed");
            }
        }
    } /* end of TableNameView */

    protected void doCreateTable (JTable newTable)
    {
      JOptionPane msgBoard = new JOptionPane();
      // userName and dateStr are for columns username and create_date
      String userName = System.getProperty("user.name");
      System.out.println("user name is " + userName);
      java.util.Date now = new java.util.Date();
      SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      String dateStr = df.format(now);
      System.out.println("dateStr is " + dateStr);
      try
      {
          System.out.println ("Entering doCreateTable.");
          System.out.println("TableName.toString is: " + TableName.toString());

          if (TableName.toString().length() == 0)
          {
            JOptionPane.showMessageDialog(msgBoard,
            "Table name is missing. Table cannot be created", "Error",
            JOptionPane.ERROR_MESSAGE);
            return;
          }

          String sb = new String("create table " + TableName.toString() +
                      "(");
          int i = 0;
          for (i=0; i<newTable.getRowCount(); i++)
          {
            /* s1 is column name */
            String s1 = newTable.getValueAt(i,0).toString();
            System.out.println("query string 1 in for loop: " + sb);
            if (s1.length()>0)
            {
              if (i > 0) // add "," between columns definitions
                sb = sb + ",";
              sb = sb + (String) newTable.getValueAt(i,0) + " ";
              /* s2 is column type */
              String s2 = newTable.getValueAt(i,1).toString();
              System.out.println("query string 2 in for loop: " + sb);
              if (s2.length()>0)
              {
                String len = newTable.getValueAt(i, 2).toString();
                System.out.println("len is " + len);
                if (s2.equals("String"))
                  sb = sb + "varchar2(" + len.toString()+")";
                else if (s2.equals("byte[]"))
                  sb = sb + "varchar2(" + len.toString()+")";
                else if (s2.equals("char[]"))
                  sb = sb + "varchar2(" + len.toString()+")";
                // all numeric datatype set to default length 38
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
                else if (s2.equals("Boolean"))
                  sb = sb + "varchar2(" + "5)";
                else
                {
                  JOptionPane.showMessageDialog(msgBoard,
                  "Invalid data type. Table cannot be created", "Error",
                  JOptionPane.ERROR_MESSAGE);
                  return;
                }
              } /* end of if (s2.length()>0)*/
              else /* s2.length()<=0 */
              {
                JOptionPane.showMessageDialog(msgBoard,
                "Column length is not provided. Table cannot be created.", "Error",
                JOptionPane.ERROR_MESSAGE);
                return;
              }
            } /* end of if (s1.length() > 0) */
            else
              break;
          } /* end for */
          // add username and create_date columns to the query string
          sb = sb + ", username varchar2(30), create_date date)";
          System.out.println("The query string is " + sb);
          System.out.println("the input in doCreateTable is " + cw.toString());
          Connection con = cw.getConnection ();
          Statement stmt = con.createStatement ();
          ResultSet result = stmt.executeQuery(sb);
          stmt.close();
          result.close();
          System.out.println("Table " + TableName.toString() + " has been created");
       }
       catch (SQLException e1)
       {
           JOptionPane.showMessageDialog(msgBoard,
                "SQL error: " + e1.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("SQL error");
           return;
       }
       catch (ClassNotFoundException e2)
       {
           JOptionPane.showMessageDialog(msgBoard,
                "Class not found: " + e2.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Class not found error");
           return;
       }
       catch (InstantiationException e3)
       {
           JOptionPane.showMessageDialog(msgBoard,
                "Instantiation exception: " + e3.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Instantiation exception");
           return;
       }
       catch (IllegalAccessException e4)
       {
           JOptionPane.showMessageDialog(msgBoard,
                "Illegal access: " + e4.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Illegal access");
           return;
      }

       /* insert data */
      try
      {
        Connection con = cw.getConnection ();
        Statement stmt;
        for(int i = 0; i < vt.getNumRows(); i++)
        {
          String sb = new String("insert into " + TableName.toString() +
                        " values (");
          for(int j = 0; j < vt.getNumColumns(); j++)
          {
            /* find the data type */
            String colType = newTable.getValueAt(j,1).toString();
            String s = vt.getString(i, j);
            if (j > 0) // add "," between columns
              sb = sb + ",";
            if (colType.equals("String") || colType.equals("byte[]") ||
                colType.equals("char[]") || colType.equals("Boolean"))
            {
              sb = sb + "'" + s + "'";
            }
            else
            {
              sb = sb + s;
            }
          } /* end of for j */
          /* add username and create_date to the inserted row */
          sb = sb + ", '" + userName + "', to_date('" + dateStr + "', 'yyyy/mm/dd hh24:mi:ss'))";
          System.out.println("sb in row i: " + i + " insert data is " + sb);
          stmt = con.createStatement ();
          stmt.executeUpdate(sb);
          stmt.close();
        } /* end of for i */
        JOptionPane.showMessageDialog(msgBoard,
              "Table " + TableName.toString() + " has been created and data has been loaded.", "Information",
              JOptionPane.INFORMATION_MESSAGE);
        viewAbort();
      }
      catch (SQLException e1)
      {
           JOptionPane.showMessageDialog(msgBoard,
                "SQL error: " + e1.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("SQL error");
      }
      catch (ClassNotFoundException e2)
      {
           JOptionPane.showMessageDialog(msgBoard,
                "Class not found: " + e2.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Class not found error");
      }
      catch (InstantiationException e3)
      {
           JOptionPane.showMessageDialog(msgBoard,
                "Instantiation exception: " + e3.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Instantiation exception");
      }
      catch (IllegalAccessException e4)
      {
           JOptionPane.showMessageDialog(msgBoard,
                "Illegal access: " + e4.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Illegal access");
      }
    } /* end of doCreateTable */
    public class EmptyTableModel extends AbstractTableModel
    {
       final Object data[][] = {{"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""},
                           {"","",""}};
        final String columnNames []={"Column Name","Data Type","Column Length"};

        public int getRowCount()
        {
            return data.length;
        }
        public int getColumnCount()
        {
            return columnNames.length;
        }
        public String getColumnName (int col)
        {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col)
        {
            return data[row][col];
        }
        public boolean isCellEditable (int row, int col)
        {
            if (col == 0 || col == 1)
              return true;
            else if (col == 2 &&
                    (data[row][1].toString().equals("String")||
                     data[row][1].toString().equals("byte[]")||
                     data[row][1].toString().equals("char[]")))
              return true;
            else // Length column is not editable for numeric and boolean column
              return false;
        }
        public void setValueAt(Object value, int row, int col)
        {
            boolean pass = validateData(value, row, col);
            if (pass) // pass the validation
            {
              if (col == 0) // first column value can't contain space and minus sign
              {
                value = squeezeSpace(value);
                value = value.toString().replace('-','_');
                System.out.println("after replace, the value is " + value.toString());
              }
              data[row][col] = new String(value.toString());
              fireTableCellUpdated(row, col);
              System.out.println("New value of data at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               +value.getClass() + ")");
            }
        } /* end of setValueAt */

        /* data validation */
        public boolean validateData(Object value, int row, int col)
        {
            JOptionPane msgBoard = new JOptionPane();
            System.out.println("in Validatedata, row and col are: " + row + " " + col);
            // Must fill the previous row before move to a new row
            if (row > 0)
            {
              int i;
              for (i=0; i<3; i++)
              {
                if (data[row-1][i].toString().equals(""))
                {
                  switch(i)
                  {
                    case 0:
                      JOptionPane.showMessageDialog(msgBoard,
                      "You must give a column name for the previous row.", "Error",
                      JOptionPane.ERROR_MESSAGE);
                      return(false);
                    case 1:
                      JOptionPane.showMessageDialog(msgBoard,
                      "You must choose a data type for the previous row.", "Error",
                      JOptionPane.ERROR_MESSAGE);
                      return(false);
                    case 2: // length must be specified for varchar datatype
                      if (data[row-1][1].toString().equals("String"))
                      {
                        JOptionPane.showMessageDialog(msgBoard,
                        "You must specify the length of String for the previous row.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                        return(false);
                      }
                      else
                        return(true);
                  } /* end of switch */
                } /* end of if (data[row-1][i] == "" */
              } /* end of for */
            } /* end of if (row > 0) */
           return (true);
        } /* end of validateData */

        public Object squeezeSpace(Object value)
        {
          int j;
          String strValue = value.toString();
          String newStr = "";
          for (j=0; j<value.toString().length();j++)
          {
            if (strValue.charAt(j)!=' ')
              newStr = newStr + strValue.charAt(j);
          }
          value = (Object)newStr;
          System.out.println("after squeeze, the value is" + value.toString());
          return(value);
        }
   } /* end of EmptyTableModel */
}
