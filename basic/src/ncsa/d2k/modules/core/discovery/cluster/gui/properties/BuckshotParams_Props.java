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
import ncsa.d2k.modules.core.discovery.cluster.sample.BuckshotParams;
import ncsa.d2k.modules.core.discovery.cluster.sample.BuckshotParamSpaceGenerator;

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
 * <p>Title: BuckshotParams_Props</p>
 *
 * <p>Description: The Custom Properties Editor GUI for BuckshotParams Module
 * </p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class BuckshotParams_Props extends JPanel implements CustomModuleEditor,
                                                            ChangeListener,
                                                            ActionListener {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -3065085553325799005L;

   //~ Instance fields *********************************************************

   /**
    * If true then the user is to choose a distance threshold at which point
    * clustering will stop.
    */
   private boolean _initAuto = false;

   /**
    * The module this GUI component belongs to. This reference is needed so as
    * to update the module's properties with the values the user had set.
    */
   private BuckshotParams _src = null;

   /** A check box component to set the value of <code>_initAuto</codE>. */
   private JCheckBox m_auto = null;

   /** Whether <code>m_auto</code> is selected or not. */
   private boolean m_auto_sel = false;

   /** The label for the clustering method combo box. */
   private JLabel m_cmLbl = null;

   /** The label for the distance cutoff slider. */
   private JLabel m_distLbl = null;

   /**
    * A combo box component for the various distance metrics. The avialable
    * distance metrics are defined in <code>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</codE>
    */
   private javax.swing.JComboBox m_distMetrics = null;

   /**
    * A slider component to select the distance cutoff percentage to be used if
    * <code>m_auto</code> is true.
    */
   private JSlider m_distSlide = null;

   /** The label for the distance metric combo box. */
   private JLabel m_dmLbl = null;

   /** The layout constraints to apply to this GUI component. */
   private GridBagConstraints m_gbc = new GridBagConstraints();

   /** The layout manager for this GUI component. */
   private GridBagLayout m_gbl = new GridBagLayout();

   /** The text field to enter the maximum number of iterations. */
   private JTextField m_max = null;

   /** The label for the maximum number of iteration text field. */
   private JLabel m_maxLbl = null;

   /**
    * drop down list component for the clustering methods. Clustering methods
    * are defined in <code>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</codE>
    */
   private javax.swing.JComboBox m_methods = null;

   /** Text field for the user to enter number of clusters to be formed. */
   private JTextField m_numClust = null;

   /** Label for the number of clusters text field. */
   private JLabel m_numClustLbl = null;

   /**
    * Text field component for the user to enter the seed for random sampling,
    * in case <code>m_userFirst</code> is unchecked.
    */
   private JTextField m_seed = null;

   /** The label for the seed text field. */
   private JLabel m_seedLbl = null;

   /**
    * check box component for the user to choose between sequential sampling of
    * rows (checked status - the first N rows are sampled for clustering) or a
    * random sampling of N rows (unchecked).
    */
   private JCheckBox m_useFirst = null;

   //~ Constructors ************************************************************

   /**
    * Creates a new BuckshotParams_Props object. Updates the components
    * according the setting of the properties in <code>src</code>
    *
    * @param src The module this GUI component belongs to
    */
   public BuckshotParams_Props(BuckshotParams src) {
      _src = src;
      init();
   }

   //~ Methods *****************************************************************

   /**
    * Instantiates the GUI components of the editor. The components values are
    * set according to the values of the properties of <code>_src</code>
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
      //m_cmLbl.setText("Cluster Method: ");
       m_cmLbl.setText(BuckshotParamSpaceGenerator.CLUSTER_METHOD+": ");
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
      //m_seedLbl.setText("Random seed: ");
      m_seedLbl.setText(BuckshotParamSpaceGenerator.SEED+": ");
      m_seedLbl.setToolTipText("Enter integer value >= 0 specifying random" +
                               " seed.");
      m_gbl.setConstraints(m_seedLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_seed =
         new JTextField(Integer.toString((_src.getSeed() < 0) ? 0
                                                              : _src.getSeed()),
                        5);
      m_seed.setFont(new Font("Arial", Font.BOLD, 12));
      m_seed.setToolTipText("Enter integer value >= 0 specifying random" +
                            " seed.");
      m_gbl.setConstraints(m_seed, m_gbc);

      m_gbc.gridwidth = 2;

      m_gbc.gridx = 0;
      m_gbc.gridy++;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.CENTER;
      //m_useFirst = new JCheckBox("Use First", _src.getUseFirst());
      m_useFirst = new JCheckBox(BuckshotParamSpaceGenerator.USE_FIRST,
              _src.getUseFirst());
      m_useFirst.setToolTipText("Use first N rows as samples.");
      m_gbl.setConstraints(m_useFirst, m_gbc);

      m_gbc.gridy++;
      m_gbc.gridx = 0;
      m_gbc.gridwidth = 1;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.EAST;
      m_numClustLbl = new JLabel();
      //m_numClustLbl.setText("Number of clusters: ");
      m_numClustLbl.setText(BuckshotParamSpaceGenerator.NUM_CLUSTERS+": ");
      m_numClustLbl.setToolTipText("Enter integer value > 2 specifying number" +
                                   " of clusters desired.");
      m_gbl.setConstraints(m_numClustLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_numClust =
         new JTextField(Integer.toString((_src.getNumClusters() < 2)
                                         ? 5 : _src.getNumClusters()), 5);
      m_numClust.setFont(new Font("Arial", Font.BOLD, 12));
      m_numClust.setToolTipText("Enter integer value > 2 specifying number" +
                                " of clusters desired.");
      m_gbl.setConstraints(m_numClust, m_gbc);

      m_gbc.gridwidth = 2;
      m_gbc.gridx = 0;
      m_gbc.gridy++;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.insets = new Insets(10, 2, 2, 2);

      m_auto = new JCheckBox("Auto Cluster", true);

      // m_auto.addMouseListener(this);
      _initAuto = (_src.getDistanceThreshold() > 0);
      m_auto_sel = _initAuto;
      m_auto.setSelected(m_auto_sel);
      m_auto.setToolTipText("If selected you must specify a distance" +
                            " threshold at which point clustering will stop.");
      m_gbl.setConstraints(m_auto, m_gbc);

      m_gbc.gridy++;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.gridwidth = 2;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_distLbl = new JLabel();
      //m_distLbl.setText("Distance Cutoff (% of Max):  " +
      m_distLbl.setText(BuckshotParamSpaceGenerator.DISTANCE_THRESHOLD+" (% of Max):  "+
                        Integer.toString((_src.getDistanceThreshold() == 0)
                                         ? 1 : _src.getDistanceThreshold()));
      m_gbl.setConstraints(m_distLbl, m_gbc);

      m_gbc.gridy++;
      m_gbc.gridwidth = 2;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_distSlide =
         new JSlider(JSlider.HORIZONTAL, 0, 100,
                     (_src.getDistanceThreshold() == 0)
                     ? 1 : _src.getDistanceThreshold());
      m_distSlide.setMinorTickSpacing(10);
      m_distSlide.setMajorTickSpacing(25);
      m_distSlide.setPaintLabels(true);
      m_distSlide.addChangeListener(this);
      m_gbl.setConstraints(m_distSlide, m_gbc);

      m_gbc.gridy++;
      m_gbc.gridx = 0;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_gbc.insets = new Insets(10, 2, 2, 2);
      m_gbc.gridwidth = 1;
      m_dmLbl = new JLabel();
      //m_dmLbl.setText("Distance Metric: ");
      m_dmLbl.setText(BuckshotParamSpaceGenerator.DISTANCE_METRIC+": ");
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
      //m_maxLbl.setText("Num assignments: ");
      m_maxLbl.setText(BuckshotParamSpaceGenerator.MAX_ITERATIONS+": ");
      m_maxLbl.setToolTipText("Enter integer value > 0 specifying number " +
                              "of assignment passes to perform.");
      m_gbl.setConstraints(m_maxLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_max =
         new JTextField(Integer.toString((_src.getMaxIterations() < 1)
                                         ? 5 : _src.getMaxIterations()), 5);
      m_max.setFont(new Font("Arial", Font.BOLD, 12));
      m_max.setToolTipText("Enter integer value > 0 specifying number " +
                           "of assignment passes to perform.");
      m_gbl.setConstraints(m_max, m_gbc);

      add(m_cmLbl);
      add(m_methods);
      add(m_numClustLbl);
      add(m_numClust);
      add(m_auto);
      add(m_distLbl);
      add(m_distSlide);
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
    * Called when the user is manipulating the components in the GUI.
    *
    * @param e An action event trigered by the user.
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
    * Called when the user is manipulating the slider in the GUI.
    *
    * @param e A change event trigered bythe user.
    */
   public void stateChanged(ChangeEvent e) {

      if (e.getSource() == m_distSlide) {
         String disp =
            Integer.toString((m_distSlide.getValue() == 0)
                             ? 1 : m_distSlide.getValue());
         m_distSlide.setToolTipText(disp);
         //m_distLbl.setText("Distance Cutoff (% of Max):  " + disp);
         m_distLbl.setText(BuckshotParamSpaceGenerator.DISTANCE_THRESHOLD+" (% of Max):  "+disp);
      }
   }

   /**
    * Called whenever a GUI component is modified by the user. Updates the
    * module it belongs to (BuckshotParams) with the new values.
    *
    * @return boolean true if update succeeds.
    *
    * @throws Exception             If an invalid value is inserted in one of
    *                               the GUI fields (such as a String in a
    *                               numeric field).
    * @throws PropertyVetoException If one of the properties set by the user via
    *                               this GUI is invalid (e.g. number of clusters
    *                               is less than 2, or an alphabethic string is
    *                               inserted in a numeric field.)
    */
   public boolean updateModule() throws Exception {

      /*
       * need to set this to a default value because the sampler needs to get
       * sqrt(num_clusters*table_rows) of examples even if we are doing auto
       * clustering.
       */
      int num = -1;

      try {
         num = Integer.parseInt(m_numClust.getText());
      } catch (Exception e) {
         throw new PropertyVetoException("Error in number of clusters field. " +
                                         "Value should be an integer greater than 1.",

                                         null);
      }

      if (num < 2) {
         throw new PropertyVetoException("Number of clusters must be two or more.",
                                         null);
      }

      int seed = -1;

      try {
         seed = Integer.parseInt(m_seed.getText());
      } catch (Exception e) {
         throw new PropertyVetoException("Error in seed field: " +
                                         "Value should be a non negative integer.", null);
      }

      if (seed < 0) {
         throw new PropertyVetoException("Seed must be >= 0.", null);
      }

      int maxit = -1;

      try {
         maxit = Integer.parseInt(m_max.getText());
      } catch (Exception e) {
         throw new PropertyVetoException("Error in number of assignments field. " +
                                         "Value should be an integer greater than 1.",
                                         null);
      }

      if (maxit < 1) {
         throw new PropertyVetoException("Number of assignments must be > 1.",
                                         null);
      }

      if (_src != null) {
         _src.setUseFirst(m_useFirst.isSelected());

         if (m_auto.isSelected()) {
            _src.setDistanceThreshold((m_distSlide.getValue() == 0)
                                      ? 1 : m_distSlide.getValue());
         } else {
            _src.setDistanceThreshold(0);
         }

         _src.setNumClusters(num);
         _src.setSeed(seed);
         _src.setMaxIterations(maxit);
         _src.setClusterMethod(this.m_methods.getSelectedIndex());
         _src.setDistanceMetric(this.m_distMetrics.getSelectedIndex());
      }

      return true;
   } // end method updateModule

} // end class BuckshotParams_Props
