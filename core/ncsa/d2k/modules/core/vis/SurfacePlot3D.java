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
import com.sun.j3d.utils.behaviors.keyboard.*;
import ncsa.gui.Constrain;
import ncsa.d2k.gui.JD2KFrame;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.controller.userviews.widgits.*;
import ncsa.d2k.modules.core.vis.widgets.ComputationalGeometry;
import ncsa.d2k.util.datatype.*;

public class SurfacePlot3D extends VisModule {

   public String getModuleInfo() {
      return "SurfacePlot3D is a three-dimensional visualization of VerticalTable data as a surface plot.";
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.util.datatype.VerticalTable"}; return i;
   }

   public String[] getOutputTypes() {
      return null;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "The VerticalTable to be visualized.";
      else
         return "SurfacePlot3D has no such input.";
   }

   public String getOutputInfo(int index) {
      return "SurfacePlot3D has no outputs.";
   }

   public String[] getFieldNameMapping() { return null; }
   protected UserView createUserView() { return new SurfacePlot3DView(); }

   private class SurfacePlot3DView extends JUserPane {

      private SurfacePlot3DControl control;
      private SurfacePlot3DCanvas canvas;
      private TransformGroup objTrans;

      private VerticalTable input;
      private Point3d[] points;

      private boolean show_vertices, show_triangles;
      private double xmin, xmax, ymin, ymax, zmin, zmax;

      private HelpWindow helpWindow;
      private JMenuItem helpItem;
      private JMenuBar menuBar;

      private final Color3f axisColor = new Color3f(.5098f, .5098f, .5098f);
      private final Color3f backgroundColor = new Color3f(.35294f, .35294f, .35294f);
      private final Color3f plainColor = new Color3f(0.8f, 0.9254901f, .9568627f);
      private final Color3f labelColor = new Color3f(1.0f, 1.0f, .4f);

      private Vector inCH;
      private Vector notInCH;

      public void initView(ViewModule m) { }

      public void setInput(Object o, int i) {
         if (i == 0) {
            input = (VerticalTable)o;
            initialize();
         }
      }

      public Object getMenu() {
         return menuBar;
      }

      private void initialize() {

         control = new SurfacePlot3DControl();
         canvas = new SurfacePlot3DCanvas();

         points = new Point3d[input.getNumRows()];

         show_vertices = show_triangles = true;

         // create java3d universe
         SimpleUniverse su = new SimpleUniverse(canvas);
         su.getViewingPlatform().setNominalViewingTransform();

         BranchGroup scene = createInitialScene();
         scene.compile();

         su.addBranchGraph(scene);

         // set vis layout
         JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, control, canvas);
         split.setOneTouchExpandable(true);

         setLayout(new BorderLayout());
         add(split, BorderLayout.CENTER);

         // set up menu
         helpWindow = new HelpWindow();
         menuBar = new JMenuBar();
         JMenu helpMenu = new JMenu("Help");
         helpItem = new JMenuItem("About SurfacePlot3D...");
         helpItem.addActionListener(new HelpListener());
         helpMenu.add(helpItem);
         menuBar.add(helpMenu);

      }

      private BranchGroup createInitialScene() {

         BranchGroup objRoot = new BranchGroup();
         objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
         objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

         objTrans = new TransformGroup();
         objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
         objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
         objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
         objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

         MouseRotate mr = new MouseRotate(objTrans);
         mr.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(mr);

         MouseZoom mz = new MouseZoom(objTrans);
         mz.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(mz);

         MouseTranslate mt = new MouseTranslate(objTrans);
         mt.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(mt);
/*
         KeyNavigatorBehavior keyNav = new KeyNavigatorBehavior(objTrans);
         keyNav.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(keyNav);
*/
         objRoot.addChild(objTrans);

         BranchGroup b0 = new BranchGroup();
         b0.setCapability(BranchGroup.ALLOW_DETACH);
         BranchGroup b1 = new BranchGroup();
         b1.setCapability(BranchGroup.ALLOW_DETACH);
         BranchGroup b2 = new BranchGroup();
         b2.setCapability(BranchGroup.ALLOW_DETACH);
         objTrans.addChild(b0); // child 0: axes
         objTrans.addChild(b1); // child 1: vertices
         objTrans.addChild(b2); // child 2: triangles

         return objRoot;

      }

      private void findMinMax() {

         xmin = ymin = zmin = Double.POSITIVE_INFINITY;
         xmax = ymax = zmax = Double.NEGATIVE_INFINITY;

         for (int i = 0; i < points.length; i++) {

            if (points[i].x > xmax)
               xmax = points[i].x;
            if (points[i].x < xmin)
               xmin = points[i].x;

            if (points[i].y > ymax)
               ymax = points[i].y;
            if (points[i].y < ymin)
               ymin = points[i].y;

            if (points[i].z > zmax)
               zmax = points[i].z;
            if (points[i].z < zmin)
               zmin = points[i].z;

         }

      }

