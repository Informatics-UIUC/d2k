package ncsa.d2k.modules.core.vis;


import java.awt.*;
import java.io.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
	Creates a BarChart visualization.  The data is kept in a Table.
	The first column must be a labels column, and the second column must contain
	the frequencies.
*/
public class BarChart2D extends VisModule
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
		String[] types = {		};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Creates a BarChart visualization. The data is kept in a Table. The first     column must be a labels column, and the second column must contain the     frequencies.  </body></html>";
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
class BarChartUserPane extends ncsa.d2k.userviews.swing.JUserPane {
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

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "BarChart2D";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			default: return "NO SUCH OUTPUT!";
		}
	}
}

