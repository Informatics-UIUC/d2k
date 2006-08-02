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
import ncsa.d2k.modules.core.discovery.cluster.sample.FractionationParams;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;


/**
 * <p>Title: FractionationParams_Props</p>
 *
 * <p>Description: The Custom Properties Editor for FractionationParams module</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class FractionationParams_Props extends JPanel
   implements CustomModuleEditor, MouseInputListener, ChangeListener,
              ActionListener {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 543811482265363848L;

   //~ Instance fields *********************************************************

   /** Initial value for m_auto_sel. */
   private boolean _initAuto = false;

   /** The parent module this editor belongs to. */
   private FractionationParams _src = null;

   /**
    * A check box component to enable/disable the slider that selects the
    * percentage for the HAc distance cutoff.
    */
   private JCheckBox m_auto = null;

   /** Whether the <codE>m_auto</code> check box is selected or not. */
   private boolean m_auto_sel = false;

   /** Label for the clustering methods drop down list. */
   private JLabel m_cmLbl = null;

   /** Label for the HAC distance cutoff slider. */
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

   /**
    * Text field for the user to enter the maximum number of iterations to be
    * performed.
    */
   private JTextField m_max = null;

   /** Label for maximum number of iterations to be performed. */
   private JLabel m_maxLbl = null;

   /** Text field component for the user to enter the maximum partition size. */
   private JTextField m_maxPart = null;

   /** Label for the maximum partition size text field. */
   private JLabel m_maxPartLbl = null;

   /**
    * Combo box component to host the available clustering methods. The
    * avialable clustering methods are defined in <code>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</codE>
    */
   private javax.swing.JComboBox m_methods = null;

   /**
    * Text field component for the user to enter the sorting column index text
    * field.
    */
   private JTextField m_nthSort = null;

   /** Label for the sorting column index text field. */
   private JLabel m_nthSortLbl = null;

   /** Text field for the user to enter number of clusters to be formed. */
   private JTextField m_numClust = null;

   /** Label for Number of CLusters text field. */
   private JLabel m_numClustLbl = null;

   //~ Constructors ************************************************************

   /**
    * Instantiates the editor according to the properties values in <code>src.
    * </code>
    *
     * @param src FractionationParams The module this editor belongs to
    */
   public FractionationParams_Props(FractionationParams src) {
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
      m_cmLbl.setText("Cluster Method: ");
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
      m_gbc.gridwidth = 1;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.EAST;
      m_numClustLbl = new JLabel();
      m_numClustLbl.setText("Number of clusters: ");
      m_numClustLbl.setToolTipText("Enter integer value > 2 specifying " +
                                   "number of clusters desired.");
      m_gbl.setConstraints(m_numClustLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_numClust =
         new JTextField(Integer.toString((_src.getNumClusters() < 2)
                                         ? 5 : _src.getNumClusters()), 5);
      m_numClust.setFont(new Font("Arial", Font.BOLD, 12));
      m_numClust.setToolTipText("Enter integer value > 2 specifying number " +
                                "of clusters desired.");
      m_gbl.setConstraints(m_numClust, m_gbc);

      m_gbc.gridx = 0;
      m_gbc.gridy++;
      m_gbc.gridwidth = 1;
      m_gbc.fill = GridBagConstraints.VERTICAL;
      m_gbc.insets = new Insets(2, 2, 2, 2);

      m_gbc.anchor = GridBagConstraints.EAST;
      m_maxPartLbl = new JLabel();
      m_maxPartLbl.setText("Max partition size: ");
      m_maxPartLbl.setToolTipText("Enter integer value > 0 specifying max " +
                                  "partition size.");
      m_gbl.setConstraints(m_maxPartLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_maxPart =
         new JTextField(Integer.toString((_src.getPartitionSize() < 1)
                                         ? 250 : _src.getPartitionSize()), 7);
      m_maxPart.setFont(new Font("Arial", Font.BOLD, 12));
      m_maxPart.setToolTipText("Enter integer value > 0 specifying max " +
                               "partition size.");
      m_gbl.setConstraints(m_maxPart, m_gbc);

      m_gbc.gridx = 0;
      m_gbc.gridy++;
      m_gbc.gridwidth = 1;
      m_gbc.fill = GridBagConstraints.VERTICAL;
      m_gbc.insets = new Insets(2, 2, 2, 2);

      m_gbc.anchor = GridBagConstraints.EAST;
      m_nthSortLbl = new JLabel();
      m_nthSortLbl.setText("Sort attribute: ");
      m_nthSortLbl.setToolTipText("Enter integer value >= 0 specifying the " +
                                  "index of attribute to sort on.");
      m_gbl.setConstraints(m_nthSortLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_nthSort =
         new JTextField(Integer.toString((_src.getNthSortTerm() < 0)
                                         ? 0 : _src.getNthSortTerm()), 5);
      m_nthSort.setFont(new Font("Arial", Font.BOLD, 12));
      m_nthSort.setToolTipText("Enter integer value >= 0 specifying the " +
                               "index of attribute to sort on.");
      m_gbl.setConstraints(m_nthSort, m_gbc);


      m_gbc.gridwidth = 2;
      m_gbc.gridx = 0;
      m_gbc.gridy++;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.insets = new Insets(10, 2, 2, 2);

      m_auto = new JCheckBox("Auto Cluster", true);
      m_auto.addMouseListener(this);
      _initAuto = (_src.getHacDistanceThreshold() > 0);
      m_auto_sel = _initAuto;
      m_auto.setSelected(m_auto_sel);
      m_auto.setToolTipText("If selected you must specify a distance " +
                            "threshold at which point clustering will stop.");
      m_gbl.setConstraints(m_auto, m_gbc);

      m_gbc.gridy++;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.gridwidth = 2;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_distLbl = new JLabel();
      m_distLbl.setText("Distance Cutoff (% of Max):  " +
                        Integer.toString((_src.getHacDistanceThreshold() == 0)
                                         ? 1 : _src.getHacDistanceThreshold()));
      m_gbl.setConstraints(m_distLbl, m_gbc);

      m_gbc.gridy++;
      m_gbc.gridwidth = 2;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.insets = new Insets(2, 2, 2, 2);
      m_distSlide =
         new JSlider(JSlider.HORIZONTAL, 0, 100,
                     (_src.getHacDistanceThreshold() == 0)
                     ? 1 : _src.getHacDistanceThreshold());
      m_distSlide.setMinorTickSpacing(10);
      m_distSlide.setMajorTickSpacing(25);
      m_distSlide.setPaintLabels(true);
      m_distSlide.addChangeListener(this);
      m_distSlide.setEnabled(m_auto_sel);
      m_gbl.setConstraints(m_distSlide, m_gbc);

      m_gbc.gridy++;
      m_gbc.gridx = 0;
      m_gbc.anchor = GridBagConstraints.EAST;
      m_gbc.insets = new Insets(10, 2, 2, 2);
      m_gbc.gridwidth = 1;
      m_dmLbl = new JLabel();
      m_dmLbl.setText("Distance Metric: ");
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
      m_maxLbl.setText("Num assignments: ");

      // m_numClustLbl.setFont(new Font("Arial", Font.BOLD,10));
      m_maxLbl.setToolTipText("Enter integer value > 0 specifying number " +
                              "of assignment passes to perform.");
      m_gbl.setConstraints(m_maxLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_max =
         new JTextField(Integer.toString((_src.getRefinementMaxIterations() < 1)
                                         ? 5
                                         : _src.getRefinementMaxIterations()),
                        5);
      m_max.setFont(new Font("Arial", Font.BOLD, 12));
      m_max.setToolTipText("Enter integer value > 0 specifying number of " +
                           "assignment passes to perform.");
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
      add(m_maxLbl);
      add(m_max);
      add(this.m_maxPart);
      add(this.m_maxPartLbl);
      add(this.m_nthSort);
      add(this.m_nthSortLbl);

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
    * Inteface inplementation. This method is just an override, and does nothing
    *
    * @param evt MouseEvent
    */
   public void mouseClicked(MouseEvent evt) {
      // System.out.println("MOUSE CLICKED");
   }

   /**
    * Inteface inplementation. This method is just an override, and does nothing
    *
    * @param evt MouseEvent
    */
   public void mouseDragged(MouseEvent evt) {
      // System.out.println("MOUSE DRAGGED");
   }

   /**
    * Inteface inplementation. This method is just an override, and does nothing
    *
    * @param evt MouseEvent
    */
   public void mouseEntered(MouseEvent evt) { }

   /**
    * Inteface inplementation. This method is just an override, and does nothing
    *
    * @param evt MouseEvent
    */
   public void mouseExited(MouseEvent evt) { }

   /**
    * Inteface inplementation. This method is just an override, and does nothing
    *
    * @param evt MouseEvent
    */
   public void mouseMoved(MouseEvent evt) {
      // System.out.println("MOUSE MOVED");
   }

   /**
    * Handles mouse pressed events associated with the HAC distance cut off
    * enabling checkbox. If the check box is selected - enables the slider, if
    * it is de-selected disables the slider.
    *
    * @param evt MouseEvent A click performed on the HAC distance cut off
    *            clider's check box.
    */
   public void mousePressed(MouseEvent evt) {

      if (evt.getSource() == m_auto) {
         m_auto_sel = !m_auto_sel;
         m_distSlide.setEnabled(m_auto_sel);
         m_numClust.setEnabled(!m_auto_sel);
         repaint();
      }
   }

   /**
    * Handles mouse released events associated with the HAC distance cut off
    * enabling checkbox. If the check box is selected - enables the slider, if
    * it is de-selected disables the slider.
    *
    * @param evt MouseEvent A click performed on the HAC distance cut off
    *            clider's check box.
    */
   public void mouseReleased(MouseEvent evt) {

      if (evt.getSource() == m_auto) {

         if (m_auto.isSelected() != m_auto_sel) {
            m_auto.setSelected(m_auto_sel);
         }

         repaint();
      }
   }


   /**
    * Handles state changed events related to the sliders in the editor. Updates
    * the displayed string with the new selected value. Other state changed
    * events are ignored.
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

      if (this.m_numClust.isEnabled()) {

         try {
            num = Integer.parseInt(m_numClust.getText());
         } catch (Exception e) {
            throw new PropertyVetoException("Error in number of clusters field: " +
                                            e.getMessage(), null);
         }

         if (num < 2) {
            throw new PropertyVetoException("Number of clusters must be two or more.",
                                            null);
         }
      }

      int max = -1;

      try {
         max = Integer.parseInt(m_maxPart.getText());
      } catch (Exception e) {
         throw new PropertyVetoException("Error in max partition size field: " +
                                         e.getMessage(), null);
      }

      if (max < 1) {
         throw new PropertyVetoException("Max partition size must be > 0.",
                                         null);
      }

      int nth = -1;

      try {
         nth = Integer.parseInt(m_nthSort.getText());
      } catch (Exception e) {
         throw new PropertyVetoException("Error in nth sort field: " +
                                         e.getMessage(), null);
      }

      if (nth < 0) {
         throw new PropertyVetoException("Nth sort index must be >= 0.", null);
      }

      int maxit = -1;

      try {
         maxit = Integer.parseInt(m_max.getText());
      } catch (Exception e) {
         throw new PropertyVetoException("Error in number of assignments field: " +
                                         e.getMessage(), null);
      }

      if (maxit < 1) {
         throw new PropertyVetoException("Number of assignments must be > 1.",
                                         null);
      }

      if (_src != null) {

         if (m_auto.isSelected()) {
            _src.setHacDistanceThreshold((m_distSlide.getValue() == 0)
                                         ? 1 : m_distSlide.getValue());
         } else {
            _src.setHacDistanceThreshold(0);
         }

         _src.setNumClusters(num);
         _src.setRefinementMaxIterations(maxit);
         _src.setClusterMethod(this.m_methods.getSelectedIndex());
         _src.setDistanceMetric(this.m_distMetrics.getSelectedIndex());
         _src.setPartitionSize(max);
         _src.setNthSortTerm(nth);
      }

      return true;
   } // end method updateModule

} // end class FractionationParams_Props
