package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;

/**
 * This module creates a cluster bar chart visualization from
 * <code>Table</code> data.
 * <p>
 * One column (by default, column 0) must contain the labels of the bars on
 * the chart, another column (by default, column 1) must contain their
 * respective heights, and one last column must contain their time values.
 * Negative height values are ignored (treated as zero). The data
 * <b>must</b> be sorted first by label and second by time value.
 */
public class ClusterBarChart2D extends VisModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   public String[] getFieldNameMapping() {
      return null;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "A <i>Table</i> containing the data to be visualized.";
      return "NO SUCH INPUT";
   }

   public String getInputName(int index) {
      if (index == 0)
         return "Table";
      return "NO SUCH INPUT";
   }

   public String[] getInputTypes() {
      return new String[] {"ncsa.d2k.modules.core.datatype.table.Table"};
   }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module creates a cluster bar chart visualization from ");
      sb.append("<i>Table</i> data. One column (by default, column 0) ");
      sb.append("must contain the labels of the bars on the chart, ");
      sb.append("another column (by default, column 1) must contain their ");
      sb.append("respective heights, and one last column must contain their ");
      sb.append("time values.");
      sb.append("</p><p>Data Type Restrictions: ");
      sb.append("The data must be sorted first by label and second by time ");
      sb.append("value.");
      sb.append("</p><p>Data Handling: ");
      sb.append("Negative height values are ignored (treated as zero).");
      sb.append("</p>");
      return sb.toString();
   }

   public String getModuleName() {
      return "2D Cluster Bar Chart";
   }

   public String getOutputInfo(int index) {
      return "NO SUCH OUTPUT";
   }

   public String getOutputName(int index) {
      return "NO SUCH OUTPUT";
   }

   public String[] getOutputTypes() {
      return null;
   }

   protected UserView createUserView() {
      return new BarChartUserPane();
   }

////////////////////////////////////////////////////////////////////////////////
// properties                                                                 //
////////////////////////////////////////////////////////////////////////////////

   private int _labelsColumn = 0;
   public int getLabelsColumn() { return _labelsColumn; }
   public void setLabelsColumn(int value) { _labelsColumn = value; }

   private int _heightsColumn = 1;
   public int getHeightsColumn() { return _heightsColumn; }
   public void setHeightsColumn(int value) { _heightsColumn = value; }

   private int _timeColumn = 2;
   public int getTimeColumn() { return _timeColumn; }
   public void setTimeColumn(int value) { _timeColumn = value; }

   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[3];

      pds[0] = new PropertyDescription("labelsColumn", "Labels column",
         "Specifies which column of the table contains the data labels.");

      pds[1] = new PropertyDescription("heightsColumn", "Heights column",
         "Specifies which column of the table contains the data heights.");

      pds[2] = new PropertyDescription("timeColumn", "Time column",
         "Specifies which column of the table contains the data time values.");

      return pds;

   }

////////////////////////////////////////////////////////////////////////////////
// user pane                                                                  //
////////////////////////////////////////////////////////////////////////////////

   private class BarChartUserPane extends JUserPane {

      private Dimension preferredSize = new Dimension(600, 300);
      private Table table;

      public void initView(ViewModule mod) { }

      public void setInput(Object obj, int ind) {
         table = (Table)obj;
         initialize();
      }

      private void initialize() {

         DataSet set = new DataSet("dataset", Color.gray,
            _labelsColumn, _heightsColumn, _timeColumn);

         GraphSettings settings = new GraphSettings();
         String xaxis = table.getColumnLabel(_labelsColumn);
         String yaxis = table.getColumnLabel(_heightsColumn);
         settings.title = xaxis + " and " + yaxis;
         settings.xaxis = xaxis;
         settings.yaxis = yaxis;

         add(new JScrollPane(new ClusterBarChart(table, set, settings)));

      }

      public Dimension getPreferredSize() {
         return preferredSize;
      }

   }

}
