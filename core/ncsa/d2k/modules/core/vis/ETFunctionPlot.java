package ncsa.d2k.modules.core.vis;

import ncsa.d2k.infrastructure.views.*;
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
		StringBuffer sb = new StringBuffer("Given an ExampleTable, plot ");
		sb.append("each numeric input variable against each numeric ");
		sb.append("output variable in a FunctionPlot.  A matrix of these ");
		sb.append("plots is shown.  The plots can be selected and a ");
		sb.append("larger composite graph of these plots can be displayed.");
		return sb.toString();
    }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "ETFunctionPlot";
    }

	protected UserView createUserView() {
		//return new ETPlotView();
		return new ETFunctionPlotWidget();
	}
}
