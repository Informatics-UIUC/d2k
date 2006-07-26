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
package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;


/**
 * Generate a parameter space for a continuous decision tree.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class DecisionTreeParamSpaceGenerator
   extends AbstractParamSpaceGenerator {

   //~ Instance fields *********************************************************

   /** the number of bias dimensions. */
   int numBiasDimensions = 10;

   //~ Methods *****************************************************************

   /**
    * Returns a reference to the developer supplied defaults. These are like
    * factory settings, absolute ranges and definitions that are not mutable.
    *
    * @return the factory settings space.
    */
   protected ParameterSpace getDefaultSpace() {

      int numControlParameters = 10;
      double[] minControlValues = new double[numControlParameters];
      double[] maxControlValues = new double[numControlParameters];
      double[] defaults = new double[numControlParameters];
      int[] resolutions = new int[numControlParameters];
      int[] types = new int[numControlParameters];
      String[] biasNames = new String[numControlParameters];

      int biasIndex = 0;

      biasNames[biasIndex] = "MinDecompositionPopulation";
      minControlValues[biasIndex] = 1;
      maxControlValues[biasIndex] = 100;
      defaults[biasIndex] = 20;
      resolutions[biasIndex] =
         (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] +
         1;
      types[biasIndex] = ColumnTypes.INTEGER;
      biasIndex++;

      biasNames[biasIndex] = "MaxTreeDepth";
      minControlValues[biasIndex] = 1;
      maxControlValues[biasIndex] = 100;
      defaults[biasIndex] = 20;
      resolutions[biasIndex] =
         (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] +
         1;
      types[biasIndex] = ColumnTypes.INTEGER;
      biasIndex++;

      biasNames[biasIndex] = "MinErrorReduction";
      minControlValues[biasIndex] = -999999.0;
      maxControlValues[biasIndex] = 0.0;
      defaults[biasIndex] = -999999.0;
      resolutions[biasIndex] = 1000000000;
      types[biasIndex] = ColumnTypes.DOUBLE;
      biasIndex++;

      biasNames[biasIndex] = "UseOneHalfSplit";
      minControlValues[biasIndex] = 0;
      maxControlValues[biasIndex] = 1;
      defaults[biasIndex] = 0;
      resolutions[biasIndex] = 1;
      types[biasIndex] = ColumnTypes.BOOLEAN;
      biasIndex++;

      biasNames[biasIndex] = "UseMidPointBasedSplit";
      minControlValues[biasIndex] = 0;
      maxControlValues[biasIndex] = 1;
      defaults[biasIndex] = 0;
      resolutions[biasIndex] = 1;
      types[biasIndex] = ColumnTypes.BOOLEAN;
      biasIndex++;

      biasNames[biasIndex] = "UseMeanBasedSplit";
      minControlValues[biasIndex] = 0;
      maxControlValues[biasIndex] = 1;
      defaults[biasIndex] = 1;
      resolutions[biasIndex] = 1;
      types[biasIndex] = ColumnTypes.BOOLEAN;
      biasIndex++;

      biasNames[biasIndex] = "UsePopulationBasedSplit";
      minControlValues[biasIndex] = 0;
      maxControlValues[biasIndex] = 1;
      defaults[biasIndex] = 0;
      resolutions[biasIndex] = 1;
      types[biasIndex] = ColumnTypes.BOOLEAN;

      biasIndex++;
      biasNames[biasIndex] = "SaveNodeExamples";
      minControlValues[biasIndex] = 0;
      maxControlValues[biasIndex] = 1;
      defaults[biasIndex] = 0;
      resolutions[biasIndex] = 1;
      types[biasIndex] = ColumnTypes.BOOLEAN;

      biasIndex++;
      biasNames[biasIndex] = "UseMeanNodeModels";
      minControlValues[biasIndex] = 0;
      maxControlValues[biasIndex] = 1;
      defaults[biasIndex] = 1;
      resolutions[biasIndex] = 1;
      types[biasIndex] = ColumnTypes.BOOLEAN;
      biasIndex++;

      biasNames[biasIndex] = "UseLinearNodeModels";
      minControlValues[biasIndex] = 0;
      maxControlValues[biasIndex] = 1;
      defaults[biasIndex] = 0;
      resolutions[biasIndex] = 1;
      types[biasIndex] = ColumnTypes.BOOLEAN;
      biasIndex++;

      ParameterSpace psi = new ParameterSpaceImpl();
      psi.createFromData(biasNames, minControlValues, maxControlValues,
                         defaults, resolutions, types);

      return psi;

   } // end method getDefaultSpace

   /**
    * All we have to do here is push the parameter space and function inducer
    * class.
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {

      Class functionInducerClass = null;

      try {
         functionInducerClass =
            Class.forName("ncsa.d2k.modules.core.prediction.decisiontree.continuous.DecisionTreeInducerOpt");
      } catch (Exception e) {

         // System.out.println("could not find class");
         // throw new Exception();
         throw new Exception(getAlias() +
                             ": could not find class DecisionTreeInducerOpt ");
      }

      if (space == null) {
         space = this.getDefaultSpace();
      }

      this.pushOutput(space, 0);
      this.pushOutput(functionInducerClass, 1);
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() {
      return "Decision Tree Param Space Generator";
   }


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
            return "Control Parameter Space for Decision Tree Inducer";

         case 1:
            return "Decision Tree Function Inducer Class";
      }

      return "";
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
            return "Parameter Space";

         case 1:
            return "Function Inducer Class";
      }

      return "";
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] out =
      {
         "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
         "java.lang.Class"
      };

      return out;
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

      int i = 0;
      pds[i++] =
         new PropertyDescription("minDecompositionPopulation",
                                 "Minimum examples per leaf",
                                 "Prevents the generation of splits that result in leaf nodes with " +
                                 "less than the specified number of examples.  ");

      pds[i++] =
         new PropertyDescription("maxTreeDepth",
                                 "Maximum depth of tree",
                                 "Prevents the generation of splits that result in trees with " +
                                 "more than the specified number depth.  ");

      pds[i++] =
         new PropertyDescription("minErrorReduction",
                                 "Minimum split error reduction",
                                 "The units of this error reduction are relative to the error function passed to the " +
                                 "decision tree inducer.  " +
                                 "A split will not occur unless the error is reduced by at least the amount specified.");

      pds[i++] =
         new PropertyDescription("useOneHalfSplit",
                                 "Generate splits only at 1/2",
                                 "This works fine for boolean and continuous values.  " +
                                 "If used as the sole decomposition strategy, it forces the system to only split on a variable once.  ");

      pds[i++] =
         new PropertyDescription("useMeanBasedSplit",
                                 "Generate mean splits",
                                 "The mean of each attribute value is calculated in the given node and used to generate " +
                                 "splits for that node.  One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

      pds[i++] =
         new PropertyDescription("useMidPointBasedSplit",
                                 "Generate midpoint splits",
                                 "The min and max values of each attribute at each node in the tree are used to generate splits for that node.  " +
                                 "The split occurs at the midpoint between the min and max values.  " +
                                 "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

      pds[i++] =
         new PropertyDescription("usePopulationBasedSplit",
                                 "Generate median splits",
                                 "The median of each attribute value is calculated in the given node and used to generate " +
                                 "splits for that node.  This requires sorting of all the examples in a node and therefore " +
                                 "scales at n log n in time complexity.  " +
                                 "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

      pds[i++] =
         new PropertyDescription("saveNodeExamples",
                                 "Save examples at each node",
                                 "In order to compute and print statistics of the node you must save the examples at the node " +
                                 "which increases the space and time complexity of the algorithm by a linear factor.  ");

      pds[i++] =
         new PropertyDescription("useMeanNodeModels",
                                 "Use the mean averaging for models",
                                 "This results in a simple decision tree with constant functions at the leaves.  " +
                                 "UseMeanNodeModels and UseLinearNodeModels are mutually exclusive.  ");

      pds[i++] =
         new PropertyDescription("useLinearNodeModels",
                                 "Use multiple regression for models",
                                 "This results in a decision tree with linear functions of the input attributes at the leaves.  " +
                                 "UseLinearNodeModels and UseMeanNodeModels are mutually exclusive.  ");

      return pds;
   } // end method getPropertiesDescriptions
} // end class DecisionTreeParamSpaceGenerator
