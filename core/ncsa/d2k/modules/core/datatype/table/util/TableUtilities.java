package ncsa.d2k.modules.core.datatype.table.util;

import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Contains useful methods to find statistics about columns of a Table.
 * Also methods for comparing/sorting by multiple columns of a Table.
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author clutter
 * @version 1.0
 */
public class TableUtilities {
	public static final int MINIMUM = 0;
	public static final int MAXIMUM = 1;

	private TableUtilities() {}

	/**
	 * Get all the statistics about a scalar column.  This will calculate the mean,
	 * median, variance, standard deviation, minimum, and maximum and return them
	 * in a ScalarStatistics object.
	 * @param table
	 * @param colNum
	 * @return
	 */
	public static ScalarStatistics getScalarStatistics(Table table, int colNum) {
		if(!table.isColumnNumeric(colNum))
			return null;

         double sample_mean, sample_variance;
         double[] d = new double[table.getNumRows()];

		 double median, stddev;
         double total = 0, max = Double.MIN_VALUE, min = Double.MAX_VALUE;
         for (int i = 0; i < d.length; i++) {

            d[i] = table.getDouble(i, colNum);
            total += d[i];

            if (table.getDouble(i, colNum) > max)
               max = table.getDouble(i, colNum);
            if (table.getDouble(i, colNum) < min)
               min = table.getDouble(i, colNum);
         }

         sample_mean = total/(double)table.getNumRows();

         Arrays.sort(d);
         median = (d[(int)Math.ceil(d.length/2.0)] + d[(int)Math.floor(d.length/2.0)])/2.0;

         total = 0; // for calculating sample variance
         for (int i = 0; i < d.length; i++)
            total += (d[i] - sample_mean) * (d[i] - sample_mean);

         sample_variance = total / (double)(d.length - 1); // unbiased estimator

         stddev = Math.sqrt(sample_variance);
		 return new ScalarStatistics(sample_mean, median, sample_variance, stddev, min, max);
	}

	/**
	 * Get all the unique values in a specific column of a Table.
	 * @param table the table
	 * @param colNum the column to find the unique values for
	 * @return an array of Strings containing all the unique values in the specified column
	 */
	public static String[] uniqueValues(Table table, int colNum) {
		int numRows = table.getNumRows();

		// count the number of unique items in this column
		HashSet set = new HashSet();
		for(int i = 0; i < numRows; i++) {
			String s = table.getString(i, colNum);
			if(!set.contains(s))
				set.add(s);
		}

		String[] retVal = new String[set.size()];
		int idx = 0;
		Iterator it = set.iterator();
		while(it.hasNext()) {
			retVal[idx] = (String)it.next();
			idx++;
		}
		return retVal;
	}

	/**
	 * Expand an array by one.
	 * @param orig
	 * @return
	 */
	private static int[] expandArray(int[] orig) {
		int [] newarray = new int[orig.length+1];
		System.arraycopy(orig, 0, newarray, 0, orig.length);
		return newarray;
	}


	public static Table getPDFTable(Table t, int idx, double mean, double stdDev) {
		double[] vals = new double[t.getNumRows()];
		t.getColumn(vals, idx);

		double[] pdfs = new double[t.getNumRows()];
		for(int i = 0; i < t.getNumRows(); i++) {
			pdfs[i] = pdfCalc(vals[i], mean, stdDev);
		}

		Column[] c = new Column[2];
		c[0] = new DoubleColumn(vals);
		c[1] = new DoubleColumn(pdfs);
		TableImpl tbl = new TableImpl(c);
		return tbl;
	}

	private static double pdfCalc(double X, double mean, double stdDev) {
		double exp = Math.pow( (X-mean), 2);
		exp *= -1;
		exp /= (2*Math.pow(stdDev, 2));

		double numerator = Math.pow(Math.E, exp);
		double denom = (stdDev*Math.pow((2*Math.PI), .5));
		return numerator/denom;
	}

