/*&%^1 Do not modify this section. */

package ncsa.d2k.modules.core.discovery.ruleassociation.apriori;

import java.io.*;
import java.util.*;

/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	ComputeConfidence.java
*/
public class ComputeConfidence extends ncsa.d2k.infrastructure.modules.ComputeModule
/*#end^2 Continue editing. ^#&*/
implements Serializable
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"examples\">    <Text>This is the dataset containing the examples. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"distinct values\">    <Text>This array contains a list of all the names associated with the integer representationof the sets. </Text>  </Info></D2K>";
			case 2: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"rules\">    <Text>The rules from the apriori computation. </Text>  </Info></D2K>";
			case 3: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Name2ID\">    <Text>This table maps attributes by name to an id number. </Text>  </Info></D2K>";
			case 4: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"targets\">    <Text>This is a list of target names that can be identified. It is optional. </Text>  </Info></D2K>";
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
			"[Ljava.lang.String;",
			"[[I",
			"java.util.Hashtable",
			"[Ljava.lang.String;"};
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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"confident rules\">    <Text>The resulting rules with a high enough confidence. </Text>  </Info></D2K>";
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
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"\">    <Text> </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
	}

	private boolean debug = true;
	public boolean getDebug () {return debug; }
	public void setDebug (boolean newConfidence) {debug = newConfidence; }

	/** confidence in the result. */
	float confidence;
	public float getConfidence () {return confidence; }
	public void setConfidence (float newConfidence) {confidence = newConfidence; }

	/** If this field exists, it contains the name of the item that is the preferred target. */
	String  targetItem = "";
	/**
		the module should ONLY check if input from pipes is ready through this method - jjm-uniq
		return true if ready to fire , false otherwise
	*/
	public boolean isReady () {
		if (inputFlags[0] > 0 && inputFlags[1] > 0 && inputFlags[2] > 0 && inputFlags[3] > 0)
			return true;
		else
			return false;
	}
	/**
		So we have computed the support that each set is pertinate, now
		compute confidence that each permutation of the set is a rule.
	*/
	public void doit () throws Exception {

		// pull the inputs.
		String [] items = (String []) this.pullInput (1);
		String [][] examples = (String [][]) this.pullInput (0);
		int [][] rules = (int [][]) this.pullInput (2);
		Hashtable names = (Hashtable) this.pullInput (3);

		// Init the counters.
		int numExamples = examples.length;
		int numRules = rules.length;
		int numItems = items.length;
		boolean [][] itemFlags = new boolean [numExamples][numItems];
     	int [] targetIndex = null;

		// What are the outputs...
		if (this.inputFlags[4] != 0) {

			// So here we have a list of fields that we are going to
			// target, these are the outputs.
			Object obj = this.pullInput (4);
			System.out.println ("Input is "+obj.getClass().getName());
			String[] targets = (String [])obj;
			Enumeration keys = names.keys ();
			Enumeration indxs = names.elements ();
			ArrayList list = new ArrayList ();

			// for each of the attributes, see if the inputs include the attribute.
			while (keys.hasMoreElements ()) {
				String name = (String) keys.nextElement ();
				int[] indx = (int[]) indxs.nextElement ();
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
	     		Enumeration keys = names.keys ();
				Enumeration indxs = names.elements ();
				ArrayList list = new ArrayList ();

				// for each of the attributes, see if the inputs include the attribute.
				while (keys.hasMoreElements ()) {
					String name = (String) keys.nextElement ();
					int[] indx = (int[]) indxs.nextElement ();
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

		//////////////////////
		// First, set up the item flags, set the bit associated with each
		// eletment in each set.
		for (int i = 0 ; i < numExamples ; i++)
			for (int j = 0 ; j < examples[i].length ; j++) {
			  String item_desc = examples[i][j];
			  int[] count_id = (int[])names.get (item_desc);
			  if (count_id.length < 2)
				System.out.println("WHOOOAAAA i = "+i+" item name = "+item_desc);
			  itemFlags[i][count_id[1]] = true;
			}

		Vector finalRules = new Vector ();
		if (debug) {
			System.out.println ("ComputeConfidence, number of rules :"+numRules);
			System.out.println ("            number of unique items :"+numItems);
			System.out.println ("                        numExamples:"+numExamples);
		}
		for (int i = 0 ; i < numRules ; i++) {
			int ruleNum = rules[i].length - 1;
			for (int j = 0 ; j < rules[i].length-1; j++) {

				// Make a new rule array with one extra slot for the confidence
				int [] newRule = new int [rules[i].length+1];
				for (int k = 0 ; k < j ; k++) newRule[k] = rules[i][k];
				for (int k = j+1 ; k < ruleNum ; k++)
					newRule[k-1] = rules[i][k];
				int mark = newRule[newRule.length-3] = rules[i][j];
				newRule[newRule.length-2] = rules[i][ruleNum]; // the support.

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
					for ( ; y < ruleNumLessOne ; y++)
						if (itemFlags[x][newRule[y]] == false)
							break;

					// Did the antecedent exist in this set?
					if (y == ruleNumLessOne) {
						total++;
						if (itemFlags[x][mark] == true)
							hits++;
					}
				}

				// Here we will have the confidence.
				if ((float)hits/(float)total > confidence) {
					newRule[newRule.length-1] = (int)(((float)hits*1000000.0)/
										(float)total);
					if (targetIndex != null) {
						for (int tgts = 0 ; tgts < targetIndex.length ; tgts++)
							if (newRule[newRule.length-3] == targetIndex [tgts]) {
								finalRules.addElement (newRule);
								break;
							}
					} else
						finalRules.addElement (newRule);
				}
			}
			Thread.currentThread ().yield ();
		}

		if (debug) {
			for (int i = 0 ; i < finalRules.size(); i++) {
				int [] rule = (int [])  finalRules.elementAt(i);
				System.out.print (items[rule[0]]);
				for (int k = 1 ; k < rule.length-2 ; k++)
					System.out.print(","+items[rule[k]]);
				System.out.println("->"+rule[rule.length-2]+","+rule[rule.length-1]);
			}
			System.out.println ("ComputeConfidence, rules found : "+finalRules.size());
		}
		int rsize = finalRules.size();
		int[][] resultingRules = new int [rsize][];
		for (int i = 0 ; i < rsize; i++) {
			int [] rule = (int [])  finalRules.elementAt(i);
			resultingRules[i] = rule;
		}
		this.pushOutput (resultingRules, 0);
	}

/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
}



