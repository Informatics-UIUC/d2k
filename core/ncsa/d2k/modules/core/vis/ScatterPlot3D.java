package ncsa.d2k.modules.core.vis;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import ncsa.gui.Constrain;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.controller.userviews.widgits.*;
import ncsa.d2k.util.datatype.*;

public class ScatterPlot3D extends VisModule implements Serializable {

   public String getModuleInfo() {
      return "ScatterPlot3D is a three-dimensional visualization of " +
             "VerticalTable data as a scatter plot.";
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.util.datatype.VerticalTable"}; return i;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "The VerticalTable to be visualized.";
      else
         return "ScatterPlot3D has no such input.";
   }

   public String[] getOutputTypes() {
      return null;
   }

   public String getOutputInfo(int index) {
      return "ScatterPlot3D has no outputs.";
   }

   public String[] getFieldNameMapping() { return null; }

   public ScatterPlot3DView vis;

   protected UserView createUserView() {
      vis = new ScatterPlot3DView(); return vis;
   }

   public class ScatterPlot3DView extends JUserPane implements Serializable {

      transient public ScatterPlot3DControl control;
      transient public ScatterPlot3DGraph graph;
      transient public VerticalTable table;

      // public HashMap column_labels;

      transient public JPanel graphpane;
      transient public JSplitPane split;

      public void initView(ViewModule m) { }

      public void setInput(Object o, int i) {

         if (i == 0) {

            table = (VerticalTable)o;
            execute();

         }

      }

      public void execute() {

         // for (int count = 0; count < table.getNumColumns(); count++)
         //    column_labels.put(table.getColumnLabel(count), new Integer(count));

         control = new ScatterPlot3DControl();
         graph = new ScatterPlot3DGraph();

         graphpane = new JPanel();
         // graphpane.setPreferredSize(new Dimension(640, 480));
         graphpane.setLayout(new GridBagLayout());
         Constrain.setConstraints(graphpane, graph.canvas, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.WEST, 1, 1,
            new Insets(0, 0, 0, 0));

         split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, control, graphpane);
         split.setOneTouchExpandable(true);

