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
package ncsa.d2k.modules.core.discovery.ruleassociation.apriori;

import java.io.Serializable;


/**
 * <p>Title:</p>
 *
 * <p>Description:</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company:</p>
 *
 * @author  $Author$
 * @version 1.0
 *
 * @todo    Must review this code. It has many traps for array index out of
 *          bounds exceptions. Checking for availability of space in the
 *          integer array is never performed - must add these checks to the add
 *          methods. Also some of the add methods don't add but just override.
 *          Search D2K files to see where these class is used. Maybe use Trove
 *          Int Hash Set instead.
 */
final class MutableIntegerArray implements Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 699816325133822226L;

   /**
    * The default value for resizing the integers array. Used in case
    * setResizeFactor is called with an invalid number.
    */
   static public final double DEFAULT_RESIZE_FACTOR = 1.2;

   //~ Instance fields *********************************************************

   /** The debug property -= whether to out debug informaiton to stdout. */
   private final boolean debug = false;

   /**
    * The resize factor of the integers array. when adding new items to the
    * array and the array is full - allocates a new array with length <codE>
    * integers.length * resizeFactor</code>
    */
   private double resizeFactor = 1.2;

   /** the number of entries occupying the array.,. */
   int count;

   /** the data. */
   int[] integers = null;

   //~ Constructors ************************************************************

   /**
    * Constructs a MutableIntegerArray by copying data from <code>mia</code>.
    * Copying only <code>mia.count</code> integers from <code>
    * mia.integers</code> array.
    *
    * @param mia MutableIntegerArray Copying the data from <coe>mia into this
    *            object.
    */
   MutableIntegerArray(MutableIntegerArray mia) {
      count = mia.count;
      integers = new int[count];
      System.arraycopy(mia.integers, 0, integers, 0, count);
   }

   /**
    * Constructs a MutableIntegerArray with <code>size</code> integers. All set
    * to zero.
    *
    * @param size int The number of integers in the array.
    */
   MutableIntegerArray(int size) {
      integers = new int[size];
      count = 0;
   }

   /**
    * Constructs a MutableIntegerArray with <code>t</code> as the data integers
    * and <cod>t.length as the count.
    *
    * @param t int[] The data for this new mutable integr array.
    */
   MutableIntegerArray(int[] t) {
      integers = t;
      count = t.length;
   }

   //~ Methods *****************************************************************

   /**
    * Add an integer to the array. Verifies that the array isn't full already.
    * If the array is full allocates a new larger array, according to the resize
    * factor.
    *
    * @param val the value to add.
    */
   final void add(int val) {

      if (integers.length == count) {
         int[] temp = new int[(int) Math.ceil(integers.length * resizeFactor)];
         System.arraycopy(integers, 0, temp, 0, count);
         integers = temp;
      }

      integers[count++] = val;
   }

   /**
    * Add all the elements from the <code>MutableIntegerArray</code> passed in.
    *
    * @param addMe the mutable array to copy into this one.
    */
   final void add(MutableIntegerArray addMe) {
      System.arraycopy(addMe.getArray(), 0, integers, 0, addMe.count);
      this.count = addMe.count;
   }

   /**
    * count the items these two sorted mutable integer arrays share.
    *
    * @param  addMe the array to intersect.
    *
    * @return count the items these two sorted mutable integer arrays share.
    */
   final int countIntersection(MutableIntegerArray addMe) {
      int othersIndex = 0;
      int intersectionCount = 0;

      if (debug) {
         System.out.println("Count-----");
         System.out.println("set a:" + this.toString());
         System.out.println("set b:" + addMe.toString());
      }

done:
      for (int i = 0; i < this.count; i++) {

         // Find the first entry in the other integer array that
         // is greater than or equal to the current entry.
         while (addMe.get(othersIndex) < integers[i]) {
            othersIndex++;

            if (othersIndex >= addMe.count) {
               break done;
            }
         }

         // If they are equal, add the item to the new integer array.
         if (addMe.get(othersIndex) == integers[i]) {
            intersectionCount++;
         }
      }

      if (debug) {
         System.out.println("result:" + intersectionCount);
      }

      return intersectionCount;
   } // end method countIntersection

   /**
    * Delete an item.
    *
    * @param which The index of the item to be removed.
    */
   final void delete(int which) {
      System.arraycopy(integers, which, integers, which + 1,
                       integers.length - (which + 1));
      count--;
   }

   /**
    * Returns integer indexed <codE>i< /code> in the integers array.</codE>
    *
    * @param  i int integer indexed <codE>i< /code> in the integers array</codE>
    *
    * @return int
    */
   final int get(int i) { return integers[i]; }

   /**
    * Returns the integers array.
    *
    * @return int[] The integers array
    */
   final int[] getArray() { return integers; }

   /**
    * Returns an integers array with size <code>count</code>, with the first
    * <code>count</code> items in this array.
    *
    * @return int[ ] an array of size <code>count</code>, with the first <code>
    *         count</code> items in this array
    */
   final int[] getPackedArray() {

      if (integers.length != count) {
         int[] pp = new int[count];
         System.arraycopy(integers, 0, pp, 0, count);
         integers = pp;
      }

      return integers;
   }

   /**
    * Find out what items these two sorted mutable integer arrays share.
    *
    * @param addMe the array to intersect.
    * @param mia   add the intersection to this guy.
    */
   final void intersection(MutableIntegerArray addMe, MutableIntegerArray mia) {

      if (debug) {
         System.out.println("Intersection-----");
         System.out.println("set a:" + this.toString());
         System.out.println("set b:" + addMe.toString());
      }

      // reset tmp.
      mia.count = 0;

      // compute the max possible size of the intersection, and create
      // a mia to hold it.
      int size = addMe.count < this.count ? addMe.count : this.count;

      // These are for the addMe array.
      int othersIndex = 0;
      int[] otherInts = addMe.getArray();
      int[] myInts = integers;
      int[] newInts = mia.integers;
      int newCount = 0;

done:
      for (int i = 0; i < this.count; i++) {

         // Find the first entry in the other integer array that
         // is greater than or equal to the current entry.
         while (otherInts[othersIndex] < myInts[i]) {
            othersIndex++;

            if (othersIndex >= addMe.count) {
               break done;
            }
         }

         // If they are equal, add the item to the new integer array.
         if (otherInts[othersIndex] == myInts[i]) {
            newInts[newCount++] = myInts[i];
         }
      }

      mia.count = newCount;

      if (debug) {
         System.out.println("result:" + mia.toString());
      }
   } // end method intersection

   /**
    * Reset the size to zero.
    */
   final void reset() { count = 0; }

   /**
    * Returns the value of the resize factor.
    *
    * @return double Teh value of the resize factor.
    */
   public double getResizeFactor() { return resizeFactor; }

   /**
    * Sets the value of the resize factor.
    *
    * @param factor int The value for the resize factor. Must be greater than 1.
    */
   public void setResizeFacotr(double factor) {

      if (factor <= 1) {
         System.out.println("Resieze Factor must be greater than 1. setting the Resize Factor to the default value: " +
                            DEFAULT_RESIZE_FACTOR);
         resizeFactor = DEFAULT_RESIZE_FACTOR;

         return;
      }

      resizeFactor = factor;
   }


   /**
    * Returns a String representation of the first <codE>count</codE> integers
    * that are in <code>integers.</code>
    *
    * @return String a String representation of this object - the first <codE>
    *         count</codE> integers in <code>integers</code> concatenated with a
    *         comma separating them.
    */
   public final String toString() {
      String r = "{";

      for (int i = 0; i < count - 1; i++) {
         r += Integer.toString(integers[i]) + ",";
      }

      if (count == 0) {
         r += "}";
      } else {
         r += Integer.toString(integers[count - 1]) + "}";
      }

      r += ":" + count + ":";

      return r;
   }

} // end class MutableIntegerArray
