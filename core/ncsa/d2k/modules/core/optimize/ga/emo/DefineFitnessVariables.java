package ncsa.d2k.modules.core.optimize.ga.emo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.transform.attribute.*;

import ncsa.d2k.modules.core.datatype.table.transformations.Construction;

public class DefineFitnessVariables
    extends AttributeConstruction {

  public String getModuleName() {
    return "Define Fitness Variables";
  }

  protected UserView createUserView() {
    return new DefineVarView();
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

  public String getInputName(int i) {
    return "EMOPopulationInfo";
  }

  public String getOutputName(int i) {
    return "EMOPopulationInfo";
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  /** Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[0];
    return pds;
  }

  //inner class, extends attributeConstruction's inner gui class
  public class DefineVarView
      extends ColumnConstructionGUI {

    // columnModel keeps track of all the already present columns in the table
    // as well as the new columns defined recently using the widget
    // It is used in the redoBox() function
    private DefaultComboBoxModel columnModel;

    private EMOPopulationInfo data;

    public void setInput(Object o, int i) {
      data = (EMOPopulationInfo) o;
      table = (MutableTable) data.varNames;
      this.initialize();
    }

    public Dimension getPreferredSize() {
      return new Dimension(750, 350);
    }

    // function to repaint the columnBox
    // since new column are added by the user
    // the columnBox needs to be continuously updated so
    // that recently defined defined variables can be used to define
    // other variables
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
          columnModel.addElement( ((Construction)constructions[j]).label);
        }
      }

      // set the columnBox model to the recently
      // updated columnModel
      columnBox.setModel(columnModel);

      //bottomPanel.removeAll();
    }

    public void actionPerformed(ActionEvent e) {

      Object src = e.getSource();
      // delete the selected expression
      if (src == deleteButton) {
        //Selected row index of table retrieved and stored
        int selected = newColumnList.getSelectedIndex();
        //Index of item in the list calculated here
        int item = (table.getNumColumns()) - newColumnModel.getSize() +
            (selected);

        if (selected != -1) {
          String label = ( (Construction) (newColumnModel.elementAt(selected))).label;

          /**
           * Remove label from columnBox,
           * remove row from modelData
           * table columnLabel null at item's location
           * Remove item at selected index from newColumnModel
           */
          columnBox.removeItem(label);
          //table.setColumnLabel(null, item);
          //table.removeColumn(item);
          newColumnModel.removeElementAt(selected);

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
          if (_newLab != null) {
            _newLab = newLabels;
          }
          _newTyp = newTypes;
          _lastCons = newColumnModel.toArray();
        }
      }
      else if (src == this.doneButton) {
        //help.setVisible(false);
        //pushOutput(new AttributeTransform(newColumnModel.toArray()), 0);
        //myConstructions = new ArrEMOConstruction(ccwVar.getnewConstructions(),
        //     ccwFit.getnewConstructions());

        constructions = (Object[]) getLastCons();
        if(constructions != null) {
          Construction[] tmp = new Construction[constructions.length];
          for (int i = 0; i < constructions.length; i++) {
            tmp[i] = (Construction) constructions[i];

            float[] tmpfloat = new float[0];
            // add an empty column of floats to the table
            table.addColumn(tmpfloat);
            // set the label of the new column added to the table
            table.setColumnLabel(tmp[i].label, (table.getNumColumns() - 1));
          }

          if (data.fitnessVariableConstructions == null) {
            data.fitnessVariableConstructions = tmp;
          }
          // append the new constructions onto the older constructions
          else {
            Construction[] tmp2 = new Construction[tmp.length +
                data.fitnessVariableConstructions.length];
            int i = 0;
            for (; i < data.fitnessVariableConstructions.length; i++) {
              tmp2[i] = data.fitnessVariableConstructions[i];
            }
            int j = 0;
            for (; i < tmp2.length; i++) {
              tmp2[i] = tmp[j];
              j++;
            }
            data.fitnessVariableConstructions = tmp2;
          }
        }

        pushOutput(data, 0);
        viewDone("Done");
      }
      else {
        super.actionPerformed(e);
      }
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
                                      "Define New Variables",
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
              "Error in Define New Variables. Check your expression.",
              "Define New Variables", JOptionPane.ERROR_MESSAGE);
          return;
        }

        String newLab = newLabelBuffer.toString();
        String newStr = gui.getTextArea().getText();
        int newTyp = newExp.evaluateType();

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
              "Error in Define New Variables. Check your expression.",
              "Define New Variables", JOptionPane.ERROR_MESSAGE);
          return;
        }

        // if the expression is valid set the expression of gui
        gui.setExpression(newExp);

        Construction added = new Construction(
            newNameField.getText(), gui.getTextArea().getText());
        newColumnModel.addElement(added);
        newColumnList.setMinimumSize(new Dimension(200, 200));

        _newLab = newLabels;
        _newTyp = newTypes;
        _lastCons = newColumnModel.toArray();

        newNameField.setText("");
        gui.getTextArea().setText("");

//        float[] tmpfloat = new float[0];

        // add an empty column of floats to the table
//        table.addColumn(tmpfloat);

        // set the label of the new column added to the table
//        table.setColumnLabel(newLab, (table.getNumColumns() - 1));

        // the columnBox needs to be revalidated so that the new
        // defined variable can also be used to create more
        //new variables
        redoBox();

        //reset the new name field
        newNameField.setText("");
        //reset the gui text area
        gui.getTextArea().setText("");
      }
    }
  }
}