package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import javax.swing.*;

/**
	A simple pie chart.  The data must be in a VerticalTable.  The x value
	of the DataSet must contain the index of the label column, and the y
	value must contain the index of the numerical column.
*/
public class PieChart extends Chart {

	/**
		Create a new PieChart that normalizes the data.  Each item
		will be represented as a fraction of 1.0
	*/
	public PieChart(Table vt, DataSet ds, GraphSettings gs,
		boolean normalize) {
		this(vt, ds, gs);

		DoubleColumn dc = new DoubleColumn(bins);
		Column []cols = new Column[2];
		cols[0] = table.getColumn(set.x);
		cols[1] = dc;

		double total = 0;
		for(int i = 0; i < bins; i++)
			total += table.getDouble(i, set.y);

		for(int i = 0; i < bins; i++) {
			double val = table.getDouble(i, set.y);
			dc.setDouble((double)(val/total), i);
		}

		table = TableFactory.createTable(cols);
	}

	/**
		Create a new PieChart with the data already normalized.
	*/
	public PieChart(Table vt, DataSet ds, GraphSettings gs) {
		super(vt, ds, gs);

		setBackground(Color.white);

		title = settings.title;
	}

	/**
		Paint the charts and the legend.
	*/
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

		if(settings.displaylegend)
			drawLegend(g2);
		if (settings.displaytitle)
			drawTitle(g2);
		drawDataSet(g2, set);
	}

	public void initOffsets() {
		// Offsets of axis
		leftoffset = largespace;
		rightoffset = 65;
		//bottomoffset = 65;
		//topoffset = 65;

		// Offset of legend
		if (!settings.displaylegend) {
			legendheight = 0;
		}
		else {
			String[] names = new String[table.getNumRows()];

			names[0] = table.getString(0, set.x);
			legendwidth = metrics.stringWidth(names[0]);
			for (int index=1; index < table.getNumRows(); index++) {
				String s = table.getString(index, set.x);
				int stringwidth = metrics.stringWidth(s);
				if (stringwidth > legendwidth)
					legendwidth = stringwidth;
			}

			legendwidth += 4*smallspace+samplecolorsize;
			legendheight = (table.getNumRows()*fontheight)+
				(fontheight-samplecolorsize);

			legendleftoffset = leftoffset;
			legendtopoffset = legendheight+2*smallspace;
		}
	 }

	public void resize() {
		leftoffset = graphwidth*(.125);
		topoffset = fontheight+4;
		graphwidth = (.75)*graphwidth;
		graphheight = (.75)*graphheight;
		if(settings.displaylegend) {
			if(graphheight > (getHeight()-legendtopoffset-topoffset-smallspace))
				graphheight = getHeight()-legendtopoffset - topoffset - smallspace;
		}
	}

	/**
		Draw the chart.
	*/
	public void drawDataSet(Graphics2D g2, DataSet set) {
		NumericColumn column = (NumericColumn) table.getColumn(1);

       	// draw chart
		int angle = 0;
		for (int count = 0; count < bins; count++) {

			double ratio = column.getDouble(count);

			if (count == bins - 1)
				drawArc(g2, leftoffset, topoffset+2,
					graphwidth, graphheight, angle,
					(int)(360 - angle), getColor(count));
			else
				drawArc(g2, leftoffset, topoffset+2,
					graphwidth, graphheight, angle,
					(int)(360*ratio), getColor(count));

			angle += (int)(360*ratio);
		}
	}

	/**
		Draw a slice of the pie chart.
	*/
	public void drawArc(Graphics2D g2, double x, double y, double w, double h,
		double start, double end, Color color) {
			g2.setColor(color);
			g2.fill(new Arc2D.Double(x, y, w, h, start, end, Arc2D.PIE));
	}

	/**
		Draw the title of the pie chart.
	*/
	public void drawTitle(Graphics2D g2) {
		int stringwidth = metrics.stringWidth(title);
		double x = (getWidth()-stringwidth)/2;
		double y = (topoffset)/2;

		g2.drawString(title, (int) x, (int) y);
	}

	/**
		Draw the legend for the chart.
		TO DO: Put multiple columns in the legend, right now the items are
		listed in one column
	*/
	public void drawLegend(Graphics2D g2) {
		Color previouscolor = g2.getColor();

		double x = legendleftoffset;
		double y = getHeight()-legendtopoffset;

		g2.setColor(Color.black);

		g2.draw(new Rectangle.Double(x, y, legendwidth, legendheight));

		x += smallspace;
		y += fontheight-samplecolorsize;

		for (int index=0; index < table.getNumRows(); index++) {
			g2.setColor(getColor(index));
			g2.fill(new Rectangle.Double(x, y, samplecolorsize, samplecolorsize));
			y += fontheight;
		}

		g2.setColor(previouscolor);

		x = legendleftoffset;
		y = getHeight()-legendtopoffset;

		x += 2*smallspace+samplecolorsize;
		y += fontheight;

		for (int index=0; index < table.getNumRows(); index++) {
			g2.drawString(table.getString(index, set.x), (int) x, (int) y);
			y += fontheight;
		}
	}
}
