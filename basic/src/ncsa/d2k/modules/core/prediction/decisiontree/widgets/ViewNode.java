package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;

/*
 DecisionTreeVis

 Represents a decision tree node with a bar graph
*/
public class ViewNode {

  private static final String GREATER_THAN = ">";
  private static final String LESS_THAN = "<";
  private static final String GREATER_THAN_EQUAL_TO = ">=";
  private static final String LESS_THAN_EQUAL_TO = "<=";
  private static final String NOT_EQUAL_TO = "!=";
  private static final String EQUAL_TO = "==";

  // Decision tree model
  ViewableDTModel dmodel;

  ViewableDTNode dnode;
  ViewNode parent;
  ArrayList children;

  // Distribution values
  double[] values;

  boolean search = false;
  boolean searchbackground = false;

  boolean collapsed = false;

  // X is midpoint of node, y is top left of bar graph
  double x, y;

  // Space between nodes
  public static double xspace = 20;
  public static double yspace = 80;

  double gwidth;
  double gheight = 45;
  double leftinset = 5;
  double rightinset = 5;
  double barwidth = 16;
  double barspace = 5;
  double ygrid = 5;
  double tickmark = 3;

  String branchlabel;

  double searchspace = 4;

  double tside = 8;
  double tspace = 10;
  double theight;

  double yscale;
  double scalesize = 100;
  double xincrement, yincrement;

  static JFrame graphicsframe;

  DecisionTreeScheme scheme;

  public ViewNode(ViewableDTModel model, ViewableDTNode node, ViewNode vnode, String label) {
    dmodel = model;
    dnode = node;
    parent = vnode;
    branchlabel = label;
    children = new ArrayList(dnode.getNumChildren());

    findValues();

    scheme = new DecisionTreeScheme();

    gwidth = leftinset + tickmark + (barwidth + barspace)*values.length + rightinset;
    yincrement = gheight/(ygrid+1);
    yscale = (gheight - 2*yincrement)/scalesize;

    if (graphicsframe == null) {
      graphicsframe = new JFrame();
      graphicsframe.addNotify();
      graphicsframe.setFont(DecisionTreeScheme.textfont);
    }
  }

  public ViewNode(ViewableDTModel model, ViewableDTNode node, ViewNode vnode) {
    this(model, node, vnode, null);
  }

  public void addChild(ViewNode vnode) {
    children.add(vnode);
  }

  public ViewNode getChild(int index) {
    return (ViewNode) children.get(index);
  }

  public int getNumChildren() {
    return children.size();
  }

  public boolean isLeaf() {
    if (children.size() == 0)
      return true;

    return false;
  }

  public int getDepth() {
    if (parent == null)
      return 0;

    return parent.getDepth() + 1;
  }

  public String getBranchLabel(int index) {
    return dnode.getBranchLabel(index);
  }

  public void findValues() {
    String[] output = dmodel.getUniqueOutputValues();
    values = new double[output.length];
    for (int index = 0; index < values.length; index++){
      try {
        values[index] = 100*(double)dnode.getOutputTally(output[index])/(double)dnode.getTotal();
      } catch (Exception exception) {
        System.out.println("Exception from getOutputTally");
      }
    }
  }

  public double getWidth() {
    Graphics g = null;

    while (g == null)
      g = graphicsframe.getGraphics();

    FontMetrics metrics = g.getFontMetrics();
    int swidth1;

    if (branchlabel != null)
      swidth1 = metrics.stringWidth(branchlabel);
    else
      swidth1 = 0;

    double width1 = swidth1*2;
    double width2 = xspace + gwidth + xspace;

    if (width1 > width2)
      return width1;
    else
      return width2;
  }

  public double findSubtreeWidth() {
    if (isLeaf())
      return getWidth();

    double subtreewidth = 0;

    for (int index = 0; index < getNumChildren(); index++) {
      ViewNode vchild = getChild(index);
      subtreewidth += vchild.findSubtreeWidth();
    }

    return subtreewidth;
  }

