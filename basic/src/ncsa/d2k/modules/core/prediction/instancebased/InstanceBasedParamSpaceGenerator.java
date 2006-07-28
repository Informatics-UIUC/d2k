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
package ncsa.d2k.modules.core.prediction.instancebased;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;


/**
 * Generate a ParameterSpace for instance based models.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class InstanceBasedParamSpaceGenerator
   extends AbstractParamSpaceGenerator {

   //~ Static fields/initializers **********************************************

   /** constant for neighborhood size description */
   static public final String NEIGHBORHOOD_SIZE = "NeighborhoodSize";

   //~ Instance fields *********************************************************


   /** the number of bias paramters */
   int numBiasDimensions = 3;

   //~ Methods *****************************************************************

   /**
    * Returns a reference to the developer supplied defaults. These are like
    * factory settings, absolute ranges and definitions that are not mutable.
    *
    * @return the factory settings space.
    */
   protected ParameterSpace getDefaultSpace() {

      int numControlParameters = 3;
      double[] minControlValues = new double[numControlParameters];
      double[] maxControlValues = new double[numControlParameters];
      double[] defaults = new double[numControlParameters];
      int[] resolutions = new int[numControlParameters];
      int[] types = new int[numControlParameters];
      String[] biasNames = new String[numControlParameters];

      int biasIndex = 0;

      biasNames[biasIndex] = NEIGHBORHOOD_SIZE; // "NeighborhoodSize";
      minControlValues[biasIndex] = 1.0;
      maxControlValues[biasIndex] = 100;
      defaults[biasIndex] = 20;
      resolutions[biasIndex] =
         (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] +
         1;
      types[biasIndex] = ColumnTypes.INTEGER;
      biasIndex++;

      biasNames[biasIndex] = "DistanceWeightingPower";
      minControlValues[biasIndex] = 0.0;
      maxControlValues[biasIndex] = 300;
      defaults[biasIndex] = 0.0;
      resolutions[biasIndex] =
         (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] +
         1;
      types[biasIndex] = ColumnTypes.DOUBLE;
      biasIndex++;

      biasNames[biasIndex] = "ZeroDistanceWeight";
      minControlValues[biasIndex] = 0.0;
      maxControlValues[biasIndex] = 500;
      defaults[biasIndex] = 0.0;
      resolutions[biasIndex] =
         (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] +
         1;
      types[biasIndex] = ColumnTypes.DOUBLE;
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
            Class.forName("ncsa.d2k.modules.core.prediction.instancebased.InstanceBasedInducerOpt");
      } catch (Exception e) {

         // System.out.println("could not find class");
         // throw new Exception();
         throw new Exception(getAlias() +
                             ": could not find class InstanceBasedInducerOpt ");
      }

      if (space == null) {
         space = getDefaultSpace();
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
      return "Instance Based Param Space Generator";
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
            return "Control Parameter Space for InstanceBased Inducer";

         case 1:
            return "Instance Based Function Inducer Class";
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

      pds[0] =
         new PropertyDescription(NEIGHBORHOOD_SIZE,
                                 "Neighborhood Size",
                                 "The number of examples to use for fitting the prediction module.  " +
                                 "This must be set to 1 or greater.  ");

      pds[1] =
         new PropertyDescription("DistanceWeightingPower",
                                 "Distance Weighting Power",
                                 "The value of the power term in the inverse distance weighting formula.  " +
                                 "Setting this to zero causes equal weighting of all examples.  " +
                                 "Setting it to 1.0 gives inverse distance weighting.  " +
                                 "Setting it to 2.0 gives inverse distance squared weighting and so on.  ");

      pds[2] =
         new PropertyDescription("zeroDistanceWeight",
                                 "Zero Distance Value",
                                 "What weight to associate to a stored example which has zero distance to example to be predicted.  " +
                                 "Since division by zero is not permitted, some value must be assigned to examples with zero distance.  " +
                                 "This value is the weight and exact match should be given.  ");

      return pds;
   } // end method getPropertiesDescriptions
} // end class InstanceBasedParamSpaceGenerator
