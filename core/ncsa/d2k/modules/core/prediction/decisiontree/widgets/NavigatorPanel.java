package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import ncsa.d2k.util.*;
import ncsa.d2k.util.datatype.*;
//import ncsa.d2k.modules.compute.learning.modelgen.decisiontree.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;
import ncsa.gui.*;

/*
	DecisionTreeVis

	Displays a scaled view of decision tree from tree scroll pane
	Draws a navigator that shows how much of tree is visible
*/
public class NavigatorPanel extends JPanel {

	public NavigatorPanel(DecisionTreeModel model, TreeScrollPane scrollpane) {
		Navigator navigator = new Navigator(model, scrollpane);

		setBackground(DecisionTreeScheme.borderbackgroundcolor);
		setLayout(new GridBagLayout());
		Constrain.setConstraints(this, navigator, 0, 0, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 1, 1);
	}

	class Navigator extends JPanel implements MouseListener, MouseMotionListener, ChangeListener {

		// Decision tree model
		DecisionTreeModel dmodel;

		// Decision tree root
		DecisionTreeNode droot;

		// Scaled tree root
		ScaledNode sroot;

		// Width and height of decision tree
		double dwidth, dheight;

		// Scaled width and height of decision tree
		double swidth = 200;
		double sheight;

		// Scaling factor
		double scale;

		// Maximum depth
		int mdepth;

		TreeScrollPane treescrollpane;
		JViewport viewport;

		// Width and height of navigator
		double nwidth, nheight;

		double xscale, yscale;

		// Offsets of navigator
		double x, y, lastx, lasty;

		boolean statechanged;

		public Navigator(DecisionTreeModel model, TreeScrollPane scrollpane) {
			dmodel = model;
			droot = dmodel.getRoot();
			sroot = new ScaledNode(dmodel, droot, null);

			treescrollpane = scrollpane;
			viewport = treescrollpane.getViewport();

			findMaximumDepth(droot);
			buildTree(droot, sroot);

			sroot.x = sroot.findLeftSubtreeWidth();
			sroot.y = sroot.yspace;

			findTreeOffsets(sroot);

			dwidth = sroot.findSubtreeWidth();
			dheight = (sroot.yspace + sroot.gheight)*(mdepth + 1) + sroot.yspace;

			scale = swidth/dwidth;
			sheight = scale*dheight;

			findSize();

			setOpaque(true);
			setBackground(DecisionTreeScheme.borderbackgroundcolor);

			addMouseListener(this);
			addMouseMotionListener(this);
			viewport.addChangeListener(this);
		}

		public void findMaximumDepth(DecisionTreeNode dnode) {
			int depth = dnode.getDepth();

			if (depth > mdepth)
				mdepth = depth;

			for (int index = 0; index < dnode.getNumChildren(); index++) {
				DecisionTreeNode dchild = dnode.getChild(index);
				findMaximumDepth(dchild);
			}
		}

		public void findTreeOffsets(ScaledNode snode) {
			snode.findOffsets();

			for (int index = 0; index < snode.getNumChildren(); index++) {
				ScaledNode schild = snode.getChild(index);
				findTreeOffsets(schild);
			}
		}

		public void buildTree(DecisionTreeNode dnode, ScaledNode snode) {
			for (int index = 0; index < dnode.getNumChildren(); index++) {
				DecisionTreeNode dchild = dnode.getChild(index);
				ScaledNode schild = new ScaledNode(dmodel, dchild, snode);
				snode.addChild(schild);
				buildTree(dchild, schild);
			}
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			AffineTransform transform = g2.getTransform();
			AffineTransform sinstance = AffineTransform.getScaleInstance(scale, scale);
			g2.transform(sinstance);

			drawTree(g2, sroot);

			g2.setTransform(transform);

			g2.setColor(DecisionTreeScheme.viewercolor);
			g2.setStroke(new BasicStroke(1));
			g2.draw(new Rectangle2D.Double(x, y, nwidth-1, nheight-1));
		}

		public void drawTree(Graphics2D g2, ScaledNode snode) {
			snode.drawScaledNode(g2);

			for (int index = 0; index < snode.getNumChildren(); index++) {
				ScaledNode schild = snode.getChild(index);

				double x1 = snode.x;
				double y1 = snode.y + snode.gheight;
				double x2 = schild.x;
				double y2 = schild.y;

				drawLine(g2, x1, y1, x2, y2);

				drawTree(g2, schild);
			}
		}

		public void drawLine(Graphics2D g2, double x1, double y1, double x2, double y2) {
			int linestroke = 1;

			g2.setStroke(new BasicStroke(linestroke));
			g2.setColor(DecisionTreeScheme.scaledviewbackgroundcolor);
			g2.draw(new Line2D.Double(x1, y1, x2, y2));
		}

		// Determine size of navigator
		public void findSize() {
			Point position = viewport.getViewPosition();
			Dimension vpdimension = viewport.getExtentSize();

			double vpwidth = vpdimension.getWidth();
			nwidth = vpwidth/dwidth*swidth;
			if (nwidth > swidth)
				nwidth = swidth;

			xscale = swidth/dwidth;
			x = xscale*position.x;

			double vpheight = vpdimension.getHeight();
			nheight = vpheight/dheight*sheight;
			if (nheight > sheight)
				nheight = sheight;

			yscale = sheight/dheight;
			y = yscale*position.y;
		}

		public Dimension getMinimumSize() {
			return new Dimension((int) swidth, (int) sheight);
		}

		public Dimension getPreferredSize() {
			return getMinimumSize();
		}

		public void mousePressed(MouseEvent event) {
			lastx = event.getX();
			lasty = event.getY();
		}

		public void mouseDragged(MouseEvent event) {
			int x1 = event.getX();
			int y1 = event.getY();

			double xchange = x1 - lastx;
			double ychange = y1 - lasty;

			x += xchange;
			y += ychange;

			if (x < 0)
				x = 0;
			if (y < 0)
				y = 0;
			if (x + nwidth > swidth)
				x = swidth - nwidth;
			if (y + nheight > sheight)
				y = sheight - nheight;

			statechanged = false;
			treescrollpane.scroll((int) (x/xscale), (int) (y/yscale));

			lastx = x1;
			lasty = y1;

			repaint();
		}

		/*
			Scrolling causes a change event, but scrolling caused by
			moving the navigator should not cause a change event.
		*/
		public void stateChanged(ChangeEvent event) {
			if (statechanged) {
				findSize();
				repaint();
			}

			statechanged = true;
		}

		public void mouseExited(MouseEvent event) {	}
		public void mouseEntered(MouseEvent event) { }
		public void mouseReleased(MouseEvent event) { }
		public void mouseClicked(MouseEvent event) { }
		public void mouseMoved(MouseEvent event) { }
	}
}
