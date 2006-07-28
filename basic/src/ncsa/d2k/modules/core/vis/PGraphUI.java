package ncsa.d2k.modules.core.vis;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.algorithms.cluster.*;
import edu.uci.ics.jung.utils.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.contrib.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.vis.pgraph.nodes.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.vis.pgraph.*;

public class PGraphUI extends UIModule {

   private PGView pgview;

   public UserView createUserView() { return pgview = new PGView(); }
   public String[] getFieldNameMapping() { return null; }
   /**
 * Returns a description of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a description should be returned.
 *
 * @return <code>String</code> describing the input at the specified index.
 */
public String getInputInfo(int index) { return null; }

   /**
 * Returns the name of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the input at the specified index.
 */
public String getInputName(int index) {

      if (index == 0) {
         return "Graph";
      }
      else {
         return null;
      }

   }

   public String[] getInputTypes() {
      return new String[] {"edu.uci.ics.jung.graph.Graph"};
   }

   /**
* Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleName() {
	   return "PGraph UI";
   }
   /**
 * Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module implements a zooming user interface of a JUNG ");
      sb.append("graph. It provides some layout and clustering options as ");
      sb.append("well.</p>");
      sb.append("<p>Acknowledgement: ");
      sb.append("This module uses functionality from the JUNG project. See ");
      sb.append("http://jung.sourceforge.net.</p>");
      sb.append("<p>Acknowledgement: ");
      sb.append("This module uses functionality from the Piccolo project. See ");
      sb.append("http://www.cs.umd.edu/hcil/piccolo/.</p>");
      return sb.toString();
   }

   /**
 * Returns a description of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a description should be returned.
 *
 * @return <code>String</code> describing the output at the specified index.
 */
public String getOutputInfo(int index) { return null; }
   public String[] getOutputTypes() { return null; }

   /**
 * Called by the D2K Infrastructure after the itinerary completes execution.
 */
public void endExecution() {

      if (pgview != null && pgview.canvas != null) {
         pgview.canvas.stop();
      }

   }

   private static Insets emptyInsets = new Insets(0, 0, 0, 0);

   private int initWidth = 500, initHeight = 500;

   public int getInitWidth() {
      return initWidth;
   }

   public void setInitWidth(int value) {
      initWidth = value;
   }

   public int getInitHeight() {
      return initHeight;
   }

   public void setInitHeight(int value) {
      initHeight = value;
   }

