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
package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.core.gui.ErrorDialog;
import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Transformation;
import ncsa.d2k.modules.core.datatype.table.basic.ColumnUtilities;
import ncsa.d2k.userviews.swing.JUserPane;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


/**
 * This module presents a user interface for the interactive scaling of <code>
 * MutableTable</code> data. Selected columns are scaled to a user-specified
 * range (the default range is 0 to 1).
 *
 * <p>All transformed columns are converted to type <code>double</code>.</p>
 *
 * @author  gpape
 * @version $Revision$, $Date$
 */
public class Scale extends HeadlessUIModule {

   //~ Instance fields *********************************************************

   /** max to scale from. */
   protected double[] from_max;

   /** min to scale from. */
   protected double[] from_min;

   /** indices of the columns to scale. */
   protected int[] indices;

   /** max to scale to. */
   protected double[] to_max;

   /** min to scale to. */
   protected double[] to_min;

   //~ Methods *****************************************************************

   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   public UserView createUserView() { return new ScaleUI(); }

   /**
    * headless conversion support. If the table is in the same format as the
    * last successful execution, push out the same selected attributes.
    *
    * @throws Exception when something goes wrong.
    */
   public void doit() throws Exception {
      MutableTable table = (MutableTable) this.pullInput(0);

      if (
          indices == null ||
             from_min == null ||
             from_max == null ||
             to_min == null ||
             to_max == null) {
         throw new Exception(this.getAlias() +
                             " has not been configured. Before running headless, run with the gui and configure the parameters.");
      }

      int nc = table.getNumColumns();

      if (nc < indices.length) {
         throw new Exception(this.getAlias() +
                             " has not been configured to run headless with a table of this structure. Not enough columns in the table");
      }

      // Find the maximum column index.
      int max = -1;

      for (int i = 0; i < indices.length; i++) {

         if (indices[i] > max) {
            max = indices[i];
         }
      }

      if (nc < max) {
         throw new Exception(this.getAlias() +
                             " has not been configured to run headless with a table of this structure. Not enough columns in the table");
      }

      for (int i = 0; i < indices.length; i++) {

         if (indices[i] >= 0) {
            Column col = table.getColumn(indices[i]);

            if (!col.getIsScalar()) {
               throw new Exception(this.getAlias() +
                                   " has not been configured to run headless with a table of this structure. Column " +
                                   col.getLabel() + " is not scalar.");
            }
         }
      }

      pushOutput(new ScalingTransformation(indices, from_min, from_max,
                                           to_min, to_max), 0);
   } // end method doit

   /**
    * The list of strings returned by this method allows the module to map the
    * results returned from the pier to the position dependent outputs. The
    * order in which the names appear in the string array is the order in which
    * to assign them to the outputs.
    *
    * @return a string array containing the names associated with the outputs in
    *         the order the results should appear in the outputs.
    */
   public String[] getFieldNameMapping() { return null; }

   /**
    * Get from_max.
    *
    * @return from_max
    */
   public double[] getFromMax() { return this.from_max; }

   /**
    * Get from_min.
    *
    * @return from_min
    */
   public double[] getFromMin() { return this.from_min; }

