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
package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;


/**
 * Generate a parameter space for SVM
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class SVMParamSpaceGenerator extends AbstractParamSpaceGenerator {

   //~ Methods *****************************************************************

   /**
    * Returns a reference to the developer supplied defaults. These are like
    * factory settings, absolute ranges and definitions that are not mutable.
    *
    * @return the default settings space.
    */
   protected ParameterSpace getDefaultSpace() {

      ParameterSpace psi = new ParameterSpaceImpl();
      String[] names = SVMParameters.PROPS_NAMES;
      double[] min = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      double[] max = { 4, 3, 9, 5, 0, 0, 0, 0, 0, 0, 0 };
      double[] def = { 0, 2, 3, 0, 0, 0.5, 40, 1, 0.001, 0.1, 1 };
      int[] res = { 5, 4, 10, 6, 1, 1, 1, 1, 1, 1, 1 };
      int[] types =
      {
         ColumnTypes.INTEGER, ColumnTypes.INTEGER,
         ColumnTypes.DOUBLE, ColumnTypes.DOUBLE, ColumnTypes.DOUBLE,
         ColumnTypes.DOUBLE, ColumnTypes.DOUBLE, ColumnTypes.DOUBLE,
         ColumnTypes.DOUBLE, ColumnTypes.DOUBLE, ColumnTypes.INTEGER
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
   public String getModuleName() {
      return "new Support Vector Machine Parameter Space Generator";
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = SVMParameters.getPropertiesDescriptions();
      // because this module's editor is a special one and all of the values
      // are numeric - replace the description for properties that are
      // represented by drop down lists in the regular SVM Builder module.

      pds[SVMParameters.SVM_TYPE] =
         new PropertyDescription(SVMParameters
                                    .PROPS_NAMES[SVMParameters.SVM_TYPE],
                                    SVMParameters
                                    .PROPS_NAMES[SVMParameters.SVM_TYPE],
                                    "Type of the SVM. (0 = C-SVC, 1 = nu-SVC, 2 = One-Class SVM, 3 = epsilon-SVR, 4 = nu-SVR)");

      pds[SVMParameters.KERNEL_TYPE] =
         new PropertyDescription(SVMParameters
                                    .PROPS_NAMES[SVMParameters.KERNEL_TYPE],
                                    SVMParameters
                                    .PROPS_NAMES[SVMParameters.KERNEL_TYPE],
                                    "Type of the SVM.  (0 = Linear, 1 = Polynomial, 2 = Radial Basis, 3 = Sigmoid)");


      pds[SVMParameters.SHRINKING] =
         new PropertyDescription(SVMParameters
                                    .PROPS_NAMES[SVMParameters.SHRINKING],
                                    SVMParameters
                                    .PROPS_NAMES[SVMParameters.SHRINKING],
                                    "Toggles shrinking heuristics on (1) and off (0)");


      return pds;
   } // end method getPropertiesDescriptions

} // end class SVMParamSpaceGenerator
