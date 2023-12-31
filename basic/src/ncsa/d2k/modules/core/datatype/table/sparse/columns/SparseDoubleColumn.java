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
          .VIntDoubleHashMap;

   import java.io.ByteArrayInputStream;
   import java.io.ByteArrayOutputStream;
   import java.io.ObjectInputStream;
   import java.io.ObjectOutputStream;


/**
 * SparseDoubleColumn is a column in a sparse table that holds data of type
 * double.
 *
 * @author  suvalala
 * @author  searsmith
 * @author  goren
 * @version $Revision$, $Date$
 */
public class SparseDoubleColumn extends AbstractSparseColumn
   implements NumericColumn {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 1L;

   //~ Instance fields *********************************************************

   /** Elements in this column. */
   private VIntDoubleHashMap elements;

   /** Max value in this column. */
   private double max;

   /** Min value in this column. */
   private double min;

   //~ Constructors ************************************************************

   /**
    * Creates a new <code>SparseDoubleColumn</code> instance with the default
    * capacity and load factor.
    */
   public SparseDoubleColumn() { this(0); }

   /**
    * Creates a new <code>SparseDoubleColumn</code> instance with a prime
    * capacity equal to or greater than <code>initialCapacity</code> and with
    * the default load factor.
    *
    * @param initialCapacity Initial capacity for the column
    */
   public SparseDoubleColumn(int initialCapacity) {
      super();

      if (initialCapacity == 0) {
         elements = new VIntDoubleHashMap();
      } else {
         elements = new VIntDoubleHashMap(initialCapacity);
      }

      type = ColumnTypes.DOUBLE;
      setIsScalar(true);
   }

   /**
    * Creates a new <code>SparseDoubleColumn</code> instance that will hold the
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
   public SparseDoubleColumn(double[] data) {
      this(data.length);

      for (int i = 0; i < data.length; i++) {
         setDouble(data[i], i);
      }
   }

   /**
    * Each value data[i] is set to validRows[i]. If validRows is smaller than
    * data, the rest of the values in data are being inserted to the end of this
    * column
    *
    * @param data      A double array that holds the values to be inserted into
    *                  this column
    * @param validRows The indices to be valid in this column
    */
   public SparseDoubleColumn(double[] data, int[] validRows) {
      this(data.length);

      int i;

      for (i = 0; i < data.length && i < validRows.length; i++) {
         setDouble(data[i], validRows[i]);
      }

      for (; i < data.length; i++) {
         elements.put(getNumRows(), data[i]);
      }
   }

   //~ Methods *****************************************************************

   /**
    * Compares 2 values and Retruns an int representation of the relation
    * between the values.
    *
    * @param  val_1 First value to be compared
    * @param  val_2 Second value to be compared
    *
    * @return The relation between the values
    */
   static public int compareDoubles(double val_1, double val_2) {

      if (val_1 > val_2) {
         return 1;
      }

      if (val_1 < val_2) {
         return -1;
      }

      return 0;
   }

   /**
    * Converts obj into type double. If obj is null returns the negative
    * infinity of class Double.
    *
    * @param  obj Object to be converted into type double
    *
    * @return A double representation of the data held by <code>obj</code>. If
    *         obj is null returns a value signifying the position is empty, as
    *         defined by this class. If obj is a Number return its double value.
    *         If obj is a Character return it char value casted into double. If
    *         obj is a Boolean return 1 if obj=true else return 0. Otherwise:
    *         construct a String from obj and attempt to parse a double from it.
    */
   static public double toDouble(Object obj) {

      if (obj == null) {
         return SparseDefaultValues.getDefaultDouble();
      }

      if (obj instanceof Number) {
         return ((Number) obj).doubleValue();
      }

      if (obj instanceof Character) {
         return (double) ((Character) obj).charValue();
      }

      if (obj instanceof Boolean) {
         return ((Boolean) obj).booleanValue() ? (double) 1 : (double) 0;
      }

      String str;

      if (obj instanceof char[]) {
         str = new String((char[]) obj);
      } else if (obj instanceof byte[]) {
         str = new String((byte[]) obj);
      } else {
         str = obj.toString();
      }

      return Double.parseDouble(str);
   } // end method toDouble

   /**
    * Returns the internal map that stores the elements.
    *
    * @return Column elements in a map
    */
   protected VHashMap getElements() { return elements; }

   /**
    * Returns the valid values in rows <codE>begin</code> through <codE>end.
    * </code>
    *
    * @param  begin Row number from to begin retrieving of values
    * @param  end   Last row number in the section from which values are
    *               retrieved.
    *
    * @return Valid values from rows no. <code>begin</code> through <codE>
    *         end</code>, sorted.
    */
   protected double[] getValuesInRange(int begin, int end) {

      if (end < begin) {
         double[] retVal = {};

         return retVal;
      }

      return elements.getValuesInRange(begin, end);
   }


   /**
    * Initializes the <code>min</code> and <code>max</code> of this
    * SparseDoubleColumn.
    */
   protected void initRange() {
      max = Double.MIN_VALUE;
      min = Double.MAX_VALUE;

      for (int i = 1; i < getNumRows(); i++) {

         if (!isValueMissing(i) && !isValueEmpty(i)) {

            if (getDouble(i) > max) {
               max = getDouble(i);
            }

            if (getDouble(i) < min) {
               min = getDouble(i);
            }
         }
      }
   }

   /**
    * Sets the elements.
    *
    * @param map Elements to set
    */
   protected void setElements(VHashMap map) {
      elements = (VIntDoubleHashMap) map;
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
    * Compares the value represented by element and the one of row number <code>
    * pos</code>. <code>elements</code> will be converted to a compatible type
    * to this column. If element > pos returns 1. if element < pos retruns -1.
    * if the are equal returns 0. If one of the representation does not hold a
    * value, it is considered smaller than the other.
    *
    * @param  obj Object for comparison
    * @param  pos Row element to compare
    *
    * @return Result of comparison (-1,0,1)
    */
   public int compareRows(Object obj, int pos) {
      int val = validate(obj, pos);

      if (val <= 1) {
         return val;
      } else {
         double val_1 = toDouble(obj);
         double val_2 = elements.get(pos);

         return compareDoubles(val_1, val_2);
      }
   }

   /**
    * Compares 2 values that are in this column. Retruns an int representation
    * of the relation between the values.
    *
    * @param  pos1 First value to be compared
    * @param  pos2 Second value to be compared
    *
    * @return Result of comparison (-1,0,1)
    */
   public int compareRows(int pos1, int pos2) {
      int val = validate(pos1, pos2);

      if (val <= 1) {
         return val;
      } else {
         double val_1 = elements.get(pos1);
         double val_2 = elements.get(pos2);

         return compareDoubles(val_1, val_2);
      }
   }

   /**
    * Performs a deep copy of this <code>SparseDoubleColumn</code> returns an
    * exact copy of this SparseDoubleColumn.
    *
    * @return Copy of this <code>SparseDoubleColumn</code>
    */
   public Column copy() {
      SparseDoubleColumn retVal;

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(this);

         byte[] buf = baos.toByteArray();
         oos.close();

         ByteArrayInputStream bais = new ByteArrayInputStream(buf);
         ObjectInputStream ois = new ObjectInputStream(bais);
         retVal = (SparseDoubleColumn) ois.readObject();
         ois.close();

         return retVal;
      } catch (Exception e) {
         retVal = new SparseDoubleColumn();
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

      return (getDouble(row) != 0);
   }

   /**
    * Returns the value at row # row, cast to type byte.
    *
    * @param  row The row number
    *
    * @return Value at row cast to byte. If no such value exists returns a value
    *         signifying the position is empty, as defined by SparseByteColumn
    */
   public byte getByte(int row) {

      if (!elements.containsKey(row)) {
         return SparseDefaultValues.getDefaultByte();
      }

      return (byte) getDouble(row);
   }

   /**
    * Returns the value at row # row converted to a bytes array.
    *
    * @param  row The row number
    *
    * @return Value in row # row represented with a bytes array. If no such
    *         value exists returns null.
    */
   public byte[] getBytes(int row) {

      if (!elements.containsKey(row)) {
         return (byte[]) SparseDefaultValues.getDefaultBytes();
      }

      return String.valueOf(getDouble(row)).getBytes();
   }

   /**
    * Returns the value at row # row casted to char type.
    *
    * @param  row The row number
    *
    * @return Value at row # row cast to char. If no such value exists returns a
    *         value signifying the position is empty, as defined by
    *         SparseCharColumn.
    */
   public char getChar(int row) {

      if (!elements.containsKey(row)) {
         return SparseDefaultValues.getDefaultChar();
      }

      return (char) getDouble(row);
   }

   /**
    * Returns the value at row # row, in a char array.
    *
    * @param  row The row number
    *
    * @return Value at row # row represented with a chars array. If no such
    *         value exists returns null.
    */
   public char[] getChars(int row) {

      if (!elements.containsKey(row)) {
         return (char[]) SparseDefaultValues.getDefaultChars();
      }

      return Double.toString(getDouble(row)).toCharArray();
   }

   /**
    * Returns the value at row # row.
    *
    * @param  row The row number
    *
    * @return the double value at row # row
    */
   public double getDouble(int row) {

      if (elements.containsKey(row)) {
         return elements.get(row);
      } else {
         return SparseDefaultValues.getDefaultDouble();
      }
   }

   /**
    * Returns the value at row # row, cast to type float.
    *
    * @param  row The row number
    *
    * @return The value at row # row casted to float. If no such value exists
    *         returns a value signifying the position is empty, as defined by
    *         SparseFloatColumn.
    */
   public float getFloat(int row) {

      if (!elements.containsKey(row)) {
         return (float) SparseDefaultValues.getDefaultDouble();
      }

      return (float) getDouble(row);
   }

   /**
    * Returns the value at row # row cast to int.
    *
    * @param  row The row number
    *
    * @return Value at row number row casted to int. If no such value exist
    *         returns a value signifying the position is empty, as defined by
    *         SparseIntColumn.
    */
   public int getInt(int row) {

      if (!elements.containsKey(row)) {
         return SparseDefaultValues.getDefaultInt();
      }

      return (int) getDouble(row);
   }

   /**
    * Returns the internal representation of this column.
    *
    * @return Internal representation of this column.
    */
   public Object getInternal() {
      int max_index = -1;
      double[] internal = null;
      int[] keys = elements.keys();

      for (int i = 0; i < keys.length; i++) {

         if (keys[i] > max_index) {
            max_index = keys[i];
         }
      }

      internal = new double[max_index + 1];

      for (int i = 0; i < max_index + 1; i++) {
         internal[i] = SparseDefaultValues.getDefaultDouble();
      }

      for (int i = 0; i < keys.length; i++) {
         internal[keys[i]] = elements.get(keys[i]);
      }

      return internal;
   }

   /**
    * Returns the value at row # row, cast to type long.
    *
    * @param  row The row number
    *
    * @return Value at row # row casted to long. If no such value exist returns
    *         a value signifying the position is empty, as defined by
    *         SparseLongColumn.
    */
   public long getLong(int row) {

      if (!elements.containsKey(row)) {
         return (long) SparseDefaultValues.getDefaultInt();
      }

      return (long) getDouble(row);
   }

   /**
    * Gets the maximum value contained in this Column.
    *
    * @return Maximum value of this Column
    */
   public double getMax() {
      initRange();

      return max;
   }

   /**
    * Get the minimum value contained in this Column.
    *
    * @return Minimum value of this Column
    */
   public double getMin() {
      initRange();

      return min;
   }

   /**
    * Returns the value at row # row, encapsulated in a Double object.
    *
    * @param  row The row number
    *
    * @return Double object encapsulating the value at row # row
    */
   public Object getObject(int row) {

      if (elements.containsKey(row)) {
         return new Double(getDouble(row));
      } else {
         return new Double(SparseDefaultValues.getDefaultDouble());
      }
   }

   /**
    * Returns the value at row # row, cast to type short.
    *
    * @param  row The row number
    *
    * @return Value at row # row cast to short. If no such value exist returns a
    *         value signifying the position is empty, as defined by
    *         SparseShortColumn.
    */
   public short getShort(int row) {

      if (!elements.containsKey(row)) {
         return (short) SparseDefaultValues.getDefaultInt();
      }

      return (short) getDouble(row);
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
         return "" + SparseDefaultValues.getDefaultDouble();
      }

      return String.valueOf(getDouble(row));
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
      SparseDoubleColumn retVal = new SparseDoubleColumn(indices.length);

      for (int i = 0; i < indices.length; i++) {

         if (elements.containsKey(indices[i])) {
            retVal.setDouble(getDouble(indices[i]), i);
         }
      }

      super.getSubset(retVal, indices);

      return retVal;
   }

   /**
    * Returns a SparseDoubleColumn that holds only the data from rows <code>
    * pos</code> through <code>pos+len.</code>
    *
    * @param  pos Row number which is the beginning of the subset
    * @param  len Number of consequetive rows after <code>pos</code> that are to
    *             be included in the subset.
    *
    * @return SparseDoubleColumn with the data from rows <code>pos</code>
    *         through <code>pos+len</code>
    */
   public Column getSubset(int pos, int len) {
      SparseDoubleColumn subCol = new SparseDoubleColumn();
      subCol.elements = (VIntDoubleHashMap) elements.getSubset(pos, len);
      getSubset(subCol, pos, len);

      return subCol;
   }

   /**
    * Removes the byte value in row # <code>pos</code> and returns it
    * encapsulated in a Double object.
    *
    * <p>Also adjusts the indices for all rows beyond the one removed so that
    * they correctly reflect their new index position.</p>
    *
    * <p>Once again, an empty or missing value is not the same as a non-existent
    * one. Rows that are removed are rows that no longer exist.</p>
    *
    * @param  pos Position to remove row
    *
    * @return Removes the byte value in row # <code>pos</code> and returns it
    *         encapsulated in a Double object.
    *
    *         <p>Also adjusts the indices for all rows beyond the one removed so
    *         that they correctly reflect their new index position.</p>
    *
    *         <p>Once again, an empty or missing value is not the same as a
    *         non-existent one. Rows that are removed are rows that no longer
    *         exist.</p>
    */
   public Object removeRow(int pos) {

      if (elements.containsKey(pos)) {
         return new Double(elements.remove(pos));
      } else {
         return null;
      }
   }

   /**
    * Sets the value at pos to be 1.0 if newEntry is true, sets the value to 0.0
    * otherwise.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setBoolean(boolean newEntry, int pos) {

      if (newEntry) {
         setDouble(1.0, pos);
      } else {
         setDouble(0.0, pos);
      }
   }

   /**
    * Converts newEntry to a double and sets the ocnverted value at row
    * #<code>pos</code> the conversion to a double is done through converting to
    * String first.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setByte(byte newEntry, int pos) {

      /*byte[] arr = {newEntry};
       * setBytes(arr, pos);*/
      setDouble((double) newEntry, pos);
   }

   /**
    * Converts newEntry to a double and sets the ocnverted value at row
    * #<code>pos</code> the conversion to a double is done through converting to
    * String first.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setBytes(byte[] newEntry, int pos) {
      setString(new String(newEntry), pos);
   }

   /**
    * Converts newEntry to a char array and calls setChars().
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setChar(char newEntry, int pos) {
      setDouble((double) newEntry, pos);
   }

   /**
    * Converts newEntry to a String and calls setString().
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setChars(char[] newEntry, int pos) {
      setString(new String(newEntry), pos);
   }

   /**
    * Sets the value in row # <code>pos</code> to be <code>newEntry.</code>
    *
    * @param newEntry New value
    * @param pos      Row number to set its value
    */
   public void setDouble(double newEntry, int pos) {
      elements.put(pos, newEntry);
   }

   /**
    * Converts <code>newEntry</code> into double and assign it to row
    * #<code>pos.</code>
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setFloat(float newEntry, int pos) {
      setDouble((double) newEntry, pos);
   }

   /**
    * Converts <code>newEntry</code> into double and assign it to row
    * #<code>pos.</code>
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setInt(int newEntry, int pos) {
      setDouble((double) newEntry, pos);
   }

   /**
    * Converts <code>newEntry</code> into double and assign it to row
    * #<code>pos.</code>
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setLong(long newEntry, int pos) {
      setDouble((double) newEntry, pos);
   }

   /**
    * Sets the value at pos to be newEntry. If newEntry is a Number, it is
    * converted to a double and stored accordingly. Otherwise, setString() is
    * called with newEntry.toString()
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setObject(Object newEntry, int pos) {
      setDouble(toDouble(newEntry), pos);
   }

   /**
    * Sets the entry at the given position to newEntry. uses setObject for this
    * purpose.
    *
    * @param newEntry A new entry, a subclass of Number
    * @param pos      The position to set
    */
   public void setRow(Object newEntry, int pos) { setObject(newEntry, pos); }

   /**
    * Converts <code>newEntry</code> into double and assign it to row
    * #<code>pos.</code>
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setShort(short newEntry, int pos) {
      setDouble((double) newEntry, pos);
   }

   /**
    * Sets the value at pos to be newEntry by calling Double.parseDouble().
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setString(String newEntry, int pos) {
      setDouble(Double.parseDouble(newEntry), pos);
   }

   /**
    * Swaps the values between 2 rows. If there is no data in row
    * #<code>pos1</code> then nothing is stored in row #<ocde>pos2 , and vice
    * versia.
    *
    * @param pos1 - Row number of first item to be swaped
    * @param pos2 - Row number of second item to be swaped
    */
   public void swapRows(int pos1, int pos2) {

      if (pos1 == pos2) {
         return;
      }

      boolean valid_1 = elements.containsKey(pos1);
      boolean valid_2 = elements.containsKey(pos2);
      double val1 = elements.remove(pos1);
      double val2 = elements.remove(pos2);

      if (valid_1) {
         setDouble(val1, pos2);
      }

      if (valid_2) {
         setDouble(val2, pos1);
      }

      missing.swapRows(pos1, pos2);
      empty.swapRows(pos1, pos2);
   }
} // end class SparseDoubleColumn
