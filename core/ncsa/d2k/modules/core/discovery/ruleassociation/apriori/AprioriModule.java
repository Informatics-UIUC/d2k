package ncsa.d2k.modules.core.discovery.ruleassociation.apriori;



import java.io.*;
import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.discovery.ruleassociation.*;
/**
	AprioriModule.java

*/
public class AprioriModule extends ncsa.d2k.core.modules.ComputeModule{

	/** the names of the various items. */
	String [] nameList;

	/** the number of valid rules obtained.*/
	int validRules;

	/** this is the number of sets that must contain a given rule for it to
		meet the support. */
	int cutoff;

	/** this is a list of results. */
	ArrayList results;

	/** this is an integer representation of the sets, each mutable integer array contains
	 *  a list of the items that set contains. */
	MutableIntegerArray [] documentMap = null;

	/** minimum support. */
	double score=0.05;

	/** start time. */
	long startTime;

	/**
		Start a timer when we begin execution.
	*/
	public void beginExecution () {
		startTime = System.currentTimeMillis ();
		results = new ArrayList();
	}

	public void endExecution () {
		super.endExecution ();
		System.out.println ("Elapsed Wallclock Time : "
			+(System.currentTimeMillis()-startTime));
		results = null;
		nameList = null;
		documentMap = null;
		sNames = null;
		currentItemsFlags = null;
		validRules = 0;
		targetIndices = null;
		nameAry = null;
	}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "This object contains all the itemsets.";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.ItemSets"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "Each rull consists of a list of distinct item ids followed by a confidence     and a support value. The support is the percentage of the sets that     contained that combination, the confidence is provided by a different     module.";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"[[I"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<p>      Overview: This module will find all the itemsets that are frequent in       the given"+
			" data.    </p>    <p>      Detailed Description: Given the ItemSets as an input, find all the"+
			" <i>       frequent item sets</i>, that is all combinations of items that occur in       more"+
			" than a user specified percentage(called support) of the itemsets.       For more information"+
			" on how the Apriori rule associated algorithm works,       see &quot;Fast Algorithms for Mining"+
			" Association Rules&quot;, Agrawal et al., 1994.    </p>    <p>      Scalability: This module"+
			" will create an array of integers to contain the       indexes of the items in each frequent"+
			" itemset it finds. It may be       computationally intensive, and scales with the size of the"+
			" frequent       itemsets it finds and the number of itemsets to search. The user can       limit"+
			" the size of the frequent itemsets and the support via the       properties editor.    </p>";
	}

	/** this property is the min acceptable support score. */
	public void setMinimumSupport (double i) throws Exception {
		if (i > 1.0 || i < 0.0) throw new Exception ("Support must range from 0.0 to 1.0");
		score = i;
	}
	public double getMinimumSupport () {
		return score;
	}

	/**
	 * the Debug property.
	 */
	private boolean debug = false;
	public void setDebug (boolean yy) {
		this.debug = yy;
	}
	public boolean getDebug () {
		return this.debug;
	}

	/**
	 * the Debug property.
	 */
	private int maxSize = 6;
	public void setMaxRuleSize (int yy) {
		this.maxSize = yy;
	}
	public int getMaxRuleSize () {
		return this.maxSize;
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [3];
		pds[2] = new PropertyDescription ("debug", "Debug", "Generate debugging output in the console.");
		pds[1] = new PropertyDescription ("maxRuleSize", "Maximum Frequent Set Size", "The maximum number of items in a frequent item set.");
		pds[0] = new PropertyDescription ("minimumSupport", "Support", "This is the minimum percent of the examples that must contain an itemset before it can be considered frequent. Values range from 0 to 1, and should be expressed in decimal notation.");
		return pds;
	}

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
	 * These are the rules we have stored.
	 */
	class LocalResultSet {
		int [] set;
		MutableIntegerArray mia;
		LocalResultSet (int [] too, MutableIntegerArray mia) {
			this.set = too;
			this.mia = mia;
		}
	}

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
		final int fastHash (int [] ints) {
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
		final void setIntegers (int [] ints) {
			toto = ints;
			hash = fastHash (ints);
		}

		/**
			Return a reference to a hashcode.
		*/
		final public int hashCode() {
			return hash;
		}

		/**
			Compare each entry if necessary.
		*/
		final public boolean equals(Object obj) {
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
		@returns all the possible rules of the given cardinality.
	*/
	private int [][] generatePossibleRules (int [] items, int cardinality) {

		int numItems = items.length;
		if (numItems < cardinality)
			return null;

		// Compute the number of rules we will need to generate.
		// This will be equal to the number of distinct sets generated
		// last time * the number of items available to choose from less
		// the number we already have. I think.
		int numSets = (results.size() - validRules) * (numItems - (cardinality - 1));
		FastHashIntArray fhia = new FastHashIntArray ();

		//Allocate space for the rules
		int [][] rules = new int [numSets][];

		// Now generate each rule
		int [] rulebuffer = new int [cardinality-1];
		int ruleCount = 0;
		HashMap dups = new HashMap ();
		int foobar = results.size ();	// Since the list will grow.
		if (debug)
			System.out.println ("Valid Rules Possible : "+numSets);

		// We construct new rules from existing rules of size (cardinality-1).
		MutableIntegerArray tmp = new MutableIntegerArray (numExamples);
		for (int i = validRules ; i < foobar ; i++) {

			// Get the next rule of size cardinality-1, copy into the rule buffer.
			LocalResultSet lrs = (LocalResultSet) results.get (i);
			System.arraycopy (lrs.set, 0, rulebuffer, 0, cardinality-1);
			MutableIntegerArray mia = lrs.mia;

			// Add each item we have to the current rule
nextrule:	for (int ruleIndex = 0, itemIndex = 0; itemIndex < items.length;
					itemIndex++) {

				// Find the first item in the rule not less than current item.
				while (ruleIndex < rulebuffer.length-1 && rulebuffer[ruleIndex] < items[itemIndex])
					ruleIndex++;

				if (rulebuffer[ruleIndex] != items[itemIndex]) {
					if (cardinality == 2) {

						// After we have all the rules of size two, we will know that each rule
						// will have at least one output item included, because the rules from
						// which we construct new rules will all contain a target. He we make sure
						// we ignore any rule that does not contain an output.
						int targetIndex;
						if (targetIndices != null) {
							for (targetIndex = 0 ; targetIndex < targetIndices.length ;targetIndex++)
								if (rulebuffer[ruleIndex] == targetIndices[targetIndex])
									break;
							if (targetIndex >= targetIndices.length)
								for (targetIndex = 0 ; targetIndex < targetIndices.length ;targetIndex++)
									if (items[itemIndex] == targetIndices[targetIndex])
										break;
						}
					}


					// Find the number of examples that demonstrate the current rule and the new item.
					tmp.count = 0;
					mia.intersection (documentMap[items[itemIndex]], tmp);
					if (tmp.count >= cutoff) {

						// meet the support criterion, we have a new rule
						int [] newRule = new int [cardinality+1];
						if (rulebuffer[ruleIndex] < items[itemIndex]) {

							// the new item should be added at the end.
							System.arraycopy(rulebuffer, 0, newRule, 0, ruleIndex+1);
							newRule[ruleIndex+1] = items[itemIndex];
						} else {
							System.arraycopy(rulebuffer, 0, newRule, 0, ruleIndex);
							newRule[ruleIndex] = items[itemIndex];
							System.arraycopy(rulebuffer,ruleIndex,newRule,ruleIndex+1,
									rulebuffer.length-ruleIndex);
						}
						newRule[cardinality] = tmp.count;

						// We found one, is it a dup?
						fhia.setIntegers (newRule);
						Object nos = (Object) dups.get (fhia);
						if (nos == null) {

							// We have a valid rule.
							rules[ruleCount] = newRule;

							// Need a new rule buffer;
							FastHashIntArray darn = new FastHashIntArray (newRule);
							dups.put (darn, darn);
							results.add (new LocalResultSet (rules[ruleCount],
									new MutableIntegerArray(tmp)));
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

	/**
	 * Return true if either we still have input, or have more work to do.
	 * @return
	 */
	public boolean isReady() {
		if ((inputFlags[0] > 0) || !done)
			return true;
		return false;
	}

	/**
	 * Called when we are all done, it will construct the rules, and
	 * if there are any, pass them along.
	 */
	final private void finish () {
		done = true;
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
		if (finalRules != null)
			this.pushOutput (finalRules, 0);
	}

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

	int attributeCount;
	HashMap sNames;
	boolean done = true;
	boolean [] currentItemsFlags;
	int numExamples;
	int [] targetIndices;
	String [] nameAry;
	public void doit () throws Exception {

		HashMap names = sNames;

		// get the item names, and the sets, from these num items and num examples.
		if (documentMap == null) {
			done = false;
			ItemSets iss = (ItemSets)this.pullInput(0);
			sNames = iss.unique;
			targetIndices = iss.outputIndices;
			names = sNames;
			nameAry = iss.names;
			numExamples = iss.numExamples;
			cutoff = (int) ((double)numExamples * score);
			if (((double)numExamples * score) > (double)cutoff)
				cutoff++;
			int numItems = names.size();

			// Compile an array of names where the name is referenced by he index.
			nameList = iss.names;
			if (debug) {
				for (int i = 0 ; i < nameList.length ;i++)
					System.out.println (i+":"+nameList[i]);
				System.out.println ("--------------------------");
				System.out.println ("number examples : "+numExamples);
				System.out.println ("cutoff : "+cutoff);
				System.out.println ("number unique items : "+numItems);
				System.out.println ("--------------------------");
			}

			boolean [][] itemFlags = iss.getItemFlags();
			documentMap = new MutableIntegerArray [numItems];
			currentItemsFlags = new boolean [numItems];

			//////////////////////
			// First set up the doc list for each item, that is the indices
			// of all the examples that contain the item. Don't bother for
			// items which don't meet the support criterion.
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
			iss.userData = documentMap;
			attributeCount = 2; // number attributes to include in the rule		}
		}

		// The rules themselves will actually be stored in a vector where
		// the first entry in the vector is a list of all the 2 item combos,
		// the second is all the 3 item combos, and so on.


		// Now convert the flags that indicate an item exists or not to a
		// sorted array of indices.
		int [] currentItemIndices = this.convertFlagsToIndices (currentItemsFlags);
		if (currentItemIndices == null) {
			System.out.println ("DONE");
			this.finish();
			return;
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
			System.out.println ("DONE");
			this.finish();
			return;
		}

		// In the first round, we will discard any rule that does not contain a target value.
		if (debug) {
			System.out.println ("APriori -> validRules : "+newRules);
		}
		attributeCount++;
		if (newRules <= 0 || attributeCount > maxSize) {
			System.out.println ("DONE");
			this.finish();
			return;
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
		else
			System.out.println ();
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "A Priori Rule Assoc";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Item Sets";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Rules";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

