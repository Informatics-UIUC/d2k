package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

import ncsa.d2k.modules.core.optimize.ga.emo.*;

public class PopulationFitnessPlot extends FitnessPlot {
  protected NsgaPopulation pop;

  public void setPopulation(NsgaPopulation p) {
    setChanged(true);

    if(p != pop) {
      pop = p;
    }
  }

  protected int getNumIndividuals() {
    return pop.size();
  }

  protected void drawPoints(Graphics2D g, double xscale, double yscale) {
    synchronized (pop) {
      g.setColor(Color.red);
      NsgaSolution[] members = (NsgaSolution[]) pop.getMembers();
      int numMembers = members.length;
      for (int i = 0; i < numMembers; i++) {
        if (members[i].getRank() == 0) {
          double xvalue = members[i].getObjective(xObjective);
          double yvalue = members[i].getObjective(yObjective);
          double x = (xvalue - xMin) / xscale + left;
          double y = graphheight - bottom - (yvalue - yMin) / yscale;

          g.fill(new Rectangle2D.Double(x, y, 2, 2));
        }
      }
      /*if (selected != null) {
        g.setColor(Color.blue);
        for (int i = 0; i < numMembers; i++) {
          if (members[i].getRank() == 0 && selected[i]) {
            float xvalue = (float) members[i].getObjective(xObjective);
            float yvalue = (float) members[i].getObjective(yObjective);

            float x = (xvalue - xMin) / xscale + left;
            float y = graphheight - bottom - (yvalue - yMin) / yscale;

            g.fill(new Rectangle2D.Float(x, y, 2, 2));
          }
        }
      }*/
    }
  }

  public void setObjectives(int x, int y) {
    if(pop == null)
      return;

    xObjective = x;
    yObjective = y;
  }

  protected void findMinMax() {

    // reset min/max for both objectives by looping through pop
    xMin = yMin = Float.POSITIVE_INFINITY;
    xMax = yMax = Float.NEGATIVE_INFINITY;

    NsgaSolution[] members = (NsgaSolution[])pop.getMembers();
    int numMembers = members.length;
    for (int i = 0; i < members.length; i++) {
      if(members[i].getRank() == 0) {
        float xVal = (float)members[i].getObjective(xObjective);
        if (xVal > xMax)
          xMax = xVal;
        if (xVal < xMin)
          xMin = xVal;
        float yVal = (float)members[i].getObjective(yObjective);
        if (yVal > yMax)
          yMax = yVal;
        if (yVal < yMin)
          yMin = yVal;
      }
    }

    setChanged(true);
  }

  protected double getXValue(int i) {
    return ((NsgaSolution)pop.getMember(i)).getObjective(xObjective);
  }
  protected double getYValue(int i) {
    return ((NsgaSolution)pop.getMember(i)).getObjective(yObjective);
  }
}