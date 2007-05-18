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

import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Transformation;
import ncsa.d2k.modules.core.datatype.table.basic.ColumnUtilities;
import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.d2k.userviews.swing.JUserPane;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import ncsa.d2k.modules.core.util.*;


/**
 * This module presents a user interface for interactive normalization of <code>
 * MutableTable</code> data. Selected numeric columns are normalized
 * (<i>standardized</i>) to a set of values such that the mean of that set is
 * approximately zero and the standard deviation of that set is approximately
 * one.
 *
 * <p>All transformed columns are converted to type <code>double</code>.</p>
 *
 * @author  gpape
 * @version $Revision$, $Date$
 */
public class Normalize extends HeadlessUIModule {

   //~ Instance fields *********************************************************

   /** lables of the attributes to normalize */
   private String[] numericLabels;
   private D2KModuleLogger myLogger;
   
   public void beginExecution() {
	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   }

   //~ Methods *****************************************************************

   /******************************************************************************/
/* UIModule methods                                                           */
   /******************************************************************************/

    /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   public UserView createUserView() { return new NormalizeUI(); }


    /**
    * headless conversion support. If the table is in the same format as the
    * last successful execution, push out the same selected attributes in the
     * transform object.
    *
    * @throws Exception when something goes wrong.
    */
    public void doit() throws Exception {
        MutableTable _table = (MutableTable) pullInput(0);

        int[] transform = new int[0]; // with this array the normalization
        // trasform will be build

        if (numericLabels == null) {
            throw new Exception(this.getAlias() +
                    " has not been configured. Before running headless, run with the gui and configure the parameters.");
        }


        HashMap availableNumericColumns = new HashMap();

        for (int i = 0; i < _table.getNumColumns(); i++) {

            if (_table.isColumnNumeric(i)) {
                availableNumericColumns.put(_table.getColumnLabel(i),
                        new Integer(i));
            }
        }

        if (availableNumericColumns.size() == 0) {
        	myLogger.warn(getAlias() + ": Warning - Table " +
                    _table.getLabel() +
                    " has no numeric columns. The transformation will be " +
                    "an empty one");
            // pushOutput(new NormalizingTransformation(transform), 0); return;
        }


        if (numericLabels.length == 0) {
        	myLogger.warn(getAlias() +
                    ": no numeric columns were selected. " +
                    "the transformation will be an empty one.\n");
            pushOutput(new NormalizingTransformation(transform), 0);

            return;
        }

        // finding out how many columns are in the intersection between
        // numericLabels and the available numeric columns in the table


        transform =
                StaticMethods.getIntersectIds(numericLabels, availableNumericColumns);
/*        int numNumeric = 0;
 *      for (int i = 0; i < numericLabels.length; i++)       if
 * (availableNumericColumns.containsKey(numericLabels[i]))         numNumeric++;
 */

        if (transform.length < numericLabels.length) {
            String str;
            String label;
            label = _table.getLabel();

            if (label == null || label.length() == 0) {
                str = "The input table";
            } else {
                str = "Table " + label;
            }

            throw new Exception(getAlias() + ": " + str +
                    " does not contain all of the configured numeric columns." +
                    " Please reconfigure this module via a GUI run so it can run Headless.");
            // pushOutput(new NormalizingTransformation(transform), 0);
            // return;

        }

        /*
        * transform = new int[numNumeric]; for (int i = 0; i <
        * numericLabels.length; i++) if
        * (availableNumericColumns.containsKey(numericLabels[i])) transform[i] =
        * ( (Integer) availableNumericColumns.get(numericLabels[
        * i])).intValue();
        */
        pushOutput(new NormalizingTransformation(transform), 0);


    } // end method doit
// headless conversion support

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
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      if (index == 0) {
         return "A <i>MutableTable</i> with columns to be normalized.";
      }

      return null;
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

      if (index == 0) {
         return "Mutable Table";
      }

      return null;
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
       return new String[] {
               "ncsa.d2k.modules.core.datatype.table.MutableTable"
       };
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module presents a simple user interface for the ");
      sb.append("interactive normalization (<i>standardization</i>) of ");
      sb.append("numeric <i>MutableTable</i> data. Numeric columns selected ");
      sb.append("by the user are normalized to a set of values such that the ");
      sb.append("mean of that set is approximately zero and the standard ");
      sb.append("deviation of that set is approximately one.</P>");

