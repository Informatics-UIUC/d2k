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
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.gui.ErrorDialog;
import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.NumericColumn;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.d2k.userviews.swing.JUserPane;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Merge rows in a table based on identical key attributes.
 *
 * @author  unascribed
 * @version 1.0
 */
public class MergeTableRows extends HeadlessUIModule {

   //~ Static fields/initializers **********************************************

   /** constant for Sum */
   static private final String SUM = "Sum";

   /** constant for Average */
   static private final String AVE = "Average";

   /** constant for Maximum */
   static private final String MAX = "Maximum";

   /** constant for Minimum */
   static private final String MIN = "Minimum";

   /** constant for Count */
   static private final String CNT = "Count";

   //~ Instance fields *********************************************************

   /**
    * key columns' names. rows with same value in these columns will be merged
    */
   private String control;

   /** keep the keys for headless support */
   private String[] keys;

   /**
    * the last control used
    */
   private String lastControl;

   /** set of the last keys used */
   private HashSet lastKeys;

   /** last merge method used */
   private String lastMergeMethod;

   /** last set of toMerge used */
   private HashSet lastToMerge;

   /**
    * control column name. the values of the row with largest value in this
    * column will be copied to the merged row.
    */
   private String[] merges; // column names to merge their values.

   /** type of merging (sum, count, etc.) */
   private String type;

