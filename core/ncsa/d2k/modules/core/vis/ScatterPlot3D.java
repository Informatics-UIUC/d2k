package  ncsa.d2k.modules.core.vis;

import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.media.j3d.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.vecmath.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.util.datatype.*;
import ncsa.gui.*;


/**
 * A 3-dimensional scatter plot.
 */
public class ScatterPlot3D extends VisModule implements Serializable {
    private static final String EMPTY = "";
    private static final String COMMA = ", ";
    private static final String SPACE = " ";

    /**
     * put your documentation comment here
     * @return
     */
    public String getModuleInfo () {
        return  "ScatterPlot3D is a three-dimensional visualization of " +
                "VerticalTable data as a scatter plot.";
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getInputTypes () {
        String[] i =  { "ncsa.d2k.util.datatype.VerticalTable" };
        return  i;
    }

    /**
     * put your documentation comment here
     * @param index
     * @return
     */
    public String getInputInfo (int index) {
        if (index == 0)
            return  "The VerticalTable to be visualized.";
        else
            return  "ScatterPlot3D has no such input.";
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getOutputTypes () {
        return  null;
    }

    /**
     * put your documentation comment here
     * @param index
     * @return
     */
    public String getOutputInfo (int index) {
        return  "ScatterPlot3D has no outputs.";
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getFieldNameMapping () {
        return  null;
    }

    /**
     * put your documentation comment here
     * @return
     */
    protected UserView createUserView () {
        return  new ScatterPlot3DView();
    }

	private static final int X_AXIS = 0;
	private static final int Y_AXIS = 1;
	private static final int Z_AXIS = 2;
	private static final int X_SCALE = 3;
	private static final int Y_SCALE = 4;
	private static final int Z_SCALE = 5;

    private class ScatterPlot3DView extends JUserPane implements Serializable {
        private ScatterPlot3DControl control;
        private VerticalTable table;
        private ScatterPlot3DCanvas canvas;
      	private BranchGroup objRoot = new BranchGroup();
      	private TransformGroup objTrans = new TransformGroup();

        /**
         * put your documentation comment here
         * @param m
         */
        public void initView (ViewModule m) {}

        /**
         * put your documentation comment here
         * @param o
         * @param i
         */
        public void setInput (Object o, int i) {
            if (i == 0) {
                table = (VerticalTable)o;
                execute();
            }
        }

        /**
         * put your documentation comment here
         */
        private void execute () {
            control = new ScatterPlot3DControl();
            canvas = new ScatterPlot3DCanvas(SimpleUniverse.getPreferredConfiguration());
            //rebuild(new ScatterPlot3DGraphSettings(), null);

            // create scene
            SimpleUniverse u = new SimpleUniverse(canvas);
            u.getViewingPlatform().setNominalViewingTransform();
            BranchGroup scene = createSceneGraph(null, null);
            scene.compile();
            u.addBranchGraph(scene);

            setLayout(new BorderLayout());
            add(control, BorderLayout.WEST);
            add(canvas, BorderLayout.CENTER);
        }

        /**
         * put your documentation comment here
         * @param settings
         * @param set
         */
        void rebuild (ScatterPlot3DGraphSettings settings, ScatterPlot3DDataSet[] set) {
            //canvas = new ScatterPlot3DCanvas(SimpleUniverse.getPreferredConfiguration());
            //scene = createSceneGraph(settings, set);
            //for(int i = 0; i <
            /*BranchGroup s1 = createSceneGraph(settings, set);
            s1.compile();
            if (universe == null) {
                universe = new SimpleUniverse(canvas);
                universe.getViewingPlatform().setNominalViewingTransform();
            }
            universe.addBranchGraph(s1);
			*/
			addScene(settings, set);
        }

        private void addScene(ScatterPlot3DGraphSettings settings,
			ScatterPlot3DDataSet[] set) {

         	BranchGroup total = new BranchGroup();
         	Transform3D t = new Transform3D();
         	total.setCapability(BranchGroup.ALLOW_DETACH);

            Appearance[] a = new Appearance[set.length];
            for (int count = 0; count < set.length; count++) {
                a[count] = new Appearance();
                a[count].setColoringAttributes(new ColoringAttributes(new Color3f(set[count].color),
                        ColoringAttributes.FASTEST));
            }
            Sphere s;
            TransformGroup tg;
            double dx, dy, dz;
            for (int count = 0; count < set.length; count++) {
                for (int count2 = 0; count2 < table.getNumRows(); count2++) {
                    s = new Sphere(0.01f);
                    // !: different data set scales?
                    s.setAppearance(a[count]);
                    dx = table.getDouble(count2, set[count].x);
                    dy = table.getDouble(count2, set[count].y);
                    dz = table.getDouble(count2, set[count].z);
                    t = new Transform3D();
                    t.setTranslation(new Vector3d(.15*dx, .15*dy, .15*(-dz)));
                    tg = new TransformGroup(t);
                    tg.addChild(s);
					BranchGroup b = new BranchGroup();
		    		b.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
         			b.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
					b.addChild(tg);
                    objTrans.addChild(b);
                }
            }
		}

        /**
         * put your documentation comment here
         * @param settings
         * @param set
         * @return
         */
        private BranchGroup createSceneGraph (ScatterPlot3DGraphSettings settings,
                ScatterPlot3DDataSet[] set) {
            objRoot = new BranchGroup();
		    objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
         	objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

            Transform3D transform = new Transform3D();
            //TransformGroup objTrans = new TransformGroup(transform);

            //objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
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
            // create axes
            LineArray xaxis = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
            LineArray yaxis = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
            LineArray zaxis = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
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
            objTrans.addChild(shape);           // child 1
            shape = new Shape3D(yaxis);
            objTrans.addChild(shape);           // child 2
            shape = new Shape3D(zaxis);
            objTrans.addChild(shape);           // child 3
            objRoot.addChild(objTrans);
            KeyNavigatorBehavior keyNav = new KeyNavigatorBehavior(objTrans);
            keyNav.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
            objRoot.addChild(keyNav);

            if (set == null)
                return  objRoot;

            Appearance[] a = new Appearance[set.length];
            for (int count = 0; count < set.length; count++) {
                a[count] = new Appearance();
                a[count].setColoringAttributes(new ColoringAttributes(new Color3f(set[count].color),
                        ColoringAttributes.FASTEST));
            }
            Sphere s;
            Transform3D t;
            TransformGroup tg;
            double dx, dy, dz;
            for (int count = 0; count < set.length; count++) {
                for (int count2 = 0; count2 < table.getNumRows(); count2++) {
                    s = new Sphere(0.01f);
                    // !: different data set scales?
                    s.setAppearance(a[count]);
                    dx = table.getDouble(count2, set[count].x);
                    dy = table.getDouble(count2, set[count].y);
                    dz = table.getDouble(count2, set[count].z);
                    t = new Transform3D();
                    t.setTranslation(new Vector3d(.15*dx, .15*dy, .15*(-dz)));
                    tg = new TransformGroup(t);
                    tg.addChild(s);
                    objTrans.addChild(tg);
                }
            }
			return objRoot;
        }

        /**
         * The controls
         */
        private class ScatterPlot3DControl extends JPanel
                implements ActionListener, Serializable {
            private JTabbedPane tabbed;
            private JPanel editlist, properties;
            // Edit/List panel objects
            private JLabel edit_name_label, edit_color_label, edit_xvar_label,
                    edit_yvar_label, edit_zvar_label;
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

            /**
             * put your documentation comment here
             */
            ScatterPlot3DControl () {
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
                for (int count = 0; count < table.getNumColumns(); count++) {
                    column = table.getColumn(count);
                    if (column instanceof NumericColumn) {
                        labels.add((String)table.getColumnLabel(count));
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
                Constrain.setConstraints(editpanel, edit_name_label, 0, 0,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(editpanel, edit_name_field, 1, 0,
                        1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(editpanel, edit_color_label, 0, 1,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(editpanel, edit_color, 1, 1, 1, 1,
                        GridBagConstraints.NONE, GridBagConstraints.WEST, 0,
                        0);
                Constrain.setConstraints(editpanel, edit_xvar_label, 0, 2,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(editpanel, edit_xbox, 1, 2, 1, 1,
                        GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(editpanel, edit_yvar_label, 0, 3,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(editpanel, edit_ybox, 1, 3, 1, 1,
                        GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(editpanel, edit_zvar_label, 0, 4,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(editpanel, edit_zbox, 1, 4, 1, 1,
                        GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(editpanel, edit_add, 1, 5, 1, 1, GridBagConstraints.NONE,
                        GridBagConstraints.EAST, 1, 1);
                // List subpanel
                edit_list = new JList(new DefaultListModel());
                edit_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                edit_listmodel = (DefaultListModel)edit_list.getModel();
                edit_delete = new JButton("Delete");
                edit_delete.addActionListener(this);
                edit_scroll = new JScrollPane(edit_list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                JPanel listpanel = new JPanel();
                listpanel.setBorder(new TitledBorder("List: "));
                listpanel.setLayout(new GridBagLayout());
                Constrain.setConstraints(listpanel, edit_scroll, 0, 0, 1, 1,
                        GridBagConstraints.BOTH, GridBagConstraints.WEST, 1,
                        .5);
                Constrain.setConstraints(listpanel, edit_delete, 0, 1, 1, 1,
                        GridBagConstraints.NONE, GridBagConstraints.NORTHEAST,
                        0, .5);
                // Edit/List panel
                editlist = new JPanel();
                editlist.setLayout(new GridBagLayout());
                Constrain.setConstraints(editlist, editpanel, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                        GridBagConstraints.NORTHWEST, 0, 0);
                Constrain.setConstraints(editlist, listpanel, 0, 1, 1, 1, GridBagConstraints.BOTH,
                        GridBagConstraints.WEST, 1, 1);
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
                Constrain.setConstraints(properties, prop_xmin_label, 0, 0,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_xmin_field, 1, 0,
                        1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_xmax_label, 2, 0,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_xmax_field, 3, 0,
                        1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_ymin_label, 0, 1,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_ymin_field, 1, 1,
                        1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        1, 0);
                Constrain.setConstraints(properties, prop_ymax_label, 2, 1,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_ymax_field, 3, 1,
                        1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        1, 0);
                Constrain.setConstraints(properties, prop_zmin_label, 0, 3,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_zmin_field, 1, 3,
                        1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        1, 0);
                Constrain.setConstraints(properties, prop_zmax_label, 2, 3,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_zmax_field, 3, 3,
                        1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        1, 0);
                Constrain.setConstraints(properties, prop_title_label, 0, 4,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_title_field, 1, 4,
                        3, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_xaxis_label, 0, 5,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_xaxis_field, 1, 5,
                        3, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_yaxis_label, 0, 6,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_yaxis_field, 1, 6,
                        3, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_zaxis_label, 0, 7,
                        1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_zaxis_field, 1, 7,
                        3, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_grid, 0, 8, 1, 1,
                        GridBagConstraints.NONE, GridBagConstraints.NORTHWEST,
                        0, 0);
                Constrain.setConstraints(properties, prop_legend, 0, 9, 1,
                        1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST,
                        0, 1);
                // Tab the control panels
                tabbed = new JTabbedPane(SwingConstants.TOP);
                tabbed.add("Scatter Plot", editlist);
                tabbed.add("Settings", properties);
                edit_refresh = new JButton("Refresh");
                edit_refresh.addActionListener(this);
                // ...and finish
                setMinimumSize(new Dimension(0, 0));
                setLayout(new GridBagLayout());
                Constrain.setConstraints(this, tabbed, 0, 0, 1, 1, GridBagConstraints.BOTH,
                        GridBagConstraints.NORTHWEST, 1, 1);
                Constrain.setConstraints(this, edit_refresh, 0, 1, 1, 1, GridBagConstraints.NONE,
                        GridBagConstraints.NORTHWEST, 0, 0);
            }

            /**
             * put your documentation comment here
             * @param event
             */
            public void actionPerformed (ActionEvent event) {
                Object src = event.getSource();
                if (src == edit_add) {
                    String name = edit_name_field.getText();
                    if (name.equals(EMPTY))
                        return;
                    Color color = edit_color.getColor();
                    int index = edit_xbox.getSelectedIndex();
                    Integer X = (Integer)map.get(new Integer(index));
                    String label = table.getColumnLabel(X.intValue());
                    name = name + " " + label;
                    index = edit_ybox.getSelectedIndex();
                    Integer Y = (Integer)map.get(new Integer(index));
                    label = table.getColumnLabel(Y.intValue());
                    name = name + COMMA + label;
                    index = edit_zbox.getSelectedIndex();
                    Integer Z = (Integer)map.get(new Integer(index));
                    label = table.getColumnLabel(Z.intValue());
                    name = name + COMMA + label;
                    edit_listmodel.addElement(new ScatterPlot3DDataSet(name,
                            color, X.intValue(), Y.intValue(), Z.intValue()));
                    edit_name_field.setText(EMPTY);
                }
                else if (src == edit_delete) {
                    int index = edit_list.getSelectedIndex();
                    if (index != -1)
                        edit_listmodel.removeElementAt(index);
                }
                else if (src == edit_refresh) {
                    int size = edit_listmodel.getSize();
                    if (size == 0) {
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
                    String value;
                    Integer Xmin, Xmax, Ymin, Ymax, Zmin, Zmax;
                    value = prop_xmin_field.getText();
                    if (value.equals(EMPTY))
                        Xmin = null;
                    else
                        Xmin = new Integer(value);
                    value = prop_xmax_field.getText();
                    if (value.equals(EMPTY))
                        Xmax = null;
                    else
                        Xmax = new Integer(value);
                    value = prop_ymin_field.getText();
                    if (value.equals(EMPTY))
                        Ymin = null;
                    else
                        Ymin = new Integer(value);
                    value = prop_ymax_field.getText();
                    if (value.equals(EMPTY))
                        Ymax = null;
                    else
                        Ymax = new Integer(value);
                    value = prop_zmin_field.getText();
                    if (value.equals(EMPTY))
                        Zmin = null;
                    else
                        Zmin = new Integer(value);
                    value = prop_zmax_field.getText();
                    if (value.equals(EMPTY))
                        Zmax = null;
                    else
                        Zmax = new Integer(value);
                    boolean display_grid = prop_grid.isSelected(), display_legend = prop_legend.isSelected();
                    ScatterPlot3DGraphSettings settings = new ScatterPlot3DGraphSettings(title,
                            xaxis, yaxis, zaxis, Xmin, Xmax, Ymin, Ymax, Zmin,
                            Zmax, 10, display_grid, true, display_legend, true,
                            true, true);
                    //vis.graphpane.removeAll();
                    // !: ...
                    rebuild(settings, set);
                    //Constrain.setConstraints(/*vis.*/graphpane, /*vis.graph.*/canvas, 0, 0,
                    //  1, 1, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1, 1);
                    ///*vis.*/graphpane.validate();
                    //vis.graphpane.repaint(); // !: do we need to do this for j3d?
                }
            }
        } // ScatterPlot3D$ScatterPlot3DControl


        /**
         * A data set for the scatterplot
         */
        private class ScatterPlot3DDataSet {
            String name;
            Color color;
            int x, y, z;

            /**
             * put your documentation comment here
             * @param             String name
             * @param             Color color
             * @param             int x
             * @param             int y
             * @param             int z
             */
            ScatterPlot3DDataSet (String name, Color color, int x, int y, int z) {
                this.name = name;
                this.color = color;
                this.x = x;
                this.y = y;
                this.z = z;
            }

            /**
             * put your documentation comment here
             * @return
             */
            public String toString () {
                return  name;
            }
        }

		/**
		 * The graph settings
		 */
        class ScatterPlot3DGraphSettings {
            String title_string, x, y, z;
            Integer Xmin, Xmax, Ymin, Ymax, Zmin, Zmax;
            int grid_size;
            boolean grid, scale, legend, ticks, title, labels;

            /**
             * put your documentation comment here
             */
            ScatterPlot3DGraphSettings () {
                title_string = EMPTY;
                x = EMPTY;
                y = EMPTY;
                z = EMPTY;
                Xmin = null;
                Xmax = null;
                Ymin = null;
                Ymax = null;
                Zmin = null;
                Zmax = null;
                grid_size = 10;
                grid = true;
                scale = true;
                legend = true;
                ticks = true;
                title = true;
                labels = true;
            }

            /**
             * put your documentation comment here
             * @param             String title_string
             * @param             String x
             * @param             String y
             * @param             String z
             * @param             Integer Xmin
             * @param             Integer Xmax
             * @param             Integer Ymin
             * @param             Integer Ymax
             * @param             Integer Zmin
             * @param             Integer Zmax
             * @param             int grid_size
             * @param             boolean grid
             * @param             boolean scale
             * @param             boolean legend
             * @param             boolean ticks
             * @param             boolean title
             * @param             boolean labels
             */
            ScatterPlot3DGraphSettings (String title_string, String x, String y,
                    String z, Integer Xmin, Integer Xmax, Integer Ymin, Integer Ymax,
                    Integer Zmin, Integer Zmax, int grid_size, boolean grid,
                    boolean scale, boolean legend, boolean ticks, boolean title,
                    boolean labels) {
                this.title_string = title_string;
                this.x = x;
                this.y = y;
                this.z = z;
                this.Xmin = Xmin;
                this.Xmax = Xmax;
                this.Ymin = Ymin;
                this.Ymax = Ymax;
                this.Zmin = Zmin;
                this.Zmax = Zmax;
                this.grid_size = 10;
                this.grid = grid;
                this.scale = scale;
                this.legend = legend;
                this.ticks = ticks;
                this.title = title;
                this.labels = labels;
            }
        }

        /**
         * A Canvas3D with a decent default size.
         */
        class ScatterPlot3DCanvas extends Canvas3D {

            /**
             * put your documentation comment here
             * @param             GraphicsConfiguration graphicsConfig
             */
            public ScatterPlot3DCanvas (GraphicsConfiguration graphicsConfig) {
                super(graphicsConfig);
            }

            /**
             * put your documentation comment here
             * @return
             */
            public Dimension getMinimumSize () {
                return  new Dimension(0, 0);
            }

            /**
             * put your documentation comment here
             * @return
             */
            public Dimension getPreferredSize () {
                return  new Dimension(400, 400);
            }
        }

        /**
         * Choose the color of the points.
         */
        private class ColorPanel extends JPanel
                implements ActionListener {
            private JPanel renderpanel;
            private JButton editorbutton;
            private Color defaultColor = Color.red;
            private Color color;

            /**
             * put your documentation comment here
             */
            ColorPanel () {
                editorbutton = new JButton("Edit");
                editorbutton.addActionListener(this);
                color = defaultColor;
                renderpanel = new JPanel();
                renderpanel.setBackground(color);
                renderpanel.setSize(new Dimension(10, 10));
                add(renderpanel);
                add(editorbutton);
            }

            /**
             * put your documentation comment here
             * @param event
             */
            public void actionPerformed (ActionEvent event) {
                color = JColorChooser.showDialog(this, "Edit Color", defaultColor);
                renderpanel.setBackground(color);
            }

            /**
             * put your documentation comment here
             * @return
             */
            Color getColor () {
                return  color;
            }

            /**
             * put your documentation comment here
             * @param color
             */
            void setColor (Color color) {
                this.color = color;
            }
        }
    } // ScatterPlot3D$ScatterPlot3DView
} // ScatterPlot3D



