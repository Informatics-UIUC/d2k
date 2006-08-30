package ncsa.d2k.modules.core.prediction.decisiontree;


import ncsa.d2k.core.modules.*;

/**
	Given a DecisionTreeNode that is the root of a decision tree,
	create a new DecisionTreeModel from it.
*/
public class ClearDTTallies extends DataPrepModule  {


	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Given a DecisionTreeModel, clear the values from its nodes.  </body></html>";
	}

	public String getModuleName() {
		return "ClearDTTallies";
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return types;
	}

	// change to prediction table
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "A DecisionTreeModel.";
			default: return "No such input";
		}
	}

	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "DTModel";
			default: return "NO SUCH INPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The same DecisionTreeModel with its values cleared.";
			default: return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "DTModel";
			default: return "NO SUCH OUTPUT!";
		}
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
