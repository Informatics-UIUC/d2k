package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.Constrain;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

/**
 * An interface to input the fitness functions when the constraints are
 * calculated by a foreign executable
 * @author David Clutter
 * @version 1.0
 */
public class DefineExternalConstraints extends UIModule {

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String[] getOutputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String getInputInfo(int i) {
    return "The parameters for the EMO problem.";
  }

  public String getOutputInfo(int i) {
    return "The parameters for the EMO problem.";
  }

  public String getInputName(int i) {
    return "Parameters";
  }

  public String getOutputName(int i) {
    return "Parameters";
  }

  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "Input constraint violation functions that are calculated by an executable.";
    s += "<p>Detailed Description: ";
    s += "Input all the properties needed to call an executable to evaluate ";
    s += "the constraint violation functions for a population.";
    s += "First, input the name of the constraint.  Next, input the path ";
    s += "to the executable.  Next, input the paths to the input and output files. ";
    s += "Finally, input a weight to assign to this constraint.";
    s += "<p>An executable must read the genes in from a file and write out ";
    s += "a file containing the values of the fitness functions.  The format ";
    s += "of the input file is fixed.  The first line contains the size of the ";
    s += "population.  The second line contains the number of traits.";
    s += "Every following line should contain the values for an individual, ";
    s += "space-delimited.";
    s += "The output file must also follow a specific format.  Each row of ";
    s += "the file must contain the value for this constraint violation function on the ";
    s += "associated individual in the genes file.  Order is preserved.";
    return s;
  }

  protected UserView createUserView() {
    return new ExternalView();
  }

  public String[] getFieldNameMapping() {
    return null;
  }

  private Object[] constraints;
  public void setConstraints(Object[] o) {
    constraints = o;
  }
  public Object[] getConstraints() {
    return constraints;
  }

  /*  Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }

  protected class ExternalView extends JUserPane {
    protected DefaultListModel definedFunctionsModel;
    protected JList definedFunctions;
    protected JButton removeFunction;

    protected JTextField functionName;
    protected JTextField execPath;
    protected JTextField inputFilePath;
    protected JTextField outputFilePath;
//    JComboBox min;
    protected JTextField weight;

    protected Parameters parameters;

    public Dimension getPreferredSize() {
      return new Dimension(600, 250);
    }

    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(g2);
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
      Object[] ff = getConstraints();
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

      Constrain.setConstraints(mainPanel, new JLabel("Constraint Name"), 0, 0, 1,
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

      Constrain.setConstraints(mainPanel, new JLabel("Weight"), 0, 4, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);
      weight = new JTextField(5);
      Constrain.setConstraints(mainPanel, weight, 1, 4, 1, 1,
                               GridBagConstraints.NONE, GridBagConstraints.WEST,
                               1, 1);
      Constrain.setConstraints(mainPanel, new JPanel(), 0, 5, 1, 1,
                               GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST,
                               1,1);

      JButton add = new JButton("Add Constraint");
      add.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          // add the ff

          String nme = functionName.getText();
          if(nme == null || nme.trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Constraint name not specified",
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
          String wght = (String)weight.getText();

          // if we get to here, everything checks out ok
          // add the function
          Constraint ff = new Constraint(nme, exec, input, output, wght);
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

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton done = new JButton("Done");
      done.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          Object[] functions = definedFunctionsModel.toArray();
          setConstraints(functions);

          // now create the table and add it to the pop info
/*          MutableTableImpl tbl = new MutableTableImpl();
          int numFunctions = functions.length;
          tbl.addColumn(new String[numFunctions]);
          tbl.addColumn(new String[numFunctions]);
          tbl.addColumn(new String[numFunctions]);
          tbl.addColumn(new String[numFunctions]);
          tbl.addColumn(new String[numFunctions]);

          tbl.setColumnLabel("Constraint Name", 0);
          tbl.setColumnLabel("Executable", 1);
          tbl.setColumnLabel("Input File", 2);
          tbl.setColumnLabel("Output File", 3);
          tbl.setColumnLabel("Min/Max", 4);

          for(int i = 0; i < functions.length; i++) {
            Constraint f = (Constraint)functions[i];
            tbl.setString(f.name, i, 0);
            tbl.setString(f.exec, i, 1);
            tbl.setString(f.input, i, 2);
            tbl.setString(f.output, i, 3);
            tbl.setString(f.weight, i, 4);
          }*/

          Constraints constraints = parameters.constraints;
          if(constraints == null) {
            constraints = new Constraints();
            parameters.constraints = constraints;
          }
          for(int i = 0; i < functions.length; i++) {
            Constraint f = (Constraint)functions[i];
            double wgh = 0;
            constraints.addExternConstraint(f.name, f.exec, f.input, f.output,
                                            wgh);
          }

          // push out the pop info

          //popInfo.useExternalFitnessEvaluation = true;
          //popInfo.externalFitnessInfo = tbl;

          pushOutput(parameters, 0);
          parameters = null;
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
      add(new JLabel("Enter Constraints"), BorderLayout.NORTH);
      add(top, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setInput(Object o, int i) {
      parameters = (Parameters)o;
    }
  }

  private static class Constraint implements Serializable {
 static final long serialVersionUID = -415474569233066569L;
    String name;
    String exec;
    String input;
    String output;
    String weight;

    Constraint(String n, String e, String i, String o, String w) {
      name = n;
      exec = e;
      input = i;
      output = o;
      weight = w;
    }

    public String toString() {
      return name+" : "+exec+" "+input+" "+output+" "+weight;
    }
  }

}