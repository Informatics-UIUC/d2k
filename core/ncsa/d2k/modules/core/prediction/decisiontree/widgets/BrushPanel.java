package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
//import ncsa.d2k.modules.compute.learning.modelgen.decisiontree.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/*
	DecisionTreeVis

	Draws data when mouse moves over a node in tree scroll pane
*/
public class BrushPanel extends JPanel {

	ViewableDTModel dmodel;
	ViewableDTNode droot;
	ViewableDTNode dnode;

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

	public BrushPanel(ViewableDTModel model) {
		dmodel = model;
		droot = dmodel.getViewableRoot();

		outputs = model.getUniqueOutputValues();

		scheme = new DecisionTreeScheme();

		metrics = getFontMetrics(scheme.textfont);
		ascent = metrics.getAscent();
		descent = metrics.getDescent();
		percentwidth = metrics.stringWidth("100.0%");

		numberformat = NumberFormat.getInstance();
		numberformat.setMaximumFractionDigits(1);

		setOpaque(true);
		setBackground(scheme.borderbackgroundcolor);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(scheme.textfont);

		if (dnode != null) {
			values = new double[outputs.length];
			for(int index = 0; index < values.length; index++)
				try{
				values[index] = 100*(double)dnode.getOutputTally(outputs[index])/(double)dnode.getTotal();
				}catch(Exception e){
					System.out.println("getOutputTally threw an exception");
				}
		}

		BarColors barcolors = scheme.getBarColors();

		Insets insets = getInsets();
		double x = insets.left;
		double y = insets.top;

		for (int index=0; index < outputs.length; index++) {
			g2.setColor(barcolors.getNextColor());
			g2.fill(new Rectangle2D.Double(x, y, samplesize, samplesize));

			x += samplesize + samplespace;
			y += samplesize;
			String output = outputs[index];
			g2.setColor(scheme.textcolor);
			g2.drawString(output, (int) x, (int) y);

			if (dnode != null) {
				x += outputwidth + outputspace;
				String value = numberformat.format(values[index]) + "%";
				g2.drawString(value, (int) x, (int) y);
			}

			x = insets.left;
			y += samplespace;
		}
	}

	// Called by tree scroll pane
	public void updateBrush(ViewableDTNode node) {
		dnode = node;
		repaint();
	}

	public Dimension getMinimumSize() {
		Insets insets = getInsets();
		int pwidth = (int) (insets.left + samplesize + samplespace + outputwidth + outputspace + percentwidth + insets.right);
		int pheight = (int) (insets.top + samplesize*(outputs.length) + samplespace*(outputs.length-1) + descent + insets.bottom);
		return new Dimension(pwidth, pheight);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
}
