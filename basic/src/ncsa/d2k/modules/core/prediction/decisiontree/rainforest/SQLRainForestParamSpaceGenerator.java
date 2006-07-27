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
package ncsa.d2k.modules.core.prediction.decisiontree.rainforest;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;


/**
 * Description of class SQLRainForestParamSpaceGenerator.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class SQLRainForestParamSpaceGenerator
   extends AbstractParamSpaceGenerator {

   //~ Static fields/initializers **********************************************

   /** constant for min leaf ratio. */
   static public final String MIN_RATIO = "Minimum Leaf Size Ratio";

   /** constant for threshold. */
   static public final String MODE_THRESHOLD = "Mode Threshold";

   /** constant for bin number. */
   static public final String BIN_NUMBER = "Bin Number";

   /** constant. */
   static public final String DOMINATE_RATIO = "Dominate Class Ratio";

   //~ Methods *****************************************************************

   /**
    * Returns a reference to the developer supplied defaults. These are like
    * factory settings, absolute ranges and definitions that are not mutable.
    *
    * @return the factory settings space.
    */
   protected ParameterSpace getDefaultSpace() {
      ParameterSpace psi = new ParameterSpaceImpl();
      String[] names =
      { MIN_RATIO, MODE_THRESHOLD, BIN_NUMBER, DOMINATE_RATIO };
      double[] min = { 0, 0, 1, 1 };
      double[] max =
      { 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Double.MAX_VALUE };
      double[] def = { 0.001, 200000, 100, 100 };
      int[] res = { 1000, 1000, 1000, 1000 };
      int[] types =
      {
         ColumnTypes.DOUBLE, ColumnTypes.DOUBLE, ColumnTypes.DOUBLE,
         ColumnTypes.DOUBLE
      };
      psi.createFromData(names, min, max, def, res, types);

      return psi;
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Rain Forest Param Space Generator"; }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[4];
      pds[0] =
         new PropertyDescription(MIN_RATIO, MIN_RATIO,
                                 "The minimum ratio of records in a leaf to the total number of records in the tree. The tree construction is terminated when this ratio is reached.");
      pds[1] =
         new PropertyDescription(MODE_THRESHOLD, MODE_THRESHOLD,
                                 "If the number of examples is greater than this threshold, the in-database mode is used. Otherwise, the in-memory mode is used.");
      pds[2] =
         new PropertyDescription(BIN_NUMBER, BIN_NUMBER,
                                 "If the number of distinct values in a numeric attribute is greater than Bin Number, data is grouped into this number of bins.");
      pds[3] =
         new PropertyDescription(DOMINATE_RATIO, DOMINATE_RATIO,
                                 "Ratio of most-common class to second-most-common class. The tree construction is terminated when this ratio is reached.");

      return pds;
   }
} // end class SQLRainForestParamSpaceGenerator
