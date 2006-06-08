package ncsa.d2k.modules.core.vis;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.graph.*;
// import ncsa.d2k.modules.projects.maids.graph.io.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.Constrain;
import prefuse.*;
import prefuse.action.*;
import prefuse.action.Action;
import prefuse.action.assignment.*;
import prefuse.action.layout.graph.*;
import prefuse.controls.*;
import prefuse.data.*;
import prefuse.render.*;
import prefuse.render.Renderer;
import prefuse.util.*;
import prefuse.util.force.*;
import prefuse.visual.*;
import prefuse.visual.sort.*;

/**
 * three types of vis:
 *  x weighted
 *    - colors are a gradient (default: from cool color to warm color)
 *  x unweighted
 *    - colors are uniform (default: mellow)
 *  x partition (formerly binary)
 *    - two colors: a node is of one type or the other
 *
 * new features:
 *  x size reset
 *  x advanced neighborhood highlight control
 *  - more intelligent physics mechanisms
 */
public class PrefuseGraphUI extends UIModule {

  private String displayedLabel;
  private RendererFactory b;

  private static final String NLN = "\n";
  private static final String TAB = "\t";

  private static final int VIS_TYPE_MIN = 0;
  private static final int VIS_TYPE_MAX = 3;

  public static final int VIS_TYPE_UNWEIGHTED = 0;
  public static final int VIS_TYPE_WEIGHTED   = 1;
  public static final int VIS_TYPE_PARTITION  = 2;
  public static final int VIS_TYPE_BY_ATT     = 3;

  public static final Color[] distinctColors = new Color[] {
    new Color( 29, 111, 117), // {  0,  83,   0},
    new Color(  0, 255,   0),
    new Color(255, 175, 185), // { 46, 155,  28},
    new Color(152, 255, 152),
    new Color(  0,  83,   0), // { 29, 111, 117},
    new Color( 72, 209, 204),
    new Color(178, 143,  86),
    new Color(142, 107,  35),
    new Color(255, 138,   0),
    new Color(254, 197,  68),
    new Color(255, 255,   0),
    new Color(136,  18,  13),
    new Color(103,   7,  72),
    new Color(255,   0,   0),
    new Color(218, 107, 212),
    new Color(199,  21, 133),
    new Color(114,  33, 188),
    new Color( 55, 121, 153),
    new Color( 12,  62,  99),
    new Color(  0,   0, 255),
    new Color(171, 197, 255)
  };

  private class PrefuseView extends JUserPane implements ActionListener {

    private static final String graphHolder = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";
    private AdaptiveTextItemRenderer nodeRender;
    private Display display;
    private Graph graph;
    private Visualization registry;

    private ActionList actionList;
    private Action forceAction;

    private int maxEdgeWeight;
    private int maxNodeWeight;

    private JFileChooser fileChooser;
    private JMenuBar menuBar;
    private JMenuItem fileLoadPositionItem;
    private JMenuItem fileSavePositionItem;
    private JMenuItem viewResetItem;
    private JCheckBoxMenuItem viewLegendItem;
    private JCheckBoxMenuItem viewAAItem;
    private JCheckBoxMenuItem viewEdgeLabelsItem;
    private JCheckBoxMenuItem viewPhysicsItem;
    private JRadioButtonMenuItem viewCompWeightedItem;
    private JRadioButtonMenuItem viewCompPart0Item;
    private JRadioButtonMenuItem viewCompPart1Item;
    private JRadioButtonMenuItem modeUnweightedItem;
    private JRadioButtonMenuItem modeWeightedItem;
    private JRadioButtonMenuItem modePartItem;
    private JRadioButtonMenuItem modeByAttItem;
    private JMenuItem setNodeLabelField;
    private JMenuItem setEdgeLabelField;
    private JMenuItem saveDisplayAsImage;
    private JMenuItem setNodeColorField;

    private JButton cancelButton, doneButton;

    private String nodeColorAtt;
    private HashMap nodeColorAttMap;
    private Legend legend;
    private JFrame legendFrame;

    private LabeledEdgeRenderer edgeRenderer;

    public void actionPerformed(ActionEvent ae) {

      Object source = ae.getSource();

      if (source == fileLoadPositionItem) {

        if (fileChooser == null) {
          fileChooser = new JFileChooser();
        }

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
          return;
        }

        readPositions(fileChooser.getSelectedFile());

      }
      else if (source == fileSavePositionItem) {

        if (fileChooser == null) {
          fileChooser = new JFileChooser();
        }

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
          return;
        }

