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
package ncsa.d2k.modules.core.transform.binning;

import ncsa.d2k.core.gui.ErrorDialog;
import ncsa.d2k.core.gui.JD2KFrame;
import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.ADTree;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.transformations.BinTransform;
import ncsa.d2k.modules.core.vis.widgets.TableMatrix;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.gui.Constrain;
import ncsa.gui.DisposeOnCloseListener;
import ncsa.gui.JOutlinePanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;


/**
 * Description of class ADTBinColumns.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ADTBinColumns extends HeadlessUIModule {

   //~ Static fields/initializers **********************************************

   /** constant for empty string. */
   static private final String EMPTY = "";

   /** constant for colon. */
   static private final String COLON = " : ";

   /** constant for comma. */
   static private final String COMMA = ",";

   /** constant for dots. */
   static private final String DOTS = "...";

   /** constant for open parenthesis. */
   static private final String OPEN_PAREN = "(";

   /** constant for close parenthesis. */
   static private final String CLOSE_PAREN = ")";

   /** constant for open bracket. */
   static private final String OPEN_BRACKET = "[";

   /** constant for close bracket. */
   static private final String CLOSE_BRACKET = "]";

   //~ Instance fields *********************************************************

   /** the BinDescriptors that were created. */
   private BinDescriptor[] binDes;

   /**
    * Set this property to true if you wish the binned columns to be created in
    * new columns. It will be used only when 'Supress User Interface Display' is
    * set to true.
    */
   private boolean newColumn;

   /** number format. */
   private NumberFormat nf;

   //~ Methods *****************************************************************

   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new ADTBinColumnsView(); }


   /**
    * headless conversion support. If the table is in the same format as the
    * last successful execution, push out the same BinDescriptors in a new
    * BinTransform.
    *
    * @throws Exception when something goes wrong.
    */
   public void doit() throws Exception {

      // the tree is not necessary for validating relevancy of bins to the
      // table.
      /*ADTree tree = (ADTree) */
      pullInput(0);

      ExampleTable table = (ExampleTable) pullInput(1);

      // BinningUtils.validateBins(table, binDes, getAlias());

      pushOutput(new BinTransform(table, binDes, newColumn), 0);

   } // doit

   // headless conversion support

   /**
    * Get the bin descriptors.
    *
    * @return the bin descriptors
    */
   public Object[] getBinDes() { return binDes; }


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
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "An ADTree containing counts.";

         case 1:
            return "MetaData ExampleTable containing the names of the input/output features.";

         default:
            return "No such input";
      }
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

      switch (i) {

         case 0:
            return "AD Tree";

         case 1:
            return "Meta Data Example Table";

         default:
            return "no such input";
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
      String[] types =
      {
         "ncsa.d2k.modules.core.datatype.ADTree",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<html>  <head>      </head>  <body>" +
             "<P><b>Overview:</B> This module allows a user to interactively bin data using counts stored in an ADTree.</P>" +
             "<P><B>Detailed Description:</B> Numeric data cannot be binned." +
             " The user may bin only nominal data.</P>" +
             "<P>For further information on how to use this module the user may click on the \"Help\" button during run time and get detailed description of each functionality.</P>" +
             "<P><B>Data Handling:</b><BR> This module does not change its input. Rather than that it outputs a Transformation that can be then applied to the data.</P>" +
             "</body></html>";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "AD Tree Bin Columns"; }

   /**
    * Description of method getNewColumn.
    *
    * @return Description of return value.
    */
   public boolean getNewColumn() { return newColumn; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      switch (i) {

         case 0:
            return "A BinTransform, as defined by the user, that can be applied to the input Table.";

         default:
            return "No such output";
      }
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {

      switch (i) {

         case 0:
            return "Binning Transformation";

         case 1:
            return "Meta Data Example Table";

         default:
            return "no such output!";
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
      String[] types =
      { "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform" };

      return types;
   }


   /**
    * Get the descriptions of the properties of this module.
    *
    * @return the PropertyDescriptor for each property of this module.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[2];
      pds[0] = super.supressDescription;
      pds[1] =
         new PropertyDescription("newColumn",
                                 "Create In New Column",
                                 "Set this property to true if you wish the binned columns to be created in new columns. " +
                                 "It will be used only when 'Supress User Interface Display' is set to true.");

      return pds;
   }

   /**
    * Set the bin descriptors.
    *
    * @param bins the new bin descriptors
    */
   public void setBinDes(Object[] bins) { binDes = (BinDescriptor[]) bins; }

   /**
    * Set newColumn.
    *
    * @param val new newColumn
    */
   public void setNewColumn(boolean val) { newColumn = val; }

   //~ Inner Classes ***********************************************************

   /**
    * View class.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class ADTBinColumnsView extends JUserPane {

      /** abort button. */
      private JButton abort;

      /** ADTree. */
      private ADTree adt;

      /** list model for binList. */
      private DefaultListModel binListModel;

      /** Maps column label to index. */
      private HashMap columnLookup;

      /** checkbox. */
      private JCheckBox createInNewColumn;

      /** the current bins. */
      private JList /*numericColumnLabels*/ currentBins;

      /** currently selected bin. */
      private BinDescriptor currentSelectedBin;

      /** list of current selections. */
      private JList currentSelectionItems;

      /** list model for currentSelectionItems. */
      private DefaultListModel currentSelectionModel;

      /* current selection fields */
      private JTextField curSelName;

      /** done button. */
      private JButton done;

      /** Description of field numArrived. */
      private int numArrived = 0;

      /** setup complete. */
      private boolean setup_complete;

      /** the table. */
      private ExampleTable tbl;

      /* textual text field */
      private JTextField textBinName;
      private JList textCurrentGroup;

      /** list model. */
      private DefaultListModel textCurrentModel;

      /** the labels of text columns. */
      private JList /*numericColumnLabels*/ textualColumnLabels;

      /** list model. */
      private DefaultListModel textUniqueModel;

      /* textual lists */
      private JList textUniqueVals;
      private TreeSet[] uniqueColumnValues;

      /* numeric text fields */
      // private JTextField uRangeField;
      // private JTextField specRangeField;
      // private JTextField intervalField;
      // private JTextField weightField;

      private int uniqueTextualIndex = 0;

      /**
       * Constructor.
       */
      private ADTBinColumnsView() {
         setup_complete = false;
         nf = NumberFormat.getInstance();
         nf.setMaximumFractionDigits(3);
      }

      /**
       * Add a bin to the bin list.
       *
       * @param bd new bin
       */
      private void addItemToBinList(BinDescriptor bd) {
         binListModel.addElement(bd);
      }

      /**
       * check to see if a bin already exists with this name.
       *
       * @param  newName new bin name
       *
       * @return true if a bin with this name already exists
       */
      private boolean checkDuplicateBinNames(String newName) {

         for (int bdi = 0; bdi < binListModel.getSize(); bdi++) {
            BinDescriptor bd = (BinDescriptor) binListModel.elementAt(bdi);

            if (newName.equals(bd.name)) {
               return false;
            }
         }

         return true;
      }

      /**
       * Create a numeric bin that goes from min to Double.MAX_VALUE.
       *
       * @param  idx  column index
       * @param  name minimum value
       * @param  sel  Description of parameter sel.
       *
       * @return create a numeric bin that goes from min to Double.MAX_VALUE.
       */
      /*private BinDescriptor createMaxNumericBinDescriptor(int col,
       *                                                  double min) {
       * StringBuffer nameBuffer = new StringBuffer();
       * nameBuffer.append(OPEN_PAREN); nameBuffer.append(nf.format(min));
       * nameBuffer.append(COLON); nameBuffer.append(DOTS);
       * nameBuffer.append(CLOSE_BRACKET);
       *
       * BinDescriptor nb =   new NumericBinDescriptor(col,
       *     nameBuffer.toString(),                            min,
       *               Double.MAX_VALUE,
       * tbl.getColumnLabel(col));
       *
       * return nb;} */

      /**
       * Create a numeric bin that goes from Double.MIN_VALUE to max.
       *
       * @param  idx  column index
       * @param  name Description of parameter max.
       * @param  sel  Description of parameter sel.
       *
       * @return create a numeric bin that goes from Double.MIN_VALUE to max.
       */
      /*private BinDescriptor createMinNumericBinDescriptor(int col,
       *                                                  double max) {
       * StringBuffer nameBuffer = new StringBuffer();
       * nameBuffer.append(OPEN_BRACKET); nameBuffer.append(DOTS);
       * nameBuffer.append(COLON); nameBuffer.append(nf.format(max));
       * nameBuffer.append(CLOSE_BRACKET);
       *
       * BinDescriptor nb =   new NumericBinDescriptor(col,
       *     nameBuffer.toString(),                            Double.MIN_VALUE,
       *                            max,
       * tbl.getColumnLabel(col));
       *
       * return nb;} */

      /**
       * Create a numeric bin that goes from min to max.
       *
       * @param  idx  Description of parameter col.
       * @param  name Description of parameter min.
       * @param  sel  Description of parameter max.
       *
       * @return create a numeric bin that goes from min to max.
       */
      /*private BinDescriptor createNumericBinDescriptor(int col,
       *                                               double min,
       *                                double max) { StringBuffer nameBuffer =
       * new StringBuffer(); nameBuffer.append(OPEN_PAREN);
       * nameBuffer.append(nf.format(min)); nameBuffer.append(COLON);
       * nameBuffer.append(nf.format(max)); nameBuffer.append(CLOSE_BRACKET);
       *
       * BinDescriptor nb =   new NumericBinDescriptor(col,
       *     nameBuffer.toString(),                            min,
       *               max,                            tbl.getColumnLabel(col));
       *
       * return nb;}  */

      /**
       * Create a bin that holds string values.
       *
       * @param  idx  Column index
       * @param  name name of bin
       * @param  sel  String that fall in this bin
       *
       * @return put your documentation comment here.
       */
      private BinDescriptor createTextualBin(int idx,
                                             String name,
                                             Object[] sel) {
         String[] vals = new String[sel.length];

         for (int i = 0; i < vals.length; i++) {
            vals[i] = sel[i].toString();
         }

         return new TextualBinDescriptor(idx, name, vals, adt.getLabel(idx));
      }

      /**
       * Get some help text to put in help window.
       *
       * @return help text
       */
      private String getHelpString() {
         StringBuffer sb = new StringBuffer();
         sb.append("<html><body><h2>Bin Columns</h2>");
         sb.append("This module allows a user to interactively bin nominal data from a table. ");

         sb.append("Click on a nominal column to show a list ");
         sb.append("of unique nominal values in that column in the \"Unique Values\" area below. ");
         sb.append("Select one or more of these values and click the right arrow button to group ");
         sb.append("these values. They can then be assigned a collective name, by entering a string in " +
                   "the name text field on the left.");
         sb.append("<br><br>To assign a name to a particular bin, select that bin in ");
         sb.append("the \"Current Bins\" selection area (top right), enter a name in ");
         sb.append("the \"Name\" field below, and click \"Update\". To bin the data and ");
         sb.append("output the new table, click \"Done\".");
         sb.append("</body></html>");

         return sb.toString();
      }

      /**
       * Get a Set of all the unique values in a acolumn.
       *
       * @param  col column index
       *
       * @return A TreeSet containing all the unique values in a column
       */
      private TreeSet uniqueValues(int col) {

         // return the number of unique items in this column
         TreeSet set = adt.getUniqueValuesTreeSet(col);

         return set;
      }

      /**
       * validate bins.
       *
       * @param  newBins list model containing bins
       *
       * @return true if bins are valid
       */
      private boolean validateBins(DefaultListModel newBins) {
         boolean match = false;

         for (int binIdx = 0; binIdx < newBins.size(); binIdx++) {

            if (
                !(columnLookup.containsKey(((BinDescriptor) newBins.get(binIdx)).label))) {

               // ErrorDialog.showDialog("Current bins contain non-selected
               // attributes. Please remove them.", "Error");
               // System.out.println("no good: " +
               // ((BinDescriptor)newBins.get(binIdx)).label);
               return false;
            }
         }

         return true;
      }

      /**
       * Create all of the components and add them to the view.
       *
       * @param m module
       */
      public void initView(ViewModule m) {
         currentBins = new JList();
         binListModel = new DefaultListModel();
         currentBins.setModel(binListModel);
         currentBins.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         currentBins.addListSelectionListener(new CurrentListener());
         // set up the numeric tab

         // vered: removing the numeric tab, since this module does not support
         // binning of numeric data.

         // textual bins
         JPanel txtpnl = new JPanel();
         txtpnl.setLayout(new GridBagLayout());
         textualColumnLabels = new JList();
         textualColumnLabels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         textualColumnLabels.addListSelectionListener(new TextualListener());
         textualColumnLabels.setModel(new DefaultListModel());
         textUniqueVals = new JList();
         textUniqueModel = new DefaultListModel();
         textUniqueVals.setModel(textUniqueModel);
         textUniqueVals.setFixedCellWidth(100);
         textCurrentGroup = new JList();
         textCurrentGroup.setFixedCellWidth(100);
         textCurrentModel = new DefaultListModel();
         textCurrentGroup.setModel(textCurrentModel);

         JButton addTextToGroup = new JButton(">");
         addTextToGroup.addActionListener(new AbstractAction() {


               /**
                * Invoked when an action occurs.
                *
                * @param e Description of parameter e.
                */
               public void actionPerformed(ActionEvent e) {

                  if (!setup_complete) {
                     return;
                  }

                  Object[] sel = textUniqueVals.getSelectedValues();

                  for (int i = 0; i < sel.length; i++) {

                     // textUniqueModel.removeElement(sel[i]);
                     if (!textCurrentModel.contains(sel[i])) {
                        textCurrentModel.addElement(sel[i]);
                     }
                  }
               }
            });

         JButton removeTextFromGroup = new JButton("<");
         removeTextFromGroup.addActionListener(new AbstractAction() {


               /**
                * Invoked when an action occurs.
                *
                * @param e Description of parameter e.
                */
               public void actionPerformed(ActionEvent e) {

                  if (!setup_complete) {
                     return;
                  }

                  Object[] sel = textCurrentGroup.getSelectedValues();

                  for (int i = 0; i < sel.length; i++) {
                     textCurrentModel.removeElement(sel[i]);
                     // textUniqueModel.addElement(sel[i]);
                  }
               }
            });

         JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);

         // jtp.add(numpnl, "Scalar");
         jtp.add(txtpnl, "Nominal");

         Box bx = new Box(BoxLayout.Y_AXIS);
         bx.add(Box.createGlue());
         bx.add(addTextToGroup);
         bx.add(removeTextFromGroup);
         bx.add(Box.createGlue());

         Box bx1 = new Box(BoxLayout.X_AXIS);
         JScrollPane jp1 = new JScrollPane(textUniqueVals);
         jp1.setColumnHeaderView(new JLabel("Unique Values"));
         bx1.add(jp1);
         bx1.add(Box.createGlue());
         bx1.add(bx);
         bx1.add(Box.createGlue());

         JScrollPane jp2 = new JScrollPane(textCurrentGroup);
         jp2.setColumnHeaderView(new JLabel("Current Group"));
         bx1.add(jp2);
         textBinName = new JTextField(10);

         JButton addTextBin = new JButton("Add");
         addTextBin.addActionListener(new AbstractAction() {


               /**
                * Invoked when an action occurs.
                *
                * @param e Description of parameter e.
                */
               public void actionPerformed(ActionEvent e) {
                  Object[] sel = textCurrentModel.toArray();

                  if (sel.length == 0) {
                     ErrorDialog.showDialog("You must select some nominal values to group.",
                                            "Error");

                     return;
                  }

                  Object val = textualColumnLabels.getSelectedValue();

                  // int idx = ((Integer)columnLookup.get(val)).intValue();
                  int idx = adt.getIndexForLabel((String) val);

                  String textualBinName;

                  if (textBinName.getText().length() == 0) {
                     textualBinName = "bin" + uniqueTextualIndex++;
                  } else {
                     textualBinName = textBinName.getText();
                  }

                  if (!checkDuplicateBinNames(textualBinName)) {
                     ErrorDialog.showDialog("The bin name must be unique, " +
                                            textualBinName + " already used.",
                                            "Error");

                     return;
                  }

                  BinDescriptor bd = createTextualBin(idx, textualBinName, sel);

                  TreeSet set = uniqueColumnValues[idx];

                  for (int i = 0; i < sel.length; i++) {
                     textUniqueModel.removeElement(sel[i]);
                     textCurrentModel.removeElement(sel[i]);
                     set.remove(sel[i]);
                  }

                  addItemToBinList(bd);
                  textBinName.setText(EMPTY);
               } // end method actionPerformed
            });

         JOutlinePanel jop = new JOutlinePanel("Group");
         JPanel pp = new JPanel();
         pp.add(new JLabel("Name"));
         pp.add(textBinName);
         pp.add(addTextBin);
         jop.setLayout(new BoxLayout(jop, BoxLayout.Y_AXIS));
         jop.add(bx1);
         jop.add(pp);

         JScrollPane jp3 = new JScrollPane(textualColumnLabels);
         Constrain.setConstraints(txtpnl,
                                  jp3,
                                  0,
                                  0,
                                  4,
                                  1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.WEST,
                                  1,
                                  1);
         Constrain.setConstraints(txtpnl,
                                  jop,
                                  0,
                                  1,
                                  4,
                                  1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1,
                                  1);

         // now add everything
         JPanel pq = new JPanel();
         pq.setLayout(new BorderLayout());

         JScrollPane jp4 = new JScrollPane(currentBins);
         jp4.setColumnHeaderView(new JLabel("Current Bins"));
         pq.add(jp4, BorderLayout.CENTER);

         JOutlinePanel jop5 = new JOutlinePanel("Current Selection");
         currentSelectionItems = new JList();
         currentSelectionItems.setVisibleRowCount(4);
         currentSelectionItems.setEnabled(false);
         currentSelectionModel = new DefaultListModel();
         currentSelectionItems.setModel(currentSelectionModel);

         JPanel pt = new JPanel();
         curSelName = new JTextField(10);
         pt.add(new JLabel("Name"));
         pt.add(curSelName);

         JButton updateCurrent = new JButton("Update");
         updateCurrent.addActionListener(new AbstractAction() {


               /**
                * Invoked when an action occurs.
                *
                * @param e Description of parameter e.
                */
               public void actionPerformed(ActionEvent e) {

                  if (!setup_complete) {
                     return;
                  }

                  if (currentSelectedBin != null) {
                     currentSelectedBin.name = curSelName.getText();
                     currentBins.repaint();
                  }
               }
            });

         JButton removeBin = new JButton("Remove Bin");
         removeBin.addActionListener(new AbstractAction() {


               /**
                * Invoked when an action occurs.
                *
                * @param e Description of parameter e.
                */
               public void actionPerformed(ActionEvent e) {

                  if (!setup_complete) {
                     return;
                  }

                  if (currentSelectedBin != null) {
                     int col = currentSelectedBin.column_number;

                     if (currentSelectedBin instanceof TextualBinDescriptor) {
                        uniqueColumnValues[col].addAll(((TextualBinDescriptor)
                                                           currentSelectedBin).vals);
                     }

                     binListModel.removeElement(currentSelectedBin);
                     currentSelectionModel.removeAllElements();
                     curSelName.setText(EMPTY);

                     // update the group
                     Object lbl = textualColumnLabels.getSelectedValue();

                     // gpape:
                     if (lbl != null) {

                        // int idx =
                        // ((Integer)columnLookup.get(lbl)).intValue();
                        int idx = adt.getIndexForLabel((String) lbl);
                        TreeSet unique = uniqueColumnValues[idx];
                        textUniqueModel.removeAllElements();
                        textCurrentModel.removeAllElements();

                        Iterator i = unique.iterator();

                        while (i.hasNext()) {
                           textUniqueModel.addElement(i.next());
                        }
                     }
                  } // end if
               } // end method actionPerformed
            });

         // gpape:
         JButton removeAllBins = new JButton("Remove All");
         removeAllBins.addActionListener(new AbstractAction() {


               /**
                * Invoked when an action occurs.
                *
                * @param e Description of parameter e.
                */
               public void actionPerformed(ActionEvent e) {

                  if (!setup_complete) {
                     return;
                  }

                  while (binListModel.getSize() > 0) {

                     BinDescriptor bd =
                        (BinDescriptor) binListModel.firstElement();

                     if (bd instanceof TextualBinDescriptor) {
                        uniqueColumnValues[bd.column_number].addAll(((TextualBinDescriptor)
                                                                        bd).vals);
                     }

                     binListModel.remove(0);

                  }

                  // binListModel.removeAllElements();
                  currentSelectionModel.removeAllElements();
                  curSelName.setText(EMPTY);

                  // update the group
                  Object lbl = textualColumnLabels.getSelectedValue();

                  // gpape:
                  if (lbl != null) {
                     int idx = ((Integer) columnLookup.get(lbl)).intValue();
                     TreeSet unique = uniqueColumnValues[idx];
                     textUniqueModel.removeAllElements();
                     textCurrentModel.removeAllElements();

                     Iterator i = unique.iterator();

                     while (i.hasNext()) {
                        textUniqueModel.addElement(i.next());
                     }
                  }

               } // end method actionPerformed
            });

         // gpape:
         createInNewColumn = new JCheckBox("Create in new column", false);

         Box pg = new Box(BoxLayout.X_AXIS);
         pg.add(updateCurrent);

         // pg.add(removeItems);
         pg.add(removeBin);
         pg.add(removeAllBins);

         // gpape:
         Box pg2 = new Box(BoxLayout.X_AXIS);
         pg2.add(createInNewColumn);
         jop5.setLayout(new BoxLayout(jop5, BoxLayout.Y_AXIS));
         jop5.add(pt);

         JScrollPane pane = new JScrollPane(currentSelectionItems);
         pane.setColumnHeaderView(new JLabel("Items"));
         jop5.add(pane);
         jop5.add(pg);
         jop5.add(pg2);

         JPanel bgpnl = new JPanel();
         bgpnl.setLayout(new BorderLayout());
         bgpnl.add(jp4, BorderLayout.CENTER);
         bgpnl.add(jop5, BorderLayout.SOUTH);

         // finally add everything to this
         setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

         Box bxl = new Box(BoxLayout.X_AXIS);

         // bxl.add(jtp); commented out by vered. no need for a tab...
         bxl.add(txtpnl);
         bxl.add(bgpnl);

         JPanel buttonPanel = new JPanel();
         abort = new JButton("Abort");
         abort.addActionListener(new AbstractAction() {


               /**
                * Invoked when an action occurs.
                *
                * @param e Description of parameter e.
                */
               public void actionPerformed(ActionEvent e) { viewCancel(); }
            });
         done = new JButton("Done");
         done.addActionListener(new AbstractAction() {


               /**
                * Invoked when an action occurs.
                *
                * @param e Description of parameter e.
                */
               public void actionPerformed(ActionEvent e) {

                  // binIt(createInNewColumn.isSelected());
                  Object[] tmp = binListModel.toArray();
                  BinDescriptor[] bins = new BinDescriptor[tmp.length];

                  for (int i = 0; i < bins.length; i++) {
                     bins[i] = (BinDescriptor) tmp[i];
                  }

                  // ANCA add "unknown" bins for missing values
                  // bins = BinningUtils.addMissingValueBins(tbl, bins);

                  // headless conversion support
                  setBinDes(bins);

                  BinTransform bt =
                     new BinTransform(tbl, bins,
                                      createInNewColumn.isSelected());

                  pushOutput(bt, 0);

                  // pushOutput(tbl, 1);
                  viewDone("Done");
               }
            });

         JButton showTable = new JButton("Show Table");
         showTable.addActionListener(new AbstractAction() {


               /**
                * Invoked when an action occurs.
                *
                * @param e Description of parameter e.
                */
               public void actionPerformed(ActionEvent e) {
                  JD2KFrame frame = new JD2KFrame("Table");
                  frame.getContentPane().add(new TableMatrix(tbl));
                  frame.addWindowListener(new DisposeOnCloseListener(frame));
                  frame.pack();
                  frame.setVisible(true);
               }
            });

         JButton helpButton = new JButton("Help");
         helpButton.addActionListener(new AbstractAction() {


               /**
                * Invoked when an action occurs.
                *
                * @param e Description of parameter e.
                */
               public void actionPerformed(ActionEvent e) {
                  HelpWindow help = new HelpWindow();
                  help.setVisible(true);
               }
            });
         buttonPanel.add(abort);
         buttonPanel.add(done);
         buttonPanel.add(showTable);
         buttonPanel.add(helpButton);
         setLayout(new BorderLayout());
         add(bxl, BorderLayout.CENTER);
         add(buttonPanel, BorderLayout.SOUTH);
      } // end method initView


      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param o  The object that has been input.
       * @param id The index of the module input that been received.
       */
      public void setInput(Object o, int id) {

         if (id == 0) {
            adt = (ADTree) o;
            numArrived = 1;
         }

         if (id == 1) {
            tbl = (ExampleTable) o;
            numArrived++;
         }

         if (numArrived == 2) {

            // clear all text fields and lists...
            curSelName.setText(EMPTY);
            textBinName.setText(EMPTY);

            // uRangeField.setText(EMPTY);
            // specRangeField.setText(EMPTY);
            // intervalField.setText(EMPTY);
            // weightField.setText(EMPTY);
            columnLookup = new HashMap();


            uniqueColumnValues = new TreeSet[tbl.getNumColumns() + 1];
            binListModel.removeAllElements();

            // DefaultListModel numModel =
            // (DefaultListModel) numericColumnLabels.getModel(),
            DefaultListModel txtModel =
               (DefaultListModel) textualColumnLabels.getModel();

            // numModel.removeAllElements();
            txtModel.removeAllElements();

            textCurrentModel.removeAllElements();
            textUniqueModel.removeAllElements();
            uniqueTextualIndex = 0;

            // ANCA moved fix below from BinAttributes to ADTBinAttributes
            // !: check inputs/outputs if example table
            ExampleTable et = null;
            HashMap etInputs = null;
            HashMap etOutputs = null;

            if (tbl instanceof ExampleTable) {
               et = (ExampleTable) tbl;

               int[] inputs = et.getInputFeatures();
               int[] outputs = et.getOutputFeatures();
               etInputs = new HashMap();
               etOutputs = new HashMap();

               for (int i = 0; i < inputs.length; i++) {
                  etInputs.put(new Integer(inputs[i]), null);
               }

               for (int i = 0; i < outputs.length; i++) {
                  etOutputs.put(new Integer(outputs[i]), null);
               }
            }

            for (int i = 0; i < tbl.getNumColumns(); i++) {

               if (et != null) {

                  if (
                      !etInputs.containsKey(new Integer(i)) &&
                         !etOutputs.containsKey(new Integer(i))) {
                     continue;
                  }
               }

               columnLookup.put(tbl.getColumnLabel(i), new Integer(i));

               // if (table.getColumn(i) instanceof NumericColumn)
               if (!tbl.isColumnScalar(i)) {
                  int idx = adt.getIndexForLabel(tbl.getColumnLabel(i));
                  columnLookup.put(tbl.getColumnLabel(i), new Integer(idx));
                  txtModel.addElement(tbl.getColumnLabel(i));
                  uniqueColumnValues[idx] = uniqueValues(idx);
               }

            }

            if (!validateBins(binListModel)) {
               binListModel.removeAllElements();
            }

            // finished...
            setup_complete = true;
         } // end if
      } // end method setInput

      /**
       * listen for when the currently selected bin changes.
       *
       * @author  $Author$
       * @version $Revision$, $Date$
       */
      private class CurrentListener implements ListSelectionListener {


         /**
          * Called whenever the value of the selection changes.
          *
          * @param e the event that characterizes the change.
          */
         public void valueChanged(ListSelectionEvent e) {

            if (!setup_complete) {
               return;
            }

            if (!e.getValueIsAdjusting()) {
               currentSelectedBin =
                  (BinDescriptor) currentBins.getSelectedValue();

               if (currentSelectedBin == null) {
                  currentSelectionModel.removeAllElements();
                  curSelName.setText(EMPTY);

                  return;
               }

               curSelName.setText(currentSelectedBin.name);

               if (currentSelectedBin instanceof NumericBinDescriptor) {
                  currentSelectionModel.removeAllElements();
                  currentSelectionModel.addElement(currentSelectedBin.name);
               } else {
                  currentSelectionModel.removeAllElements();

                  HashSet hs =
                     (HashSet) ((TextualBinDescriptor) currentSelectedBin).vals;
                  Iterator i = hs.iterator();

                  while (i.hasNext()) {
                     currentSelectionModel.addElement(i.next());
                  }
               }
            } // end if
         } // end method valueChanged
      } // ADTBinColumnsView$CurrentListener

      /**
       * A window that will contain the help text.
       *
       * @author  $Author$
       * @version $Revision$, $Date$
       */
      private class HelpWindow extends JD2KFrame {

         /**
          * put your documentation comment here.
          */
         public HelpWindow() {
            super("About ADT Bin Columns");

            JEditorPane ep = new JEditorPane("text/html", getHelpString());
            ep.setCaretPosition(0);
            getContentPane().add(new JScrollPane(ep));
            setSize(400, 400);
         }
      }

      /**
       * listen for when the textual column labels selection changes.
       *
       * @author  $Author$
       * @version $Revision$, $Date$
       */
      private class TextualListener implements ListSelectionListener {


         /**
          * Called whenever the value of the selection changes.
          *
          * @param e the event that characterizes the change.
          */
         public void valueChanged(ListSelectionEvent e) {

            if (!setup_complete) {
               return;
            }

            if (!e.getValueIsAdjusting()) {
               Object lbl = textualColumnLabels.getSelectedValue();

               if (lbl != null) {

                  // int idx = ((Integer)columnLookup.get(lbl)).intValue();
                  int idx = adt.getIndexForLabel((String) lbl);

                  // System.out.println("index " + idx + " label " +
                  // (String)lbl);
                  TreeSet unique = uniqueColumnValues[idx];
                  textUniqueModel.removeAllElements();
                  textCurrentModel.removeAllElements();

                  Iterator i = unique.iterator();

                  while (i.hasNext()) {
                     textUniqueModel.addElement(i.next());
                  }
               }
            }
         } // end method valueChanged
      } // ADTBinColumnsView$TextualListener
   } // ADTBinColumnsView

} // ADTBinColumns
