package ncsa.d2k.modules.core.prediction.decisiontree;

import java.io.Serializable;
import java.util.*;

import ncsa.d2k.util.datatype.VerticalTable;

/**
	A DecisionTreeNode for numerical data.  These are binary nodes that
	split on a value of an attribute.
*/
public class NumericDecisionTreeNode extends DecisionTreeNode
	implements Serializable {

	/** everything less than the split value goes left */
	static int LEFT = 0;
	/** everything greater than the split value goes right */
	static int RIGHT = 1;

	/** the value used to compare whether to go left or right */
	double splitValue;

	/*public NumericDecisionTreeNode() {
		super();
	}*/

	/**
		Create a new NumericDecisionTreeNode
		@param lbl the label
	*/
	public NumericDecisionTreeNode(String lbl) {
		super(lbl);
	}

	/**
		Create a new NumericDecisionTreeNode
		@param lbl the label
		@param prnt the parent node
	*/
	public NumericDecisionTreeNode(String lbl, DecisionTreeNode prnt) {
		super(lbl, prnt);
	}

	/**
		Should never be called, because NumericDecisionTreeNodes use
		a split value.
	*/
	public void addBranch(String val, DecisionTreeNode child) {
	}

	/**
		Add left and right children to this node.
		@param split the split value for this node
		@param leftLabel the label for the left branch
		@param left the left child
		@param rightLabel the label for the right branch
		@param right the right child
	*/
	public void addBranches(double split, String leftLabel,
		DecisionTreeNode left, String rightLabel, DecisionTreeNode right) {

		//left.setLabel(leftLabel);
		//right.setLabel(rightLabel);
		splitValue = split;
		branchLabels.add(leftLabel);
		children.add(left);
		left.setParent(this);
		branchLabels.add(rightLabel);
		children.add(right);
		right.setParent(this);
	}

	/**
		Evaluate a record from the data set.  If this is a leaf, return the
		label of this node.  Otherwise find the column of the table that
		represents the attribute that this node evaluates.  Get the value
		of the attribute for the row to test and call evaluate() on the left
		child if the value is less than our split value, or call evaluate() on
		the right child if the split value is greater than or equal to the
		split value.

		@param vt the VerticalTable with the data
		@param row the row of the table to evaluate
		@return the result of evaluating the record
	*/
	public Object evaluate(VerticalTable vt, int row, int outputCol) {
		String output = vt.getString(row, outputCol);

		if(isLeaf()) {
			incrementOutputTally(output);
			return label;
		}

		// otherwise find our column.  this will be the column
		// whose label is equal to this node's label.
		int colNum = -1;
		for(int i = 0; i < vt.getNumColumns(); i++) {
			String s = vt.getColumnLabel(i);
			if(s.equals(label)) {
				colNum = i;
				break;
			}
		}
		if(colNum < 0) {
			incrementOutputTally(UNKNOWN);
			return UNKNOWN;
		}

		// now get the value from the row.
		double d = vt.getDouble(row, colNum);

		// go left if d is less than split value
		if(d < splitValue) {
			DecisionTreeNode dtn = (DecisionTreeNode)children.get(LEFT);
			incrementOutputTally(output);
			return dtn.evaluate(vt, row, outputCol);
		}
		// otherwise go right
		else {
			DecisionTreeNode dtn = (DecisionTreeNode)children.get(RIGHT);
			incrementOutputTally(output);
			return dtn.evaluate(vt, row, outputCol);
		}
	}
}
