package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.util.*;

public class ClusterBarChart extends BarChart {
  private static final int LEFTOFFSET = 20;
  private static final int RIGHTOFFSET = 20;
  private static final int TOPOFFSET = 20;
  private static final int BOTTOMOFFSET = 20;

  // Clustering
  private int runs = 1;
  private int runsize;

  // Unique cluster values
  private HashSet valueset;

  // Data
  private MutableTable mutable;

  public ClusterBarChart(Table table, DataSet set, GraphSettings settings, int xincrement, int yincrement) throws Exception {
    super(table, set, settings);

    settings.displayaxislabels = false;
    gridsize = settings.gridsize;

    minimumxoffsetincrement = xincrement;
    minimumyoffsetincrement = yincrement;

    yvalueincrement = (ymaximum-yminimum)/gridsize;

    // Dependencies
    if (table.getNumRows() == 0)
      throw new Exception("Table exception");

    if (!table.isColumnNominal(set.z))
      throw new Exception("Nominal exception");

    mutable = table.toExampleTable();

    // Runs
    int[] sort = {set.x, set.z};
    int[] indices = TableUtilities.multiSortIndex(mutable, sort);
    mutable = (mutable.reorderRows(indices)).toExampleTable();

    // Missing values
    valueset = TableUtilities.uniqueValueSet(mutable, set.z);
    runsize = valueset.size();

    // Determine number of runs
    String label = mutable.getString(0, set.x);
    HashSet runset = new HashSet(valueset);
    int rows = mutable.getNumRows();
    for (int row=0; row < rows; row++) {
      String runlabel = mutable.getString(row, set.x);
      String runtime = mutable.getString(row, set.z);

      if (!runlabel.equals(label)) {
        // Missing values
        Iterator iterator = runset.iterator();
        while (iterator.hasNext()) {
          String[] values = new String[3];
          values[set.x] = label;
          values[set.y] = new String("0.0");
          values[set.z] = (String) iterator.next();
          mutable.addRow(values);
        }

        runs++;
        label = runlabel;

        runset = new HashSet(valueset);
      }
      runset.remove(runtime);
    }

    indices = TableUtilities.multiSortIndex(mutable, sort);
    mutable = (mutable.reorderRows(indices)).toExampleTable();

    // Include bins for spacing runs
    // Impacts mapping of bins to table values
    bins = (runsize+1)*runs;
  }

  public void initOffsets() {
    NumberFormat numberformat = NumberFormat.getInstance();
    numberformat.setMaximumFractionDigits(3);

    // Determine maximum string widths
    // X axis
    for (int run=0; run < runs; run++) {
      String value = mutable.getString(run*runsize, set.x);

      int stringwidth = metrics.stringWidth(value);
      if (stringwidth > longest_font_width_x)
        longest_font_width_x = stringwidth;
    }

    // Y axis
    double yvalue =  yminimum;
    for (int index=0; index < gridsize; index++) {
      String value = numberformat.format(yvalue);

      int stringwidth = metrics.stringWidth(value);
      if (stringwidth > longest_font_width_y)
        longest_font_width_y = stringwidth;

      yvalue += yvalueincrement;
    }

    // Determine offsets
    if (!settings.displaylegend) {
      legendheight = 0;
      legendwidth = 0;
    }
    else {
      String[] values = TableUtilities.uniqueValues(table, set.z);
      legendwidth = metrics.stringWidth(values[0]);
      for (int index=1; index < values.length; index++) {
        int stringwidth = metrics.stringWidth(values[index]);
        if (stringwidth > legendwidth)
          legendwidth = stringwidth;
      }

      legendwidth += 4*smallspace+samplecolorsize;
      legendheight = (values.length*fontheight)+(fontheight-samplecolorsize);
    }

    // Primary offsets
    leftoffset = LEFTOFFSET+point_size+longest_font_width_y;
    rightoffset = RIGHTOFFSET+legendwidth+2*legendspace;
    bottomoffset = BOTTOMOFFSET+point_size+longest_font_width_x;
    topoffset = TOPOFFSET+point_size;

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

    /*
    while ((yoffsetincrement < minimumyoffsetincrement) && (gridsize > 0)) {
      gridsize = gridsize/2;
      yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
    }
    */
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
    int multiplier = 1;
    int offset = 0;

    for (int bin=0; bin < bins; bin++) {

      if (bin != runsize*multiplier+offset)
        g2.draw(new Line2D.Double(x, graphheight-bottomoffset-tickmarksize, x, graphheight-bottomoffset+tickmarksize));

      else {
        multiplier++;
        offset++;
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

    double xincrement = (runsize+1)*xoffsetincrement;

    double x = leftoffset + (xincrement/2);

    AffineTransform transform = g2.getTransform();
    g2.rotate(Math.toRadians(90));

    for (int run=0; run < runs; run++) {
      String value = mutable.getString(run*runsize, set.x);
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

  public void drawLegend(Graphics2D g2) {
    Color previouscolor = g2.getColor();

    double x = legendleftoffset;
    double y = legendtopoffset;

    g2.draw(new Rectangle.Double(x, y, legendwidth, legendheight));

    x += smallspace;
    y += fontheight-samplecolorsize;

    String[] values = new String[runsize];
    table.getSubset(0, runsize).getColumn(values, set.z);
    for (int index=0; index < values.length; index++) {
      double gradient = (double) ((double) index)/((double) runsize);
      g2.setColor(new Color((int)(255 - 255*gradient), 25, (int)(255*gradient)));
      g2.fill(new Rectangle.Double(x, y, samplecolorsize, samplecolorsize));
      y += fontheight;
    }

    g2.setColor(previouscolor);

    x = legendleftoffset;
    y = legendtopoffset;

    x += 2*smallspace+samplecolorsize;
    y += fontheight;

    for (int index=0; index < values.length; index++) {
      String value = values[index];
      g2.drawString(value, (int) x, (int) y);
      y += fontheight;
    }
  }

  public void drawDataSet(Graphics2D g2, DataSet set) {
    double x = leftoffset;
    double barwidth = xoffsetincrement;

    // Map bins to runs
    int multiplier = 1;
    int offset = 0;

    for (int bin=0; bin < bins; bin++) {

      if (bin == runsize*multiplier+offset) {
        multiplier++;
        offset++;
      }

      else {
        double value = mutable.getDouble(bin-offset, set.y);
        double barheight = (value-yminimum)/yscale;
        double y = getGraphHeight()-bottomoffset-barheight;

        double gradient = (double)((bin-offset) % runsize)/(double)runsize;
        g2.setColor(new Color((int)(255 - 255*gradient), 25, (int)(255*gradient)));
        g2.fill(new Rectangle2D.Double(x, y, barwidth, barheight));
        g2.setColor(Color.black);
        g2.draw(new Rectangle2D.Double(x, y, barwidth, barheight));
      }

      x += xoffsetincrement;
    }
  }
}