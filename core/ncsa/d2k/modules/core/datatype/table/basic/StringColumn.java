package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class StringColumn extends AbstractColumn implements TextualColumn {

   static final long serialVersionUID = -8586278089615271574L;

   /** a map of integer values to Strings */
   HashMap setOfValues;
   /** the unique strings contained in the table */
   String[] values;
   /** the int value for each row.  the int is an index into the values array */
   int[] rowIndicies;

   public StringColumn() {
      this(0);
   }

   public StringColumn(int numRows) {
      setOfValues = new HashMap();
      values = new String[0];
      rowIndicies = new int[numRows];
      type = ColumnTypes.STRING;
      setIsNominal(true);
   }

   public StringColumn(String[] data) {
      setOfValues = new HashMap();
      values = new String[0];
      rowIndicies = new int[data.length];
      for(int i = 0; i < data.length; i++) {
         setString(data[i], i);
      }
      type = ColumnTypes.STRING;
      setIsNominal(true);
   }

   /**
    * A copy constructor.
    * @param rows
    * @param vals
    * @param set
    */
   private StringColumn(int[] rows, String[] vals, HashMap set) {
        setOfValues = (HashMap)set.clone();
        values = new String[vals.length];
        for(int i = 0; i < vals.length; i++)
            values[i] = vals[i];
        rowIndicies = new int[rows.length];
        for(int i = 0; i < rowIndicies.length; i++)
            rowIndicies[i] = rows[i];
        setIsNominal(true);
        type = ColumnTypes.STRING;
   }

   private int addValue(String newVal) {
      String [] tmp = new String[values.length+1];
      System.arraycopy(values, 0, tmp, 0, values.length);
      tmp[tmp.length-1] = newVal;

      setOfValues.put(newVal, new Integer((int)(tmp.length-1)));
      values = tmp;
      return (int)(tmp.length-1);
   }

   /**
    * Currently we just leave removed values in the values array.
    * Eventually we should compact the array, but this will require shuffling
    * the rowIndicies...??? will it??
    * @param toRemove
    */
   private void removeValue(String toRemove) {
      ;
   }

   public void trim() {}

   public int getNumRows() {
      return rowIndicies.length;
   }

   public int getNumEntries() {
      return rowIndicies.length;
   }

   public String getString(int row) {
      return values[rowIndicies[row]];
   }

   public void setString(String s, int row) {
      if(!setOfValues.containsKey(s)) {
         int r = addValue(s);
         rowIndicies[row] = r;
      }
      else {
         Integer r = (Integer)setOfValues.get(s);
         rowIndicies[row] = r.intValue();
      }
   }

   public double getDouble(int row) {
      return Double.parseDouble(getString(row));
   }

   public void setDouble(double d, int row) {
      setString(Double.toString(d), row);
   }

   public int getInt(int row) {
      return Integer.parseInt(getString(row));
   }

   public void setInt(int i, int row) {
      setString(Integer.toString(i), row);
   }

   public short getShort(int row) {
      return Short.parseShort(getString(row));
   }

   public void setShort(short s, int row) {
      setString(Short.toString(s), row);
   }

   public float getFloat(int row) {
      return Float.parseFloat(getString(row));
   }

   public void setFloat(float f, int row) {
      setString(Float.toString(f), row);
   }

   public long getLong(int row) {
      return Long.parseLong(getString(row));
   }

   public void setLong(long l, int row) {
      setString(Long.toString(l), row);
   }

   public char[] getChars(int row) {
      return getString(row).toCharArray();
   }

   public void setChars(char[] c, int row) {
      setString(new String(c), row);
   }

   public byte[] getBytes(int row) {
      return getString(row).getBytes();
   }

   public void setBytes(byte[] b, int row) {
      setString(new String(b), row);
   }

   public char getChar(int row) {
      return getString(row).toCharArray()[0];
   }

   public void setChar(char c, int row) {
      char[] ar = {c};
      setString(new String(ar), row);
   }

   public byte getByte(int row) {
      return getString(row).getBytes()[0];
   }

   public void setByte(byte b, int row) {
      byte[] ar = {b};
      setString(new String(ar), row);
   }

   public void setBoolean(boolean b, int row) {
      setString(new Boolean(b).toString(), row);
   }

   public boolean getBoolean(int row) {
      return Boolean.valueOf(getString(row)).booleanValue();
   }

   public void setObject(Object o, int row) {
      setString(o.toString(), row);
   }

   public Object getObject(int row) {
      return getString(row);
   }

   public void sort() {
      sort(null);
   }

   public void sort(MutableTable t) {
        rowIndicies = doSort(rowIndicies, 0, rowIndicies.length - 1, t);
   }
   public void sort(MutableTable t, int begin, int end) {
         if (end > rowIndicies.length -1) {
            System.err.println(" end index was out of bounds");
            end = rowIndicies.length -1;
         }
         rowIndicies = doSort(rowIndicies, begin, end, t);
   }

    /**
     * Implement the quicksort algorithm.  Partition the array and
     * recursively call doSort.
     * @param A the array to sort
     * @param p the beginning index
     * @param r the ending index
     * @param t the Table to swap rows for
    * @return a sorted array of floats
     */
    private int[] doSort (int[] A, int p, int r, MutableTable t) {
        if (p < r) {
            int q = partition(A, p, r, t);
            doSort(A, p, q, t);
            doSort(A, q + 1, r, t);
        }
        return  A;
    }

    /**
     Rearrange the subarray A[p..r] in place.
     @param A the array to rearrange
     @param p the beginning index
     @param r the ending index
     @param t the Table to swap rows for
    @return the new partition point
     */
    private int partition (int[] A, int p, int r, MutableTable t) {
        int x = A[p];
        int i = p - 1;
        int j = r + 1;
        while (true) {
            do {
                j--;
            } //while (A[j] > x);
         while(compareRows(j, p) > 0);
            do {
                i++;
            } //while (A[i] < x);
         while(compareRows(i, p) < 0);
            if (i < j) {
                if (t == null) {
                    //short temp = A[i];
                    //A[i] = A[j];
                    //A[j] = temp;
               swapRows(i, j);
                }
                else
                    t.swapRows(i, j);
            }
            else
                return  j;
        }
    }


   public int compareRows(Object o, int row) {
      return compareStrings(o.toString(), getString(row));
   }

   public int compareRows(int r1, int r2) {
      return compareStrings(getString(r1), getString(r2));
   }

   private static int compareStrings(String s1, String s2) {
      return s1.compareTo(s2);
   }

   public void setNumRows(int nr) {
      int[] tmprow = new int[nr];
      if(rowIndicies == null) {
         rowIndicies = tmprow;
         return;
      }

      if(nr < rowIndicies.length) {
         System.arraycopy(rowIndicies, 0, tmprow, 0, tmprow.length);
      }
      else {
         System.arraycopy(rowIndicies, 0, tmprow, 0, rowIndicies.length);
      }
      rowIndicies = tmprow;
   }

   public Object getRow(int row) {
      return getString(row);
   }

   public void setRow(Object o, int row) {
      setObject(o, row);
   }

   public Column getSubset(int pos, int len) {
        if ((pos + len) > rowIndicies.length)
            throw  new ArrayIndexOutOfBoundsException();
        int[] subset = new int[len];
        System.arraycopy(rowIndicies, pos, subset, 0, len);
      StringColumn ic = new StringColumn(subset, values, setOfValues);
        ic.setLabel(getLabel());
        ic.setComment(getComment());
      ic.setScalarEmptyValue(getScalarEmptyValue());
      ic.setScalarMissingValue(getScalarMissingValue());
      ic.setNominalEmptyValue(getNominalEmptyValue());
      ic.setNominalMissingValue(getNominalMissingValue());
        return  ic;
   }

   public Column reorderRows(int[] newOrder) {
        int[] newInternal = null;
        if (newOrder.length == rowIndicies.length) {
            newInternal = new int[rowIndicies.length];
            for (int i = 0; i < rowIndicies.length; i++)
                newInternal[i] = rowIndicies[newOrder[i]];
        }
        else
            throw  new ArrayIndexOutOfBoundsException();
        StringColumn ic = new StringColumn(newInternal, values, setOfValues);
        ic.setLabel(getLabel());
        ic.setComment(getComment());
      ic.setScalarEmptyValue(getScalarEmptyValue());
      ic.setScalarMissingValue(getScalarMissingValue());
      ic.setNominalEmptyValue(getNominalEmptyValue());
      ic.setNominalMissingValue(getNominalMissingValue());
        return  ic;
   }

   public void swapRows(int pos1, int pos2) {
      int tmp = rowIndicies[pos1];
      rowIndicies[pos1] = rowIndicies[pos2];
      rowIndicies[pos2] = tmp;
   }

   public Column copy() {
        StringColumn newCol = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            newCol = (StringColumn)ois.readObject();
            ois.close();
            return  newCol;
        } catch (Exception e) {
         int[] tmprow = new int[rowIndicies.length];
         String[] vals = new String[values.length];
         HashMap set = new HashMap();
            for (int i = 0; i < rowIndicies.length; i++)
            tmprow[i] = rowIndicies[i];
         for(int i = 0; i < vals.length; i++)
            vals[i] = values[i];
         Iterator i = setOfValues.keySet().iterator();
         while(i.hasNext()) {
            Object key = i.next();
            Object val = setOfValues.get(key);
            set.put(key, val);
         }

         newCol = new StringColumn(tmprow, vals, set);

            newCol.setLabel(getLabel());
            newCol.setComment(getComment());
         newCol.type = getType();
         newCol.setScalarEmptyValue(getScalarEmptyValue());
         newCol.setScalarMissingValue(getScalarMissingValue());
         newCol.setNominalEmptyValue(getNominalEmptyValue());
         newCol.setNominalMissingValue(getNominalMissingValue());
            return  newCol;
        }

   }

   public Object removeRow(int pos) {
        int removed = rowIndicies[pos];
        System.arraycopy(rowIndicies, pos + 1, rowIndicies, pos, rowIndicies.length -
                (pos + 1));
        int newInternal[] = new int[rowIndicies.length - 1];
        System.arraycopy(rowIndicies, 0, newInternal, 0, rowIndicies.length - 1);
        rowIndicies = newInternal;
        return values[removed];
   }

   public void addRow(Object o) {
      int idx;
      if(!setOfValues.containsKey(o.toString()))
         idx = addValue(o.toString());
      else {
         Integer r = (Integer)setOfValues.get(o.toString());
         idx = r.intValue();
      }

        int last = rowIndicies.length;
        int[] newInternal = new int[rowIndicies.length + 1];
        System.arraycopy(rowIndicies, 0, newInternal, 0, rowIndicies.length);
        newInternal[last] = idx;
        rowIndicies = newInternal;
   }

   public void insertRow(Object newEntry, int pos) {

        int[] newInternal = new int[rowIndicies.length + 1];
        if (pos > getNumRows()) {
            addRow(newEntry);
            return;
        }
        if (pos == 0)
            System.arraycopy(rowIndicies, 0, newInternal, 1, getNumRows());
        else {
            System.arraycopy(rowIndicies, 0, newInternal, 0, pos);
            System.arraycopy(rowIndicies, pos, newInternal, pos + 1,
            rowIndicies.length - pos);
        }

      int idx;
      if(!setOfValues.containsKey(newEntry.toString()))
         idx = addValue(newEntry.toString());
      else {
         Integer r = (Integer)setOfValues.get(newEntry.toString());
         idx = r.intValue();
      }

        newInternal[pos] = idx;
        rowIndicies = newInternal;
   }

   public void removeRowsByIndex(int[] indices) {
        HashSet toRemove = new HashSet(indices.length);
        for (int i = 0; i < indices.length; i++) {
            Integer id = new Integer(indices[i]);
            toRemove.add(id);
        }
        int newInternal[] = new int[rowIndicies.length - indices.length];
        int newIntIdx = 0;
        for (int i = 0; i < getNumRows(); i++) {
            // check if this row is in the list of rows to remove
            // if this row is not in the list, copy it into the new internal
         if(!toRemove.contains(new Integer(i))) {
                newInternal[newIntIdx] = rowIndicies[i];
                newIntIdx++;
            }
        }
        rowIndicies = newInternal;
   }
}
