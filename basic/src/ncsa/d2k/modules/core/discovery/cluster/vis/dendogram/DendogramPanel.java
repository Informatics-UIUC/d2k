/*
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 *
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 *
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
package ncsa.d2k.modules.core.discovery.cluster.vis.dendogram;


import ncsa.d2k.core.gui.JD2KFrame;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.discovery.cluster.ClusterModel;
import ncsa.d2k.modules.core.discovery.cluster.util.TableCluster;
import ncsa.d2k.userviews.swing.JUserPane;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;


/**
 * <p>Title: DendogramPanel</p>
 *
 * <p>Description:</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class DendogramPanel extends JUserPane implements UserView,
                                                         ActionListener {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -5517876694464597015L;

   //~ Instance fields *********************************************************

   /** Nodes from the cluster's graph that were already built. */
   private ArrayList _built = null;

   /** clusters of the cluster model <codE>_model.</code> */
   private ArrayList _clusters = null;

   /** The visualizing module of the table cluster. */
   private DendogramClusterVis _dcv = null;

   /** maps a node to its rect-wrapper. */
   private Hashtable _hash = null;

   /** The cluster model to be rendered on this panel. */
   private ClusterModel _model = null;

   /** The root cluster of cluster model. */
   private TableCluster _root = null;

   /** Menu item to invoke the help window. */
   private JMenuItem helpItem = null;

   /** The help window component. */
   private HelpWindow helpWindow;

   /** Menu item to select the high value color. */
   private ColorMenuItem hiSelectedColor = null;

   /** Menu item to select the low value color. */
   private ColorMenuItem lowSelectedColor = null;

   /** A radio button to choose whether to display cohesion. */
   private JRadioButton m_cohesion = null;

   /** childless nodes in the cluster graph. */
   private ArrayList m_leaves = null;

   /** nodes in the cluster graph that have children. */
   private ArrayList m_rects = null;

   /** Reset button, to reset the graph. */
   private JButton m_reset = null;

   /** scroll bar pane. */
   private JScrollPane m_scroll = null;

   /**
    * A component to allow the user to choose whether to display the clusters'
    * size.
    */
   private JRadioButton m_size = null;

   /**
    * A component to allow the user to choose whether to display the clusters'
    * size as colors.
    */
   private JRadioButton m_sizecolor = null;

   /** Menu bar to hold the menu items. */
   private JMenuBar menuBar = null;

   /** Highest value. */
   double _highvalue = 0;

   /** The lowest value in the graph. */
   double _lowvalue = 0;

   /** contains the graph(<code>m_rectpan</code>) and the buttons. */
   JPanel _pan = null;

   /** Where the Y axle is cut off at the bottom, not necessarily zero. */
   double _ycut = 0;

   /** When rectangle is created this is its y position int he pane. */
   int _ymark = 0;

   /** holds the graph nodes. */
   JPanel m_rectpan = null;

   //~ Constructors ************************************************************

   /**
    * Constructs empty menu bar and the help window.
    *
    * @param dcv DendogramClusterVis A visualization module to visualize the
    *            cluster model
    */
   public DendogramPanel(DendogramClusterVis dcv) {
      _dcv = dcv;
      menuBar = new JMenuBar();
      helpWindow = new HelpWindow();
   }

   //~ Methods *****************************************************************

   /**
    * Returns the text that is displayed int he help window.
    *
    * @return String The text that is displayed int he help window
    */
   static private final String getHelpString() {
      String s = new String("<html>");
      s += "<h2>Dendogram Vis</h2>";
      s += "<p><b>Overview:</b> ";
      s +=
         "This module visualizes a ClusterModel object that is the output of an ";
      s +=
         "hierarchical agglomerative clustering algorithm.  The visualization is ";
      s += "of the dendogram produced by the agglomerative (bottom-up) process.";
      s += "</p>";

      s += "<p><b>Detailed Description:</b> ";
      s +=
         "The dendogram produced represents a hard clustering of a data table using ";
      s +=
         "a hierarchical agglomerative clustering algorithm.  Some cluster models will ";
      s +=
         "contain complete dendogram trees (from the actual table rows to the single ";
      s +=
         "root cluster.  Other models will contain trees that start at some cluster cut ";
      s +=
         "(for example the clusters returned from a KMeans algorithm) to the root. ";
      s +=
         "Also, not all trees are monotonic (i.e. the height of subclusters is always <= ";
      s +=
         "to the height of their parents where height is measured in cluster dissimilarity. ";
      s +=
         "In particular, the centroid clustering methods (see HierAgglomClusterer props) are ";
      s += "known to sometimes produce non-monotonic dendogram trees.";
      s += "</p>";

      s += "<p> <b>GUI Controls:</b> ";
      s +=
         "If you double click on a cluster in the tree the dendogram will be repainted with ";
      s +=
         "the chosen cluster as root.  If you hit the reset button the original root will be ";
      s +=
         "restored. </p><p> If you double click on a cluster while holding down the <b>shift key</b> a table ";
      s +=
         "of values for that cluster will be displayed.</p><p>If you double click on a cluster ";
      s +=
         "while holding down the <b>control key</b> the centroid for that cluster will be displayed. ";
      s += "</p>";

      s += "<p><b>Data Type Restrictions:</b> ";
      s += "The input ClusterModel must contain a table that is serializable.";
      s += "</p>";

      s += "<p><b>Data Handling:</b> ";
      s +=
         "The input ClusterModel will be saved as part of the visualization.  It is not modified.";
      s += "</p>";

      s += "<p><b>Scalability:</b> ";
      s +=
         "The entire data table is saved as part of this visualization.  Sufficient ";
      s += "memory resources must be available to stage this data.";
      s += "</p>";
      s += "</html>";

      return s;
   } // end method getHelpString


   /**
    * Adds nodes to the visualization.
    *
    * @param root TableCluster the root node of the cluster tree
    */
   private void buildRects(TableCluster root) {

      // set start mark for Y axis
      _built = new ArrayList();
      m_rects = new ArrayList();
      m_leaves = new ArrayList();

      genBuilt(root);

      TreeSet sorted = new TreeSet(new cRank_Comparator());
      sorted.addAll(_built);
      _built = new ArrayList();

      for (Iterator it = sorted.iterator(); it.hasNext();) {
         _built.add(it.next());
      }

      Iterator it = _built.iterator();
      TableCluster lowest = null;
      int leaves = 0;

      while (it.hasNext()) {
         lowest = (TableCluster) it.next();

         if (lowest.getChildDistance() >= 0) {
            break;
         }

         leaves++;
      }

      _ymark = leaves * 10;

      // _ycut = Math.round((1 - selfsim)*10000*.8);
      // _lowvalue = Math.round(1/Math.pow(highestSim,2));
      // _lowvalue = Math.round(Math.pow(1 + lowest.getChildDistance(),1.5));
      _lowvalue = Math.round((1 + lowest.getChildDistance()) * 100000);
      _ycut = _lowvalue * .8;
      _lowvalue -= _ycut;

      // _highvalue = Math.round(1/Math.pow(highestSim,2)) - _ycut; _highvalue =
      // Math.round(Math.pow(1 + root.getChildDistance(),1.5) - _ycut);
      _highvalue = Math.round((1 + root.getChildDistance()) * 100000 - _ycut);

      try {

         // call postorder walk and build rect wrappers
         _hash = new Hashtable();

         // also adds each cluster to _built
         postOrderWalk(root);

         for (int i = 0, n = _built.size(); i < n; i++) {
            RectWrapper rw = (RectWrapper) _hash.get(_built.get(n - i - 1));

            if (!(rw.getRect().getHeight() == 0)) {
               m_rects.add(rw);
            } else {
               m_leaves.add(rw);
            }
         }
      } catch (Exception exc) {
         System.out.println("Unable to build dendogram due to -- " + exc);
         exc.printStackTrace();

         return;
      }
   } // end method buildRects

   /**
    * Recursively adds nodes to the <code>_built</code> array list. call
    * genBuilt with the children of node then add node to <codE>_built</code>
    *
    * @param node TableCluster a node in the cluster tree graph.
    */
   private void genBuilt(TableCluster node) {

      if (node != null) {
         genBuilt(node.getLC());
         genBuilt(node.getRC());

         // do stuff
         _built.add(node);
      }

   }


   /**
    * Initializes this panel.
    */
   private void init() { }

   /**
    * Performs a post order walk of the graph, starting from <code>node</code>
    * and builds rect-wrappers for each node.
    *
    * @param node TableCluster the root of the sub cluster tree to start
    *             performing the post order walk.
    */
   private void postOrderWalk(TableCluster node) {

      if (node != null) {
         postOrderWalk(node.getLC());
         postOrderWalk(node.getRC());

         /**
          * This is a complete binary tree so if the left child is null then
          * this is a leaf.
          */
         if (node.isLeaf()) {
            RectWrapper rw =
               new RectWrapper(new Rectangle(0, _ymark, 0, 0), node,
                               new Color(0, 0, 0));
            rw.addLeaf(node);
            _ymark -= 10;
            _hash.put(node, rw);
         } else {
            long newX = 0;
            long newY = 0;

            // long newW = Math.round((Math.pow(node.getChildDistance(),2)) -
            // _ycut); long newW = Math.round(Math.pow(1 +
            // node.getChildDistance(),1.5) - _ycut);
            long newW =
               Math.round((1 + node.getChildDistance()) * 100000 - _ycut);
            long newH = 0;
            TableCluster topC = null;
            TableCluster bottomC = null;
            topC = node.getLC();
            bottomC = node.getRC();

            Rectangle topR = ((RectWrapper) _hash.get(topC)).getRect();
            Rectangle bottomR = ((RectWrapper) _hash.get(bottomC)).getRect();
            newY = Math.round(topR.getY() - (topR.getHeight() / 2));
            newH =
               Math.round(newY - bottomR.getY() + (bottomR.getHeight() / 2));

            Color colval = null;

            double ratio = (newW / (_highvalue - _lowvalue));

            /*
             *       if (ratio <= .4){ colval = Color.red;      } else  if
             * (ratio <= .75) { colval = Color.orange;      } else { colval =
             * Color.yellow;      }
             */
            colval = new Color(0, 0, 0);

            // Color colval = new Color((int)Math.round(Math.random() * 255),
            // (int)Math.round(Math.random() * 255),
            // (int)Math.round(Math.random() * 255));
            RectWrapper rw =
               new RectWrapper(new Rectangle((int) newX, (int) newY, (int) newW,
                                             (int) newH), node, colval);
            rw.addLeaves(((RectWrapper) _hash.get(topC)).getLeaves());
            rw.addLeaves(((RectWrapper) _hash.get(bottomC)).getLeaves());

            _hash.put(node, rw);

         } // end if

      } // end if
   } // end method postOrderWalk


   /**
    * Called when the user manipulated the GUI components.
    *
    * @param evt ActionEvent An action event trigered by the user.
    */
   public void actionPerformed(ActionEvent evt) {

      if (evt.getSource() == m_reset) {
         this.resetPanel(_root);
      }

      if (evt.getSource() == m_cohesion) {

         if (m_cohesion.isSelected()) {
            ((RectGraph) m_rectpan).setDisplayMode(RectGraph.DisplayCohesion);
         }
      }

      if (evt.getSource() == m_size) {

         if (m_size.isSelected()) {
            ((RectGraph) m_rectpan).setDisplayMode(RectGraph.DisplayClusterSize);
         }
      }

      if (evt.getSource() == m_sizecolor) {

         if (m_sizecolor.isSelected()) {
            ((RectGraph) m_rectpan).setDisplayMode(RectGraph.DisplaySizeAsColor);
         }
      }

      if (evt.getSource() instanceof ColorMenuItem) {
         ColorMenuItem cmi = (ColorMenuItem) evt.getSource();
         String text = cmi.getText();
         Color oldColor = cmi.c;
         Color newColor = JColorChooser.showDialog(this,
                                                   "Choose", oldColor);

         if (newColor != null) {

            if (cmi == hiSelectedColor) {
               ImageIcon hi =
                  new ImageIcon(new ColorComponent(newColor).getImage());
               cmi.setIcon(hi);
               ((RectGraph) m_rectpan).setHighColor(newColor);

               // ma.updateImage();
               return;
            }

            if (cmi == lowSelectedColor) {
               ImageIcon low =
                  new ImageIcon(new ColorComponent(newColor).getImage());
               cmi.setIcon(low);
               ((RectGraph) m_rectpan).setLowColor(newColor);

               // ma.updateImage();
               return;
            }
         }
      } // end if

      if (evt.getSource() == helpItem) {
         helpWindow.setVisible(true);
      }
   } // end method actionPerformed

   /**
    * Initializes all the GUI components contained in <codE>_pan</code> to
    * display the data in the cluster tree that its root is <code>root.</code>
    *
    * @param  root TableClusterthe root node in a cluster tree
    *
    * @return JPanel A Panel holding the graphed visualization of the cluster
    *         tree.
    */
   public JPanel buildPanel(TableCluster root) {

      _pan = new JPanel();

      // call build rects
      buildRects(root);
      // instantiate the RectGraph and return it

      GraphSettings settings = new GraphSettings();
      settings.displayaxislabels = false;
      settings.displaygrid = false;
      settings.displaylegend = false;
      settings.displayscale = false;
      settings.displaytickmarks = false;
      settings.displaytitle = false;
      settings.title = "Dendogram Visualization";
      settings.xaxis = "Cluster Dissimilarity";
      settings.yaxis = "Clusters";

      GridBagLayout gbl = new GridBagLayout();
      GridBagConstraints gbc = new GridBagConstraints();

      _pan.setLayout(gbl);

      RectGraph oldrg = (RectGraph) m_rectpan;
      m_rectpan = new RectGraph(m_rects, m_leaves, settings, this);

      if (oldrg != null) {
         ((RectGraph) m_rectpan).setHighColor(oldrg.getHighColor());
         ((RectGraph) m_rectpan).setLowColor(oldrg.getLowColor());
      }

      m_scroll = new JScrollPane(m_rectpan);

      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.insets = new Insets(10, 10, 10, 10);
      gbc.weightx = 1;
      gbc.weighty = 1;
      gbc.fill = GridBagConstraints.BOTH;

      gbl.setConstraints(m_scroll, gbc);

      gbc.gridy = 1;
      gbc.weighty = 0;
      gbc.insets = new Insets(5, 5, 5, 5);

      JPanel buttpan = new JPanel();
      m_cohesion = new JRadioButton("Cohesion");
      m_cohesion.addActionListener(this);
      m_cohesion.setSelected(true);
      buttpan.add(m_cohesion);
      m_size = new JRadioButton("Size");
      m_size.addActionListener(this);
      buttpan.add(m_size);
      m_sizecolor = new JRadioButton("Size as Color");
      m_sizecolor.addActionListener(this);
      buttpan.add(m_sizecolor);

      ButtonGroup group = new ButtonGroup();
      group.add(m_cohesion);
      group.add(m_size);
      group.add(m_sizecolor);

      gbl.setConstraints(buttpan, gbc);

      gbc.gridy = 2;
      gbc.weighty = 0;
      gbc.insets = new Insets(5, 5, 5, 5);

      m_reset = new JButton("Reset");
      m_reset.addActionListener(this);
      gbl.setConstraints(m_reset, gbc);

      _pan.add(m_scroll);
      _pan.add(buttpan);
      _pan.add(m_reset);

      return _pan;

   } // end method buildPanel

   /**
    * Returns the menu bar.
    *
    * @return Object The menu bar component in this panel
    */
   public Object getMenu() { return menuBar; }

   /**
    * Called by the D2K Infrastructure to allow the view to perform
    * initialization tasks.
    *
    * @param module The module this view is associated with.
    */
   public void initView(ViewModule module) { }

   /**
    * Resets the panel displaying the graph to display the cluster tree that
    * starts with <code>root.</code>
    *
    * @param root TableCluster the root node in a cluster tree.
    */
   public void resetPanel(TableCluster root) {
      this.removeAll();
      add(buildPanel(root));
      this.revalidate();
      this.getParent().getParent().repaint();
   }

   /**
    * Called to pass the inputs received by the module to the view.
    *
    * @param  input The object that has been input.
    * @param  index The index of the module input that been received.
    *
    * @throws Exception if The expected input (a ClustModel) contains no
    *                   clusters or does not contain the cluster tree.
    */
   public void setInput(Object input, int index) throws Exception {

      if (index == 0) {

         _model = (ClusterModel) input;

         if (_model.getClusters() == null) {
            throw new Exception("DendogramPanel.setInput(...) -- Cluster model contains no clusters.");
         }

         if (_model.getRoot() == null) {
            throw new Exception("DendogramPanel.setInput(...) -- Cluster model does not contain a cluster tree.");
         }

         _clusters = _model.getClusters();
         _root = _model.getRoot();

         add(buildPanel(_root));

         JMenu chooseColors = new JMenu("Choose Colors");
         ImageIcon hi =
            new ImageIcon(new ColorComponent(Color.green).getImage());
         hiSelectedColor =
            new ColorMenuItem("Selected High Color", hi, Color.red);
         hiSelectedColor.addActionListener(this);
         chooseColors.add(hiSelectedColor);

         ImageIcon low =
            new ImageIcon(new ColorComponent(Color.red).getImage());
         lowSelectedColor =
            new ColorMenuItem("Selected Low Color", low,
                              Color.green);
         lowSelectedColor.addActionListener(this);
         chooseColors.add(lowSelectedColor);

         menuBar.add(chooseColors);

         JMenu helpMenu = new JMenu("Help");
         helpItem = new JMenuItem("About Dendogram Vis..");
         helpItem.addActionListener(this);
         helpMenu.add(helpItem);
         menuBar.add(helpMenu);

         this.setPreferredSize(new Dimension(640, 480));

      } else {
         return;
      }

   } // end method setInput

   //~ Inner Classes ***********************************************************

   /**
    * <p>Title: ColorComponent</p>
    *
    * <p>Description: A small square with a black outline. The color of the
    * square is given in the constructor.</p>
    *
    * <p>Copyright: Copyright (c) 2006</p>
    *
    * <p>Company:</p>
    *
    * @author  D. Searsmith
    * @version 1.0
    */
   private final class ColorComponent extends JComponent {

      /** Use serialVersionUID for interoperability. */
      static private final long serialVersionUID = 5148590434183123106L;

      /** Dimensions of width and height. */
      private final int DIM = 12;

      /** background color of this swatch. */
      Color bkgrd;

      /**
       * Instantiates this color component with color <codE>c.</codE>
       *
       * @param c Color the background of this component.
       */
      ColorComponent(Color c) {
         super();
         setOpaque(true);
         bkgrd = c;
      }

      /**
       * Returns this component as an image.
       *
       * @return BufferedImage the Image representation of this component
       */
      BufferedImage getImage() {
         BufferedImage image =
            new BufferedImage(DIM, DIM,
                              BufferedImage.TYPE_INT_RGB);
         Graphics g = image.getGraphics();
         paint(g);

         return image;
      }

      /**
       * Sets the background color of this component.
       *
       * @param c Color
       */
      void setBkgrd(Color c) { bkgrd = c; }

      /**
       * Returns the minimum size - defined by <codE>DIM.</code>
       *
       * @return Dimension a dimension object with the prefered minimum width
       *         and height.
       */
      public Dimension getMinimumSize() { return new Dimension(DIM, DIM); }

      /**
       * Returns the prefered size of this component. defined by <codE>
       * DIM</codE>
       *
       * @return Dimension a Dimension object witht he prefered width and height
       */
      public Dimension getPreferredSize() { return new Dimension(DIM, DIM); }

      /**
       * Renders this swatch.
       *
       * @param g Graphics a graphics handler
       */
      public void paint(Graphics g) {
         g.setColor(bkgrd);
         g.fillRect(0, 0, DIM - 1, DIM - 1);
         g.setColor(Color.black);
         g.drawRect(0, 0, DIM - 1, DIM - 1);
      }
   } // end class ColorComponent


   /**
    * <p>Title: ColorMenuItem</p>
    *
    * <p>Description: A menu item to select which color to use in rendering the
    * graph</p>
    *
    * <p>Copyright: Copyright (c) 2006</p>
    *
    * <p>Company:</p>
    *
    * @author  D. Searsmith
    * @version 1.0
    */
   private final class ColorMenuItem extends JMenuItem {

      /** Use serialVersionUID for interoperability. */
      static private final long serialVersionUID = -5559327120319779371L;
      Color c;

      ColorMenuItem(String s, Icon i, Color c) { super(s, i); }
   }

   /**
    * <p>Title: cRank_Comparator</p>
    *
    * <p>Description: Compares 2 TableCluster objects</p>
    *
    * <p>Copyright: Copyright (c) 2006</p>
    *
    * <p>Company:</p>
    *
    * @author  D. Searsmith
    * @version 1.0
    */
   private class cRank_Comparator implements java.util.Comparator {


      public cRank_Comparator() { }

      /**
       * Compares 2 TableCluster objects. The natural order of TableCluster
       * object is derived from the natural order of their children distance. If
       * children distance is identical, natural order is derived from the
       * natural order of the labels of the clusters.
       *
       * @param  tc1 TableCluster first table cluster to be compared
       * @param  tc2 TableCluster second table cluster to be compared
       *
       * @return int returns 1 if <code>tc1</code> is naturally greater than
       *         <code>tc2</code> -1 if <code>tc1</code> is naturally less than
       *         <code>tc2</code> or 0 if they are naturally equal.
       */
      public int compare(TableCluster tc1, TableCluster tc2) {

         if (eq(tc1.getChildDistance(), tc2.getChildDistance())) {

            if (tc1.getClusterLabel() > tc2.getClusterLabel()) {
               return 1;
            } else if (tc1.getClusterLabel() < tc2.getClusterLabel()) {
               return -1;
            } else {
               return 0;
            }
         } else if (tc1.getChildDistance() > tc2.getChildDistance()) {
            return 1;
         } else {
            return -1;
         }

      }

      /**
       * Compares 2 objects.
       *
       * @param  o1 Object supposed to be a <code>
       *            ncsa.d2k.modules.core.discovery.cluster.util.TableCluster</code>
       * @param  o2 Object supposed to be a <code>
       *            ncsa.d2k.modules.core.discovery.cluster.util.TableCluster</code>
       *
       * @return int 1 if <code>o1</codE> is greater than <code>o2</code>
       *
       * @throws ClassCastException If either <code>o1</code> or <code>o2</code>
       *                            are not an intance of <code>
       *                            ncsa.d2k.modules.core.discovery.cluster.util.TableCluster</code>.
       *
       * @see    compare(TableCluster, TableCluster)
       */
      public int compare(Object o1, Object o2) throws ClassCastException {

         if (!(o1 instanceof TableCluster)) {
            System.err.println("Cannot compare objects, o1 is not an instance of TableCluster!");
            throw new ClassCastException("Cannot cast " +
                                         o1.getClass().getName() +
                                         " into a TableCluster.");
         }

         if (!(o2 instanceof TableCluster)) {
            System.err.println("Cannot compare objects, o2 is not an instance of TableCluster!");
            throw new ClassCastException("Cannot cast " +
                                         o2.getClass().getName() +
                                         " into a TableCluster.");
         }

         return compare((TableCluster) o1, (TableCluster) o2);

      }

      public boolean eq(double a, double b) { return a == b; }

      /**
       * Interface implementation. Just verifies that <code>o</codE> equals this
       * comparator.
       *
       * @param  o An object to test if same as this comparator
       *
       * @return true if both are cRank_Comparator objects.
       */
      public boolean equals(Object o) { return o instanceof cRank_Comparator; }


   } // end class cRank_Comparator

   /**
    * <p>Title: HelpWindow</p>
    *
    * <p>Description: This window is displayed when the userclicks on the help
    * menu item.</p>
    *
    * <p>Copyright: Copyright (c) 2006</p>
    *
    * <p>Company:</p>
    *
    * @author  not attributable
    * @version 1.0
    */
   private final class HelpWindow extends JD2KFrame {

      /** Use serialVersionUID for interoperability. */
      static private final long serialVersionUID = -3701158547445964502L;

      HelpWindow() {
         super("About Dendogram Vis");

         JEditorPane jep = new JEditorPane("text/html", getHelpString());
         jep.setBackground(new Color(255, 255, 240));
         getContentPane().add(new JScrollPane(jep));
         setSize(400, 400);
      }
   }

} // end class DendogramPanel
