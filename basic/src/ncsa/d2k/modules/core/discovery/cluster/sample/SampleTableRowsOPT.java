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
package ncsa.d2k.modules.core.discovery.cluster.sample;


import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.Table;

import java.util.ArrayList;
import java.util.Random;


/**
 * <p>Title: SampleTableRowsOPT</p>
 *
 * <p>Description: Creates a sample of the given Table. If the useFirst property
 * is set, then the first N rows of the table will be the sample. Otherwise, the
 * sampled table will contain N random rows from the table. The original table
 * is left untouched.</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company:</p>
 *
 * @author  David Clutter
 * @version 1.0
 */
public class SampleTableRowsOPT extends DataPrepModule {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 8432832306606006725L;

   //~ Instance fields *********************************************************

   /**
    * If true this module performs in verbose mode and outputs information to
    * stdout.
    */
   private boolean verbose_ = false;


   /** the number of rows to sample. */
   int N;

   /** the seed for the random number generator. */
   int seed;

   /**
    * true if the first N rows should be the sample, false if the sample should
    * be random rows from the table.
    */
   boolean useFirst;

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module: Sample <codE>N</code> rows from the
    * input table according to the values of the input ParameterPoint object's
    * properties.
    *
    * @throws Exception if <codE>N</code> (number of rows to be sampled) is
    *                   greater than the total number of rows in the input
    *                   table.
    */
   public void doit() throws Exception {

      ParameterPoint pp = (ParameterPoint) this.pullInput(0);

      this.N = (int) pp.getValue(0);
      this.seed = (int) pp.getValue(1);

      boolean uf = false;

      if (pp.getValue(2) == 1) {
         uf = true;
      }

      this.useFirst = uf;


      Table orig = (Table) pullInput(1);
      Table newTable = null;

      if (N > (orig.getNumRows() - 1)) {
         int numRows = orig.getNumRows() - 1;
         throw new Exception(getAlias() +
                             ": Sample size (" + N +
                             ") is >= the number of rows in the table (" +
                             numRows +
                             "). \n" +
                             "Use a smaller sample size.");
      }

      if (verbose_) {
         System.out.println("Sampling " + N + " rows from a table of " +
                            orig.getNumRows() + " rows.");
      }

      // only keep the first N rows
      if (useFirst) {
         newTable = (Table) orig.getSubset(0, N);
      } else {
         int numRows = orig.getNumRows();
         int[] keeps = new int[N];
         Random r = new Random(seed);

         if (N < (orig.getNumRows() / 2)) {
            ArrayList keepers = new ArrayList();

            for (int i = 0; i < N; i++) {
               int ind = Math.abs(r.nextInt()) % numRows;
               Integer indO = new Integer(ind);

               if (keepers.contains(indO)) {
                  i--;
               } else {
                  keeps[i] = ind;
                  keepers.add(indO);
               }
            }
         } else {
            ArrayList pickers = new ArrayList();

            for (int i = 0, n = numRows; i < n; i++) {
               pickers.add(new Integer(i));
            }

            for (int i = 0; i < N; i++) {
               int ind = Math.abs(r.nextInt()) % pickers.size();
               keeps[i] = ((Integer) pickers.remove(ind)).intValue();
            }
         }

         newTable = orig.getSubset(keeps);
      } // end if

      if (verbose_) {
         System.out.println("Sampled table contains " + newTable.getNumRows() +
                            " rows.");
      }

      pushOutput(newTable, 0);
   } // end method doit

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a description should be
    *                    returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int inputIndex) {

      switch (inputIndex) {

         case 0:
            return "Control parameters for the module.";

         case 1:
            return "The table that will be sampled.";

         default:
            return "No such input.";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int inputIndex) {

      switch (inputIndex) {

         case 0:
            return "Parameter Point";

         case 1:
            return "Table";

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
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s +=
         "This module samples the input <i>Table</i> and chooses a certain " +
         "number of rows to copy ";
      s +=
         "to a new <i>Sample Table</i>.  The number of rows and sampling " +
         "method are determined by the ";
      s += "input <i>Parameter Point</i>. ";

      s += "</p><p>Detailed Description: ";
      s +=
         "This module is the <i>OPT</i>, optimizable, version of <i>Sample " +
         "Table Rows</i>, and uses control ";
      s +=
         "parameters encapsulated in a <i>Parameter Point</i> to direct the " +
         "sampling behavior. ";
      s +=
         "The control parameters specify a <i>Random Seed</i>, a <i>Use " +
         "First Rows</i> flag, and a <i>Sample ";
      s +=
         "Size</i>.  These parameters are set as properties in the non-OPT " +
         "version of the module. ";

      s += "</p><p>";
      s +=
         "This module creates a new <i>Sample Table</i> by sampling rows of " +
         "the input <i>Table</i>.  If <i>Use First Rows</i> ";
      s +=
         "is set, the first <i>Sample Size</i> rows in the input table are " +
         "copied to the new table.  If it is not ";
      s +=
         "set, <i>Sample Size</i> rows are selected randomly from the input " +
         "table, using the <i>Random Seed</i> ";
      s +=
         "to seed the random number generator.  If the same seed is used " +
         "across runs with the same input table, ";
      s += "the sample tables produced by the module will be identical. ";

      s += "</p><p>";
      s +=
         "If the input table has fewer than <i>Sample Size</i> rows, an " +
         "exception will be raised. ";

      s += "</p><p>Data Handling: ";
      s += "The input table is not changed. ";

      s += "</p><p>Scalability: ";
      s +=
         "This module should scale very well. There must be memory to " +
         "accommodate both the input table ";
      s += "and the resulting sample table. ";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Sample Table Rows"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int outputIndex) {

      switch (outputIndex) {

         case 0:
            return "A new table containing a sample of rows from the " +
                    "original table.";

         default:
            return "No such output";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int outputIndex) {

      switch (outputIndex) {

         case 0:
            return "Sample Table";

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
      String[] types = {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return types;
   }

   /**
    * Returns the number of rows to be sampled.
    *
    * @return int The number of rows to be sampled.
    */
   public int getSampleSize() { return N; }

   /**
    * Returns the value of the seed for random sampling.
    *
    * @return int The value of the seed for random sampling
    */
   public int getSeed() { return seed; }


   /**
    * Returns the value for the Use First Rows/Sample Randomly property.
    *
    * @return boolean The value for the Use First Rows/Sample Randomly property
    */
   public boolean getUseFirst() { return useFirst; }


} // end class SampleTableRowsOPT
