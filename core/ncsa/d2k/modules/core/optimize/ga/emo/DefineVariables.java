package ncsa.d2k.modules.core.optimize.ga.emo;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 Newemo1.java
*/
public class DefineVariables
    extends UIModule
    implements Serializable {

  public String getModuleName() {
    return "Define Variables";
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getInputName(int index) {
    return "EMOData";
  }

  public String getInputInfo(int index) {
    return "EMOData";
  }

  /**
     This pair returns an array of strings that contains the data types for the outputs.
     @return the data types of all outputs.
  */
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
    return types;
  }

  /**
     This pair returns the description of the outputs.
     @return the description of the indexed output.
  */
  public String getOutputName(int i) {
    /*switch (i) {
      case 0:
        return "Population table";
      case 1:
        return "Variable table model";
      default:
        return "No such output";
    }*/
    return "EMOPopulationInfo";
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        String s = "This is the mutable table that has zero rows.";
        s += "The coulmn corresponds to the variables of the problem.";
        return s;
      case 1:
        s = "This is the default table model that has information";
        s += " of the ranges of each variable and required precision for";
        s += " each variable.";
      default:
        return "No such output";
    }
  }

  /**
     This pair returns the description of the module.
     @return the description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This is the place of first interaction of user with the EMO GUI.";
    s += "This takes in the information regarding the variables of the";
    s += "problem.";
    s += "</p><p>Detailed Description: ";
    s += "This module asks user to fill in information regarding the";
    s += "number of variables in the problem, the desired precision of";
    s += "each variable, the upper and lower value of each variable.";
    s += "It calculates the stringlength for each variable.";
    s += "</p><p>Data Type Restrictions:";
    s += "The variables of the problem are floats.";
    s += "</p>";
    return s;
  }

  /*  Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }

  private CachedRowValue[] savedRows;
  /**
    set method for property of savedRows (cachedRowValue variable)
   */
  public void setSavedRows(CachedRowValue[] mySave) {
    savedRows = mySave;
  }

  /**
   get method for property of savedRows (cachedRowValue variable)
   @return savedRows
   */
  public CachedRowValue[] getSavedRows() {
    return savedRows;
  }

  /**
     This pair is called by D2K to get the UserView for this module.
     @return the UserView.
   */
  protected UserView createUserView() {
    return new DefineVarView();
  }

  /**
       This pair returns an array with the names of each DSComponent in the UserView
     that has a value.  These DSComponents are then used as the outputs of this module.
   */
  public String[] getFieldNameMapping() {
    return null;
  }

  /**
     The GUI for feeding the information of the variables of the problem.
   */
  private class DefineVarView
      extends ncsa.d2k.userviews.swing.JUserPane {

    private TableImpl table1;
    private JTextField numVarTf;

    private JScrollPane jsp1;
    private JTable tb;

    private DefaultTableModel model;
    private JLabel total_string_length = new JLabel("Total String Length:");

    public void initView(ViewModule viewmodule) {
      setLayout(new BorderLayout());
      JPanel dummy = new JPanel();
      JPanel MainPanel[] = new JPanel[2];

      MainPanel[0] = new JPanel();
      MainPanel[1] = new JPanel();

      MainPanel[1].setLayout(new BoxLayout(MainPanel[1], BoxLayout.Y_AXIS));
      MainPanel[0].setMinimumSize(new Dimension(200, 50));
      MainPanel[0].setPreferredSize(new Dimension(200, 50));
      MainPanel[1].setMinimumSize(new Dimension(400, 230));
      MainPanel[1].setPreferredSize(new Dimension(400, 230));

      // this is the number of variables specified by
      //the user.
      JLabel numVarLbl = new JLabel("Number of variables:");
      numVarTf = new JTextField(4);
      //This Update button will add the rows to the table
      //in accordance with the number of variables specified by
      //the user. If the number of variable specified is too large
      // a scroll pane will be used. Also, user can change this
      // number of variables as many times as he wants
      // and this will update the number of rows in the table.
      JButton updateBt = new JButton("Update");

      /**
       The Color object, buttonColor, creates a yellowish
       tan color to applied to buttons
       */
      //Color buttonColor = new Color(255, 240, 40);
      //updateBt.setBackground( (buttonColor));

      updateBt.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent actionevent) {

          try {
            /**
             Table model created to hold number of objects needed based on entered variable number
             */
            for (int i = model.getRowCount();
                 i < Integer.parseInt(numVarTf.getText()); i++) {
              model.addRow(new Object[] {"x" + i, "x" + i, "",
                           "", "", ""});
            }
            for (int i = model.getRowCount();
                 i > Integer.parseInt(numVarTf.getText()); i--) {
              model.removeRow(i - 1);
            }

            /**
                 If previous values are stored in itinerary, initialized table model to them,
                 helps cache values so that saves time when entering values
             */
            /*CachedRowValue[] previousSaved = getSavedRows();
                           if(previousSaved != null) {
              for (int i = 0; i < model.getRowCount(); i++) {
                if (i < previousSaved.length && previousSaved[i] != null &&
                    previousSaved[i].getlower() != null &&
                    previousSaved[i].getupper() != null &&
                    previousSaved[i].getprecision() != null) {
                  model.setValueAt(previousSaved[i].getlower(), i, 2);
                  model.setValueAt(previousSaved[i].getupper(), i, 3);
                  model.setValueAt(previousSaved[i].getprecision(), i, 4);
                }
              }
                           }*/
            //revalidate();
            //repaint();
          }
          catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                "Please enter non-null/non-character in the text box",
                "alert", JOptionPane.ERROR_MESSAGE);

          }
          catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null,
                "Please enter positive value in the text box",
                "alert", JOptionPane.ERROR_MESSAGE);
          }
        }
      });

      // this button is pressed after the user has entered
      // the ranges for all the variables and also the
      // precision values for all the variables. When this button
      // is pressed it calculates the string length.
      // if all the values are not entered a warning dialog
      // box is displayed.
      //JButton calStrLenBt = new JButton("Calc. String Len");
      //calStrLenBt.setBackground(buttonColor);

      /*calStrLenBt.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent actionevent) {
          boolean flag = false;
          for (int i = 0; i < model.getRowCount(); i++) {
            if ( ( (String) model.getValueAt(i, 2)).trim().length() == 0) {
              flag = true;
              break;
            }

            if ( ( (String) model.getValueAt(i, 3)).trim().length() == 0) {
              flag = true;
              break;
            }
            if ( ( (String) model.getValueAt(i, 4)).trim().length() == 0) {
              flag = true;
              break;
            }
          }

          if (flag) {
            JOptionPane.showMessageDialog(null,
                                          "Please enter all information",
                                          "alert", JOptionPane.ERROR_MESSAGE);
          }

          //this is the total length of the binary
          //string that will be used to encode the
          //individuals in the GA population.
          else {
            int totalLength = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
              float numU, numL, numP, temp;

              int sLength;
              numU = Float.parseFloat( (String) model.getValueAt(i, 3));
              numL = Float.parseFloat( (String) model.getValueAt(i, 2));
              numP = Float.parseFloat( (String) model.getValueAt(i, 4));
              if (numU < numL) {
                JOptionPane.showMessageDialog(null,
                    "Upper Value is less than lower value at row" + (i + 1) +
                    " ", "alert",
                    JOptionPane.ERROR_MESSAGE);
                break;
              }
              temp = (numU - numL) / numP;
              temp = temp + 1;
              temp = (float) Math.log( (double) temp);
              double temp1 = 2.0;
              temp = (float) (temp / Math.log(temp1));
              sLength = (int) temp;
              if ( (temp - sLength) > 0.00001) {
                sLength = sLength + 1;
              }
              totalLength += sLength;
              model.setValueAt(new Integer(sLength), i, 5);
            }
            // display the total length of the string
            total_string_length.setText(" Total String Length : " +
                                        totalLength + " ");
          }
        }
      });*/

      /**
       * 4 panels created, 2 of JPanel type, and 2 of Box type
       * (box allows objects to positioned in a structured manner)
       */
      JPanel panel_0 = new JPanel();
      JPanel top_left_panel = new JPanel();
      Box bottom_left_panel = new Box(BoxLayout.X_AXIS);
      Box bottom_left_panel2 = new Box(BoxLayout.X_AXIS);

      /**Color object, background color created with certain rgb settings
       *
       */
      //Color background_color = new Color(235, 235, 255);

      /**
       * Panel top_left_panel property set: background color
       * Two objects added to top_left_panel
       */
      //top_left_panel.setBackground(background_color);
      top_left_panel.add(numVarLbl);
      top_left_panel.add(numVarTf);

      bottom_left_panel.add(Box.createHorizontalStrut(100));
      bottom_left_panel.add(updateBt);
      bottom_left_panel2.add(Box.createHorizontalStrut(55));
      //bottom_left_panel2.add(calStrLenBt);

      /**
       * myBox holds the left panels with rigid areas between them
       * myBox2 holds myBox
       */
      Box myBox;
      Box myBox2;
      myBox2 = new Box(BoxLayout.X_AXIS);
      myBox = new Box(BoxLayout.Y_AXIS);

      myBox.add(top_left_panel);
      myBox.add(Box.createRigidArea(new Dimension(1, 20)));
      myBox.add(bottom_left_panel);
      myBox.add(Box.createRigidArea(new Dimension(1, 15)));
      myBox.add(bottom_left_panel2);
      myBox2.add(myBox);

      (MainPanel[0]).add(myBox2);

      final String[] names = {
          "",
          "Name",
          "lower",
          "upper",
          "precision",
          "string length"
      };
      // model for the table

      /**
       * model initialized to defaultTableModel with 4 methods implemented
       * getColumnCount, getColumnName, getColumnClass, and isCellEditable
       */
      model = new DefaultTableModel() {
        public int getColumnCount() {
          return names.length;
        }

        public String getColumnName(int column) {
          return names[column];
        }

        /*public Class getColumnClass(int c) {
          return getValueAt(0, c).getClass();
        }*/

        public boolean isCellEditable(int row, int col) {
          return (col != 5 && col != 0);
        }

        public void setValueAt(Object value, int row, int col) {
          super.setValueAt(value, row, col);
          if (col == 4) {
            String val = (String)value;

            try {
//              double d = Double.parseDouble(val);
//System.out.println("hello: "+d);
              boolean flag = false;
              for (int i = 0; i < model.getRowCount(); i++) {
                if ( model.getValueAt(i, 2) != null && ((String) model.getValueAt(i, 2)).trim().length() == 0) {
                  flag = true;
                  break;
                }

                if ( model.getValueAt(i, 3) != null &&  ((String) model.getValueAt(i, 3)).trim().length() == 0) {
                  flag = true;
                  break;
                }
                //if ( ( (String) model.getValueAt(i, 4)).trim().length() == 0) {
                //  flag = true;
                //  break;
                //}
              }

              /*if (flag) {
                JOptionPane.showMessageDialog(null,
                                              "Please enter all information",
                                              "alert", JOptionPane.ERROR_MESSAGE);
              }*/

              //this is the total length of the binary
              //string that will be used to encode the
              //individuals in the GA population.
              //else {
                int totalLength = 0;
                for (int i = 0; i < model.getRowCount(); i++) {
                  float numU, numL, numP, temp;

                  int sLength;
                  numU = Float.parseFloat( (String) model.getValueAt(i, 3));
                  numL = Float.parseFloat( (String) model.getValueAt(i, 2));
                  numP = Float.parseFloat( (String) model.getValueAt(i, 4));
                  if (numU < numL) {
                    JOptionPane.showMessageDialog(null,
                        "Upper Value is less than lower value at row" + (i + 1) +
                        " ", "alert",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                  }
                  temp = (numU - numL) / numP;
                  temp = temp + 1;
                  temp = (float) Math.log( (double) temp);
                  double temp1 = 2.0;
                  temp = (float) (temp / Math.log(temp1));
                  sLength = (int) temp;
                  if ( (temp - sLength) > 0.00001) {
                    sLength = sLength + 1;
                  }
                  totalLength += sLength;
                  model.setValueAt(new Integer(sLength), i, 5);
                }
                // display the total length of the string
                total_string_length.setText(" Total String Length : " +
                                            totalLength + " ");
            }
            catch(Exception e) {
              return;
            }
          }
        }
      };

      CachedRowValue[] cvr = getSavedRows();
      if(cvr != null) {
        model.setRowCount(cvr.length);
        numVarTf.setText(Integer.toString(cvr.length));
        for(int i = 0; i < cvr.length; i++) {
          model.setValueAt(cvr[i].xNum, i, 0);
          model.setValueAt(cvr[i].varName, i, 1);
          model.setValueAt(cvr[i].lower, i, 2);
          model.setValueAt(cvr[i].upper, i, 3);
          model.setValueAt(cvr[i].precision, i, 4);
          model.setValueAt(cvr[i].length, i, 5);
        }
      }

      /**
       * table properties set: model object, background color,
       * gridcolor
       */
      tb = new JTable(model);
      DefaultCellEditor dce = new DefaultCellEditor(new JTextField());
      dce.setClickCountToStart(1);
      tb.getColumnModel().getColumn(1).setCellEditor(dce);
      tb.getColumnModel().getColumn(2).setCellEditor(dce);
      tb.getColumnModel().getColumn(3).setCellEditor(dce);
      tb.getColumnModel().getColumn(4).setCellEditor(dce);
      //tb.setBackground(background_color);
      tb.setGridColor(Color.lightGray);
      //tb.setSelectionBackground(buttonColor);

      /**
       * MainPanels properties set:  background color
       */
      //MainPanel[0].setBackground(background_color);
      //MainPanel[1].setBackground(background_color);
      //panel_0.setBackground(background_color);

      /**
       * Scrollpane object intialized with added table object
       * Also, properties set for JScrollPane: min, preferred size,
       * and background color
       */
      jsp1 = new JScrollPane(tb);
      jsp1.setPreferredSize(new Dimension(400, 100));
      jsp1.setMinimumSize(new Dimension(400, 100));
      //jsp1.setBackground(background_color);

      /**
       * Add jscrollpane to mainpanel
       */
      MainPanel[1].add(jsp1);

      /**
       * Box object, string_length_panel, is a panel that holds
       * the label object, total_string_length
       */
      Box string_length_panel = new Box(BoxLayout.X_AXIS);
      string_length_panel.add(Box.createHorizontalStrut(200));
      string_length_panel.add(total_string_length);

      MainPanel[1].add(string_length_panel);
      /**
       * Panel of JPanel type, holds buttons at bottom
       */
      JPanel bottom_button_panel = new JPanel();

      // user presses the done button when he has finished
      // filing in all the information
      // acheck is made to make sure that every needed information
      // is fed in by the user and then the output is passed.
      //if something is missing then a warning dialog box appears.
      JButton doneBt = new JButton("Done");
      doneBt.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent actionevent) {
          boolean flag = false;
          for (int i = 0; i < model.getRowCount(); i++) {
            if ( ( (String) model.getValueAt(i, 2)).trim().length() == 0) {
              flag = true;
              break;
            }

            if ( ( (String) model.getValueAt(i, 3)).trim().length() == 0) {
              flag = true;
              break;
            }
            if ( ( (String) model.getValueAt(i, 4)).trim().length() == 0) {
              flag = true;
              break;
            }
          }

          if (flag) {
            JOptionPane.showMessageDialog(null,
                                          "Please enter all information",
                                          "alert", JOptionPane.ERROR_MESSAGE);
          }
          else {
            passOutput();
          }

          // added by DC
          CachedRowValue[] saves = new CachedRowValue[model.getRowCount()];
          for (int i = 0; i < model.getRowCount(); i++) {
            saves[i] = new CachedRowValue();
            saves[i].xNum = model.getValueAt(i, 0);
            saves[i].varName = model.getValueAt(i, 1);
            saves[i].lower = model.getValueAt(i, 2);
            saves[i].upper = model.getValueAt(i, 3);
            saves[i].precision = model.getValueAt(i, 4);
            saves[i].length = model.getValueAt(i, 5);
          }
          setSavedRows(saves);

          viewDone("Done");
        }
      });

      // abort button is also provide
      //when this is pressed all open windows are closed
      //and itineary is aborted
      JButton abrtBt = new JButton("Abort");
      abrtBt.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent actionevent) {
          viewCancel();
        }
      });

      /**
       * bottom_button_panel initialized to JPanel.
       * Two button objects added to it: done button and abort button
       * Property set:  background color
       */
      bottom_button_panel = new JPanel();
      //bottom_button_panel.setBackground(background_color);
      bottom_button_panel.add(abrtBt);
      bottom_button_panel.add(doneBt);

      /**
       * Mainpanel adds bottom_button_panel
       */
      MainPanel[1].add(bottom_button_panel);
      add(MainPanel[0], BorderLayout.WEST);
      JPanel myPanel = new JPanel();
      myPanel.add(new JLabel(" "));
      //myPanel.setBackground(background_color);
      JPanel myPanel2 = new JPanel();
      myPanel2.add(new JLabel("        "));
      //myPanel2.setBackground(background_color);
      add(myPanel2, BorderLayout.EAST);
      add(myPanel, BorderLayout.NORTH);

      add(MainPanel[1], BorderLayout.CENTER);

    }

    public void setInput(Object object, int index) {}

    /**
     * this method is used to pass output from the module
     * a mutable table is created based on the variable
     * information provided by the user and is passed as
     * an output. Also, the default table model of the
     * displayed table is also passed.
     */
    private void passOutput() {
      table1 = new MutableTableImpl(Integer.parseInt(numVarTf.getText()));
      float[] tempdata = new float[0];
      for (int i = 0; i < (Integer.parseInt(numVarTf.getText())); i++) {
        table1.setColumn(tempdata, i);
        table1.setColumnLabel( (String) (model.getValueAt(i, 1)), i);
      }
      table1.setNumRows(0);

      int numVars = model.getRowCount();
      Column[] cols = new Column[5];
      cols[0] = new StringColumn(numVars);
      cols[1] = new FloatColumn(numVars);
      cols[2] = new FloatColumn(numVars);
      cols[3] = new FloatColumn(numVars);
      cols[4] = new FloatColumn(numVars);

      for (int i = 0; i < numVars; i++) {
        cols[0].setString( (String) model.getValueAt(i, 1), i);
        String min = (String) model.getValueAt(i, 2);
        cols[1].setString(min, i);
        String max = (String) model.getValueAt(i, 3);
        cols[2].setString(max, i);
        String pred = (String)model.getValueAt(i, 4);
        cols[3].setString(pred, i);
        Object len = model.getValueAt(i, 5);
        cols[4].setObject(len, i);
      }
      Table varTable = new MutableTableImpl(cols);

      EMOPopulationInfo data = new EMOPopulationInfo();
      data.boundsAndPrecision = varTable;
      data.varNames = table1;

      pushOutput(data, 0);

      //pushOutput(table1, 0);
      //pushOutput(varTable, 1);
    }
  }

  /**
   * <code>CachedRowValue</code> is a simple class that contains the elements of
   * one row in a JTable with six headers.
   *
   * @author navarrob
   */
  private class CachedRowValue
      implements Serializable {

    /**
     * The six columns of one row in a JTable of NewEmo1.java
     */
    private Object xNum;
    private Object varName;
    private Object lower;
    private Object upper;
    private Object precision;
    private Object length;
  }
}