package ncsa.d2k.modules.core.optimize.ga.emo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.d2k.modules.core.transform.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.gui.*;

/**
 * <code>FilterConstruction</code> is a simple user interface that facilitates
 * the creation of an expression for filtering rows from a
 * <code>MutableTable</code>. (See the documentation for
 * <code>FilterExpression</code> for details on the underlying expression
 * string and its format.)
 */
public class DefineConstraintFunctions
    extends AttributeConstruction {

  public String getModuleName() {
    return "Define Constraint Functions";
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
    return out;
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getInputName(int i) {
    return "EMOPopulationInfo";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getOutputName(int i) {
    return "EMOPopulationInfo";
  }

  protected UserView createUserView() {
    return new ConstraintsByFormulaGUI();
  }

  /** Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription [0];
      return pds;
  }


  //inner class, extends attributeConstruction's inner gui class
  public class ConstraintsByFormulaGUI
      extends ColumnConstructionGUI {

    //create an istance of FilterExpression
    private FilterExpression expression1;

    private JTable tableData;
    private DefaultTableModel modelData;
    private JScrollPane jsp;

    private JButton timeButton;
    // columnModel keeps track of all the already present columns in
    // the table as well as all the new columns defined recently
    // using the widget. It is used in the redoBox() function.
    private DefaultComboBoxModel columnModel;

    private EMOPopulationInfo data;

    public void setInput(Object o, int i) {
      if (i != 0) {
        return;
      }
      data = (EMOPopulationInfo)o;
      table = (MutableTable)data.varNames;
      initialize();
    }

    public Dimension getPreferredSize() {
      return new Dimension(750, 350);
    }

    // function to repaint the columnBox
    // since new column are added by the user
    // the columnBox needs to be continuously updated so
    // that recently defined defined variables can be used to define
    //other variables
    public void redoBox() {
      Object[] constructions;
      constructions = (newColumnModel.toArray());
      columnModel = new DefaultComboBoxModel();
      int i = 0;
      // add elements in the columnModel using the table
      for (i = 0; i < table.getNumColumns(); i++) {
        columnModel.addElement(table.getColumnLabel(i));
      }

      if(constructions != null) {
        for(int j = 0; j < constructions.length; j++) {
          columnModel.addElement( ((EMOConstruction)constructions[j]).label);
        }
      }

      // set the columnBox model to the recently
      // updated columnModel
      columnBox.setModel(columnModel);
    }

    public void initialize() {
      //Initialize method called of class that is extended
      super.initialize();

      //Initialize FilterExpression object
      expression1 = new FilterExpression(table);
      //Expression gui object reinitialized using filterexpression object
      gui = new ExpressionGUI(expression1, true);
      gui.addExpressionListener(this);

      //GuiPanel removes all components on its panel, changed components to be added
      guiPanel.removeAll();
      guiPanel.setLayout(new GridBagLayout());

      // Panel newNamePanel created as JPanel
      newNamePanel = new JPanel();

      /**
       * newNameField textfield created as new JTextfield, 16 spaces wide
       * 3 Properties set: Layout, minimum size, and preferredsize
       */
      newNameField = new JTextField(16);
      newNamePanel.setLayout(new GridBagLayout());
      newNameField.setMinimumSize(new Dimension(180, 20));
      newNameField.setPreferredSize(new Dimension(180, 20));
      newNamePanel.setLayout(new GridBagLayout());

      /**
       * newNamePanel adds newNameField with constraints
       */
      Constrain.setConstraints(newNamePanel, newNameField, 0, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.CENTER, 1, 0);

      constructionLabel.setText("Formulate Constraints");

      /**
       * guiPanel adds newNamePanel, constructionLabel, and gui
       * all with constraints (alignments, weights, etc.)
       */
      Constrain.setConstraints(guiPanel, newNamePanel, 0, 0, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(guiPanel, constructionLabel, 0, 1, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(guiPanel, gui, 0, 2, 1, 1,
                               GridBagConstraints.BOTH,
                               GridBagConstraints.CENTER, 1, 1);

      //initialize columnModel
      columnModel = new DefaultComboBoxModel();
      columnBox.setModel(columnModel);
      for (int i = 0; i < table.getNumColumns(); i++) {
        columnModel.addElement(table.getColumnLabel(i));
      }

      //Strings array which represents the column labels of tableData
      final String[] names = {
          "",
          "Time"
      };

      modelData = new DefaultTableModel() {
        public int getColumnCount() {
          return names.length;
        }

        public String getColumnName(int column) {
          return names[column];
        }

        public Class getColumnClass(int c) {
          return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int r, int c) {
          return false;
        }
      };

      Object[] last = (Object[])getLastCons();
      if(last != null ) {
        for (int i = 0; i < last.length; i++) {
          EMOConstruction constr = (EMOConstruction) last[i];
          modelData.addRow(new Object[] {constr.getLabel()+":"+
              constr.getExpression(), new String()});
        }
      }

      /**
       * tableData object initialized to JTable, DefaultTableModel parameter
       * Property of tableData set: GridColor
       */
      tableData = new JTable(modelData);
      //tableData.setGridColor(Color.LIGHT_GRAY);

      /**
       * JScrollPane jsp intialized with JTable parameter
       * Four properties of jsp set: verticalScrollBar policy,
       * horizontalScrollbar policy, Minimum size, and preferred size
       */
      jsp = new JScrollPane(tableData);
      jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jsp.setHorizontalScrollBarPolicy(JScrollPane.
                                       HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jsp.setPreferredSize(new Dimension(220, 200));
      jsp.setMinimumSize(new Dimension(220, 200));

      /**
       * New tableColumn created
       */
      TableColumn maxColumn = new TableColumn();

      /**
           * DefaultTableCellRenderer object created to control attributes of table cells
       */
      DefaultTableCellRenderer myRenderer = new DefaultTableCellRenderer();
      myRenderer.setHorizontalAlignment(SwingConstants.CENTER);
      tableData.setDefaultRenderer(tableData.getClass(), myRenderer);

      /**
       *  rightPanel removes all components on its panel, changed components to be added
       * One property set: setLayout
       */
      rightPanel.setLayout(new GridBagLayout());
      rightPanel.removeAll();
      /**
           * TimeButton initialized to JButton, properties set: preferredSize, minimumSize
       */
      timeButton = new JButton("Time it");
      timeButton.setPreferredSize(new Dimension(75, 30));
      timeButton.setMinimumSize(new Dimension(75, 30));

      /**
       * deleteButton initialized to JButton, properties set: preferredSize, minimumSize
       */
      deleteButton.setPreferredSize(new Dimension(70, 30));
      deleteButton.setMinimumSize(new Dimension(70, 30));

      /**
       * buttonPanel initialized to JPanel and property set: setLayout
       */
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new GridBagLayout());
      /**
       * timeText initialized to JTextfield, 10 spaces wide,
       * two properties set: minimumSize and preferredSize
       */
      JTextField timeText = new JTextField(10);
      timeText.setMinimumSize(new Dimension(50, 20));
      timeText.setPreferredSize(new Dimension(50, 20));

      /**
       * Panel button_boxPanel and textlabel_boxPanel initialized to Box
       */
      Box button_boxPanel = new Box(BoxLayout.X_AXIS);
      Box textlabel_boxPanel = new Box(BoxLayout.X_AXIS);

      /**
       * Panel button_boxPanel adds three items: timeButton, horizontal strut
       * and delete button
       */
      button_boxPanel.add(timeButton);
      button_boxPanel.add(Box.createHorizontalStrut(50));
      button_boxPanel.add(deleteButton);

      /**
       * Panel textlabe_boxPanel adds three items: horizontal strut, JLabel
       * and timeText
       */

      textlabel_boxPanel.add(Box.createHorizontalStrut(70));
      textlabel_boxPanel.add(new JLabel("Total Time: "));
      textlabel_boxPanel.add(timeText);

      /**
       * buttonPanel adds textlabel_boxPanel, and button_boxPanel
       * all with constraints (alignments, weights, etc.)
       */
      Constrain.setConstraints(buttonPanel, textlabel_boxPanel, 0, 0, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.CENTER, 0, 0);
      Constrain.setConstraints(buttonPanel, button_boxPanel, 0, 2, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.CENTER, 0, 0);

      /**
       * brightPanel adds JLabel, new JScrollPane, and buttonPanel
       * with constraints (alignments, weights, etc.)
       */
      Constrain.setConstraints(rightPanel,
                               new JLabel("New attributes defined:  "), 0, 0, 1,
                               1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(rightPanel, jsp, 0, 1, 1, 1,
                               GridBagConstraints.BOTH,
                               GridBagConstraints.CENTER, 1, 1);
      Constrain.setConstraints(rightPanel, buttonPanel, 0, 2, 0, 0,
                               GridBagConstraints.NONE,
                               GridBagConstraints.SOUTH, 0, 0);

      //Change the operator's used from addition-sub-mult-mod-div to
      //Comparison operators
      //ADD Item's
      operationBox.addItem("==");
      operationBox.addItem("!=");
      operationBox.addItem("<");
      operationBox.addItem("<=");
      operationBox.addItem(">");
      operationBox.addItem(">=");
      //REMOVE Item's
      operationBox.removeItem("+");
      operationBox.removeItem("-");
      operationBox.removeItem("*");
      operationBox.removeItem("/");
      operationBox.removeItem("%");

      //Remove all components from operationPanel
      operationPanel.removeAll();

      //ReAdd all components (some changed) back to operationPanel
      operationPanel.setLayout(new GridBagLayout());
      Constrain.setConstraints(operationPanel, new JLabel(), 0, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.CENTER, 1, 0);
      Constrain.setConstraints(operationPanel, operationBox, 1, 0, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.EAST,
                               0, 0);
      Constrain.setConstraints(operationPanel, addOperationButton, 2, 0, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.EAST,
                               0, 0);

      //bottomPanel.removeAll();
    }

    // define actionlisteners
    public void actionPerformed(ActionEvent e) {

      Object src = e.getSource();
      // add the expression written in gui
      // to the table and rightpanel
      /*if (src == addColumnButton) {
        gui.getTextArea().insert( (String) columnBox.getSelectedItem(),
                                 gui.getTextArea().getCaretPosition());

        // add scalar to the gui expression
      }
      else if (src == addScalarButton) {
        gui.getTextArea().insert(scalarField.getText(),
                                 gui.getTextArea().getCaretPosition());

        // add the operator to gui expression
      }
      else if (src == addOperationButton) {
        gui.getTextArea().insert(" " + operationBox.getSelectedItem() + " ",
                                 gui.getTextArea().getCaretPosition());

        // add boolean to the gui expression
      }
      else if (src == addBooleanButton) {
        gui.getTextArea().insert(" " + booleanBox.getSelectedItem() + " ",
                                 gui.getTextArea().getCaretPosition());

        /**
         * Delete item from list
         */

      //}*/
      if (src == deleteButton) {
        //Selected row index of table retrieved and stored
        int selected2 = tableData.getSelectedRow();

        //Index of item in the list calculated here
        int item = (table.getNumColumns()) - (tableData.getRowCount()) +
            (selected2);

        if (selected2 != -1) { //if selected exist
          String label = ( (EMOConstruction) (newColumnModel.elementAt(
              selected2))).label;

          /**
           * Remove label from columnBox, label from the columnModel,
           * remove row from modelData
           * table columnLabel null at item's location
           * Remove item at selected2 index from newColumnModel
           */

          columnBox.removeItem(label);
          columnModel.removeElement(label);
          modelData.removeRow(selected2);
          table.setColumnLabel(null, item);
          table.removeColumn(item);
          newColumnModel.removeElementAt(selected2);
        }
      }
      else if(src == this.doneButton) {
         //help.setVisible(false);
         //pushOutput(new AttributeTransform(newColumnModel.toArray()), 0);
         //myConstructions = new ArrEMOConstruction(ccwVar.getnewConstructions(),
         //     ccwFit.getnewConstructions());

         constructions = (Object[])getLastCons();
         if(constructions != null) {
           EMOConstruction[] tmp = new EMOConstruction[constructions.length];
           for (int i = 0; i < constructions.length; i++) {
             tmp[i] = (EMOConstruction) constructions[i];

             float[] tmpfloat = new float[0];
             // add an empty column of floats to the table
             table.addColumn(tmpfloat);
             // set the label of the new column added to the table
             table.setColumnLabel(tmp[i].getLabel(), (table.getNumColumns() - 1));
           }

           if (data.constraintFunctionConstructions == null) {
             data.constraintFunctionConstructions = tmp;
           }
           // append the new constructions onto the older constructions
           else {
             EMOConstruction[] tmp2 = new EMOConstruction[tmp.length +
                 data.constraintFunctionConstructions.length];
             int i = 0;
             for (; i < data.constraintFunctionConstructions.length; i++)
               tmp2[i] = data.constraintFunctionConstructions[i];
             int j = 0;
             for (; i < tmp2.length; i++) {
               tmp2[i] = tmp[j];
               j++;
             }
             data.constraintFunctionConstructions = tmp2;
           }
         }

         pushOutput(data, 0);
         viewDone("Done");
      }
      else
        super.actionPerformed(e);
    }

    /**
     * this is the action listener attached with the addButton
     * it adds the column to the table and take all the necessary actions
     */
    public void expressionChanged(Object evaluation) {
      // is no new name field given display error dialog
      if (newNameField.getText().length() == 0) {
        JOptionPane.showMessageDialog(this,
                                      "You must specify a new column name.",
                                      "Define Constraints By Formula",
                                      JOptionPane.ERROR_MESSAGE);
      }
      else {
        StringBuffer newLabelBuffer = new StringBuffer(newNameField.getText());

        for (int i = 0; i < newLabelBuffer.length(); i++) {
          if (newLabelBuffer.charAt(i) == '\\') {
            newLabelBuffer.deleteCharAt(i);
            i--;
          }
        }

        FilterExpression newExp = new FilterExpression(table);

        String newLab = newLabelBuffer.toString();
        //String newStr = gui.getTextArea().getText();

        // add to arrays of new expressions, labels, types

        /*if (newExpressions == null) {
          newExpressions = new FilterExpression[1];
          newExpressions[0] = newExp;
        }
        else {
          FilterExpression[] newNewExp = new FilterExpression[newExpressions.
              length + 1];
          for (int i = 0; i < newExpressions.length; i++) {
            newNewExp[i] = newExpressions[i];
          }
          newNewExp[newExpressions.length] = newExp;
          newExpressions = newNewExp;
        }

        if (newLabels == null) {
          newLabels = new String[1];
          newLabels[0] = newLab;
        }
        else {
          String[] newNewLab = new String[newLabels.length + 1];
          for (int i = 0; i < newLabels.length; i++) {
            newNewLab[i] = newLabels[i];
          }
          newNewLab[newLabels.length] = newLab;
          newLabels = newNewLab;
        }

        if (newExpressionStrings == null) {
          newExpressionStrings = new String[1];
          newExpressionStrings[0] = newStr;
        }
        else {
          String[] newNewStr = new String[newExpressionStrings.length + 1];
          for (int i = 0; i < newExpressionStrings.length; i++) {
            newNewStr[i] = newExpressionStrings[i];
          }
          newNewStr[newExpressionStrings.length] = newStr;
          newExpressionStrings = newNewStr;
        }

        if ( (newLabels != null) && (newExpressions != null)) {
          newConstructions = new EMOConstruction[newLabels.length];
          for (int i = 0; i < newLabels.length; i++) {
            newConstructions[i] = new EMOConstruction( (String) newLabels[i],
                (String) newExpressionStrings[i]);
          }
        }*/

        //newExp = new FilterExpression(table);
        // if the expression is valid set the expression of gui
        gui.setExpression(newExp);
        // add the expression to the newColumnModel

        modelData.addRow(new Object[] {newNameField.getText()+":"+
                         gui.getTextArea().getText(), new String()});

        //columnModel.addElement(new String(newNameField.getText() +
        //                                  gui.getTextArea().getText()));

        EMOConstruction added = new EMOConstruction(newNameField.getText(),
            gui.getTextArea().getText());
        newColumnModel.addElement(added);

        _newLab = newLabels;
        _newTyp = newTypes;
        _lastCons = newColumnModel.toArray();

        //reset the new name field
        newNameField.setText("");
        //reset the gui text area
        gui.getTextArea().setText("");
      }
    }
  }
}