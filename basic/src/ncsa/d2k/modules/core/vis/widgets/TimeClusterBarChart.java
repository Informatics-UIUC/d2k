package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
 * This module creates a cluster bar chart visualization from <code>Table</code> data.
 * This module is specifically implemented for the data using a tilted window as a
 * dimension.
 * The table should contain 4 columns: data for X-axis, data for clustered value,
 * data for Y-axis and data for the granularity of the tilted time window.
 */

public class TimeClusterBarChart extends BarChart
    implements MouseListener, MouseMotionListener {
  private static final int LEFTOFFSET = 20;
  private static final int RIGHTOFFSET = 20;
  private static final int TOPOFFSET = 20;
  private static final int BOTTOMOFFSET = 20;

  // Clustering
  private int runs = 0;
  private int runSizeTotal;

  // Granularities
  private String[] granularities;
  private String[] legendStrings;
  private String[] legendTimeRanges;
  private int[] runSizes;
  private String[] granularityLevels;

  // Unique cluster values
  private HashSet valueset;
  // Unique Xaxis values
  private String[] uniqValues;

  // Data
  private MutableTable mutable;

  private double minimumgraphwidth;
  private double minimumgraphheight;

  private int legendspace = 35;

  private int longestwidthx;
  private int longestwidthy;
  private int longestwidthz;

  GradientColorSet[] barColors;

  Rectangle2D.Double rectangle;
  Rectangle2D.Double[]  barBoundary;    //array to keep rectangle boundary infomation
  String[] tipValues;

  public TimeClusterBarChart(Table table, DataSet set, GraphSettings settings, int xincrement, int yincrement) throws Exception {
    super(table, set, settings);

    // Dependencies
    if (table.getNumRows() == 0)
      throw new Exception("Table exception");

    if (!table.isColumnNominal(set.z))
      throw new Exception("Nominal exception");

    barColors = getGradientColorSets();

    mutable = table.toExampleTable();
    uniqValues = TableUtilities.uniqueValues(mutable, 0);
    uniqValues = sortStringArray(uniqValues);

    // Granularity for the time dimension
    granularities = TableUtilities.uniqueValues(mutable, set.g);
    // Strings to legend
    legendStrings = getLegendStrings(granularities);
    // Time range to show on legend
    legendTimeRanges = getLegendTimeRanges(mutable);
    // run size for each granularity level
    runSizes = getRunSizes(mutable);
    // distinct granularity levels
    granularityLevels = TableUtilities.uniqueValues(mutable, set.l);
    // sort the granularity levels asc.
    granularityLevels = sortLongArray(granularityLevels);

    settings.displayaxislabels = false;
    settings.displaylegend = true;

    gridsize = settings.gridsize;

    minimumxoffsetincrement = xincrement;
    minimumyoffsetincrement = yincrement;

    yvalueincrement = (ymaximum-yminimum)/gridsize;

    // reorder all columns. From this point on forward, all table access should
    // use column number, not set labels.
    int[] reorder = {set.x, set.y, set.z, set.g, set.l};
    mutable = (MutableTable) mutable.reorderColumns(reorder);
    //printTable(mutable);

    if (mutable.getNumColumns() > 5)
      mutable.removeColumns(5, mutable.getNumColumns()-5);

    // each granularity level has a runSize. Now get total run size.
    runSizeTotal = 0;
    for (int i=0; i<runSizes.length; i++) {
      runSizeTotal += runSizes[i];
    }

    int[] sort = {0, 2};
    int[] indices = TableUtilities.multiSortIndex(mutable, sort);
    mutable = (MutableTable) mutable.reorderRows(indices);
    //printTable(mutable);

    // Determine number of runs
    valueset = TableUtilities.uniqueValueSet(mutable, 2);
    String label = mutable.getString(0, 0);
    HashSet runset = new HashSet(valueset);
    int rows = mutable.getNumRows();
    for (int row=0; row < rows; row++) {
      String runlabel = mutable.getString(row, 0);
      String runtime = mutable.getString(row, 2);

      if (!runlabel.equals(label)) {
        // Fill in missing values
        Iterator iterator = runset.iterator();
        while (iterator.hasNext()) {
          String[] values = new String[mutable.getNumColumns()];
          values[0] = label;
          values[1] = new String("0.0");
          String timeValue = (String) iterator.next();
          values[2] = timeValue;
          values[3] = new String("0");  // for granularity
          String levelValue = findLevelValue(timeValue);
          values[4] = new String(levelValue);  // for granularity level
          mutable.addRow(values);
        }
        runs++;
        label = runlabel;

        runset = new HashSet(valueset);
      }
      runset.remove(runtime);
    }

    // Last run
    Iterator iterator = runset.iterator();
    while (iterator.hasNext()) {
      String[] values = new String[mutable.getNumColumns()];
      values[0] = label;
      values[1] = new String("0.0");
      String timeValue = (String) iterator.next();
      values[2] = timeValue;
      values[3] = new String("0");  // for granularity
      String levelValue = findLevelValue(timeValue);
      values[4] = new String(levelValue);  // for granularity level
      mutable.addRow(values);
    }
    runs++;

    // All new rows are added to the end, need to resort
    indices = TableUtilities.multiSortIndex(mutable, sort);
    mutable = (mutable.reorderRows(indices)).toExampleTable();

    // Include bins for spacing runs
    // Impacts mapping of bins to table values
    bins = (runSizeTotal+1)*runs;

    addMouseListener(this);
  }

  /**
   * This chart makes missing values as an blank bar which has count = 0, therefore we need
   * to find the granularity level for the missing value. By matching the time value,
   * we can find the granularity level from other existing rows.
   * @param timeValue
   * @return
   */
  public String findLevelValue(String timeValue) {
    for (int row=0; row<mutable.getNumRows(); row++) {
      if (mutable.getString(row,2).equals(timeValue)) {
        return mutable.getString(row,4);
      }
    }
    return "0"; // should never reach here
  }

  public GradientColorSet[] getGradientColorSets() {
    GradientColorSet[] colorSets = new GradientColorSet[6];
    GradientColorSet colorSet;
    colorSet = new GradientColorSet(127,253,6,200,0,149);
    colorSets[0] = colorSet;
    colorSet = new GradientColorSet(13,204,40,234,11,141);
    colorSets[1] = colorSet;
    colorSet = new GradientColorSet(161,255,95,214,0,0);
    colorSets[2] = colorSet;
    colorSet = new GradientColorSet(11,212,1,225,56,238);
    colorSets[3] = colorSet;
    colorSet = new GradientColorSet(109,248,1,179,91,216);
    colorSets[4] = colorSet;
    colorSet = new GradientColorSet(82,235,137,246,23,42);
    colorSets[5] = colorSet;
    return colorSets;
  }

  public void initOffsets() {
    NumberFormat numberformat = NumberFormat.getInstance();
    numberformat.setMaximumFractionDigits(3);

    // Determine maximum string widths
    // X axis
    for (int valIdx=0; valIdx<uniqValues.length; valIdx++) {
      int stringwidth = metrics.stringWidth(uniqValues[valIdx]);
      if (stringwidth > longestwidthx)
        longestwidthx = stringwidth;
    }

    // Y axis
    double yvalue =  yminimum;
    for (int index=0; index < gridsize; index++) {
      String value = numberformat.format(yvalue);

      int stringwidth = metrics.stringWidth(value);
      if (stringwidth > longestwidthy)
        longestwidthy = stringwidth;

      yvalue += yvalueincrement;
    }

    // Z axis
    String label = mutable.getColumnLabel(2);
    int labelwidth = metrics.stringWidth(label);
    if (labelwidth > longestwidthx)
      longestwidthz = labelwidth;

    // Determine offsets
    if (!settings.displaylegend) {
      legendheight = 0;
      legendwidth = 0;
    }
    else {
      String legendLabel = mutable.getString(0, 2);
      legendwidth = metrics.stringWidth(legendLabel);
      legendwidth += 3*smallspace+samplecolorsize;

      if (legendwidth < longestwidthz)
        legendwidth = longestwidthz;

      legendheight = (fontheight+smallspace)+(legendStrings.length*fontheight*2);
    }

    // Primary offsets
    leftoffset = LEFTOFFSET+longestwidthy;
    rightoffset = RIGHTOFFSET+legendwidth+2*legendspace;
    bottomoffset = BOTTOMOFFSET+longestwidthx;
    topoffset = TOPOFFSET;

    // Minimum dimensions
    minimumgraphwidth = minimumxoffsetincrement*bins+leftoffset+rightoffset;
    minimumgraphheight = Math.max(minimumyoffsetincrement*gridsize+topoffset+bottomoffset,
                                  legendheight+topoffset+bottomoffset);

    // Legend offsets
    legendleftoffset = getGraphWidth()-legendwidth-legendspace;
    legendtopoffset = getGraphHeight()/2-legendheight/2;
  }

  // Resize scale
  public void resize() {
    xoffsetincrement = (getGraphWidth()-leftoffset-rightoffset)/bins;
    yoffsetincrement = (getGraphHeight()-topoffset-bottomoffset)/gridsize;
    yscale = (ymaximum-yminimum)/(graphheight-topoffset-bottomoffset);
  }

  public int getGraphWidth() {
    if (getWidth() < minimumgraphwidth)
      return (int) minimumgraphwidth;

    return getWidth();
  }

  public int getGraphHeight() {
    if (getHeight() < minimumgraphheight)
      return (int) minimumgraphheight;

    return getHeight();
  }

  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  public Dimension getMinimumSize() {
    return new Dimension((int) minimumgraphwidth, (int) minimumgraphheight);
  }

  /*
    Drawing functions
  */

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

    font = g2.getFont();
    metrics = getFontMetrics(font);
    fontheight = metrics.getHeight();
    fontascent = metrics.getAscent();

    graphwidth = getWidth();
    graphheight = getHeight();

    // Determine offsets
    initOffsets();

    resize();

    yvalueincrement = (ymaximum-yminimum)/gridsize;

    yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
    xoffsetincrement = (graphwidth-leftoffset-rightoffset)/bins;

    yscale = (ymaximum-yminimum)/(graphheight-topoffset-bottomoffset);

    drawAxis(g2);
    if (settings.displaygrid)
      drawGrid(g2);
    if (settings.displaytickmarks)
      drawTickMarks(g2);
    if (settings.displayscale)
      drawScale(g2);
    if (settings.displayaxislabels)
      drawAxisLabels(g2);
    if (settings.displaytitle)
      drawTitle(g2);
    if (settings.displaylegend)
      drawLegend(g2);
    drawDataSet(g2, set);
    addMouseMotionListener(this);
  }

  public void drawTitle(Graphics2D g2) {
    int stringwidth = metrics.stringWidth(title);
    double x = (getGraphWidth()-stringwidth)/2;
    double y = (topoffset)/2 + fontheight/2;

    g2.drawString(title, (int) x, (int) y);
  }

  public void drawAxis(Graphics2D g2) {
    g2.draw(new Line2D.Double(leftoffset, topoffset,
                              leftoffset, getGraphHeight()-bottomoffset));
    g2.draw(new Line2D.Double(leftoffset, getGraphHeight()-bottomoffset,
                              getGraphWidth()-rightoffset, getGraphHeight()-bottomoffset));
  }

  public void drawTickMarks(Graphics2D g2) {
    double x = leftoffset+xoffsetincrement/2;

    // Map bins to runs
    int counter = 0;
    int offset = 0;

    for (int bin=0; bin < bins; bin++) {

      if (counter == runSizeTotal) {
        counter = 0;
        // draw a seperation line between clustered values
        g2.draw(new Line2D.Double(x, getGraphHeight()-bottomoffset, x, getGraphHeight()-bottomoffset+(tickmarksize*4)));
      }

      else {
        g2.draw(new Line2D.Double(x, getGraphHeight()-bottomoffset-tickmarksize, x, getGraphHeight()-bottomoffset+tickmarksize));
        counter++;
      }

      x += xoffsetincrement;
    }

    double y = topoffset+yoffsetincrement;
    for (int bin=0; bin < gridsize; bin++) {
      g2.draw(new Line2D.Double(leftoffset-tickmarksize, y, leftoffset+tickmarksize, y));
      y += yoffsetincrement;
    }
  }

  public void drawGrid(Graphics2D g2) {
    Color previouscolor = g2.getColor();
    g2.setColor(Color.gray);

    // y axis
    double y = topoffset+yoffsetincrement;
    for (int index=0; index < gridsize-1; index++) {
      g2.draw(new Line2D.Double(leftoffset, y, getGraphWidth()-rightoffset, y));
      y += yoffsetincrement;
    }

    g2.setColor(previouscolor);
  }

  public void drawScale(Graphics2D g2) {
    NumberFormat numberformat = NumberFormat.getInstance();
    numberformat.setMaximumFractionDigits(3);
    int ascent = metrics.getAscent();

    double xincrement = (runSizeTotal+1)*xoffsetincrement;

    double x = leftoffset + (xincrement/2);

    AffineTransform transform = g2.getTransform();
    g2.rotate(Math.toRadians(90));


    //for (int run=0; run < runs; run++) {
    for (int valIdx=0; valIdx<uniqValues.length; valIdx++) {
      String value = uniqValues[valIdx];
      int stringwidth = metrics.stringWidth(value);

      g2.drawString(value,
                    (int) (getGraphHeight()-bottomoffset+tickmarksize+smallspace),
                    (int) -(x-ascent/2));
      x += xincrement;
    }

    g2.setTransform(transform);

    double y = getGraphHeight()-bottomoffset;
    double yvalue =  yminimum;
    for (int index=0; index < gridsize; index++) {
      String value = numberformat.format(yvalue);
      int stringwidth = metrics.stringWidth(value);

      g2.drawString(value, (int) (leftoffset-stringwidth-smallspace), (int) (y+fontascent/2));
      y -= yoffsetincrement;
      yvalue += yvalueincrement;
    }
  }

  public void mouseClicked(MouseEvent event) {}

  public void mousePressed(MouseEvent event) {}

  public void mouseReleased(MouseEvent event) {
  }

  public void mouseEntered(MouseEvent event) {
  }

  public void mouseExited(MouseEvent event) {
  }

  public void mouseMoved(MouseEvent e) {
    setToolTipText(e);
  }

  public void setToolTipText(MouseEvent e) {
    double cx = e.getX();
    double cy = e.getY();
    String tip = "";
    // search barBoundary to find which bar has been pointed to
    for (int i=0; i<barBoundary.length; i++) {
      if (inRectangle(cx, cy, barBoundary[i])) {
        tip = tipValues[i];
        break;
      }
    }
    setToolTipText(tip);
  }

  public boolean inRectangle(double x, double y, Rectangle2D.Double rectangle) {
    if (x >= rectangle.getMinX() && x < rectangle.getMaxX() &&
        y >= rectangle.getMinY() && y < rectangle.getMaxY())
      return true;
    else
      return false;
  }

  HashMap map = new HashMap();

  public void drawLegend(Graphics2D g2) {
    double x = legendleftoffset;
    double y = legendtopoffset;

    g2.drawString(mutable.getColumnLabel(2), (int) x, (int) y);
    y += smallspace;
    g2.draw(new Rectangle.Double(x, y, legendwidth, legendheight));

    for (int index=0; index < granularityLevels.length; index++) {
      x = legendleftoffset + smallspace;
      y += fontheight - samplecolorsize;
      g2.setColor(barColors[Integer.parseInt(granularityLevels[index])].getMaxColor());
      g2.fill(new Rectangle.Double(x, y, samplecolorsize, samplecolorsize));
      g2.setColor(barColors[Integer.parseInt(granularityLevels[index])].getMinColor());
      x += samplecolorsize+smallspace;
      g2.fill(new Rectangle.Double(x, y, samplecolorsize, samplecolorsize));
      g2.setColor(Color.black);
      y += samplecolorsize;
      g2.drawString(legendStrings[index], (int) (x+samplecolorsize+smallspace), (int) y);
      y += fontheight - samplecolorsize;
      g2.drawString(legendTimeRanges[index], (int) x, (int) y);
    }
  }

  public String[] sortLongArray(String[] stringArray) {
    for (int i=0; i<stringArray.length; i++) {
      for (int j=stringArray.length-1; j>i; j--) {
        if (Long.parseLong(stringArray[j-1]) > Long.parseLong(stringArray[j])) {
          String tmpStr = stringArray[j-1];
          stringArray[j-1] = stringArray[j];
          stringArray[j] = tmpStr;
        }
      }
    }
    return stringArray;
  }

  public String[] sortStringArray(String[] stringArray) {
    for (int i=0; i<stringArray.length; i++) {
      for (int j=stringArray.length-1; j>i; j--) {
        if (stringArray[j-1].compareTo(stringArray[j]) > 0) {
          String tmpStr = stringArray[j-1];
          stringArray[j-1] = stringArray[j];
          stringArray[j] = tmpStr;
        }
      }
    }
    return stringArray;
  }

  public String[] getLegendStrings(String[] granString) {
    String[] legend = new String[granString.length];
    granString = sortLongArray(granString);
    for (int i=0; i<granString.length; i++) {
      if (Long.parseLong(granString[i]) >= 86400000L) {
        legend[i] = "Day";
      }
      else if (Long.parseLong(granString[i]) >= 3600000L) {
        legend[i] = "Hour";
      }
      else if (Long.parseLong(granString[i]) >= 900000L) {
        legend[i] = "Quarter";
      }
      else if (Long.parseLong(granString[i]) >= 60000L) {
        legend[i] = "Minute";
      }
    }
    return (legend);
  }

  public String[] getLegendTimeRanges(MutableTable mTable) {
    legendTimeRanges = new String[legendStrings.length];
    int[] sort = {set.l, set.z};
    int[] indices = TableUtilities.multiSortIndex(mutable, sort);
    MutableTable sortedTable = (MutableTable) mTable.reorderRows(indices);
    int level = 0;
    String str = sortedTable.getString(0, set.z);  // string to show start time to end time
    for (int row=1; row<sortedTable.getNumRows(); row++) {
      if (sortedTable.getInt(row, set.l) == level) {
        // update the end time
        str = str.substring(0,9) + sortedTable.getString(row, set.z).substring(9,17);
      }
      else if (sortedTable.getInt(row, set.l) != level) {
        legendTimeRanges[level] = str;
        level++;
        str = sortedTable.getString(row, set.z);
      }
    }
    // reach the end
    legendTimeRanges[level] = str;
    return legendTimeRanges;
  }


  public int[] getRunSizes(MutableTable mTable) {
    runSizes = new int[legendStrings.length];
    int[] sort = {set.l, set.z};
    int[] indices = TableUtilities.multiSortIndex(mutable, sort);
    MutableTable sortedTable = (MutableTable) mTable.reorderRows(indices);
    int level = 0;
    String str = sortedTable.getString(0, set.z);  // string to show start time to end time
    int cnt = 1;
    for (int row=1; row<sortedTable.getNumRows(); row++) {
      if (sortedTable.getInt(row, set.l)==level && !sortedTable.getString(row, set.z).equals(str)) {
        cnt++;
        str = sortedTable.getString(row, set.z);
      }
      else if (sortedTable.getInt(row, set.l) != level) {
        runSizes[level] = cnt;
        level++;
        str = sortedTable.getString(row, set.z);
        cnt = 1;
      }
    }
    // reach the end
    runSizes[level] = cnt;

    return runSizes;
  }

  public void drawDataSet(Graphics2D g2, DataSet set) {
    double x = leftoffset;
    double barwidth = xoffsetincrement;

    // Map bins to runs
    int counter = 0;
    int offset = 0;

    barBoundary = new Rectangle2D.Double[bins];
    tipValues = new String[bins];
    int granLevel = 0;
    int countOnLevel = 0;

    //printTable(mutable);
    for (int bin=0; bin < bins; bin++) {

      if (counter == runSizeTotal) {
        rectangle = new Rectangle2D.Double(x, 0, barwidth, 0);
        barBoundary[bin] = rectangle;
        tipValues[bin] = " ";
        counter = 0;
        offset++;
      }

      else {
        double value = mutable.getDouble(bin-offset, 1);
        double barheight = (value-yminimum)/yscale;
        double y = getGraphHeight()-bottomoffset-barheight;
        StringBuffer tip = new StringBuffer("");

        rectangle = new Rectangle2D.Double(x, y, barwidth, barheight);
        barBoundary[bin] = rectangle;
        tip.append("<html>");
        tip.append(" " + mutable.getColumnLabel(0).toLowerCase() + ": " + mutable.getString(bin-offset, 0));
        tip.append("<br>");
        tip.append(" " + mutable.getColumnLabel(2).toLowerCase() + ": " + mutable.getString(bin-offset, 2));
        tip.append("<br>");
        tip.append(" frequency: " + value);
        tip.append("</html>");
        tipValues[bin] = tip.toString();

        int colorIndex = (int) (mutable.getDouble(bin-offset, 4)); // get the granularity level
        GradientColorSet barColor = barColors[colorIndex];
        if (colorIndex == granLevel) {
          countOnLevel += 1;
        }
        else { // go to a new granularity level
          granLevel = colorIndex;
          countOnLevel = 1;
        }
        double gradient = ((double)((countOnLevel-1) % (runSizes[colorIndex])))/(double)(runSizes[colorIndex]) ;
        double gradientR = barColor.getSpanR()* gradient;
        double gradientG = barColor.getSpanG()* gradient;
        double gradientB = barColor.getSpanB()* gradient;
        g2.setColor(barColor.getNextColor(gradientR, gradientG, gradientB));
        g2.fill(rectangle);
        g2.setColor(Color.black);
        g2.draw(rectangle);

        counter++;
      }

      x += xoffsetincrement;
    }
  }

  protected void printTable(MutableTable tbl) {
  System.out.println("in timeClusterBarChart print table");
  for (int i=0; i<tbl.getNumEntries(); i++) {
    for (int j=0; j<tbl.getNumColumns(); j++) {
      System.out.print(tbl.getObject(i,j)+", ");
    }
    System.out.println(" ");
  }
}


}