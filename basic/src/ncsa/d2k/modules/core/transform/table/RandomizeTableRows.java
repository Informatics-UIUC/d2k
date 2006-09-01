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


import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.MutableTable;

import java.util.Random;


/**
 * This module randomly reorders the rows of a Table.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class RandomizeTableRows extends ncsa.d2k.core.modules.DataPrepModule {

   //~ Instance fields *********************************************************

   /** random number generator. */
   private transient Random rand;

   /** The Seed is used to initialize the random number generator. */
   private int seed = 345;

   /** Use Seed Value Indicated. */
   private boolean useSeed = true;

   //~ Methods *****************************************************************

   /**
    * Randomizes the rows of the Table using swapping.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {

      if (useSeed) {
         rand = new Random(seed);
      } else {
         rand = new Random();
      }

      MutableTable table = (MutableTable) pullInput(0);
      int numRow = table.getNumRows();

      int j = 0;

      for (int i = 0; i < numRow; i++) {
         j = getRandomNumber(i, numRow - 1);
         table.swapRows(i, j);
      }

      pushOutput(table, 0);
   }

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
            return "The table to have rows randomized";

         default:
            return "NO SUCH INPUT!";
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
            return "Original Table";

         default:
            return "NO SUCH INPUT!";
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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: This module randomly reorders the rows of a Table. </p><p>Detailed Description:" +
             " The rows in the Table are randomly swapped to ensure a random ordering of the data. A" +
             " <i>Seed</i> may be specified for initializing the random number generator. If you specify" +
             " a <i>Seed</i>, remember to set the <i>Use Seed Value Indicated</i> flag to TRUE. Otherwise, it will" +
             " generate a random number without a seed initialization.    </p>    <p> Data Handling:" +
             " This module does its work on the data in place, so it doesn't need to allocate more memory." +
             " However, it does actually swap the rows of the data.    </p>    <p> Scalability:" +
             " For large data sets this module could do a lot of row swapping, which would mean a lot" +
             " of data movement. </p>";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Randomize Table Rows"; }


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
            return "The resulting table with rows randomly reordered.";

         default:
            return "NO SUCH OUTPUT!";
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
            return "Randomized Table";

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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

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
      PropertyDescription[] pds = new PropertyDescription[2];

      pds[0] =
         new PropertyDescription("seed", "Seed",
                                 "The Seed is used to initialize the random number generator.");

      pds[1] =
         new PropertyDescription("useSeed",
                                 "Use Seed Value Indicated",
                                 "The random number generator can be called with or without an initialization seed value. " +
                                 "This flag indicates whether the seed value given should be used.");

      return pds;
   }

   /**
    * Chooses a random integer between two integers (inclusive).
    *
    * @param  m - the lower integer
    * @param  n - the higher integer
    *
    * @return the pseudorandom integer between m and n (inclusive)
    */
   public int getRandomNumber(int m, int n) {

      if (m == n) {
         return m;
      } else {
         double rnd = (Math.abs(rand.nextDouble())) * (n - m + 1) + m;
         int theNum = (int) (rnd);

         return theNum;
      }
   }

   /**
    * Get the seed.
    *
    * @return the seed
    */
   public int getSeed() { return seed; }

   /**
    * Get use seed.
    *
    * @return use the seed
    */
   public boolean getUseSeed() { return useSeed; }

   /**
    * Set the seed.
    *
    * @param x the seed
    */
   public void setSeed(int x) { seed = x; }

   /**
    * Set use seed.
    *
    * @param b use the seed
    */
   public void setUseSeed(boolean b) { useSeed = b; }
} // end class RandomizeTableRows
