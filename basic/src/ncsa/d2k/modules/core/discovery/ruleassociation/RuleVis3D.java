package ncsa.d2k.modules.core.discovery.ruleassociation;

import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.universe.*;
import gnu.trove.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.userviews.swing.*;

public class RuleVis3D extends VisModule implements Serializable {

   public String[] getFieldNameMapping() {
      return null;
   }

   public String getInputInfo(int index) {
      return "Rule Table";
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.discovery.ruleassociation.RuleTable"
      };
   }

   public String getOutputInfo(int index) {
      return null;
   }

   public String[] getOutputTypes() {
      return null;
   }

   public String getModuleInfo() {
      return "A three-dimensional rule table visualization.";
   }

   protected UserView createUserView() {
      return new Rules3DView();
   }

   private class Rules3DView extends JUserPane implements Serializable {

      private JTextArea ruleTextArea;
      private HashMap rulePickLookup; // map Shape3D to Integer (rule index)

      private TIntIntHashMap antecedentMap; // rule table index -> order seen
      private TIntIntHashMap consequentMap;

      public Dimension getPreferredSize() {
         return new Dimension(640, 480);
      }

      public void initView(ViewModule mod) { }

      public void setInput(Object obj, int index) {

         if (index == 0) {

            ruleTable = (RuleTable)obj;

            if (ruleTable.getNumRows() < 1) {
               JOptionPane.showMessageDialog(this, "No valid rules found; nothing to display.",
                  "Error", JOptionPane.ERROR_MESSAGE);
               return;
            }

            int numRows = ruleTable.getNumRows();
            antecedentMap = new TIntIntHashMap();
            consequentMap = new TIntIntHashMap();
            int aIndex = 0;
            int cIndex = 0;
            for (int row = 0; row < numRows; row++) {
              int a = ruleTable.getInt(row, 0);
              int c = ruleTable.getInt(row, 1);
              if (!antecedentMap.containsKey(a)) {
                antecedentMap.put(a, aIndex++);
              }
              if (!consequentMap.containsKey(c)) {
                consequentMap.put(c, cIndex++);
              }
            }

            execute();

         }

      }

      private BranchGroup createScene() {

         BranchGroup objRoot = new BranchGroup();
         // objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

         Background background = new Background(backgroundColor);
         background.setApplicationBounds(bounds);

         objRoot.addChild(background);
         objRoot.addChild(drawAxes());
         objRoot.addChild(drawGrid());
         objRoot.addChild(drawRules());

         return objRoot;

      }

      private BranchGroup drawAxes() {

         BranchGroup axesGroup = new BranchGroup();
         // axesGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

         // actual axis lines

         // int leftmax = (int)((IntColumn)ruleTable.getColumn(0)).getMax();
         // int rightmax = (int)((IntColumn)ruleTable.getColumn(1)).getMax();
         int leftmax = antecedentMap.size();
         int rightmax = consequentMap.size();

         float gridtop = -(leftmax + 1) * gridf; // !:h?
         float gridright = (rightmax + 1) * gridf;

         LineArray axisLines = new LineArray(4, LineArray.COORDINATES);
         axisLines.setCoordinate(0, new float[] {0, 0, 0});
         axisLines.setCoordinate(1, new float[] {0, 0, gridtop});
         axisLines.setCoordinate(2, new float[] {0, 0, 0});
         axisLines.setCoordinate(3, new float[] {gridright, 0, 0});

         BranchGroup bg = new BranchGroup();
         Shape3D shape = new Shape3D(axisLines, axisAppearance);
         shape.setPickable(false);
         bg.addChild(shape);

         axesGroup.addChild(bg);

         // text labels

         Text2D text;
         Transform3D transform1, transform2;
         TransformGroup tg1, tg2;

         java.util.List itemSetsList = ruleTable.getItemSetsList();
         java.util.List namesList = ruleTable.getNamesList();

         // text along horizontal axis (consequent)

         // float xpos = gridf;

         // for (int j = 0; j <= rightmax; j++) {
         TIntIntIterator iter = consequentMap.iterator();
         while (iter.hasNext()) {

            iter.advance();
            int ruleIndex = iter.key();
            int orderSeen = iter.value();
            // int cIndex = consequentMap.get(j);

            FreqItemSet fis = (FreqItemSet)itemSetsList.get(ruleIndex);
            TIntArrayList items = fis.items;

            StringBuffer sb = new StringBuffer(namesList.get(items.get(0))
               .toString());
            for (int c = 1; c < items.size(); c++) {
               sb.append(',');
               sb.append(' ');
               sb.append(namesList.get(items.get(c)).toString());
            }

            text = new Text2D(sb.toString(), axisColor,
               FONT_TYPE, FONT_SIZE, FONT_STYLE);
            text.setPickable(false);

            transform1 = new Transform3D();
            transform1.rotX(-Math.PI/2);

            tg1 = new TransformGroup(transform1);

            transform2 = new Transform3D();
            transform2.rotZ(-Math.PI/2);
            // !:h
            float xpos = orderSeen * gridf + gridf;
            transform2.setTranslation(new Vector3f(xpos
               // !: kind of arbitrary:
               - .03f,
               -.03f, 0));
            // xpos += gridf;

            tg2 = new TransformGroup(transform2);

            tg2.addChild(text);
            tg1.addChild(tg2);

            axesGroup.addChild(tg1);

         }

         // text along vertical axis (antecedent)

         iter = antecedentMap.iterator();
         // for (int j = 0; j <= leftmax; j++) {
         while (iter.hasNext()) {

            iter.advance();
            int ruleIndex = iter.key();
            int orderSeen = iter.value();
            // int aIndex = antecedentMap.get(j);

            FreqItemSet fis = (FreqItemSet)itemSetsList.get(ruleIndex);
            TIntArrayList items = fis.items;

            // !: shouldn't repeat this business
            StringBuffer sb = new StringBuffer(namesList.get(items.get(0))
               .toString());
            for (int c = 1; c < items.size(); c++) {
               sb.append(',');
               sb.append(' ');
               sb.append(namesList.get(items.get(c)).toString());
            }

            text = new Text2D(sb.toString(), axisColor,
               FONT_TYPE, FONT_SIZE, FONT_STYLE);
            text.setPickable(false);

            transform1 = new Transform3D();
            transform1.rotX(-Math.PI/2);

            tg1 = new TransformGroup(transform1);

            transform2 = new Transform3D();
            // transform2.rotZ(-Math.PI/2);
            // !: 1) unnecessary transform/groups here
            //    2) arbitrariness again...
            transform2.setTranslation(new Vector3f(
               -(getWidth3D(sb.toString()) + .03f),
               (orderSeen + 1) * gridf - .03f, 0));

            tg2 = new TransformGroup(transform2);

            tg2.addChild(text);
            tg1.addChild(tg2);

            axesGroup.addChild(tg1);

         }

         return axesGroup;

      }

      private BranchGroup drawGrid() {

         // int leftmax = (int)((IntColumn)ruleTable.getColumn(0)).getMax();
         // int rightmax = (int)((IntColumn)ruleTable.getColumn(1)).getMax();
         int leftmax = antecedentMap.size();
         int rightmax = consequentMap.size();

         LineArray gridLines = new LineArray(2 * (leftmax + rightmax + 2),
            LineArray.COORDINATES);

         float gridtop = -(leftmax + 1) * gridf; // !:h?
         float gridright = (rightmax + 1) * gridf;// (rightmax + 1) * gridf;

         int coord = 0;

         float zpos = -gridf;
         for (int left = 0; left <= leftmax; left++) {

            gridLines.setCoordinate(coord++, new float[] {0, 0, zpos});
            gridLines.setCoordinate(coord++, new float[] {gridright, 0, zpos});

            zpos -= gridf;

         }

         float xpos = gridf;
         for (int right = 0; right <= rightmax; right++) {

            gridLines.setCoordinate(coord++, new float[] {xpos, 0, 0});
            gridLines.setCoordinate(coord++, new float[] {xpos, 0, gridtop});

            xpos += gridf;

         }

         BranchGroup bg = new BranchGroup();
         Shape3D shape = new Shape3D(gridLines, gridAppearance);
         shape.setPickable(false);
         bg.addChild(shape);

         return bg;

      }

      private BranchGroup drawRules() {

         BranchGroup bgAll = new BranchGroup();
         // bgAll.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

         int numRules = ruleTable.getNumRules();
         float xpos, zpos, height, conf;
         for (int rule = 0; rule < numRules; rule++) {

            // xpos = ruleTable.getRuleConsequentID(rule) * gridf + gridf;
            // zpos = -ruleTable.getRuleAntecedentID(rule) * gridf + gridf;
            xpos = consequentMap.get(ruleTable.getRuleConsequentID(rule)) * gridf + gridf;
            zpos = -antecedentMap.get(ruleTable.getRuleAntecedentID(rule)) * gridf + gridf;

            height = (float)ruleTable.getSupport(rule) * ruleh;
            conf = (float)ruleTable.getConfidence(rule);

            TriangleArray t = new TriangleArray(36, TriangleArray.COORDINATES);

            // eight vertices for drawing a cube:

            float[] c1 = new float[] {xpos - rulefh, 0, zpos + rulefh},
                    c2 = new float[] {xpos + rulefh, 0, zpos + rulefh},
                    c3 = new float[] {xpos + rulefh, height, zpos + rulefh},
                    c4 = new float[] {xpos - rulefh, height, zpos + rulefh},
                    c5 = new float[] {xpos - rulefh, 0, zpos - rulefh},
                    c6 = new float[] {xpos + rulefh, 0, zpos - rulefh},
                    c7 = new float[] {xpos - rulefh, height, zpos - rulefh},
                    c8 = new float[] {xpos + rulefh, height, zpos - rulefh};

            // front cube face

            t.setCoordinate(0, c1);
            t.setCoordinate(1, c2);
            t.setCoordinate(2, c4);

            t.setCoordinate(3, c2);
            t.setCoordinate(4, c3);
            t.setCoordinate(5, c4);

            // left cube face

            t.setCoordinate(6, c1);
            t.setCoordinate(7, c4);
            t.setCoordinate(8, c5);

            t.setCoordinate(9, c7);
            t.setCoordinate(10, c5);
            t.setCoordinate(11, c4);

            // bottom cube face

            t.setCoordinate(12, c5);
            t.setCoordinate(13, c2);
            t.setCoordinate(14, c1);

            t.setCoordinate(15, c5);
            t.setCoordinate(16, c6);
            t.setCoordinate(17, c2);

            // top cube face

            t.setCoordinate(18, c3);
            t.setCoordinate(19, c7);
            t.setCoordinate(20, c4);

            t.setCoordinate(21, c3);
            t.setCoordinate(22, c8);
            t.setCoordinate(23, c7);

            // right cube face

            t.setCoordinate(24, c6);
            t.setCoordinate(25, c3);
            t.setCoordinate(26, c2);

            t.setCoordinate(27, c3);
            t.setCoordinate(28, c6);
            t.setCoordinate(29, c8);

            // back cube face

            t.setCoordinate(30, c7);
            t.setCoordinate(31, c6);
            t.setCoordinate(32, c5);

            t.setCoordinate(33, c7);
            t.setCoordinate(34, c8);
            t.setCoordinate(35, c6);

            Appearance a = new Appearance();
            a.setColoringAttributes(new ColoringAttributes(
               getStdGradientColor(conf),
               ColoringAttributes.NICEST));

            // BranchGroup bg = new BranchGroup();
            Shape3D shape = new Shape3D(t, a);
            shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            rulePickLookup.put(shape, new Integer(rule));
            bgAll.addChild(shape);

         }

         // !:h
         // return bgAll;
         Transform3D hackTransform = new Transform3D();
         hackTransform.setTranslation(new Vector3f(
            0, // ((int)((IntColumn)ruleTable.getColumn(1)).getMax() - ruleTable.getNamesList().size() + 1) * -gridf,
            0, -2 * gridf));
         TransformGroup hackTG = new TransformGroup(hackTransform);
         hackTG.addChild(bgAll);
         BranchGroup hackBG = new BranchGroup();
         hackBG.addChild(hackTG);
         return hackBG;

      }

      private void execute() {

         GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
         Canvas3D cs = new Canvas3D(gc) {
           public Dimension getMinimumSize() { return new Dimension(250, 250); }
         };
         SimpleUniverse su = new SimpleUniverse(cs);

         OrbitBehavior ob = new OrbitBehavior(cs, OrbitBehavior.REVERSE_ALL);
         ob.setSchedulingBounds(bounds);
         ob.setRotationCenter(new Point3d());

         su.getViewingPlatform().setViewPlatformBehavior(ob);
         su.getViewingPlatform().setNominalViewingTransform();

         axisAppearance = new Appearance();
         axisAppearance.setColoringAttributes(new ColoringAttributes(
            axisColor, ColoringAttributes.FASTEST));

         gridAppearance = new Appearance();
         gridAppearance.setColoringAttributes(new ColoringAttributes(
            gridColor, ColoringAttributes.FASTEST));

         highlightAppearance = new Appearance();
         highlightAppearance.setColoringAttributes(new ColoringAttributes(
            highlightColor, ColoringAttributes.FASTEST));

         rulePickLookup = new HashMap();
         BranchGroup objRoot = createScene();

         RulesPickBehavior pick = new RulesPickBehavior(cs, objRoot, bounds);

         objRoot.compile();
         su.addBranchGraph(objRoot);

         this.removeAll();

         ruleTextArea = new JTextArea("\n\n");

         GridBagLayout layout = new GridBagLayout();
         this.setLayout(layout);

         layout.addLayoutComponent(cs, new GridBagConstraints(
            0, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
         layout.addLayoutComponent(ruleTextArea, new GridBagConstraints(
            0, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));

         this.add(cs);
         this.add(ruleTextArea);

      }

      private Color3f getStdGradientColor(double f) {

         if (f <= .25f) {
            return interpolateColor(Color.blue, Color.cyan, f/.25f);
         }
         else if (f <= .5f) {
            return interpolateColor(Color.cyan, Color.green, (f - .25f)/.25f);
         }
         else if (f <= .75f) {
            return interpolateColor(Color.green, Color.yellow, (f - .5f)/.25f);
         }
         else {
            return interpolateColor(Color.yellow, Color.red, (f - .75f)/.25f);
         }

      }

      private float getWidth3D(String str) {

         Font font = new Font(FONT_TYPE, FONT_STYLE, FONT_SIZE);
         FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
         return 0.00390625f /* 1/256 */ * fm.stringWidth(str);

      }

      private final Color3f interpolateColor(Color L, Color H, double f) {

         if (f < 0) {
            f *= -1;
         }

         double r, g, b;

         if (L.getRed() > H.getRed()) {
            r = (double)((L.getRed() - H.getRed()) * (1.0 - f) + H.getRed());
         }
         else {
            r = (double)((H.getRed() - L.getRed()) * f + L.getRed());
         }

         if (L.getGreen() > H.getGreen()) {
            g = (double)((L.getGreen() - H.getGreen()) * (1.0 - f) + H.getGreen());
         }
         else {
            g = (double)((H.getGreen() - L.getGreen()) * f + L.getGreen());
         }

         if (L.getBlue() > H.getBlue()) {
            b = (double)((L.getBlue() - H.getBlue()) * (1.0 - f) + H.getBlue());
         }
         else {
            b = (double)((H.getBlue() - L.getBlue()) * f + L.getBlue());
         }

         return new Color3f((float)r/255, (float)g/255, (float)b/255);

      }

      private class RulesPickBehavior extends PickMouseBehavior {

         private Shape3D lastShape = null;
         private Appearance lastPickAppearance = null;

         RulesPickBehavior(Canvas3D canvas, BranchGroup root, Bounds bounds) {

            super(canvas, root, bounds);
            setSchedulingBounds(bounds);
            root.addChild(this);

            pickCanvas.setMode(PickTool.BOUNDS);

         }

         public void updateScene(int xpos, int ypos) {

            PickResult pickResult = null;
            Shape3D shape = null;

            pickCanvas.setShapeLocation(xpos, ypos);
            pickResult = pickCanvas.pickClosest();

            if (pickResult != null) {
               shape = (Shape3D)pickResult.getNode(PickResult.SHAPE3D);
            }
            if (shape != null) {

               Object o = rulePickLookup.get(shape);

               if (o != null) {

                  int ruleIndex = ((Integer)o).intValue();

                  java.util.List namesList = ruleTable.getNamesList();
                  int[] antecedent = ruleTable.getRuleAntecedent(ruleIndex);
                  int[] consequent = ruleTable.getRuleConsequent(ruleIndex);

                  StringBuffer antsb = new StringBuffer();
                  for (int i = 0; i < antecedent.length; i++) {
                     antsb.append(namesList.get(antecedent[i]));
                     if (i < antecedent.length - 1) {
                        antsb.append(' ');
                     }
                  }

                  StringBuffer consb = new StringBuffer();
                  for (int i = 0; i < consequent.length; i++) {
                     consb.append(namesList.get(consequent[i]));
                     if (i < consequent.length - 1) {
                        consb.append(' ');
                     }
                  }

                  if (lastShape != null) {
                     lastShape.setAppearance(lastPickAppearance);
                  }

                  lastShape = shape;
                  lastPickAppearance = shape.getAppearance();
                  shape.setAppearance(highlightAppearance);

                  ruleTextArea.setText(antsb.toString() + " -> " + consb.toString() +
                     "\nsupport: " + ruleTable.getSupport(ruleIndex) +
                     "\nconfidence: " + ruleTable.getConfidence(ruleIndex));

               }

            }

         }

      }

      private Appearance axisAppearance;
      // private Color3f axisColor = new Color3f(Color.black);
      private Color3f axisColor = new Color3f(Color.white);
      // private Color3f backgroundColor = new Color3f(Color.white);
      private Color3f backgroundColor = new Color3f(Color.black);
      private Bounds bounds = new BoundingSphere(new Point3d(), 1000.0);
      private static final int FONT_SIZE = 14;
      private static final int FONT_STYLE = 1;
      private static final String FONT_TYPE = "Helvetica";
      private Appearance gridAppearance;
      private Color3f gridColor = new Color3f(Color.lightGray);
      private float gridf = 0.1f;
      private Appearance highlightAppearance;
      private Color3f highlightColor = new Color3f(Color.lightGray);
      private float rulef = 0.05f;
      private float rulefh = rulef / 2;
      private float ruleh = 0.5f;
      private RuleTable ruleTable;

   }

}