         setLayout(new GridBagLayout());
         Constrain.setConstraints(this, split, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.WEST, 1, 1,
            new Insets(0, 0, 0, 0));

      }

   } // ScatterPlot3D$ScatterPlot3DView

   public class ScatterPlot3DControl extends JPanel
      implements ActionListener, Serializable {

      private JTabbedPane tabbed;
      private JPanel editlist, properties;

      // Edit/List panel objects

      private JLabel edit_name_label, edit_color_label,
         edit_xvar_label, edit_yvar_label, edit_zvar_label;
      private JTextField edit_name_field;
      private ColorPanel edit_color;
      private JComboBox edit_xbox, edit_ybox, edit_zbox;
      private JButton edit_add;

      private JList edit_list;
      private DefaultListModel edit_listmodel;
      private JScrollPane edit_scroll;
      private JButton edit_delete, edit_refresh;

      // Properties panel objects

      private JLabel prop_xmin_label, prop_ymin_label, prop_zmin_label,
         prop_xmax_label, prop_ymax_label, prop_zmax_label, prop_title_label,
         prop_xaxis_label, prop_yaxis_label, prop_zaxis_label;
      private JTextField prop_xmin_field, prop_ymin_field, prop_zmin_field,
         prop_xmax_field, prop_ymax_field, prop_zmax_field, prop_title_field,
         prop_xaxis_field, prop_yaxis_field, prop_zaxis_field;
      private JCheckBox prop_grid, prop_legend;

      private Vector labels;
      private HashMap map;

      public ScatterPlot3DControl() {

         // Edit subpanel

         edit_name_label = new JLabel("Name: ");
         edit_color_label = new JLabel("Color: ");
         edit_xvar_label = new JLabel("X Variable: ");
         edit_yvar_label = new JLabel("Y Variable: ");
         edit_zvar_label = new JLabel("Z Variable: ");
         edit_name_field = new JTextField();
         edit_color = new ColorPanel();

         labels = new Vector();
         map = new HashMap();

         Column column;
         int index = 0;
         for (int count = 0; count < vis.table.getNumColumns(); count++) {
            column = vis.table.getColumn(count);
            if (column instanceof NumericColumn) {
               labels.add((String)vis.table.getColumnLabel(count));
               map.put(new Integer(index++), new Integer(count));
            }
         }

         edit_xbox = new JComboBox(labels);
         edit_ybox = new JComboBox(labels);
         edit_zbox = new JComboBox(labels);

         edit_add = new JButton("Add");
         edit_add.addActionListener(this);

         JPanel editpanel = new JPanel();
         editpanel.setBorder(new TitledBorder("Edit: "));
         editpanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(editpanel, edit_name_label, 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(editpanel, edit_name_field, 1, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(editpanel, edit_color_label, 0, 1, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(editpanel, edit_color, 1, 1, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(editpanel, edit_xvar_label, 0, 2, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(editpanel, edit_xbox, 1, 2, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(editpanel, edit_yvar_label, 0, 3, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(editpanel, edit_ybox, 1, 3, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(editpanel, edit_zvar_label, 0, 4, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(editpanel, edit_zbox, 1, 4, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(editpanel, edit_add, 1, 5, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 1, 1);

         // List subpanel

         edit_list = new JList(new DefaultListModel());
         edit_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         edit_listmodel = (DefaultListModel)edit_list.getModel();

         edit_delete = new JButton("Delete");
         edit_delete.addActionListener(this);

         edit_scroll = new JScrollPane(edit_list,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

         JPanel listpanel = new JPanel();
         listpanel.setBorder(new TitledBorder("List: "));
         listpanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(listpanel, edit_scroll, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.WEST, 1, .5);
         Constrain.setConstraints(listpanel, edit_delete, 0, 1, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTHEAST, 0, .5);

         // Edit/List panel

         editlist = new JPanel();
         editlist.setLayout(new GridBagLayout());
         Constrain.setConstraints(editlist, editpanel, 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST, 0, 0);
         Constrain.setConstraints(editlist, listpanel, 0, 1, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.WEST, 1, 1);

         // Properties panel

         prop_xmin_label = new JLabel("X Minimum: ");
         prop_ymin_label = new JLabel("Y Minimum: ");
         prop_zmin_label = new JLabel("Z Minimum: ");
         prop_xmax_label = new JLabel("X Maximum: ");
         prop_ymax_label = new JLabel("Y Maximum: ");
         prop_zmax_label = new JLabel("Z Maximum: ");
         prop_title_label = new JLabel("Title: ");
         prop_xaxis_label = new JLabel("X Axis: ");
         prop_yaxis_label = new JLabel("Y Axis: ");
         prop_zaxis_label = new JLabel("Z Axis: ");
         prop_xmin_field = new JTextField();
         prop_ymin_field = new JTextField();
         prop_zmin_field = new JTextField();
         prop_xmax_field = new JTextField();
         prop_ymax_field = new JTextField();
         prop_zmax_field = new JTextField();
         prop_title_field = new JTextField();
         prop_xaxis_field = new JTextField();
         prop_yaxis_field = new JTextField();
         prop_zaxis_field = new JTextField();
         prop_grid = new JCheckBox("Grid", true);
         prop_legend = new JCheckBox("Legend", true);

         properties = new JPanel();
         properties.setBorder(new TitledBorder("Properties: "));
         properties.setLayout(new GridBagLayout());
         Constrain.setConstraints(properties, prop_xmin_label, 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_xmin_field, 1, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_xmax_label, 2, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_xmax_field, 3, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_ymin_label, 0, 1, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_ymin_field, 1, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
         Constrain.setConstraints(properties, prop_ymax_label, 2, 1, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_ymax_field, 3, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
         Constrain.setConstraints(properties, prop_zmin_label, 0, 3, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_zmin_field, 1, 3, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
         Constrain.setConstraints(properties, prop_zmax_label, 2, 3, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_zmax_field, 3, 3, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
         Constrain.setConstraints(properties, prop_title_label, 0, 4, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_title_field, 1, 4, 3, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_xaxis_label, 0, 5, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_xaxis_field, 1, 5, 3, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_yaxis_label, 0, 6, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_yaxis_field, 1, 6, 3, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_zaxis_label, 0, 7, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_zaxis_field, 1, 7, 3, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(properties, prop_grid, 0, 8, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
         Constrain.setConstraints(properties, prop_legend, 0, 9, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 1);

         // Tab the control panels

         tabbed = new JTabbedPane(SwingConstants.TOP);
         tabbed.add("Scatter Plot", editlist);
         tabbed.add("Settings", properties);

         edit_refresh = new JButton("Refresh");
         edit_refresh.addActionListener(this);

         // ...and finish

         setMinimumSize(new Dimension(0, 0));
         setLayout(new GridBagLayout());
         Constrain.setConstraints(this, tabbed, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, 1, 1);
         Constrain.setConstraints(this, edit_refresh, 0, 1, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);

      }

      public void actionPerformed(ActionEvent event) {

         Object src = event.getSource();

         if (src == edit_add) {

            String name = edit_name_field.getText();

            if (name.equals(""))
               return;

            Color color = edit_color.getColor();

            int index = edit_xbox.getSelectedIndex();
            Integer X = (Integer)map.get(new Integer(index));
            String label = vis.table.getColumnLabel(X.intValue());
            name = name + " " + label;

            index = edit_ybox.getSelectedIndex();
            Integer Y = (Integer)map.get(new Integer(index));
            label = vis.table.getColumnLabel(Y.intValue());
            name = name + ", " + label;

            index = edit_zbox.getSelectedIndex();
            Integer Z = (Integer)map.get(new Integer(index));
            label = vis.table.getColumnLabel(Z.intValue());
            name = name + ", " + label;

            edit_listmodel.addElement(new ScatterPlot3DDataSet(name,
               color, X.intValue(), Y.intValue(), Z.intValue()));

            edit_name_field.setText("");

         }
         else if (src == edit_delete) {

            int index = edit_list.getSelectedIndex();
            if (index != -1)
               edit_listmodel.removeElementAt(index);

         }
         else if (src == edit_refresh) {

            int size = edit_listmodel.getSize();
            if (size == 0) {
               vis.graphpane.removeAll();
               vis.graphpane.validate();
               vis.graphpane.repaint();
               return;
            }

            // scatter plot data
            ScatterPlot3DDataSet[] set = new ScatterPlot3DDataSet[size];
            for (int count = 0; count < size; count++)
               set[count] = (ScatterPlot3DDataSet)edit_listmodel.getElementAt(count);

            // property data
            String title = prop_title_field.getText();
            String xaxis = prop_xaxis_field.getText();
            String yaxis = prop_yaxis_field.getText();
            String zaxis = prop_zaxis_field.getText();

            String value; Integer Xmin, Xmax, Ymin, Ymax, Zmin, Zmax;
            value = prop_xmin_field.getText();
            if (value.equals(""))
               Xmin = null;
            else
               Xmin = new Integer(value);
            value = prop_xmax_field.getText();
            if (value.equals(""))
               Xmax = null;
            else
               Xmax = new Integer(value);
            value = prop_ymin_field.getText();
            if (value.equals(""))
               Ymin = null;
            else
               Ymin = new Integer(value);
            value = prop_ymax_field.getText();
            if (value.equals(""))
               Ymax = null;
            else
               Ymax = new Integer(value);
            value = prop_zmin_field.getText();
            if (value.equals(""))
               Zmin = null;
            else
               Zmin = new Integer(value);
            value = prop_zmax_field.getText();
            if (value.equals(""))
               Zmax = null;
            else
               Zmax = new Integer(value);

            boolean display_grid = prop_grid.isSelected(),
               display_legend = prop_legend.isSelected();

            ScatterPlot3DGraphSettings settings =
               new ScatterPlot3DGraphSettings(title, xaxis, yaxis, zaxis,
                  Xmin, Xmax, Ymin, Ymax, Zmin, Zmax, 10, display_grid, true,
                  display_legend, true, true, true);

            vis.graphpane.removeAll();

            // !: ...
            vis.graph.rebuild(settings, set);

            Constrain.setConstraints(vis.graphpane, vis.graph.canvas, 0, 0,
               1, 1, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1, 1);
            vis.graphpane.validate();
            vis.graphpane.repaint(); // !: do we need to do this for j3d?

         }

      }

   } // ScatterPlot3D$ScatterPlot3DControl

   public class ScatterPlot3DGraph extends JPanel implements Serializable {

      transient public ScatterPlot3DCanvas canvas;
      transient private BranchGroup scene;
      transient private SimpleUniverse universe;

      public ScatterPlot3DGraph() {

         canvas = new ScatterPlot3DCanvas(SimpleUniverse.getPreferredConfiguration());

         setLayout(new GridBagLayout());
         Constrain.setConstraints(this, canvas, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.WEST, 1, 1,
            new Insets(0, 0, 0, 0));

         rebuild(new ScatterPlot3DGraphSettings(), null);

      }

      private BranchGroup createSceneGraph(ScatterPlot3DGraphSettings settings,
         ScatterPlot3DDataSet[] set) {

         BranchGroup objRoot = new BranchGroup();

         Transform3D transform = new Transform3D();
         TransformGroup objTrans = new TransformGroup(transform);
         objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

         MouseRotate mr = new MouseRotate(objTrans);
         mr.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(mr);

         MouseZoom mz = new MouseZoom(objTrans);
         mz.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(mz);

         MouseTranslate mt = new MouseTranslate(objTrans);
         mt.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(mt);

         // create axes
         LineArray xaxis = new LineArray(2, LineArray.COORDINATES |
            LineArray.COLOR_3);
         LineArray yaxis = new LineArray(2, LineArray.COORDINATES |
            LineArray.COLOR_3);
         LineArray zaxis = new LineArray(2, LineArray.COORDINATES |
            LineArray.COLOR_3);

         xaxis.setCoordinate(0, new Point3f(-100, 0, 0));
         xaxis.setCoordinate(1, new Point3f(100, 0, 0));
         xaxis.setColor(0, new Color3f(1.0f, 1.0f, 1.0f));
         xaxis.setColor(1, new Color3f(1.0f, 1.0f, 1.0f));

         yaxis.setCoordinate(0, new Point3f(0, -100, 0));
         yaxis.setCoordinate(1, new Point3f(0, 100, 0));
         yaxis.setColor(0, new Color3f(1.0f, 1.0f, 1.0f));
         yaxis.setColor(1, new Color3f(1.0f, 1.0f, 1.0f));

         zaxis.setCoordinate(0, new Point3f(0, 0, -100));
         zaxis.setCoordinate(1, new Point3f(0, 0, 100));
         zaxis.setColor(0, new Color3f(1.0f, 1.0f, 1.0f));
         zaxis.setColor(1, new Color3f(1.0f, 1.0f, 1.0f));

         Shape3D shape = new Shape3D(xaxis);
         objTrans.addChild(shape);   // child 1
         shape = new Shape3D(yaxis);
         objTrans.addChild(shape);   // child 2
         shape = new Shape3D(zaxis);
         objTrans.addChild(shape);   // child 3

         objRoot.addChild(objTrans);

         if (set == null)
            return objRoot;

         Appearance[] a = new Appearance[set.length];
         for (int count = 0; count < set.length; count++) {

            a[count] = new Appearance();
            a[count].setColoringAttributes(new ColoringAttributes(
               new Color3f(set[count].color), ColoringAttributes.FASTEST));

         }

         Sphere s;
         Transform3D t; TransformGroup tg; double dx, dy, dz;
         for (int count = 0; count < set.length; count++) {

            for (int count2 = 0; count2 < vis.table.getNumRows(); count2++) {

               s = new Sphere(0.01f); // !: different data set scales?
               s.setAppearance(a[count]);

               dx = vis.table.getDouble(count2, set[count].x);
               dy = vis.table.getDouble(count2, set[count].y);
               dz = vis.table.getDouble(count2, set[count].z);

               t = new Transform3D();
               t.setTranslation(new Vector3d(.15*dx, .15*dy, .15*(-dz)));

               tg = new TransformGroup(t);
               tg.addChild(s);

               objTrans.addChild(tg);

            }

         }

         return objRoot;

      }

      public void rebuild(ScatterPlot3DGraphSettings settings,
         ScatterPlot3DDataSet[] set) {

         canvas = new ScatterPlot3DCanvas(SimpleUniverse.getPreferredConfiguration());
         scene = createSceneGraph(settings, set);
         scene.compile();
         universe = new SimpleUniverse(canvas);
         universe.getViewingPlatform().setNominalViewingTransform();
         universe.addBranchGraph(scene);

      }

   } // ScatterPlot3D$ScatterPlot3DGraph

   public class ScatterPlot3DDataSet {

      public String name; public Color color; public int x, y, z;

      public ScatterPlot3DDataSet(String name, Color color, int x, int y, int z) {
         this.name = name; this.color = color;
         this.x = x; this.y = y; this.z = z;
      }

      public String toString() { return name; }

   }

   public class ScatterPlot3DGraphSettings {

      public String title_string, x, y, z;
      public Integer Xmin, Xmax, Ymin, Ymax, Zmin, Zmax;
      public int grid_size;
      public boolean grid, scale, legend, ticks, title, labels;

      public ScatterPlot3DGraphSettings() {
         title_string = ""; x = ""; y = ""; z = ""; Xmin = null; Xmax = null;
         Ymin = null; Ymax = null; Zmin = null; Zmax = null; grid_size = 10;
         grid = true; scale = true; legend = true;
         ticks = true; title = true; labels = true;
      }

      public ScatterPlot3DGraphSettings(String title_string, String x, String y,
         String z, Integer Xmin, Integer Xmax, Integer Ymin, Integer Ymax,
         Integer Zmin, Integer Zmax, int grid_size, boolean grid, boolean scale,
         boolean legend, boolean ticks, boolean title, boolean labels) {

         this.title_string = title_string; this.x = x; this.y = y; this.z = z;
         this.Xmin = Xmin; this.Xmax = Xmax; this.Ymin = Ymin; this.Ymax = Ymax;
         this.Zmin = Zmin; this.Zmax = Zmax; this.grid_size = 10;
         this.grid = grid; this.scale = scale; this.legend = legend;
         this.ticks = ticks; this.title = title; this.labels = labels;

      }

   }

   public class ScatterPlot3DCanvas extends Canvas3D {

      public ScatterPlot3DCanvas(GraphicsConfiguration graphicsConfig) {
         super(graphicsConfig);
      }

      public Dimension getMinimumSize() {
         return new Dimension(0, 0);
      }

      public Dimension getPreferredSize() {
         return new Dimension(300, 300);
      }

   }

   public class ColorPanel extends JPanel implements ActionListener {

      JPanel renderpanel;
      JButton editorbutton;
      Color color;

      public ColorPanel() {
         editorbutton = new JButton("Edit");
         editorbutton.addActionListener(this);
         color = Color.black;

         renderpanel = new JPanel();
         renderpanel.setBackground(color);
         renderpanel.setSize(new Dimension(10,10));

         add(renderpanel);
         add(editorbutton);
      }

      public void actionPerformed(ActionEvent event) {
         color = JColorChooser.showDialog(this, "Edit Color", Color.black);
         renderpanel.setBackground(color);
      }

      public Color getColor() {
         return color;
      }

      public void setColor(Color color) {
         this.color = color;
      }

   }

} // ScatterPlot3D
