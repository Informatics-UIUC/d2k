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


/**
 * Description of class SVMParameters.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class SVMParameters {

   //~ Static fields/initializers **********************************************

   /** Description of field NUM_PROPS. */
   static public final int NUM_PROPS = 11;

   // parameters (properties) code

   /** Type of the SVM. C-SVC, nu-SVC, One-Class SVM, epsilon-SVR or nu-SVR) */
   static public final int SVM_TYPE = 0;

   /** Type of the kernel. Linear, Polynomial, Radial Basis or Sigmoid */
   static public final int KERNEL_TYPE = 1;

   /** Degree of kernel function, applicable to polynomial kernel only. */
   static public final int DEGREE = 2;

   /**
    * Gamma of kernel function. Applicable to polynomial, radial or sigmoid
    * kernels.
    */
   static public final int GAMMA = 3;

   /**
    * Coefficent 0 of kernel function. Applicable to polynomial or sigmoid
    * kernels.
    */
   static public final int COEF0 = 4;

   /** Cache memory size in MB. */
   static public final int CACHE_SIZE = 6;

   /** Stopping criterion. */
   static public final int EPS = 8;

   /** Parameter C of C-SVC, Epsilon-SVR, and nu-SVR. */
   static public final int C = 7;

   /** Parameter nu of nu-SVC, One-class SVM, and nu-SVR. */
   static public final int NU = 5;

   /** Epsilon of loss function in epsilon-SVR. */
   static public final int P = 9;

   /** Binary value to turn on/off shrinking heuristics. */
   static public final int SHRINKING = 10;

   /** properties names. */
   static public final String[] PROPS_NAMES =
   {
      "SVM Type",
      "Kernel Type",
      "Degree",
      "Gamma",
      "Coefficent 0 ",
      "Nu",
      "Cache Size",
      "C",
      "Epsilon",
      "P",
      "Shrinking Heuristics"
   };

   /** properties descriptions. */
   static public final String[] PROPS_DESCS =
   {
      "Type of the SVM. C-SVC, nu-SVC, One-Class SVM, epsilon-SVR or nu-SVR)",
      "Type of the kernel. Linear, Polynomial, Radial Basis or Sigmoid",
      "Degree of kernel function, applicable to polynomial kernel only.",
      "Gamma of kernel function. Applicable to polynomial, radial or sigmoid kernels.",
      "Coefficent 0 of kernel function. Applicable to polynomial or sigmoid kernels.",
      "Parameter nu of nu-SVC, One-class SVM, and nu-SVR.",
      "Cache memory size in MB.",
      "Parameter C of C-SVC, Epsilon-SVR, and nu-SVR.",
      "Stopping criterion.",
      "Epsilon of loss function in epsilon-SVR.",
      "Binary value to turn on/off shrinking heuristics."
   };


   // svm type values
   /** C-SVC. */
   static public final int C_SVC = 0;

   /** nu-SVC. */
   static public final int NU_SVC = 1;

   /** One Class SVM. */
   static public final int ONE_CLASS_SVM = 2;

   /** Epsilon SVR. */
   static public final int EPSILON_SVR = 3;

   /** nu SVR. */
   static public final int NU_SVR = 4;

   /** Description of field SVM_TYPE_NAMES. */
   static public final String[] SVM_TYPE_NAMES =
   {
      "C-SVC", "nu-SVC", "One Class SVM",
      "Epsilon SVR", "nu SVR"
   };

   // kernel type values
   /** Linear. */
   static public final int LINEAR = 0;

   /** Polynomial. */
   static public final int POLYNOMIAL = 1;

   /** Radial. */
   static public final int RADIAL = 2;

   /** Sigmoid. */
   static public final int SIGMOID = 3;

   /** Description of field KERNEL_TYPE_NAMES. */
   static public final String[] KERNEL_TYPE_NAMES =
   {
      "Linear", "Polynomial",
      "Radial", "Sigmoid"
   };

   //~ Methods *****************************************************************

   /**
    * Returns a list of the property descriptions.
    *
    * @return a list of the property descriptions.
    */
   static public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[NUM_PROPS];

      for (int i = 0; i < NUM_PROPS; i++) {
         pds[i] =
            new PropertyDescription(PROPS_NAMES[i], PROPS_NAMES[i],
                                    PROPS_DESCS[i]);
      }

      return pds;
   }

} // end class SVMParameters
