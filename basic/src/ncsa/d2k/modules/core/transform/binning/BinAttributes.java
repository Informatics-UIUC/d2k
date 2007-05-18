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
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.transformations.BinTransform;
import ncsa.d2k.modules.core.datatype.table.util.ScalarStatistics;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;
import ncsa.d2k.modules.core.vis.widgets.Histogram;
import ncsa.d2k.modules.core.vis.widgets.IntervalHistogram;
import ncsa.d2k.modules.core.vis.widgets.RangeHistogram;
import ncsa.d2k.modules.core.vis.widgets.TableMatrix;
import ncsa.d2k.modules.core.vis.widgets.UniformHistogram;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import ncsa.d2k.modules.core.util.*;


/**
 * put your documentation comment here.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class BinAttributes extends HeadlessUIModule {

   //~ Static fields/initializers **********************************************

   /** empty string */
   static private final String EMPTY = "";
    /** colon */
   static private final String COLON = " : ";
    /** comma */
   static private final String COMMA = ",";
    /** dots */
   static private final String DOTS = "...";
    /** open parenthesis */
   static private final String OPEN_PAREN = "(";
    /** close parenthesis */
   static private final String CLOSE_PAREN = ")";
    /** open bracket */
   static private final String OPEN_BRACKET = "[";
    /** close bracket */
   static private final String CLOSE_BRACKET = "]";

   //~ Instance fields *********************************************************

    /**
     * Set this property to true if you wish the binned columns to be created
     * in new columns (applied only when 'Supress User Interface Display' is
     * set to true)
     */
   private boolean newColumn;

   /** number format */
   private NumberFormat nf;

   /** the saved bins. */
   private BinDescriptor[] savedBins;

   //~ Methods *****************************************************************

   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new BinColumnsView(); }
   
   private D2KModuleLogger myLogger;
   
   public void beginExecution() {
	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   }

   /**
    * headless conversion support.  Push out a bin transform based on the
    * contents of the last set of bins created, regardless of the table
    * structure
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {
      Table table = (Table) pullInput(0);

      // BinningUtils.validateBins(table, savedBins, getAlias());
      pushOutput(new BinTransform(table, savedBins, newColumn), 0);
   }


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
            return "A Table with attributes to bin. If it is an Example Table, only input and output features will be eligible for binning.";

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

      if (i == 0) {
         return "Table";
      }

      return "no such input";
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] types = {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module allows a user to interactively bin data from a table.");

      sb.append("</p><p>Detailed Description: ");
      sb.append("This module provides a powerful interface that allows the user to ");
      sb.append("control how numeric (continuous/scalar) and nominal (categorical) data in the input ");
      sb.append("Table should be divided into bins. ");

      sb.append("</p><p> Numeric data can be binned in four ways:<br>");
      sb.append("<BR><U>Uniform range:</U><br>");
      sb.append("Enter a positive integer value for the number of bins. The module will divide the ");
      sb.append("binning range evenly over these bins. <br> ");
      sb.append("<BR><U>Specified range:</U><br> ");
      sb.append("Enter a comma-separated sequence of integer or floating-point values for the endpoints of each bin. <br> ");
      sb.append("<BR><U>Bin Interval:</u><br> ");
      sb.append("Enter an integer or floating-point value for the width of each bin.<br> ");
      sb.append("<BR><u>Uniform Weight:</U><br> ");
      sb.append("Enter a positive integer value for even binning with that number in each bin. ");

      sb.append("</P><P>The user may also bin nominal data. ");

      sb.append("</P><P>For further information on how to use this module the user may click on the \"Help\" button ");
      sb.append("during run time and get a detailed description of the functionality.");

      sb.append("</p><P>Missing and Empty Values Handling: In scalar attributes, missing and empty values will be binned into ");
      sb.append("\"UNKNOWN\". In nominal attributes, however, missing/empty values which are represented by ");
      sb.append("'?' will be treated as a unique value in the attribute. In this case, if the user does not group ");
      sb.append("the '?' and assign it a bin, then it would also be binned into \"UNKNOWN\".");

      sb.append("</p><P>Data Handling:  This module does not change its input.  ");
      sb.append("Rather, it outputs a Transformation that can later be applied to the original table data. ");
      sb.append("Note that if its input is an example table, only input and output features will be eligible for binning.</P>");

      return sb.toString();
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Bin Attributes"; }

   /**
    * Get newColumn
    *
    * @return newColumn
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

            String s =
               "A Binning Transformation, as defined by the user via this module.  " +
               "This output is typically connected to a <i>Create Bin Tree</i> module " +
               "or to an <i>Apply Transformation</i> module where it is applied to the input Table. ";

            return s;

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
      {
         "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform"
      };

      return types;
   }


   /**
    * Get the descriptions of the properties of this module.
    *
    * @return the PropertyDescriptor for each property of this module.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[2];
      pds[0] = this.supressDescription;
      pds[1] =
         new PropertyDescription("newColumn", "Create In New Column",
                                 "Set this property to true if you wish the binned columns to be created in new columns (applied only when 'Supress User Interface Display' is set to true)");

      return pds;
   }

   /**
    * Get the saved bins.
    *
    * @return saved bins
    */
   public Object getSavedBins() { return savedBins; }

   /**
    * Set newColumn
    *
    * @param val new newColumn
    */
   public void setNewColumn(boolean val) { newColumn = val; }

   /**
    * Set the saved bins.
    *
    * @param value new saved bins
    */
   public void setSavedBins(Object value) {
      savedBins = (BinDescriptor[]) value;
   }

   //~ Inner Classes ***********************************************************

   /**
    * View class.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class BinColumnsView extends JUserPane {
       /** abort button */
      private JButton abort;
       /** done button */
      private JButton done;
       /** list model for binList */
      private DefaultListModel binListModel;
       /** maps column label to column index */
      private HashMap columnLookup;
       /** checkbox */
      private JCheckBox createInNewColumn;
       /** the currently selected bin */
      private BinDescriptor currentSelectedBin;
       /** the currently selected items */
      private JList currentSelectionItems;
       /** list model for currentSelectionItems */
      private DefaultListModel currentSelectionModel;

      /* current selection fields */
      private JTextField curSelName;
       /** list of the numeric column labels */
      private JList numericColumnLabels;
       /** list of the textual column labels */
      private JList textualColumnLabels;
       /** list of the current bins */
      private JList currentBins;
       /** is setup complete? */
      private boolean setup_complete;
       /** the table */
      private MutableTable tbl;

      /* textual text field */
      private JTextField textBinName;
       /** list model */
      private DefaultListModel textUniqueModel;
       /** list model */
      private DefaultListModel textCurrentModel;

      /* textual lists */
      private JList textUniqueVals;
       /** list */
      private JList textCurrentGroup;
       /** Sets containing the unique textual values for each column */
      private HashSet[] uniqueColumnValues;

      /* numeric text fields */
      private JTextField uRangeField;
      private JTextField specRangeField;
      private JTextField intervalField;
      private JTextField weightField;

      private int uniqueTextualIndex = 0;

      /**
       * Constructor
       */
      private BinColumnsView() {
         setup_complete = false;
         nf = NumberFormat.getInstance();
         nf.setMaximumFractionDigits(5);
         nf.setMinimumFractionDigits(5);
      }

      /**
       * Add bins from an interval - the width of each bin.
       */
      private void addFromInterval() {
         int[] colIdx = getSelectedNumericIndices();

         // vered:
         if (colIdx.length == 0) {
            ErrorDialog.showDialog("You must select an attribute to bin.",
                                   "Error");

            return;
         }

         if (!checkDuplicateNumericBins(colIdx)) {
            return;
         }

         // the interval is the width
         String txt = intervalField.getText();
         double intrval;

         try {
            intrval = Double.parseDouble(txt);
         } catch (NumberFormatException e) {

            // vered:
            ErrorDialog.showDialog("Must specify a positive number", "Error");

            return;
         }

         if (intrval <= 0) {
            ErrorDialog.showDialog("Must specify a positive number", "Error");

            return;
         }

         // find the mins and maxes
         double[] maxes = new double[colIdx.length];
         double[] mins = new double[colIdx.length];

         for (int i = 0; i < colIdx.length; i++) {
            maxes[i] = Double.NEGATIVE_INFINITY;
            mins[i] = Double.POSITIVE_INFINITY;
         }

         for (int i = 0; i < colIdx.length; i++) {

            // find the max and min
            // NumericColumn nc = (NumericColumn)table.getColumn(colIdx[i]);
            /*      for (int j = 0; j < tbl.getNumRows(); j++) {
             *        double d = tbl.getDouble(j, colIdx[i]);       if (d >
             * maxes[i])             maxes[i] = d;       if (d < mins[i])
             *      mins[i] = d; }
             */
            // ANCA added support for missing  values
            ScalarStatistics ss =
               TableUtilities.getScalarStatistics(tbl, colIdx[i]);
            maxes[i] = ss.getMaximum();
            mins[i] = ss.getMinimum();


            // vered: (01-16-04) added this code to create the desired number of
            // bins. not too few and not too many.

            double up;
            int idx;
            Vector bounds = new Vector();

            for (idx = 0, up = mins[i]; up < maxes[i]; idx++, up += intrval) {
               bounds.add(idx, new Double(up));
            }

            // System.out.println("column " + i + " max " + maxes[i] + " min "
            // + mins[i] + " num " + num+ " original " +(
            // maxes[i]-mins[i])/intrval + " interval " + intrval);

            // vered: (01-16-04)replaced *num* with *bounds.size()*
            double[] binMaxes = new double[bounds.size()];

            binMaxes[0] = /*mins[i]*/ ((Double) bounds.get(0)).doubleValue();


            // add the first bin manually ANCA BinDescriptor nbd =
            // createMinNumericBinDescriptor(colIdx[i],binMaxes[0]);
            BinDescriptor nbd =
               BinDescriptorFactory.createMinNumericBinDescriptor(colIdx[i],
                                                                  binMaxes[0],
                                                                  nf, tbl);
            addItemToBinList(nbd);

            for (int j = 1; j < binMaxes.length; j++) {

               // vered: (01-16-04)replaced *binMaxes[k - 1] + intrval* with
               // *((Double)bounds.get(k)).doubleValue()*
               binMaxes[j] = /*binMaxes[k - 1] + intrval*/
                  ((Double) bounds.get(j)).doubleValue();

               // System.out.println("binMax j " + binMaxes[j] + " " + j); now
               // create the BinDescriptor and add it to the bin list ANCA nbd =
               // createNumericBinDescriptor(colIdx[i], binMaxes[j - 1],
               // binMaxes[j]);
               nbd =
                  BinDescriptorFactory.createNumericBinDescriptor(colIdx[i],
                                                                  binMaxes[j -
                                                                           1],
                                                                  binMaxes[j],
                                                                  nf, tbl);
               addItemToBinList(nbd);
            }

            // ANCA nbd = createMaxNumericBinDescriptor(colIdx[i],
            // binMaxes[binMaxes.length- 1]);
            nbd =
               BinDescriptorFactory.createMaxNumericBinDescriptor(colIdx[i],
                                                                  binMaxes[binMaxes.length -
                                                                           1],
                                                                  nf, tbl);
            addItemToBinList(nbd);
         } // end for
      } // end method addFromInterval

      /**
       * Add bins given a weight. This will attempt to make bins with an equal
       * number of tallies in each.
       */
      private void addFromWeight() {
         int[] colIdx = getSelectedNumericIndices();

         // vered:
         if (colIdx.length == 0) {
            ErrorDialog.showDialog("You must select an attribute to bin.",
                                   "Error");

            return;
         }

         if (!checkDuplicateNumericBins(colIdx)) {
            return;
         }

         // the weight is the number of items in each bin
         String txt = weightField.getText();
         int weight;

         try {
            weight = Integer.parseInt(txt);

            // vered:
            if (weight <= 0) {
               throw new NumberFormatException();
            }

         } catch (NumberFormatException e) {

            // vered:
            ErrorDialog.showDialog("Must specify a positive integer", "Error");

            return;
         }

         // we need to sort the data, but do not want to sort the
         // actual column, so we get a copy of the data
         for (int i = 0; i < colIdx.length; i++) {

            // NumericColumn nc = (NumericColumn)table.getColumn(colIdx[i]);
            // ANCA added support for eliminating missing values when setting
            // interval limits
            int missing = 0;

            if (tbl.getColumn(colIdx[i]).hasMissingValues()) {
               missing = tbl.getColumn(colIdx[i]).getNumMissingValues();
            }


            double[] data = new double[tbl.getNumRows() - missing];


            // vered: (01-20-04) commented out next code line and made k the
            // exclusive index of data and j of tbl so the loop won't end before
            // all non-missing values from tbl are copied into data. int k=0; k
            // index into data, j index into tbl.
            for (
                 int k = 0, j = 0;
                    k < data.length && j < tbl.getNumRows();
                    j++) {

               if (missing > 0) {

                  if (!tbl.isValueMissing(j, colIdx[i])) {
                     data[k++] = tbl.getDouble(j, colIdx[i]);

                     // System.out.println("data for k-1 = " + (k-1) + " is " +
                     // data[k-1]);
                  }
               } else {
                  data[k++] = tbl.getDouble(j, colIdx[i]);
                  // System.out.println("data for j = " + j + " is " + data[j]);
               }
            }

            // sort it
            Arrays.sort(data);


            ArrayList list = new ArrayList();
            // now find the bin maxes... loop through the sorted data.  the next
            // max will lie at data[curLoc+weight] items

            // vered - changed curIdx from 0 to -1. this way, first bin won't
            // be too large.
            int curIdx = -1;

            while (curIdx < data.length - 1) {


               curIdx += weight;


               if (curIdx > data.length - 1) {
                  curIdx = data.length - 1;
               }

               // now loop until the next unique item is found
               boolean done = false;

               if (curIdx == data.length - 1) {
                  done = true;
               }

               // vered - debug
               myLogger.debug("curIdx = " + curIdx +
                       " and points to element " +
                       data[curIdx]);
               // end debug

               while (!done) {

                  if (data[curIdx] != data[curIdx + 1]) {
                     done = true;
                  } else {
                     curIdx++;
                  }

                  if (curIdx == data.length - 1) {
                     done = true;
                  }
               }

               // now we have the current index
               // add the value at this index to the list
               Double dbl = new Double(data[curIdx]);


               list.add(dbl);
            } // end while

            // System.out.println("BEFORE");
            double[] binMaxes = new double[list.size()];

            for (int j = 0; j < binMaxes.length; j++) {

               binMaxes[j] = ((Double) list.get(j)).doubleValue();
               // System.out.println("binmaxes for j = " + j + " is " +
               // binMaxes[j]);
            }

            if (binMaxes.length < 2) {
               BinDescriptor nbd =
                  BinDescriptorFactory.createMinMaxBinDescriptor(colIdx[i],
                                                                 tbl);
               addItemToBinList(nbd);
            } else { // binMaxes has more than one element

               // add the first bin manually ANCA BinDescriptor nbd =
               // createMinNumericBinDescriptor(colIdx[i],binMaxes[0]);
               BinDescriptor nbd =
                  BinDescriptorFactory.createMinNumericBinDescriptor(colIdx[i],
                                                                     binMaxes[0],
                                                                     nf, tbl);
               addItemToBinList(nbd);

               for (int j = 1; j < binMaxes.length - 1; j++) {

                  // now create the BinDescriptor and add it to the bin list
                  // ANCA nbd = createNumericBinDescriptor(colIdx[i], binMaxes[j
                  // - 1], binMaxes[j]);
                  nbd =
                     BinDescriptorFactory.createNumericBinDescriptor(colIdx[i],
                                                                     binMaxes[j -
                                                                              1],
                                                                     binMaxes[j],
                                                                     nf, tbl);
                  addItemToBinList(nbd);
               }

               // ANCA nbd = createMaxNumericBinDescriptor(colIdx[i],
               // binMaxes[binMaxes.length- 2]); if (binMaxes.length>2)
               nbd =
                  BinDescriptorFactory.createMaxNumericBinDescriptor(colIdx[i],
                                                                     binMaxes[binMaxes.length -
                                                                              2],
                                                                     nf, tbl);

               // else nbd =
               // BinDescriptorFactory.createMaxNumericBinDescriptor(colIdx[i],
               // binMaxes[0],nf,tbl);
               addItemToBinList(nbd);
            } // end if
         } // end for
      } // end method addFromWeight

      /**
       * Add a bin
       *
       * @param bd bin to add
       */
      private void addItemToBinList(BinDescriptor bd) {
         binListModel.addElement(bd);
      }

      /**
       * Add bins from a user-specified range.
       */
      private void addSpecifiedRange() {
         int[] colIdx = getSelectedNumericIndices();

         // vered:
         if (colIdx.length == 0) {
            ErrorDialog.showDialog("You must select an attribute to bin.",
                                   "Error");

            return;
         }

         if (!checkDuplicateNumericBins(colIdx)) {
            return;
         }

         // specified range is a comma-separated list of bin maxes
         String txt = specRangeField.getText();

         // vered:
         if (txt == null || txt.length() == 0) {
            ErrorDialog.showDialog("Please enter a comma-separated sequence of\ninteger or floating-point values for\nthe endpoints of each bin. ",
                                   "Error");

            return;
         }

         ArrayList al = new ArrayList();
         StringTokenizer strTok = new StringTokenizer(txt, COMMA);
         double[] binMaxes = new double[strTok.countTokens()];
         int idx = 0;

         try {

            while (strTok.hasMoreElements()) {
               String s = (String) strTok.nextElement();
               binMaxes[idx++] = Double.parseDouble(s);
            }
         } catch (NumberFormatException e) {

            // vered
            ErrorDialog.showDialog("Please enter a comma-separated sequence of\ninteger or floating-point values for\nthe endpoints of each bin. ",
                                   "Error");

            return;
         }

         // now create and add the bins
         for (int i = 0; i < colIdx.length; i++) {

            // ANCA BinDescriptor nbd =
            // createMinNumericBinDescriptor(colIdx[i],binMaxes[0]);
            BinDescriptor nbd =
               BinDescriptorFactory.createMinNumericBinDescriptor(colIdx[i],
                                                                  binMaxes[0],
                                                                  nf, tbl);
            addItemToBinList(nbd);

            for (int j = 1; j < binMaxes.length; j++) {
               // now create the BinDescriptor and add it to the bin list

               // ANCA nbd = createNumericBinDescriptor(colIdx[i], binMaxes[j
               // -1], binMaxes[j]);
               nbd =
                  BinDescriptorFactory.createNumericBinDescriptor(colIdx[i],
                                                                  binMaxes[j -
                                                                           1],
                                                                  binMaxes[j],
                                                                  nf, tbl);
               addItemToBinList(nbd);
            }

            // ANCA nbd = createMaxNumericBinDescriptor(colIdx[i],
            // binMaxes[binMaxes.length- 1]);
            nbd =
               BinDescriptorFactory.createMaxNumericBinDescriptor(colIdx[i],
                                                                  binMaxes[binMaxes.length -
                                                                           1],
                                                                  nf, tbl);
            addItemToBinList(nbd);
         } // end for
      } // end method addSpecifiedRange

      /**
       * Add uniform range bins.
       */
      private void addUniRange() {
         int[] colIdx = getSelectedNumericIndices();

         // vered
         if (colIdx.length == 0) {
            ErrorDialog.showDialog("Must select an attribute to bin.", "Error");

            return;
         }

         if (!checkDuplicateNumericBins(colIdx)) {
            return;
         }

         // uniform range is the number of bins...
         String txt = uRangeField.getText();
         int num;

         // ...get this number
         try {
            num = Integer.parseInt(txt);

            // vered:
            if (num <= 1) {
               throw new NumberFormatException();
            }
         } catch (NumberFormatException e) {

            // vered:
            ErrorDialog.showDialog("Must specify an integer greater thatn 1.",
                                   "Error");

            return;
         }

         // find the maxes and mins
         double[] maxes = new double[colIdx.length];
         double[] mins = new double[colIdx.length];

         for (int i = 0; i < colIdx.length; i++) {
            maxes[i] = Double.NEGATIVE_INFINITY;
            mins[i] = Double.POSITIVE_INFINITY;
         }

         for (int i = 0; i < colIdx.length; i++) {

            // find the max and min and make equally spaced bins
            // NumericColumn nc = (NumericColumn)table.getColumn(colIdx[i]);
/*
 *              for (int j = 0; j < tbl.getNumRows(); j++) {
 *
 *              double  d = tbl.getDouble(j, colIdx[i]);
 *
 *             if (d > maxes[i])                     maxes[i] = d;
 * if (d < mins[i])                     mins[i] = d;             } */
// ANCA added support for missing  values
            ScalarStatistics ss =
               TableUtilities.getScalarStatistics(tbl, colIdx[i]);
            maxes[i] = ss.getMaximum();
            mins[i] = ss.getMinimum();

            double[] binMaxes = new double[num - 1];
            double interval = (maxes[i] - mins[i]) / (double) num;

            // add the first bin manually
            binMaxes[0] = mins[i] + interval;

            // ANCA BinDescriptor nbd =
            // createMinNumericBinDescriptor(colIdx[i],binMaxes[0]);
            BinDescriptor nbd =
               BinDescriptorFactory.createMinNumericBinDescriptor(colIdx[i],
                                                                  binMaxes[0],
                                                                  nf, tbl);
            addItemToBinList(nbd);

            for (int j = 1; j < binMaxes.length; j++) {
               binMaxes[j] = binMaxes[j - 1] + interval;

               //  System.out.println("bin Maxes " + binMaxes[j-1]); now create
               // the BinDescriptor and add it to the bin list ANCA nbd =
               // createNumericBinDescriptor(colIdx[i], binMaxes[j - 1],
               // binMaxes[j]);
               nbd =
                  BinDescriptorFactory.createNumericBinDescriptor(colIdx[i],
                                                                  binMaxes[j -
                                                                           1],
                                                                  binMaxes[j],
                                                                  nf, tbl);
               addItemToBinList(nbd);
            }

            // ANCA nbd = createMaxNumericBinDescriptor(colIdx[i],
            // binMaxes[binMaxes.length- 1]);
            nbd =
               BinDescriptorFactory.createMaxNumericBinDescriptor(colIdx[i],
                                                                  binMaxes[binMaxes.length -
                                                                           1],
                                                                  nf, tbl);
            addItemToBinList(nbd);
         } // end for
      } // end method addUniRange

       /**
        * Check to see if a bin already exists with newName
        * @param newName name to check
        * @return true if a bin already exists with newName
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
        * Check for duplicate numeric bins
        * @param newIndices column indices
        * @return true if duplicate bins exist for any of the column in newIndices
        */
      private boolean checkDuplicateNumericBins(int[] newIndices) {

         for (int bdi = 0; bdi < binListModel.getSize(); bdi++) {

            BinDescriptor bd = (BinDescriptor) binListModel.elementAt(bdi);

            for (int i = 0; i < newIndices.length; i++) {

               if (newIndices[i] == bd.column_number) {

                  JOptionPane.showMessageDialog(this,
                                                "You must remove all existing bins on attribute '" +
                                                tbl.getColumnLabel(newIndices[i]) +
                                                "' before creating new ones.",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE);

                  return false;

               }

            }

         }

         return true;

      } // end method checkDuplicateNumericBins

      /**
       * Create a textual bin
       *
       * @param  idx  column index
       * @param  name name of bin
       * @param  sel  strings that fall into this bin
       *
       * @return put your documentation comment here.
       */
      private BinDescriptor createTextualBin(int idx, String name,
                                             Object[] sel) {
         String[] vals = new String[sel.length];

         for (int i = 0; i < vals.length; i++) {
            vals[i] = sel[i].toString();
         }

         return new TextualBinDescriptor(idx, name, vals,
                                         tbl.getColumnLabel(idx));
      }

      /**
       * Get the help string
       *
       * @return help info about this module
       */
      private String getHelpString() {
         StringBuffer sb = new StringBuffer();
         sb.append("<html><body><h2>Bin Attributes</h2>");
         sb.append("This module allows a user to interactively bin data from a table. ");
         sb.append("Scalar data can be binned in four ways:<ul>");
         sb.append("<li><b>Uniform range</b><br>");
         sb.append("Enter a positive integer value for the number of bins. Bin Attributes will ");
         sb.append("divide the binning range evenly over these bins.");
         sb.append("<br><li><b>Specified range</b>:<br>");
         sb.append("Enter a comma-separated sequence of integer or floating-point values ");
         sb.append("for the endpoints of each bin.");
         sb.append("<br><li><b>Bin Interval</b>:<br>");
         sb.append("Enter an integer or floating-point value for the width of each bin.");
         sb.append("<br><li><b>Uniform Weight</b>:<br>");
         sb.append("Enter a positive integer value for even binning with that number in each bin.");
         sb.append("</ul>");
         sb.append("To bin scalar data, select attributes from the \"Scalar Attributes\" ");
         sb.append("selection area (top left) and select a binning method by entering a value ");
         sb.append("in the corresponding text field and clicking \"Add\". Data can ");
         sb.append("alternately be previewed in histogram form by clicking the corresponding ");
         sb.append("\"Show\" button (this accepts, but does not require, a value in the text field). ");
         sb.append("Uniform weight binning has no histogram (it would always look the same).");
         sb.append("<br><br>To bin nominal data, click the \"Nominal\" tab (top left) to bring ");
         sb.append("up the \"Nominal Attributes\" selection area. Click on an attibute to show a list ");
         sb.append("of unique nominal values in that attribute in the \"Unique Values\" area below. ");
         sb.append("Select one or more of these values and click the right arrow button to group ");
         sb.append("these values. They can then be assigned a collective name as before.");
         sb.append("<br><br>To assign a name to a particular bin, select that bin in ");
         sb.append("the \"Current Bins\" selection area (top right), enter a name in ");
         sb.append("the \"Name\" field below, and click \"Update\". To bin the data and ");
         sb.append("output the new table, click \"Done\".");
         sb.append("</body></html>");

         return sb.toString();
      } // end method getHelpString

      /**
       * Get the range of the first selected column.
       *
       * @return get the range of the first selected column.
       */
      private double getInterval() {

// !:
// int colIdx = numericColumnLabels.getSelectedIndex();
         int colIdx =
            ((Integer) columnLookup.get(numericColumnLabels.getSelectedValue()))
               .intValue();
         double max = Double.NEGATIVE_INFINITY;
         double min = Double.POSITIVE_INFINITY;

         for (int i = 0; i < tbl.getNumRows(); i++) {
            double d = tbl.getDouble(i, colIdx);

            if (d < min) {
               min = d;
            }

            if (d > max) {
               max = d;
            }
         }

         return max - min;
      }

      /**
       * Get the column indices of the selected numeric columns.
       *
       * @return get the column indices of the selected numeric columns.
       */
      private int[] getSelectedNumericIndices() {
         Object[] setVals = numericColumnLabels.getSelectedValues();
         int[] colIdx = new int[setVals.length];

         for (int i = 0; i < colIdx.length; i++) {
            colIdx[i] = ((Integer) columnLookup.get(setVals[i])).intValue();
         }

         return colIdx;
      }

      /**
       * Set selected numeric index
       *
       * @param index new index
       */
      private void setSelectedNumericIndex(int index) {
         numericColumnLabels.setSelectedIndex(index);
      }

       /**
        * Generate a Set of the unique values in col
        * @param col column of interest
        * @return Set of the unique values
        */
      private HashSet uniqueValues(int col) {

         // count the number of unique items in this column
         HashSet set = new HashSet();

         for (int i = 0; i < tbl.getNumRows(); i++) {

            // vered - added thsi condition, only if the value is not missing
            // then the unique value will be added.
            if (!tbl.isValueMissing(i, col)) {
               String s = tbl.getString(i, col);

               if (!set.contains(s)) {
                  set.add(s);
               }
            } // if value is not missing
         } // for i

         return set;
      }

       /**
        * validate the bins
        * @param newBins list model containing bins
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
         numericColumnLabels = new JList();
         numericColumnLabels.setModel(new DefaultListModel());

         // uniform range
         JOutlinePanel urangepnl = new JOutlinePanel("Uniform Range");
         uRangeField = new JTextField(5);

         JButton addURange = new JButton("Add");
         addURange.addActionListener(new AbstractAction() {

               public void actionPerformed(ActionEvent e) {
                  addUniRange();
                  uRangeField.setText(EMPTY);
               }
            });

         JButton showURange = new JButton("Show");
         showURange.addActionListener(new AbstractAction() {

               public void actionPerformed(ActionEvent e) {
                  HashMap colLook = new HashMap();

                  for (int i = 0; i < tbl.getNumColumns(); i++) {

                     if (tbl.isColumnNumeric(i)) {
                        colLook.put(tbl.getColumnLabel(i), new Integer(i));
                     }
                  }

                  String txt = uRangeField.getText();

                  // vered: replacing if else with try catch
                  // if(txt != null && txt.length() != 0)
                  try {
                     int i = Integer.parseInt(txt); // if this is successful
                                                    // then it is safe to go on
                                                    // with the preview

                     if (i <= 1) {
                        throw new NumberFormatException();
                     }


                     String selCol =
                        (String) numericColumnLabels.getSelectedValue();

                     if (selCol == null) {
                        throw new NullPointerException();
                     }

                     final Histogram H =
                        new UniformHistogram(new TableBinCounts(tbl),
                                             uRangeField.getText(), colLook,
                                             selCol);
                     JD2KFrame frame = new JD2KFrame("Uniform Range");
                     frame.getContentPane().setLayout(new GridBagLayout());
                     Constrain.setConstraints(frame.getContentPane(), H, 0,
                                              0, 3, 1, GridBagConstraints.BOTH,
                                              GridBagConstraints.CENTER,
                                              1, 1);

                     final JButton uniformAdd = new JButton("Add");
                     Constrain.setConstraints(frame.getContentPane(),
                                              new JLabel(""),
                                              0, 1, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.SOUTHWEST,
                                              .33, 0);
                     Constrain.setConstraints(frame.getContentPane(),
                                              uniformAdd,
                                              1, 1, 1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.SOUTH,
                                              .34, 0);
                     Constrain.setConstraints(frame.getContentPane(),
                                              new JLabel(""),
                                              2, 1, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.SOUTHEAST,
                                              .33, 0);
                     uniformAdd.addActionListener(new AbstractAction() {
                           final JSlider uniformSlider = H.getSlider();

                           public void actionPerformed(ActionEvent e) {
                              uRangeField.setText(Integer.toString(uniformSlider
                                                                      .getValue()));
                              numericColumnLabels.clearSelection();
                              setSelectedNumericIndex(H.getSelection());
                              addUniRange();
                              uRangeField.setText(EMPTY);
                           }
                        });
                     frame.pack();
                     frame.setVisible(true);
                  } // try

                  // else
                  catch (NumberFormatException nfe) {

                     // message dialog...must specify range
                     ErrorDialog.showDialog("Must specify a valid range - an integer greater than 1.",
                                            "Error");
                     uRangeField.setText(EMPTY);
                  } catch (NullPointerException npe) {
                     ErrorDialog.showDialog("You must select an attribute to bin.",
                                            "Error");
                  }
                  /*
                   * final JSlider uniformSlider = H.getSlider();
                   * uniformSlider.addChangeListener(new ChangeListener() {
                   * public void stateChanged(ChangeEvent e) {
                   * uRangeField.setText(Integer.toString(uniformSlider.getValue()));
                   * } });
                   */

               } // end method actionPerformed
            });
         urangepnl.setLayout(new GridBagLayout());
         Constrain.setConstraints(urangepnl, new JLabel("Number of Bins"),
                                  0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Box b = new Box(BoxLayout.X_AXIS);
         b.add(uRangeField);
         b.add(addURange);
         b.add(showURange);
         Constrain.setConstraints(urangepnl, b, 1, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 1, 1);

         // specified range
         JOutlinePanel specrangepnl = new JOutlinePanel("Specified Range");
         specrangepnl.setLayout(new GridBagLayout());
         Constrain.setConstraints(specrangepnl, new JLabel("Range"), 0,
                                  0, 1, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         specRangeField = new JTextField(5);

         JButton addSpecRange = new JButton("Add");
         addSpecRange.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  addSpecifiedRange();
                  specRangeField.setText(EMPTY);
               }
            });

         JButton showSpecRange = new JButton("Show");
         showSpecRange.addActionListener(new AbstractAction() {

               public void actionPerformed(ActionEvent e) {

                  // @todo: add exception handling for illegal input
                  String txt = specRangeField.getText();

                  // vered: wrapped it all with try catch.
                  try {

                     if (txt == null || txt.length() == 0) {

                        // show message dialog
                        // vered
                        throw new IllegalArgumentException();

                        // ErrorDialog.showDialog("Must specify range.",
                        // "Error"); return;
                     }


                     HashMap colLook = new HashMap();

                     for (int i = 0; i < tbl.getNumColumns(); i++) {

                        if (tbl.isColumnNumeric(i)) {
                           colLook.put(tbl.getColumnLabel(i), new Integer(i));
                        }
                     } // for

                     JD2KFrame frame = new JD2KFrame("Specified Range");
                     String col =
                        (String) numericColumnLabels.getSelectedValue();

                     // vered:
                     if (col == null) {
                        throw new NullPointerException();
                     }

                     frame.getContentPane().add(new RangeHistogram(new TableBinCounts(tbl),
                                                                   /*Histogram.HISTOGRAM_RANGE,*/ specRangeField
                                                                      .getText(),
                                                                   colLook,
                                                                   col));
                     frame.pack();
                     frame.setVisible(true);
                  } // try
                  catch (NullPointerException npe) {
                     ErrorDialog.showDialog("You must select an attribute to bin.",
                                            "Error");
                  } catch (IllegalArgumentException iae) {
                     ErrorDialog.showDialog("Please enter a comma-separated sequence of\ninteger or floating-point values for\nthe endpoints of each bin. ",
                                            "Error");
                  }
               } // action performed.
            });

         Box b1 = new Box(BoxLayout.X_AXIS);
         b1.add(specRangeField);
         b1.add(addSpecRange);
         b1.add(showSpecRange);
         Constrain.setConstraints(specrangepnl, b1, 1, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 1, 1);

         // interval
         JOutlinePanel intervalpnl = new JOutlinePanel("Bin Interval");
         intervalpnl.setLayout(new GridBagLayout());
         intervalField = new JTextField(5);

         JButton addInterval = new JButton("Add");
         addInterval.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  addFromInterval();
                  intervalField.setText(EMPTY);
               }
            });

         JButton showInterval = new JButton("Show");
         showInterval.addActionListener(new AbstractAction() {

               public void actionPerformed(ActionEvent e) {
                  HashMap colLook = new HashMap();

                  for (int i = 0; i < tbl.getNumColumns(); i++) {

                     if (tbl.isColumnNumeric(i)) {
                        colLook.put(tbl.getColumnLabel(i), new Integer(i));
                     }
                  }

                  String txt = intervalField.getText();

                  double intrval;

                  try {
                     intrval = Double.parseDouble(txt);
                  } catch (NumberFormatException ex) {
                     ErrorDialog.showDialog("You must specify a valid, positive interval.",
                                            "Error");

                     return;
                  }

                  if (intrval <= 0) {
                     ErrorDialog.showDialog("You must specify a valid, positive interval.",
                                            "Error");

                     return;
                  }

                  // vered: inserting a try catch to deal with inllegal
                  // argument exceptions
                  try {

                     // if(txt != null && txt.length() != 0) {
                     if (txt == null || txt.length() == 0) {
                        throw new IllegalArgumentException("Must specify an interval");
                     }

                     String col =
                        (String) numericColumnLabels.getSelectedValue();

                     // vered: added this to prevent null pointer exception in
                     // init of histogram.
                     if (col == null) {
                        throw new NullPointerException();
                     }

                     final Histogram H =
                        new IntervalHistogram(new TableBinCounts(tbl),
                                              /*Histogram.HISTOGRAM_INTERVAL,*/ intervalField.getText(),
                                              colLook, col);
                     JD2KFrame frame = new JD2KFrame("Bin Interval");
                     frame.getContentPane().setLayout(new GridBagLayout());
                     Constrain.setConstraints(frame.getContentPane(), H, 0,
                                              0, 3, 1, GridBagConstraints.BOTH,
                                              GridBagConstraints.CENTER,
                                              1, 1);

                     final JButton intervalAdd = new JButton("Add");
                     Constrain.setConstraints(frame.getContentPane(),
                                              new JLabel(""),
                                              0, 1, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.SOUTHWEST,
                                              .33, 0);
                     Constrain.setConstraints(frame.getContentPane(),
                                              intervalAdd,
                                              1, 1, 1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.SOUTH,
                                              .34, 0);
                     Constrain.setConstraints(frame.getContentPane(),
                                              new JLabel(""),
                                              2, 1, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.SOUTHEAST,
                                              .33, 0);
                     intervalAdd.addActionListener(new AbstractAction() {
                           final JSlider intervalSlider = H.getSlider();

                         /**
                          * Invoked when an action occurs.
                          */
                         public void actionPerformed(ActionEvent e) {
                             int sel = H.getSelection();
                             numericColumnLabels.clearSelection();
                             setSelectedNumericIndex(sel);

                             double interval = getInterval();
                             intervalField.setText(Double.toString(H
                                     .getPercentage() *
                                     interval));
                             addFromInterval();
                             intervalField.setText(EMPTY);
                         }
                        });
                     frame.pack();
                     frame.setVisible(true);
                  } // try
                  catch (IllegalArgumentException iae) {
                     String str = iae.getMessage();

                     if (str == null || str.length() == 0) {
                        str = "You must specify a valid interval";
                     }

                     ErrorDialog.showDialog(str, "Error");
                  } catch (NullPointerException npe) {
                     ErrorDialog.showDialog("You must select an attribute to bin.",
                                            "Error");
                  }
                  /*vered - the if else replaced by try catch
                   * else { // message dialog...you must specify an interval
                   * ErrorDialog.showDialog("Must specify interval.",
                   * "Error");}*/
                  /*
                   * final JSlider intervalSlider = H.getSlider();
                   * intervalSlider.addChangeListener(new ChangeListener() {
                   * public void stateChanged(ChangeEvent e) {
                   * intervalField.setText(Integer.toString(intervalSlider.getValue()));
                   * } });
                   */

               } // end method actionPerformed
            });
         Constrain.setConstraints(intervalpnl, new JLabel("Interval"), 0,
                                  0, 1, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Box b2 = new Box(BoxLayout.X_AXIS);
         b2.add(intervalField);
         b2.add(addInterval);
         b2.add(showInterval);
         Constrain.setConstraints(intervalpnl, b2, 1, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 1, 1);

         // uniform weight
         JOutlinePanel weightpnl = new JOutlinePanel("Uniform Weight");
         weightpnl.setLayout(new GridBagLayout());
         weightField = new JTextField(5);

         JButton addWeight = new JButton("Add");
         addWeight.addActionListener(new AbstractAction() {

               /**
                * put your documentation comment here.
                *
                * @param e action event
                */
               public void actionPerformed(ActionEvent e) {
                  addFromWeight();
                  weightField.setText(EMPTY);
               }
            });

         // JButton showWeight = new JButton("Show");
         // showWeight.setEnabled(false);
         Constrain.setConstraints(weightpnl, new JLabel("Number in each bin"),
                                  0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Box b3 = new Box(BoxLayout.X_AXIS);
         b3.add(weightField);
         b3.add(addWeight);

         // b3.add(showWeight);
         Constrain.setConstraints(weightpnl, b3, 1, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 1, 1);

         // add all numeric components
         JPanel numpnl = new JPanel();
         numpnl.setLayout(new GridBagLayout());

         JScrollPane jsp = new JScrollPane(numericColumnLabels);

         // jsp.setColumnHeaderView(new JLabel("Numeric Columns"));
         Constrain.setConstraints(numpnl, jsp, 0, 0, 4, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(numpnl, urangepnl, 0, 1, 4, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(numpnl, specrangepnl, 0, 2, 4, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(numpnl, intervalpnl, 0, 3, 4, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(numpnl, weightpnl, 0, 4, 4, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);

         // textual bins
         JPanel txtpnl = new JPanel();
         txtpnl.setLayout(new GridBagLayout());

         textualColumnLabels = new JList();
         textualColumnLabels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         textualColumnLabels.addListSelectionListener(new TextualListener());
         textualColumnLabels.setModel(new DefaultListModel());

         // berkin
         JButton autobin = new JButton("Auto Bin");
         autobin.addActionListener(new AbstractAction() {

               public void actionPerformed(ActionEvent event) {
                  Object[] inputs =
                     (Object[]) textualColumnLabels.getSelectedValues();

                  for (int input = 0; input < inputs.length; input++) {
                     Integer integer =
                        (Integer) columnLookup.get((String) inputs[input]);
                     // String[] values = TableUtilities.uniqueValues(tbl,
                     // integer.intValue());
                     Object[] values = textUniqueModel.toArray();

                     for (int value = 0; value < values.length; value++) {
                        String[] bin = { values[value].toString() };
                        BinDescriptor descriptor =
                           BinDescriptorFactory.createTextualBin(integer
                                                                    .intValue(),
                                                                 values[value]
                                                                    .toString(),
                                                                 bin, tbl);
                        addItemToBinList(descriptor);
                     }

                     Object val = inputs[input];
                     int idx = ((Integer) columnLookup.get(val)).intValue();
                     HashSet set = uniqueColumnValues[idx];
                     textUniqueModel.removeAllElements();
                     textCurrentModel.removeAllElements();
                     set.clear();

                  }
               } // end method actionPerformed
            });

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
         jtp.add(numpnl, "Scalar");
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
              */
             public void actionPerformed(ActionEvent e) {
                 Object[] sel = textCurrentModel.toArray();

                 if (sel.length == 0) {
                     ErrorDialog.showDialog("You must select some nominal values to group.",
                             "Error");

                     return;
                 }

                 Object val = textualColumnLabels.getSelectedValue();
                 int idx = ((Integer) columnLookup.get(val)).intValue();

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

                 BinDescriptor bd = createTextualBin(idx, textualBinName,
                         sel);

                 HashSet set = uniqueColumnValues[idx];

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
         Constrain.setConstraints(txtpnl, jp3, 0, 0, 4, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.NORTHWEST, 1, .5);
         Constrain.setConstraints(txtpnl, autobin, 0, 1, 4, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(txtpnl, jop, 0, 2, 4, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.NORTHWEST, 1, .5);

         /*
          * Constrain.setConstraints(txtpnl, jp3, 0, 0, 4, 1,
          * GridBagConstraints.BOTH,
          * GridBagConstraints.WEST, 1, 1); Constrain.setConstraints(txtpnl,
          * jop, 0, 1, 4, 1, GridBagConstraints.HORIZONTAL,
          *                 GridBagConstraints.WEST, 1, 1);
          */

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
                         int idx = ((Integer) columnLookup.get(lbl)).intValue();
                         HashSet unique = uniqueColumnValues[idx];
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
                     HashSet unique = uniqueColumnValues[idx];
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
         bxl.add(jtp);
         bxl.add(bgpnl);

         JPanel buttonPanel = new JPanel();
         abort = new JButton("Abort");
         abort.addActionListener(new AbstractAction() {


             /**
              * Invoked when an action occurs.
              */
             public void actionPerformed(ActionEvent e) {
                 viewCancel();
             }
            });
         done = new JButton("Done");
         done.addActionListener(new AbstractAction() {


             /**
              * Invoked when an action occurs.
              */
             public void actionPerformed(ActionEvent e) {

                 // binIt(createInNewColumn.isSelected());
                 // if (validateBins(binListModel)) {
                 Object[] tmp = binListModel.toArray();
                 BinDescriptor[] bins = new BinDescriptor[tmp.length];

                 for (int i = 0; i < bins.length; i++) {
                     bins[i] = (BinDescriptor) tmp[i];
                 }

                 savedBins = new BinDescriptor[bins.length];

                 for (int i = 0; i < bins.length; i++) {
                     savedBins[i] = bins[i];
                 }

                 // ANCA               savedBins =
                 // BinningUtils.addMissingValueBins(tbl,savedBins);

                 BinTransform bt =
                         new BinTransform(tbl, savedBins,
                                 createInNewColumn.isSelected());

                 pushOutput(bt, 0);
                 viewDone("Done");
                 // }
             } // end method actionPerformed
            });

         JButton showTable = new JButton("Show Table");
         showTable.addActionListener(new AbstractAction() {


             /**
              * Invoked when an action occurs.
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
        * @param o The object that has been input.
        * @param id The index of the module input that been received.
        */
       public void setInput(Object o, int id) {
           tbl = (MutableTable) o;
           // tbl = (Table)o;

           // set column labels on the table if necessary...
           for (int i = 0; i < tbl.getNumColumns(); i++) {

               if (
                       tbl.getColumnLabel(i) == null ||
                               tbl.getColumnLabel(i).length() == 0) {
                   tbl.setColumnLabel("column_" + i, i);
               }
           }

           // clear all text fields and lists...
           curSelName.setText(EMPTY);
           textBinName.setText(EMPTY);
           uRangeField.setText(EMPTY);
           specRangeField.setText(EMPTY);
           intervalField.setText(EMPTY);
           weightField.setText(EMPTY);
           columnLookup = new HashMap();
           uniqueColumnValues = new HashSet[tbl.getNumColumns()];
           binListModel.removeAllElements();

           /* This was causing it to put the same bins in multiple times.
      * if (savedBins != null) { for (int i = 0; i < savedBins.length; i++)
      * {   binListModel.addElement(savedBins[i]); }}*/

           DefaultListModel numModel =
                   (DefaultListModel) numericColumnLabels.getModel();
           DefaultListModel txtModel =
                   (DefaultListModel) textualColumnLabels.getModel();
           numModel.removeAllElements();
           txtModel.removeAllElements();

           textCurrentModel.removeAllElements();
           textUniqueModel.removeAllElements();
           uniqueTextualIndex = 0;

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
               if (tbl.isColumnScalar(i)) {
                   numModel.addElement(tbl.getColumnLabel(i));
               } else { // if (table.getColumn(i) instanceof TextualColumn) {
                   txtModel.addElement(tbl.getColumnLabel(i));
                   uniqueColumnValues[i] = uniqueValues(i);
               }

           }

           if (savedBins != null && savedBins.length > 0) {

               for (int b = 0; b < savedBins.length; b++) {
                   binListModel.addElement(savedBins[b]);
               }

           }

           if (!validateBins(binListModel)) {
               binListModel.removeAllElements();
           }

           // finished...
           setup_complete = true;
       } // end method setInput

       /**
        * Listen for when the currently selected bin changes
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
      } // BinColumnsView$CurrentListener

       /**
        * A window to dispaly help text
        */
      private class HelpWindow extends JD2KFrame {

         /**
          * Constructor
          */
         public HelpWindow() {
            super("About Bin Attributes");

            JEditorPane ep = new JEditorPane("text/html", getHelpString());
            ep.setCaretPosition(0);
            getContentPane().add(new JScrollPane(ep));
            setSize(400, 400);
         }
      }

       /**
        * Listen for when the currently selected textual bin changes
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
                       int idx = ((Integer) columnLookup.get(lbl)).intValue();
                       HashSet unique = uniqueColumnValues[idx];
                       textUniqueModel.removeAllElements();
                       textCurrentModel.removeAllElements();

                       Iterator i = unique.iterator();

                       while (i.hasNext()) {
                           textUniqueModel.addElement(i.next());
                       }
                   }
               }
           }
      } // BinColumnsView$TextualListener
   } // BinColumnsView

   // headless conversion support
} // BinAttributes


/**
 * Given bin boundaries, tally up the bins for each column.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
class TableBinCounts implements BinCounts {

   //~ Static fields/initializers **********************************************

   /** constant used in minMaxes array access */
   static private final int MIN = 0;

   /** constant used in minMaxes array access */
   static private final int MAX = 1;

   //~ Instance fields *********************************************************

   /** table. */
   private Table table;

   /** mins and maxes */
   double[][] minMaxes;

   //~ Constructors ************************************************************

   /**
    * Constructor
    *
    * @param t table
    */
   public TableBinCounts(Table t) {
      table = t;
      minMaxes = new double[table.getNumColumns()][];

      for (int i = 0; i < minMaxes.length; i++) {

         if (table.isColumnNumeric(i)) {
            minMaxes[i] = new double[2];

            double max = Double.NEGATIVE_INFINITY;
            double min = Double.POSITIVE_INFINITY;

            // get the max and min
            for (int j = 0; j < table.getNumRows(); j++) {

               if (table.getDouble(j, i) < min) {
                  min = table.getDouble(j, i);
               }

               if (table.getDouble(j, i) > max) {
                  max = table.getDouble(j, i);
               }
            }

            minMaxes[i][MIN] = min;
            minMaxes[i][MAX] = max;
         }
      }
   }

   //~ Methods *****************************************************************

   /**
    * Get the counts for a column
    *
    * @param  col     column index
    * @param  borders bin borders
    *
    * @return int[] containing the counts, size is borders.length+1
    */
   public int[] getCounts(int col, double[] borders) {
      int[] counts = new int[borders.length + 1];

      // some redundancy here
      boolean found;

      for (int i = 0; i < table.getNumRows(); i++) {
         found = false;

         for (int j = 0; j < borders.length; j++) {

            if (table.getDouble(i, col) <= borders[j] && !found) {
               counts[j]++;
               found = true;

               break;
            }
         }

         if (!found) {
            counts[borders.length]++;
         }
      }

      return counts;
   }

   /**
    * Get the max for a column
    *
    * @param  col column
    *
    * @return max for the column
    */
   public double getMax(int col) { return minMaxes[col][MAX]; }

   /**
    * Get the min for a column
    *
    * @param  col column
    *
    * @return min for the column
    */
   public double getMin(int col) { return minMaxes[col][MIN]; }

   /**
    * Get the number of rows
    *
    * @return number of rows
    */
   public int getNumRows() { return table.getNumRows(); }

   /**
    * Get the sum of all elements in a column
    *
    * @param  col column
    *
    * @return sum of all elements in this column
    */
   public double getTotal(int col) {
      double tot = 0;

      for (int i = 0; i < table.getNumRows(); i++) {
         tot += table.getDouble(i, col);
      }

      return tot;
   }
} // end class TableBinCounts