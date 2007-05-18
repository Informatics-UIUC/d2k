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
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;
import ncsa.gui.Constrain;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JEditorPane;
import java.awt.Dimension;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import ncsa.d2k.modules.core.util.*;
import ncsa.d2k.preferences.UserPreferences;
import ncsa.gui.JOutlinePanel;


/**
 * Create a DelimitedFileReader for a file.
 * <p><b>Note:</b>  This module is the same as deprecated module
 * <i>CreateDelimitedFileParser</i>, extended to access the data through
 * <i>DataObjectProxy</i>.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class CreateDelimitedParserFromURL extends InputModule {

   //~ Instance fields *********************************************************

   /** Description of field hasLabels. */
   private boolean hasLabels = true;

   /** Description of field hasSpecDelim. */
   private boolean hasSpecDelim = false;

   /** Description of field hasTypes. */
   private boolean hasTypes = true;

   /** Description of field labelsRow. */
   private int labelsRow = 0;

   /** Description of field specDelim. */
   private String specDelim = null;

   /** Description of field typesRow. */
   private int typesRow = 1;
   
   /** the fontstyle tags. */
   final String fontstyle =
      "<font size=\"" +
      UserPreferences.thisPreference.getFontSize() +
      "\" face=\"Arial,Helvetica,SansSerif \">";
   
   /** the end of the font style. */
   final String endfontstyle = "</font>";
   
   //~ Methods *****************************************************************
   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   public void beginExecution() {
	   myLogger.setLoggingLevel(moduleLoggingLevel);
   }

   private int moduleLoggingLevel=
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass())
	   .getLoggingLevel();
   
   public int getmoduleLoggingLevel(){
	   return moduleLoggingLevel;
   }

   public void setmoduleLoggingLevel(int level){
	   moduleLoggingLevel = level;
   }

   /**
    * Description of method doit.
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {

      DataObjectProxy dataobj = (DataObjectProxy) pullInput(0);
      DelimitedFileParserFromURL df;

      myLogger.setLoggingLevel(moduleLoggingLevel);
      
      int lbl = -1;

      if (getHasLabels()) {
         lbl = getLabelsRow();
      }

      int typ = -1;

      if (getHasTypes()) {
         typ = getTypesRow();
      }

      if (!getHasSpecDelim()) {
         df = new DelimitedFileParserFromURL(dataobj, lbl, typ);
      } else {
         String s = getSpecDelim();
         char[] del = s.toCharArray();
         System.out.println("delimiter is: " + del[0]);

         if (del.length == 0) {
            throw new Exception("User specified delimiter has not been set");
         }

         df = new DelimitedFileParserFromURL(dataobj, lbl, typ, del[0]);
      }

      pushOutput(df, 0);


   } // end method doit

   /**
    * Description of method getHasLabels.
    *
    * @return Description of return value.
    */
   public boolean getHasLabels() { return hasLabels; }

   /**
    * Description of method getHasSpecDelim.
    *
    * @return Description of return value.
    */
   public boolean getHasSpecDelim() { return hasSpecDelim; }

   /**
    * Description of method getHasTypes.
    *
    * @return Description of return value.
    */
   public boolean getHasTypes() { return hasTypes; }

   /**
    * Description of method getInputInfo.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
    */
   public String getInputInfo(int i) {
      return "Data Object pointing to a resource.";
   }

   /**
    * Description of method getInputName.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
    */
   public String getInputName(int i) { return "DataObjectProxy"; }

   /**
    * Description of method getInputTypes.
    *
    * @return Description of return value.
    */
   public String[] getInputTypes() {
      String[] in = { "ncsa.d2k.modules.core.io.proxy.DataObjectProxy" };

      return in;
   }

   /**
    * Description of method getLabelsRow.
    *
    * @return Description of return value.
    */
   public int getLabelsRow() { return labelsRow; }

   /**
    * Description of method getModuleInfo.
    *
    * @return Description of return value.
    */
   public String getModuleInfo() {
      StringBuffer s = new StringBuffer("<p>Overview: ");
      s.append("This module creates a parser for the specified data object " +
      		"proxy. The file is expected to ");
      s.append("have a consistent delimiter character. ");
      s.append("<p><b>Note:</b>  This module is the same as deprecated module " +
      		"<i>CreateDelimitedFileParser</i>, extended to access the data " +
      		"through <i>DataObjectProxy</i>.</p>");
      s.append("</p><p>DetailedDescription: ");
      s.append("This module creates a parser that can be ");
      s.append("used to read data from a file that uses a single delimiter ");
      s.append("character to separate the data into fields. ");
      s.append("The delimiter can be found automatically, or it can be " +
      		"input in the properties ");
      s.append("editor.  If the delimiter is to be found automatically, " +
      		"the file must be ");
      s.append("contain at least 2 rows. ");
      s.append("The file can contain a row of labels, and a row of data ");
      s.append("types.  These are also specified via the properties editor.");

      s.append("</p><p>Properties are used to specify the delimiter, " +
      		"the labels row number, ");
      s.append("and the types row number. The row numbers are indexed " +
      		"from zero.");

      s.append("</p><p>Typically the <i>File Parser</i> output port of this ");
      s.append("module is connected to the <i>File Parser</i> input port of ");
      s.append("a module whose name begins with 'Parse File', for example, ");
      s.append("<i>Parse File To Table</i> or  <i>Parse File To Paging Table</i>.");

      s.append("<p>Data Type Restrictions: ");
      s.append("The input to this module must be a delimited file. If " +
      		"the file is ");
      s.append("large a java OutOfMemory error might occur");
      s.append("<p>Data Handling: ");
      s.append("The module does not destroy or modify the input data.");

      return s.toString();
   } // end method getModuleInfo

   /**
    * Description of method getModuleName.
    *
    * @return Description of return value.
    */
   public String getModuleName() { return "Create Delimited File Parser"; }

   /**
    * Description of method getOutputInfo.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
    */
   public String getOutputInfo(int i) {
      return "A Delimited File Parser for the specified file.";
   }

   /**
    * Description of method getOutputName.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
    */
   public String getOutputName(int i) { return "File Parser"; }

   /**
    * Description of method getOutputTypes.
    *
    * @return Description of return value.
    */
   public String[] getOutputTypes() {
      String[] out =
      { "ncsa.d2k.modules.core.io.file.input.DelimitedFileParserFromURL" };

      return out;
   }

   /**
    * Description of method getPropertiesDescriptions.
    *
    * @return Description of return value.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] retVal = new PropertyDescription[4];
      retVal[0] =
         new PropertyDescription("labelsRow", "Labels Row",
                                 "This is the index of the labels row in the file, or -1 if there is no labels row.");
      retVal[1] =
         new PropertyDescription("typesRow", "Types Row",
                                 "This is the index of the types row in the file, or -1 if there is no types row.");
      retVal[2] =
         new PropertyDescription("specDelim", "Delimiter",
                                 "The delimiter of this file if it is different than space, tab '|' or '='");
      retVal[3] = 
    	  new PropertyDescription("moduleLoggingLevel", "Module Logging Level",
          "The logging level of this modules");
      return retVal;
   }

   /**
    * Description of method getPropertyEditor.
    *
    * @return Description of return value.
    */
   public CustomModuleEditor getPropertyEditor() { return new PropEdit(); }

   /**
    * Description of method getSpecDelim.
    *
    * @return Description of return value.
    */
   public String getSpecDelim() { return specDelim; }

   /**
    * Description of method getTypesRow.
    *
    * @return Description of return value.
    */
   public int getTypesRow() { return typesRow; }

   /**
    * Description of method setHasLabels.
    *
    * @param b Description of parameter b.
    */
   public void setHasLabels(boolean b) { hasLabels = b; }

   /**
    * Description of method setHasSpecDelim.
    *
    * @param b Description of parameter b.
    */
   public void setHasSpecDelim(boolean b) { hasSpecDelim = b; }

   /**
    * Description of method setHasTypes.
    *
    * @param b Description of parameter b.
    */
   public void setHasTypes(boolean b) { hasTypes = b; }

   /**
    * Description of method setLabelsRow.
    *
    * @param i Description of parameter i.
    */
   public void setLabelsRow(int i) { labelsRow = i; }

   /**
    * Description of method setSpecDelim.
    *
    * @param s Description of parameter s.
    */
   public void setSpecDelim(String s) { specDelim = s; }

   /**
    * Description of method setTypesRow.
    *
    * @param i Description of parameter i.
    */
   public void setTypesRow(int i) { typesRow = i; }

   //~ Inner Classes ***********************************************************

   private class PropEdit extends JPanel implements CustomModuleEditor {
      boolean delChange = false;
      JCheckBox delim;
      JTextField delimfld;
      JLabel dellbl;

      JCheckBox lblbtn;

      boolean lblChange = false;
      JLabel lbllbl;
      JTextField lblrow;
      JCheckBox typbtn;
      boolean typChange = false;
      JLabel typlbl;
      JTextField typrow;
      private JComboBox logginglevelcb;

      private PropEdit() {

         lblbtn = new JCheckBox("File Has Labels Row", getHasLabels());
         lblbtn.setToolTipText("Select this option if the file has a row of column labels.");
         lbllbl = new JLabel("Labels Row:");
         lbllbl.setToolTipText("This is the index of the labels row in the file.");
         lblrow = new JTextField(5);

         if (!getHasLabels()) {
            lbllbl.setEnabled(false);
            lblrow.setEnabled(false);
         }

         lblrow.setText(Integer.toString(getLabelsRow()));
         lblrow.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) { lblChange = true; }
            });

         lblbtn.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  lblChange = true;

                  if (lblbtn.isSelected()) {
                     lbllbl.setEnabled(true);
                     lblrow.setEnabled(true);
                     lblrow.setText("0");
                  } else {
                     lbllbl.setEnabled(false);
                     lblrow.setEnabled(false);
                     lblrow.setText("-1");
                  }
               }
            });

         typbtn = new JCheckBox("File Has Types Row", getHasTypes());
         typbtn.setToolTipText("Select this option if the file has a row of data types for columns.");
         typrow = new JTextField(5);
         typlbl = new JLabel("Types Row");
         typlbl.setToolTipText("This is the index of the types row in the file.");

         if (!getHasTypes()) {
            typrow.setEnabled(false);
            typlbl.setEnabled(false);
         }

         typrow.setText(Integer.toString(getTypesRow()));
         typrow.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) { typChange = true; }
            });

         typbtn.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  typChange = true;

                  if (typbtn.isSelected()) {
                     typlbl.setEnabled(true);
                     typrow.setEnabled(true);
                     typrow.setText("1");
                  } else {
                     typlbl.setEnabled(false);
                     typrow.setEnabled(false);
                     typrow.setText("-1");
                  }
               }
            });

         delim =
            new JCheckBox("File Has User-specified Delimiter",
                          getHasSpecDelim());
         delim.setToolTipText("Select this option if the file has a special delimiter.");
         delimfld = new JTextField(5);
         dellbl = new JLabel("Delimiter:");
         dellbl.setToolTipText("Specify the special delimiter.");

         if (getHasSpecDelim()) {
            delimfld.setText(specDelim);
         } else {
            delimfld.setEnabled(false);
            dellbl.setEnabled(false);
         }

         delimfld.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) { delChange = true; }
            });

         delim.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  delChange = true;

                  if (delim.isSelected()) {
                     dellbl.setEnabled(true);
                     delimfld.setEnabled(true);
                  } else {
                     dellbl.setEnabled(false);
                     delimfld.setEnabled(false);
                  }
               }
            });
         
         String[] loglevelEnum = {"DEBUG","INFO","WARN","ERROR","FATAL","OFF"};
         logginglevelcb = new JComboBox(loglevelEnum);
         logginglevelcb.setEditable(false);
         logginglevelcb.setSelectedIndex(moduleLoggingLevel);
         
         JOutlinePanel loggingLevelPanel = new JOutlinePanel("Module Logging Level");
         loggingLevelPanel.setLayout(new GridBagLayout());
         
         StringBuffer tp = new StringBuffer("<html>");
         tp.append(fontstyle);
         tp.append("The Logging Level");
         tp.append(endfontstyle);
         tp.append("</html>");

         
         JEditorPane jta5 =
             new JEditorPane("text/html", tp.toString()) {
                static private final long serialVersionUID = 1L;

                // we can no longer have a horizontal scrollbar if we are always
                // set to the same width as our parent....may need to be fixed.
                public Dimension getPreferredSize() {
                   Dimension d = this.getMinimumSize();

                   return new Dimension(100, d.height);
                }
             };         
             jta5.setBackground(loggingLevelPanel.getBackground());

             Constrain.setConstraints(loggingLevelPanel, jta5, 0, 0, 2, 1,
            		 GridBagConstraints.HORIZONTAL,
                     GridBagConstraints.CENTER, 0, 0);
             Constrain.setConstraints(loggingLevelPanel, logginglevelcb, 0, 1, 1, 1,
                     GridBagConstraints.HORIZONTAL,
                     GridBagConstraints.CENTER, 0, 0);
             
         // add the components for delimited properties
         setLayout(new GridBagLayout());

         Constrain.setConstraints(this, lblbtn, 0, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, lbllbl, 1, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, lblrow, 2, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, typbtn, 0, 2, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, typlbl, 1, 3, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, typrow, 2, 3, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, delim, 0, 4, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, dellbl, 1, 5, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, delimfld, 2, 5, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, loggingLevelPanel, 0, 6, 1, 1,
				  GridBagConstraints.HORIZONTAL,
				  GridBagConstraints.CENTER, 1, 1);
      }

      public boolean updateModule() throws Exception {
         boolean didChange = false;

         if (lblChange) {

            if (!lblbtn.isSelected()) {
               setLabelsRow(-1);
               setHasLabels(false);
               didChange = true;
            } else {
               String lrow = lblrow.getText();
               int lrownum;

               try {
                  lrownum = Integer.parseInt(lrow);
               } catch (NumberFormatException e) {
                  throw new Exception("The Labels Row must be a non negative integer.");
               }

               if (lrownum != getLabelsRow()) {
                  setLabelsRow(lrownum);

                  if (lrownum >= 0) {
                     setHasLabels(true);
                  } else {
                     setHasLabels(false);
                  }

                  didChange = true;
               }
            }
         } // end if

         if (typChange) {

            if (!typbtn.isSelected()) {
               setTypesRow(-1);
               setHasTypes(false);
               didChange = true;
            } else {
               String trow = typrow.getText();
               int trownum;

               try {
                  trownum = Integer.parseInt(trow);
               } catch (NumberFormatException e) {
                  throw new Exception("The Types Row must be a non negative integer.");
               }

               if (trownum != getTypesRow()) {
                  setTypesRow(trownum);

                  if (trownum >= 0) {
                     setHasTypes(true);
                  } else {
                     setHasTypes(false);
                  }

                  didChange = true;
               }
            }
         } // end if

         if (delChange) {

            if (!delim.isSelected()) {
               setHasSpecDelim(false);
               setSpecDelim(null);
               didChange = true;
            } else {
               String dd = null;

               if (delim.isSelected()) {
                  dd = delimfld.getText();

                  if (dd.length() != 1) {
                     throw new Exception("The delimiter must be exactly one character long.");
                  }
               }

               if (dd != getSpecDelim()) {
                  setSpecDelim(dd);
                  setHasSpecDelim(true);
                  didChange = true;
               }
            }
         }
         String f0 = Integer.toString(logginglevelcb.getSelectedIndex());
         if (Integer.parseInt(f0) != getmoduleLoggingLevel()){
        	 setmoduleLoggingLevel(Integer.parseInt(f0));
        	 didChange = true;
         }
         
         return didChange;
      } // end method updateModule
   } // end class PropEdit
} // end class FactoryCreateDelimitedParser
