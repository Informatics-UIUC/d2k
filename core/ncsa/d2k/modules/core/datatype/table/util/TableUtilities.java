package ncsa.d2k.modules.core.datatype.table.util;

import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Contains useful methods to find statistics about columns of a Table.
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
}