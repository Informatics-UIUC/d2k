package ncsa.d2k.modules.core.discovery.ruleassociation;

/**
	This class manages a list of rules, a two dim array of integer
	values where the last two entry in each of the arrays is the support
	and confidence values. This class will sort the matrix by generating
	a secondary index which represents the ordering of the items. It
	can sort on confidence first then support, or visa versa.
*/
class RuleDataModel {
	int [] order = null;
	int [][] rules = null;

	/**
		given the rules, sort into the default order, confidence first
		then support.
		@param rules the rule set.
	*/
	RuleDataModel (int [][] rules) {

		// The order each rule
		this.rules = rules;
		this.sortConfidenceSupport ();
	}

	int primaryKey = 1;
	int secondaryKey = 2;

	/**
		Compare two rules on the basis of a primary key first and then a secondary
		key. Primary and secondary keys can be either of support or confidence.
		@param l the first entry to compare
		@param r the second entry.
	*/
	public int compare (int l, int r) {

		// First check on the primary keys
		int primary1 = rules [order [l]][rules[order [l]].length - primaryKey];
		int primary2 = rules [order [r]][rules[order [r]].length - primaryKey];
		if (primary1 > primary2)
			return 1;
		if (primary1 < primary2)
			return -1;

		// Primarys are equal, check the secondary keys.
		int secondary1 = rules [order [l]][rules[order [l]].length - secondaryKey];
		int secondary2 = rules [order [r]][rules[order [r]].length - secondaryKey];
		if (secondary1 > secondary2)
			return 1;
		if (secondary1 < secondary2)
			return -1;
		return 0;
	}

	/**
		Compare two rules on the basis of a primary key first and then a secondary
		key. Primary and secondary keys can be either of support or confidence.
		@param l the first entry to compare
		@param r the second entry.
	*/
	public int compare (int l) {

		// First check on the primary keys
		int primary1 = rules [order [l]][rules[order [l]].length - primaryKey];
		if (primary1 > bestPrimary)
			return 1;
		if (primary1 < bestPrimary)
			return -1;

		// Primarys are equal, check the secondary keys.
		int secondary1 = rules [order [l]][rules[order [l]].length - secondaryKey];
		if (secondary1 > bestSecondary)
			return 1;
		if (secondary1 < bestSecondary)
			return -1;
		return 0;
	}

	/**
		Swap two entries int he order array.
		@param l the first entry.
		@param r the second entry.
	*/
	public void swap (int l, int r) {
		int swap = order [l];
		order [l] = order [r];
		order [r] = swap;
	}

	/**
		Set the pivot value for the quicksort.
	*/
	int bestPrimary;
	int bestSecondary;
	private void setPivot (int l) {
		this.bestPrimary = rules [order [l]][rules[order [l]].length - primaryKey];
		this.bestSecondary = rules [order [l]][rules[order [l]].length - secondaryKey];
	}

	/**
		Perform a quicksort on the data using the Tri-median method to select the pivot.
		@param l the first rule.
		@param r the last rule.
	*/
	private void quickSort(int l, int r) {

		int pivot = (r + l) / 2;
		this.setPivot (pivot);

		// from position i=l+1 start moving to the right, from j=r-2 start moving
		// to the left, and swap when the fitness of i is more than the pivot
		// and j's fitness is less than the pivot
		int i = l;
		int j = r;
		while (i <= j) {
			while ((i < r) && (this.compare (i) > 0))
				i++;
			while ((j > l) && (this.compare (j) < 0))
				j--;
			if (i <= j) {
				this.swap (i, j);
				i++;
				j--;
			}
		}

		// sort the two halves
		if (l < j)
			quickSort(l, j);
		if (i < r)
			quickSort(j+1, r);
	}

	/**
		Bubble sort on confidence as primary key.
	*/
	public void sortConfidenceSupport () {
		primaryKey = 1;
		secondaryKey = 2;
		int numRules = rules.length;

		// Create and init a new handle array.
		order = new int [numRules];
		for (int i = 0 ; i < numRules ; i++)
			order[i] = i;

		// Sort.
		this.quickSort (0, numRules-1);
	}

	/**
		Bubble sort on support as primary key.
	*/
	public void sortSupportConfidence () {
		primaryKey = 2;
		secondaryKey = 1;
		int numRules = rules.length;

		// Create and init a new handle array.
		order = new int [numRules];
		for (int i = 0 ; i < numRules ; i++)
			order[i] = i;

		// Sort.
		this.quickSort (0, rules.length-1);
	}

	/**
		The order we originally get the rules in is sorted on the basis
		of the size of the rules.
	*/
	public void sortOnSize () {
		int numRules = rules.length;
		order = new int [numRules];
		for (int i = 0 ; i < numRules ; i++)
			order[i] = i;
	}

	/**
		Get the value at the given row and column.
	*/
	public int get (int i, int j) {
		return rules [order [i]][j];
	}

	/**
		Get the value at the given row and column.
	*/
	public int getSize (int i) {
		return rules [order [i]].length-2;
	}

	/**
		Get the value at the given row and column.
	*/
	public int getNumRules () {
		return rules.length;
	}

	/**
		Returns the index of the rule displayed in column i.
	*/
	public int getRuleIndex (int i) {
		return order [i];
	}

	/**
		Get the support at the given column.
	*/
	public int getSupport (int i) {
		return rules[order [i]] [rules [order [i]].length-2];
	}
	/**
		Get the confidence at the givend column.
	*/
	public int getConfidence (int i) {
		return rules[order [i]] [rules [order [i]].length-1];
	}

	/**
		Returns the status of the attribute in the rule, input,
		output or not represented
		@param rule the rule id number.
		@param attribute the number of the attribute.
	*/
	public byte getAttributeType (int rule, int attribute) {
		int ruleSize = this.getSize (rule);

		// The last item is the output
		if (rules[order [rule]] [ruleSize-1] == attribute)
			return (byte) 'O';

		// All others are inputs.
		for (int i = 0 ; i < ruleSize - 1; i++)
			if (rules[order [rule]] [i] == attribute)
				return (byte) 'I';

		// Attribute not represented in this rule.
		return (byte) ' ';
	}

}