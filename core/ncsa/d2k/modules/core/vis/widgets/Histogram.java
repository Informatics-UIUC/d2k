package ncsa.d2k.modules.core.vis.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.text.NumberFormat;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import ncsa.d2k.modules.core.datatype.table.basic.NumericColumn;
import ncsa.d2k.modules.core.datatype.table.basic.TableImpl;
import ncsa.gui.Constrain;

/**
 * A histogram panel.
 *
 * @author gpape (from clutter)
 */

public class Histogram extends JPanel {

   private static final int
      HISTOGRAM_MIN = 101,
      HISTOGRAM_MAX = 103;
   public static final int
      HISTOGRAM_UNIFORM  = 101,
      HISTOGRAM_RANGE    = 102,
      HISTOGRAM_INTERVAL = 103;

   private boolean initialized = false;
   private int behavior;
   private int[] counts;
   private double[] heights;

   private TableImpl table;
   private NumericColumn current;
   private String parameter;

   private HashMap columnLookup;
   private JComboBox columnSelect;

   private JTextField
      n, mean, median, stddev, variance;

   private SelectionPanel selection;
   private HistogramPanel histogram;
   private SliderPanel slider;
   private VisualPanel visual;
   private StatisticsPanel statistics;

   public Histogram(TableImpl table, int behavior, String parameter)
      throws IllegalArgumentException {

      if (behavior < HISTOGRAM_MIN || behavior > HISTOGRAM_MAX)
         throw new IllegalArgumentException("Invalid histogram behavior.");

      this.table = table;
      this.behavior = behavior;
      this.parameter = parameter.substring(0);

      columnLookup = new HashMap();
      columnSelect = new JComboBox();

      boolean found_numeric = false;
      for (int i = 0; i < table.getNumColumns(); i++) {
         if (table.getColumn(i) instanceof NumericColumn) {
            if (!found_numeric) {
               current = (NumericColumn)table.getColumn(i);
               found_numeric = true;
            }
            columnLookup.put(table.getColumnLabel(i), new Integer(i));
            columnSelect.addItem(table.getColumnLabel(i));
         }
      }

      histogram = new HistogramPanel();  // order is important here!
      slider = new SliderPanel();
      selection = new SelectionPanel();
      visual = new VisualPanel();
      statistics = new StatisticsPanel();
      columnSelect.addActionListener(new SelectionListener());
      calculateBins();
      statistics.updateStatistics();

      this.setLayout(new BorderLayout());
      this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
         visual, statistics));

      initialized = true;

   }

   private void calculateBins() {

      int index;
      double max, min;

      switch(behavior) {

         case HISTOGRAM_UNIFORM:

            counts = new int[slider.getValue()];
            for (int i = 0; i < counts.length; i++)
               counts[i] = 0;
            heights = new double[counts.length];

            max = Double.MIN_VALUE; min = Double.MAX_VALUE;

            for (int i = 0; i < current.getNumRows(); i++) {
               if (current.getDouble(i) < min)
                  min = current.getDouble(i);
               if (current.getDouble(i) > max)
                  max = current.getDouble(i);
            }

            double increment = (max - min) / (double)counts.length, ceiling;

            for (int i = 0; i < current.getNumRows(); i++) {
               index = (int)((current.getDouble(i) - min)/increment);
               if (index == counts.length) index--;
               counts[index]++;
            }

            for (int i = 0; i < heights.length; i++)
               heights[i] = (double)counts[i] / (double)current.getNumRows();

            break;

         case HISTOGRAM_RANGE:

            StringTokenizer strTok = new StringTokenizer(parameter, ",");
            double[] binMaxes = new double[strTok.countTokens()];

            int idx = 0;
            try {
               while(strTok.hasMoreElements()) {
                  String s = (String)strTok.nextElement();
                  binMaxes[idx++] = Double.parseDouble(s);
               }
            }
            catch(NumberFormatException e) { return; }

            Arrays.sort(binMaxes);

            counts = new int[binMaxes.length + 1];
            heights = new double[counts.length];

            for (int i = 0; i < counts.length; i++)
               counts[i] = 0;

            // some redundancy here
            boolean found;
            for (int i = 0; i < current.getNumRows(); i++) {
               found = false;
               for (int j = 0; j < binMaxes.length; j++) {
                  if (current.getDouble(i) <= binMaxes[j] && !found) {
                     counts[j]++;
                     found = true;
                     break;
                  }
               }
               if (!found)
                  counts[binMaxes.length]++;
            }

            for (int i = 0; i < heights.length; i++)
               heights[i] = (double)counts[i] / (double)current.getNumRows();

            break;

         case HISTOGRAM_INTERVAL:

            max = Double.MIN_VALUE; min = Double.MAX_VALUE;

            for (int i = 0; i < current.getNumRows(); i++) {
               if (current.getDouble(i) < min)
                  min = current.getDouble(i);
               if (current.getDouble(i) > max)
                  max = current.getDouble(i);
            }

            double interval = ((double)slider.getValue()/100.0)*(double)(max - min);

            counts = new int[(int)Math.ceil(100.0/(double)slider.getValue())];
            heights = new double[counts.length];

            for (int i = 0; i < counts.length; i++)
               counts[i] = 0;

            for (int i = 0; i < current.getNumRows(); i++) {
               index = (int)((current.getDouble(i) - min)/interval);
               if (index == counts.length) index--;
               counts[index]++;
            }

            for (int i = 0; i < heights.length; i++)
               heights[i] = (double)counts[i] / (double)current.getNumRows();

            break;

      }

   }

   private class VisualPanel extends JPanel {

      public VisualPanel() {

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, selection, 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(this, histogram, 0, 1, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

         if (behavior == HISTOGRAM_UNIFORM || behavior == HISTOGRAM_INTERVAL)
            Constrain.setConstraints(this, slider, 0, 2, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, 0);

      }

      public Dimension getPreferredSize() {
         return new Dimension(400, 400);
      }

   }

   private class SelectionPanel extends JPanel {

      public SelectionPanel() {

         this.setBorder(new TitledBorder(" Select attribute: "));
         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, columnSelect, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

      }

   }

   private class HistogramPanel extends JPanel {

      boolean calculated;
      int width, height, offset_x, offset_y, hist_x, hist_y;

      Graphics2D g2;

      public HistogramPanel() {
         calculated = false;
      }

      public void setBounds(int x, int y, int w, int h) {
         super.setBounds(x, y, w, h);
         width = w;
         height = h;
         offset_x = (int)(.1*width);
         offset_y = (int)(.1*height);
         hist_x = (int)(.8*width);
         hist_y = (int)(.8*height);
         calculated = true;
      }

      protected void paintComponent(Graphics g) {

         if (!calculated || !initialized)
            return;

         double increment = (double)hist_x/(double)heights.length;

         g2 = (Graphics2D)g;
         g2.setColor(new Color(235, 235, 235));
         g2.fillRect(0, 0, width, height);

         double xloc = (double)offset_x;
         for (int i = 0; i < heights.length; i++) {

            g2.setColor(new Color((int)(255 - 255*i/heights.length), 51, (int)(255*i/heights.length)));
            g2.fillRect(
               (int)xloc,
               (int)(offset_y + (1-heights[i])*hist_y),
               (int)(xloc + increment) - (int)(xloc), // diminishes rounding errors
               (offset_y + hist_y) - (int)(offset_y + (1-heights[i])*hist_y));
            g2.setColor(new Color(0, 0, 0));
            g2.drawRect(
               (int)xloc,
               (int)(offset_y + (1-heights[i])*hist_y),
               (int)(xloc + increment) - (int)(xloc),
               (offset_y + hist_y) - (int)(offset_y + (1-heights[i])*hist_y));

            xloc += increment;

         }

      }

   }

   private class SliderPanel extends JPanel {

      private JSlider slide;

      public SliderPanel() {

         int val = 5;
         double dval;
         switch(behavior) {

            case HISTOGRAM_UNIFORM:

               this.setBorder(new TitledBorder(" Number of bins: "));
               try { val = Integer.parseInt(parameter); }
               catch(NumberFormatException e) { val = 5; break; }
               break;

            case HISTOGRAM_INTERVAL:

               double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
               for (int i = 0; i < current.getNumRows(); i++) {
                  if (current.getDouble(i) < min)
                     min = current.getDouble(i);
                  if (current.getDouble(i) > max)
                     max = current.getDouble(i);
               }

               this.setBorder(new TitledBorder(" Bin interval: "));
               try { dval = Double.parseDouble(parameter); }
               catch(NumberFormatException e) { val = 50; break; }

               if (dval < 0 || dval > (double)(max - min))
                  val = 100;
               else
                  val = (int)(100.0 * (dval/(double)(max - min)));

               break;

         }

         if (behavior == HISTOGRAM_INTERVAL)
            slide = new JSlider(1, 100, val);
         else
            slide = new JSlider(1, val*2, val);
         slide.addChangeListener(new SliderListener());

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, slide, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

      }

      public int getValue() {
         return slide.getValue();
      }

   }

   private class StatisticsPanel extends JPanel {

      private NumberFormat N;

      public StatisticsPanel() {

         n = new JTextField(10);
         n.setEditable(false);
         mean = new JTextField(10);
         mean.setEditable(false);
         median = new JTextField(10);
         median.setEditable(false);
         stddev = new JTextField(10);
         stddev.setEditable(false);
         variance = new JTextField(10);
         variance.setEditable(false);

         N = NumberFormat.getInstance();
         N.setMaximumFractionDigits(4);

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, new JLabel("N:"), 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, n, 1, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, new JLabel("Mean:"), 0, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, mean, 1, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, new JLabel("Median:"), 0, 2, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, median, 1, 2, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, new JLabel("Std. Dev:"), 0, 3, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, stddev, 1, 3, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, new JLabel("Variance:"), 0, 4, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, variance, 1, 4, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, new JLabel(""), 1, 5, 2, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

      }

      public void updateStatistics() {

         n.setText(N.format(current.getNumRows()));

         double sample_mean, sample_variance;
         double[] d = new double[current.getNumRows()];

         double total = 0, max = Double.MIN_VALUE, min = Double.MAX_VALUE;
         for (int i = 0; i < d.length; i++) {

            d[i] = current.getDouble(i);
            total += d[i];

            if (current.getDouble(i) > max)
               max = current.getDouble(i);
            if (current.getDouble(i) < min)
               min = current.getDouble(i);

         }

         sample_mean = total/(double)current.getNumRows();
         mean.setText(N.format(sample_mean));

         Arrays.sort(d);
         median.setText(N.format((d[(int)Math.ceil(d.length/2.0)] + d[(int)Math.floor(d.length/2.0)])/2.0));

         total = 0; // for calculating sample variance
         for (int i = 0; i < d.length; i++)
            total += (d[i] - sample_mean) * (d[i] - sample_mean);

         sample_variance = total / (double)(d.length - 1); // unbiased estimator

         variance.setText(N.format(sample_variance));
         stddev.setText(N.format(Math.sqrt(sample_variance)));

      }

      public Dimension getPreferredSize() {
         return new Dimension(200, 400);
      }

   }

   private class SelectionListener extends AbstractAction {
      public void actionPerformed(ActionEvent e) {
         current = (NumericColumn)table.getColumn(((Integer)
            columnLookup.get(columnSelect.getSelectedItem())).intValue());
         calculateBins();
         statistics.updateStatistics();
         histogram.repaint();
      }
   }

   private class SliderListener implements ChangeListener {
      public void stateChanged(ChangeEvent e) {
         calculateBins();
         histogram.repaint();
      }
   }

}