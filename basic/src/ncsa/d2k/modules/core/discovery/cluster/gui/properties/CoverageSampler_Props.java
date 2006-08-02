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
package ncsa.d2k.modules.core.discovery.cluster.gui.properties;


import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.modules.core.discovery.cluster.hac.HAC;
import ncsa.d2k.modules.core.discovery.cluster.sample.CoverageSampler;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;


/**
 * <p>Title: CoverageSampler_Props</p>
 *
 * <p>Description: The Custom Properties Editor for CoverageSampler module</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class CoverageSampler_Props extends JPanel implements CustomModuleEditor,
                                                             ChangeListener,
                                                             ActionListener {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -2070406641789955747L;

   //~ Instance fields *********************************************************

   /**
    * If set to true the missing value check box appears in the layout of this
    * editor.
    */
   private boolean _showMVCheck = false;

   /**
    * If set to true - the verbose check box appears in the layout of this
    * editor.
    */
   private boolean _showVerbose = false;

   /** The parent module this editor belongs to. */
   private CoverageSampler _src = null;

   /**
    * If checked then the module this editor belongs to performs a check for
    * missing values in the input table (if missing values are found an
    * exception is thrown).
    */
   private JCheckBox m_checkMV = null;

   /** Label for the distance cutoff slider. */
   private JLabel m_distLbl = null;

   /**
    * Combo box component to host the available distance metrics. The avialable
    * distance metrics are defined in <code>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</codE>
    */
   private javax.swing.JComboBox m_distMetrics = null;

   /** Slider component for the HAC distance cutoff value. */
   private JSlider m_distSlide = null;

   /** Label for the distance metrics drop down list. */
   private JLabel m_dmLbl = null;

   /** Layout constraints of this editor's components. */
   private GridBagConstraints m_gbc = new GridBagConstraints();

   /** Layout manager for this editor. */
   private GridBagLayout m_gbl = new GridBagLayout();

   /** Text field for the user to enter number of clusters to be formed. */
   private JTextField m_numClust = null;


   /** Label for Number of CLusters text field. */
   private JLabel m_numClustLbl = null;

   /**
    * If checked then the module this editor belongs to performs in verbose
    * mode.
    */
   private JCheckBox m_verbose = null;

   //~ Constructors ************************************************************

   /**
    * Instantiates the editor according to the properties values in <code>src.
    * </code>
    *
    * @param src CoverageSampler The module this editor belongs to
    * @param ver boolean whether to include the verbosity checkbox in the layout
    * @param mvc boolean whether to include the missing value check box in the
    *            layout
    */
   public CoverageSampler_Props(CoverageSampler src, boolean ver, boolean mvc) {
      this._showVerbose = ver;
      this._showMVCheck = mvc;
      _src = src;
      init();
   }

   //~ Methods *****************************************************************

   /**
    * Initializes this editor according to the properties value in <code>_src.
    * </codE>
    */
   private void init() {
      setLayout(m_gbl);

      m_gbc.gridx = 0;
      m_gbc.gridy = 0;
      m_gbc.gridwidth = 1;

      // m_gbc.weightx = 1;
      // m_gbc.weighty = 1;
      m_gbc.fill = GridBagConstraints.VERTICAL;
      m_gbc.insets = new Insets(2, 2, 2, 2);

      m_gbc.anchor = GridBagConstraints.EAST;
      m_numClustLbl = new JLabel();
      m_numClustLbl.setText("Max samples: ");
      m_numClustLbl.setToolTipText("Enter integer value > 0 specifying max number of samples desired.");
      m_gbl.setConstraints(m_numClustLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_numClust =
         new JTextField(Integer.toString((_src.getMaxNumCenters() < 1)
                                         ? 500 : _src.getMaxNumCenters()), 7);
      m_numClust.setFont(new Font("Arial", Font.BOLD, 12));
      m_numClust.setToolTipText("Enter integer value > 0 specifying max number of samples desired.");
      m_gbl.setConstraints(m_numClust, m_gbc);


      m_gbc.gridy++;
      m_gbc.gridx = 0;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.gridwidth = 2;
      m_gbc.insets = new Insets(10, 2, 2, 2);
      m_distLbl = new JLabel();
      m_distLbl.setText("Distance Cutoff (% of Max):  " +
                        Integer.toString((_src.getCutOff() == 0)
                                         ? 1 : _src.getCutOff()));

      // m_distLbl.setFont(new Font("Arial", Font.BOLD,10));
      m_gbl.setConstraints(m_distLbl, m_gbc);

      m_gbc.gridy++;
      m_gbc.gridwidth = 2;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.insets = new Insets(2, 2, 10, 2);
      m_distSlide =
         new JSlider(JSlider.HORIZONTAL, 0, 100, (_src.getCutOff() == 0)
                                                 ? 1 : _src.getCutOff());
      m_distSlide.setMinorTickSpacing(10);
      m_distSlide.setMajorTickSpacing(25);
      m_distSlide.setPaintLabels(true);

//    for (Enumeration enum = m_distSlide.getLabelTable().elements();
// enum.hasMoreElements();){      ((JLabel)enum.nextElement()).setFont(new
// Font("Arial", Font.BOLD,10));    }
      m_distSlide.addChangeListener(this);
      m_gbl.setConstraints(m_distSlide, m_gbc);


      m_gbc.gridy++;
      m_gbc.gridx = 0;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_gbc.insets = new Insets(10, 2, 2, 2);
      m_gbc.gridwidth = 1;
      m_dmLbl = new JLabel();
      m_dmLbl.setText("Distance Metric: ");

      // m_dmLbl.setFont(new Font("Arial", Font.BOLD,10));
      m_gbl.setConstraints(m_dmLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      this.m_distMetrics = new JComboBox(HAC.s_DistanceMetricLabels);
      m_distMetrics.setEditable(false);

      // m_distMetrics.setFont(new Font("Arial", Font.BOLD,10));
      m_distMetrics.setToolTipText(HAC
                                      .s_DistanceMetricDesc[_src
                                                               .getDistanceMetric()]);
      m_distMetrics.setSelectedIndex(_src.getDistanceMetric());
      m_distMetrics.addActionListener(this);
      m_gbl.setConstraints(m_distMetrics, m_gbc);

      m_gbc.gridwidth = 2;

      m_gbc.gridx = 0;
      m_gbc.gridy++;
      m_gbc.insets = new Insets(4, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_verbose = new JCheckBox("Verbose", _src.getVerbose());

      // m_verbose.setFont(new Font("Arial", Font.BOLD,10));
      m_verbose.setToolTipText("Print verbose messages to console.");
      m_gbl.setConstraints(m_verbose, m_gbc);
      m_verbose.setVisible(_showVerbose);

      m_gbc.gridy++;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.insets = new Insets(4, 2, 2, 2);
      this.m_checkMV =
         new JCheckBox("Check Missing for Values",
                       _src.getCheckMissingValues());

      // m_checkMV.setFont(new Font("Arial", Font.BOLD,10));
      m_checkMV.setToolTipText("Screen for missing values in the input table.");
      m_gbl.setConstraints(m_checkMV, m_gbc);
      m_checkMV.setVisible(this._showMVCheck);

      add(m_numClustLbl);
      add(m_numClust);
      add(m_distLbl);
      add(m_distSlide);
      add(m_dmLbl);
      add(m_distMetrics);
      add(m_verbose);
      add(m_checkMV);

      this.setMinimumSize(this.getPreferredSize());
      this.validateTree();

   } // end method init

   /**
    * Called when the user had trigered one of the GUI components. If the event
    * caused a change of the value of one of the components then the
    * updateModule method is called and the parent module is updated.
    *
    * @param e ActionEvent trigered by the user
    */
   public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();

      if (src == this.m_distMetrics) {
         JComboBox cb = (JComboBox) src;
         m_distMetrics.setToolTipText(HAC
                                         .s_DistanceMetricDesc[cb
                                                                  .getSelectedIndex()]);
      }
   }


   /**
    * Handles state changed events related to the slider in the editor. Updates
    * the displayed string with the new selected value. All other change events
    * are ignored.
    *
    * @param e ChangeEvent A general state change event, if fired by the slider
    *          it is handled by this method, otherwise it is ignored.
    */
   public void stateChanged(ChangeEvent e) {

      if (e.getSource() == m_distSlide) {
         String disp =
            Integer.toString((m_distSlide.getValue() == 0)
                             ? 1 : m_distSlide.getValue());
         m_distSlide.setToolTipText(disp);
         m_distLbl.setText("Distance Cutoff (% of Max):  " + disp);
      }
   }

   /**
    * Called when the user changes values in one of the editor's components.
    *
    * @return boolean true if the parent module (<code>_src</code>) was updated
    *         successfully. returns otherwise false
    *
    * @throws Exception             If one of the values inserted by the user is
    *                               invalid (e.g. a string where a numeric value
    *                               is expected or a number that does not adhere
    *                               to a certain range)
    * @throws PropertyVetoException If one of the values inserted by the user is
    *                               invalid (e.g. a string where a numeric value
    *                               is expected or a number that does not adhere
    *                               to a certain range)
    */
   public boolean updateModule() throws Exception {

      int num = -1;

      try {
         num = Integer.parseInt(m_numClust.getText());
      } catch (Exception e) {
         throw new PropertyVetoException("Error in max number of samples field: " +
                                         e.getMessage(), null);
      }

      if (num < 1) {
         throw new PropertyVetoException("Max number of samples must be > 0",
                                         null);
      }

      if (_src != null) {

         if (this._showMVCheck) {
            _src.setCheckMissingValues(this.m_checkMV.isSelected());
         }

         if (this._showVerbose) {
            _src.setVerbose(m_verbose.isSelected());
         }

         _src.setCutOff((m_distSlide.getValue() == 0) ? 1
                                                      : m_distSlide.getValue());
         _src.setMaxNumCenters(num);
         _src.setDistanceMetric(this.m_distMetrics.getSelectedIndex());
      }

      return true;
   } // end method updateModule

} // end class CoverageSampler_Props
