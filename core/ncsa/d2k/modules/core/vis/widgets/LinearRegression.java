package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.awt.*;
import java.awt.geom.*;

public class LinearRegression extends Graph {

	public LinearRegression(TableImpl table, DataSet[] set, GraphSettings settings) {
		super(table, set, settings);
	}

	public void drawDataSet(Graphics2D g2, DataSet set) {
		NumericColumn xcolumn = (NumericColumn) table.getColumn(set.x);
		NumericColumn ycolumn = (NumericColumn) table.getColumn(set.y);

		int size = xcolumn.getNumRows();

		xdataminimum = xcolumn.getMin();
		xdatamaximum = xcolumn.getMax();
		ydataminimum = ycolumn.getMin();
		ydatamaximum = ycolumn.getMax();

		// Draw points
		for (int index=0; index < size; index++) {
			double xvalue = xcolumn.getDouble(index);
			double yvalue = ycolumn.getDouble(index);

			drawPoint(g2, set.color, xvalue, yvalue);
		}

		// Draw line
		double sumx = 0;
		double sumy = 0;
		double sumproductxy = 0;
		double sumxsquared = 0;

		for (int index=0; index < size; index++) {
			double xvalue = xcolumn.getDouble(index);
			double yvalue = ycolumn.getDouble(index);

			sumx += xvalue;
			sumy += yvalue;
			sumproductxy += (xvalue*yvalue);
			sumxsquared += (xvalue*xvalue);
		}

		double numerator = (size*sumproductxy)-(sumx*sumy);
		double denominator = (size*sumxsquared)-(sumx*sumx);
		double slope = numerator/denominator;

		double intercept = (sumy-slope*sumx)/size;

		double startregression = slope*xdataminimum+intercept;
		double endregression = slope*xdatamaximum+intercept;

		drawRegressionLine(g2, set.color, xdataminimum, startregression, xdatamaximum, endregression);
	}

	public void drawRegressionLine(Graphics2D g2, Color color, double x0, double y0, double x1, double y1) {
		Color previouscolor = g2.getColor();

		double x0scale = (x0-xminimum)/xscale+leftoffset;
		double y0scale = graphheight-bottomoffset-(y0-yminimum)/yscale;
		double x1scale = (x1-xminimum)/xscale+leftoffset;
		double y1scale = graphheight-bottomoffset-(y1-yminimum)/yscale;

		Rectangle previousbounds = g2.getClipBounds();
		Rectangle bounds = new Rectangle((int) leftoffset, (int) topoffset,
			(int) (graphwidth-leftoffset-rightoffset), (int) (graphheight-bottomoffset-topoffset));

		g2.setClip(bounds);
		g2.setColor(color);
		g2.draw(new Line2D.Double(x0scale, y0scale, x1scale, y1scale));
		g2.setColor(previouscolor);
		g2.setClip(previousbounds);
	}
}
