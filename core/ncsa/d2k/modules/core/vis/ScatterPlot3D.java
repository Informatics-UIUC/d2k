package  ncsa.d2k.modules.core.vis;

import  com.sun.j3d.utils.behaviors.keyboard.*;
import  com.sun.j3d.utils.behaviors.mouse.*;
import  com.sun.j3d.utils.geometry.*;
import  com.sun.j3d.utils.universe.*;
import  java.awt.*;
import  java.awt.event.*;
import  java.io.*;
import  java.text.*;
import  java.util.*;
import  javax.media.j3d.*;
import  javax.swing.*;
import  javax.swing.border.*;
import  javax.vecmath.*;
import  ncsa.d2k.controller.userviews.swing.*;
import  ncsa.d2k.infrastructure.modules.*;
import  ncsa.d2k.infrastructure.views.*;
import  ncsa.d2k.util.datatype.*;
import  ncsa.gui.*;
import ncsa.d2k.gui.JD2KFrame;

/**
 * A three-dimensional scatter plot.  Requires Java3D.
 * Still to be done: add a legend.  Use J3DGraphics2D or display in 3d?
 */
public class ScatterPlot3D extends VisModule implements Serializable {

    /** used in the display of text */
    private static final String EMPTY = "";
    private static final String COMMA = ", ";
    private static final String SPACE = " ";
    private static final String COLON = ": ";
    private static final String FONT_TYPE = "Helvetica";
    private static final int FONT_SIZE = 14;
    private static final String DASH = " - ";
    private static final String X = "X";
    private static final String Y = "Y";
    private static final String Z = "Z";

    /** the child number of each component of the scene */
    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;
    private static final int Z_AXIS = 2;
    private static final int X_SCALE = 3;
    private static final int Y_SCALE = 4;
    private static final int Z_SCALE = 5;
    private static final int LEGEND = 6;
    private static final int NUM_STATIC_CHILDREN = 7;

    /** the multiplier that each coordinate is multiplied by */
    private static final double multFactor = .15;

    /** colors used */
	// lt. grey
    private static final Color3f axisColor = new Color3f(.5098f, .5098f, .5098f);
	//dk. grey
    private static final Color3f backgroundColor = new Color3f(.35294f, .35294f, .35294f);
	// lt. blue
    private static final Color3f plainColor = new Color3f(0.8f, 0.9254901f, .9568627f);
	// yellow
    private static final Color3f labelColor = new Color3f(1.0f, 1.0f, .4f);

    /**
	 * Return a description of this module.
     * @return a description
     */
    public String getModuleInfo () {
        return  "ScatterPlot3D is a three-dimensional visualization of " +
                "Table data as a scatter plot.";
    }

    /**
	 * The input types.
     * @return the input types
     */
    public String[] getInputTypes () {
        String[] i =  { "ncsa.d2k.util.datatype.Table" };
        return  i;
    }

    /**
	 * The input info
     * @param index the index of the input
     * @return that input's info
     */
    public String getInputInfo (int index) {
        if (index == 0)
            return  "The Table to be visualized.";
        else
            return  "ScatterPlot3D has no such input.";
    }

    /**
	 * The output types
     * @return the output types
     */
    public String[] getOutputTypes () {
        return  null;
    }

    /**
	 * The output info
     * @param index the index of the output
     * @return that output's info
     */
    public String getOutputInfo (int index) {
        return  "ScatterPlot3D has no outputs.";
    }

    /**
	 * not used.
     * @return null
     */
    public String[] getFieldNameMapping () {
        return  null;
    }

    /**
	 * Return the user view
     * @return the user view
     */
    protected UserView createUserView () {
        return  new ScatterPlot3DView();
    }

	/**
	 * The user view.
	 */
    private class ScatterPlot3DView extends JUserPane implements Serializable {
        /** the control panel */
        private ScatterPlot3DControl control;

        /** the table holding the data */
        private Table table;

        /** the canvas area */
        private ScatterPlot3DCanvas canvas;

