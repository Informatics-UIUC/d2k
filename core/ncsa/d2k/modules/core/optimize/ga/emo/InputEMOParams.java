package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;

public class InputEMOParams extends UIModule {

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
  }

  public String[] getOutputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  protected UserView createUserView() {
    return new ParamView();
  }

  public String[] getFieldNameMapping() {
    return null;
  }

  private class ParamView extends JUserPane {

    JTable paramsTable;
    DefaultTableModel paramsModel;

    public void initView(ViewModule vm) {
      paramsModel = new MultiObjectiveParamsTableModel();
      paramsTable = new JTable(paramsModel);
      JScrollPane jsp = new JScrollPane(paramsTable);

      setLayout(new BorderLayout());
      JLabel label = new JLabel("Set GA Parameters");
      label.setBorder(new EmptyBorder(20, 10, 20, 5));
      add(label, BorderLayout.NORTH);
      add(jsp, BorderLayout.CENTER);
    }

    boolean multiObjective;

    public void setInput(Object o, int i) {
      EMOPopulationInfo popInfo = (EMOPopulationInfo)o;
      if(popInfo.fitnessFunctionConstructions.length == 1)
        multiObjective = false;
      else
        multiObjective = true;

      if(!multiObjective) {

      }
      else {
        paramsModel.setValueAt(Integer.toString(5), 0, 1);
        paramsModel.setValueAt(Integer.toString(10), 2, 1);
        paramsModel.setValueAt(Integer.toString(1), 5, 1);
      }
    }

    private class MultiObjectiveParamsTableModel extends DefaultTableModel {

      String[] col0 = {"Number of Nondominated Solutions",
          "Starting Population Size",
          "Tournament Size",
          "Probability of Crossover",
          "Probability of Mutation",
          "Generation Gap"};

      String[] col1 = {"", "", "", "", "", ""};
      String[] col2 = {"", "", "", "", "", ""};

      String[] names = {"", "Recommended", "Override"};

      MultiObjectiveParamsTableModel() {
        setValueAt(Integer.toString(20), 0, 1);
        setValueAt(Integer.toString(41), 1, 1);
        setValueAt(Integer.toString(5), 2, 1);
        setValueAt(Double.toString(.8), 3, 1);
        setValueAt(Double.toString(.2), 4, 1);
        setValueAt(Integer.toString(1), 5, 1);
      }

      public int getColumnCount() {
        return 3;
      }

      public int getRowCount() {
        return 6;
      }

      public String getColumnName(int i) {
        return names[i];
      }

      public Object getValueAt(int r, int c) {
        if(c == 0)
          return col0[r];
        else if(c == 1)
          return col1[r];
        else
          return col2[r];
      }

      public void setValueAt(Object value, int r, int c) {
        if(c == 0)
          col0[r] = (String)value;
        else if(c == 1)
          col1[r] = (String)value;
        else if(c == 2)
          col2[r] = (String)value;
      }

      public boolean isCellEditable(int r, int c) {
        if(c == 2)
          return true;
        return false;
      }
    }
  }
}