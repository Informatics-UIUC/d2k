package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.util.*;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;

/**
	Scroll pane that holds tree panel that renders the tree
*/
public class SimpleTreeScrollPane extends JScrollPane {

	TreePanel treepanel;
	JViewport viewport;
	DecisionTreeModel mdl;

	public SimpleTreeScrollPane(DecisionTreeModel mdl, DecisionTreeNode tree) {
		this.mdl = mdl;
		treepanel = new TreePanel(mdl, tree);

		viewport = getViewport();
		viewport.setView(treepanel);
	}

	public void setTree(DecisionTreeNode tree) {
		treepanel.setTree(tree);
	}

	/*public void toggleBreakpoints() {
		if (treepanel.breakpoints == true)
			treepanel.breakpoints = false;
		else
			treepanel.breakpoints = true;
		treepanel.repaint();
	}*/

	/*public void clearSearchResults() {
		treepanel.clearSearchResults();
	}*/

	public int getTreeWidth() {
		return treepanel.getTreeWidth();
	}

	public int getTreeHeight() {
		return treepanel.getTreeHeight();
	}

	/**
		Renders decision tree
	*/
	public class TreePanel extends JPanel implements MouseListener, MouseMotionListener {

		DecisionTreeModel model;
		DecisionTreeNode tree;
		ViewNode viewtree;
		LinkedList[] offsets;

		int treeheight;
		int leaves;

		int lastx, lasty;

		// Draw break points
		boolean breakpoints = true;

		// Empty space
		double xspace = 50;
		double yspace = 75;

		double top = 50;
		double bottom = 50;

		// Dimensions of tree
		double width, height;

		// Dimensions of nodes
		double viewwidth, viewheight;

		ViewNode viewroll = null;

		public TreePanel(DecisionTreeModel mdl, DecisionTreeNode tree) {
			this.model = mdl;
			this.tree = tree;

			buildViewTree();

			setBackground(DecisionTreeScheme.treebackgroundcolor);

			addMouseListener(this);
			addMouseMotionListener(this);
		}

		public void setTree(DecisionTreeNode tree) {
			this.tree = tree;
			buildViewTree();
			repaint();
		}

		int maxDepth = 0;
		int maxWidth = 0;

		/**
			Build view tree representing decision tree

			Determine dimensions of tree
			Determine offsets of view nodes
		*/
		public void buildViewTree() {
			if(tree.getNumChildren() == 0)
				treeheight = 1;
			else
				treeheight = 2;
			int leaves = 0;
			leaves = tree.getNumChildren();

			viewtree = new ViewNode(model, tree, null);
			viewwidth = viewtree.width;
			viewheight = viewtree.height;

			width = leaves*(viewwidth+xspace)+xspace;
			height = treeheight*(viewheight+yspace)+viewheight+top+bottom;

			viewtree.xoffset = (width/2)-(viewwidth/2);
			viewtree.yoffset = top;

			offsets = new LinkedList[treeheight+1];
			for (int index=0; index <= treeheight; index++) {
				offsets[index] = new LinkedList();
			}
			LinkedList levellist = offsets[0];
			levellist.add(viewtree);

			buildRecursive(tree, viewtree);
		}

		public void buildRecursive(DecisionTreeNode modelnode, ViewNode viewnode) {
			if (modelnode.isLeaf()) {
				viewnode.leaf = true;
				return;
			}

			int level = maxDepth-modelnode.getDepth();

			/*double div = Math.pow(2.0, (double) (level+2));
			double increment = width/div;

			if (modelnode.getChild(0) != null) {
				ViewNode leftview = new ViewNode(model,
					modelnode.getChild(0), viewnode);
				viewnode.left = leftview;
				leftview.xoffset = viewnode.xoffset-increment;
				leftview.yoffset = viewnode.yoffset+viewheight+yspace;
				buildRecursive(modelnode.getChild(0), leftview);

				level = maxDepth-modelnode.getChild(0).getDepth();
				LinkedList levellist = offsets[level];
				levellist.add(leftview);
			}
			if (modelnode.getChild(0) != null) {
				ViewNode rightview = new ViewNode(model,
					modelnode.getChild(1), viewnode);
				viewnode.right = rightview;
				rightview.xoffset = viewnode.xoffset+increment;
				rightview.yoffset = viewnode.yoffset+viewheight+yspace;
				buildRecursive(modelnode.getChild(1), rightview);

				level = maxDepth-modelnode.getChild(1).getDepth();
				LinkedList levellist = offsets[level];
				levellist.add(rightview);
			}*/
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

			drawTree(g2);
			drawGrid(g2);
		}

