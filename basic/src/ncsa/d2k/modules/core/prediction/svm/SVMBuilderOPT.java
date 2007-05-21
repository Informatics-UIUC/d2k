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
import libsvm.svm_parameter;
import libsvm.svm_problem;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.util.*;


/**
 * Builds a Support Vector Machine (SVM). This is a wrapper for the popular
 * libsvm library (the Java version). The desired SVM could be of several types
 * and kernel types.
 *
 * <p>The original libsvm can be found at
 * http://www.csie.ntu.edu.tw/~cjlin/libsvm/. It has a modified BSD licence that
 * is compatible with GPL.</p>
 *
 * @author  Xiaolei Li
 * @version $Revision$, $Date$
 */
public class SVMBuilderOPT extends ComputeModule {

   //~ Methods *****************************************************************

   /**
    * Given a parameter point, create the native svm_parameter class that is
    * used by svm_train().
    *
    * @param  num_attributes The number of attributes in the input data (i.e.,
    *                        size of input vector).
    *
    * @return The parameters chosen for training the SVM encapsulated in the
    *         native svm_parameter class.
    *
    * @throws Exception Description of exception Exception.
    */
   private svm_parameter createParameters(Integer num_attributes)
      throws Exception {
      ParameterPoint pp = (ParameterPoint) this.pullInput(2);

      if (pp == null) {
         throw new Exception("SVMBuilderOPT: Invalid parameter point.");
      }

      svm_parameter param = new svm_parameter();

      param.svm_type = (int) pp.getValue(SVMParameters.SVM_TYPE);
      param.kernel_type = (int) pp.getValue(SVMParameters.KERNEL_TYPE);
      param.degree = (int) pp.getValue(SVMParameters.DEGREE);

      /* if the user entered 0.0, default to 1/k where k is the
       * number of attributes in the input data. */
      param.gamma = (double) pp.getValue(SVMParameters.GAMMA);

      if (param.gamma == 0.0) {
         param.gamma = 1.0 / num_attributes.doubleValue();
      }

      param.coef0 = (double) pp.getValue(SVMParameters.COEF0);
      param.nu = (double) pp.getValue(SVMParameters.NU);
      param.cache_size = (double) pp.getValue(SVMParameters.CACHE_SIZE);
      param.C = (double) pp.getValue(SVMParameters.C);
      param.eps = (double) pp.getValue(SVMParameters.EPS);
      param.p = (double) pp.getValue(SVMParameters.P);
      param.shrinking = (int) pp.getValue(SVMParameters.SHRINKING);

      /* these are hard-coded.  should be user inputs. */
      param.nr_weight = 0;
      param.weight_label = new int[0];
      param.weight = new double[0];

      return param;
   } // end method createParameters

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   protected void doit() throws Exception {

      try {

         /* get the training data */
         svm_problem prob = (libsvm.svm_problem) this.pullInput(0);

         /* create the parameters */
         svm_parameter param = createParameters((Integer) this.pullInput(1));

         /* check for errors */
         String error_msg = svm.svm_check_parameter(prob, param);

         if (error_msg != null) {
        	 myLogger.error("ERROR: SVMBuilder.doit()");
        	 myLogger.error(error_msg + "\n");
            System.exit(1);
         }

         /* build the actual SVM */
         svm_model model = svm.svm_train(prob, param);

         this.pushOutput(model, 0);

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(ex.getMessage());
         myLogger.error("ERROR: SVMBuilder.doit()");
         throw ex;
      }
   } // end method doit
   private D2KModuleLogger myLogger;

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    */
   public void beginExecution() {
	   myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   }

   /**
    * Called by the D2K Infrastructure after the itinerary completes execution.
    */
   public void endExecution() { super.endExecution(); }


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "Training data to be used for building the SVM.";

         case 1:
            return "Number of attributes in a single input.  The " +
                   "size of the input vector.";

         case 2:
            return "Control point in the parameter space.";

         default:
            return "";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Training Data";

         case 1:
            return "Number of Attributes";

         case 2:
            return "Parameter Point";

         default:
            return "";
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
      String[] in =
      {
         "libsvm.svm_problem", "java.lang.Integer",
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"
      };

      return in;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<b>Overview</b>: Builds a Support Vector Machine (SVM).<p> " +
             "<b>Detailed description</b>: This is a " +
             "wrapper for the popular libsvm library (the Java " +
             "version).  It builds a Support Vector Machine (SVM) for " +
             "the given sample data.  SVMs are popular in classification" +
             " due to its marginal maximization property which finds " +
             "a decision hyperplane that maximizes the distance to" +
             " the separate classes.  This makes for better " +
             "generalization.<p>" +


      "<b>Restrictions</b>: The SVM can deal with binary or " +
             "multi-class classification.  The classes need to be " +
             "integers and the attribute values need to be numerical.<p>" +

      "<b>Reference</b>: Chih-Chung Chang and Chih-Jen Lin, LIBSVM : a " +
             "library for support vector machines, 2001. Software " +
             "available at http://www.csie.ntu.edu.tw/~cjlin/libsvm/." +

      "<p>Note that libsvm has a modified BSD licence that is " +
             "compatible with GPL.";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "new Optimized SVM Builder"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "The built SVM in its native class.";

         default:
            return "";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "SVM Model";

         default:
            return "";
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
      String[] out = { "libsvm.svm_model" };

      return out;
   }
} // end class SVMBuilderOPT
