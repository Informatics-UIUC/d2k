package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

/**
	Given a DecisionTreeNode that is the root of a decision tree,
	create a new DecisionTreeModel from it.
*/
public class CreateDTModel extends ModelProducerModule {


	public String getModuleInfo() {
		String s = "Given a DecisionTreeNode that is the root ";
		s += " of a decision tree, create a new DecisionTreeModel from it.";
		return s;
	}

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode",
			"ncsa.d2k.util.datatype.ExampleTable"};
		return in;
	}

	// change to prediction table
	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return out;
	}

	public String getInputInfo(int i) {
		return "The root of the decision tree";
	}

	public String getOutputInfo(int i) {
		return "A DecisionTreeModel created from the DecisionTreeNode";
	}

	/**
		Pull in the tree and create the model.  Then push the model out.
	*/
	public void doit() {
		DecisionTreeNode root = (DecisionTreeNode)pullInput(0);
		ExampleTable table = (ExampleTable)pullInput(1);
		DecisionTreeModel mdl = new DecisionTreeModel(root, table);
		pushOutput(mdl, 0);
	}
}
