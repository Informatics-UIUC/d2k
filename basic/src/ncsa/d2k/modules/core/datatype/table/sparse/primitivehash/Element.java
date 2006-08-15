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
package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

import ncsa.d2k.modules.core.datatype.table.sparse.ObjectComparator;


/**
 * Wrapper for an item in a sparse column. Used in sorting methods.
 *
 * @author  goren
 * @version $Revision$, $Date$
 */

public class Element implements Comparable {

   //~ Instance fields *********************************************************

   /** Whether or not this element is default. */
   private boolean _default;

   /** Whether or not this element is empty. */
   private boolean empty;

   /** Whether or not this elelemt exists. */
   private boolean exist;

   /** Index of this element. */
   private int index;

   /** Whether or not this element is missing. */
   private boolean missing;

   /** Element contents. */
   private Object obj;

   //~ Constructors ************************************************************

   /**
    * Creates a new Element object.
    *
    * @param _obj   Contents of this element
    * @param _index Index of this element
    */
   public Element(Object _obj, int _index) {
      obj = _obj;
      index = _index;
      missing = false;
      empty = false;
      _default = false;
      exist = true;
   }


   /**
    * Creates a new Element object.
    *
    * @param _obj     Contents of this element
    * @param _index   Index of this element
    * @param _missing Whether or not this element is missing
    * @param _empty   Whether or not this element is empty
    * @param _def     Whether or not this element is default
    * @param _exist   Whether or not this element exists
    */
   public Element(Object _obj, int _index, boolean _missing, boolean _empty,
                  boolean _def, boolean _exist) {
      obj = _obj;
      index = _index;
      exist = _exist;
      missing = _missing;
      empty = _empty;
      _default = _def;
   }

   //~ Methods *****************************************************************

   /**
    * Compares for equality with another element.
    *
    * @param  obj Object to compare
    *
    * @return Result of comparison (-1,0, or 1)
    */
   public int compareTo(Object obj) {

      if (obj instanceof Element) {
         System.out.println("WARNING: Cannot compare object " +
                            "of type Element to object of type " +
                            obj.getClass().getName());

         return -1;
      }

      Element other = (Element) obj;
      // checking for missing values of any kind;

      // if this object is not valid
      if (missing || empty || _default || !exist) {

         // and the other object is not valid too
         if (
             other.getMissing() ||
                other.getEmpty() ||
                other.getDefault() ||
                !other.getExist()) {
            return 0; // then they are equal
         }
         // if the other object is valid, then this object is "greater" than
         // other. (missing values are sorted to the end)
         else {
            return 1;
         }
      }
      // this object is valid but the other is not valid
      else if (
               other.getMissing() ||
                  other.getEmpty() ||
                  other.getDefault() ||
                  !other.getExist()) {
         return -1; // then this object is smaller.
      }


      // comparing the class of obj
      Object otherObj = other.getObj();
      String myObjectClass = this.obj.getClass().getName();
      String otherObjectClass = otherObj.getClass().getName();

      if (!myObjectClass.equals(otherObjectClass)) {
         System.out.println("WARNING: Cannot compare object " +
                            "of type " + myObjectClass + " to object of type " +
                            otherObjectClass);

         return -1;
      }


      // both objects are valid objects. compare them using ObjectComparator
      return new ObjectComparator().compare(obj, otherObj);
   } // end method compareTo

   /**
    * Returns whether or not this element is default.
    *
    * @return Whether or not this element is default
    */
   public boolean getDefault() { return _default; }

   /**
    * Returns whether or not this element is empty.
    *
    * @return Whether or not this element is empty
    */
   public boolean getEmpty() { return empty; }

   /**
    * Returns whether or not this element exists.
    *
    * @return Whether or not this element exists
    */
   public boolean getExist() { return exist; }

   /**
    * Returns the index of this element
    *
    * @return Index of this element
    */
   public int getIndex() { return index; }

   /**
    * Returns whether or not this element is missing.
    *
    * @return Whether or not this element is missing
    */
   public boolean getMissing() { return missing; }


   /**
    * Returns the contents of the element.
    *
    * @return Contents of this element
    */
   public Object getObj() { return obj; }

   /**
    * Sets whether or not this element is default.
    *
    * @param bl Whether or not this element is default
    */
   public void setDefault(boolean bl) { _default = bl; }

   /**
    * Sets whether or not this element is empty.
    *
    * @param bl Whether or not this element is empty
    */
   public void setEmpty(boolean bl) { empty = bl; }

   /**
    * Sets whether or not this element exists.
    *
    * @param bl Whether or not this element exists
    */
   public void setExist(boolean bl) { exist = bl; }

   /**
    * Sets whether or not this element is missing.
    *
    * @param bl Whether or not this element is mising
    */
   public void setMissing(boolean bl) { missing = bl; }

   /**
    * Returns the String representation of this element.
    *
    * @return String representation of this element
    */
   public String toString() {

      if (missing) {
         return "Missing";
      }

      if (empty) {
         return "Empty";
      }

      if (!exist) {
         return "Does not exist";
      }

      if (_default) {
         return "Default";
      }

      if (obj instanceof char[]) {
         return new String((char[]) obj);
      }

      if (obj instanceof byte[]) {
         return new String((byte[]) obj);
      }

      return obj.toString();
   } // end method toString


} // end class Element
