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

import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.MutableTable;

import java.util.Iterator;
import java.util.LinkedList;


/**
 * Implements several methods common to all <code>
 * ncsa.d2k.modules.core.datatype.table.Column</code> implementations.
 *
 * @author  suvalala
 * @author  redman
 * @author  $Author$
 * @version $Revision$, $Date$
 * @see     ncsa.d2k.modules.core.datatype.table.Column
 */
public abstract class AbstractColumn implements Column {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static final long serialVersionUID = -213911595597217168L;

   //~ Instance fields *********************************************************

   /** The comment associated with this <code>Column</code>. */
   private String comment;

   /** Whether or not this <code>Column</code> is nominal. */
   private boolean isNominal;

   /** Whether or not this <code>Column</code> is scalar. */
   private boolean isScalar;

   /** The label associated with this <code>Column</code>. */
   private String label;

   /** Data type of the column. */
   protected int type;

   //~ Constructors ************************************************************

   /**
    * Constructor.
    */
   protected AbstractColumn() { }

   //~ Methods *****************************************************************

   /**
    * Sorts the elements in this <code>Column</code>.
    */
   public abstract void sort();

   /**
    * Sorts the elements in this <code>Column</code>, and swaps the rows in the
    * table it is a member of.
    *
    * @param t <code>MutableTable</code> to swap rows for
    */
   public abstract void sort(MutableTable t);

   /**
    * Sorts the elements in this <code>Column</code> starting with row 'begin'
    * up to row 'end', and swaps the rows in the <code>Table</code> we are a
    * part of.
    *
    * @param t     <code>MutableTable</code> to swap rows for
    * @param begin Row number which marks the beginnig of the column segment to
    *              be sorted
    * @param end   Row number which marks the end of the column segment to be
    *              sorted
    */
   public abstract void sort(MutableTable t, int begin, int end);

   /**
    * Gets the comment associated with this <code>Column</code>.
    *
    * @return The comment which describes this <code>Column</code>
    */
   public String getComment() { return comment; }

   /**
    * Tests if the <code>Column</code> is nominal.
    *
    * @return Whether or not the <code>Column</code> is nominal.
    */
   public boolean getIsNominal() { return isNominal; }

   /**
    * Tests if the <code>Column</code> is scalar.
    *
    * @return Whether or not the <code>Column</code> is scalar.
    */
   public boolean getIsScalar() { return isScalar; }

   /**
    * Gets the label associated with this <code>Column</code>.
    *
    * @return Label which describes this <code>Column</code>
    */
   public String getLabel() { return label; }

   /**
    * Gets the data type of the <code>Column</code>.
    *
    * @return Data type of the <code>Column</code>
    */
   public int getType() { return type; }

   /**
    * Removes the positions in the <code>Column</code> from the index specified
    * by the start parameter to the index equaling start + length.
    *
    * @param start  Position at which to start removing.
    * @param length Number of positions to remove.
    */
   public void removeRows(int start, int length) {
      int[] toRemove = new int[length];
      int idx = start;

      for (int i = 0; i < length; i++) {
         toRemove[i] = idx;
         idx++;
      }

      removeRowsByIndex(toRemove);
   }

   /**
    * Given an array of booleans, will remove the positions in the Column which
    * correspond to the positions in the boolean array which are marked true. If
    * the boolean array and Column do not have the same number of elements, the
    * remaining elements will be discarded.
    *
    * @param flags The boolean array of remove flags
    */
   public void removeRowsByFlag(boolean[] flags) {

      // keep a list of the row indices to remove
      LinkedList ll = new LinkedList();
      int i = 0;

      for (; i < flags.length; i++) {

         if (flags[i]) {
            ll.add(new Integer(i));
         }
      }

      for (; i < getNumRows(); i++) {
         ll.add(new Integer(i));
      }

      int[] toRemove = new int[ll.size()];
      int j = 0;
      Iterator iter = ll.iterator();

      while (iter.hasNext()) {
         Integer in = (Integer) iter.next();
         toRemove[j] = in.intValue();
         j++;
      }

      removeRowsByIndex(toRemove);
   } // end method removeRowsByFlag

   /**
    * Sets the comment associated with this <code>Column</code>.
    *
    * @param cmt Comment which associated this <code>Column</code>.
    */
   public void setComment(String cmt) { comment = cmt; }

   /**
    * Sets whether or not the <code>Column</code> is nominal.
    *
    * @param value Whether or not the <code>Column</code> is nominal.
    */
   public void setIsNominal(boolean value) {
      isNominal = value;
      isScalar = !value;
   }

   /**
    * Sets whether or not the <code>Column</code> is scalar.
    *
    * @param value Whether or not the <code>Column</code> is scalar.
    */
   public void setIsScalar(boolean value) {
      isScalar = value;
      isNominal = !value;
   }

   /**
    * Set the label associated with this <code>Column</code>.
    *
    * @param labl Label which describes this <code>Column</code>.
    */
   public void setLabel(String labl) { label = labl; }

} // end class AbstractColumn
