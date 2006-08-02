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
import ncsa.d2k.modules.core.discovery.cluster.sample.ClusterParameterDefns;
import ncsa.d2k.modules.core.discovery.cluster.sample.KMeansParams;

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
 * <p>Title: KMeansParams_Props</p>
 *
 * <p>Description: The Custom Properties Editor for KMeansParams module</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class KMeansParams_Props extends JPanel
   implements CustomModuleEditor, ActionListener, ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -4803891456666929240L;

   //~ Instance fields *********************************************************

   /** The parent module this editor belongs to. */
   private KMeansParams _src = null;

   /** Label for the clustering methods drop down list. */
   private JLabel m_cmLbl = null;

   /**
    * Combo box component to host the available distance metrics. The avialable
    * distance metrics are defined in <code>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</codE>
    */
   private javax.swing.JComboBox m_distMetrics = null;

   /** Label for the distance metrics drop down list. */
   private JLabel m_dmLbl = null;

   /** Layout constraints of this editor's components. */
   private GridBagConstraints m_gbc = new GridBagConstraints();

   /** Layout manager for this editor. */
   private GridBagLayout m_gbl = new GridBagLayout();

   /** The text field to enter max number of iterations. */
   private JTextField m_max = null;

   /** The label for max number of iterations. */
   private JLabel m_maxLbl = null;

   /**
    * Combo box component to host the available clustering methods. The
    * avialable clustering methods are defined in <code>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</codE>
    */
   private javax.swing.JComboBox m_methods = null;

   /** Text field for the user to enter number of clusters to be formed. */
   private JTextField m_numClust = null;


   /** Label for Number of CLusters text field. */
   private JLabel m_numClustLbl = null;

   /**
    * Text field component for the user to enter the seed for the random number
    * generator used to select the sample set.
    */
   private JTextField m_seed = null;

   /** Label for the seed text filed. */
   private JLabel m_seedLbl = null;

   /**
    * Check box component for the user to select between sequential smapling
    * (where the first N rows of the table are selected) and random sampling
    * (where the seed is used to generate the sampling of N rows).
    */
   private JCheckBox m_useFirst = null;

   //~ Constructors ************************************************************

   /**
    * Instantiates the editor according to the properties values in <code>src.
    * </code>
    *
    * @param src KMeansParams The module this editor belongs to
    */
   public KMeansParams_Props(KMeansParams src) {
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
      m_gbc.fill = GridBagConstraints.VERTICAL;
      m_gbc.insets = new Insets(2, 2, 2, 2);

      m_gbc.gridwidth = 1;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_cmLbl = new JLabel();
      m_cmLbl.setText(CLUSTER_METHOD + ": ");
      m_cmLbl.setToolTipText("Select method to use in determining similarity between two clusters.");
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
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.EAST;
      m_seedLbl = new JLabel();
      m_seedLbl.setText(SEED + ": ");
      m_seedLbl.setToolTipText("Enter integer value >= 0 specifying random seed.");
      m_gbl.setConstraints(m_seedLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_seed =
         new JTextField(Integer.toString((_src.getSeed() < 0) ? 0
                                                              : _src.getSeed()),
                        5);
      m_seed.setFont(new Font("Arial", Font.BOLD, 12));
      m_seed.setToolTipText("Enter integer value >= 0 specifying random seed.");
      m_gbl.setConstraints(m_seed, m_gbc);

      m_gbc.gridwidth = 2;

      m_gbc.gridx = 0;
      m_gbc.gridy++;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_useFirst = new JCheckBox(USE_FIRST, _src.getUseFirst());
      m_useFirst.setToolTipText("If checked, use first rows in table as sample set.");
      m_gbl.setConstraints(m_useFirst, m_gbc);


      m_gbc.gridy++;
      m_gbc.gridx = 0;
      m_gbc.gridwidth = 1;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.EAST;
      m_numClustLbl = new JLabel();
      m_numClustLbl.setText(NUM_CLUSTERS + ": ");
      m_numClustLbl.setToolTipText("Enter integer value > 1 specifying number of clusters desired.");
      m_gbl.setConstraints(m_numClustLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_numClust =
         new JTextField(Integer.toString((_src.getNumClusters() < 2)
                                         ? 5 : _src.getNumClusters()), 5);
      m_numClust.setFont(new Font("Arial", Font.BOLD, 12));
      m_numClust.setToolTipText("Enter integer value > 1 specifying number of clusters desired.");
      m_gbl.setConstraints(m_numClust, m_gbc);

      m_gbc.gridy++;
      m_gbc.gridx = 0;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_gbc.gridwidth = 1;
      m_dmLbl = new JLabel();
      m_dmLbl.setText(DISTANCE_METRIC + ": ");
      m_dmLbl.setToolTipText("Select method to use in determining distance between two examples.");
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

      m_gbc.gridy++;
      m_gbc.gridx = 0;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.EAST;
      m_maxLbl = new JLabel();
      m_maxLbl.setText(MAX_ITERATIONS + ": ");

      // m_numClustLbl.setFont(new Font("Arial", Font.BOLD,10));
      m_maxLbl.setToolTipText("Enter integer value > 0 specifying maximum number of " +
                              "cluster assignment/refinement iterations.");
      m_gbl.setConstraints(m_maxLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_max =
         new JTextField(Integer.toString((_src.getMaxIterations() < 1)
                                         ? 5 : _src.getMaxIterations()), 5);
      m_max.setFont(new Font("Arial", Font.BOLD, 12));
      m_max.setToolTipText("Enter integer value > 0 specifying maximum number of " +
                           "cluster assignment/refinement iterations.");
      m_gbl.setConstraints(m_max, m_gbc);

      add(m_cmLbl);
      add(m_methods);
      add(m_numClustLbl);
      add(m_numClust);
      add(m_dmLbl);
      add(m_distMetrics);
      add(m_useFirst);
      add(m_seed);
      add(m_seedLbl);
      add(m_maxLbl);
      add(m_max);


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

      int num = -1;

      if (this.m_numClust.isEnabled()) {

         try {
            num = Integer.parseInt(m_numClust.getText());
         } catch (Exception e) {
            throw new PropertyVetoException("Error in " + NUM_CLUSTERS +
                                            " field: " + e.getMessage(), null);
         }

         if (num < 2) {
            throw new PropertyVetoException(NUM_CLUSTERS + " must be > 1.",
                                            null);
         }
      }

      int seed = -1;

      try {
         seed = Integer.parseInt(m_seed.getText());
      } catch (Exception e) {
         throw new PropertyVetoException("Error in " + SEED + " field: " +
                                         e.getMessage(), null);
      }

      if (seed < 0) {
         throw new PropertyVetoException(SEED + " must be >= 0.", null);
      }

      int maxit = -1;

      try {
         maxit = Integer.parseInt(m_max.getText());
      } catch (Exception e) {
         throw new PropertyVetoException("Error in " + MAX_ITERATIONS +
                                         " field: " +
                                         e.getMessage(), null);
      }

      if (maxit < 1) {
         throw new PropertyVetoException(MAX_ITERATIONS + " must be > 0.",
                                         null);
      }

      if (_src != null) {
         _src.setUseFirst(m_useFirst.isSelected());
         _src.setNumClusters(num);
         _src.setSeed(seed);
         _src.setMaxIterations(maxit);
         _src.setClusterMethod(this.m_methods.getSelectedIndex());
         _src.setDistanceMetric(this.m_distMetrics.getSelectedIndex());
      }

      return true;
   } // end method updateModule


} // end class KMeansParams_Props