   /**
    * Get indices.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "A <i>MutableTable</i> with columns to be scaled.";

         default:
            return "No such input";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Mutable Table";

         default:
            return "NO SUCH INPUT!";
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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>      Overview: This module presents a simple user interface for the       interactive scaling" +
             " of numeric <i>MutableTable</i> data. Numeric columns       selected by the user can be scaled" +
             " to a specified range (the default       range is 0 to 1).    </p>    <p>      Detailed Description:" +
             " A user interface is displayed that has an entry       for the current range of data values" +
             " for each attribute in the table       passed in. The user may change the current range as well" +
             " as the range to       scale the data to. The scaling is done as if the data is within the " +
             "      current range values specified, values which are out of this range are       pinned. If" +
             " the gui is suppressed, this modules will use the last entries       the user made to apply" +
             " to the input table, however, the current range is       always gotten from the table, the previous" +
             " selection made via the gui is       ignored. If the columns the user has previously selected" +
             " did not exist       or if they are no longer scalar, it will fail.    </p>  " +

      "</p><p>Missing Values Handling: Missing values are preserved by " +
             "the output Transformation of this module. Missing values are left as they are " +
             "and are not being scaled or considered during scaling. " +

      "    <p>      Data Handling: This module does not modify" +
             " its input data. Rather, its       output is a <i>Transformation</i> that can later be used" +
             " to scale the       specified columns. All transformed columns will be converted to type <i>" +
             "      double</i>.    </p>";
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Scale Values"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "A scaling transformation for the specified columns.";

         default:
            return "No such output";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Transformation";

         default:
            return "NO SUCH OUTPUT!";
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
      String[] types =
      { "ncsa.d2k.modules.core.datatype.table.Transformation" };

      return types;
   }

   /**
    * Get to_max.
    *
    * @return to_max
    */
   public double[] getToMax() { return this.to_max; }

   /**
    * Get to_min.
    *
    * @return to_min
    */
   public double[] getToMin() { return this.to_min; }

   /**
    * Set from_max.
    *
    * @param ni new from_max
    */
   public void setFromMax(double[] ni) { this.from_max = ni; }

   /**
    * Set from_min.
    *
    * @param ni new from_min
    */
   public void setFromMin(double[] ni) { this.from_min = ni; }

   /**
    * Set indices.
    *
    * @param ni new indices
    */
   public void setIndices(int[] ni) { this.indices = ni; }

   /**
    * Set to_max.
    *
    * @param ni new to_max
    */
   public void setToMax(double[] ni) { this.to_max = ni; }

   /**
    * Set to_min.
    *
    * @param ni new to_min
    */
   public void setToMin(double[] ni) { this.to_min = ni; }

   //~ Inner Classes ***********************************************************

   /**
    * View.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class ScaleUI extends JUserPane implements ActionListener {

      /** abort button. */
      private JButton abortButton;

      /** insets around buttons. */
      private Insets buttonInsets = new Insets(5, 5, 5, 5);

      /** done button. */
      private JButton doneButton;

      /** empty insets. */
      private Insets emptyInsets = new Insets(0, 0, 0, 0);

      /** points into table, at numeric columns. */
      private int[] indirection;

      /** insets around labels. */
      private Insets labelInsets = new Insets(10, 10, 10, 10);

      /** the labels of the numeric cols. */
      private String[] numericLabels;

      /** map column indices to column panels. */
      private HashMap panelMap;

      /** preferred size. */
      private Dimension preferredSize = new Dimension(600, 300);

      /** table. */
      private MutableTable table;

      /**
       * Invoked when an action occurs.
       *
       * @param event action event
       */
      public void actionPerformed(ActionEvent event) {

         Object src = event.getSource();

         if (src == abortButton) {
            viewCancel();
         } else if (src == doneButton) {

            int numRelevantColumns = indirection.length;

            indices = new int[numRelevantColumns];
            from_min = new double[numRelevantColumns];
            from_max = new double[numRelevantColumns];
            to_min = new double[numRelevantColumns];
            to_max = new double[numRelevantColumns];

            for (int count = 0; count < indirection.length; count++) {

               int index = indirection[count];

               ColumnPanel columnPanel =
                  (ColumnPanel) panelMap.get(new Integer(index));

               if (columnPanel.scaleCheck.isSelected()) {

                  indices[count] = index;

                  double d;

                  try {
                     d = Double.parseDouble(columnPanel.fromMinField.getText());
                  } catch (NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                                                   "not a number: " +
                                                   columnPanel.fromMinField
                                                      .getText(),
                                                   "Error",
                                                   JOptionPane.ERROR_MESSAGE);

                     return;
                  }

                  from_min[count] = d;

                  try {
                     d = Double.parseDouble(columnPanel.fromMaxField.getText());
                  } catch (NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                                                   "not a number: " +
                                                   columnPanel.fromMaxField
                                                      .getText(),
                                                   "Error",
                                                   JOptionPane.ERROR_MESSAGE);

                     return;
                  }

                  from_max[count] = d;

                  try {
                     d = Double.parseDouble(columnPanel.toMinField.getText());
                  } catch (NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                                                   "not a number: " +
                                                   columnPanel.toMinField
                                                      .getText(),
                                                   "Error",
                                                   JOptionPane.ERROR_MESSAGE);

                     return;
                  }

                  to_min[count] = d;

                  try {
                     d = Double.parseDouble(columnPanel.toMaxField.getText());
                  } catch (NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                                                   "not a number: " +
                                                   columnPanel.toMaxField
                                                      .getText(),
                                                   "Error",
                                                   JOptionPane.ERROR_MESSAGE);

                     return;
                  }

