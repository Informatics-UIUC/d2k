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
package ncsa.d2k.modules.core.transform.binning;

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.transformations.BinTransform;
import ncsa.gui.Constrain;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.text.NumberFormat;


/**
 * Automatically discretize scalar data for the Naive Bayesian classification
 * model. This module requires a ParameterPoint to determine the method of
 * binning to be used.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class AutoBin extends AutoBinOPT {

   //~ Static fields/initializers **********************************************

   /** constant for binning by weight. */
   static public final int WEIGHT = 1;

   /** constant for uniform binning. */
   static public final int UNIFORM = 0;

   //~ Instance fields *********************************************************

   /**
    * The method to use for discretization. Select 1 to create bins by weight.
    * This will create bins with an equal number of items in each slot. Select 0
    * to do uniform discretization by specifying the number of bins. This will
    * result in equally spaced bins between the minimum and maximum for each
    * scalar column.
    */
   private int binMethod;

   /**
    * When binning by weight, this is the number of items that will go in each
    * bin. However, the bins may contain more or fewer values than weight
    * values, depending on how many items equal the bin limits. Typically the
    * last bin will contain less or equal to weight values and the rest of the
    * bins will contain a number that is equal or greater to weight values.
    */
   private int binWeight = 1;

   /**
    * Define the number of bins absolutely. This will give equally spaced bins
    * between the minimum and maximum for each scalar column.
    */
   private int numberOfBins = 2;

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      tbl = (ExampleTable) pullInput(0);

      inputs = tbl.getInputFeatures();
      outputs = tbl.getOutputFeatures();

      if ((inputs == null) || (inputs.length == 0)) {
         throw new Exception(getAlias() +
                             ": Please select the input features, they are missing.");
      }

      if (outputs == null || outputs.length == 0) {
         throw new Exception(getAlias() +
                             ": Please select an output feature, it is missing");
      }

      if (tbl.isColumnScalar(outputs[0])) {
         throw new Exception(getAlias() +
                             ": Output feature must be nominal. Please transform it.");
      }

      nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(3);

      int type = getBinMethod();

      // if type == 0, specify the number of bins
      // if type == 1, use uniform weight
      // BinTree bt;
      BinDescriptor[] bins;

      if (type == 0) {

         // int number = (int)pp.getValue(ParamSpaceGenerator.NUMBER_OF_BINS);
         int number = getNumberOfBins();

         if (number < 0) {
            throw new Exception(getAlias() + ": Number of bins not specified!");
         }

         bins = numberOfBins(number);
      } else {

         // int weight = (int)pp.getValue(ParamSpaceGenerator.ITEMS_PER_BIN);
         int weight = getBinWeight();
         bins = sameWeight(weight);
      }

      BinTransform bt = new BinTransform(tbl, bins, false);


      pushOutput(bt, 0);
   } // doit

   /**
    * Get the bin method.
    *
    * @return bin method
    */
   public int getBinMethod() { return binMethod; }

   /**
    * Get the bin weight.
    *
    * @return bin weight
    */
   public int getBinWeight() { return binWeight; }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] in = { "ncsa.d2k.modules.core.datatype.table.ExampleTable" };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {

      String s =
         "<p>Overview: Automatically discretize scalar data for the " +
         "Naive Bayesian classification model." +
         "<p>Detailed Description: Given a table of Examples, define the bins for each " +
         "scalar input column.  </P><P>When binning Uniformly, " +
         "the number of bins is determined by '<i>Number of Bins</i>' property, " +
         "and the boundaries of the bins are set so that they divide evenly over the range " +
         "of the binned column.</p><P>" +
         " When binning by weight, '<i>Number of Items per Bin</I>' sets the size of each bin. " +
         "The values are then binned so that in each bin there is the same number of items. " +
         "For more details see description of property '<i>Number of Items per Bin</I>'." +
         "</P><P>Data Handling: This module does not modify the input data." +
         "<p>Scalability: The module requires enough memory to make copies of " +
         "each of the scalar input columns.";

      return s;
   }

   /**
    * Get the number of bins.
    *
    * @return the number of bins
    */
   public int getNumberOfBins() { return numberOfBins; }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[3];
      pds[0] =
         new PropertyDescription("binMethod",
                                 "Discretization Method",
                                 "The method to use for discretization.  Select 1 to create bins" +
                                 " by weight.  This will create bins with an equal number of items in " +
                                 "each slot.  Select 0 to do uniform discretization by specifying the number of bins. " +
                                 "This will result in equally spaced bins between the minimum and maximum for " +
                                 "each scalar column.");
      pds[1] =
         new PropertyDescription("binWeight",
                                 "Number of Items per Bin",
                                 "When binning by weight, this is the number of items" +
                                 " that will go in each bin.  However, the bins may contain more or fewer values than " +
                                 "weight values, depending on how many items equal the bin limits. Typically " +
                                 "the last bin will contain less or equal to weight  values and the rest of the " +
                                 "bins will contain a number that is  equal or greater to weight  values.");
      pds[2] =
         new PropertyDescription("numberOfBins",
                                 "Number of Bins",
                                 "Define the number of bins absolutely. " +
                                 "This will give equally spaced bins between " +
                                 "the minimum and maximum for each scalar " +
                                 "column.");

      return pds;
   } // end method getPropertiesDescriptions

   /**
    * Return the custom property editor.
    *
    * @return custom property editor
    */
   public CustomModuleEditor getPropertyEditor() { return new PropEdit(); }

   /**
    * Set the bin method.
    *
    * @param  i new bin method
    *
    * @throws PropertyVetoException when i is not 0 or 1
    */
   public void setBinMethod(int i) throws PropertyVetoException {
      binMethod = i;

      if (binMethod != 0 && binMethod != 1) {
         throw new PropertyVetoException("Discretization Method must be 0 or 1",
                                         null);
      }
   }

   /**
    * set the bin weight. Must be at least 1.
    *
    * @param  i new bin weight
    *
    * @throws PropertyVetoException when bin weight is less than 1.
    */
   public void setBinWeight(int i) throws PropertyVetoException {

      if (i < 1) {
         throw new PropertyVetoException("Number of items per bin must be a positive integer.",
                                         null);
      }

      binWeight = i;
   }

   /**
    * Set the number of bins. Must be at least 2.
    *
    * @param  i the number of bins
    *
    * @throws PropertyVetoException when i is less than 2
    */
   public void setNumberOfBins(int i) throws PropertyVetoException {

      if (i < 2) {
         throw new PropertyVetoException("Number of bins must be higher than 1.",
                                         null);
      }

      numberOfBins = i;
   }

   //~ Inner Classes ***********************************************************

   /**
    * Custom property editor.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class PropEdit extends JPanel implements CustomModuleEditor {
      boolean methodChange = false;

      /** label. */
      JLabel methodLabel = new JLabel("Discretization Method");

      /** list of bin methods. */
      JComboBox methodList;

      /** the methods. */
      String[] methods = { "Uniform", "Weight" };

      /** did num bins change? */
      boolean numBinsChange = false;

      /** label. */
      JLabel numBinslbl = new JLabel("Number of Bins");

      /** text field to enter num bins. */
      JTextField numBinsText;

      /** did number of items change? */
      boolean numItemsChange = false;

      /** label. */
      JLabel numItemslbl = new JLabel("Number of Items per Bin");

      /** text field to enter num items. */
      JTextField numItemsText;

      /**
       * Constructor.
       */
      private PropEdit() {

         // int lr = getLabelsRow();
         int method = getBinMethod();
         methodList = new JComboBox(methods);
         methodList.setSelectedIndex(method);
         // add action listener.

         methodLabel.setToolTipText("The method used for discretization");

         numItemslbl.setToolTipText(getPropertiesDescriptions()[1]
                                       .getDescription());
         numItemsText = new JTextField();

         numBinsText = new JTextField();
         numBinslbl.setToolTipText(getPropertiesDescriptions()[2]
                                      .getDescription());

         numBinsText.setText(Integer.toString(getNumberOfBins()));
         numItemsText.setText(Integer.toString(getBinWeight()));

         numBinsText.setEnabled(getBinMethod() == AutoBin.UNIFORM);
         numItemsText.setEnabled(getBinMethod() == AutoBin.WEIGHT);

         numBinsText.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) { numBinsChange = true; }
            });

         numItemsText.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) { numItemsChange = true; }
            });

         methodList.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {

                  int method = methodList.getSelectedIndex();

                  try {
                     setBinMethod(method);
                  } catch (PropertyVetoException e1) {

                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                     System.out.println("This is not a valid method!");
                  }

                  numBinsText.setEnabled(method == AutoBin.UNIFORM);
                  numItemsText.setEnabled(method == AutoBin.WEIGHT);

               }
            });

         // add the components for delimited properties
         setLayout(new GridBagLayout());

         /*Constrain.setConstraints(pnl, new JLabel("Delimited Format File
          * Properties"), 0, 0, 2, 1,
          * GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
          *         1,1, new Insets(2, 2, 15, 2));
          */
         Constrain.setConstraints(this,
                                  methodLabel,
                                  0,
                                  0,
                                  1,
                                  1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.EAST,
                                  1,
                                  1);
         Constrain.setConstraints(this,
                                  methodList,
                                  1,
                                  0,
                                  1,
                                  1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1,
                                  1);
         Constrain.setConstraints(this,
                                  numBinslbl,
                                  0,
                                  1,
                                  1,
                                  1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.EAST,
                                  1,
                                  1);
         Constrain.setConstraints(this,
                                  numBinsText,
                                  1,
                                  1,
                                  1,
                                  1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1,
                                  1);
         Constrain.setConstraints(this,
                                  numItemslbl,
                                  0,
                                  2,
                                  1,
                                  1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.EAST,
                                  1,
                                  1);
         Constrain.setConstraints(this,
                                  numItemsText,
                                  1,
                                  2,
                                  1,
                                  1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1,
                                  1);
      }

      /**
       * This method is called when the property sheet is closed and the
       * properties should be committed. This method will set the changed values
       * in the module. If the properties can not be set for any reason, return
       * false
       *
       * @return true if any parameters actually changed, otherwise return
       *         false.
       *
       * @throws Exception if for any reason, the parameters can not be set,
       *                   throw an exception with an appropriate message.
       */
      public boolean updateModule() throws Exception {
         boolean didChange = false;

         if (numItemsChange) {

            try {
               int numItems = Integer.parseInt(numItemsText.getText());
               setBinWeight(numItems);
               didChange = true;
            } catch (NumberFormatException e) {
               throw new Exception("Please indicate an integer number for the number of items per bin.");
            }

         } // if

         if (numBinsChange) {

            try {
               int numBins = Integer.parseInt(numBinsText.getText());
               setNumberOfBins(numBins);
               didChange = true;
            } catch (NumberFormatException e) {
               throw new Exception("Please indicate an integer number for the number bins.");
            }

         } // if

         return didChange;
      } // updateModule
   } // PropEdit

} // AutoBin