   //~ Methods *****************************************************************

   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new CleanView(); }


    /**
     * This method is provided here for compatability only, it must be overriden
     * by HeadlessUIModules.
     *
     * @throws Exception
     */
    public void doit() throws Exception {
        final Table table = (Table) pullInput(0);

        HashMap columns = StaticMethods.getAvailableAttributes(table);

        // validating that properties are not null.
        if (
                keys == null ||
                        keys.length == 0 ||
                        merges == null ||
                        merges.length == 0 ||
                        control == null ||
                        control.length() == 0 ||
                        type == null ||
                        type.length() == 0) {
            throw new Exception(this.getAlias() +
                    " has not been configured. Before running headless, run with the gui and configure the parameters.");
        }

        // fnding out which of keys are relevant to the input table.

        final int[] ks = StaticMethods.getIntersectIds(keys, columns);

        // ks[i] is index of column keys[i]
        if (ks.length < keys.length) {
            throw new Exception("Some of the configured Key Columns were not found in the " +
                    "input table " +
                    ((table.getLabel() == null)
                            ? "" : (" " + table.getLabel())) +
                    ". Please reconfigure the module.");
        }

        for (int i = 0; i < ks.length; i++) {

            if (table.hasMissingValues(ks[i])) {
                throw new Exception(this.getAlias() +
                        " : A key column (" +
                        table.getColumnLabel(ks[i]) +
                        ") has missing values. That is not allowed.");
            }
        }

        final int[] ms = StaticMethods.getIntersectIds(merges, columns);
        ; // ms[i] is index of column merges[i]

        if (ms.length < merges.length) {
            throw new Exception("Some of the configured Merging Columns were not found in the " +
                    "input table " +
                    ((table.getLabel() == null)
                            ? "" : (" " + table.getLabel())) +
                    ". Please reconfigure the module.");
        }

        for (int i = 0; i < ms.length; i++) {

            if (table.hasMissingValues(ms[i])) {
                throw new Exception(this.getAlias() +
                        " : A merge column (" +
                        table.getColumnLabel(ms[i]) +
                        ") has missing values. That is not allowed.");
            }
        }

        final int cntrl = StaticMethods.getID(control, columns);

        // cntrl is index of column control
        if (cntrl == -1) {
            throw new Exception(getAlias() +
                    "The control column \"" +
                    control +
                    "\" could not be found in " +
                    "the input table" +
                    ((table.getLabel() == null)
                            ? "" : (" " + table.getLabel())) +
                    ". Please reconfigure the module.");
        }

        if (table.hasMissingValues(cntrl)) {
            throw new Exception(this.getAlias() +
                    " : The control column (" +
                    control +
                    ") has missing values. That is not allowed.");
        }

        final String _type = type;

        if (
                !(type.equalsIgnoreCase(MergingClass.AVE) ||
                        type.equalsIgnoreCase(MergingClass.CNT) ||
                        type.equals(MergingClass.MAX) ||
                        type.equalsIgnoreCase(MergingClass.MIN) ||
                        type.equalsIgnoreCase(MergingClass.SUM))) {
            throw new Exception(getAlias() +
                    ": The merging type is illegal!\n" +
                    "Please reconfigure this module using the properties editor " +
                    "or via a GUI run, so it can run Headless.");
        }

        // end validation.
        final MutableTable mtbl =
                MergingClass.mergeTable(ks, ms, cntrl, _type, table);
        pushOutput(mtbl, 0);

    } // doit

   /**
    * Get the control field
    *
    * @return control field
    */
   public String getControl() { return control; }

   /**
    * The list of strings returned by this method allows the module to map the
    * results returned from the pier to the position dependent outputs. The
    * order in which the names appear in the string array is the order in which
    * to assign them to the outputs.
    *
    * @return a string array containing the names associated with the outputs in
    *         the order the results should appear in the outputs.
    */
   public String[] getFieldNameMapping() { return null; }


    /**
     * Returns a description of the input at the specified index.
     *
     * @param i Index of the input for which a description should be returned.
     * @return <code>String</code> describing the input at the specified index.
     */
    public String getInputInfo(int i) {

        switch (i) {

            case 0:
                return "The table to be processed by the row merge operation.";

            default:
                return "No such input.";
        }
    }


    /**
     * Returns the name of the input at the specified index.
     *
     * @param index Index of the input for which a name should be returned.
     * @return <code>String</code> containing the name of the input at the specified index.
     */
    public String getInputName(int index) {

        switch (index) {

            case 0:
                return "Input Table";

            default:
                return "No such input.";
        }
    }


    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     *         the corresponding index.
     */
    public String[] getInputTypes() {

        String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};

        return types;
    }

   /**
    * Get the keys used
    *
    * @return keys
    */
   public Object[] getKeys() { return keys; }

   /**
    * Get the last control used
    *
    * @return last control used
    */
   public String getLastControl() { return lastControl; }

   /**
    * Get the set of last keys used
    *
    * @return last keys used
    */
   public HashSet getLastKeys() { return lastKeys; }

   /**
    * Get the last merge method used
    *
    * @return last merge method used
    */
   public String getLastMergeMethod() { return lastMergeMethod; }

   /**
    * Get the last toMerge set used.
    *
    * @return the last toMerge set used
    */
   public HashSet getLastToMerge() { return lastToMerge; }

   /**
    * Get the merges
    *
    * @return merges
    */
   public Object[] getMerges() { return merges; }


    /**
     * Describes the purpose of the module.
     *
     * @return <code>String</code> describing the purpose of the module.
     */
    public String getModuleInfo() {

        String s = "<p>Overview: ";
        s +=
                "This module merges rows in a table that have the same values for one or more key attributes.  ";
        s +=
                "The user selects the key attribute(s) and specifies other information about how the rows should be merged. ";

        s += "</p><p>Detailed Description: ";
        s +=
                "This module merges multiple rows from the <i>Input Table</i> into a single row in the <i>Output Table</i>. ";
        s +=
                "Rows are merged if they have identical values for one or more key attributes. ";
        s +=
                "A set of rows from the <i>Input Table</i> that has identical values for the key attributes are called ";
        s +=
                "<i>matching rows</i>.   One output row is produced for each set of matching rows. ";
        s +=
                "The module presents a dialog that allows selection of the key attribute(s) and control over additional ";
        s += "merge parameters. ";

        s += "</p><p>";
        s +=
                "The module dialog lists all of the attributes in the <i>Input Table</i> and allows the user to select one ";
        s += "or more of them to be the <i>Key</i> for the merge. ";
        s +=
                "The module merges table rows with identical values for all of the specified Key attributes. ";
        s +=
                "The module dialog also lists all of the numeric attributes in the <i>Input Table</i> and allows the user ";
        s += "to select one of these as the <i>Control</i> attribute. ";
        s +=
                "The Control determines which row in each matching row set will be used as the basis ";
        s += "for the resulting merged row. ";
        s +=
                "For a set of matching rows, the row with the maximum value for the Control attribute is the control row. ";

        s += "</p><p>";
        s +=
                "The module dialog also lists the numeric attributes under the <i>Merge</i> heading, ";
        s += "and allows the user to select ";
        s +=
                "one or more of these attributes to be merged across matching rows using the operation specified via the ";
        s += "dialog's <i>Merge Method</i>. ";
        s += "The possible merge methods are <i>Sum</i>, <i>Average</i>, ";
        s += "<i>Maximum</i>, <i>Minimum</i>, and <i>Count</i>. ";
        s +=
                "For each of the Merge attributes selected, the merge method will be applied to the attribute values of all ";
        s +=
                "matching rows in a set and the result will appear in the output merged row. ";

        s += "</p><p>";
        s +=
                "Each row in the <i>Output Table</i> will have the values of the control row attributes ";
        s +=
                "for all string attributes and for the numeric attributes that were not selected as Merge attributes. ";
        s += "That is to say, all data that is not ";
        s +=
                "merged using the merge method is simply copied from the control row for each set of matching rows. ";

        s += "</p><p>Data Type Restrictions: ";
        s +=
                "The <i>Input Table</i> must contain at least one numeric attribute that can be used as the <i>Control</i>. ";
        s +=
                "In addition, the Merge Method can only be applied to numeric attributes. ";

        s += "</p><p>Data Handling: ";
        s +=
                "The <i>Input Table</i> is not modified.   The <i>Output Table</i> is created by the module. ";

        s += "</p><p>Missing Values Handling: ";
        s +=
                "The key, control and merge columns in the <i>Input Table</i> should be clean of missing values. " +
                        "If a missing value is encountered an Exception will be thrown. Use 'RemoveRowsWithMissingValues' module " +
                        "before this module, to clean the <i>Input Table</i>.";

        s += "</p><p>Scalability: ";
        s +=
                "This module should scale very well for tables where the key attribute has a limited number ";
        s += "of unique values. When that is not the case, ";
        s +=
                "in other words, if the key attribute selected is not nominal, the module will not scale ";
        s += "well.</p>";

        return s;
    } // end method getModuleInfo


    /**
     * Returns the name of the module that is appropriate for end-user consumption.
     *
     * @return The name of the module.
     */
    public String getModuleName() {
        return "Merge Table Rows";
    }


    /**
     * Returns a description of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> describing the output at the specified index.
     */
    public String getOutputInfo(int i) {

        switch (i) {

            case 0:
                return "The table that results from the row merge operation.";

            default:
                return "No such output.";
        }
    }


    /**
     * Returns the name of the output at the specified index.
     *
     * @param index Index of the output for which a description should be returned.
     * @return <code>String</code> containing the name of the output at the specified index.
     */
    public String getOutputName(int index) {

        switch (index) {

            case 0:
                return "Output Table";

            default:
                return "No such output.";
        }
    }


    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     *         the corresponding index.
     */
    public String[] getOutputTypes() {

        String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};

        return types;
    }


    /**
     * Get the descriptions of the properties of this module.
     *
     * @return the PropertyDescriptor for each property of this module.
     */
    public PropertyDescription[] getPropertiesDescriptions() {
        PropertyDescription[] pds = new PropertyDescription[3];
        pds[0] = super.supressDescription;
        pds[1] =
                new PropertyDescription("control",
                        "Control Column",
                        "Name of control column for merging (will be used if \"Supress User Interface Display\" is set to true");
        pds[2] =
                new PropertyDescription("type",
                        "Merging Method",
                        "Type of merging method (Sum, Average, Minimum, Maximum or Count) will be used if \"Supress User Interface Display\" is set to true");

        return pds;
    }

   /**
    * Get the type
    *
    * @return type
    */
   public String getType() { return type; }

   /**
    * Set the control
    *
    * @param c control
    */
   public void setControl(String c) { control = c; }

   /**
    * Set the keys
    *
    * @param k keys
    */
   public void setKeys(Object[] k) {
      keys = new String[k.length];

      for (int i = 0; i < k.length; i++) {
         keys[i] = (String) k[i];
      }
   }

   /**
    * Set the last control used
    *
    * @param s last control
    */
   public void setLastControl(String s) { lastControl = s; }

   /**
    * Set the last keys used
    *
    * @param s last keys used
    */
   public void setLastKeys(HashSet s) { lastKeys = s; }

   /**
    * Set the last merge method used
    *
    * @param s the last merge method
    */
   public void setLastMergeMethod(String s) { lastMergeMethod = s; }

   /**
    * Set the last toMerge set used
    *
    * @param s last toMerge set used
    */
   public void setLastToMerge(HashSet s) { lastToMerge = s; }

   /**
    * Set the merges
    *
    * @param m merges
    */
   public void setMerges(Object[] m) {
      merges = new String[m.length];

      for (int i = 0; i < m.length; i++) {
         merges[i] = (String) m[i];
      }
   }

   /**
    * Set the type
    *
    * @param t type
    */
   public void setType(String t) { type = t; }

   //~ Inner Classes ***********************************************************

   /**
    * User view.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class CleanView extends JUserPane {
       /** list holding attributes to merge */
      JList attributesToMerge;

       /** maps attribute name to column index */
      HashMap columnLookup;
       /** list of control attributes */
      JList controlAttribute;
       /** list model for controlAttribute */
      DefaultListModel controlListModel;
       /** list of key attirbutes */
      JList keyAttributeList;
       /** list model for keyAttributeList */
      DefaultListModel keyListModel;
       /** list model for merge */
      DefaultListModel mergeListModel;

       /** options for merge method */
      JComboBox mergeMethod;

      /** table */
      Table table;

       /**
        * The preferred size is (400,300)
        * @return the preferred size
        */
      public Dimension getPreferredSize() { return new Dimension(400, 300); }


       /**
        * Called by the D2K Infrastructure to allow the view to perform initialization tasks.
        *
        * @param m The module this view is associated with.
        */
       public void initView(ViewModule m) {
           keyAttributeList = new JList();
           keyListModel = new DefaultListModel();
           keyAttributeList.setModel(keyListModel);
           controlAttribute = new JList();
           controlListModel = new DefaultListModel();
           controlAttribute.setModel(controlListModel);
           controlAttribute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
           attributesToMerge = new JList();
           mergeListModel = new DefaultListModel();
           attributesToMerge.setModel(mergeListModel);

           JScrollPane jsp1 = new JScrollPane(keyAttributeList);
           jsp1.setColumnHeaderView(new JLabel("Key"));

           JScrollPane jsp2 = new JScrollPane(controlAttribute);
           jsp2.setColumnHeaderView(new JLabel("Control"));

           JScrollPane jsp3 = new JScrollPane(attributesToMerge);
           jsp3.setColumnHeaderView(new JLabel("To Merge"));

           String[] methods = {SUM, AVE, MAX, MIN, CNT};
           mergeMethod = new JComboBox(methods);

           JPanel pnl = new JPanel();
           pnl.add(new JLabel("Merge Method"));

           JPanel pn2 = new JPanel();
           pn2.add(mergeMethod);

           Box b1 = new Box(BoxLayout.Y_AXIS);
           b1.add(jsp3);
           b1.add(pnl);
           b1.add(pn2);

           Box b2 = new Box(BoxLayout.X_AXIS);
           b2.add(jsp1);
           b2.add(jsp2);
           b2.add(b1);

           setLayout(new BorderLayout());
           add(b2, BorderLayout.CENTER);

           JPanel buttonPanel = new JPanel();
           JButton abort = new JButton("Abort");
           abort.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                   viewCancel();
               }
           });

           JButton done = new JButton("Done");
           done.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                   Object[] keys = keyAttributeList.getSelectedValues();
                   Object control = controlAttribute.getSelectedValue();
                   Object[] merges = attributesToMerge.getSelectedValues();
                   final Object type = mergeMethod.getSelectedItem();

                   if (keys == null || keys.length == 0) {
                       ErrorDialog.showDialog("You must select a key attribute.",
                               "Error");

                       return;
                   }

                   if (control == null) {
                       ErrorDialog.showDialog("You must select a control attribute.",
                               "Error");

                       return;
                   }

                   if (merges == null || merges.length == 0) {
                       ErrorDialog.showDialog("You must select one or more attributes to merge.",
                               "Error");

                       return;
                   }

                   if (type == null) {
                       ErrorDialog.showDialog("You must select a method to merge by.",
                               "Error");

                       return;
                   }

                   final int[] ks = new int[keys.length];

                   for (int i = 0; i < keys.length; i++) {
                       ks[i] = ((Integer) columnLookup.get(keys[i])).intValue();
                   }

                   final int[] ms = new int[merges.length];

                   for (int i = 0; i < merges.length; i++) {
                       ms[i] = ((Integer) columnLookup.get(merges[i])).intValue();
                   }

                   final int ctrl =
                           ((Integer) columnLookup.get(control)).intValue();

                   // mTbl;

                   SwingUtilities.invokeLater(new Runnable() {
                       public void run() {
                           // changed by vered - 9/18/03 moved the code of the
                           // merging methods to MergingClass, so it could be
                           // static and reused by doit.

                           final MutableTable mTbl =
                                   MergingClass.mergeTable(ks,
                                           ms,
                                           ctrl, (String) type,
                                           table);
                           pushOutput(mTbl, 0);
                           viewDone("Done");

                           // mergeTable(ks, ms, ctrl, (String)type);
                       }
                   });

                   HashSet usedKeys = new HashSet();

                   for (int i = 0; i < keys.length; i++) {
                       usedKeys.add(keys[i]);
                   }

                   HashSet usedMerges = new HashSet();

                   for (int i = 0; i < merges.length; i++) {
                       usedMerges.add(merges[i]);
                   }

                   setLastControl(control.toString());
                   setLastKeys(usedKeys);
                   setLastToMerge(usedMerges);
                   setLastMergeMethod(type.toString());

                   // headless conversion support
                   setControl(control.toString());
                   setKeys(usedKeys.toArray());
                   setMerges(usedMerges.toArray());
                   setType(type.toString());
                   // headless conversion support
               } // end method actionPerformed
           });
           buttonPanel.add(abort);
           buttonPanel.add(done);
           add(buttonPanel, BorderLayout.SOUTH);
       } // end method initView

      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param  o  The object that has been input.
       * @param  id The index of the module input that been received.
       *
       * @throws Exception when something goes wrong
       */
      public void setInput(Object o, int id) throws Exception {
         table = (Table) o;

         // clear all lists
         keyListModel.removeAllElements();
         controlListModel.removeAllElements();
         mergeListModel.removeAllElements();

         columnLookup = new HashMap(table.getNumColumns());

         String longest = "";

         HashSet selectedKeys = new HashSet();
         HashSet selectedControls = new HashSet();
         HashSet selectedMerges = new HashSet();

         // now add the column labels keyListModel entries can be string or
         // numeric type columns controlListModel and mergeListModel entries
         // must be numeric type columns
         int ni = 0; // index for numeric type selections

         for (int i = 0; i < table.getNumColumns(); i++) {

            if (table.hasMissingValues(i)) {
               continue;
            }

            columnLookup.put(table.getColumnLabel(i), new Integer(i));

            keyListModel.addElement(table.getColumnLabel(i));

            if (
                lastKeys != null &&
                   lastKeys.contains(table.getColumnLabel(i))) {
               selectedKeys.add(new Integer(i));
            }

            if (table.getColumn(i) instanceof NumericColumn) {
               controlListModel.addElement(table.getColumnLabel(i));

               if (
                   lastControl != null &&
                      lastControl.equals(table.getColumnLabel(i))) {
                  selectedControls.add(new Integer(ni));
               }

               mergeListModel.addElement(table.getColumnLabel(i));

               if (
                   lastToMerge != null &&
                      lastToMerge.contains(table.getColumnLabel(i))) {
                  selectedMerges.add(new Integer(ni));
               }

               ni++;
            }

            if (table.getColumnLabel(i).length() > longest.length()) {
               longest = table.getColumnLabel(i);
            }

         } // end for

         // Don't force user to Abort if table data is wrong - abort for them
         // with message.
         if (controlListModel.size() == 0) {
            throw new Exception(getAlias() +
                                ": Input Table does not contain any numeric attributes - itinerary will be aborted");
         }

         keyAttributeList.setPrototypeCellValue(longest);
         controlAttribute.setPrototypeCellValue(longest);
         attributesToMerge.setPrototypeCellValue(longest);

         int[] selKeys = new int[selectedKeys.size()];
         int idx = 0;
         Iterator iter = selectedKeys.iterator();

         while (iter.hasNext()) {
            Integer num = (Integer) iter.next();
            selKeys[idx] = num.intValue();
            idx++;
         }

         int[] selControls = new int[selectedControls.size()];
         idx = 0;
         iter = selectedControls.iterator();

         while (iter.hasNext()) {
            Integer num = (Integer) iter.next();
            selControls[idx] = num.intValue();
            idx++;
         }

         int[] selMerge = new int[selectedMerges.size()];
         idx = 0;
         iter = selectedMerges.iterator();

         while (iter.hasNext()) {
            Integer num = (Integer) iter.next();
            selMerge[idx] = num.intValue();
            idx++;
         }

         keyAttributeList.setSelectedIndices(selKeys);
         controlAttribute.setSelectedIndices(selControls);
         attributesToMerge.setSelectedIndices(selMerge);
         mergeMethod.setSelectedItem(lastMergeMethod);
      } // end method setInput

   } // CleanView

   // headless conversion support

} // end class MergeTableRows
