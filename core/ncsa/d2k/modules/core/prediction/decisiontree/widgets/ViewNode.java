package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;

/*
	DecisionTreeVis

	Represents a decision tree node with a bar graph
*/
public final class ViewNode {

	static JFrame frame;

	// Decision tree model
	ViewableDTModel dmodel;

	ViewableDTNode dnode;

	ViewNode parent;
	ArrayList children;

	// Distribution values
	double[] values;

	boolean roll = false;

	boolean collapsed = false;

	// x is midpoint of node
	// y is top left of bar graph
	double x, y;
	double xspace = 20;
	double yspace = 80;

	double gwidth;
	double gheight = 60;
	double leftinset = 5;
	double rightinset = 5;
	double barwidth = 16;
	double barspace = 5;
	double ygrid = 5;
	double tickmark = 3;

	double tside = 8;
	double tspace = 8;
	double theight;

	double yscale;
	double scalesize = 100;
	double xincrement, yincrement;

	DecisionTreeScheme scheme;

	// the branch label above this node
	private String blabel;

	public ViewNode(ViewableDTModel model, ViewableDTNode node, ViewNode vnode, String blab) {
		dmodel = model;
		dnode = node;
		parent = vnode;
		blabel = blab;
		children = new ArrayList(dnode.getNumChildren());

		findValues();

		scheme = new DecisionTreeScheme();
		gwidth = leftinset + tickmark + (barwidth + barspace)*values.length + rightinset;
		yincrement = gheight/(ygrid+1);
		yscale = (gheight - 2*yincrement)/scalesize;

		if(frame == null) {
			frame = new JFrame();
			frame.addNotify();
			frame.setFont(DecisionTreeScheme.textfont);
		}
	}

	public ViewNode(ViewableDTModel model, ViewableDTNode node, ViewNode vnode) {
		this(model, node, vnode, null);
	}

	public void addChild(ViewNode vnode) {
		children.add(vnode);
	}

	public ViewNode getChild(int index) {
		return (ViewNode) children.get(index);
	}

	public int getNumChildren() {
		return children.size();
	}

	public boolean isLeaf() {
		if (children.size() == 0)
			return true;

		return false;
	}

	public int getDepth() {
		if (parent == null)
			return 0;

		return parent.getDepth() + 1;
	}

	public String getBranchLabel(int index) {
		return dnode.getBranchLabel(index);
	}

	public void findValues() {
		String[] output = dmodel.getUniqueOutputValues();
		values = new double[output.length];
		for(int index = 0; index < values.length; index++){
			try{
			values[index] = 100*(double)dnode.getOutputTally(output[index])/(double)dnode.getTotal();
			}catch(Exception e){
				System.out.println("Exception from getOutputTally");
			}
		}
	}

	public double getWidth() {
		//if(blabel == null)
		//	return xspace + gwidth+ xspace;

		Graphics g = null;
		while(g == null)
			g = frame.getGraphics();
		FontMetrics fm = g.getFontMetrics();
		int strwid1;
		if(blabel != null)
			strwid1 = fm.stringWidth(blabel);
		else
			strwid1 = 0;
		//int strwid1 = fm.stringWidth(blabel);

		double w1 = xspace+(gwidth/2);
		double w2 = strwid1+(gwidth/2);

		/*if(w1 > w2)
			return w1+(gwidth/2)+xspace;
		else
			return w2+(gwidth/2)+xspace;
			*/

		double wid1 = strwid1*2;
		double wid2 = xspace + gwidth+ xspace;

		if(wid1 > wid2)
			return wid1;
		else
			return wid2;
	}

	public double findSubtreeWidth() {
		if (isLeaf())
			return getWidth();

		double subtreewidth = 0;

		for (int index = 0; index < getNumChildren(); index++) {
			ViewNode vchild = getChild(index);
			subtreewidth += vchild.findSubtreeWidth();
		}

		return subtreewidth;
	}

	// Width from midpoint to leftmost child node
	public double findLeftSubtreeWidth() {
		if (isLeaf())
			return getWidth()/2;

		int numchildren = getNumChildren();

		if (numchildren%2 == 0) {
			int midindex = numchildren/2;
			return findIntervalWidth(0, midindex-1);
		}
		else {
			int midindex = numchildren/2 + 1;
			ViewNode midchild = getChild(midindex-1);
			return findIntervalWidth(0, midindex-2) + midchild.findLeftSubtreeWidth();
		}
	}

	// Width from midpoint to rightmost child node
	public double findRightSubtreeWidth() {
		if (isLeaf())
			return getWidth()/2;

		int numchildren = getNumChildren();

		if (numchildren%2 == 0) {
			int midindex = numchildren/2;
			return findIntervalWidth(midindex, numchildren-1);
		}
		else {
			int midindex = numchildren/2 + 1;
			ViewNode midchild = getChild(midindex-1);
			return findIntervalWidth(midindex, numchildren-1) + midchild.findRightSubtreeWidth();
		}
	}

