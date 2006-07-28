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

import cern.jet.math.Functions;

import java.io.IOException;
import java.io.Serializable;


/**
 * Description of class Isvm.
 *
 * @author  Hwanjo Yu (added by vered)
 * @version 1.0
 *
 * @todo this class supports only one output feature and binary
 *          classification. make this more generic?
 */
public class Isvm implements Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -8102650683593365989L;

   //~ Instance fields *********************************************************

   /** ED */
   private DoubleMatrix1D myED;

   /** EE */
   private DoubleMatrix2D myEE;

   /** Weight. */
   private DoubleMatrix1D Weight;

   /** Algebra */
   protected Algebra Alg;

   //~ Constructors ************************************************************

   /**
    * creates an Isvm object, identical to src.
    *
    * @param src Isvm to copy
    */
   public Isvm(Isvm src) {
      myEE = src.myEE.copy();
      myED = src.myED.copy();
      Weight = src.Weight.copy();
      Alg = new Algebra();
   }


   /**
    * Creates a new Isvm object.
    *
    * @param num_attr number of attributes
    */
   public Isvm(int num_attr) {
      myEE = new SparseDoubleMatrix2D(num_attr + 1, num_attr + 1);
      myED = new SparseDoubleMatrix1D(num_attr + 1);
      Weight = new SparseDoubleMatrix1D(num_attr + 1);
      Alg = new Algebra();
   }

   //~ Methods *****************************************************************

   // main function

   /**
    * for testing
    *
    * @param  args Description of parameter $param.name$.
    *
    * @throws IOException
    */
   static public void main(String[] args) throws IOException {
      int m;
      int n = 2;
      Isvm isvm = new Isvm(n);
      DoubleMatrix1D weight;

      // train all the six data points at once

      m = 6;

      DoubleMatrix2D A = new SparseDoubleMatrix2D(m, n);
      A.set(0, 0, 1);
      A.set(0, 1, 0);
      A.set(1, 0, 0);
      A.set(1, 1, -1);
      A.set(2, 0, 0);
      A.set(2, 1, 1);
      A.set(3, 0, -1);
      A.set(3, 1, -0);
      A.set(4, 0, 2);
      A.set(4, 1, -1);
      A.set(5, 0, -1);
      A.set(5, 1, 1);

      DoubleMatrix1D D = new SparseDoubleMatrix1D(m);
      D.set(0, 1);
      D.set(1, 1);
      D.set(2, -1);
      D.set(3, -1);
      D.set(4, 1);
      D.set(5, -1);
      weight = isvm.train(A, D, 0.1);
      SVMUtils.printMatrix(weight);

      // train the first four data points

      isvm.init();
      m = 4;

      DoubleMatrix2D A1 = new SparseDoubleMatrix2D(m, n);
      A1.set(0, 0, 1);
      A1.set(0, 1, 0);
      A1.set(1, 0, 0);
      A1.set(1, 1, -1);
      A1.set(2, 0, 0);
      A1.set(2, 1, 1);
      A1.set(3, 0, -1);
      A1.set(3, 1, -0);

      DoubleMatrix1D D1 = new SparseDoubleMatrix1D(m);
      D1.set(0, 1);
      D1.set(1, 1);
      D1.set(2, -1);
      D1.set(3, -1);
      weight = isvm.train(A1, D1, 0.1);
      SVMUtils.printMatrix(weight);

      // train the last two data points on top of the previous data

      m = 2;

      DoubleMatrix2D A2 = new SparseDoubleMatrix2D(m, n);
      A2.set(0, 0, 2);
      A2.set(0, 1, -1);
      A2.set(1, 0, -1);
      A2.set(1, 1, 1);

      DoubleMatrix1D D2 = new SparseDoubleMatrix1D(m);
      D2.set(0, 1);
      D2.set(1, -1);

      weight = isvm.train(A2, D2, 0.1);
      SVMUtils.printMatrix(weight);

      // test

      DoubleMatrix1D data = new SparseDoubleMatrix1D(n + 1);
      data.set(0, 2);
      data.set(1, 3);
      data.set(2, 1);
      System.out.println(isvm.classify(data));
   } // end method main

   /**
    * classify
    *
    * @param  data data to classifiy
    *
    * @return result
    */
   public double classify(DoubleMatrix1D data) {
      return Alg.mult(Weight, data);
   }

   /**
    * return a deep copy og this Isvm.
    *
    * @return return a deep copy og this Isvm.
    */
   public Isvm copy() { return new Isvm(this); }


   // public members

   /**
    * initialize myEE and myED
    */
   public void init() {
      myEE.assign(0.0);
      myED.assign(0.0);
   }


// for debugging...

   /**
    * print model contents
    */
   public void printModel() {
      System.out.println("Printing ED matrix");
      SVMUtils.printMatrix(myED);
      System.out.println("Printing EE matrix");
      SVMUtils.printMatrix(myEE);
      System.out.println("Printing Weight matrix");
      SVMUtils.printMatrix(Weight);
   }

   /**
    * train
    *
    * @param  A  matrix A
    * @param  D  Description of parameter $param.name$.
    * @param  nu Description of parameter $param.name$.
    *
    * @return Description of return value.
    */
   public DoubleMatrix1D train(DoubleMatrix2D A, DoubleMatrix1D D, double nu) {
      DoubleMatrix2D E = SVMUtils.getE(A);
      DoubleMatrix2D I = SVMUtils.getI(E.columns());

      // Part1 = E'E + I/nu

      DoubleMatrix2D Part1 = new SparseDoubleMatrix2D(E.columns(), E.columns());
      Part1 = Alg.mult(Alg.transpose(E), E);
      Part1.assign(this.myEE, Functions.plus);
      this.myEE.assign(Part1);

      I = I.assign(Functions.mult(1.0 / nu));
      Part1.assign(I, Functions.plus);

      // Part2 = E'D e

      DoubleMatrix1D Part2 = new SparseDoubleMatrix1D(E.columns());
      Part2 = Alg.mult(Alg.transpose(E), D);
      Part2.assign(this.myED, Functions.plus);
      this.myED.assign(Part2);

      // Weight = Part1 \ Part2

      Weight.assign(Alg.mult(Alg.inverse(Part1), Part2));

      return Weight;
   } // end method train


} // end class Isvm
