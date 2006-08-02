/* 
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 * 
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 * 
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.prediction.decisiontree.ScalarViewableDTNode;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTModel;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTNode;
import ncsa.d2k.modules.core.prediction.mean.continuous.MeanOutputModel;
import ncsa.d2k.modules.core.prediction.regression.continuous
          .StepwiseLinearModel;

   import javax.swing.*;

   import java.awt.*;
   import java.awt.geom.Line2D;
   import java.awt.geom.Rectangle2D;
   import java.text.NumberFormat;


/**
 * Draws a small bar chart for the predicted, scalar outputs of a model. Each
 * bar is scaled based on the minimum and maximum values from the example set.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ScalarView implements View {

   //~ Static fields/initializers **********************************************

   /** Description of field graphics. */
   static JFrame graphics;

   //~ Instance fields *********************************************************

   /** Description of field barwidth. */
   double barwidth = 5;

   /** Description of field brushwidth. */
   double brushwidth;
   double brushheight;

   /** Description of field error. */
   double error;

   /** Description of field featurewidth. */
   double featurewidth;

   /** Description of field inset. */
   double inset = 5;

   /** Description of field maximumvalues. */
   double[] maximumvalues;

   /** Description of field meanvalues. */
   double[] meanvalues;

   /** Description of field minimumvalues. */
   double[] minimumvalues;

   /** Description of field numberformat. */
   NumberFormat numberformat;

   /** Description of field ranges. */
   double[] ranges;

   /** Description of field reduction. */
   double reduction;

   /** Description of field samplesize. */
   double samplesize = 10;

   /** Description of field samplespace. */
   double samplespace = 8;

   /** Description of field scalarmodel. */
   Model scalarmodel;

   /** Description of field scalarnode. */
   ScalarViewableDTNode scalarnode;

   /** Description of field scheme. */
   DecisionTreeScheme scheme;

   /** Description of field width. */
   double width;
   double height;

   //~ Constructors ************************************************************

   /**
    * Creates a new ScalarView object.
    */
   public ScalarView() { }

   //~ Methods *****************************************************************

   /**
    * Description of method drawBrush.
    *
    * @param g2 Description of parameter g2.
    */
   public void drawBrush(Graphics2D g2) {
      double x;
      double y;
      double fontheight = g2.getFontMetrics().getHeight();

      y = 0;
      scheme.setIndex(0);

      for (int index = 0; index < meanvalues.length; index++) {
         x = 0;

         if (samplesize < fontheight) {
            y += fontheight - samplesize;
         }

         g2.setColor(scheme.getNextColor());
         g2.fill(new Rectangle2D.Double(x, y, samplesize, samplesize));

         x += samplesize + samplespace;

         y += samplesize;

         g2.setColor(scheme.textcolor);

         String value =
            scalarmodel.getOutputFeatureName(index) + " = " +
            numberformat.format(meanvalues[index]);
         g2.drawString(value, (int) x, (int) y);

         y += samplespace;
      }

      x = 0;
      y += fontheight;

      g2.drawString("Error " + numberformat.format(error), (int) x, (int) y);
      y += fontheight;
      g2.drawString("Best Error Reduction " + numberformat.format(reduction),
                    (int) x, (int) y);
   } // end method drawBrush

   /**
    * Description of method drawView.
    *
    * @param g2 Description of parameter g2.
    */
   public void drawView(Graphics2D g2) {
      double x;

      // Background
      g2.setColor(scheme.viewbackgroundcolor);
      g2.fill(new Rectangle2D.Double(0, 0, width, height));

      // Bars
      x = inset;
      scheme.setIndex(0);

      for (int index = 0; index < ranges.length; index++) {
         double barheight =
            meanvalues[index] * (height - inset * 2) / ranges[index];

         g2.setColor(scheme.getNextColor());
         g2.fill(new Rectangle2D.Double(x, height - inset - barheight, barwidth,
                                        barheight));

         x += barwidth;
      }
   }

   /**
    * Description of method expand.
    *
    * @return Description of return value.
    */
   public JComponent expand() {

      if (scalarmodel instanceof MeanOutputModel) {
         return new ScalarExpanded();
      } else if (scalarmodel instanceof StepwiseLinearModel) {
         return null;
      } else {
         return null;
      }
   }

   /**
    * Description of method getBrushHeight.
    *
    * @return Description of return value.
    */
   public double getBrushHeight() {

      if (brushheight == 0) {
         FontMetrics metrics = graphics.getGraphics().getFontMetrics();

         double fontheight = metrics.getHeight();

         if (samplesize < fontheight) {
            brushheight =
               (meanvalues.length) * (metrics.getHeight() + samplespace) +
               2 * metrics.getHeight();
         } else {
            brushheight =
               (meanvalues.length) * (samplesize + samplespace) +
               2 * metrics.getHeight();
         }
      }

      return brushheight;
   }

   /**
    * Description of method getBrushWidth.
    *
    * @return Description of return value.
    */
   public double getBrushWidth() {

      if (brushwidth == 0) {
         FontMetrics metrics = graphics.getGraphics().getFontMetrics();

         String[] features = scalarmodel.getOutputFeatureNames();

         featurewidth = 0;

         double valuewidth = 0;
         double stringwidth = 0;

         for (int index = 0; index < features.length; index++) {
            stringwidth = metrics.stringWidth(features[index] + " = ");

            if (stringwidth > featurewidth) {
               featurewidth = stringwidth;
            }

            stringwidth =
               metrics.stringWidth(numberformat.format(meanvalues[index]));

            if (stringwidth > valuewidth) {
               valuewidth = stringwidth;
            }
         }

         double errorwidth =
            metrics.stringWidth("Best Error Reduction " +
                                numberformat.format(reduction));
         double datawidth =
            samplesize + samplespace + featurewidth + valuewidth;

         if (datawidth > errorwidth) {
            brushwidth = datawidth;
         } else {
            brushwidth = errorwidth;
         }
      } // end if

      return brushwidth;
   } // end method getBrushWidth

   /**
    * Description of method getHeight.
    *
    * @return Description of return value.
    */
   public double getHeight() { return height; }

   /**
    * Description of method getWidth.
    *
    * @return Description of return value.
    */
   public double getWidth() { return width; }

   /**
    * Description of method setData.
    *
    * @param model Description of parameter model.
    * @param node  Description of parameter node.
    */
   public void setData(ViewableDTModel model, ViewableDTNode node) {
      scalarnode = (ScalarViewableDTNode) node;

      scalarmodel = scalarnode.getModel();

      if (scalarmodel instanceof MeanOutputModel) {
         MeanOutputModel meanmodel = (MeanOutputModel) scalarmodel;

         minimumvalues = scalarnode.getMinimumValues();
         maximumvalues = scalarnode.getMaximumValues();
         error = scalarnode.getError();
         reduction = scalarnode.getErrorReduction();
         meanvalues = meanmodel.Evaluate(new double[0]);
      } else if (scalarmodel instanceof StepwiseLinearModel) { }
      else { }

      ranges = new double[minimumvalues.length];

      for (int index = 0; index < minimumvalues.length; index++) {
         double minimum = minimumvalues[index];
         double maximum = maximumvalues[index];
         double mean = meanvalues[index];

         ranges[index] = maximum - minimum;
      }

      scheme = new DecisionTreeScheme(minimumvalues.length);

      height = 50;
      width = inset * 2 + barwidth * minimumvalues.length;

      numberformat = NumberFormat.getInstance();
      numberformat.setMaximumFractionDigits(3);
      numberformat.setMinimumFractionDigits(3);

      if (graphics == null) {
         graphics = new JFrame();
         graphics.addNotify();
         graphics.setFont(DecisionTreeScheme.textfont);
      }
   } // end method setData

   //~ Inner Classes ***********************************************************

   class ScalarExpanded extends JPanel {

      static private final String SPLIT = "Split: ";
      static private final String LEAF = "Leaf: ";
      double barspace = 5;

      double barwidth = 60;
      double bottom = 15;
      double databottom = 10;
      double dataleft = 10;
      double dataright = 10;
      double datatop = 10;
      double datawidth;
      double dataheight;
      double graphbottom = 30;
      double graphleft = 30;
      double graphright = 30;
      double graphspace = 15;
      double graphspacing = 15;
      double graphtop = 30;
      double graphwidth;
      double graphheight;
      double gridheight = 300;

      double gridsize = 10;
      float gridstroke = .1f;
      double gridwidth;
      int largeascent;
      int smallascent;

      FontMetrics largemetrics;
      FontMetrics smallmetrics;

      double largetick = 10;
      double left = 15;
      String[] path;
      double pathbottom = 10;
      int pathindex;
      double pathleading = 2;
      double pathleft = 10;
      double pathright = 15;
      double pathtop = 6;
      double pathwidth;
      double pathheight;
      double right = 15;

      double samplesize = 10;
      double samplespace = 8;
      double smalltick = 4;
      double tickspace = 8;
      double top = 15;
      double valuespace = 5;

      double[] valuewidths;

      double xdata;
      double ydata;

      double xgraph;
      double ygraph;

      double xlabel;
      double ylabel;

      double xpath;
      double ypath;
      double ylabelspace = 15;
      double ypathspace = 15;

      ScalarExpanded() {
         int depth = scalarnode.getDepth();
         path = new String[depth];

         if (path.length > 0) {
            pathindex = depth - 1;
            findPath(scalarnode);
         }

         largemetrics = getFontMetrics(scheme.expandedfont);
         largeascent = largemetrics.getAscent();

         smallmetrics = getFontMetrics(scheme.textfont);
         smallascent = smallmetrics.getAscent();

         setBackground(scheme.expandedbackgroundcolor);
      }

      void drawData(Graphics2D g2) {
         double x;
         double y;

         // Background
         g2.setColor(scheme.expandedborderbackgroundcolor);
         g2.fill(new Rectangle2D.Double(xdata, ydata, datawidth, dataheight));

         double fontheight = g2.getFontMetrics().getHeight();

         scheme.setIndex(0);
         y = ydata + datatop;

         for (int index = 0; index < meanvalues.length; index++) {
            x = xdata + dataleft;

            if (samplesize < fontheight) {
               y += fontheight - samplesize;
            }

            g2.setColor(scheme.getNextColor());
            g2.fill(new Rectangle2D.Double(x, y, samplesize, samplesize));

            x += samplesize + samplespace;

            y += samplesize;

            g2.setColor(scheme.textcolor);

            String value =
               scalarmodel.getOutputFeatureName(index) + " = " +
               numberformat.format(meanvalues[index]);
            g2.drawString(value, (int) x, (int) y);

            y += samplespace;
         }

         x = xdata + dataleft;
         y += fontheight;

         g2.drawString("Error " + numberformat.format(error), (int) x, (int) y);
         y += fontheight;
         g2.drawString("Best Error Reduction " + numberformat.format(reduction),
                       (int) x, (int) y);
      } // end method drawData

      void drawGraph(Graphics2D g2) {
         scheme.setIndex(0);

         // Background
         g2.setColor(scheme.expandedborderbackgroundcolor);
         g2.fill(new Rectangle2D.Double(xgraph, ygraph, graphwidth,
                                        graphheight));

         double x = xgraph + graphleft;
         double y = ygraph + graphheight - graphbottom;

         for (int index = 0; index < meanvalues.length; index++) {
            double initial = x;
            double valueincrement = ranges[index] / gridsize;
            double gridincrement = (gridheight - smallascent) / ranges[index];
            double value = minimumvalues[index];

            for (int increment = 0; increment <= gridsize; increment++) {

               // Value
               g2.setColor(scheme.expandedfontcolor);
               g2.drawString(numberformat.format(value), (int) x, (int) y);

               // Tickmark
               x += valuewidths[index] + valuespace;
               y -= smallascent / 2;
               g2.setColor(scheme.expandedgraphgridcolor);
               g2.setStroke(new BasicStroke(gridstroke));
               g2.draw(new Line2D.Double(x, y, x + largetick, y));

               x = initial;
               y -= gridincrement * valueincrement - smallascent / 2;
               value += valueincrement;
            }

            // Axis
            x = initial + valuewidths[index] + valuespace + largetick;
            y = ygraph + graphheight - graphbottom;
            g2.draw(new Line2D.Double(x, y, x, y - gridheight));
            g2.draw(new Line2D.Double(x, y, x + 2 * barspace + barwidth, y));

            // Bar
            double barheight = meanvalues[index] * gridincrement;
            g2.setColor(scheme.getNextColor());
            g2.fill(new Rectangle2D.Double(x + barspace, y - barheight,
                                           barwidth, barheight));

            // Label
            String feature = scalarmodel.getOutputFeatureName(index);
            double featurenamewidth = smallmetrics.stringWidth(feature);
            g2.setColor(scheme.textcolor);
            g2.drawString(feature,
                          (int) (x + barspace +
                                 (barwidth - featurenamewidth) / 2),
                          (int) (y + smallascent));

            x += 2 * barspace + barwidth + graphspacing;
         } // end for
      } // end method drawGraph

      void drawLabel(Graphics2D g2) {
         StringBuffer label;

         if (scalarnode.getNumChildren() != 0) {
            label = new StringBuffer(SPLIT);
         } else {
            label = new StringBuffer(LEAF);
         }

         label.append(scalarnode.getLabel());

         g2.setFont(scheme.expandedfont);
         g2.setColor(scheme.expandedfontcolor);
         g2.drawString(label.toString(), (int) xlabel, (int) ylabel);
      }

      void drawLabelPath(Graphics2D g2) {
         g2.setFont(scheme.textfont);

         // Background
         g2.setColor(scheme.expandedborderbackgroundcolor);
         g2.fill(new Rectangle2D.Double(xpath, ypath, pathwidth, pathheight));

         // Path
         double y = ypath + pathtop + smallascent;
         double x = pathleft + xpath;
         g2.setColor(scheme.textcolor);

         for (int index = 0; index < path.length; index++) {
            g2.drawString(path[index], (int) x, (int) y);
            y += smallascent + pathleading;
         }
      }

      void findPath(ViewableDTNode node) {
         ViewableDTNode parent = node.getViewableParent();

         if (parent == null) {
            return;
         }

         for (int index = 0; index < parent.getNumChildren(); index++) {
            ViewableDTNode child = parent.getViewableChild(index);

            if (child == node) {
               path[pathindex] = parent.getBranchLabel(index);
               pathindex--;
            }
         }

         findPath(parent);
      }

      public Dimension getMinimumSize() { return getPreferredSize(); }

      public Dimension getPreferredSize() {
         xlabel = left;
         ylabel = top + largeascent;

         xpath = xlabel;
         ypath = ylabel + ylabelspace;

         StringBuffer label;

         if (scalarnode.getNumChildren() != 0) {
            label = new StringBuffer(SPLIT);
         } else {
            label = new StringBuffer(LEAF);
         }

         label.append(scalarnode.getLabel());

         pathwidth = largemetrics.stringWidth(new String(label));

         for (int index = 0; index < path.length; index++) {
            int smallwidth = smallmetrics.stringWidth(path[index]);

            if (smallwidth > pathwidth) {
               pathwidth = smallwidth;
            }
         }

         if (path.length > 0) {
            pathwidth += pathleft + pathright;
            pathheight =
               pathtop + path.length * smallascent +
               (path.length - 1) * pathleading + pathbottom;
         }

         xdata = xpath;
         ydata = ypath + pathheight + ypathspace;

         datawidth = dataleft + getBrushWidth() + dataright;
         dataheight = datatop + getBrushHeight() + databottom;

         if (pathwidth > datawidth) {
            datawidth = pathwidth;
         } else {
            pathwidth = datawidth;
         }

         ygraph = top;
         xgraph = xpath + pathwidth + graphspace;

         graphheight = graphtop + gridheight + graphbottom;

         if (featurewidth > barwidth) {
            barwidth = featurewidth;
         }

         graphwidth = 0;
         valuewidths = new double[maximumvalues.length];

         for (int index = 0; index < maximumvalues.length; index++) {
            valuewidths[index] =
               smallmetrics.stringWidth(numberformat.format(maximumvalues[index]));
            graphwidth +=
               valuewidths[index] + valuespace + largetick + 2 * barspace +
               barwidth + graphspacing;
         }

         graphwidth += graphleft + graphright;

         double width = left + pathwidth + graphspace + graphwidth + right;
         double height;

         double firstheight = ydata + dataheight + bottom;
         double secondheight = top + graphheight + bottom;

         if (firstheight > secondheight) {
            height = firstheight;
         } else {
            height = secondheight;
         }

         return new Dimension((int) width, (int) height);
      } // end method getPreferredSize

      public void paintComponent(Graphics g) {
         super.paintComponent(g);

         Graphics2D g2 = (Graphics2D) g;
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

         drawLabel(g2);
         drawLabelPath(g2);
         drawData(g2);
         drawGraph(g2);
      }
   } // end class ScalarExpanded
} // end class ScalarView
