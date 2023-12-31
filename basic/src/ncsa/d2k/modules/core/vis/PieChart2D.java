package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;

/**
 * This module creates a pie chart visualization for <i>Table</i> data.
 * <p>
 * One column (by default, column 0) must contain the labels of the
 * components of the chart, and another column (by default, column 1) must
 * contain their corresponding ratios.
 */
public class PieChart2D extends VisModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   public String[] getFieldNameMapping() {
      return null;
   }

   /**
 * Returns a description of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a description should be returned.
 *
 * @return <code>String</code> describing the input at the specified index.
 */
public String getInputInfo(int index) {
      if (index == 0)
         return "A <i>Table</i> containing the data to be visualized.";
      return "NO SUCH INPUT";
   }

   /**
 * Returns the name of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the input at the specified index.
 */
public String getInputName(int index) {
      if (index == 0)
         return "Table";
      return "NO SUCH INPUT";
   }

   public String[] getInputTypes() {
      return new String[] {"ncsa.d2k.modules.core.datatype.table.Table"};
   }

   /**
 * Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module creates a pie chart visualization from ");
      sb.append("<i>Table</i> data. One column (by default, column 0) ");
      sb.append("must contain the labels of components of the chart, and ");
      sb.append("another column (by default, column 1) must contain their ");
      sb.append("corresponding ratios.");
      sb.append("</p><p>Missing Values Handling: ");
     sb.append("Missing values are treated as regular ones. ");


      sb.append("</p><p>Data Type Restrictions: ");
      sb.append("Negative ratio values (which make no sense for a pie chart) ");
      sb.append("will lead to inconsistent results.");
      sb.append("</p>");
      return sb.toString();
   }

   /**
* Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleName() {
      return "2D Pie Chart";
   }

   /**
 * Returns a description of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a description should be returned.
 *
 * @return <code>String</code> describing the output at the specified index.
 */
public String getOutputInfo(int index) {
      return "NO SUCH OUTPUT";
   }

   /**
 * Returns the name of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the output at the specified index.
 */
public String getOutputName(int index) {
      return "NO SUCH OUTPUT";
   }

   public String[] getOutputTypes() {
      return null;
   }

   protected UserView createUserView() {
      return new PieChartUserPane();
   }

////////////////////////////////////////////////////////////////////////////////
// properties                                                                 //
////////////////////////////////////////////////////////////////////////////////

   private int _labelsColumn = 0;
   public int getLabelsColumn() { return _labelsColumn; }
   public void setLabelsColumn(int value) { _labelsColumn = value; }

   private int _ratiosColumn = 1;
   public int getRatiosColumn() { return _ratiosColumn; }
   public void setRatiosColumn(int value) { _ratiosColumn = value; }

   private int _maxLegendRows = 5;
   public int getMaxLegendRows() { return _maxLegendRows; }
   public void setMaxLegendRows(int value) {  _maxLegendRows = value; }

   /**
 * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects for each property of the module.
 *
 * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects.
 */
public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[3];

      pds[0] = new PropertyDescription("labelsColumn", "Labels column",
         "Specifies which column of the table contains the data labels.");

      pds[1] = new PropertyDescription("ratiosColumn", "Ratios column",
         "Specifies which column of the table contains the data ratios.");

      pds[2] = new PropertyDescription("maxLegendRows", "Max Legend Rows",
         "Specifies the maximum number of rows drawn in the chart's legend.");

      return pds;

   }

////////////////////////////////////////////////////////////////////////////////
// user pane                                                                  //
////////////////////////////////////////////////////////////////////////////////

   private class PieChartUserPane extends JUserPane {

      private Dimension preferredSize = new Dimension(300, 300);
      private Table table;

      /**
 * Called by the D2K Infrastructure to allow the view to perform initialization tasks.
 *
 * @param module The module this view is associated with.
 */
public void initView(ViewModule mod) { }

      /**
 * Called to pass the inputs received by the module to the view.
 *
 * @param input The object that has been input.
 * @param index The index of the module input that been received.
 */
public void setInput(Object obj, int ind) {
         table = (Table)obj;
         buildView();
      }

      public void buildView() {

         DataSet set = new DataSet("dataset", Color.gray,
            _labelsColumn, _ratiosColumn);

         GraphSettings settings = new GraphSettings();
         String xaxis = table.getColumnLabel(_labelsColumn);
         String yaxis = table.getColumnLabel(_ratiosColumn);
         settings.title = xaxis + " and " + yaxis;
         settings.xaxis = xaxis;
         settings.yaxis = yaxis;

         add(new JScrollPane(new PieChart(table, set, settings, true, _maxLegendRows)));

      }

      public Dimension getPreferredSize() {
         return preferredSize;
      }

   }

}


 /**
 * QA comments:
 *
 * 12-29-03:
 * Vered started qa process.
 * added to module info documentation about missing values handling.
 *
*/