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
package ncsa.d2k.modules.core.datatype.sort;

/**
 * This function sorts a 2-d double array by row based on the first column
 * value.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class QuickSort {

   //~ Methods *****************************************************************

   /**
    * Private method to implement the recursive quick sort: 
    * partition, sort left, sort right.  REturn the merged result.
    *
    * @param  A Description of parameter A.
    * @param  p Description of parameter p.
    * @param  r Description of parameter r.
    *
    * @return Description of return value.
    */
   static private double[][] doSort(double[][] A, int p, int r) {

      if (p < r) {
         int q = partition(A, p, r);
         doSort(A, p, q);
         doSort(A, q + 1, r);
      }

      return A;
   }

   /**
    * Private method to partition the array.
    *
    * @param  A Description of parameter A.
    * @param  p Description of parameter p.
    * @param  r Description of parameter r.
    *
    * @return The index of the partition.
    */
   static private int partition(double[][] A, int p, int r) {
      double x = A[p][0];

      int i = p - 1;
      int j = r + 1;

      while (true) {

         do {
            j--;
         } while (A[j][0] > x);

         do {
            i++;
         } while (A[i][0] < x);

         if (i < j) {
            double[] temp = A[i];
            A[i] = A[j];
            A[j] = temp;
         } else {
            return j;
         }
      }
   }

   /**
    * Implementation of standard quicksort.
    *
    * @param  A Description of parameter A.
    *
    * @return Description of return value.
    */
   static public double[][] sort(double[][] A) {
      return doSort(A, 0, A.length - 1);
   }
} // end class QuickSort