  // Width from midpoint to leftmost child node
  public double findLeftSubtreeWidth() {
    if (isLeaf())
      return getWidth()/2;

    int numchildren = getNumChildren();

    if (numchildren%2 == 0) {
      int midindex = numchildren/2;
      return findIntervalWidth(0, midindex-1);
    }
    else {
      int midindex = numchildren/2 + 1;
      ViewNode midchild = getChild(midindex-1);
      return findIntervalWidth(0, midindex-2) + midchild.findLeftSubtreeWidth();
    }
  }

  // Width from midpoint to rightmost child node
  public double findRightSubtreeWidth() {
    if (isLeaf())
      return getWidth()/2;

    int numchildren = getNumChildren();

    if (numchildren%2 == 0) {
      int midindex = numchildren/2;
      return findIntervalWidth(midindex, numchildren-1);
    }
    else {
      int midindex = numchildren/2 + 1;
      ViewNode midchild = getChild(midindex-1);
      return findIntervalWidth(midindex, numchildren-1) + midchild.findRightSubtreeWidth();
    }
  }

  // Determines offsets of children
  public void findOffsets() {
    int numchildren = getNumChildren();

    if (numchildren%2 == 0) {
      int midindex = numchildren/2;

      for (int index = 0; index < numchildren; index++) {
        ViewNode vchild = getChild(index);

        if (index <= midindex-1)
          vchild.x = x - findIntervalWidth(index+1, midindex-1) - vchild.findRightSubtreeWidth();
        else
          vchild.x = x + findIntervalWidth(midindex, index-1) + vchild.findLeftSubtreeWidth();

        vchild.y = y + gheight + yspace;
      }
    }
    else {
      int midindex = numchildren/2 + 1;
      ViewNode midchild = getChild(midindex-1);

      for (int index = 0; index < numchildren; index++) {
        ViewNode vchild = getChild(index);

        if (index < midindex-1) {
          if (index == midindex-2)
            vchild.x = x - midchild.findLeftSubtreeWidth() - vchild.findRightSubtreeWidth();
          else
            vchild.x = x - midchild.findLeftSubtreeWidth() - findIntervalWidth(index+1, midindex-2) - vchild.findRightSubtreeWidth();
        }
        else if (index == midindex-1)
          vchild.x = x;
        else {
          if (index == midindex)
            vchild.x = x + midchild.findRightSubtreeWidth() + vchild.findLeftSubtreeWidth();
          else
            vchild.x = x + midchild.findRightSubtreeWidth() + findIntervalWidth(midindex, index-1) + vchild.findLeftSubtreeWidth();
        }

        vchild.y = y + gheight + yspace;
      }
    }
  }

  public double findIntervalWidth(int start, int end) {
    double intervalwidth = 0;

    for (; start <= end; start++) {
      ViewNode vchild = getChild(start);
      intervalwidth += vchild.findSubtreeWidth();
    }

    return intervalwidth;
  }

  // Find branch index of parent corresponding to node
  public int findBranchIndex() {
    if (parent == null)
      return -1;

    for (int index = 0; index < parent.getNumChildren(); index++) {
      ViewNode node = parent.getChild(index);

      if (node == this)
        return index;
    }

    return -1;
  }

  public void toggle() {
    if (collapsed)
      collapsed = false;
    else
      collapsed = true;
  }

  public boolean isVisible() {
    ViewNode vnode = this;
    while (vnode.parent != null) {
      if (vnode.parent.collapsed)
        return false;
      vnode = vnode.parent;
    }

    return true;
  }

  // Determine if given point falls in bounds of node
  public int test(int x1, int y1, double scale) {
    if (x1 >= scale*(x - gwidth/2) && x1 <= scale*(x + gwidth/2))
      return 1;

    if (x1 >= scale*(x + gwidth/2) && x1 <= scale*(x + gwidth/2 + tspace + tside + tspace)) {
      if (y1 >= scale*(y + gheight - tside - tspace) && y1 <= scale*(y + gheight))
        return 2;
    }

    return -1;
  }

