package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.gui.*;

import ncsa.d2k.modules.core.datatype.table.transformations.Construction;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

/**
 * Define the fitness functions.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DefineFitnessFunctions
    extends AttributeConstruction {

  public String getModuleName() {
    return "Define Fitness Functions";
  }

  public String getModuleInfo() {
    return "";
  }

  protected UserView createUserView() {
    return new FFConstructionGUI();
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOParams"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOParams"};
    return out;
  }

  public String getInputName(int i) {
    return "EMOParams";
  }

  public String getOutputName(int i) {
    return "EMOParams";
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  private static final String MIN = "Min";
  private static final String MAX = "Max";

  /** Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription [0];
      return pds;
  }

  /**
   * inner class, extends attributeConstruction's inner gui class
   */
  public class FFConstructionGUI
      extends ColumnConstructionGUI {

    //for use in determining time
    //private JButton timeButton;
    //for use in displaying fitness function
    private JTable tableData;
    private DefaultTableModel modelData;
    private EMOParams data;

    public void setInput(Object o, int i) {
      data = (EMOParams) o;
//      this.table = (MutableTable) data.varNames;
      this.table = data.decisionVariables.createVariableNameTable();
      this.initialize();
    }

    public Dimension getPreferredSize() {
      return new Dimension(800, 400);
    }

    // function to repaint the columnBox
    // since new column are added by the user
    // the columnBox needs to be continuously updated so
    // that recently defined defined variables can be used to define
    //other variables
    public void redoBox() {
      Object[] constructions;
      constructions = (newColumnModel.toArray());
      DefaultComboBoxModel columnModel = new DefaultComboBoxModel();
      int i = 0;
      // add elements in the columnModel using the table
      for (i = 0; i < table.getNumColumns(); i++) {
        columnModel.addElement(table.getColumnLabel(i));
      }

      if(constructions != null) {
        for(int j = 0; j < constructions.length; j++) {
          columnModel.addElement( ((Construction)constructions[j]).label);
        }
      }

      // set the columnBox model to the recently
      // updated columnModel
      columnBox.setModel(columnModel);
    }

    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(g2);
    }

    public void initialize() {
      //Initialize method called of class that is extended
      //super.setValues(newExpressionStrings, guiTypes, guiConstructions);
      super.initialize();

      //columnModel = new DefaultComboBoxModel();
      //Strings array which represents the column labels of tableData
      final String[] names = {
          "",
          "",
          //"Time"
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
          if (c == 0) {
            return true;
          }
          else {
            return false;
          }
        }

        public void setValueAt(Object o, int r, int c) {
          super.setValueAt(o, r, c);

          if (c == 0) {
            Object[] cons = (Object[]) getLastCons();
//            ( (EMOConstruction) cons[r]).setmyflag(1);
            if (o.toString().equalsIgnoreCase(MIN)) {
              ( (FitnessFunctionConstruction) cons[r]).setIsMinimizing(true);
            }
            else {
              ( (FitnessFunctionConstruction) cons[r]).setIsMinimizing(false);
            }
          }
        }
      };

      Object[] last = (Object[])getLastCons();
      if(last != null ) {
        for (int i = 0; i < last.length; i++) {
          FitnessFunctionConstruction constr = (FitnessFunctionConstruction) last[i];
          if (constr.getIsMinimizing())
            modelData.addRow(new Object[] {MIN, constr.label+" = "+
                             constr.expression/*, new String()*/});
          else
            modelData.addRow(new Object[] {MAX, constr.label+" = "+
                             constr.expression/*, new String()*/});
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
      JScrollPane jsp = new JScrollPane(tableData);
      jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jsp.setHorizontalScrollBarPolicy(JScrollPane.
                                       HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jsp.setPreferredSize(new Dimension(200, 200));
      jsp.setMinimumSize(new Dimension(200, 200));

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
       * method: setMaxMinColumn called, sets the first column of tableDat to
       * display combo box
       */

      setMaxMinColumn(tableData.getColumnModel().getColumn(0));

      /**
           *  GuiPanel removes all components on its panel, changed components to be added
       * One property set: setLayout
       */
      guiPanel.removeAll();
      guiPanel.setLayout(new GridBagLayout());

      /**
       *  Panel newNamePanel created as JPanel
       */
      newNamePanel = new JPanel();

      /**
       * newNameField textfield created as new JTextfield, 16 spaces wide
       * 3 Properties set: Layout, minimum size, and preferredsize
       */
      newNameField = new JTextField(16);
      newNameField.setMinimumSize(new Dimension(180, 20));
      newNameField.setPreferredSize(new Dimension(180, 20));
      newNamePanel.setLayout(new GridBagLayout());

      /**
       * newNamePanel adds newNameField with constraints
       */
      Constrain.setConstraints(newNamePanel, newNameField, 0, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.CENTER, 1, 0);

      constructionLabel.setText("Formula Configuration:");
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

      /**
       *  rightPanel removes all components on its panel, changed components to be added
       * One property set: setLayout
       */

      rightPanel.removeAll();
      rightPanel.setLayout(new GridBagLayout());

      /**
           * TimeButton initialized to JButton, properties set: preferredSize, minimumSize
       */
      //timeButton = new AAButton("Time it");
      //timeButton.setEnabled(false);
      //timeButton.setPreferredSize(new Dimension(75, 30));
      //timeButton.setMinimumSize(new Dimension(75, 30));
      /**
       * deleteButton initialized to JButton, properties set: preferredSize, minimumSize
       */
      //deleteButton.setPreferredSize(new Dimension(70, 30));
      //deleteButton.setMinimumSize(new Dimension(70, 30));

      /**
       * buttonPanel initialized to JPanel and property set: setLayout
       */
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new GridBagLayout());

      /**
       * timeText initialized to JTextfield, 10 spaces wide,
       * two properties set: minimumSize and preferredSize
       */
      //JTextField timeText = new AATextField(5);
      //timeText.setEnabled(false);
      //timeText.setMinimumSize(new Dimension(50, 20));
      //timeText.setPreferredSize(new Dimension(50, 20));

      /**
       * Panel button_boxPanel and textlabel_boxPanel initialized to Box
       */

      Box button_boxPanel = new Box(BoxLayout.X_AXIS);
      Box textlabel_boxPanel = new Box(BoxLayout.X_AXIS);

      /**
       * Panel button_boxPanel adds three items: timeButton, horizontal strut
       * and delete button
       */
      //button_boxPanel.add(timeButton);
      //button_boxPanel.add(Box.createHorizontalStrut(50));
      //button_boxPanel.add(Box.createHorizontalStrut(20));
      button_boxPanel.add(deleteButton);

      /**
       * Panel textlabe_boxPanel adds three items: horizontal strut, JLabel
       * and timeText
       */
      //textlabel_boxPanel.add(Box.createHorizontalStrut(70));
      textlabel_boxPanel.add(Box.createHorizontalStrut(20));
      //textlabel_boxPanel.add(new AALabel("Total Time: "));
      //textlabel_boxPanel.add(timeText);

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
                               new JLabel("Fitness Functions:  "), 0, 0, 1,
                               1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(rightPanel, jsp, 0, 1, 1, 1,
                               GridBagConstraints.BOTH,
                               GridBagConstraints.CENTER, 1, 1);
      Constrain.setConstraints(rightPanel, buttonPanel, 0, 2, 0, 0,
                               GridBagConstraints.NONE,
                               GridBagConstraints.SOUTH, 0, 0);

      //bottomPanel.removeAll();
    }

    public void setMaxMinColumn(TableColumn column) {
      JComboBox myBox = new JComboBox();
      myBox.addItem(MIN);
      myBox.addItem(MAX);
      column.setCellEditor(new DefaultCellEditor(myBox));
    }

    // define actionlisteners
    public void actionPerformed(ActionEvent e) {

      Object src = e.getSource();
      if (src == deleteButton) {
        //Selected row index of table retrieved and stored
        int selected2 = tableData.getSelectedRow();
        //Index of item in the list calculated here
        int item = (table.getNumColumns()) - tableData.getRowCount() +
            (selected2);

        if (selected2 != -1) { //if selected exist
          String label = ( (Construction) (newColumnModel.elementAt(
              selected2))).label;

          /**
               * Below allows the label removed above to permanently be removed from
           * existence so as not to be reloaded elsewhere
           */
          if (newLabels != null) {

            String[] newNewLabels = new String[newLabels.length - 1];
            int[] newNewTypes = new int[newTypes.length - 1];

            int index = 0;
            for (index = 0; index < newLabels.length; index++) {
              if (newLabels[index].equals(label)) {
                break;
              }
              else {
                newNewLabels[index] = newLabels[index];
                newNewTypes[index] = newTypes[index];
              }
            }

            for (; index < newNewLabels.length; index++) {
              newNewLabels[index] = newLabels[index + 1];
              newNewTypes[index] = newTypes[index + 1];
            }

            newLabels = newNewLabels;
            newTypes = newNewTypes;

          }

          _newLab = newLabels;
          _newTyp = newTypes;
          _lastCons = newColumnModel.toArray();

          /**
           * Remove label from columnBox, label from the columnModel,
           * remove row from modelData
           * table columnLabel null at item's location
           * Remove item at selected2 index from newColumnModel
           */
          columnBox.removeItem(label);
          //columnModel.removeElement(label);
          newColumnModel.removeElementAt(selected2);
          modelData.removeRow(selected2);
        }
      }
      else if (src == this.doneButton) {

        constructions = (Object[]) getLastCons();
        Construction[] tmp = new Construction[constructions.length];
        for (int i = 0; i < constructions.length; i++) {
          tmp[i] = (Construction) constructions[i];

          float[] tmpfloat = new float[0];
          // add an empty column of floats to the table
          table.addColumn(tmpfloat);
          // set the label of the new column added to the table
          table.setColumnLabel(tmp[i].label, (table.getNumColumns() - 1));
        }

        /*if (data.fitnessFunctionConstructions == null) {
          data.fitnessFunctionConstructions = tmp;
        }
        // append the new constructions onto the older constructions
        else {
          Construction[] tmp2 = new Construction[tmp.length +
              data.fitnessFunctionConstructions.length];
          int i = 0;
          for (; i < data.fitnessFunctionConstructions.length; i++)
            tmp2[i] = data.fitnessFunctionConstructions[i];
          int j = 0;
          for (; i < tmp2.length; i++) {
            tmp2[i] = tmp[j];
            j++;
          }
          data.fitnessFunctionConstructions = tmp2;
        }*/
        FitnessFunctions ff = data.fitnessFunctions;
        for(int i = 0; i < tmp.length; i++) {
          ff.addFitnessFunction((FitnessFunctionConstruction)tmp[i]);
        }

        pushOutput(data, 0);
        data = null;
        viewDone("Done");
      }
      else {
        super.actionPerformed(e);
      }
    }

    /**
     *  this is the action listener attached with the addButton
     *  it adds the column to the table and take all the necessary actions
     */
    public void expressionChanged(Object evaluation) {
      // is no new name field given display error dialog
      if (newNameField.getText().length() == 0) {
        JOptionPane.showMessageDialog(this,
                                      "You must specify a new column name.",
                                      "Define Fitness Functions",
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

        // determine new expression, label, and type
        ColumnExpression newExp = new ColumnExpression(table);
        try {
          newExp.setLazyExpression(gui.getTextArea().getText(), newLabels,
                                   newTypes);
        }
        catch (ExpressionException e) {
          JOptionPane.showMessageDialog(this,
              "Error in ColumnConstruction. Check your expression.",
              "Define Fitness Functions", JOptionPane.ERROR_MESSAGE);
          return;
        }

        String newLab = newLabelBuffer.toString();
        String newStr = gui.getTextArea().getText();
        int newTyp = newExp.evaluateType();

        // add to arrays of new expressions, labels, types

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

        if (newTypes == null) {
          newTypes = new int[1];
          newTypes[0] = newTyp;
        }
        else {
          int[] newNewTyp = new int[newTypes.length + 1];
          for (int i = 0; i < newTypes.length; i++) {
            newNewTyp[i] = newTypes[i];
          }
          newNewTyp[newTypes.length] = newTyp;
          newTypes = newNewTyp;
        }
        //check if the expression is valid
        newExp = new ColumnExpression(table);
        try {
          newExp.setLazyExpression(gui.getTextArea().getText(), newLabels,
                                   newTypes);
        }
        catch (ExpressionException e) {
          JOptionPane.showMessageDialog(this,
              "Error in ColumnConstruction. Check your expression.",
              "Define Fitness Functions", JOptionPane.ERROR_MESSAGE);
          return;
        }

        /**
         *  if the expression is valid set the expression of gui/
         * add the new expression to the right side of the
         * gui and also add a drop down list
         * for selecting max or min for
         * the newly added fitness function
         */
        gui.setExpression(newExp);

        /**
         * Add to table object array with: empty string, label of function,  function itself, and time stirng
         */
        modelData.addRow(new Object[] {MIN, newNameField.getText()+" = "+
                         gui.getTextArea().getText()/*, new String()*/});

        FitnessFunctionConstruction added = new FitnessFunctionConstruction(
            newNameField.getText(), gui.getTextArea().getText());
        added.setIsMinimizing(true);
        newColumnModel.addElement(added);
        newColumnList.setMinimumSize(new Dimension(200, 200));

        _newLab = newLabels;
        _newTyp = newTypes;
        _lastCons = newColumnModel.toArray();

        //newColumnModel.addElement(added);

        // if the expression is valid set the expression of gui

        //float[] tmpFloat = new float[0];
        // add an empty column of floats to the table
        //table.addColumn(tmpFloat);
        // set the label of the new column added to the table
        //table.setColumnLabel(newLab, (table.getNumColumns() - 1));
        // the columnBox needs to be revalidated so that the new
        // defined variable can also be used to create more
        //new variables

        //newColumnModel.toArray();

        redoBox();
        //reset the new name field
        newNameField.setText("");
        //reset the gui text area
        gui.getTextArea().setText("");

      }
    }
  }
}