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
package ncsa.d2k.modules.core.prediction.decisiontree.c45;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode;

import java.beans.PropertyVetoException;


/**
 * Build a C4.5 decision tree. The tree is build recursively, always choosing
 * the attribute with the highest information gain as the root. The gain ratio
 * is used, whereby the information gain is divided by the information given by
 * the size of the subsets that each branch creates. This prevents highly
 * branching attributes from always becoming the root. The minimum number of
 * records per leaf can be specified. If a leaf is created that has less than
 * the minimum number of records per leaf, the parent will be turned into a leaf
 * itself.
 *
 * @author  David Clutter
 * @version $Revision$, $Date$
 */

/**
 *
 */
public class C45TreeBuilder extends C45TreeBuilderOPT {

   //~ Methods *****************************************************************

   /**
    * Build the decision tree.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {
      table = (ExampleTable) pullInput(0);
      numExamples = table.getNumRows();

/*    HashMap nameToIndexMap = new HashMap();
 *
 * for(int i = 0; i < pp.getNumParameters(); i++) {   String name = pp.getName(i);
 *   nameToIndexMap.put(name, new Integer(i)); }*/

/*    int idx = (int)pp.getValue(C45ParamSpaceGenerator.MIN_RECORDS);
 *  if(idx == null)   throw new Exception(getAlias()+": Minimum Number of
 * records per leaf not defined!");
 */
      // this.minimumRecordsPerLeaf =
      // pp.getValue(C45ParamSpaceGenerator.MIN_RATIO); if(minimumRecordsPerLeaf
      // < 1)  throw new Exception(getAlias()+": Must be at least one record per
      // leaf!");

      int[] inputs = table.getInputFeatures();

      if (inputs == null || inputs.length == 0) {
         throw new Exception(getAlias() + ": No inputs were defined!");
      }

      outputs = table.getOutputFeatures();

      if (outputs == null || outputs.length == 0) {
         throw new Exception("No outputs were defined!");
      }

      if (outputs.length > 1) {
         System.out.println("Only one output feature is allowed.");
         System.out.println("Building tree for only the first output variable.");
      }

      if (table.isColumnScalar(outputs[0])) {
         throw new Exception(getAlias() +
                             " C4.5 Decision Tree can only predict nominal values.");
      }

      // the set of examples.  the indices of the example rows
      int[] exampleSet;

      // use all rows as examples at first
      exampleSet = new int[table.getNumRows()];

      for (int i = 0; i < table.getNumRows(); i++) {
         exampleSet[i] = i;

         // use all columns as attributes at first
      }

      int[] atts = new int[inputs.length];

      for (int i = 0; i < inputs.length; i++) {
         atts[i] = inputs[i];

      }

      DecisionTreeNode rootNode = buildTree(exampleSet, atts);
      pushOutput(rootNode, 0);
      // pushOutput(table, 1);
   } // end method doit

   /**
    * The types of inputs to this module
    *
    * @return a String[] containing the classes of the inputs
    */
   public String[] getInputTypes() {
      String[] in = {
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

      return in;
   }

   /*public String[] getOutputTypes() {
    * String[] out = {
    * "ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode",
    * "ncsa.d2k.modules.core.datatype.table.ExampleTable"}; return out;}*/

   /**
    * Get the minimum ratio per leaf
    *
    * @return the minimum ratio per leaf
    */
   public double getMinimumRatioPerLeaf() {
      return super.getMinimumRatioPerLeaf();
   }

   /**
    * Return a list of the property descriptions.
    *
    * @return a list of the property descriptions.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] retVal = new PropertyDescription[1];
      retVal[0] =
         new PropertyDescription("minimumRatioPerLeaf", "Minimum Leaf Ratio",
                                 "The minimum ratio of records in a leaf to the total number of records in the tree. " +
                                 "The tree construction is terminated when this ratio is reached.");

      return retVal;
   }

   /**
    * Set the minimum ratio per leaf.
    *
    * @param  d the new minimum ratio
    *
    * @throws PropertyVetoException when the ratio is not between 0 and 1.
    */
   public void setMinimumRatioPerLeaf(double d) throws PropertyVetoException {

      if (d < 0 || d > 1) {
         throw new PropertyVetoException("minimumRatioPerLeaf must be between 0 and 1",
                                         null);
      }

      super.setMinimumRatioPerLeaf(d);
   }
} // end class C45TreeBuilder
