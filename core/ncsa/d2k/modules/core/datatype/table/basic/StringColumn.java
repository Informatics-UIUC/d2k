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
   private HashMap setOfValues;
   /** the unique strings contained in the table */
   private String[] values;
   /** the int value for each row.  the int is an index into the values array */
   private int[] rowIndicies;
   private boolean[] missing = null;
   private boolean[] empty = null;

   public StringColumn() {
	  this(0);
   }

   public StringColumn(int numRows) {
	  setOfValues = new HashMap();
	  values = new String[0];
	  rowIndicies = new int[numRows];
	  type = ColumnTypes.STRING;
	  setIsNominal(true);
	  missing = new boolean[numRows];
	  empty = new boolean[numRows];
	  String dummy = "?";
	  for(int i = 0; i < numRows; i++) {
		  missing[i] = false;
		  empty[i] = false;
		 setString(dummy, i);
	  }
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
	  missing = new boolean[rowIndicies.length];
	  empty = new boolean[rowIndicies.length];
	  for(int i = 0; i < rowIndicies.length; i++) {
		  missing[i] = false;
		  empty[i] = false;
		}
   }

   /**
	* A copy constructor.
	* @param rows
	* @param vals
	* @param set
	*/
   private StringColumn(int[] rows, String[] vals, HashMap set,
						boolean[] missing, boolean[] empty, String lbl, String comm) {
		setOfValues = (HashMap)set.clone();
		values = new String[vals.length];
		for(int i = 0; i < vals.length; i++)
			values[i] = vals[i];
		rowIndicies = new int[rows.length];
		for(int i = 0; i < rowIndicies.length; i++)
			rowIndicies[i] = rows[i];
		setIsNominal(true);
		type = ColumnTypes.STRING;
		missing = new boolean[rowIndicies.length];
		empty = new boolean[rowIndicies.length];
		for(int i = 0; i < rowIndicies.length; i++) {
			missing[i] = false;
			empty[i] = false;
		}
		setLabel(lbl);
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
	  //return getString(row).getBytes()[0];
	   return Byte.parseByte(getString(row));
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
	  boolean[] newMissing = new boolean[nr];
	  boolean[] newEmpty = new boolean[nr];
	  if(rowIndicies == null) {
		 rowIndicies = tmprow;
		 missing = newMissing;
		 empty = newEmpty;
		 return;
	  }

	  if(nr < rowIndicies.length) {
		 System.arraycopy(rowIndicies, 0, tmprow, 0, tmprow.length);
		 System.arraycopy(missing, 0, newMissing, 0, tmprow.length);
		 System.arraycopy(empty, 0, newEmpty, 0, tmprow.length);
	  }
	  else {
		 System.arraycopy(rowIndicies, 0, tmprow, 0, rowIndicies.length);
		 System.arraycopy(missing, 0, newMissing, 0, rowIndicies.length);
		 System.arraycopy(empty, 0, newEmpty, 0, rowIndicies.length);
	  }
	  rowIndicies = tmprow;
	  missing = newMissing;
	  empty = newEmpty;
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
		boolean[] newMissing = new boolean[len];
		boolean[] newEmpty = new boolean[len];
		System.arraycopy(rowIndicies, pos, subset, 0, len);
		System.arraycopy(missing, pos, newMissing, 0, len);
		System.arraycopy(empty, pos, newEmpty, 0, len);
		StringColumn ic = new StringColumn(subset, values, setOfValues, newMissing,
				newEmpty, getLabel(), getComment());
		//ic.setLabel(getLabel());
		//ic.setComment(getComment());
		//ic.missing = newMissing;
		//ic.empty = newEmpty;
		//ic.setScalarEmptyValue(getScalarEmptyValue());
		//ic.setScalarMissingValue(getScalarMissingValue());
		//ic.setNominalEmptyValue(getNominalEmptyValue());
		//ic.setNominalMissingValue(getNominalMissingValue());
		return  ic;

/*        if ((pos + len) > internal.length)
			throw  new ArrayIndexOutOfBoundsException();
		boolean[] subset = new boolean[len];
		boolean[] newMissing = new boolean[len];
		boolean[] newEmpty = new boolean[len];
		System.arraycopy(internal, pos, subset, 0, len);
		System.arraycopy(missing, pos, newMissing, 0, len);
		System.arraycopy(empty, pos, newEmpty, 0, len);
		BooleanColumn bc = new BooleanColumn(subset);
		bc.missing = newMissing;
		bc.empty = newEmpty;
		bc.setLabel(getLabel());
		bc.setComment(getComment());
		return  bc;
		*/
   }

   /**
	* Gets a subset of this <code>Column</code>, given a start position and
	* length. The primitive values are copied, so they have no destructive
	* abilities as far as the <code>Column</code> is concerned.
	*
	* @param pos            the start position for the subset
	* @param len            the length of the subset
	* @return               a subset of this <code>Column</code>
	*/
   public Column getSubset (int[] rows) {
	   int[] subset = new int[rows.length];
	   boolean[] newMissing = new boolean[rows.length];
	   boolean[] newEmpty = new boolean[rows.length];
	   for(int i = 0; i < rows.length; i++) {
		 subset[i] = rowIndicies[rows[i]];
		 newMissing[i] = missing[rows[i]];
		 newEmpty[i] = empty[rows[i]];
	   }

//                System.arraycopy(missing, pos, newMissing, 0, len);
//                System.arraycopy(empty, pos, newEmpty, 0, len);
	   /*BooleanColumn bc = new BooleanColumn(subset);
			   bc.missing = newMissing;
			   bc.empty = newEmpty;
	   bc.setLabel(getLabel());
	   bc.setComment(getComment());
	   */
	   StringColumn ic = new StringColumn(subset, values, setOfValues, newMissing,
				newEmpty, getLabel(), getComment());
	   //BooleanColumn bc = new BooleanColumn(subset, newMissing, newEmpty, getLabel(), getComment());
	   return ic;
   }


   public Column reorderRows(int[] newOrder) {
		int[] newInternal = null;
		boolean[] newMissing = null;
		boolean[] newEmpty = null;
		if (newOrder.length == rowIndicies.length) {
			newInternal = new int[rowIndicies.length];
			newMissing=new boolean[rowIndicies.length];
			newEmpty=new boolean[rowIndicies.length];
			for (int i = 0; i < rowIndicies.length; i++) {
				newInternal[i] = rowIndicies[newOrder[i]];
				newMissing[i] = missing[newOrder[i]];
				newEmpty[i] = empty[newOrder[i]];
			}
		}
		else
			throw  new ArrayIndexOutOfBoundsException();
		StringColumn ic = new StringColumn(newInternal, values, setOfValues, newMissing, newEmpty,
				getLabel(), getComment());
		//ic.setLabel(getLabel());
		//ic.setComment(getComment());
	  //ic.setScalarEmptyValue(getScalarEmptyValue());
	  //ic.setScalarMissingValue(getScalarMissingValue());
	  //ic.setNominalEmptyValue(getNominalEmptyValue());
	  //ic.setNominalMissingValue(getNominalMissingValue());
		return  ic;
/*        boolean[] newInternal = null;
		boolean[] newMissing = null;
		boolean[] newEmpty = null;
		if (newOrder.length == internal.length) {
			newInternal = new boolean[internal.length];
			newMissing = new boolean[internal.length];
			newEmpty = new boolean[internal.length];
			for (int i = 0; i < internal.length; i++) {
				newInternal[i] = internal[newOrder[i]];
				newMissing[i] = missing[newOrder[i]];
				newEmpty[i] = empty[newOrder[i]];
			}
		}
		else
			throw  new ArrayIndexOutOfBoundsException();
		BooleanColumn bc = new BooleanColumn(newInternal, newMissing, newEmpty, getLabel(), getComment());
		return  bc;
		*/
   }

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
/*        boolean d1 = internal[pos1];
		boolean miss = missing[pos1];
		boolean emp = empty[pos1];
		internal[pos1] = internal[pos2];
		internal[pos2] = d1;

		missing[pos1] = missing[pos2];
		missing[pos2] = miss;

		empty[pos1] = empty[pos2];
		empty[pos2] = emp;
		*/
   }

   public Column copy() {
		StringColumn newCol;
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


		 //   newCol.setLabel(getLabel());
		 //   newCol.setComment(getComment());
		 //newCol.type = getType();
		 //newCol.setScalarEmptyValue(getScalarEmptyValue());
		 //newCol.setScalarMissingValue(getScalarMissingValue());
		 //newCol.setNominalEmptyValue(getNominalEmptyValue());
		 //newCol.setNominalMissingValue(getNominalMissingValue());
		 boolean[] miss = new boolean[rowIndicies.length];
		 boolean[] em = new boolean[rowIndicies.length];
		 for(int j = 0; j < rowIndicies.length; j++) {
			 miss[j] = missing[j];
			 em[j] = empty[j];
		 }
		 // newCol.missing = miss;
		//	newCol.empty = em;
		 newCol = new StringColumn(tmprow, vals, set, miss, em, getLabel(), getComment());
			return  newCol;
		}

   }

   public Object removeRow(int pos) {
		int removed = rowIndicies[pos];
		System.arraycopy(rowIndicies, pos + 1, rowIndicies, pos, rowIndicies.length -
				(pos + 1));
		System.arraycopy(missing, pos + 1, missing, pos, rowIndicies.length -
				(pos + 1));

		System.arraycopy(empty, pos + 1, empty, pos, rowIndicies.length -
				(pos + 1));
		int newInternal[] = new int[rowIndicies.length - 1];
		boolean newMissing[] = new boolean[rowIndicies.length-1];
		boolean newEmpty[] = new boolean[rowIndicies.length-1];
		System.arraycopy(rowIndicies, 0, newInternal, 0, rowIndicies.length - 1);
		System.arraycopy(missing, 0, newMissing, 0, rowIndicies.length - 1);
		System.arraycopy(empty, 0, newEmpty, 0, rowIndicies.length - 1);
		rowIndicies = newInternal;
		missing = newMissing;
		empty = newEmpty;
		return values[removed];
/*        boolean removed = internal[pos];
		// copy all the items after the item to be removed one position up
		System.arraycopy(internal, pos + 1, internal, pos, internal.length -
				(pos + 1));

		System.arraycopy(missing, pos + 1, missing, pos, internal.length -
				(pos + 1));

		System.arraycopy(empty, pos + 1, empty, pos, internal.length -
				(pos + 1));

		// copy the items into a new array
		boolean newInternal[] = new boolean[internal.length - 1];
		boolean newMissing[] = new boolean[internal.length-1];
		boolean newEmpty[] = new boolean[internal.length-1];
		System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
		System.arraycopy(missing, 0, newMissing, 0, internal.length - 1);
		System.arraycopy(empty, 0, newEmpty, 0, internal.length - 1);

		internal = newInternal;
		missing = newMissing;
		empty = newEmpty;
		return  new Boolean(removed);
		*/
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
		boolean[] newMissing = new boolean[rowIndicies.length + 1];
		boolean[] newEmpty = new boolean[rowIndicies.length + 1];
		System.arraycopy(rowIndicies, 0, newInternal, 0, rowIndicies.length);
		System.arraycopy(missing, 0, newMissing, 0, missing.length);
		System.arraycopy(empty, 0, newEmpty, 0, empty.length);
		newInternal[last] = idx;
		rowIndicies = newInternal;
		missing = newMissing;
		empty = newEmpty;

/*        int last = internal.length;
		boolean[] newInternal = new boolean[internal.length + 1];
		boolean[] newMissing = new boolean[internal.length + 1];
		boolean[] newEmpty = new boolean[internal.length + 1];
		System.arraycopy(internal, 0, newInternal, 0, internal.length);
		System.arraycopy(missing, 0, newMissing, 0, missing.length);
		System.arraycopy(empty, 0, newEmpty, 0, empty.length);
		newInternal[last] = ((Boolean)newEntry).booleanValue();

		internal = newInternal;
		missing = newMissing;
		empty = newEmpty;
		*/

   }

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
		}
		else {
			System.arraycopy(rowIndicies, 0, newInternal, 0, pos);
			System.arraycopy(rowIndicies, pos, newInternal, pos + 1,
			rowIndicies.length - pos);
			System.arraycopy(missing, 0, newMissing, 0, pos);
			System.arraycopy(missing, pos, newMissing, pos + 1, rowIndicies.length
					- pos);

			System.arraycopy(empty, 0, newEmpty, 0, pos);
			System.arraycopy(empty, pos, newEmpty, pos + 1, rowIndicies.length
					- pos);
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
		missing = newMissing;
		empty = newEmpty;
/*        if (pos > getNumRows()) {
			addRow(newEntry);
			return;
		}
		boolean[] newInternal = new boolean[internal.length + 1];
		boolean[] newMissing = new boolean[internal.length + 1];
		boolean[] newEmpty = new boolean[internal.length + 1];
		if (pos == 0) {
			System.arraycopy(internal, 0, newInternal, 1, getNumRows());
			System.arraycopy(missing, 0, newMissing, 1, getNumRows());
			System.arraycopy(empty, 0, newEmpty, 1, getNumRows());
		}
		else {
			System.arraycopy(internal, 0, newInternal, 0, pos);
			System.arraycopy(internal, pos, newInternal, pos + 1, internal.length
					- pos);

			System.arraycopy(missing, 0, newMissing, 0, pos);
			System.arraycopy(missing, pos, newMissing, pos + 1, internal.length
					- pos);

			System.arraycopy(empty, 0, newEmpty, 0, pos);
			System.arraycopy(empty, pos, newEmpty, pos + 1, internal.length
					- pos);
		}
		newInternal[pos] = ((Boolean)newEntry).booleanValue();
		internal = newInternal;
		missing = newMissing;
		empty = newEmpty;
		*/

   }

   public void removeRowsByIndex(int[] indices) {
		HashSet toRemove = new HashSet(indices.length);
		for (int i = 0; i < indices.length; i++) {
			Integer id = new Integer(indices[i]);
			toRemove.add(id);
		}
		int newInternal[] = new int[rowIndicies.length - indices.length];
		boolean newMissing[] = new boolean[rowIndicies.length - indices.length];
		boolean newEmpty[] = new boolean[rowIndicies.length - indices.length];
		int newIntIdx = 0;
		for (int i = 0; i < getNumRows(); i++) {
			// check if this row is in the list of rows to remove
			// if this row is not in the list, copy it into the new internal
		 if(!toRemove.contains(new Integer(i))) {
				newInternal[newIntIdx] = rowIndicies[i];
				newMissing[newIntIdx] = missing[i];
				newEmpty[newIntIdx] = empty[i];
				newIntIdx++;
			}
		}
		rowIndicies = newInternal;
		missing = newMissing;
		empty = newEmpty;
/*        HashSet toRemove = new HashSet(indices.length);
		for (int i = 0; i < indices.length; i++) {
			Integer id = new Integer(indices[i]);
			toRemove.add(id);
		}
		boolean newInternal[] = new boolean[internal.length - indices.length];
		boolean newMissing[] = new boolean[internal.length - indices.length];
		boolean newEmpty[] = new boolean[internal.length - indices.length];

		int newIntIdx = 0;
		for (int i = 0; i < getNumRows(); i++) {
			// check if this row is in the list of rows to remove
			//Integer x = (Integer)toRemove.get(new Integer(i));
			// if this row is not in the list, copy it into the new internal
			//if (x == null) {
		 if(!toRemove.contains(new Integer(i))) {
				newInternal[newIntIdx] = internal[i];
				newMissing[newIntIdx] = missing[i];
				newEmpty[newIntIdx] = empty[i];
				newIntIdx++;
			}
		}
		internal = newInternal;
		missing = newMissing;
		empty = newEmpty;
		*/
   }
   public void setValueToMissing(boolean b, int row) {
	   missing[row] = b;
   }

   public void setValueToEmpty(boolean b, int row) {
	   empty[row] = b;
   }

   public boolean isValueMissing(int row) {
	   return missing[row];
   }

   public boolean isValueEmpty(int row) {
	   return empty[row];
	}
}
