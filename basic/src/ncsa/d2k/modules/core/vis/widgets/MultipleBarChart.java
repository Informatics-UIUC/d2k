package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.Table;

public class MultipleBarChart extends ncsa.d2k.modules.core.vis.widgets.BarChart {

  // Overwrite BarChart Constructor
  public MultipleBarChart(Table table, DataSet set, GraphSettings settings) throws Exception {
    super(table, set, settings);

    settings.displayaxislabels=true;
    settings.displaylegend=true;

    gridsize = settings.gridsize;

    if ( table.getNumRows()== 0 ) throw new Exception( "Table Exception" );

    bins = table.getNumColumns()*table.getNumRows()+ table.getNumRows() -1;

  } // end of Constructor

  public double[] getMinAndMax( Table table){
    double[] minAndMax = new double[2];
    double mandm;
    for(int i=1;  i<table.getNumColumns(); i++){
      for(int j=0; j<table.getNumRows(); j++){
        mandm = table.getDouble(j,i);
        if ( mandm > minAndMax[1]) {
          minAndMax[1] = mandm;
        }
        if ( mandm < minAndMax[0]) {
          minAndMax[0] = mandm;
        }
      } // next j
    } // next i

    minAndMax[1] = maxScale( minAndMax[1] );
    return minAndMax;
  } // end of getMinAndMax


  public void drawDataSet(Graphics2D g2, DataSet set) {
    double x = leftoffset;
    double barwidth = xoffsetincrement;

    for( int row = 0; row < table.getNumRows(); row++ ){
      for (int col = 1; col < table.getNumColumns(); col++) {
        double value = table.getDouble(row, col);
        double barheight = (value - yminimum) / yscale;
        double y = getGraphHeight() - bottomoffset - barheight;
        Rectangle2D.Double rectangle = new Rectangle2D.Double(x, y, barwidth,
            barheight);
        g2.setColor(colors[col % colors.length]);
        g2.fill(rectangle);
        g2.setColor(Color.black);
        g2.draw(rectangle);
        x += xoffsetincrement;
      } // next col
      x+= xoffsetincrement;
    } // next row
  }

}
