package ncsa.d2k.modules.core.discovery.ruleassociation.apriori;



import java.io.*;
import java.util.*;
import gnu.trove.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.discovery.ruleassociation.*;
import java.beans.PropertyVetoException;

/**
	ComputeConfidence.java
*/
public class ComputeConfidence extends ncsa.d2k.core.modules.ComputeModule{

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Compute Confidence";
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
	    StringBuffer sb = new StringBuffer( "<p>Overview: ");
	    sb.append("This module works in conjunction with other modules implementing the Apriori ");
	    sb.append("rule association algorithm to generate association rules satisfying a minimum confidence ");
	    sb.append("threshold. ");

            sb.append("</p><p>Detailed Description: ");
	    sb.append("This module takes as input an <i>Item Sets</i> object generated by the <i>Table To Item Sets</i> ");
	    sb.append("module, and <i>Frequent Itemsets</i> generated by the <i>Apriori</i> module.  ");
	    sb.append("From these inputs, it develops a set of possible association rules, each with a single ");
	    sb.append("target item, where an item consists of an [attribute,value] pair. " );
	    sb.append("For each possible rule, this module computes the <i>Confidence</i> in the prediction, " );
	    sb.append("and accepts those rules that meet a minimum confidence threshhold specified via the " );
	    sb.append("property editor. " );

	    sb.append("</p><p>");
	    sb.append("For a rule of the form Antecedent A implies Consequent C, the <i>Confidence</i> is the percentage of ");
	    sb.append("examples in the original data that contain A that also contain C.   The ");
	    sb.append("formula to compute the confidence of the rule A->C is: <br>");
	    sb.append(" Confidence = ( (# of examples with A and C) / (# of examples with A ) ) * 100.00 ");

            sb.append( "</p><p>Limitations: ");
            sb.append( "The <i>Apriori</i> and <i>Compute Confidence</i> modules currently ");
            sb.append( "build rules with a single item in the consequent.  ");

	    sb.append("</p><p>Scalability: ");
	    sb.append("This module searches all the Items Sets to compute the confidence for each Frequent Itemset. ");
	    // In at least one case this took longer so I removed the following sentence - Ruth.
            // sb.append("This is done quite efficiently, so this module should never take as long as the Apriori module.");
	    sb.append("The module allocated memory for the resulting Rule Table. </p>");