      private BranchGroup drawAxes() {

         BranchGroup axes = new BranchGroup();
         axes.setCapability(BranchGroup.ALLOW_DETACH);
         axes.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

         LineArray y_z = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         y_z.setCoordinate(0, new Point3d(xmin, ymin, zmax));
         y_z.setCoordinate(1, new Point3d(xmax, ymin, zmax));
         y_z.setColor(0, axisColor);
         y_z.setColor(1, axisColor);
         axes.addChild(new Shape3D(y_z));
         LineArray yz = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         yz.setCoordinate(0, new Point3d(xmin, ymax, zmax));
         yz.setCoordinate(1, new Point3d(xmax, ymax, zmax));
         yz.setColor(0, axisColor);
         yz.setColor(1, axisColor);
         axes.addChild(new Shape3D(yz));
         LineArray y_z_ = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         y_z_.setCoordinate(0, new Point3d(xmin, ymin, zmin));
         y_z_.setCoordinate(1, new Point3d(xmax, ymin, zmin));
         y_z_.setColor(0, axisColor);
         y_z_.setColor(1, axisColor);
         axes.addChild(new Shape3D(y_z_));
         LineArray yz_ = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         yz_.setCoordinate(0, new Point3d(xmin, ymax, zmin));
         yz_.setCoordinate(1, new Point3d(xmax, ymax, zmin));
         yz_.setColor(0, axisColor);
         yz_.setColor(1, axisColor);
         axes.addChild(new Shape3D(yz_));
         LineArray xy_ = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         xy_.setCoordinate(0, new Point3d(xmax, ymin, zmin));
         xy_.setCoordinate(1, new Point3d(xmax, ymin, zmax));
         xy_.setColor(0, axisColor);
         xy_.setColor(1, axisColor);
         axes.addChild(new Shape3D(xy_));
         LineArray xy = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         xy.setCoordinate(0, new Point3d(xmax, ymax, zmin));
         xy.setCoordinate(1, new Point3d(xmax, ymax, zmax));
         xy.setColor(0, axisColor);
         xy.setColor(1, axisColor);
         axes.addChild(new Shape3D(xy));
         LineArray x_y_ = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         x_y_.setCoordinate(0, new Point3d(xmin, ymin, zmin));
         x_y_.setCoordinate(1, new Point3d(xmin, ymin, zmax));
         x_y_.setColor(0, axisColor);
         x_y_.setColor(1, axisColor);
         axes.addChild(new Shape3D(x_y_));
         LineArray x_y = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         x_y.setCoordinate(0, new Point3d(xmin, ymax, zmin));
         x_y.setCoordinate(1, new Point3d(xmin, ymax, zmax));
         x_y.setColor(0, axisColor);
         x_y.setColor(1, axisColor);
         axes.addChild(new Shape3D(x_y));
         LineArray x_z = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         x_z.setCoordinate(0, new Point3d(xmin, ymin, zmax));
         x_z.setCoordinate(1, new Point3d(xmin, ymax, zmax));
         x_z.setColor(0, axisColor);
         x_z.setColor(1, axisColor);
         axes.addChild(new Shape3D(x_z));
         LineArray x_z_ = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         x_z_.setCoordinate(0, new Point3d(xmin, ymin, zmin));
         x_z_.setCoordinate(1, new Point3d(xmin, ymax, zmin));
         x_z_.setColor(0, axisColor);
         x_z_.setColor(1, axisColor);
         axes.addChild(new Shape3D(x_z_));
         LineArray xz = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         xz.setCoordinate(0, new Point3d(xmax, ymin, zmax));
         xz.setCoordinate(1, new Point3d(xmax, ymax, zmax));
         xz.setColor(0, axisColor);
         xz.setColor(1, axisColor);
         axes.addChild(new Shape3D(xz));
         LineArray xz_ = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         xz_.setCoordinate(0, new Point3d(xmax, ymin, zmin));
         xz_.setCoordinate(1, new Point3d(xmax, ymax, zmin));
         xz_.setColor(0, axisColor);
         xz_.setColor(1, axisColor);
         axes.addChild(new Shape3D(xz_));

         return axes;

      }

      private BranchGroup drawVertices() {

         BranchGroup all = new BranchGroup();
         all.setCapability(BranchGroup.ALLOW_DETACH);

         Appearance a;
         Sphere s;
         TransformGroup tg;
         Transform3D t = new Transform3D();
         Color lc = control.getLowColor(),
               hc = control.getHighColor();
         double dx, dy, dz;
         for (int i = 0; i < points.length; i++) {

            s = new Sphere(0.05f);
            a = new Appearance();
            a.setColoringAttributes(new ColoringAttributes( interpolateColor(lc, hc,
               ((ymax - points[i].y) / (ymax - ymin))), ColoringAttributes.FASTEST));
            s.setAppearance(a);

            dx = points[i].x;
            dy = points[i].y;
            dz = points[i].z;

            t.setTranslation(new Vector3d(dx, dy, dz));

            tg = new TransformGroup(t);
            tg.addChild(s);
            all.addChild(tg);

         }

         return all;

      }

