package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import ncsa.d2k.util.*;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;
import ncsa.gui.*;

/**
	Wrapper class
*/
public class NavigatorPanel extends JPanel {

	public NavigatorPanel(DecisionTreeModel mdl, DecisionTreeNode tree, TreeScrollPane treescrollpane) {
		Navigator navigator = new Navigator(mdl, tree, treescrollpane);

		setBackground(DecisionTreeScheme.borderbackgroundcolor);
		setLayout(new GridBagLayout());
		Constrain.setConstraints(this, navigator, 0, 0, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 1, 1);
	}

	class Navigator extends JPanel implements MouseListener, MouseMotionListener, ChangeListener {

		// Width of panel
		double panelwidth = 250;

		/**
			Viewer
		*/
		TreeScrollPane treescrollpane;
		JViewport viewport;

		// Dimensions of viewer
		double viewerwidth, viewerheight;

		double xscale, yscale;

		// Coordinates of viewer
		double xview = 0;
		double yview = 0;
		double lastxview = 0;
		double lastyview = 0;

		/**
			Background
		*/
		DecisionTreeModel model;
		DecisionTreeNode tree;
		ScaledNode viewtree;

		int treeheight;
		int leaves;

		// Empty space
		double xspace = 50;
		double yspace = 75;

		double panelwidthratio;

		// Dimensions of tree
		double fullwidth, fullheight;

		// Dimenions of scaled tree
		double scaledwidth, scaledheight;

		// Dimensions of nodes
		double viewwidth, viewheight;

		double top = 50;
		double bottom = 50;

		public Navigator(DecisionTreeModel mdl, DecisionTreeNode tree, TreeScrollPane treescrollpane) {
			this.model = mdl;
			this.tree = tree;
			this.treescrollpane = treescrollpane;
			viewport = treescrollpane.getViewport();

			setOpaque(true);
			setBackground(DecisionTreeScheme.borderbackgroundcolor);

			addMouseListener(this);
			addMouseMotionListener(this);
			viewport.addChangeListener(this);
		}

		/**
			Build view tree representing decision tree

			Determine dimensions of tree
			Determine offsets of view nodes

		*/
		public void buildViewTree() {
			viewtree.xoffset = (fullwidth/2)-(viewwidth/2);
			viewtree.yoffset = top;

			buildRecursive(tree, viewtree);
		}

		public void buildRecursive(DecisionTreeNode modelnode, ScaledNode viewnode) {
			if (modelnode.isLeaf()) {
				viewnode.leaf = true;
				return;
			}

			int level = modelnode.getDepth();//modelnode.getLevel();
			double div = Math.pow(2.0, (double) (level+2));
			double increment = fullwidth/div;

			if (/*modelnode.getLeft()*/modelnode.getChild(0) != null) {
				ScaledNode leftview = new ScaledNode(model, /*modelnode.getLeft()*/
					modelnode.getChild(0), viewnode);
				viewnode.left = leftview;
				leftview.xoffset = viewnode.xoffset-increment;
				leftview.yoffset = viewnode.yoffset+viewheight+yspace;
				buildRecursive(/*modelnode.getLeft()*/
					modelnode.getChild(0), leftview);
			}
			if (/*modelnode.getRight()*/modelnode.getChild(1) != null) {
				ScaledNode rightview = new ScaledNode(model, /*modelnode.getRight()*/
					modelnode.getChild(1), viewnode);
				viewnode.right = rightview;
				rightview.xoffset = viewnode.xoffset+increment;
				rightview.yoffset = viewnode.yoffset+viewheight+yspace;
				buildRecursive(/*modelnode.getRight()*/
					modelnode.getChild(1), rightview);
			}
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			buildViewTree();

			AffineTransform transform = g2.getTransform();
			AffineTransform scale = AffineTransform.getScaleInstance(panelwidthratio, panelwidthratio);
			g2.transform(scale);
			drawTree(g2);
			g2.setTransform(transform);

			/**
				Draw viewer
			*/
			Point pos = viewport.getViewPosition();
			Dimension viewdim = viewport.getExtentSize();

			double viewportwidth = viewdim.getWidth();
			viewerwidth = viewportwidth/fullwidth*scaledwidth;

			// Resize viewerwidth and move viewer horizontally
			xscale = fullwidth/scaledwidth;
			if (xscale > 0)
				xview = pos.x/xscale;
			if (viewerwidth > scaledwidth)
				viewerwidth = scaledwidth;

			double viewportheight = viewdim.getHeight();
			viewerheight = viewportheight/fullheight*scaledheight;

			// Resize viewerheight and move viewer vertically
			yscale = fullheight/scaledheight;
			if (yscale > 0)
				yview = pos.y/yscale;
			if (viewerheight > scaledheight)
				viewerheight = scaledheight;

			g2.setColor(DecisionTreeScheme.viewercolor);
			g2.setStroke(new BasicStroke(1));
			g2.draw(new Rectangle2D.Double(xview, yview, viewerwidth-1, viewerheight-1));
		}