        /** the settings */
        private ScatterPlot3DGraphSettings settings;

        /** the transform group to add/remove children from */
        private TransformGroup objTrans;

        /** the number of user-defined scenes displayed as of the last refresh */
        private int numScenes = 0;

        /** the max and min for each column */
        private double xMax = 5;
        private double xMin = -5;
        private double yMax = 5;
        private double yMin = -5;
        private double zMax = 5;
        private double zMin = -5;

        /** used to format numbers to strings */
        private NumberFormat nf;

        private HelpWindow helpWindow;
        private JMenuItem helpItem;
        private JMenuBar menuBar;

        /**
		 * not used.
         * @param m the module
         */
        public void initView (ViewModule m) {}

        /**
		 * receive an input
         * @param o the input object
         * @param i the index
         */
        public void setInput (Object o, int i) {
            if (i == 0) {
                table = (Table)o;
                execute();
            }
        }

        public Object getMenu() {
            return menuBar;
        }

        /**
         * Find the maximum and minimum for each column.  Iterate
         * over all data sets to find the maximum and minimum over all
         * the data sets.
		 * @param sets the data sets that will be shown
         */
        private void findMinMax (ScatterPlot3DDataSet[] sets) {
            xMax = 1;
            xMin = 0;
            yMax = 1;
            yMin = 0;
            zMax = 1;
            zMin = 0;
            for (int i = 0; i < sets.length; i++) {
                int x = sets[i].x;
                int y = sets[i].y;
                int z = sets[i].z;
                NumericColumn nc = (NumericColumn)table.getColumn(x);
                for (int j = 0; j < nc.getNumRows(); j++) {
                    double d = nc.getDouble(j);
                    if (d > xMax)
                        xMax = d;
                    if (d < xMin)
                        xMin = d;
                }
                nc = (NumericColumn)table.getColumn(y);
                for (int j = 0; j < nc.getNumRows(); j++) {
                    double d = nc.getDouble(j);
                    if (d > yMax)
                        yMax = d;
                    if (d < yMin)
                        yMin = d;
                }
                nc = (NumericColumn)table.getColumn(z);
                for (int j = 0; j < nc.getNumRows(); j++) {
                    double d = nc.getDouble(j);
                    if (d > zMax)
                        zMax = d;
                    if (d < zMin)
                        zMin = d;
                }
            }
        }

        /**
         * Initial set-up.
         */
        private void execute () {
            settings = new ScatterPlot3DGraphSettings();
            nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            control = new ScatterPlot3DControl();
            canvas = new ScatterPlot3DCanvas(SimpleUniverse.getPreferredConfiguration());
            // create universe
            SimpleUniverse u = new SimpleUniverse(canvas);
            u.getViewingPlatform().setNominalViewingTransform();
            BranchGroup scene = createInitialScene();
            scene.compile();
            u.addBranchGraph(scene);
            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    control, canvas);
            split.setOneTouchExpandable(true);
            setLayout(new BorderLayout());
            add(split, BorderLayout.CENTER);

            helpWindow = new HelpWindow();
            menuBar = new JMenuBar();
            JMenu hlp = new JMenu("Help");
            helpItem = new JMenuItem("About ScatterPlot3D..");
            helpItem.addActionListener(new HelpListener());
            hlp.add(helpItem);
            menuBar.add(hlp);
        }

