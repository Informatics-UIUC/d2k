package ncsa.d2k.modules.core.vis.widgets;

import javax.swing.JPanel;
import java.awt.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	A Chart is similar to a Graph, but it does not plot
	data points.  Aggregate values are shown, such as in
	a BarChart or PieChart.
*/
public abstract class Chart extends JPanel {
	// Legend
	double legendleftoffset, legendtopoffset;
	double legendwidth, legendheight;
	// Offset from left
	double leftoffset;

	// Offset from right
	double rightoffset;

	// Offset from top
	double topoffset;

	// Offset from bottom
	double bottomoffset;

	// Empty space
	int smallspace = 5;
	int largespace = 10;
	double samplecolorsize = 8;

	// the data
	DataSet set;
	GraphSettings settings;
	TableImpl table;
	int bins;

	// dimensions of the chart
	double graphwidth, graphheight;
	int gridsize;

	// labels to show
	String title, xlabel, ylabel;

	// Font
	Font font;
	FontMetrics metrics;
	int fontheight, fontascent;

	// color generation
	Color[] colors = {
		new Color(253, 204, 138), new Color(148, 212, 161),
		new Color(153, 185, 216), new Color(189, 163, 177),
		new Color(213, 213, 157), new Color(193,  70,  72),
		new Color( 29, 136, 161), new Color(187, 116, 130),
		new Color(200, 143,  93), new Color(127, 162, 133)
	};

	abstract public void initOffsets();
	abstract public void resize();

	public Chart(TableImpl t, DataSet d, GraphSettings g) {
		table = t;
		set = d;
		settings = g;
		bins = table.getNumRows();
	}

	/**
		Get a color.
	*/
	public Color getColor(int i) {
		return colors[i % colors.length];
	}
}
