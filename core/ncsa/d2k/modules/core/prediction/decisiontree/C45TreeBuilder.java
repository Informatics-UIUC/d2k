package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.util.*;

import java.util.*;
import java.io.Serializable;
import java.text.NumberFormat;

/**
	Build a C4.5 decision tree.  The tree is build recursively, always choosing
	the attribute with the highest information gain as the root.  The
	gain ratio is used, whereby the information gain is divided by the
	information given by the size of the subsets that each branch creates.
	This prevents highly branching attributes from always becoming the root.
	A threshold can be used to prevent the tree from perfect fitting the
	training data.  If the information gain ratio is not above the threshold,
	a leaf will be created instead of a node.  This will may cause some
	incorrect classifications, but will keep the tree from overfitting
	the data.  The threshold should be set low.  The default value should
	suffice for most trees.

	@author David Clutter
*/
public class C45TreeBuilder extends ComputeModule
    implements Serializable, HasNames, HasProperties {

	public static void main(String[] args) {
		double d1 = (double)9/(double)14;
		double d2 = (double)5/(double)14;
		double dd[] = {d1, d2};
		System.out.println(lg(2));
		System.out.println(entropy(dd));
	}

	/**
		Calculate the entropy given probabilities.  The entropy is the amount
		of information conveyed by a potential split.

		entropy(p1, p2,...pn) = -p1*lg(p1) - p2*lg(p2) -...-pn*lg(pn)

		@param data the probabilities
		@return the information conveyed by the probabilities
	*/
	private static final double entropy(double[] data) {
		double retVal = 0;
		for(int i = 0; i < data.length; i++) {
			retVal += -1*data[i]*lg(data[i]);
		}
		return retVal;
	}

	/**
		Return the binary log of a number.  This is defined as
		x such that 2^x = d
		@param d the number to take the binary log of
		@return the binary log of d
	*/
	private static final double lg(double d) {
		return Math.log(d)/Math.log(2.0);
	}

	/**
		The threshold for information gain
	*/
	private double threshold = 0.005;

	public void setThreshold(double d) {
		threshold = d;
	}

	public double getThreshold() {
		return threshold;
	}

	private NumberFormat nf;
	private static final String LESS_THAN = " < ";
	private static final String GREATER_THAN_EQUAL_TO = " >= ";
	private static final int NUMERIC_VAL_COLUMN = 0;
	private static final int NUMERIC_OUT_COLUMN = 1;

	/**
		Calculate the entropy of a numeric attribute.  This is the sum of
		the information given by the examples less than the split value and the
		information given by the examples greater than or equal to the split
		value.

		@param vt the data set
		@param splitVal the split
		@param examples the list of examples, which correspond to rows of
			the table
		@param attCol the column we are interested in
		@param outCol the output column
		@return the information given by a numeric attribute with the given
			split value
	*/
	private final double numericAttributeEntropy(VerticalTable vt, double splitVal,
		ArrayList examples, int attCol, int outCol) {

		HashMap lessThanTally = new HashMap();
		HashMap greaterThanTally = new HashMap();
		double lessThanTot = 0;
		double greaterThanTot = 0;

		// count up the number of examples less than and greater than
		// the split point
		for(int i = 0; i < examples.size(); i++) {
			Integer rowIdx = (Integer)examples.get(i);
			double val = vt.getDouble(rowIdx.intValue(), attCol);

			String out = table.getString(rowIdx.intValue(), outCol);
			if(val < splitVal) {
				if(lessThanTally.containsKey(out)) {
					Integer in = (Integer)lessThanTally.get(out);
					int tal = in.intValue() + 1;
					lessThanTally.put(out, new Integer(tal));
				}
				else {
					lessThanTally.put(out, new Integer(1));
				}
				lessThanTot++;
			}
			else {
				if(greaterThanTally.containsKey(out)) {
					Integer in = (Integer)greaterThanTally.get(out);
					int tal = in.intValue() + 1;
					greaterThanTally.put(out, new Integer(tal));
				}
				else {
					greaterThanTally.put(out, new Integer(1));
				}
				greaterThanTot++;
			}
		}
		// now that we have tallies of the outputs for this att value
		// we can calculate the entropy.

		// get the probablities for the examples less than the split
		double[] probs = new double[lessThanTally.size()];
		Iterator it = lessThanTally.values().iterator();
		int idx = 0;
		while(it.hasNext()) {
			Integer in = (Integer)it.next();
			double d = (double)in.intValue();
			probs[idx] = d/lessThanTot;
			idx++;
		}

		// get the probablities for the examples greater than or equal to
		// the split
		double[] greaterProbs = new double[greaterThanTally.size()];
		it = greaterThanTally.values().iterator();
		idx = 0;
		while(it.hasNext()) {
			Integer in = (Integer)it.next();
			double d = (double)in.intValue();
			greaterProbs[idx] = d/greaterThanTot;
			idx++;
		}

		// return the sum of the information given on each side of the split
		return (lessThanTot/(double)examples.size())*entropy(probs) +
			(greaterThanTot/(double)examples.size())*entropy(greaterProbs);
	}

	/**
		Find the best split value for the given column with the given examples.
		The best split value will be the one that gives the maximum information.
		This is found by sorting the set of examples and testing each possible
		split point.  (The possible split points are located halfway between
		unique values in the set of examples)  The information on each possible
		split is then calculated.
		@param attCol the index of the attribute column
		@param examples the list of examples, which correspond to rows of the
			table
		@return the split value for this attribute that gives the maximum
			information
	*/
	private final double findSplitValue(int attCol, ArrayList examples) {
		// copy the examples into a new table
		DoubleColumn dc = new DoubleColumn(examples.size());
		StringColumn sc = new StringColumn(examples.size());

		Integer rowIdx;
		int idx = 0;
		for(int i = 0; i < examples.size(); i++) {
			rowIdx = (Integer)examples.get(i);
		 	dc.setDouble(table.getDouble(rowIdx.intValue(), attCol), idx);
			sc.setString(table.getString(rowIdx.intValue(), outputs[0]), idx);
			idx++;
		}
		Column[] cols = {dc, sc};
		VerticalTable vt = new VerticalTable(cols);

		// sort the table
		try {
			vt.sortByColumn(0);
		}
		catch(Exception e) {
		}

		// each row of the new table is an example
		ArrayList exams = new ArrayList();
		for(int i = 0; i < vt.getNumRows(); i++)
			exams.add(new Integer(i));

		// now test the possible split values.  these are the half-way point
		// between two adjacent values.  keep the highest.
		double splitValue = 0;
		double highestGain = 0;

		double lastTest = vt.getDouble(0, NUMERIC_VAL_COLUMN);
		for(int i = 1; i < vt.getNumRows(); i++) {
			double next = vt.getDouble(i, NUMERIC_VAL_COLUMN);
			if(next != lastTest) {
				double testSplitValue = (next-lastTest)/2+lastTest;

				// count up the number greater than and the number less than
				// the split value and calculate the information gain
				double gain = numericAttributeEntropy(vt, testSplitValue,
					exams, NUMERIC_VAL_COLUMN, NUMERIC_OUT_COLUMN);

				// if the gain is better than what we have already seen, save
				// it and the splitValue
				if(gain > highestGain) {
					highestGain = gain;
					splitValue = testSplitValue;
				}
			}
		}

		return splitValue;
	}

	/**
		Find the information gain for a numeric attribute.  The best split
		value is found, and then the information gain is calculated using
		the split value.
		@param attCol the index of the attribute column
		@param examples the list of examples, which correspond to rows of
			the table
		@return an object holding the gain and the best split value of
			the column
	*/
	private final EntrSplit numericGain(int attCol, ArrayList examples) {
		double gain = outputEntropy(outputs[0], examples);

		double splitVal = findSplitValue(attCol, examples);
		double numEntr = numericAttributeEntropy(table, splitVal, examples,
			attCol, outputs[0]);

		gain -= numEntr;
		double spliter = splitInfo(attCol, splitVal, examples);
		gain /= spliter;

		return new EntrSplit(splitVal, gain);
	}

	/**
		A simple structure to hold the gain and split value of a column.
	*/
	private final class EntrSplit {
		double splitValue;
		double gain;

		EntrSplit(double s, double g) {
			splitValue = s;
			gain = g;
		}
	}

	/**
		Find the information gain for a categorical attribute.  The gain
		ratio is used, where the information gain is divided by the
		split information.  This prevents highly branching attributes
		from becoming dominant.
		@param attCol the index of the attribute column
		@param examples the list of examples, which correspond to rows of
			the table
		@return the gain of the column
	*/
	private final double categoricalGain(int attCol, ArrayList examples) {
		// total entropy of the class column -
		// entropy of each of the possibilities of the attribute
		// (p =#of that value, n=#rows)

		// entropy of the class col
		double gain = outputEntropy(outputs[0], examples);
		double catEntr = 0.0;

		// now subtract the entropy for each unique value in the column
		// ie humidity=high, count # of yes and no
		// humidity=low, count # of yes and no
		String []vals = uniqueValues(attCol, examples);
		for(int i = 0; i < vals.length; i++)
			catEntr += categoricalAttributeEntropy(attCol, vals[i], examples);

		gain -= catEntr;
		double sInfo = splitInfo(attCol, 0, examples);
		// divide the information gain by the split info
		gain /= sInfo;
		return gain;
	}

	/**
		Calculate the entropy of a specific value of an attribute.
		@param colNum the index of the attribute column
		@param attValue the value of the attribute we are interested in
		@param examples the list of examples, which correspond to rows of
			the table
		@return the information given by attValue
	*/
	private final double categoricalAttributeEntropy(int colNum, String attValue,
		ArrayList examples) {

		HashMap outTally = new HashMap();
		double tot = 0;

		for(int i = 0; i < examples.size(); i++) {
			Integer rowIdx = (Integer)examples.get(i);
			String s = table.getString(rowIdx.intValue(), colNum);
			if(s.equals(attValue)) {
				// check that branches are satisfied
				String out = table.getString(rowIdx.intValue(), outputs[0]);

				if(outTally.containsKey(out)) {
					Integer in = (Integer)outTally.get(out);
					int val = in.intValue() + 1;
					outTally.put(out, new Integer(val));
				}
				else {
					outTally.put(out, new Integer(1));
				}
				tot++;
			}
		}
		// now that we have tallies of the outputs for this att value
		// we can calculate the entropy.
		double[] probs = new double[outTally.size()];
		Iterator it = outTally.values().iterator();
		int idx = 0;
		while(it.hasNext()) {
			Integer in = (Integer)it.next();
			double d = (double)in.intValue();
			probs[idx] = d/tot;
			idx++;
		}

		return (tot/(double)examples.size())*entropy(probs);
	}

	/**
		Get the unique values of the examples in a column.
		@param colNum the index of the attribute column
		@param examples the list of examples, which correspond to rows of
			the table
		@return an array containing all the unique values for examples in
			this column
	*/
	private final String[] uniqueValues(int colNum, ArrayList examples) {
		int numRows = examples.size();

		// count the number of unique items in this column
		HashMap map = new HashMap();
		for(int i = 0; i < numRows; i++) {
			Integer rowIdx = (Integer)examples.get(i);
			String s = table.getString(rowIdx.intValue(), colNum);
			if(!map.containsKey(s))
				map.put(s, s);
		}

		String[] retVal = new String[map.size()];
		int idx = 0;
		Iterator it = map.keySet().iterator();
		while(it.hasNext()) {
			retVal[idx] = (String)it.next();
			idx++;
		}
		return retVal;
	}

	/**
		Determine the split info.  This is the information given by the
		number of branches of a node.  The size of the subsets that each
		branch creates is calculated and then the information is calculated
		from that.
		@param colNum the index of the attribute column
		@param splitVal the split value for a numeric attribute
		@param examples the list of examples, which correspond to rows of
			the table
		@return the information value of the branchiness of this attribute
	*/
	private final double splitInfo(int colNum, double splitVal, ArrayList examples) {
		int numRows = examples.size();
		double tot = 0;
		double[] probs;

		// if it is a numeric column, there will be two branches.
		// count up the number of examples less than and greater
		// than the split value
		if(table.getColumn(colNum) instanceof NumericColumn) {
			double lessThanTally = 0;
			double greaterThanTally = 0;

			for(int i = 0; i < numRows; i++) {
				Integer rowIdx = (Integer)examples.get(i);
				double d = table.getDouble(rowIdx.intValue(), colNum);
				if(d < splitVal)
					lessThanTally++;
				else
					greaterThanTally++;
				tot++;
			}
			probs = new double[2];
			probs[0] = lessThanTally/tot;
			probs[1] = greaterThanTally/tot;
		}
		// otherwise it is nominal.  count up the number of
		// unique values, because there will be a branch for
		// each unique value
		else {
			HashMap map = new HashMap();
			for(int i = 0; i < numRows; i++) {
				Integer rowIdx = (Integer)examples.get(i);
				String s = table.getString(rowIdx.intValue(), colNum);
				if(!map.containsKey(s))
					map.put(s, new Integer(1));
				else {
					Integer ii = (Integer)map.get(s);
					int tal = ii.intValue();
					tal++;
					map.put(s, new Integer(tal));
				}
				tot++;
			}

			probs = new double[map.size()];
			Iterator it = map.values().iterator();
			int idx = 0;
			while(it.hasNext()) {
				Integer in = (Integer)it.next();
				double d = (double)in.intValue();
				probs[idx] = d/tot;
				idx++;
			}
		}

		// calculate the information given by the branches
		return entropy(probs);
	}

	/**
		Determine the entropy of the output column.
		@param colNum the index of the attribute column
		@param examples the list of examples, which correspond to rows of
			the table
		@return the entropy of the output column
	*/
	private final double outputEntropy(int colNum, ArrayList examples) {
		double numRows = (double)examples.size();

		// count the number of unique items in this column
		HashMap map = new HashMap();
		for(int i = 0; i < numRows; i++) {
			Integer rowIdx = (Integer)examples.get(i);
			String s = table.getString(rowIdx.intValue(), colNum);
			if(! map.containsKey(s)) {
				map.put(s, new Integer(1));
			}
			else {
				Integer in = (Integer)map.get(s);
				int val = in.intValue() + 1;
				map.put(s, new Integer(val));
			}
		}

		double[] probs = new double[map.size()];
		Iterator it = map.values().iterator();
		int idx = 0;
		while(it.hasNext()) {
			Integer in = (Integer)it.next();
			double d = (double)in.intValue();
			probs[idx] = d/numRows;
			idx++;
		}

		return entropy(probs);
	}

	/**
		Return the column number of the attribute with the highest gain from
		the available columns.  If none of the attributes has a gain higher
		than the threshold, return null
		@param attributes the list of available attributes, which correspond to
			columns of the table
		@param examples the list of examples, which correspond to rows of
			the table
		@return an object containing the index of the column with the highest
			gain and (if numeric) the best split for that column, or null
			if no attributes provide information gain over the threshold.
	*/
	private final ColSplit getHighestGainAttribute(ArrayList attributes, ArrayList examples) {
		ArrayList list = new ArrayList();

		int topCol = 0;
		double highestGain = 0;

		ColSplit retVal = new ColSplit();
		// for each available column, calculate the entropy
		for(int i = 0; i < attributes.size(); i++) {
			int col = ((Integer)attributes.get(i)).intValue();
			// categorical data
			if(!(table.getColumn(col) instanceof NumericColumn)) {
				double d = categoricalGain(col, examples);
				//System.out.println(table.getColumnLabel(col)+" "+d);
				if(d > highestGain) {
					highestGain = d;
					retVal.col = col;
				}
			}
			// numeric column
			else {
				EntrSplit sce = numericGain(col, examples);
				if(sce.gain > highestGain) {
					highestGain = sce.gain;
					retVal.col = col;
					retVal.splitValue = sce.splitValue;
				}
			}
		}

		// return null if the highest information gain is less than threshold
		if(highestGain < threshold)
			return null;
		return retVal;
	}

	/**
		A simple structure to hold a column index and the best split value of
		an attribute.
	*/
	private final class ColSplit {
		int col;
		double splitValue;
	}

	// the table that contains the data set
	private transient ExampleTable table;
	// the indices of the columns with output variables
	private transient int[] outputs;

	public String getModuleInfo() {
	    String s = "Build a C4.5 decision tree.  The tree is build recursively, ";
        s += "always choosing the attribute with the highest information gain ";
        s += "as the root.  The gain ratio is used, whereby the information ";
        s += "gain is divided by the information given by the size of the ";
        s += "subsets that each branch creates.  This prevents highly branching ";
        s += "attributes from always becoming the root.  A threshold can be ";
        s += "used to prevent the tree from perfect fitting the training data.  ";
        s += "If the information gain ratio is not above the threshold, a leaf ";
        s += "will be created instead of a node.  This will may cause some ";
        s += "incorrect classifications, but will keep the tree from overfitting ";
	    s += "the data.  The threshold should be set low.  The default value ";
        s += "should suffice for most trees.  The threshold is a property of ";
        s += "this module.";
        return s;
	}

    public String getModuleName() {
        return "c4.5builder";
    }

	public String getInputInfo(int i) {
	    String in = "An ExampleTable to build a decision tree from. ";
		in += "Only one output feature is used.";
        return in;
	}

    public String getInputName(int i) {
        return "TrainingTable";
    }

	public String getOutputInfo(int i) {
        if(i == 0)
	        return "The root of the decision tree built by this module.";
        else
            return "The ExampleTable used to build the tree, unchanged.";
	}

    public String getOutputName(int i) {
        if(i == 0)
            return "Tree";
        else
            return "TrainingTable";
    }

	public String[] getInputTypes() {
	    String[] in = {"ncsa.d2k.util.datatype.ExampleTable"};
        return in;
	}

	public String[] getOutputTypes() {
	    String[] out = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode",
		    "ncsa.d2k.util.datatype.ExampleTable"};
        return out;
	}

    public void endExecution() {
        super.endExecution();
        table = null;
        outputs = null;
    }

	/**
		Build the decision tree
	*/
	public void doit() {
		table = (ExampleTable)pullInput(0);
		int[] inputs = table.getInputFeatures();
		outputs = table.getOutputFeatures();

		if(outputs.length > 1) {
			System.out.println("Only one output feature is allowed.");
			System.out.println("Building tree for only the first output variable.");
		}

		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(5);

		// use all rows as examples at first
		ArrayList examples = new ArrayList();
		for(int i = 0; i < table.getNumRows(); i++)
			examples.add(new Integer(i));

		// use all columns as attributes at first
		ArrayList atts = new ArrayList();
		for(int i = 0; i < inputs.length; i++)
			atts.add(new Integer(inputs[i]));

		DecisionTreeNode rootNode = buildTree(examples, atts);
		pushOutput(rootNode, 0);
		pushOutput(table, 1);
	}

	/**
		Build a decision tree.

		let examples(v) be those examples with A = v.

		if examples(v) is empty, make the new branch a leaf labelled with
		the most common value among examples.

		else let the new branch be the tree created by
		buildTree(examples(v), target, attributes-{A})

		@param examples the indices of the rows to use
		@param attributes the indices of the columns to use
		@return a tree
	*/
	private final DecisionTreeNode buildTree(ArrayList examples, ArrayList attributes) {
		DecisionTreeNode root = null;
		String s;
		Integer rowIdx;

		// if all examples have the same output value, give the root this
		// label-- this node is a leaf.
		boolean allSame = true;
		int counter = 0;
		rowIdx = (Integer)examples.get(counter);
		s = table.getString(rowIdx.intValue(), outputs[0]);
		counter++;
		while(allSame && counter < examples.size()) {
			rowIdx = (Integer)examples.get(counter);
			String t = table.getString(rowIdx.intValue(), outputs[0]);
			if(!t.equals(s)) {
				allSame = false;
			}
			counter++;
		}
		if(allSame) {
			// this is a leaf
			root = new CategoricalDecisionTreeNode(s);
			return root;
		}

		// if attributes is empty, label the root according to the most common
		// value this will result in some incorrect classifications...
		// this node is a leaf.
		if(attributes.size() == 0) {
			String mostCommon = mostCommonOutputValue(examples);
			// make a leaf
			root = new CategoricalDecisionTreeNode(mostCommon);
			return root;
		}

		// otherwise build the subtree rooted at this node

		// calculate the information gain for each attribute
		// select the attribute, A, with the lowest average entropy, make
		// this be the one tested at the root
		ColSplit best = getHighestGainAttribute(attributes, examples);

		// if the information gain was above the threshold
		if(best != null) {
			int col = best.col;

			// categorical data
			if(!(table.getColumn(col) instanceof NumericColumn)) {
				// for each possible value v of this attribute in the set
				// of examples add a new branch below the root,
				// corresponding to A = v
				String[] branchVals = uniqueValues(col, examples);
				root = new CategoricalDecisionTreeNode(
					table.getColumnLabel(col));
				for(int i = 0; i < branchVals.length; i++) {
					ArrayList branchExam = narrowCategoricalExamples(col,
						branchVals[i], examples);
					ArrayList branchAttr = narrowAttributes(col, attributes);
					if(branchExam.size() != 0)
						root.addBranch(branchVals[i], buildTree(branchExam,
							branchAttr));

					// if examples(v) is empty, make the new branch a leaf
					// labelled with the most common value among examples
					else {
						String val = mostCommonOutputValue(examples);
						DecisionTreeNode nde = new
							CategoricalDecisionTreeNode(val);
						root.addBranch(val, nde);
					}
				}
			}

			// else if numeric find the binary split point and create
			// two branches
			else {
				DecisionTreeNode left;
				DecisionTreeNode right;
				root = new NumericDecisionTreeNode(table.getColumnLabel(col));

				// create the less than branch
				ArrayList branchExam = narrowNumericExamples(col,
					best.splitValue, examples, false);
				if(branchExam.size() != 0) {
					left = buildTree(branchExam, attributes);
				}

				// else if examples(v) is empty, make the new branch a leaf
				// labelled with the most common value among examples
				else {
					String val = mostCommonOutputValue(examples);
					left = new CategoricalDecisionTreeNode(val);
				}

				// create the greater than branch
				branchExam = narrowNumericExamples(col, best.splitValue,
					examples, true);
				if(branchExam.size() != 0) {
					right = buildTree(branchExam, attributes);
				}

				// else if examples(v) is empty, make the new branch a leaf
				// labelled with the most common value among examples
				else {
					String val = mostCommonOutputValue(examples);
					right = new CategoricalDecisionTreeNode(val);
				}
				// add the branches to the root
				StringBuffer lesser =
					new StringBuffer(table.getColumnLabel(col));
				lesser.append(LESS_THAN);
				//lesser.append(Double.toString(best.splitValue));
				lesser.append(nf.format(best.splitValue));

				StringBuffer greater =
					new StringBuffer(table.getColumnLabel(col));
				greater.append(GREATER_THAN_EQUAL_TO);
				//greater.append(Double.toString(best.splitValue));
				greater.append(nf.format(best.splitValue));
				root.addBranches(best.splitValue, lesser.toString(), left,
					greater.toString(), right);
			}
		}

		// otherwise the information gain was below the threshold
		// make a leaf labelled with the most common value among the examples
		else {
			//System.out.println("below threshold");
			String val = mostCommonOutputValue(examples);
			root = new CategoricalDecisionTreeNode(val);
		}

		return root;
	}

	/**
		Find the most common output value from a list of examples.
		@param examples the list of examples
		@return the most common output value from the examples
	*/
	private final String mostCommonOutputValue(ArrayList examples) {
		HashMap map = new HashMap();
		Integer rowIdx;
		for(int i = 0; i < examples.size(); i++) {
			rowIdx = (Integer)examples.get(i);
			String s = table.getString(rowIdx.intValue(), outputs[0]);
			if(map.containsKey(s)) {
				Integer tal = (Integer)map.get(s);
				int val = tal.intValue();
				val++;
				map.put(s, new Integer(val));
			}
			else {
				map.put(s, new Integer(1));
			}
		}

		int highestTal = 0;
		String mostCommon = null;

		Iterator i = map.keySet().iterator();
		while(i.hasNext()) {
			String s = (String)i.next();
			Integer tal = (Integer)map.get(s);
			if(tal.intValue() > highestTal) {
				highestTal = tal.intValue();
				mostCommon = s;
			}
		}
		return mostCommon;
	}

	/**
		Create a subset of the examples.  Only those examples where the value
		is equal to val will be in the subset.
		@param col the column to test
		@param the value to test
		@param exam the list of examples to narrow
		@return a subset of the original list of examples
	*/
	private final ArrayList narrowCategoricalExamples(int col, String val, ArrayList exam) {
		ArrayList examples = new ArrayList();

		Integer rowIdx;
		for(int i = 0; i < exam.size(); i++) {
			rowIdx = (Integer)exam.get(i);
			String s = table.getString(rowIdx.intValue(), col);
			if(s.equals(val))
				examples.add(rowIdx);
		}
		return examples;
	}

	/**
		Create a subset of the examples.  If greaterThan is true, only those
		rows where the value is greater than than the splitValue will be in
		the subset.  Otherwise only the rows where the value is less than the
		splitValue will be in the subset.
		@param col the column to test
		@param splitValue the value to test
		@param exam the list of examples to narrow
		@param greaterThan true if values greater than the split value should
			be in the new list of examples, false if values less than the split
			value should be in the list of examples
		@return a subset of the original list of examples
	*/
	private final ArrayList narrowNumericExamples(int col, double splitValue, ArrayList exam,
		boolean greaterThan) {

		ArrayList examples = new ArrayList();
		Integer rowIdx;

		for(int i = 0; i < exam.size(); i++) {
			rowIdx = (Integer)exam.get(i);
			double d = table.getDouble(rowIdx.intValue(), col);
			if(greaterThan) {
				if(d >= splitValue)
					examples.add(rowIdx);
			}
			else {
				if(d < splitValue)
					examples.add(rowIdx);
			}
		}
		return examples;
	}

	/**
		Remove the specified column from list of attributes.
		@param col the column to remove
		@param attr the list of attributes
		@return a subset of the original list of attributes
	*/
	private final ArrayList narrowAttributes(int col, ArrayList attr) {
		ArrayList attributes = (ArrayList)attr.clone();
		attributes.remove(new Integer(col));
		return attributes;
	}
}
