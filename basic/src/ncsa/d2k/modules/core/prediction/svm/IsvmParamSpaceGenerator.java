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
 * Generate a parameter space for Isvm. The only parameter is the nu value.
 *
 * @author  vered goren
 * @version 1.0
 */
public class IsvmParamSpaceGenerator extends AbstractParamSpaceGenerator {

   //~ Static fields/initializers **********************************************

   /** constant: Nu of Kernel. */
   static public final String NU = "Nu of Kernel";

   //~ Methods *****************************************************************

   /**
    * Returns a reference to the developer supplied defaults. These are like
    * factory settings, absolute ranges and definitions that are not mutable.
    *
    * @return the factory settings space.
    */
   protected ParameterSpace getDefaultSpace() {
      ParameterSpace psi = new ParameterSpaceImpl();
      String[] names = { NU };
      double[] min = { 0 };
      double[] max = { 1 };
      double[] def = { 0.1 };
      int[] res = { 1 };
      int[] types = { ColumnTypes.DOUBLE };
      psi.createFromData(names, min, max, def, res, types);

      return psi;

   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Isvm Parameter Space Generator"; }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[1];
      pds[0] = new PropertyDescription(NU, NU, "SVM Parameter nu");

      /*    pds[1] = new PropertyDescription("numAttributes", "Number Input
       * Features",
       *"Number of input features in the SVM problem");*/
      return pds;
   }

} // end class IsvmParamSpaceGenerator
