package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.Constrain;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DefineExternalFitnessFunctions extends UIModule {

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
  }

  public String[] getOutputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
  }

  public String getInputInfo(int i) {
    return "A data structure to hold the information about the fitness functions.";
  }

  public String getOutputInfo(int i) {
    return "A data structure to hold the information about the fitness functions.";
  }

  public String getModuleInfo() {
    return "";
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

  private class ExternalView extends JUserPane {
    DefaultListModel definedFunctionsModel;
    JList definedFunctions;
    JButton removeFunction;

    JTextField functionName;
    JTextField execPath;
    JTextField inputFilePath;
    JTextField outputFilePath;
    JComboBox min;

    EMOPopulationInfo popInfo;

    public Dimension getPreferredSize() {
      return new Dimension(600, 250);
    }

    public void initView(ViewModule vm) {
      // the remove button
      JPanel removeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      removeFunction = new JButton("Remove");
      removeFunction.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          Object[] vals = definedFunctions.getSelectedValues();
          for(int i = 0; i < vals.length; i++)
            definedFunctionsModel.removeElement(vals[i]);
        }
      });
      removeButtonPanel.add(removeFunction);

      // the list
      Object[] ff = getFitnessFunctions();
        definedFunctionsModel = new DefaultListModel();
      if(ff != null) {
        for(int i = 0; i < ff.length; i++)
          definedFunctionsModel.addElement(ff[i]);
      }
      definedFunctions = new JList(definedFunctionsModel);
      JScrollPane scroll = new JScrollPane(definedFunctions);
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
           if(retVal == JFileChooser.APPROVE_OPTION)
            fn = jfc.getSelectedFile().getAbsolutePath();
           else
            return;
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
           if(retVal == JFileChooser.APPROVE_OPTION)
            fn = jfc.getSelectedFile().getAbsolutePath();
           else
            return;
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
           if(retVal == JFileChooser.APPROVE_OPTION)
            fn = jfc.getSelectedFile().getAbsolutePath();
           else
            return;
          outputFilePath.setText(fn);
        }
      });

      Constrain.setConstraints(mainPanel, new JLabel("Function Name"), 0, 0, 1,
                               1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);
      functionName = new JTextField(20);
      Constrain.setConstraints(mainPanel, functionName, 1, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                               1, 1);

      Constrain.setConstraints(mainPanel, new JLabel("Path to Executable"), 0, 1,
                               1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);
      execPath = new JTextField(20);
      Constrain.setConstraints(mainPanel, execPath, 1, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                               1, 1);
      Constrain.setConstraints(mainPanel, selectExec, 2, 1, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);

      Constrain.setConstraints(mainPanel, new JLabel("Input File"), 0, 2, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);
      inputFilePath = new JTextField(20);
      Constrain.setConstraints(mainPanel, inputFilePath, 1, 2, 1, 1,
                               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                               1, 1);
      Constrain.setConstraints(mainPanel, selectInput, 2, 2, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);

      Constrain.setConstraints(mainPanel, new JLabel("Output File"), 0, 3, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);
      outputFilePath = new JTextField(20);
      Constrain.setConstraints(mainPanel, outputFilePath, 1, 3, 1, 1,
                               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                               1, 1);
      Constrain.setConstraints(mainPanel, selectOutput, 2, 3, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);

      Constrain.setConstraints(mainPanel, new JLabel("Minimize/Maximize"), 0, 4, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);
      Object[] items = {"Minimize", "Maximize"};
      min = new JComboBox(items);
      Constrain.setConstraints(mainPanel, min, 1, 4, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);

      JButton add = new JButton("Add Fitness Function");
      add.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          // add the ff

          String nme = functionName.getText();
          if(nme == null || nme.trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Function name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String exec = execPath.getText();
          if(exec == null || exec.trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Executable path name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String input = inputFilePath.getText();
          if(input == null || input.trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Input file path name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String output = outputFilePath.getText();
          if(output == null || output.trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Output file path name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String minmax = (String)min.getSelectedItem();

          // if we get to here, everything checks out ok
          // add the function
          FitnessFunction ff = new FitnessFunction(nme, exec, input, output, minmax);
          definedFunctionsModel.addElement(ff);

          functionName.setText("");
          execPath.setText("");
          inputFilePath.setText("");
          outputFilePath.setText("");
        }
      });

      Constrain.setConstraints(mainPanel, add, 1, 5, 1, 1,
                               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                               1, 1);

      JPanel buttonPanel = new JPanel();
      JButton done = new JButton("Done");
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

          tbl.setColumnLabel("Function Name", 0);
          tbl.setColumnLabel("Executable", 1);
          tbl.setColumnLabel("Input File", 2);
          tbl.setColumnLabel("Output File", 3);
          tbl.setColumnLabel("Min/Max", 4);

          for(int i = 0; i < functions.length; i++) {
            FitnessFunction f = (FitnessFunction)functions[i];
            tbl.setString(f.name, i, 0);
            tbl.setString(f.exec, i, 1);
            tbl.setString(f.input, i, 2);
            tbl.setString(f.output, i, 3);
            tbl.setString(f.minmax, i, 4);
          }

          // push out the pop info

          popInfo.setUseExternalFitnessEvaluation(true);
          popInfo.externalFitnessInfo = tbl;

          pushOutput(popInfo, 0);
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
      top.add(mainPanel, BorderLayout.CENTER);
      top.add(listPanel, BorderLayout.EAST);

      setLayout(new BorderLayout());
      add(new JLabel("Enter Fitness Functions"), BorderLayout.NORTH);
      add(top, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setInput(Object o, int i) {
      popInfo = (EMOPopulationInfo)o;
    }

    class FitnessFunction implements Serializable {
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
        return name+" : "+exec+" "+input+" "+output+" "+minmax;
      }
    }
  }
}