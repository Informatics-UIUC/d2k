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

import ncsa.d2k.modules.core.prediction.decisiontree.NominalViewableDTModel;
import ncsa.d2k.modules.core.prediction.decisiontree.NominalViewableDTNode;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTModel;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTNode;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;


/**
 * Description of class NominalView.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class NominalView implements View {

   //~ Static fields/initializers **********************************************

   /** constant for greater than. */
   static private final String GREATER_THAN = ">";

   /** constant for less than. */
   static private final String LESS_THAN = "<";

   /** constant for greater than or equal to. */
   static private final String GREATER_THAN_EQUAL_TO = ">=";

   /** constant for less than or equal to. */
   static private final String LESS_THAN_EQUAL_TO = "<=";

   /** constant for not equal to. */
   static private final String NOT_EQUAL_TO = "!=";

   /** constant for equal to. */
   static private final String EQUAL_TO = "==";

   /** graphics. */
   static JFrame graphics;

   //~ Instance fields *********************************************************

   /** space between bars */
   double barspace = 5;

   /** width of bars. */
   double barwidth = 16;

   /** branch label. */
   String branchlabel;

   /** height. */
   double height = 45;

   /** left inset. */
   double leftinset = 5;

   /** Decision tree model. */
   NominalViewableDTModel model;

   /** node. */
   NominalViewableDTNode node;

   /** number format. */
   NumberFormat numberformat;

   /** unique outputs. */
   String[] outputs;

   /** output space. */
   double outputspace = 10;

   /** output width. */
   double outputwidth = 80;

   /** percent width. */
   double percentwidth;

   /** right inset. */
   double rightinset = 5;

   /** sample size. */
   double samplesize = 10;

   /** sample space. */
   double samplespace = 8;

   /** scale size. */
   double scalesize = 100;

   /** scheme defines the colors used. */
   DecisionTreeScheme scheme;

   /** search space. */
   double searchspace = 4;

   /** tallies. */
   int[] tallies;

   /** space between tally  */
   double tallyspace = 10;

   /** with of tally area. */
   double tallywidth;

   /** height of tally area. */
   double theight;

   /** tickmark. */
   double tickmark = 3;

   /** tside. */
   double tside = 8;

   /** tspace. */
   double tspace = 10;

   /** values. */
   double[] values;

   /** width. */
   double width;

   /** xincrement. */
   double xincrement;
    /** yincrement. */
   double yincrement;

   /** ygrid. */
   double ygrid = 5;

   /** yscale. */
   double yscale;

   //~ Constructors ************************************************************

   /**
    * Creates a new NominalView object.
    */
   public NominalView() {
      numberformat = NumberFormat.getInstance();
      numberformat.setMaximumFractionDigits(5);

      if (graphics == null) {
         graphics = new JFrame();
         graphics.addNotify();
         graphics.setFont(DecisionTreeScheme.textfont);
      }
   }

   //~ Methods *****************************************************************

   /**
    * Description of method drawBrush.
    *
    * @param g2 Description of parameter g2.
    */
   public void drawBrush(Graphics2D g2) {
      FontMetrics metrics = graphics.getGraphics().getFontMetrics();
      double fontheight = metrics.getHeight();

      double x = 0;
      double y = 0;

      scheme.setIndex(0);

      for (int index = 0; index < outputs.length; index++) {
         x = 0;

         if (samplesize < fontheight) {
            y += fontheight - samplesize;
         }

         g2.setColor(scheme.getNextColor());
         g2.fill(new Rectangle2D.Double(x, y, samplesize, samplesize));

         x += samplesize + samplespace;
         y += samplesize;

         g2.setColor(scheme.textcolor);
         g2.drawString(outputs[index], (int) x, (int) y);

         x += outputwidth + outputspace;

         g2.drawString(Integer.toString(tallies[index]), (int) x, (int) y);

         x += tallywidth + tallyspace;

         g2.drawString(numberformat.format(values[index]) + "%", (int) x,
                       (int) y);

         y += samplespace;
      } // end for
   } // end method drawBrush

   /**
    * Description of method drawView.
    *
    * @param g2 Description of parameter g2.
    */
   public void drawView(Graphics2D g2) {
      double x1;
      double y1;

      // Background
      g2.setColor(scheme.viewbackgroundcolor);
      g2.fill(new Rectangle2D.Double(0, 0, width, height));

      // Tickmarks
      g2.setColor(scheme.viewtickcolor);
      g2.setStroke(new BasicStroke(1));
      x1 = leftinset;
      y1 = yincrement;

      for (int index = 0; index < ygrid; index++) {
         g2.draw(new Line2D.Double(x1, y1, x1 + tickmark, y1));
         y1 += yincrement;
      }

      // Bars
      x1 = leftinset + tickmark + barspace;
      scheme.setIndex(0);

      for (int index = 0; index < values.length; index++) {
         double barheight = yscale * values[index];
         y1 = 1 + height - yincrement - barheight;
         g2.setColor(scheme.getNextColor());
         g2.fill(new Rectangle2D.Double(x1, y1, barwidth, barheight));
         x1 += barwidth + barspace;
      }
   } // end method drawView

   /**
    * get expanded conponent
    *
    * @return expanded component
    */
   public JComponent expand() { return new NominalExpanded(); }

   /**
    * compute the values, which corresponds to the height of the bars.
    */
   public void findValues() {
      outputs = model.getUniqueOutputValues();

      values = new double[outputs.length];
      tallies = new int[outputs.length];

      for (int index = 0; index < values.length; index++) {

         try {
            tallies[index] = node.getOutputTally(outputs[index]);
            values[index] =
               100 * (double) tallies[index] / (double) node.getTotal();
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   /**
    * get the height of the brushable area that contains bar chart
    *
    * @return height of brushable area
    */
   public double getBrushHeight() {
      FontMetrics metrics = graphics.getGraphics().getFontMetrics();
      double fontheight = metrics.getHeight();

      double size;

      if (samplesize > fontheight) {
         size = samplesize;
      } else {
         size = fontheight;
      }

      return (size + samplespace) * outputs.length;
   }

   /**
    * get the width of the brushable area that contains bar chart
    *
    * @return width of brushable area
    */
   public double getBrushWidth() {
      FontMetrics metrics = graphics.getGraphics().getFontMetrics();
      double fontheight = metrics.getHeight();

      for (int index = 0; index < outputs.length; index++) {
         double stringwidth = metrics.stringWidth(outputs[index]);

         if (stringwidth > outputwidth) {
            outputwidth = stringwidth;
         }

         stringwidth =
            metrics.stringWidth(numberformat.format(values[index]) + "%");

         if (stringwidth > percentwidth) {
            percentwidth = stringwidth;
         }

         stringwidth = metrics.stringWidth(Integer.toString(tallies[index]));

         if (stringwidth > tallywidth) {
            tallywidth = stringwidth;
         }
      }

      return samplesize + samplespace + outputwidth + outputspace + tallywidth +
             tallyspace + percentwidth;
   } // end method getBrushWidth

   /**
    * get the height of this component
    *
    * @return height
    */
   public double getHeight() { return height; }

   /**
    * get the width of this component
    *
    * @return width
    */
   public double getWidth() { return width; }

   /**
    * Set the data for this component.
    *
    * @param model The decision tree model
    * @param node  decision tree node
    */
   public void setData(ViewableDTModel model, ViewableDTNode node) {
      this.model = (NominalViewableDTModel) model;
      this.node = (NominalViewableDTNode) node;

      findValues();

      scheme = new DecisionTreeScheme(outputs.length);

      width =
         leftinset + tickmark + (barwidth + barspace) * values.length +
         rightinset;
      yincrement = height / (ygrid + 1);
      yscale = (height - 2 * yincrement) / scalesize;
   }

   //~ Inner Classes ***********************************************************

   class NominalExpanded extends JPanel {

      static private final String SPLIT = "Split: ";
      static private final String LEAF = "Leaf: ";
      double axisspace = 4;
      double barspace = 20;
      double barwidth = 80;
      double bottom = 15;
      double databottom = 10;
      double dataleft = 10;
      double dataright = 10;
      int datasize;
      double datatop = 10;
      double datawidth;
      double dataheight;
      double dpercentwidth;
      double graphbottom = 30;
      double graphleft = 30;
      double graphright = 30;
      double graphtop = 30;
      double graphwidth;
      double graphheight;
      double gridheight = 300;

      float gridstroke = .1f;
      double gridwidth;
      int largeascent;
      int smallascent;

      FontMetrics largemetrics;
      FontMetrics smallmetrics;
      double largetick = 10;
      double left = 15;

      NumberFormat numberformat;

      String[] outputs;
      double outputspace = 10;
      double outputwidth = 80;
      String[] path;
      double pathbottom = 10;
      int pathindex;
      double pathleading = 2;
      double pathleft = 10;
      double pathright = 15;
      double pathtop = 6;
      double pathwidth;
      double pathheight;
      double percentspace = 8;
      double percentwidth;
      double right = 15;

      double samplesize = 10;
      double samplespace = 8;

      DecisionTreeScheme scheme;
      double smalltick = 4;
      int[] tallies;
      double tallyspace = 10;

      double tallywidth;
      double tickspace = 8;
      double top = 15;
      double[] values;

      double xdata;
      double ydata;

      double xgraph;
      double ygraph;
      double xgraphspace = 15;

      double xlabel;
      double ylabel;

      double xpath;
      double ypath;
      double ylabelspace = 15;
      double ypathspace = 15;

      NominalExpanded() {
         outputs = model.getUniqueOutputValues();
         values = new double[outputs.length];
         tallies = new int[outputs.length];

         for (int index = 0; index < outputs.length; index++) {

            try {

               if (node.getTotal() == 0) {
                  values[index] = 0;
               } else {
                  values[index] =
                     100 * (double) node.getOutputTally(outputs[index]) /
                        (double) node.getTotal();
               }

               tallies[index] = node.getOutputTally(outputs[index]);
            } catch (Exception exception) {
               System.out.println("Exception from getOutputTally");
            }
         }

         datasize = values.length;

         int depth = node.getDepth();
         path = new String[depth];

         if (path.length > 0) {
            pathindex = depth - 1;
            findPath(node);
         }

         scheme = new DecisionTreeScheme();

         largemetrics = getFontMetrics(scheme.expandedfont);
         largeascent = largemetrics.getAscent();

         numberformat = NumberFormat.getInstance();
         numberformat.setMaximumFractionDigits(1);

         smallmetrics = getFontMetrics(scheme.textfont);
         smallascent = smallmetrics.getAscent();
         dpercentwidth = smallmetrics.stringWidth("100.0%");
         percentwidth = smallmetrics.stringWidth("100");

         for (int index = 0; index < tallies.length; index++) {
            double width =
               smallmetrics.stringWidth(Integer.toString(tallies[index]));

            if (width > tallywidth) {
               tallywidth = width;
            }
         }

         setBackground(scheme.expandedbackgroundcolor);
      }

      void drawData(Graphics2D g2) {

         // Background
         g2.setColor(scheme.expandedborderbackgroundcolor);
         g2.fill(new Rectangle2D.Double(xdata, ydata, datawidth, dataheight));

         // Data
         double x = xdata + dataleft;
         double y = ydata + datatop;

         g2.setFont(scheme.textfont);

         for (int index = 0; index < datasize; index++) {
            Color color = scheme.getNextColor();
            g2.setColor(color);
            g2.fill(new Rectangle2D.Double(x, y, samplesize, samplesize));

            x += samplesize + samplespace;
            y += samplesize;
            g2.setColor(scheme.textcolor);
            g2.drawString(outputs[index], (int) x, (int) y);

            x += outputwidth + outputspace;

            String tally = Integer.toString(tallies[index]);
            g2.drawString(tally, (int) x, (int) y);

            x += tallywidth + tallyspace;

            String value = numberformat.format(values[index]) + "%";
            g2.drawString(value, (int) x, (int) y);

            x = xdata + dataleft;
            y += samplespace;
         }
      } // end method drawData

      void drawGraph(Graphics2D g2) {

         // Background
         g2.setColor(scheme.expandedborderbackgroundcolor);
         g2.fill(new Rectangle2D.Double(xgraph, ygraph, graphwidth,
                                        graphheight));

         // Grid
         g2.setColor(scheme.expandedgraphgridcolor);
         g2.setFont(scheme.textfont);

         double yincrement = gridheight / 10;
         double x = xgraph + graphleft;
         double y = ygraph + graphheight - graphbottom;
         int val = 0;

         for (int index = 0; index <= 10; index++) {
            Integer integer = new Integer(val);
            String svalue = integer.toString();
            g2.drawString(svalue, (int) x, (int) y);

            g2.setStroke(new BasicStroke(gridstroke));
            x += percentwidth + percentspace;
            g2.draw(new Line2D.Double(x, y, x + largetick, y));
            x += largetick + tickspace;
            g2.draw(new Line2D.Double(x, y, x + gridwidth, y));

            x = xgraph + graphleft;
            y -= yincrement;
            val += 10;
         }

         // Small grid
         x =
            xgraph + graphleft + percentwidth + percentspace + largetick -
            smalltick;
         yincrement = gridheight / 20;
         y = ygraph + graphheight - graphbottom - yincrement;

         for (int index = 0; index < 10; index++) {
            g2.draw(new Line2D.Double(x, y, x + smalltick, y));
            x += smalltick + tickspace;
            g2.draw(new Line2D.Double(x, y, x + gridwidth, y));

            x =
               xgraph + graphleft + percentwidth + percentspace + largetick -
               smalltick;
            y -= 2 * yincrement;
         }

         // Bars
         x =
            xgraph + graphleft + percentwidth + percentspace + largetick +
            tickspace + barspace;

         double yscale = gridheight / 100;

         for (int index = 0; index < values.length; index++) {
            double barheight = yscale * values[index];
            y = ygraph + graphheight - graphbottom - barheight;
            g2.setColor(scheme.getNextColor());
            g2.fill(new Rectangle2D.Double(x, y, barwidth, barheight));
            x += barspace + barwidth;
         }

         x =
            xgraph + graphleft + percentwidth + percentspace + largetick +
            tickspace + barspace + barwidth / 2;
         y = ygraph + graphheight - graphbottom + smallascent + axisspace;
         g2.setColor(scheme.textcolor);

         for (int index = 0; index < outputs.length; index++) {
            String output = outputs[index];
            int outputwidth = smallmetrics.stringWidth(output);
            g2.drawString(output, (int) (x - outputwidth / 2), (int) y);
            x += barspace + barwidth;
         }
      } // end method drawGraph

      void drawLabel(Graphics2D g2) {
         StringBuffer label;

         if (node.getNumChildren() != 0) {
            label = new StringBuffer(SPLIT);
         } else {
            label = new StringBuffer(LEAF);
         }

         label.append(node.getLabel());

         g2.setFont(scheme.expandedfont);
         g2.setColor(scheme.expandedfontcolor);
         g2.drawString(label.toString(), (int) xlabel, (int) ylabel);
      }

      void drawLabelPath(Graphics2D g2) {

         // Background
         g2.setColor(scheme.expandedborderbackgroundcolor);
         g2.fill(new Rectangle2D.Double(xpath, ypath, pathwidth, pathheight));

         // Path
         double y = ypath + pathtop + smallascent;
         double x = pathleft + xpath;

         g2.setColor(scheme.textcolor);
         g2.setFont(scheme.textfont);

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

         // Label bounds
         xlabel = left;
         ylabel = top + largeascent;

         // Path bounds
         xpath = xlabel;
         ypath = ylabel + ylabelspace;

         StringBuffer sb = new StringBuffer();

         if (node.getNumChildren() != 0) {
            sb.append(SPLIT);
         } else {
            sb.append(LEAF);
         }

         sb.append(node.getLabel());
         pathwidth = largemetrics.stringWidth(sb.toString());

         for (int index = 0; index < path.length; index++) {
            int twidth = smallmetrics.stringWidth(path[index]);

            if (twidth > pathwidth) {
               pathwidth = twidth;
            }
         }

         if (path.length > 0) {
            pathwidth += pathleft + pathright;
            pathheight =
               pathtop + path.length * smallascent +
               (path.length - 1) * pathleading + pathbottom;
         }

         // Data bounds
         xdata = xpath;
         ydata = ypath + pathheight + ypathspace;

         datawidth =
            dataleft + samplesize + samplespace + outputwidth +
            outputspace +
            tallywidth + tallyspace + dpercentwidth + dataright;
         dataheight =
            datatop + datasize * samplesize +
            (datasize - 1) * samplespace +
            databottom;

         if (pathwidth > datawidth) {
            datawidth = pathwidth;
         } else {
            pathwidth = datawidth;
         }

         // Graph bounds
         ygraph = top;
         xgraph = xpath + pathwidth + xgraphspace;

         graphheight = graphtop + gridheight + graphbottom;

         gridwidth = barwidth * datasize + barspace * (datasize + 1);
         graphwidth =
            graphleft + percentwidth + percentspace + largetick + tickspace +
            gridwidth + graphright;

         double width = left + pathwidth + xgraphspace + graphwidth + right;

         double pdheight = ydata + dataheight + bottom;
         double gheight = top + graphheight + bottom;
         double height;

         if (pdheight > gheight) {
            height = pdheight;
         } else {
            height = gheight;
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
   } // end class NominalExpanded
} // end class NominalView
