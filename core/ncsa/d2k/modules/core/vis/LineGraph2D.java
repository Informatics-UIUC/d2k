package ncsa.d2k.modules.core.vis;

import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;
import ncsa.d2k.util.datatype.*;
import ncsa.gui.*;

import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
	LineGraph2D.java
*/
public class LineGraph2D extends ncsa.d2k.infrastructure.modules.VisModule
    implements Serializable, HasNames {

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
        return "A table to visualize.";
	}

    public String getInputName(int index) {
        return "Table";
    }

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.util.datatype.Table"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			default: return "No such output";
		}

	}

    public String getOutputName(int index) {
        return "";
    }

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {		};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
        String s = "Visualize the NumericColumns in a 2D scatter plot. ";
        s += "A line is drawn through each point in order.";
        return s;
	}

    public String getModuleName() {
        return "LineGraph2D";
    }

	/**
		This pair is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new LineGraphUserPane();
	}

	/**
		This pair returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
		return null;

	}

}


/**
	LineGraphUserPane
*/
class LineGraphUserPane extends ncsa.d2k.controller.userviews.swing.JUserPane {
	LineGraph2D module;

	Table table;

	public void initView(ViewModule viewmodule) {
		module = (LineGraph2D) viewmodule;
	}

	public void setInput(Object object, int index) {
		table = (Table) object;

		buildView();
	}

	public void buildView() {
		setLayout(new GridBagLayout());

		Constrain.setConstraints(this, new GraphEditor(table, GraphEditor.LINE_GRAPH), 0, 0, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 1, 1);
	}

}

