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
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.gui.Constrain;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.util.Random;
import ncsa.d2k.modules.core.util.*;

/**
 * SimpleTrainTest.java The user to select a percentage of the table to be train
 * and test. This module provide a custom gui that is a slider indicating the
 * percent of the data to be used as training data.
 *
 * @author  Tom Redman, revised Xiaolei Li, edited R.Aydt
 * @version $Revision$, $Date$
 */
public class SimpleTrainTest extends DataPrepModule {

   //~ Static fields/initializers **********************************************

   /** constant for random sampling. */
   static public final int RANDOM = 0;

   /** constant for sequential sampling. */
   static public final int SEQUENTIAL = 1;

   //~ Instance fields *********************************************************

   /** true if in debug mode */
   private boolean debug = false;

   /** The type of sampling to use: random or sequential. */
   private int samplingMethod;

   /** the seed for the random number generator. */
   private int seed = 123;

   /** percent of dataset to use to test the model. */
   private int testPercent = 50;

   /** percent of dataset to use to train the model. */
   private int trainPercent = 50;
   private D2KModuleLogger myLogger;
   

   //~ Methods *****************************************************************
   public void beginExecution() {
		  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
	   }

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      Table orig = (Table) this.pullInput(0);

      // This is the number that will be test
      int nr = orig.getNumRows();
      int numTest = (int) (((long) nr * (long) this.getTestPercent()) / 100L);
      int numTrain = (int) (((long) nr * (long) this.getTrainPercent()) / 100L);

      if (numTest < 1 || numTrain < 1) {
         throw new Exception(this.getAlias() +
                             ": The selected table was to small to be practical with the percentages specified.");
      }

      int[] test = new int[numTest];
      int[] train = new int[numTrain];

      // only keep the first N rows
      int[] random = new int[nr];

      for (int i = 0; i < nr; i++) {
         random[i] = i;
      }

      // If we are to select the examples for test and train at random,
      // we need to to shuffle the indices.
      if (samplingMethod == RANDOM) {

         // Shuffle the indices randomly.
         Random r = new Random(seed);

         for (int i = 0; i < nr; i++) {
            int which = (int) (r.nextDouble() * (double) nr);

            if (i != which) {
               int s = random[which];
               random[which] = random[i];
               random[i] = s;
            }
         }
      }

      // do the train assignment, from the start of the array of indices.
      for (int i = 0; i < numTrain; i++) {
         train[i] = random[i];
      }

      // do the test assignment, from the end of the array of indices.
      for (int i = numTest - 1, j = nr - 1; i >= 0; i--, j--) {
         test[i] = random[j];
      }

      random = null;

      if (debug) {
    	  myLogger.setDebugLoggingLevel();//temp set to debug
    	  myLogger.debug("test set");

         for (int i = 0; i < test.length; i++) {

            if (i > 0) {
            	myLogger.debug(",");
            }

            myLogger.debug(test[i]);
         }

         // do the train assignment, from the end of the array of indices.
         myLogger.debug("train set");

         for (int i = 0; i < train.length; i++) {

            if (i > 0) {
            	myLogger.debug(",");
            }

            myLogger.debug(train[i]);
         }
         myLogger.resetLoggingLevel();//re-set level to original level
      }

      ExampleTable et = orig.toExampleTable();
      et.setTestingSet(test);
      et.setTrainingSet(train);

