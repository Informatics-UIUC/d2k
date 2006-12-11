/* 
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 * 
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 * 
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
package ncsa.d2k.modules.core.vis;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UIModule;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.io.file.output.WriteTableToURL;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyException;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyFactory;
import ncsa.d2k.modules.core.vis.widgets.TableMatrix;
import ncsa.d2k.userviews.swing.JUserPane;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;


/**
 * This module displays the contents of a <code>Table</code>.
 *
 * @author  David Clutter
 * @version $Revision$, $Date$
 */
public class TableViewer extends UIModule {

   //~ Instance fields *********************************************************

   /**
    * //////////////////////////////////////////////////////////////////////////////
    * properties //
    * //////////////////////////////////////////////////////////////////////////////.
    */
   protected int maxFractionDigits = -1;

   //~ Methods *****************************************************************

   /**
    * //////////////////////////////////////////////////////////////////////////////
    * Module methods //
    * //////////////////////////////////////////////////////////////////////////////
    *
    * @return Description of return value.
    */
   public UserView createUserView() { return new TableView(); }

   /**
    * Not used by this module.
    *
    * @return not used by this module.
    */
   public String[] getFieldNameMapping() { return null; }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      if (i == 0) {
         return "The <i>Table</i> to be displayed.";
      }

      return "No such input";
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  i Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int i) {

      if (i == 0) {
         return "Table";
      }

      return "No such input";
   }

   /**
    * Description of method getInputTypes.
    *
    * @return Description of return value.
    */
   public String[] getInputTypes() { return new String[] {
                                               "ncsa.d2k.modules.core.datatype.table.Table"
                                            }; }

   /**
    * Description of method getMaxFractionDigits.
    *
    * @return Description of return value.
    */
   public int getMaxFractionDigits() { return maxFractionDigits; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module displays the contents of a <i>Table</i>.");

      sb.append("</p><p>Detailed Description: ");
      sb.append("This module creates a display window and shows the contents ");
      sb.append("of the input <i>Table</i>.  Table entries that contain ");
      sb.append("missing values are indicated by a  ?  in the display. ");

      sb.append("</p><p>The window can be resized and has both horizontal ");
      sb.append("and vertical scroll bars to accommodate large ");
      sb.append("table sizes.  An individual column can be made wider by ");
      sb.append("clicking on the column divider in the labels row and moving ");
      sb.append("it to the right while keeping the mouse button pressed. ");
      sb.append("Releasing the button sets the new column size.");

      sb.append("</p><p>");
      sb.append("Should you wish to limit the number of decimal digits displayed ");
      sb.append("in the table view, please see the <i>maximum fraction digits</i> ");
      sb.append("property. The underlying data will not be affected.");

      sb.append("</p><p>");
      sb.append("The <i>File</i> pull-down menu offers a <i>Save</i> option to ");
      sb.append("save the displayed table to a tab-delimited file. ");
      sb.append("A file browser window pops up, allowing the user to select ");
      sb.append("where the table should be saved. ");
      sb.append("Missing values in the table appear as blanks in the saved file. ");

      sb.append("</p><p>");
      sb.append("The <i>Done</i> button closes the table viewer window. ");
      sb.append("The <i>Abort</i> button closes the table viewer window and " +
      		"aborts itinerary execution. ");

      sb.append("</p><p>Known Limitations in Current Release: ");
      sb.append("This module was designed to work with a single input table " +
      		"per itinerary run. ");
      sb.append("It will not work properly if it receives multiple inputs per " +
      		"run. ");
      sb.append("If you accidently direct multiple inputs to the module, it " +
      		"may be necessary ");
      sb.append("to resize the Table Viewer Window before the table contents " +
      		"and <i>Abort</i> ");
      sb.append("and <i>Done</i> buttons are visible and/or operational.   " +
      		"Until you resize, it may ");
      sb.append("seem that you have no way to stop the itinerary and " +
      		"correct the problem. ");

      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify its input. The <i>Table</i> ");
      sb.append("is passed, unchanged, as the module's output.");
      sb.append("Only the Table data that is presently visible in the " +
      		"window is requested. ");
      sb.append("For some table representations, in particular those " +
      		"where the table data is not all kept in memory, ");
      sb.append("refocusing the window view on different table cells " +
      		"can result in some noticeable ");
      sb.append("delay while the new table data is loaded. ");

      return sb.toString();
   } // end method getModuleInfo

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleName() { return "Table Viewer"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      if (i == 0) {
         return "The <i>Table</i> that was displayed, unmodified.";
      }

      return "No such output.";
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {

      if (i == 0) {
         return "Table";
      }

      return "No such output";
   }

   /**
    * Description of method getOutputTypes.
    *
    * @return Description of return value.
    */
   public String[] getOutputTypes() { return new String[] {
                                                "ncsa.d2k.modules.core.datatype.table.Table"
                                             }; }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[1];

      pds[0] =
         new PropertyDescription("maxFractionDigits",
                                 "Maximum fraction digits displayed",
                                 "Specifies the maximum number of digits after the decimal point to be displayed for numeric data (-1 for no restriction). The underlying data is not affected.");

      return pds;

   }

   /**
    * Description of method setMaxFractionDigits.
    *
    * @param value Description of parameter value.
    */
   public void setMaxFractionDigits(int value) { maxFractionDigits = value; }

   //~ Inner Classes ***********************************************************

