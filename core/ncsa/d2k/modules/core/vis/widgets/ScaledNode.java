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
public class ScaledNode {

	// Decision node
	DecisionTreeNode modelnode;

	ScaledNode parent, left, right;

	// View node displays descendants
	boolean collapsed = false;

	// Represents leaf node
	boolean leaf = false;

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

	double xincrement, yincrement;

	BarColors viewcolor;

	public ScaledNode(DecisionTreeModel mdl, DecisionTreeNode modelnode,
		ScaledNode parent) {

		this.modelnode = modelnode;
		this.parent = parent;

		String[] outs = mdl.getUniqueOutputValues();
		//values = modelnode.valuemapArray();
		values = new double[outs.length];
		for(int i = 0; i < values.length; i++)
			values[i] = 100*(double)modelnode.getOutputTally(outs[i])/(double)modelnode.getTotal();
		left = null;
		right = null;

		width = leftinset+tickmark+(barwidth+barspace)*values.length+rightinset;
		yincrement = height/(ygrid+1);
		yscale= (height-2*yincrement)/scalesize;
	}

	public void drawScaledNode(Graphics2D g2) {

		// Draw background
		g2.setColor(DecisionTreeScheme.scaledviewbackgroundcolor);
		g2.fill(new Rectangle2D.Double(xoffset, yoffset, width, height));

		// Draw bars
		g2.setColor(DecisionTreeScheme.scaledviewbarcolor);
		double x = xoffset+leftinset+tickmark+barspace;
		for (int index=0; index < values.length; index++) {
			double barheight = yscale*values[index];
			double y = yoffset+1+height-yincrement-barheight;
			g2.fill(new Rectangle2D.Double(x, y, barwidth, barheight));
			x += barwidth+barspace;
		}
	}
}
