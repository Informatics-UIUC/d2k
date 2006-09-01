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
package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.gui.Constrain;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * This panel displays the editable properties of the SimpleTestTrain modules.
 *
 * @author  Thomas Redman
 * @version $Revision$, $Date$
 */
public class SetParameterSpace extends JPanel implements CustomModuleEditor {

   //~ Instance fields *********************************************************

   /** editors. */
   private ParameterEditor[] editors;

   /** count. */
   int count;

   /** the parameter space generator. */
   AbstractParamSpaceGenerator spaceGen = null;

   //~ Constructors ************************************************************

   /**
    * Creates a new SetParameterSpace object.
    *
    * @param sg parameter space generator
    */
   SetParameterSpace(AbstractParamSpaceGenerator sg) {

      // this.setPreferredSize(new Dimension(600, 200));
      this.spaceGen = sg;

      ParameterSpace space = this.spaceGen.getCurrentSpace();
      count = space.getNumParameters();

      ParameterSpace defSpace = this.spaceGen.getDefaultSpace();
      this.setLayout(new GridLayout(count, 1));
      editors = new ParameterEditor[count];

      for (int i = 0; i < count; i++) {
         editors[i] =
            new ParameterEditor(space.getName(i), space.getMinValue(i),
                                space.getMaxValue(i), space.getDefaultValue(i),
                                space.getNumRegions(i), defSpace.getMinValue(i),
                                defSpace.getMaxValue(i),
                                defSpace.getDefaultValue(i),
                                defSpace.getNumRegions(i));
         this.add(editors[i]);
      }
   }

   //~ Methods *****************************************************************

   /**
    * Update the fields of the module.
    *
    * @return a string indicating why the properties could not be set, or null
    *         if successfully set.
    *
    * @throws Exception when something goes wrong
    */
   public boolean updateModule() throws Exception {
      double[] min = new double[count];
      double[] max = new double[count];
      double[] def = new double[count];
      int[] numRegions = new int[count];
      ParameterSpace space = this.spaceGen.getCurrentSpace();
      ParameterSpace mydef = this.spaceGen.getDefaultSpace();

      for (int i = 0; i < count; i++) {
         min[i] = editors[i].getMin();
         max[i] = editors[i].getMax();
         def[i] = editors[i].getDefault();
         numRegions[i] = editors[i].getNumRegions();
      }

      // check ot see if anything changed.
      boolean changed = false;

      for (int i = 0; i < count; i++) {

         if (min[i] != space.getMinValue(i)) {

            // if (min[i] < mydef.getMinValue(i))      throw new Exception
            // ("User minimum must not be less than the default settings.");
            changed = true;
         }

         if (max[i] != space.getMaxValue(i)) {

            // if (max[i] > mydef.getMaxValue(i))      throw new Exception
            // ("User maximum must not be greater than the default maximum.");
            changed = true;
         }

         if (def[i] != space.getDefaultValue(i)) {

            // if (def[i] < mydef.getMinValue(i) || def[i] >
            // mydef.getMaxValue(i))      throw new Exception ("Default must be
            // between the min and max of the factory settings.");
            changed = true;
         }

         if (numRegions[i] != space.getNumRegions(i)) {
            changed = true;
         }

         if (max[i] < min[i]) {
            throw new Exception("The max must be greater than or equal to the min.");
         }
      } // end for


      // if any changes, create a new parameter space and return true;
      if (changed) {
         String[] names = new String[space.getNumParameters()];
         int[] types = new int[space.getNumParameters()];

         for (int i = 0; i < names.length; i++) {
            names[i] = space.getName(i);
            types[i] = space.getType(i);
         }

         this.spaceGen.space = (ParameterSpace) space.getClass().newInstance();
         this.spaceGen.space.createFromData(names, min, max, def, numRegions,
                                            types);

         return true;
      } else {

         // no change.
         return false;
      }
   } // end method updateModule
} // end class SetParameterSpace

