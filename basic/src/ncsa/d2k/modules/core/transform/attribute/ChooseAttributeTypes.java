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

import ncsa.d2k.core.gui.ErrorDialog;
import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.d2k.userviews.swing.JUserPane;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import ncsa.d2k.modules.core.util.*;


/**
 * Allows the user to choose which columns of a table are scalar or nominal.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ChooseAttributeTypes extends HeadlessUIModule {

   //~ Instance fields *********************************************************

   /** names of the nominal columns. */
   private String[] nominalColumns;

   /** names of the scalar columns. */
   private String[] scalarColumns;

   //~ Methods *****************************************************************

   /**
    * Validate that the input table is in the same format as the last successful
    * execution.
    *
    * @return true if the input table is in the correct format
    *
    * @throws Exception when something goes wrong
    */
   private boolean validate() throws Exception {


      HashMap scalarMap;
      scalarMap = new HashMap();

      if (scalarColumns == null) {
         scalarColumns = new String[0];
      }

      if (nominalColumns == null) {
         nominalColumns = new String[0];
      }

      if (scalarColumns.length == 0 && nominalColumns.length == 0) {
         throw new Exception(this.getAlias() +
                             " has not been configured. Before running headless, run with the gui and configure the parameters.");
// return false;
      } // if length


      for (int i = 0; i < scalarColumns.length; i++) {
         scalarMap.put(scalarColumns[i].toUpperCase(), new Integer(i));
      }


      for (int i = 0; i < nominalColumns.length; i++) {

         if (scalarMap.containsKey(nominalColumns[i].toUpperCase())) {
            throw new Exception(this.getAlias() + ": Attribute " +
                                nominalColumns[i] +
                                " was set as both scalar and nominal. A column can be " +
                                "only either scalar or nominal, it cannot be both!\n");
         }
      }


      return true;
   } // validate


   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new DefineView(); }


   /**
    * The list of strings returned by this method allows the module to map the
    * results returned from the pier to the position dependent outputs. The
    * order in which the names appear in the string array is the order in which
    * to assign them to the outputs.
    *
    * @return a string array containing the names associated with the outputs in
    *         the order the results should appear in the outputs.
    */
   protected String[] getFieldNameMapping() { return null; }
   private D2KModuleLogger myLogger;
   
   public void beginExecution() {
	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   }

   /**
    * headless conversion support. If the table is in the same format as the
    * last successful execution, push out the same selected attributes.
    *
    * @throws Exception when something goes wrong.
    */
   public void doit() throws Exception {
      Table _table = (Table) pullInput(0);


      // creating a hash map of available columns: column name <-> column index.
      HashMap availableColumns = new HashMap();

      // validating that there is no intersection between the scalar and nominal
      // columns if validate returns true that means that some columns were
      // assigned the is nominal is scalar property. hence it is worth while
      // building the map
      if (validate()) {

         /*for (int i = 0; i < _table.getNumColumns(); i++)
          *availableColumns.put(_table.getColumnLabel(i), new Integer(i));*/
         availableColumns = StaticMethods.getAvailableAttributes(_table);
      }

      if (availableColumns.size() == 0) {
    	  myLogger.warn(getAlias() +
                  ": Warning - The input table has no columns. It will be output as is.");
         // pushOutput(_table, 0);
         // return;
      }


      // if validate returns false - it does not matter that the map is empty
      // because it means that the arrays of scalar and nominal columns are
      // of size zero.

      // going over the scalar columns.
      for (int i = 0; i < scalarColumns.length; i++) {

         if (availableColumns.containsKey(scalarColumns[i])) {
            int index =
               ((Integer) availableColumns.get(scalarColumns[i])).intValue();
            _table.setColumnIsScalar(true, index);
            _table.setColumnIsNominal(false, index);

            if (!_table.isColumnNumeric(index)) {
            	myLogger.warn(getAlias() + ": Column " + scalarColumns[i] +
                        " was selected as scalar, but " +
                        "this column is not numeric. Continuing anyway.");
            }

         } // if contains
         else {
            throw new Exception(getAlias() +
                                ": The table does not contain a column named " +
                                scalarColumns[i] +
                                ". Please reconfigure this module");
         }
      }


      // going over the nominal columns.
      for (int i = 0; i < nominalColumns.length; i++) {

         if (availableColumns.containsKey(nominalColumns[i])) {
            int index =
               ((Integer) availableColumns.get(nominalColumns[i])).intValue();
            _table.setColumnIsScalar(false, index);
            _table.setColumnIsNominal(true, index);

            if (_table.isColumnNumeric(index)) {
            	myLogger.warn(getAlias() + ": Column " + nominalColumns[i] +
                        " was selected as nominal, but " +
                        "this column is numeric. Continuing anyway.");
            }

         } // if contains
         else {
            throw new Exception(getAlias() +
                                ": The table does not contain a column named " +
                                nominalColumns[i] +
                                ". Please reconfigure this module");
         }
      }

      pushOutput(_table, 0);

   } // doit


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
            return "The table to choose field types from.";

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
      } else {
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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String info = "<p>Overview: ";
      info +=
         "This module allows the user to choose which columns/attributes of a table are scalar or nominal.";
      info += "</p><p>Detailed Description: ";
      info +=
         "This module outputs a Table with the attributes assigned to be either scalar or nominal. The user has the power to mark a numeric (e.g double) attribute as nominal and a string type attribute as scalar. Extreme care must be exercised when making these assignments, because the learning algorithms use these assignments.  ";
      info += "</p><p>Data Handling: ";
      info +=
         "This module does not modify the data in the table, but it does assign attributes to be either scalar or nominal.";

      return info;
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Choose Attribute Types"; }

   /**
    * Get the names of the nominal columns.
    *
    * @return get the names of the nominal columns.
    */
   public Object[] getNominalColumns() { return nominalColumns; }


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
            return "The Table with nominal and scalar types assigned.";

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

      if (i == 0) {
         return "Table";
      } else {
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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return types;
   }

   /**
    * Get the names of the scalar columns.
    *
    * @return get the names of the scalar columns.
    */
   public Object[] getScalarColumns() { return scalarColumns; }

   /**
    * Set the names of the nominal columns.
    *
    * @param nominal new names of the nominal columns
    */
   public void setNominalColumns(Object[] nominal) {
      nominalColumns = new String[nominal.length];


      for (int i = 0; i < nominal.length; i++) {
         nominalColumns[i] = (String) nominal[i];

      }
   }

   /**
    * Set the names of the scalar columns.
    *
    * @param scalar new names of the scalar columns
    */
   public void setScalarColumns(Object[] scalar) {

      scalarColumns = new String[scalar.length];

      for (int i = 0; i < scalar.length; i++) {
         scalarColumns[i] = (String) scalar[i];

      }
   }

   //~ Inner Classes ***********************************************************

   /**
    * The user view class.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class DefineView extends JUserPane implements ActionListener {

      /** abort button. */
      private JButton abort;

      /** done button. */
      private JButton done;

      /** button to move from nominal to scalar. */
      private JButton fromNomToScalar;

      /** button to move from scalar to nominal. */
      private JButton fromScalarToNom;

      /** maps column name to index. */
      private HashMap indexLookup;

      /** the module. */
      private ChooseAttributeTypes module;

      /** list of nominals. */
      private JList nominalList;

      /** list model for nominalList. */
      private DefaultListModel nominalListModel;

      /** list of scalars. */
      private JList scalarList;

      // private JCheckBoxMenuItem miColumnOrder;
      // private JCheckBoxMenuItem miAlphaOrder;

      // private JMenuBar menuBar;

      /** list model for scalarList. */
      private DefaultListModel scalarListModel;

      /** the table. */
      private Table table;

      /**
       * Add all the components.
       */
      private void addComponents() {
         JPanel back = new JPanel();

         scalarList = new JList();
         scalarListModel = new DefaultListModel();
         scalarList.setModel(scalarListModel);

         nominalList = new JList();
         nominalListModel = new DefaultListModel();
         nominalList.setModel(nominalListModel);

         JScrollPane leftScrollPane = new JScrollPane(scalarList);
         JScrollPane rightScrollPane = new JScrollPane(nominalList);

         JLabel inputLabel = new JLabel("Scalar Columns");
         inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
         leftScrollPane.setColumnHeaderView(inputLabel);

         JLabel outputLabel = new JLabel("Nominal Columns");
         outputLabel.setHorizontalAlignment(SwingConstants.CENTER);
         rightScrollPane.setColumnHeaderView(outputLabel);

         fromScalarToNom = new JButton(">");
         fromScalarToNom.addActionListener(this);
         fromNomToScalar = new JButton("<");
         fromNomToScalar.addActionListener(this);

         /*Box b1 = new Box(BoxLayout.Y_AXIS);
          * b1.add(b1.createGlue()); b1.add(fromScalarToNom);
          * b1.add(fromNomToScalar); b1.add(b1.createGlue());
          */
         JPanel b1 = new JPanel();
         b1.setLayout(new GridLayout(2, 1));
         b1.add(fromScalarToNom);
         b1.add(fromNomToScalar);

         JPanel b2 = new JPanel();
         b2.add(b1);

         back.setLayout(new MirrorLayout(leftScrollPane, b2, rightScrollPane));
         back.add(leftScrollPane);
         back.add(b2);
         back.add(rightScrollPane);

         // back.setLayout(new GridBagLayout());

         // Constrain.setConstraints(back, leftScrollPane, 0, 0, 2, 1,
         // GridBagConstraints.BOTH, GridBagConstraints.CENTER, .5, 1);
         // Constrain.setConstraints(back, b2, 2, 0, 1, 1,
         // GridBagConstraints.NONE, GridBagConstraints.CENTER, 0, 0);
         // Constrain.setConstraints(back, rightScrollPane, 3, 0, 2, 1,
         // GridBagConstraints.BOTH, GridBagConstraints.CENTER, .5, 1);

         JPanel buttons = new JPanel();
         buttons.add(abort);
         buttons.add(done);

         this.add(back, BorderLayout.CENTER);
         this.add(buttons, BorderLayout.SOUTH);
      } // end method addComponents

      /**
       * Fill the scalar and nominal lists with the appropriate column names.
       */
      private void fillComponents() {
         indexLookup = new HashMap();
         scalarListModel.removeAllElements();
         nominalListModel.removeAllElements();

         LinkedList scalars = new LinkedList();
         LinkedList nominals = new LinkedList();

         for (int i = 0; i < table.getNumColumns(); i++) {
            String label = table.getColumnLabel(i);

            if (label.equals("")) {
               label = "Column " + i;
            }

            if (table.isColumnScalar(i)) {
               scalars.add(label);
            } else {
               nominals.add(label);
            }

            indexLookup.put(label, new Integer(i));
         }

         for (int i = 0; i < scalars.size(); i++) {
            scalarListModel.addElement(scalars.get(i));
         }

         for (int i = 0; i < nominals.size(); i++) {
            nominalListModel.addElement(nominals.get(i));
         }
      } // end method fillComponents

      /**
       * Set the column in the table to be sclaar or nominal, based on the UI
       * selections.
       */
      private void setFieldsInTable() {
         Enumeration e = scalarListModel.elements();


         while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            Integer ii = (Integer) indexLookup.get(s);
            table.setColumnIsScalar(true, ii.intValue());
            table.setColumnIsNominal(false, ii.intValue());
         }

         e = nominalListModel.elements();

         while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            Integer ii = (Integer) indexLookup.get(s);
            table.setColumnIsNominal(true, ii.intValue());
            table.setColumnIsScalar(false, ii.intValue());
         }

         // headless conversion
         setScalarColumns(scalarListModel.toArray());
         setNominalColumns(nominalListModel.toArray());
         // headless conversion
      }

      /**
       * Not used Make sure all choices are valid.
       *
       * @return not used Make sure all choices are valid.
       */
      protected boolean checkChoices() {

         /*if(outputList.getSelectedIndex() == -1){
          * JOptionPane.showMessageDialog(this, "You must select at least one
          * output", "Error", JOptionPane.ERROR_MESSAGE); return false; }
          * if(inputList.getSelectedIndex() == -1){
          * JOptionPane.showMessageDialog(this, "You must select at least one
          * input", "Error", JOptionPane.ERROR_MESSAGE); return false; } return
          * true;
          */
         return true;
      }


      /**
       * Invoked when an action occurs.
       *
       * @param e action event
       */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();

         if (src == abort) {
            module.viewCancel();
         } else if (src == done) {

            // if(checkChoices()) {
            setFieldsInTable();
            pushOutput(table, 0);
            viewDone("Done");
            // }
         }
         /*else if(src == miColumnOrder) {
          * String [] labels = orderedLabels(); miAlphaOrder.setState(false);
          * DefaultListModel dlm = (DefaultListModel)inputList.getModel();
          * dlm.removeAllElements(); for(int i = 0; i < labels.length; i++) {
          * dlm.addElement(labels[i]); } dlm =
          * (DefaultListModel)outputList.getModel(); dlm.removeAllElements();
          * for(int i = 0; i < labels.length; i++) { dlm.addElement(labels[i]);
          * } } else if(src == miAlphaOrder) { String [] labels =
          * alphabetizeLabels(); miColumnOrder.setState(false); DefaultListModel
          * dlm = (DefaultListModel)inputList.getModel();
          * dlm.removeAllElements(); for(int i = 0; i < labels.length; i++) {
          * dlm.addElement(labels[i]); } dlm =
          * (DefaultListModel)outputList.getModel(); dlm.removeAllElements();
          * for(int i = 0; i < labels.length; i++) { dlm.addElement(labels[i]);
          * }}*/
         else if (src == fromNomToScalar) {
            Object[] selected = nominalList.getSelectedValues();

            for (int i = 0; i < selected.length; i++) {
               boolean b = true;
               Integer idx = (Integer) indexLookup.get(selected[i]);

               if (!table.isColumnNumeric(idx.intValue())) {
                  b =
                     ErrorDialog.showQuery(selected[i] +
                                           " is not numeric. Continue?",
                                           "Warning");
               }

               if (b) {
                  nominalListModel.removeElement(selected[i]);
                  scalarListModel.addElement(selected[i]);
               }
            }
         } else if (src == fromScalarToNom) {
            Object[] selected = scalarList.getSelectedValues();

            for (int i = 0; i < selected.length; i++) {
               scalarListModel.removeElement(selected[i]);
               nominalListModel.addElement(selected[i]);
            }
         }
      } // end method actionPerformed

      /**
       * Make me at least this big.
       *
       * @return make me at least this big.
       */
      public Dimension getPreferredSize() { return new Dimension(400, 300); }


      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param v The module this view is associated with.
       */
      public void initView(ViewModule v) {
         module = (ChooseAttributeTypes) v;
         abort = new JButton("Abort");
         done = new JButton("Done");
         abort.addActionListener(this);
         done.addActionListener(this);

         /*menuBar = new JMenuBar();
          * JMenu m1 = new JMenu("File"); miColumnOrder = new
          * JCheckBoxMenuItem("Column Order");
          * miColumnOrder.addActionListener(this); miColumnOrder.setState(true);
          * miAlphaOrder = new JCheckBoxMenuItem("Alphabetical Order");
          * miAlphaOrder.addActionListener(this); miAlphaOrder.setState(false);
          * m1.add(miColumnOrder); m1.add(miAlphaOrder); menuBar.add(m1);
          */
         addComponents();
      }

      /*public Object getMenu() {
       * return menuBar;}*/


      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param o  The object that has been input.
       * @param id The index of the module input that been received.
       */
      public void setInput(Object o, int id) {

         if (id == 0) {
            table = (Table) o;
            fillComponents();
         }
      }
   } // end class DefineView

   /**
    * Symmetrically lays out three components reflected about a vertical axis.
    * The mirror component lays on the axis and the two image components lay on
    * either side. The image components are the same size and take up all the
    * space left from the mirror.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class MirrorLayout implements LayoutManager {
      /** buffer space */
      int gap = 1;
      /** left */
      Component leftimage;
       /** mirror */
      Component mirror;
       /** right */
      Component rightimage;

       /**
        * Constructor
        * @param leftimage left
        * @param mirror mirror
        * @param rightimage right
        */
      public MirrorLayout(Component leftimage, Component mirror,
                          Component rightimage) {
         this.leftimage = leftimage;
         this.mirror = mirror;
         this.rightimage = rightimage;
      }

       /**
        * If the layout manager uses a per-component string,
        * adds the component <code>comp</code> to the layout,
        * associating it
        * with the string specified by <code>name</code>.
        *
        * @param name the string to be associated with the component
        * @param component the component to be added
        */
       public void addLayoutComponent(String name, Component component) {
       }

       /**
        * Lays out the specified container.
        *
        * @param parent the container to be laid out
        */
       public void layoutContainer(Container parent) {
           Insets insets = parent.getInsets();
           Dimension dimension = mirror.getPreferredSize();

           int width = parent.getSize().width;
           int height = parent.getSize().height;

           int imagewidth =
                   (width - insets.left - insets.right - dimension.width) / 2;
           int imageheight = height - insets.top - insets.bottom;

           mirror.setBounds(insets.left +
                   (width - insets.left - insets.right) / 2 -
                   dimension.width / 2 + gap,
                   height / 2 - dimension.height / 2,
                   dimension.width, dimension.height);
           leftimage.setBounds(insets.left, insets.top, imagewidth, imageheight);
           rightimage.setBounds(insets.left + imagewidth + dimension.width +
                   2 * gap, insets.top, imagewidth, imageheight);
       }

       /**
        * Calculates the minimum size dimensions for the specified
        * container, given the components it contains.
        *
        * @param parent the component to be laid out
        * @see #preferredLayoutSize
        */
       public Dimension minimumLayoutSize(Container parent) {
           Insets insets = parent.getInsets();

           int imageminimumwidth =
                   Math.max(leftimage.getMinimumSize().width,
                           rightimage.getMinimumSize().width);
           int imageminimumheight =
                   Math.max(leftimage.getMinimumSize().height,
                           rightimage.getMinimumSize().height);

           int minimumwidth =
                   insets.left + mirror.getMinimumSize().width + 2 * gap +
                           2 * imageminimumwidth + insets.right;
           int minimumheight =
                   insets.top + mirror.getMinimumSize().height + 2 * gap +
                           2 * imageminimumheight + insets.bottom;

           return new Dimension(minimumwidth, minimumheight);
       }

       /**
        * Calculates the preferred size dimensions for the specified
        * container, given the components it contains.
        *
        * @param parent the container to be laid out
        * @see #minimumLayoutSize
        */
       public Dimension preferredLayoutSize(Container parent) {
           Insets insets = parent.getInsets();

           int imagepreferredwidth =
                   Math.max(leftimage.getPreferredSize().width,
                           rightimage.getPreferredSize().width);
           int imagepreferredheight =
                   Math.max(leftimage.getPreferredSize().height,
                           rightimage.getPreferredSize().height);

           int preferredwidth =
                   insets.left + mirror.getPreferredSize().width + 2 * gap +
                           2 * imagepreferredwidth + insets.right;
           int preferredheight =
                   insets.top + mirror.getPreferredSize().height + 2 * gap +
                           2 * imagepreferredheight + insets.bottom;

           return new Dimension(preferredwidth, preferredheight);
       }

       /**
        * Removes the specified component from the layout.
        *
        * @param component the component to be removed
        */
       public void removeLayoutComponent(Component component) {
       }
   } // end class MirrorLayout

   // headless conversion support


} // end class ChooseAttributeTypes
