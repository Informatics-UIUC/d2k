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


import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.prediction.UpdateableModelModule;

import java.io.Serializable;


/**
 * Makes predictions on a set of examples, utilizing the svm algorithm.  This
 * model wraps the Isvm class and serves as an interface between Isvm and D2K
 * Toolkit.
 * <P>Note: There is a great importancy to the value of 'Number of Input
 * Features' in the generating module of this model 'CreateIsvmModel'. If the
 * value of this property does not match the number of input features of the
 * input table of this model, (or the Table according to which the update is
 * being done), then an exception is thrown and the itinerary is aborted.</P>
 * <P><U>Data Restriction</U>: The input data to this model should be scalarized
 * by <i>Scalarize Nominals</i>, so that each attribute is binary and numeric as
 * well.  The model can handle only one output feature that should also be binary.
 * The values of the output feature should be 1 and -1.</P>
 *
 * @author  vered
 * @version 1.0
 */
public class IsvmModel extends PredictionModelModule
   implements UpdateableModelModule, Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 9104942579917665068L;

   //~ Instance fields *********************************************************


   /** model */
   protected Isvm model;

   /** nu parameter. */
   protected double nu;

   /** number of attributes */
   protected int numAttributes;

   //~ Constructors ************************************************************

   /**
    * Creates a new IsvmModel object.
    */
   public IsvmModel() { }

   /**
    * Creates a new IsvmModel object.
    *
    * @param num number of attributes
    */
   public IsvmModel(int num) { numAttributes = num; }

   //~ Methods *****************************************************************

   /**
    * Make predictions on a set of examples.
    *
    * @param  pt table containing examples to predict
    *
    * @throws java.lang.Exception when something goes wrong
    */
   protected void makePredictions(PredictionTable pt)
      throws java.lang.Exception {

      if (numAttributes != pt.getNumInputFeatures()) {
         throw new Exception("Incompatible number of input features! This model " +
                             "was initialized with " + numAttributes +
                             " input features " +
                             "but the prediction table has " +
                             pt.getNumInputFeatures() +
                             "input features.");
      }


      int[] predIdx = pt.getPredictionSet();

      for (int i = 0; i < pt.getNumRows(); i++) {

         for (int j = 0; j < predIdx.length; j++) {
            SparseDoubleMatrix1D example = SVMUtils.getExampleMatrix(pt, i);
            double pred = model.classify(example);

            // converting the prediction to be 1 or -1.
            if (pred > 0) {
               pred = 1;
            } else {
               pred = -1;
            }

            pt.setDoublePrediction(pred, i, j);
         }
      }
   } // end method makePredictions


   /**
    * the copy method is needed to create a deep copy of the model. this way
    * other modules can use the older versions of the model without being
    * affected by the update.
    *
    * @return a copy
    */
   public UpdateableModelModule copy() {
      IsvmModel ret = new IsvmModel();
      ret.model = model.copy();
      ret.nu = nu;
      ret.numAttributes = numAttributes;

      return ret;
   }


   /**
    * The input is a Table.
    *
    * @return the input types
    */
   public String[] getInputTypes() {
      String[] in = { "ncsa.d2k.modules.core.datatype.table.ExampleTable" };

      return in;
   }

   /**
    * Get the model that was built.
    *
    * @return model
    */
   public Isvm getModel() { return model; }


   /**
    * Describe the function of this module.
    *
    * @return the description of this module.
    */
   public String getModuleInfo() {
      return "Makes predictions on a set of examples, utilizing the svm algorithm." +
             "This model wraps the Isvm class and serves as an interface between Isvm and D2K Toolkit." +
             "<P>Note: There is a great importancy to the value of 'Number of Input Features' in " +
             "the generating module of this model 'CreateIsvmModel'. If the value of this " +
             "property does not match the number of input features of the input table of this" +
             "model, (or the Table according to which the update is being done), then " +
             "an exception is thrown and the itinerary is aborted.</P>" +
             "<P><U>Data Restriction</U>: The input data to this model should be scalarized " +
             "by <i>Scalarize Nominals</i>, so that each attribute is binary and numeric as well. " +
             "The model " +
             "can handle only one output feature that should also be binary. " +
             "The values of the output feature should be 1 and -1.</P>";
   }

   /**
    * Get nu
    *
    * @return nu
    */
   public double getNu() { return nu; }

   /**
    * Get number of attributes
    *
    * @return number of attributes
    */
   public int getNumAttributes() { return numAttributes; }


   /**
    * Return a list of the property descriptions.
    *
    * @return a list of the property descriptions.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[1];
      pds[0] = new PropertyDescription("nu", "Nu", "SVM Parameter nu");

      return pds;
   }

   /**
    * A model producer module will generate the model and call the init method.
    */
   public void init() {
      model = new Isvm(numAttributes);
      model.init();
   }

   /**
    * debugging purposes.
    */
   public void printModel() { model.printModel(); }

   /**
    * Set the Isvm model
    *
    * @param _model Isvm model
    */
   public void setModel(Isvm _model) { model = _model; }

   /**
    * Set nu
    *
    * @param dbl new nu
    */
   public void setNu(double dbl) { nu = dbl; }

   /**
    * Set number of attributes
    *
    * @param num number of attributes
    */
   public void setNumAttributes(int num) { numAttributes = num; }


   /**
    * Retrain on a new data set.
    *
    * @param  tbl table to train on
    *
    * @throws Exception when something goes wrong
    */
   public void update(ExampleTable tbl) throws Exception {

      if (numAttributes != tbl.getNumInputFeatures()) {
         throw new Exception("Incompatible number of input features! This model " +
                             "was initialized with " + numAttributes +
                             " input features " +
                             "but the input table has " +
                             tbl.getNumInputFeatures() +
                             "input features.");
      }

      setTrainingTable(tbl);

      SparseDoubleMatrix2D data = SVMUtils.get2DMatrix(tbl);
      SparseDoubleMatrix1D classes = SVMUtils.getClassMatrix(tbl);

      model.train(data, classes, nu);
      // this.printModel();


   }

} // end class IsvmModel