                  to_max[count] = d;

               } else {

                  indices[count] = -1;

               }

            } // end for

            // Check the mins are less than the maxes.
            for (int i = 0; i < from_max.length; i++) {

               if (from_max[i] < from_min[i]) {
                  ColumnPanel columnPanel =
                     (ColumnPanel) panelMap.get(new Integer(i));
                  ErrorDialog.showDialog(this,
                                         columnPanel.lbl +
                                         ": from maximum can not be less than the min.",
                                         "Max < Min");

                  return;
               }

               if (to_max[i] < to_min[i]) {
                  ColumnPanel columnPanel =
                     (ColumnPanel) panelMap.get(new Integer(i));
                  ErrorDialog.showDialog(this,
                                         columnPanel.lbl +
                                         ": to maximum can not be less than the min.",
                                         "Max < Min");

                  return;
               }
            }

            pushOutput(new ScalingTransformation(indices, from_min, from_max,
                                                 to_min, to_max), 0);
            viewDone("Done");

         } // end if-else

      } // end method actionPerformed

      /**
       * If the minimum size has been set to a non-<code>null</code> value just
       * returns it. If the UI delegate's <code>getMinimumSize</code> method
       * returns a non-<code>null</code> value then return that; otherwise defer
       * to the component's layout manager.
       *
       * @return the value of the <code>minimumSize</code> property
       *
       * @see    #setMinimumSize
       * @see    javax.swing.plaf.ComponentUI
       */
      public Dimension getMinimumSize() { return preferredSize; }

      /**
       * If the <code>preferredSize</code> has been set to a
       * non-<code>null</code> value just returns it. If the UI delegate's
       * <code>getPreferredSize</code> method returns a non <code>null</code>
       * value then return that; otherwise defer to the component's layout
       * manager.
       *
       * @return the value of the <code>preferredSize</code> property
       *
       * @see    #setPreferredSize
       * @see    javax.swing.plaf.ComponentUI
       */
      public Dimension getPreferredSize() { return preferredSize; }

      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param mod The module this view is associated with.
       */
      public void initView(ViewModule mod) { }

      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param obj The object that has been input.
       * @param ind The index of the module input that been received.
       */
      public void setInput(Object obj, int ind) {

         if (ind != 0) {
            return;
         }

         table = (MutableTable) obj;
         removeAll();

         // how many numeric columns are there?

         int numNumericColumns = 0;
         int columnType;

         for (int i = 0; i < table.getNumColumns(); i++) {

            columnType = table.getColumnType(i);

            if (
                columnType == ColumnTypes.BYTE ||
                   columnType == ColumnTypes.DOUBLE ||
                   columnType == ColumnTypes.FLOAT ||
                   columnType == ColumnTypes.INTEGER ||
                   columnType == ColumnTypes.LONG ||
                   columnType == ColumnTypes.SHORT) {

               numNumericColumns++;

            }

         }

         // construct list of available numeric columns

         indirection = new int[numNumericColumns];
         numericLabels = new String[numNumericColumns];

         int index = 0;

         for (int i = 0; i < table.getNumColumns(); i++) {

            columnType = table.getColumnType(i);

            if (
                columnType == ColumnTypes.BYTE ||
                   columnType == ColumnTypes.DOUBLE ||
                   columnType == ColumnTypes.FLOAT ||
                   columnType == ColumnTypes.INTEGER ||
                   columnType == ColumnTypes.LONG ||
                   columnType == ColumnTypes.SHORT) {

               indirection[index] = i;

               numericLabels[index] = table.getColumnLabel(i);

               if (
                   numericLabels[index] == null ||
                      numericLabels[index].length() == 0) {

                  numericLabels[index] = "column " + i;

               }

               index++;

            }

         } // end for

         // set up button panel

         abortButton = new JButton("Abort");
         abortButton.addActionListener(this);
         doneButton = new JButton("Done");
         doneButton.addActionListener(this);

         JLabel buttonFillerLabel = new JLabel(" ");

         JPanel buttonPanel = new JPanel();
         GridBagLayout buttonLayout = new GridBagLayout();
         buttonPanel.setLayout(buttonLayout);

         buttonLayout.addLayoutComponent(abortButton,
                                         new GridBagConstraints(0, 0, 1, 1, 0.0,
                                                                0.0,
                                                                GridBagConstraints.WEST,
                                                                GridBagConstraints.NONE,
                                                                buttonInsets, 0,
                                                                0));
         buttonPanel.add(abortButton);

         buttonLayout.addLayoutComponent(buttonFillerLabel,
                                         new GridBagConstraints(1, 0, 1, 1, 1.0,
                                                                0.0,
                                                                GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                emptyInsets, 0,
                                                                0));
         buttonPanel.add(buttonFillerLabel);

         buttonLayout.addLayoutComponent(doneButton,
                                         new GridBagConstraints(2, 0, 1, 1, 0.0,
                                                                0.0,
                                                                GridBagConstraints.EAST,
                                                                GridBagConstraints.NONE,
                                                                buttonInsets, 0,
                                                                0));
         buttonPanel.add(doneButton);

         // if no numeric columns, attach a message to that effect. otherwise,
         // attach numeric column panels.

         GridBagLayout layout = new GridBagLayout();
         setLayout(layout);

         if (numNumericColumns == 0) {

            JLabel noNumericLabel =
               new JLabel("This table does not have any numeric columns to scale.");

            layout.addLayoutComponent(noNumericLabel,
                                      new GridBagConstraints(0, 0, 2, 1, 1.0,
                                                             1.0,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH,
                                                             labelInsets, 0,
                                                             0));
            add(noNumericLabel);

         } else {

            Box numericColumnsBox = new Box(BoxLayout.Y_AXIS);

            panelMap = new HashMap();

            // add fields for scaling

            for (int count = 0; count < indirection.length; count++) {

               index = indirection[count];

               // find min and max for this column

               double min = Double.POSITIVE_INFINITY;
               double max = Double.NEGATIVE_INFINITY;
               double d;


               for (int j = 0; j < table.getNumRows(); j++) {

                  if (!table.isValueMissing(j, index)) {
                     d = table.getDouble(j, index);

                     if (d < min) {
                        min = d;
                     }

                     if (d > max) {
                        max = d;
                     }
                  }
               }

               // add column panel

               ColumnPanel columnPanel =
                  new ColumnPanel(numericLabels[count],
                                  min, max, (count < indirection.length - 1));

               panelMap.put(new Integer(index), columnPanel);

               numericColumnsBox.add(columnPanel);

            } // end for

            JScrollPane numericScroll = new JScrollPane(numericColumnsBox);

            layout.addLayoutComponent(numericScroll,
                                      new GridBagConstraints(0, 0, 1, 1, 1.0,
                                                             1.0,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH,
                                                             emptyInsets, 0,
                                                             0));
            add(numericScroll);

         } // end if

         layout.addLayoutComponent(buttonPanel,
                                   new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0,
                                                          GridBagConstraints.SOUTH,
                                                          GridBagConstraints.HORIZONTAL,
                                                          emptyInsets, 0, 0));
         add(buttonPanel);

      } // end method setInput

      /******************************************************************************/
