package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;

/*
	DecisionTreeVis

	Scaled node used by navigator panel
*/
public final class ScaledNode {

	static JFrame frame;

	ViewableDTModel dmodel;

	ViewableDTNode dnode;

	ScaledNode parent;
	ArrayList children;

	// Distribution values
	double[] values;

	double x, y;
	double xspace = 20;
	double yspace = 80;

	double gwidth;
	double gheight = 45;
	double leftinset = 5;
	double rightinset = 5;
	double barwidth = 16;
	double barspace = 5;
	double ygrid = 5;
	double tickmark = 3;

	double yscale;
	double scalesize = 100;
	double xincrement, yincrement;

	private String blabel;

	public ScaledNode(ViewableDTModel model, ViewableDTNode node, ScaledNode snode, String blab) {
		dmodel = model;
		dnode = node;
		parent = snode;
		blabel = blab;
		children = new ArrayList(dnode.getNumChildren());

		findValues();

		gwidth = leftinset + tickmark + (barwidth + barspace)*values.length + rightinset;
		yincrement = gheight/(ygrid + 1);
		yscale = (gheight-2*yincrement)/scalesize;
		if(frame == null) {
			frame = new JFrame();
			frame.addNotify();
			frame.setFont(DecisionTreeScheme.textfont);
		}
	}

	public ScaledNode(ViewableDTModel model, ViewableDTNode node, ScaledNode snode) {
		this(model, node, snode, null);
	}

	public void addChild(ScaledNode snode) {
		children.add(snode);
	}

	public ScaledNode getChild(int index) {
		return (ScaledNode) children.get(index);
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

	public void findValues() {
		String[] output = dmodel.getUniqueOutputValues();
		values = new double[output.length];
		for(int index = 0; index < values.length; index++){
			try{
				values[index] = 100*(double)dnode.getOutputTally(output[index])/(double)dnode.getTotal();
			} catch(Exception exception){
				System.out.println("Exception from getOutputTally");
			}
		}
	}

	public double getWidth() {
		Graphics g = null;
		while(g == null)
			g = frame.getGraphics();
		FontMetrics fm = g.getFontMetrics();
		int strwid1;
		if(blabel != null)
			strwid1 = fm.stringWidth(blabel);
		else
			strwid1 = 0;

		double w1 = xspace+(gwidth/2);
		double w2 = strwid1+(gwidth/2);

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
			ScaledNode schild = getChild(index);
			subtreewidth += schild.findSubtreeWidth();
		}

		return subtreewidth;
	}

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
			ScaledNode midchild = getChild(midindex-1);
			return findIntervalWidth(0, midindex-2) + midchild.findLeftSubtreeWidth();
		}
	}

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
			ScaledNode midchild = getChild(midindex-1);
			return findIntervalWidth(midindex, numchildren-1) + midchild.findRightSubtreeWidth();
		}
	}

	public void findOffsets() {
		int numchildren = getNumChildren();

		if (numchildren%2 == 0) {
			int midindex = numchildren/2;

			for (int index = 0; index < numchildren; index++) {
				ScaledNode schild = getChild(index);

				if (index <= midindex-1)
					schild.x = x - findIntervalWidth(index+1, midindex-1) - schild.findRightSubtreeWidth();
				else
					schild.x = x + findIntervalWidth(midindex, index-1) + schild.findLeftSubtreeWidth();

				schild.y = y + gheight + yspace;
			}
		}
		else {
			int midindex = numchildren/2 + 1;
			ScaledNode midchild = getChild(midindex-1);

			for (int index = 0; index < numchildren; index++) {
				ScaledNode schild = getChild(index);

				if (index < midindex-1) {
					if (index == midindex-2)
						schild.x = x - midchild.findLeftSubtreeWidth() - schild.findRightSubtreeWidth();
					else
						schild.x = x - midchild.findLeftSubtreeWidth() - findIntervalWidth(index+1, midindex-2) - schild.findRightSubtreeWidth();
				}
				else if (index == midindex-1)
					schild.x = x;
				else {
					if (index == midindex)
						schild.x = x + midchild.findRightSubtreeWidth() + schild.findLeftSubtreeWidth();
					else
						schild.x = x + midchild.findRightSubtreeWidth() + findIntervalWidth(midindex, index-1) + schild.findLeftSubtreeWidth();
				}

				schild.y = y + gheight + yspace;
			}
		}
	}

	public double findIntervalWidth(int start, int end) {
		double intervalwidth = 0;

		for (; start <= end; start++) {
			ScaledNode schild = getChild(start);
			intervalwidth += schild.findSubtreeWidth();
		}

		return intervalwidth;
	}


	public void drawScaledNode(Graphics2D g2) {
		// Background
		g2.setColor(DecisionTreeScheme.scaledviewbackgroundcolor);
		g2.fill(new Rectangle2D.Double(x-gwidth/2, y, gwidth, gheight));

		// Bars
		g2.setColor(DecisionTreeScheme.scaledviewbarcolor);
		double x1 = x - gwidth/2 + leftinset + tickmark + barspace;
		for (int index = 0; index < values.length; index++) {
			double barheight = yscale*values[index];
			double y1 = y + 1 + gheight - yincrement - barheight;
			g2.fill(new Rectangle2D.Double(x1, y1, barwidth, barheight));
			x1 += barwidth + barspace;
		}
	}
}
