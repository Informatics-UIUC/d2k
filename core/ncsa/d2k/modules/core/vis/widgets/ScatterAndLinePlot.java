package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.awt.*;

/**
	ScatterAndLinePlot.java

	Extends the Graph class to plot a set of data points along with a
	connected set of data.
	@author talumbau
*/

public class ScatterAndLinePlot extends ScatterPlot {

	DataSet[] ConnectedSets;
	TableImpl fTable;
	public ScatterAndLinePlot () {}

	public ScatterAndLinePlot(TableImpl Scattertable, TableImpl Functiontable, DataSet[] Scatterset, DataSet[] Functionset, GraphSettings settings) {
		super(Scattertable, Scatterset, settings);
		ConnectedSets = Functionset;
		fTable = Functiontable;

		// check if the x and y min and max (based on the scatter data) are
		// really the min and max when compared to the connected data
		DataSet set;
		double xmin, xmax, ymin, ymax;
		NumericColumn column;

		// x data check
		set = ConnectedSets[0];
		column = (NumericColumn) fTable.getColumn(set.x);
		xmin = column.getMin();
		xmax = column.getMax();
		for (int index=1; index < ConnectedSets.length; index++) {
			set = ConnectedSets[index];
			column = (NumericColumn) fTable.getColumn(set.y);

			double value = column.getMin();
			if (value < xmin)
				xmin = value;

			value = column.getMax();
			if (value > xmax)
				xmax = value;
		}
		if (xmin < xminimum)
			xminimum = xmin;
		if (xmax > xmaximum)
			xmaximum = xmax;


		// y data check
		set = ConnectedSets[0];
		column = (NumericColumn) fTable.getColumn(set.y);
		ymin = column.getMin();
		ymax = column.getMax();
		for (int index=1; index < ConnectedSets.length; index++) {
			set = ConnectedSets[index];
			column = (NumericColumn) fTable.getColumn(set.y);

			double value = column.getMin();
			if (value < ymin)
				ymin = value;

			value = column.getMax();
			if (value > ymax)
				ymax = value;
		}
		if (ymin < yminimum)
			yminimum = ymin;
		if (ymax > ymaximum)
			ymaximum = ymax;
	}

	//implements the abstract method of the Graph class to draw a function
	//line
	public void drawConnectedDataSet(Graphics2D g2, DataSet set) {
		System.out.println("the color is "+set.color);
		NumericColumn xcolumn = (NumericColumn) fTable.getColumn(set.x);
		NumericColumn ycolumn = (NumericColumn) fTable.getColumn(set.y);

		int ypixel, lastypixel;
		int size = xcolumn.getNumRows();
		double xvalue, yvalue, oldx, oldy;
		yvalue = ycolumn.getDouble(0);
		xvalue = xcolumn.getDouble(0);

		for ( int index=1; index<size; index++){
			oldx = xvalue;
			oldy = yvalue;
			xvalue = xcolumn.getDouble(index);
			yvalue = ycolumn.getDouble(index);
			drawFLine(g2, set.color, oldx, oldy, xvalue, yvalue);
		}
	}

	//handles the painting of a line from one point to another
	public void drawFLine(Graphics2D g2, Color color, double oldx, double oldy, double xvalue, double yvalue) {
		Color prevcolor = g2.getColor();

		double x1 = (oldx - xminimum)/xscale + leftoffset;
		double y1 = graphheight - bottomoffset - (oldy-yminimum)/yscale;
		double x2 = (xvalue-xminimum)/xscale + leftoffset;
		double y2 = graphheight-bottomoffset - (yvalue-yminimum)/yscale;

		int xint1 = (int) (Math.round(x1));
		int yint1 = (int) (Math.round(y1));
		int xint2 = (int) (Math.round(x2));
		int yint2 = (int) (Math.round(y2));



		g2.setColor(color);
		g2.drawLine(xint1, yint1, xint2, yint2);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		font = g2.getFont();
		metrics = getFontMetrics(font);
		fontheight = metrics.getHeight();
		fontascent = metrics.getAscent();

		graphwidth = getWidth();
		graphheight = getHeight();

		// Determine offsets
		initOffsets();

		resize();

		xvalueincrement = (xmaximum-xminimum)/gridsize;
		yvalueincrement = (ymaximum-yminimum)/gridsize;

		xscale = (xmaximum-xminimum)/(graphwidth-leftoffset-rightoffset);
		yscale = (ymaximum-yminimum)/(graphheight-topoffset-bottomoffset);

		drawAxis(g2);
		if (settings.displaylegend)
			drawLegend(g2);
		if (settings.displaygrid)
			drawGrid(g2);
		if (settings.displaytickmarks)
			drawTickMarks(g2);
		if (settings.displayscale)
			drawScale(g2);
		if (settings.displayaxislabels)
			drawAxisLabels(g2);
		if (settings.displaytitle)
			drawTitle(g2);
		for (int index=0; index < sets.length; index++)
			drawDataSet(g2, sets[index]);

		for (int j=0; j < ConnectedSets.length; j++){
			drawConnectedDataSet(g2, ConnectedSets[j]);
	}

}

}
