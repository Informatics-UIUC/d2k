package ncsa.d2k.modules.core.vis;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;
import javax.vecmath.*;
import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.behaviors.keyboard.*;
import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.gui.Constrain;
import ncsa.d2k.modules.core.datatype.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

// author: mbach

// 06/11/2001:
// (gpape) Appended classes that were previously in separate files
//         Excised DataTable in favor of VerticalTable

// 08/17/2001:
// (gpape) Excised VerticalTable in favor of HashLookupTable
//         Various fixes
//         NOTE: The code still assumes that the number of
//         records per line is constant.

public class LineGraphAppModule extends VisModule {

   public String getModuleInfo () {
      return "LineGraphAppModule is a three-dimensional visualization of " +
             "Table data (referenced in a HashLookupTable) as a " +
             "line graph.";
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.modules.core.datatype.HashLookupTable",
         "ncsa.d2k.modules.core.datatype.table.Table"};
      return i;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "A HashLookupTable referencing the related VerticalTable.";
      else if (index == 1)
         return "A VerticalTable to be visualized.";
      else
         return "LineGraphAppModule has no such input.";
   }

   public String[] getOutputTypes () { return null; }

   public String getOutputInfo (int index) {
      return "LineGraphAppModule has no outputs.";
   }

   protected UserView createUserView() {
      return new LineGraphAppModuleView();
   }

   public String[] getFieldNameMapping() {
      return null;
   }

   protected class LineGraphAppModuleView
      extends ncsa.d2k.controller.userviews.swing.JUserPane
      implements ActionListener, ItemListener {

      public HashLookupTable hlt;
      public Table vt;
      public LineGraphVerticalTable dt;
      public FindClosest fc;

      private boolean[] inputs_set = {false, false};

      GraphicsConfiguration config =
         SimpleUniverse.getPreferredConfiguration();
      Canvas3D canvas = new LineGraphCanvas(config);

      BranchGroup objRoot = new BranchGroup();
      TransformGroup objTrans = new TransformGroup();

      // PickHighlightBehavior:
      //  allows user to pick lines from the scene by using the mouse

      PickHighlightBehavior phb = new PickHighlightBehavior(canvas, objRoot,
         new BoundingSphere(new Point3d(), 1000.0), this);

      float xmax = .5f, ymax = .5f, zmax = .5f;

      public final Color3f
         selectedColor = new Color3f(1.0f, 1.0f, .4f), // yellow
         plainColor = new Color3f(0.8f, 0.9254901f, .9568627f),  // lt. blue
         closestColor = new Color3f(1.0f, 0.6f, 0.0f), // orange
         axisColor = new Color3f(.5098f, .5098f, .5098f),  // lt. grey
         backgroundColor = new Color3f(.35294f, .35294f, .35294f), //dk. grey
         labelColor = new Color3f(1.0f, 1.0f, 1.0f);  // white

      JTextField closestN = new JTextField(3),
         lowerBound = new JTextField(3), highBound = new JTextField(3);
      java.awt.Choice fields = new java.awt.Choice();

      public int selected = -1, field = 1, startLine=0, num2find = 0;
      public double lineSpace = .1;

      LineStripArray[] lines;
      Shape3D[] shapes;
      double pointSpace = .1d, lineInc = .01d;
      float inc = .1f;

      double yscale = 1.0d;
      float yoffset = 0.0f;

      int index=-1;

      int currentLow = 0, currentHigh = 0, currentSpan = 0;

      public boolean[] closest;
      boolean inClosest = false;

      Shape3D[][] allShapes;

      int origIndex = -1;

      // Color controlBackgroundColor = new Color(97, 97, 97);
      // Color controlSecondaryColor = new Color(69, 69, 69);

      ImageIcon goNorm /*= new ImageIcon("l-go-normal.gif")*/,
         goPres /*= new ImageIcon("l-go-pressed.gif")*/,
         leftNorm /*= new ImageIcon("left-normal.gif")*/,
         leftPres /*= new ImageIcon("left-pressed.gif")*/,
         rightNorm /*= new ImageIcon("right-normal.gif")*/,
         rightPres /*= new ImageIcon("right-pressed.gif")*/,
         darkGoNorm /*= new ImageIcon("d-go-normal.gif")*/,
         darkGoPres;// = new ImageIcon("d-go-pressed.gif");

      SimpleButton doIt /*= new SimpleButton(goNorm, goPres)*/,
         increment /*= new SimpleButton(rightNorm, rightPres)*/,
         decrement /*= new SimpleButton(leftNorm, leftPres)*/,
         findClosest;// = new SimpleButton(darkGoNorm, darkGoPres);

      public void initView(ViewModule mod) {
      	goNorm = new ImageIcon(mod.getImage("/images/linegraph/l-go-normal.gif"));
         goPres = new ImageIcon(mod.getImage("/images/linegraph/l-go-pressed.gif"));
         leftNorm = new ImageIcon(mod.getImage("/images/linegraph/left-normal.gif"));
         leftPres = new ImageIcon(mod.getImage("/images/linegraph/left-pressed.gif"));
         rightNorm = new ImageIcon(mod.getImage("/images/linegraph/right-normal.gif"));
         rightPres = new ImageIcon(mod.getImage("/images/linegraph/right-pressed.gif"));
         darkGoNorm = new ImageIcon(mod.getImage("/images/linegraph/d-go-normal.gif"));
         darkGoPres = new ImageIcon(mod.getImage("/images/linegraph/d-go-pressed.gif"));

      	doIt = new SimpleButton(goNorm, goPres);
         increment = new SimpleButton(rightNorm, rightPres);
         decrement = new SimpleButton(leftNorm, leftPres);
         findClosest = new SimpleButton(darkGoNorm, darkGoPres);

         findClosest.addActionListener(this);

         doIt.addActionListener(this);
         fields.addItemListener(this);
         increment.addActionListener(this);
         decrement.addActionListener(this);

         JLabel settings = new JLabel("Settings"),
            zValue = new JLabel("Set Z Value"),
            showLines = new JLabel("Show Lines"),
            low = new JLabel("Low"),
            high = new JLabel("High"),
            closestLabel = new JLabel("Find Closest");

         settings.setUI(new YellowLabelUI());
         zValue.setUI(new WhiteLabelUI());
         showLines.setUI(new WhiteLabelUI());
         low.setUI(new WhiteLabelUI());
         high.setUI(new WhiteLabelUI());
         closestLabel.setUI(new WhiteLabelUI());

         Color bgColor = new Color(119, 119, 119),
            findClosestColor = new Color(90,90,90);
         this.setLayout(new GridBagLayout());
         this.setBackground(bgColor);

         // add components to showLinesPanel
         JPanel showLinesPanel = new JPanel();
         showLinesPanel.setBackground(bgColor);
         showLinesPanel.setLayout(new GridBagLayout());

         JPanel one = new JPanel();
         one.setBackground(bgColor);
         JPanel two = new JPanel();
         two.setBackground(bgColor);
         JPanel three = new JPanel();
         three.setBackground(bgColor);
         JPanel four = new JPanel();
         four.setBackground(bgColor);

         four.setLayout(new GridBagLayout());
         Constrain.setConstraints(four, decrement, 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 1,
            new Insets(0, 0, 0, 0));
         Constrain.setConstraints(four, increment, 1, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 1,
            new Insets(0, 0, 0, 0));

         one.setLayout(new GridBagLayout());
         Constrain.setConstraints(one, low, 0, 0, 1, 1,
            GridBagConstraints.NONE,
            GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(one, high, 0, 1, 1, 1,
            GridBagConstraints.NONE,
            GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(one, lowerBound, 1, 0, 1, 1,
            GridBagConstraints.NONE,
            GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(one, highBound, 1, 1, 1, 1,
            GridBagConstraints.NONE,
            GridBagConstraints.WEST, 1, 1);

         three.add(doIt);

         Constrain.setConstraints(showLinesPanel, showLines, 0, 0,
            4, 1, GridBagConstraints.HORIZONTAL,
            GridBagConstraints.WEST, 4, 1);
         Constrain.setConstraints(showLinesPanel, two, 0, 1, 1, 2,
            GridBagConstraints.BOTH,
            GridBagConstraints.WEST, 1, 2);
         Constrain.setConstraints(showLinesPanel, one, 1, 1, 2, 2,
            GridBagConstraints.NONE,
            GridBagConstraints.WEST, 2, 2);
         Constrain.setConstraints(showLinesPanel, three, 3, 1, 1, 2,
            GridBagConstraints.NONE,
            GridBagConstraints.WEST, 1, 2);
         Constrain.setConstraints(showLinesPanel, four, 2, 3, 1, 1,
            GridBagConstraints.NONE,
            GridBagConstraints.WEST, 1, 2);

         JPanel closestPanel = new JPanel();
         closestPanel.setBackground(findClosestColor);
         closestPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(closestPanel, closestLabel, 0, 0,
            1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST,
            1, 1, new Insets(0,0,0,0));
         Constrain.setConstraints(closestPanel, closestN, 1, 0,
            1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST,
            1, 1, new Insets(0,0,0,0));
         Constrain.setConstraints(closestPanel, findClosest,
            2, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST,
            1, 1, new Insets(0,0,0,0));
         JPanel closestPanel2 = new JPanel();
         closestPanel2.setBackground(bgColor);
         closestPanel2.setLayout(new GridBagLayout());
         Constrain.setConstraints(closestPanel2, closestPanel, 0, 0,
            1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTHWEST,
            1, 1, new Insets(0,0,0,0));

         JPanel topPanel = new JPanel();
         topPanel.setBackground(bgColor);
         topPanel.setLayout(new GridBagLayout());

         // add stuff to topPanel
         Constrain.setConstraints(topPanel, settings, 0, 0,
            1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST,
            1, 1, new Insets(0, 0, 5, 0));
         Constrain.setConstraints(topPanel, zValue, 0, 1,
            1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST,
            1, 1, new Insets(5, 0, 2, 0));
         Constrain.setConstraints(topPanel, fields, 0, 2,
            1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST,
            1, 1, new Insets(2, 0, 5, 0));
         Constrain.setConstraints(topPanel, showLinesPanel, 0, 3,
            1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST,
            1, 1, new Insets(3, 0, 0, 0));

         JPanel filler = new JPanel();
         filler.setBackground(bgColor);

         JPanel back = new JPanel();
         back.setBackground(bgColor);
         back.setLayout(new GridBagLayout());

         Constrain.setConstraints(back, topPanel, 0, 0,
            1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST,
            1, 1);

         Constrain.setConstraints(this, back, 0, 0,
            1, 2, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST,
            1, 2, new Insets(0,0,0,0));

         Constrain.setConstraints(this, closestPanel2, 0, 2,
            1, 1, GridBagConstraints.BOTH, GridBagConstraints.SOUTHWEST,//
            1, 1, new Insets(0,0,0,0));

         // add canvas to this
         Constrain.setConstraints(this, canvas, 1, 0, 4, 4,
            GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, 4, 4,
            new Insets(0, 0, 0, 0));

      } // LineGraphAppModuleView::initView

      // setInput is called whenever an input arrives, and is responsible
      // for modifying the contents of any gui components that should reflect
      // the value of the input.
      public void setInput(Object o, int index) {

         if (index == 0)
            hlt = (HashLookupTable)o;
         else if (index == 1)
            vt = (Table)o;

         inputs_set[index] = true;

         for (int count = 0; count < 2; count++)
            if (inputs_set[count] == false)
               return;

         execute();

      }

      public void execute() {

            dt = new LineGraphVerticalTable(hlt, vt);

            // initialize FindClosest
            fc = new FindClosest(dt);

            // create name headers
            String[] names = dt.getNames();
            for(int i = 0; i < names.length; i++) {
               fields.add(names[i]);
            }

            // create closest array
            closest = new boolean[dt.getNumLines()];
            phb.setClosest(closest);

            // create yscale so the lines fit within the box
            yscale = 1/(dt.getMax(field)-dt.getMin(field));

            // determine space between points
            pointSpace = 2*xmax/dt.getNumRecords();

            // create scene
            SimpleUniverse u = new SimpleUniverse(canvas);
            u.getViewingPlatform().setNominalViewingTransform();
            field = 1;
            BranchGroup scene = createScene(dt);
            scene.compile();
            u.addBranchGraph(scene);

            // debug
            // System.out.println("columns: " + vt.getNumColumns());
            // System.out.println("fields: " + dt.getNumFields());
            // System.out.println("record 3 5 7: " + dt.getRecord(3, 5, 7));

      }

      public void itemStateChanged(ItemEvent e) {

         if(e.getSource() == fields) {

            if(!inClosest) {

               field = fields.getSelectedIndex()+1;
               index = -1;
               changeFields(field);

            }

         }

      }

      public void actionPerformed(ActionEvent p1) {

         if(p1.getSource() == findClosest) {

            // get index of currently selected line
            if(phb.getSelectedIndex() != -1) {
               index = startLine+phb.getSelectedIndex();
            }

            try{

               int n = (new Integer(closestN.getText())).intValue();
               if(n > dt.getNumLines()-1) {
                  n = dt.getNumLines()-1;
                  closestN.setText(Integer.toString(n));
               }
               if(n < 0) {
                  n = 0;
                  closestN.setText("0");
               }
               if(index != -1) {
                  closest = fc.findClosestN(index,n);
                  createClosestNShapes(n);
               }
               inClosest = true;
               phb.setClosest(true);

            }
            catch(NumberFormatException e) {
               System.out.println("Incorrect closest to find number");
            }

         }
         else if(p1.getSource() == doIt) {

            if(phb.getSelectedIndex() != -1) {

               if(phb.getSelectedIndex()+startLine != index) {

                  index = phb.getSelectedIndex()+startLine;

                  for(int i=0; i<closest.length; i++) {
                     closest[i] = false;
                  }

               }

            }

            if(!phb.isSelected()) { // nothing is selected

               index = -1;
               for(int i=0; i<closest.length; i++) {
                  closest[i] = false;
               }
            }

            try{

               int l = Integer.parseInt(lowerBound.getText());
               int h = Integer.parseInt(highBound.getText());

               // check bounds
               if(l < 0)
                  l = 0;
               if(h < 0)
                  h = 0;
               if(l > dt.getNumLines()-1)
                  l = dt.getNumLines()-1;
               if(h > dt.getNumLines()-1)
                  h = dt.getNumLines()-1;

               int newSpan = h - l;

               if(newSpan < 0) {}
               else {
                  lineSpace = (2*xmax)/(double)newSpan;

                  startLine = l;

                  if(inClosest) {
                     index = origIndex;
                     origIndex = -1;
                  }

                  objTrans.setChild(createShapeGroup(h), 0);

                  inClosest = false;
                  phb.setClosest(false);

                  if((index>=startLine) && (index<startLine+shapes.length)) {
                     phb.setSelected(shapes[index-startLine]);
                  }

                  objTrans.setChild(drawXScale(), 4);

               }

            }

            catch(NumberFormatException e) {
               System.out.println("Incorrect inputs for high/low lines.");
            }

         }
         else if(p1.getSource() == increment) {

            try{
               int lwr = Integer.parseInt(lowerBound.getText());
               int hgr = Integer.parseInt(highBound.getText());

               // increment by 1/2 the number of lines showing
               int diff = hgr-lwr;
               int newlwr = lwr+(int)(diff/2);

               int newupr = hgr+(int)(diff/2);
               if(newupr < (newlwr+(diff/2)))
                  newupr = newlwr+(int)(diff/2);

               if(newupr > dt.getNumLines()-1)
                  newupr = dt.getNumLines()-1;
               if(newlwr > dt.getNumLines()-1)
                  newlwr = dt.getNumLines()-1;

               lowerBound.setText(Integer.toString(newlwr));
               highBound.setText(Integer.toString(newupr));

               if(phb.getSelectedIndex() != -1) {
                  if(phb.getSelectedIndex()+startLine != index) {
                     // new line has been selected
                     index = phb.getSelectedIndex()+startLine;
                     // clear closest[]
                     for(int i=0; i<closest.length; i++) {
                        closest[i] = false;
                     }
                  }
               }

               if(!phb.isSelected()) { // nothing is selected
                  index = -1;
                  for(int i=0; i<closest.length; i++) {
                     closest[i] = false;
                  }
               }

               int l = Integer.parseInt(lowerBound.getText());
               int h = Integer.parseInt(highBound.getText());

               // check bounds
               if(l < 0)
                  l = 0;
               if(h < 0)
                  h = 0;
               if(l > dt.getNumLines()-1)
                  l = dt.getNumLines()-1;
               if(h > dt.getNumLines()-1)
                  h = dt.getNumLines()-1;

               int newSpan = h - l;

               if(newSpan < 0) {}
               else {
                  lineSpace = (2*xmax)/(double)newSpan;

                  startLine = l;
                  objTrans.setChild(createShapeGroup(h), 0);

                  inClosest = false;
                  phb.setClosest(false);
                  if((index>startLine) && (index<startLine+shapes.length)) {
                     phb.setSelected(shapes[index-startLine]);
                  }
                  objTrans.setChild(drawXScale(), 4);
               }
            }
            catch(NumberFormatException e) {
               System.out.println("Incorrect inputs for high/low lines.");
            }

         }
         else if(p1.getSource() == decrement) {

            try{
               int lwr = Integer.parseInt(lowerBound.getText());
               int hgr = Integer.parseInt(highBound.getText());

               // decrement by 1/2 number of lines showing
               int diff = hgr-lwr;
               int newlwr = lwr-(int)(diff/2);

               if(newlwr < 0)
                  newlwr = 0;

               int newupr = hgr-(int)(diff/2);
               if(newupr < 0)
                  newupr = 0;
               else if(newupr < (newlwr+(diff/2)))
                  newupr = newlwr+(int)(diff/2);

               lowerBound.setText(Integer.toString(newlwr));
               highBound.setText(Integer.toString(newupr));

               if(phb.getSelectedIndex() != -1) {

                  if(phb.getSelectedIndex()+startLine != index) {

                     // new line has been selected
                     index = phb.getSelectedIndex()+startLine;
                     // clear closest[]
                     for(int i=0; i<closest.length; i++) {
                        closest[i] = false;
                     }

                  }

               }

               if(!phb.isSelected()) { // nothing is selected

                  index = -1;
                  for(int i=0; i<closest.length; i++) {
                     closest[i] = false;
                  }

               }

               int l = Integer.parseInt(lowerBound.getText());
               int h = Integer.parseInt(highBound.getText());

               // check bounds
               if(l < 0)
                  l = 0;
               if(h < 0)
                  h = 0;
               if(l > dt.getNumLines()-1)
                  l = dt.getNumLines()-1;
               if(h > dt.getNumLines()-1)
                  h = dt.getNumLines()-1;

               int newSpan = h - l;

               if(newSpan < 0) {}
               else {
                  lineSpace = (2*xmax)/(double)newSpan;

                  startLine = l;
                  objTrans.setChild(createShapeGroup(h), 0);

                  inClosest = false;
                  phb.setClosest(false);
                  if((index>startLine) && (index<startLine+shapes.length)) {
                     phb.setSelected(shapes[index-startLine]);
                  }

                  objTrans.setChild(drawXScale(), 4);
               }

            }
            catch(NumberFormatException e) {
               System.out.println("Incorrect inputs for high/low lines.");
            }

         }

         System.gc();

      }

      public Point3d[][] createPoints(LineGraphVerticalTable dt, int field) {

         int numLines = (int)(2*xmax/lineSpace);
         if(numLines > dt.getNumLines())
            numLines = dt.getNumLines();
         if(startLine+numLines > dt.getNumLines())
            numLines = dt.getNumLines() - startLine;

         double z = -zmax;
         double x = -xmax;
         Point3d[][] points = new Point3d[numLines][dt.getNumRecords()];
         for(int i=0; i<numLines; i++) {
            for(int j=0; j<dt.getNumRecords(); j++) {
               points[i][j] = new Point3d(x, yscale*(dt.getRecord(startLine+i,
                  field, j)-dt.getMin(field))-ymax, z);
               z+=pointSpace;
            }
            z=-zmax;
            x+=lineSpace;
         }

         lowerBound.setText(Integer.toString(0));
         highBound.setText(Integer.toString(numLines));

         return points;

      }

      public Point3d[][] createPoints(LineGraphVerticalTable dt, int field,
         int endLine) {

         int numLines = endLine - startLine+1;
         if(numLines>dt.getNumLines()) {
            numLines = dt.getNumLines();
         }
         lineSpace = (2*xmax)/numLines;
         double z = -zmax;
         double x = -xmax;
         Point3d[][] points = new Point3d[numLines][dt.getNumRecords()];
         for(int i=0; i<numLines; i++) {
            for(int j=0; j<dt.getNumRecords(); j++) {
               points[i][j] = new Point3d(x, yscale*(dt.getRecord(startLine+i,
                  field, j)-dt.getMin(field))-ymax, z);
               z+=pointSpace;
            }
            z=-zmax;
            x+=lineSpace;
         }

         lowerBound.setText(Integer.toString(startLine));
         highBound.setText(Integer.toString(endLine));

         return points;

      }

      public Shape3D[] createShapes() {

         Point3d[][] pts = createPoints(dt, field);
         Shape3D[] newShapes = new Shape3D[pts.length];
         LineStripArray[] lsaArray = new LineStripArray[pts.length];
         Text2D text;
         for(int i=0; i<pts.length; i++) {
            int[] a = {pts[0].length};

            // create LineStripArray
            LineStripArray lsa = new LineStripArray(pts[0].length,
            GeometryArray.COORDINATES | GeometryArray.COLOR_3, a);
            lsa.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
            lsa.setCapability(GeometryArray.ALLOW_COUNT_READ);
            lsa.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
            lsa.setCapability(GeometryArray.ALLOW_COLOR_READ);

            lsa.setCoordinates(0, pts[i]);

            if(index == startLine + i) {  // selected line
               for(int j=0; j<pts[0].length; j++) {
                  lsa.setColor(j, selectedColor);
               }
            }
            else if(closest[i+startLine]) { // a closest line
               for(int j=0; j<pts[0].length; j++) {
                  lsa.setColor(j, closestColor);
               }
            }
            else {  // other lines
               for(int j=0; j<pts[0].length; j++) {
                  lsa.setColor(j, plainColor);
               }
            }

            lsaArray[i] = lsa;

            newShapes[i] = new Shape3D(lsaArray[i]);
            newShapes[i].setCapability(Shape3D.ALLOW_GEOMETRY_READ);
            newShapes[i].setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
         }

         shapes = newShapes;
         return shapes;

      }

      public Shape3D[] createShapes(int upperBound) {

         Point3d[][] pts = createPoints(dt, field, upperBound);
         Shape3D[] newShapes = new Shape3D[pts.length];
         LineStripArray[] lsaArray = new LineStripArray[pts.length];
         Text2D text;
         for(int i=0; i<pts.length; i++) {
            int[] a = {pts[0].length};

            // create LineStripArray
            LineStripArray lsa = new LineStripArray(pts[0].length,
            GeometryArray.COORDINATES | GeometryArray.COLOR_3, a);
            lsa.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
            lsa.setCapability(GeometryArray.ALLOW_COUNT_READ);
            lsa.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
            lsa.setCapability(GeometryArray.ALLOW_COLOR_READ);

            lsa.setCoordinates(0, pts[i]);

            if(index == startLine + i) {  // selected line
               for(int j=0; j<pts[0].length; j++) {
                  lsa.setColor(j, selectedColor);
               }
            }
            else if(closest[i+startLine]) { // a closest line
               for(int j=0; j<pts[0].length; j++) {
                  lsa.setColor(j, closestColor);
               }
            }
            else {  // other lines
               for(int j=0; j<pts[0].length; j++) {
                  lsa.setColor(j, plainColor);
               }
            }

            lsaArray[i] = lsa;

            newShapes[i] = new Shape3D(lsaArray[i]);
            newShapes[i].setCapability(Shape3D.ALLOW_GEOMETRY_READ);
            newShapes[i].setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
         }

         shapes = newShapes;
         return shapes;

      }

      public BranchGroup createShapeGroup() {
         // creates the BranchGroup containing the lines
         BranchGroup total = new BranchGroup();
         BranchGroup temp;
         Transform3D trans = new Transform3D();
         TransformGroup tg;
         total.setCapability(BranchGroup.ALLOW_DETACH);

         shapes = createShapes();
         for(int i = 0; i < shapes.length; i++) {
            temp = new BranchGroup();
            temp.addChild(shapes[i]);
            total.addChild(temp);
         }

         phb.setShapes(shapes);
         return total;

      }

      public BranchGroup createShapeGroup(int upperBound) {
         // creates the BranchGroup containing the lines
         BranchGroup total = new BranchGroup();
         BranchGroup temp;
         Transform3D trans = new Transform3D();
         TransformGroup tg;
         total.setCapability(BranchGroup.ALLOW_DETACH);

         shapes = createShapes(upperBound);
         for(int i = 0; i < shapes.length; i++) {
            temp = new BranchGroup();
            temp.addChild(shapes[i]);
            total.addChild(temp);
         }

         phb.setShapes(shapes);
         return total;
      }

      public BranchGroup createScene(LineGraphVerticalTable dat) {
         Transform3D trans = new Transform3D();
         Transform3D temp = new Transform3D();

         // set view
         temp.rotX(Math.PI/8.0d);
         trans.mul(temp);
         temp.rotY(Math.PI/4.0d);
         trans.mul(temp);
         objTrans = new TransformGroup(trans);

         objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
         objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
         objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
         objRoot.addChild(objTrans);

         // Note: Order in which children are added to objTrans is important for
         //       future code. The main shape group of lines is the first
         //       added, and becomes child 0. This is important when replacing it
         //       and other BranchGroups(axis labels, etc.)
         //       when the scene is changed using objTrans.setChild().

         BranchGroup b = createShapeGroup(10);
         b.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
         b.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
         objTrans.addChild(b);     // child 0

         // create axes
         LineArray xaxis = new LineArray(2, LineArray.COORDINATES |
            LineArray.COLOR_3);
         LineArray yaxis = new LineArray(2, LineArray.COORDINATES |
            LineArray.COLOR_3);
         LineArray zaxis = new LineArray(2, LineArray.COORDINATES |
            LineArray.COLOR_3);
         LineArray zaxis2 = new LineArray(2, LineArray.COORDINATES |
            LineArray.COLOR_3);

         xaxis.setCoordinate(0, new Point3f(-xmax, -ymax, -zmax));
         xaxis.setCoordinate(1, new Point3f(xmax, -ymax, -zmax));
         xaxis.setColor(0, axisColor);
         xaxis.setColor(1, axisColor);

         yaxis.setCoordinate(0, new Point3f(-xmax, -ymax, -zmax));
         yaxis.setCoordinate(1, new Point3f(-xmax, ymax, -zmax));
         yaxis.setColor(0, axisColor);
         yaxis.setColor(1, axisColor);

         zaxis.setCoordinate(0, new Point3f(-xmax, -ymax, -zmax));
         zaxis.setCoordinate(1, new Point3f(-xmax, -ymax, zmax));
         zaxis.setColor(0, axisColor);
         zaxis.setColor(1, axisColor);

         zaxis2.setCoordinate(0, new Point3f(xmax, -ymax, -zmax));
         zaxis2.setCoordinate(1, new Point3f(xmax, -ymax, zmax));
         zaxis2.setColor(0, axisColor);
         zaxis2.setColor(1, axisColor);

         Shape3D shape = new Shape3D(xaxis);
         objTrans.addChild(shape);   // child 1
         shape = new Shape3D(yaxis);
         objTrans.addChild(shape);   // child 2
         shape = new Shape3D(zaxis);
         objTrans.addChild(shape);   // child 3

         objTrans.addChild(drawXScale());  // child 4
            objTrans.addChild(drawYScale());  // child 5
         objTrans.addChild(drawZScale());  // child 6

         //draw grid
         for(int i=1; i<11; i++) {
            LineArray horiz = new LineArray(2, LineArray.COORDINATES |
               LineArray.COLOR_3);
            LineArray vert = new LineArray(2, LineArray.COORDINATES |
               LineArray.COLOR_3);
            horiz.setCoordinate(0, new Point3d(xmax, (double)(i*.1d)-ymax, -zmax));
            horiz.setCoordinate(1, new Point3d(-xmax, (double)(i*.1d)-ymax, -zmax));
            horiz.setColor(0, axisColor);
            horiz.setColor(1, axisColor);

            vert.setCoordinate(0, new Point3d((double)(i*.1d)-xmax, ymax, -zmax));
            vert.setCoordinate(1, new Point3d((double)(i*.1d)-xmax, -ymax, -zmax));
            vert.setColor(0, axisColor);
            vert.setColor(1, axisColor);

            shape = new Shape3D(horiz);
            objTrans.addChild(shape);
            shape = new Shape3D(vert);
            objTrans.addChild(shape);
         }

         shape = new Shape3D(zaxis2);
         objTrans.addChild(shape);

         objRoot.setCapability(Group.ALLOW_CHILDREN_READ);
         objTrans.setCapability(Group.ALLOW_CHILDREN_READ);
         objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

         /* KeyNavigatorBehavior:
            Up/Down arrows: translate +/- in Z direction
            Left/Right arrows: rotate around Y(up) direction
            Page Up/ Page Down: rotate in X direction
            Keypad +/- : Zoom in/ Zoom out
            = : reset to original view
         */

         KeyNavigatorBehavior keyNav = new KeyNavigatorBehavior(objTrans);
         keyNav.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(keyNav);

         /* MouseRotation
         MouseRotate mr = new MouseRotate(objTrans);
         mr.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
         objRoot.addChild(mr);
         */

         Background background = new Background(backgroundColor);
         background.setApplicationBounds(new BoundingSphere(new Point3d(0.0d, 0.0d, 0.0d), 1000));
         objRoot.addChild(background);

         return objRoot;
      }

      public BranchGroup drawXScale() { // child 4
                                      // Line Numbers

         BranchGroup bg = new BranchGroup();
         bg.setCapability(BranchGroup.ALLOW_DETACH);
         bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
         Text2D text;

         int skipfactor = shapes.length / 10;
         boolean dodraw;  // interpolate so as to draw
                          // 10 line numbers max

         for(int i=0; i<shapes.length; i++) {
            Transform3D trans = new Transform3D();
            Transform3D temp = new Transform3D();
            trans.rotY(Math.PI/2.0);
            temp.setTranslation(new Vector3f(xmax+.4f, -ymax, (float)(i*lineSpace)-zmax));
            trans.mul(temp);
            temp.rotY(Math.PI);
            trans.mul(temp);
            TransformGroup tg = new TransformGroup(trans);
            tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

            if (((startLine+i) % skipfactor == 0) || shapes.length < 10)
               dodraw = true;
            else
               dodraw = false;

            if (dodraw) {
               if(i+startLine == index) {  // selected line
                  text = new Text2D("Line " + (startLine+i), selectedColor, "Helvetica", 20, 1);
               }
               else if(closest[i+startLine]) { // one of closest
                  text = new Text2D("Line " + (startLine+i), closestColor, "Helvetica", 20, 1);
               }
               else {  // other lines
                  text = new Text2D("Line " + (startLine+i), plainColor, "Helvetica", 20, 1);
               }
               tg.addChild(text);
            }

            bg.addChild(tg);
         }

         return bg;

      }

      public BranchGroup drawYScale() { // child 5

         Transform3D trans = new Transform3D();
         BranchGroup bg = new BranchGroup();
         TransformGroup tg3 = new  TransformGroup();
         bg.setCapability(BranchGroup.ALLOW_DETACH);
         double n = dt.getMax(field)-dt.getMin(field);

         float inc = (float)n/10.0f;
         Text2D label;
         for(int i=0; i<11; i++) {

            String s = (new Double((i*inc)+dt.getMin(field))).toString();
            if(i == 10) { // maximum value
               s = (new Double(dt.getMax(field))).toString();
            }

            // shorten string
            if(s.length() > 4) {
               s=s.substring(0,5);
            }

            label = new Text2D(""+s, labelColor, "Helvetica", 20, 1);
            label.setCapability(Text2D.ALLOW_GEOMETRY_READ);
            label.setCapability(Text2D.ALLOW_GEOMETRY_WRITE);
            label.setCapability(Text2D.ALLOW_APPEARANCE_READ);
            label.setCapability(Text2D.ALLOW_APPEARANCE_WRITE);
            trans = new Transform3D();

            trans.setTranslation(new Vector3f(-xmax-.2f, (float)(2*i*inc*ymax/(float)n)-ymax, -zmax));
            TransformGroup textTranslationGroup = new TransformGroup(trans);
            textTranslationGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            textTranslationGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            textTranslationGroup.addChild(label);
            bg.addChild(textTranslationGroup);

         }

         // create Main Axis Label
         trans.setTranslation(new Vector3f(-xmax, ymax+.1f, -zmax));
         label = new Text2D(""+(dt.getNames())[field-1], labelColor, "Helvetica", 25, 1);
         label.setCapability(Text2D.ALLOW_GEOMETRY_READ);
         label.setCapability(Text2D.ALLOW_GEOMETRY_WRITE);
         label.setCapability(Text2D.ALLOW_APPEARANCE_READ);
         label.setCapability(Text2D.ALLOW_APPEARANCE_WRITE);
         TransformGroup tg = new TransformGroup(trans);
         tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
         tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         tg.addChild(label);

         bg.addChild(tg);
         return bg;
      }

      public BranchGroup drawZScale() {      // time

         BranchGroup bg = new BranchGroup();
         int n = dt.getNumRecords();
         bg.setCapability(BranchGroup.ALLOW_DETACH);

         // try to create appropriate scale for labels
         int inc = 1;
         if(n > 10) {
            inc = 10;
         }
         if(n > 50) {
            inc = 50;
         }
         if(n > 100) {
            inc = 100;
         }
         if(n > 500) {
            inc = 500;
         }
         if(n > 1000) {
            inc = 1000;
         }
         if(n > 5000) {
            inc = 5000;
         }
         if(n > 10000) {
            inc = 10000;
         }

         for(int i=0; i<n; i+= inc) {
            if(i==0) {}
            else{
               Transform3D trans = new Transform3D();
               trans.setTranslation(new Vector3f(-xmax-.2f, -ymax, (float)pointSpace*i-zmax));
               TransformGroup tg = new TransformGroup(trans);

               Text2D text = new Text2D("" + (int)(n*((double)(i)/(double)n)), labelColor, "Helvetica", 20, 1);
               tg.addChild(text);
               bg.addChild(tg);
            }
         }

         Text2D time = new Text2D(dt.getNumRecords() + "", labelColor, "Helvetica", 25, 1);
         Transform3D trans = new Transform3D();
         Transform3D temp = new Transform3D();
         trans.setTranslation(new Vector3f(-xmax, -ymax, zmax-.2f));
         temp.rotX(-Math.PI/2);
         trans.mul(temp);
         temp.rotZ(-Math.PI/2);
         trans.mul(temp);

         TransformGroup tg = new TransformGroup(trans);
         tg.addChild(time);
         bg.addChild(tg);

         return bg;

      }

      public void changeFields(int newField) {

         field = newField;
         fc.setField(newField);

         // create scale so all lines fit within the box
         yscale = 1/(dt.getMax(field)-dt.getMin(field));
         // calculate offset so all lines fit within the box
         yoffset = (float)dt.getMin(field);

         for(int i=0; i<dt.getNumLines(); i++) {
            closest[i] = false; // clear closest[]
         }

         objTrans.setChild(createShapeGroup(startLine+shapes.length-1), 0);

         objTrans.setChild(drawXScale(), 4);
         objTrans.setChild(drawYScale(), 5);

         index = -1;

      }

      public void createClosestNShapes(int n) {

         fc.setField(field);

         if(phb.getSelectedIndex() != -1) {
            index = startLine+phb.getSelectedIndex();
         }

         origIndex = index;

         if(n>=dt.getNumLines()){
            n=dt.getNumLines()-1;
         }
         if(n<0)
            n=0;
         lineSpace = 2*xmax/(float) n;
         double z = -zmax;
         double x = -xmax;
         int next = 0;

         if(index == -1) {}
         else {

            int indx = startLine+origIndex;
            Point3d[][] points = new Point3d[n+1][dt.getNumRecords()];
            Text2D[] lineNumbers = new Text2D[n+1];
            Shape3D[] newShapes = new Shape3D[n+1];
            LineStripArray[] lsaArray = new LineStripArray[n+1];
            int[] a = {dt.getNumRecords()};

            DistanceInfo[] d = fc.getDistanceInfo();
            // DistanceInfo[] d contains the lines sorted in order of similarity to
            // the selected line.
            // See also: DistanceInfo.java, FindClosest.java

            for(int i=0; i<n+1; i++) {
               int lineNum = d[i].GetLineNum();
               // System.out.println("Distance " + i + " = " + d[i].GetDistance());
               if(lineNum == index)
                  lineNumbers[i] = new Text2D("Line " + lineNum, selectedColor, "Helvetica", 20, 1);
               else
                  lineNumbers[i] = new Text2D("Line " + lineNum, closestColor, "Helvetica", 20, 1);

               for(int j=0; j<dt.getNumRecords(); j++) {
                  points[i][j] = new Point3d(x, yscale*(dt.getRecord(lineNum, field, j)-dt.getMin(field))-ymax, z);
                  z+=pointSpace;
               }

               z=-zmax;
               x+=lineSpace;
            }

            // create LineStripArrays
            for(int i=0; i<n+1; i++) {
               LineStripArray lsa = new LineStripArray(dt.getNumRecords(), GeometryArray.COORDINATES | GeometryArray.COLOR_3, a);
               lsa.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
               lsa.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
               lsa.setCapability(GeometryArray.ALLOW_COUNT_READ);
               lsa.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
               lsa.setCapability(GeometryArray.ALLOW_COLOR_READ);

               lsa.setCoordinates(0, points[i]);

               if(d[i].GetLineNum()==index) {  // selected line, highlight
                  for(int j=0; j<points[0].length; j++) {
                     lsa.setColor(j, selectedColor);
                  }
               }
               else {    // the closest lines
                  for(int j=0; j<points[0].length; j++) {
                     lsa.setColor(j, closestColor);
                  }
               }
               lsaArray[i] = lsa;
               newShapes[i] = new Shape3D(lsa);
               newShapes[i].setCapability(Shape3D.ALLOW_GEOMETRY_READ);
               newShapes[i].setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

            }

            BranchGroup total = new BranchGroup();
            BranchGroup temp;
            Transform3D trans = new Transform3D();
            TransformGroup tg;
            total.setCapability(BranchGroup.ALLOW_DETACH);

            // add shapes to a BranchGroup
            for(int i=0; i<newShapes.length; i++) {
               temp = new BranchGroup();
               temp.addChild(newShapes[i]);
               total.addChild(temp);
            }

            phb.setShapes(newShapes);
            objTrans.setChild(total, 0);

            BranchGroup bg = new BranchGroup();
            bg.setCapability(BranchGroup.ALLOW_DETACH);

            // add line numbers to BranchGroup
            for(int i=0; i<lineNumbers.length; i++) {
               Transform3D trans2 = new Transform3D();
               Transform3D temp2 = new Transform3D();
               trans2.rotY(Math.PI/2.0);
               temp2.setTranslation(new Vector3f(xmax+.4f, -ymax, (float)(i*lineSpace)-zmax));
               trans2.mul(temp2);
               temp2.rotY(Math.PI);
               trans2.mul(temp2);
               TransformGroup tg2 = new TransformGroup(trans2);

               tg2.addChild(lineNumbers[i]);
               bg.addChild(tg2);
            }

            shapes = newShapes;
            objTrans.setChild(bg,4);
            // set inClosest: not allowed to do anything else until
            // "DoIt" or the "increment" or "decrement" buttons are pressed.
            inClosest = true;
            phb.setClosest(true);

         }

      }

   }

/*
   public interface ClosestInterface {
      public boolean[] findClosestN(int selected, int number2find);
      public double computeCloseness(int line, int line2, int field);
   }
*/

   public class DistanceInfo {

      int LineNum = 0;
      double Distance = 0.0;

      public DistanceInfo(int Line, double Dist) {
         LineNum = Line;
         Distance = Dist;
      }

      public boolean lessthan(DistanceInfo item) {
         return (Distance<(item).GetDistance());
      }

      public int GetLineNum() {
         return LineNum;
      }

      public double GetDistance() {
         return Distance;
      }

   }

   public class FindClosest implements /* ClosestInterface, */ java.io.Serializable {

      LineGraphVerticalTable dt;
      int field;
      DistanceInfo[] dist;

      /** Creates new FindClosest */
      public FindClosest(LineGraphVerticalTable dt) {
         this.dt = dt;
         field = 1;
      }

      public boolean[] findClosestN(int selected, int num2find) {
         System.out.println("findClosestN");
         System.out.println("num2find: "+num2find);

         DistanceInfo[] di = new DistanceInfo[dt.getNumLines()];
         boolean[] b = new boolean[di.length];

         // TODO: save only the line numbers of the closest lines
         /**int []lineNumArray = new int[num2find];
         int lineNumIndex = 0;
         DistanceInfo max = null;
         DistanceInfo min = null;
         DistanceInfo temp = null;
         */

         for(int i=0; i<b.length; i++) {
            b[i] = false; // initialize
         }

         if(selected != -1) {

            for(int i=0; i<dt.getNumLines(); i++) {

               di[i] = new DistanceInfo(i,
                  computeCloseness(selected, i,field));

               // temp = new DistanceInfo(i,
               // computeCloseness(selected, i,field));

            }

            Arrays.sort(di, new CompareCloseness());

            for(int i=0; i<num2find+1; i++) {
               b[di[i].GetLineNum()] = true;
            }

         }

         dist = di;
         return b;
      }

      public DistanceInfo[] getDistanceInfo() {
         return dist;
      }

      public double computeCloseness(int line, int line2, int field) {

         // System.out.println("computeCloseness");
         double totaldiff = 0.0d;
         double tempdiff = 0.0d;

         if(line==-1) {}
         else {

            for(int i=0; i<dt.getNumRecords(); i++) {

               // System.out.println("line, field, record = " + line + ","
               //    + field + "," + i);
               tempdiff = dt.getRecord(line, field, i)-dt.getRecord(line2,
                  field, i);
               // totaldiff += tempdiff*tempdiff;
               totaldiff += Math.abs(tempdiff);

            }

         }

         return totaldiff;

      }

      public void setField(int newfield) {
         this.field = newfield;
      }

      class CompareCloseness implements Comparator {

         public int compare(Object a, Object b) {

            if(((DistanceInfo)a).lessthan(((DistanceInfo)b)))
               return -1;
            else return 1;

         }

      }

   }

   public class LineGraphCanvas extends Canvas3D {

      public LineGraphCanvas(GraphicsConfiguration graphicsConfig) {
         super(graphicsConfig);
      }

      public Dimension getMinimumSize() {
         return new Dimension(300, 300);
      }

      public Dimension getPreferredSize() {
         return new Dimension(300, 300);
      }

   }

   public class LineGraphVerticalTable extends TableImpl
      implements Serializable {

      int numRecords, numFields, numLines;
      public String[] names;
      private Table vtable;
      private HashLookupTable htable;

      public LineGraphVerticalTable(HashLookupTable inhlt, Table invt) {

         htable = inhlt; vtable = invt;
         names = new String[vtable.getNumColumns()];

         for (int i = 0; i < names.length; i++)
            names[i] = vtable.getColumnLabel(i);

         // process for number of records, fields, lines

         numLines = htable.getMerged(0).keySet().size();

         Object[] key = new Object[1];
         key[0] = (Object)new Double(1);
         numRecords = ((HashMap)htable.get(key)).keySet().size();

         numFields = vtable.getNumColumns() + 1;

      }

      public double getRecord(int line, int field, int record) {

         if (field == 0) {

            Object[] key = new Object[1];
            key[0] = (Object)new Double(line + 1);

            HashMap hm = (HashMap)htable.get(key);
            Object[] o = hm.keySet().toArray();
            Collections.sort(Arrays.asList(o));

            return ((Double)o[record]).doubleValue();

         }
         else {

            Object[] keys = new Object[3];
            keys[0] = (Object)new Double(line + 1);
            keys[1] = (Object)new Double(record + 1);
            keys[2] = (Object)names[field - 1];

            VTReference vtref = (VTReference)htable.get(keys);
            // System.out.println(line + " " + record + " " + keys[2]);
            return vtable.getDouble(vtref.row, vtref.col);

         }

      }

      public int getNumRecords() {
         return numRecords;
      }

      public int getNumLines() {
        return numLines;
      }

      public int getNumFields() {
         return numFields;
      }

      public String[] getNames() {
        return names;
      }

      public void setNames(String[] newNames) {
         names = newNames;
      }

      public int getNumTotalRecords() {
         return vtable.getNumRows();
      }

      public double getMax(int field) {
         if (field == 0) {
            System.out.println("This shouldn't be happening (Max)");
            return 120;
         }
         else {
            double[] mm = getMinAndMax(vtable, field - 1);
            return mm[1];
         }
      }

      public double getMin(int field) {
         if (field == 0) {
            System.out.println("This shouldn't be happening (Min)");
            return 0;
         }
         else {
            double[] mm = getMinAndMax(vtable, field - 1);
            return mm[0];
         }
      }

   }

	public double[] getMinAndMax(Table table, int ndx) {
		double[] minAndMax = new double[2];
		double mandm;
		for (int i = 0; i < table.getNumRows(); i++) {
			mandm = table.getDouble(i, ndx);
			if (mandm > minAndMax[1]) {
				minAndMax[1] = mandm;
			}
			if (mandm < minAndMax[0]) {
				minAndMax[0] = mandm;
			}
		}
		return minAndMax;
	}
/*
 * @(#)PickHighlightBehavior.java 1.11 00/03/20 09:08:51
 *
 * Copyright (c) 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

   public class PickHighlightBehavior extends PickMouseBehavior {

      Appearance savedAppearance = null;
      Shape3D oldShape = null;
      Shape3D[] shapes;
      boolean selected = false;
      boolean[] b;
      boolean inClosest = false;
      LineGraphAppModuleView view;

      public final Color3f selectedColor = new Color3f(1.0f, 1.0f, .4f); // yellow
      public final Color3f plainColor = new Color3f(0.8f, 0.9254901f, .9568627f);  // lt. blue
      public final Color3f closestColor = new Color3f(1.0f, 0.6f, 0.0f); // orange

      Appearance highlightAppearance;

      public PickHighlightBehavior(Canvas3D canvas, BranchGroup root,
                Bounds bounds, LineGraphAppModuleView v) {

         super(canvas, root, bounds);
         this.setSchedulingBounds(bounds);
         root.addChild(this);
         view = v;

         // Material highlightMaterial = new Material(highlightColor, black,
         //    highlightColor, white, 80.0f);
         highlightAppearance = new Appearance();
         // highlightAppearance.setMaterial(new Material(highlightColor, black,
         //    highlightColor, white, 80.0f));
         highlightAppearance.setColoringAttributes(new ColoringAttributes(selectedColor, ColoringAttributes.FASTEST));
         pickCanvas.setMode(PickTool.BOUNDS);

      }

      public void updateScene(int xpos, int ypos) {

         PickResult pickResult = null;
         Shape3D shape = null;

         // else if(pickCanvas.getCanvas().getView().getProjectionPolicy() == null) {
         //    System.out.println("ProjectionPolicy == null");
         // }

         pickCanvas.setShapeLocation(xpos, ypos);

         pickResult = pickCanvas.pickClosest();

         if(inClosest) {}
         else {
            if(pickResult == null) {
               selected = false;
               view.index = -1;
               view.objTrans.setChild(view.drawXScale(), 4);
            }
        // if(pickResult != null) {
            else {
               if(pickResult.getNode(PickResult.SHAPE3D).getClass().getName().equals("com.sun.j3d.utils.geometry.Text2D")) {
               /*if(oldShape != null) {
                  LineStripArray l = (LineStripArray)(oldShape.getGeometry());
                  int[] a = new int[1];
                  l.getStripVertexCounts(a);
                  int q = a[0];
                  for(int i=0; i<q; i++) {
                  l.setColor(i, plainColor);
                  }
                  oldShape.setGeometry(l);
                  }
                  shape = null;
                  oldShape = null;*/

                  BranchGroup lineNumbers = ((BranchGroup)view.objTrans.getChild(4));

                  // children of lineNumbers = TransformGroups
                  // child of TransformGroup = Text2D
                  int i=0;

                  for(Enumeration e = lineNumbers.getAllChildren(); e.hasMoreElements();) {
                     if(((TransformGroup)e.nextElement()).getChild(0).equals((Text2D)pickResult.getNode(PickResult.SHAPE3D))) {

                        view.index = view.startLine + i;
                        setSelected(shapes[i]);
                        selected = true;
                        view.objTrans.setChild(view.drawXScale(), 4);
                        view.objTrans.setChild(view.createShapeGroup(view.startLine+shapes.length-1), 0);
                        shape = shapes[i];
                      }
                      i++;
                  }
               }

               else {
                  shape = (Shape3D)pickResult.getNode(PickResult.SHAPE3D);
               }

               if(shape == null) {
                  selected = false;
                  view.index = -1;
                  for(int i=0; i<view.closest.length; i++) {
                     view.closest[i] = false;
                  }
                  view.objTrans.setChild(view.drawXScale(), 4);
               }

               // clear
               for(int i=0; i<b.length; i++) {
                  b[i] = false;
               }

               if((shape != null) && (!shape.getCapability(Shape3D.ALLOW_GEOMETRY_READ))) {
                  shape = null;
                  selected = false;
                  for(int i=0; i<view.closest.length; i++) {
                     view.closest[i] = false;
                  }
                  view.index = -1;
                  view.objTrans.setChild(view.drawXScale(), 4);
               }

            }

            if(oldShape != null) {
               LineStripArray oldL = (LineStripArray)(oldShape.getGeometry());
               int[] a = new int[1];
               oldL.getStripVertexCounts(a);
               int q = a[0];
               // for(int i=0; i<q; i++) {
               //    oldL.setColor(i, plainColor);
               // }
               try {
                  for(int i=0; i<shapes.length; i++) {
                     for(int j=0; j<q; j++) {
                        ((LineStripArray)shapes[i].getGeometry()).setColor(j, plainColor);
                     }
                  }
               }
               catch(ArrayIndexOutOfBoundsException e) {
                  System.out.println(e);
                  System.out.println("Not done drawing previous scene.");
               }

               // oldShape.setGeometry(oldL);
               oldShape = null;
            }


            if(shape != null) {
               selected = true;
               LineStripArray l = (LineStripArray)(shape.getGeometry());
               int[] a = new int[1];
               l.getStripVertexCounts(a);
               int q = a[0];
               for(int i=0; i<q; i++) {
                  l.setColor(i, selectedColor);
               }
               shape.setGeometry(l);
               oldShape = shape;
               selected = true;
               for(int i=0; i<view.closest.length; i++) {
                  view.closest[i] = false;
               }
               view.index = view.startLine+getSelectedIndex();
               view.objTrans.setChild(view.drawXScale(), 4);
            }
         }
      }

      public Shape3D getSelected() {
         return oldShape;
      }

      public void setSelected(Shape3D newShape) {
         oldShape = newShape;
      }

      public void setShapes(Shape3D[] shapes) {
         this.shapes = shapes;
      }

      public void setClosest(boolean[] b) {
         this.b = b;
      }

      public int getSelectedIndex() {
         if(shapes != null) {
            for(int i=0; i<shapes.length; i++) {
               if((oldShape != null) && (oldShape.equals(shapes[i]))) {
                  return i;
               }
            }
         }
         return -1;
      }

      public void setClosest(boolean b) {
         inClosest = b;
      }

      public boolean isSelected() {
         return selected;
      }

   }

   public class SimpleButton extends JPanel {

      /** True if this is currently pressed */
      protected boolean pressed = false;

      /** The action listener */
      protected ActionListener listener = null;

      /** The icon to use when this is enabled */
      protected Icon normalIcon = null;
      /** The icon to use when this is disabled */
      protected Icon pressedIcon = null;
      /** The icon that is currenly used */
      protected Icon currentIcon = null;

      /** The width of this */
      protected int myWidth;
      /** The height of this */
      protected int myHeight;

      /** Don't use this! */
      protected SimpleButton() {}

      public SimpleButton(Icon norm, Icon pres) {

         enableEvents(AWTEvent.MOUSE_EVENT_MASK);

         normalIcon = norm;
         pressedIcon = pres;

         currentIcon = normalIcon;

         myWidth = normalIcon.getIconWidth();
         myHeight = normalIcon.getIconHeight();

         setSize(myWidth, myHeight);
      }

      /**
        Return the insets of this button
        @return the insets
      */
      public Insets getInsets() {
         return new Insets(0, 0, 0, 0);
      }

      public void paintComponent(Graphics g) {
         super.paintComponent(g);

         currentIcon.paintIcon(this, g, 0, 0);
      }

      /**
       Return the preferred size
       @return the preferred size of this button
      */
      public Dimension getPreferredSize() {
         return new Dimension(myWidth, myHeight);
      }

      /**
       Return the preferred size
       @return the preferred size of this button
      */
      public Dimension getMinimumSize() {
         return getPreferredSize();
      }

      /**
       Return the preferred size
       @return the preferred size of this button
      */
      public Dimension getMaximumSize() {
         return getPreferredSize();
      }

      /**
       *   Add an action listener.
       *   @param al the ActionListener to add
       */
      public void addActionListener(ActionListener al) {
         enableEvents(AWTEvent.ACTION_EVENT_MASK);
         listener = AWTEventMulticaster.add(listener, al);
      }

      /**
       *   Remove an action listener.
       *   @param al the ActionListener to remove
       */
      public void removeActionListener(ActionListener al) {
         listener = AWTEventMulticaster.remove(listener, al);
      }

      /**
       *   Process the mouse events. Repaint accordingly and
       *   alert our ActionListeners when pressed.
       *   @param e the MouseEvent
       */
      public void processMouseEvent(MouseEvent e) {

         switch(e.getID()) {
         case(MouseEvent.MOUSE_PRESSED):
            // alert the ActionListener
            if(listener != null) {
               listener.actionPerformed(new ActionEvent(this,
                  ActionEvent.ACTION_PERFORMED, "p"));
            }

            currentIcon = pressedIcon;
            repaint();
            break;

         case(MouseEvent.MOUSE_RELEASED):
            currentIcon = normalIcon;

            repaint();
            break;

         default:
            break;
         }

         super.processMouseEvent(e);
      }

   }

   public class WhiteLabelUI extends MetalLabelUI {

      // paint the text white
      protected void paintEnabledText(JLabel l, Graphics g, String s,
        int textX, int textY) {

         int accChar = l.getDisplayedMnemonic();
         g.setColor(new Color(255, 255, 255));
         BasicGraphicsUtils.drawString(g, s, accChar, textX, textY);

      }

      // paint the text white
      protected void paintDisabledText(JLabel l, Graphics g, String s,
         int textX, int textY) {

         int accChar = l.getDisplayedMnemonic();
         g.setColor(new Color(255, 255, 255));
         BasicGraphicsUtils.drawString(g, s, accChar, textX, textY);

      }
   }

   public class YellowLabelUI extends MetalLabelUI {

      // paint the text yellow
      protected void paintEnabledText(JLabel l, Graphics g, String s,
         int textX, int textY) {

         int accChar = l.getDisplayedMnemonic();
         g.setColor(new Color(255, 255, 127));
         BasicGraphicsUtils.drawString(g, s, accChar, textX, textY);

      }

      // paint the text yellow
      protected void paintDisabledText(JLabel l, Graphics g, String s,
         int textX, int textY) {

         int accChar = l.getDisplayedMnemonic();
         g.setColor(new Color(255, 255, 127));
         BasicGraphicsUtils.drawString(g, s, accChar, textX, textY);

      }

   }

}
