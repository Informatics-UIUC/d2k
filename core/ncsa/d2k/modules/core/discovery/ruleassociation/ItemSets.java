package ncsa.d2k.modules.core.discovery.ruleassociation;

import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;

/**
 * This class holds itemsets for ruleassociation. Each set is actually
 * an example containing a list of items the set contains. The sets
 * are represented as integers, with another string array containing the actual
 * bined string representation of the int. This is much more compact than
 * a vertical table typically.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class ItemSets implements Serializable {
	/** number of examples. */
	public int numExamples;

	/** this array contains a list of attribute names of those output attributes. */
	public String [] outputNames = null;

	/** this array contains a list of attribute names of those output attributes. */
	public int [] outputIndices = null;

	/** for each unique item, this hashtable contains it's frequency count and it's
	 *  order in terms of frequency. */
	public HashMap unique = new HashMap ();

	/** this is the list of unique attribute value names ordered by frequency. */
	public String [] names;

	/** for each example contains a boolean array with an entry for each item,
	 *  set to true only if the item is represented in the example or not.  */
	private boolean [][] itemFlags;

	/** holds some method specific data.*/
	public Object userData;

    public ItemSets(Table vt) {
		int numColumns = vt.getNumColumns ();
		int numRows = this.numExamples = vt.getNumRows ();

		int [] attributes;
		boolean isExampleTable = vt instanceof ExampleTable;
		if (isExampleTable) {
			ExampleTable et = (ExampleTable)vt;
			int [] inputs = et.getInputFeatures ();
			int [] outputs = et.getOutputFeatures ();
			numColumns =inputs.length+outputs.length;
			attributes = new int [numColumns];
			System.arraycopy(inputs,0,attributes,0,inputs.length);
			System.arraycopy(outputs,0,attributes,inputs.length,outputs.length);

			// Construct the output names.
			outputNames = new String [outputs.length];
			for (int i = 0 ; i < outputNames.length; i++)
				outputNames[i] = new String (vt.getColumnLabel (outputs[i]));
		} else {
			attributes = new int [numColumns];
			for (int i = 0; i < numColumns; i++)
				attributes[i] = i;
		}

		// Allocate an array of string for each column prefix.
		String [] prefix = new String [numColumns];

		// Init each prefix, if there is no column label, use our own
		// home brew.
		for (int i = 0 ; i < numColumns ; i++) {
			String tmp = vt.getColumnLabel (attributes[i]);
			if (tmp != null && tmp.length() > 0)
				prefix [i] = tmp+"^";
			else
				prefix [i] = "^"+Integer.toString (i)+"^";
		}

		/** Construct the table containing the unique attributes and their
		 *  counts. */

		int counter = 0;
		char [] chars = new char [1024];
		ArrayList set = new ArrayList();
		for (int i = 0 ; i < numRows ; i++) {
			for (int j = 0 ; j < numColumns ; j++) {
				String a = prefix[j];
				int alen = a.length();
				String b = vt.getString (i, attributes[j]);
				int blen = b.length();
				if ((alen+blen) > chars.length)
					chars = new char [alen+blen];
				a.getChars(0,alen,chars,0);
				b.getChars(0,blen,chars,alen);
				String item_desc = new String(chars,0,alen+blen);
				int [] cnt_and_id = (int []) unique.get (item_desc);
				if (cnt_and_id == null) {
					cnt_and_id = new int [2];
					cnt_and_id[0] = 1;
					cnt_and_id[1] = counter++;
					unique.put (item_desc, cnt_and_id);
					set.add(cnt_and_id);
				} else
					cnt_and_id[0]++;
			}
		}

		// create the arrays to sort.
		int [][] vals = new int [set.size()][];
		for (int i = 0 ; i < set.size();i++) vals[i] = (int[])set.get(i);
		int [] ind = new int [set.size()];
		for (int i = 0 ; i < set.size() ; i++) ind[i] = i;
		this.quickSort (ind, vals, 0, ind.length-1);
		names = new String [unique.size()];
		for (int i = 0 ; i < ind.length ; i++) {
			vals[ind[i]][1] = i;
		}

		// Now we have the order, the index value in the int array keyed by
		// name in the unique names hashmap is set correctly. Now we will just
		// create an array of unique names in the order sorted by frequency.
		names = new String [unique.size()];
		Iterator enum = unique.values().iterator();
		Iterator enum2 = unique.keySet().iterator();
		while (enum.hasNext ()) {
			int [] tmp = (int[]) enum.next ();
			String tmpName = (String) enum2.next ();
			names[tmp[1]] = tmpName;
		}

		// We will now sort the unique items on the basis of their frequency distribution
		// with those items with the highest frequency distribution having the highest indices.
		// This will allow some optimization fo the search space.

/*		HashMap result = new HashMap ();
		int numUnique = unique.size ();
		names = new String [numUnique];
		for (int i = 0 ; i < numUnique ;i++) {
			Iterator enum = unique.values().iterator();
			Iterator enum2 = unique.keySet().iterator();
			int smallestFrequency = numRows;
			String smallestName = null;
			int [] smallestInts = null;
			while (enum.hasNext ()) {
				int [] tmp = (int[]) enum.next ();
				String tmpName = (String) enum2.next ();
				if (tmp[0] <= smallestFrequency) {
					smallestName = tmpName;
					smallestInts = tmp;
					smallestFrequency = tmp[0];
				}
			}
			smallestInts[1] = i;
			names[i] = smallestName;
			result.put (smallestName, smallestInts);
			unique.remove (smallestName);
		}
		unique = result;
*/
		// Now construct the new representation of the vertical table, where each
		// entry is represented by an integer.
		int [][] documents = new int [numRows][numColumns];
		for (int i = 0 ; i < numRows ; i++) {
			for (int j = 0 ; j < numColumns ; j++) {
				String a = prefix[j];
				int alen = a.length();
				String b = vt.getString (i, attributes[j]);
				int blen = b.length();
				if ((alen+blen) > chars.length)
					chars = new char [alen+blen];
				a.getChars(0,alen,chars,0);
				b.getChars(0,blen,chars,alen);
				String name = new String(chars,0,blen+alen);
				int [] pz = (int[])unique.get(name);
				documents[i][j] = pz[1];
			}
		}

		//////////////////////
		// First, set up the item flags, set the bit associated with each
		// element in each set.
		// this boolean array has a flag for each possible item for each document to
		// indicate if the document contains the item or not.
		itemFlags = new boolean [numRows][unique.size()];
		for (int i = 0 ; i < numRows ; i++)
			for (int j = 0 ; j < numColumns ; j++)
				itemFlags[i][documents[i][j]] = true;

		// Figure out what the indices of those items that are outputs
		// are.
		String[] targets = outputNames;
		Iterator keys = unique.keySet().iterator();
		Iterator indxs = unique.values().iterator();
		ArrayList list = new ArrayList ();

		// for each of the attributes, see if the inputs include the attribute.

		while (keys.hasNext ()) {
			String name = (String) keys.next ();
			int[] indx = (int[]) indxs.next ();
			for (int i = 0 ; i < targets.length; i++)
				if (name.startsWith (targets[i]))
					list.add (indx);
		}

		// Put the indexes into the list.
		int size = list.size ();
		if (size != 0) {
			outputIndices = new int [size];
			for (int i = 0 ; i < size ; i++)
				outputIndices[i] = ((int[])list.get (i))[1];
		}
	}

	/**
		Perform a quicksort on the data using the Tri-median method to select the pivot.
		@param l the first rule.
		@param r the last rule.
	*/
	private void quickSort(int [] ind, int [][] vals, int l, int r) {

		int pivot = (r + l) / 2;
		int pivotVal = vals[ind[pivot]][0];

		// from position i=l+1 start moving to the right, from j=r-2 start moving
		// to the left, and swap when the fitness of i is more than the pivot
		// and j's fitness is less than the pivot
		int i = l;
		int j = r;
		while (i <= j) {
			while ((i < r) && (vals[ind[i]][0] > pivotVal))
				i++;
			while ((j > l) && (vals[ind[j]][0] < pivotVal))
				j--;
			if (i <= j) {
				int swap = ind[i];
				ind[i] = ind[j];
				ind[j] = swap;
				i++;
				j--;
			}
		}

		// sort the two halves
		if (l < j)
			quickSort(ind, vals, l, j);
		if (i < r)
			quickSort(ind, vals, j+1, r);
	}

	/**
	 * Returns an 2D array of booleans with one row for each example, and one bit
	 * in each row for each possible item. The bit is set if the item exists in the
	 * set.
	 * @return a 2D array of booleans indicating if an item was purchased or not for each example.
	 */
	public boolean [][] getItemFlags () {
		return itemFlags;
	}

}