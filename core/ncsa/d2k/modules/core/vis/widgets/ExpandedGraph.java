package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;

/*
	DecisionTreeVis

	Graph displayed when mouse clicks a node in tree scroll pane
*/
public class ExpandedGraph extends JPanel {

	double left = 15;
	double right = 15;
	double top = 15;
	double bottom = 15;

	double xlabel, ylabel;
	double ylabelspace = 15;

	double xpath, ypath;
	double pathleft = 10;
	double pathright = 15;
	double pathtop = 6;
	double pathbottom = 10;
	double pathleading = 2;
	double pathwidth, pathheight;
	double ypathspace = 15;
	String[] path;

	double xdata, ydata;
	double dataleft = 10;
	double dataright = 10;
	double datatop = 10;
	double databottom = 10;
	double datawidth, dataheight;

	double samplesize = 10;
	double samplespace = 8;
	double outputwidth = 80;
	double outputspace = 10;
	double dpercentwidth;

	double xgraph, ygraph;
	double graphleft = 30;
	double graphright = 30;
	double graphtop = 30;
	double graphbottom = 30;
	double gridheight = 300;
	double gridwidth;
	double graphwidth, graphheight;
	double xgraphspace = 15;

	float gridstroke = .1f;
	double barwidth = 80;
	double barspace = 20;
	double largetick = 10;
	double smalltick = 4;
	double tickspace = 8;
	double percentwidth;
	double percentspace = 8;
	double axisspace = 4;

	DecisionTreeNode dnode;

	String[] outputs = {"one", "two", "three"};
	double[] values = {40, 10, 50};
	int datasize;

	DecisionTreeScheme scheme;

	FontMetrics largemetrics, smallmetrics;
	int largeascent, smallascent;

	NumberFormat numberformat;

	public ExpandedGraph(DecisionTreeModel model, DecisionTreeNode node) {
		dnode = node;

		outputs = model.getUniqueOutputValues();
		values = new double[outputs.length];
		for(int index = 0; index < outputs.length; index++)
			values[index] = 100*(double)dnode.getOutputTally(outputs[index])/(double)dnode.getTotal();
		datasize = values.length;

		scheme = new DecisionTreeScheme();

		largemetrics = getFontMetrics(scheme.expandedfont);
		largeascent = largemetrics.getAscent();

		numberformat = NumberFormat.getInstance();
		numberformat.setMaximumFractionDigits(1);

		smallmetrics = getFontMetrics(scheme.textfont);
		smallascent = smallmetrics.getAscent();
		dpercentwidth = smallmetrics.stringWidth("100.0%");
		percentwidth = smallmetrics.stringWidth("100");

		setBackground(scheme.expandedbackgroundcolor);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawLabel(g2);
		drawLabelPath(g2);
		drawData(g2);
		drawGraph(g2);
	}

	public void drawLabel(Graphics2D g2) {
		String label = dnode.getLabel();

		g2.setFont(scheme.expandedfont);
		g2.setColor(scheme.expandedfontcolor);
		g2.drawString(label, (int) xlabel, (int) ylabel);
	}

	public void drawLabelPath(Graphics2D g2) {
		g2.setFont(scheme.textfont);

		// Background
		g2.setColor(scheme.expandedborderbackgroundcolor);
		g2.fill(new Rectangle2D.Double(xpath, ypath, pathwidth, pathheight));


		// Path
		double y = ypath + pathtop + smallascent;
		double x = pathleft + xpath;
		g2.setColor(scheme.textcolor);
		for (int index = 0; index < path.length; index++) {
			g2.drawString(path[index], (int) x, (int) y);
			y += smallascent + pathleading;
		}
	}

	public void drawData(Graphics2D g2) {

		// Background
		g2.setColor(scheme.expandedborderbackgroundcolor);
		g2.fill(new Rectangle2D.Double(xdata, ydata, datawidth, dataheight));

		// Data
		double x = xdata + dataleft;
		double y = ydata + datatop;

		BarColors barcolors = scheme.getBarColors();

		for (int index = 0; index < datasize; index++) {
			Color color = barcolors.getNextColor();
			g2.setColor(color);
			g2.fill(new Rectangle2D.Double(x, y, samplesize, samplesize));

			x += samplesize + samplespace;
			y += samplesize;
			g2.setColor(scheme.textcolor);
			g2.drawString(outputs[index], (int) x, (int) y);

			x += outputwidth + outputspace;
			String value = numberformat.format(values[index]) + "%";
			g2.drawString(value, (int) x, (int) y);

			x = xdata + dataleft;
			y += samplespace;
		}
	}