		public void drawTree(Graphics2D g2) {
			viewtree.drawScaledNode(g2);
			drawRecursive(g2, tree, viewtree);
		}

		public void drawRecursive(Graphics2D g2, DecisionTreeNode modelnode, ScaledNode viewnode) {

			if (modelnode.isLeaf())
				return;

			if (/*modelnode.getLeft()*/modelnode.getChild(0) != null) {
				ScaledNode leftview = viewnode.left;
				leftview.drawScaledNode(g2);
				drawLine(g2, viewnode.xoffset+(viewwidth/2), viewnode.yoffset+viewheight,
					leftview.xoffset+(viewwidth/2), leftview.yoffset);
				drawRecursive(g2, /*modelnode.getLeft()*/
					modelnode.getChild(0), leftview);
			}
			if (/*modelnode.getRight()*/modelnode.getChild(1) != null) {
				ScaledNode rightview = viewnode.right;
				rightview.drawScaledNode(g2);
				drawLine(g2, viewnode.xoffset+(viewwidth/2), viewnode.yoffset+viewheight,
					rightview.xoffset+(viewwidth/2), rightview.yoffset);
				drawRecursive(g2, /*modelnode.getRight()*/
					modelnode.getChild(1), rightview);
			}
		}

		public void drawLine(Graphics2D g2, double x1, double y1, double x2, double y2) {

			int linestroke = 1;

			// Draw line
			g2.setStroke(new BasicStroke(linestroke));
			g2.setColor(DecisionTreeScheme.scaledviewbackgroundcolor);
			g2.draw(new Line2D.Double(x1, y1, x2, y2));
		}

		public void mousePressed(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();

			lastxview = x;
			lastyview = y;
		}

		public void mouseDragged(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();

			double xdiff = x-lastxview;
			double ydiff = y-lastyview;

			xview += xdiff;
			yview += ydiff;

			if (xview < 0)
				xview = 0;
			if (yview < 0)
				yview = 0;
			if (xview+viewerwidth > scaledwidth)
				xview = scaledwidth-viewerwidth;
			if (yview+viewerheight > scaledheight)
				yview = scaledheight-viewerheight;

			treescrollpane.scroll((int) (xview*xscale), (int) (yview*yscale));

			lastxview = x;
			lastyview = y;

			repaint();
		}

		/**
			View port listener
		*/
		public void stateChanged(ChangeEvent event) {
			repaint();
		}

		public void mouseExited(MouseEvent event) {	}
		public void mouseEntered(MouseEvent event) {	}
		public void mouseReleased(MouseEvent event) { }
		public void mouseClicked(MouseEvent event) { }
		public void mouseMoved(MouseEvent event) { }

		public Dimension getMinimumSize() {
			treeheight = tree.getDepth();
			leaves = (int) Math.pow(2.0, (double) treeheight);

			viewtree = new ScaledNode(model, tree, null);
			viewwidth = viewtree.width;
			viewheight = viewtree.height;

			fullwidth = leaves*(viewwidth+xspace)+xspace;
			fullheight = treeheight*(viewheight+yspace)+viewheight+top+bottom;

			panelwidthratio = panelwidth/fullwidth;
			scaledwidth = panelwidth;
			scaledheight = panelwidthratio*fullheight;

			return new Dimension((int) scaledwidth, (int) scaledheight);
		}

		public Dimension getPreferredSize() {
			return getMinimumSize();
		}
	}
}
