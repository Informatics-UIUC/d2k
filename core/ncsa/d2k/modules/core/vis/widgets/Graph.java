package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import javax.swing.*;
import java.util.HashMap;

public abstract class Graph extends JPanel {

	// Data
	protected Table table;
	protected DataSet[] sets;
	protected GraphSettings settings;

	protected HashMap[] xStringLookup;
	protected HashMap[] yStringLookup;

	// Minimum and maximum scale values
	protected double xminimum, xmaximum;
	protected double yminimum, ymaximum;

	// Offset from left
	protected double leftoffset;

	// Offset from right
	protected double rightoffset;

	// Offset from top
	protected double topoffset;

	// Offset from bottom
	protected double bottomoffset;

	// Units per increment
	protected double xvalueincrement, yvalueincrement;

	// Pixels per increment
	protected double xoffsetincrement, yoffsetincrement;
	protected double minimumxoffsetincrement = 40;
	protected double minimumyoffsetincrement = 15;

	// Units per pixel
	protected double xscale, yscale;

	// Minimum and maximum data values
	protected double xdataminimum, xdatamaximum;
	protected double ydataminimum, ydatamaximum;

	// Legend
	protected double legendleftoffset, legendtopoffset;
	protected double legendwidth, legendheight;
	protected double samplecolorsize = 8;

	// Labels
	protected String title, xlabel, ylabel;

	protected int graphwidth, graphheight;
	protected int gridsize;
	protected int tickmarksize = 4;

	// Empty space
	protected int smallspace = 5;
	protected int largespace = 10;

	// Point size
	protected int point_size = 4;

	// Font
	protected Font font;
	protected FontMetrics metrics;
	protected int fontheight, fontascent;

	public Graph() {
	}

	public Graph(Table table, DataSet[] sets, GraphSettings settings) {
		this.init (table, sets, settings);
	}

	public void init (Table table, DataSet[] sets, GraphSettings settings) {
		this.table = table;
		this.sets = sets;
		this.settings = settings;

		xStringLookup = new HashMap[table.getNumColumns()];
		yStringLookup = new HashMap[table.getNumColumns()];

		setBackground(Color.white);

		title = settings.title;
		xlabel = settings.xaxis;
		ylabel = settings.yaxis;

		DataSet set;
		Column column;

		// Find interval for x data
		if ((settings.xminimum == null) || (settings.xmaximum == null)) {
			if ((settings.xdataminimum == null) ||
				(settings.xdatamaximum == null)) {

				// find the min + max for the first set
				set = sets[0];
				column = table.getColumn(set.x);
				if(column instanceof NumericColumn) {
					xminimum = ((NumericColumn)column).getMin();
					xmaximum = ((NumericColumn)column).getMax();
				}
				else {
					// iterate through column and assign an integer to each unique val
					xStringLookup[set.x] = createUniqueValueMap(table, set.x);
					xminimum = 0;
					xmaximum = xStringLookup[set.x].size()+1;
				}
				// find the min and max for all other sets
				for (int index=1; index < sets.length; index++) {
					set = sets[index];
					column = table.getColumn(set.x);

					if(column instanceof NumericColumn) {
						double value = ((NumericColumn)column).getMin();
						if (value < xminimum)
							xminimum = value;

						value = ((NumericColumn)column).getMax();
						if (value > xmaximum)
							xmaximum = value;
					}
					else {
						// iterate through column and assign an integer to each unique val
						xStringLookup[set.x] = createUniqueValueMap(table, set.x);
						if(0 < xminimum)
							xminimum = 0;
						if(xStringLookup[set.x].size()+1 > xmaximum)
							xmaximum = xStringLookup[set.x].size()+1;
					}
				}
			} else {
				xminimum = settings.xdataminimum.doubleValue ();
				xmaximum = settings.xdatamaximum.doubleValue ();
			}
		}
		else {
			xminimum = settings.xminimum.doubleValue();
			xmaximum = settings.xmaximum.doubleValue();
		}

		// Find interval for y data
		if ((settings.yminimum == null) || (settings.ymaximum == null)) {
			if ((settings.ydataminimum == null) ||
				(settings.ydatamaximum == null)) {
				// find the min and max for the first set
				set = sets[0];
				column = table.getColumn(set.y);
				if(column instanceof NumericColumn) {
					yminimum = ((NumericColumn)column).getMin();
					ymaximum = ((NumericColumn)column).getMax();
				}
				else {
					// iterate through column and assign an integer to each unique val
					yStringLookup[set.y] = createUniqueValueMap(table, set.y);
					yminimum = 0;
					ymaximum = yStringLookup[set.y].size()+1;
				}
				for (int index=1; index < sets.length; index++) {
					set = sets[index];
					column = table.getColumn(set.y);

					if(column instanceof NumericColumn) {
						double value = ((NumericColumn)column).getMin();
						if (value < yminimum)
							yminimum = value;

						value = ((NumericColumn)column).getMax();
						if (value > ymaximum)
							ymaximum = value;
					}
					else {
						// iterate through column and assign an integer to each unique val
						yStringLookup[set.y] = createUniqueValueMap(table, set.y);
						if(0 < yminimum)
							yminimum = 0;
						if(yStringLookup[set.y].size()+1 > ymaximum)
							ymaximum = yStringLookup[set.y].size()+1;
					}
				}
			} else {
				yminimum = settings.ydataminimum.doubleValue ();
				ymaximum = settings.ydatamaximum.doubleValue ();
			}
		}
		else {
			yminimum = settings.yminimum.doubleValue();
			ymaximum = settings.ymaximum.doubleValue();
		}

		if(xmaximum == xminimum) {
			xmaximum = xmaximum+xmaximum;
			xminimum = xminimum - xminimum;
		}
		if(ymaximum == yminimum) {
			ymaximum = ymaximum+ymaximum;
			yminimum = yminimum-yminimum;
		}
	}

