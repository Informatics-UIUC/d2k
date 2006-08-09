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
package ncsa.d2k.modules.core.datatype.table.sparse.columns;

import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.TextualColumn;
import ncsa.d2k.modules.core.datatype.table.sparse.SparseDefaultValues;


/**
 * SparseBooleanColumn is a column in a sparse table that holds data of type
 * byte array.
 *
 * @author  suvalala
 * @author  searsmith
 * @author  goren
 * @version $Revision$, $Date$
 */
public class SparseByteArrayColumn extends SparseObjectColumn
   implements TextualColumn {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static private final long serialVersionUID = 1L;

   //~ Constructors ************************************************************

   /**
    * Creates a new <code>SparseByteArrayColumn</code> with capacity zero and a
    * default load factor.
    */
   public SparseByteArrayColumn() { this(0); }

   /**
    * This costructor is used mainly when utilizing <code>
    * SparseObjectColumn</code> methods because a <code>
    * SparseByteArrayColumn</code> is the same as a <code>
    * SparseObjectColumn</code> as far as data storing, this sub calss uses its
    * methods for duplicating, reordering etc.
    *
    * @param column <code>SparseObjectColumn</code> instance
    */
   protected SparseByteArrayColumn(SparseObjectColumn column) {
      copy(column);
      type = ColumnTypes.BYTE_ARRAY;
   }

   /**
    * Creates a new <code>SparseByteArrayColumn</code> with <code>
    * initialCapacity</code> capacity and a default load factor.
    *
    * @param initialCapacity Initial capacity of the column
    */
   public SparseByteArrayColumn(int initialCapacity) {
      super(initialCapacity);
      type = ColumnTypes.BYTE_ARRAY;
   }

   /**
    * Creates a new <code>SparseByteArrayColumn</code> with a capacity equal to
    * the size of <code>data</code> and a default load factor. The valid row
    * numbers will be zero through size of <code>data</code>. This is just to
    * make this sparse column compatible to the behavior of other regular
    * columns.
    *
    * @param data Data for the column
    */
   public SparseByteArrayColumn(byte[][] data) {
      super(data);
      type = ColumnTypes.BYTE_ARRAY;
   }

   /**
    * Each value data[i] is set to validRows[i]. If validRows is smaller than
    * data, the rest of the values in data are being inserted to the end of this
    * column
    *
    * @param data      Byte[] array that holds the values to be inserted into
    *                  this column
    * @param validRows The indices to be valid in this column
    */
   public SparseByteArrayColumn(byte[][] data, int[] validRows) {
      super(data, validRows);
      type = ColumnTypes.BYTE_ARRAY;
   }

   //~ Methods *****************************************************************

   /**
    * Constructs a byte array from <code>obj</code> and returns it: # If obj is
    * a byte array or null - returns it. # If obj is a Byte constructing a byte
    * array from it. # Otherwise: construct a String from obj, and return
    * String's call getBytes method.
    *
    * @param  obj Object to convert
    *
    * @return Byte array from <code>obj</code>
    */
   static public byte[] toByteArray(Object obj) {

      if (obj == null) {
         return SparseDefaultValues.getDefaultBytes();
      }

      if (obj instanceof byte[]) {
         return (byte[]) obj;
      }

      if (obj instanceof Byte) {
         byte[] retVal = new byte[1];
         retVal[0] = ((Byte) obj).byteValue();

         return retVal;
      }

      String str;

      if (obj instanceof char[]) {
         str = new String((char[]) obj);
      } else {
         str = obj.toString();
      }

      return str.getBytes();
   } // end method toByteArray

   /**
    * Adds the specified number of blank rows.
    *
    * @param number Number of rows to add.
    */
   public void addRows(int number) {

      // table is already sparse.  nothing to do.
   }

   /**
    * Performs a deep copy of this SparseByteArrayColumne returns an exact copy
    * of this SparseByteArrayColumn.
    *
    * <p>The super class copy method is called, which returns a <code>
    * SparseObjectColumn</code> object, then the suitable constructor is called
    * in order to return a sparse byte array column.</p>
    *
    * @return Column object which is actually a SparseByteArrayColumn, that
    *         holds the data this column has
    */
   public Column copy() {
      SparseByteArrayColumn retVal =
         new SparseByteArrayColumn((SparseObjectColumn) super.copy());

      return retVal;
   }

   /**
    * Returns the entry at <code>pos.</code>
    *
    * @param  pos Position of the entry in the column
    *
    * @return The entry at <code>pos</code>
    */
   public byte[] getBytes(int pos) { return (byte[]) elements.get(pos); }

   /**
    * Returns the internal representation of this column.
    *
    * @return The internal representation of this column.
    */
   public Object getInternal() {
      int max_index = -1;
      byte[][] internal = null;
      int[] keys = elements.keys();

      for (int i = 0; i < keys.length; i++) {

         if (keys[i] > max_index) {
            max_index = keys[i];
         }
      }

      internal = new byte[max_index + 1][];

      for (int i = 0; i < max_index + 1; i++) {
         internal[i] = SparseDefaultValues.getDefaultBytes();
      }

      for (int i = 0; i < keys.length; i++) {
         internal[keys[i]] = (byte[]) elements.get(keys[i]);
      }

      return internal;
   }

   /**
    * Returns a subset of this column with entried from rows indicated by <code>
    * indices</code>.
    *
    * @param  indices Row numbers to include in the returned subset.
    *
    * @return Subset of this column, including rows indicated by <code>
    *         indices</code>.
    */
   public Column getSubset(int[] indices) {
      SparseByteArrayColumn retVal = new SparseByteArrayColumn(indices.length);

      for (int i = 0; i < indices.length; i++) {

         if (elements.containsKey(indices[i])) {

            // XIAOLEI
            // retVal.setBytes(getBytes(indices[i]), indices[i]);
            retVal.setBytes(getBytes(indices[i]), i);
         }
      }

      super.getSubset(retVal, indices);

      return retVal;
   }

   /**
    * Returns a SparseByteArrayColumn that holds only the data from rows <code>
    * pos</code> through <code>pos+len.</code>
    *
    * @param  pos Row number which is the beginning of the subset
    * @param  len Number of consequetive rows after <code>pos</code> that are to
    *             be included in the subset.
    *
    * @return A SparseByteArrayColumn with the data from rows <code>pos</code>
    *         through <code>pos+len</code>
    */
   public Column getSubset(int pos, int len) {
      SparseByteArrayColumn subCol =
         new SparseByteArrayColumn((SparseObjectColumn) super.getSubset(pos,
                                                                        len));

      return subCol;
   }


   /**
    * Converts <code>newEntry</code> into a String and calls setString method,
    * to set the new value at row # <code>pos</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setBoolean(boolean newEntry, int pos) {
      setString(new Boolean(newEntry).toString(), pos);
   }

   /**
    * Converts newEntry to byte[] and stores the array at <code>pos.</code>
    *
    * @param newEntry The new item
    * @param pos      The position to store newEntry
    */
   public void setByte(byte newEntry, int pos) {
      byte[] b = new byte[1];
      b[0] = newEntry;
      setBytes(b, pos);
   }

   /**
    * Sets <code>newEntry</code> to row #<code>pos.</code>
    *
    * @param newEntry The new item
    * @param pos      The position to store newEntry
    */
   public void setBytes(byte[] newEntry, int pos) {
      elements.put(pos, newEntry);
   }

   /**
    * Set the entry at pos to be a byte array that holds <code>newEntry.</code>
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setChar(char newEntry, int pos) {
      char[] c = new char[1];
      c[0] = newEntry;
      setChars(c, pos);
   }

   /**
    * Stores newEntry as a byte[]. If the object is a char[] or byte[], the
    * appropriate method is called, otherwise setString() is called with
    * newEntry.toString()
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setObject(Object newEntry, int pos) {
      setBytes(toByteArray(newEntry), pos);
   }
} // end class SparseByteArrayColumn
