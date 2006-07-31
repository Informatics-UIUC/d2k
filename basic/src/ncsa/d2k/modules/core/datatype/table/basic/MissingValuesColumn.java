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
package ncsa.d2k.modules.core.datatype.table.basic;

/**
 * This class provides some support for missing values to all the subclasses. It
 * provides a method that returns true if there are any missing values in the
 * column, and also provides some support to maintain the list of missing
 * values.
 *
 * @author  redman
 * @author  suvalala
 * @author  clutter
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public abstract class MissingValuesColumn extends AbstractColumn {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static final long serialVersionUID = -1800413948697627105L;

   //~ Instance fields *********************************************************

   /**
    * Stores the missing values. A boolean for each row, true if the value in
    * that row is missing.
    */
   protected boolean[] missing = null;

   /** Count of the number of missing values. */
   protected int numMissingValues = 0;

   //~ Methods *****************************************************************

   /**
    * Gets the missing values in a <code>boolean</code> array.
    *
    * @return Missing values in a boolean array
    */
   public final boolean[] getMissingValues() { return missing; }

   /**
    * Gets the number of missing values in the <code>Column</code>.
    *
    * @return Number of missing values in the <code>Column</code>
    */
   public final int getNumMissingValues() { return numMissingValues; }

   /**
    * Tests if there are any missing values.
    *
    * @return True if there are any missing values
    */
   public final boolean hasMissingValues() { return numMissingValues != 0; }

   /**
    * Tests if the value at the specified row is missing.
    *
    * @param  row Row index to test if missing
    *
    * @return True if the value is missing
    */
   public final boolean isValueMissing(int row) { return missing[row]; }

   /**
    * Sets the missing values to the array passed in.
    *
    * @param miss Array of missing value flags
    */
   public final void setMissingValues(boolean[] miss) {
      missing = miss;
      this.numMissingValues = 0;

      for (int i = 0; i < miss.length; i++) {

         if (miss[i]) {
            this.numMissingValues++;
         }
      }
   }

   /**
    * Sets the value at the given row to missing if <code>b</code> is true, not
    * missing otherwise.
    *
    * @param b   True if the value is missing
    * @param row Row to set the missing flag for
    */
   public final void setValueToMissing(boolean b, int row) {

      if (b == missing[row]) {
         return;
      }

      if (b == true) {
         numMissingValues++;
      } else {
         numMissingValues--;
      }

      missing[row] = b;
   }

} // end class MissingValuesColumn
