package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import javax.swing.*;

/**
   A simple BarChart.  The data is stored in a Table.
   The x value of the DataSet is the index of the column with the
   labels, while the y value must be the index of the column with
   frequencies.
*/
public class BarChart extends Chart {

   // Minimum and maximum scale values
   double xminimum, xmaximum;
   double yminimum, ymaximum;

   // Units per increment
   double xvalueincrement, yvalueincrement;

   // Pixels per increment
   double xoffsetincrement, yoffsetincrement;
   double minimumxoffsetincrement = 5;
   double minimumyoffsetincrement = 5;

   double minimumgraphwidth;
   double minimumgraphheight;

   // Units per pixel
   double xscale, yscale;

   // Minimum and maximum data values
   double xdataminimum, xdatamaximum;
   double ydataminimum, ydatamaximum;

   int tickmarksize = 4;

   public BarChart(Table table, DataSet set, GraphSettings settings) {
     super(table, set, settings);

      setBackground(Color.white);

      title = settings.title;
      xlabel = settings.xaxis;
      ylabel = settings.yaxis;

      // Find interval for y data
      if ((settings.yminimum == null) || (settings.ymaximum == null)) {
         double[] mm = getMinAndMax(table, set.y);
         yminimum = mm[0] - .25 * mm[0];
         if (yminimum < 0)
            yminimum = 0;
         ymaximum = mm[1] + .25 * mm[1];
      }
      else {
         yminimum = settings.yminimum.doubleValue();
         ymaximum = settings.ymaximum.doubleValue();
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

      // Scale intelligently
      minAndMax[1] = maxScale(minAndMax[1]);

      return minAndMax;
   }

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
        drawLegend(g2); // Draw legend after drawing scale
      drawDataSet(g2, set);
   }

   public void initOffsets() {
     if (!settings.displaylegend) {
       legendheight = 0;
       legendwidth = 0;
       rightoffset = .05*graphwidth;
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

       legendleftoffset = graphwidth-legendwidth-legendspace;
       legendtopoffset = graphheight/2-legendheight/2;

       rightoffset = legendwidth+2*legendspace;
     }

     leftoffset = .05*graphwidth+point_size+longest_font_width_y;
     bottomoffset = .1*graphheight+point_size+longest_font_width_x;
     topoffset = .1*graphheight+point_size;
   }

   // Resize scale
   public void resize() {
      gridsize = settings.gridsize;

      yvalueincrement = (ymaximum-yminimum)/gridsize;
      yscale = (ymaximum-yminimum)/(graphheight-topoffset-bottomoffset);

      // x axis
      xoffsetincrement = (graphwidth-leftoffset-rightoffset)/bins;

      // y axis
      yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
      while ((yoffsetincrement < minimumyoffsetincrement) && (gridsize > 0)) {
         gridsize = gridsize/2;
         yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
      }
   }

   public void drawLegend(Graphics2D g2) {
   }

   public void drawDataSet(Graphics2D g2, DataSet set) {
      double x = leftoffset;
      double barwidth = xoffsetincrement;

      for (int index=0; index < bins; index++) {
         double value = table.getDouble(index, set.y);
         double y = graphheight-bottomoffset-(value-yminimum)/yscale;
         double barheight = (value-yminimum)/yscale;

         g2.setColor(new Color((int)(255 - 255*index/bins), 51, (int)(255*index/bins)));
         g2.fill(new Rectangle2D.Double(x, y, barwidth, barheight));
         g2.setColor(Color.black);
         g2.draw(new Rectangle2D.Double(x, y, barwidth, barheight));
         x += xoffsetincrement;
      }
   }

   // Draw data point
   public void drawPoint(Graphics2D g2, Color color, double xvalue,
      double yvalue) {
      Color previouscolor = g2.getColor();

      double x = (xvalue-xminimum)/xscale+leftoffset;
      double y = graphheight-bottomoffset-(yvalue-yminimum)/yscale;

      g2.setColor(color);
      g2.fill(new Rectangle2D.Double(x, y, 4, 4));

      g2.setColor(previouscolor);
   }

   public void drawAxis(Graphics2D g2) {
      g2.draw(new Line2D.Double(leftoffset,topoffset,
         leftoffset, graphheight-bottomoffset));
      g2.draw(new Line2D.Double(leftoffset,graphheight-bottomoffset,
         graphwidth-rightoffset, graphheight-bottomoffset));
   }

