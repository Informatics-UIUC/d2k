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
package ncsa.d2k.modules.core.datatype.table.sparse;

import java.util.Comparator;


/**
 * Compares two objects.
 *
 * @author  goren
 * @version $Revision$, $Date$
 */
public class ObjectComparator implements Comparator {

   //~ Instance fields *********************************************************

   /** String version of one object to compare. */
   private String str1;
   
   /** String version of another object to compare. */
   private String str2;

   //~ Constructors ************************************************************

   /**
    * Default ObjectComparator constructor.
    */
   public ObjectComparator() { }

   //~ Methods *****************************************************************


   /**
    * Performs the object comparison.
    *
    * @param  o1 First object to compare
    * @param  o2 Second object to compare
    *
    * @return Result of comparison
    */
   public int compare(Object o1, Object o2) {
      getStrings(o1, o2);

      try {
         float f1 = Float.parseFloat(str1);
         float f2 = Float.parseFloat(str2);
         float result = f1 - f2;

         if (result < 0) {
            return -1;
         } else if (result > 0) {
            return 1;
         } else {
            return 0;
         }

      } catch (NumberFormatException e) {
         return str1.compareTo(str2);
      }


   }

   /**
    * Tests for equality between the passed in Object and this Object.
    *
    * @param  obj Object to compare
    *
    * @return Result of comparison
    */
   public boolean equals(Object obj) {

      if (!obj.getClass().getName().equals(getClass().getName())) {
         return false;
      }

      return (compare(this, obj) == 0);
   }

   /**
    * Returns the String versions of each Object passed in.
    *
    * @param o1 First Object to get the String for
    * @param o2 Second Object to get the String for
    */
   public void getStrings(Object o1, Object o2) {

      if (o1 instanceof char[]) {
         str1 = new String((char[]) o1);
         str2 = new String((char[]) o2);
      } else if (o1 instanceof byte[]) {
         str1 = new String((byte[]) o1);
         str2 = new String((byte[]) o2);
      } else {
         str1 = o1.toString();
         str2 = o2.toString();
      }

   }

} // end class ObjectComparator
