package ncsa.d2k.modules.core.vis;

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
	Creates a BarChart visualization.  The data is kept in a Table.
	The first column must be a labels column, and the second column must contain
	the frequencies.
*/
public class BarChart2D extends VisModule implements Serializable
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "A Table that holds the data to show.";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
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
		return null;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("Creates a BarChart visualization.  The ");
		sb.append(" data is kept in a Table.  The first column must be a ");
		sb.append(" labels column, and the second column must contain the frequencies.");
		return sb.toString();
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
		return new BarChartUserPane();
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
	BarChartUserPane
*/
class BarChartUserPane extends ncsa.d2k.controller.userviews.swing.JUserPane {
	BarChart2D module;

	Table table;

	public void initView(ViewModule viewmodule) {
		module = (BarChart2D) viewmodule;
	}

	public void setInput(Object object, int index) {
		table = (Table) object;

		buildView();
	}

	public void buildView() {
		DataSet set = new DataSet("dataset", Color.gray, 0, 1);

		GraphSettings settings = new GraphSettings();
		String xaxis = table.getColumnLabel(0);
		String yaxis = table.getColumnLabel(1);
		settings.title = xaxis + " and " + yaxis;
		settings.xaxis = xaxis;
		settings.yaxis = yaxis;

		add(new BarChart(table, set, settings));
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 300);
	}
}