   public void drawScale(Graphics2D g2) {
      NumberFormat numberformat = NumberFormat.getInstance();
      numberformat.setMaximumFractionDigits(3);

      // x axis
      double x = leftoffset+(xoffsetincrement/2);
      double lastx = 0;
      AffineTransform transform = g2.getTransform();
      g2.rotate(Math.toRadians(-90));
      int ascent = metrics.getAscent();
      for (int index=0; index < bins; index++) {
         if (x - ascent/2 + 3 > lastx) {
            String bin = (String) table.getString(index, set.x);

            int stringwidth = metrics.stringWidth(bin);
            if (stringwidth > longest_font_width_x) {
              longest_font_width_x = stringwidth;
              repaint();
            }

            g2.drawString(bin,
                          (int) -(graphheight-bottomoffset+stringwidth+tickmarksize+3),
                          (int) (x+ascent/2));
            lastx = x + ascent/2;
         }
         x += xoffsetincrement;
      }
      g2.setTransform(transform);

      // y axis
      double yvalue =  yminimum;
      double y = graphheight-bottomoffset;
      for (int index=0; index < gridsize; index++) {
         String string = numberformat.format(yvalue);

         int stringwidth = metrics.stringWidth(string);
         if (stringwidth > longest_font_width_y) {
           longest_font_width_y = stringwidth;
           repaint();
         }

         g2.drawString(string, (int) (leftoffset-stringwidth-smallspace),
                       (int) (y+fontascent/2));

         y -= yoffsetincrement;
         yvalue += yvalueincrement;
      }
   }

   public void drawTitle(Graphics2D g2) {
      int stringwidth = metrics.stringWidth(title);
      double x = (graphwidth-stringwidth)/2;
      double y = (topoffset)/2 + fontheight/2;

      g2.drawString(title, (int) x, (int) y);
   }

   public void drawAxisLabels(Graphics2D g2) {
      int stringwidth;
      double xvalue, yvalue;

      // x axis
      stringwidth = metrics.stringWidth(xlabel);
      xvalue = (graphwidth-stringwidth)/2;
      yvalue = graphheight-(bottomoffset+fontheight)/2+(2*largespace);
      g2.drawString(xlabel, (int) xvalue, (int) yvalue);

      // y axis
      AffineTransform transform = g2.getTransform();

      stringwidth = metrics.stringWidth(ylabel);
      xvalue = (leftoffset-fontascent-smallspace)/2;
      yvalue = (graphheight+stringwidth)/2;
      AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(-90), xvalue, yvalue);
      g2.transform(rotate);
      g2.drawString(ylabel, (int) xvalue, (int) yvalue);

      g2.setTransform(transform);
   }

   public void drawTickMarks(Graphics2D g2) {
      // x axis
      double x = leftoffset+(xoffsetincrement/2);
      for (int index=0; index < bins; index++) {
         g2.draw(new Line2D.Double(x, graphheight-bottomoffset-tickmarksize, x, graphheight-bottomoffset+tickmarksize));
         x += xoffsetincrement;
      }

      // y axis
      double y = topoffset+yoffsetincrement;
      for (int index=0; index < gridsize; index++) {
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
         g2.draw(new Line2D.Double(leftoffset, y, graphwidth-rightoffset, y));
         y += yoffsetincrement;
      }

      g2.setColor(previouscolor);
   }

   /**
    * Scale a double up, for graph max -- 14 to 15, 19 to 20, 99 to 100, etc.
    * @param d
    * @return
    */
   protected double maxScale(double d) {
     if (d <= 0) {
       // do nothing
     }
     else if (d < 1) {
       // do nothing
     }
     else {
       double magnitude = 1;
       double danger = Double.MAX_VALUE / 100;

       while (magnitude < danger) {
         if (d < magnitude * 10) { // matches
           double addend = magnitude;
           while (addend < d) {
             addend += magnitude;
           }

           if (d < addend - magnitude / 2)
             d = addend - magnitude / 2;
           else
             d = addend;

           break;
         }
         magnitude = magnitude * 10;
       }
     }
     return d;
   }
}