package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.file.output.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;

/**
 * This module displays the contents of a <code>Table</code>.
 *
 * @author David Clutter
 */
public class TableViewer extends UIModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   public UserView createUserView() {
      return new TableView();
   }

   /**
    * Not used by this module.
    */
   public String[] getFieldNameMapping() {
      return null;
   }

   public String getInputInfo(int i) {
      if (i == 0)
         return "The <i>Table</i> to be displayed.";
      return "NO SUCH INPUT";
   }

   public String getInputName(int i) {
      if (i == 0)
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
      sb.append("This module displays the contents of a <i>Table</i>.");
      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify its input. The <i>Table</i> ");
      sb.append("is passed, unchanged, as the module's output.");
      sb.append("</p>");
      return sb.toString();
   }

   public String getModuleName() {
      return "View Table";
   }

   public String getOutputInfo(int i) {
      if (i == 0)
         return "The <i>Table</i> that was displayed, unmodified.";
      return "NO SUCH OUTPUT";
   }

   public String getOutputName(int i) {
      if (i == 0)
         return "Table";
      return "NO SUCH OUTPUT";
   }

   public String[] getOutputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };
   }

////////////////////////////////////////////////////////////////////////////////
// user view                                                                  //
////////////////////////////////////////////////////////////////////////////////

   /**
    * This class uses a <code>TableMatrix</code> to display the
    * <code>Table</code>.
    */
   public class TableView extends JUserPane implements ActionListener {

      TableMatrix matrix;
      /** the table with data */
      protected Table table = null;
      /** a reference to our parent module */
      protected TableViewer parent;
      /** ok button */
      protected JButton ok;
      /** cancel button */
      protected JButton cancel;

      JMenuBar menuBar;
      JMenuItem print;

      /**
         Initialize the view.  Insert all components into the view.
         @param mod The VerticalTableViewer module that owns us
         */
      public void initView(ViewModule mod) {
         parent = (TableViewer)mod;
         menuBar = new JMenuBar();
         JMenu fileMenu = new JMenu("File");
         print = new JMenuItem("Save...");
         print.addActionListener(this);
         fileMenu.add(print);
         menuBar.add(fileMenu);
      }

      public Object getMenu() {
         return menuBar;
      }

      /**
         Called whenever inputs arrive to the module.  Save a
         reference to the table and call initializeTable().
         @param input the Object that is the input
         @param idx the index of the input
         */
      public void setInput(Object input, int idx) {
         if(idx == 0) {
            removeAll();
            table = (Table)input;
            // a panel to put the buttons on
            JPanel buttonPanel = new JPanel();
            ok = new JButton("Done");
            ok.addActionListener(this);
            cancel = new JButton("Abort");
            cancel.addActionListener(this);
            buttonPanel.add(cancel);
            buttonPanel.add(ok);

            // create the matrix
            matrix = new TableMatrix(table);
            // add everything to this
            add(matrix, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
         }
      }

      /**
         Perform any clean up to the table and call the finish() method
         on the VerticalTableViewer module.  Since all cells are
         uneditable in this implementation, we simply call the finish()
         method.  A subclass may want to juggle the contents of the table,
         however.
         */
      protected void finishUp() {
         pushOutput(table, 0);
         viewDone("Done");
      }

      /**
         This is the ActionListener for the ok and cancel buttons.  The
         finishUp() method is called if ok is pressed.  The viewCancel()
         method of the VerticalTableViewer module is called if cancel is
         pressed.
         @param e the ActionEvent
         */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();
         if(src == ok)
            finishUp();
         else if(src == cancel)
            parent.viewCancel();
         else if(src == print)
            printVT();
      }

      private void printVT() {
         JFileChooser chooser = new JFileChooser();
         String delimiter = "\t";
         String newLine = "\n";
         String fileName;
         int retVal = chooser.showSaveDialog(null);
         if(retVal == JFileChooser.APPROVE_OPTION)
            fileName = chooser.getSelectedFile().getAbsolutePath();
         else
            return;
         try {
            WriteTableToFile.writeTable(table, delimiter, fileName, true, true);
         }
         catch(IOException e) {

            // e.printStackTrace();

            JOptionPane.showMessageDialog(this,
               "Unable to write to file " + fileName + ":\n\n" + e.getMessage(),
               "Error writing file", JOptionPane.ERROR_MESSAGE);

         }
      }

   }

}
