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
package ncsa.d2k.modules.deprecated.core.io.sql;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.userviews.swing.JUserPane;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;


/**
 * Old module to select attributes. <b>This module is deprecated.</b>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class SelectFields extends ncsa.d2k.core.modules.UIModule {

   //~ Instance fields *********************************************************

   /** The workspace. */
   JOptionPane msgBoard = new JOptionPane();

   //~ Methods *****************************************************************

   /**
    * This method is called by D2K to get the UserView for this module.
    *
    * @return the UserView.
    */
   protected UserView createUserView() { return new SelectFieldsView(); }

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * Provides notification that this module is deprecated.
    */
   public void beginExecution() {
      System.out.println(getClass().getName() + " is deprecated.");
      super.beginExecution();
   }


   /**
    * This method returns an array with the names of each DSComponent in the
    * UserView that has a value. These DSComponents are then used as the outputs
    * of this module.
    *
    * @return this method returns an array with the names of each DSComponent in
    *         the UserView that has a value. These DSComponents are then used as
    *         the outputs of this module.
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
            return "A list of available attributes.";

         default:
            return "No such input.";
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
            return "Attributes List";

         default:
            return "No such input";
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
      String[] types = { "java.util.Vector" };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s +=
         "This module allows the user to select one or more attributes " +
         "(fields) " +
         "from a list of attributes. </p>";
      s += "<p>Detailed Description: ";
      s +=
         "This module provides a user-interface that allows one or more " +
         "attributes to be chosen ";
      s += "from a list of attributes. The list of attributes is ";
      s += "retrieved from the <i>Attributes List</i> input port. ";
      s +=
         "The selected attributes will be used to construct SQL queries.  </p>";
      s += "<p>Restrictions: ";
      s +=
         "Currently only Oracle, SQLServer, DB2 and MySql databases are " +
         "supported. ";

      return s;

   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Select Fields"; }

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
            return "No such output";
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
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      // so that "WindowName" property is invisible
      return new PropertyDescription[0];
   }

   //~ Inner Classes ***********************************************************

   /**
    * SelectFieldsView This is the UserView class.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class SelectFieldsView extends JUserPane {
      JButton add = new JButton("Add");
      JList possibleFields = new JList();
      DefaultListModel possibleModel = new DefaultListModel();
      JButton remove = new JButton("Remove");

      JList selectedFields = new JList();
      DefaultListModel selectedModel = new DefaultListModel();

      /**
       * This method adds the components to a Panel and then adds the Panel to
       * the view.
       *
       * @param mod The component.
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
         add.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  Object[] sel = possibleFields.getSelectedValues();

                  for (int i = 0; i < sel.length; i++) {

                     possibleModel.removeElement(sel[i]);

                     selectedModel.addElement(sel[i]);
                  }
               }
            });

         // The listener for the remove button moves stuff from the selected
         // list to the possible list.
         remove.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  Object[] sel = selectedFields.getSelectedValues();

                  for (int i = 0; i < sel.length; i++) {

                     selectedModel.removeElement(sel[i]);

                     possibleModel.addElement(sel[i]);
                  }
               }
            });

         selectedFields.setModel(selectedModel);
         possibleFields.setModel(possibleModel);

         JScrollPane jsp = new JScrollPane(possibleFields);
         jsp.setColumnHeaderView(new JLabel("Possible Attributes"));

         JScrollPane jsp1 = new JScrollPane(selectedFields);
         jsp1.setColumnHeaderView(new JLabel("Selected Attributes"));

         canvasArea.add(b1, BorderLayout.CENTER);
         canvasArea.add( /*possibleFields*/jsp, BorderLayout.WEST);
         canvasArea.add( /*selectedFields*/jsp1, BorderLayout.EAST);

         JPanel buttonPanel = new JPanel();
         JButton done = new JButton("Done");
         done.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent ae) {
                  Object[] values = selectedModel.toArray();
                  String[] retVal = new String[values.length];

                  if (retVal.length == 0) {
                     JOptionPane.showMessageDialog(msgBoard,
                                                   "You must select at least " +
                                                   "one attribute.",
                                                   "Error",
                                                   JOptionPane.ERROR_MESSAGE);
                     System.out.println("No columns are selected");
                  } else {

                     for (int i = 0; i < retVal.length; i++) {
                        retVal[i] = (String) values[i];
                     }

                     pushOutput(retVal, 0);
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
       * This method is called whenever an input arrives, and is responsible for
       * modifying the contents of any gui components that should reflect the
       * value of the input.
       *
       * @param o     this is the object that has been input.
       * @param index the index of the input that has been received.
       */
      public void setInput(Object o, int index) {

         JOptionPane.showMessageDialog(msgBoard,
                                       "This module has been deprecated.  " +
                                       "The functionality is included " +
                                       "in a new " +
                                       "module called 'SelectAttributes' " +
                                       "in core/io/sql.",
                                       "Alert - Module Deprecated",
                                       JOptionPane.ERROR_MESSAGE);
         System.out.println("This module has been deprecated.  The " +
                            "functionality " +
                            "is included in a new module called " +
                            "'SelectAttributes' " +
                            "core/io/sql.");

         Vector fields = (Vector) o;
         selectedModel.removeAllElements();
         possibleModel.removeAllElements();

         String longest = null;
         int lengthOfLongest = 0;

         for (int i = 0; i < fields.size(); i++) {
            String elem = (String) fields.elementAt(i);
            possibleModel.addElement(elem);

            if (elem.length() > lengthOfLongest) {
               longest = elem;
               lengthOfLongest = elem.length();
            }
         }

         // If Possible Attributes or Selected Attributes labels are
         // longer than any of the fields, we want the width to be
         // based on those, not on the longest field.  -RA 6/03
         String label = "XXXXXXXX Attributes";

         if (label.length() > lengthOfLongest) {
            longest = label;
         }

         possibleFields.setPrototypeCellValue(longest);
         selectedFields.setPrototypeCellValue(longest);
      } // end method setInput
   } // end class SelectFieldsView
} // end class SelectFields