      this.pushOutput(et.getTrainTable(), 0);
      this.pushOutput(et.getTestTable(), 1);
   } // end method doit


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
            return "The table containing the data that will be split into training and testing examples.";

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

      switch (i) {

         case 0:
            return "Original Table";

         default:
            return "No such input.";
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
      String sb = "<p>Overview: ";
      sb +=
         "This module generates a training table and a testing table from the original table. ";

      sb += "</p><p>Detailed Description: ";
      sb +=
         "This module presents the user with a custom property editor which allows them to ";
      sb +=
         "specify the percentages of the <i>Original Table</i> examples that should be used to build ";
      sb +=
         "train and test tables.   The user can specify whether the train and test examples are selected ";
      sb +=
         "at random or sequentially from the beginning (train data) and the end (test data) of the ";
      sb +=
         "original examples.  If the examples are selected randomly, the user can specify the seed used ";
      sb += "by the random number generator. ";

      sb += "</p><p>";
      sb +=
         "If the train and test percentages sum to more than 100 percent, some examples will appear in ";
      sb +=
         "both the train and test tables.   To change the train and test percentages independently, ";
      sb +=
         "select and drag the side arrows on the GUI display. This is useful when you wish to use more or ";
      sb +=
         "less than 100% of the original examples in populating the train and test tables.  ";
      sb +=
         "To change both percentages at the same time, select and drag the invisible line between ";
      sb +=
         "the percentages. This technique insures that all of the original examples are used to populate ";
      sb +=
         "either the train table or the test table, but no examples are used more than once.  ";

      sb += "</p><p>Data Type Restrictions: ";
      sb +=
         "Although this module works with tables containing any type of data, many supervised learning ";
      sb +=
         "algorithms will work only on doubles. If one of these algorithms is to be used, the ";
      sb +=
         "conversion to floating point data should take place prior to this module.   ";

      sb += "</p><p>Data Handling: ";
      sb +=
         "This module does not change the original data. It creates an instance of an example table ";
      sb += "that manages the data data differently.  ";

      sb += "</p><p>Scalability: ";
      sb +=
         "This module should scale linearly with the number of rows in the table.  The module needs to ";
      sb +=
         "be able to allocate arrays of integers to hold the indices of the test and train examples.</p>";

      return sb.toString();
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Simple Train Test"; }


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
            return "The table containing the training data.";

         case 1:
            return "The table containing the test data.";

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

      switch (i) {

         case 0:
            return "Train Table";

         case 1:
            return "Test Table";

         default:
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
      String[] types =
      {
         "ncsa.d2k.modules.core.datatype.table.TrainTable",
         "ncsa.d2k.modules.core.datatype.table.TestTable"
      };

      return types;
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[4];
      pds[0] =
         new PropertyDescription("trainPercent",
                                 "Train Percentage",
                                 "The percentage of the data to be used for training the model.");
      pds[1] =
         new PropertyDescription("testPercent",
                                 "Test Percentage",
                                 "The percentage of the data to be used for testing the model.");
      pds[2] =
         new PropertyDescription("samplingMethod",
                                 "Sampling Method",
                                 "The method to use when sampling the original examples.  " +
                                 "The choices are: " +
                                 "<p>Random: Train and test examples are drawn randomly from the original table.</p>" +
                                 "<p>Sequential: Training examples are taken sequentially from the beginning of the " +
                                 "original table and testing examples are " +
                                 "taken sequentially from the end of the original table. ");
      pds[3] =
         new PropertyDescription("seed",
                                 "Random Seed",
                                 "Seed for random sampling." +
                                 "Ignored if Random Sampling is not used.");

      return pds;
   } // end method getPropertiesDescriptions

   /**
    * return a reference a custom property editor to select the percent test and
    * train.
    *
    * @return a reference a custom property editor
    */
   public CustomModuleEditor getPropertyEditor() {
      return new JSetPercentage(this);
   }

   /**
    * Get the sampling method.
    *
    * @return sampling method
    */
   public int getSamplingMethod() { return samplingMethod; }

   /**
    * Get the random number seed.
    *
    * @return the seed
    */
   public int getSeed() { return seed; }

   /**
    * Get the test percent.
    *
    * @return the test percent
    */
   public int getTestPercent() { return testPercent; }

   /**
    * Get the train percent.
    *
    * @return the train percent
    */
   public int getTrainPercent() { return trainPercent; }

   /**
    * Set the sampling method.
    *
    * @param val new sampling method
    */
   public void setSamplingMethod(int val) { samplingMethod = val; }

   /**
    * Set the random number seed.
    *
    * @param  i the seed
    *
    * @throws PropertyVetoException if the seed is less than or equal to zero
    */
   public void setSeed(int i) throws PropertyVetoException {

      if (i < 0) {
         throw new PropertyVetoException(" Value must be >= 0. ", null);
      } else {
         seed = i;
      }
   }

   /**
    * Set the percentage to use for testing data.
    *
    * @param  i testing data percentage
    *
    * @throws PropertyVetoException when less than zero or greater than 100
    */
   public void setTestPercent(int i) throws PropertyVetoException {

      if (i < 0 || i > 100) {
         throw new PropertyVetoException("Test percentage must be between 0 and 100.",
                                         null);
      }

      testPercent = i;
   }

   /**
    * Set the percentage to use for training data.
    *
    * @param  i train percent
    *
    * @throws PropertyVetoException when less than 0 or greater than 100
    */
   public void setTrainPercent(int i) throws PropertyVetoException {

      if (i < 0 || i > 100) {
         throw new PropertyVetoException("Train percentage must be between 0 and 100.",
                                         null);
      }

      trainPercent = i;
   }

   //~ Inner Classes ***********************************************************

   /**
    * This panel displays the editable properties of the SimpleTestTrain
    * modules.
    *
    * @author  Thomas Redman
    * @version $Revision$, $Date$
    */
   class JSetPercentage extends JPanel implements CustomModuleEditor,
                                                  ActionListener {

      /** sequential vs random sampling. */
      JLabel m_sampling_method_label = null;

      /** combo box to hold sampling methods. */
      javax.swing.JComboBox m_sampling_methods = null;

      /** text field to input seed value. */
      JTextField m_seed = null;

      /** if random sampling is used, these will be exposed. */

      /** label. */
      JLabel m_seed_label = null;

      /** the module to modify. */
      SimpleTrainTest module;

      /** descriptions of sampling methods. */
      String[] sampling_method_desc =
      {
         "Randomly sample to build train and test tables. ",
         "First entries in original table used as training examples and last entries used as testing examples."
      };

      /** labels of sampling methods. */
      String[] sampling_method_labels = { "Random", "Sequential" };

      /** a slider indicating the percent test. */
      TestTrainSlider slider = null;

      /**
       * Given the module to change.
       *
       * @param stt the module.
       */
      JSetPercentage(SimpleTrainTest stt) {
         Font tmp = new Font("Serif", Font.PLAIN, 12);
         this.module = stt;
         this.setLayout(new GridBagLayout());
         slider =
            new TestTrainSlider(stt.getTestPercent(), stt.getTrainPercent());

         m_sampling_method_label = new JLabel();
         m_sampling_method_label.setText("Sample Method: ");
         m_sampling_method_label.setToolTipText("Select method of sampling.");
         m_sampling_method_label.setFont(tmp);

         m_sampling_methods = new JComboBox(sampling_method_labels);
         m_sampling_methods.setEditable(false);
         m_sampling_methods.setToolTipText(sampling_method_desc[stt
                                                                   .getSamplingMethod()]);
         m_sampling_methods.setSelectedIndex(stt.getSamplingMethod());
         m_sampling_methods.setFont(tmp);
         m_sampling_methods.addActionListener(this);

         m_seed_label = new JLabel("Random Seed: ");
         m_seed_label.setFont(tmp);
         m_seed = new JTextField(Integer.toString(stt.getSeed()), 5);
         m_seed.setFont(tmp);

         JLabel label = new JLabel("  Select train and test percentages:  ");
         label.setFont(tmp);
         Constrain.setConstraints(this, label, 0, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 0.0, 0.0);
         Constrain.setConstraints(this, slider, 0, 1, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0.0, 0.0);
         Constrain.setConstraints(this, label, 0, 2, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 0.0, 0.0);

         Constrain.setConstraints(this, m_sampling_method_label, 0, 3, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 0.0, 0.0);

         Constrain.setConstraints(this, m_sampling_methods, 0, 3, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0.0, 0.0);

         Constrain.setConstraints(this, m_seed_label, 0, 4, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 0.0, 0.0);

         Constrain.setConstraints(this, m_seed, 0, 4, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0.0, 0.0);

         if (stt.getSamplingMethod() == stt.RANDOM) {
            m_seed.setEnabled(true);
            m_seed_label.setEnabled(true);
         } else {
            m_seed.setEnabled(false);
            m_seed_label.setEnabled(false);
         }
      }

      /**
       * Invoked when an action occurs.
       *
       * @param e Description of parameter e.
       */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();

         if (src == this.m_sampling_methods) {
            JComboBox cb = (JComboBox) src;
            m_sampling_methods.setToolTipText(sampling_method_desc[cb
                                                                      .getSelectedIndex()]);

            if (cb.getSelectedIndex() == module.RANDOM) {
               m_seed.setEnabled(true);
               m_seed_label.setEnabled(true);
            } else {
               m_seed.setEnabled(false);
               m_seed_label.setEnabled(false);
            }
         }
      }

      /**
       * Update the fields of the module.
       *
       * @return a string indicating why the properties could not be set, or
       *         null if successfully set.
       *
       * @throws Exception             Description of exception Exception.
       * @throws PropertyVetoException Description of exception
       *                               PropertyVetoException.
       */
      public boolean updateModule() throws Exception {
         int testpercent = slider.getTest();
         int trainpercent = slider.getTrain();

         // Percentages in range 0 to 100.
         if (testpercent < 0 || testpercent > 100) {
            throw new PropertyVetoException("Test percentage must be in range 0 to 100.",
                                            null);
         }

         if (trainpercent < 0 || trainpercent > 100) {
            throw new PropertyVetoException("Train percentage must be in range 0 to 100.",
                                            null);
         }

         boolean changed = false;

         // set up the properties.
         if (module.getTestPercent() != testpercent) {
            module.setTestPercent(testpercent);
            changed = true;
         }

         if (module.getTrainPercent() != trainpercent) {
            module.setTrainPercent(trainpercent);
            changed = true;
         }

         if (
             module.getSamplingMethod() !=
                m_sampling_methods.getSelectedIndex()) {
            module.setSamplingMethod(this.m_sampling_methods
                                        .getSelectedIndex());
            changed = true;
         }

         try {
            int seed = Integer.parseInt(m_seed.getText());

            if (module.getSeed() != seed) {
               module.setSeed(seed);
               changed = true;
            }
         } catch (Exception e) {
            throw new PropertyVetoException("Error in seed field: " +
                                            e.getMessage(), null);
         }

         return changed;
      } // end method updateModule


      /**
       * A slider to set the train and test percentages simultaneously in the
       * same component.
       *
       * @author  $Author$
       * @version $Revision$, $Date$
       */
      class TestTrainSlider extends JComponent implements MouseMotionListener,
                                                          MouseListener {

         /** height. */
         private final int THEIGHT = 202;

         /** Description of field RHEIGHT. */
         private final int RHEIGHT = THEIGHT - 2;

         /** font metrics. */
         FontMetrics fm = null;

         /** constant for horizontal space. */
         final int hinc = 40;
         final Color lightBlue = new Color(220, 220, 255);


         final Color lightRed = new Color(255, 220, 220);
         final Color lightYellow = new Color(225, 255, 220);

         /** identifies the mousing operations. */
         final int SET_BOTH = 0;
         int op = SET_BOTH;
         final int SET_TEST = 1;
         final int SET_TRAIN = 2;
         final String TEST = "test";

         /** the width of the rendered width string. */
         int test_string_width;

         /** the initial position of the thumb. */
         int testPercentage = 50;

         /** the text ascent. */
         int text_ascent;

         /** the text ascent. */
         int text_descent;

         /** the height of text. */
         int text_height;

         final int THUMB_SIZE = 5;
         final String TRAIN = "train";

         /** the width of the rendered train string. */
         int train_string_width;

         /** the initial position of the thumb. */
         int trainPercentage = 50;

         /** constant for vertical space. */
         final int vinc = 7;

         /**
          * TestTrainSlider.
          *
          * @param pos_test  test postion
          * @param pos_train train position
          */
         TestTrainSlider(int pos_test, int pos_train) {
            super();
            this.setFont(new Font("Serif", Font.BOLD, 12));
            fm = this.getFontMetrics(this.getFont());
            this.text_height = fm.getHeight();
            this.text_ascent = fm.getAscent();
            this.text_descent = fm.getDescent();
            this.test_string_width = fm.stringWidth(TEST);
            this.train_string_width = fm.stringWidth(TRAIN);
            this.setTest(pos_test);
            this.setTrain(pos_train);
            this.addMouseMotionListener(this);
            this.addMouseListener(this);
            this.setBackground(Color.yellow);
         }

         /**
          * Identify the operation based on x location.
          *
          * @param x x location
          */
         private void identifyOperation(int x) {

            if (x > this.THUMB_SIZE) {

               if (x < this.getSize().width - THUMB_SIZE) {
                  this.op = SET_BOTH;
               } else {
                  this.op = SET_TRAIN;
               }
            } else {
               this.op = SET_TEST;
            }
         }

         /**
          * paint a background color indicating what is test, what is train, and
          * what is resampled.
          *
          * @param g graphics context
          */
         private void paintBacking(Graphics g) {
            int right = this.getSize().width - (THUMB_SIZE * 2);
            int bottom = this.getSize().height - 2;

            // fill the test percent
            g.setColor(this.lightRed);
            g.fillRect(this.THUMB_SIZE, 1, right, this.trainPercentage - 1);

            g.setColor(this.lightBlue);
            g.fillRect(this.THUMB_SIZE, this.testPercentage, right, bottom -
                       this.testPercentage);

            if (this.testPercentage == this.trainPercentage) {
               return;
            }

            if (this.testPercentage < this.trainPercentage) {
               g.setColor(this.lightYellow);
            } else {
               g.setColor(Color.white);
            }

            int amount = this.trainPercentage - this.testPercentage;
            g.fillRect(this.THUMB_SIZE, this.testPercentage, right, amount);

            if (this.testPercentage < this.trainPercentage) {
               g.setColor(Color.black);

               int wdth = fm.stringWidth("resampled");
               g.drawString("resampled", this.THUMB_SIZE + 24,
                            this.testPercentage + this.text_ascent);
            }
         } // end method paintBacking

         /**
          * paint the thumb indicator.
          *
          * @param g graphics context
          */
         private void paintThumb(Graphics g) {
            int comp_width = this.getSize().width;
            int comp_height = this.getSize().height;

            // draw the test thumb

            g.setColor(Color.red.brighter().brighter());
            this.paintThumb(g, 0, trainPercentage - THUMB_SIZE, THUMB_SIZE,
                            trainPercentage,
                            0, trainPercentage + THUMB_SIZE, true);

            // draw the line.
            int halfway = 4;
            int ploc = this.trainPercentage + this.text_descent;

            if (ploc < this.text_ascent) {
               ploc = this.text_ascent;
            }

            if (ploc > (comp_height - this.text_descent)) {
               ploc = comp_height - this.text_descent;
            }

            String percent = " " + Integer.toString(this.getTrain()) + "% ";
            int stringwidth = fm.stringWidth(percent);
            g.drawString(percent, halfway, ploc);

            // draw the training thumb
            g.setColor(Color.blue.brighter().brighter());

            int right = comp_width - THUMB_SIZE;
            this.paintThumb(g, comp_width, testPercentage - THUMB_SIZE, right,
                            testPercentage,
                            comp_width, testPercentage + THUMB_SIZE, true);

            // draw the line.
            ploc = testPercentage + this.text_descent;

            if (ploc < this.text_ascent) {
               ploc = this.text_ascent;
            }

            if (ploc > (comp_height - this.text_descent)) {
               ploc = comp_height - this.text_descent;
            }

            percent = " " + Integer.toString(this.getTest()) + "% ";
            stringwidth = fm.stringWidth(percent);
            halfway = right - 4 - stringwidth;
            g.drawString(percent, halfway, ploc);
         } // end method paintThumb

         /**
          * Render the thumb.
          *
          * @param g    the graphics object.
          * @param x1   the x coord of the first point.
          * @param y1   the y coord of the first point.
          * @param x2   the x coord of the second point.
          * @param y2   the y coord of the second point.
          * @param x3   the x coord of the last point.
          * @param y3   the y coord of the last point.
          * @param fill Description of parameter $param.name$.
          */
         private void paintThumb(Graphics g, int x1, int y1, int x2, int y2,
                                 int x3, int y3, boolean fill) {
            int[] Xs = new int[3];
            int[] Ys = new int[3];
            Xs[0] = x1;
            Xs[1] = x2;
            Xs[2] = x3;
            Ys[0] = y1;
            Ys[1] = y2;
            Ys[2] = y3;

            if (fill) {
               g.fillPolygon(Xs, Ys, 3);
            } else {
               g.drawPolygon(Xs, Ys, 3);
            }
         }

         /**
          * Set the test value. The percentage passed in is converted to an
          * offset.
          *
          * @param val the percent test.
          */
         private void setTest(int val) {
            testPercentage = 100 - val;
            testPercentage = (testPercentage * 2);
            testPercentage++;
         }

         /**
          * Set the train value. The percentage passed in is converted to an
          * offset.
          *
          * @param val the percent train.
          */
         private void setTrain(int val) {
            trainPercentage = val;
            trainPercentage = trainPercentage * 2;
            trainPercentage++;
         }

         /**
          * return the percent test.
          *
          * @return percent test
          */
         int getTest() {
            int current = testPercentage - 1;
            current = current / 2;

            return 100 - current;
         }

         /**
          * return the percent train.
          *
          * @return percent train
          */
         int getTrain() {
            int current = trainPercentage - 1;
            current *= 100;
            current /= RHEIGHT;

            return current;
         }

         /**
          * Maximum size is the minimum size.
          *
          * @return maximum size
          */
         public Dimension getMaximumSize() { return this.getMinimumSize(); }

         /**
          * The minimum size is (120, 202).
          *
          * @return the minimum size
          */
         public Dimension getMinimumSize() {
            return new Dimension(120, THEIGHT);
         }

         /**
          * Preferred size is the minimum size.
          *
          * @return preferred size
          */
         public Dimension getPreferredSize() { return this.getMinimumSize(); }

         /**
          * If the mouse is clicked, the thumb jumps to the vertical point
          * clicked.
          *
          * @param mouseEvent Description of parameter $param.name$.
          */
         public void mouseClicked(MouseEvent mouseEvent) {
            this.identifyOperation(mouseEvent.getX());
            this.mouseDragged(mouseEvent);
         }

         /**
          * The mouse has moved, set the field identified when the operation
          * started to the new value, if necessary.
          *
          * @param mouseEvent Description of parameter $param.name$.
          */
         public void mouseDragged(MouseEvent mouseEvent) {
            int y = mouseEvent.getY();
            int height = this.getSize().height - 1;

            if (y > height) {
               y = height;
            }

            if (y < 1) {
               y = 1;
            }

            switch (op) {

               case SET_BOTH:
                  this.trainPercentage = y;
                  this.testPercentage = y;

                  break;

               case SET_TEST:
                  this.trainPercentage = y;

                  break;

               case SET_TRAIN:
                  this.testPercentage = y;

                  break;
            }

            this.repaint();
         } // end method mouseDragged

         /**
          * Invoked when the mouse enters a component.
          *
          * @param mouseEvent Description of parameter mouseEvent.
          */
         public void mouseEntered(MouseEvent mouseEvent) { }

         /**
          * Invoked when the mouse exits a component.
          *
          * @param mouseEvent Description of parameter mouseEvent.
          */
         public void mouseExited(MouseEvent mouseEvent) { }

         /**
          * Invoked when the mouse cursor has been moved onto a component but no
          * buttons have been pushed.
          *
          * @param mouseEvent Description of parameter mouseEvent.
          */
         public void mouseMoved(MouseEvent mouseEvent) { }

         /**
          * If the mouse is pressed, the thumb jumps to the vertical point
          * clicked.
          *
          * @param mouseEvent Description of parameter $param.name$.
          */
         public void mousePressed(MouseEvent mouseEvent) {
            this.identifyOperation(mouseEvent.getX());
            this.mouseDragged(mouseEvent);
         }

         /**
          * Invoked when a mouse button has been released on a component.
          *
          * @param mouseEvent Description of parameter mouseEvent.
          */
         public void mouseReleased(MouseEvent mouseEvent) { }

         /**
          * Paint the component, a representation of a table of data.
          *
          * @param g Description of parameter $param.name$.
          */
         public void paintComponent(Graphics g) {
            g.setFont(this.getFont());

            int comp_width = this.getSize().width;
            int comp_height = this.getSize().height;
            this.paintBacking(g);
            g.setColor(Color.gray);

            // paint the box around the document.
            g.drawLine(THUMB_SIZE, 0, comp_width - THUMB_SIZE, 0);
            g.drawLine(comp_width - THUMB_SIZE, 0, comp_width - THUMB_SIZE,
                       comp_height - 1);
            g.drawLine(comp_width - THUMB_SIZE, comp_height - 1, THUMB_SIZE,
                       comp_height - 1);
            g.drawLine(THUMB_SIZE, comp_height - 1, THUMB_SIZE, 0);

            // paint some lines to make it look like a table.
            for (int where = vinc + 1; where < comp_height; where += vinc) {
               g.drawLine(THUMB_SIZE, where, comp_width - THUMB_SIZE, where);
            }

            for (
                 int where = hinc + 1;
                    where < (comp_width - THUMB_SIZE);
                    where += hinc) {
               g.drawLine(where, 1, where, comp_height - 1);
            }

            // paint some lines in there.
            g.setColor(Color.black);
            g.drawString(this.TRAIN, this.THUMB_SIZE + 24,
                         this.text_ascent + 1);
            g.drawString(this.TEST, this.THUMB_SIZE + 24,
                         comp_height - (this.text_descent + 1));
            this.paintThumb(g);
         } // end method paintComponent

         // mouse events.
      } // end class TestTrainSlider
   } // end class JSetPercentage
} // end class SimpleTrainTest
