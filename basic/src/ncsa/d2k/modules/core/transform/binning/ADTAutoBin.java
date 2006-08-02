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

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.modules.core.datatype.ADTree;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.transformations.BinTransform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


/**
 * Automatically discretize scalar data for the Naive Bayesian classification
 * model. This module requires a ParameterPoint to determine the method of
 * binning to be used.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ADTAutoBin extends DataPrepModule {

   //~ Instance fields *********************************************************

   /** the ADTree */
   private ADTree adt;

   /** example table */
   private ExampleTable tbl;

   //~ Methods *****************************************************************

   /**
    * Create nominal bins automatically.
    *
    * @return Array of BinDescriptors
    *
    * @throws Exception when something goes wrong
    */
   protected BinDescriptor[] createAutoNominalBins() throws Exception {

      List bins = new ArrayList();
      int[] inputs = tbl.getInputFeatures();

      for (int i = 0; i < inputs.length; i++) {
         boolean isScalar = tbl.isColumnScalar(i);

         // System.out.println("scalar ? " + i + " " + isScalar);
         if (isScalar) {
            throw new Exception("ADTrees do not support scalar values");
         }

         // if it is nominal, create a bin for each unique value.
         // attributes indexes in the ADTree start at 1
         else {
            TreeSet vals = adt.getUniqueValuesTreeSet(inputs[i] + 1);
            Iterator iter = vals.iterator();
            int numRows = tbl.getNumRows();

            /*      if (tbl.getColumn(inputs[i]).hasMissingValues()) {
             *              String[] miss = new String[1];
             * miss[0]= tbl.getMissingString();
             *
             *
             *           BinDescriptor bd = new TextualBinDescriptor
             * (inputs[i],"Unknown",miss,tbl.getColumnLabel(inputs[i]));
             *     bins.add(bd);             //System.out.println("has missing
             * values for column " + inputs[i]);             while
             * (iter.hasNext()) {
             *      String st[] = new String[1];
             *                     String item = (String) iter.next();
             *                                           st[0] = item;
             *
             *                                   if(!item.equals(miss[0])) {
             *
             *                                                   BinDescriptor
             * bdm =
             * new TextualBinDescriptor(
             *                             inputs[i],
             *                                          item,
             *                                                  st,
             *
             * tbl.getColumnLabel(inputs[i]));
             *                   bins.add(bdm);}             }
             *
             *   }
             *
             *
             * else { // there are no missing values in this column
             */
            // System.out.println("no missing values for column " + inputs[i]);
            while (iter.hasNext()) {
               String item = (String) iter.next();
               String[] st = new String[1];
               st[0] = item;

               BinDescriptor bd =
                  new TextualBinDescriptor(inputs[i],
                                           item,
                                           st,
                                           tbl.getColumnLabel(inputs[i]));
               bins.add(bd);
            }
            // }
         } // end if
      } // end for

      BinDescriptor[] bn = new BinDescriptor[bins.size()];

      for (int i = 0; i < bins.size(); i++) {
         bn[i] = (BinDescriptor) bins.get(i);

      }

      // ANCA: adding missing values bins for attributes with missing values
      // bn = BinningUtils.addMissingValueBins(tbl,bn);
      return bn;

   } // end method createAutoNominalBins

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      adt = (ADTree) pullInput(0);
      tbl = (ExampleTable) pullInput(1);

      int[] inputs = tbl.getInputFeatures();

      if (inputs == null || inputs.length == 0) {
         throw new Exception("Input features are missing. Please select an input feature.");
      }

      int[] outputs = tbl.getOutputFeatures();

      if (outputs == null || outputs.length == 0) {
         throw new Exception("Output feature is missing. Please select an output feature.");
      }

      if (tbl.isColumnScalar(outputs[0])) {
         throw new Exception("Output feature must be nominal.");
      }

      BinDescriptor[] bins = createAutoNominalBins();

      BinTransform bt = new BinTransform(tbl, bins, false);

      pushOutput(bt, 0);

      // pushOutput(et, 1);
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
            return "The ADTree containing counts";

         case 1:
            return "MetaData ExampleTable containing the names of the input/output features";

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
            return "AD Tree";

         case 1:
            return "Meta Data Example Table";

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
      String[] in =
      {
         "ncsa.d2k.modules.core.datatype.ADTree",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s =
         "<p>Overview: Automatically discretize nominal data for the " +
         "Naive Bayesian classification model using ADTrees. " +
         "<p>Detailed Description: Given an ADTree and an Example table containing labels and " +
         "types of the columns, define the bins for each nominal input column, one bin " +
         "for each unique value in the column." +
         "<P>Data Handling: This module does not change its input. " +
         "Rather, it outputs a Transformation that can later be applied to the tree." +
         " The Transformation will be applied only to the input/output features defined by " +
         "the <i>Meta Data Example Table</i>." +
         "<p><u>Note</u>: The output of this modules should be used " +
         "to create a binned tree. Applying the Transformation on a Table " +
         "won't change the Table's content." +
         "<p>Data Type Restrictions: This module does not bin numeric data.";

      return s;
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return " AD Tree Auto Discretization"; }


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
            return "A BinTransform object that contains column_numbers, names and labels";

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
            return "Binning Transformation";

         default:
            return "no such output!";
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
      { "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform" };

      return types;
   }
} // end class ADTAutoBin