      private BranchGroup drawTriangles() {

         BranchGroup b = new BranchGroup();
         b.setCapability(BranchGroup.ALLOW_DETACH);

         Vector tri = triangulate();

         IndexedTriangleArray A;
         TriangleArray t1;

         Shape3D s; Appearance a;

         Color lc = control.getLowColor(),
               hc = control.getHighColor();

         Point3d p0 = new Point3d(), p1 = new Point3d(), p2 = new Point3d();
         double midpoint;

         for (int j = 0; j < tri.size(); j++) {

            A = (IndexedTriangleArray)tri.get(j);

            for (int i = 0; i < A.getVertexCount(); i += 3) {

               A.getCoordinate(i, p0);
               A.getCoordinate(i + 1, p1);
               A.getCoordinate(i + 2, p2);

               midpoint = (p0.y + p1.y + p2.y) / 3;

               t1 = new TriangleArray(6, TriangleArray.COORDINATES);

               t1.setCoordinate(0, p0);
               t1.setCoordinate(1, p1);
               t1.setCoordinate(2, p2);
               t1.setCoordinate(3, p2);
               t1.setCoordinate(4, p1);
               t1.setCoordinate(5, p0);

               s = new Shape3D();
               a = new Appearance();

               a.setColoringAttributes(new ColoringAttributes( interpolateColor(lc, hc,
                  ((ymax - midpoint) / (ymax - ymin))), ColoringAttributes.FASTEST));

               s.setGeometry(t1);
               s.setAppearance(a);

               b.addChild(s);

            }

         }

         return b;

      }

      private Vector triangulate() {

         /* remove duplicate (x, *, z) pairs. */

         HashMap X = new HashMap(), Z;
         for (int i = 0; i < points.length; i++) {

            if (X.containsKey(new Double(points[i].x))) {
               Z = (HashMap)X.get(new Double(points[i].x));
               if (Z.containsKey(new Double(points[i].z))) {
                  Point3d old = (Point3d)Z.get(new Double(points[i].z));
                  if (old.y < points[i].y)
                     Z.put(new Double(points[i].z), points[i]);
               }
               else {
                  Z.put(new Double(points[i].z), points[i]);
               }
            }
            else {
               Z = new HashMap();
               X.put(new Double(points[i].x), Z);
               Z.put(new Double(points[i].z), points[i]);
            }

         }

         /* HashMap X now contains as values HashMaps that themselves contain */
         /* as values all the relevant vertices; we must iterate over them.   */

         Vector v = new Vector();

         Object[] Xs = X.values().toArray();
         for (int i = 0; i < Xs.length; i++) {
            Object[] Zs = ((HashMap)Xs[i]).values().toArray();
            for (int j = 0; j < Zs.length; j++)
               v.add(Zs[j]);
         }

         /* v is now a Vector of all relevant Point3d objects. we want their */
         /* convex hull with respect to the xz plane.                        */

         Vector inHull = new Vector(), notInHull = new Vector(),
                inNextHull = new Vector(), notInNextHull = new Vector();
         ComputationalGeometry.convexHullXZ(v, inHull, notInHull);

         Vector tri = new Vector();

         while (notInHull.size() > 2) {
            System.out.println("go");
            ComputationalGeometry.convexHullXZ(
               notInHull,
               inNextHull,
               notInNextHull);
            tri.add(ComputationalGeometry.naiveSingleTriangulationXZ(inHull, inNextHull));
            inHull = (Vector)inNextHull.clone();
            notInHull = (Vector)notInNextHull.clone();
            System.out.println(inHull.size() + " " + notInHull.size());
            inNextHull.clear();
            notInNextHull.clear();
         }

         return tri;

      }

      private final class SurfacePlot3DControl extends JPanel implements ActionListener {

         private JPanel properties;
         private JComboBox xsel, ysel, zsel;
         private ColorPanel color_low, color_high;
         private JButton refresh;

         private Vector labels;
         private HashMap labelmap;

