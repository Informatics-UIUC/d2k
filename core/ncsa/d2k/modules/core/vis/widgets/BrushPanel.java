package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.modules.core.prediction.decisiontree.*;

public class BrushPanel extends JPanel {

	double samplesize = 10;
	double samplespace = 8;
	double outputwidth = 80;
	double outputspace = 10;
	double percentwidth;

	// Outputs
	String[] outputs;

	// Distribution values
	double[] values;

	DecisionTreeScheme scheme;

	FontMetrics metrics;
	int ascent, descent;

	NumberFormat numberformat;

	DecisionTreeModel model;
	DecisionTreeNode tree;
	DecisionTreeNode node;

	//public BrushPanel(DecisionTreeNode tree) {
	public BrushPanel(DecisionTreeModel mdl) {
		this.model = mdl;
		this.tree = model.getRoot();

		outputs = model.getUniqueOutputValues();//tree.outputmapArray();

		scheme = new DecisionTreeScheme();

		metrics = getFontMetrics(scheme.textfont);
		ascent = metrics.getAscent();
		descent = metrics.getDescent();
		percentwidth = metrics.stringWidth("100.0%");

		numberformat = NumberFormat.getInstance();
		numberformat.setMaximumFractionDigits(1);

		node = null;

		setOpaque(true);
		setBackground(scheme.borderbackgroundcolor);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(scheme.textfont);

		if (node != null) {
			values = new double[outputs.length];
			//values = node.valuemapArray();
			for(int i = 0; i < values.length; i++)
				values[i] = 100*(double)node.getOutputTally(outputs[i])/(double)node.getTotal();
		}

		// Draw brush data
		BarColors barcolors = scheme.getBarColors();

		Insets insets = getInsets();

		double xoff = insets.left;
		double yoff = insets.top;
		for (int index=0; index < outputs.length; index++) {
			g2.setColor(barcolors.getNextColor());
			g2.fill(new Rectangle2D.Double(xoff, yoff, samplesize, samplesize));

			xoff += samplesize+samplespace;
			yoff += samplesize;
			String output = outputs[index];
			g2.setColor(scheme.textcolor);
			g2.drawString(output, (int) xoff, (int) yoff);

			if (node != null) {
				xoff += outputwidth+outputspace;
				String value = numberformat.format(values[index]) + "%";
				g2.drawString(value, (int) xoff, (int) yoff);
			}

			xoff = insets.left;
			yoff += samplespace;
		}
	}

	public void updateBrush(DecisionTreeNode node) {
		this.node = node;
		repaint();
	}

	public Dimension getMinimumSize() {
		Insets insets = getInsets();
		int prefwidth = (int) (insets.left+samplesize+samplespace+outputwidth+outputspace+percentwidth+insets.right);
		int prefheight = (int) (insets.top+samplesize*(outputs.length)+samplespace*(outputs.length-1)+descent+insets.bottom);
		return new Dimension(prefwidth, prefheight);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
}
