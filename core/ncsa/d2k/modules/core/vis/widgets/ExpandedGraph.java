package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.modules.core.prediction.decisiontree.*;

public class ExpandedGraph extends JPanel {

	double left = 15;
	double right = 15;
	double top = 15;
	double bottom = 15;

	double xrule, yrule;
	double yrulespace = 15;

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

	DecisionTreeNode modelnode;

	String[] outputs;
	double[] values;
	int datasize;

	DecisionTreeScheme scheme;

	FontMetrics largemetrics, smallmetrics;
	int largeascent, smallascent;

	NumberFormat numberformat;

	public ExpandedGraph(DecisionTreeModel mdl, DecisionTreeNode modelnode) {
		this.modelnode = modelnode;

		//outputs = modelnode.outputmapArray();
		outputs = mdl.getUniqueOutputValues();
		//values = modelnode.valuemapArray();
		values = new double[outputs.length];
		for(int i = 0; i < outputs.length; i++)
			values[i] = 100*(double)modelnode.getOutputTally(outputs[i])/(double)modelnode.getTotal();
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

		drawRule(g2);
		drawRulePath(g2);
		drawData(g2);
		drawGraph(g2);
	}

	public void drawRule(Graphics2D g2) {
		// ** Correct??
		String rule = modelnode.getLabel();//modelnode.getRule();

		g2.setFont(scheme.expandedfont);
		g2.setColor(scheme.expandedfontcolor);
		g2.drawString(rule, (int) xrule, (int) yrule);
	}

	public void drawRulePath(Graphics2D g2) {

		g2.setFont(scheme.textfont);

		// Draw background
		g2.setColor(scheme.expandedborderbackgroundcolor);
		g2.fill(new Rectangle2D.Double(xpath, ypath, pathwidth, pathheight));


		// Draw path
		double yoff = ypath+pathtop+smallascent;
		double xoff = pathleft+xpath;
		g2.setColor(scheme.textcolor);
		for (int index=0; index < path.length; index++) {
			g2.drawString(path[index], (int) xoff, (int) yoff);
			yoff += smallascent+pathleading;
		}
	}

	public void drawData(Graphics2D g2) {

		// Draw background
		g2.setColor(scheme.expandedborderbackgroundcolor);
		g2.fill(new Rectangle2D.Double(xdata, ydata, datawidth, dataheight));

		// Draw data
		double xoff = xdata+dataleft;
		double yoff = ydata+datatop;

		BarColors barcolors = scheme.getBarColors();

		for (int index=0; index < datasize; index++) {
			Color color = barcolors.getNextColor();
			g2.setColor(color);
			g2.fill(new Rectangle2D.Double(xoff, yoff, samplesize, samplesize));

			xoff += samplesize+samplespace;
			yoff += samplesize;
			g2.setColor(scheme.textcolor);
			g2.drawString(outputs[index], (int) xoff, (int) yoff);

			xoff += outputwidth+outputspace;
			String value = numberformat.format(values[index]) + "%";
			g2.drawString(value, (int) xoff, (int) yoff);

			xoff = xdata+dataleft;
			yoff += samplespace;
		}
	}

	public void drawGraph(Graphics2D g2) {

		// Draw background
		g2.setColor(scheme.expandedborderbackgroundcolor);
		g2.fill(new Rectangle2D.Double(xgraph, ygraph, graphwidth, graphheight));

		// Draw grid
		g2.setColor(scheme.expandedgraphgridcolor);

		double yincrement = gridheight/10;
		double xoff = xgraph+graphleft;
		double yoff = ygraph+graphheight-graphbottom;
		int val = 0;
		for (int index=0; index <= 10; index++) {
			Integer integer = new Integer(val);
			String stringval = integer.toString();
			int valwidth = smallmetrics.stringWidth(stringval);
			g2.drawString(stringval, (int) xoff, (int) yoff);

			g2.setStroke(new BasicStroke(gridstroke));
			xoff += percentwidth+percentspace;
			g2.draw(new Line2D.Double(xoff, yoff, xoff+largetick, yoff));
			xoff += largetick+tickspace;
			g2.draw(new Line2D.Double(xoff, yoff, xoff+gridwidth, yoff));

			xoff = xgraph+graphleft;
			yoff -= yincrement;
			val += 10;
		}


		// Draw small grid
		xoff = xgraph+graphleft+percentwidth+percentspace+largetick-smalltick;
		yincrement = gridheight/20;
		yoff = ygraph+graphheight-graphbottom-yincrement;
		for (int index=0; index < 10; index++) {
			g2.draw(new Line2D.Double(xoff, yoff, xoff+smalltick, yoff));
			xoff += smalltick+tickspace;
			g2.draw(new Line2D.Double(xoff, yoff, xoff+gridwidth, yoff));

			xoff = xgraph+graphleft+percentwidth+percentspace+largetick-smalltick;
			yoff -= 2*yincrement;
		}

		// Draw bars
		BarColors barcolors = scheme.getBarColors();

		xoff = xgraph+graphleft+percentwidth+percentspace+largetick+tickspace+barspace;
		double yscale = gridheight/100;
		for (int index=0; index < values.length; index++) {
			double barheight = yscale*values[index];
			yoff = ygraph+graphheight-graphbottom-barheight;
			g2.setColor(barcolors.getNextColor());
			g2.fill(new Rectangle2D.Double(xoff, yoff, barwidth, barheight));
			xoff += barspace+barwidth;
		}

		xoff = xgraph+graphleft+percentwidth+percentspace+largetick+tickspace+barspace+barwidth/2;
		yoff = ygraph+graphheight-graphbottom+smallascent+axisspace;
		g2.setColor(scheme.textcolor);
		for (int index=0; index < outputs.length; index++) {
			String output = outputs[index];
			int outputwidth = smallmetrics.stringWidth(output);
			g2.drawString(output, (int) (xoff-outputwidth/2), (int) yoff);
			xoff += barspace+barwidth;
		}
	}

	public Dimension getMinimumSize() {
		// Rule bounds
		xrule = left;
		yrule = top+largeascent;

		// Path bounds
		xpath = xrule;
		ypath = yrule+yrulespace;

		path = new String[0];//modelnode.getRulePath();
		pathwidth = 0;
		for (int index=0; index < path.length; index++) {
			int tempwidth = smallmetrics.stringWidth(path[index]);
			if (tempwidth > pathwidth)
				pathwidth = tempwidth;
		}
		pathwidth += pathleft+pathright;
		pathheight = pathtop+path.length*smallascent+(path.length-1)*pathleading+pathbottom;

		// Data bounds
		xdata = xpath;
		ydata = ypath+pathheight+ypathspace;

		datawidth = dataleft+samplesize+samplespace+outputwidth+outputspace+dpercentwidth+dataright;
		dataheight = datatop+datasize*samplesize+(datasize-1)*samplespace+databottom;

		if (pathwidth > datawidth)
			datawidth = pathwidth;
		else
			pathwidth = datawidth;

		// Graph bounds
		ygraph = top;
		xgraph = xpath+pathwidth+xgraphspace;

		graphheight = graphtop+gridheight+graphbottom;

		gridwidth = barwidth*datasize+barspace*(datasize+1);
		graphwidth = graphleft+percentwidth+percentspace+largetick+tickspace+gridwidth+graphright;

		double width = left+pathwidth+xgraphspace+graphwidth+right;
		double height = top+graphheight+bottom;

		return new Dimension((int) width, (int) height);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

}