		public void drawTree(Graphics2D g2) {
			drawRecursive(g2, tree, viewtree);
		}

		public void drawRecursive(Graphics2D g2, DecisionTreeNode modelnode,
			ViewNode viewnode) {

			if(!modelnode.isLeaf()) {
			/*if (modelnode.getChild(0) != null) {
				if (!viewnode.collapsed) {
					ViewNode leftview = viewnode.left;
					drawLine(g2, modelnode,
						modelnode.getBranchLabel(0), viewnode.xoffset+(viewwidth/2), viewnode.yoffset+viewheight,
						leftview.xoffset+(viewwidth/2), leftview.yoffset);
					drawRecursive(g2,
						modelnode.getChild(0), leftview);
				}
			}
			if (modelnode.getChild(1) != null) {
				if (!viewnode.collapsed) {
					ViewNode rightview = viewnode.right;
					drawLine(g2, modelnode,
						modelnode.getBranchLabel(1), viewnode.xoffset+(viewwidth/2), viewnode.yoffset+viewheight,
						rightview.xoffset+(viewwidth/2), rightview.yoffset);
					drawRecursive(g2,
						modelnode.getChild(1), rightview);
				}
			}*/
			}

			viewnode.drawViewNode(g2);
		}

		public void drawLine(Graphics2D g2, DecisionTreeNode node, String rule,
			double x1, double y1, double x2, double y2) {
			int linestroke = 1;

			double diameter = 8;
			double radius = diameter/2;
			double xcircle, ycircle;
			int circlestroke = 2;

			FontMetrics metrics = getFontMetrics(DecisionTreeScheme.textfont);
			int fontascent = metrics.getAscent();

			double xrule, yrule;
			double rulespace = 20;
			int rulewidth = metrics.stringWidth(rule);

			// Draw line
			g2.setStroke(new BasicStroke(linestroke));
			g2.setColor(DecisionTreeScheme.treelinecolor);
			g2.draw(new Line2D.Double(x1, y1, x2, y2-1));

			if (x1 < x2) {
				xcircle = x1+(x2-x1)/2-radius;
				xrule= xcircle+diameter-2*circlestroke+rulespace;
			}
			else {
				xcircle = x1-radius-(x1-x2)/2;
				xrule = xcircle-rulespace-rulewidth;
			}

			ycircle = y1+(y2-y1)/2-radius;
			yrule = ycircle+diameter;

			// Draw rule
			if (breakpoints) {
				g2.setFont(DecisionTreeScheme.textfont);
				g2.setColor(DecisionTreeScheme.textcolor);
				g2.drawString(rule, (int) xrule, (int) yrule);
			}

			// Draw circle
			g2.setColor(DecisionTreeScheme.treecirclebackgroundcolor);
			g2.fill(new Ellipse2D.Double(xcircle, ycircle, 8, 8));

			g2.setColor(DecisionTreeScheme.treecirclestrokecolor);
			g2.setStroke(new BasicStroke(circlestroke));
			g2.draw(new Ellipse2D.Double(xcircle, ycircle, 8, 8));
		}

		public void drawGrid(Graphics2D g2) {

			float gridstroke = .1f;

			g2.setStroke(new BasicStroke(gridstroke));
			g2.setColor(DecisionTreeScheme.treelinelevelcolor);

			double yoffset = top+viewheight;

			for (int level=0; level <= treeheight; level++) {
				g2.draw(new Line2D.Double(0, yoffset, width, yoffset));
				yoffset += viewheight+yspace;
			}
		}


		/*public void clearSearchResults() {
			clearRecursive(viewtree);
			repaint();
		}*/

		/*public void clearRecursive(ViewNode view) {
			if (view == null)
				return;

			view.searchmatch = false;
			clearRecursive(view.left);
			clearRecursive(view.right);
		}*/

		/**
			Determine tree level at given y offset
		*/
		public int findLevel(int yoff) {
			double y = (double) yoff;

			if (y < top)
				return -1;
			if (y > (height-bottom))
				return -1;

			if (y < top+viewheight)
				return 0;

			y = y-top-viewheight;
			int level = (int) (y/(yspace+viewheight));
			if ((y-level*(yspace+viewheight)) >= yspace)
				return level+1;

			return -1;
		}

