package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
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
public class C45TreeBuilder extends ComputeModule {

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
	private double infoGainThreshold = 0.005;

	public void setInfoGainThreshold(double d) {
		infoGainThreshold = d;
	}

	public double getInfoGainThreshold() {
		return infoGainThreshold;
	}

	/**
	 * Turns debugging statements on or off.
	 */
	private boolean debug = false;

	public void setDebug(boolean b) {
		debug = b;
	}

	public boolean getDebug() {
		return debug;
	}

	private boolean useGainRatio = true;

	public void setUseGainRatio(boolean b) {
		useGainRatio = b;
	}

	public boolean getUseGainRatio() {
		return useGainRatio;
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
	private double numericAttributeEntropy(Table vt, double splitVal,
		int[] examples, int attCol, int outCol) {

		int lessThanTot = 0;
		int greaterThanTot = 0;

		int [] lessThanTally = new int[0];
		int [] greaterThanTally = new int[0];
		HashMap lessThanIndexMap = new HashMap();
		HashMap greaterThanIndexMap = new HashMap();
		for(int i = 0; i < examples.length; i++) {
			int idx = examples[i];

            double val;
            //try {
			    val = vt.getDouble(idx, attCol);
            /*}
            catch(RuntimeException e) {
                System.out.println("IDX: "+idx);
                System.out.println(vt.getNumRows());
                throw e;
            }*/
			String out = vt.getString(idx, outCol);

			int loc;
			if(val < splitVal) {
				if(lessThanIndexMap.containsKey(out)) {
					Integer in = (Integer)lessThanIndexMap.get(out);
					loc = in.intValue();
					lessThanTally[loc]++;
				}
				// found a new one..
				else {
					lessThanIndexMap.put(out, new Integer(lessThanIndexMap.size()));
					lessThanTally = expandArray(lessThanTally);
					lessThanTally[lessThanTally.length-1] = 1;
				}
				lessThanTot++;
			}
			else {
				if(greaterThanIndexMap.containsKey(out)) {
					Integer in = (Integer)greaterThanIndexMap.get(out);
					loc = in.intValue();
					greaterThanTally[loc]++;
				}
				// found a new one..
				else {
					greaterThanIndexMap.put(out, new Integer(greaterThanIndexMap.size()));
					greaterThanTally = expandArray(greaterThanTally);
					greaterThanTally[greaterThanTally.length-1] = 1;
				}
				greaterThanTot++;
			}
		}

		// now that we have tallies of the outputs for this att value
		// we can calculate the entropy.

		// get the probablities for the examples less than the split
		//double[] probs = new double[lessThanTally.size()];
		double[] probs = new double[lessThanTally.length];
		for(int i = 0; i < lessThanTally.length; i++)
			probs[i] = ((double)lessThanTally[i])/((double)lessThanTot);

		double[] greaterProbs = new double[greaterThanTally.length];
		for(int i = 0; i < greaterThanTally.length; i++)
			greaterProbs[i] = ((double)greaterThanTally[i])/((double)greaterThanTot);

		// return the sum of the information given on each side of the split
		return (lessThanTot/(double)examples.length)*entropy(probs) +
			(greaterThanTot/(double)examples.length)*entropy(greaterProbs);
	}

	private static int[] expandArray(int[] orig) {
		int [] newarray = new int[orig.length+1];
		System.arraycopy(orig, 0, newarray, 0, orig.length);
		return newarray;
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
	private double findSplitValue(int attCol, int[] examples) {
		// copy the examples into a new table
		DoubleColumn dc = new DoubleColumn(examples.length);
		//StringColumn sc = new StringColumn(examples.size());
		IntColumn ic = new IntColumn(examples.length);
		//StringColumn sc = new StringColumn();
		ObjectColumn sc = new ObjectColumn(examples.length);
		//ArrayList exams = new ArrayList();

		int idx = 0;
		for(int i = 0; i < examples.length; i++) {
			//exams.add(new Integer(i));
			//rowIdx = (Integer)examples.get(i);
            int rowIdx = examples[i];
		 	dc.setDouble(table.getDouble(rowIdx, attCol), idx);
			//sc.setString(table.getString(rowIdx.intValue(), outputs[0]), idx);
			//sc.addRow(table.getString(rowIdx.intValue(), outputs[0]));
			ic.setInt(rowIdx, idx);
			sc.setString(table.getString(rowIdx, outputs[0]), idx);
			idx++;
		}
		//sc.trim();
		//Column[] cols = {dc, sc};
		Column[] cols = {dc, sc};
		TableImpl vt = (TableImpl)DefaultTableFactory.getInstance().createTable(cols);

		// sort the table
		vt.sortByColumn(0);

		// now replace ic with a string column
		/*for(int i = 0; i < vt.getNumRows(); i++) {
			//sc.addRow(table.getString(vt.getInt(i, 1), outputs[0]));
			sc.setString(table.getString(vt.getInt(i, 1), outputs[0]), i);
		}

		vt.setColumn(sc, 1);
		*/

		// each row of the new table is an example
		//ArrayList exams = new ArrayList();
        int[] exams = new int[vt.getNumRows()];
		for(int i = 0; i < vt.getNumRows(); i++)
			exams[i] = i;

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
	private EntrSplit numericGain(int attCol, int[] examples) {
		if(debug)
			System.out.println("Calc numericGain: "+table.getColumnLabel(attCol)+" size: "+examples.length+" out: "+table.getColumnLabel(outputs[0]));
		double gain = outputEntropy(outputs[0], examples);

		double splitVal = findSplitValue(attCol, examples);
		double numEntr = numericAttributeEntropy(table, splitVal, examples,
			attCol, outputs[0]);

		gain -= numEntr;
		if(useGainRatio) {
			double spliter = splitInfo(attCol, splitVal, examples);
			gain /= spliter;
		}

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
	private double categoricalGain(int attCol, int[] examples) {
		if(debug)
			System.out.println("Calc categoricalGain: "+table.getColumnLabel(attCol)+" size: "+examples.length);
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
		if(useGainRatio) {
			double sInfo = splitInfo(attCol, 0, examples);
			// divide the information gain by the split info
			gain /= sInfo;
		}
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
	private double categoricalAttributeEntropy(int colNum, String attValue,
		int[] examples) {

		//HashMap outTally = new HashMap();
		int tot = 0;

		int [] outtally = new int[0];
		HashMap outIndexMap = new HashMap();
		for(int i = 0; i < examples.length; i++) {
			int idx = examples[i];

			String s = table.getString(idx, colNum);

			if(s.equals(attValue)) {
				String out = table.getString(idx, outputs[0]);

				if(outIndexMap.containsKey(out)) {
					Integer in = (Integer)outIndexMap.get(out);
					outtally[in.intValue()]++;
				}
				else {
					outIndexMap.put(out, new Integer(outIndexMap.size()));
					outtally = expandArray(outtally);
					outtally[outtally.length-1] = 1;
				}
				tot++;
			}
		}

		/*for(int i = 0; i < examples.size(); i++) {
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
		}*/

		// now that we have tallies of the outputs for this att value
		// we can calculate the entropy.
		/*double[] probs = new double[outTally.size()];
		Iterator it = outTally.values().iterator();
		int idx = 0;
		while(it.hasNext()) {
			Integer in = (Integer)it.next();
			double d = (double)in.intValue();
			probs[idx] = d/tot;
			idx++;
		}*/
		double [] probs = new double[outtally.length];
		for(int i = 0; i < probs.length; i++)
			probs[i] = ((double)outtally[i])/((double)tot);

		return (tot/(double)examples.length)*entropy(probs);
	}

	/**
		Get the unique values of the examples in a column.
		@param colNum the index of the attribute column
		@param examples the list of examples, which correspond to rows of
			the table
		@return an array containing all the unique values for examples in
			this column
	*/
	private String[] uniqueValues(int colNum, int[] examples) {
		int numRows = examples.length;

		// count the number of unique items in this column
		HashSet set = new HashSet();
		for(int i = 0; i < numRows; i++) {
			int rowIdx = examples[i];
			String s = table.getString(rowIdx, colNum);
			if(!set.contains(s))
				set.add(s);
		}

		String[] retVal = new String[set.size()];
		int idx = 0;
		Iterator it = set.iterator();
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
	private double splitInfo(int colNum, double splitVal, int[] examples) {
		int numRows = examples.length;
		double tot = 0;
		double[] probs;

		// if it is a numeric column, there will be two branches.
		// count up the number of examples less than and greater
		// than the split value
		//if(table.getColumn(colNum) instanceof NumericColumn) {
		//if(table.isNumericColumn(colNum)) {
        if(table.isColumnScalar(colNum)) {
			double lessThanTally = 0;
			double greaterThanTally = 0;

			for(int i = 0; i < numRows; i++) {
				int rowIdx = examples[i];
				double d = table.getDouble(rowIdx, colNum);
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
				int rowIdx = examples[i];
				String s = table.getString(rowIdx, colNum);
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
	private double outputEntropy(int colNum, int[] examples) {
		double numRows = (double)examples.length;

		// count the number of unique items in this column
		HashMap map = new HashMap();
		for(int i = 0; i < numRows; i++) {
			//Integer rowIdx = (Integer)examples.get(i);
            int rowIdx = examples[i];
			String s = table.getString(rowIdx, colNum);
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
	private ColSplit getHighestGainAttribute(int[] attributes, int[] examples) {
		ArrayList list = new ArrayList();

		int topCol = 0;
		double highestGain = 0;

		ColSplit retVal = new ColSplit();
		// for each available column, calculate the entropy
		for(int i = 0; i < attributes.length; i++) {
			int col = attributes[i];
			// categorical data
			//if(!(table.getColumn(col) instanceof NumericColumn)) {
			//if(table.isNumericColumn(col)) {
            if(!table.isColumnScalar(col)) {
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
		if(highestGain < infoGainThreshold)
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
	    String[] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
        return in;
	}

	public String[] getOutputTypes() {
	    String[] out = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode",
		    "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
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

        // the set of examples.  the indices of the example rows
        int[] exampleSet;
		// use all rows as examples at first
		System.out.println("DOIT: "+table+" "+table.getNumRows());
        exampleSet = new int[table.getNumRows()];
		for(int i = 0; i < table.getNumRows(); i++)
            exampleSet[i] = i;

		// use all columns as attributes at first
		//ArrayList atts = new ArrayList();
        int[] atts = new int[inputs.length];
		for(int i = 0; i < inputs.length; i++)
			//atts.add(new Integer(inputs[i]));
            atts[i] = inputs[i];

		DecisionTreeNode rootNode = buildTree(exampleSet, atts);
        // rootNode = pruneTree();
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
	private DecisionTreeNode buildTree(int[] examples, int[] attributes) {
		if(debug) {
			System.out.println("BuildTree with "+examples.length+" examples and "+attributes.length+" attributes.");
		}
		DecisionTreeNode root = null;
		String s;
		//Integer rowIdx;
        int rowIdx;

		// if all examples have the same output value, give the root this
		// label-- this node is a leaf.
		boolean allSame = true;
		int counter = 0;
		//rowIdx = (Integer)examples.get(counter);
		rowIdx = examples[counter];
		s = table.getString(rowIdx/*.intValue()*/, outputs[0]);
		counter++;
		while(allSame && counter < examples.length) {
			rowIdx = examples[counter];
			String t = table.getString(rowIdx, outputs[0]);
			if(!t.equals(s)) {
				allSame = false;
			}
			counter++;
		}
		if(allSame) {
			// this is a leaf
			if(debug)
				System.out.println("Creating new CategoricalDTN: "+s);
			root = new CategoricalDecisionTreeNode(s);
			return root;
		}

		// if attributes is empty, label the root according to the most common
		// value this will result in some incorrect classifications...
		// this node is a leaf.
		if(attributes.length == 0) {
			String mostCommon = mostCommonOutputValue(examples);
			// make a leaf
			if(debug)
				System.out.println("Creating new CategoricalDTN: "+mostCommon);
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
			//if(!(table.getColumn(col) instanceof NumericColumn)) {
			//if(!table.isNumericColumn(col)) {
            if(!table.isColumnScalar(col)) {
				// for each possible value v of this attribute in the set
				// of examples add a new branch below the root,
				// corresponding to A = v
				String[] branchVals = uniqueValues(col, examples);
				root = new CategoricalDecisionTreeNode(
					table.getColumnLabel(col));
				for(int i = 0; i < branchVals.length; i++) {
					int[] branchExam = narrowCategoricalExamples(col,
						branchVals[i], examples);
					int[] branchAttr = narrowAttributes(col, attributes);
					if(branchExam.length != 0)
						root.addBranch(branchVals[i], buildTree(branchExam,
							branchAttr));

					// if examples(v) is empty, make the new branch a leaf
					// labelled with the most common value among examples
					else {
						String val = mostCommonOutputValue(examples);
						DecisionTreeNode nde = new
							CategoricalDecisionTreeNode(val);
						root.addBranch(val, nde);
						if(debug)
							System.out.println("Adding Categorical Branch: "+val);
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
				int[] branchExam = narrowNumericExamples(col,
					best.splitValue, examples, false);
				if(branchExam.length != 0) {
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
				if(branchExam.length != 0) {
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
				if(debug)
					System.out.println("Adding Numeric Branches: "+lesser.toString()+" and "+greater.toString());
			}
		}

		// otherwise the information gain was below the threshold
		// make a leaf labelled with the most common value among the examples
		else {
			//System.out.println("below threshold");
			String val = mostCommonOutputValue(examples);
			root = new CategoricalDecisionTreeNode(val);
			if(debug)
				System.out.println("Info gain below threshhold, creating new CategoricalDTN: "+val);
		}

		return root;
	}

	/**
		Find the most common output value from a list of examples.
		@param examples the list of examples
		@return the most common output value from the examples
	*/
	private String mostCommonOutputValue(int[] examples) {
		HashMap map = new HashMap();
		int rowIdx;
		for(int i = 0; i < examples.length; i++) {
			rowIdx = examples[i];
			String s = table.getString(rowIdx, outputs[0]);
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
	private int[] narrowCategoricalExamples(int col, String val, int[] exam) {
		//ArrayList examples = new ArrayList();
        int numNewExamples = 0;
        boolean[] map = new boolean[exam.length];

		for(int i = 0; i < exam.length; i++) {
			String s = table.getString(exam[i], col);
			if(s.equals(val)) {
				//examples.add(rowIdx);
                numNewExamples++;
                map[i] = true;
            }
            else
                map[i] = false;
		}
        int[] examples = new int[numNewExamples];
        int curIdx = 0;
        for(int i = 0; i < exam.length; i++) {
            if(map[i]) {
                examples[curIdx] = exam[i];
                curIdx++;
            }
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
	private int[] narrowNumericExamples(int col, double splitValue, int[] exam,
		boolean greaterThan) {

		//ArrayList examples = new ArrayList();
		int rowIdx;
        int numNewExamples = 0;
        boolean[] map = new boolean[exam.length];

		for(int i = 0; i < exam.length; i++) {
			//rowIdx = (Integer)exam.get(i);
            rowIdx = exam[i];
			double d = table.getDouble(rowIdx, col);
			if(greaterThan) {
				if(d >= splitValue) {
					//examples.add(rowIdx);
                    numNewExamples++;
                    map[i] = true;
                }
                else
                    map[i] = false;
			}
			else {
				if(d < splitValue) {
					//examples.add(rowIdx);
                    numNewExamples++;
                    map[i] = true;
                 }
                 else
                    map[i] = false;
			}
		}

        int[] examples = new int[numNewExamples];
        int curIdx = 0;
        for(int i = 0; i < exam.length; i++) {
            if(map[i]) {
                examples[curIdx] = exam[i];
                curIdx++;
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
	private int[] narrowAttributes(int col, int[] attr) {
		//ArrayList attributes = (ArrayList)attr.clone();
		//attributes.remove(new Integer(col));
        int[] retVal = new int[attr.length-1];
		//return attributes;
        int curIdx = 0;
        for(int i = 0; i < attr.length; i++) {
            if(attr[i] != col) {
                retVal[curIdx] = attr[i];
                curIdx++;
             }
         }
         return retVal;
	}
}
