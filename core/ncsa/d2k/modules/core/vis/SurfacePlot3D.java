package ncsa.d2k.modules.core.vis;

import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.event.*;
import java.lang.StrictMath;
import java.util.HashMap;
import java.util.Vector;
import javax.media.j3d.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.vecmath.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.controller.userviews.widgits.*;
import ncsa.d2k.gui.JD2KFrame;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.vis.widgets.ComputationalGeometry;
import ncsa.gui.Constrain;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * <code>SurfacePlot3D</code> is a three-dimensional visualization of
 * <code>Table</code> data as a surface plot. Java3D is required.
 *
 * @author gpape
 */

public class SurfacePlot3D extends VisModule {

   public String getModuleInfo() {
      return "SurfacePlot3D is a three-dimensional visualization of Table data as a surface plot.";
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.modules.core.datatype.table.Table"}; return i;
   }

   public String[] getOutputTypes() {
      return null;
   }

   public String getInputInfo(int index) {
      if (index == 0) return "The Table to be visualized.";
      else return "SurfacePlot3D has no such input.";
   }

   public String getOutputInfo(int index) {
      return "SurfacePlot3D has no outputs.";
   }

   public String[] getFieldNameMapping() { return null; }
   protected UserView createUserView() { return new SurfacePlot3DVis(); }

   private class SurfacePlot3DVis extends JUserPane {

      private SurfacePlot3DControl control;
      private SurfacePlot3DCanvas canvas;
      private BranchGroup objRoot;
      private Transform3D scale, transform;
      private TransformGroup objScale, objTrans;

      private Table input;
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

      private static final String FONT_TYPE = "Helvetica";
      private static final int FONT_SIZE = 14;

      public void initView(ViewModule m) { }
      public void setInput(Object o, int i) { if (i == 0) go((Table)o); }
      public Object getMenu() { return menuBar; }

      private void go(Table table) {

         input = table;
         points = new Point3d[input.getNumRows()];

         /* initialize data structures                                      */

         control = new SurfacePlot3DControl();
         canvas = new SurfacePlot3DCanvas();

         show_vertices = show_triangles = true;

         /* create java3d universe                                          */

         SimpleUniverse su = new SimpleUniverse(canvas);
         su.getViewingPlatform().setNominalViewingTransform();

         BranchGroup scene = createInitialScene();
         scene.compile();

         su.addBranchGraph(scene);

         /* set vis layout                                                  */

         JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            control, canvas);
         split.setOneTouchExpandable(true);

         setLayout(new BorderLayout());
         add(split, BorderLayout.CENTER);

         /* set up menu                                                     */

         helpWindow = new HelpWindow();
         menuBar = new JMenuBar();
         JMenu helpMenu = new JMenu("Help");
         helpItem = new JMenuItem("About SurfacePlot3D...");
         helpItem.addActionListener(new HelpListener());
         helpMenu.add(helpItem);
         menuBar.add(helpMenu);

      }