   private class PGView extends JUserPane implements ActionListener,
      VertexSelectionListener, ZoomListener {

      private Graph graph;
      private Layout layout;
      PGraphCanvas canvas;

      private JFileChooser fileChooser;

      private JMenuBar menuBar;
      private JMenuItem loadItem, saveItem;
      private JMenuItem kkLayoutItem, frLayoutItem, circleLayoutItem,
         springLayoutItem, isomLayoutItem, dagLayoutItem;
      private JMenuItem edgeClustItem, weakClustItem, biClustItem;
      private JCheckBoxMenuItem aaOptItem, labelsOptItem, brushingOptItem,
         centerOnZoomOptItem;

      private AdvanceControl advanceControl;
      private ClusterControl clusterControl;

      private boolean aa_set = true, labels_set = true, brushing_set = false;
      private boolean czoom_set = false;

      public void actionPerformed(ActionEvent e) {

         Object source = e.getSource();

         boolean did_suspend;

         if (advanceControl.canvas_suspended) {
            did_suspend = false;
         }
         else {
            canvas.suspend();
            did_suspend = true;
         }

         if (source == loadItem) {

            Graph gg = null;
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
               try {
                  gg = PGraphML.load(fileChooser.getSelectedFile());
               }
               catch (IOException ioe) { ioe.printStackTrace(); return; }
            }
            else {
               return;
            }

            Layout ll = new AbstractLayout(gg) {

               public void advancePositions() { }
               public boolean incrementsAreDone() { return true; }
               public void initialize_local() { }
               public void initialize_local_vertex(Vertex v) { }
               public boolean isIncremental() { return false; }

            };
            ll.initialize(layout.getCurrentSize());

            Iterator vIter = gg.getVertices().iterator();
            Vertex v;
            double x, y;
            String xS, yS;
            while (vIter.hasNext()) {

               v = (Vertex)vIter.next();

               xS = (String)v.removeUserDatum("x");
               yS = (String)v.removeUserDatum("y");

               if (xS != null && yS != null) {
                  x = Double.parseDouble(xS);
                  y = Double.parseDouble(yS);
                  ll.forceMove(v, (int)x, (int)y);
               }

            }

            graph = gg;
            layout = ll;
            canvas.reset(gg, ll);
            clusterControl.clear();

         }
         else if (source == saveItem) {

            Iterator vIter = graph.getVertices().iterator();
            Vertex v;
            double x, y;
            while (vIter.hasNext()) {

               v = (Vertex)vIter.next();

               x = layout.getX(v);
               y = layout.getY(v);

               v.setUserDatum("x", new Double(x), UserData.SHARED);
               v.setUserDatum("y", new Double(y), UserData.SHARED);

            }

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
               try {
                  PGraphML.save(graph, fileChooser.getSelectedFile());
               }
               catch (IOException ioe) { ioe.printStackTrace(); }
            }

         }
         else if (source == aaOptItem) {

            aa_set = !aa_set;
            aaOptItem.setSelected(aa_set);
            canvas.setAntialiasing(aa_set);

         }
         else if (source == labelsOptItem) {

            labels_set = !labels_set;
            labelsOptItem.setSelected(labels_set);
            if(! brushing_set)
            {
              canvas.setLabelsVisible(labels_set);
            }
         }
         else if (source == centerOnZoomOptItem) {

            czoom_set = !czoom_set;
            centerOnZoomOptItem.setSelected(czoom_set);

         }
         else if(source == brushingOptItem)
         {
           brushing_set = !brushing_set;
           brushingOptItem.setSelected(brushing_set);
           canvas.setBrushing(brushing_set);
           if(brushing_set)
           {
             canvas.setLabelsVisible(false);
           }
           else
           {
             labelsOptItem.setSelected(labels_set);
             canvas.setLabelsVisible(labels_set);
           }
         }
         else if (source == kkLayoutItem) {

            Layout kkLayout = new KKLayout(graph);
            canvas.setLayout(kkLayout);
            layout = kkLayout;

         }
         else if (source == frLayoutItem) {

            Layout frLayout = new FRLayout(graph);
            canvas.setLayout(frLayout);
            layout = frLayout;

         }
         else if (source == circleLayoutItem) {

            Layout circleLayout = new CircleLayout(graph);
            canvas.setLayout(circleLayout);
            layout = circleLayout;

         }
         else if (source == springLayoutItem) {

            Layout springLayout = new
               edu.uci.ics.jung.visualization.SpringLayout(graph);
            canvas.setLayout(springLayout);
            layout = springLayout;

         }
         else if (source == isomLayoutItem) {

            Layout isomLayout = new ISOMLayout(graph);
            canvas.setLayout(isomLayout);
            layout = isomLayout;

         }
         else if (source == dagLayoutItem) {

            Layout dagLayout = new DAGLayout(graph);
            canvas.setLayout(dagLayout);
            layout = dagLayout;

         }
         else if (source == edgeClustItem) {

            clusterControl.cluster(new EdgeBetweennessClusterer(0));
            canvas.revalidate();
            canvas.repaint();

         }
         else if (source == weakClustItem) {

            clusterControl.cluster(new WeakComponentClusterer());
            canvas.revalidate();
            canvas.repaint();

         }
         else if (source == biClustItem) {

            clusterControl.cluster(new BicomponentClusterer());
            canvas.revalidate();
            canvas.repaint();

         }

