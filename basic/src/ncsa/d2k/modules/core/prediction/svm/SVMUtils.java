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

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

import ncsa.d2k.modules.core.datatype.table.ExampleTable;
// import cern.colt.matrix.linalg.*;


/**
 * Utility functions for SVM
 * @author  vered
 * @version 1.0
 */
public class SVMUtils {

   //~ Static fields/initializers **********************************************

   /** Algebra */
   static public Algebra Alg;

   //~ Constructors ************************************************************

   /**
    * Creates a new SVMUtils object.
    */
   public SVMUtils() { }

   //~ Methods *****************************************************************

   /**
    * given an example table, creates a 2D double matrix with the same content,
    * as found in the input features columns.
    *
    * @param  tbl - data source.
    *
    * @return - 2D double matrix with data from the inputs features in tbl.
    */
   static public SparseDoubleMatrix2D get2DMatrix(ExampleTable tbl) {
      int numRows = tbl.getNumRows();
      int numInputs = tbl.getNumInputFeatures();
      SparseDoubleMatrix2D mtrx = new SparseDoubleMatrix2D(numRows, numInputs);

      for (int i = 0; i < numRows; i++) {

         for (int j = 0; j < numInputs; j++) {
            mtrx.set(i, j, tbl.getInputDouble(i, j));
         }
      }


      return mtrx;

   }


   /**
    * given an example table, creates a vector of doubles with the content from
    * the first output feature column in tbl. This output feature must be
    * binary. assuming the values are going to be 0 and 1, all zeros are
    * transformed into -1, to sute the Isvm class computation.
    *
    * @param  tbl - data source.
    *
    * @return - vector of doubles with the content from the first output feature
    *         column in tbl.
    */
   static public SparseDoubleMatrix1D getClassMatrix(ExampleTable tbl) {
      int numRows = tbl.getNumRows();
      SparseDoubleMatrix1D mtrx = new SparseDoubleMatrix1D(numRows);

      for (int i = 0; i < numRows; i++) {
         double dbl = tbl.getOutputDouble(i, 0);

         if (dbl == 0) {
            dbl = -1;
         }

         mtrx.set(i, dbl);
      }

      return mtrx;

   }

   /**
    * returns a 2D double matrix with the data of A and an additional column all
    * with -1.
    *
    * @param  A - data set
    *
    * @return - the dataset labeled -1 in an additional column
    */
   static public DoubleMatrix2D getE(DoubleMatrix2D A) {
      DoubleMatrix2D E = new SparseDoubleMatrix2D(A.rows(), A.columns() + 1);
      int i;
      int j;

      for (i = 0; i < A.rows(); i++) {

         for (j = 0; j < A.columns(); j++) {
            E.set(i, j, A.get(i, j));
         }

         E.set(i, j, -1.0);
      }

      return E;
   }


   /**
    * given an example table, creates a vector of doubles with the content of
    * input features in row number <codE>index</code> in <codE>tbl</code>. the
    * last double in the returned vector is 1. (this is so because of
    * computations done by ISvmModel)
    *
    * @param  tbl   - data source.
    * @param  index - row index into tbl.
    *
    * @return - vector of doubles with the content of input features in row
    *         number <codE>index</code> in <codE>tbl</code>. and an additional
    *         double with value 1, for computation done by Isvm class.
    */
   static public SparseDoubleMatrix1D getExampleMatrix(ExampleTable tbl,
                                                       int index) {
      int numInputs = tbl.getNumInputFeatures();
      SparseDoubleMatrix1D mtrx = new SparseDoubleMatrix1D(numInputs + 1);

      for (int i = 0; i < numInputs; i++) {

         mtrx.set(i, tbl.getInputDouble(index, i));
      }

      mtrx.set(numInputs, 1);

      return mtrx;

   }

   /**
    * creates the an Identity matrix with <codE>col</code> by <codE>col</code>
    * dimenssions.
    *
    * @param  col - number of columns and rows in the returned matrix
    *
    * @return - an Identity matrix.
    */
   static public DoubleMatrix2D getI(int col) {
      DoubleMatrix2D I = new SparseDoubleMatrix2D(col, col);

      for (int i = 0; i < col; i++) {
         I.set(i, i, 1);
      }

      return I;
   }


   /**
    * Prints a cern.colt matrix.
    *
    * @param A - supposed to be either DoubleMatrix1D or DoubleMatrix2D
    */
   static public void printMatrix(Object A) {
      int i;
      int j;

      if (A.getClass().getName().endsWith("1D")) {

         for (i = 0; i < ((DoubleMatrix1D) A).size(); i++) {
            System.out.print(((DoubleMatrix1D) A).get(i) + " ");
         }

         System.out.println();
         System.out.println();
      } else if (A.getClass().getName().endsWith("2D")) {

         for (i = 0; i < ((DoubleMatrix2D) A).rows(); i++) {

            for (j = 0; j < ((DoubleMatrix2D) A).columns(); j++) {
               System.out.print(((DoubleMatrix2D) A).get(i, j) + " ");
            }

            System.out.println();
         }

         System.out.println();
      }
   }


} // end class SVMUtils
