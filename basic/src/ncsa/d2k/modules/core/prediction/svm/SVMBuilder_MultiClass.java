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
package ncsa.d2k.modules.core.prediction.svm;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.gui.Constrain;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * Builds a Support Vector Machine (SVM). This is a wrapper for the popular
 * libsvm library (the Java version). The desired SVM could be of several types
 * and kernel types.
 *
 * <p>The original libsvm can be found at
 * http://www.csie.ntu.edu.tw/~cjlin/libsvm/. It has a modified BSD licence that
 * is compatible with GPL.</p>
 *
 * @author  Xiaolei Li
 * @version $Revision$, $Date$
 */
public class SVMBuilder_MultiClass extends ComputeModule {

   //~ Instance fields *********************************************************

   /** Parameter C of C-SVC, Epsilon-SVR, and nu-SVR. Default is 1.0. */
   private double C = 1.0;

   /** Cache memory size in MB. Default is 40MB. */
   private double CacheSize = 40.0;

   /**
    * Coefficent 0 of the kernel. Applicable for the polynomial and sigmoid
    * kernels. Default is 0.
    */
   private double Coef0 = 0.0;

   /**
    * Degree of the kernel function (if a polynomial kernel is chosen). Default
    * is 3.
    */
   private double Degree = 3.0;

   /** Tolerance of termination (stopping criterion). Default is 0.001. */
   private double Eps = 0.001;

   /**
    * Gamma of the kernel function. Applicable for the polynomial, radial, and
    * sigmoid kernels only. Default is 1 / (number of attributes in the input
    * data).
    */
   private double Gamma;

   /**
    * Type of kernel. Has 4 possible choices: linear, polynomial, radial basis,
    * and sigmoid. Default is radial basis.
    */
   private int KernelType = SVMParameters.RADIAL;

   /* the following 3 parameters are for C-SVC only and haven't been
    * coded in yet.  It will require a dynamic properties module
    * because the size of the weight array depends on user input */
   // int nrWeight = 0;
   // int[] weight_label;
   // double[] weight;

   /** Parameter nu of nu-SVC, One-class SVM, and nu-SVR. Default value is 1. */
   private double Nu = 0.5;

   /** Epsilon in the loss function of epsilon-SVR. Default is 0.1. */
   private double P = 0.1;

   /**
    * Binary value (0 or 1) to disable or enable the shrinking heuristics.
    * Default is on or 1.
    */
   private int Shrinking = 1;

   /**
    * Type of SVM. Has 5 possible choices: C-SVC, nu-SVC, one-class SVM,
    * epsilon-SVR, and nu-SVR. Default is C-SVC.
    */
   private int SvmType = SVMParameters.C_SVC;

   //~ Methods *****************************************************************

   /**
    * Given the properties set by the user, create the native svm_parameter
    * class that is used by svm_train().
    *
    * @param  num_attributes The number of attributes in the input data (i.e.,
    *                        size of input vector).
    *
    * @return The parameters chosen for training the SVM encapsulated in the
    *         native svm_parameter class.
    */
   private svm_parameter createParameters(Integer num_attributes) {
      svm_parameter param = new svm_parameter();

      param.svm_type = this.SvmType;
      param.kernel_type = this.KernelType;
      param.degree = (int)this.Degree;
      param.probability=1;

      /* if the user entered 0.0, default to 1/k where k is the
       * number of attributes in the input data. */
      param.gamma = this.Gamma;

      if (this.Gamma == 0.0) {
         param.gamma = 1.0 / num_attributes.doubleValue();
      }

      param.coef0 = this.Coef0;
      param.nu = this.Nu;
      param.cache_size = this.CacheSize;
      param.C = this.C;
      param.eps = this.Eps;
      param.p = this.P;
      param.shrinking = this.Shrinking;

      /* these are hard-coded.  should be user inputs. */
      param.nr_weight = 0;
      param.weight_label = new int[0];
      param.weight = new double[0];

      return param;
   } // end method createParameters

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   protected void doit() throws Exception {

      try {

         /* get the training data */
         svm_problem[] probs = (libsvm.svm_problem[]) this.pullInput(0);

         /* create the parameters */
         svm_parameter param = createParameters((Integer) this.pullInput(1));
         svm_model[] models = new svm_model[probs.length];
         /* check for errors */
         for(int i=0; i<probs.length; i++){
         String error_msg = svm.svm_check_parameter(probs[i], param);

         if (error_msg != null) {
            System.out.println(this.getAlias() + " -- ERROR: SVMBuilder.doit()");
            System.err.print(error_msg + "\n");
         }

         /* build the actual SVM */

          models[i] = svm.svm_train(probs[i], param);
         }
         this.pushOutput(models, 0);

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(ex.getMessage());
         System.out.println("ERROR: SVMBuilder.doit()");
         throw ex;
      }
   } // end method doit

