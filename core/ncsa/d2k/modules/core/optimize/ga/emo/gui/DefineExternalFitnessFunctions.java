package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

/**
 * An interface to input the fitness functions when the fitness functions are
 * calculated by a foreign executable
 * @author David Clutter
 * @version 1.0
 */
public class DefineExternalFitnessFunctions
    extends UIModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationParams"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationParams"};
  }

  public String getInputInfo(int i) {
    return
        "A data structure to hold the information about the fitness functions.";
  }

  public String getOutputInfo(int i) {
    return
        "A data structure to hold the information about the fitness functions.";
  }

  public String getModuleInfo() {
    return "Input the parameters for an executable to calculate fitness functions.";
  }

  public String getInputName(int i) {
    return "EMOParams";
  }

  public String getOutputName(int i) {
    return "EMOParams";
  }

  protected UserView createUserView() {
    return new ExternalView();
  }

  public String[] getFieldNameMapping() {
    return null;
  }

  private Object[] fitnessFunctions;
  public void setFitnessFunctions(Object[] o) {
    fitnessFunctions = o;
  }

  public Object[] getFitnessFunctions() {
    return fitnessFunctions;
  }

  /*  Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }

  private class ExternalView
      extends JUserPane {

    /** the list model for the jlist that contains the FFs */
    DefaultListModel definedFunctionsModel;
    /** the list that contains the defined FFs */
    JList definedFunctions;
    /** remove the selected FF from the jlist */
    JButton removeFunction;

    /** the name of the FF */
    JTextField functionName;
    /** the path to the foreign executable */
    JTextField execPath;
    /** the path to the input file */
    JTextField inputFilePath;
    /** the path to the output file */
    JTextField outputFilePath;
    /** minimize/maximize */
    JComboBox min;

    EMOPopulationParams popInfo;

    public Dimension getPreferredSize() {
      return new Dimension(600, 250);
    }

    private static final String HTML = "<html>";
    private static final String INPUT = "Input: ";
    private static final String BR = "<br>";
    private static final String OUTPUT = "Output: ";
    private static final String MAXMIN = "Max/Min: ";
    private static final String CLOSE = "</html>";

    public void initView(ViewModule vm) {
      // the remove button
      JPanel removeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      removeFunction = new JButton("Remove");
      // when remove is pressed, remove all the selected elements in the jlist
      removeFunction.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          Object[] vals = definedFunctions.getSelectedValues();
          for (int i = 0; i < vals.length; i++) {
            definedFunctionsModel.removeElement(vals[i]);
          }
        }
      });
      removeButtonPanel.add(removeFunction);

      // the jlist
      Object[] ff = getFitnessFunctions();
      definedFunctionsModel = new DefaultListModel();
      // add the FFs from the last run ..
      if (ff != null) {
        for (int i = 0; i < ff.length; i++) {
          definedFunctionsModel.addElement(ff[i]);
        }
      }
      // the jlist component
      definedFunctions = new JList(definedFunctionsModel) {
        // set the tool tip text for each FF defined
        public String getToolTipText(MouseEvent me) {
          Point p = me.getPoint();
          int idx = locationToIndex(p);
          FitnessFunction ff = (FitnessFunction) definedFunctionsModel.
              elementAt(idx);

          StringBuffer tip = new StringBuffer();
          tip.append(HTML);
          tip.append(INPUT);
          tip.append(ff.input);
          tip.append(BR);
          tip.append(OUTPUT);
          tip.append(ff.output);
          tip.append(BR);
          tip.append(MAXMIN);
          tip.append(ff.minmax);
          tip.append(CLOSE);
          return tip.toString();
        }
      };
      JScrollPane scroll = new JScrollPane(definedFunctions);
      scroll.setColumnHeaderView(new JLabel("Fitness Functions"));
      scroll.setPreferredSize(new Dimension(200, 200));
      JPanel listPanel = new JPanel(new BorderLayout());
      listPanel.add(scroll, BorderLayout.CENTER);
      listPanel.add(removeButtonPanel, BorderLayout.SOUTH);

      JPanel mainPanel = new JPanel(new GridBagLayout());
      JButton selectExec = new JButton("...");
      selectExec.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          JFileChooser jfc = new JFileChooser();

          // set the title of the FileDialog
          StringBuffer sb = new StringBuffer("Select File");
          jfc.setDialogTitle(sb.toString());

          // get the file
          String fn;
          int retVal = jfc.showOpenDialog(null);
          if (retVal == JFileChooser.APPROVE_OPTION) {
            fn = jfc.getSelectedFile().getAbsolutePath();
          }
          else {
            return;
          }
          execPath.setText(fn);
        }
      });
      JButton selectInput = new JButton("...");
      selectInput.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          JFileChooser jfc = new JFileChooser();

          // set the title of the FileDialog
          StringBuffer sb = new StringBuffer("Select File");
          jfc.setDialogTitle(sb.toString());

          // get the file
          String fn;
          int retVal = jfc.showOpenDialog(null);
          if (retVal == JFileChooser.APPROVE_OPTION) {
            fn = jfc.getSelectedFile().getAbsolutePath();
          }
          else {
            return;
          }
          inputFilePath.setText(fn);
        }
      });
      JButton selectOutput = new JButton("...");
      selectOutput.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          JFileChooser jfc = new JFileChooser();

          // set the title of the FileDialog
          StringBuffer sb = new StringBuffer("Select File");
          jfc.setDialogTitle(sb.toString());

          // get the file
          String fn;
          int retVal = jfc.showOpenDialog(null);
          if (retVal == JFileChooser.APPROVE_OPTION) {
            fn = jfc.getSelectedFile().getAbsolutePath();
          }
          else {
            return;
          }
          outputFilePath.setText(fn);
        }
      });

      Constrain.setConstraints(mainPanel, new JLabel("Function Name"), 0, 0, 1,
                               1, GridBagConstraints.NONE,
                               GridBagConstraints.WEST,
                               1, 1);
      functionName = new JTextField(20);
      Constrain.setConstraints(mainPanel, functionName, 1, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.WEST,
                               1, 1);

      Constrain.setConstraints(mainPanel, new JLabel("Path to Executable"), 0,
                               1,
                               1, 1, GridBagConstraints.NONE,
                               GridBagConstraints.WEST,
                               1, 1);
      execPath = new JTextField(20);
      Constrain.setConstraints(mainPanel, execPath, 1, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.WEST,
                               1, 1);
      Constrain.setConstraints(mainPanel, selectExec, 2, 1, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);

      Constrain.setConstraints(mainPanel, new JLabel("Input File"), 0, 2, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);
      inputFilePath = new JTextField(20);
      Constrain.setConstraints(mainPanel, inputFilePath, 1, 2, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.WEST,
                               1, 1);
      Constrain.setConstraints(mainPanel, selectInput, 2, 2, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);

      Constrain.setConstraints(mainPanel, new JLabel("Output File"), 0, 3, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);
      outputFilePath = new JTextField(20);
      Constrain.setConstraints(mainPanel, outputFilePath, 1, 3, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.WEST,
                               1, 1);
      Constrain.setConstraints(mainPanel, selectOutput, 2, 3, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);

      Constrain.setConstraints(mainPanel, new JLabel("Minimize/Maximize"), 0, 4,
                               1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);
      Object[] items = {
          "Minimize", "Maximize"};
      min = new JComboBox(items);
      Constrain.setConstraints(mainPanel, min, 1, 4, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);

      JButton add = new JButton("Add Fitness Function");
      add.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          // add the ff

          String nme = functionName.getText();
          if (nme == null || nme.trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Function name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String exec = execPath.getText();
          if (exec == null || exec.trim().length() == 0) {
            JOptionPane.showMessageDialog(null,
                                          "Executable path name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String input = inputFilePath.getText();
          if (input == null || input.trim().length() == 0) {
            JOptionPane.showMessageDialog(null,
                                          "Input file path name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String output = outputFilePath.getText();
          if (output == null || output.trim().length() == 0) {
            JOptionPane.showMessageDialog(null,
                                          "Output file path name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String minmax = (String) min.getSelectedItem();

          // if we get to here, everything checks out ok
          // add the function
          FitnessFunction ff = new FitnessFunction(nme, exec, input, output,
              minmax);
          definedFunctionsModel.addElement(ff);

          functionName.setText("");
          execPath.setText("");
          inputFilePath.setText("");
          outputFilePath.setText("");
        }
      });

      Constrain.setConstraints(mainPanel, add, 1, 5, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.WEST,
                               1, 1);

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton done = new JButton("Done");
      // when done is pressed, push out the EMOPopulationParams
      // with the externalFitnessInfo set, and the useExternalFitnessEvaluation
      // flag to true
      done.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          Object[] functions = definedFunctionsModel.toArray();
          setFitnessFunctions(functions);

          // now create the table and add it to the pop info
          MutableTableImpl tbl = new MutableTableImpl();
          int numFunctions = functions.length;
          tbl.addColumn(new String[numFunctions]);
          tbl.addColumn(new String[numFunctions]);
          tbl.addColumn(new String[numFunctions]);
          tbl.addColumn(new String[numFunctions]);
          tbl.addColumn(new String[numFunctions]);

          // the table that describes the FFs has 5 columns
          tbl.setColumnLabel("Function Name", 0);
          tbl.setColumnLabel("Executable", 1);
          tbl.setColumnLabel("Input File", 2);
          tbl.setColumnLabel("Output File", 3);
          tbl.setColumnLabel("Min/Max", 4);

          for (int i = 0; i < functions.length; i++) {
            FitnessFunction f = (FitnessFunction) functions[i];
            tbl.setString(f.name, i, 0);
            tbl.setString(f.exec, i, 1);
            tbl.setString(f.input, i, 2);
            tbl.setString(f.output, i, 3);
            tbl.setString(f.minmax, i, 4);
          }

          popInfo.useExternalFitnessEvaluation = true;
          popInfo.externalFitnessInfo = tbl;

          // push out the pop info
          pushOutput(popInfo, 0);
          popInfo = null;
          viewDone("Done");
        }
      });
      JButton abort = new JButton("Abort");
      abort.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          viewCancel();
        }
      });
      buttonPanel.add(abort);
      buttonPanel.add(done);

      JPanel top = new JPanel(new BorderLayout());
      mainPanel.setBorder(new EmptyBorder(1, 10, 5, 0));
      top.add(mainPanel, BorderLayout.CENTER);
      top.add(listPanel, BorderLayout.EAST);

      setLayout(new BorderLayout());
      JLabel lbl = new JLabel("Enter Fitness Functions");
      Font f = lbl.getFont();
      Font newFont = new Font(f.getFamily(), Font.BOLD, 16);
      lbl.setFont(newFont);
      lbl.setBorder(new EmptyBorder(10, 10, 10, 0));

      add(lbl, BorderLayout.NORTH);
      add(top, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
    }

    // anti-alias everything
    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(g2);
    }

    public void setInput(Object o, int i) {
      popInfo = (EMOPopulationParams) o;
    }

    class FitnessFunction
        implements Serializable {
      static final long serialVersionUID = 1824836916530132076L;

      String name;
      String exec;
      String input;
      String output;
      String minmax;

      FitnessFunction(String n, String e, String i, String o, String m) {
        name = n;
        exec = e;
        input = i;
        output = o;
        minmax = m;
      }

      public String toString() {
        //return "<html>"+name+" :<br>    "+exec+"<br>"+input+"<br>"+output+"<br>"+minmax+"</html>";
        return name;
      }
    }
  }
}