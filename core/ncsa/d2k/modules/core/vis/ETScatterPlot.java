package ncsa.d2k.modules.core.vis;

import java.io.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
	Given an ExampleTable, plot each numeric input variable against each
	numeric output variable in a ScatterPlot.  A matrix of these plots is
	shown.  The plots can be selected and a larger composite graph of
	these plots can be displayed.

   @author David Clutter
*/
public class ETScatterPlot extends VisModule
	implements HasNames, Serializable {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("Given an ExampleTable, plot ");
		sb.append("each numeric input variable against each numeric ");
		sb.append("output variable in a ScatterPlot.  A matrix of these ");
		sb.append("plots is shown.  The plots can be selected and a ");
		sb.append("larger composite graph of these plots can be displayed.");
		return sb.toString();
    }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "ETScatterPlot";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String []in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return in;
    }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		return null;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		return "The ExampleTable to plot.";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		return "ET";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		return "";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	return "";
    }

	protected UserView createUserView() {
		//return new ETPlotView();
		return new ETScatterPlotWidget();
	}

	protected String[] getFieldNameMapping() {
		return null;
	}
}
