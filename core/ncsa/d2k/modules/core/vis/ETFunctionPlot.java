package ncsa.d2k.modules.core.vis;


import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
	Given an ExampleTable, plot each numeric input variable against each
	numeric output variable in a FunctionPlot.  A matrix of these plots is
	shown.  The plots can be selected and a larger composite graph of
	these plots can be displayed.

   @author David Clutter
*/
public class ETFunctionPlot extends ETScatterPlot {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Given an ExampleTable, plot each numeric input variable against each     numeric output variable in a FunctionPlot. A matrix of these plots is     shown. The plots can be selected and a larger composite graph of these     plots can be displayed.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "ETFunctionPlot";
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

	protected UserView createUserView() {
		return new ETFunctionPlotWidget();
	}
}
