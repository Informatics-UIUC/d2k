package ncsa.d2k.modules.core.vis.widgets;

import javax.swing.JPanel;
import java.awt.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 A Chart is similar to a Graph, but it does not plot
 data points. Aggregate values are shown, such as in
 a BarChart or PieChart.
 */
public abstract class Chart extends JPanel {

  // Legend
  double legendleftoffset, legendtopoffset;
  double legendwidth, legendheight;
  double legendspace = 35;

  // Offset from left
  double leftoffset;

  // Offset from right
  double rightoffset;

  // Offset from top
  double topoffset;

  // Offset from bottom
  double bottomoffset;

  // Empty space
  int smallspace = 5;
  int largespace = 10;
  double samplecolorsize = 8;

  // Point size
  int point_size = 4;

  // Longest font widths
  int longest_font_width_x = 0;
  int longest_font_width_y = 0;

  // Data
  DataSet set;
  GraphSettings settings;
  Table table;
  int bins;

  // Dimensions of the chart
  double graphwidth, graphheight;
  int gridsize;

  // Labels
  String title, xlabel, ylabel;

  // Font
  Font font;
  FontMetrics metrics;
  int fontheight, fontascent;

  // Colors
  Color[] colors = {new Color(71, 74, 98), new Color(191, 191, 115),
    new Color(111, 142, 116), new Color(178, 198, 181),
    new Color(153, 185, 216), new Color(96, 93, 71),
    new Color(146, 205, 163), new Color(203, 84, 84),
    new Color(217, 183, 170), new Color(140, 54, 57),
    new Color(203, 136, 76)
  };

  abstract public void initOffsets();
  abstract public void resize();

  public Chart(Table t, DataSet d, GraphSettings g) {
    table = t;
    set = d;
    settings = g;
    bins = table.getNumRows();
  }

  public Color getColor(int i) {
    return colors[i % colors.length];
  }
}