	/**
	 * Find all the unique values of a column, as well as a count of their
	 * frequencies.  A HashMap is returned, with the unique values as the keys
	 * and their frequencies as the values.  The frequencies are stored as Integers.
	 * @param table
	 * @param colNum
	 * @return
	 */
	public static HashMap uniqueValuesWithCounts(Table table, int colNum) {
		int [] outtally = new int[0];
		HashMap outIndexMap = new HashMap();
		int numRows = table.getNumRows();
		for(int i = 0; i < numRows; i++) {
			String s = table.getString(i, colNum);

			if(outIndexMap.containsKey(s)) {
				Integer in = (Integer)outIndexMap.get(s);
				outtally[in.intValue()]++;
			}
			else {
				outIndexMap.put(s, new Integer(outIndexMap.size()));
				outtally = expandArray(outtally);
				outtally[outtally.length-1] = 1;
			}
		}

		HashMap retVal = new HashMap();
		Iterator ii = outIndexMap.keySet().iterator();
		while(ii.hasNext()) {
			String val = (String)ii.next();
			Integer idx = (Integer)outIndexMap.get(val);
			retVal.put(val, new Integer(outtally[idx.intValue()]));
		}
		return retVal;
	}


	public static boolean equalTo(Table table, int r1, int c1, int r2, int c2) {
		return false;
	}

	/**
	 * Get both the minimum and maximum of a column.  An array of doubles of length
	 * 2 is returned, with the first element being the minimum and the second
	 * element being the maximum.
	 * @param table
	 * @param colNum
	 * @return
	 */
	public static double[] getMinMax(Table table, int colNum) {
         double total = 0, max = Double.MIN_VALUE, min = Double.MAX_VALUE;
		 int numRows = table.getNumRows();
		 double d;
         for (int i = 0; i < numRows; i++) {

            d = table.getDouble(i, colNum);
            total += d;

            if (d > max)
               max = d;
            if (d < min)
               min = d;
         }
		 double[] retVal = new double[2];
		 retVal[MINIMUM] = min;
		 retVal[MAXIMUM] = max;
		 return retVal;
	}

	/**
	 * Get the mean of a column of a Table.
	 * @param table
	 * @param colNum
	 * @return
	 */
	public static double mean(Table table, int colNum) {
         double sample_mean;

         double total = 0, max = Double.MIN_VALUE, min = Double.MAX_VALUE;
		 int numRows = table.getNumRows();
         for (int i = 0; i < numRows; i++)
            total += table.getDouble(i, colNum);

         sample_mean = total/(double)table.getNumRows();
		 return sample_mean;
	}

	/**
	 * Get the median of a column of a Table.
	 * @param table
	 * @param colNum
	 * @return
	 */
	public static double median(Table table, int colNum) {
         double[] d = new double[table.getNumRows()];
		 table.getColumn(d, colNum);

         Arrays.sort(d);
         double median = (d[(int)Math.ceil(d.length/2.0)] + d[(int)Math.floor(d.length/2.0)])/2.0;
		 return median;
	}

	/**
	 * Get the standard deviation of a column of a table.
	 * @param table
	 * @param colNum
	 * @return
	 */
	public static double standardDeviation(Table table, int colNum) {
         double sample_mean, sample_variance;
         double[] d = new double[table.getNumRows()];

		 double median, stddev;
         double total = 0, max = Double.MIN_VALUE, min = Double.MAX_VALUE;

         for (int i = 0; i < d.length; i++) {
            d[i] = table.getDouble(i, colNum);
            total += d[i];

            if (table.getDouble(i, colNum) > max)
               max = table.getDouble(i, colNum);
            if (table.getDouble(i, colNum) < min)
               min = table.getDouble(i, colNum);
         }

         sample_mean = total/(double)table.getNumRows();

         Arrays.sort(d);
         median = (d[(int)Math.ceil(d.length/2.0)] + d[(int)Math.floor(d.length/2.0)])/2.0;

         total = 0; // for calculating sample variance
         for (int i = 0; i < d.length; i++)
            total += (d[i] - sample_mean) * (d[i] - sample_mean);

         sample_variance = total / (double)(d.length - 1); // unbiased estimator

		 return Math.sqrt(sample_variance);
	}

	/**
	 * Get the variance of a column of a Table.
	 * @param table
	 * @param colNum
	 * @return
	 */
	public static double variance(Table table, int colNum) {
         double sample_mean, sample_variance;
         double[] d = new double[table.getNumRows()];

		 double median, stddev;
         double total = 0, max = Double.MIN_VALUE, min = Double.MAX_VALUE;

         for (int i = 0; i < d.length; i++) {
            d[i] = table.getDouble(i, colNum);
            total += d[i];

            if (table.getDouble(i, colNum) > max)
               max = table.getDouble(i, colNum);
            if (table.getDouble(i, colNum) < min)
               min = table.getDouble(i, colNum);
         }

         sample_mean = total/(double)table.getNumRows();

         Arrays.sort(d);
         median = (d[(int)Math.ceil(d.length/2.0)] + d[(int)Math.floor(d.length/2.0)])/2.0;

         total = 0; // for calculating sample variance
         for (int i = 0; i < d.length; i++)
            total += (d[i] - sample_mean) * (d[i] - sample_mean);

         sample_variance = total / (double)(d.length - 1); // unbiased estimator

		 return sample_variance;
	}


