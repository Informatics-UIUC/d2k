/*
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 *
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 *
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
/*
 * put your module comment here formatted with JxBeauty (c)
 * johann.langhofer@nextra.at
 */
package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.gui.Constrain;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;


/**
 * ChooseAttributes.java (previously ChooseFields) Allows the user to choose
 * which columns of a table are inputs and outputs. Then assigns them in an
 * ExampleTable.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ChooseAttributes extends HeadlessUIModule {

   //~ Instance fields *********************************************************

   /** holds the names of the inputs attributes. */
   protected Object[] selectedInputs;

   /** holds the names of the output attributes. */
   protected Object[] selectedOutputs;

   //~ Methods *****************************************************************

   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new AttributeView(); }


   /**
    * The list of strings returned by this method allows the module to map the
    * results returned from the pier to the position dependent outputs. The
    * order in which the names appear in the string array is the order in which
    * to assign them to the outputs.
    *
    * @return a string array containing the names associated with the outputs in
    *         the order the results should appear in the outputs.
    */
   protected String[] getFieldNameMapping() { return null; }

   /**
    * headless conversion support. If the table is in the same format as the
    * last successful execution, push out the same selected attributes.
    *
    * @throws Exception when something goes wrong.
    */
   public void doit() throws Exception {
      Table table = (Table) this.pullInput(0);

      if (selectedInputs == null || selectedOutputs == null) {
         throw new Exception(getAlias() +
                             " has not been configured. Before running headless, run with the gui and configure the parameters.");
      }

      // Map each column name to an index for the column
      HashMap colindices = new HashMap();

      for (int i = 0; i < table.getNumColumns(); i++) {
         String label = table.getColumnLabel(i);
         colindices.put(label, new Integer(i));
      }

      // Create the input feature index array.
      Object[] selected = this.getSelectedInputs();
      int[] inputFeatures = new int[0];
      String[] selectedNames;

      if (selected.length == 0) {
         System.out.println(getAlias() +
                            ": Warning - No input attributes were selected. Skipping " +
                            "setting of input attributes.");
      } else {

         // vered - test that all columns are really in the map
         selectedNames = new String[selected.length];

         for (int i = 0; i < selectedNames.length; i++) {
            selectedNames[i] = (String) selected[i];
         }

         inputFeatures =
            StaticMethods.getIntersectIds(selectedNames, colindices);

         if (inputFeatures.length < selectedNames.length) {
            throw new Exception(getAlias() +
                                ": Some of the configured input attributes were not found in the input table." +
                                " Please reconfigure this module via a GUI run so it can run Headless.");
            // the following was commented out and replaced by the previous code
            // line.
            // vered.
            /*
             * int[] inputFeatures = new int[selected.length]; for (int i = 0; i
             * < selected.length; i++) { String s = (String) selected[i];
             * Integer ii = (Integer) colindices.get (s); inputFeatures[i] =
             * ii.intValue (); }
             */
         }
      } // end if

      // Create the output features array
      selected = this.getSelectedOutputs();

      int[] outputFeatures = new int[0];

      if (selected == null || selected.length == 0) {
         System.out.println(getAlias() +
                            ": Warning - No output attributes were selected. Skipping " +
                            "setting of output attributes.");
      } else {
         selectedNames = new String[selected.length];

         for (int i = 0; i < selectedNames.length; i++) {
            selectedNames[i] = (String) selected[i];
         }

         // the following was commented out and replaced by the previous code
         // line. vered.
         outputFeatures =
            StaticMethods.getIntersectIds(selectedNames, colindices);

         if (outputFeatures.length < selectedNames.length) {
            throw new Exception(getAlias() +
                                ": Some of the configured output attributes were not found in the input table." +
                                " Please reconfigure this module via a GUI run so it can run Headless."); /*int[] outputFeatures = new int[selected.length];
                                                                                                          for (int i = 0; i < selected.length; i++) {
                                                                                                          String s = (String) selected[i];
                                                                                                          Integer ii = (Integer) colindices.get (s);
                                                                                                          outputFeatures[i] = ii.intValue ();
                                                                                                          }
                                                                                                           */
         }

      }

      // Create the example table and push it.
      ExampleTable et = table.toExampleTable();
      et.setInputFeatures(inputFeatures);
      et.setOutputFeatures(outputFeatures);
      this.pushOutput(et, 0);
   } // end method doit


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "The Table to choose inputs and outputs from.";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  i Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int i) {

      switch (i) {

         case 0:
            return "Table";

         default:
            return "No such input";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] types = {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String info = "<p>Overview: ";
      info +=
         "This module allows the user to choose which columns of a table are inputs and outputs.";
      info += "</p><p>Detailed Description: ";
      info +=
         "This module outputs an <i>Example Table</i> with the input and output features assigned. ";
      info +=
         "Inputs and outputs do not have to be selected, nor do they have to be mutually exclusive. ";
      info += "</p><p>Data Handling: ";
      info +=
         "This module does not modify the data in the table. It only sets the input and output features.";

      return info;
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Choose Attributes"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      switch (i) {

         case 0:
            return "The Example Table with input and output features assigned.";

         default:
            return "No such output";
      }
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {

      switch (i) {

         case 0:
            return "Example Table";

         default:
            return "No such output";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] types = {
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

      return types;
   }


   /**
    * Get the descriptions of the properties of this module.
    *
    * @return the PropertyDescriptor for each property of this module.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] desc = new PropertyDescription[1];
      desc[0] = this.supressDescription;

      return desc;
   }

   /**
    * Get the selected inputs.
    *
    * @return selected inputs
    */
   public Object[] getSelectedInputs() { return selectedInputs; }

   /**
    * Get the selected outputs.
    *
    * @return selected outputs
    */
   public Object[] getSelectedOutputs() { return selectedOutputs; }

   /**
    * Set the selected inputs.
    *
    * @param nsi new selected inputs
    */
   public void setSelectedInputs(Object[] nsi) { selectedInputs = nsi; }

   /**
    * Set the selected outputs.
    *
    * @param nsi new selected outputs
    */
   public void setSelectedOutputs(Object[] nsi) { selectedOutputs = nsi; }

   //~ Inner Classes ***********************************************************

   /**
    * The user view class.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   class AttributeView extends JUserPane implements ActionListener {

      /** abort button. */
      private JButton abort;

      /** done button. */
      private JButton done;

      /** example table with input and outputs set. */
      private ExampleTable et;

      /** label. */
      private JLabel inputLabel;

      /** list of possible inputs. */
      private JList inputList;

      /** Maps input name to column index. */
      private HashMap inputToIndexMap;

      /** menu bar. */
      private JMenuBar menuBar;

      /** alpha order. */
      private JCheckBoxMenuItem miAlphaOrder;

      /** natural ordering based on table. */
      private JCheckBoxMenuItem miColumnOrder;

      /** the module. */
      private ChooseAttributes module;

      /** label. */
      private JLabel outputLabel;

      /** list of possible outputs. */
      private JList outputList;

      /** Maps output name to column index. */
      private HashMap outputToIndexMap;

      /** original table. */
      private Table table;

      /**
       * Add all the components.
       */
      private void addComponents() {
         JPanel back = new JPanel();
         String[] labels = orderedLabels();

         if (table.getColumnLabel(0).equals("")) {
            miColumnOrder.setEnabled(false);
            miAlphaOrder.setEnabled(false);
         } else {
            miColumnOrder.setEnabled(true);
            miAlphaOrder.setEnabled(true);
         }

         inputList = new JList();

         // inputList.setModel(null);
         DefaultListModel dlm = new DefaultListModel();
         inputList.setModel(dlm);

         for (int i = 0; i < labels.length; i++) {
            dlm.addElement(labels[i]);
         }

         if (table instanceof ExampleTable) {
            int[] ins = ((ExampleTable) table).getInputFeatures();

            if (ins != null) {
               int[] sel = new int[ins.length];

               for (int i = 0; i < ins.length; i++) {
                  String s = table.getColumnLabel(ins[i]);
                  Integer ii = (Integer) inputToIndexMap.get(s);
                  sel[i] = ii.intValue();
               }

               inputList.setSelectedIndices(sel);
            }
         } else if (selectedInputs != null) {
            ArrayList indices = new ArrayList();

            for (int i = 0; i < selectedInputs.length; i++) {
               Integer ii = (Integer) inputToIndexMap.get(selectedInputs[i]);

               if (ii != null) {
                  indices.add(ii);
               }
            }

            int[] ints = new int[indices.size()];

            for (int i = 0; i < ints.length; i++) {
               ints[i] = ((Integer) indices.get(i)).intValue();
            }

            inputList.setSelectedIndices(ints);
         }

         // inputList.setModel(dlm);
         outputList = new JList(
                      /*labels*/);
         dlm = new DefaultListModel();
         outputList.setModel(dlm);

         for (int i = 0; i < labels.length; i++) {
            dlm.addElement(labels[i]);
         }

         if (table instanceof ExampleTable) {

            // outputList.setSelectedIndices(((ExampleTable)table).getOutputFeatures());
            int[] ins = ((ExampleTable) table).getOutputFeatures();

            if (ins != null) {
               int[] sel = new int[ins.length];

               for (int i = 0; i < ins.length; i++) {
                  String s = table.getColumnLabel(ins[i]);
                  Integer ii = (Integer) outputToIndexMap.get(s);
                  sel[i] = ii.intValue();
               }

               outputList.setSelectedIndices(sel);
            }
         } else if (selectedOutputs != null) {
            ArrayList indices = new ArrayList();

            for (int i = 0; i < selectedOutputs.length; i++) {
               Integer ii = (Integer) outputToIndexMap.get(selectedOutputs[i]);

               if (ii != null) {
                  indices.add(ii);
               }
            }

            int[] ints = new int[indices.size()];

            for (int i = 0; i < ints.length; i++) {
               ints[i] = ((Integer) indices.get(i)).intValue();
            }

            outputList.setSelectedIndices(ints);
         }

         // outputList.setModel(dlm);
         // outputList.setModel(null);
         JScrollPane leftScrollPane = new JScrollPane(inputList);
         JScrollPane rightScrollPane = new JScrollPane(outputList);
         inputLabel = new JLabel("Input Attributes");
         inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
         outputLabel = new JLabel("Output Attributes");
         outputLabel.setHorizontalAlignment(SwingConstants.CENTER);
         back.setLayout(new GridBagLayout());
         Constrain.setConstraints(back, inputLabel, 0, 0, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(back, outputLabel, 1, 0, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(back, leftScrollPane, 0, 1, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(back, rightScrollPane, 1, 1, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);

         JPanel buttons = new JPanel();
         buttons.add(abort);
         buttons.add(done);
         this.add(back, BorderLayout.CENTER);
         this.add(buttons, BorderLayout.SOUTH);
      } // end method addComponents

      /**
       * Get the column labels and fill the inputToIndexMap and
       * outputToIndexMap. Uses alphabetical ordering.
       *
       * @return String[] containing the labels of the columns
       */
      private final String[] alphabetizeLabels() {
         String[] labels = new String[table.getNumColumns()];
         inputToIndexMap = new HashMap(labels.length);
         outputToIndexMap = new HashMap(labels.length);

         for (int i = 0; i < labels.length; i++) {
            labels[i] = table.getColumnLabel(i);
            inputToIndexMap.put(labels[i], new Integer(i));
            outputToIndexMap.put(labels[i], new Integer(i));
         }

         Arrays.sort(labels, new StringComp());

         return labels;
      }

      /**
       * Get the column labels and fill the inputToIndexMap and
       * outputToIndexMap. Uses natural ordering.
       *
       * @return String[] containing the labels of the columns
       */
      private final String[] orderedLabels() {
         String[] labels = new String[table.getNumColumns()];
         inputToIndexMap = new HashMap(labels.length);
         outputToIndexMap = new HashMap(labels.length);

         for (int i = 0; i < labels.length; i++) {
            String label = table.getColumnLabel(i);

            if (label.equals("")) {
               label = new String("Column " + Integer.toString(i));
            }

            labels[i] = label;
            inputToIndexMap.put(labels[i], new Integer(i));
            outputToIndexMap.put(labels[i], new Integer(i));
         }

         return labels;
      }

      /**
       * Create an example table, and set the input and output features based on
       * the selections.
       */
      private void setFieldsInTable() {
         et = table.toExampleTable();

         Object[] selected = inputList.getSelectedValues();
         int[] inputFeatures = new int[selected.length];

         for (int i = 0; i < selected.length; i++) {
            String s = (String) selected[i];
            Integer ii = (Integer) inputToIndexMap.get(s);
            inputFeatures[i] = ii.intValue();
         }

         setSelectedInputs(selected);
         selected = outputList.getSelectedValues();

         int[] outputFeatures = new int[selected.length];

         for (int i = 0; i < selected.length; i++) {
            String s = (String) selected[i];
            Integer ii = (Integer) outputToIndexMap.get(s);
            outputFeatures[i] = ii.intValue();
         }

         setSelectedOutputs(selected);
         et.setInputFeatures(inputFeatures);
         et.setOutputFeatures(outputFeatures);
         table = null;
      } // end method setFieldsInTable

      /**
       * Not used Make sure all choices are valid. vered - added test that input
       * features and output featurs have no intersection
       *
       * @return not used Make sure all choices are valid. vered - added test
       *         that input features and output featurs have no intersection
       */
      protected boolean checkChoices() {

         if (outputList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this,
                                          "You must select at least one output",
                                          "Error", JOptionPane.ERROR_MESSAGE);

            return false;
         }

         if (inputList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this,
                                          "You must select at least one input",
                                          "Error", JOptionPane.ERROR_MESSAGE);

            return false;
         }

         // vered - added this code
         Object[] selected = inputList.getSelectedValues();
         HashMap ins = new HashMap();

         for (int i = 0; i < selected.length; i++) {
            ins.put(selected[i], new Integer(i));
         }

         selected = outputList.getSelectedValues();

         for (int i = 0; i < selected.length; i++) {

            if (ins.containsKey(selected[i])) {
               JOptionPane.showMessageDialog(this,
                                             "You cannot select an attribute both as an input and an output",
                                             "Error",
                                             JOptionPane.ERROR_MESSAGE);

               return false;
            } // if contains
         }

         return true;
      } // end method checkChoices

      /**
       * Listen for ActionEvents.
       *
       * @param e action event
       */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();

         if (src == abort) {
            module.viewCancel();
         } else if (src == done) {

            // if(checkChoices()){
            setFieldsInTable();
            pushOutput(et, 0);
            viewDone("Done");
            et = null;
            this.removeAll();
            // }
         } else if (src == miColumnOrder) {
            String[] labels = orderedLabels();
            miAlphaOrder.setState(false);

            DefaultListModel dlm = (DefaultListModel) inputList.getModel();
            dlm.removeAllElements();

            for (int i = 0; i < labels.length; i++) {
               dlm.addElement(labels[i]);
            }

            dlm = (DefaultListModel) outputList.getModel();
            dlm.removeAllElements();

            for (int i = 0; i < labels.length; i++) {
               dlm.addElement(labels[i]);
            }
         } else if (src == miAlphaOrder) {
            String[] labels = alphabetizeLabels();
            miColumnOrder.setState(false);

            DefaultListModel dlm = (DefaultListModel) inputList.getModel();
            dlm.removeAllElements();

            for (int i = 0; i < labels.length; i++) {
               dlm.addElement(labels[i]);
            }

            dlm = (DefaultListModel) outputList.getModel();
            dlm.removeAllElements();

            for (int i = 0; i < labels.length; i++) {
               dlm.addElement(labels[i]);
            }
         }
      } // end method actionPerformed


      /**
       * Supplies the default behavior for getMenu, which is to return null
       * indicating that no menu specific to this component is to be added.
       *
       * @return a menubar specific to this component.
       */
      public Object getMenu() { return menuBar; }

      /**
       * Make me at least this big.
       *
       * @return make me at least this big.
       */
      public Dimension getPreferredSize() { return new Dimension(400, 300); }


      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param v The module this view is associated with.
       */
      public void initView(ViewModule v) {
         module = (ChooseAttributes) v;
         abort = new JButton("Abort");
         done = new JButton("Done");
         abort.addActionListener(this);
         done.addActionListener(this);
         menuBar = new JMenuBar();

         JMenu m1 = new JMenu("File");
         miColumnOrder = new JCheckBoxMenuItem("Column Order");
         miColumnOrder.addActionListener(this);
         miColumnOrder.setState(true);
         miAlphaOrder = new JCheckBoxMenuItem("Alphabetical Order");
         miAlphaOrder.addActionListener(this);
         miAlphaOrder.setState(false);
         m1.add(miColumnOrder);
         m1.add(miAlphaOrder);
         menuBar.add(m1);
      }


      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param o  The object that has been input.
       * @param id The index of the module input that been received.
       */
      public void setInput(Object o, int id) {

         if (id == 0) {
            table = (Table) o;
            this.removeAll();
            addComponents();
         }
      }

      /**
       * Comparator that compares strings.
       *
       * @author  $Author$
       * @version $Revision$, $Date$
       */
      private final class StringComp implements Comparator {


         /**
          * Compare the lower-case values of two strings.
          * @param  o1 the first object to be compared.
          * @param  o2 the second object to be compared.
          *
          * @return a negative integer, zero, or a positive integer as the first
          *         argument is less than, equal to, or greater than the second.
          *
          * @throws ClassCastException if the arguments' types prevent them from
          *                            being compared by this Comparator.
          */
         public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;

            return s1.toLowerCase().compareTo(s2.toLowerCase());
         }


         /**
          * Compare this object to another for equality.
          * @param  o the reference object with which to compare.
          *
          * @return <code>true</code> if this object is the same as the obj
          *         argument; <code>false</code> otherwise.
          *
          * @see    #hashCode()
          * @see    java.util.Hashtable
          */
         public boolean equals(Object o) { return super.equals(o); }
      } // end class StringComp
   } // end class AttributeView
} // end class ChooseAttributes
