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
package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;


/**
 * <p>Overview: This module is used in the optimization of modules.  It takes
 * as input the module (class) to be optimized and a point in control parameter
 * space from the optimizer.  Next it creates the module from the class, sets
 * the module control parameters, and outputs the module for evaluation.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class GenerateFunctionInducer extends ComputeModule {

   //~ Instance fields *********************************************************

   /** Class of the function inducer */
   protected Class functionInducerClass = null;

   /** true if this is the first execution */
   protected boolean InitialExecution = true;

   //~ Methods *****************************************************************

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    */
   public void beginExecution() { InitialExecution = true; }

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      if (InitialExecution) {
         functionInducerClass = (Class) this.pullInput(0);
         InitialExecution = false;
      }

      ParameterPoint parameterPoint = (ParameterPoint) this.pullInput(1);

      FunctionInducerOpt functionInducerOpt = null;

      try {
         functionInducerOpt =
            (FunctionInducerOpt) functionInducerClass.newInstance();
      } catch (Exception e) {
         System.out.println("could not create class");
         throw new Exception();
      }

      functionInducerOpt.setControlParameters(parameterPoint);

      this.pushOutput(functionInducerOpt, 0);
   }

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
            return "A class that defines a optimizable module";

         case 1:
            return "A point in control parameter space for defining the module behavior";

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
            return "Function Inducer Opt Module";

         case 1:
            return "Control Parameter Point";

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
      String[] types =
      {
         "java.lang.Class",
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"
      };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: This module is used in the optimization of modules.  " +
             "It takes as input the module (class) to be optimized and a point "+
             "in control parameter space from the optimizer.  " +
             "Next it creates the module from the class, sets the module "+
             "control parameters, and outputs the module for evaluation.</p>";
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Generate Function Inducer"; }

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
            return "A module that has its control parameters set and is ready to evaluate";

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
            return "Function Inducer Module";

         default:
            return "No such output";
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
      { "ncsa.d2k.modules.core.prediction.FunctionInducerOpt" };

      return types;
   }


   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {
      boolean value = false;

      if (InitialExecution) {
         value = (this.getFlags()[0] > 0) && (this.getFlags()[1] > 0);
      } else {
         value = (this.getFlags()[1] > 0);
      }

      return value;
   }
} // end class GenerateFunctionInducer