  public void drawViewNode(Graphics2D g2) {
    double x1, y1;

    if (search) {
      g2.setColor(scheme.searchcolor);
      g2.setStroke(new BasicStroke(.5f));
      g2.draw(new Rectangle2D.Double(x-gwidth/2-searchspace, y-searchspace, gwidth+2*searchspace, gheight+2*searchspace));
    }

    // Background
    if (searchbackground)
      g2.setColor(scheme.viewsearchbackgroundcolor);
    else
      g2.setColor(scheme.viewbackgroundcolor);

    g2.fill(new Rectangle2D.Double(x-gwidth/2, y, gwidth, gheight));

    // Tickmarks
    g2.setColor(scheme.viewtickcolor);
    g2.setStroke(new BasicStroke(1));
    x1 = x - gwidth/2 + leftinset;
    y1 = y + yincrement;
    for (int index = 0; index < ygrid; index++) {
      g2.draw(new Line2D.Double(x1, y1, x1+tickmark, y1));
      y1 += yincrement;
    }

    // Bars
    x1 = x - gwidth/2 + leftinset + tickmark + barspace;
    for (int index = 0; index < values.length; index++) {
      double barheight = yscale*values[index];
      y1 = y + 1 + gheight - yincrement - barheight;
      g2.setColor(scheme.getNextColor());
      g2.fill(new Rectangle2D.Double(x1, y1, barwidth, barheight));
      x1 += barwidth + barspace;
    }

    // Triangle
    if (isLeaf())
      return;

    theight = .866025*tside;
    double ycomponent = tside/2;
    double xcomponent = .577350*ycomponent;
    double xcenter, ycenter;

    if (collapsed) {
      xcenter = x + gwidth/2 + tspace + xcomponent;
      ycenter = y + gheight - ycomponent;

      int xpoints[] = {(int) (xcenter-xcomponent), (int) (xcenter+theight-xcomponent), (int) (xcenter-xcomponent)};
      int ypoints[] = {(int) (ycenter-ycomponent), (int) ycenter, (int) (ycenter+ycomponent)};

      GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xpoints.length);
      triangle.moveTo((int) (xcenter-xcomponent), (int) (ycenter-ycomponent));
      for (int index = 1; index < xpoints.length; index++) {
        triangle.lineTo(xpoints[index], ypoints[index]);
      }
      triangle.closePath();

      g2.setColor(scheme.viewtrianglecolor);
      g2.fill(triangle);
    }
    else {
      xcenter = x + gwidth/2 + tspace + xcomponent;
      ycenter = y + gheight - ycomponent;

      int xpoints[] = {(int) (xcenter-ycomponent), (int) (xcenter+ycomponent), (int) (xcenter)};
      int ypoints[] = {(int) (ycenter-xcomponent), (int) (ycenter-xcomponent), (int) (ycenter+ycomponent)};

      GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xpoints.length);
      triangle.moveTo((int) (xcenter-ycomponent), (int) (ycenter-xcomponent));
      for (int index = 1; index < xpoints.length; index++) {
        triangle.lineTo(xpoints[index], ypoints[index]);
      }
      triangle.closePath();