      sb.append("</p><p>Missing Values Handling: Missing values are preserved by " +
                "the output Transformation of this module. Missing values are ignored " +
                "and not being considered during normalization. ");


      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify its input data. Rather, its ");
      sb.append("output is a <i>Transformation</i> that can later be used to ");
      sb.append("normalize the specified columns. All transformed columns ");
      sb.append("will be converted to type <i>double</i>.");
      sb.append("</p>");

      return sb.toString();
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Normalize Values"; }

   /**
    * Get lables of the attributes to normalize
    *
    * @return labels of attributes to normalize
    */
   public Object[] getNumericLabels() { return numericLabels; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      if (index == 0) {
         return "A normalizing transformation for the specified columns.";
      }

      return null;
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

      if (index == 0) {
         return "Transformation";
      }

      return null;
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() { return new String[] {
                                                "ncsa.d2k.modules.core.datatype.table.Transformation"
                                             }; }

   /**
    * Set lables of the attributes to normalize
    *
    * @param labels lables of the attributes to normalize
    */
   public void setNumericLabels(Object[] labels) {
      numericLabels = new String[labels.length];

      for (int i = 0; i < labels.length; i++) {
         numericLabels[i] = (String) labels[i];

      }
   }

   //~ Inner Classes ***********************************************************

   /******************************************************************************/
/* GUI                                                                        */
   /******************************************************************************/

   /**
    * View
    */
   private class NormalizeUI extends JUserPane implements ActionListener {
       /** abort button */
      private JButton abortButton;
       /** done button */
      private JButton doneButton;

       /** empty insets */
      private Insets emptyInsets = new Insets(0, 0, 0, 0);
       /** insets to use around labels */
      private Insets labelInsets = new Insets(10, 10, 10, 10);
       /** insets to use around buttons */
      private Insets buttonInsets = new Insets(5, 5, 5, 5);

       /** */
      private int[] indirection; // points into table, at numeric columns
       /** numeric labels */
      private String[] numericLabels;

       /** list of available numeric columns */
      private JList numericList;
       /** list model for numericList */
      private DefaultListModel numericModel;

       /** the table */
      private MutableTable table;

       /**
        * Invoked when an action occurs.
        */
       public void actionPerformed(ActionEvent event) {

           Object src = event.getSource();

           if (src == abortButton) {
               viewCancel();
           } else if (src == doneButton) {

               // headless conversion support
               setNumericLabels(numericList.getSelectedValues());
               // headless conversion support


               if (indirection.length == 0) {
                   pushOutput(new NormalizingTransformation(new int[0]), 0);
               } else {

                   int[] indices = numericList.getSelectedIndices();
                   int[] transform = new int[indices.length];

                   for (int i = 0; i < indices.length; i++) {
                       transform[i] = indirection[indices[i]];
                   }

                   pushOutput(new NormalizingTransformation(transform), 0);

               }

               viewDone("Done");

           }

       } // end method actionPerformed

       /**
        * Called by the D2K Infrastructure to allow the view to perform initialization tasks.
        *
        * @param mod The module this view is associated with.
        */
       public void initView(ViewModule mod) {
       }

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

           numericModel = new DefaultListModel();

           for (int i = 0; i < numericLabels.length; i++) {
               numericModel.addElement(numericLabels[i]);
           }

           numericList = new JList(numericModel);

           String[] savedLabels = Normalize.this.numericLabels;

           if (savedLabels != null) {

               ArrayList indices = new ArrayList();

               int ndx;

               for (int i = 0; i < savedLabels.length; i++) {
                   ndx = numericModel.indexOf(savedLabels[i]);

                   if (ndx != -1) {
                       indices.add(new Integer(ndx));
                   }
               }

               int[] ints = new int[indices.size()];

               for (int i = 0; i < ints.length; i++) {
                   ints[i] = ((Integer) indices.get(i)).intValue();
               }

               numericList.setSelectedIndices(ints);

           }

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
           // attach the JList.

           GridBagLayout layout = new GridBagLayout();
           setLayout(layout);

           if (numNumericColumns == 0) {

               JLabel noNumericLabel =
                       new JLabel("This table does not have any numeric columns to normalize.");

               layout.addLayoutComponent(noNumericLabel,
                       new GridBagConstraints(0, 0, 1, 1, 1.0,
                               1.0,
                               GridBagConstraints.CENTER,
                               GridBagConstraints.BOTH,
                               labelInsets, 0,
                               0));
               add(noNumericLabel);

           } else {

               JScrollPane numericScroll = new JScrollPane(numericList);

               layout.addLayoutComponent(numericScroll,
                       new GridBagConstraints(0, 0, 1, 1, 1.0,
                               1.0,
                               GridBagConstraints.CENTER,
                               GridBagConstraints.BOTH,
                               emptyInsets, 0,
                               0));
               add(numericScroll);

           }

           layout.addLayoutComponent(buttonPanel,
                   new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                           GridBagConstraints.SOUTH,
                           GridBagConstraints.HORIZONTAL,
                           emptyInsets, 0, 0));
           add(buttonPanel);

       } // end method setInput

   } // end class NormalizeUI

   /******************************************************************************/
