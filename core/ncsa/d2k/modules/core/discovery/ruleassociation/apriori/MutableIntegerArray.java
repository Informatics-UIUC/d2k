package ncsa.d2k.modules.core.discovery.ruleassociation.apriori;

final class MutableIntegerArray {
	final boolean debug = false;
	/** the integers. */
	int [] integers = null;

	/** the number of entries occupying the array., */
	int count;

	/** means the array needs to be compressed. */
	boolean dirty = false;

	MutableIntegerArray (int size) {
		integers = new int [size];
		count = 0;
	}
	MutableIntegerArray (MutableIntegerArray mia) {
		integers = mia.integers;
		count = mia.count;
	}
	MutableIntegerArray (int [] t) {
		integers = t;
		count = t.length;
	}

	final public String toString () {
		String r = "{";
		for (int i = 0 ; i < count-1; i++)
			r += Integer.toString (integers[i])+",";

		if (count == 0)
			r+="}";
		else
			r += Integer.toString (integers[count-1])+"}";
		r+=":"+count+":";
		return r;
	}

	/**
		Add an integer to the array.
		@param val the value to add.
	*/
	final void add (int val) {
		integers[count++] = val;
	}

	/**
		Add all the elements from the <code>MutableIntegerArray</code>
		passed in.
		@param addMe the mutable array to copy into this one.
	*/
	final void add (MutableIntegerArray addMe) {
		System.arraycopy (addMe.getArray(), 0, integers, 0, addMe.count);
		this.count = addMe.count;
	}

	/**
		Find out what items these two sorted mutable integer arrays
		share.
		@param addMe the array to intersect.
	*/
	final MutableIntegerArray intersection (MutableIntegerArray addMe) {
		if (debug) {
			System.out.println ("Intersection-----");
			System.out.println ("set a:"+this.toString ());
			System.out.println ("set b:"+addMe.toString());
		}
		int size = addMe.count < this.count ? addMe.count : this.count;
		MutableIntegerArray mia = new MutableIntegerArray (size);
		int othersIndex = 0;
		done:
			for (int i = 0 ; i < this.count ; i++) {

				// Find the first entry in the other integer array that
				// is greater than or equal to the current entry.
				while (addMe.get (othersIndex) < integers[i]) {
					othersIndex++;
					if (othersIndex >= addMe.count)
						break done;
				}

				// If they are equal, add the item to the new integer array.
				if (addMe.get (othersIndex) == integers[i]) {
					mia.add (integers[i]);
				}
			}
		if (debug)
			System.out.println ("result:"+mia.toString());
		return mia;
	}

	/**
		count the items these two sorted mutable integer arrays
		share.
		@param addMe the array to intersect.
	*/
	final int countIntersection (MutableIntegerArray addMe) {
		int othersIndex = 0;
		int intersectionCount = 0;
		if (debug) {
			System.out.println ("Count-----");
			System.out.println ("set a:"+this.toString ());
			System.out.println ("set b:"+addMe.toString());
		}
		done:
			for (int i = 0 ; i < this.count ; i++) {

				// Find the first entry in the other integer array that
				// is greater than or equal to the current entry.
				while (addMe.get (othersIndex) < integers[i]) {
					othersIndex++;
					if (othersIndex >= addMe.count)
						break done;
				}

				// If they are equal, add the item to the new integer array.
				if (addMe.get (othersIndex) == integers[i])
					intersectionCount++;
			}
		if (debug)
			System.out.println ("result:"+intersectionCount);
		return intersectionCount;
	}

	/**
		Reset the size to zero.
	*/
	final void reset () {
		count = 0;
	}

	/**
		Delete an item.
	*/
	final void delete (int which) {
		System.arraycopy (integers, which, integers, which+1, integers.length - (which+1));
		count--;
	}
	final int [] getArray () {
		if (dirty)
			clean ();
		return integers;
	}
	final int get (int i) {
		if (dirty)
			clean ();
		return integers[i];
	}
	final void clean () {
	}
}
