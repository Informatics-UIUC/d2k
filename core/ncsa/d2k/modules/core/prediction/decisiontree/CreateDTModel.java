package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
	Given a DecisionTreeNode that is the root of a decision tree,
	create a new DecisionTreeModel from it.
*/
public class CreateDTModel extends ModelProducerModule implements HasNames {

	public String getModuleInfo() {
		String s = "Given a DecisionTreeNode that is the root ";
		s += " of a decision tree, create a new DecisionTreeModel from it.";
		return s;
	}

    public String getModuleName() {
        return "CreateDTModel";
    }

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode",
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return in;
	}

	// change to prediction table
	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return out;
	}

	public String getInputInfo(int i) {
        if(i == 0)
		    return "The root of the decision tree";
        else
            return "The table used to build the tree.";
	}

    public String getInputName(int i) {
        if(i == 0)
            return "DTRoot";
        else
            return "TrainingTable";
    }


	public String getOutputInfo(int i) {
		return "A DecisionTreeModel created from the DecisionTreeNode";
	}

    public String getOutputName(int i) {
        return "DTModel";
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
