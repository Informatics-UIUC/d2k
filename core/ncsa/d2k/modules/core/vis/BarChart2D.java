package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.io.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;

/**
 * This module creates a bar chart visualization from <code>Table</code> data.
 * The first column of the table is expected to contain labels, and the second
 * column is expected to contain frequencies.
 */
public class BarChart2D extends VisModule {

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
      sb.append("This module creates a bar chart visualization from ");
      sb.append("<i>Table</i> data. The first column of the table is ");
      sb.append("expected to contain labels, and the second column is ");
      sb.append("expected to contain frequencies.");
      sb.append("</p>");
      return sb.toString();
   }

   public String getModuleName() {
      return "2D Bar Chart";
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

  private int _frequenciesColumn = 1;
  public int getFrequenciesColumn() { return _frequenciesColumn; }
  public void setFrequenciesColumn(int value) { _frequenciesColumn = value; }

////////////////////////////////////////////////////////////////////////////////
// user pane                                                                  //
////////////////////////////////////////////////////////////////////////////////

   private class BarChartUserPane extends JUserPane {

      private Dimension preferredSize = new Dimension(400, 300);
      private Table table;

      public void initView(ViewModule mod) { }

      public void setInput(Object obj, int ind) {
         table = (Table)obj;
         initialize();
      }

      private void initialize() {

         DataSet set = new DataSet("dataset", Color.gray,
            _labelsColumn, _frequenciesColumn);

         GraphSettings settings = new GraphSettings();
         String xaxis = table.getColumnLabel(_labelsColumn);
         String yaxis = table.getColumnLabel(_frequenciesColumn);
         settings.title = xaxis + " and " + yaxis;
         settings.xaxis = xaxis;
         settings.yaxis = yaxis;

         add(new BarChart(table, set, settings));

      }

      public Dimension getPreferredSize() {
         return preferredSize;
      }

   }

}