/**
 * Edit an individual parameter definition, including max, min, increment and
 * default.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
class ParameterEditor extends JPanel implements java.io.Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 2914050999282305447L;

   //~ Instance fields *********************************************************

   /** default. */
   private JTextField defaultText = new JTextField(6);

   /** increment. */
   private JTextField incrementText = new JTextField(6);

   /** max. */
   private JTextField maxText = new JTextField(6);

   /** min. */
   private JTextField minText = new JTextField(6);

   /** default default value. */
   double defaultDefault;

   /** default max value. */
   double defaultMax;

   /** default min value. */
   double defaultMin;

   /** default resolution value. */
   int defaultResolution;

   //~ Constructors ************************************************************

   /**
    * Creates a new ParameterEditor object.
    *
    * @param name    parameter name.
    * @param min     min.
    * @param max     max.
    * @param d       default
    * @param inc     increment
    * @param def_min default min
    * @param def_max default max
    * @param def_def default default
    * @param def_inc default increment
    */
   ParameterEditor(String name, double min, double max, double d, int inc,
                   double def_min, double def_max, double def_def,
                   int def_inc) {

      // Store the defaults.
      this.defaultMin = def_min;
      this.defaultMax = def_max;
      this.defaultDefault = def_def;
      this.defaultResolution = def_inc;

      this.setLayout(new GridBagLayout());

      // The first row will contain the editors.
      Constrain.setConstraints(this, new JLabel("Min"), 0, 0, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.NORTH, 0.0, 0.0);
      minText.setToolTipText("Minimum value for the parameter");
      minText.setText(Double.toString(min));
      Constrain.setConstraints(this, minText, 1, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTH, 0.33, 0.0);

      Constrain.setConstraints(this, new JLabel("Max"), 2, 0, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.NORTH, 0.0, 0.0);
      maxText.setToolTipText("Maxmimum value for the parameter");
      maxText.setText(Double.toString(max));
      Constrain.setConstraints(this, maxText, 3, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTH, 0.33, 0.0);

      Constrain.setConstraints(this, new JLabel("Default"), 4, 0, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.NORTH, 0.0, 0.0);
      defaultText.setToolTipText("Default value for the parameter");
      defaultText.setText(Double.toString(d));
      Constrain.setConstraints(this, defaultText, 5, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTH, 0.33, 0.0);

      Constrain.setConstraints(this, new JLabel("NumRegions"), 6, 0, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.NORTH, 0.0, 0.0);
      incrementText.setToolTipText("Number of regions between min and max values");
      incrementText.setText(Integer.toString(inc));
      Constrain.setConstraints(this, incrementText, 7, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTH, 0.33, 0.0);

      // Second row displays the defaults.
      Color dg = Color.gray;

      /*JLabel lab = new JLabel("Defaults:");
       *lab.setForeground(dg);*/
      JButton restore = new JButton("Reset");
      restore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               restoreFactorySettings();
            }
         });
      restore.setToolTipText("Restore factory defaults for all values");
      Constrain.setConstraints(this, restore, 0, 1, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.NORTH, 0.0, 0.0);

      JLabel dminText = new JLabel(Double.toString(defaultMin));
      dminText.setForeground(dg);
      Constrain.setConstraints(this, dminText, 1, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTH, 0.33, 0.0);

      JLabel dmaxText = new JLabel(Double.toString(defaultMax));
      dmaxText.setForeground(dg);
      Constrain.setConstraints(this, dmaxText, 3, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTH, 0.33, 0.0);

      JLabel ddText = new JLabel(Double.toString(defaultDefault));
      ddText.setForeground(dg);
      Constrain.setConstraints(this, ddText, 5, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTH, 0.33, 0.0);

      JLabel dresText = new JLabel(Integer.toString(defaultResolution));
      dresText.setForeground(dg);
      Constrain.setConstraints(this, dresText, 7, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTH, 0.33, 0.0);
      this.setBorder(new TitledBorder(new EtchedBorder(), name));
   }

   //~ Methods *****************************************************************

   /**
    * get the value for the current max.
    *
    * @return the value for the current max.
    *
    * @throws java.lang.NumberFormatException
    */
   double getDefault() throws java.lang.NumberFormatException {
      return Double.parseDouble(defaultText.getText());
   }

   /**
    * get the value for the current max.
    *
    * @return the value for the current max.
    *
    * @throws java.lang.NumberFormatException
    */
   double getMax() throws java.lang.NumberFormatException {
      return Double.parseDouble(maxText.getText());
   }

   /**
    * get the value for the current max.
    *
    * @return the value for the current max.
    *
    * @throws java.lang.NumberFormatException
    */
   double getMin() throws java.lang.NumberFormatException {
      return Double.parseDouble(minText.getText());
   }

   /**
    * get the value for the current max.
    *
    * @return the value for the current max.
    *
    * @throws java.lang.NumberFormatException
    */
   int getNumRegions() throws java.lang.NumberFormatException {
      return Integer.parseInt(incrementText.getText());
   }

   /**
    * Restore everything to its factory setting.
    */
   void restoreFactorySettings() {
      minText.setText(Double.toString(defaultMin));
      maxText.setText(Double.toString(defaultMax));
      defaultText.setText(Double.toString(defaultDefault));
      incrementText.setText(Integer.toString(defaultResolution));
   }
} // end class ParameterEditor
