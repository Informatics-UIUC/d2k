package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;

/**
 * This module creates a box-and-whisker plot of scalar <code>Table</code> data.
 */
public class BoxPlot extends UIModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   public String getInputInfo(int index) {
      if (index == 0)
         return "A <i>Table</i> with data to be visualized.";
      return "NO SUCH INPUT";
   }

   public String getInputName(int index) {
      if (index == 0)
         return "Table";
      return "NO SUCH INPUT";
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };
   }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module creates a box-and-whisker plot of scalar ");
      sb.append("<i>Table</i> data.");
      sb.append("</p>");
      return sb.toString();
   }

   public String getModuleName() {
      return "Box Plot";
   }

   public String getOutputInfo(int index) {
      return "NO SUCH OUTPUT";
   }

   public String getOutputName(int i) {
      return "NO SUCH OUTPUT";
   }

   public String[] getOutputTypes() {
      return null;
   }

   protected UserView createUserView() {
      return new BoxPlotView();
   }

   protected String[] getFieldNameMapping() {
      return null;
   }

////////////////////////////////////////////////////////////////////////////////
// user view                                                                  //
////////////////////////////////////////////////////////////////////////////////

   private class BoxPlotView extends JUserPane implements ActionListener {

      private Table table;

      private JButton done, abort;
      private JTabbedPane tabbedpane;

      public void initView(ViewModule mod) { }

      public void layoutPanes() {

         removeAll();

         tabbedpane = new JTabbedPane();

         for (int column = 0; column < table.getNumColumns(); column++) {
            if (table.isColumnScalar(column)) {
               BoxPlotPane boxplotpane = new BoxPlotPane(table, column);
               tabbedpane.add(table.getColumnLabel(column), boxplotpane);
            }
         }

         done = new JButton("Done");
         abort = new JButton("Abort");

         done.addActionListener(this);
         abort.addActionListener(this);

         JPanel buttonpanel = new JPanel();
         buttonpanel.add(abort);
         buttonpanel.add(done);

         add(tabbedpane, BorderLayout.CENTER);
         add(buttonpanel, BorderLayout.SOUTH);

      }

      public void setInput(Object object, int input) {
         if (input == 0) {
            table = (Table) object;
            layoutPanes();
         }
      }

      public void actionPerformed(ActionEvent event) {

         Object source = event.getSource();

         if (source == done) {
            // pushOutput((Table) table, 0);
            viewDone("Done");
         }

         if (source == abort)
            viewAbort();

      }

      public Dimension getPreferredSize() {
         return new Dimension(500, 300);
      }

   }

}