      private BranchGroup createInitialScene() {

         objRoot = new BranchGroup();
         objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
         objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

         transform = new Transform3D();
         objTrans = new TransformGroup(transform);
         objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
         objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
         objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
         objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

         scale = new Transform3D();
         objScale = new TransformGroup(scale);
         objScale.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
         objScale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         objScale.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
         objScale.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
         objScale.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

         BranchGroup dummy = new BranchGroup();         // just to have a BG
         dummy.setCapability(BranchGroup.ALLOW_DETACH); // between scale and
                                                        // trans

         objRoot.addChild(objScale);
         objScale.addChild(dummy);
         dummy.addChild(objTrans);

         MouseRotate mr = new MouseRotate(objScale);
         mr.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(mr);

         MouseZoom mz = new MouseZoom(objScale);
         mz.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(mz);

         MouseTranslate mt = new MouseTranslate(objScale);
         mt.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(mt);

         KeyNavigatorBehavior keyNav = new KeyNavigatorBehavior(objScale);
         keyNav.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(keyNav);

         BranchGroup b0 = new BranchGroup();
         b0.setCapability(BranchGroup.ALLOW_DETACH);
         BranchGroup b1 = new BranchGroup();
         b1.setCapability(BranchGroup.ALLOW_DETACH);
         BranchGroup b2 = new BranchGroup();
         b2.setCapability(BranchGroup.ALLOW_DETACH);
         BranchGroup b3 = new BranchGroup();
         b3.setCapability(BranchGroup.ALLOW_DETACH);
         objTrans.addChild(b0); // child 0: axes
         objTrans.addChild(b1); // child 1: vertices
         objTrans.addChild(b2); // child 2: triangles
         objTrans.addChild(b3); // child 3: labels

         Background background = new Background(backgroundColor);
         background.setApplicationBounds(
            new BoundingSphere(new Point3d(0.0d, 0.0d, 0.0d), 1000));
         objRoot.addChild(background);

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

      private BranchGroup drawLabels(double xx, double yy, double zz) {

         BranchGroup L = new BranchGroup();
         L.setCapability(BranchGroup.ALLOW_DETACH);

         Text2D text;
         Transform3D T3D;
         TransformGroup TG;

         // xmin
         T3D = new Transform3D();
         T3D.setTranslation(new Vector3d(xmin + .1*(xmax - xmin), ymin - yy*FONT_SIZE/256, zmax));
         T3D.setScale(new Vector3d(xx, yy, zz));
         TG = new TransformGroup(T3D);
         text = new Text2D("-X", plainColor, FONT_TYPE, FONT_SIZE, 1);
         TG.addChild(text);
         L.addChild(TG);

         // xmax
         T3D = new Transform3D();
         T3D.setTranslation(new Vector3d(xmax - .1*(xmax - xmin), ymin - yy*FONT_SIZE/256, zmax));
         T3D.setScale(new Vector3d(xx, yy, zz));
         TG = new TransformGroup(T3D);
         text = new Text2D("+X", plainColor, FONT_TYPE, FONT_SIZE, 1);
         TG.addChild(text);
         L.addChild(TG);

         // ymin
         T3D = new Transform3D();
         T3D.setTranslation(new Vector3d(xmax, ymin + .1*(ymax - ymin), zmax));
         T3D.setScale(new Vector3d(xx, yy, zz));
         TG = new TransformGroup(T3D);
         text = new Text2D("-Y", plainColor, FONT_TYPE, FONT_SIZE, 1);
         TG.addChild(text);
         L.addChild(TG);

         // ymax
         T3D = new Transform3D();
         T3D.setTranslation(new Vector3d(xmax, ymax - .1*(ymax - ymin), zmax));
         T3D.setScale(new Vector3d(xx, yy, zz));
         TG = new TransformGroup(T3D);
         text = new Text2D("+Y", plainColor, FONT_TYPE, FONT_SIZE, 1);
         TG.addChild(text);
         L.addChild(TG);

         // zmin
         T3D = new Transform3D();
         T3D.setTranslation(new Vector3d(xmax, ymin - yy*FONT_SIZE/256, zmin + .1*(zmax - zmin)));
         T3D.setScale(new Vector3d(xx, yy, zz));
         TG = new TransformGroup(T3D);
         text = new Text2D("-Z", plainColor, FONT_TYPE, FONT_SIZE, 1);
         TG.addChild(text);
         L.addChild(TG);

         // zmax
         T3D = new Transform3D();
         T3D.setTranslation(new Vector3d(xmax, ymin - yy*FONT_SIZE/256, zmax - .1*(zmax - zmin)));
         T3D.setScale(new Vector3d(xx, yy, zz));
         TG = new TransformGroup(T3D);
         text = new Text2D("+Z", plainColor, FONT_TYPE, FONT_SIZE, 1);
         TG.addChild(text);
         L.addChild(TG);

         return L;

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

         Vector V = triangulate();

         BranchGroup b = new BranchGroup();
         b.setCapability(BranchGroup.ALLOW_DETACH);

         Shape3D s; Appearance a;

         Color lc = control.getLowColor(),
               hc = control.getHighColor();

         Point3d p0, p1, p2;
         double midpoint;
         for (int j = 0; j < V.size(); j += 3) {

            s = new Shape3D();
            a = new Appearance();

            p0 = (Point3d)V.get(j);
            p1 = (Point3d)V.get(j + 1);
            p2 = (Point3d)V.get(j + 2);

            if (p0 == null || p1 == null || p2 == null)
               break;

            midpoint = (p0.y + p1.y + p2.y) / 3;

            a.setColoringAttributes(new ColoringAttributes( interpolateColor(lc, hc,
               ((ymax - midpoint) / (ymax - ymin))), ColoringAttributes.FASTEST));

            TriangleArray t1 = new TriangleArray(6, TriangleArray.COORDINATES);

            t1.setCoordinate(0, p0);
            t1.setCoordinate(1, p1);
            t1.setCoordinate(2, p2);
            t1.setCoordinate(3, p2);
            t1.setCoordinate(4, p1);
            t1.setCoordinate(5, p0);

            s.setGeometry(t1);
            s.setAppearance(a);

            b.addChild(s);

         }

         return b;

      }

      private Vector triangulate() {

         /* remove duplicate (x, *, z) pairs, since we'll be projecting     */
         /* onto the x-z plane.                                             */

         HashMap map = new HashMap();

         Point2D.Double P;
         for (int i = 0; i < points.length; i++) {

            P = new Point2D.Double(points[i].x, -points[i].z);
            if (map.containsKey(P)) {
               Point3d old = (Point3d)map.get(P);
               if (points[i].y > old.y)
                  map.put(P, points[i]);
            }
            else {
               map.put(P, points[i]);
            }

         }

         /* map now contains all the relevant vertices as values. their     */
         /* keys are the corresponding projections into the x-z plane. we   */
         /* want to iterate over them to build a Vector.                    */

         Vector V = new Vector();

         Object[] relevant2D = map.keySet().toArray();
         for (int i = 0; i < relevant2D.length; i++)
            V.add(relevant2D[i]);

         // first convex hull:

         Vector all = new Vector(), // vector of vectors of points
                current;

         Vector[] hullInfo = ComputationalGeometry.convexHull(V);
         Vector inHull = hullInfo[0], notInHull = hullInfo[1];

         while (notInHull.size() > 2) {

            hullInfo = ComputationalGeometry.convexHull(notInHull);
            current = ComputationalGeometry.holeTriangulation
               (inHull, hullInfo[0]);
            inHull = hullInfo[0];
            notInHull = hullInfo[1];

            for (int i = 0; i < current.size(); i++)
               all.add((Point3d)map.get((Point2D.Double)current.get(i)));

         }

         if (inHull.size() > 2) {

            current = ComputationalGeometry.holeTriangulation
               (inHull, notInHull);

            for (int i = 0; i < current.size(); i++)
               all.add((Point3d)map.get((Point2D.Double)current.get(i)));

         }

         return all;

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
            // high and low colors got switched somehow, so we account for
            // that by switching the labels here.
            Constrain.setConstraints(
               properties, new JLabel("High color: "), 0, 3, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, color_low, 1, 3, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(
               properties, new JLabel("Low color: "), 2, 3, 1, 1,
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

            double xx = xmax - xmin,
                   yy = ymax - ymin,
                   zz = zmax - zmin;

            transform.setTranslation(new Vector3d(
               - ((xx / 2) + xmin),
               - ((yy / 2) + ymin),
               - ((zz / 2) + zmin)
            ));

            if (xx == 0) xx = 1;
            if (yy == 0) yy = 1;
            if (zz == 0) zz = 1;

            scale.setScale(new Vector3d(1/xx, 1/yy, 1/zz));

            objScale.setTransform(scale);
            objTrans.setTransform(transform);

            objTrans.setChild(drawAxes(), 0);

            if (show_vertices)
               objTrans.setChild(drawVertices(), 1);
            else
               objTrans.setChild(new BranchGroup(), 1);

            if (show_triangles)
               objTrans.setChild(drawTriangles(), 2);
            else
               objTrans.setChild(new BranchGroup(), 2);

            if (show_vertices || show_triangles) {
               objTrans.setChild(drawLabels(xx, yy, zz), 3);
            }

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
            setSize(400, 400);
         }
      }

      private final class HelpListener implements ActionListener {
         public void actionPerformed(ActionEvent e) {
            helpWindow.setVisible(true);
         }
      }

      private final String getHelpString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<html><body><h2>SurfacePlot3D</h2>");
        sb.append("This module visualizes a data set in three dimensions.");
        sb.append("It is intended to uncover patterns in data; random data ");
        sb.append("may not produce appealing plots.");
        sb.append("<h3>Keyboard controls</h3>");
        sb.append("<ul><li>number pad -: zoom out");
        sb.append("<li>number pad +: zoom in");
        sb.append("<li>up arrow: move scene back");
        sb.append("<li>down arrow: move scene forward");
        sb.append("<li>left arrow: rotate scene about +y axis");
        sb.append("<li>right arrow: rotate scene about -y axis");
        sb.append("<li>Page Up: rotate scene about -x axis");
        sb.append("<li>Page Down: rotate scene about +x axis");
        sb.append("</ul>");
        sb.append("<h3>Mousing Functions</h3>");
        sb.append("<ul><li>Drag with left mouse button: rotate scene");
        sb.append("<li>Drag with right mouse button: move scene");
        sb.append("<li>Drag with middle mouse button: zoom scene");
        sb.append("<li>'Refresh' button: bring scene to home view");
        sb.append("</ul></body></html>");
        return sb.toString();
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

   } // SurfacePlot3DVis

} // SurfacePlot3D
