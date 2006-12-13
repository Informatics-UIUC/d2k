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
import ncsa.d2k.modules.core.discovery.cluster.sample.ClusterAssignment;
import ncsa.d2k.modules.core.discovery.cluster.sample.ClusterParameterDefns;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;


/**
 * <p>Title: ClusterAssignment_Props</p>
 *
 * <p>Description: The Custom Properties Editor for ClusterAssignment module</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */

public class ClusterAssignment_Props extends JPanel
   implements CustomModuleEditor, ActionListener, ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 2724993208969569897L;

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


   /** The module this editor belongs to. */
   private ClusterAssignment _src = null;


   /**
    * If checked then the module this editor belongs to performs a check for
    * missing values in the input table (if missing values are found an
    * exception is thrown).
    */
   private JCheckBox m_checkMV = null;

   /** The label for the clustering method combo box. */
   private JLabel m_cmLbl = null;

   /**
    * A combo box component for the various distance metrics. The avialable
    * distance metrics are defined in <code>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</codE>
    */
   private javax.swing.JComboBox m_distMetrics = null;

   /** The label for the distance metric combo box. */
   private JLabel m_dmLbl = null;

   /** The layout constraints to apply on this GUI component. */
   private GridBagConstraints m_gbc = new GridBagConstraints();

   /** The layout manager for this GUI component. */
   private GridBagLayout m_gbl = new GridBagLayout();

   /** The text field to enter max number of iterations. */
   private JTextField m_max = null;

   /** The label for max number of iterations. */
   private JLabel m_maxLbl = null;

   /**
    * A combo box component for the clustering methods. Clustering methods are
    * defined in <code>ncsa.d2k.modules.core.discovery.cluster.hac.HAC</codE>
    */
   private javax.swing.JComboBox m_methods = null;

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
    * @param src ClusterAssignment The module this editor belongs to
    * @param ver boolean whether to include the verbosity checkbox in the layout
    * @param mvc boolean whether to include the missing value check box in the
    *            layout
    */
   public ClusterAssignment_Props(ClusterAssignment src, boolean ver,
                                  boolean mvc) {
      this._showVerbose = ver;
      this._showMVCheck = mvc;
      _src = src;
      init();
   }

   //~ Methods *****************************************************************

   /**
    * Instantiates this editor components using the values of properties from
    * <code>_src.</code>
    */
   private void init() {
      setLayout(m_gbl);

      m_gbc.gridx = 0;
      m_gbc.gridy = 0;
      m_gbc.gridwidth = 1;
      m_gbc.fill = GridBagConstraints.VERTICAL;
      m_gbc.insets = new Insets(2, 2, 2, 2);

      m_gbc.gridwidth = 1;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_cmLbl = new JLabel();
      m_cmLbl.setText(CLUSTER_METHOD + ": ");
      m_cmLbl.setToolTipText("Select method to use in determining similarity" +
                             " between two clusters.");
      m_gbl.setConstraints(m_cmLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;

      m_methods = new JComboBox(HAC.s_ClusterMethodLabels);
      m_methods.setEditable(false);

      m_methods.setToolTipText(HAC.s_ClusterMethodDesc[_src.getClusterMethod()]);
      m_methods.setSelectedIndex(_src.getClusterMethod());
      m_methods.addActionListener(this);
      m_gbl.setConstraints(m_methods, m_gbc);

      m_gbc.gridy++;
      m_gbc.gridx = 0;
      m_gbc.insets = new Insets(4, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.EAST;
      m_maxLbl = new JLabel();
      m_maxLbl.setText(MAX_ITERATIONS + ": ");

      m_maxLbl.setToolTipText("Enter integer value > 0 specifying maximum" +
                              " number of " +
                              "cluster assignment/refinement iterations.");
      m_gbl.setConstraints(m_maxLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_max =
         new JTextField(Integer.toString((_src.getNumAssignments() < 1)
                                         ? 5 : _src.getNumAssignments()), 5);
      m_max.setFont(new Font("Arial", Font.BOLD, 12));
      m_maxLbl.setToolTipText("Enter integer value > 0 specifying maximum " +
                              "number of " +
                              "cluster assignment/refinement iterations.");
      m_gbl.setConstraints(m_max, m_gbc);

      m_gbc.gridy++;
      m_gbc.gridx = 0;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_gbc.insets = new Insets(10, 2, 2, 2);
      m_gbc.gridwidth = 1;
      m_dmLbl = new JLabel();
      m_dmLbl.setText(DISTANCE_METRIC + ": ");
      m_dmLbl.setToolTipText("Select method to use in determining distance" +
                             " between two examples.");

      m_gbl.setConstraints(m_dmLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      this.m_distMetrics = new JComboBox(HAC.s_DistanceMetricLabels);
      m_distMetrics.setEditable(false);

      m_distMetrics.setToolTipText(HAC
                                      .s_DistanceMetricDesc[_src
                                                               .getDistanceMetric()]);
      m_distMetrics.setSelectedIndex(_src.getDistanceMetric());
      m_distMetrics.addActionListener(this);
      m_gbl.setConstraints(m_distMetrics, m_gbc);

      m_gbc.gridwidth = 2;

      m_gbc.gridx = 0;
      m_gbc.gridy++;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.insets = new Insets(4, 2, 2, 2);
      this.m_checkMV = new JCheckBox(CHECK_MV, _src.getCheckMissingValues());

      m_checkMV.setToolTipText("Check for missing values in the table of " +
                               "examples.");
      m_gbl.setConstraints(m_checkMV, m_gbc);
      m_checkMV.setVisible(this._showMVCheck);

      m_gbc.gridy++;
      m_gbc.insets = new Insets(4, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_verbose = new JCheckBox(VERBOSE, _src.getVerbose());

      m_verbose.setToolTipText("Write verbose status information to console.");
      m_gbl.setConstraints(m_verbose, m_gbc);
      m_verbose.setVisible(_showVerbose);

      add(m_cmLbl);
      add(m_methods);
      add(m_maxLbl);
      add(m_max);
      add(m_dmLbl);
      add(m_distMetrics);
      add(m_checkMV);
      add(m_verbose);

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

      if (src == this.m_methods) {
         JComboBox cb = (JComboBox) src;
         m_methods.setToolTipText(HAC.s_ClusterMethodDesc[cb.getSelectedIndex()]);
      }

      if (src == this.m_distMetrics) {
         JComboBox cb = (JComboBox) src;
         m_distMetrics.setToolTipText(HAC
                                         .s_DistanceMetricDesc[cb
                                                                  .getSelectedIndex()]);
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

      int maxit = -1;

      try {
         maxit = Integer.parseInt(m_max.getText());
      } catch (Exception e) {
         throw new PropertyVetoException("Error in " + MAX_ITERATIONS +
                                         " field. Value should be an integer greater than zero.",
                                         null);
      }

      if (maxit < 1) {
         throw new PropertyVetoException(MAX_ITERATIONS + " must be > 0.",
                                         null);
      }

      if (_src != null) {

         if (this._showMVCheck) {
            _src.setCheckMissingValues(this.m_checkMV.isSelected());
         }

         if (this._showVerbose) {
            _src.setVerbose(m_verbose.isSelected());
         }

         _src.setNumAssignments(maxit);
         _src.setClusterMethod(this.m_methods.getSelectedIndex());
         _src.setDistanceMetric(this.m_distMetrics.getSelectedIndex());
      }

      return true;
   } // end method updateModule

} // end class ClusterAssignment_Props
