package ncsa.d2k.modules.core.prediction.decisiontree;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
	Given a DecisionTreeNode that is the root of a decision tree,
	create a new DecisionTreeModel from it.
*/
public class CreateDTModel extends ModelProducerModule  {

	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Given a DecisionTreeNode that is the root of a decision tree, create a new     DecisionTreeModel from it.  </body></html>";
	}

    public String getModuleName() {
		return "CreateDTModel";
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode","ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	// change to prediction table
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The root of the decision tree";
			case 1: return "The table used to build the tree.";
			default: return "No such input";
		}
	}

    public String getInputName(int i) {
		switch(i) {
			case 0:
				return "DTRoot";
			case 1:
				return "TrainingTable";
			default: return "NO SUCH INPUT!";
		}
	}


	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "A DecisionTreeModel created from the DecisionTreeNode";
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
		DecisionTreeNode root = (DecisionTreeNode)pullInput(0);
		ExampleTable table = (ExampleTable)pullInput(1);
		DecisionTreeModel mdl = new DecisionTreeModel(root, table);
		pushOutput(mdl, 0);
	}
}
