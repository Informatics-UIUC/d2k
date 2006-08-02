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

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;


/**
 * Parameter space generator for AutoBin.  Can vary the binning method, the
 * number of items per bin, and the number of bins.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class AutoBinParamSpaceGenerator extends AbstractParamSpaceGenerator {

   //~ Static fields/initializers **********************************************

   /** constant for binning method. */
   static public final String BIN_METHOD = "Binning Method";

   /** constant for items per bin. */
   static public final String ITEMS_PER_BIN = "Number of items per bin";

   /** constant for number of bins. */
   static public final String NUMBER_OF_BINS = "Number of bins";

   //~ Methods *****************************************************************

   /**
    * Returns a reference to the developer supplied defaults. These are like
    * factory settings, absolute ranges and definitions that are not mutable.
    *
    * @return the factory settings space.
    */
   protected ParameterSpace getDefaultSpace() {

      ParameterSpace psi = new ParameterSpaceImpl();
      String[] names = { BIN_METHOD, ITEMS_PER_BIN, NUMBER_OF_BINS };
      double[] min = { 0, 1, 2 };
      double[] max = { 1, Integer.MAX_VALUE, Integer.MAX_VALUE };
      double[] def = { 0, 3, 3 };
      int[] res = { 1, 1, 1 };
      int[] types =
      { ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER };
      psi.createFromData(names, min, max, def, res, types);

      return psi;
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() {
      return "Auto Bin Parameter Space Generator";
   }


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
         new PropertyDescription(BIN_METHOD,
                                 "Discretization Method - Space definition",
                                 "The method to use for discretization.  Only 0 or 1 are valid values. " +
                                 "Select 1 to create bins " +
                                 "by weight.  This will create bins with an equal number of items in " +
                                 "each slot.  Select 0 to discretize by specifying the number of bins. " +
                                 "This will give equally spaced bins between the minimum and maximum for " +
                                 "each scalar column.");
      pds[1] =
         new PropertyDescription(ITEMS_PER_BIN,
                                 "Number of Items per Bin - Space definition ",
                                 "When binning by weight, this is the number of items" +
                                 " that will go in each bin.   However, the bins may contain more or fewer values than " +
                                 "<i>weight</i> values, depending on how many items equal the bin limits. Typically " +
                                 "the last bin will contain less than or equal to <i>weight</i> values and the rest of the " +
                                 "bins will contain a number that is  equal to or greater than <i>weight</i> values. " +
                                 "Minimum value must be 1, maximum is unlimited here but practically is limited by " +
                                 "the number of values.");
      pds[2] =
         new PropertyDescription(NUMBER_OF_BINS,
                                 "Number of Bins - Space Definition",
                                 "Define the number of bins absolutely. This will give equally spaced bins between " +
                                 "the minimum and maximum for each scalar column. The minimum number of bins can be 2 " +
                                 "and the maximum is unlimited. However, a very high number of bins is likely to results " +
                                 "in a high number of bins with no values.");

      return pds;
   } // end method getPropertiesDescriptions
} // end class AutoBinParamSpaceGenerator
