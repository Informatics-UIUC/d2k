package ncsa.d2k.modules.core.discovery.ruleassociation;



import java.util.*;
import gnu.trove.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class ConvertItemSetsToRuleTable extends DataPrepModule {

	public String[] getInputTypes() {
		String[] types = {"[[I","ncsa.d2k.modules.core.discovery.ruleassociation.ItemSets","ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.RuleTable"};
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "<p>      This is the frequent itemsets stored as arrays of indices.    </p>";
			case 1: return "<p>      The is a datastructure containining all the itemsets.    </p>";
			case 2: return "<p>      This is the table from which the itemsets were derived.    </p>";
			default: return "No such input";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "<p>      This is the table containing the rules.    </p>";
			default: return "No such output";
		}
	}

	public String getModuleInfo() {
		return "<p>      Overview: Convert the results of the apriori implementation to a rule       table."+
			"    </p>    <p>      Detailed Description: Take the frequent itemsets, the original table, "+
			"      and the <i>ItemSets</i> and produce a rule table containing the frequent       itemsets."+
			" The frequent itemsets is just a list of indices with the       support and confidence tacked"+
			" onto the end. The original table actually       contains the names of columns, which is needed"+
			" to construct the rule       table. The itemsets contains the mapping of the indices in the"+
			" frequent       item sets to their associated names.    </p>    <p>      Scalability: This module"+
			" will allocate memory for the resulting table,       and since there will usually be only a"+
			" few frequent item sets, this       module should not use much memory. The computational demands"+
			" are also       insignificant.    </p>";
	}

	public void doit() {
		int[][] rules = (int[][])pullInput(0);
		ItemSets sets = (ItemSets)pullInput(1);
		Table table = (Table)pullInput(2);

		int numExamples = sets.numExamples;

		int numRules = rules.length;
		int[] head = new int[numRules];
		int[] body = new int[numRules];
		double[] confidence = new double[numRules];
		double[] support = new double[numRules];

		HashMap itemSets = new HashMap();

		for(int i = 0; i < numRules; i++) {
			int[] rule = rules[i];
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
			double conf = (double)rule[rule.length-1];
			conf /= 1000000.0;
			double supp = rule[rule.length-2];
			supp /= numExamples;
			//System.out.println("RULE "+i+" "+conf+" "+supp);
			//confidence.setDouble(conf, i);
			//support.setDouble(supp, i);
			confidence[i] = conf;
			support[i] = supp;
		}
		IntColumn headColumn = new IntColumn(head);
		headColumn.setLabel(HEAD);
		IntColumn bodyColumn = new IntColumn(body);
		bodyColumn.setLabel(BODY);
		DoubleColumn confidenceCol = new DoubleColumn(confidence);
		confidenceCol.setLabel(CONFIDENCE);
		DoubleColumn supportCol = new DoubleColumn(support);
		supportCol.setLabel(SUPPORT);
		Column[] c = {headColumn, bodyColumn, supportCol, confidenceCol};
		TableImpl ti = new MutableTableImpl(c);
		String[] names = sets.names;
		ArrayList al = new ArrayList(names.length);
		for(int i = 0; i < names.length; i++)
			al.add(names[i].replace('^', '='));

		Object[] freqSets = new Object[itemSets.size()];
		Iterator iter = itemSets.keySet().iterator();
		while(iter.hasNext()) {
			TIntArrayList itemset = (TIntArrayList)iter.next();
			int idx = ((Integer)itemSets.get(itemset)).intValue();
			FreqItemSet fis = new FreqItemSet();
			fis.items = itemset;
			fis.numberOfItems = itemset.size();
			/** !!!!!!!!!!!!!!!!!!!!!! **/
			fis.support = 0;
			freqSets[idx] = fis;
		}

		ArrayList allsets = new ArrayList(freqSets.length);
		for(int i = 0; i < freqSets.length; i++)
			allsets.add(freqSets[i]);

		RuleTable rt = new RuleTable(ti, 0, 0, table.getNumRows(), al, allsets);
		rt.sortByConfidence();
		pushOutput(rt, 0);
	}

	private static final String HEAD = "Head";
	private static final String BODY = "Body";
	private static final String CONFIDENCE = "Confidence";
	private static final String SUPPORT = "Support";

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Generate Rule Table";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Frequent Item Sets";
			case 1:
				return "Itemsets";
			case 2:
				return "Original Table";
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
				return "Rule Table";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