/* numeric column information panel                                           */
      /******************************************************************************/
      private class ColumnPanel extends JPanel implements ActionListener {

         /** from label. */
         JLabel fromLabel;

         /** text field for from max. */
         JTextField fromMaxField;

         /** from max label. */
         JLabel fromMaxLabel;

         /** text field for from min. */
         JTextField fromMinField;

         /** from min label. */
         JLabel fromMinLabel;

         /** label. */
         String lbl;

         /** checkbox. */
         JCheckBox scaleCheck;

         /** to label. */
         JLabel toLabel;

         /** text field for to max. */
         JTextField toMaxField;

         /** to max label. */
         JLabel toMaxLabel;

         /** text field for to min. */
         JTextField toMinField;

         /** to min label. */
         JLabel toMinLabel;

         /**
          * Constructor.
          *
          * @param label        label of column
          * @param fromMin      from min value
          * @param fromMax      from max value
          * @param addSeparator true if separator should be added
          */
         public ColumnPanel(String label, double fromMin, double fromMax,
                            boolean addSeparator) {

            super();

            lbl = label;
            scaleCheck = new JCheckBox();
            scaleCheck.setSelected(true);
            scaleCheck.addActionListener(this);

            fromMinField = new JTextField(6);
            fromMinField.setText(Double.toString(fromMin));
            fromMaxField = new JTextField(6);
            fromMaxField.setText(Double.toString(fromMax));
            toMinField = new JTextField(6);
            toMinField.setText("0.0");
            toMaxField = new JTextField(6);
            toMaxField.setText("1.0");

            fromLabel = new JLabel("  From:  ");
            fromMinLabel = new JLabel("min");
            fromMaxLabel = new JLabel("max");
            toLabel = new JLabel("  To:  ");
            toMinLabel = new JLabel("min");
            toMaxLabel = new JLabel("max");

            JPanel controlsPanel = new JPanel();
            controlsPanel.add(scaleCheck);
            controlsPanel.add(fromLabel);
            controlsPanel.add(fromMinLabel);
            controlsPanel.add(fromMinField);
            controlsPanel.add(fromMaxLabel);
            controlsPanel.add(fromMaxField);
            controlsPanel.add(toLabel);
            controlsPanel.add(toMinLabel);
            controlsPanel.add(toMinField);
            controlsPanel.add(toMaxLabel);
            controlsPanel.add(toMaxField);

            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);

            JLabel labelLabel = new JLabel(label); // *grin*

            layout.addLayoutComponent(labelLabel,
                                      new GridBagConstraints(0, 0, 1, 1, 1.0,
                                                             0.0,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL,
                                                             labelInsets, 0,
                                                             0));
            add(labelLabel);

            layout.addLayoutComponent(controlsPanel,
                                      new GridBagConstraints(1, 0, 1, 1, 0.0,
                                                             0.0,
                                                             GridBagConstraints.EAST,
                                                             GridBagConstraints.NONE,
                                                             emptyInsets, 0,
                                                             0));
            add(controlsPanel);

            if (addSeparator) {

               JSeparator separator = new JSeparator();

               layout.addLayoutComponent(separator,
                                         new GridBagConstraints(0, 1, 2, 1, 1.0,
                                                                0.0,
                                                                GridBagConstraints.SOUTH,
                                                                GridBagConstraints.HORIZONTAL,
                                                                emptyInsets, 0,
                                                                0));
               add(separator);

            }

         }

         /**
          * Invoked when an action occurs.
          *
          * @param event action event
          */
         public void actionPerformed(ActionEvent event) {

            boolean selected = scaleCheck.isSelected();

            fromMinField.setEnabled(selected);
            fromMaxField.setEnabled(selected);
            toMinField.setEnabled(selected);
            toMaxField.setEnabled(selected);

            fromLabel.setEnabled(selected);
            fromMinLabel.setEnabled(selected);
            fromMaxLabel.setEnabled(selected);
            toLabel.setEnabled(selected);
            toMinLabel.setEnabled(selected);
            toMaxLabel.setEnabled(selected);

         }

      } // end class ColumnPanel

   } // end class ScaleUI
} // end class Scale