         if (did_suspend) {
            canvas.unsuspend();
         }

      }

      private void createMenuBar() {

         menuBar = new JMenuBar();

         JMenu fileMenu = new JMenu("File");
         loadItem = new JMenuItem("Load...");
         loadItem.addActionListener(this);
         fileMenu.add(loadItem);
         saveItem = new JMenuItem("Save...");
         saveItem.addActionListener(this);
         fileMenu.add(saveItem);
         menuBar.add(fileMenu);

         JMenu optMenu = new JMenu("Options");
         aaOptItem = new JCheckBoxMenuItem("Anti-aliasing", aa_set);
         aaOptItem.addActionListener(this);
         labelsOptItem = new JCheckBoxMenuItem("Show labels", labels_set);
         labelsOptItem.addActionListener(this);
         brushingOptItem = new JCheckBoxMenuItem("Use brushing to show labels", brushing_set);
         brushingOptItem.addActionListener(this);
         centerOnZoomOptItem = new JCheckBoxMenuItem("Center on zoom", czoom_set);
         centerOnZoomOptItem.addActionListener(this);
         optMenu.add(aaOptItem);
         optMenu.add(brushingOptItem);
         optMenu.add(labelsOptItem);
         optMenu.add(centerOnZoomOptItem);
         menuBar.add(optMenu);

         JMenu layoutMenu = new JMenu("Layout");
         kkLayoutItem = new JMenuItem("KK Layout");
         kkLayoutItem.addActionListener(this);
         layoutMenu.add(kkLayoutItem);
         frLayoutItem = new JMenuItem("FR Layout");
         frLayoutItem.addActionListener(this);
         layoutMenu.add(frLayoutItem);
         circleLayoutItem = new JMenuItem("Circle Layout");
         circleLayoutItem.addActionListener(this);
         layoutMenu.add(circleLayoutItem);
         springLayoutItem = new JMenuItem("Spring Layout");
         springLayoutItem.addActionListener(this);
         layoutMenu.add(springLayoutItem);
         isomLayoutItem = new JMenuItem("ISOM Layout");
         isomLayoutItem.addActionListener(this);
         layoutMenu.add(isomLayoutItem);
         dagLayoutItem = new JMenuItem("DAG Layout");
         dagLayoutItem.addActionListener(this);
         layoutMenu.add(dagLayoutItem);

         menuBar.add(layoutMenu);

         JMenu clusterMenu = new JMenu("Cluster");
         edgeClustItem = new JMenuItem("By Edge Betweenness");
         edgeClustItem.addActionListener(this);
         clusterMenu.add(edgeClustItem);
         weakClustItem = new JMenuItem("By Weak Component");
         weakClustItem.addActionListener(this);
         clusterMenu.add(weakClustItem);
         biClustItem = new JMenuItem("By Bicomponent");
         biClustItem.addActionListener(this);
         // clusterMenu.add(biClustItem);
         menuBar.add(clusterMenu);

      }

      public Object getMenu() {

         if (menuBar == null) {
            createMenuBar();
         }

         return menuBar;

      }

      public void graphZoomed() {

         if (czoom_set) {
            canvas.reinitializeAndCenter();
         }

      }

      /**
 * Called by the D2K Infrastructure to allow the view to perform initialization tasks.
 *
 * @param module The module this view is associated with.
 */
public void initView(ViewModule vm) { }

      /**
 * Called to pass the inputs received by the module to the view.
 *
 * @param input The object that has been input.
 * @param index The index of the module input that been received.
 */
