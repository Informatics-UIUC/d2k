package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.modules.core.prediction.decisiontree.*;

/**
	Renders decision tree nodes
*/
public class ViewNode {

	DecisionTreeModel model;

	// Decision node
	DecisionTreeNode modelnode;

	ViewNode parent, left, right;

	// View node displays descendants
	boolean collapsed = false;

	// Match for search
	boolean searchmatch = false;//true;

	// Represents leaf node
	boolean leaf = false;

	boolean roll = false;

	// Distribution values
	double[] values;

	double xoffset, yoffset;

	double width;
	double height = 60;
	double leftinset = 5;
	double rightinset = 5;
	double barwidth = 16;
	double barspace = 5;
	double ygrid = 5;
	double yscale;
	double scalesize = 100;
	double tickmark = 3;
	double triside = 8;
	double trispace = 8;
	double triheight;
	double searchspace = 3;

	double xincrement, yincrement;

	DecisionTreeScheme scheme;

	//public ViewNode(DecisionTreeNode modelnode, ViewNode parent) {
	public ViewNode(DecisionTreeModel mdl, DecisionTreeNode modelnode,
		ViewNode parent) {

		model = mdl;
		this.modelnode = modelnode;
		this.parent = parent;

		//values = modelnode.valuemapArray();
		String[] outs = model.getUniqueOutputValues();
		values = new double[outs.length];
		for(int i = 0; i < values.length; i++)
			values[i] = 100*(double)modelnode.getOutputTally(outs[i])/(double)modelnode.getTotal();

		left = null;
		right = null;

		leaf = modelnode.isLeaf();

		width = leftinset+tickmark+(barwidth+barspace)*values.length+rightinset;
		yincrement = height/(ygrid+1);
		yscale= (height-2*yincrement)/scalesize;

		scheme = new DecisionTreeScheme();
	}

	public void drawViewNode(Graphics2D g2) {

		if (searchmatch) {
			g2.setColor(scheme.treebackgroundcolor);
			g2.fill(new Rectangle2D.Double(xoffset-searchspace, yoffset-searchspace, width+2*searchspace, height+2*searchspace));

			g2.setColor(scheme.viewsearchcolor);;
			g2.setStroke(new BasicStroke(1));
			g2.draw(new Rectangle2D.Double(xoffset-searchspace, yoffset-searchspace, width+2*searchspace, height+2*searchspace));
		}

		// Draw background
		if (roll) {
			g2.setColor(scheme.viewrollcolor);
			g2.fill(new Rectangle2D.Double(xoffset, yoffset, width, height));
		}
		else {
			g2.setColor(scheme.viewbackgroundcolor);
			g2.fill(new Rectangle2D.Double(xoffset, yoffset, width, height));
		}

		// Draw tickmarks
		g2.setColor(scheme.viewtickcolor);
		g2.setStroke(new BasicStroke(1));
		double x = xoffset+leftinset;
		double y = yoffset+yincrement;
		for (int index=0; index < ygrid; index++) {
			g2.draw(new Line2D.Double(x, y, x+tickmark, y));
			y += yincrement;
		}

		BarColors barcolors = scheme.getBarColors();
		// Draw bars
		x = xoffset+leftinset+tickmark+barspace;
		for (int index=0; index < values.length; index++) {
			double barheight = yscale*values[index];
			y = yoffset+1+height-yincrement-barheight;
			g2.setColor(barcolors.getNextColor());
			g2.fill(new Rectangle2D.Double(x, y, barwidth, barheight));
			x += barwidth+barspace;
		}

		// Draw triangle
		if (leaf)
			return;

		triheight = .866025*triside;
		double ycomponent = triside/2;
		double xcomponent = .577350*ycomponent;
		double xcenter, ycenter;

		if (collapsed) {
			xcenter = xoffset+width+trispace+xcomponent;
			ycenter = yoffset+height-ycomponent;
			int xpoints[] = {(int) (xcenter-xcomponent), (int) (xcenter+triheight-xcomponent), (int) (xcenter-xcomponent)};
			int ypoints[] = {(int) (ycenter-ycomponent), (int) ycenter, (int) (ycenter+ycomponent)};
			GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xpoints.length);
			triangle.moveTo((int) (xcenter-xcomponent), (int) (ycenter-ycomponent));
			for (int index=1; index < xpoints.length; index++) {
				triangle.lineTo(xpoints[index], ypoints[index]);
			}
			triangle.closePath();
			g2.setColor(scheme.viewtrianglecolor);
			g2.fill(triangle);
		}
		else {
			xcenter = xoffset+width+trispace+xcomponent;
			ycenter = yoffset+height-ycomponent;
			int xpoints[] = {(int) (xcenter-ycomponent), (int) (xcenter+ycomponent), (int) (xcenter)};
			int ypoints[] = {(int) (ycenter-xcomponent), (int) (ycenter-xcomponent), (int) (ycenter+ycomponent)};
			GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xpoints.length);
			triangle.moveTo((int) (xcenter-ycomponent), (int) (ycenter-xcomponent));
			for (int index=1; index < xpoints.length; index++) {
				triangle.lineTo(xpoints[index], ypoints[index]);
			}
			triangle.closePath();
			g2.setColor(DecisionTreeScheme.viewtrianglecolor);
			g2.fill(triangle);
		}
	}

	public void toggle() {
		if (collapsed)
			collapsed = false;
		else
			collapsed = true;
	}

	/**
		Returns one if x offset falls on background of view node
		Returns two if x and y offsets falls around triangle
	*/
	public int evaluate(int xoff, int yoff) {
		if (xoff >= xoffset && xoff <= xoffset+width)
			return 1;
		if (xoff >= xoffset+width && xoff <= xoffset+width+trispace+triside+trispace) {
			if (yoff >= yoffset+height-triside-trispace && yoff <= yoffset+height)
				return 2;
		}

		return -1;
	}

	/**
		Determine if view node is visible

		Returns true if the parent of all view nodes up to the root are not collapsed
	*/
	public boolean visible() {
		ViewNode iterator = this;
		while (iterator.parent != null) {
			if (iterator.parent.collapsed)
				return false;
			iterator = iterator.parent;
		}

		return true;
	}
}
