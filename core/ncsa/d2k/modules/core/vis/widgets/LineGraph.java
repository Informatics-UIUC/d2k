package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.util.datatype.*;

import java.awt.*;
import java.awt.geom.*;

public class LineGraph extends Graph {

	public LineGraph(VerticalTable table, DataSet[] set, GraphSettings settings) {
		super(table, set, settings);
	}

	public void drawDataSet(Graphics2D g2, DataSet set) {
		NumericColumn xcolumn = (NumericColumn) table.getColumn(set.x);
		NumericColumn ycolumn = (NumericColumn) table.getColumn(set.y);

		int size = xcolumn.getNumRows();

		for (int index=0; (index+1) < size; index++) {
			double xvalue0 = xcolumn.getDouble(index);
			double yvalue0 = ycolumn.getDouble(index);

			double xvalue1 = xcolumn.getDouble(index+1);
			double yvalue1 = ycolumn.getDouble(index+1);

			drawPointLine(g2, set.color, xvalue0, yvalue0, xvalue1, yvalue1);
		}
	}

	// Draw two data points and line
	public void drawPointLine(Graphics2D g2, Color color, double x0, double y0, double x1, double y1) {
		Color previouscolor = g2.getColor();

		double x0scale = (x0-xminimum)/xscale+leftoffset;
		double y0scale = graphheight-bottomoffset-(y0-yminimum)/yscale;
		double x1scale = (x1-xminimum)/xscale+leftoffset;
		double y1scale = graphheight-bottomoffset-(y1-yminimum)/yscale;

		g2.setColor(color);
		g2.draw(new Line2D.Double(x0scale, y0scale, x1scale, y1scale));
		g2.fill(new Rectangle2D.Double(x0scale, y0scale, 4, 4));
		g2.fill(new Rectangle2D.Double(x1scale, y1scale, 4, 4));
		g2.setColor(previouscolor);
	}
}
