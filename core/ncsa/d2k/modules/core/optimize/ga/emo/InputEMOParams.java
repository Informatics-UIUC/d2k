package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class InputEMOParams extends UIModule {

  public String[] getInputTypes() {
    //return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
    return null;
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

    private JTable paramsTable;
    private DefaultTableModel paramsModel;
    private boolean multiObjective;

    public void initView(ViewModule vm) {
      paramsModel = new MultiObjectiveParamsTableModel();
      paramsTable = new JTable(paramsModel);
      paramsTable.setPreferredScrollableViewportSize(new Dimension(410, 80));
      JScrollPane jsp = new JScrollPane(paramsTable);

      setLayout(new BorderLayout());
      JLabel label = new JLabel("Set GA Parameters");
      label.setBorder(new EmptyBorder(20, 10, 20, 5));

      JPanel leftPanel = new JPanel(new BorderLayout());
      leftPanel.add(label, BorderLayout.NORTH);

      JPanel l1 = new JPanel(new BorderLayout());
      l1.add(jsp, BorderLayout.NORTH);
      l1.add(new TimePanel(), BorderLayout.CENTER);

      leftPanel.add(l1, BorderLayout.CENTER);

      add(leftPanel, BorderLayout.WEST);

      JPanel rightPanel = new JPanel(new BorderLayout());
      JLabel l2 = new JLabel("Estimated Time Factor");
      l2.setBorder(new EmptyBorder(20, 10, 20, 5));
      rightPanel.add(l2, BorderLayout.NORTH);
      JPanel pp = new JPanel(new BorderLayout());
      pp.add(new RunTimeChart(), BorderLayout.NORTH);
      pp.add(new RunTimePanel(), BorderLayout.CENTER);
      rightPanel.add(pp, BorderLayout.CENTER);

      rightPanel.setBorder(new EmptyBorder(10, 50, 10, 50));
      add(rightPanel, BorderLayout.EAST);

      advanced.setEnabled(false);
      tips.setEnabled(false);

      numSolutions.addKeyListener(new KeyAdapter() {
        public void keyTyped(KeyEvent e) {
          String txt = numSolutions.getText();
          char c = e.getKeyChar();
          StringBuffer sb = new StringBuffer(txt);
          sb.append(c);

          txt = sb.toString();

          // now parse this
          try {
            double d = Double.parseDouble(txt);
            int recPop = (int)(2*d+1);
            paramsModel.setValueAt(Double.toString(recPop), 0, 1);
          }
          catch(Exception ex) {
            return;
          }
        }
      });
        paramsModel.setValueAt("30", 0, 1);
        paramsModel.setValueAt("4", 2, 1);
        paramsModel.setValueAt("1", 5, 1);
    }

    private JLabel timeRequired = new JLabel();
    private JTextField maxTime = new JTextField(4);
    private JTextField numSolutions = new JTextField(4);
    private JButton advanced = new JButton("Advanced Settings");
    private RunTimeChart runTimeChart;

    private JLabel timeRequired2 = new JLabel("   ");
    private JLabel maxTime2 = new JLabel("    ");

    class RunTimePanel extends JPanel {
      RunTimePanel() {
        setLayout(new GridBagLayout());
        Constrain.setConstraints(this, new JLabel("Estimated Time Required", JLabel.RIGHT),
                                                  0, 0, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST,
                                                  1, 1);
        Constrain.setConstraints(this, timeRequired2, 1, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
        Constrain.setConstraints(this, new JLabel("Specified Maximum Run Time", JLabel.RIGHT),
                                 0, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, maxTime2, 1,1,1,1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("Difference of ", JLabel.RIGHT),
                                 0,2,1,1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("   "), 0,2,1,1,
                                 GridBagConstraints.BOTH,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, tips, 0, 3, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.EAST,
                                 1, 1);

      }
    }
    JButton tips = new JButton("Optimization Tips");

    class RunTimeChart extends JPanel {
      RunTimeChart() {}

      public Dimension getPreferredSize() {
        return new Dimension(300, 100);
      }

      protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        // paint divider
        g2.setColor(Color.black);
        g2.drawLine(0, (int)height/2, (int)width, (int)height/2);

        String req = timeRequired.getText();
        String max = maxTime.getText();
        double timeReq = 0;
        double maxTime = 0;

        try {
          timeReq = Double.parseDouble(req);
          maxTime = Double.parseDouble(max);
        }
        catch(Exception e) {
          return;
        }

        if(timeReq > maxTime)
          ;
        else
          ;
      }

      double height;
      double width;

      public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        height = h;
        width = w;
      }
    }

    class TimePanel extends JPanel {
      TimePanel() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Time Required", JLabel.RIGHT);
        Constrain.setConstraints(this, lbl,
                                 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, timeRequired, 1, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("minutes"),
                                 2, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("    "),
                                 0, 1, 1, 1,
                                 GridBagConstraints.BOTH, GridBagConstraints.WEST,
                                 1, 1);
        JLabel lbl2 = new JLabel("Maximum Run Time", JLabel.RIGHT);
        Constrain.setConstraints(this, lbl2,
                                 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, maxTime,
                                 1, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("minutes"),
                                 2, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);
        /*JLabel lbl3 = new JLabel("Number of Solutions Desired:", JLabel.RIGHT);
        Constrain.setConstraints(this, lbl3,
                                 0, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, numSolutions,
                                 1, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("  "),
                                 0, 4, 1, 1,
                                 GridBagConstraints.BOTH, GridBagConstraints.WEST,
                                 1, 1);*/
        Constrain.setConstraints(this, advanced,
                                 0, 4, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);
      }
    }

    public void setInput(Object o, int i) {
      EMOPopulationInfo popInfo = (EMOPopulationInfo)o;
      if(popInfo.fitnessFunctionConstructions.length == 1)
        multiObjective = false;
      else
        multiObjective = true;

multiObjective = true;
      if(!multiObjective) {
      }
      else {
        paramsModel.setValueAt("30", 1, 1);
        paramsModel.setValueAt("4", 2, 1);
        paramsModel.setValueAt("1", 5, 1);
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
//        setValueAt(Integer.toString(20), 0, 1);
//        setValueAt(Integer.toString(41), 1, 1);
/*        setValueAt(Integer.toString(5), 0, 1);
        setValueAt(Double.toString(.8), 1, 1);
        setValueAt(Double.toString(.2), 2, 1);
        setValueAt(Integer.toString(1), 3, 1);*/
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
        else if(c == 1) {
          col1[r] = (String) value;
          if(r == 0) {
            try {
              String str = (String)value;
              double vl = Double.parseDouble(str);
              double popSize = 2*vl+1;
              setValueAt(Double.toString(popSize), 1, 1);
              double mut = 1/vl;
              mut = mut*100;
              setValueAt(Double.toString(mut), 4, 1);
            }
            catch(Exception ex) {
              return;
            }
          }
          if(r == 2) {
            String val = (String)value;
            try {
              double d = Double.parseDouble(val);
              double crossover = (d-1)/d;
              crossover = crossover * 100;
              setValueAt(Double.toString(crossover), 3, 1);
            }
            catch(Exception e) {
              return;
            }
          }

        }
        else if(c == 2) {
          col2[r] = (String) value;
          // the user set the number of solutions
          if(r == 0) {
            try {
              String str = (String)value;
              double vl = Double.parseDouble(str);
              double popSize = 2*vl+1;
              setValueAt(Double.toString(popSize), 1, 1);
              double mut = 1/vl;
              mut = mut*100;
              setValueAt(Double.toString(mut), 4, 1);
            }
            catch(Exception ex) {
              return;
            }
          }

          // user set the tournament size
          if(r == 2) {
            String val = (String)value;
            try {
              double d = Double.parseDouble(val);
              double crossover = (d-1)/d;
              crossover = crossover * 100;
              setValueAt(Double.toString(crossover), 3, 1);
            }
            catch(Exception e) {
              return;
            }
          }
        }
      }

      public boolean isCellEditable(int r, int c) {
        if(c == 2)
          return true;
        return false;
      }
    }
  }
}