            return sb.toString();
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
			case 1:
				return "Frequent Itemsets";
			default:
				return "No such input";
		}
	}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
		    case 0:
			return "An Item Sets object containing the items of interest in the original data. " +
                               "This object is typically produced by a <i>Table To Item Sets</i> module.";
		    case 1:
			return "The frequent itemsets found by an <i>Apriori</i> module.  These are the " +
  			       "item combinations that frequently appear together in the original examples.";
		    default:
			return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.ItemSets","[[I"};
		return types;
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Rule Table";
			default:
				return "No such output";
		}
	}


	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
		  case 0:
 		    String s = "A representation of the association rules found and accepted by this module. " +
                               "This output is typically connected to a <i>Rule Visualization</i> module.";
                    return s;
		  default:
		    return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
                    String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.RuleTable"};
		return types;
	}


	/** Properties **/

	/** confidence in the result. */
	double confidence = 70.0;
	public double getConfidence () {
		return confidence;
	}
	public void setConfidence (double newConfidence) throws PropertyVetoException {
		if ( newConfidence <= 0.0 || newConfidence > 100 ) {
 			throw new PropertyVetoException (
                        	" Minimum Confidence % must be greater than 0 and less than or equal to 100.",
                        	null );
		}
		confidence = newConfidence;
	}

	/** showProgress option **/
	private boolean showProgress = true;
	public boolean getShowProgress () {
		return showProgress;
	}
	public void setShowProgress (boolean newShowProgress) {
		showProgress = newShowProgress;
	}

	/** debug option **/
	private boolean debug = false;
	public boolean getDebug () {
		return debug;
	}
	public void setDebug (boolean newDebug) {
		debug = newDebug;
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [3];
		pds[0] = new PropertyDescription(
			"confidence",
			"Minimum Confidence %",
			"The percent of the examples containing a rule antecedent that must also contain the " +
			"rule consequent before a potential association rule is accepted. " +
			"This value must be greater than 0 and less than or equal to 100. ");
	        pds[1] = new PropertyDescription(
			"showProgress",
			"Report Module Progress",
			"If this property is true, the module will report progress information to the console." );
		pds[2] = new PropertyDescription (
			"debug",
			"Generate Verbose Output",
			"If this property is true, the module will write verbose status information to the console. ");
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

		long startTime = System.currentTimeMillis();

		// pull the inputs.
		ItemSets iss = (ItemSets) this.pullInput(0);
		int [][] fis = (int [][]) this.pullInput (1);
		String [] items = iss.names;
		HashMap names = iss.unique;

		// Init the counters.
		int numExamples = iss.numExamples;
		int numFis = fis.length;
		int numItems = items.length;
		int [] targetIndex = null;

		// So here we have a list of fields that we are going to
		// target, these are the outputs.
		String[] targets = iss.targetNames;
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

		// get the bit map indicating what items were bought for each example.
		boolean [][] itemFlags = iss.getItemFlags();
		Vector finalRules = new Vector ();
		MutableIntegerArray[] documentMap = (MutableIntegerArray[]) iss.userData;
		iss.userData = null;
		if (debug) {
			System.out.println ("ComputeConfidence-> number of items: "+numItems);
			System.out.println ("ComputeConfidence-> number of frequent item sets: "+numFis);
			System.out.println ("ComputeConfidence-> number of examples: "+numExamples);
		}

///////////////////////////////////////
// Compute the confidence for each Rule.
//
		int previousAttrCnt = 0;
		for (int i = 0 ; i < numFis ; i++) {
			int [] currentRule = fis [i];
			int ruleLen = currentRule.length - 1;

			if ( showProgress ) {
				if ( previousAttrCnt != ruleLen ) {
				    System.out.println( getAlias() +
						": Beginning to compute confidence for frequent itemsets containing " +
						(ruleLen) +
						" attributes. ");
				    previousAttrCnt = ruleLen;
				}
			}

			for (int j = 0 ; j < fis[i].length-1; j++) {

				// Make a new rule array with one extra slot for the confidence
				int [] newRule = new int [fis[i].length+1];
				for (int k = 0 ; k < j ; k++) newRule[k] = fis[i][k];
				for (int k = j+1 ; k < ruleLen ; k++)
					newRule[k-1] = fis[i][k];
				int mark = newRule[newRule.length-3] = fis[i][j];
				newRule[newRule.length-2] = fis[i][ruleLen]; // the support.

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
				int ruleLenLessOne = ruleLen-1;
				for (int x = 0 ; x < numExamples ; x++) {
					// First determine if the antecedents exist in the set.
					int y = 0;
					boolean [] itf = itemFlags[x];
					for ( ; y < ruleLenLessOne ; y++)
						if (itf[newRule[y]] == false)
							break;

					// Did the antecedent exist in this set?
					if (y == ruleLenLessOne) {
						total++;
						if (itf[mark] == true)
							hits++;
					}
				}

				// Here we will have the confidence.  Is it more than
				// minimum required?
				if ( (((double)hits/(double)total)*100.0) >= confidence) {
					// since we have an array of ints we put the confidence value in there
					// with some extra 0's so we can reintroduce precision later
					newRule[newRule.length-1] =
							(int)(((double)hits/(double)total) * 10000.00);
					finalRules.addElement (newRule);
					newRule = null;
				}
			}

		}

		if (debug) {
			for (int i = 0 ; i < finalRules.size(); i++) {
				int [] rule = (int [])  finalRules.elementAt(i);
				System.out.print ("ComputeConfidence -> " + items[rule[0]]);
				for (int k = 1 ; k < rule.length-2 ; k++)
					System.out.print(","+items[rule[k]]);
				System.out.println("->"+rule[rule.length-2]+","+rule[rule.length-1]);
			}

		}

		if ( showProgress || debug )  {
			System.out.println( getAlias()
				+ ": A total of "
				+ finalRules.size()
				+ " rules were found that met the specified Minimum Confidence of "
				+ getConfidence() + "%." );
		}

		if ( finalRules.size() == 0 ) {
			throw new Exception( getAlias()
				+ ": No rules met the minimum confidence of "
				+ getConfidence() + "%."  );
		}


		// Following code basically extracted from ConvertItemSetsToRuleTable
		// and folded into this module reusing datastructures we already have.
		// May still be some things that can be done better but am short on time.
                // There didn't seem to be any reason to keep them separate as the
		// format of the association rules previously formed here is not used
		// elsewhere - so, just transform into RuleTable directly.

		int numRules = finalRules.size();
		int[] head = new int[numRules];
		int[] body = new int[numRules];
		double[] ruleconfidence = new double[numRules];
		double[] rulesupport = new double[numRules];

		HashMap itemSets = new HashMap();

		for(int i = 0; i < numRules; i++) {
			int[] rule = (int[]) finalRules.elementAt(i);
			int numIf = rule.length-3;
			int numThen = 1;

			int[] ifstmt = new int[numIf];
			int[] thenstmt = new int[numThen];
			for(int j = 0; j < numIf; j++)
				ifstmt[j] = rule[j];
			thenstmt[0] = rule[rule.length-3];

			TIntArrayList ifitem = new TIntArrayList(ifstmt);
			TIntArrayList thenitem = new TIntArrayList(thenstmt);

			int ifidx;
			int thenidx;

			if(itemSets.containsKey(ifitem)) {
				ifidx = ((Integer)itemSets.get(ifitem)).intValue();
			}
			else {
				ifidx = itemSets.size();
				itemSets.put(ifitem, new Integer(ifidx));
			}
			if(itemSets.containsKey(thenitem))
				thenidx = ((Integer)itemSets.get(thenitem)).intValue();
			else {
				thenidx = itemSets.size();
				itemSets.put(thenitem, new Integer(thenidx));
			}

			head[i] = ifidx;
			body[i] = thenidx;
                        // in rule table both confidence and support are represented
                        // as fractions.
			double conf = (double)rule[rule.length-1];
			conf /= 10000.0;  // adjust decimal as earlier it was saved as int
			double supp = rule[rule.length-2];
			supp /= numExamples;
			ruleconfidence[i] = conf;
			rulesupport[i] = supp;
		}
		IntColumn headColumn = new IntColumn(head);
		headColumn.setLabel(HEAD);
		IntColumn bodyColumn = new IntColumn(body);
		bodyColumn.setLabel(BODY);
		DoubleColumn confidenceCol = new DoubleColumn(ruleconfidence);
		confidenceCol.setLabel(CONFIDENCE);
		DoubleColumn supportCol = new DoubleColumn(rulesupport);
		supportCol.setLabel(SUPPORT);
		Column[] c = {headColumn, bodyColumn, supportCol, confidenceCol};
		TableImpl ti = new MutableTableImpl(c);
		ArrayList al = new ArrayList(items.length);
		for(int i = 0; i < items.length; i++)
			al.add(items[i].replace('^', '='));

		Object[] freqSets = new Object[itemSets.size()];
		Iterator iter = itemSets.keySet().iterator();
		while(iter.hasNext()) {
			TIntArrayList itemset = (TIntArrayList)iter.next();
			int idx = ((Integer)itemSets.get(itemset)).intValue();
			FreqItemSet fris = new FreqItemSet();
			fris.items = itemset;
			fris.numberOfItems = itemset.size();
			/** !!!!!!!!!!!!!!!!!!!!!! **/
			fris.support = 0;
			freqSets[idx] = fris;
		}

		ArrayList allsets = new ArrayList(freqSets.length);
		for(int i = 0; i < freqSets.length; i++)
			allsets.add(freqSets[i]);

		RuleTable rt = new RuleTable(ti, 0, 0, numExamples, al, allsets);
		rt.sortByConfidence();
		pushOutput(rt, 0);

                if ( showProgress || debug )  {
			System.out.println( getAlias()
				+ ": Elapsed Wallclock time was "
				+ (System.currentTimeMillis()-startTime) / 1000.00
				+ " Seconds" );
		}
	}

	private static final String HEAD = "Head";
	private static final String BODY = "Body";
	private static final String CONFIDENCE = "Confidence";
	private static final String SUPPORT = "Support";


}



// Start QA Comments
// 2/28/03 - Recv from Tom
// 3/18/03 - Ruth starts QA.
//         - Removed TargetAttributes input port; info now available from ItemSets object.
// 	   - Expanded desciptions;
//	   - Confidence now entered as %, not fraction.
// 3/20/03 - Folded in functionality of ItemSetsToRuleTable module so that RuleTable is
//           produced directly.
// WISH: Support rules with multiple items in the rule consequent.
// WISH: Talk to Dora about some of the rule pruning options she allows - may want to support
//       those here as well.
//
//