	/** a helpful function for copying data from table to table
		copies a value from table1 to table2.Assumes columns are of
		the same type

		*/
	public static void setValue(Table t1, int row1, int col1,
								MutableTable t2, int row2, int col2){
		
		if(t1.isColumnNumeric(col1)){
			t2.setDouble(t1.getDouble(row1,col1), row2, col2);
		}else{
			t2.setObject(t1.getObject(row1,col1), row2, col2);
		}
	}

	//////////////////////
	///Multi-Column Ops
	//////////////////////
	/**
		multiSortIndex.
		
		takes a table and an array of column indices to sort by.
		The resulting array of row indices will contain row pointers
		ordered such that rows are ordered by the first column index,
		then the second, etc.

		@param tbl the table to multisort
		@param sortByCols the columns to sort by

		@return an int[] of size tbl.getNumRows() representing the 
				sorted order of tbl
	**/
	public static int[] multiSortIndex(Table tbl, int[] sortByCols){
		int size=tbl.getNumRows();
		int[] order=new int[size];
		for(int i=0; i<size; i++){
			order[i]=i;
		}
		
		multiQuickSort(tbl, sortByCols, order, 0, size-1);
		multiInsertionSort(tbl, sortByCols, order);
		return order;
	}

	/** multiQuickSort.
		
		DO NOT use this function directly, as it does not completely
		sort the table (an insertion sort must be done afterwards,
		use multiSortIndex)
	*/
	private static void multiQuickSort(	Table tbl, int[] sortByCols, int[] order, 
							int l, int r){

		if(r-l<=3){
			return;
		}
		int pivot;

		int i=(r+l)/2;		

		if(compareMultiCols(tbl,tbl,order[l],order[i],sortByCols,sortByCols)>0)
			swap(order, l, i);
		if(compareMultiCols(tbl,tbl,order[l],order[r],sortByCols,sortByCols)>0)
			swap(order, l, r);
		if(compareMultiCols(tbl,tbl,order[i],order[r],sortByCols,sortByCols)>0)
			swap(order, i, r);

		swap(order, i, r-1);

		pivot=r-1;			
		
		i=l+1;	
		int j=r-2;

		while(j>i){

			while(	(compareMultiCols(tbl,tbl,order[i],
						order[pivot],sortByCols,sortByCols)<=0)
						&& (i<j)){
				i++;
			}
			while(	(compareMultiCols(tbl,tbl,order[j],
						order[pivot],sortByCols,sortByCols)>=0)
						&& (i<j)){
				j--;
			}
			
			if(i<j){
				swap(order, i, j);	
			}	
		}
		swap(order, r-1, j);

		multiQuickSort(tbl, sortByCols, order, l, i-1);
		multiQuickSort(tbl, sortByCols, order, j+1, r);
	}

	/**
		multiInsertionSort.

		A slow sort alg meant to be used after the quickSort function
		contained in this class

		@param tbl the table to multisort
		@param sortByCols the columns to sort by

		@return an int[] of size tbl.getNumRows() representing the 
				sorted order of tbl

	*/
	public static void multiInsertionSort(	Table tbl, int[] sortByCols, 
											int[] order){

		int size=order.length;
		int i;
		int j;
		int v;
		for(j=1; j<size; j++){
			i=j-1;
			v=order[j];
			while((i>=0)&&(0<compareMultiCols(tbl,tbl,order[i],
								v,sortByCols,sortByCols))){
				order[i+1]=order[i];
				i--;
			}
			order[i+1]=v;
		}
	}

	
	/**
		compareMultiCols.
		
		if the value of the row in table 1 is greater, returns 1.
		if they are equal returns 0
		if less than, -1

		e1>e2 -> 1
		e1=e2 -> 0
		e1<e2 -> -1
	**/
	public static int compareMultiCols(Table t1, Table t2, int row1, int row2,
								int[] cols1, int[] cols2){
		
		int numCompareCols=cols1.length;
		int eq=0;
		for(int i=0; i<numCompareCols; i++){
			eq=compareValues(t1,row1,cols1[i],t2,row2,cols2[i]);
			if(eq!=0){
				return eq;
			}
		}
		return 0;
	}

