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
package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.core.modules.ComputeModule;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * <p>Overview: Generate statistics about a NaiveBayesModel. This will output
 * the probabilites of each possible combination of bins. The results will be
 * written to a file. The file is pipe-delimited.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class NBModelStatistics extends ComputeModule {

   //~ Instance fields *********************************************************

   /** list of all the bins */
   private List allBins;

   /** the attribute names from the model */
   private String[] attributes;

   /** map: (attributeName->index into attributes[]) */
   private HashMap attributeToIndexMap;

   /** All the bins for a particular attribute */
   private Object[] binsForAttribute;

   /** the classes from the model */
   private String[] classes;

   /** the naive bayes model */
   private NaiveBayesModel nbModel;

   //~ Methods *****************************************************************

   /**
    * Generate all the combinations of the bins. Recurse when necessary.
    *
    * @param  bins list containing bins
    *
    * @return All the combinations of the bins.
    */
   private List generateCombos(List bins) {
      List retVal = new ArrayList();

      // first, add the argument
      retVal.add(bins);

      // now, get the last bin in the list
      Bin b = (Bin) bins.get(bins.size() - 1);

      // add each bin from the next attribute
      // and recurse on each
      int attnum =
         ((Integer) attributeToIndexMap.get(b.attributeName)).intValue();

      attnum++;

      if (attnum < attributes.length) {
         List binList = (List) this.binsForAttribute[attnum];

         for (int i = 0; i < binList.size(); i++) {
            Bin bb = (Bin) binList.get(i);

            // need to make a copy of the argument.  since we are recursing we
            // cannot pass the same list
            List ll = new ArrayList(bins);
            ll.add(bb);
            retVal.addAll(generateCombos(ll));
         }
      }

      return retVal;
   } // end method generateCombos

   /**
    * Generate a string to print to the file and compute the probabilities.
    *
    * @param  data Description of parameter $param.name$.
    *
    * @return generate a string to print to the file and compute the
    *         probabilities.
    */
   private String process(List data) {
      nbModel.clearEvidence();

      double[] evidence = new double[0];

      StringBuffer desc = new StringBuffer();
      int size = data.size();

      for (int i = 0; i < size; i++) {
         Bin d = (Bin) data.get(i);

         // add this as evidence to the model
         evidence = nbModel.addEvidence(d.attributeName, d.binName);

         if (i != 0) {
            desc.append(" ^ ");
         }

         desc.append(d.attributeName + " = " + d.binName);
      }

      // now normalize the evidence
      double total = 0;

      for (int i = 0; i < evidence.length; i++) {
         total += evidence[i];
      }

      for (int i = 0; i < evidence.length; i++) {
         evidence[i] /= total;
      }

      desc.append("|");

      for (int i = 0; i < evidence.length; i++) {

         if (i != 0) {
            desc.append("|");
         }

         desc.append(evidence[i]);
      }

      desc.append("\n");

      return desc.toString();
   } // end method process

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      nbModel = (NaiveBayesModel) pullInput(0);

      // just in case
      if (!nbModel.isReadyForVisualization()) {
         nbModel.setupForVis();
         nbModel.setIsReadyForVisualization(true);
      }

      String file = (String) pullInput(1);

      attributes = nbModel.getAttributeNames();
      classes = nbModel.getClassNames();

      attributeToIndexMap = new HashMap();

      for (int i = 0; i < attributes.length; i++) {
         attributeToIndexMap.put(attributes[i], new Integer(i));
      }

      // create a list of all the possible combinations
      binsForAttribute = new Object[attributes.length];
      allBins = new ArrayList();

      for (int i = 0; i < attributes.length; i++) {
         String an = attributes[i];

         String[] binNames = nbModel.getBinNames(an);

         List bins = new ArrayList();

         for (int j = 0; j < binNames.length; j++) {
            String bn = binNames[j];

            Bin d = new Bin();
            d.attributeName = an;
            d.binName = bn;
            bins.add(d);
            allBins.add(d);
         }

         binsForAttribute[i] = bins;
      }

      // a list of all the possible combinations
      // this can get quite large!
      List allCombos = new ArrayList();

      for (int idx = 0; idx < allBins.size(); idx++) {
         Bin bin = (Bin) allBins.get(idx);

         List lst = new ArrayList();
         lst.add(bin);
         allCombos.addAll(generateCombos(lst));
      }

      // now we have a list of all the possible combinations.

      // create the file writer
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));

      // write out the first line, which simply says the column names
      writer.write("Condition");

      for (int i = 0; i < classes.length; i++) {

         if (i != classes.length - 1) {
            writer.write("|");
         }

         writer.write("Prob(" + classes[i] + ")");
      }

      writer.write("\n");

      // process each combination of bins
      for (int idx = 0; idx < allCombos.size(); idx++) {
         List lst = (List) allCombos.get(idx);
         String str = process(lst);
         writer.write(str);
      }

      writer.flush();
      writer.close();

      nbModel.clearEvidence();
   } // end method doit

   /**
    * Called by the D2K Infrastructure after the itinerary completes execution.
    */
   public void endExecution() {
      attributes = null;
      classes = null;
      nbModel = null;

      binsForAttribute = null;
      allBins = null;
      attributeToIndexMap = null;
   }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      if (i == 0) {
         return "The NaiveBayesModel to generate statistics about.";
      } else {
         return "File name to write the output to.";
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

      if (i == 0) {
         return "NaiveBayesModel";
      } else {
         return "FileName";
      }
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() { return new String[] {
                                               "ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel",
                                               "java.lang.String"
                                            }; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: Generate statistics about a NaiveBayesModel. This will output the probabilites of each " +
             "possible combination of bins. The results will be written to a file. The file is " +
             "pipe-delimited.</p>";
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Naive Bayes Model Statistics"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) { return null; }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() { return null; }

   //~ Inner Classes ***********************************************************

   /**
    * A representation of a Bin. It has an attribute name and a bin name.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class Bin {
      String attributeName;
      String binName;

      public String toString() { return attributeName + " = " + binName; }
   }
} // end class NBModelStatistics