	public void drawGraph(Graphics2D g2) {

		// Background
		g2.setColor(scheme.expandedborderbackgroundcolor);
		g2.fill(new Rectangle2D.Double(xgraph, ygraph, graphwidth, graphheight));

		// Grid
		g2.setColor(scheme.expandedgraphgridcolor);

		double yincrement = gridheight/10;
		double x = xgraph + graphleft;
		double y = ygraph + graphheight - graphbottom;
		int val = 0;
		for (int index=0; index <= 10; index++) {
			Integer integer = new Integer(val);
			String svalue = integer.toString();
			g2.drawString(svalue, (int) x, (int) y);

			g2.setStroke(new BasicStroke(gridstroke));
			x += percentwidth + percentspace;
			g2.draw(new Line2D.Double(x, y, x+largetick, y));
			x += largetick + tickspace;
			g2.draw(new Line2D.Double(x, y, x+gridwidth, y));

			x = xgraph + graphleft;
			y -= yincrement;
			val += 10;
		}


		// Small grid
		x = xgraph + graphleft + percentwidth + percentspace + largetick - smalltick;
		yincrement = gridheight/20;
		y = ygraph + graphheight - graphbottom - yincrement;
		for (int index=0; index < 10; index++) {
			g2.draw(new Line2D.Double(x, y, x+smalltick, y));
			x += smalltick+tickspace;
			g2.draw(new Line2D.Double(x, y, x+gridwidth, y));

			x = xgraph + graphleft + percentwidth + percentspace + largetick - smalltick;
			y -= 2*yincrement;
		}

		// Bars
		BarColors barcolors = scheme.getBarColors();

		x = xgraph + graphleft + percentwidth + percentspace + largetick + tickspace + barspace;
		double yscale = gridheight/100;
		for (int index=0; index < values.length; index++) {
			double barheight = yscale*values[index];
			y = ygraph + graphheight - graphbottom - barheight;
			g2.setColor(barcolors.getNextColor());
			g2.fill(new Rectangle2D.Double(x, y, barwidth, barheight));
			x += barspace + barwidth;
		}

		x = xgraph + graphleft + percentwidth + percentspace + largetick + tickspace + barspace + barwidth/2;
		y = ygraph + graphheight - graphbottom + smallascent + axisspace;
		g2.setColor(scheme.textcolor);
		for (int index=0; index < outputs.length; index++) {
			String output = outputs[index];
			int outputwidth = smallmetrics.stringWidth(output);
			g2.drawString(output, (int) (x-outputwidth/2), (int) y);
			x += barspace + barwidth;
		}
	}

	public Dimension getMinimumSize() {
		// Label bounds
		xlabel = left;
		ylabel = top + largeascent;

		// Path bounds
		xpath = xlabel;
		ypath = ylabel + ylabelspace;

		path = new String[0];
		pathwidth = 0;
		for (int index=0; index < path.length; index++) {
			int twidth = smallmetrics.stringWidth(path[index]);
			if (twidth > pathwidth)
				pathwidth = twidth;
		}
		pathwidth += pathleft + pathright;
		pathheight = pathtop + path.length*smallascent + (path.length-1)*pathleading + pathbottom;

		// Data bounds
		xdata = xpath;
		ydata = ypath + pathheight + ypathspace;

		datawidth = dataleft + samplesize + samplespace + outputwidth + outputspace + dpercentwidth + dataright;
		dataheight = datatop + datasize*samplesize + (datasize-1)*samplespace + databottom;

		if (pathwidth > datawidth)
			datawidth = pathwidth;
		else
			pathwidth = datawidth;

		// Graph bounds
		ygraph = top;
		xgraph = xpath + pathwidth + xgraphspace;

		graphheight = graphtop + gridheight + graphbottom;

		gridwidth = barwidth*datasize + barspace*(datasize+1);
		graphwidth = graphleft + percentwidth + percentspace + largetick + tickspace + gridwidth + graphright;

		double width = left + pathwidth + xgraphspace + graphwidth + right;
		double height = top + graphheight + bottom;

		return new Dimension((int) width, (int) height);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
}