        try {

          File f = fileChooser.getSelectedFile();
          BufferedWriter w = new BufferedWriter(new FileWriter(f));

          //Iterator nodeIterator = registry.getNodeItems();
          Iterator nodeIterator = registry.items(nodes);
          while (nodeIterator.hasNext()) {


            NodeItem node = (NodeItem)nodeIterator.next();
            w.write(node.getString(GraphUtils.LABEL));
            w.write(TAB);
            w.write(Double.toString(node.getX()));
            w.write(TAB);
            w.write(Double.toString(node.getY()));
            w.write(NLN);
          }

          w.flush();
          w.close();

          //registry.run("layout");

        }
        catch (IOException ioe) { ioe.printStackTrace(); }

      }
      else if (source == viewResetItem) {
        resetView();
      }
      else if (source == viewLegendItem) {
        legendFrame.setVisible(viewLegendItem.isSelected());
      }
      else if (source == viewAAItem) {
        display.setHighQuality(viewAAItem.isSelected());
      }
      else if (source == viewEdgeLabelsItem) {
        edgeRenderer.toggle();
      }
      else if (source == viewPhysicsItem) {

        if (viewPhysicsItem.isSelected()) {
          forceAction.setEnabled(true);
        }
        else {
          forceAction.setEnabled(false);
        }

      }
      else if (source == viewCompWeightedItem) {
        //registry.setItemComparator(weightedComparator);
      }
      else if (source == viewCompPart0Item) {
        //registry.setItemComparator(binaryComparator0);
      }
      else if (source == viewCompPart1Item) {
        //registry.setItemComparator(binaryComparator1);
      }
      else if (source == modeUnweightedItem) {
        setVisType(VIS_TYPE_UNWEIGHTED);
      }
      else if (source == modeWeightedItem) {
        setVisType(VIS_TYPE_WEIGHTED);
      }
      else if (source == modePartItem) {
        setVisType(VIS_TYPE_PARTITION);
      }
      else if (source == modeByAttItem) {
        setVisType(VIS_TYPE_BY_ATT);
      }

      else if (source == setNodeLabelField)
      {

      	Table ntbl = graph.getNodeTable();
      	int numCols = ntbl.getColumnCount();
      	String[] choices = new String[numCols];
      	for (int i = 0; i < numCols; i++)
      	{
      		choices[i] = ntbl.getColumnName(i);
      	}

      	String nodeLabel = (String) JOptionPane.showInputDialog(
      			this, "Choose a table field to use as labels for " +
      				"the nodes.", "Node Label Chooser",
					JOptionPane.PLAIN_MESSAGE,
					null,
					choices,
					choices[0]);

      	//System.out.println(nodeLabel);

      	if(nodeLabel == null)
      	{

      	}

      	else
      	{

      		Iterator nodeIterator = registry.items(nodes);
      		while (nodeIterator.hasNext()) {
      			NodeItem ni = (NodeItem)nodeIterator.next();
      			AdaptiveTextItemRenderer r = (AdaptiveTextItemRenderer)ni.getRenderer();
      			r.setTextField(nodeLabel);
      			//r.render(Renderer.DEFAULT_GRAPHICS, ni);
      			registry.repaint();

      		}

      		registry.repaint();
      		//System.out.println("All done with Action performed");
      	}

      }

      else if (source == setEdgeLabelField)
      {
      	Table etbl = graph.getEdgeTable();
      	int numCols = etbl.getColumnCount();
      	String labels[] = new String[numCols-2];
      	for (int i = 2; i < numCols; i++)
      	{
      		labels[i-2] = etbl.getColumnName(i);
      	}

      	String edgeLabel = (String) JOptionPane.showInputDialog(
      			this, "Choose a table field to use as labels for " +
      				"the edges.", "Edge Label Chooser",
					JOptionPane.PLAIN_MESSAGE,
					null,
					labels,
					labels[0]);

      	if (edgeLabel == null)
      	{

      	}

      	else
      	{
      		Iterator edgeIterator = registry.items(edges);
      		while (edgeIterator.hasNext())
      		{
      			EdgeItem e = (EdgeItem) edgeIterator.next();
      			LabeledEdgeRenderer rend =
      				(LabeledEdgeRenderer)e.getRenderer();
      			rend.setLabel(edgeLabel);
      			//rend.render(Renderer.DEFAULT_GRAPHICS,e);
      			registry.repaint();

      		}
      		registry.repaint();
      	}
      }

      // DC added node color field dialog
      else if(source == setNodeColorField) {
      	Table ntbl = graph.getNodeTable();
      	int numCols = ntbl.getColumnCount();
      	String[] choices = new String[numCols];
      	for (int i = 0; i < numCols; i++)
      	{
      		choices[i] = ntbl.getColumnName(i);
      	}

      	nodeColorAtt = (String) JOptionPane.showInputDialog(
      			this, "Choose a table field to use as colors for " +
      				"the nodes.", "Node Color Chooser",
					JOptionPane.PLAIN_MESSAGE,
					null,
					choices,
					choices[0]);

        if (nodeColorAttMap == null) {
          nodeColorAttMap = new HashMap();
        }
        else {
          nodeColorAttMap.clear();
        }

        Iterator nIter = graph.nodes();
        while (nIter.hasNext()) {

          Node n = (Node)nIter.next();
          String attVal = n.getString(nodeColorAtt);

          if (!nodeColorAttMap.containsKey(attVal)) {
            nodeColorAttMap.put(
                attVal,
                distinctColors[nodeColorAttMap.size() % distinctColors.length]
            );
          }

        }

        legend.updateLegend(nodeColorAttMap);

        modeByAttItem.setEnabled(true);
        viewLegendItem.setEnabled(true);

      }

