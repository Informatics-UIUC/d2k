package ncsa.d2k.modules.core.vis;

import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;
import ncsa.d2k.util.datatype.*;
import ncsa.gui.*;
import ncsa.d2k.gui.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
	LinearRegression2D.java
*/
public final class LinearRegression2D extends ncsa.d2k.infrastructure.modules.VisModule
    implements Serializable, HasNames {

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
        return "A Table to visualize.";
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
       String s = "Visualize the NumericColumns in a 2D scatter plot.  A line ";
       s += "is approximated through the points.";
       return s;
	}

    public String getModuleName() {
        return "LinearRegression";
    }

	/**
		This pair is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new LinearRegressionUserPane();
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
	LinearRegressionUserPane
*/
class LinearRegressionUserPane extends ncsa.d2k.controller.userviews.swing.JUserPane
	implements ActionListener {
	LinearRegression2D module;

	Table table;
	JMenuItem help;
	JMenuBar menuBar;
	HelpWindow hWindow;

	public void initView(ViewModule viewmodule) {
		module = (LinearRegression2D) viewmodule;
		menuBar = new JMenuBar();
		JMenu m1 = new JMenu("File");
		help = new JMenuItem("Help...");
		help.addActionListener(this);
		m1.add(help);
		menuBar.add(m1);
		hWindow = new HelpWindow();
	}

	public Object getMenu() {
		return menuBar;
	}

	public void setInput(Object object, int index) {
		table = (Table) object;

		buildView();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == help)
			hWindow.setVisible(true);
	}

	public void buildView() {
		setLayout(new GridBagLayout());

		Constrain.setConstraints(this, new GraphEditor(table, GraphEditor.LINEAR_REGRESSION), 0, 0, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 1, 1);
	}

    private class HelpWindow extends JD2KFrame {
    	HelpWindow() {
			super("About LinearRegression2D");
			JEditorPane jep = new JEditorPane("text/html", getHelpString());
			getContentPane().add(new JScrollPane(jep));
			setSize(400, 400);
		}
	}

    private static final String getHelpString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<h2>LinearRegression2D</h2>");
        sb.append("This module visualizes a data set in two dimensions.  The data points ");
		sb.append("are drawn and a line is approximated through the points.  ");
		sb.append("Multiple data sets can be plotted on the same set of coordinate axes.  ");
		sb.append("Each data set must have a unique name.");
		sb.append("<h3>Scatter Plot</h3>");
		sb.append("Customize which data sets are plotted.  Changes are not reflected ");
		sb.append("until Refresh is pressed.");
		sb.append("<ul><li>Name: The name for a data set.");
		sb.append("<li>Color: The color to shade points of this data set. A color ");
		sb.append("chooser is displayed when the button is pressed.");
		sb.append("<li>X Variable: The column of the Table to plot on the x-axis.");
		sb.append("<li>Y Variable: The column of the Table to plot on the y-axis.");
		sb.append("<li>Add: Add the new data set to the list of data sets.  It will ");
		sb.append("not be displayed until Refresh is pressed.");
		sb.append("<li>List: The list of data sets to plot.");
		sb.append("<li>Delete: Remove the highlighted data set from the list.  ");
		sb.append("It will not be reflected in the graph until Refresh is pressed.");
		sb.append("</ul>");
		sb.append("<h3>Settings</h3>");
		sb.append("Customize how the data sets are displayed.  When left blank, ");
		sb.append("default values appropriate to the range of the data are used.");
		sb.append("  Changes are not reflected until Refresh is pressed.");
		sb.append("<ul><li>X Minimum: The minimum x value for the scale.");
		sb.append("<li>X Maximum: The maximum x value for the scale.");
		sb.append("<li>Y Minimum: The minimum y value for the scale.");
		sb.append("<li>Y Maximum: The maximum y value for the scale.");
		sb.append("<li>Title: The title for the graph.");
		sb.append("<li>X Axis: The label for the x axis.");
		sb.append("<li>Y Axis: The label for the y axis.");
		sb.append("<li>Grid: Show the grid if checked.");
		sb.append("<li>Legend: Show the legend if checked.");
        sb.append("</ul></body></html>");
        return sb.toString();
    }
}
