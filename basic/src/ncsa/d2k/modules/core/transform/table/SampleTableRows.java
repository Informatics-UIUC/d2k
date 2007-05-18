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
package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.transform.table.gui.properties
          .SampleTableRows_Props;

   import java.beans.PropertyVetoException;
   import java.util.ArrayList;
   import java.util.Random;
   import ncsa.d2k.modules.core.util.*;


/**
 * Creates a sample of the given Table. If the sequential sampling is set, then
 * the first N rows of the table will be the sample. Otherwise, the sampled
 * table will contain N random rows (sampled without replacement) from the
 * table. The original table is left untouched.
 *
 * @author  David Clutter, revised Xiaolei Li (07/15/03)
 * @version $Revision$, $Date$
 */
public class SampleTableRows extends DataPrepModule {

   //~ Static fields/initializers **********************************************

   /** for sample size, use an absolute number. */
   static public final int ABSOLUTE = 10;

   /** for sample size, use a percentage. */
   static public final int PERCENTAGE = 11;

   /**
    * samples will be drawn randomly without replacement from the original
    * table.
    */
   static public final int RANDOM = 0;

   /** the first entries in the table will be used as the sample. */
   static public final int SEQUENTIAL = 1;

   //~ Instance fields *********************************************************

   /**
    * The number of rows to sample. This could either be an absolute value or a
    * percentage of the entire dateset.
    */
   private double N = 1;

   /** The type of sampling to use: random or sequential. */
   private int samplingMethod = RANDOM;

   /**
    * How to calculate the number of rows to sample. Either absolute or
    * percentage.
    */
   private int samplingSizeType = ABSOLUTE;

   /** The seed for the random number generator. */
   private int seed = 0;
   private D2KModuleLogger myLogger;
   
 
   //~ Methods *****************************************************************
   public void beginExecution() {
		  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
	   }

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      Table orig = (Table) pullInput(0);
      Table newTable = null;

      int realN;

      if (samplingSizeType == ABSOLUTE) {
         realN = (int) N;
      } else {
         realN = (int) (N * orig.getNumRows());
      }

      if (realN > (orig.getNumRows() - 1)) {
         int numRows = orig.getNumRows() - 1;
         throw new Exception(getAlias() + ": Sample size (" + realN +
                             ") is >= the number of rows in the table (" +
                             numRows + "). \n" + "Use a smaller sample size.");
      }

      myLogger.debug("Sampling " + realN + " rows from a table " +
              "of " + orig.getNumRows() + " rows.");

      /* only use the first N rows */
      if (samplingMethod == SEQUENTIAL) {
         newTable = (Table) orig.getSubset(0, realN);
      } else {
         int numRows = orig.getNumRows();
         int[] keeps = new int[realN];

         Random r = new Random(seed);

         /* optimization to do the least amount of work.  depending
          * on which set is bigger, either make a set to keep or a
          * set not to keep. */
         if (realN < (orig.getNumRows() / 2)) {
            ArrayList keepers = new ArrayList();

            for (int i = 0; i < realN; i++) {
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

            for (int i = 0; i < realN; i++) {
               int ind = Math.abs(r.nextInt()) % pickers.size();

               // System.out.println(pickers.size() + " " + ind);
               keeps[i] = ((Integer) pickers.remove(ind)).intValue();
            }
         }

         newTable = orig.getSubset(keeps);
      } // end if

      // System.out.println("Sampled table contains " +
      // newTable.getNumRows() + " rows.");

      pushOutput(newTable, 0);
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
            return "The table that will be sampled.";

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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

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
         "This module samples the input <i>Table</i> and chooses a certain number of rows to copy ";
      s +=
         "to a new <i>Sample Table</i>.  The number of rows and sampling method are determined by the ";
      s += "module properties. ";

      s += "</p><p>Detailed Description: ";
      s +=
         "This module creates a new <i>Sample Table</i> by sampling rows of the input <i>Table</i>.  If <i>Random Sampling</i> ";
      s +=
         "is not set, the first <i>Sample Size</i> rows in the input table are copied to the new table.  If it is ";
      s +=
         "set, <i>Sample Size</i> rows are selected randomly from the input table, using the <i>Random Seed</i> ";
      s +=
         "to seed the random number generator.  If the same seed is used across runs with the same input table, ";
      s += "the sample tables produced by the module will be identical. ";

      s += "</p><p>";
      s +=
         "If the input table has <i>Sample Size</i> or fewer rows, an exception will be raised. ";

      s += "</p><p>";
      s +=
         "An <i>OPT</i>, optimizable, version of this module that uses control ";
      s +=
         "parameters encapsulated in a <i>Parameter Point</i> instead of properties is also available. ";

      s += "</p><p>Data Handling: ";
      s += "The input table is not changed. ";

      s += "</p><p>Scalability: ";
      s +=
         "This module should scale very well. There must be memory to accommodate both the input table ";
      s += "and the resulting sample table. ";

      return s;
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Sample Table Rows"; }


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
            return "A new table containing a sample of rows from the original table.";

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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return types;
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[4];

      pds[0] =
         new PropertyDescription("samplingMethod", "Sampling " +
                                 "Method",
                                 "The method of sampling to use.  The choices are: " +
                                 "<p>Random: samples will be drawn randomly without " +
                                 "replacement from the original table.</p>" +
                                 "<p>Sequential: the first entries in the table will be used" +
                                 "as the sample.");
      pds[1] =
         new PropertyDescription("seed", "Random Seed", "The " +
                                 "seed for the random number generator used to select " +
                                 "the sample set of <i>Sample Size</i> rows.  It must " +
                                 "be greater than or equal to 0.  The seed is not " +
                                 "used if <i>Random Sampling</i> is off.");
      pds[2] =
         new PropertyDescription("samplingSizeType", "Sample Size Type",
                                 "To calculate the sample size as either an absolute " +
                                 "value or percentage of the original.");
      pds[3] =
         new PropertyDescription("sampleSize", "Sample Size",
                                 "The number of rows to include in the resulting table. " +
                                 "Could be an absolute value or percentage of " +
                                 "original.");

      return pds;
   } // end method getPropertiesDescriptions


   /**
    * Return a CustomModuleEditor to display the properties.
    *
    * @return custom module editor
    */
   public CustomModuleEditor getPropertyEditor() {
      return new SampleTableRows_Props(this);
   }

   /**
    * Get the sample size.
    *
    * @return sample size
    */
   public double getSampleSize() { return N; }

   /**
    * Get the sampling method.
    *
    * @return sampling method
    */
   public int getSamplingMethod() { return samplingMethod; }

   /**
    * Get the sampling size type.
    *
    * @return sampling size type
    */
   public int getSamplingSizeType() { return samplingSizeType; }

   /**
    * Get the seed.
    *
    * @return seed
    */
   public int getSeed() { return seed; }

   /**
    * Set the sample size.
    *
    * @param val new sample size
    */
   public void setSampleSize(double val) { N = val; }

   /**
    * Set the sampling method.
    *
    * @param val sampling method, either RANDOM or SEQUENTIAL
    */
   public void setSamplingMethod(int val) { samplingMethod = val; }

   /**
    * Set the sampling size type.
    *
    * @param val sampling size type, either ABSOLUTE or PERCENTAGE
    */
   public void setSamplingSizeType(int val) { samplingSizeType = val; }

   /**
    * Set the seed for random number generation.
    *
    * @param  i new seed
    *
    * @throws PropertyVetoException when something goes wrong
    */
   public void setSeed(int i) throws PropertyVetoException { seed = i; }
} // end class SampleTableRows
