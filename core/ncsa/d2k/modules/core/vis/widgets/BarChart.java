package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.util.datatype.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import javax.swing.*;

/**
	A simple BarChart.  The data is stored in a Table.
	The x value of the DataSet is the index of the column with the
	labels, while the y value must be the index of the column with
	frequencies.
*/
public class BarChart extends Chart {

	// Minimum and maximum scale values
	double xminimum, xmaximum;
	double yminimum, ymaximum;

	// Units per increment
	double xvalueincrement, yvalueincrement;

	// Pixels per increment
	double xoffsetincrement, yoffsetincrement;
	double minimumxoffsetincrement = 40;
	double minimumyoffsetincrement = 15;

	// Units per pixel
	double xscale, yscale;

	// Minimum and maximum data values
	double xdataminimum, xdatamaximum;
	double ydataminimum, ydatamaximum;

	int tickmarksize = 4;

	public BarChart(Table table, DataSet set, GraphSettings settings) {
		/*this.table = table;
		this.set = set;
		this.settings = settings;
		bins = table.getNumRows();
		*/
		super(table, set, settings);

		setBackground(Color.white);

		title = settings.title;
		xlabel = settings.xaxis;
		ylabel = settings.yaxis;

		/*
		// Find interval for x data
		if ((settings.xminimum == null) || (settings.xmaximum == null)) {

		}
		else {
			xminimum = settings.xminimum.doubleValue();
			xmaximum = settings.xmaximum.doubleValue();
		}
		*/

		// Find interval for y data
		NumericColumn column;
		if ((settings.yminimum == null) || (settings.ymaximum == null)) {
			column = (NumericColumn) table.getColumn(set.y);
			yminimum = column.getMin()-.25*column.getMin();
			if (yminimum < 0)
				yminimum = 0;
			ymaximum = column.getMax()+.25*column.getMax();
		}
		else {
			yminimum = settings.yminimum.doubleValue();
			ymaximum = settings.ymaximum.doubleValue();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);

		font = g2.getFont();
		metrics = getFontMetrics(font);
		fontheight = metrics.getHeight();
		fontascent = metrics.getAscent();

		graphwidth = getWidth();
		graphheight = getHeight();

		// Determine offsets
		initOffsets();

		resize();

		yvalueincrement = (ymaximum-yminimum)/gridsize;

		yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
		xoffsetincrement = (graphwidth-leftoffset-rightoffset)/bins;

		//xscale = (xmaximum-xminimum)/(graphwidth-leftoffset-rightoffset);
		yscale = (ymaximum-yminimum)/(graphheight-topoffset-bottomoffset);

		drawAxis(g2);
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
		drawDataSet(g2, set);
	}

	public void initOffsets() {
		// Offsets of axis
		leftoffset = 65;
		rightoffset = 65;
		bottomoffset = 65;
		topoffset = 65;
	}

	// Resize scale
	public void resize() {
		gridsize = settings.gridsize;

		// y
		yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
		while ((yoffsetincrement < minimumyoffsetincrement) && (gridsize > 0)) {
			gridsize = gridsize/2;
			yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
		}
	}

	public void drawDataSet(Graphics2D g2, DataSet set) {
		NumericColumn column = (NumericColumn) table.getColumn(1);
		double x = leftoffset;
		double barwidth = xoffsetincrement;

		for (int index=0; index < bins; index++) {
			double value = column.getDouble(index);
			double y = graphheight-bottomoffset-(value-yminimum)/yscale;
			double barheight = (value-yminimum)/yscale;

			g2.setColor(new Color(102, 153, 255));
			g2.fill(new Rectangle2D.Double(x, y, barwidth, barheight));
			g2.setColor(Color.black);
			g2.draw(new Rectangle2D.Double(x, y, barwidth, barheight));
			x += xoffsetincrement;
		}
	}