////////////////////////////////////////////////////////////////////////////////
// user view
// //
////////////////////////////////////////////////////////////////////////////////

   /**
    * This class uses a <code>TableMatrix</code> to display the <code>
    * Table</code>.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   public class TableView extends JUserPane implements ActionListener {

      /** Use serialVersionUID for interoperability. */
      static private final long serialVersionUID = 1167587287011530931L;

      TableMatrix matrix;

      JMenuBar menuBar;
      JMenuItem print;

      /** cancel button. */
      protected JButton cancel;

      /** ok button. */
      protected JButton ok;

      /** a reference to our parent module. */
      protected TableViewer parent;

      /** the table with data. */
      protected Table table = null;

      private void printVT() {
         JFileChooser chooser = new JFileChooser();
         String delimiter = "\t";
 //        String newLine = "\n";
         String fileName;
         int retVal = chooser.showSaveDialog(null);

         if (retVal == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile().getAbsolutePath();
         } else {
            return;
         }

         DataObjectProxy dop = null;

         try {
            dop =
               DataObjectProxyFactory.getDataObjectProxy(new URL("file://" +
                                                                 fileName.replaceAll(" ", "%20")));

            WriteTableToURL.writeTable(table, delimiter, dop, true, true);

         } catch (IOException e) {

            JOptionPane.showMessageDialog(this,
                                          "Unable to write to file " +
                                          dop.getURL() + ":\n\n" +
                                          e.getMessage(),
                                          "Error writing file",
                                          JOptionPane.ERROR_MESSAGE);

         } catch (DataObjectProxyException dope) {
            JOptionPane.showMessageDialog(this,
                                          "Unable to write to file " +
                                          dop.getURL() + ":\n\n" +
                                          dope.getMessage(),
                                          "Error writing file",
                                          JOptionPane.ERROR_MESSAGE);

         }
      } // end method printVT

      /**
       * Perform any clean up to the table and call the finish() method on the
       * VerticalTableViewer module. Since all cells are uneditable in this
       * implementation, we simply call the finish() method. A subclass may want
       * to juggle the contents of the table, however.
       */
      protected void finishUp() {
         pushOutput(table, 0);
         viewDone("Done");
      }

      /**
       * This is the ActionListener for the ok and cancel buttons. The
       * finishUp() method is called if ok is pressed. The viewCancel() method
       * of the VerticalTableViewer module is called if cancel is pressed.
       *
       * @param e the ActionEvent
       */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();

         if (src == ok) {
            finishUp();
         } else if (src == cancel) {
            parent.viewCancel();
         } else if (src == print) {
            printVT();
         }
      }

      public Object getMenu() { return menuBar; }

      /**
       * Initialize the view. Insert all components into the view.
       *
       * @param mod The VerticalTableViewer module that owns us
       */
      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param mod The module this view is associated with.
       */
      public void initView(ViewModule mod) {
         parent = (TableViewer) mod;
         menuBar = new JMenuBar();

         JMenu fileMenu = new JMenu("File");
         print = new JMenuItem("Save...");
         print.addActionListener(this);
         fileMenu.add(print);
         menuBar.add(fileMenu);
      }

      /**
       * Called whenever inputs arrive to the module. Save a reference to the
       * table and call initializeTable().
       *
       * @param input the Object that is the input
       * @param idx   the index of the input
       */
      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param input The object that has been input.
       * @param idx   The index of the module input that been received.
       */
      public void setInput(Object input, int idx) {

         if (idx == 0) {
            removeAll();
            table = (Table) input;

            // a panel to put the buttons on
            JPanel buttonPanel = new JPanel();
            ok = new JButton("Done");
            ok.addActionListener(this);
            cancel = new JButton("Abort");
            cancel.addActionListener(this);
            buttonPanel.add(cancel);
            buttonPanel.add(ok);

            // create the matrix
            if (maxFractionDigits < 0) {
               matrix = new TableMatrix(table);
            } else {
               matrix = new TableMatrix(table, maxFractionDigits);
            }

            // add everything to this
            add(matrix, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
         }

      } // end method setInput

   } // end class TableView

} // end class TableViewerFromURL

