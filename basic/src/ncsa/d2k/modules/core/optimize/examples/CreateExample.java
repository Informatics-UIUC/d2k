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
package ncsa.d2k.modules.core.optimize.examples;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.Example;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl;

import java.util.ArrayList;


/**
 * used to be ncsa.d2k.modules.core.optimize.CreateExample.
 *
 * @author  David Tcheng
 * @version 1.0
 */
public class CreateExample extends ComputeModule {

   //~ Instance fields *********************************************************

   /** objectivePoints. */
   ArrayList objectivePoints;

   /** Description of field parameterPoints. */
   ArrayList parameterPoints;

   //~ Methods *****************************************************************

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * <In this case, the code does these specific activities>.
    */
   public void beginExecution() {
      parameterPoints = new ArrayList();
      objectivePoints = new ArrayList();
   }

   /**
    * Performs the main work of the module. {explain}
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module. <In this case, exceptions will be thrown in the
    *                   following scenarios>.
    */
   public void doit() {

      // add any new data at the end of the list.
      if (this.getFlags()[0] > 0) {
         parameterPoints.add(this.pullInput(0));
      }

      if (this.getFlags()[1] > 0) {
         objectivePoints.add(this.pullInput(1));
      }

      // if we have data to push, push it.
      if (parameterPoints.size() > 0 && objectivePoints.size() > 0) {
         ParameterPoint controlParameterPoint =
            (ParameterPoint) parameterPoints.remove(0);
         ParameterPoint objectiveParameterPoint =
            (ParameterPoint) objectivePoints.remove(0);
         int numInputs = controlParameterPoint.getNumParameters();
         int numOutputs = objectiveParameterPoint.getNumParameters();

         // Compile the data.
         double[][] data = new double[numInputs + numOutputs][1];
         int index = 0;

         for (int i = 0; i < numInputs; i++, index++) {
            data[index][0] = controlParameterPoint.getValue(i);
         }

         for (int i = 0; i < numOutputs; i++, index++) {
            data[index][0] = objectiveParameterPoint.getValue(i);
         }

         // get the names.
         String[] inputNames = new String[numInputs];
         int[] inputs = new int[numInputs];
         index = 0;

         for (int v = 0; v < numInputs; v++, index++) {

            // tlr change to preserve the real names of the paramters.
            inputNames[v] = controlParameterPoint.getName(v);

            if (inputNames[v] == null) {
               inputNames[v] = "in" + (v + 1);
            }

            inputs[v] = index;
         }

         String[] outputNames = new String[numOutputs];
         int[] outputs = new int[numOutputs];

         for (int v = 0; v < numOutputs; v++, index++) {

            // tlr change to preserve the real names of the ovjectives.
            outputNames[v] = objectiveParameterPoint.getName(v);

            if (outputNames[v] == null) {
               outputNames[v] = "out" + (v + 1);
            }

            outputs[v] = index;
         }

         // Make the column objects and build the table.
         int totCol = numInputs + numOutputs;
         Column[] cols = new Column[totCol];
         int colIdx = 0;

         for (; colIdx < numInputs; colIdx++) {
            cols[colIdx] = new DoubleColumn(data[colIdx]);
            cols[colIdx].setLabel(inputNames[colIdx]);
         }

         for (int i = 0; i < numOutputs; i++) {
            cols[colIdx] = new DoubleColumn(data[colIdx]);
            cols[colIdx].setLabel(outputNames[i]);
            colIdx++;
         }

         // Create the example table.
         ExampleTableImpl et = new ExampleTableImpl(cols);
         et.setInputFeatures(inputs);
         et.setOutputFeatures(outputs);

         // construct an example, first create a table.
         Example example = new ParameterPointImpl(et);
         example.setIndex(0);
         this.pushOutput(example, 0);
      } // end if
   } // end method doit

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
            return "The point in control parameter space that defines the " +
            		"behavior of the module being optimized.  ";

         case 1:
            return "The point in objective parameter space that defines the " +
            		"performance of the module given the control point.  ";

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
            return "Control Parameter Point";

         case 1:
            return "Objective Parameter Point";

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
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
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
      return "<p>Overview: This module creates an example for function " +
      		"induction given a point in control space paired with a point " +
      		"in objective space.</p>";
   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Create Example"; }

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
            return "A supervised learning example that is used for guiding " +
            		"the optimization process";

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
            return "Example";

         default:
            return "NO SUCH OUTPUT!";
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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Example" };

      return types;
   }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run. {explain}
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {

      if (this.getFlags()[0] > 0 || this.getFlags()[1] > 0) {
         return true;
      } else if (parameterPoints.size() > 0 && objectivePoints.size() > 0) {
         return true;
      }

      return false;
   }
} // end class CreateExample