/******************************************************************************/
/* Transformation                                                             */
/******************************************************************************/

class ScalingTransformation implements Transformation {

   //~ Instance fields *********************************************************

   /** max to scale from. */
   private double[] from_max;

   /** min to scale from. */
   private double[] from_min;

   /** indices of the columns to scale. */
   private int[] indices;

   /** max to scale to. */
   private double[] to_max;

   /** min to scale to. */
   private double[] to_min;


   //~ Constructors ************************************************************

   /**
    * Creates a new ScalingTransformation object.
    *
    * @param indices  indices of the columns to scale
    * @param from_min min to scale from
    * @param from_max  max to scale from
    * @param to_min   min to scale to.
    * @param to_max   max to scale to
    */
   ScalingTransformation(int[] indices, double[] from_min, double[] from_max,
                         double[] to_min, double[] to_max) {

      this.indices = indices;
      this.from_min = from_min;
      this.from_max = from_max;
      this.to_min = to_min;
      this.to_max = to_max;

   }

   //~ Methods *****************************************************************

   /**
    * Transform the table.  Scale the appropriate columns.
    *
    * @param  table table to transform
    *
    * @return true if successful
    */
   public boolean transform(MutableTable table) {

      if (indices.length == 0 || table.getNumRows() == 0) {

         // no transformation is necessary
         return true;
      }

      for (int count = 0; count < indices.length; count++) {

         int index = indices[count];

         if (index < 0) { // this column wasn't selected for scaling
            continue;
         }

         double[] data = new double[table.getNumRows()];
         boolean[] missing = new boolean[table.getNumRows()];
         double from_range = from_max[count] - from_min[count];
         double to_range = to_max[count] - to_min[count];

         double d;

         if (from_range == 0) { // no variance in data...

            d = table.getDouble(0, index);

            if (d >= to_min[count] && d <= to_max[count]) {

               // data is in new range; leave it alone
               for (int j = 0; j < data.length; j++) {
                  data[j] = table.getDouble(j, index);
                  missing[j] = table.isValueMissing(j, index);
               }
            } else {

               // data is out of new range; set to min
               for (int j = 0; j < data.length; j++) {
                  missing[j] = table.isValueMissing(j, index);

                  if (missing[j]) {
                     data[j] = table.getDouble(j, index);
                  } else {
                     data[j] = to_min[count];
                  }
               }
            }
         } else { // ordinary data; scale away!

            for (int j = 0; j < data.length; j++) {

               if (table.isValueMissing(j, index)) {
                  data[j] = table.getDouble(j, index);
                  missing[j] = true;
               } else {
                  d = table.getDouble(j, index);
                  data[j] =
                     (d - from_min[count]) * to_range / from_range +
                     to_min[count];
                  missing[j] = false;
               }
            }
         }

         // Create the new column, set the column in the table, then
         // set the new scaled values. In this way, we preserve the subset.
         Column col = null;

         if (table.getColumnType(index) == ColumnTypes.DOUBLE) {
            col = ColumnUtilities.toDoubleColumn(table.getColumn(index));
         } else {
            col = ColumnUtilities.toFloatColumn(table.getColumn(index));
         }

         table.setColumn(col, index);

         for (int i = 0; i < data.length; i++) {

            if (missing[i]) {
               table.setDouble(table.getMissingDouble(), i, index);
            } else {
               table.setDouble(data[i], i, index);
            }
         }
      } // end for

      return true;
   } // end method transform
} // end class ScalingTransformation
