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
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.TextualColumn;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import ncsa.d2k.modules.core.util.*;



/**
 * <p>StringColumn is an implementation of TextualColumn which stores textual
 * data in a String form.</p>
 *
 * @author  suvalala
 * @author  redman
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class StringColumn extends MissingValuesColumn implements TextualColumn {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static final long serialVersionUID = -8586278089615271574L;

   //~ Instance fields *********************************************************

   /** Stores empty rows of the column. */
   private boolean[] empty = null;

   /** The int value for each row. Each is an index into the values array */
   private int[] rowIndicies;

   /** Map of integer values to Strings. */
   private HashMap setOfValues;

   /** Unique strings contained in the table. */
   private String[] values;

   //~ Constructors ************************************************************

   /**
    * Creates a new StringColumn object.
    */
   public StringColumn() { this(0); }

   /**
    * A copy constructor.
    *
    * @param rows     Description of parameter $param.name$.
    * @param vals     Description of parameter $param.name$.
    * @param set      Description of parameter $param.name$.
    * @param missingV Description of parameter missingV.
    * @param emptyV   Description of parameter emptyV.
    * @param lbl      Description of parameter lbl.
    * @param comm     Description of parameter comm.
    */
   private StringColumn(int[] rows, String[] vals, HashMap set,
                        boolean[] missingV, boolean[] emptyV, String lbl,
                        String comm) {
      setOfValues = (HashMap) set.clone();
      values = new String[vals.length];

      for (int i = 0; i < vals.length; i++) {
         values[i] = vals[i];
      }

      rowIndicies = new int[rows.length];

      for (int i = 0; i < rowIndicies.length; i++) {
         rowIndicies[i] = rows[i];
      }

      setIsNominal(true);
      type = ColumnTypes.STRING;
      missing = new boolean[rowIndicies.length];
      empty = new boolean[rowIndicies.length];

      for (int i = 0; i < rowIndicies.length; i++) {
         missing[i] = missingV[i];
         empty[i] = emptyV[i];
      }

      this.setMissingValues(missing);
      setLabel(lbl);
      this.setComment(comm);
   }

   /**
    * Creates a new StringColumn object.
    *
    * @param numRows Description of parameter numRows.
    */
   public StringColumn(int numRows) {
      setOfValues = new HashMap();
      values = new String[0];
      rowIndicies = new int[numRows];
      type = ColumnTypes.STRING;
      setIsNominal(true);
      missing = new boolean[numRows];
      empty = new boolean[numRows];

      String dummy = "?";

      for (int i = 0; i < numRows; i++) {
         missing[i] = false;
         empty[i] = false;
         setString(dummy, i);
      }
   }

   /**
    * Creates a new StringColumn object.
    *
    * @param data Description of parameter data.
    */
   public StringColumn(String[] data) {
      setOfValues = new HashMap();
      values = new String[0];
      rowIndicies = new int[data.length];

      for (int i = 0; i < data.length; i++) {
         setString(data[i], i);
      }

      type = ColumnTypes.STRING;
      setIsNominal(true);
      missing = new boolean[rowIndicies.length];
      empty = new boolean[rowIndicies.length];

      for (int i = 0; i < rowIndicies.length; i++) {
         missing[i] = false;
         empty[i] = false;
      }
   }

   //~ Methods *****************************************************************

   /**
    * Description of method compareStrings.
    *
    * @param  s1 Description of parameter s1.
    * @param  s2 Description of parameter s2.
    *
    * @return Description of return value.
    */
   static private int compareStrings(String s1, String s2) {
      return TableUtilities.compareStrings(s1, s2);
      // return s1.compareTo(s2);
   }

   /**
    * Description of method addValue.
    *
    * @param  newVal Description of parameter newVal.
    *
    * @return Description of return value.
    */
   private int addValue(String newVal) {
      String[] tmp = new String[values.length + 1];
      System.arraycopy(values, 0, tmp, 0, values.length);
      tmp[tmp.length - 1] = newVal;

      setOfValues.put(newVal, new Integer((int) (tmp.length - 1)));
      values = tmp;

      return (int) (tmp.length - 1);
   }

   /**
    * Implement the quicksort algorithm. Partition the array and recursively
    * call doSort.
    *
    * @param  A the array to sort
    * @param  p the beginning index
    * @param  r the ending index
    * @param  t the Table to swap rows for
    *
    * @return a sorted array of floats
    */
   private int[] doSort(int[] A, int p, int r, MutableTable t) {

      if (p < r) {
         int q = partition(A, p, r, t);
         doSort(A, p, q, t);
         doSort(A, q + 1, r, t);
      }

      return A;
   }

   /**
    * Rearrange the subarray A[p..r] in place.
    *
    * @param  B the array to rearrange
    * @param  p the beginning index
    * @param  r the ending index
    * @param  t the Table to swap rows for
    *
    * @return the new partition point
    */
   private int partition(int[] B, int p, int r, MutableTable t) {
      boolean xMissing = this.isValueMissing(p);
      String pStr = this.getString(p);
      int i = p - 1;
      int j = r + 1;

      while (true) {

         if (xMissing) {
            j--;

            do {
               i++;
            } while (!this.isValueMissing(i));
         } else {

            do {
               j--;
            } while (
                     this.isValueMissing(j) ||
                        TableUtilities.compareStrings(this.getString(j), pStr) >
                        0);

            do {
               i++;
            } while (
                     !this.isValueMissing(i) &&
                        TableUtilities.compareStrings(this.getString(i), pStr) <
                        0);
         }

         if (i < j) {

            if (t == null) {
               this.swapRows(i, j);
            } else {
               t.swapRows(i, j);
            }
         } else {
            return j;
         }
      } // end while
   } // end method partition

   /**
    * Currently we just leave removed values in the values array. Eventually we
    * should compact the array, but this will require shuffling the
    * rowIndicies...??? will it??
    *
    * @param toRemove Description of parameter $param.name$.
    */
   private void removeValue(String toRemove) { ; }

   /**
    * Description of method addRow.
    *
    * @param o Description of parameter o.
    */
   public void addRow(Object o) {
      int idx;

      if (!setOfValues.containsKey(o.toString())) {
         idx = addValue(o.toString());
      } else {
         Integer r = (Integer) setOfValues.get(o.toString());
         idx = r.intValue();
      }

      int last = rowIndicies.length;
      int[] newInternal = new int[rowIndicies.length + 1];
      boolean[] newMissing = new boolean[rowIndicies.length + 1];
      boolean[] newEmpty = new boolean[rowIndicies.length + 1];
      System.arraycopy(rowIndicies, 0, newInternal, 0, rowIndicies.length);
      System.arraycopy(missing, 0, newMissing, 0, missing.length);
      System.arraycopy(empty, 0, newEmpty, 0, empty.length);
      newInternal[last] = idx;
      rowIndicies = newInternal;
      this.setMissingValues(newMissing);
      empty = newEmpty;
   }

   /**
    * Add the specified number of blank rows.
    *
    * @param number number of rows to add.
    */
   public void addRows(int number) {

      // ANCA : replaced this: int last = values.length; and all value with
      // rowIndicies
      int last = rowIndicies.length;
      int[] newInternal = new int[last + number];
      boolean[] newMissing = new boolean[last + number];
      boolean[] newEmpty = new boolean[last + number];

      System.arraycopy(rowIndicies, 0, newInternal, 0, last);
      System.arraycopy(missing, 0, newMissing, 0, missing.length);
      System.arraycopy(empty, 0, newEmpty, 0, empty.length);
      rowIndicies = newInternal;
      this.setMissingValues(newMissing);
      empty = newEmpty;
   }


   /**
    * Description of method compareRows.
    *
    * @param  o   Description of parameter o.
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public int compareRows(Object o, int row) {
      return compareStrings(o.toString(), getString(row));
   }

   /**
    * Description of method compareRows.
    *
    * @param  r1 Description of parameter r1.
    * @param  r2 Description of parameter r2.
    *
    * @return Description of return value.
    */
   public int compareRows(int r1, int r2) {
      return compareStrings(getString(r1), getString(r2));
   }

   /**
    * Description of method copy.
    *
    * @return Description of return value.
    */
   public Column copy() {
      StringColumn newCol;

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(this);

         byte[] buf = baos.toByteArray();
         oos.close();

         ByteArrayInputStream bais = new ByteArrayInputStream(buf);
         ObjectInputStream ois = new ObjectInputStream(bais);
         newCol = (StringColumn) ois.readObject();
         ois.close();

         return newCol;
      } catch (Exception e) {
         int[] tmprow = new int[rowIndicies.length];
         String[] vals = new String[values.length];
         HashMap set = new HashMap();

         for (int i = 0; i < rowIndicies.length; i++) {
            tmprow[i] = rowIndicies[i];
         }

         for (int i = 0; i < vals.length; i++) {
            vals[i] = values[i];
         }

         Iterator i = setOfValues.keySet().iterator();

         while (i.hasNext()) {
            Object key = i.next();
            Object val = setOfValues.get(key);
            set.put(key, val);
         }

         boolean[] miss = new boolean[rowIndicies.length];
         boolean[] em = new boolean[rowIndicies.length];

         for (int j = 0; j < rowIndicies.length; j++) {
            miss[j] = missing[j];
            em[j] = empty[j];
         }

         newCol =
            new StringColumn(tmprow, vals, set, miss, em, getLabel(),
                             getComment());

         return newCol;
      } // end try-catch

   } // end method copy

   /**
    * Description of method getBoolean.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public boolean getBoolean(int row) {
      return Boolean.valueOf(getString(row)).booleanValue();
   }

   /**
    * Description of method getByte.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public byte getByte(int row) {
      return Byte.parseByte(getString(row));
             // return getString(row).getBytes()[0]; ANCA: the above was commented but
             // it 's the corect one- return Byte.parseByte(getString(row));
   }

   /**
    * Description of method getBytes.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public byte[] getBytes(int row) { return getString(row).getBytes(); }

   /**
    * Description of method getChar.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public char getChar(int row) { return getString(row).toCharArray()[0]; }

   /**
    * Description of method getChars.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public char[] getChars(int row) { return getString(row).toCharArray(); }

   /**
    * Description of method getDouble.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public double getDouble(int row) {
      return Double.parseDouble(getString(row));
   }

   /**
    * Description of method getFloat.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public float getFloat(int row) { return Float.parseFloat(getString(row)); }

   /**
    * Description of method getInt.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public int getInt(int row) { return Integer.parseInt(getString(row)); }

   /**
    * This method will expand the compressed nominal string data into a complete
    * string array.
    *
    * @return the internal representation of the data.
    */
   public Object getInternal() {
      int num = this.getNumRows();
      String[] ir = new String[num];

      for (int i = 0; i < num; i++) {
         ir[i] = this.getString(i);
      }

      return ir;
   }

   /**
    * Description of method getLong.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public long getLong(int row) { return Long.parseLong(getString(row)); }

   /**
    * Description of method getNumEntries.
    *
    * @return Description of return value.
    */
   public int getNumEntries() {
      int numEntries = 0;

      for (int i = 0; i < rowIndicies.length; i++) {

         if (!isValueMissing(i) && !isValueEmpty(i)) {
            numEntries++;
         }
      }

      return numEntries;
      // ANCA: replaced with the above:  return rowIndicies.length;
   }

   /**
    * Description of method getNumRows.
    *
    * @return Description of return value.
    */
   public int getNumRows() { return rowIndicies.length; }

   /**
    * Description of method getObject.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public Object getObject(int row) { return getString(row); }

   /**
    * Description of method getRow.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public Object getRow(int row) { return getString(row); }

   /**
    * Description of method getShort.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public short getShort(int row) { return Short.parseShort(getString(row)); }

   /**
    * Description of method getString.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public String getString(int row) { return values[rowIndicies[row]]; }

   /**
    * Gets a subset of this <code>Column</code>, given a start position and
    * length. The primitive values are copied, so they have no destructive
    * abilities as far as the <code>Column</code> is concerned.
    *
    * @param  rows the start position for the subset
    *
    * @return a subset of this <code>Column</code>
    */
   public Column getSubset(int[] rows) {
      int[] subset = new int[rows.length];
      boolean[] newMissing = new boolean[rows.length];
      boolean[] newEmpty = new boolean[rows.length];

      for (int i = 0; i < rows.length; i++) {
         subset[i] = rowIndicies[rows[i]];
         newMissing[i] = missing[rows[i]];
         newEmpty[i] = empty[rows[i]];
      }

      StringColumn ic =
         new StringColumn(subset, values, setOfValues, newMissing,
                          newEmpty, getLabel(), getComment());

      return ic;
   }

   /**
    * Description of method getSubset.
    *
    * @param  pos Description of parameter pos.
    * @param  len Description of parameter len.
    *
    * @return Description of return value.
    *
    * @throws ArrayIndexOutOfBoundsException Description of exception
    *                                        ArrayIndexOutOfBoundsException.
    */
   public Column getSubset(int pos, int len) {

      if ((pos + len) > rowIndicies.length) {
         throw new ArrayIndexOutOfBoundsException();
      }

      int[] subset = new int[len];
      boolean[] newMissing = new boolean[len];
      boolean[] newEmpty = new boolean[len];
      System.arraycopy(rowIndicies, pos, subset, 0, len);
      System.arraycopy(missing, pos, newMissing, 0, len);
      System.arraycopy(empty, pos, newEmpty, 0, len);

      StringColumn ic =
         new StringColumn(subset, values, setOfValues, newMissing,
                          newEmpty, getLabel(), getComment());

      return ic;
   }

   /**
    * Description of method insertRow.
    *
    * @param newEntry Description of parameter newEntry.
    * @param pos      Description of parameter pos.
    */
   public void insertRow(Object newEntry, int pos) {

      if (pos > getNumRows()) {
         addRow(newEntry);

         return;
      }

      int[] newInternal = new int[rowIndicies.length + 1];
      boolean[] newMissing = new boolean[rowIndicies.length + 1];
      boolean[] newEmpty = new boolean[rowIndicies.length + 1];

      if (pos == 0) {
         System.arraycopy(rowIndicies, 0, newInternal, 1, getNumRows());
         System.arraycopy(missing, 0, newMissing, 1, getNumRows());
         System.arraycopy(empty, 0, newEmpty, 1, getNumRows());
      } else {
         System.arraycopy(rowIndicies, 0, newInternal, 0, pos);
         System.arraycopy(rowIndicies, pos, newInternal, pos + 1,
                          rowIndicies.length - pos);
         System.arraycopy(missing, 0, newMissing, 0, pos);
         System.arraycopy(missing, pos, newMissing, pos + 1,
                          rowIndicies.length -
                          pos);

         System.arraycopy(empty, 0, newEmpty, 0, pos);
         System.arraycopy(empty, pos, newEmpty, pos + 1,
                          rowIndicies.length -
                          pos);
      }

      int idx;

      if (!setOfValues.containsKey(newEntry.toString())) {
         idx = addValue(newEntry.toString());
      } else {
         Integer r = (Integer) setOfValues.get(newEntry.toString());
         idx = r.intValue();
      }

      newInternal[pos] = idx;
      rowIndicies = newInternal;
      this.setMissingValues(newMissing);
      empty = newEmpty;
   } // end method insertRow

   /**
    * Description of method isValueEmpty.
    *
    * @param  row Description of parameter row.
    *
    * @return Description of return value.
    */
   public boolean isValueEmpty(int row) { return empty[row]; }

   /**
    * Description of method removeRow.
    *
    * @param  pos Description of parameter pos.
    *
    * @return Description of return value.
    */
   public Object removeRow(int pos) {
      int removed = rowIndicies[pos];
      System.arraycopy(rowIndicies, pos + 1, rowIndicies, pos,
                       rowIndicies.length -
                       (pos + 1));
      System.arraycopy(missing, pos + 1, missing, pos, rowIndicies.length -
                       (pos + 1));

      System.arraycopy(empty, pos + 1, empty, pos, rowIndicies.length -
                       (pos + 1));

      int[] newInternal = new int[rowIndicies.length - 1];
      boolean[] newMissing = new boolean[rowIndicies.length - 1];
      boolean[] newEmpty = new boolean[rowIndicies.length - 1];
      System.arraycopy(rowIndicies, 0, newInternal, 0, rowIndicies.length - 1);
      System.arraycopy(missing, 0, newMissing, 0, rowIndicies.length - 1);
      System.arraycopy(empty, 0, newEmpty, 0, rowIndicies.length - 1);
      rowIndicies = newInternal;
      this.setMissingValues(newMissing);
      empty = newEmpty;

      return values[removed];
   }

   /**
    * Description of method removeRowsByIndex.
    *
    * @param indices Description of parameter indices.
    */
   public void removeRowsByIndex(int[] indices) {
      HashSet toRemove = new HashSet(indices.length);

      for (int i = 0; i < indices.length; i++) {
         Integer id = new Integer(indices[i]);
         toRemove.add(id);
      }

      int[] newInternal = new int[rowIndicies.length - indices.length];
      boolean[] newMissing = new boolean[rowIndicies.length - indices.length];
      boolean[] newEmpty = new boolean[rowIndicies.length - indices.length];
      int newIntIdx = 0;

      for (int i = 0; i < getNumRows(); i++) {

         // check if this row is in the list of rows to remove
         // if this row is not in the list, copy it into the new internal
         if (!toRemove.contains(new Integer(i))) {
            newInternal[newIntIdx] = rowIndicies[i];
            newMissing[newIntIdx] = missing[i];
            newEmpty[newIntIdx] = empty[i];
            newIntIdx++;
         }
      }

      rowIndicies = newInternal;
      this.setMissingValues(newMissing);
      empty = newEmpty;
   } // end method removeRowsByIndex


   /**
    * Description of method reorderRows.
    *
    * @param  newOrder Description of parameter newOrder.
    *
    * @return Description of return value.
    *
    * @throws ArrayIndexOutOfBoundsException Description of exception
    *                                        ArrayIndexOutOfBoundsException.
    */
   public Column reorderRows(int[] newOrder) {
      int[] newInternal = null;
      boolean[] newMissing = null;
      boolean[] newEmpty = null;

      if (newOrder.length == rowIndicies.length) {
         newInternal = new int[rowIndicies.length];
         newMissing = new boolean[rowIndicies.length];
         newEmpty = new boolean[rowIndicies.length];

         for (int i = 0; i < rowIndicies.length; i++) {
            newInternal[i] = rowIndicies[newOrder[i]];
            newMissing[i] = missing[newOrder[i]];
            newEmpty[i] = empty[newOrder[i]];
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }

      StringColumn ic =
         new StringColumn(newInternal, values, setOfValues, newMissing,
                          newEmpty,
                          getLabel(), getComment());

      return ic;
   }

   /**
    * Description of method setBoolean.
    *
    * @param b   Description of parameter b.
    * @param row Description of parameter row.
    */
   public void setBoolean(boolean b, int row) {
      setString(new Boolean(b).toString(), row);
   }

   /**
    * Description of method setByte.
    *
    * @param b   Description of parameter b.
    * @param row Description of parameter row.
    */
   public void setByte(byte b, int row) {
      setString(Byte.toString(b), row);
      /*byte[] ar = {b};
       *setString(new String(ar), row);*/
   }

   /**
    * Description of method setBytes.
    *
    * @param b   Description of parameter b.
    * @param row Description of parameter row.
    */
   public void setBytes(byte[] b, int row) { setString(new String(b), row); }

   /**
    * Description of method setChar.
    *
    * @param c   Description of parameter c.
    * @param row Description of parameter row.
    */
   public void setChar(char c, int row) {
      char[] ar = { c };
      setString(new String(ar), row);
   }

   /**
    * Description of method setChars.
    *
    * @param c   Description of parameter c.
    * @param row Description of parameter row.
    */
   public void setChars(char[] c, int row) { setString(new String(c), row); }

   /**
    * Description of method setDouble.
    *
    * @param d   Description of parameter d.
    * @param row Description of parameter row.
    */
   public void setDouble(double d, int row) {
      setString(Double.toString(d), row);
   }

   /**
    * Description of method setFloat.
    *
    * @param f   Description of parameter f.
    * @param row Description of parameter row.
    */
   public void setFloat(float f, int row) { setString(Float.toString(f), row); }

   /**
    * Description of method setInt.
    *
    * @param i   Description of parameter i.
    * @param row Description of parameter row.
    */
   public void setInt(int i, int row) { setString(Integer.toString(i), row); }

   /**
    * Description of method setLong.
    *
    * @param l   Description of parameter l.
    * @param row Description of parameter row.
    */
   public void setLong(long l, int row) { setString(Long.toString(l), row); }

   /**
    * Description of method setNumRows.
    *
    * @param nr Description of parameter nr.
    */
   public void setNumRows(int nr) {
      int[] tmprow = new int[nr];
      boolean[] newMissing = new boolean[nr];
      boolean[] newEmpty = new boolean[nr];

      if (rowIndicies == null) {
         rowIndicies = tmprow;
         missing = newMissing;
         empty = newEmpty;

         return;
      }

      if (nr < rowIndicies.length) {
         System.arraycopy(rowIndicies, 0, tmprow, 0, tmprow.length);
         System.arraycopy(missing, 0, newMissing, 0, tmprow.length);
         System.arraycopy(empty, 0, newEmpty, 0, tmprow.length);
      } else {
         System.arraycopy(rowIndicies, 0, tmprow, 0, rowIndicies.length);
         System.arraycopy(missing, 0, newMissing, 0, rowIndicies.length);
         System.arraycopy(empty, 0, newEmpty, 0, rowIndicies.length);
      }

      rowIndicies = tmprow;
      this.setMissingValues(newMissing);
      empty = newEmpty;
   } // end method setNumRows

   /**
    * Description of method setObject.
    *
    * @param o   Description of parameter o.
    * @param row Description of parameter row.
    */
   public void setObject(Object o, int row) { setString(o.toString(), row); }

   /**
    * Description of method setRow.
    *
    * @param o   Description of parameter o.
    * @param row Description of parameter row.
    */
   public void setRow(Object o, int row) { setObject(o, row); }

   /**
    * Description of method setShort.
    *
    * @param s   Description of parameter s.
    * @param row Description of parameter row.
    */
   public void setShort(short s, int row) { setString(Short.toString(s), row); }

   /**
    * Description of method setString.
    *
    * @param s   Description of parameter s.
    * @param row Description of parameter row.
    */
   public void setString(String s, int row) {

      if (!setOfValues.containsKey(s)) {
         int r = addValue(s);
         rowIndicies[row] = r;
      } else {
         Integer r = (Integer) setOfValues.get(s);
         rowIndicies[row] = r.intValue();
      }
   }

   /**
    * Description of method setValueToEmpty.
    *
    * @param b   Description of parameter b.
    * @param row Description of parameter row.
    */
   public void setValueToEmpty(boolean b, int row) { empty[row] = b; }

   /**
    * Description of method sort.
    */
   public void sort() { sort(null); }

   /**
    * Description of method sort.
    *
    * @param t Description of parameter t.
    */
   public void sort(MutableTable t) {
      rowIndicies = doSort(rowIndicies, 0, rowIndicies.length - 1, t);
   }
   
   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   /**
    * Description of method sort.
    *
    * @param t     Description of parameter t.
    * @param begin Description of parameter begin.
    * @param end   Description of parameter end.
    */
   public void sort(MutableTable t, int begin, int end) {

      if (end > rowIndicies.length - 1) {
    	  myLogger.error(" end index was out of bounds");
         end = rowIndicies.length - 1;
      }

      rowIndicies = doSort(rowIndicies, begin, end, t);
   }

   /**
    * Description of method swapRows.
    *
    * @param pos1 Description of parameter pos1.
    * @param pos2 Description of parameter pos2.
    */
   public void swapRows(int pos1, int pos2) {
      int tmp = rowIndicies[pos1];
      boolean miss = missing[pos1];
      boolean emp = empty[pos1];
      rowIndicies[pos1] = rowIndicies[pos2];
      rowIndicies[pos2] = tmp;
      missing[pos1] = missing[pos2];
      missing[pos2] = miss;

      empty[pos1] = empty[pos2];
      empty[pos2] = emp;
   }

   /**
    * Description of method trim.
    */
   public void trim() { }
} // end class StringColumn