	/**
	 *	Set the table for this graph.  Reinitialize the table.
	 *	@param t the new table
	 */
	public void setTable(Table t) {
		init(t, sets, settings);
		repaint();
	}

	/**
	 * Iteratate through a column and map each unique value to an integer.
	 * Return a HashMap containing the relations.
	 */
	static protected HashMap createUniqueValueMap(Table table, int column) {
		HashMap map = new HashMap();
		int idx = 1;
		for(int i = 0; i < table.getColumn(column).getNumRows(); i++) {
			String s = table.getString(i, column);
			if(!map.containsKey(s)) {
				map.put(s, new Integer(idx));
				idx++;
			}
		}
		return map;
	}

	/**
	 * This method computes whatever is necessary to create a mouse mask. How this
	 * mouse mask is maintained is the responsiblity of the subclass, since this
	 * class does not mousing.
	 */
	public void getMouseMask () {}

	/**
	 * Set the point size.
	 */
	public void setPointSize (int ns) { point_size = ns; }

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
		getMouseMask ();
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
	}

	public void initOffsets() {
		// Offsets of axis
		leftoffset = 65-point_size;
		rightoffset = 65+point_size;

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
			legendtopoffset = legendheight+2*largespace;
		}

		// Offsets of axis
		bottomoffset = 60+legendtopoffset+point_size;
		topoffset = 60-point_size;
	}

	// Resize scale
	public void resize() {
		gridsize = settings.gridsize;

		// x axis
		xoffsetincrement = (graphwidth-leftoffset-rightoffset)/gridsize;
		while ((xoffsetincrement < minimumxoffsetincrement) && (gridsize > 0)) {
			gridsize = gridsize/2;
			xoffsetincrement = (graphwidth-leftoffset-rightoffset)/gridsize;
		}

		// y and x axis
		yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
		while ((yoffsetincrement < minimumyoffsetincrement) && (gridsize > 0)) {
			gridsize = gridsize/2;
			yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
			xoffsetincrement = (graphwidth-leftoffset-rightoffset)/gridsize;
		}
	}

	public abstract void drawDataSet(Graphics2D g2, DataSet set);

	// Draw data point
	public void drawPoint(Graphics2D g2, Color color, double xvalue, double yvalue) {
		Color previouscolor = g2.getColor();

		double x = (xvalue-xminimum)/xscale+leftoffset;
		double y = graphheight-bottomoffset-(yvalue-yminimum)/yscale;

		g2.setColor(color);
		g2.fill(new Rectangle2D.Double(x-(point_size/2),
			y-(point_size/2), point_size, point_size));

		g2.setColor(previouscolor);
	}

	public void drawAxis(Graphics2D g2) {
		g2.draw(new Line2D.Double(leftoffset/*-point_size*/,topoffset/*-point_size*/,
			leftoffset/*-point_size*/, graphheight-bottomoffset/*+point_size*/));
		g2.draw(new Line2D.Double(leftoffset/*-point_size*/,
			graphheight-bottomoffset/*+point_size*/,
			graphwidth-rightoffset/*+point_size*/,
			graphheight-bottomoffset/*+point_size*/));
	}

	public void drawScale(Graphics2D g2) {
		NumberFormat numberformat = NumberFormat.getInstance();
		numberformat.setMaximumFractionDigits(3);

		// x axis
		double xvalue =  xminimum;
		double x = leftoffset/*-point_size*/;
		for (int index=0; index < gridsize; index++) {
			String string = numberformat.format(xvalue);
			int stringwidth = metrics.stringWidth(string);
			g2.drawString(string, (int) (x-stringwidth/2), (int) (graphheight-bottomoffset+fontheight/*+point_size*/));
			x += xoffsetincrement;
			xvalue += xvalueincrement;
		}

		// y axis
		double yvalue =  yminimum;
		double y = graphheight-bottomoffset/*+point_size*/;
		for (int index=0; index < gridsize; index++) {
			String string = numberformat.format(yvalue);
			int stringwidth = metrics.stringWidth(string);
			g2.drawString(string, (int) (leftoffset-stringwidth-2*smallspace/*-point_size*/),
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
		yvalue = graphheight-(bottomoffset-(bottomoffset-legendtopoffset-fontheight)/2)+2*largespace;
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
		double x = leftoffset/*-point_size*/;
		for (int index=0; index < gridsize; index++) {
			g2.draw(new Line2D.Double(x, graphheight-bottomoffset/*-point_size*/-tickmarksize, x, graphheight-bottomoffset/*-point_size*/+tickmarksize));
			x += xoffsetincrement;
		}

		// y axis
		double y = topoffset+yoffsetincrement/*+point_size*/;
		for (int index=0; index < gridsize; index++) {
			g2.draw(new Line2D.Double(leftoffset/*-point_size*/-tickmarksize, y, leftoffset/*-point_size*/+tickmarksize, y));
			y += yoffsetincrement;
		}
	}

	public void drawGrid(Graphics2D g2) {
		Color previouscolor = g2.getColor();
		g2.setColor(Color.gray);

		// x axis
		double x = leftoffset+xoffsetincrement;
		for (int index=0; index < gridsize-1; index++) {
			g2.draw(new Line2D.Double(x, graphheight-bottomoffset, x, topoffset));
			x += xoffsetincrement;
		}

		// y axis
		double y = topoffset+yoffsetincrement;
		for (int index=0; index < gridsize-1; index++) {
			g2.draw(new Line2D.Double(leftoffset, y, graphwidth-rightoffset, y));
			y += yoffsetincrement;
		}

		g2.setColor(previouscolor);
	}

	public void drawLegend(Graphics2D g2) {
		Color previouscolor = g2.getColor();

		double x = legendleftoffset;
		double y = graphheight-legendtopoffset;

		g2.draw(new Rectangle.Double(x, y, legendwidth, legendheight));

		x += smallspace;
		y += fontheight-samplecolorsize;

		DataSet set;
		for (int index=0; index < sets.length; index++) {
			set = sets[index];
			g2.setColor(set.color);
			g2.fill(new Rectangle.Double(x, y, samplecolorsize, samplecolorsize));
			y += fontheight;
		}

		g2.setColor(previouscolor);

		x = legendleftoffset;
		y = graphheight-legendtopoffset;

		x += 2*smallspace+samplecolorsize;
		y += fontheight;

		for (int index=0; index < sets.length; index++) {
			set = sets[index];
			g2.drawString(set.name, (int) x, (int) y);
			y += fontheight;
		}
	}
}