		/**
			Mouse listener methods

			Display expanded graph if necessary
			Display view roll if necessary
		*/
		public void mouseClicked(MouseEvent event) {
			int xoff = event.getX();
			int yoff = event.getY();

			int level = findLevel(yoff);

			if (level == -1) {
				return;
			}

			LinkedList levellist = offsets[level];
			boolean valid = true;
			int index = 0;

			while (valid) {
				ViewNode view = (ViewNode) levellist.get(index);
				int eval = view.evaluate(xoff, yoff);
				if (eval == -1) {
					index++;
				}
				else if (eval == 1) {
					valid = false;
					if (view.visible())
						showViewWindow(view.modelnode);
				}
				else if (eval == 2) {
					valid = false;
					view.toggle();
					repaint();
				}

				if (index == levellist.size()) {
					valid = false;
				}
			}
		}

		public void showViewWindow(DecisionTreeNode node) {
			JFrame frame = new JFrame();
			ExpandedGraph graph = new ExpandedGraph(model, node);
			Dimension dimension = graph.getPreferredSize();
			frame.getContentPane().add(graph);
			frame.pack();
			frame.setVisible(true);
		}

		public void mousePressed(MouseEvent event) {
			int xoff = event.getX();
			int yoff = event.getY();

			lastx = xoff;
			lasty = yoff;

			int level = findLevel(yoff);

			if (level == -1) {
				return;
			}

			LinkedList levellist = offsets[level];
			boolean valid = true;
			int index = 0;

			while (valid) {
				ViewNode view = (ViewNode) levellist.get(index);
				int eval = view.evaluate(xoff, yoff);
				if (eval == -1) {
					index++;
				}
				else if (eval == 1) {
					valid = false;
					if (view.visible()) {
						view.roll = true;
						viewroll = view;
						repaint();
					}
				}
				else if (eval == 2) {
					valid = false;
				}

				if (index == levellist.size()) {
					valid = false;
				}
			}
		}

		public void mouseReleased(MouseEvent event) {
			if (viewroll != null) {
				viewroll.roll = false;
				viewroll = null;
				repaint();
			}
		}

		public void mouseExited(MouseEvent event) {	}
		public void mouseEntered(MouseEvent event) { }

		/**
			Mouse motion listener methods

			Update brush pane if necessary
		*/
		public void mouseMoved(MouseEvent event) {
			int xoff = event.getX();
			int yoff = event.getY();

			int level = findLevel(yoff);

			if (level == -1) {
				return;
			}

			LinkedList levellist = offsets[level];
			if (levellist.size() == 0)
				return;

			boolean valid = true;
			int index = 0;

			while (valid) {
				ViewNode view = (ViewNode) levellist.get(index);
				int eval = view.evaluate(xoff, yoff);
				if (eval == -1) {
					index++;
				}
				else if (eval == 1) {
					valid = false;
					//if (view.visible())
					//	brushpanel.updateBrush(view.modelnode);
				}
				else if (eval == 2) {
					valid = false;
				}

				if (index == levellist.size()) {
					valid = false;
					//brushpanel.updateBrush(null);
				}
			}
		}

		public void mouseDragged(MouseEvent event) {
			int xoff = event.getX();
			int yoff = event.getY();

			int xdiff = xoff-lastx;
			int ydiff = yoff-lasty;

			Dimension viewportdim = viewport.getExtentSize();
			double viewportwidth = viewport.getWidth();
			double viewportheight = viewport.getHeight();

			Point pos = viewport.getViewPosition();

			pos.x += -xdiff;
			pos.y += -ydiff;

			if (pos.x < 0 )
				pos.x = 0;
			else if (viewportwidth > width)
				pos.x = 0;
			else if (pos.x+viewportwidth > width)
				pos.x = (int) (width-viewportwidth);

			if (pos.y < 0)
				pos.y = 0;
			else if (viewportheight > height)
				pos.y = 0;
			else if (pos.y+viewportheight > height)
				pos.y = (int) (height-viewportheight);

			viewport.setViewPosition(pos);
		}

		/**
			Dimensions
		*/
		public int getTreeWidth() {
			return (int) width;
		}

		public int getTreeHeight() {
			return (int) height;
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public Dimension getPreferredSize() {
			return new Dimension(getTreeWidth(), getTreeHeight());
		}
	}
}
