/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.discovery.ruleassociation.apriori;
import java.io.*;
import java.util.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	AprioriModule.java

*/
public class AprioriModule extends ncsa.d2k.infrastructure.modules.ComputeModule
/*#end^2 Continue editing. ^#&*/
implements Serializable
{
	/**
		Start a timer when we begin execution.
	*/
	long startTime;
	public void beginExecution () {
		startTime = System.currentTimeMillis ();
	}

	public void endExecution () {
		super.endExecution ();
		System.out.println ("Elapsed Wallclock Time : "
			+(System.currentTimeMillis()-startTime));
		results = null;
		nameList = null;
	}
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"sets\">    <Text>This is the list of sets we are going to build association rules for. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"item ids\">    <Text>For each item that may appear in the sets, this hashtable has an int array where the first entry is the count of the total occurances of that item out of all sets, and the second int is the unique id of that item. </Text>  </Info></D2K>";
			default: return "No such input";
		}
/*#end^3 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
/*&%^4 Do not modify this section. */
		String [] types =  {
			"[[Ljava.lang.String;",
			"java.util.Hashtable"};
		return types;
/*#end^4 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
/*&%^5 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"distinct items\">    <Text>This is the list of distinct items, the id of the item is the index into this array. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"rules\">    <Text>Each rull consists of a list of distinct item ids followed by a confidence and a support value. The support is the percentage of the sets that contained that combination, the confidence is, well , we don't know what that is. </Text>  </Info></D2K>";
			default: return "No such output";
		}
/*#end^5 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
/*&%^6 Do not modify this section. */
		String [] types =  {
			"[Ljava.lang.String;",
			"[[I"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"A Priori Rule Assoc\">    <Text>This module will take a number of sets and a hashtable as it's inputs. The sets is a list of co-occurances, for example, a list purchases from a store, where each purchase entry (a set) is a list of items purchased. This module will find rules that define a high probability that combinations of items will be purchased together. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
	}

	/** this property is the min acceptable support score. */
	double score=0.05;
	public void setMinimumSupport (double i) {
		score = i;
	}
	public double getMinimumSupport () {
		return score;
	}

	MutableIntegerArray [] documentMap = null;

	/**
		Convert an array of flags that indicate the presence of an item
		in the set to a list of the indices of the indexes of items present.
		@param flags the boolean array containing a flag for each item, present or not present.
		@param indices will contain an array of the indices of the items there.
	*/
	private int [] convertFlagsToIndices (boolean [] flags) {
		int [] tmp = new int [flags.length];
		int counter = 0;
		for (int i = 0 ; i < flags.length ; i++)
			if (flags[i])
				tmp [counter++] = i;
		if (counter == 0)
			return null;
		int indices [] = new int [counter];
		System.arraycopy (tmp, 0, indices, 0, counter);
		return indices;
	}

	/**
		Generate a set of rules if all but one item is fixed, or fix
		the next item and call in again. The way I generate these
		rules is that keep an array of fixed items. At each level
		in the recursion, I fix the next entry in rule, and recurse
		until there is only one item left to fix, and at this point
		I generate the rules for all possible values of that item. In
		this way, when I am ready to generate the rules, I have only
		to find the index of the last item in the fixed items list, and
		then add each item after that one to get the list of rules. <p>

		Notice that each rule is constructed by adding an item to an existing rule.
		We keep track of all the documents that contain the rule we are adding
		to so that we need only check those documents to see if the new item
		exists. In this way we can compute the support or coverage cumulatively
		rather that searching the entire document list to compute the support
		each time.

		@param fixedSets this is the list of items that are already
			set.
		@param numFixed the number of items already fixed.
		@param items the items to choose from.
		@param rules the list of rules.
		@param currentRule the index of the next rule to generate.
		@param currentDocList the list of documents which contain the current set.
		@return the index of next rule to generate.

	private int generateRules (int [] fixedSets, int numFixed,
			int [] items, int [][] rules, int currentRule,
			MutableIntegerArray currentDocList) {

		// find the first item available to add to the sets
		int numItems = items.length;
		int ruleSize = fixedSets.length;
		int fixedIndex = 0;

		// Find the first entry in the items list that is not
		// already fixed.
		if (numFixed > 0)
			while (fixedSets[numFixed-1] != items[fixedIndex++]);

		// If all items are fixed but one, generate the sets.
		if (ruleSize == numFixed+1) {

			// Fix the last item, we can now create the rule.
			for (; fixedIndex < numItems ; fixedIndex++) {

				// One additional entry in each rule for the
				// support value.
				int support = currentDocList.countIntersection (documentMap[items[fixedIndex]]);
				if (support < cutoff)
					rules[currentRule] = null;
				else {
					validRules++;
					rules [currentRule] = new int [ruleSize+1];
					for (int i = 0; i < numFixed; i++)
						rules [currentRule][i] = fixedSets [i];

					rules[currentRule][numFixed] = items [fixedIndex];
					rules[currentRule][ruleSize] = support;
					results.add (rules[currentRule]);
				}
				currentRule++;
			}
		} else {
			// Fix the next position in the rule vector
			int numPositions = numItems - (fixedSets.length - (numFixed+1));
			for (; fixedIndex < numPositions; fixedIndex++) {
				fixedSets[numFixed] = items[fixedIndex];
				if (numFixed == 0) {
					currentDocList.add (documentMap[items[fixedIndex]]);
				}

				// Find the intersection of the currently represented examples,
				// and those which contain the new item.
				MutableIntegerArray newcurrent =
					currentDocList.intersection (documentMap[items[fixedIndex]]);
				currentRule = this.generateRules (fixedSets, numFixed+1,
					items, rules, currentRule, newcurrent);
			}
		}
		return currentRule;
	}
	*/
	class LocalResultSet {
		int [] set;
		MutableIntegerArray mia;
		LocalResultSet (int [] too, MutableIntegerArray mia) {
			this.set = too;
			this.mia = mia;
		}
	};

	/**
		This is a hashable object containing an integer array.
	*/
	class FastHashIntArray {
		int []toto;
		int hash;
		/**
			take an array of integers.
		*/
		FastHashIntArray (int [] ints) {
			toto = ints;
			hash = (int) this.fastHash (ints);
		}

		FastHashIntArray () {
		}

		/**
			This method computes the hashcode. The hashcode computation
			here sucks.
		*/
		int fastHash (int [] ints) {
			long thash = 0;
			for (int i = 0; i < ints.length;i++)
				thash += ints[i];
			return (int) thash/ints.length;
		}

		/**
			Given a new array of intergers, set the local
			array and compute a new hashcode.
			@param ints the new integer array.
		*/
		void setIntegers (int [] ints) {
			toto = ints;
			hash = fastHash (ints);
		}

		/**
			Return a reference to a hashcode.
		*/
		public int hashCode() {
			return hash;
		}

		/**
			Compare each entry if necessary.
		*/
		public boolean equals(Object obj) {
			FastHashIntArray fh = (FastHashIntArray) obj;
			if (fh.toto.length != this.toto.length)
				return false;

			for (int i = 0; i < toto.length;i++)
				if (toto[i] != fh.toto[i])
					return false;

			return true;
    	}

	}

	/**
		Compute all the rules with the given cardinality (size). To do this, we
		will take all the rules already generated of size cardinality-1, and add
		each of the items which remain in our list of valid items to them to get
		all teh rules of size cardinality. For example, say the previous round
		generated the following rules:
		<OL>
		<LI>1,3,6
		<LI>1,2,3
		<LI>2,3,6
		</OL>
		So the items that are still valid will be {1, 2, 3, 6}. So we will
		derive the rules of size 4 from all the rules of size 3 by adding each
		item from our list to each of the rules above where the rule does not
		already contain that item. So, from the rules of three, here are our rules
		of 4:
		<ol>
		<li>1,2,3,6
		<li>1,2,3,6
		<li>1,2,3,6
		</ol>
		Then, we eleminate the redundancies, and in fact in this case we end up
		with only one rule.
		@param items is the list of items.
		@param cardinality the size of the resulting sets.
	*/
	private int [][] generatePossibleRules (int [] items, int cardinality) {

		int numItems = items.length;
		if (numItems < cardinality)
			return null;

		// Compute the number of rules we will need to generate.
		// This will be equal to the number of distinct sets generated
		// last time * the number of items available to choose from less
		// the number we already have. I think.
		int numValidSets = results.size() - validRules;
		int numElementsToAdd = numItems - (cardinality - 1);
		int numSets = numValidSets * numElementsToAdd;
		FastHashIntArray fhia = new FastHashIntArray ();

		//Compute the number of sets.
		int [][] rules = new int [numSets][];

		// Now generate each rule
		int [] rulebuffer = new int [cardinality-1];
		int ruleCount = 0;
		HashMap dups = new HashMap ();
		int foobar = results.size ();	// Since the list will grow.
		System.out.println ("Valid Rules Possible : "+numSets);
		for (int i = validRules ; i < foobar ; i++) {
			LocalResultSet lrs = (LocalResultSet) results.get (i);
			System.arraycopy (lrs.set, 0, rulebuffer, 0, cardinality-1);
			MutableIntegerArray mia = lrs.mia;

			for (int ruleIndex = 0, itemIndex = 0; itemIndex < items.length;
					itemIndex++) {

				// Find the first rule that is not less than current
				// item.
				while (ruleIndex < rulebuffer.length-1 && rulebuffer[ruleIndex] < items[itemIndex])
					ruleIndex++;

				// If the items are equal skip it.
				if (rulebuffer[ruleIndex] != items[itemIndex]) {

					// Generate a new rule.
					MutableIntegerArray tmp = mia.intersection (documentMap[items[itemIndex]]);
					if (tmp.count >= cutoff) {
						// First construct the new rule, in sorted order.
						int [] newRule = new int [cardinality+1];
						int p;
						for (p = 0 ; p < cardinality-1 ; p++) {
							if (rulebuffer [p] < items[itemIndex])
								newRule[p] = rulebuffer [p];
							else {
								newRule[p] = items[itemIndex];
								break;
							}
						}
						if (p == cardinality-1)
							newRule[cardinality-1] = items[itemIndex];
						else
							for (; p < cardinality-1; p++) newRule[p+1] = rulebuffer[p];

						// The support is the count of the intersection.
						newRule[cardinality] = tmp.count;

						// We found one, is it a dup?
						fhia.setIntegers (newRule);
						Object nos = (Object) dups.get (fhia);
						if (nos == null) {

							// We have a valid rule.
							rules[ruleCount] = newRule;

							// Need a new rule buffer;
							FastHashIntArray darn =
								new FastHashIntArray (newRule);
							dups.put (darn, darn);
							results.add (new LocalResultSet (rules[ruleCount], tmp));
							rules[ruleCount] = newRule;
						} else
							rules[ruleCount] = null;
					} else
						rules[ruleCount] = null;
					ruleCount++;
				}
			}
			lrs.mia = null;
		}
		return rules;
	}
	final boolean debug = true;
	/**
		This is how we implement the apriori algorithm:
		<ol>
		<li>set up a bit matrix with one row for each set, one bit for each
		item index, with the bit set at an entry if that item exists in that
		set.
		<li>get the initial list of all the items we are interested in. This
			means finding all the items that occur in more sets than than a
			percentage defined by the <code>support</code> property.
		<li>while we are still finding valid rules meeting our support criterion,
		<li>convert the list of integers to an array of booleans, one bit for
			each possible item, that bit being set if the item is of interest.
		<li>compile the next set of rules by computing the support for each o
			the possible resulting rules (checking against the bit matrix for
			efficiency) and then discarding any rules that don't pass muster.
		<li>compile the list of all possible indices again,
		<li> backto step 3.
		</ol>
	*/
	String [] nameList;
	int validRules;
	int cutoff;
	ArrayList results;
	public void doit () throws Exception {

		// get the item names, and the sets, from these num items and num examples.
		Hashtable names = (Hashtable) this.pullInput (1);
		String [][] sets = (String [][]) this.pullInput (0);
		int numExamples = sets.length;
		int numItems = names.size();

		// Compile an array of names where the name is referenced by he index.
		nameList = new String [numItems];
		Enumeration keys = names.keys ();
		Enumeration elems = names.elements ();
		cutoff = (int) Math.round (((double)numExamples*score));
		results = new ArrayList ();
		validRules = 0;
		while (keys.hasMoreElements ()) {
			String nm = (String) keys.nextElement ();
			int[] vals = (int []) elems.nextElement ();
			nameList [vals[vals.length-1]] = nm;
		}

		// the name list is an output.
		this.pushOutput (nameList, 0);
		for (int i = 0 ; i < nameList.length ;i++)
			System.out.println (i+":"+nameList[i]);

		System.out.println ("--------------------------");
		System.out.println ("number examples : "+numExamples);
		System.out.println ("cutoff : "+cutoff);
		System.out.println ("number unique items : "+numItems);
		System.out.println ("--------------------------");

		boolean [][] itemFlags = new boolean [numExamples][numItems];

		//////////////////////
		// First, set up the item flags, set the bit associated with each
		// element in each set.
		for (int i = 0 ; i < numExamples ; i++)
			for (int j = 0 ; j < sets[i].length ; j++) {
				String item_desc = sets[i][j];
				int[] count_id = (int[])names.get (item_desc);
				itemFlags[i][count_id[1]] = true;
			}

		documentMap = new MutableIntegerArray [numItems];
		boolean [] currentItemsFlags = new boolean [numItems];

		//////////////////////
		// First set up the doc list for each item, that is the number
		// of all the examples that contain the item. Don't bother for
		// items wich don't meet the support criterion.
		for (int i = 0 ; i < numItems ; i++) {
			String item_desc = nameList[i];
			int[] count_id = (int[])names.get (item_desc);
			if (count_id[0] >= cutoff) {

				// This item is of interest, has sufficient support.
				currentItemsFlags[i] = true;
				documentMap[i] = new MutableIntegerArray (numExamples);
				int [] tmp = new int [1];
				tmp[0]=i;
				results.add (new LocalResultSet (tmp, documentMap[i]));
				for (int j = 0 ; j < numExamples ; j++)
					if (itemFlags[j][i] == true)

						// The example contained this item, so add the
						// example to the doc list.
						documentMap[i].add (j);
			}
		}
		itemFlags = null;

		// The rules themselves will actually be stored in a vector where
		// the first entry in the vector is a list of all the 2 item combos,
		// the second is all the 3 item combos, and so on.
		int attributeCount = 2; // number attributes to include in the rule

		//////////////////////////////
		// Now loop until we reach a point where no additional sets will
		// meet our support criterion.
		while (true) {

			// Now convert the flags that indicate an item exists or not to a
			// sorted array of indices.
			int [] currentItemIndices = this.convertFlagsToIndices (currentItemsFlags);
			if (currentItemIndices == null) {
				System.out.println ("DONE: No elements.");
				break;
			}
			if (debug) {
				System.out.println ("APriori -> currentItemIndices count :"+currentItemIndices.length);
				System.out.print ("APriori ->{");
				for (int i = 0 ; i < currentItemIndices.length-1; i++)
					System.out.print (nameList[currentItemIndices[i]]+",");
				System.out.println (nameList[currentItemIndices[currentItemIndices.length-1]]+"}");
			}

			// Generate all the new possible rules and compute their support values. Any rule
			// not meeting the support criterion returns as null. The results vector also is up
			// to date on return.
			int oldRuleSize = results.size();
			int [][] fixedResultSets = this.generatePossibleRules (currentItemIndices, attributeCount);
			int newRules = results.size() - oldRuleSize;
			validRules = oldRuleSize;
			if (fixedResultSets == null) {
				System.out.println ("DONE: No sets.");
				break;
			}
			if (debug) {
				System.out.println ("APriori -> validRules : "+newRules);
			}
			attributeCount++;
			if (newRules <= 0) {
				System.out.println ("DONE");
				break;
			}

			// Clear the current item flags.
			int testCounter = 0;
			for (int i = 0 ; i < currentItemsFlags.length; i++)
				currentItemsFlags[i] = false;

			// Set the appropriate bits.
			for (int i = 0 ; i < fixedResultSets.length; i++)
				if (fixedResultSets[i] != null)
					for (int k = 0 ; k < fixedResultSets[i].length-1; k++) {
						if (currentItemsFlags[fixedResultSets[i][k]] == false)
							testCounter++;
						currentItemsFlags[fixedResultSets[i][k]] = true;
					}
			if (!debug)
				System.out.print (".");
			else System.out.println ();
			Thread.currentThread ().yield ();
		}
		int [][] finalRules = null;
		int finalRuleCount = 0;
		for (int i = 0 ; i < results.size(); i++) {
			int [] tmp = ((LocalResultSet) results.get (i)).set;
			if (finalRules == null)
				if (tmp.length > 1)
					finalRules = new int [results.size ()-i][];
				else
					continue;

			finalRules[finalRuleCount++] = tmp;
		}
		this.pushOutput (finalRules, 1);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
}