public void setInput(Object o, int i) {

         if (i != 0) {
            return;
         }

         graph = (Graph)o;
         layout = new FRLayout(graph);
         layout.initialize(new Dimension(initWidth, initHeight));

         canvas = new PGraphCanvas(graph, layout);
         canvas.addVertexSelectionListener(this);
         canvas.addZoomListener(this);
         fileChooser = new JFileChooser();

         JPanel controls = new JPanel();
         advanceControl = new AdvanceControl(canvas);
         JSeparator sep1 = new JSeparator();
         clusterControl = new ClusterControl(canvas);
         GridBagLayout clayout = new GridBagLayout();
         controls.setLayout(clayout);
         clayout.addLayoutComponent(advanceControl, new GridBagConstraints(
            0, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         clayout.addLayoutComponent(sep1, new GridBagConstraints(
            0, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         clayout.addLayoutComponent(clusterControl, new GridBagConstraints(
            0, 2, 1, 1, 1.0, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            emptyInsets, 0, 0));
         controls.add(advanceControl);
         controls.add(sep1);
         controls.add(clusterControl);

         removeAll();
         createMenuBar();

         GridBagLayout glayout = new GridBagLayout();
         setLayout(glayout);

         glayout.addLayoutComponent(canvas, new GridBagConstraints(
            0, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            emptyInsets, 0, 0));
         glayout.addLayoutComponent(controls, new GridBagConstraints(
            1, 0, 1, 1, 0.0, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
            emptyInsets, 0, 0));

         add(canvas);
         add(controls);

      }

      public void vertexSelected(Vertex v) {

         boolean did_suspend;

         if (advanceControl.canvas_suspended) {
            did_suspend = false;
         }
         else {
            canvas.suspend();
            did_suspend = true;
         }

         clusterControl.highlightFromVertex(v);

         if (did_suspend) {
            canvas.unsuspend();
         }

      }

   }

   private class AdvanceControl extends JPanel implements ActionListener,
      ChangeListener {

      private PGraphCanvas canvas;

      private JButton advanceButton, pauseButton;
      private JSlider advanceSlider;

      boolean canvas_suspended = true;

      AdvanceControl(PGraphCanvas canvas) {

         super();

         this.canvas = canvas;

         advanceSlider = new JSlider(50, 1000, 800);
         advanceSlider.addChangeListener(this);
         advanceSlider.setInverted(true);
         advanceSlider.setBorder(new TitledBorder("Advance speed: "));

         advanceButton = new JButton("Advance");
         advanceButton.addActionListener(this);
         pauseButton = new JButton("Pause");
         pauseButton.addActionListener(this);
         pauseButton.setEnabled(false);

         GridBagLayout layout = new GridBagLayout();
         setLayout(layout);

         layout.addLayoutComponent(advanceButton, new GridBagConstraints(
            0, 0, 1, 1, 0.5, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         layout.addLayoutComponent(pauseButton, new GridBagConstraints(
            1, 0, 1, 1, 0.5, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         layout.addLayoutComponent(advanceSlider, new GridBagConstraints(
            0, 1, 2, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));

         add(advanceButton);
         add(pauseButton);
         add(advanceSlider);

      }

      public void actionPerformed(ActionEvent e) {

         Object source = e.getSource();

         if (source == advanceButton) {

            advanceButton.setEnabled(false);
            canvas.unsuspend();
            canvas_suspended = false;
            pauseButton.setEnabled(true);

         }
         else if (source == pauseButton) {

            pauseButton.setEnabled(false);
            canvas.suspend();
            canvas_suspended = true;
            advanceButton.setEnabled(true);

         }

      }

      public void stateChanged(ChangeEvent e) {

         canvas.setAdvanceSleep((long)advanceSlider.getValue());

      }

   }

   private class ClusterControl extends JPanel implements ChangeListener {

      private PGraphCanvas canvas;

      private JSlider clusterSlider;
      private JTextArea messageArea;

      private ClusterSet clusterSet;
      private int current_cluster = -1;
      private Color savedColor;

      ClusterControl(PGraphCanvas canvas) {

         super();
         setBorder(new TitledBorder("View cluster: "));

         this.canvas = canvas;

         clusterSlider = new JSlider(0, 0);
         clusterSlider.setMajorTickSpacing(10);
         clusterSlider.setPaintLabels(true);
         clusterSlider.setPaintTicks(true);
         clusterSlider.addChangeListener(this);

         messageArea = new JTextArea();
         messageArea.setEditable(false);
         JScrollPane messageScroll = new JScrollPane(messageArea) {
            private Dimension size = new Dimension(50, 100);
            public Dimension getMinimumSize() { return size; }
            public Dimension getPreferredSize() { return size; }
         };

         GridBagLayout layout = new GridBagLayout();
         setLayout(layout);

         layout.addLayoutComponent(clusterSlider, new GridBagConstraints(
            0, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         layout.addLayoutComponent(messageScroll, new GridBagConstraints(
            0, 1, 1, 1, 1.0, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            emptyInsets, 0, 0));

         add(clusterSlider);
         add(messageScroll);

      }

      void clear() {

         clusterSet = null;
         current_cluster = -1;
         messageArea.setText("");
         clusterSlider.setMaximum(0);
         clusterSlider.setValue(0);

      }

      void cluster(GraphClusterer clusterer) {

         current_cluster = -1;
         clusterSlider.setValue(0);

         Graph graph = canvas.getGraphLayout().getGraph();
         clusterSet = clusterer.extract(graph);
         clusterSet.sort();

         int num_clusters = clusterSet.size();
         clusterSlider.setMaximum(num_clusters - 1);

         int c_min_size = clusterSet.getCluster(0).size(),
             c_max_size = clusterSet.getCluster(num_clusters - 1).size();

         Iterator cIter = clusterSet.iterator(), vIter;
         Set cluster;
         Vertex v;
         Color c;
         while (cIter.hasNext()) {

            cluster = (Set)cIter.next();

            c = interpolateColor(Color.blue, Color.red, (double)(cluster.size()
               - c_min_size) / (double)(c_max_size - c_min_size));

            vIter = cluster.iterator();
            while (vIter.hasNext()) {

               v = (Vertex)vIter.next();
               v.setUserDatum(PVertexNode.COLOR, c, UserData.SHARED);
               canvas.setVertexColor(v, c);

            }

         }

         stateChanged(null);

      }

      void highlightFromVertex(Vertex v) {

         if (clusterSet != null && clusterSet.size() > 0) {

            Iterator csIter = clusterSet.iterator(), vIter;
            Set vertices;
            Color c;
            Vertex nv;
            int index = 0;

            while (csIter.hasNext()) {

               vertices = (Set)csIter.next();

               if (vertices.contains(v)) {

                  clusterSlider.setValue(index);

                  /*
                  int c_min_size = clusterSet.getCluster(0).size(),
                      c_max_size = clusterSet.getCluster(clusterSet.size() - 1).size();

                  c = interpolateColor(Color.blue, Color.red, (double)(vertices.size()
                      - c_min_size) / (double)(c_max_size - c_min_size));

                  vIter = vertices.iterator();
                  while (vIter.hasNext()) {

                     nv = (Vertex)vIter.next();
                     nv.setUserDatum(PVertexNode.COLOR, c, UserData.SHARED);
                     canvas.setVertexColor(nv, c);

                  }
                  */

                  break;

               }

               index++;

            }

         }

         stateChanged(null);

      }

      private Color interpolateColor(Color low, Color high, double pct) {

         int red = low.getRed() +
                   (int)(pct * (double)(high.getRed() - low.getRed())),
             blu = low.getBlue() +
                   (int)(pct * (double)(high.getBlue() - low.getBlue())),
             grn = low.getGreen() +
                   (int)(pct * (double)(high.getGreen() - low.getGreen()));

         return new Color(red, grn, blu);

      }

      public void stateChanged(ChangeEvent e) {

         if (clusterSet != null && clusterSet.size() > 0) {

            if (current_cluster != -1 && savedColor != null) {

               Set oldCluster = clusterSet.getCluster(current_cluster);
               Iterator vIter = oldCluster.iterator();
               Vertex v;
               while (vIter.hasNext()) {

                  v = (Vertex)vIter.next();
                  canvas.setVertexColor(v, savedColor);

               }

            }

            current_cluster = clusterSlider.getValue();
            Set cluster = clusterSet.getCluster(current_cluster);
            savedColor = (Color)((Vertex)cluster.iterator().next()).
               getUserDatum(PVertexNode.COLOR);

            messageArea.setText("Cluster: " + current_cluster + "\n");
            messageArea.append("Size: " + cluster.size() + "\n\n");

            Iterator vIter = cluster.iterator();
            Vertex v;
            while (vIter.hasNext()) {

               v = (Vertex)vIter.next();
               canvas.setVertexColor(v, Color.YELLOW);

               messageArea.append((String)v.getUserDatum(PVertexNode.LABEL));
               messageArea.append("\n");

            }

            messageArea.setCaretPosition(0);

            canvas.repaint();

         }
         else {
            System.out.println("Please run a cluster algorithm first.");
         }

      }

   }
   
   /**
 * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects for each property of the module.
 *
 * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects.
 */
public PropertyDescription[] getPropertiesDescriptions() {
	    PropertyDescription[] pds = new PropertyDescription[2];

	    pds[0] = new PropertyDescription("initWidth", "Initial width",
	                                     "Specifies the initial width of the canvas.");

	    pds[1] = new PropertyDescription("initHeight", "Initial height",
	                                     "Specifies the initial height of the canvas.");

	    return pds;
	  }

}
