package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.infrastructure.modules.*;

/**
	Given a DecisionTreeNode that is the root of a decision tree,
	create a new DecisionTreeModel from it.
*/
public class ClearDTTallies extends DataPrepModule implements HasNames {


	public String getModuleInfo() {
		String s = "Given a DecisionTreeModel, clear the values from its nodes.";
		return s;
	}

	public String getModuleName() {
		return "ClearDTTallies";
	}

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return in;
	}

	// change to prediction table
	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return out;
	}

	public String getInputInfo(int i) {
		return "A DecisionTreeModel.";
	}

	public String getInputName(int i) {
		return "DTModel";
	}

	public String getOutputInfo(int i) {
		return "The same DecisionTreeModel with its values cleared.";
	}

	public String getOutputName(int i) {
		return "DTModel";
	}

	/**
		Pull in the tree and create the model.  Then push the model out.
	*/
	public void doit() {
        DecisionTreeModel mdl = (DecisionTreeModel)pullInput(0);
        DecisionTreeNode root = mdl.getRoot();
        root.clear();
		pushOutput(mdl, 0);
	}
}