   /**
    * Get C
    *
    * @return C
    */
   public double getC() { return C; }

   /**
    * Get cache size
    *
    * @return cache size
    */
   public double getCacheSize() { return CacheSize; }

   /**
    * Get coef 0
    *
    * @return coef 0
    */
   public double getCoef0() { return Coef0; }

   /**
    * Get degree
    *
    * @return degree
    */
   public double getDegree() { return Degree; }

   /**
    * Get eps
    *
    * @return eps
    */
   public double getEps() { return Eps; }

   /**
    * Get gamma
    *
    * @return gamma
    */
   public double getGamma() { return Gamma; }


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "an array of libsvm.svm_problem, one for each output feature";

         case 1:
            return "Number of attributes in a single input.  The " +
                   "size of the input vector.";

         default:
            return "";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Training Data";

         case 1:
            return "Number of Attributes";

         default:
            return "";
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
      String[] in = { "java.lang.Object", "java.lang.Integer" };

      return in;
   }

   /**
    * Get the kernel type
    *
    * @return kernel type
    */
   public int getKernelType() { return KernelType; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<b>Overview</b>: Builds a Support Vector Machine (SVM).<p> " +
             "<b>Detailed description</b>: This is a " +
             "wrapper for the popular libsvm library (the Java " +
             "version).  It builds a Support Vector Machine (SVM) for " +
             "the given sample data.  SVMs are popular in classification" +
             " due to its marginal maximization property which finds " +
             "a decision hyperplane that maximizes the distance to" +
             " the separate classes.  This makes for better " +
             "generalization.<p>" +


      "<b>Data Restrictions</b>: The SVM can deal with binary or " +
             "multi-class classification.  The classes need to be " +
             "integers and the attribute values need to be numerical.<p>" +

      "<b>Reference</b>: Chih-Chung Chang and Chih-Jen Lin, LIBSVM : a " +
             "library for support vector machines, 2001. Software " +
             "available at http://www.csie.ntu.edu.tw/~cjlin/libsvm/." +

      "<p>Note that libsvm has a modified BSD licence that is " +
             "compatible with GPL.";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "SVM Builder"; }

   /**
    * Get nu
    *
    * @return nu
    */
   public double getNu() { return Nu; }

   /**
    * Get P
    *
    * @return P
    */
   public double getP() { return P; }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      return SVMParameters.getPropertiesDescriptions();
   }


   /**
    * Description of method getPropertyEditor.
    *
    * @return Description of return value.
    */
   public CustomModuleEditor getPropertyEditor() { return new PropEdit(); }

   /**
    * Get shrinking
    *
    * @return shrinking
    */
   public int getShrinking() { return Shrinking; }

   /**
    * Get svm type
    *
    * @return svm type
    */
   public int getSvmType() { return SvmType; }

   /**
    * Set C
    *
    * @param val new C
    */
   public void setC(double val) { C = val; }

   /**
    * Set cache size
    *
    * @param val new cache size
    */
   public void setCacheSize(double val) { CacheSize = val; }

   /**
    * Set coef0
    *
    * @param val new coef0
    */
   public void setCoef0(double val) { Coef0 = val; }

   /**
    * Set degree
    *
    * @param val new degree
    */
   public void setDegree(double val) { Degree = val; }

   /**
    * Set Eps
    *
    * @param val new Eps
    */
   public void setEps(double val) { Eps = val; }

   /**
    * Set gamma
    *
    * @param val new gamma
    */
   public void setGamma(double val) { Gamma = val; }

   /**
    * Set kernel type
    *
    * @param val new kernel type
    */
   public void setKernelType(int val) { KernelType = val; }

   /**
    * Set nu
    *
    * @param val new nu
    */
   public void setNu(double val) { Nu = val; }

   /**
    * Set P
    *
    * @param val new P
    */
   public void setP(double val) { P = val; }

   /**
    * Set shrinking
    *
    * @param val shrinking
    */
   public void setShrinking(int val) { Shrinking = val; }

   /**
    * Set svm type
    *
    * @param val new svm type
    */
   public void setSvmType(int val) { SvmType = val; }

   //~ Inner Classes ***********************************************************

   /**
    * A property editor for this module.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class PropEdit extends JPanel implements CustomModuleEditor {
      private JTextField _c = new JTextField(Double.toString(getC()));
      private JTextField _cache =
         new JTextField(Double.toString(getCacheSize()));
      private JTextField _coef = new JTextField(Double.toString(getCoef0()));
      private JTextField _degree = new JTextField(Double.toString(getDegree()));
      private JTextField _epsilon = new JTextField(Double.toString(getEps()));

      private JTextField _gamma = new JTextField(Double.toString(getGamma()));
      private JTextField _nu = new JTextField(Double.toString(getNu()));
      private JTextField _p = new JTextField(Double.toString(getP()));

      private boolean[] change;
      private JComboBox kernel_type_list =
         new JComboBox(SVMParameters.KERNEL_TYPE_NAMES);


      private JLabel[] propLabels;
      private JComboBox svm_type_list =
         new JComboBox(SVMParameters.SVM_TYPE_NAMES);

      public final String[] BOOLEANS = { "false", "true" };
      private JComboBox shrinking_list = new JComboBox(BOOLEANS);
      public final int FALSE = 0;
      public final int TRUE = 1;

       /**
        * Constructor
        */
      private PropEdit() {

         change = new boolean[SVMParameters.NUM_PROPS];

         // creating the labels
         propLabels = new JLabel[SVMParameters.NUM_PROPS];

         for (int i = 0; i < SVMParameters.NUM_PROPS; i++) {
            propLabels[i] = new JLabel(SVMParameters.PROPS_NAMES[i]);
            propLabels[i].setToolTipText(SVMParameters.PROPS_DESCS[i]);
         }

         // selecting the items in the combo boxes.
         svm_type_list.setSelectedIndex(getSvmType());
         kernel_type_list.setSelectedIndex(getKernelType());
         shrinking_list.setSelectedIndex(getShrinking());

         // adding listeners
         _gamma.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) {
                  change[SVMParameters.GAMMA] = true;
               }
            });

         _nu.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) {
                  change[SVMParameters.NU] = true;
               }
            });

         _c.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) {
                  change[SVMParameters.C] = true;
               }
            });


         _p.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) {
                  change[SVMParameters.GAMMA] = true;
               }
            });

         _coef.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) {
                  change[SVMParameters.COEF0] = true;
               }
            });

         _epsilon.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) {
                  change[SVMParameters.EPS] = true;
               }
            });


         _cache.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) {
                  change[SVMParameters.CACHE_SIZE] = true;
               }
            });

         _degree.addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) {
                  change[SVMParameters.DEGREE] = true;
               }
            });
         svm_type_list.addActionListener(new SVMTypeListener());
         kernel_type_list.addActionListener(new KernelTypeListener());
         shrinking_list.addActionListener(new ShrinkingListener());


         enableKernelDependencies();
         enableSvmDependencies();

         setLayout(new GridBagLayout());


         // adding svm type drop down list
         Constrain.setConstraints(this, propLabels[SVMParameters.SVM_TYPE], 1,
                                  0,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, svm_type_list, 2, 0,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);


         // adding parameters that are svm type dependencies
         JPanel type_panel = new JPanel(new GridBagLayout());
         Constrain.setConstraints(type_panel, propLabels[SVMParameters.C], 0, 0,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.EAST,
                                  1, 1);
         Constrain.setConstraints(type_panel, _c, 1, 0,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Constrain.setConstraints(type_panel, propLabels[SVMParameters.NU], 0,
                                  1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.EAST,
                                  1, 1);
         Constrain.setConstraints(type_panel, _nu, 1, 1,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Constrain.setConstraints(type_panel, propLabels[SVMParameters.P], 0, 2,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.EAST,
                                  1, 1);
         Constrain.setConstraints(type_panel, _p, 1, 2,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Constrain.setConstraints(this, type_panel, 2, 1,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);


         // adding kernel type drop down list
         Constrain.setConstraints(this, propLabels[SVMParameters.KERNEL_TYPE],
                                  1, 2,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);
         Constrain.setConstraints(this, kernel_type_list, 2, 2,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         // adding parameters that are kernel type dependencies
         JPanel kernel_panel = new JPanel(new GridBagLayout());

         Constrain.setConstraints(kernel_panel, propLabels[SVMParameters.GAMMA],
                                  0, 0,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.EAST,
                                  1, 1);
         Constrain.setConstraints(kernel_panel, _gamma, 1, 0,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Constrain.setConstraints(kernel_panel,
                                  propLabels[SVMParameters.DEGREE], 0, 1,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.EAST,
                                  1, 1);
         Constrain.setConstraints(kernel_panel, _degree, 1, 1,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Constrain.setConstraints(kernel_panel, propLabels[SVMParameters.COEF0],
                                  0, 2,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.EAST,
                                  1, 1);
         Constrain.setConstraints(kernel_panel, _coef, 1, 2,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Constrain.setConstraints(this, kernel_panel, 2, 3,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);


         Constrain.setConstraints(this, propLabels[SVMParameters.EPS], 1, 4,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);


         Constrain.setConstraints(this, _epsilon, 2, 4,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Constrain.setConstraints(this, propLabels[SVMParameters.CACHE_SIZE], 1,
                                  5,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Constrain.setConstraints(this, _cache, 2, 5,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Constrain.setConstraints(this, propLabels[SVMParameters.SHRINKING], 1,
                                  6,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);

         Constrain.setConstraints(this, shrinking_list, 2, 6,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);


         Constrain.setConstraints(this, new Label("\t"), 4, 0,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);


         Constrain.setConstraints(this, new Label("\t"), 0, 0,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST,
                                  1, 1);


      } // PropEdit

       /**
        * Enable components based on the kernel dependencies
        */
      private void enableKernelDependencies() {
         int selected = kernel_type_list.getSelectedIndex();

         boolean enableGamma =
            (selected == SVMParameters.POLYNOMIAL ||
                selected == SVMParameters.RADIAL ||
                selected == SVMParameters.SIGMOID);

         boolean enableCoef =
            (selected == SVMParameters.POLYNOMIAL ||
                selected == SVMParameters.SIGMOID);

         _degree.setEnabled(selected == SVMParameters.POLYNOMIAL);
         change[SVMParameters.DEGREE] = true;

         _gamma.setEnabled(enableGamma);
         change[SVMParameters.GAMMA] = true;

         _coef.setEnabled(enableCoef);
         change[SVMParameters.COEF0] = true;

      } // enableKernelDependencies

       /**
        * Enable components based on svm dependencies
        */
      private void enableSvmDependencies() {
         int selected = svm_type_list.getSelectedIndex();

         boolean enableC =
            (selected == SVMParameters.C_SVC ||
                selected == SVMParameters.EPSILON_SVR ||
                selected == SVMParameters.NU_SVR);

         boolean enableNu =
            (selected == SVMParameters.NU_SVC ||
                selected == SVMParameters.ONE_CLASS_SVM ||
                selected == SVMParameters.NU_SVR);

         _c.setEnabled(enableC);
         change[SVMParameters.C] = true;

         _nu.setEnabled(enableNu);
         change[SVMParameters.NU] = true;

         _p.setEnabled(selected == SVMParameters.EPSILON_SVR);
         change[SVMParameters.P] = true;

      } // enableSvmDependencies


      /**
       * This method is called when the property sheet is closed and the
       * properties should be committed. This method will set the changed values
       * in the module. If the properties can not be set for any reason, return
       * false
       *
       * @return true if any parameters actually changed, otherwise return
       *         false.
       *
       * @throws Exception if for any reason, the parameters can not be set,
       *                   throw an exception with an appropriate message.
       */
      public boolean updateModule() throws Exception {
         boolean didChange = false;

         if (change[SVMParameters.SVM_TYPE]) {
            setSvmType(svm_type_list.getSelectedIndex());
            didChange = true;
         }


         if (change[SVMParameters.KERNEL_TYPE]) {
            setKernelType(kernel_type_list.getSelectedIndex());
            didChange = true;
         }

         if (change[SVMParameters.SHRINKING]) {
            setShrinking(shrinking_list.getSelectedIndex());
            didChange = true;
         }


         if (change[SVMParameters.GAMMA]) {

            try {
               double val = Double.parseDouble(_gamma.getText());
               setGamma(val);
            } catch (NumberFormatException e) {
               throw new Exception("Gamma parameter must be a real number.");
            }

            didChange = true;
         } // change gamma


         if (change[SVMParameters.C]) {

            try {
               double val = Double.parseDouble(_c.getText());

               if (val <= 0) {
                  throw new Exception("C Parameter should be greater than 0");
               }

               setC(val);
            } catch (NumberFormatException e) {
               throw new Exception("C parameter should be a real number greater than 0.");
            }

            didChange = true;
         } // change c

         if (change[SVMParameters.P]) {

            try {
               double val = Double.parseDouble(_p.getText());

               if (val < 0) {
                  throw new Exception("P Parameter should be greater than or equal to 0");
               }

               setP(val);
            } catch (NumberFormatException e) {
               throw new Exception("P parameter must be a real number greater than or equal to 0.");
            }

            didChange = true;
         } // change p

         if (change[SVMParameters.NU]) {

            try {
               double val = Double.parseDouble(_nu.getText());

               if (val < 0 || val > 1) {
                  throw new Exception("nu Parameter should be between 0 and 1");
               }

               setNu(val);
            } catch (NumberFormatException e) {
               throw new Exception("NU parameter should be a real number between 0 and 1.");
            }

            didChange = true;
         } // change nu

         if (change[SVMParameters.CACHE_SIZE]) {

            try {
               double val = Double.parseDouble(_cache.getText());

               if (val <= 0) {
                  throw new Exception("Cache Size parameter should be greater than zero");
               }

               setCacheSize(val);
            } catch (NumberFormatException e) {
               throw new Exception("Cache Size parameter must be a number greater than zero.");
            }

            didChange = true;
         } // change cache size

         if (change[SVMParameters.EPS]) {

            try {
               double val = Double.parseDouble(_epsilon.getText());

               if (val <= 0) {
                  throw new Exception("Epsilon parameter should be greater than zero");
               }

               setEps(val);
            } catch (NumberFormatException e) {
               throw new Exception("Epsilon parameter should be a real number greater than 0.");
            }

            didChange = true;
         } // change c

         if (change[SVMParameters.DEGREE]) {

            try {
               double val = Double.parseDouble(_degree.getText());
               setDegree(val);
            } catch (NumberFormatException e) {
               throw new Exception("Degree parameter must be a real number.");
            }

            didChange = true;
         } // change degree

         if (change[SVMParameters.COEF0]) {

            try {
               double val = Double.parseDouble(_coef.getText());
               setCoef0(val);
            } catch (NumberFormatException e) {
               throw new Exception("Coef0 parameter should be a real number.");
            }

            didChange = true;
         } // change coef0

         return didChange;
      } // end method updateModule


       /**
        * Listen for kernel changes and update kernel dependencies
        * when kernel changes
        */
      private class KernelTypeListener implements ActionListener {

         public void actionPerformed(ActionEvent e) {
            enableKernelDependencies();
            change[SVMParameters.KERNEL_TYPE] = true;
         } // action performed

      } // KernelTypeListener

       /**
        * Listen for shrinking changes
        */
      private class ShrinkingListener implements ActionListener {
         public void actionPerformed(ActionEvent e) {
            int selected = shrinking_list.getSelectedIndex();
// setShrinking(selected);
            change[SVMParameters.SHRINKING] = true;
         }
      }

       /**
        * Listen for svm changes and update svm dependencies
        * when svm changes
        */
      private class SVMTypeListener implements ActionListener {
         public void actionPerformed(ActionEvent e) {
            enableSvmDependencies();
            change[SVMParameters.SVM_TYPE] = true;
         } // action performed

      } // SVMTypeListener
   } // class prop edit

   public String[] getOutputTypes() {
         String[] out = { "java.lang.Object" };

         return out;
   }

   public String getOutputInfo(int index) {

     switch (index) {

        case 0:
           return "An array of SVM models (in their native class - libsvm.svm_model), one for each output feature.";

        default:
           return "";
     }
  }

  public String getOutputName(int index) {

    switch (index) {

       case 0:
          return "SVM models";

       default:
          return "";
    }
 }


} // end class SVMBuilder
