// package ncsa.d2k.modules.projects.berkin.plot;
package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;

import javax.swing.*;
import java.awt.*;
import java.text.*;
import java.util.*;

public class BoxPlotPane extends JPanel {

   // Data
   Table table;
   int column;

   // Constants
   double BORDER = 25;
   double BUFFER = 5;

   // Scale constants
   double SCALETICKWIDTH = 5;
   double DIVISIONS = 10;
   double SCALEBUFFER = 20;

   // Plot constants
   double PLOTTICKWIDTH = 20;
   double BOXWIDTH = 20;
   double PLOTBUFFER = 200;

   // Sizes and offsets
   double width, height;
   double scaleoffset;
   double plotoffset;
   double minimumoffset;
   double yminimum, ymaximum;
   double yfirst, ythird, ymedian;

   // Data and statistics
   double minimum, maximum;
   double fquartile, median, tquartile;

   // Scale
   double sminimum, smaximum;
   double ratio;

   NumberFormat nformat;
   FontMetrics metrics;

   public BoxPlotPane() {
   }

   public BoxPlotPane(Table table, int column) {
      this.table = table;
      this.column = column;

      calculate();
   }

   public void calculate() {
      ScalarStatistics statistics = TableUtilities.getScalarStatistics(table, column);

      minimum = statistics.getMinimum();
      maximum = statistics.getMaximum();
      median = statistics.getMedian();
      fquartile = statistics.getFirstQuartile();
      tquartile = statistics.getThirdQuartile();

      sminimum = minimum;
      smaximum = maximum;
   }

   public void paintComponent(Graphics graphic) {
      super.paintComponent(graphic);

      width = getWidth();
      height = getHeight();
      ratio = (height-2*BORDER)/(smaximum-sminimum);
      minimumoffset = sminimum*ratio;

      nformat = NumberFormat.getInstance();
      nformat.setMaximumFractionDigits(1);
      nformat.setMinimumFractionDigits(1);
      metrics = graphic.getFontMetrics();

      drawScale(graphic);
      drawPlot(graphic);
   }

   public void drawScale(Graphics graphic) {
      double x, y;
      double sincrement = (smaximum-sminimum)/DIVISIONS;

      String svalue = nformat.format(smaximum);
      scaleoffset = BORDER + metrics.stringWidth(svalue) + BUFFER + SCALETICKWIDTH;

      x = scaleoffset;
      y = BORDER;
      graphic.drawLine((int) x, (int) y, (int) x, (int) (height-BORDER));

      double value = smaximum;
      for (int index = 0; index <= DIVISIONS; index++) {
         svalue = nformat.format(value);
         graphic.drawLine((int) x, (int) y, (int) (x-SCALETICKWIDTH), (int) y);
         graphic.drawString(svalue, (int) (x-SCALETICKWIDTH-BUFFER-metrics.stringWidth(svalue)), (int) (y+(metrics.getAscent()/2)));

         y += sincrement*ratio;
         value -= sincrement;
      }
   }

   public void drawPlot(Graphics graphic) {
      double x, y;

      x = scaleoffset + SCALEBUFFER;

      // Draw minimum
      yminimum = height-BORDER-(minimum*ratio)+minimumoffset;
      graphic.drawLine((int) x, (int) yminimum, (int) (x+PLOTTICKWIDTH), (int) yminimum);

      // Draw maximum
      ymaximum = height-BORDER-(maximum*ratio)+minimumoffset;
      graphic.drawLine((int) x, (int) ymaximum, (int) (x+PLOTTICKWIDTH), (int) ymaximum);

      // Draw median
      ymedian = height-BORDER-(median*ratio)+minimumoffset;
      graphic.drawLine((int) x, (int) ymedian, (int) (x+PLOTTICKWIDTH), (int) ymedian);

      // Draw interange quartile
      yfirst = height-BORDER-(fquartile*ratio)+minimumoffset;
      ythird = height-BORDER-(tquartile*ratio)+minimumoffset;

      graphic.drawRect((int) x, (int) ythird, (int) BOXWIDTH, (int) (yfirst-ythird));

      // Draw lines
      graphic.setColor(Color.black);
      x += PLOTTICKWIDTH/2;
      graphic.drawLine((int) x, (int) yminimum, (int) x, (int) yfirst);
      graphic.drawLine((int) x, (int) ythird, (int) x, (int) ymaximum);

      // Draw statistics
      double ascent = metrics.getAscent();

      x += PLOTBUFFER;
      y = BORDER+ascent;

      graphic.drawString("Minimum: " + nformat.format(minimum), (int) x, (int) y);
      y += ascent;
      graphic.drawString("Maximum: " + nformat.format(maximum), (int) x, (int) y);
      y += ascent;
      graphic.drawString("First Quartile: " + nformat.format(fquartile), (int) x, (int) y);
      y += ascent;
      graphic.drawString("Median: " + nformat.format(median), (int) x, (int) y);
      y += ascent;
      graphic.drawString("Third Quartile: " + nformat.format(tquartile), (int) x, (int) y);
      y += 2*ascent;
   }

}