	private static void swap(int[] ar, int i, int j){
		int t=ar[i];
		ar[i]=ar[j];
		ar[j]=t;
	}
	/**
		Return 0 if they
    	are the same, greater than zero if element is greater,
	 	and less than zero if element is less.

		Assumes columns are of same type.
		*/

	public static int compareValues(Table t1, int row1, int col1,
									Table t2, int row2, int col2){
		
		int type=t1.getColumnType(col1);
		
		//the numeric case
		if(t1.isColumnNumeric(col1)){
			double d1=t1.getDouble(row1,col1);
			double d2=t2.getDouble(row2,col2);
			if(d1==d2)
				return 0;
			if(d1>d2)
				return 1;

			return -1;
		}
		
		int it=-2;
		//the other cases	
		switch(type){
			/*case (ColumnTypes.INTEGER) : {

				break;
			}
			case (ColumnTypes.FLOAT) : {
				break;
			}
			case (ColumnTypes.SHORT) : {
				break;
			}
			case (ColumnTypes.LONG) : {
				break;
			}
			*/
			case (ColumnTypes.STRING) : {
				it=t1.getString(row1,col1).
						compareTo(t2.getString(row2,col2));
				break;		
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				it=compareChars(t1.getChars(row1,col1),
								t2.getChars(row2,col2));
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				it=compareBytes(t1.getBytes(row1,col1),
								t2.getBytes(row2,col2));
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				if(t1.getBoolean(row1,col1)==t2.getBoolean(row2,col2)){
					it=0;
				}else{
					it=1;
				}
				
				break;
			}
			case (ColumnTypes.OBJECT) : {
				boolean null1=false;
				boolean null2=false;
				Object ob1;
				Object ob2;
				ob1=t1.getObject(row1,col1);
				if(ob1==null){
					null1=true;
				}
				ob2=t2.getObject(row2,col2);
				if(ob2==null){
					null2=true;
				}
				if(null1){
					if(null2){
						return 0;
					}
					return -1;
				}
				if(null2)
					return 1;
				if(ob1.equals(ob2))
					return 0;

				return t1.getString(row1,col1).
						compareTo(t2.getString(row2,col2));
			}
			case (ColumnTypes.BYTE) : {
				byte[] b1 = new byte[1];
				b1[0] = t1.getByte(row1,col1);
				byte[] b2 = new byte[1];
				b2[0] = t2.getByte(row2,col2);
				it= compareBytes(b1, b2);
				break;
			}
			case (ColumnTypes.CHAR) : {
				byte[] b1 = new byte[1];
				b1[0] = t1.getByte(row1,col1);
				byte[] b2 = new byte[1];
				b2[0] = t2.getByte(row2,col2);
				it= compareBytes(b1, b2);
				break;
			}/*
			case (ColumnTypes.DOUBLE) : {
				break;

			}*/
			default : {
				System.err.println("TableUtilities:CompareVals: Error");
			}
		}
		return it;
	}
    /**
	 * Compare two byte arrays
     * @param b1 the first byte array to compare
     * @param b2 the second byte array to compare
     * @return -1, 0, 1
     */
    private static int compareBytes (byte[] b1, byte[] b2) {
        if (b1 == null) {
            if (b2 == null)
                return  0;
            else
                return  -1;
        }
        else if (b2 == null)
            return  1;
        if (b1.length < b2.length) {
            for (int i = 0; i < b1.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  -1;
        }
        else if (b1.length > b2.length) {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  1;
        }
        else {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  0;
        }
    }
		
	    /**
	 * Compare two char arrays
     * @param b1 the first char[] to compare
     * @param b2 the second char[] to compare
     * @return -1, 0, 1
     */
    private static int compareChars (char[] b1, char[] b2) {
        if (b1 == null) {
            if (b2 == null)
                return  0;
            else
                return  -1;
        }
        else if (b2 == null)
            return  1;
        if (b1.length < b2.length) {
            for (int i = 0; i < b1.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  -1;
        }
        else if (b1.length > b2.length) {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  1;
        }
        else {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  0;
        }
    }
	
}
