package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

/**
	Given a DecisionTreeNode that is the root of a decision tree,
	create a new DecisionTreeModel from it.
*/
public class DecisionTreeModelGen extends ModelGeneratorModule {

	static String INFO = "Given a DecisionTreeNode that is the root "
		+" of a decision tree, create a new DecisionTreeModel from it.";
	static String[] IN = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode",
		"ncsa.d2k.util.datatype.ExampleTable"};
	static String[] OUT = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
	static String IN0 = "The root of the decision tree";
	static String OUT0 = "A DecisionTreeModel created from the DecisionTreeNode";

	public String getModuleInfo() {
		return INFO;
	}

	public String[] getInputTypes() {
		return IN;
	}

	// change to prediction table
	public String[] getOutputTypes() {
		return OUT;
	}

	public String getInputInfo(int i) {
		return IN0;
	}

	public String getOutputInfo(int i) {
		return OUT0;
	}

	DecisionTreeModel mdl;

	/**
		Pull in the tree and create the model.  Then push the model out.
	*/
	public void doit() {
		DecisionTreeNode root = (DecisionTreeNode)pullInput(0);
		ExampleTable tbl = (ExampleTable)pullInput(1);
		mdl = new DecisionTreeModel(root, tbl);
		pushOutput(mdl, 0);
	}

	/**
		Return a reference to the model created by this module.
	*/
	public ModelModule getModel() {
		return mdl;
	}

	public void beginExecution() {
		mdl = null;
	}
}
