package ncsa.d2k.modules.core.discovery.ruleassociation.apriori;


import java.io.*;
import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.discovery.ruleassociation.*;

/**
	ComputeConfidence.java
*/
public class ComputeConfidence extends ncsa.d2k.core.modules.ComputeModule{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The itemset contains the all the itemsets in the original data.";
			case 1: return "These are the frequent item sets for which confidence must be computed.";
			case 2: return "<p>      This is a list of the names of those items that are the targets of       interest.    </p>";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.ItemSets","[[I","[Ljava.lang.String;"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<p>      Each entry in this array contains a frequent item set with the       confidence value append to the end of the array.    </p>";
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
		return "<p>      Overview: Given a list of frequent item sets, compute the percentage of       examples"+
			" that contain the first items that also contain the last item.       This value is called the"+
			" confidence.    </p>    <p>      Scalability: This module will search all the items sets to"+
			" compute the       confidence for each frequent item set. This is done quite efficiently,  "+
			"     so this module should never take as long as the apriori module does. It       also allocates"+
			" no additional memory, the confidence is stored at the end       of the frequent item set, space"+
			" is already allocated there for it.    </p>";
	}

	private boolean debug = true;
	public boolean getDebug () {return debug; }
	public void setDebug (boolean newConfidence) {debug = newConfidence; }

	/** confidence in the result. */
	float confidence;
	public float getConfidence () {return confidence; }
	public void setConfidence (float newConfidence) throws Exception {
		if (newConfidence > 1.0 || newConfidence < 0.0)
			throw new Exception ("Confidence must range from 0.0 to 1.0");
		confidence = newConfidence;
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [2];
		pds[1] = new PropertyDescription ("debug", "Debug", "Generate debugging output in the console.");
		pds[0] = new PropertyDescription ("confidence", "Confidence", "The percentage of the examples that contain the antecedent to the rule that must also contain the target to be considered. Values range from 0.0 to 1.0.");
		return pds;
	}

	/** If this field exists, it contains the name of the item that is the preferred target. */
	String  targetItem = "";
	/**
		the module should ONLY check if input from pipes is ready through this method - jjm-uniq
		return true if ready to fire , false otherwise
	*/
	public boolean isReady () {
		if (inputFlags[0] > 0 && inputFlags[1] > 0)
			return true;
		else
			return false;
	}

	/**
		So we have computed the support that each set is pertinate, now
		compute confidence that each permutation of the set is a rule.
	*/
	public void doit () throws Exception {

		long start = System.currentTimeMillis();

		// pull the inputs.
		ItemSets iss = (ItemSets) this.pullInput(0);
		int [][] rules = (int [][]) this.pullInput (1);
		String [] items = iss.names;
		HashMap names = iss.unique;

		// Init the counters.
		int numExamples = iss.numExamples;
		int numRules = rules.length;
		int numItems = items.length;
		int [] targetIndex = null;

		// What are the outputs.
		if (this.inputFlags[2] != 0) {

			// So here we have a list of fields that we are going to
			// target, these are the outputs.
			Object obj = this.pullInput (2);
			String[] targets = (String [])obj;
			Iterator keys = names.keySet().iterator();
			Iterator indxs = names.values().iterator();
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
				targetIndex = new int [size];
				for (int i = 0 ; i < size ; i++)
					targetIndex[i] = ((int[])list.get (i))[1];
			}
		}
		else
			if (targetItem.length () > 0) {
				Iterator keys = names.keySet().iterator();
				Iterator indxs = names.values().iterator();
				ArrayList list = new ArrayList();

				// for each of the attributes, see if the inputs include the attribute.
				while (keys.hasNext ()) {
					String name = (String) keys.next ();
					int[] indx = (int[]) indxs.next ();
					if (name.startsWith (targetItem))
						list.add (indx);
				}

				// Put the indexes into the list.
				int size = list.size ();
				if (size != 0) {
					targetIndex = new int [size];
					for (int i = 0 ; i < size ; i++)
						targetIndex[i] = ((int[])list.get (i))[1];
				}
			}

		// get the bit map indicating what items were bought for each example.
		boolean [][] itemFlags = iss.getItemFlags();
		Vector finalRules = new Vector ();
		MutableIntegerArray[] documentMap = (MutableIntegerArray[]) iss.userData;
		iss.userData = null;
		if (debug) {
			System.out.println ("ComputeConfidence, number of rules :"+numRules);
			System.out.println ("            number of unique items :"+numItems);
			System.out.println ("                       numExamples :"+numExamples);
		}

///////////////////////////////////////
// Compute the confidence for each rule.
//
		for (int i = 0 ; i < numRules ; i++) {
			int [] currentRule = rules [i];
			int ruleNum = currentRule.length - 1;
			for (int j = 0 ; j < rules[i].length-1; j++) {

				// Make a new rule array with one extra slot for the confidence
				int [] newRule = new int [rules[i].length+1];
				for (int k = 0 ; k < j ; k++) newRule[k] = rules[i][k];
				for (int k = j+1 ; k < ruleNum ; k++)
					newRule[k-1] = rules[i][k];
				int mark = newRule[newRule.length-3] = rules[i][j];
				newRule[newRule.length-2] = rules[i][ruleNum]; // the support.

				if (targetIndex != null) {
					int tgts;
					for (tgts = 0 ; tgts < targetIndex.length ; tgts++)
						if (newRule[newRule.length-3] == targetIndex [tgts])
							break;
					if (tgts == targetIndex.length)
						continue;
				}

				// the last entry in this array is the confidence that
				// given the first n-1 attributes the last attribute will
				// occur. To discover this, search for each example that
				// has the first n-1 elements, and check to see if it has
				// the nth.
				int total = 0;
				int hits = 0;
				int ruleNumLessOne = ruleNum-1;
				for (int x = 0 ; x < numExamples ; x++) {
					// First determine if the antecedents exist in the set.
					int y = 0;
					boolean [] itf = itemFlags[x];
					for ( ; y < ruleNumLessOne ; y++)
						if (itf[newRule[y]] == false)
							break;

					// Did the antecedent exist in this set?
					if (y == ruleNumLessOne) {
						total++;
						if (itf[mark] == true)
							hits++;
					}
				}

				// Here we will have the confidence.
				if (((double)hits/(double)total) >= confidence) {
					newRule[newRule.length-1] =
							(int)(((double)hits/(double)total) * 1000000.0);
					finalRules.addElement (newRule);
					newRule = null;
				}
			}
		}

		if (debug) {
			for (int i = 0 ; i < finalRules.size(); i++) {
				int [] rule = (int [])  finalRules.elementAt(i);
				System.out.print (items[rule[0]]);
				for (int k = 1 ; k < rule.length-2 ; k++)
					System.out.print(","+items[rule[k]]);
				System.out.println("->"+rule[rule.length-2]+","+rule[rule.length-1]);
			}

		}
		System.out.println ("ComputeConfidence, rules found : "+finalRules.size()+":"+(
				System.currentTimeMillis()-start));

		int rsize = finalRules.size();
		if (rsize > 0) {
			int[][] resultingRules = new int [rsize][];
			for (int i = 0 ; i < rsize; i++) {
				int [] rule = (int [])  finalRules.elementAt(i);
				resultingRules[i] = rule;
			}
			this.pushOutput (resultingRules, 0);
		}
	}

/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Compute Confidence";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Itemset";
			case 1:
				return "Rules";
			case 2:
				return "Targets";
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
				return "Confident Rules";
			default: return "NO SUCH OUTPUT!";
		}
	}
}



