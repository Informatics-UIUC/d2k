package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.print.*;

import javax.swing.*;
import ncsa.d2k.util.*;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.*;

/*
	DecisionTreeVis

	Draws the decision tree
*/
public class TreeScrollPane extends JScrollPane {

	JViewport viewport;
	TreePanel treepanel;

	public TreeScrollPane(ViewableDTModel model, BrushPanel panel) {

		treepanel = new TreePanel(model, panel);

		viewport = getViewport();
		viewport.setView(treepanel);
	}

	// Called by navigator panel
	public void scroll(int x, int y) {
		Point point = viewport.toViewCoordinates(new Point(x, y));
		viewport.setViewPosition(point);
	}

	public void toggleLabels() {
		if (treepanel.labels == true)
			treepanel.labels = false;
		else
			treepanel.labels = true;
		treepanel.repaint();
	}

	public Printable getPrintable() {
		return treepanel;
	}

	public class TreePanel extends JPanel implements MouseListener, MouseMotionListener, Printable {

		// Brush panel
		BrushPanel brushpanel;

		// Decision tree model
		ViewableDTModel dmodel;

		// Decision tree root
		ViewableDTNode droot;

		// View tree root
		ViewNode vroot;

		// List of offsets for each depth
		LinkedList[] offsets;

		// Width and height of decision tree
		double dwidth, dheight;

		// Maximum depth
		int mdepth;

		// Roll node
		ViewNode rnode;

		// Draw labels
		boolean labels = true;

		int lastx, lasty;

		public TreePanel(ViewableDTModel model, BrushPanel panel) {
			brushpanel = panel;

			dmodel = model;
			droot = dmodel.getViewableRoot();
			vroot = new ViewNode(dmodel, droot, null, "");

			findMaximumDepth(droot);
			buildViewTree(droot, vroot);

			offsets = new LinkedList[mdepth + 1];
			for (int index = 0; index <= mdepth; index++)
				offsets[index] = new LinkedList();

			vroot.x = vroot.findLeftSubtreeWidth();
			vroot.y = vroot.yspace;
			offsets[0].add(vroot);

			findViewTreeOffsets(vroot);

			dwidth = vroot.findSubtreeWidth();
			dheight = (vroot.yspace + vroot.gheight)*(mdepth + 1) + vroot.yspace;

			setBackground(DecisionTreeScheme.treebackgroundcolor);

			addMouseListener(this);
			addMouseMotionListener(this);
		}

		public void findMaximumDepth(ViewableDTNode dnode) {
			int depth = dnode.getDepth();

			if (depth > mdepth)
				mdepth = depth;

			for (int index = 0; index < dnode.getNumChildren(); index++) {
				ViewableDTNode dchild = dnode.getViewableChild(index);
				findMaximumDepth(dchild);
			}
		}

		// Finds the offsets for each node
		public void findViewTreeOffsets(ViewNode vnode) {
			vnode.findOffsets();
			offsets[vnode.getDepth()].add(vnode);

			for (int index = 0; index < vnode.getNumChildren(); index++) {
				ViewNode vchild = vnode.getChild(index);
				findViewTreeOffsets(vchild);
			}
		}

		// Builds a copy of the decision tree using view nodes
		public void buildViewTree(ViewableDTNode dnode, ViewNode vnode) {
			for (int index = 0; index < dnode.getNumChildren(); index++) {
				ViewableDTNode dchild = dnode.getViewableChild(index);
				ViewNode vchild;
				if( index == 0 )
					vchild = new ViewNode(dmodel, dchild, vnode, vnode.getBranchLabel(index));
				else if(index == dnode.getNumChildren())
					vchild = new ViewNode(dmodel, dchild, vnode, vnode.getBranchLabel(index));
				else
					vchild = new ViewNode(dmodel, dchild, vnode, vnode.getBranchLabel(index));
				vnode.addChild(vchild);
				buildViewTree(dchild, vchild);
			}
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			drawViewTree(g2, vroot);
		}

		// Draws the view tree
		public void drawViewTree(Graphics2D g2, ViewNode vnode) {
			vnode.drawViewNode(g2);

			if (vnode.collapsed)
				return;

			for (int index = 0; index < vnode.getNumChildren(); index++) {
				ViewNode vchild = vnode.getChild(index);

				double x1 = vnode.x;
				double y1 = vnode.y + vnode.gheight;
				double x2 = vchild.x;
				double y2 = vchild.y;

				drawLine(g2, vnode.getBranchLabel(index), x1, y1, x2, y2);

				drawViewTree(g2, vchild);
			}
		}

