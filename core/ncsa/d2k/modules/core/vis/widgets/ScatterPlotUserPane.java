package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;


import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.controller.userviews.swing.JUserPane;
import ncsa.d2k.util.datatype.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.vis.*;

/**
	ScatterPlotUserPane
*/
public class ScatterPlotUserPane extends ncsa.d2k.controller.userviews.swing.JUserPane {
	ScatterPlot2D module;

	Table table;

	public void initView(ViewModule viewmodule) {
		module = (ScatterPlot2D) viewmodule;
	}
	public Module getModule () { return module; }

	public void setInput(Object object, int index) {
		table = (Table) object;
		buildView();
	}

	public void buildView() {
		setLayout(new GridBagLayout());

		Constrain.setConstraints(this, new GraphEditor(table, GraphEditor.SCATTER_PLOT), 0, 0, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 1, 1);
	}
}

