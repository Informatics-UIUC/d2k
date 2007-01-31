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

import libsvm.svm_node;
import libsvm.svm_problem;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

import java.util.Vector;


/**
 * Creates a svm_problem class to be used by a SVM. The libsvm library reads
 * input data in the format of a svm_problem class. This module will convert an
 * ExampleTable into such a class. It will also output the number of attributes
 * in the input data.
 *
 * @author  Xiaolei Li
 * @version $Revision$, $Date$
 */

public class SVMProbMaker_MultiClass extends DataPrepModule {

   //~ Constructors ************************************************************

   /**
    * empty constructor.
    */
   public SVMProbMaker_MultiClass() { }

   //~ Methods *****************************************************************

   /**
    * convert string to double.
    *
    * @param  s string
    *
    * @return double
    */
   static private double atof(String s) {

      if ((s == null) || (s.length() == 0)) {
         return 0.0;
      } else {
         return Double.parseDouble(s);
      }
   }

   /**
    * convert string to int.
    *
    * @param  s string
    *
    * @return int
    */
   static private int atoi(String s) {

      if ((s == null) || (s.length() == 0)) {
         return 0;
      } else {
         return Integer.parseInt(s);
      }
   }

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   protected void doit() throws Exception {

      try {
         ExampleTable table = (ExampleTable) this.pullInput(0);

         /* get the input features */
         int[] inputs = table.getInputFeatures();

         /* get the output feature */
         int[] outputs = table.getOutputFeatures();

         /* there should only be 1 output feature */
          if (outputs.length == 0 || inputs.length == 0) {

            // vered - 02-23-04: throwing and exception instead of outputing a
            // message. this will terminate the intinerary.
            String msg =
               "Invalid features! You must assign input and output features to the input table!";
            throw new Exception(msg);
         }
         libsvm.svm_problem[] problems = new svm_problem[outputs.length];

         Vector[] yVectors = new Vector[outputs.length];
         for(int v=0; v<outputs.length; v++){
           yVectors[v] = new Vector();

         }
         Vector vx = new Vector();
         int max_index = 0;
         int num_valid_columns = 0;
         int k;

  /* for all given examples */
  for (int i = 0; i < table.getNumRows(); i++) {

    /* the first element should be the classification */
    for(int vy=0; vy<outputs.length; vy++){
          yVectors[vy].addElement(table.getString(i, outputs[vy]));
    }
//    vy.addElement(table.getString(i, outputs[0]));

      /* create nodes for all input features */
    svm_node[] x = new svm_node[inputs.length];

    num_valid_columns = 0;

    /* for all input attributes in the example */
    for (int j = 0; j < inputs.length; j++) {
      x[j] = new svm_node();

      /* add 1 because SVM starts index at 1 */
      x[j].index = inputs[j] + 1;
      x[j].value = atof(table.getString(i, inputs[j]));

      /* count the number of columns with value > 0 */
      if (x[j].value > 0) {
        num_valid_columns++;
      }

    }

    svm_node[] x_pruned = new svm_node[num_valid_columns];
    k = 0;

    /* now make a sparse version of the input example */
    for (int j = 0; j < num_valid_columns; j++) {
      x_pruned[j] = new svm_node();

      /* find the next non-zero value */
      while (x[k].value == 0) {
        k++;
      }

      x_pruned[j].index = x[k].index;
      x_pruned[j].value = x[k].value;
      k++;
    }

    /* add this particular example to the training set */
    vx.addElement(x_pruned);


  } // end for i

for(int vy=0; vy<outputs.length; vy++){
  problems[vy] = new svm_problem();
  problems[vy].l = yVectors[vy].size();
  problems[vy].x = new svm_node[problems[vy].l][];
  for (int i = 0; i < problems[vy].l; i++) {
  problems[vy].x[i] = (svm_node[]) vx.elementAt(i);
}
problems[vy].y = new double[problems[vy].l];
for (int i = 0; i < problems[vy].l; i++) {
  problems[vy].y[i] = atof( (String) yVectors[vy].elementAt(i));
}

}
/*  libsvm.svm_problem prob = new svm_problem();
  prob.l = vy.size();
  prob.x = new svm_node[prob.l][];
  for (int i = 0; i < prob.l; i++) {
    prob.x[i] = (svm_node[]) vx.elementAt(i);
  }
  prob.y = new double[prob.l];
  for (int i = 0; i < prob.l; i++) {
    prob.y[i] = atof( (String) vy.elementAt(i));
  }*/

         this.pushOutput(problems, 0);
         this.pushOutput(new Integer(inputs.length), 1);

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(getAlias() + " -- ERROR: doit()");
         System.out.println(ex.getMessage());

         throw ex;
      }
   } // end method doit

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    */
   public void beginExecution() { }

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
            return "Example table with input and output features assigned.";

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
            return "Example Table";

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
      String[] in = { "ncsa.d2k.modules.core.datatype.table.ExampleTable" };

      return in;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<P>Overview: Given an ExampleTable, this module will produce the " +
             "corresponding svm_problem classes that can be used by the " +
             "SVM library (libsvm).</P>" +
             "<P>Data Restrictions:<UL><LI> The module will build an svm_problem for " +
             "each output feature. The output features should be binaries. " +
             "<LI>The classes need to be " +
             "integers and the attribute values need to be numerical</LI></UL></P>";
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Create SVM Problem"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  parm1 Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int parm1) {

      switch (parm1) {

         case 0:
            return "An array of libsvm.svm_problem classes.";

         case 1:
            return "Number of input features in the original input table";

         default:
            return "";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  parm1 Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int parm1) {

      switch (parm1) {

         case 0:
            return "SVM Problems";

         case 1:
            return "Number of Attributes";

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
      String[] out = { "java.lang.Object", "java.lang.Integer" };

      return out;
   }

} // end class SVMProbMaker
