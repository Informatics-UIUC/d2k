package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.file.output.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
   Display the contents of a VerticalTable.
   @author David Clutter
*/
public class TableViewer extends UIModule implements HasNames {

   /**
      Return a description of the function of this module.
      @return A description of this module.
   */
   public String getModuleInfo() {
      StringBuffer b = new StringBuffer( "A table viewer.  This displays");
      b.append(" the contents of a Table.  The table is then");
      b.append(" passed along as the output.");
      return b.toString();
    }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
      return "TableViewer";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
      String[] in = {"ncsa.d2k.modules.core.datatype.table.Table"};
      return in;
    }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
   public String[] getOutputTypes() {
      String[] out = {"ncsa.d2k.modules.core.datatype.table.Table"};
      return out;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
      if(i == 0)
         return "The Table to display.";
      else
         return "No such input!";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
      if(i == 0)
         return "Table";
      else
         return "No such input!";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
      if(i == 0) {
         StringBuffer b = new StringBuffer("The Table that was");
         b.append(" displayed.  No changes are made to the table by");
         b.append(" this module.");
         return b.toString();
      }
      else
         return "No such output!";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
      if(i == 0)
         return "Table";
      else
         return "No such output!";
    }

    /**
       Not used.
    */
    public String[] getFieldNameMapping() {
      return null;
    }

    /**
       Return the UserView that will display the table.
       @return The UserView part of this module.
    */
    public UserView createUserView() {
      return new TableView();
    }

    /**
       The TableView class.  Uses a VerticalTableMatrix to display the
      VerticalTable.
    */
    public class TableView extends JUserPane implements ActionListener{
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
         executionManager.moduleDone(parent);
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
         int retVal = chooser.showOpenDialog(null);
         if(retVal == JFileChooser.APPROVE_OPTION)
            fileName = chooser.getSelectedFile().getAbsolutePath();
         else
            return;
         try {
            FileWriter fw = new FileWriter(fileName);

            // write the column labels
            for(int i = 0; i < table.getNumColumns(); i++) {
               String s = table.getColumnLabel(i);
               fw.write(s, 0, s.length());
               if(i != (table.getNumColumns() - 1))
                  fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }
            fw.write(newLine.toCharArray(), 0, newLine.length());

            // write the datatypes.
            for(int i = 0; i < table.getNumColumns(); i++) {
               String s = WriteTableToFile.getDataType(table.getColumnType(i));
               fw.write(s, 0, s.length());
               if(i != (table.getNumColumns() - 1))
                  fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }
            fw.write(newLine.toCharArray(), 0, newLine.length());

            // write the actual data
            for(int i = 0; i < table.getNumRows(); i++) {
               for(int j = 0; j < table.getNumColumns(); j++) {
                  String s = table.getString(i, j);
                  //System.out.println("s: "+s);
                  fw.write(s, 0, s.length());
                  if(j != (table.getNumColumns() - 1) )
                     fw.write(delimiter.toCharArray(), 0, delimiter.length());
               }
               fw.write(newLine.toCharArray(), 0, newLine.length());
            }
            fw.flush();
            fw.close();
         }
         catch(IOException e) {
            e.printStackTrace();
         }
      }
   }
}
