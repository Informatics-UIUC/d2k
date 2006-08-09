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
 * SparseCharArrayColumn is a type of a SparseObjectColumn. The data (of type
 * char[]) is stored in an int to Object hash map.
 *
 * @author  suvalala
 * @author  searsmith
 * @author  goren
 * @version $Revision$, $Date$
 */
public class SparseCharArrayColumn extends SparseObjectColumn
   implements TextualColumn {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 1L;

   //~ Constructors ************************************************************

   /**
    * Creates a new <code>SparseCharArrayColumn</code> with capacity zero and a
    * default load factor.
    */
   public SparseCharArrayColumn() { this(0); }

   /**
    * This constructor is used mainly when utilizing <code>
    * SparseObjectColumn</code> methods. Because a <code>
    * SparseCharArrayColumn</code> is the same as a <code>
    * SparseObjectColumn</code> as far as class variables, this subclass uses
    * its methods for duplicating, reordering etc.
    *
    * @param column <code>SparseObjectColumn</code> to base this <code>
    *               SparseCharArrayColumn</code> on
    */
   protected SparseCharArrayColumn(SparseObjectColumn column) {
      type = ColumnTypes.CHAR_ARRAY;
      copy(column);
   }

   /**
    * Creates a new <code>SparseCharArrayColumn</code> with <code>
    * initialCapacity</code> capacity and a default load factor.
    *
    * @param initialCapacity Initial capacity
    */
   public SparseCharArrayColumn(int initialCapacity) {
      super(initialCapacity);
      type = ColumnTypes.CHAR_ARRAY;
   }

   /**
    * Creates a new <code>SparseCharArrayColumn</code> with a capacity equals to
    * the size of <code>data</code> and a default load factor. The valid row
    * numbers will be zero through size of <code>data</code>. This is just to
    * make this sparse column compatible to the behavior of other regular
    * columns.
    *
    * @param data Data to populate this column with
    */
   public SparseCharArrayColumn(char[][] data) {
      super(data);
      type = ColumnTypes.CHAR_ARRAY;
   }

   /**
    * Each value data[i] is set to validRows[i]. If validRows is smaller than
    * data, the rest of the values in data are being inserted to the end of this
    * column
    *
    * @param data      A char[] array that holds the values to be inserted into
    *                  this column
    * @param validRows The indices to be valid in this column
    */
   public SparseCharArrayColumn(char[][] data, int[] validRows) {
      super(data, validRows);
      type = ColumnTypes.CHAR_ARRAY;
   }

   //~ Methods *****************************************************************

   /**
    * Constructs a char array from <code>obj</code> and returns it.
    *
    * @param  obj Object to convert
    *
    * @return Char array from <code>obj</code>
    */
   static public char[] toCharArray(Object obj) {

      if (obj == null) {
         return SparseDefaultValues.getDefaultChars();
      }

      if (obj instanceof char[]) {
         return (char[]) obj;
      }

      String str;

      if (obj instanceof byte[]) {
         str = new String((byte[]) obj);
         // covers cases of Boolean, Character, Number, String.
      } else {
         str = obj.toString();
      }

      return str.toCharArray();
   }

   /**
    * Adds the specified number of blank rows.
    *
    * @param number Number of rows to add.
    */
   public void addRows(int number) {

      // table is already sparse.  nothing to do.
   }

   /**
    * Performs a deep copy of this SparseCharArrayColumn returns an exact copy
    * of this SparseCharArrayColumn.
    *
    * <p>The superclass copy method is called, which returns a <code>
    * SparseObjectColumn</code> object, then the suitable constructor is called
    * in order to return a <code>SparseCharArrayColumn</code>.</p>
    *
    * @return Column object which is actually a SparseCharArrayColumn, that
    *         holds the data this column has
    */
   public Column copy() {
      SparseCharArrayColumn retVal =
         new SparseCharArrayColumn((SparseObjectColumn) super.copy());

      return retVal;
   }

   /**
    * Returns the entry at <code>pos</code>.
    *
    * @param  pos The index
    *
    * @return Entry at pos
    */
   public char[] getChars(int pos) { return (char[]) elements.get(pos); }

   /**
    * Returns the internal representation of this column.
    *
    * @return Internal representation of this column.
    */
   public Object getInternal() {
      int max_index = -1;
      char[][] internal = null;
      int[] keys = elements.keys();

      for (int i = 0; i < keys.length; i++) {

         if (keys[i] > max_index) {
            max_index = keys[i];
         }
      }

      internal = new char[max_index + 1][];

      for (int i = 0; i < max_index + 1; i++) {
         internal[i] = SparseDefaultValues.getDefaultChars();
      }

      for (int i = 0; i < keys.length; i++) {
         internal[keys[i]] = (char[]) elements.get(keys[i]);
      }

      return internal;
   }

   /**
    * Returns a subset of this column populated from rows indicated by <code>
    * indices</code>.
    *
    * @param  indices Row numbers to include in the returned subset.
    *
    * @return Subset of this column, including rows indicated by <code>
    *         indices</code>.
    */
   public Column getSubset(int[] indices) {
      SparseCharArrayColumn retVal = new SparseCharArrayColumn(indices.length);

      for (int i = 0; i < indices.length; i++) {

         if (elements.containsKey(indices[i])) {
            retVal.setChars(getChars(indices[i]), i);
         }
      }

      super.getSubset(retVal, indices);

      return retVal;
   }

   /**
    * Returns a SparseCharArrayColumn that holds only the data from rows <code>
    * pos</code> through <code>pos+len.</code>
    *
    * @param  pos Row number which is the beginning of the subset
    * @param  len Number of consequetive rows after <code>pos</code> that are to
    *             be included in the subset.
    *
    * @return SparseCharArrayColumn with the data from rows <code>pos</code>
    *         through <code>pos+len</code>
    */
   public Column getSubset(int pos, int len) {
      SparseCharArrayColumn subCol =
         new SparseCharArrayColumn((SparseObjectColumn) super.getSubset(pos,
                                                                        len));

      return subCol;
   }

   /**
    * Calls the toString() method on newEntry to get a String and stores the
    * String as a char[].
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setBoolean(boolean newEntry, int pos) {
      setChars(new Boolean(newEntry).toString().toCharArray(), pos);
   }

   /**
    * Converts newEntry to char[].
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setByte(byte newEntry, int pos) {
      setString(new Byte(newEntry).toString(), pos);
   }

   /**
    * Converts newEntry to char[] by calling ByteUtils.toChars().
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setBytes(byte[] newEntry, int pos) {
      setString(new String(newEntry), pos);
   }

   /**
    * Sets the entry at pos to be a char array that holds <code>newEntry.</code>
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
    * Sets the entry at pos to be newEntry.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setChars(char[] newEntry, int pos) {
      elements.put(pos, newEntry);
   }

   /**
    * Converts newEntry to a String and stores it as a char[].
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setDouble(double newEntry, int pos) {
      setChars(Double.toString(newEntry).toCharArray(), pos);
   }

   /**
    * Converts newEntry to a String and stores it as a char[].
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setFloat(float newEntry, int pos) {
      setChars(Float.toString(newEntry).toCharArray(), pos);
   }

   /**
    * Converts newEntry to a String and stores it as a char[].
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setInt(int newEntry, int pos) {
      setChars(Integer.toString(newEntry).toCharArray(), pos);
   }

   /**
    * Converts newEntry to a String and stores it as a char[].
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setLong(long newEntry, int pos) {
      setChars(Long.toString(newEntry).toCharArray(), pos);
   }

   /**
    * Stores newEntry as a char[]. If the object is a char[] or byte[], the
    * appropriate method is called, otherwise setString() is called with
    * newEntry.toString()
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setObject(Object newEntry, int pos) {
      setChars(toCharArray(newEntry), pos);
   }

   /**
    * Sets the entry at the given position to newEntry.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setRow(Object newEntry, int pos) { setObject(newEntry, pos); }

   /**
    * Converts newEntry to a String and stores it as a char[].
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setShort(short newEntry, int pos) {
      setChars(Short.toString(newEntry).toCharArray(), pos);
   }

   /**
    * Stores newEntry in this column at pos as a char[].
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setString(String newEntry, int pos) {
      setChars(newEntry.toCharArray(), pos);
   }
} // end class SparseCharArrayColumn