		// Draws a line between nodes
		public void drawLine(Graphics2D g2, String label, double x1, double y1, double x2, double y2) {
			int linestroke = 1;

			double diameter = 8;
			double radius = diameter/2;
			double xcircle, ycircle;
			int circlestroke = 2;

			FontMetrics metrics = getFontMetrics(DecisionTreeScheme.textfont);
			int fontascent = metrics.getAscent();

			double xlabel, ylabel;
			double labelspace = 20;
			int labelwidth = metrics.stringWidth(label);

			// Line
			g2.setStroke(new BasicStroke(linestroke));
			g2.setColor(DecisionTreeScheme.treelinecolor);
			g2.draw(new Line2D.Double(x1, y1, x2, y2-1));

			if (x1 < x2) {
				xcircle = x1 + (x2 - x1)/2 - radius;
				xlabel = xcircle + diameter - 2*circlestroke + labelspace;
			}
			else {
				xcircle = x1 - radius - (x1 - x2)/2;
				xlabel = xcircle - labelspace - labelwidth;
			}

			ycircle = y1 + (y2 - y1)/2 - radius;
			ylabel = ycircle + diameter;

			// Label
			if (labels) {
				g2.setFont(DecisionTreeScheme.textfont);
				g2.setColor(DecisionTreeScheme.textcolor);
				g2.drawString(label, (int) xlabel, (int) ylabel);
			}

			// Circle
			g2.setColor(DecisionTreeScheme.treecirclebackgroundcolor);
			g2.fill(new Ellipse2D.Double(xcircle, ycircle, 8, 8));

			g2.setColor(DecisionTreeScheme.treecirclestrokecolor);
			g2.setStroke(new BasicStroke(circlestroke));
			g2.draw(new Ellipse2D.Double(xcircle, ycircle, 8, 8));
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public Dimension getPreferredSize() {
			return new Dimension((int) dwidth, (int) dheight);
		}

		// Returns the tree depth given y offset
		public int findDepth(int y) {
			if (y > dheight - vroot.yspace)
				return -1;

			int depth = (int) (y/(vroot.yspace + vroot.gheight));
			if ((y - depth*(vroot.yspace + vroot.gheight)) >= vroot.yspace)
				return depth;

			return -1;
		}


		// Mouse listener methods
		public void mouseClicked(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();

			int depth = findDepth(y);

			if (depth == -1) {
				return;
			}

			LinkedList list = offsets[depth];
			boolean valid = true;
			int index = 0;

			while (valid) {
				ViewNode vnode = (ViewNode) list.get(index);
				int test = vnode.test(x, y);

				if (test == -1) {
					index++;
				}
				else if (test == 1) {
					valid = false;
					if (vnode.isVisible())
						expandView(vnode.dnode);
				}
				else if (test == 2) {
					valid = false;
					vnode.toggle();
					repaint();
				}

				if (index == list.size()) {
					valid = false;
				}
			}
		}

		public void expandView(ViewableDTNode dnode) {
			ExpandedGraph graph = new ExpandedGraph(dmodel, dnode);

			JFrame frame = new JFrame();
			frame.getContentPane().add(new JScrollPane(graph));
			frame.pack();
			frame.setVisible(true);
		}

		public void mousePressed(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();

			lastx = x;
			lasty = y;

			int depth = findDepth(y);
			if (depth == -1) {
				return;
			}

			LinkedList list = offsets[depth];
			boolean valid = true;
			int index = 0;

			while (valid) {
				ViewNode vnode = (ViewNode) list.get(index);
				int test = vnode.test(x, y);

				if (test == -1) {
					index++;
				}
				else if (test == 1) {
					valid = false;
					if (vnode.isVisible()) {
						vnode.roll = true;
						rnode = vnode;
						repaint();
					}
				}
				else if (test == 2) {
					valid = false;
				}

				if (index == list.size()) {
					valid = false;
				}
			}
		}

		public void mouseReleased(MouseEvent event) {
			if (rnode != null) {
				rnode.roll = false;
				rnode = null;
				repaint();
			}
		}

		public void mouseExited(MouseEvent event) {	}
		public void mouseEntered(MouseEvent event) { }

		// Mouse motion listener methods
		public void mouseMoved(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();

			int depth = findDepth(y);

			if (depth == -1) {
				brushpanel.updateBrush(null);
				return;
			}

			LinkedList list = offsets[depth];
			boolean valid = true;
			int index = 0;

			while (valid) {
				ViewNode vnode = (ViewNode) list.get(index);
				int test = vnode.test(x, y);
				if (test == -1) {
					index++;
				}
				else if (test == 1) {
					valid = false;
					if (vnode.isVisible())
						brushpanel.updateBrush(vnode.dnode);
				}
				else if (test == 2) {
					valid = false;
				}

				if (index == list.size()) {
					valid = false;
					brushpanel.updateBrush(null);
				}
			}
		}

		public void mouseDragged(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();

			int xchange = x - lastx;
			int ychange = y - lasty;

			Dimension vpdimension = viewport.getExtentSize();
			double vpwidth = viewport.getWidth();
			double vpheight = viewport.getHeight();

			Point point = viewport.getViewPosition();

			point.x -= xchange;
			point.y -= ychange;

			if (point.x < 0 )
				point.x = 0;
			else if (vpwidth > dwidth)
				point.x = 0;
			else if (point.x + vpwidth > dwidth)
				point.x = (int) (dwidth - vpwidth);

			if (point.y < 0)
				point.y = 0;
			else if (vpheight > dheight)
				point.y = 0;
			else if (point.y + vpheight > dheight)
				point.y = (int) (dheight - vpheight);

			viewport.setViewPosition(point);
		}

		/**
		* Print this component.
		*/
		public int print(Graphics g, PageFormat pf, int pi)
			throws PrinterException {

			double pageHeight = pf.getImageableHeight();
			double pageWidth = pf.getImageableWidth();

			double cWidth = getWidth();
			double cHeight = getHeight();

			double scale = 1;
			if(cWidth >= pageWidth)
				scale = pageWidth/cWidth;
			if(cHeight >= pageHeight)
				scale = Math.min(scale, pageHeight/cHeight);

			double cWidthOnPage = cWidth*scale;
			double cHeightOnPage = cHeight*scale;

			if(pi >= 1)
				return Printable.NO_SUCH_PAGE;

			Graphics2D g2 = (Graphics2D)g;
			g2.translate(pf.getImageableX(), pf.getImageableY());
			g2.scale(scale, scale);
			print(g2);
			return Printable.PAGE_EXISTS;
		}
	}
}
