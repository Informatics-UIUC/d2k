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

    // int numSolutions;
  }

  private class ParamsView
      extends JUserPane {
    JTable paramsTable;
    EMOPopulationParams params;
    ParamsTableModel paramsModel;
    JTextField estimatedTime;
    JTextField maxRunTime;
    JTextField numSolutions;

    public void initView(ViewModule vm) {
      ParamsPanel pp = new ParamsPanel();
      pp.setBorder(new EmptyBorder(0, 10, 10, 0));
      setLayout(new BorderLayout());
      add(pp, BorderLayout.WEST);

      TimePanel tp = new TimePanel();
      tp.setBorder(new EmptyBorder(0, 10, 10, 0));
      add(tp, BorderLayout.EAST);
    }

    public void setInput(Object o, int i) {
      params = (EMOPopulationParams) o;
    }

    public Dimension getPreferredSize() {
      return new Dimension(700, 425);
    }

    protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(g2);
    }

    private class ParamsPanel
        extends JPanel {
      ParamsPanel() {
        setLayout(new GridBagLayout());

        paramsModel = new ParamsTableModel();
        paramsTable = new JTable(paramsModel);
        paramsTable.getTableHeader().setReorderingAllowed(false);
        paramsTable.setPreferredScrollableViewportSize(new Dimension(300, 70));
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

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timePanel.setBorder(new CompoundBorder(new TopBorder(),
                                               new EmptyBorder(10, 0, 0, 0)));
        JLabel l1 = new JLabel("Time for fitness and constraint eval:");
        timePanel.add(l1);
        estimatedTime = new JTextField(5);
        estimatedTime.setHorizontalAlignment(JTextField.CENTER);
        timePanel.add(estimatedTime);
        timePanel.add(new JLabel(" minutes"));
        Constrain.setConstraints(this, timePanel, 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        JPanel maxPanel = new JPanel();
        maxPanel.setBorder(new CompoundBorder(new TopBorder(),
                                              new EmptyBorder(10, 0, 0, 0)));

        maxPanel.setLayout(new GridBagLayout());

        Constrain.setConstraints(maxPanel, new JLabel("Maximum run time:"),
                                 0, 0, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST, 0, 0);
        maxRunTime = new JTextField(4);
        maxRunTime.setHorizontalAlignment(JTextField.CENTER);
        Constrain.setConstraints(maxPanel, maxRunTime,
                                 1, 0, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST, 0, 0);
        Constrain.setConstraints(maxPanel, new JPanel(),
                                 2, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        Constrain.setConstraints(maxPanel, new JLabel("Number of solutions desired:"),
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
            AdvSettingsPanel asp = new AdvSettingsPanel();
            JD2KFrame frm = new JD2KFrame("Advanced Settings");
            frm.getContentPane().add(asp);
            frm.pack();
            frm.show();
          }
        });
        Constrain.setConstraints(this, settingsPanel, 0, 4, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);


        numSolutions.addMouseListener(new MouseAdapter() {
           public void mousePressed(MouseEvent e) {
             numSolutions.setEditable(true);
           }
        });
        numSolutions.addFocusListener(new FocusAdapter() {
           public void focusLost(FocusEvent e) {
             // set the #solutions ...
             numSolutions.setEditable(false);
           }
        });
        // make the session name field uneditable and transfer the focus
        // to someone else when "enter' is pressed
        numSolutions.addKeyListener(new KeyAdapter() {
           public void keyPressed(KeyEvent e) {
              if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                 estimatedTime.setEditable(false);
                 estimatedTime.transferFocus();
              }
           }
        });

        estimatedTime.addMouseListener(new MouseAdapter() {
           public void mousePressed(MouseEvent e) {
             estimatedTime.setEditable(true);
           }
        });
        estimatedTime.addFocusListener(new FocusAdapter() {
           public void focusLost(FocusEvent e) {
             // set the #solutions ...
             estimatedTime.setEditable(false);
           }
        });
        // make the session name field uneditable and transfer the focus
        // to someone else when "enter' is pressed
        estimatedTime.addKeyListener(new KeyAdapter() {
           public void keyPressed(KeyEvent e) {
              if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                 estimatedTime.setEditable(false);
                 estimatedTime.transferFocus();
              }
           }
        });

        maxRunTime.addFocusListener(new FocusAdapter() {
           public void focusLost(FocusEvent e) {

           }
        });
        // make the session name field uneditable and transfer the focus
        // to someone else when "enter' is pressed
        maxRunTime.addKeyListener(new KeyAdapter() {
           public void keyPressed(KeyEvent e) {
              if(e.getKeyCode() == KeyEvent.VK_ENTER) {
              }
           }
        });
      }
    }

    private JLabel estimatedRunTime;
    private JLabel specifiedMaxTime;
    private JLabel difference;

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

        JPanel lblPanel = new JPanel(new GridLayout(3, 2));
        JLabel l1 = new JLabel("Estimated Run Time:");
        l1.setBorder(new EmptyBorder(2, 0, 2, 10));
        lblPanel.add(l1);
        estimatedRunTime = new JLabel();
        lblPanel.add(estimatedRunTime);
        JLabel l2 = new JLabel("Specified Max Run Time:");
        l2.setBorder(new EmptyBorder(2, 0, 2, 10));
        lblPanel.add(l2);
        specifiedMaxTime = new JLabel();
        lblPanel.add(specifiedMaxTime);
        JLabel l3 = new JLabel("Difference of:");
        l3.setBorder(new EmptyBorder(2, 0, 2, 10));
        lblPanel.add(l3);
        difference = new JLabel();
        lblPanel.add(difference);

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

    JRadioButton[] mutationTechniques;
    JRadioButton[] crossoverTechniques;
    JRadioButton[] selectionTechniques;

    private class AdvSettingsPanel
        extends JPanel {
      AdvSettingsPanel() {

        JOutlinePanel gaType = new JOutlinePanel("GA Type");
        JRadioButton simple = new JRadioButton("Simple GA");
        JRadioButton real = new JRadioButton("Real-coded GA");
        ButtonGroup bg = new ButtonGroup();
        bg.add(simple);
        bg.add(real);
        gaType.setLayout(new GridLayout(2, 1));
        gaType.add(simple);
        gaType.add(real);

        JOutlinePanel mutType = new JOutlinePanel("Mutation Technique");

        //JRadioButton mut = new JRadioButton("Mutation");
        //JRadioButton realMut = new JRadioButton("Real Mutation");
        int numMutations = Mutation.TYPES.length;
        mutationTechniques = new JRadioButton[numMutations];
        ButtonGroup bg2 = new ButtonGroup();
        for (int i = 0; i < numMutations; i++) {
          mutationTechniques[i] = new JRadioButton(Mutation.TYPES[i]);
          bg2.add(mutationTechniques[i]);
        }

        JLabel nLabel = new JLabel("n");
        JTextField nField = new JTextField(10);

        mutType.setLayout(new GridLayout(numMutations, 1));
        for (int i = 0; i < numMutations; i++) {
          mutType.add(mutationTechniques[i]);
        }

        JOutlinePanel xType = new JOutlinePanel("Crossover Technique");
        int numCrossover = Crossover.TYPES.length;
        crossoverTechniques = new JRadioButton[numCrossover];
        ButtonGroup bg3 = new ButtonGroup();
        for (int i = 0; i < numCrossover; i++) {
          crossoverTechniques[i] = new JRadioButton(Crossover.TYPES[i]);
          bg3.add(crossoverTechniques[i]);
        }

        xType.setLayout(new GridLayout(3, 1));
        for (int i = 0; i < numCrossover; i++) {
          xType.add(crossoverTechniques[i]);
        }

        JOutlinePanel selType = new JOutlinePanel("Selection Technique");
        int numSelection = Selection.TYPES.length;
        ButtonGroup bg4 = new ButtonGroup();
        selectionTechniques = new JRadioButton[numSelection];
        for (int i = 0; i < numSelection; i++) {
          selectionTechniques[i] = new JRadioButton(Selection.TYPES[i]);
          bg4.add(selectionTechniques[i]);
        }

        selType.setLayout(new GridLayout(5, 1));
        for (int i = 0; i < numSelection; i++) {
          selType.add(selectionTechniques[i]);
        }

        setLayout(new GridLayout(2, 2));
        add(gaType);
        add(mutType);
        add(xType);
        add(selType);
      }

      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g2);
      }

    }

    private class BarPanel
        extends JPanel {
      public Dimension getPreferredSize() {
        return new Dimension(250, 100);
      }
    }

    /**
     * A table model for the input of GA parameters.  is subclassed for
     * SO and MO populations.
     * @author not attributable
     * @version 1.0
     */
    private class ParamsTableModel
        extends DefaultTableModel {
      String[] col0;
      String[] col1;
      String[] col2;
      String[] names = {
          "", "Recommended", "Override"};

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
            "", "", "", "", "", ""};
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
          /*if (r == 0) {
            try {
              String str = (String) value;
              double vl = Double.parseDouble(str);
              double popSize = 2 * vl;
              setValueAt(Double.toString(popSize), 1, 1);
              double mut = 1 / vl;
              setValueAt(Double.toString(mut), 5, 1);
              fireTableDataChanged();
            }
            catch (Exception ex) {
              return;
            }
                     }*/
          if (r == 2) {
            String val = (String) value;
            try {
              double d = Double.parseDouble(val);
              double crossover = (d - 1) / d;
              setValueAt(Double.toString(crossover), 3, 1);
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
          /*if (r == 0) {
            try {
              String str = (String) value;
              double vl = Double.parseDouble(str);
              double popSize = 2 * vl;
              setValueAt(Double.toString(popSize), 1, 1);
              double mut = 1 / vl;
              setValueAt(Double.toString(mut), 5, 1);
              fireTableDataChanged();
            }
            catch (Exception ex) {
              return;
            }
                     }*/

          // user set the tournament size
          if (r == 2) {
            String val = (String) value;
            try {
              double d = Double.parseDouble(val);
              double crossover = (d - 1) / d;
              setValueAt(Double.toString(crossover), 3, 1);
              fireTableDataChanged();
            }
            catch (Exception e) {
              return;
            }
          }
        }
      }
    }

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