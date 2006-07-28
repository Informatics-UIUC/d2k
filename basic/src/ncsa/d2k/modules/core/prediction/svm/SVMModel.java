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

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import ncsa.d2k.core.modules.ModelProducer;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.Table;

import java.io.Serializable;


/**
 * Support Vector Machine prediction module. Makes binary or multi-class
 * decisions for the given input. Input data must be numerical. Output class
 * must be integers.
 *
 * @author  Xiaolei Li
 * @version $Revision$, $Date$
 */
public final class SVMModel extends PredictionModelModule
   implements Serializable, ModelProducer {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -750669085121059108L;

   //~ Instance fields *********************************************************

   /** There are no visuals for a SVM. Hence this is always false. */
   private boolean isReadyForVisualization = false;

   /** The actual SVM stored in its native class. */
   private svm_model model;

   //~ Constructors ************************************************************

   /**
    * empty constructor.
    */
   public SVMModel() { }

   /**
    * Constructor which will save the given SVM model class and calculate the
    * input and output features based on the ExampleTable.
    *
    * @param model The native SVM model.
    * @param vt    The example table.
    */
   SVMModel(svm_model model, ExampleTable vt) {
      super(vt);
      this.model = model;
      setName("SVMModel");
   }

   //~ Methods *****************************************************************

   /**
    * get double value of a string.
    *
    * @param  s string
    *
    * @return double value
    */
   static private double atof(String s) {

      if ((s == null) || (s.length() == 0)) {
         return 0;
      } else {
         return Double.valueOf(s).doubleValue();
      }
   }

   /**
    * Get int value of string.
    *
    * @param  s string
    *
    * @return int value
    */
   static private int atoi(String s) {

      if ((s == null) || (s.length() == 0)) {
         return 0;
      } else {
         return Integer.parseInt(s);
      }
   }

   /**
    * Make predictions for the given data. Utilizes the native svm_predict()
    * from libsvm.
    *
    * @param pt Prediction Table.
    */
   protected void makePredictions(PredictionTable pt) {
      int[] ins = pt.getInputFeatures();
      int[] outs = pt.getOutputFeatures();
      int[] preds = pt.getPredictionSet();
      int non_zeros;
      double v = 0;

      /* for all examples */
      for (int i = 0; i < pt.getNumRows(); i++) {

         non_zeros = 0;

         for (int j = 0; j < ins.length; j++) {

            if (atof(pt.getString(i, ins[j])) > 0.0) {
               non_zeros++;
            }
         }

         /* create nodes for all input features */
         svm_node[] x = new svm_node[non_zeros];

         int k = 0;

         // System.out.print(">" + pt.getString(i, outs[0]) + " ");

         /* for all input attributes in the example */
         for (int j = 0; j < ins.length; j++) {

            if (atof(pt.getString(i, ins[j])) > 0.0) {
               x[k] = new svm_node();
               x[k].index = ins[j] + 1;
               x[k].value = atof(pt.getString(i, ins[j]));

               // System.out.print(x[k].index + ":" + x[k].value + " ");
               k++;
            }
         }
         // System.out.println();

         /* make the actual prediction */
         v = svm.svm_predict(model, x);

         /* so for some reason, svm_predict returns a double even
          * though the documentation of libsvm says classes should be integers.
          * so that's why there is the cast to int in
          * front of v. */
         pt.setIntPrediction((int) v, i, 0);
         // System.out.println("Prediction: " + v + " for row: " + i);
      } // end for
      // System.out.println("----------------------------------------");
   } // end method makePredictions

   /**
    * Pull in the ExampleTable, pass it to the predict() method, and push out
    * the PredictionTable returned by predict();
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {

      try {
         Table vt = (Table) pullInput(0);
         PredictionTable result;

         if (vt instanceof ExampleTable) {
            result = predict((ExampleTable) vt);
         } else {
            result = predict(vt.toExampleTable());
         }

         pushOutput(result, 0);
         pushOutput(this, 1);

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(ex.getMessage());
         System.out.println("ERROR: SVMModel.doit()");
         throw ex;
      }
   }

   /**
    * The input is an ExampleTable.
    *
    * @param  index the index of the input
    *
    * @return the description of the input
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "Input data in an ExampleTable.";

         default:
            return "";
      }
   }

   /**
    * Get the name of the input.
    *
    * @param  index the index of the input
    *
    * @return the name of the input
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Input Data";

         default:
            return "";
      }
   }

   /**
    * The input is a Table.
    *
    * @return the input types
    */
   public String[] getInputTypes() {
      String[] in = {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return in;
   }

   /**
    * Describe the function of this module.
    *
    * @return the description of this module.
    */
   public String getModuleInfo() {
      return "Support Vector Machine prediction module.  Makes binary" +
             "or multi-class decisions for the given input.  Input " +
             "data must be numerical.  Output class must be integers.";
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "SVMModel"; }

   /**
    * Describe the output.
    *
    * @param  index the index of the output
    *
    * @return the description of the output
    */
   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "Prediction results for the given data.";

         case 1:
            return "The SVM model for reference.";

         default:
            return "";
      }
   }

   /**
    * Get the name of the output.
    *
    * @param  index the index of the output
    *
    * @return the name of the output
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Predictions";

         case 1:
            return "SVM Model";

         default:
            return "";
      }
   }

   /**
    * The output is a PredictionTable.
    *
    * @return the output types
    */
   public String[] getOutputTypes() {
      String[] out =
      {
         "ncsa.d2k.modules.core.datatype.table.PredictionTable",
         "ncsa.d2k.modules.projects.xli.SVM.SVMModel"
      };

      return out;
   }


} // end class SVMModel
