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
import ncsa.d2k.modules.core.datatype.table.NumericColumn;
import ncsa.d2k.modules.core.datatype.table.sparse.SparseDefaultValues;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.VHashMap;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash
          .VIntLongHashMap;

   import java.io.ByteArrayInputStream;
   import java.io.ByteArrayOutputStream;
   import java.io.ObjectInputStream;
   import java.io.ObjectOutputStream;


/**
 * SparseLongColumn is a column in a sparse table that holds data of type long.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class SparseLongColumn extends AbstractSparseColumn
   implements NumericColumn {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static private final long serialVersionUID = 1L;

   //~ Instance fields *********************************************************

   /** Values in this column. */
   private VIntLongHashMap elements;

   /** Max value in this column. */
   private long max;

   /** Min value in this column. */
   private long min;

   //~ Constructors ************************************************************

   /**
    * Creates a new <code>SparseLongColumn</code> instance with the capacity
    * zero and default load factor.
    */
   public SparseLongColumn() { this(0); }

   /**
    * Creates a new <code>SparseLongColumn</code> instance with a prime capacity
    * equal to or greater than <code>initialCapacity</code> and with the default
    * load factor.
    *
    * @param initialCapacity Initial capacity for the column
    */
   public SparseLongColumn(int initialCapacity) {
      super();

      if (initialCapacity == 0) {
         elements = new VIntLongHashMap();
      } else {
         elements = new VIntLongHashMap(initialCapacity);
      }

      setIsScalar(true);
      type = ColumnTypes.LONG;
   }

   /**
    * Creates a new <code>SparseLongColumn</code> instance that will hold the
    * data in the <code>data</code> array. The elements in <code>data</code> are
    * being stored in <code>elements</code> in rows 0 through the size of <code>
    * data</code>.
    *
    * <p>This is just to comply with regular column objects that have this
    * constructor. Because this is a sparse column it is unlikely to be used.
    * </p>
    *
    * @param data Data to populate the column with
    */
   public SparseLongColumn(long[] data) {
      this(data.length);

      for (int i = 0; i < data.length; i++) {
         elements.put(i, data[i]);
      }
   }

   /**
    * Each value data[i] is set to validRows[i]. If validRows is smaller than
    * data, the rest of the values in data are being inserted to the end of this
    * column
    *
    * @param data      A long array that holds the values to be inserted into
    *                  this column
    * @param validRows The indices to be valid in this column
    */
   public SparseLongColumn(long[] data, int[] validRows) {
      this(data.length);

      int i;

      for (i = 0; i < data.length && i < validRows.length; i++) {
         setLong(data[i], validRows[i]);
      }

      for (; i < data.length; i++) {
         elements.put(getNumRows(), data[i]);
      }
   }

   //~ Methods *****************************************************************

   /**
    * Converts obj into type long. If obj is null returns the Minimum Value of
    * class Long.
    *
    * @param  obj Object to be converted into type long
    *
    * @return Long representation of the data held by <code>obj</code>. If obj
    *         is null returns a value signifying the position is empty, as
    *         defined by this class. If obj is a Number return its long value.
    *         If obj is a Character return it char value casted into long. If
    *         obj is a Boolean return 1 if obj=true else return 0. Otherwise:
    *         construct a String from obj and attempt to parse a long from it.
    */
   static public long toLong(Object obj) {

      if (obj == null) {
         return (long) SparseDefaultValues.getDefaultInt();
      }

      if (obj instanceof Number) {
         return ((Number) obj).longValue();
      }

      if (obj instanceof Character) {
         return (long) ((Character) obj).charValue();
      }

      if (obj instanceof Boolean) {
         return ((Boolean) obj).booleanValue() ? 1 : 0;
      }

      String str;

      if (obj instanceof char[]) {
         str = new String((char[]) obj);
      } else if (obj instanceof byte[]) {
         str = new String((byte[]) obj);
      } else { // obj is a String or an unknown object
         str = obj.toString();
      }

      float f = Float.parseFloat(str);

      return (long) f;
   } // end method toLong

   /**
    * Compares 2 values and Retruns an int representation of the relation
    * between the values.
    *
    * @param  val_1 First value to be compared
    * @param  val_2 Second value to be compared
    *
    * @return Result of the comparison (-1,0, or 1)
    */
   private int compareLongs(long val_1, long val_2) {

      if (val_1 > val_2) {
         return 1;
      }

      if (val_1 < val_2) {
         return -1;
      }

      return 0;
   }


   /**
    * Initializes the min and max of this LongColumn.
    */
   private void initRange() {
      max = Long.MIN_VALUE;
      min = Long.MAX_VALUE;

      for (int i = 1; i < getNumRows(); i++) {

         if (!isValueMissing(i) && !isValueEmpty(i)) {

            if (getLong(i) > max) {
               max = getLong(i);
            }

            if (getLong(i) < min) {
               min = getLong(i);
            }
         }
      }
   }

   /**
    * Returns a reference to the data in this column.
    *
    * @return Map that holds the data of this column (VIntByteHashMap).
    */
   protected VHashMap getElements() { return elements; }

   /**
    * Returns the valid values in rows <code>begin</code> through <code>
    * end</code>.
    *
    * @param  begin Row number from to begin retrieving of values
    * @param  end   Last row number in the section from which values are
    *               retrieved
    *
    * @return Valid values from rows no. <code>begin</code> through <code>
    *         end</code>, sorted
    */
   protected long[] getValuesInRange(int begin, int end) {

      if (end < begin) {
         long[] retVal = {};

         return retVal;
      }

      return elements.getValuesInRange(begin, end);
   }

   /**
    * Inserts <code>val</code>into row #<code>pos</code>. If this position
    * already holds data - insert the old data into row #<code>pos+1</code>
    * recursively.
    *
    * @param val New boolean value to be inserted at pos
    * @param pos Row number to insert val
    */
   protected void insertRow(long val, int pos) {
      boolean valid = elements.containsKey(pos);
      long removedValue = elements.remove(pos);

      // putting the new value
      setLong(val, pos);

      // recursively moving the items in the column as needed
      if (valid) {
         insertRow(removedValue, pos + 1);
      }
   }

   /**
    * Replaces the current map of elements with the supplied map.
    *
    * @param map New elements
    */
   protected void setElements(VHashMap map) {
      elements = (VIntLongHashMap) map;
   }

   /**
    * Add the specified number of blank rows.
    *
    * @param number Number of rows to add
    */
   public void addRows(int number) {
      // table is already sparse.  nothing to do.
   }

   /**
    * Compares the value represented by element and the one of row number <code>
    * pos</code>. <code>elements</code> will be converted to a compatible type
    * to this column. If element > pos returns 1. If element < pos returns -1.
    * If the are equal returns 0. If one of the representation does not hold a
    * value, it is considered smaller than the other.
    *
    * @param  obj Object to compare
    * @param  pos Position of element to compare
    *
    * @return Result of comparison (-1, 0, or 1)
    */
   public int compareRows(Object obj, int pos) {
      int val = validate(obj, pos);

      if (val <= 1) {
         return val;
      } else {
         long val_1 = toLong(obj);
         long val_2 = elements.get(pos);

         return compareLongs(val_1, val_2);
      }
   }

   /**
    * Compares 2 values that are in this column. Returns an int representation
    * of the relation between the values.
    *
    * @param  pos1 Row number of the first value to be compared
    * @param  pos2 Row number of the second value to be compared
    *
    * @return Result of comparison (-1, 0, or 1)
    */
   public int compareRows(int pos1, int pos2) {
      int val = validate(pos1, pos2);

      if (val <= 1) {
         return val;
      } else {
         long val_1 = elements.get(pos1);
         long val_2 = elements.get(pos2);

         return compareLongs(val_1, val_2);
      }
   }

   /**
    * Performs a deep copy of this SparseIntColumn.
    *
    * @return Deep copy of this SparseIntColumn
    */
   public Column copy() {
      SparseLongColumn retVal;

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(this);

         byte[] buf = baos.toByteArray();
         oos.close();

         ByteArrayInputStream bais = new ByteArrayInputStream(buf);
         ObjectInputStream ois = new ObjectInputStream(bais);
         retVal = (SparseLongColumn) ois.readObject();
         ois.close();

         return retVal;
      } catch (Exception e) {
         retVal = new SparseLongColumn();
         retVal.elements = elements.copy();
         retVal.copy(this);

         return retVal;
      }
   }

   /**
    * Returns the value at row # row, cast to type boolean.
    *
    * @param  row The row number
    *
    * @return False if the value at row # row equals zero, true otherwise. If no
    *         such value exists returns false.
    */
   public boolean getBoolean(int row) {

      if (!elements.containsKey(row)) {
         return SparseDefaultValues.getDefaultBoolean();
      }

      return (getLong(row) != 0);
   }

   /**
    * Returns the value at row # row, cast to type byte.
    *
    * @param  row The row number
    *
    * @return The value at row cast to byte. If no such value exists returns a
    *         value signifying the position is empty, as defined by
    *         SparseByteColumn
    */
   public byte getByte(int row) {

      if (!elements.containsKey(row)) {
         return SparseDefaultValues.getDefaultByte();
      }

      return (byte) getLong(row);
   }

   /**
    * Returns the value at row # row converted to a bytes array.
    *
    * @param  row The row number
    *
    * @return The value in row # row represented with a bytes array. If no such
    *         value exists returns null.
    */
   public byte[] getBytes(int row) {

      if (!elements.containsKey(row)) {
         return SparseDefaultValues.getDefaultBytes();
      }

      return String.valueOf(getLong(row)).getBytes();
   }

   /**
    * Returns the value at row # row cast to char type.
    *
    * @param  row The row number
    *
    * @return The value at row # row cast to char. If no such value exists
    *         return a value signifying the position is empty, as defined by
    *         SparseCharColumn.
    */
   public char getChar(int row) {

      if (!elements.containsKey(row)) {
         return SparseDefaultValues.getDefaultChar();
      }

      return (char) getInt(row);
   }

   /**
    * Returns the value at row # row, ina chars array.
    *
    * @param  row The row number
    *
    * @return The value at row # row represented with a chars array. If no such
    *         value exists returns null.
    */
   public char[] getChars(int row) {

      if (!elements.containsKey(row)) {
         return SparseDefaultValues.getDefaultChars();
      }

      return Long.toString(getLong(row)).toCharArray();
   }

   /**
    * Returns the value at row # row cast to double.
    *
    * @param  row The row number
    *
    * @return The value at row # row cast to double. If no such value exists
    *         returns a value signifying the position is empty, as defined by
    *         SparseDoubleColumn.
    */
   public double getDouble(int row) {

      if (!elements.containsKey(row)) {
         return SparseDefaultValues.getDefaultDouble();
      }

      return (double) getLong(row);
   }

   /**
    * Returns the value at row # row cast to float type.
    *
    * @param  row The row number
    *
    * @return The value at row # row cast to float. If no such value exists
    *         returns a value signifying the position is empty, as defined by
    *         SparseFloatColumn.
    */
   public float getFloat(int row) {

      if (!elements.containsKey(row)) {
         return (float) SparseDefaultValues.getDefaultDouble();
      }

      return (float) getLong(row);
   }

   /**
    * Returns the value at row # row cast to int.
    *
    * @param  row The row number
    *
    * @return Value at row number row cast to int. If no such value exists
    *         returns a value signifying the position is empty, as defined by
    *         SparseIntColumn.
    */
   public int getInt(int row) {

      if (!elements.containsKey(row)) {
         return SparseDefaultValues.getDefaultInt();
      }

      return (int) getLong(row);
   }

   /**
    * Returns the internal representation of this column.
    *
    * @return Internal representation of this column.
    */
   public Object getInternal() {
      int max_index = -1;
      long[] internal = null;
      int[] keys = elements.keys();

      for (int i = 0; i < keys.length; i++) {

         if (keys[i] > max_index) {
            max_index = keys[i];
         }
      }

      internal = new long[max_index + 1];

      for (int i = 0; i < max_index + 1; i++) {
         internal[i] = (long) SparseDefaultValues.getDefaultInt();
      }

      for (int i = 0; i < keys.length; i++) {
         internal[keys[i]] = elements.get(keys[i]);
      }

      return internal;
   }

   /**
    * Returns the value at row # row.
    *
    * @param  row The row number
    *
    * @return The value at row # row. If no such value exists returns a value
    *         signifying the position is empty, as defined by this class.
    */
   public long getLong(int row) {

      if (elements.containsKey(row)) {
         return elements.get(row);
      } else {
         return (long) SparseDefaultValues.getDefaultInt();
      }
   }

   /**
    * Get the maximum value contained in this Column.
    *
    * @return Maximum value of this Column
    */
   public double getMax() {
      initRange();

      return (double) max;
   }

   /**
    * Get the minimum value contained in this Column.
    *
    * @return Minimum value of this Column
    */
   public double getMin() {
      initRange();

      return (double) min;
   }

   /**
    * Returns the value at row # row, encapsulated in a Long object.
    *
    * @param  row The row number
    *
    * @return Long object encapsulating the value at row # row
    */
   public Object getObject(int row) {

      if (elements.containsKey(row)) {
         return new Long(elements.get(row));
      } else {
         return new Long((long) SparseDefaultValues.getDefaultInt());
      }
   }

   /**
    * Returns the value at row # row, cast to type short.
    *
    * @param  row The row number
    *
    * @return The value at row # row cast to short. If no such value exists
    *         returns a value signifying the position is empty, as defined by
    *         SparseShortColumn.
    */
   public short getShort(int row) {

      if (!elements.containsKey(row)) {
         return (short) SparseDefaultValues.getDefaultInt();
      }

      return (short) getLong(row);
   }

   /**
    * Returns the value at row # row, represented as a String.
    *
    * @param  row The row number
    *
    * @return String Object representing the value at row # row. If no such
    *         value exists returns null.
    */
   public String getString(int row) {

      if (!elements.containsKey(row)) {
         return "" + (long) SparseDefaultValues.getDefaultInt();
      }

      return String.valueOf(getLong(row));
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
      SparseLongColumn retVal = new SparseLongColumn(indices.length);

      for (int i = 0; i < indices.length; i++) {

         if (elements.containsKey(indices[i])) {
            retVal.setLong(getLong(indices[i]), i);
         }
      }

      super.getSubset(retVal, indices);

      return retVal;
   }

   /**
    * Returns a SparseLongColumn that holds only the data from rows <code>
    * pos</code> through <code>pos+len.</code>
    *
    * @param  pos The row number which is the beginning of the subset
    * @param  len Number of consequetive rows after <code>pos</code> that are to
    *             be included in the subset.
    *
    * @return SparseLongColumn with the data from rows <code>pos</code> through
    *         <code>pos+len</code>
    */
   public Column getSubset(int pos, int len) {
      SparseLongColumn subCol = new SparseLongColumn();
      subCol.elements = (VIntLongHashMap) elements.getSubset(pos, len);
      getSubset(subCol, pos, len);

      return subCol;
   }

   /**
    * Removes the byte value in row # <code>pos</code> and returns it
    * encapsulated in a Long object.
    *
    * @param  pos Description of parameter pos.
    *
    * @return Removes the byte value in row # <code>pos</code> and returns it
    *         encapsulated in a Long object.
    */
   public Object removeRow(int pos) {

      if (elements.containsKey(pos)) {
         return new Long(elements.remove(pos));
      } else {
         return null;
      }
   }

   /**
    * Set the value at pos to 1 if newEntry is true, otherwise set it to 0.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setBoolean(boolean newEntry, int pos) {

      if (newEntry) {
         setLong((long) 1, pos);
      } else {
         setLong((long) 0, pos);
      }
   }

   /**
    * Casts newEntry to long an sets its value at pos.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setByte(byte newEntry, int pos) {
      setLong((long) newEntry, pos);
   }

   /**
    * Converts <code>newEntry</code> into a String, then sets the long value
    * represented by it at row #<code>pos</code>.
    *
    * @param newEntry The new value
    * @param pos      The position
    */
   public void setBytes(byte[] newEntry, int pos) {
      setString(new String(newEntry), pos);
   }

   /**
    * Convert newEntry to a char array and call setChars().
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setChar(char newEntry, int pos) {
      setLong((long) newEntry, pos);
   }

   /**
    * Convert newEntry to a String and call setString().
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setChars(char[] newEntry, int pos) {
      setString(new String(newEntry), pos);
   }

   /**
    * Casts newEntry to long an sets its value at pos.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setDouble(double newEntry, int pos) {
      setLong((long) newEntry, pos);
   }

   /**
    * Casts newEntry to long an sets its value at pos.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setFloat(float newEntry, int pos) {
      setLong((long) newEntry, pos);
   }

   /**
    * Casts newEntry to long an sets its value at pos.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setInt(int newEntry, int pos) { setLong((long) newEntry, pos); }

   /**
    * Sets the value at pos to newEntry.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setLong(long newEntry, int pos) { elements.put(pos, newEntry); }

   /**
    * If newEntry is a Number, get its long value, otherwise call setString() on
    * newEntry.toString().
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setObject(Object newEntry, int pos) {
      long l = toLong(newEntry);
      setLong(l, pos);
   }

   /**
    * Casts newEntry to long an sets its value at pos.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setShort(short newEntry, int pos) {
      setLong((long) newEntry, pos);
   }

   /**
    * Converts newEntry to a Long and inserts the long value.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setString(String newEntry, int pos) {
      setLong((long) (Double.parseDouble(newEntry)), pos);
   }

   /**
    * Swaps the values between 2 rows. If there is no data in row
    * #<code>pos1</code> then nothing is stored in row #<ocde>pos2, and vice
    * versa.
    *
    * @param pos1 Row number of first item to be swaped
    * @param pos2 Row number of second item to be swaped
    */
   public void swapRows(int pos1, int pos2) {

      if (pos1 == pos2) {
         return;
      }

      boolean valid_1 = elements.containsKey(pos1);
      boolean valid_2 = elements.containsKey(pos2);
      long val1 = elements.remove(pos1);
      long val2 = elements.remove(pos2);

      if (valid_1) {
         setLong(val1, pos2);
      }

      if (valid_2) {
         setLong(val2, pos1);
      }

      missing.swapRows(pos1, pos2);
      empty.swapRows(pos1, pos2);
   }
} // end class SparseLongColumn
