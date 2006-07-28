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

import ncsa.d2k.core.modules.InputModule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * This module will read a text file that is already in the standard libsvm
 * format and generate the corresponding svm_problem class to be used by the
 * libsvm library.
 *
 * <p>The standard libsvm format is:<br>
 * &nbsp;&nbsp;&nbsp;label index:value index:value ... index:value</p>
 *
 * <p>The label is to be an integer and the values are to be reals. The index
 * should start at 1. Indices that do not appear in the file are assumed to
 * carry value 0.</p>
 *
 * @author  Xiaolei Li
 * @version $Revision$, $Date$
 */
public class SVMProbReader extends InputModule {

   //~ Constructors ************************************************************

   /**
    * empty constructor.
    */
   public SVMProbReader() { }

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
         return Double.valueOf(s).doubleValue();
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
         String input_file_name = (String) this.pullInput(0);

         /* open the text file */
         BufferedReader fp =
            new BufferedReader(new FileReader(input_file_name));
         Vector vy = new Vector();
         Vector vx = new Vector();
         int max_index = 0;

         /* read all lines */
         while (true) {
            String line = fp.readLine();

            if (line == null) {
               break;
            }

            StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

            /* the y vector is the class */
            vy.addElement(st.nextToken());

            /* the x vector are all the features */
            int m = st.countTokens() / 2;
            svm_node[] x = new svm_node[m];

            for (int j = 0; j < m; j++) {
               x[j] = new svm_node();
               x[j].index = atoi(st.nextToken());
               x[j].value = atof(st.nextToken());
            }

            if (m > 0) {
               max_index = Math.max(max_index, x[m - 1].index);
            }

            vx.addElement(x);
         } // end while

         /* now create the problem class from the vectors */
         libsvm.svm_problem prob = new svm_problem();
         prob.l = vy.size();
         prob.x = new svm_node[prob.l][];

         for (int i = 0; i < prob.l; i++) {
            prob.x[i] = (svm_node[]) vx.elementAt(i);
         }

         prob.y = new double[prob.l];

         for (int i = 0; i < prob.l; i++) {
            prob.y[i] = atof((String) vy.elementAt(i));
         }

         fp.close();

         this.pushOutput(prob, 0);
         this.pushOutput(new Integer(max_index), 1);

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(ex.getMessage());
         System.out.println("ERROR: SVMProbReader.doit()");
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
    * @param  parm1 Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int parm1) {

      switch (parm1) {

         case 0:
            return "Input data file name.";

         default:
            return "";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  parm1 Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int parm1) {

      switch (parm1) {

         case 0:
            return "File Name";

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
      String[] in = { "java.lang.String" };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: This module will read a text file that is already in " +
             "the standard libsvm format and generate the corresponding " +
             "svm_problem class to be used by the libsvm library.</p>" +
             "<p>The standard libsvm format is: " +
             " label index:value index:value ... " +
             "index:value</p><p>The label is to be an integer and the " +
             "values are to be reals. The index should start at 1. " +
             "Indices that do not appear in the file are assumed to " +
             "carry value 0.</p>";
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "SVM Problem Reader"; }


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
            return "Native SVM Problem class.";

         case 1:
            return "Number of attributes in problem.";

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
            return "SVM Problem";

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
      String[] out = { "libsvm.svm_problem", "java.lang.Integer" };

      return out;
   }
} // end class SVMProbReader