/* Transformation                                                             */
   /******************************************************************************/

   /**
    * Transformation that implements normalization
    */
   private class NormalizingTransformation implements Transformation {

       /** numeric column indices in the table */
      private int[] indices;

       /**
        * Constructor
        * @param indices the indices of columns to normalize
        */
      NormalizingTransformation(int[] indices) {
          this.indices = indices;
      }

       /**
        * Normalize the selected columns.
        * @param table the table
        * @return true if successful
        */
      public boolean transform(MutableTable table) {

         if (indices.length == 0 || table.getNumRows() == 0) {

            // no transformation is necessary
            return true;
         }

         // loop over all relevant numeric column indices in the table
         for (int count = 0; count < indices.length; count++) {

            double[] data = new double[table.getNumRows()];
            boolean[] missing = new boolean[data.length];

            int index = indices[count];

            // data first represents the data from the table:
            for (int j = 0; j < data.length; j++) {
               data[j] = table.getDouble(j, index);
               missing[j] = table.isValueMissing(j, index);
            }

            // calculate mean
            double mean = 0;
            int totalNotMissing = 0;

            for (int j = 0; j < data.length; j++) {

               if (!missing[j]) {
                  totalNotMissing++;
                  mean += data[j];
               }
            }

            mean /= totalNotMissing;

            // QA output - vered.
            // System.out.println("the mean of this datast is: " + mean);

            // data now represents differences from the mean:
            for (int j = 0; j < data.length; j++) {

               if (!missing[j]) {
                  data[j] = data[j] - mean;
               }
            }

            // calculate sum of squares of differences
            double sq_diff_sum = 0;

            for (int j = 0; j < data.length; j++) {

               if (!missing[j]) {
                  sq_diff_sum += (data[j] * data[j]);
               }
            }

            // calculate sample variance
            double sample_variance = 0;

            if (totalNotMissing == 1) {
               sample_variance = sq_diff_sum;
            } else {
               sample_variance = sq_diff_sum / (totalNotMissing - 1);
            }

            // calculate sample standard deviation
            double sample_std_dev = Math.sqrt(sample_variance);

            //System.out.println("the standard deviation of this datast is: " +
            //                   sample_std_dev);

            // divide to normalize data:
            if (sample_std_dev == 0.0) {

               for (int j = 0; j < data.length; j++) {

                  if (!missing[j]) {
                     data[j] = 0;
                  }
               }
            } else {

               for (int j = 0; j < data.length; j++) {

                  if (!missing[j]) {
                     data[j] = data[j] / sample_std_dev;
                  }
               }
            }

            Column newColumn =
               ColumnUtilities.toDoubleColumn(table.getColumn(index));
            table.setColumn(newColumn, index);

            for (int entry = 0; entry < data.length; entry++) {

               if (table.isValueMissing(entry, index)) {
                  table.setDouble(table.getMissingDouble(), entry, index);
               } else {
                  table.setDouble(data[entry], entry, index);
               }
            }
         } // end for

         return true;

      } // end method transform

   } // end class NormalizingTransformation


} // Normalize