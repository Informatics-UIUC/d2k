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
package ncsa.d2k.modules.core.transform.table.gui.properties;

/**
 * <p>Title: SampleTableRows_Props </p> <p>Description: </p> <p>Copyright:
 * Copyright (c) 2003</p> <p>Company: NCSA Automated Learning Group</p> @author
 * Xiaolei Li @version 1.0
 */


// ==============
// Java Imports
// ==============

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.modules.core.transform.table.SampleTableRows;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;


/**
 * A CustomModuleEditor for SampleTableRows
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class SampleTableRows_Props extends JPanel implements CustomModuleEditor,
                                                             ActionListener {

   //~ Instance fields *********************************************************


   /** the module */
   private SampleTableRows _src = null;

   /** grid bag constraints */
   private GridBagConstraints m_gbc = new GridBagConstraints();

   /** grid bag layout */
   private GridBagLayout m_gbl = new GridBagLayout();

   /** text field to enter sample size */
   private JTextField m_sample_size = null;

   /** label */
   private JLabel m_sample_size_label = null;

   /** label */
   private JLabel m_sampling_method_label = null;

   /** combo box with sampling methods. */
   private javax.swing.JComboBox m_sampling_methods = null;

   /** combo box with sample types. */
   private javax.swing.JComboBox m_sampling_size_type = null;

   /** label. */
   private JLabel m_sampling_size_type_label = null;

   /** text field to enter seed. */
   private JTextField m_seed = null;

   /** label. */
   private JLabel m_seed_label = null;

   /** Sampling method descriptions */
   private String[] sampling_method_desc =
   {
      "Randomly sample without " +
      "replacement.", "The first entries will be returned."
   };

   /** sampling method names */
   private String[] sampling_method_labels = { "Random", "Sequential" };

   /** sample size descriptions */
   private String[] sampling_size_type_desc =
   {
      "Absolute value of number of" +
      "samples.",
      "Percentage value of number of entries in the " +
      "original table."
   };

   /** sample size names. */
   private String[] sampling_size_type_labels = { "Absolute", "Percentage" };

   //~ Constructors ************************************************************

   /**
    * ================ Constructor(s) ================.
    *
    * @param src the module
    */
   public SampleTableRows_Props(SampleTableRows src) {
      _src = src;
      init();
   }

   //~ Methods *****************************************************************

   /**
    * Lay out the GUI
    */
   private void init() {
      setLayout(m_gbl);

      m_gbc.gridy = 0;
      m_gbc.gridwidth = 1;
      m_gbc.fill = GridBagConstraints.VERTICAL;
      m_gbc.insets = new Insets(2, 2, 2, 2);

      m_gbc.gridx = 0;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_sampling_method_label = new JLabel();
      m_sampling_method_label.setText("Sampling Method: ");
      m_sampling_method_label.setToolTipText("Select method of sampling.");
      m_gbl.setConstraints(m_sampling_method_label, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_sampling_methods = new JComboBox(sampling_method_labels);
      m_sampling_methods.setEditable(false);
      m_sampling_methods.setToolTipText(sampling_method_desc[_src
                                                                .getSamplingMethod()]);
      m_sampling_methods.setSelectedIndex(_src.getSamplingMethod());
      m_sampling_methods.addActionListener(this);
      m_gbl.setConstraints(m_sampling_methods, m_gbc);


      m_gbc.gridy++;
      m_gbc.insets = new Insets(4, 2, 2, 2);

      m_gbc.gridx = 0;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_seed_label = new JLabel();
      m_seed_label.setText("Seed: ");
      m_seed_label.setToolTipText("Random number generator seed.");
      m_gbl.setConstraints(m_seed_label, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_seed =
         new JTextField(Integer.toString((_src.getSeed() < 0) ? 1
                                                              : _src.getSeed()),
                        5);
      m_seed.setFont(new Font("Arial", Font.BOLD, 12));
      m_gbl.setConstraints(m_seed, m_gbc);


      m_gbc.gridy++;
      m_gbc.insets = new Insets(6, 2, 2, 2);

      m_gbc.gridx = 0;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_sampling_size_type_label = new JLabel();
      m_sampling_size_type_label.setText("Sample Size Type: ");
      m_sampling_size_type_label.setToolTipText("Select method of " +
                                                "calculating sampling size.");
      m_gbl.setConstraints(m_sampling_size_type_label, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_sampling_size_type = new JComboBox(sampling_size_type_labels);
      m_sampling_size_type.setEditable(false);
      m_sampling_size_type.setToolTipText(sampling_size_type_desc[_src
                                                                     .getSamplingMethod()]);
      m_sampling_size_type.setSelectedIndex(_src.getSamplingSizeType() - 10);
      m_sampling_size_type.addActionListener(this);
      m_gbl.setConstraints(m_sampling_size_type, m_gbc);


      m_gbc.gridy++;
      m_gbc.insets = new Insets(8, 2, 2, 2);

      m_gbc.gridx = 0;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_sample_size_label = new JLabel();
      m_sample_size_label.setText("Sample Size: ");
      m_sample_size_label.setToolTipText("Enter size of sample set.");
      m_gbl.setConstraints(m_sample_size_label, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_sample_size =
         new JTextField(Double.toString((_src.getSampleSize() < 0)
                                        ? 1 : _src.getSampleSize()), 5);
      m_sample_size.setFont(new Font("Arial", Font.BOLD, 12));
      m_gbl.setConstraints(m_sample_size, m_gbc);


      add(m_sampling_method_label);
      add(m_sampling_methods);
      add(m_seed_label);
      add(m_seed);
      add(m_sampling_size_type_label);
      add(m_sampling_size_type);
      add(m_sample_size_label);
      add(m_sample_size);

      if (_src.getSamplingMethod() == _src.RANDOM) {
         m_seed.setEnabled(true);
      } else {
         m_seed.setEnabled(false);
      }

      /*
       * if (_src.getSamplingSizeType() == _src.ABSOLUTE) {
       * m_sample_size.setText(Integer.toString(((int) _src.getSampleSize() < 0)
       * ? 1 : (int) _src.getSampleSize())); } else {
       * m_sample_size.setText(Double.toString((_src.getSampleSize() < 0) ? 0.05
       * : _src.getSampleSize())); }
       */

      this.setMinimumSize(this.getPreferredSize());
      this.validateTree();
   } // end method init

   /**
    * Handle action events
    *
    * @param e action event
    */
   public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();

      if (src == this.m_sampling_methods) {
         JComboBox cb = (JComboBox) src;
         m_sampling_methods.setToolTipText(sampling_method_desc[cb
                                                                   .getSelectedIndex()]);

         if (cb.getSelectedIndex() == _src.RANDOM) {
            m_seed.setEnabled(true);
            m_seed_label.setEnabled(true);
         } else {
            m_seed.setEnabled(false);
            m_seed_label.setEnabled(false);
         }
      } else if (src == this.m_sampling_size_type) {
         JComboBox cb = (JComboBox) src;
         m_sampling_size_type.setToolTipText(sampling_size_type_desc[cb
                                                                        .getSelectedIndex()]);

         /*
          * if (cb.getSelectedIndex() == _src.ABSOLUTE - 10) {
          * m_sample_size.setText(Integer.toString(((int) _src.getSampleSize() <
          * 0) ? 1 : (int) _src.getSampleSize())); } else {
          * m_sample_size.setText(Double.toString((_src.getSampleSize() < 0) ?
          * 0.05 : _src.getSampleSize())); }
          */
      }
   } // end method actionPerformed


    /**
     * This method is called when the property sheet is closed and the properties
     * should be committed.  This method will set the changed values in the module. If the
     * properties can not be set for any reason, return false
     *
     * @return true if any parameters actually changed, otherwise return false.
     * @throws Exception if for any reason, the parameters can not be set, throw an
     *                   exception with an appropriate message.
     */
    public boolean updateModule() throws Exception {
        int seed = -1;
        double N;

        try {
            seed = Integer.parseInt(m_seed.getText());
        } catch (Exception e) {
            throw new PropertyVetoException("Error in seed field: " +
                    e.getMessage(), null);
        }

        if (seed < 0) {
            throw new PropertyVetoException("Seed must be >= 0.", null);
        }

        try {
            N = Double.parseDouble(m_sample_size.getText());
        } catch (Exception e) {
            throw new PropertyVetoException("Error in sample size field: " +
                    e.getMessage(), null);
        }

        if (
                N > 1.0 &&
                        this.m_sampling_size_type.getSelectedIndex() + 10 ==
                                _src.PERCENTAGE) {
            throw new PropertyVetoException("Percentage value cannot be > 1.0.",
                    null);
        }

        if (
                N <= 0.0 &&
                        this.m_sampling_size_type.getSelectedIndex() + 10 ==
                                _src.PERCENTAGE) {
            throw new PropertyVetoException("Percentage value cannot be <= 0.0.",
                    null);
        } else if (
                N < 1.0 &&
                        this.m_sampling_size_type.getSelectedIndex() + 10 ==
                                _src.ABSOLUTE) {
            throw new PropertyVetoException("Absolute value cannot be < 1.", null);
        }

        if (_src != null) {
            _src.setSeed(seed);
            _src.setSampleSize(N);
            _src.setSamplingMethod(this.m_sampling_methods.getSelectedIndex());
            _src.setSamplingSizeType(this.m_sampling_size_type.getSelectedIndex() +
                    10);
        }

        return true;
    } // end method updateModule
} // end class SampleTableRows_Props