	// Determines offsets of children
	public void findOffsets() {
		int numchildren = getNumChildren();

		if (numchildren%2 == 0) {
			int midindex = numchildren/2;

			for (int index = 0; index < numchildren; index++) {
				ViewNode vchild = getChild(index);

				if (index <= midindex-1)
					vchild.x = x - findIntervalWidth(index+1, midindex-1) - vchild.findRightSubtreeWidth();
				else
					vchild.x = x + findIntervalWidth(midindex, index-1) + vchild.findLeftSubtreeWidth();

				vchild.y = y + gheight + yspace;
			}
		}
		else {
			int midindex = numchildren/2 + 1;
			ViewNode midchild = getChild(midindex-1);

			for (int index = 0; index < numchildren; index++) {
				ViewNode vchild = getChild(index);

				if (index < midindex-1) {
					if (index == midindex-2)
						vchild.x = x - midchild.findLeftSubtreeWidth() - vchild.findRightSubtreeWidth();
					else
						vchild.x = x - midchild.findLeftSubtreeWidth() - findIntervalWidth(index+1, midindex-2) - vchild.findRightSubtreeWidth();
				}
				else if (index == midindex-1)
					vchild.x = x;
				else {
					if (index == midindex)
						vchild.x = x + midchild.findRightSubtreeWidth() + vchild.findLeftSubtreeWidth();
					else
						vchild.x = x + midchild.findRightSubtreeWidth() + findIntervalWidth(midindex, index-1) + vchild.findLeftSubtreeWidth();
				}

				vchild.y = y + gheight + yspace;
			}
		}
	}

	public double findIntervalWidth(int start, int end) {
		double intervalwidth = 0;

		for (; start <= end; start++) {
			ViewNode vchild = getChild(start);
			intervalwidth += vchild.findSubtreeWidth();
		}

		return intervalwidth;
	}

	public void toggle() {
		if (collapsed)
			collapsed = false;
		else
			collapsed = true;
	}

	public boolean isVisible() {
		ViewNode vnode = this;
		while (vnode.parent != null) {
			if (vnode.parent.collapsed)
				return false;
			vnode = vnode.parent;
		}

		return true;
	}

	public int test(int x1, int y1) {
		if (x1 >= x - gwidth/2 && x1 <= x + gwidth/2)
			return 1;

		if (x1 >= x + gwidth/2 && x1 <= x + gwidth/2 + tspace + tside + tspace) {
			if (y1 >= y + gheight - tside - tspace && y1 <= y + gheight)
				return 2;
		}

		return -1;
	}

	public void drawViewNode(Graphics2D g2) {
		double x1, y1;

		// Background
		g2.setColor(scheme.viewbackgroundcolor);
		g2.fill(new Rectangle2D.Double(x-gwidth/2, y, gwidth, gheight));

		// Tickmarks
		g2.setColor(scheme.viewtickcolor);
		g2.setStroke(new BasicStroke(1));
		x1 = x - gwidth/2 + leftinset;
		y1 = y + yincrement;
		for (int index = 0; index < ygrid; index++) {
			g2.draw(new Line2D.Double(x1, y1, x1+tickmark, y1));
			y1 += yincrement;
		}

		// Bars
		BarColors barcolors = scheme.getBarColors();
		x1 = x - gwidth/2 + leftinset + tickmark + barspace;
		for (int index = 0; index < values.length; index++) {
			double barheight = yscale*values[index];
			y1 = y + 1 + gheight - yincrement - barheight;
			g2.setColor(barcolors.getNextColor());
			g2.fill(new Rectangle2D.Double(x1, y1, barwidth, barheight));
			x1 += barwidth + barspace;
		}

		// Triangle
		if (isLeaf())
			return;

		theight = .866025*tside;
		double ycomponent = tside/2;
		double xcomponent = .577350*ycomponent;
		double xcenter, ycenter;

		if (collapsed) {
			xcenter = x + gwidth/2 + tspace + xcomponent;
			ycenter = y + gheight - ycomponent;

			int xpoints[] = {(int) (xcenter-xcomponent), (int) (xcenter+theight-xcomponent), (int) (xcenter-xcomponent)};
			int ypoints[] = {(int) (ycenter-ycomponent), (int) ycenter, (int) (ycenter+ycomponent)};

			GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xpoints.length);
			triangle.moveTo((int) (xcenter-xcomponent), (int) (ycenter-ycomponent));
			for (int index = 1; index < xpoints.length; index++) {
				triangle.lineTo(xpoints[index], ypoints[index]);
			}
			triangle.closePath();

			g2.setColor(scheme.viewtrianglecolor);
			g2.fill(triangle);
		}
		else {
			xcenter = x + gwidth/2 + tspace + xcomponent;
			ycenter = y + gheight - ycomponent;

			int xpoints[] = {(int) (xcenter-ycomponent), (int) (xcenter+ycomponent), (int) (xcenter)};
			int ypoints[] = {(int) (ycenter-xcomponent), (int) (ycenter-xcomponent), (int) (ycenter+ycomponent)};

			GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xpoints.length);
			triangle.moveTo((int) (xcenter-ycomponent), (int) (ycenter-xcomponent));
			for (int index = 1; index < xpoints.length; index++) {
				triangle.lineTo(xpoints[index], ypoints[index]);
			}
			triangle.closePath();

			g2.setColor(DecisionTreeScheme.viewtrianglecolor);
			g2.fill(triangle);
		}
	}
}
