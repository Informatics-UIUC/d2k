package ncsa.d2k.modules.core.optimize.ga.emo.examples;

import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;
import ncsa.gui.*;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	ScatterPlot2D.java
*/
public class LoadScatterPlotModule extends ncsa.d2k.infrastructure.modules.VisModule implements Serializable
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"table\">    <Text>This is the table to plot.</Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
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
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"ScatterPlot\">    <Text>This module is used only with the Load problem demo of NSGA in D2K. It will scatter plot the final pareto front on the load arm problem.</Text>  </Info></D2K>";

	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
	}


	/**
		This pair is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new LoadPlotUserPane();
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
	ScatterPlotUserPane
*/
class LoadPlotUserPane extends ncsa.d2k.controller.userviews.swing.JUserPane {
	LoadScatterPlotModule module;
	TableImpl table;

	public void initView(ViewModule viewmodule) {
		module = (LoadScatterPlotModule) viewmodule;
	}
	public Module getModule () { return module; }
	public void setInput(Object object, int index) {
		table = (TableImpl) object;

		buildView();
	}

	public void buildView() {
		setLayout(new GridBagLayout());

		try {
		Constrain.setConstraints(this, new GraphEditor(table,
			Class.forName("ncsa.d2k.modules.core.optimize.ga.emo.examples.LoadProblemScatterPlot")),
			0, 0, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 1, 1);
		} catch (Exception ex) {
			System.out.println (" the LoadProblemScatterPlot class is not in the modules directory!");
			ex.printStackTrace ();
		}
	}

}

