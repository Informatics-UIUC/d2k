package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import ncsa.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

/**
 Newemo1.java
*/
public class DefineDecisionVariables
    extends UIModule
    implements Serializable {

  public String getModuleName() {
    return "Define Decision Variables";
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getInputName(int index) {
    return "";
  }

  public String getInputInfo(int index) {
    return "";
  }

  /**
     This pair returns an array of strings that contains the data types for the outputs.
     @return the data types of all outputs.
  */
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationParams"};
    return types;
  }

  /**
     This pair returns the description of the outputs.
     @return the description of the indexed output.
  */
  public String getOutputName(int i) {
    return "EMOParams";
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        String s = "A data structure containing the information about the decision variables.";
        return s;
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
    s += "Define the decision variables.";
    s += "<p>Detailed Description: ";
    s += "Define all the decision variables for the problem.  The variables ";
    s += "can be entered manually or can be loaded from a file.  To enter the ";
    s += "variables manually, input the number of decision variables and hit the ";
    s += "update button.  To load the variables from a file, hit the Read From File button ";
    s += "and select the file to load.  The file ";
    s += "should have four columns: name, lower bound, upper bound, and precision, ";
    s += "in that order.";
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

    private JTextField numVarTf;
    private DefaultTableModel model;
    private JLabel total_string_length = new JLabel("Total String Length:");

    public void initView(ViewModule viewmodule) {
      setLayout(new BorderLayout());
      JPanel MainPanel[] = new JPanel[2];

      MainPanel[0] = new JPanel();
      MainPanel[1] = new JPanel();

      MainPanel[1].setLayout(new BoxLayout(MainPanel[1], BoxLayout.Y_AXIS));
      /*MainPanel[0].setMinimumSize(new Dimension(200, 50));
      MainPanel[0].setPreferredSize(new Dimension(200, 50));
      MainPanel[1].setMinimumSize(new Dimension(400, 230));
      MainPanel[1].setPreferredSize(new Dimension(400, 230));
*/

      // this is the number of variables specified by
      //the user.
      JLabel numVarLbl = new JLabel("Number of variables:");
      numVarTf = new JTextField(4);
      numVarTf.setHorizontalAlignment(JTextField.CENTER);

      //This Update button will add the rows to the table
      //in accordance with the number of variables specified by
      //the user. If the number of variable specified is too large
      // a scroll pane will be used. Also, user can change this
      // number of variables as many times as he wants
      // and this will update the number of rows in the table.
      //JButton updateBt = new JButton("Update");
      JButton readFromFileBt = new JButton("Read From File");
      readFromFileBt.setEnabled(false);
      JButton seedFromFileBt = new JButton("Seed From File");
      seedFromFileBt.setEnabled(false);

      /**
       The Color object, buttonColor, creates a yellowish
       tan color to applied to buttons
       */
      //Color buttonColor = new Color(255, 240, 40);
      //updateBt.setBackground( (buttonColor));

      ActionListener al = new ActionListener() {
        public void actionPerformed(ActionEvent actionevent) {
          try {
            /**
             Table model created to hold number of objects needed based on
             entered variable number
            */
            for (int i = model.getRowCount();
                 i < Integer.parseInt(numVarTf.getText()); i++) {
              model.addRow(new Object[] {Integer.toString(i), "x" + i, "",
                           "", "", ""});
            }
            for (int i = model.getRowCount();
                 i > Integer.parseInt(numVarTf.getText()); i--) {
              model.removeRow(i - 1);
            }
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
      };
      //updateBt.addActionListener(al);
      numVarTf.addActionListener(al);

      readFromFileBt.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          readFromFile();
        }
      });

      /**
       * 4 panels created, 2 of JPanel type, and 2 of Box type
       * (box allows objects to positioned in a structured manner)
       */
/*      JPanel panel_0 = new JPanel();
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
/*      top_left_panel.add(numVarLbl);
      top_left_panel.add(numVarTf);

      bottom_left_panel.add(Box.createHorizontalGlue());
      bottom_left_panel.add(updateBt);
      bottom_left_panel2.add(Box.createHorizontalGlue());
      bottom_left_panel2.add(readFromFileBt);

      /**
       * myBox holds the left panels with rigid areas between them
       * myBox2 holds myBox
       */
/*      Box myBox;
      Box myBox2;
      myBox2 = new Box(BoxLayout.X_AXIS);
      myBox = new Box(BoxLayout.Y_AXIS);

      /*myBox.add(top_left_panel);
      myBox.add(Box.createRigidArea(new Dimension(1, 15)));
      myBox.add(bottom_left_panel);
      myBox.add(Box.createRigidArea(new Dimension(1, 15)));
      myBox.add(bottom_left_panel2);
      myBox2.add(myBox);
*/

      JPanel varPanel = new JPanel();
      varPanel.setLayout(new GridBagLayout());
      JPanel lblPanel = new JPanel();
      lblPanel.add(numVarLbl);
      lblPanel.add(numVarTf);
      Constrain.setConstraints(varPanel, lblPanel, 0, 0, 2, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.CENTER,
                               1,1);
/*      Constrain.setConstraints(varPanel, numVarTf, 1, 0, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.CENTER,
                               1,1);
      Constrain.setConstraints(varPanel, new JPanel(), 0, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.EAST,
                               1,1);
      Constrain.setConstraints(varPanel, updateBt, 1, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.EAST,
                               0,0);*/
      Constrain.setConstraints(varPanel, new JPanel(), 0, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.EAST,
                               1,1);
      Constrain.setConstraints(varPanel, readFromFileBt, 1, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.EAST,
                               0,0);
      Constrain.setConstraints(varPanel, new JPanel(), 0, 2, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.EAST,
                               1,1);
      Constrain.setConstraints(varPanel, seedFromFileBt, 1, 2, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.EAST,
                               0,0);

      varPanel.setBorder(new EmptyBorder(5,5,5,0));
      (MainPanel[0]).add(varPanel);

      final String[] names = {
          "",
          "Name",
          "Lower",
          "Upper",
          "Precision",
          "String Length"
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

        public boolean isCellEditable(int row, int col) {
          return (col != 5 && col != 0);
        }

        public void setValueAt(Object value, int row, int col) {
          super.setValueAt(value, row, col);
          if (col == 4) {

            try {
              /*boolean isok = true;
              for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 2) != null &&
                    ( (String) model.getValueAt(i, 2)).trim().length() == 0) {
                  isok = true;
                  break;
                }

                if (model.getValueAt(i, 3) != null &&
                    ( (String) model.getValueAt(i, 3)).trim().length() == 0) {
                  isok = true;
                  break;
                }
                if ( ( (String) model.getValueAt(i, 4)).trim().length() == 0) {
                  isok = true;
                  break;
                }
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
                float numU, numL, numP, numBits;

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
                numBits = (numU - numL + 1) / numP;
                numBits = (float) Math.log( (double) numBits);
                numBits = (float) (numBits / Math.log(2.0));

                // now we know the number of bits required to represent
                // a number in this interval

                // the string length must be an integer, so we will need to
                // round up if numBits is not an integer

                sLength = (int)Math.floor(numBits);
                if(sLength < numBits)
                  sLength++;

                totalLength += sLength;
                model.setValueAt(new Integer(sLength), i, 5);
              }
              // display the total length of the string
              total_string_length.setText(" Total String Length : " +
                                          totalLength + " ");
            }
            catch (Exception e) {
              return;
            }
          }
        }
      };

      CachedRowValue[] cvr = getSavedRows();
      if (cvr != null) {
        model.setRowCount(cvr.length);
        numVarTf.setText(Integer.toString(cvr.length));
        for (int i = 0; i < cvr.length; i++) {
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
      JTable tb = new JTable(model);
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
      JScrollPane jsp1 = new JScrollPane(tb);
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
      string_length_panel.setBorder(new EmptyBorder(10, 5,5,5));

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
      bottom_button_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      //bottom_button_panel.setBackground(background_color);
      bottom_button_panel.add(abrtBt);
      bottom_button_panel.add(doneBt);
      bottom_button_panel.setBorder(new EmptyBorder(5,5,5,5));

      /**
       * Mainpanel adds bottom_button_panel
       */
      MainPanel[0].setBorder(new EmptyBorder(10, 5,5,5));
      MainPanel[1].setBorder(new EmptyBorder(10, 5,5,5));
      MainPanel[1].add(bottom_button_panel);
      add(MainPanel[0], BorderLayout.WEST);
      //JPanel myPanel = new JPanel();
      //myPanel.add(new JLabel(" "));
      //myPanel.setBackground(background_color);
      //JPanel myPanel2 = new JPanel();
      //myPanel2.add(new JLabel("        "));
      //myPanel2.setBackground(background_color);
      //add(myPanel2, BorderLayout.EAST);
      //add(myPanel, BorderLayout.NORTH);

      add(MainPanel[1], BorderLayout.CENTER);
      JLabel lbl = new JLabel("Define Decision Variables");
      Font f = lbl.getFont();
      Font newFont = new Font(f.getFamily(), Font.BOLD, 16);
      lbl.setFont(newFont);
      lbl.setBorder(new EmptyBorder(10, 10, 10, 0));
      add(lbl, BorderLayout.NORTH);
    }

    public void setInput(Object object, int index) {}

    protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(g2);
    }


    /**
     * this method is used to pass output from the module
     * a mutable table is created based on the variable
     * information provided by the user and is passed as
     * an output. Also, the default table model of the
     * displayed table is also passed.
     */
    private void passOutput() {
      MutableTable table1 = new MutableTableImpl(model.getRowCount());
      float[] tempdata = new float[0];
      for (int i = 0; i < model.getRowCount(); i++) {
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
        String pred = (String) model.getValueAt(i, 4);
        cols[3].setString(pred, i);
        Object len = model.getValueAt(i, 5);
        cols[4].setObject(len, i);
      }
      MutableTableImpl varTable = new MutableTableImpl(cols);

      EMOPopulationParams data = new EMOPopulationParams();
      data.boundsAndPrecision = varTable;
      data.varNames = table1;

      pushOutput(data, 0);
    }

    private void readFromFile() {
      JFileChooser jfc = new JFileChooser();
      int retVal = jfc.showOpenDialog(null);

      if (retVal == jfc.APPROVE_OPTION) {
        String file = jfc.getSelectedFile().getAbsolutePath();

        try {
          BufferedReader br = new BufferedReader(new FileReader(file));
          String line = null;

          int numLines = 0;
          while( (line = br.readLine()) != null)
            numLines++;

          numVarTf.setText(Integer.toString(numLines));
          // now set the model to have this number of lines
          model.setNumRows(numLines);
          for(int i = 0; i < numLines; i++)
            model.setValueAt("x"+i, i, 0);

          br = new BufferedReader(new FileReader(file));

          int lineNum = 0;
          while ( (line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line);
            if(st.countTokens() != 4)
              throw new Exception("File format is incorrect.  File should have exactly four columns.");
            int colNum = 0;
            while(st.hasMoreTokens()) {
              String s = st.nextToken();

              model.setValueAt(s, lineNum, colNum+1);
              colNum++;
            }

            lineNum++;
          }
        }
        catch (FileNotFoundException ex) {
          JOptionPane.showMessageDialog(null, "The file was not found.",
                                        "File not found",
                                        JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e) {
        }
        catch(Exception e) {
          JOptionPane.showMessageDialog(null, e.getMessage(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
        }
      }
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

  static final long serialVersionUID = 7134596339214916408L;

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