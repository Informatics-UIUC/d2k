package ncsa.d2k.modules.core.prediction.decisiontree;

import java.io.Serializable;
import java.util.*;

import ncsa.d2k.util.datatype.VerticalTable;

/**
	A DecisionTreeNode for categorical data.  These have as many children
	as there are values of the attribute that this node tests on.
	@author David Clutter
*/
public final class CategoricalDecisionTreeNode extends DecisionTreeNode
	implements Serializable {

	/* Maps an output to a specific child.  Used in evaluate() */
	private HashMap outputToChildMap;

	/**
		Create a new, blank node.
	*/
	/*public CategoricalDecisionTreeNode() {
		super();
		outputToChildMap = new HashMap();
	}*/

	/**
		Create a new node.
		@param lbl the label of this node.
	*/
	public CategoricalDecisionTreeNode(String lbl) {
		super(lbl);
		outputToChildMap = new HashMap();
	}

	/**
		Create a new node.
		@param lbl the label of this node.
		@param prnt the parent node
	*/
	public CategoricalDecisionTreeNode(String lbl, DecisionTreeNode prnt) {
		super(lbl, prnt);
		outputToChildMap = new HashMap();
	}

	/**
		Add a branch to this node, given the label of the branch and
		the child node.  For a CategoricalDecisionTreeNode, the label
		of the branch is the same as the value used to determine the split
		at this node.
		@param val the label of the branch
		@param child the child node
	*/
	public final void addBranch(String val, DecisionTreeNode child) {
		//child.setLabel(val);
		outputToChildMap.put(val, child);
		children.add(child);
		branchLabels.add(val);
		child.setParent(this);
	}

	/**
		This should never be called, because CategoricalDecisionTreeNodes
		do not use a split value.
	*/
	public final void addBranches(double split, String leftlabel,
		DecisionTreeNode left, String rightlabel, DecisionTreeNode right) {
	}

	/**
		Evaluate a record from the data set.  If this is a leaf, return the
		label of this node.  Otherwise find the column of the table that
		represents the attribute that this node evaluates.  Get the value
		of the attribute for the row to test and call evaluate() on
		the appropriate child node.

		@param vt the VerticalTable with the data
		@param row the row of the table to evaluate
		@return the result of evaluating the record
	*/
	public final Object evaluate(VerticalTable vt, int row) {

		if(isLeaf()) {
			incrementOutputTally(label);
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
		String s = vt.getString(row, colNum);

		// lookup the node to branch on in the outputToChildMap
		if(outputToChildMap.containsKey(s)) {
			DecisionTreeNode dtn = (DecisionTreeNode)outputToChildMap.get(s);
			// recurse on the child subtree
			return dtn.evaluate(vt, row);
		}

		incrementOutputTally(UNKNOWN);
		return UNKNOWN;
	}
}
