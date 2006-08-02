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
import ncsa.d2k.modules.core.discovery.cluster.sample.FractionationSampler;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;


/**
 * <p>Title: FractionationSampler_Props</p>
 *
 * <p>Description: The Custom Properties Editor for FractionationSampler
 * module</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class FractionationSampler_Props extends JPanel
   implements CustomModuleEditor {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 2439304615988092222L;

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
   private FractionationSampler _src = null;

   /**
    * If checked then the module this editor belongs to performs a check for
    * missing values in the input table (if missing values are found an
    * exception is thrown).
    */
   private JCheckBox m_checkMV = null;

   /** Layout constraints of this editor's components. */
   private GridBagConstraints m_gbc = new GridBagConstraints();

   /** Layout manager for this editor. */
   private GridBagLayout m_gbl = new GridBagLayout();

   /** Text field component for the user to enter the maximum partition size. */
   private JTextField m_maxPart = null;

   /** Label for the maximum partition size text field. */
   private JLabel m_maxPartLbl = null;

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
    * @param src FractionationSampler The module this editor belongs to
    * @param ver boolean whether to include the verbosity checkbox in the layout
    * @param mvc boolean whether to include the missing value check box in the
    *            layout
    */
   public FractionationSampler_Props(FractionationSampler src, boolean ver,
                                     boolean mvc) {
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
      m_numClustLbl.setText("Number of clusters: ");

      // m_numClustLbl.setFont(new Font("Arial", Font.BOLD,10));
      m_numClustLbl.setToolTipText("Enter integer value > 2 specifying " +
                                   "number of clusters desired.");
      m_gbl.setConstraints(m_numClustLbl, m_gbc);

      m_gbc.gridx = 1;
      m_gbc.anchor = GridBagConstraints.WEST;
      m_numClust =
         new JTextField(Integer.toString((_src.getNumberOfClusters() < 2)
                                         ? 5 : _src.getNumberOfClusters()), 5);
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
         new JTextField(Integer.toString((_src.getmaxPartitionSize() < 1)
                                         ? 250 : _src.getmaxPartitionSize()),
                        7);
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
      m_gbc.insets = new Insets(4, 2, 2, 2);
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_verbose = new JCheckBox("Verbose", _src.getVerbose());
      m_verbose.setToolTipText("Print verbose messages to console.");
      m_gbl.setConstraints(m_verbose, m_gbc);
      m_verbose.setVisible(_showVerbose);

      m_gbc.gridy++;
      m_gbc.anchor = GridBagConstraints.CENTER;
      m_gbc.insets = new Insets(4, 2, 2, 2);
      this.m_checkMV =
         new JCheckBox("Check Missing for Values",
                       _src.getCheckMissingValues());
      m_checkMV.setToolTipText("Screen for missing values in the input table.");
      m_gbl.setConstraints(m_checkMV, m_gbc);
      m_checkMV.setVisible(this._showMVCheck);

      add(m_numClustLbl);
      add(m_numClust);
      add(m_maxPartLbl);
      add(m_maxPart);
      add(m_nthSortLbl);
      add(m_nthSort);
      add(m_verbose);
      add(m_checkMV);

      this.setMinimumSize(this.getPreferredSize());
      this.validateTree();

   } // end method init


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
         throw new PropertyVetoException("Error in number of clusters field: " +
                                         e.getMessage(), null);
      }

      if (num < 2) {
         throw new PropertyVetoException("Number of clusters must be two or more.",
                                         null);
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

      if (_src != null) {

         if (this._showMVCheck) {
            _src.setCheckMissingValues(this.m_checkMV.isSelected());
         }

         _src.setNumberOfClusters(num);
         _src.setMaxPartitionSize(max);
         _src.setNthSortTerm(nth);
      }

      return true;
   } // end method updateModule


} // end class FractionationSampler_Props
