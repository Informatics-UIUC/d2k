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
package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.userviews.swing.JUserPane;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;


/**
 * <p>Overview: This module allows the selection of attributes from a table,
 * producing an array of the names of the selected atributes.</p>
 * <p>Detailed Description: A user interface is presented to the user
 * containing two lists. The list on the left contains the names of all the
 * attributes in the table that was input. The list on the right contains a list
 * of all the attributes selected from the list on the left. The <i>add</i>
 * button will move all the attributes select in the list on the left to the
 * list on the right. The <i>remove</i> button will move all the selected
 * attrributes from the list on the right to the list on the left. When the
 * done button is clicked, a string array will be constructed containing the
 * names of the items in the right list, and it will be produced as the sole
 * output.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ChooseAttributeNames
   extends ncsa.d2k.core.modules.HeadlessUIModule {

   //~ Instance fields *********************************************************

   /** the selected attributes */
   private String[] selectedAttributes;

   /**
    * If enabled, allow selection of only those attributes containing missing
    * values.
    */
   private boolean missingOnly = false;

   //~ Methods *****************************************************************

   /**
    * Return true if s is contained in ss.
    *
    * @param  ss array of strings
    * @param  s  string of interest
    *
    * @return true if s is contained in ss
    */
   private boolean contains(String[] ss, String s) {

      if (ss == null) {
         return false;
      }

      for (int i = 0; i < ss.length; i++) {

         if (ss[i].equals(s)) {
            return true;
         }
      }

      return false;

   }


   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new ChooseAttributeView(); }

   /**
    * headless conversion support. If the table is in the same format as the
    * last successful execution, push out the same selected attributes.
    *
    * @throws Exception when something goes wrong.
    */
   public void doit() throws Exception {

      Table table = (Table) pullInput(0);

      if (table == null || selectedAttributes.length == 0) {
         throw new Exception(getAlias() +
                             " has not been configured. Before running " +
                             "headless configure the properties via running with GUI.");
      }

      // Populate a vector with a list of all column names.
      HashMap availableAttributes = new HashMap();

      for (int i = 0; i < table.getNumColumns(); i++) {
         availableAttributes.put(table.getColumnLabel(i), table);
      }

      for (int i = 0; i < selectedAttributes.length; i++) {

         if (availableAttributes.get(selectedAttributes[i]) == null) {
            throw new Exception(getAlias() +
                                ": No attribute named " +
                                selectedAttributes[i] + " was found " +
                                "found in the input table. " +
                                "Please reconfigure this module so it can run Headless.");
         }
      }

      pushOutput(selectedAttributes, 0);

   } // doit

   /**
    * If enabled, allow selection of only those attributes containing missing
    * values.
    *
    * @return true if only attributes containing missing values can be selected
    */
   public boolean getColumnsWithMissingValues() { return missingOnly; }


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
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "The table from which we will choose attributes.";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Table";

         default:
            return "NO SUCH INPUT!";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>" +
             "      Overview: This module allows the selection of attributes from a table, " +
             "      producing an array of the names of the selected atributes." +
             "    </p>" +
             "    <p>" +
             "      Detailed Description: A user interface is presented to the user " +
             "      containing two lists. The list on the left contains the names of all the " +
             "      attributes in the table that was input. The list on the right contains a " +
             "      list of all the attributes selected from the list on the left. The <i>add</i>" +
             " button will move all the attributes select in the list on the left to the " +
             "      list on the right. The <i>remove</i> button will move all the selected " +
             "      attrributes from the list on the right to the list on the left. When the " +
             "      done button is clicked, a string array will be constructed containing " +
             "      the names of the items in the right list, and it will be produced as the " +
             "      sole output." +
             "    </p>";
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Choose Attribute Names"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "The attributes that were selected.";

         default:
            return "No such output";
      }
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Selected Attributes";

         default:
            return "NO SUCH OUTPUT!";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] types = { "[Ljava.lang.String;" };

      return types;
   }

   /**
    * Get the descriptions of the properties of this module.
    *
    * @return the PropertyDescriptor for each property of this module.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] desc = new PropertyDescription[2];
      desc[0] = this.supressDescription;
      desc[1] =
         new PropertyDescription("columnsWithMissingValues",
                                 "Attributes with Missing Values Only",
                                 "If enabled, allow selection of only those attributes containing missing values.");

      return desc;
   }

   /**
    * Get the selected attributes.
    *
    * @return selected attributes
    */
   public Object[] getSelectedAttributes() { return selectedAttributes; }

   /**
    * Description of method setColumnsWithMissingValues.
    *
    * @param b Description of parameter b.
    */
   public void setColumnsWithMissingValues(boolean b) { missingOnly = b; }

   /**
    * Set the selected attributes
    *
    * @param att selected attributes
    */
   public void setSelectedAttributes(Object[] att) {
      selectedAttributes = (String[]) att;
   }

   //~ Inner Classes ***********************************************************

   /**
    * View class.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class ChooseAttributeView extends JUserPane {

      /** add button. */
      JButton add = new JButton("Add");

      /** list of the possible attributes. */
      JList possibleAttributes = new JList();

      /** list model for possibleAttributes. */
      DefaultListModel possibleModel = new DefaultListModel();

      /** remove button. */
      JButton remove = new JButton("Remove");

      /** list of the selected attributes. */
      JList selectedAttributes = new JList();

      /** list model for selectedAttributes. */
      DefaultListModel selectedModel = new DefaultListModel();


      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param mod The module this view is associated with.
       */
      public void initView(ViewModule mod) {
         JPanel canvasArea = new JPanel();
         canvasArea.setLayout(new BorderLayout());

         JPanel buttons = new JPanel();
         buttons.setLayout(new GridLayout(2, 1));
         buttons.add(add);
         buttons.add(remove);

         JPanel b1 = new JPanel();
         b1.add(buttons);

         // The listener for the add button moves stuff from the possible list
         // to the selected list.
         add.setEnabled(false);
         add.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  Object[] sel = possibleAttributes.getSelectedValues();

                  for (int i = 0; i < sel.length; i++) {
                     possibleModel.removeElement(sel[i]);
                     selectedModel.addElement(sel[i]);
                  }
               }
            });

         // The listener for the remove button moves stuff from the selected
         // list to the possible list.
         remove.setEnabled(false);
         remove.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  Object[] sel = selectedAttributes.getSelectedValues();

                  for (int i = 0; i < sel.length; i++) {
                     selectedModel.removeElement(sel[i]);
                     possibleModel.addElement(sel[i]);
                  }
               }
            });

         selectedAttributes.setModel(selectedModel);
         selectedAttributes.addListSelectionListener(new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent e) {

                  if (selectedAttributes.getSelectedIndex() == -1) {
                     remove.setEnabled(false);
                  } else {
                     remove.setEnabled(true);
                  }
               }
            });
         possibleAttributes.setModel(possibleModel);
         possibleAttributes.addListSelectionListener(new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent e) {

                  if (possibleAttributes.getSelectedIndex() == -1) {
                     add.setEnabled(false);
                  } else {
                     add.setEnabled(true);
                  }
               }
            });

         JScrollPane jsp = new JScrollPane(possibleAttributes);
         jsp.setColumnHeaderView(new JLabel("Possible Attributes"));

         JScrollPane jsp1 = new JScrollPane(selectedAttributes);
         jsp1.setColumnHeaderView(new JLabel("Selected Attributes"));

         canvasArea.add(b1, BorderLayout.CENTER);
         canvasArea.add(jsp, BorderLayout.WEST);
         canvasArea.add(jsp1, BorderLayout.EAST);

         JPanel buttonPanel = new JPanel();
         JButton done = new JButton("Done");
         done.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent ae) {
                  Object[] values = selectedModel.toArray();
                  String[] retVal = new String[values.length];

                  if (retVal.length == 0) {
                     JOptionPane.showMessageDialog(null,
                                                   "You must select at least one attribute.",
                                                   "Error",
                                                   JOptionPane.ERROR_MESSAGE);
                     System.out.println("No columns are selected");
                  } else {

                     for (int i = 0; i < retVal.length; i++) {
                        retVal[i] = (String) values[i];
                     }

                     pushOutput(retVal, 0);
                     setSelectedAttributes(retVal);
                     viewDone("Done");
                  }
               }
            });

         JButton abort = new JButton("Abort");
         abort.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent ae) { viewCancel(); }
            });

         buttonPanel.add(abort);
         buttonPanel.add(done);
         canvasArea.add(buttonPanel, BorderLayout.SOUTH);
         add(canvasArea);
      } // end method initView


      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param o     The object that has been input.
       * @param index The index of the module input that been received.
       */
      public void setInput(Object o, int index) {
         Table table = (Table) o;
         selectedModel.removeAllElements();
         possibleModel.removeAllElements();

         String longest = null;
         int lengthOfLongest = 0;
         int num = table.getNumColumns();

         for (int i = 0; i < num; i++) {

            if (!missingOnly || table.hasMissingValues(i)) {
               String elem = (String) table.getColumnLabel(i);

               if (
                   ChooseAttributeNames.this.contains(ChooseAttributeNames.this.selectedAttributes,
                                                         elem)) {
                  selectedModel.addElement(elem);
               } else {
                  possibleModel.addElement(elem);
               }

               if (elem.length() > lengthOfLongest) {
                  longest = elem;
                  lengthOfLongest = elem.length();
               }
            }
         }

         // If Possible Attributes or Selected Attributes labels are
         // longer than any of the attributes, we want the width to be
         // based on those, not on the longest attribute.  -RA 6/03
         String label = "XXXXXXXX Attributes";

         if (label.length() > lengthOfLongest) {
            longest = label;
         }

         possibleAttributes.setPrototypeCellValue(longest);
         selectedAttributes.setPrototypeCellValue(longest);
      } // end method setInput
   } // end class ChooseAttributeView
} // end class ChooseAttributeNames