      else if(source == saveDisplayAsImage)
      {
      	if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try
		{
        	File imageFile = fileChooser.getSelectedFile();

                String filename = imageFile.getAbsolutePath();
                if(!filename.endsWith(".jpg")) {
                  filename = filename+".jpg";
                }

        	FileOutputStream os = new FileOutputStream(filename);
        	boolean test;
        	test = display.saveImage(os, "JPG", 1.0);
        	if(test)
        	{

        		os.flush();
			os.close();
        	}

		}

        catch(Exception e)
		{
                 e.printStackTrace();
		}
      }
      else if (source == cancelButton) {
        legendFrame.dispose();
        viewCancel();
      }
      else if (source == doneButton) {
        legendFrame.dispose();
        viewDone("Done.");
      }


    }

    private void calculateMaxWeights() {

      // edge max

      maxEdgeWeight = 0;

      Iterator edgeIterator = graph.edges();
      int edgeIteratorCount = graph.getEdgeCount();
      while (edgeIteratorCount-- > 0) {
        Edge edge = (Edge)edgeIterator.next();
        int weight = 1;
        if(edge.canGet(GraphUtils.WEIGHT, int.class)) {
            weight = edge.getInt(GraphUtils.WEIGHT);
        }
        //int weight = Integer.parseInt(att);
        if (weight > maxEdgeWeight) {
          maxEdgeWeight = weight;
        }
      }

      // node max

      maxNodeWeight = 0;

      Iterator nodeIterator = graph.nodes();
      int nodeIteratorCount = graph.getNodeCount();
      while (nodeIteratorCount-- > 0) {
        Node node = (Node)nodeIterator.next();
        int weight = 1;
        if(node.canGet(GraphUtils.WEIGHT, int.class)) {
           weight = node.getInt(GraphUtils.WEIGHT);
        }
        ///int weight = Integer.parseInt(att);
        if (weight > maxNodeWeight) {
          maxNodeWeight = weight;
        }

      }

    }

    public void createMenuBar() {

      menuBar = new JMenuBar();

      JMenu fileMenu = new JMenu("File");
      menuBar.add(fileMenu);

      fileLoadPositionItem = new JMenuItem("Load positions...");
      fileLoadPositionItem.addActionListener(this);
      fileMenu.add(fileLoadPositionItem);

      fileSavePositionItem = new JMenuItem("Save positions...");
      fileSavePositionItem.addActionListener(this);
      fileMenu.add(fileSavePositionItem);

      saveDisplayAsImage = new JMenuItem("Save as Image...");
      saveDisplayAsImage.addActionListener(this);
      fileMenu.add(saveDisplayAsImage);

      JMenu viewMenu = new JMenu("View");
      menuBar.add(viewMenu);

      viewResetItem = new JMenuItem("Reset");
      viewResetItem.addActionListener(this);
      viewMenu.add(viewResetItem);

      viewLegendItem = new JCheckBoxMenuItem("Show Attribute Color Legend", false);
      viewLegendItem.addActionListener(this);
      viewLegendItem.setEnabled(false);
      viewMenu.add(viewLegendItem);

      viewMenu.add(new JSeparator());

      /*
      JMenu viewCompMenu = new JMenu("Stack by");
      viewMenu.add(viewCompMenu);

      viewCompWeightedItem = new JRadioButtonMenuItem("Weight", true);
      viewCompWeightedItem.addActionListener(this);
      viewCompMenu.add(viewCompWeightedItem);

      if (graph.getNodeTable().canGet(GraphUtils.PARTITION, String.class)) {

        viewCompPart0Item = new JRadioButtonMenuItem("Partition (0)", false);
        viewCompPart0Item.addActionListener(this);
        viewCompMenu.add(viewCompPart0Item);

        viewCompPart1Item = new JRadioButtonMenuItem("Partition (1)", false);
        viewCompPart1Item.addActionListener(this);
        viewCompMenu.add(viewCompPart1Item);

      }

      ButtonGroup viewCompGroup = new ButtonGroup();
      viewCompGroup.add(viewCompWeightedItem);
      viewCompGroup.add(viewCompPart0Item);
      viewCompGroup.add(viewCompPart1Item);
      */

      viewAAItem = new JCheckBoxMenuItem("Antialiasing", false);
      viewAAItem.addActionListener(this);
      viewMenu.add(viewAAItem);

      viewEdgeLabelsItem = new JCheckBoxMenuItem("Edge Labels", false);
      viewEdgeLabelsItem.addActionListener(this);
      viewMenu.add(viewEdgeLabelsItem);

      viewPhysicsItem = new JCheckBoxMenuItem("Physics", true);
      viewPhysicsItem.addActionListener(this);
      viewMenu.add(viewPhysicsItem);

      JMenu modeMenu = new JMenu("Mode");
      menuBar.add(modeMenu);

      modeUnweightedItem = new JRadioButtonMenuItem("Unweighted",
        visType == VIS_TYPE_UNWEIGHTED);
      modeUnweightedItem.addActionListener(this);
      modeMenu.add(modeUnweightedItem);

      modeWeightedItem = new JRadioButtonMenuItem("Weighted",
        visType == VIS_TYPE_WEIGHTED);
      modeWeightedItem.addActionListener(this);
      modeMenu.add(modeWeightedItem);

      if (graph.getNodeTable().canGet(GraphUtils.PARTITION, String.class)) {

        modePartItem = new JRadioButtonMenuItem("Partition",
            visType == VIS_TYPE_PARTITION);
        modePartItem.addActionListener(this);
        modeMenu.add(modePartItem);

      }

      modeByAttItem = new JRadioButtonMenuItem("By Attribute",
          visType == VIS_TYPE_BY_ATT);
      modeByAttItem.addActionListener(this);
      modeByAttItem.setEnabled(false);
      modeMenu.add(modeByAttItem);

      ButtonGroup modeGroup = new ButtonGroup();
      modeGroup.add(modeUnweightedItem);
      modeGroup.add(modeWeightedItem);
      modeGroup.add(modePartItem);
      modeGroup.add(modeByAttItem);

      JMenu labelsMenu = new JMenu("Labels");
      menuBar.add(labelsMenu);

      setNodeLabelField = new JMenuItem("Set Node Label Field...");
      setNodeLabelField.addActionListener(this);
      labelsMenu.add(setNodeLabelField);

      setEdgeLabelField = new JMenuItem("Set Edge Label Field...");
      setEdgeLabelField.addActionListener(this);
      labelsMenu.add(setEdgeLabelField);

        setNodeColorField = new JMenuItem("Set Node Color Field...");
        setNodeColorField.addActionListener(this);
        labelsMenu.add(setNodeColorField);
    }

    public Object getMenu() {
      return menuBar;
    }

    /*private void initForceAction() {
      ForceSimulator initialForce = new ForceSimulator();
      initialForce.addForce(new NBodyForce(-0.4f, -1.0f, -0.9f));
      initialForce.addForce(new SpringForce(4E-5f, 75f));
      initialForce.addForce(new DragForce(-0.005F));
      forceAction = new ForceDirectedLayout("graph", initialForce, false, false);
    }*/

    public void initView(ViewModule vm) { }

    private void readPositions(File f) {

      try {

        BufferedReader r = new BufferedReader(new FileReader(f));

        HashMap posMap = new HashMap();
        String line = null;
        while ((line = r.readLine()) != null && line.length() > 0) {
          String[] split = line.split(TAB);
          posMap.put(split[0], split);
        }

        r.close();

        boolean action_status = forceAction.isEnabled();
        forceAction.setEnabled(false);

        //Iterator nodeIterator = registry.getNodeItems();
        Iterator nodeIterator = registry.items(nodes);
        while (nodeIterator.hasNext()) {

          NodeItem node = (NodeItem)nodeIterator.next();
          String label = node.getString(GraphUtils.LABEL);
          String[] split = (String[])posMap.get(label);

          if (split == null) {
            continue;
          }

          node.setEndX(Double.parseDouble(split[1]));
          node.setEndY(Double.parseDouble(split[2]));

        }

        //actionList.remove(forceAction);
        //initForceAction();
        //forceAction.setEnabled(action_status);
        //actionList.add(new ForceDirectedLayout("graph"));
        //forceAction.run(registry, 0);
        //forceAction.run(0);
        //forceAction.run();
        resetView();
        forceAction.setEnabled(action_status);
        //registry.run("layout");
        //actionList.add(forceAction);

      }
      catch (IOException ioe) { ioe.printStackTrace(); }

    }

    private void resetView() {

      // need to find {min, max} {x, y}

      double min_x = Double.MAX_VALUE;
      double min_y = Double.MAX_VALUE;
      double max_x = Double.MIN_VALUE;
      double max_y = Double.MIN_VALUE;

      //Iterator nodeIterator = registry.getNodeItems();
      Iterator nodeIterator = registry.items(nodes);
      while (nodeIterator.hasNext()) {

        NodeItem node = (NodeItem)nodeIterator.next();
        double x = node.getX();
        double y = node.getY();
        if (x < min_x) {
          min_x = x;
        }
        if (y < min_y) {
          min_y = y;
        }
        if (x > max_x) {
          max_x = x;
        }
        if (y > max_y) {
          max_y = y;
        }
      }

      double x = (max_x - min_x)/(double)display.getWidth();
      double y = (max_y - min_y)/(double)display.getHeight();

      double scale;
      if (x > y) {
        if (max_x - min_x > display.getWidth()) {
          scale = 1d/x;

        }
        else {
          scale = x;
        }
      }
      else {
        if (max_y - min_y > display.getHeight()) {
          scale = 1d/y;

        }
        else {
          scale = y;
        }
      }

      try {
        display.setTransform(new AffineTransform());
      }
      catch(NoninvertibleTransformException nte) {
        nte.printStackTrace();
      }
      display.panToAbs(new Point2D.Double(0, 0));
      display.zoomAbs(new Point2D.Double(0, 0), 0.85*scale);

      //forceAction.setEnabled(action_status);




    }

    public void setInput(Object object, int index) {

      graph = (Graph)object;

      Table nodeTable = graph.getNodeTable();
      nodeTable.addColumn(DISTANCE, java.lang.Integer.class);

      if (getVisType() == VIS_TYPE_PARTITION && !nodeTable.canGet(GraphUtils.PARTITION, String.class)) {
        setVisType(VIS_TYPE_UNWEIGHTED);
      }
      else if (getVisType() == VIS_TYPE_BY_ATT) {
        setVisType(VIS_TYPE_UNWEIGHTED);
      }

      removeAll();
      calculateMaxWeights();
      createMenuBar();

  	  displayedLabel = GraphUtils.LABEL;
      // DC added this to force it to display the first attribute column if GraphUtils.LABEL is not present
      if(!nodeTable.canGetString(GraphUtils.LABEL)) {
            boolean done = false;
            // use the first column that can be gotten as string
            int ctr = 0;
            while(!done) {
                if(ctr < nodeTable.getColumnCount()) {
                    if(nodeTable.canGetString(nodeTable.getColumnName(ctr))) {
                        displayedLabel = nodeTable.getColumnName(ctr);
                        done = true;
                    }
                }
                else
                    done = true;

                ctr++;
            }
      }

      edgeRenderer = new LabeledEdgeRenderer(false);

      registry = new Visualization();
      registry.add(graphHolder, graph);
      //registry.setInteractive("graph.edges",null,false);

      //AdaptiveTextItemRenderer nr = new AdaptiveTextItemRenderer(maxNodeWeight);

      //registry.setRendererFactory(new DefaultRendererFactory(nr));

        registry.setRendererFactory(new RendererFactory() {
      	AdaptiveTextItemRenderer nr = new AdaptiveTextItemRenderer(
          maxNodeWeight);
        // LabeledEdgeRenderer er = new LabeledEdgeRenderer(false);
        public Renderer getRenderer(VisualItem item) {
          if (item instanceof NodeItem) {
            return nr;
          }
          else { // if (item instanceof EdgeItem) {
            return edgeRenderer;
          }
        }


      });
      //registry.setItemComparator(weightedComparator);

      //initForceAction();

      actionList = new ActionList(registry, -1L);
      //actionList.add(new GraphFilter());

      ForceSimulator initialForce = new ForceSimulator();
      initialForce.addForce(new NBodyForce(-0.4f, -1.0f, -0.9f));
      initialForce.addForce(new SpringForce(4E-5f, 75f));
      //initialForce.addForce(new DragForce(-0.005F));
      initialForce.addForce(new DragForce(0.005F));
      forceAction = new ForceDirectedLayout(graphHolder, initialForce, false, false);

      ActionList edgecolor = new ActionList();
      edgecolor.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.rgb(0,0,0)));
      edgecolor.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.rgb(0,0,0)));

      actionList.add(forceAction);
      actionList.add(new RepaintAction());
      //actionList.add(forceAction);
      registry.putAction("draw",edgecolor);
      registry.putAction("layout", actionList);

      display = new Display(registry);
      display.addControlListener(new DistanceControl());
      display.addControlListener(new DragControl(false, true));
      display.addControlListener(new PanControl(false));
      display.addControlListener(new ZoomControl());
      display.panToAbs(new Point2D.Double(0, 0));
      display.setItemSorter(new ItemSorter());

      registry.run("draw");
      registry.run("layout");
      //actionList.runNow();
      //actionList.run();

      legend = new Legend();

      legendFrame = new JFrame();
      legendFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      legendFrame.getContentPane().add(new JScrollPane(legend) {
        Dimension size = new Dimension(100, 100);
        public Dimension getMinimumSize() { return size; }
        public Dimension getPreferredSize() { return size; }
      });
      legendFrame.pack();
      legendFrame.setVisible(false);

      cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(this);

      doneButton = new JButton("Done");
      doneButton.addActionListener(this);

      JPanel buttonPanel = new JPanel();
      GridBagLayout buttonLayout = new GridBagLayout();
      buttonPanel.setLayout(buttonLayout);
      JLabel buttonFiller = new JLabel();
      buttonLayout.addLayoutComponent(buttonFiller, new GridBagConstraints(
          0, 0, 1, 1, 1.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
      buttonPanel.add(buttonFiller);
      buttonLayout.addLayoutComponent(cancelButton, new GridBagConstraints(
          1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(1, 1, 1, 1), 0, 0));
      buttonPanel.add(cancelButton);
      buttonLayout.addLayoutComponent(doneButton, new GridBagConstraints(
          2, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(1, 1, 1, 1), 0, 0));
      buttonPanel.add(doneButton);

      GridBagLayout layout = new GridBagLayout();
      setLayout(layout);
      layout.addLayoutComponent(display, new GridBagConstraints(
          0, 0, 1, 1, 1.0, 1.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      add(display);
      layout.addLayoutComponent(buttonPanel, new GridBagConstraints(
          0, 1, 1, 1, 1.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
      add(buttonPanel);

    }

    //////////////////////////////////////////////////////////////////////////////
    // utility classes
    //////////////////////////////////////////////////////////////////////////////

    private class AdaptiveTextItemRenderer extends LabelRenderer {

      private int maxWeight;

      private AdaptiveTextItemRenderer(int maxWeight) {
        super();
        this.maxWeight = maxWeight;
        //setTextAttributeName(TableToAdaptivePrefuseGraph.LABEL);
        setTextField(displayedLabel);
      }

      protected void drawShape(Graphics2D g, VisualItem item, Shape shape) {

        // set colors
        Paint itemColor = new Color(item.getFillColor());
        Paint fillColor;

        // modified from superclass: paint applicable gradient(s)
        if (item.get(DISTANCE) != null) {
          int distance = ((Integer)item.get(DISTANCE)).intValue();
          float f = (float)(distance - 1)/(float)(highlightDistanceMax - 1);
          fillColor = gradient(distanceGradientLow, distanceGradientHigh, f);
        }
        else {

          switch (visType) {
            case VIS_TYPE_UNWEIGHTED:
              fillColor = unweightedColor;
              break;
            case VIS_TYPE_WEIGHTED:
              NodeItem n = (NodeItem)item;
              String weight = n.getString(GraphUtils.WEIGHT);
              float f = (float)(Integer.parseInt(weight) - 1)/(float)maxWeight;
              fillColor = gradient(weightGradientLow, weightGradientHigh, f);
              break;
            case VIS_TYPE_PARTITION:
              n = (NodeItem)item;
              String state = n.getString(GraphUtils.PARTITION);
              if (Integer.parseInt(state) == 0) {
                fillColor = partitionColor0;
              }
              else {
                fillColor = partitionColor1;
              }
              break;
            case VIS_TYPE_BY_ATT:
              n = (NodeItem)item;
              String attVal = n.getString(nodeColorAtt);
              fillColor = (Color)nodeColorAttMap.get(attVal);
              if (fillColor == null) {
                fillColor = Color.WHITE;
              }
              break;
            default:
              fillColor = new Color(item.getFillColor(), true);
          }

        }

        // render shape
        Stroke s = g.getStroke();
        Stroke is = getStroke(item);
        if ( is != null ) g.setStroke(is);
        switch (getRenderType(item)) {
          case RENDER_TYPE_DRAW:
            g.setPaint(itemColor);
            g.draw(shape);
            break;
          case RENDER_TYPE_FILL:
            g.setPaint(fillColor);
            g.fill(shape);
            break;
          case RENDER_TYPE_DRAW_AND_FILL:
            g.setPaint(fillColor);
            g.fill(shape);
            g.setPaint(itemColor);
            g.draw(shape);
            break;
        }

        g.setStroke(s);

      }

      public void render(Graphics2D g, VisualItem item) {

        // render shape
        Shape shape = getShape(item);
        if (shape != null) {
          drawShape(g, item, shape); // modified from superclass: don't call super
        }

        // render text
        String s = getText(item);
        if (s != null) {
          Rectangle2D r = shape.getBounds2D();
          g.setPaint(new Color(item.getFillColor()));
          g.setFont(m_font);
          FontMetrics fm = g.getFontMetrics();
          double size = item.getSize();
          double x = r.getX() + size * m_horizBorder;
          double y = r.getY() + size * m_vertBorder;
          g.drawString(s, (float)x, (float)y + fm.getAscent());
          // LAM --- what is this?
  //        if (isHyperlink(item)) {
  //          int lx = (int)Math.round(x), ly = (int)Math.round(y);
  //          g.drawLine(lx, ly, lx + fm.stringWidth(s), ly + fm.getHeight() - 1);
  //        }
        }

      }


    }

  }

  private class DistanceControl extends ControlAdapter {

    private DistanceControl() { }

    // nodes in queue have had distances set/cleared
    // (so set/clear one before adding to queue!)
    private void bfs(Queue queue, boolean state) {

      while (!queue.isEmpty()) {

        NodeItem node = (NodeItem)queue.dequeue();

        int distance = 0;
        if (state) {
          distance = ((Integer)node.get(DISTANCE)).intValue();
        }

        Iterator neighborIterator = node.neighbors();
        int neighborIteratorCount = node.getDegree();

        while (neighborIteratorCount-- > 0) {

          NodeItem neighbor = (NodeItem)neighborIterator.next();

          if (state && neighbor.get(DISTANCE) == null) {

            neighbor.set(DISTANCE, new Integer(distance + 1));

            if (distance + 1 < highlightDistanceMax) {
              queue.enqueue(neighbor);
            }

          }
          else if (!state && neighbor.get(DISTANCE) != null) {

            distance = ((Integer)neighbor.get(DISTANCE)).intValue();
            neighbor.set(DISTANCE, null);

            if (distance < highlightDistanceMax) {
              queue.enqueue(neighbor);
            }

          }

        }

      }

    }

    public void itemEntered(VisualItem item, MouseEvent me) {
      if (item instanceof NodeItem) {
        setDistance((NodeItem)item, true);
      }
    }

    public void itemExited(VisualItem item, MouseEvent me) {
      if (item instanceof NodeItem) {
        setDistance((NodeItem)item, false);
      }
    }

    private void setDistance(NodeItem node, boolean state) {

      // here, a synchronization block is removed for now, but if graph data is
      // ever to be modified by means other than the UI (that is, while the UI
      // is active), it may need to be re-added

      // synchronized (registry) {

      if (state) {
        node.set(DISTANCE, new Integer(1));
      }
      else {
        node.set(DISTANCE, null);
      }

      Queue queue = new Queue();
      queue.enqueue(node);

      bfs(queue, state);

      // }

    }

  }

  //////////////////////////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////////////////////////

  private static String DISTANCE = "_DISTANCE";

  private static Comparator binaryComparator0 = new BinaryComparator(true);
  private static Comparator binaryComparator1 = new BinaryComparator(false);
  private static Comparator weightedComparator = new WeightedComparator();

  private static Color gradient(Color c1, Color c2, float f) {
    float r1 = c1.getRed();
    float g1 = c1.getGreen();
    float b1 = c1.getBlue();
    float r  = r1 + f * (c2.getRed() - r1);
    float g  = g1 + f * (c2.getGreen() - g1);
    float b  = b1 + f * (c2.getBlue() - b1);
    return new Color((int)r, (int)g, (int)b);
  }

  private static class BinaryComparator implements Comparator {

    private boolean zero;

    private BinaryComparator(boolean zero) {
      this.zero = zero;
    }

    public int compare(Object o1, Object o2) {
      if (o1 instanceof EdgeItem) {
        return -1;
      }
      else if (o2 instanceof EdgeItem) {
        return 1;
      }
      else {

        NodeItem n1 = (NodeItem)o1;
        int binary1 = n1.getInt(GraphUtils.PARTITION);
        //int binary1 = Integer.parseInt(att1);

        NodeItem n2 = (NodeItem)o2;
        int binary2 = n2.getInt(GraphUtils.PARTITION);
        //int binary2 = Integer.parseInt(att2);

        if (binary1 == binary2) {
          return n1.hashCode() - n2.hashCode();
        }
        else {
          return zero ? binary2 - binary1 : binary1 - binary2;
        }

      }
    }

  }

  private static class LabeledEdgeRenderer extends EdgeRenderer {

    private boolean renderLabels;
    private String label;
    private String field = GraphUtils.LABEL;

    private LabeledEdgeRenderer(boolean renderLabels) {
      setRenderLabels(renderLabels);
    }

    public void render(Graphics2D g, VisualItem item) {
      super.render(g, item);

      if (renderLabels) {
        double x1 = m_line.getP1().getX();
        double x2 = m_line.getP2().getX();
        double y1 = m_line.getP1().getY();
        double y2 = m_line.getP2().getY();
        float x = (float)(x1 + (x2 - x1)/2);
        float y = (float)(y1 + (y2 - y1)/2);
        EdgeItem e = (EdgeItem)item;
        label = e.getString(field);
        g.drawString(label, x, y);
      }
    }

    private void setRenderLabels(boolean renderLabels) {
      this.renderLabels = renderLabels;
    }

    private void toggle() {
      renderLabels = !renderLabels;
    }

    public void setLabel(String m)
    {

    	field = m;
    }

  }

  private static class Queue extends LinkedList {

    private Queue() {
      super();
    }

    private Object dequeue() {
      return removeLast();
    }

    private void enqueue(Object o) {
      addFirst(o);
    }

  }

  /*
  private static class UnweightedComparator implements Comparator {

    public int compare(Object o1, Object o2) {
      if (o1 instanceof EdgeItem) {
        return -1;
      }
      else if (o2 instanceof EdgeItem) {
        return 1;
      }
      return 0;
    }

  }
  */

  private static class WeightedComparator implements Comparator {

    public int compare(Object o1, Object o2) {
      if (o1 instanceof EdgeItem) {
        return -1;
      }
      else if (o2 instanceof EdgeItem) {
        return 1;
      }
      else {

        NodeItem n1 = (NodeItem)o1;
        int weight1 = n1.getInt(GraphUtils.WEIGHT);
        //int weight1 = Integer.parseInt(att1);

        NodeItem n2 = (NodeItem)o2;
        int weight2 = n2.getInt(GraphUtils.WEIGHT);
        //int weight2 = Integer.parseInt(att2);

        if (weight1 == weight2) {
          return n1.hashCode() - n2.hashCode();
        }
        else {
          return weight1 - weight2;
        }

      }
    }

  }

	private class Legend extends JPanel {
		Legend() {
			setLayout(new GridBagLayout());
			//setBackground(yellowish);
		}

		private void updateLegend(HashMap lk) {
			removeAll();
			/*JLabel leg = new JLabel("LEGEND");//new AALabel("LEGEND");
			leg.setBackground(yellowish);
			Constrain.setConstraints (this, leg, 1, 0, 1, 1,
			   GridBagConstraints.HORIZONTAL,
			   GridBagConstraints.NORTH, 1.0, 0.0,
			   new Insets(2, 4, 2, 0));
			*/
			Iterator it = lk.keySet().iterator();

			int i = 0;
			while (it.hasNext()) {
				String text = (String) it.next();
				Color c = (Color) lk.get(text);

				Insets ii = new Insets(4, 8, 4, 0);
				Insets i2 = new Insets(4, 8, 4, 0);

				JLabel ll = new JLabel(text);
				ColorComponent cc = new ColorComponent(c);
				Constrain.setConstraints(
					this,
					cc,
					0,
					i,
					1,
					1,
					GridBagConstraints.NONE,
					GridBagConstraints.NORTH,
					0.0,
					0.0,
					ii);
				Constrain.setConstraints(
					this,
					ll,
					1,
					i,
					1,
					1,
					GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTH,
					1.0,
					0.0,
					i2);
				i++;
			}
			revalidate();
			repaint();
		}
	}

	/**
	 * A small square with a black outline.  The color of the
	 * square is given in the constructor.
	 */
	private final class ColorComponent extends JComponent {
		private final int DIM = 12;
		Color bkgrd;

		ColorComponent(Color c) {
			super();
			setOpaque(true);
			bkgrd = c;
		}

		public Dimension getPreferredSize() {
			return new Dimension(DIM, DIM);
		}

		public Dimension getMinimumSize() {
			return new Dimension(DIM, DIM);
		}

		public void paint(Graphics g) {
			g.setColor(bkgrd);
			g.fillRect(0, 0, DIM - 1, DIM - 1);
			g.setColor(Color.black);
			g.drawRect(0, 0, DIM - 1, DIM - 1);
		}

		void setBkgrd(Color c) {
			bkgrd = c;
		}

		BufferedImage getImage() {
			BufferedImage image =
				new BufferedImage(DIM, DIM, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			paint(g);
			return image;
		}
	}

  //////////////////////////////////////////////////////////////////////////////
  // D2K module properties
  //////////////////////////////////////////////////////////////////////////////

  // private Color binaryColor0 = new Color(0, 128, 255);
  private Color partitionColor0 = new Color(0, 255, 128);

  public Object getPartitionColor0() {
    return partitionColor0;
  }

  public void setPartitionColor0(Object value) {
    partitionColor0 = (Color)value;
  }

  private Color partitionColor1 = new Color(255, 0, 64);

  public Object getPartitionColor1() {
    return partitionColor1;
  }

  public void setPartitionColor1(Object value) {
    partitionColor1 = (Color)value;
  }

  private Color distanceGradientHigh = Color.YELLOW;

  public Object getDistanceGradientHigh() {
    return distanceGradientHigh;
  }

  public void setDistanceGradientHigh(Object value) {
    distanceGradientHigh = (Color)value;
  }

  // private Color distanceGradientLow = Color.ORANGE;
  private Color distanceGradientLow = new Color(0, 128, 255);

  public Object getDistanceGradientLow() {
    return distanceGradientLow;
  }

  public void setDistanceGradientLow(Object value) {
    distanceGradientLow = (Color)value;
  }

  private int highlightDistanceMax = 3;

  public int getHighlightDistanceMax() {
    return highlightDistanceMax;
  }

  public void setHighlightDistanceMax(int value) {
    highlightDistanceMax = value;
  }

  // private Color unweightedColor = new Color(48, 64, 128);
  private Color unweightedColor = new Color(255, 222, 135);

  public Object getUnweightedColor() {
    return unweightedColor;
  }

  public void setUnweightedColor(Object value) {
    unweightedColor = (Color)value;
  }

  private int visType = VIS_TYPE_UNWEIGHTED;

  public int getVisType() {
    return visType;
  }

  public void setVisType(int value) {
    if (value < VIS_TYPE_MIN || value > VIS_TYPE_MAX) {
      throw new IllegalArgumentException(Integer.toString(value));
    }
    visType = value;
  }

  private Color weightGradientHigh = Color.RED;

  public Object getWeightGradientHigh() {
    return weightGradientHigh;
  }

  public void setWeightGradientHigh(Object value) {
    weightGradientHigh = (Color)value;
  }

  private Color weightGradientLow = Color.GRAY;

  public Object getWeightGradientLow() {
    return weightGradientLow;
  }

  public void setWeightGradientLow(Object value) {
    weightGradientLow = (Color)value;
  }

  //////////////////////////////////////////////////////////////////////////////
  // D2K module boilerplate
  //////////////////////////////////////////////////////////////////////////////

  public UserView createUserView() {
    return new PrefuseView();
  }

  public String[] getFieldNameMapping() {
    return null;
  }

  public String getInputInfo(int index) {
    if (index == 0) {
      return "A <i>prefuse</i> graph marked with attributes as in " +
        "<i>TableToPrefuse</i>.";
    }
    else {
      return null;
    }
  }

  public String getInputName(int index) {
    if (index == 0) {
      return "Prefuse Graph";
    }
    else {
      return null;
    }
  }

  public String[] getInputTypes() {
    return new String[] {
      "prefuse.data.Graph"
    };
  }

  public String getModuleInfo() { // !:
      String s = "Overview: A simple interactive prefuse graph visualization.";

      s += "<p>Acknowledgement: ";
      s += "This module uses functionality from the Prefuse project. See http://prefuse.org.";
      s += "</p>";

      return s;
  }

  public String getOutputInfo(int index) {
    return null;
  }

  public String[] getOutputTypes() {
    return null;
  }

}
