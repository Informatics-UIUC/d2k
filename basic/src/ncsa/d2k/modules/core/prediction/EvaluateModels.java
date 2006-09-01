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


import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;


/**
 * EvaluateModels takes in a model and an example Table and runs the
 * model's predict function on the input table capable of receiving multiple
 * models. they are all tested on the same test data, and make it easy to
 * compare performances.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class EvaluateModels
   extends ncsa.d2k.modules.core.prediction.ModelPredict {

   //~ Instance fields *********************************************************


   /** flag used to signify if this is the first exectution */
   private boolean first = true;

   /** example table used for evaluations */
   private ExampleTable tbl = null;

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      if (first) {
         tbl = (ExampleTable) pullInput(0);
         first = false;
      }

      PredictionModelModule pmm = (PredictionModelModule) pullInput(1);

      PredictionTable pt = pmm.predict(tbl);
      pushOutput(pt, 0);

   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb =
         new StringBuffer("<p>Overview: This module applies a prediction model ");
       sb.append("to a table of examples and ");
      sb.append("makes predictions for each output attribute based on the ");
      sb.append("values of the input attributes.</p>");

      sb.append("<p>Detailed Description: This module applies a previously ");
      sb.append("built model to a new set of examples that have the ");
      sb.append("same attributes as those used to train/build the model. The ");
      sb.append("module creates a new table that contains ");
      sb.append("columns for each of the values the model predicts, in ");
      sb.append("addition to the columns found in the original table. ");
      sb.append("The new columns are filled in with values predicted by the ");
      sb.append("model based on the values of the input attributes. ");
      sb.append("The module is capable of testing severals models on the same ");
      sb.append("test data.</p>");

      return sb.toString();
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Evaluate Models"; }

   /**
    * If this is the first execution, return true if both inputs are
    * satisifed.  Otherwise return true if input 0 is available.
    *
    * @return ready condtion
    */
   public boolean isReady() {

      if (first) {
         return (getInputPipeSize(0) > 0 && getInputPipeSize(1) > 0);
      } else {
         return (getInputPipeSize(1) > 0);
      }
   }

} // end class EvaluateModels