         public SurfacePlot3DControl() {

            properties = new JPanel();

            labels = new Vector();
            labelmap = new HashMap();

            Column column;
            int index = 0;
            for (int count = 0; count < input.getNumColumns(); count++) {
               column = input.getColumn(count);
               if (column instanceof NumericColumn) {
                  labels.add((String)input.getColumnLabel(count));
                  labelmap.put(new Integer(index++), new Integer(count));
               }
            }

            xsel = new JComboBox(labels);
            ysel = new JComboBox(labels);
            zsel = new JComboBox(labels);

            color_low = new ColorPanel(new Color(255, 0, 0));
            color_high = new ColorPanel(new Color(255, 255, 0));

            properties.setBorder(new TitledBorder("Properties: "));
            properties.setLayout(new GridBagLayout());
            Constrain.setConstraints(
               properties, new JLabel("X Axis: "), 0, 0, 2, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, xsel, 2, 0, 4, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, new JLabel("Y Axis: "), 0, 1, 2, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, ysel, 2, 1, 2, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, new JLabel("Z Axis: "), 0, 2, 2, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, zsel, 2, 2, 2, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, new JLabel("Low color: "), 0, 3, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, color_low, 1, 3, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, new JLabel("High color: "), 2, 3, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, color_high, 3, 3, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);

            refresh = new JButton("Refresh");
            refresh.addActionListener(this);

            setMinimumSize(new Dimension(0, 0));
            setLayout(new GridBagLayout());
            Constrain.setConstraints(
               this, properties, 0, 0, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(this, refresh, 0, 1, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);

         }

         public Color getLowColor() {
            return color_low.getColor();
         }

         public Color getHighColor() {
            return color_high.getColor();
         }

         public void actionPerformed(ActionEvent event) {

            if (event.getSource() != refresh
                || xsel.getSelectedIndex() == -1
                || ysel.getSelectedIndex() == -1
                || zsel.getSelectedIndex() == -1)
               return;

            int x = ((Integer)labelmap.get(new Integer(xsel.getSelectedIndex()))).intValue(),
                y = ((Integer)labelmap.get(new Integer(ysel.getSelectedIndex()))).intValue(),
                z = ((Integer)labelmap.get(new Integer(zsel.getSelectedIndex()))).intValue();

            for (int i = 0; i < points.length; i++) {

               points[i] = new Point3d(
                  input.getDouble(i, x), input.getDouble(i, y), input.getDouble(i, z));

            }

            findMinMax();

            objTrans.setChild(drawAxes(), 0);

            if (show_vertices)
               objTrans.setChild(drawVertices(), 1);
            else
               objTrans.setChild(new BranchGroup(), 1);

            if (show_triangles)
               objTrans.setChild(drawTriangles(), 2);
            else
               objTrans.setChild(new BranchGroup(), 2);

         }

      }

      public final class ColorPanel extends JPanel implements ActionListener {

         private JPanel panel;
         private JButton edit;
         private Color color;

         public ColorPanel(Color c) {
            edit = new JButton("Edit");
            edit.addActionListener(this);
            color = c;

            panel = new JPanel();
            panel.setBackground(color);
            panel.setSize(new Dimension(15, 15));

            add(panel);
            add(edit);
         }

         public void actionPerformed(ActionEvent event) {
            color = JColorChooser.showDialog(this, "Edit Color: ", color);
            panel.setBackground(color);
         }

         public Color getColor() { return color; }

         public void setColor(Color c) { color = c; }

      }

      private final class SurfacePlot3DCanvas extends Canvas3D {

         public SurfacePlot3DCanvas() {
            super(SimpleUniverse.getPreferredConfiguration());
         }

         public Dimension getMinimumSize() {
            return new Dimension(0, 0);
         }

         public Dimension getPreferredSize() {
            return new Dimension(400, 400);
         }

      }

      private final class HelpWindow extends JD2KFrame {
         public HelpWindow() {
            super("About SurfacePlot3D");
            JEditorPane ep = new JEditorPane("text/html", getHelpString());
            getContentPane().add(new JScrollPane(ep));
            setSize(400, 200);
         }
      }

      private final class HelpListener implements ActionListener {
         public void actionPerformed(ActionEvent e) {
            helpWindow.setVisible(true);
         }
      }

      private final String getHelpString() {
         return "All your base are belong to us";
      }

      private final Color3f interpolateColor(Color L, Color H, double f) {

         if (f < 0)
            f *= -1;

         double r, g, b;

         if (L.getRed() > H.getRed())
            r = (double)((L.getRed() - H.getRed()) * f + H.getRed());
         else
            r = (double)((H.getRed() - L.getRed()) * f + L.getRed());

         if (L.getGreen() > H.getGreen())
            g = (double)((L.getGreen() - H.getGreen()) * f + H.getGreen());
         else
            g = (double)((H.getGreen() - L.getGreen()) * f + L.getGreen());

         if (L.getBlue() > H.getBlue())
            b = (double)((L.getBlue() - H.getBlue()) * f + H.getBlue());
         else
            b = (double)((H.getBlue() - L.getBlue()) * f + L.getBlue());

         return new Color3f((float)r/255, (float)g/255, (float)b/255);

      }

   }

}
