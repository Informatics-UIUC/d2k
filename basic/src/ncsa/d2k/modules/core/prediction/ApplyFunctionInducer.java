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

import ncsa.d2k.core.modules.OrderedReentrantModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;


/**
 * Applies a FunctionInducer to an ExampleTable.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ApplyFunctionInducer extends OrderedReentrantModule {

   //~ Methods *****************************************************************

   /**
    * Description of method doit.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {

      ExampleTable exampleSet = null;
      // System.out.println("APPLYFUNCTION INDUCER: Entered doit");

      FunctionInducerOpt functionInducer =
         (FunctionInducerOpt) this.pullInput(0);
      ErrorFunction errorFunction = (ErrorFunction) this.pullInput(1);

      exampleSet = (ExampleTable) this.pullInput(2);

      // !!! do i need this?
      // exampleSet = (ExampleTable) exampleSet.copy();

      Model model = functionInducer.generateModel(exampleSet, errorFunction);

      // System.out.println("APPLYFUNCTION INDUCER: generated model");
      this.pushOutput(model, 0);
   }

   /**
    * Get a description of an input
    *
    * @param  i input index
    *
    * @return Description of the input
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "The function inducer module used to generate the model";

         case 1:
            return "The error function used to guide the function inducer";

         case 2:
            return "The example table used for generating the model";

         default:
            return "No such input";
      }
   }

   /**
    * Get the name of an input
    *
    * @param  i the input index
    *
    * @return Name of the input
    */
   public String getInputName(int i) {

      switch (i) {

         case 0:
            return "Function Inducer";

         case 1:
            return "Error Function";

         case 2:
            return "Examples";

         default:
            return "No such input";
      }
   }

   /**
    * The types of inputs to this module
    *
    * @return a String[] containing the classes of the inputs
    */
   public String[] getInputTypes() {
      String[] types =
      {
         "ncsa.d2k.modules.core.prediction.FunctionInducerOpt",
         "ncsa.d2k.modules.core.prediction.ErrorFunction",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

      return types;
   }

   /**
    * Description of the module's function
    *
    * @return Description of the module's function
    */
   public String getModuleInfo() {
      String s =  "<p>Overview: This module applies a function inducer module to the given example table using the ";
       s += "given error function to produce a model.</p>";
       return s;
   }

   /**
    * the name of this module
    *
    * @return  the name of this module
    */
   public String getModuleName() { return "Apply Function Inducer"; }

   /**
    * Description of the outputs
    *
    * @param  i output index
    *
    * @return the description of the output
    */
   public String getOutputInfo(int i) {

      switch (i) {

         case 0:
            return "The model generated from the example table and the error function";

         default:
            return "No such output";
      }
   }

   /**
    * the name of the output
    *
    * @param  i the output index
    *
    * @return name of the output
    */
   public String getOutputName(int i) {

      switch (i) {

         case 0:
            return "Model";

         default:
            return "No such output";
      }
   }

   /**
    * The types of inputs to this module
    *
    * @return a String[] containing the classes of the inputs
    */
   public String[] getOutputTypes() {
      String[] types = {
         "ncsa.d2k.modules.core.datatype.model.Model"
      };

      return types;
   }

   /**
    * Get the properties descriptions
    *
    * @return the properties descriptions
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      // hide properties that the user shouldn't udpate
      return new PropertyDescription[0];
   }
} // end class ApplyFunctionInducer
