package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class ClusterBarChart extends BarChart {

   private int distinctTimeStamps;

   public ClusterBarChart(Table table, DataSet set, GraphSettings settings) {

      super(table, set, settings);

      settings.displayaxislabels = false;

      double currentTime = Double.NEGATIVE_INFINITY;
      for (int row = 0; row < table.getNumRows(); row++) {
         if (table.getDouble(row, set.z) < currentTime) {
            break;
         }
         else {
            currentTime = table.getDouble(row, set.z);
            distinctTimeStamps++;
         }
      }

      bins = 0;
      for (int row = 0; row < table.getNumRows(); row++) {
         bins++;
         if (row > 0 && (row + 1) % distinctTimeStamps == 0 && row != table.getNumRows() - 1)
            bins++;
      }

   }

   public void drawScale(Graphics2D g2) {
      NumberFormat numberformat = NumberFormat.getInstance();
      numberformat.setMaximumFractionDigits(3);

      // x axis
      // TextualColumn textcolumn;
      double x = leftoffset+(xoffsetincrement/2);
      double lastx = 0;
      AffineTransform transform = g2.getTransform(); //
      g2.rotate(Math.toRadians(-90));
      int ascent = metrics.getAscent();

      int binOffset = 0;
      boolean nextRunToggle = false;

      for (int row=0; row < bins; row++) {

         int index = row - binOffset;

         if (!nextRunToggle && row > 0 && (index) % distinctTimeStamps == 0) {
            binOffset++;
            nextRunToggle = true;
         }
         else if (x - ascent/2 + 3 > lastx) {
            String bin = (String)table.getString(index, set.x);
            String time = (String)table.getString(index, set.z);
            int stringwidth = metrics.stringWidth(bin + ", " + time);
            g2.drawString(bin + ", " + time,
               (int)-(graphheight-bottomoffset+stringwidth+tickmarksize+3),
               (int)(x+ascent/2));
            lastx = x + ascent/2;
            nextRunToggle = false;
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
         g2.drawString(string, (int) (leftoffset-stringwidth-smallspace),
            (int) (y+fontascent/2));
         y -= yoffsetincrement;
         yvalue += yvalueincrement;
      }
   }

   public void drawDataSet(Graphics2D g2, DataSet set) {

      double x = leftoffset;
      double barwidth = xoffsetincrement;

      int binOffset = 0;
      boolean nextRunToggle = false;

      for (int row=0; row < bins; row++) {

         int index = row - binOffset;

         double value = table.getDouble(index, set.y);
         double y = graphheight-bottomoffset-(value-yminimum)/yscale;
         double barheight = (value-yminimum)/yscale;

         if (!nextRunToggle && row > 0 && (index) % distinctTimeStamps == 0) {
            binOffset++;
            x += xoffsetincrement;
            nextRunToggle = true;
         }
         else {
            double gradient = (double)(index % distinctTimeStamps)/(double)distinctTimeStamps;

            // g2.setColor(new Color(102, 153, 255));
            g2.setColor(new Color((int)(255 - 255*gradient), 51, (int)(255*gradient)));
            g2.fill(new Rectangle2D.Double(x, y, barwidth, barheight));
            g2.setColor(Color.black);
            g2.draw(new Rectangle2D.Double(x, y, barwidth, barheight));
            x += xoffsetincrement;
            nextRunToggle = false;
         }

      }

   }

}
