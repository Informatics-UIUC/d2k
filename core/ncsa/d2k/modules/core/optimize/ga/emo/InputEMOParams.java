package ncsa.d2k.modules.core.optimize.ga.emo;

import java.text.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

public class InputEMOParams
    extends UIModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo",
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputName(int i) {
    return "Population Info";
  }

  public String getInputName(int i) {
    return "Population Info";
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

  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }

  private CachedParams cachedParams;
  public void setCachedParams(CachedParams cp) {
    cachedParams = cp;
  }
  public CachedParams getCachedParams() {
    return cachedParams;
  }

  private class ParamView
      extends JUserPane {

    private JTable paramsTable;
    private MultiObjectiveParamsTableModel paramsModel;
    private boolean multiObjective;
    private EMOPopulationInfo popInfo;
    private JLabel timeRequired = new JLabel();
    private JTextField maxTime = new JTextField(4);
    private JTextField numSolutions = new JTextField(4);
    private JButton advanced = new JButton("Advanced Settings");
    private RunTimeChart runTimeChart;

    private JLabel timeRequired2 = new JLabel("          ");
    private JLabel maxTime2 = new JLabel("          ");
    private JButton tips = new JButton("Optimization Tips");
    private boolean doingTiming = false;
    private NumberFormat nf;

    public void initView(ViewModule vm) {
      nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(4);
      paramsModel = new MultiObjectiveParamsTableModel();
      paramsTable = new JTable(paramsModel);
      paramsTable.setPreferredScrollableViewportSize(new Dimension(410, 80));
      JScrollPane jsp = new JScrollPane(paramsTable);

      setLayout(new BorderLayout());
      JLabel label = new JLabel("Set GA Parameters");
      label.setBorder(new EmptyBorder(20, 0, 20, 0));

      JPanel leftPanel = new JPanel(new BorderLayout());
      leftPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
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
      runTimeChart = new RunTimeChart();
      pp.add(runTimeChart, BorderLayout.NORTH);
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
            int recPop = (int) (2 * d);
            paramsModel.setValueAt(Double.toString(recPop), 0, 1);
          }
          catch (Exception ex) {
            return;
          }
        }
      });

      JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton abort = new JButton("Abort");
      abort.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          viewCancel();
        }
      });
      JButton done = new JButton("Done");
      done.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          // set all the proper fields on the pop info

          // get the pop size
          String override = (String) paramsModel.getValueAt(1, 2);
          String rec = (String) paramsModel.getValueAt(1, 1);

          double popSize;
          try {
            popSize = Double.parseDouble(override);
          }
          catch (Exception ex) {
            popSize = Double.parseDouble(rec);
          }
          popInfo.populationSize = (int) popSize;

          override = (String) paramsModel.getValueAt(2, 2);
          rec = (String) paramsModel.getValueAt(2, 1);
          try {
            popSize = Double.parseDouble(override);
          }
          catch (Exception ex) {
            popSize = Double.parseDouble(rec);
          }
          popInfo.maxGenerations = (int) popSize;

          override = (String) paramsModel.getValueAt(3, 2);
          rec = (String) paramsModel.getValueAt(3, 1);
          try {
            popSize = Double.parseDouble(override);
          }
          catch (Exception ex) {
            popSize = Double.parseDouble(rec);
          }
          popInfo.tournamentSize = (int) popSize;

          override = (String) paramsModel.getValueAt(4, 2);
          rec = (String) paramsModel.getValueAt(4, 1);
          try {
            popSize = Double.parseDouble(override);
          }
          catch (Exception ex) {
            popSize = Double.parseDouble(rec);
          }
          popInfo.crossoverRate = popSize / 100;

          override = (String) paramsModel.getValueAt(5, 2);
          rec = (String) paramsModel.getValueAt(5, 1);
          try {
            popSize = Double.parseDouble(override);
          }
          catch (Exception ex) {
            popSize = Double.parseDouble(rec);
          }
          popInfo.mutationRate = popSize / 100;

          override = (String) paramsModel.getValueAt(6, 2);
          rec = (String) paramsModel.getValueAt(6, 1);
          try {
            popSize = Double.parseDouble(override);
          }
          catch (Exception ex) {
            popSize = Double.parseDouble(rec);
          }
          popInfo.generationGap = popSize;

          // save the values that were input
          CachedParams cp = new CachedParams();
          String[] recVals = paramsModel.col1;
          String[] recCpy = new String[recVals.length];
          System.arraycopy(recVals, 0, recCpy, 0, recVals.length);

          cp.recommended = recCpy;

          String[] overVals = paramsModel.col2;
          String[] overCpy = new String[recVals.length];
          System.arraycopy(overVals, 0, overCpy, 0, overVals.length);
          cp.override = overCpy;

          cp.maxRunTime = maxTime.getText();
          cp.estimatedTimeReq = timeRequired.getText();
          cp.diff = differenceLabel.getText();
          cp.numVars = popInfo.boundsAndPrecision.getNumRows();
          int stringLength = 0;
          for (int i = 0; i < popInfo.boundsAndPrecision.getNumRows(); i++) {
            stringLength += popInfo.boundsAndPrecision.getInt(i,
                popInfo.boundsAndPrecision.getNumColumns() - 1);
          }
          cp.stringLength = stringLength;
          setCachedParams(cp);

          // push out
          pushOutput(popInfo, 0);
          viewDone("Done");
        }
      });
      buttonsPanel.add(abort);
      buttonsPanel.add(done);
      add(buttonsPanel, BorderLayout.SOUTH);

      maxTime.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          String txt = maxTime.getText();
          maxTime2.setText(txt);
          runTimeChart.repaint();
        }
      });
      maxTime2.setHorizontalAlignment(JLabel.CENTER);
      timeRequired2.setHorizontalAlignment(JLabel.CENTER);

      CachedParams cp = getCachedParams();
      if(cp != null) {
        paramsModel.col1 = cp.recommended;
        paramsModel.col2 = cp.override;

        maxTime.setText(cp.maxRunTime);
        maxTime2.setText(cp.maxRunTime);

        timeRequired.setText(cp.estimatedTimeReq);
        timeRequired2.setText(cp.estimatedTimeReq);

        differenceLabel.setText(cp.diff);
      }
      else {
        maxTime.setText("10");
        maxTime2.setText("10");
      }
    }

    class RunTimePanel
        extends JPanel {
      RunTimePanel() {
        setLayout(new GridBagLayout());
        Constrain.setConstraints(this, new JLabel("         "),
                                 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this,
                                 new JLabel("Estimated Time Required: ",
                                            JLabel.RIGHT),
                                 0, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, timeRequired2, 1, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("minutes"), 2, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this,
                                 new JLabel("Specified Maximum Run Time: ",
                                            JLabel.RIGHT),
                                 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, maxTime2, 1, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("minutes"), 2, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this,
                                 new JLabel("Difference of: ", JLabel.RIGHT),
                                 0, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, differenceLabel, 1, 3, 1, 1,
                                 GridBagConstraints.BOTH,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("minutes"), 2, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, tips, 0, 4, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.EAST,
                                 1, 1);
      }
    }

    JLabel differenceLabel = new JLabel("      ", JLabel.CENTER);

    class RunTimeChart
        extends JPanel {
      RunTimeChart() {}

      public Dimension getPreferredSize() {
        return new Dimension(200, 100);
      }

      protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.PLAIN, 10));

        // paint divider
        g2.setColor(Color.black);
        g2.drawLine(0, (int) height / 2, (int) width, (int) height / 2);

        String req = timeRequired.getText();
        String max = maxTime.getText();
        double timeReq = 0;
        double maxTime = 0;

        try {
          timeReq = Double.parseDouble(req);
          maxTime = Double.parseDouble(max);
        }
        catch (Exception e) {
          return;
        }

        double startx = width * .1;
        double barLength = width * .8;

        double barHeight = height * .1;
        double starty = height * .2;

        double nexty = height * .7;

        int strheight = g2.getFontMetrics().getHeight();

        if (timeReq > maxTime) {
          g2.setPaint(Color.darkGray);
          g2.fill(new Rectangle2D.Double(startx, starty, barLength, barHeight));

          double len = (maxTime * barLength) / timeReq;
          g2.fill(new Rectangle2D.Double(startx, nexty, len, barHeight));
          g2.setColor(Color.black);
          g2.drawString("Estimated Required Time", (int) startx,
                        (int) (.35 * height + (strheight / 2)));
          g2.drawString("Specified Max Runtime", (int) startx,
                        (int) (.9 * height + (strheight / 2)));
        }
        else {
          g2.setPaint(Color.darkGray);
          double len = (timeReq * barLength) / maxTime;
          g2.fill(new Rectangle2D.Double(startx, starty, len, barHeight));

          g2.fill(new Rectangle2D.Double(startx, nexty, barLength, barHeight));
          g2.setColor(Color.black);
          g2.drawString("Estimated Required Time", (int) startx,
                        (int) (.35 * height + (strheight / 2)));
          //g2.drawString("Estimated Required Time", (int)startx, (int)(.5*height-2));
          //g2.drawString("Specified Max Runtime", (int)startx, (int)height-2);
          g2.drawString("Specified Max Runtime", (int) startx,
                        (int) (.9 * height + (strheight / 2)));
        }
      }

      double height;
      double width;

      public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        height = h;
        width = w;
      }
    }

    class TimePanel
        extends JPanel {
      TimePanel() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Time Required: ", JLabel.RIGHT);
        Constrain.setConstraints(this, lbl,
                                 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, timeRequired, 1, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("minutes"),
                                 2, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        estimate = new JButton("Estimate");
        estimate.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            EMOPopulationInfo testPopInfo = new EMOPopulationInfo();
            testPopInfo.boundsAndPrecision = popInfo.boundsAndPrecision;
            testPopInfo.constraintFunctionConstructions = popInfo.
                constraintFunctionConstructions;
            testPopInfo.constraintVariableConstructions = popInfo.
                constraintVariableConstructions;

            testPopInfo.externalConstraintInfo = popInfo.externalConstraintInfo;
            testPopInfo.externalFitnessInfo = popInfo.externalFitnessInfo;
            testPopInfo.fitnessFunctionConstructions = popInfo.
                fitnessFunctionConstructions;
            testPopInfo.fitnessVariableConstructions = popInfo.
                fitnessVariableConstructions;

            testPopInfo.useExternalConstraintEvaluation = popInfo.
                useExternalConstraintEvaluation;
            testPopInfo.useExternalFitnessEvaluation = popInfo.
                useExternalFitnessEvaluation;
            testPopInfo.varNames = popInfo.varNames;

            // get the pop size
            String override = (String) paramsModel.getValueAt(1, 2);
            String rec = (String) paramsModel.getValueAt(1, 1);

            double popSize;
            /*            try {
                          popSize = Double.parseDouble(override);
                        }
                        catch(Exception ex) {
                          popSize = Double.parseDouble(rec);
                        }*/
//            testPopInfo.populationSize = (int)popSize;
            testPopInfo.populationSize = 2;

            override = (String) paramsModel.getValueAt(2, 2);
            rec = (String) paramsModel.getValueAt(2, 1);
            /*try {
              popSize = Double.parseDouble(override);
                         }
                         catch(Exception ex) {
              popSize = Double.parseDouble(rec);
                         }*/
//            testPopInfo.maxGenerations = (int)popSize;
            testPopInfo.maxGenerations = 1;

            override = (String) paramsModel.getValueAt(3, 2);
            rec = (String) paramsModel.getValueAt(3, 1);
            try {
              popSize = Double.parseDouble(override);
            }
            catch (Exception ex) {
              popSize = Double.parseDouble(rec);
            }
            testPopInfo.tournamentSize = (int) popSize;

            override = (String) paramsModel.getValueAt(4, 2);
            rec = (String) paramsModel.getValueAt(4, 1);
            try {
              popSize = Double.parseDouble(override);
            }
            catch (Exception ex) {
              popSize = Double.parseDouble(rec);
            }
            testPopInfo.crossoverRate = popSize / 100;

            override = (String) paramsModel.getValueAt(5, 2);
            rec = (String) paramsModel.getValueAt(5, 1);
            try {
              popSize = Double.parseDouble(override);
            }
            catch (Exception ex) {
              popSize = Double.parseDouble(rec);
            }
            testPopInfo.mutationRate = popSize / 100;

            override = (String) paramsModel.getValueAt(6, 2);
            rec = (String) paramsModel.getValueAt(6, 1);
            try {
              popSize = Double.parseDouble(override);
            }
            catch (Exception ex) {
              popSize = Double.parseDouble(rec);
            }
            testPopInfo.generationGap = popSize;
            // push out

            doingTiming = true;
            startTime = System.currentTimeMillis();
            estimate.setEnabled(false);
            pushOutput(testPopInfo, 1);
          }
        });
        Constrain.setConstraints(this, estimate,
                                 3, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("    "),
                                 0, 1, 1, 1,
                                 GridBagConstraints.BOTH,
                                 GridBagConstraints.WEST,
                                 1, 1);
        JLabel lbl2 = new JLabel("Maximum Run Time: ", JLabel.RIGHT);
        Constrain.setConstraints(this, lbl2,
                                 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, maxTime,
                                 1, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, new JLabel("minutes"),
                                 2, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
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
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.EAST,
                                 1, 1);
      }
    }

    public void setInput(Object o, int z) {
      if (!doingTiming) {
        popInfo = (EMOPopulationInfo) o;
        if (popInfo.fitnessFunctionConstructions.length == 1) {
          multiObjective = false;
        }
        else {
          multiObjective = true;

        }
        multiObjective = true;
        if (!multiObjective) {
          ;
        }
        else {
          CachedParams cp = getCachedParams();
          // only use the cached values if the number of variables and string length are equal
          boolean useCached = false;
          if(cp != null) {
            int numVars = cp.numVars;
            int stringLen = cp.stringLength;

            int stringLength = 0;
            for (int i = 0; i < popInfo.boundsAndPrecision.getNumRows(); i++) {
              stringLength += popInfo.boundsAndPrecision.getInt(i,
                  popInfo.boundsAndPrecision.getNumColumns() - 1);
            }
            if(stringLength == stringLen && numVars == popInfo.boundsAndPrecision.getNumRows())
              useCached = true;
          }
          if(!useCached) {
            paramsModel.setValueAt("30", 0, 1);
            // set the rec. max gens to be 2*string length
            int stringLength = 0;
            for (int i = 0; i < popInfo.boundsAndPrecision.getNumRows(); i++) {
              stringLength += popInfo.boundsAndPrecision.getInt(i,
                  popInfo.boundsAndPrecision.getNumColumns() - 1);
            }
            int maxGenerations = 2 * stringLength;
            paramsModel.setValueAt(Integer.toString(maxGenerations), 2, 1);

            paramsModel.setValueAt("4", 3, 1);
            paramsModel.setValueAt("1", 6, 1);
          }
        }
      }
      else {
        doingTiming = false;
        stopTime = System.currentTimeMillis();
        long timeNeeded = stopTime - startTime;
        int stringLength = 0;
        for (int i = 0; i < popInfo.boundsAndPrecision.getNumRows(); i++) {
          stringLength += popInfo.boundsAndPrecision.getInt(i,
              popInfo.boundsAndPrecision.getNumColumns() - 1);
        }

        // this is the time in MS needed to evaluate one individual
        double msNeeded = timeNeeded / 2;

        // now multiply by the pop size
        // get the pop size
        String override = (String) paramsModel.getValueAt(1, 2);
        String rec = (String) paramsModel.getValueAt(1, 1);

        double popSize;
        try {
          popSize = Double.parseDouble(override);
        }
        catch (Exception ex) {
          popSize = Double.parseDouble(rec);
        }
        msNeeded *= popSize;
//            testPopInfo.populationSize = (int)popSize;

        // now multiply by the number of gens
        // now multiply by the pop size
        // get the pop size
        override = (String) paramsModel.getValueAt(2, 2);
        rec = (String) paramsModel.getValueAt(2, 1);

        try {
          popSize = Double.parseDouble(override);
        }
        catch (Exception ex) {
          popSize = Double.parseDouble(rec);
        }
        msNeeded *= popSize;

        //double minsNeeded = 3 * Math.pow(stringLength, 2) * timeNeeded;
        msNeeded = msNeeded / (1000 * 60);
        //minsNeeded = minsNeeded / 2;
        timeRequired.setText(nf.format(msNeeded));
        timeRequired2.setText(nf.format(msNeeded));
        runTimeChart.repaint();
        estimate.setEnabled(true);

        String mx = maxTime.getText();
        double maxtime = 0;
        try {
          maxtime = Double.parseDouble(mx);
        }
        catch (Exception e) {
        }
        double diff = msNeeded - maxtime;
        this.differenceLabel.setText(nf.format(diff));
      }
    }

    private JButton estimate;

    private long startTime;
    private long stopTime;

    private class MultiObjectiveParamsTableModel
        extends DefaultTableModel {

      String[] col0 = {
          "Number of Nondominated Solutions",
          "Starting Population Size",
          "Maximum Number of Generations",
          "Tournament Size",
          "Probability of Crossover",
          "Probability of Mutation",
          "Generation Gap"};

      String[] col1 = {
          "", "", "", "", "", "", ""};
      String[] col2 = {
          "", "", "", "", "", "", ""};

      String[] names = {
          "", "Recommended", "Override"};

      public int getColumnCount() {
        return 3;
      }

      public int getRowCount() {
        return 7;
      }

      public String getColumnName(int i) {
        return names[i];
      }

      public Object getValueAt(int r, int c) {
        if (c == 0) {
          return col0[r];
        }
        else if (c == 1) {
          return col1[r];
        }
        else {
          return col2[r];
        }
      }

      public void setValueAt(Object value, int r, int c) {
        if (c == 0) {
          col0[r] = (String) value;
        }
        else if (c == 1) {
          col1[r] = (String) value;
          if (r == 0) {
            try {
              String str = (String) value;
              double vl = Double.parseDouble(str);
              double popSize = 2 * vl;
              setValueAt(Double.toString(popSize), 1, 1);
              double mut = 1 / vl;
              mut = mut * 100;
              setValueAt(Double.toString(mut), 5, 1);
              fireTableDataChanged();
            }
            catch (Exception ex) {
              return;
            }
          }
          if (r == 2) {
            String val = (String) value;
            try {
              double d = Double.parseDouble(val);
              double crossover = (d - 1) / d;
              crossover = crossover * 100;
              setValueAt(Double.toString(crossover), 4, 1);
              fireTableDataChanged();
            }
            catch (Exception e) {
              return;
            }
          }
        }
        else if (c == 2) {
          col2[r] = (String) value;
          // the user set the number of solutions
          if (r == 0) {
            try {
              String str = (String) value;
              double vl = Double.parseDouble(str);
              double popSize = 2 * vl;
              setValueAt(Double.toString(popSize), 1, 1);
              double mut = 1 / vl;
              mut = mut * 100;
              setValueAt(Double.toString(mut), 5, 1);
              fireTableDataChanged();
            }
            catch (Exception ex) {
              return;
            }
          }

          // user set the tournament size
          if (r == 2) {
            String val = (String) value;
            try {
              double d = Double.parseDouble(val);
              double crossover = (d - 1) / d;
              crossover = crossover * 100;
              setValueAt(Double.toString(crossover), 4, 1);
              fireTableDataChanged();
            }
            catch (Exception e) {
              return;
            }
          }
        }
      }

      public boolean isCellEditable(int r, int c) {
        if (c == 2) {
          return true;
        }
        return false;
      }
    }
  }

    class CachedParams implements java.io.Serializable {
      String[] recommended;
      String[] override;
      String estimatedTimeReq;
      String maxRunTime;
      String diff;
      int numVars;
      int stringLength;
    }
}