package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class TableFitnessPlot extends FitnessPlot {
  protected Table table;
  protected DefaultTableModel tableModel;

  public TableFitnessPlot() {
    super();
    tableModel = new DefaultTableModel();
    tableModel.setRowCount(0);
  }

  public void setTable(Table t) {
    if(table != t) {
      // intialize the selected table model
      tableModel.setColumnCount(t.getNumColumns());
      Object[] colLabels = new Object[t.getNumColumns()];
      for(int i = 0; i < colLabels.length; i++) {
        colLabels[i] = t.getColumnLabel(i);
      }
      tableModel.setColumnIdentifiers(colLabels);
      table = t;
    }
    changed = true;
  }

  protected void drawPoints(Graphics2D g, float xscale, float yscale) {
    if(table != null) {
      synchronized(table) {
        g.setColor(Color.red);
        int numMembers = table.getNumRows();
        for (int index = 0; index < numMembers; index++) {
          float xvalue = table.getFloat(index, xObjective);
          float yvalue = table.getFloat(index, yObjective);

          float x = (xvalue - xMin) / xscale + left;
          float y = graphheight - bottom - (yvalue - yMin) / yscale;

          g.fill(new Rectangle2D.Float(x, y, 2, 2));
        }

        if (selected != null) {
          g.setColor(Color.blue);
          for (int index = 0; index < numMembers; index++) {
            if (selected[index]) {

              float xvalue = table.getFloat(index, xObjective);
              float yvalue = table.getFloat(index, yObjective);

              float x = (xvalue - xMin) / xscale + left;
              float y = graphheight - bottom - (yvalue - yMin) / yscale;

              g.fill(new Rectangle2D.Float(x, y, 2, 2));
            }
          }
        }
        changed = false;
      }
    }
  }

  public TableModel getSelected() {
    return tableModel;
  }

  protected void addSelection(int i) {
    super.addSelection(i);

    int numCols = table.getNumColumns();
    // copy the entries into an Object[] and add it to the table model
    Object[] row = new Object[numCols];
    for(int idx = 0; idx < numCols; idx++) {
      row[idx] = new Double(table.getDouble(i, idx));
    }
    tableModel.addRow(row);
  }

  protected void removeAllSelections() {
    super.removeAllSelections();
    tableModel.setRowCount(0);
  }

  public void setObjectives(int x, int y) {
    if(table == null)
      return;

    xObjective = x;
    yObjective = y;

    // reset min/max for both objectives by looping through pop
    xMin = yMin = Float.POSITIVE_INFINITY;
    xMax = yMax = Float.NEGATIVE_INFINITY;

    int numMembers = table.getNumRows();
      for (int i = 0; i < numMembers; i++) {
        float xVal = table.getFloat(i, xObjective);
        if (xVal > xMax)
          xMax = xVal;
        if (xVal < xMin)
          xMin = xVal;
        float yVal = table.getFloat(i, yObjective);
        if (yVal > yMax)
          yMax = yVal;
        if (yVal < yMin)
          yMin = yVal;
    }

    changed = true;
  }

  protected double getXValue(int i) {
    return table.getDouble(i, xObjective);
  }
  protected double getYValue(int i) {
    return table.getDouble(i, yObjective);
  }
}