package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.awt.*;
import java.awt.geom.*;

public class LinearRegressionSmall extends Graph {

	int smallspace = 1;
	int largespace = 3;

	public LinearRegressionSmall(Table table, DataSet[] set, GraphSettings settings) {
		super(table, set, settings);
	}

	public double[] getMinAndMax(Table table, int ndx) {
		double[] minAndMax = new double[2];
		double mandm;
		for (int i = 0; i < table.getNumRows(); i++) {
			mandm = table.getDouble(i, ndx);
			if (mandm > minAndMax[1]) {
				minAndMax[1] = mandm;
			}
			if (mandm < minAndMax[0]) {
				minAndMax[0] = mandm;
			}
		}
		return minAndMax;
	}

	public void drawDataSet(Graphics2D g2, DataSet set) {
		//NumericColumn xcolumn = (NumericColumn) table.getColumn(set.x);
		//NumericColumn ycolumn = (NumericColumn) table.getColumn(set.y);

		int size = table.getNumRows();

		double[] mm = getMinAndMax(table, set.x);
		xdataminimum = mm[0];
		xdatamaximum = mm[1];
		mm = getMinAndMax(table, set.y);
		ydataminimum = mm[0];
		ydatamaximum = mm[1];

		// Draw points
		for (int index=0; index < size; index++) {
			double xvalue = table.getDouble(index, set.x);
			double yvalue = table.getDouble(index, set.y);

			drawPoint(g2, set.color, xvalue, yvalue);
		}

		// Draw line
		double sumx = 0;
		double sumy = 0;
		double sumproductxy = 0;
		double sumxsquared = 0;

		for (int index=0; index < size; index++) {
			double xvalue = table.getDouble(index, set.x);
			double yvalue = table.getDouble(index, set.y);

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

	public void initOffsets() {
		// Offsets of axis
		leftoffset = .2*getWidth()+3;
		rightoffset = .1*getWidth()+2;

		// Offset of legend
		if (!settings.displaylegend) {
			legendheight = 0;
		}
		else {
			String[] names = new String[sets.length];

			DataSet set = sets[0];
			names[0] = set.name;
			legendwidth = metrics.stringWidth(set.name);
			for (int index=1; index < sets.length; index++) {
				set = sets[index];
				int stringwidth = metrics.stringWidth(set.name);
				if (stringwidth > legendwidth)
					legendwidth = stringwidth;
			}

			legendwidth += 4*smallspace+samplecolorsize;
			legendheight = (sets.length*fontheight)+(fontheight-samplecolorsize);

			legendleftoffset = leftoffset;
			legendtopoffset = legendheight+smallspace;
		}

		// Offsets of axis
		bottomoffset = .25*getHeight();
		topoffset = .05*getHeight();
	}

	public void drawPoint(Graphics2D g2, Color color, double xvalue, double yvalue) {
		Color previouscolor = g2.getColor();

		double x = (xvalue-xminimum)/xscale+leftoffset;
		double y = graphheight-bottomoffset-(yvalue-yminimum)/yscale;

		g2.setColor(color);
		g2.fill(new Rectangle2D.Double(x, y, 1, 1));

		g2.setColor(previouscolor);
	}

}
