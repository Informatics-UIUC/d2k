package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import javax.swing.*;

import ncsa.d2k.modules.core.prediction.decisiontree.*;

/**
 * This interface defines the methods implemented by the various "views" of
 * nodes in the decision tree visualization.
 */
public interface View {

   /**
    * Set the data for this component.
    *
    * @param model The decision tree model
    * @param node  decision tree node
    */
  public void setData(ViewableDTModel model, ViewableDTNode node);

   /**
    * Draw this node to the specified graphics context.
    *
    * @param g2 graphics context
    */
  public void drawView(Graphics2D g2);

   /**
    * Get the width for this component.
    *
    * @return width
    */
  public double getWidth();

    /**
     * Get the height for this component.
     * @return height
     */
  public double getHeight();

   /**
    * When the mouse brushes over this node, draw the total and percentages of
    * each class for this node.
    *
    * @param g2 graphics context
    */
  public void drawBrush(Graphics2D g2);

   /**
    * Get the width of the brushable area that contains bar chart
    *
    * @return width of brushable area
    */
  public double getBrushWidth();

   /**
    * Get the height of the brushable area that contains bar chart
    *
    * @return height of brushable area
    */
  public double getBrushHeight();

   /**
    * Get expanded conponent.  This component shows the contents of the node
    * in more detail.
    *
    * @return expanded component
    */    
  public JComponent expand();
}