      g2.setColor(DecisionTreeScheme.viewtrianglecolor);
      g2.fill(triangle);
    }
  }

  // Search functions
  public void setSearchBackground(boolean search) {
    searchbackground = search;
  }

  public double findPurity() {
    double sum = 0;
    double numerator = 0;
    double base = Math.log(2.0);

    try {
      String[] outputs = dmodel.getUniqueOutputValues();
      for (int index = 0; index < outputs.length; index++) {
        double tally = dnode.getOutputTally(outputs[index]);
        numerator += -1.0*tally*Math.log(tally)/base;
        sum += tally;
      }
      numerator += sum*Math.log(sum)/base;
    } catch (Exception exception) {
      System.out.println(exception);
    }
    return numerator/sum;
  }

  // Determine type of condition and call specific evaluate function
  protected boolean evaluate(SearchPanel.Condition condition) {
    try {
      if (condition instanceof SearchPanel.PopulationCondition) {
        int population = dnode.getOutputTally(condition.attribute);
        return evaluate(population, condition.value, condition.operator);
      }

      else if (condition instanceof SearchPanel.PercentCondition) {
        double percent = 100*(double)dnode.getOutputTally(condition.attribute)/(double)dnode.getTotal();
        return evaluate(percent, condition.value, condition.operator);
      }

      else if (condition instanceof SearchPanel.PurityCondition) {
        double purity = findPurity();
        return evaluate(purity, condition.value, condition.operator);
      }

      else if (condition instanceof SearchPanel.SplitCondition) {
        SearchPanel.SplitCondition splitcondition = (SearchPanel.SplitCondition) condition;

        if (splitcondition.scalar)
          return evaluateScalar(splitcondition);
        else
          return evaluateNominal(splitcondition);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      return false;
    }

    return false;
  }

  // Evaluate double values based on operator
  boolean evaluate(double dvalue, double value, String operator) {

    if (operator == GREATER_THAN)
      return value < dvalue;

    else if (operator == GREATER_THAN_EQUAL_TO)
      return value <= dvalue;

    else if (operator == LESS_THAN)
      return value > dvalue;

    else if (operator == LESS_THAN_EQUAL_TO)
      return value >= dvalue;

    else if (operator == EQUAL_TO)
      return value == dvalue;

    else if (operator == NOT_EQUAL_TO)
      return value != dvalue;

    return false;
  }

  boolean evaluate(int index, double dvalue, double value, String operator) {
    if (index == 0) {
      if (operator == GREATER_THAN)
        return value < dvalue;

      else if (operator == GREATER_THAN_EQUAL_TO)
        return value < dvalue;

      else if (operator == LESS_THAN)
        return value >= dvalue;

      else if (operator == LESS_THAN_EQUAL_TO)
        return value >= dvalue;

      else if (operator == EQUAL_TO)
        return false;

      else if (operator == NOT_EQUAL_TO)
        return value != dvalue;
    }
    else
      return evaluate(dvalue, value, operator);

    return false;
  }

  // Evaluate string values based on operator
  boolean evaluate(String svalue, String value, String operator) {
    if (operator == EQUAL_TO)
      return value.equals(svalue);

    else if (operator == NOT_EQUAL_TO)
      return !value.equals(svalue);

    return false;
  }

  // Evaluate scalar split condition
  boolean evaluateScalar(SearchPanel.SplitCondition condition) {
    if (parent == null)
      return false;

    if (!(parent.dnode instanceof NumericViewableDTNode))
      return false;


    NumericViewableDTNode numericparent = (NumericViewableDTNode) parent.dnode;

    String attribute = numericparent.getSplitAttribute();

    if (!attribute.equals(condition.attribute))
      return false;

    double splitvalue = numericparent.getSplitValue();
    int index = findBranchIndex();

    return evaluate(index, splitvalue, condition.value, condition.operator);
  }

  // Evaluate nominal split condition
  boolean evaluateNominal(SearchPanel.SplitCondition condition) {
    if (parent == null)
      return false;

    if (!(parent.dnode instanceof CategoricalViewableDTNode))
      return false;

    CategoricalViewableDTNode categoricalparent = (CategoricalViewableDTNode) parent.dnode;

    String attribute = categoricalparent.getSplitAttribute();

    if (!attribute.equals(condition.attribute))
      return false;

    String[] splitvalues = categoricalparent.getSplitValues();
    int index = findBranchIndex();
    String splitvalue = splitvalues[index];

    return evaluate(splitvalue, condition.svalue, condition.operator);
  }
}