        class HelpListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                helpWindow.setVisible(true);
            }
        }

        /**
         * Add a user-defined scene.
		 * @param set the data set to display in the scene
         */
        private void addScene (ScatterPlot3DDataSet set) {
            BranchGroup total = new BranchGroup();
            Transform3D t = new Transform3D();
            total.setCapability(BranchGroup.ALLOW_DETACH);
            Appearance a = new Appearance();
            a.setColoringAttributes(new ColoringAttributes(new Color3f(set.color),
                    ColoringAttributes.FASTEST));
            Sphere s;
            TransformGroup tg;
            double dx, dy, dz;
            for (int count2 = 0; count2 < table.getNumRows(); count2++) {
                s = new Sphere(0.01f);
                // !: different data set scales?
                s.setAppearance(a);
                dx = table.getDouble(count2, set.x);
                dy = table.getDouble(count2, set.y);
                dz = table.getDouble(count2, set.z);
                t = new Transform3D();
                t.setTranslation(new Vector3d(multFactor*dx, multFactor*dy,
                        multFactor*(dz)));
                tg = new TransformGroup(t);
                tg.addChild(s);
                total.addChild(tg);
            }
            objTrans.addChild(total);
        }

        /**
         * Create the initial scene.  This has the axes and their labels.
		 * @return the root branch group with the coordinate axes and their labels
         */
        private BranchGroup createInitialScene () {
            BranchGroup objRoot = new BranchGroup();
            objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            Transform3D transform = new Transform3D();
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
            KeyNavigatorBehavior keyNav = new KeyNavigatorBehavior(objTrans);
            keyNav.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
            objRoot.addChild(keyNav);
            // add the axes and their labels
            objTrans.addChild(drawXAxis());                     // child 0
            objTrans.addChild(drawYAxis());                     // child 1
            objTrans.addChild(drawZAxis());                     // child 2
            objTrans.addChild(drawXScale());                    // child 3
            objTrans.addChild(drawYScale());                    // child 4
            objTrans.addChild(drawZScale());                    // child 5
            objTrans.addChild(drawLegend());                    // child 6
            objRoot.addChild(objTrans);
            Background background = new Background(backgroundColor);
            background.setApplicationBounds(new BoundingSphere(new Point3d(0.0d,
                    0.0d, 0.0d), 1000));
            objRoot.addChild(background);
            return  objRoot;
        }

        /**
         * Update the coordinates axes.
         */
        private void updateAxes () {
            objTrans.setChild(drawXAxis(), X_AXIS);
            objTrans.setChild(drawYAxis(), Y_AXIS);
            objTrans.setChild(drawZAxis(), Z_AXIS);
        }

        /**
         * Update the labels and scales for the axes.
         */
        private void updateScales () {
            objTrans.setChild(drawXScale(), X_SCALE);
            objTrans.setChild(drawYScale(), Y_SCALE);
            objTrans.setChild(drawZScale(), Z_SCALE);
        }

        /**
         * put your documentation comment here
         */
        private void updateLegend () {
            objTrans.setChild(drawLegend(), LEGEND);
        }

        /**
         * Remove all the user-defined scenes from the canvas.  These nodes
         * are the points in the scatterplot.
         */
        private void removeAllUserScenes () {
            // the first NUM_STATIC_CHILDREN scenes are the axes and their labels,
            // remove all scenes with an index greater than NUM_STATIC_CHILDREN
            for (int i = NUM_STATIC_CHILDREN; i < numScenes + NUM_STATIC_CHILDREN; i++)
                objTrans.removeChild(NUM_STATIC_CHILDREN);
            numScenes = 0;
        }

        /**
		 * Create the legend
         * @return a BranchGroup with the legend
         */
        private BranchGroup drawLegend () {
            BranchGroup bg = new BranchGroup();
            bg.setCapability(BranchGroup.ALLOW_DETACH);
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            return  bg;
        }

        /**
         * Create the x-axis.
		 * @return a BranchGroup with the x-axis
         */
        private BranchGroup drawXAxis () {
            LineArray xaxis = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
            xaxis.setCoordinate(0, new Point3f((float)(settings.Xmin*multFactor),
                    0, 0));
            xaxis.setCoordinate(1, new Point3f((float)(settings.Xmax*multFactor),
                    0, 0));
            xaxis.setColor(0, axisColor);
            xaxis.setColor(1, axisColor);
            BranchGroup bg = new BranchGroup();
            bg.setCapability(BranchGroup.ALLOW_DETACH);
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            bg.addChild(new Shape3D(xaxis));
            return  bg;
        }

        /**
         * Create the y-axis.
		 * @return a BranchGroup with the y-axis
         */
        private BranchGroup drawYAxis () {
            LineArray yaxis = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
            yaxis.setCoordinate(0, new Point3f(0, (float)(settings.Ymin*multFactor),
                    0));
            yaxis.setCoordinate(1, new Point3f(0, (float)(settings.Ymax*multFactor),
                    0));
            yaxis.setColor(0, axisColor);
            yaxis.setColor(1, axisColor);
            BranchGroup bg = new BranchGroup();
            bg.setCapability(BranchGroup.ALLOW_DETACH);
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            bg.addChild(new Shape3D(yaxis));
            return  bg;
        }

        /**
         * Create the z-axis.
		 * @return a BranchGroup with the z-axis
         */
        private BranchGroup drawZAxis () {
            LineArray zaxis = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
            zaxis.setCoordinate(0, new Point3f(0, 0, (float)(settings.Zmin*multFactor)));
            zaxis.setCoordinate(1, new Point3f(0, 0, (float)(settings.Zmax*multFactor)));
            zaxis.setColor(0, axisColor);
            zaxis.setColor(1, axisColor);
            BranchGroup bg = new BranchGroup();
            bg.setCapability(BranchGroup.ALLOW_DETACH);
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            bg.addChild(new Shape3D(zaxis));
            return  bg;
        }

        /**
         * Draw the labels along the x-axis.
		 * @return a BranchGroup with the x axis labels
         */
        private BranchGroup drawXScale () {
            BranchGroup bg = new BranchGroup();
            bg.setCapability(BranchGroup.ALLOW_DETACH);
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            Text2D text;
            int numTicks = 3;
            double xInc = (settings.Xmax - settings.Xmin)/numTicks;
            double xLoc = settings.Xmin;
            for (int i = 0; i <= numTicks; i++) {
                if (xLoc != 0) {
                    Transform3D trans = new Transform3D();
                    trans.setTranslation(new Vector3d(multFactor*xLoc, 0, 0));
                    TransformGroup tg = new TransformGroup(trans);
                    tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
                    text = new Text2D(nf.format(xLoc), plainColor, FONT_TYPE,
                            FONT_SIZE, 1);
                    tg.addChild(text);
                    bg.addChild(tg);
                }
                xLoc += xInc;
            }

            xLoc += xInc/3;
            Transform3D trans = new Transform3D();
            trans.setTranslation(new Vector3d(multFactor*xLoc, 0, 0));
            TransformGroup tg = new TransformGroup(trans);
            tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
            StringBuffer sb = new StringBuffer(X);
            if (settings.x.length() > 0) {
                sb.append(DASH);
                sb.append(settings.x);
            }
            text = new Text2D(sb.toString(), labelColor, FONT_TYPE, FONT_SIZE,
                    1);
            tg.addChild(text);
            bg.addChild(tg);
            return  bg;
        }

        /**
         * Draw the labels along the y-axis.  The graph label is also drawn
         * at the top of the y-axis.
		 * @return a BranchGroup with the y axis labels
         */
        private BranchGroup drawYScale () {
            BranchGroup bg = new BranchGroup();
            bg.setCapability(BranchGroup.ALLOW_DETACH);
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            Text2D text;
            int numTicks = 3;
            double yInc = (settings.Ymax - settings.Ymin)/numTicks;
            double yLoc = settings.Ymin;
            for (int i = 0; i <= numTicks; i++) {
                if (yLoc != 0) {
                    Transform3D trans = new Transform3D();
                    trans.setTranslation(new Vector3d(0, multFactor*yLoc, 0));
                    TransformGroup tg = new TransformGroup(trans);
                    tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
                    text = new Text2D(nf.format(yLoc), plainColor, FONT_TYPE,
                            FONT_SIZE, 1);
                    tg.addChild(text);
                    bg.addChild(tg);
                }
                yLoc += yInc;
            }
            yLoc += yInc/3;
            Transform3D trans = new Transform3D();
            trans.setTranslation(new Vector3d(0, multFactor*yLoc, 0));
            TransformGroup tg = new TransformGroup(trans);
            tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
            StringBuffer sb = new StringBuffer(Y);
            if (settings.y.length() > 0) {
                sb.append(DASH);
                sb.append(settings.y);
            }
            text = new Text2D(sb.toString(), labelColor, FONT_TYPE, FONT_SIZE,
                    1);
            tg.addChild(text);
            bg.addChild(tg);
            if (settings.title_string.length() > 0) {
                yLoc += yInc/3;
                Transform3D t2 = new Transform3D();
                t2.setTranslation(new Vector3d(0, multFactor*yLoc, 0));
                TransformGroup tg2 = new TransformGroup(t2);
                tg2.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
                text = new Text2D(settings.title_string, labelColor, FONT_TYPE,
                        FONT_SIZE, 1);
                tg2.addChild(text);
                bg.addChild(tg2);
            }
            return  bg;
        }

        /**
         * Draw the labels along the z-axis.
		 * @return a BranchGroup with the z axis labels
         */
        private BranchGroup drawZScale () {
            BranchGroup bg = new BranchGroup();
            bg.setCapability(BranchGroup.ALLOW_DETACH);
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            Text2D text;
            int numTicks = 3;
            double zInc = (settings.Zmax - settings.Zmin)/numTicks;
            double zLoc = settings.Zmin;
            for (int i = 0; i <= numTicks; i++) {
                if (zLoc != 0) {
                    Transform3D trans = new Transform3D();
                    trans.setTranslation(new Vector3d(0, 0, multFactor*zLoc));
                    TransformGroup tg = new TransformGroup(trans);
                    tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
                    text = new Text2D(nf.format(zLoc), plainColor, FONT_TYPE,
                            FONT_SIZE, 1);
                    tg.addChild(text);
                    bg.addChild(tg);
                }
                zLoc += zInc;
            }
            zLoc += zInc/3;
            Transform3D trans = new Transform3D();
            trans.setTranslation(new Vector3d(0, 0, multFactor*zLoc));
            TransformGroup tg = new TransformGroup(trans);
            tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
            StringBuffer sb = new StringBuffer(Z);
            if (settings.z.length() > 0) {
                sb.append(DASH);
                sb.append(settings.z);
            }
            text = new Text2D(sb.toString(), labelColor, FONT_TYPE, FONT_SIZE,
                    1);
            tg.addChild(text);
            bg.addChild(tg);
            return  bg;
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
			 * Constructor
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
                prop_grid = new JCheckBox("Grid", false);
                prop_legend = new JCheckBox("Legend", false);
                prop_grid.setEnabled(false);
                prop_legend.setEnabled(false);
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
                //setMinimumSize(new Dimension(0, 0));
                setLayout(new GridBagLayout());
                Constrain.setConstraints(this, tabbed, 0, 0, 1, 1, GridBagConstraints.BOTH,
                        GridBagConstraints.NORTHWEST, 1, 1);
                Constrain.setConstraints(this, edit_refresh, 0, 1, 1, 1, GridBagConstraints.NONE,
                        GridBagConstraints.NORTHWEST, 0, 0);
            }

            /**
			 * This listens for action events on buttons.
             * @param event the action event
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
                    name = name + COLON + label;
                    index = edit_ybox.getSelectedIndex();
                    Integer Y = (Integer)map.get(new Integer(index));
                    label = table.getColumnLabel(Y.intValue());
                    name = name + COMMA + label;
                    index = edit_zbox.getSelectedIndex();
                    Integer Z = (Integer)map.get(new Integer(index));
                    label = table.getColumnLabel(Z.intValue());
                    name = name + COMMA + label;
                    ScatterPlot3DDataSet dataSet = new ScatterPlot3DDataSet(name,
                            color, X.intValue(), Y.intValue(), Z.intValue());
                    edit_listmodel.addElement(dataSet);
                    edit_name_field.setText(EMPTY);
                }
                else if (src == edit_delete) {
                    int index = edit_list.getSelectedIndex();
                    if (index != -1) {
                        edit_listmodel.removeElementAt(index);
                    }
                }
                else if (src == edit_refresh) {
                    int size = edit_listmodel.getSize();
                    if (size == 0) {
                        removeAllUserScenes();
                        numScenes = 0;
                        return;
                    }
                    // scatter plot data
                    ScatterPlot3DDataSet[] set = new ScatterPlot3DDataSet[size];
                    for (int count = 0; count < size; count++)
                        set[count] = (ScatterPlot3DDataSet)edit_listmodel.getElementAt(count);
                    // recalculate the min and max for each axis based
                    // on the data sets loaded
                    findMinMax(set);
                    // property data
                    String title = prop_title_field.getText();
                    String xaxis = prop_xaxis_field.getText();
                    String yaxis = prop_yaxis_field.getText();
                    String zaxis = prop_zaxis_field.getText();
                    String value;
                    double Xmin, Xmax, Ymin, Ymax, Zmin, Zmax;
                    value = prop_xmin_field.getText();
                    if (value.trim().length() == 0)
                        //Xmin = null;
                        Xmin = xMin;
                    else
                        //Xmin = new Integer(value);
                        Xmin = Double.parseDouble(value);
                    value = prop_xmax_field.getText();
                    if (value.trim().length() == 0)
                        Xmax = xMax;
                    else
                        //Xmax = new Integer(value);
                        Xmax = Double.parseDouble(value);
                    value = prop_ymin_field.getText();
                    if (value.trim().length() == 0)
                        //Ymin = null;
                        Ymin = yMin;
                    else
                        //Ymin = new Integer(value);
                        Ymin = Double.parseDouble(value);
                    value = prop_ymax_field.getText();
                    if (value.trim().length() == 0)
                        //Ymax = null;
                        Ymax = yMax;
                    else
                        //Ymax = new Integer(value);
                        Ymax = Double.parseDouble(value);
                    value = prop_zmin_field.getText();
                    if (value.trim().length() == 0)
                        //Zmin = null;
                        Zmin = zMin;
                    else
                        //Zmin = new Integer(value);
                        Zmin = Double.parseDouble(value);
                    value = prop_zmax_field.getText();
                    if (value.trim().length() == 0)
                        //Zmax = null;
                        Zmax = zMax;
                    else
                        //Zmax = new Integer(value);
                        Zmax = Double.parseDouble(value);
                    //boolean display_grid = prop_grid.isSelected(),
                    boolean display_legend = prop_legend.isSelected();

                    // remove all user-defined scenes
                    removeAllUserScenes();

                    // create new graph settings
                    settings = new ScatterPlot3DGraphSettings(title, xaxis,
                            yaxis, zaxis, Xmin, Xmax, Ymin, Ymax, Zmin, Zmax,
                            10, false, true, display_legend, false, true, true);

                    // replace each axis
                    updateAxes();

                    // replace each scale
                    updateScales();
                    // replace the legend
                    updateLegend();

                    // add each user defined scene
                    for (int i = 0; i < set.length; i++)
                        addScene(set[i]);
                    numScenes = set.length;
                }
            }
        }       // ScatterPlot3D$ScatterPlot3DControl

        /**
         * A data set for the scatterplot
         */
        private class ScatterPlot3DDataSet {
            String name;
            Color color;
            int x, y, z;

            /**
			 * Constructor
             * @param name the name of the data set
             * @param color the color to use
             * @param x the index of the x column
             * @param y the index of the y column
             * @param z the index of the z column
             */
            ScatterPlot3DDataSet (String name, Color color, int x, int y, int z) {
                this.name = name;
                this.color = color;
                this.x = x;
                this.y = y;
                this.z = z;
            }

            /**
			 * Return the name.
             * @return the name
             */
            public String toString () {
                return  name;
            }
        }

        /**
         * The graph settings
         */
        private class ScatterPlot3DGraphSettings {
            String title_string, x, y, z;
            double Xmin, Xmax, Ymin, Ymax, Zmin, Zmax;
            int grid_size;
            boolean grid, scale, legend, ticks, title, labels;

            /**
			 * Constructor
             */
            ScatterPlot3DGraphSettings () {
                title_string = EMPTY;
                x = EMPTY;
                y = EMPTY;
                z = EMPTY;
                Xmin = xMin;
                Xmax = xMax;
                Ymin = yMin;
                Ymax = yMax;
                Zmin = zMin;
                Zmax = zMax;
                grid_size = 10;
                grid = true;
                scale = true;
                legend = true;
                ticks = true;
                title = true;
                labels = true;
            }

            /**
			 * Constructor
             * @param title_string the graph title
             * @param x the x-axis title
             * @param y the y-axis title
             * @param z the z-axis title
             * @param Xmin the x minimum
             * @param Xmax the x maximum
             * @param Ymin the y minimum
             * @param Ymax the y maximum
             * @param Zmin the z minimum
             * @param Zmax the z maximum
             * @param grid_size size of the grid
             * @param grid true if grid should be shown
             * @param scale true if scale should be shown
             * @param legend true if legend should be shown
             * @param ticks true if ticks should be shown
             * @param title true if title should be shown
             * @param labels true if labels should be shown
             */
            ScatterPlot3DGraphSettings (String title_string, String x, String y,
                    String z, double Xmin, double Xmax, double Ymin, double Ymax,
                    double Zmin, double Zmax, int grid_size, boolean grid,
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
        private class ScatterPlot3DCanvas extends Canvas3D {

            /**
			 * Constructor
             * @param graphicsConfig graphics configurations for this canvas
             */
            public ScatterPlot3DCanvas (GraphicsConfiguration graphicsConfig) {
                super(graphicsConfig);
            }

            /**
			 * get the minimum size
             * @return the minimum size
             */
            public Dimension getMinimumSize () {
                return  new Dimension(0, 0);
            }

            /**
			 * get the preferred size
             * @return the preferred size
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
			 * constructor.
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
			 * listen for action events
             * @param event the action event
             */
            public void actionPerformed (ActionEvent event) {
                color = JColorChooser.showDialog(this, "Edit Color", defaultColor);
                renderpanel.setBackground(color);
            }

            /**
			 * get the chosen color
             * @return the chosen color
             */
            Color getColor () {
                return  color;
            }

            /**
			 * set the chosen color
             * @param color
             */
            void setColor (Color color) {
                this.color = color;
            }
        }
       	private class HelpWindow extends JD2KFrame {
            HelpWindow() {
			    super("About ScatterPlot3D");
			    JEditorPane jep = new JEditorPane("text/html", getHelpString());
			    getContentPane().add(new JScrollPane(jep));
			    setSize(400, 400);
		    }
	    }
    } // ScatterPlot3D$ScatterPlot3DView

    private static final String getHelpString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<h2>ScatterPlot3D</h2>");
        sb.append("This module visualizes a data set in three dimensions.");
        sb.append("<h3>Keyboard controls</h3>");
        sb.append("<ul><li>+/= key: bring scene to home view");
        sb.append("<li>number pad -: zoom out");
        sb.append("<li>number pad +: zoom in");
        sb.append("<li>up arrow: move scene back");
        sb.append("<li>down arrow: move scene forward");
        sb.append("<li>left arrow: rotate scene counterclockwise in x");
        sb.append("<li>right arrow: rotate scene clockwise in x");
        sb.append("<li>Page Up: rotate scene counterclockwise in z");
        sb.append("<li>Page Down: rotate scene clockwise in z");
        sb.append("</ul>");
        sb.append("<h3>Mousing Functions</h3>");
        sb.append("<ul><li>Drag with left mouse button: rotate scene");
        sb.append("<li>Drag with right mouse button: move scene");
        sb.append("<li>Drag with middle mouse button: zoom scene");
        sb.append("</ul></body></html>");
        return sb.toString();
    }
} // ScatterPlot3D
