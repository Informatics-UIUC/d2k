package ncsa.d2k.modules.core.optimize.ga.emo.gui;

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
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

import ncsa.d2k.modules.core.optimize.ga.emo.mutation.*;
import ncsa.d2k.modules.core.optimize.ga.emo.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.selection.*;

public class Params
    extends UIModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationParams"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationParams",
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationParams"};
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
    return new ParamsView();
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

  private class CachedParams
      implements java.io.Serializable {
    String[] recommended;
    String[] override;
    String estimatedTimeReq;
    String maxRunTime;
    String diff;
    int numVars;
    int stringLength;
    boolean multiObjective;

    int numSolutions;

    int individualType;
    int crossoverType;
    int selectionType;
    int mutationType;

    double selectionPressure;

    double nMutation;
    double nSimulatedBinaryCrossover;
  }

  private class ParamsView
      extends JUserPane {

    /** the parameters for EMO */
    EMOPopulationParams params;
    /** the table model, holds several parameters */
    ParamsTableModel paramsModel;
    /** the estimated time that the evaluation will take */
    JTextField estimatedTime;
    /** the maximum run time a user is willing to wait */
    JTextField maxRunTime;
    JLabel numSolutionsLabel;
    /** the number of solutions desired */
    JTextField numSolutions;
    /** the advanced settings */
    AdvSettingsPanel adv;
    /** the frame that holds the advanced settings */
    JFrame advFrame;

    /** the label in the TimePanel that shows the estimated run time */
    JLabel estimatedRunTime;
        /** the label in the TimePanel that shows the user-specified max run time */
    JLabel specifiedMaxTime;
    /** the label in the TimePanel that shows the difference in times */
    JLabel difference;

    /** the type of mutation to use */
    int mutationType;
    /** the type of individual to use 0 = binary, 1 = real */
    int individualType;
    /** the type of selection to use */
    int selectionType;
    /** the type of crossover to use */
    int crossoverType;

    /**
     * Add components to this gui.
     */
    public void initView(ViewModule vm) {
      ParamsPanel pp = new ParamsPanel();
      pp.setBorder(new EmptyBorder(0, 10, 10, 0));
      setLayout(new BorderLayout());
      add(pp, BorderLayout.WEST);

      TimePanel tp = new TimePanel();
      tp.setBorder(new EmptyBorder(0, 10, 10, 60));
      add(tp, BorderLayout.EAST);

      JButton done = new JButton("Done");
      JButton abort = new JButton("Abort");
      abort.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          viewCancel();
        }
      });
      done.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          done();
        }
      });

      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      pnl.add(abort);
      pnl.add(done);

      add(pnl, BorderLayout.SOUTH);
    }

    private void done() {
      // gather all params

      // get the pop size
      String override = (String) paramsModel.getValueAt(0, 2);
      String rec = (String) paramsModel.getValueAt(0, 1);

      double popSize;
      try {
        popSize = Double.parseDouble(override);
      }
      catch (Exception ex) {
        popSize = Double.parseDouble(rec);
      }
      params.populationSize = (int) popSize;

      override = (String) paramsModel.getValueAt(1, 2);
      rec = (String) paramsModel.getValueAt(1, 1);
      try {
        popSize = Double.parseDouble(override);
      }
      catch (Exception ex) {
        popSize = Double.parseDouble(rec);
      }
      params.maxGenerations = (int) popSize;

      override = (String) paramsModel.getValueAt(2, 2);
      rec = (String) paramsModel.getValueAt(2, 1);
      try {
        popSize = Double.parseDouble(override);
      }
      catch (Exception ex) {
        popSize = Double.parseDouble(rec);
      }
      params.tournamentSize = (int) popSize;

      override = (String) paramsModel.getValueAt(3, 2);
      rec = (String) paramsModel.getValueAt(3, 1);
      try {
        popSize = Double.parseDouble(override);
      }
      catch (Exception ex) {
        popSize = Double.parseDouble(rec);
      }
      params.crossoverRate = popSize;

      override = (String) paramsModel.getValueAt(4, 2);
      rec = (String) paramsModel.getValueAt(4, 1);
      try {
        popSize = Double.parseDouble(override);
      }
      catch (Exception ex) {
        popSize = Double.parseDouble(rec);
      }
      params.mutationRate = popSize;

      override = (String) paramsModel.getValueAt(5, 2);
      rec = (String) paramsModel.getValueAt(5, 1);
      try {
        popSize = Double.parseDouble(override);
      }
      catch (Exception ex) {
        popSize = Double.parseDouble(rec);
      }
      params.generationGap = popSize;

      int numSol;
      try {
        String s = numSolutions.getText();
        numSol = Integer.parseInt(s);
      }
      catch(Exception e) {
        numSol = 0;
      }

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

      cp.maxRunTime = maxRunTime.getText();
      cp.estimatedTimeReq = estimatedTime.getText();
      cp.diff = difference.getText();
      cp.numVars = params.boundsAndPrecision.getNumRows();
      int stringLength = 0;
      for (int i = 0; i < params.boundsAndPrecision.getNumRows(); i++) {
        stringLength += params.boundsAndPrecision.getInt(i,
            params.boundsAndPrecision.getNumColumns() - 1);
      }
      cp.stringLength = stringLength;
      cp.numSolutions = numSol;


      setCachedParams(cp);

      viewDone("done");
      pushOutput(params, 0);
    }

    public void setInput(Object o, int i) {
      params = (EMOPopulationParams) o;

      // check the cached params...

      // check if SO or MO.  disable/enable necessary components
    }

    public Dimension getPreferredSize() {
      return new Dimension(700, 425);
    }

    /**
     * make everything anti-aliased
     * @param g
     */
    protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(g2);
    }

    /**
     * arrange all the standard parameters.  the advanced settings are
     * contained in the AdvancedSettingsPanel
     */
    private class ParamsPanel
        extends JPanel {
      ParamsPanel() {
        setLayout(new GridBagLayout());

        paramsModel = new ParamsTableModel();
        JTable paramsTable = new JTable(paramsModel);
        paramsTable.getTableHeader().setReorderingAllowed(false);
        paramsTable.setPreferredScrollableViewportSize(new Dimension(300, 65));
        // now set the widths of the table columns
        int numColumns = paramsModel.getColumnCount();
        for (int i = 1; i < numColumns; i++) {
          TableColumn column = paramsTable.getColumnModel().getColumn(i);
          column.setMaxWidth(60);
        }
        JScrollPane jsp = new JScrollPane(paramsTable);

        JLabel lbl = new JLabel("Set GA Parameters");
        Font f = lbl.getFont();
        Font newFont = new Font(f.getFamily(), Font.BOLD, 16);
        lbl.setFont(newFont);
        Constrain.setConstraints(this, lbl, 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, jsp, 0, 1, 1, 1,
                                 GridBagConstraints.BOTH,
                                 GridBagConstraints.WEST,
                                 1, 1);

        JPanel timePanel = new JPanel(new GridBagLayout());
        timePanel.setBorder(new CompoundBorder(new TopBorder(),
                                               new EmptyBorder(10, 0, 0, 0)));
        JLabel l1 = new JLabel("Time for Fitness and Constraint Eval:");
        //timePanel.add(l1);
        Constrain.setConstraints(timePanel, l1, 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        estimatedTime = new JTextField(5);
        estimatedTime.setHorizontalAlignment(JTextField.CENTER);
        //timePanel.add(estimatedTime);
        Constrain.setConstraints(timePanel, estimatedTime, 1, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        JLabel minLabel = new JLabel("min", JLabel.LEFT);
        //timePanel.add(minLabel);
        Constrain.setConstraints(timePanel, minLabel, 2, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        JButton estimate = new JButton("Estimate");
        Constrain.setConstraints(timePanel, estimate, 0, 1, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST,
                                 0, 0);
        Constrain.setConstraints(this, timePanel, 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        JPanel maxPanel = new JPanel();
        maxPanel.setBorder(new CompoundBorder(new TopBorder(),
                                              new EmptyBorder(10, 0, 0, 0)));

        maxPanel.setLayout(new GridBagLayout());
        Constrain.setConstraints(maxPanel, new JLabel("Maximum Run Time:"),
                                 0, 0, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST, 0, 0);
        maxRunTime = new JTextField(4);
        maxRunTime.setHorizontalAlignment(JTextField.CENTER);
        Constrain.setConstraints(maxPanel, maxRunTime,
                                 1, 0, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST, 0, 0);
        JLabel minLabel2 = new JLabel("min", JLabel.LEFT);
        Constrain.setConstraints(maxPanel, minLabel2,
                                 2, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        numSolutionsLabel = new JLabel("Number of Solutions Desired:");
        Constrain.setConstraints(maxPanel, numSolutionsLabel,
                                 0, 1, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST, 0, 0);
        numSolutions = new JTextField(4);
        numSolutions.setHorizontalAlignment(JTextField.CENTER);
        Constrain.setConstraints(maxPanel, numSolutions,
                                 1, 1, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST, 0, 0);
        Constrain.setConstraints(maxPanel, new JPanel(),
                                 2, 1, 1, 1,
                                 GridBagConstraints.BOTH,
                                 GridBagConstraints.WEST, 1, 1);

        Constrain.setConstraints(this, maxPanel, 0, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        settingsPanel.setBorder(new CompoundBorder(new TopBorder(),
            new EmptyBorder(10, 0, 0, 0)));
        JButton advSettings = new JButton("Advanced Settings");
        settingsPanel.add(advSettings);
        advSettings.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent ae) {
            if (!advFrame.isVisible()) {
              advFrame.show();
            }
          }
        });
        Constrain.setConstraints(this, settingsPanel, 0, 4, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        /*numSolutions.addMouseListener(new MouseAdapter() {
           public void mousePressed(MouseEvent e) {
             numSolutions.setEditable(true);
           }
                 });*/
        numSolutions.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            // set the recommended population size in the table model

            if (numSolutions.isEnabled()) {
              String num = numSolutions.getText();
              try {
                int numSol = Integer.parseInt(num);

                // the recommended pop size is twice the number of solutions
                int popSize = 2 * numSol;
                paramsModel.setValueAt(Integer.toString(popSize), 0, 1);
                paramsModel.fireTableCellUpdated(0, 1);
              }
              catch (Exception en) {
                // show dialog
              }
            }
          }
        });
        numSolutions.addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              numSolutions.transferFocus();
            }
          }
        });

        estimatedTime.setEditable(false);
        estimatedTime.setBorder(new EmptyBorder(0, 0, 0, 0));
        estimatedTime.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            estimatedTime.setEditable(true);
          }
        });
        estimatedTime.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            // set the #solutions ...
            estimatedTime.setEditable(false);
            estimatedRunTime.setText(estimatedTime.getText());
          }
        });
        // make the session name field uneditable and transfer the focus
        // to someone else when "enter' is pressed
        estimatedTime.addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              estimatedTime.transferFocus();
            }
          }
        });

        maxRunTime.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            String txt = maxRunTime.getText();
            specifiedMaxTime.setText(txt);
          }
        });
        // make the session name field uneditable and transfer the focus
        // to someone else when "enter' is pressed
        maxRunTime.addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              maxRunTime.transferFocus();
            }
          }
        });

        adv = new AdvSettingsPanel();
        advFrame = new JD2KFrame("Advanced Settings");
        advFrame.getContentPane().add(adv);
        advFrame.pack();
        advFrame.setVisible(false);
      }
    }

    /**
     * arrange the EstimatedTimeFactor components
     */
    private class TimePanel
        extends JPanel {
      TimePanel() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Estimated Time Factor");
        Font f = lbl.getFont();
        Font newFont = new Font(f.getFamily(), Font.BOLD, 16);
        lbl.setFont(newFont);
//        lbl.setBorder(new EmptyBorder(0, 10, 5, 0));
        Constrain.setConstraints(this, lbl, 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        BarPanel bp = new BarPanel();
        Constrain.setConstraints(this, bp, 0, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        //JPanel lblPanel = new JPanel(new GridLayout(3, 3));
        JPanel lblPanel = new JPanel(new GridBagLayout());
        JLabel l1 = new JLabel("Estimated Run Time:");
        l1.setBorder(new EmptyBorder(2, 0, 2, 10));
        //lblPanel.add(l1);
        Constrain.setConstraints(lblPanel, l1, 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);
        estimatedRunTime = new JLabel("      ");
        //lblPanel.add(estimatedRunTime);
        Constrain.setConstraints(lblPanel, estimatedRunTime, 1, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);
        JLabel minLabel = new JLabel("min", JLabel.LEFT);
        //lblPanel.add(minLabel);
        Constrain.setConstraints(lblPanel, minLabel, 2, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        JLabel l2 = new JLabel("Specified Max Run Time:");
        l2.setBorder(new EmptyBorder(2, 0, 2, 10));
        //lblPanel.add(l2);
        Constrain.setConstraints(lblPanel, l2, 0, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);
        specifiedMaxTime = new JLabel("      ");
        Constrain.setConstraints(lblPanel, specifiedMaxTime, 1, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);
        //lblPanel.add(specifiedMaxTime);

        JLabel minLabel2 = new JLabel("min", JLabel.LEFT);
        //lblPanel.add(minLabel2);
        Constrain.setConstraints(lblPanel, minLabel2, 2, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        JLabel l3 = new JLabel("Difference of:");
        l3.setBorder(new EmptyBorder(2, 0, 2, 10));
        //lblPanel.add(l3);
        Constrain.setConstraints(lblPanel, l3, 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);
        difference = new JLabel("      ");
        //lblPanel.add(difference);
        Constrain.setConstraints(lblPanel, difference, 1, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        JLabel minLabel3 = new JLabel("min", JLabel.LEFT);
        //lblPanel.add(minLabel3);
        Constrain.setConstraints(lblPanel, minLabel3, 2, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        lblPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        Constrain.setConstraints(this, lblPanel, 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btn = new JButton("Optimizing Tips");
        buttonPanel.add(btn);
        Constrain.setConstraints(this, buttonPanel, 0, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        Constrain.setConstraints(this, new JPanel(), 0, 4, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
      }
    }

    /**
     * An interface for the advanced settings for the GA.
     * @author David Clutter
     * @version 1.0
     */
    private class AdvSettingsPanel
        extends JPanel
        implements ActionListener {

      class IndividualRadioButton
          extends JRadioButton {
        int type;
        IndividualRadioButton(String s) {
          super(s);
        }
      }

      class MutationRadioButton
          extends JRadioButton {
        int type;
        MutationRadioButton(String s) {
          super(s);
        }
      }

      class SelectionRadioButton
          extends JRadioButton {
        int type;
        SelectionRadioButton(String s) {
          super(s);
        }
      }

      class CrossoverRadioButton
          extends JRadioButton {
        int type;
        CrossoverRadioButton(String s) {
          super(s);
        }
      }

      JLabel selPres;
      JTextField selPressure;

      JLabel crossoverNLabel;
      JTextField crossoverNField;

      JLabel mutationNLabel;
      JTextField mutationNField;

      IndividualRadioButton[] individualRadio;
      MutationRadioButton[] mutationRadio;
      SelectionRadioButton[] selectionRadio;
      CrossoverRadioButton[] crossoverRadio;

      AdvSettingsPanel() {
        JOutlinePanel gaType = new JOutlinePanel("Individual Type");
        IndividualRadioButton binaryInd = new IndividualRadioButton(
            "Binary-coded Individuals");
        binaryInd.addActionListener(this);
        binaryInd.type = 0;

        IndividualRadioButton realInd = new IndividualRadioButton(
            "Real-coded Individuals");
        realInd.addActionListener(this);
        realInd.type = 1;

        individualRadio = new IndividualRadioButton[] {
            binaryInd, realInd};

        ButtonGroup bg = new ButtonGroup();
        bg.add(binaryInd);
        bg.add(realInd);
        gaType.setLayout(new GridLayout(2, 1));
        gaType.add(binaryInd);
        gaType.add(realInd);

        JOutlinePanel mutType = new JOutlinePanel("Mutation Technique");
        mutationNLabel = new JLabel("n");
        mutationNField = new JTextField(10);

        JPanel mutNPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mutNPanel.add(mutationNLabel);
        mutNPanel.add(mutationNField);

        int numMutations = Mutation.TYPES.length;
        mutationRadio = new MutationRadioButton[numMutations];
        ButtonGroup bg2 = new ButtonGroup();
        mutType.setLayout(new GridLayout(numMutations + 1, 1));
        for (int i = 0; i < numMutations; i++) {
          MutationRadioButton mrb = new MutationRadioButton(Mutation.TYPES[i]);
          mrb.addActionListener(this);
          mrb.type = i;
          bg2.add(mrb);
          mutType.add(mrb);
          if (i == Mutation.REAL_MUTATION) {
            mutType.add(mutNPanel);
          }
          mutationRadio[i] = mrb;
        }

        crossoverNLabel = new JLabel("n");
        crossoverNField = new JTextField(10);
        JPanel crossNPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        crossNPanel.add(crossoverNLabel);
        crossNPanel.add(crossoverNField);

        JOutlinePanel xType = new JOutlinePanel("Crossover Technique");
        int numCrossover = Crossover.TYPES.length;
        crossoverRadio = new CrossoverRadioButton[numCrossover];
        ButtonGroup bg3 = new ButtonGroup();
        xType.setLayout(new GridLayout(numCrossover + 1, 1));
        for (int i = 0; i < numCrossover; i++) {
          CrossoverRadioButton crb = new CrossoverRadioButton(Crossover.TYPES[i]);
          crb.addActionListener(this);
          crb.type = i;
          bg3.add(crb);
          xType.add(crb);
          if (i == Crossover.SIMULATED_BINARY_CROSSOVER) {
            xType.add(crossNPanel);
          }
          crossoverRadio[i] = crb;
        }

        JOutlinePanel selType = new JOutlinePanel("Selection Technique");
        int numSelection = Selection.TYPES.length;
        selectionRadio = new SelectionRadioButton[numSelection];
        ButtonGroup bg4 = new ButtonGroup();

        selPres = new JLabel("Selection Pressure");
        selPressure = new JTextField(5);
        selPres.setEnabled(false);
        selPressure.setEnabled(false);

        JPanel selPressurePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        selPressurePanel.add(selPres);
        selPressurePanel.add(selPressure);

        selType.setLayout(new GridLayout(6, 1));

        for (int i = 0; i < numSelection; i++) {
          SelectionRadioButton srb = new SelectionRadioButton(Selection.TYPES[i]);
          srb.addActionListener(this);
          srb.type = i;
          bg4.add(srb);
          selType.add(srb);
          if (i == Selection.RANK_SELECTION) {
            selType.add(selPressurePanel);
          }
          selectionRadio[i] = srb;
        }

        setLayout(new GridLayout(2, 2));
        add(gaType);
        add(mutType);
        add(xType);
        add(selType);

        binaryInd.setSelected(true);
        individualType = 0;
        binaryIndividualSelected();
      }

      private void binaryIndividualSelected() {
        mutationRadio[Mutation.MUTATION].setEnabled(true);
        mutationRadio[Mutation.REAL_MUTATION].setEnabled(false);

        mutationRadio[Mutation.MUTATION].setSelected(true);
        mutationType = Mutation.MUTATION;
        this.mutationNField.setEnabled(false);
        this.mutationNLabel.setEnabled(false);

        crossoverRadio[Crossover.TWO_POINT_CROSSOVER].setEnabled(true);
        crossoverRadio[Crossover.UNIFORM_CROSSOVER].setEnabled(true);
        crossoverRadio[Crossover.SIMULATED_BINARY_CROSSOVER].setEnabled(false);

        crossoverRadio[Crossover.UNIFORM_CROSSOVER].setSelected(true);
        crossoverType = Crossover.UNIFORM_CROSSOVER;
        this.crossoverNField.setEnabled(false);
        this.crossoverNLabel.setEnabled(false);

        selectionRadio[Selection.TOURNAMENT_WITHOUT_REPLACEMENT].setSelected(true);
        selectionType = Selection.TOURNAMENT_WITHOUT_REPLACEMENT;
      }

      private void realIndividualSelected() {
        mutationRadio[Mutation.MUTATION].setEnabled(false);
        mutationRadio[Mutation.REAL_MUTATION].setEnabled(true);

        mutationRadio[Mutation.REAL_MUTATION].setSelected(true);
        mutationType = Mutation.REAL_MUTATION;
        this.mutationNField.setEnabled(true);
        this.mutationNLabel.setEnabled(true);

        crossoverRadio[Crossover.TWO_POINT_CROSSOVER].setEnabled(false);
        crossoverRadio[Crossover.UNIFORM_CROSSOVER].setEnabled(false);
        crossoverRadio[Crossover.SIMULATED_BINARY_CROSSOVER].setEnabled(true);

        crossoverRadio[Crossover.SIMULATED_BINARY_CROSSOVER].setSelected(true);
        crossoverType = Crossover.SIMULATED_BINARY_CROSSOVER;
        this.crossoverNField.setEnabled(true);
        this.crossoverNLabel.setEnabled(true);

        selectionRadio[Selection.STOCHASTIC_UNIVERSAL_SAMPLING].setSelected(true);
        selectionType = Selection.STOCHASTIC_UNIVERSAL_SAMPLING;
      }

      public void actionPerformed(ActionEvent ae) {
        Component src = (Component) ae.getSource();

        if (src instanceof IndividualRadioButton) {
          individualType = ( (IndividualRadioButton) src).type;

          // if binary..
          if (individualType == 0) {
            binaryIndividualSelected();
            // if real
          }
          else {
            realIndividualSelected();
          }
        }
        else if (src instanceof MutationRadioButton) {
          mutationType = ( (MutationRadioButton) src).type;
          // if real mutation, enable N text field
          if (mutationType == Mutation.REAL_MUTATION) {
            this.mutationNLabel.setEnabled(true);
            this.mutationNField.setEnabled(true);
          }
          // else disable N text field
          else {
            this.mutationNLabel.setEnabled(false);
            this.mutationNField.setEnabled(false);
          }
        }
        else if (src instanceof SelectionRadioButton) {
          selectionType = ( (SelectionRadioButton) src).type;
          // if rank selection, enable selction pressure
          if (selectionType == Selection.RANK_SELECTION) {
            this.selPres.setEnabled(true);
            this.selPressure.setEnabled(true);
          }
          // else disable selection pressure
          else {
            this.selPres.setEnabled(false);
            this.selPressure.setEnabled(false);
          }
        }
        else if (src instanceof CrossoverRadioButton) {
          crossoverType = ( (CrossoverRadioButton) src).type;
          // if simulated binary crossover, enable N text field
          // else disable n text field
          if (crossoverType == Crossover.SIMULATED_BINARY_CROSSOVER) {
            this.crossoverNLabel.setEnabled(true);
            this.crossoverNField.setEnabled(true);
          }
          else {
            this.crossoverNLabel.setEnabled(false);
            this.crossoverNField.setEnabled(false);
          }
        }
      }

      /**
       * make everything anti-aliased
       * @param g
       */
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g2);
      }
    }

    private class BarPanel
        extends JPanel {
      /*      public Dimension getPreferredSize() {
              return new Dimension(300, 100);
            }*/
    }

    /**
     * The table model for the JTable.  It holds the starting population size,
     * the maximum number of generations, the tournament size,
     * the probability of crossover, the probability of mutation, and
     * the generation gap.
     *
         * When the rec. population size is set, the mutation rate will also be set.
     */
    private class ParamsTableModel
        extends DefaultTableModel {
      String[] col0;
      String[] col1;
      String[] col2;
      String[] names = {
          "", "Rec", "Override"};

      public int getColumnCount() {
        return 3;
      }

      public String getColumnName(int i) {
        return names[i];
      }

      public boolean isCellEditable(int r, int c) {
        if (c == 2) {
          return true;
        }
        return false;
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

      ParamsTableModel() {
        col0 = new String[] {
            "Starting Population Size",
            "Maximum Number of Generations",
            "Tournament Size",
            "Probability of Crossover",
            "Probability of Mutation",
            "Generation Gap"};

        col1 = new String[] {
            "", "100", "4", ".75", "", "1"};
        col2 = new String[] {
            "", "", "", "", "", ""};
      }

      public int getRowCount() {
        return 6;
      }

      public void setValueAt(Object value, int r, int c) {
        if (c == 0) {
          col0[r] = (String) value;
        }
        else if (c == 1) {
          col1[r] = (String) value;
          if (r == 0) {
            try {
              int popsize = Integer.parseInt( (String) value);
              double mutRate = 1.0 / popsize;
              setValueAt(Double.toString(mutRate), 4, 1);
              fireTableDataChanged();
            }
            catch (Exception ee) {
              return;
            }
          }
          /*if (r == 2) {
            try {
              double d = Double.parseDouble( (String) value);
              double crossover = (d - 1) / d;
              setValueAt(Double.toString(crossover), 3, 1);
              fireTableDataChanged();
            }
            catch (Exception e) {
              return;
            }
                     }*/
        }
        else if (c == 2) {
          col2[r] = (String) value;
        }
      }
    }

    /**
     * draw a line across the top of a component
     * @author David Clutter
     * @version 1.0
     */
    class TopBorder
        extends AbstractBorder {
      public void paintBorder(Component c, Graphics g, int x, int y, int w,
                              int h) {
        g.setColor(Color.darkGray);
        g.drawLine(x, y, x + w, y);
      }
    }
  }
}