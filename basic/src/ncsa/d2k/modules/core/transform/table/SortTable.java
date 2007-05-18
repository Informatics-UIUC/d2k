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
/*
 * Created on Sep 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.gui.Constrain;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import ncsa.d2k.modules.core.util.*;


/**
 * Cascade sorts a MutableTable by sorting the first column and then successive
 * columns based on runs in the previous column. A run is a collection of
 * similar values in a column.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class SortTable extends ncsa.d2k.core.modules.HeadlessUIModule {

   //~ Static fields/initializers **********************************************

   /** constant for None */
   static private final String NONE = "None";

   //~ Instance fields *********************************************************

   /** Number of attributes to use for sorting. */
   private int numberOfSorts = 5;

   /** remember the cascading order of the sort. */
   private String[] sortOrderNames;

   /** Reorder columns based on order of attributes used. */
   private boolean reorderColumns = false;
   private D2KModuleLogger myLogger;
   

   //~ Methods *****************************************************************
   public void beginExecution() {
		  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
	   }

   /**
    * figure out the cascading sort order according to sortOrderNames.
    *
    * @param  table table to sort
    *
    * @return sort order of columns
    *
    * @throws Exception Description of exception Exception.
    */
   private int[] getSortOrder(Table table) throws Exception {
      int[] retVal = new int[0];
      HashMap columns = StaticMethods.getAvailableAttributes(table);

      if (columns.size() == 0) {
    	  myLogger.warn(getAlias() +
                  ": Warning - The input table has no columns.");
      }

      // VG - with new code to reorder columns the sorted order has to
      // include all columns
      retVal = new int[columns.size()];

      for (int i = 0; i < this.sortOrderNames.length; i++) {
         int idx = ((Integer) columns.get(this.sortOrderNames[i])).intValue();
         retVal[i] = idx;
      }

      // adding indices that are not of columns in sortOrderNames
      // to the end of retVal.
      int[] toSortBy = StaticMethods.getIntersectIds(sortOrderNames, columns);

      if (toSortBy.length < sortOrderNames.length) {
         throw new Exception(getAlias() +
                             ": Some of the configured labels were not found in the input table. " +
                             "\nPlease reconfigure this module.");
      }

      Arrays.sort(toSortBy);

      // j index into toSortBy, i index into the columns of the input table
      // k index into retVal
      int i;
      int j;
      int k;

      for (
           i = 0, j = 0, k = sortOrderNames.length;
              i < columns.size() && j < toSortBy.length && k < retVal.length;
              i++) {

         // if i is an index of a column already in retVal - promote j and do
         // nothing.
         if (i == toSortBy[j]) {
            j++;

            continue;
         }

         // otherwise i is an index to be put in retVal[k]
         retVal[k] = i;
         k++;
      } // for i,j,k

      for (; k < retVal.length && i < columns.size(); i++, k++) {
         retVal[k] = i;
      }

      return retVal;
   } // getSortOrder


   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new SortTableView(); }


   /**
    * Sort the table based on the last sorting used. If last execution sorted by
    * attribute "A" and attribute "C", then sort by those same two attributes on
    * this new table.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {
      MutableTable table = (MutableTable) pullInput(0);
      CascadeSort cSort = new CascadeSort(table);


      if (sortOrderNames == null) {
         throw new Exception(this.getAlias() +
                             " has not been configured. Before running headless, run with the gui and configure the parameters.");
      }

      int[] sortorder;

      if (sortOrderNames.length == 0) {
         sortorder = cSort.getDefaultSortOrder();
      } else {
         sortorder = getSortOrder(table);
      }

      if (sortorder == null) {
         sortorder = cSort.getDefaultSortOrder();
      }

      if (sortorder != null && sortorder.length != 0) {
         cSort.sort(sortorder);
      }


      if (this.reorderColumns) {
         cSort.reorder(sortorder);
      }

      pushOutput(table, 0);
   } // doit


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
            return "The Table to sort.";

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
            return "MutableTable";

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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String info =
         "<P><b>Overview:</b><br> This module cascade sorts a MutableTable." +
         "</P><p><b>Detailed Description:</b><br> This module provides the user with an " +
         "interface to choose sorting columns for a cascading sort: The whole table is " +
         "being sorted according to the first column that is chosen. If more than one " +
         "column is chosen for the sort for each successive column, a sort is being " +
         "applied on the table only for runs in the previous column.<br>" +
         "A run is a collection of identical values in a column.</P><P>" +
         "<u>Missing Values Handling:</u> This module handles missing values as if they were " +
         "real meaningful values. For Example: If a missing value in a numeric column " +
         "is represented by zero, then this is the value according to which its record " +
         "will be sorted.</P>" +
         "<P><U>Note:</U> If, for example, the user selects a sorting column only for the second " +
         "sort, the module will relate to this selection as if it is the first sort. " +
         "No messages will be given regarding this miss-selection.</P><P><B>" +
         "Data Handling:</b><br> The data values do not change. Only the rows and possibly the columns are reordered.</P>";

      return info;
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Cascade-sort Table"; }

   /**
    * Get the number of sorts.
    *
    * @return number of sorts
    */
   public int getNumberOfSorts() { return numberOfSorts; }


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
            return "The sorted Table.";

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
            return "MutableTable";

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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

      return types;
   }


   /**
    * Get the descriptions of the properties of this module.
    *
    * @return the PropertyDescriptor for each property of this module.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[3];

      descriptions[0] = this.supressDescription;


      descriptions[1] =
         new PropertyDescription("numberOfSorts",
                                 "Number of attributes to use for sorting",
                                 "Determines the number of attributes to use for sorting.");


      descriptions[2] =
         new PropertyDescription("reorderColumns",
                                 "Reorder columns based on order of attributes used",
                                 "Determines if columns will be reordered based on order of attributes used.");


      return descriptions;
   }

   /**
    * Get reorder columns.
    *
    * @return reorder columns
    */
   public boolean getReorderColumns() { return reorderColumns; }

   /**
    * Get the sort order names.
    *
    * @return sort order names
    */
   public Object[] getSortOrderNames() { return sortOrderNames; }

   /**
    * Initialize an array to be {0,1,2...}
    *
    * @param array array to initialize
    */
   public void initialize(int[] array) {

      for (int index = 0; index < array.length; index++) {
         array[index] = index;
      }
   }

   /**
    * Set the number of sorts.
    *
    * @param sort number of sorts
    */
   public void setNumberOfSorts(int sort) { numberOfSorts = sort; }

   /**
    * Set reorder columns.
    *
    * @param order new reorder columns
    */
   public void setReorderColumns(boolean order) { reorderColumns = order; }

   /**
    * Set the sort order names.
    *
    * @param names sort order names
    */
   public void setSortOrderNames(Object[] names) {
      // becasue names holds also default labels as "None" these needed to be
      // removed. counting first how many non-"None" labels are there

      int relevant = 0;

      for (int i = 0; i < names.length; i++) {

         if (!((String) names[i]).equalsIgnoreCase("none")) {
            relevant++;
         }
      }

      sortOrderNames = new String[relevant];

      // i is counter for sortOrderNames
      // j is counter for names
      for (int i = 0, j = 0; i < relevant; j++) {
         String current = (String) names[j];

         if (!current.equalsIgnoreCase("none")) {
            sortOrderNames[i] = current;
            i++;
         } // if
      } // for i,j
   } // setSortOrderNames

   //~ Inner Classes ***********************************************************

   /**
    * SortTableView.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class SortTableView extends ncsa.d2k.userviews.swing.JUserPane {
      /** num columns */
      int columns;

      /** done button */
      JButton done;
      /** abort button */
      JButton abort;

      /** the number of columns to sort */
      int numsort;
      /** the number of rows of the table */
      int rows;

      /** each combo box holds the names of the attributes */
      JComboBox[] sortchoices;

      /** labels for attribute names */
      JLabel[] sortlabels;
      /** column indices to sort */
      int[] sortorder;

      /** table to sort */
      MutableTable table;


      /**
       * Sort the table for each column selected. Calls cascade sort to do the
       * sorting. Will call reorder if columns should be reordered
       */
      private void sort() {

         CascadeSort cSort = new CascadeSort(table);
         cSort.sort(sortorder);

         if (getReorderColumns()) {
            reorder();
         }

         // Reset global variables
         //runs = null;
         sortorder = null;
         //first = true;
         pushOutput(table, 0);

      }

      /**
       * Layout the UI.
       */
      public void buildView() {
         removeAll();
         setLayout(new BorderLayout());


         JPanel scrollpanel = new JPanel();
         scrollpanel.setLayout(new GridBagLayout());


         for (int index = 0; index < numsort; index++) {
            Constrain.setConstraints(scrollpanel, sortlabels[index], 0, index,
                                     1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 0, 1);
            Constrain.setConstraints(scrollpanel, sortchoices[index], 1, index,
                                     1, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 0);
         }


         JScrollPane scrollpane =
            new JScrollPane(scrollpanel,
                            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
         add(scrollpane, BorderLayout.CENTER);


         JPanel buttonpanel = new JPanel();
         abort = new JButton("Abort");
         done = new JButton("Done");


         abort.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) { viewAbort(); }
            });


         done.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {


                  getSortOrder();


                  // check to make sure the user hasn't chosen the same column
                  // twice
                  HashMap sortMap = new HashMap();
                  Integer I;

                  for (int i = 0; i < sortorder.length; i++) {
                     I = new Integer(sortorder[i]);

                     if (sortMap.containsKey(I)) {


                        JOptionPane.showMessageDialog(SortTableView.this,
                                                      "You cannot sort on the same column twice.",
                                                      "Error",
                                                      JOptionPane.ERROR_MESSAGE);


                        return;
                     } else {
                        sortMap.put(I, I);
                     }
                  }

                  sort();
                  viewDone("Done");
               } // end method actionPerformed
            });


         buttonpanel.add(abort);
         buttonpanel.add(done);
         add(buttonpanel, BorderLayout.SOUTH);
      } // end method buildView

      /**
       * The preferred size is 300x200.
       *
       * @return the preferred size
       */
      public Dimension getPreferredSize() { return new Dimension(300, 200); }

      /**
       * Compute the sort order. Calls setSortOrderNames() instead of returning
       * the sort order.
       */
      public void getSortOrder() {
         ArrayList sortList = new ArrayList();

         // vered: headless support
         ArrayList sortListNames = new ArrayList();

         for (int i = 0; i < sortchoices.length; i++) {
            int idx = sortchoices[i].getSelectedIndex();

            // vered - headless support
            String selectedLabel = (String) sortchoices[i].getSelectedItem();
            sortListNames.add(selectedLabel);


            if (idx != 0) {
               sortList.add(new Integer(idx - 1));
               // else
               // break;
            }
         }

         sortorder = new int[sortList.size()];

         for (int i = 0; i < sortList.size(); i++) {
            sortorder[i] = ((Integer) sortList.get(i)).intValue();
         }

         numsort = sortorder.length;

         // vered - headless support
         setSortOrderNames(sortListNames.toArray());
      } // end method getSortOrder


      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param viewmod The module this view is associated with.
       */
      public void initView(ViewModule viewmod) {
         // module = (SortTable) viewmod;
      }

      /**
       * Reorder the columns based on the sorting criteria.
       */
      public void reorder() {


         int[] indirection = new int[table.getNumColumns()];


         for (int i = 0; i < indirection.length; i++) {
            indirection[i] = i;
         }


         for (int i = 0; i < sortorder.length; i++) {


            table.swapColumns(i, indirection[sortorder[i]]);


            int tmp = indirection[i];
            indirection[i] = indirection[sortorder[i]];
            indirection[sortorder[i]] = tmp;


         }

      }

      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param object     The object that has been input.
       * @param inputindex The index of the module input that been received.
       */
      public void setInput(Object object, int inputindex) {
         table = (MutableTable) object;
         columns = table.getNumColumns();
         rows = table.getNumRows();
         numsort = getNumberOfSorts();


         if (numsort > columns) {
            numsort = columns;
         }

         sortlabels = new JLabel[numsort];

         for (int index = 0; index < numsort; index++) {
            JLabel label = new JLabel((index + 1) + ". Sort by: ");
            sortlabels[index] = label;
         }


         String[] columnlabels = new String[columns + 1];
         columnlabels[0] = NONE;

         for (int index = 0; index < columns; index++) {
            String columnlabel = table.getColumnLabel(index);


            if (columnlabel == null || columnlabel.length() == 0) {
               columnlabel = "column " + index;
            }

            columnlabels[index + 1] = columnlabel;
         }

         sortchoices = new JComboBox[numsort];

         for (int index = 0; index < numsort; index++) {
            JComboBox combobox = new JComboBox(columnlabels);
            sortchoices[index] = combobox;

            if (sortOrderNames != null && sortOrderNames.length > index) {

               // contains?
               for (int j = 0; j < columnlabels.length; j++) {

                  if (sortOrderNames[index].equals(columnlabels[j])) {
                     sortchoices[index].setSelectedItem(columnlabels[j]);
                  }
               }

            }
         }

         buildView();
      } // end method setInput

   } // end class SortTableView
} // end class SortTable