	// Draw data point
	public void drawPoint(Graphics2D g2, Color color, double xvalue,
		double yvalue) {
		Color previouscolor = g2.getColor();

		double x = (xvalue-xminimum)/xscale+leftoffset;
		double y = graphheight-bottomoffset-(yvalue-yminimum)/yscale;

		g2.setColor(color);
		g2.fill(new Rectangle2D.Double(x, y, 4, 4));

		g2.setColor(previouscolor);
	}

	public void drawAxis(Graphics2D g2) {
		g2.draw(new Line2D.Double(leftoffset,topoffset,
			leftoffset, graphheight-bottomoffset));
		g2.draw(new Line2D.Double(leftoffset,graphheight-bottomoffset,
			graphwidth-rightoffset, graphheight-bottomoffset));
	}

	public void drawScale(Graphics2D g2) {
		NumberFormat numberformat = NumberFormat.getInstance();
		numberformat.setMaximumFractionDigits(3);

		// x axis
		TextualColumn textcolumn;
		double x = leftoffset+(xoffsetincrement/2);
		for (int index=0; index < bins; index++) {
			textcolumn = (TextualColumn) table.getColumn(set.x);
			String bin = (String) textcolumn.getRow(index);
			int stringwidth = metrics.stringWidth(bin);
			g2.drawString(bin, (int) (x-stringwidth/2),
				(int) (graphheight-bottomoffset+fontheight));
			x += xoffsetincrement;
		}

		// y axis
		double yvalue =  yminimum;
		double y = graphheight-bottomoffset;
		for (int index=0; index < gridsize; index++) {
			String string = numberformat.format(yvalue);
			int stringwidth = metrics.stringWidth(string);
			g2.drawString(string, (int) (leftoffset-stringwidth-smallspace),
				(int) (y+fontascent/2));
			y -= yoffsetincrement;
			yvalue += yvalueincrement;
		}
	}

	public void drawTitle(Graphics2D g2) {
		int stringwidth = metrics.stringWidth(title);
		double x = (graphwidth-stringwidth)/2;
		double y = (topoffset)/2;

		g2.drawString(title, (int) x, (int) y);
	}

	public void drawAxisLabels(Graphics2D g2) {
		int stringwidth;
		double xvalue, yvalue;

		// x axis
		stringwidth = metrics.stringWidth(xlabel);
		xvalue = (graphwidth-stringwidth)/2;
		yvalue = graphheight-(bottomoffset+fontheight)/2+(2*largespace);
		g2.drawString(xlabel, (int) xvalue, (int) yvalue);

		// y axis
		AffineTransform transform = g2.getTransform();

		stringwidth = metrics.stringWidth(ylabel);
		xvalue = (leftoffset-fontascent-smallspace)/2;
		yvalue = (graphheight+stringwidth)/2;
		AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(-90), xvalue, yvalue);
		g2.transform(rotate);
		g2.drawString(ylabel, (int) xvalue, (int) yvalue);

		g2.setTransform(transform);
	}

	public void drawTickMarks(Graphics2D g2) {
		// x axis
		double x = leftoffset+(xoffsetincrement/2);
		for (int index=0; index < bins; index++) {
			g2.draw(new Line2D.Double(x, graphheight-bottomoffset-tickmarksize, x, graphheight-bottomoffset+tickmarksize));
			x += xoffsetincrement;
		}

		// y axis
		double y = topoffset+yoffsetincrement;
		for (int index=0; index < gridsize; index++) {
			g2.draw(new Line2D.Double(leftoffset-tickmarksize, y, leftoffset+tickmarksize, y));
			y += yoffsetincrement;
		}
	}

	public void drawGrid(Graphics2D g2) {
		Color previouscolor = g2.getColor();
		g2.setColor(Color.gray);

		// y axis
		double y = topoffset+yoffsetincrement;
		for (int index=0; index < gridsize-1; index++) {
			g2.draw(new Line2D.Double(leftoffset, y, graphwidth-rightoffset, y));
			y += yoffsetincrement;
		}

		g2.setColor(previouscolor);
	}
}
