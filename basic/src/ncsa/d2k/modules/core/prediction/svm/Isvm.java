package ncsa.d2k.modules.core.prediction.svm;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Hwanjo Yu (added by vered)
 * @version 1.0
 *
 * @todo move to package ncsa.d2k.modules.core.prediction.svm
 *
 * @todo this class supports only one output feature and binary classification.
 * make this more generic?
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import cern.colt.matrix.impl.*;
import cern.colt.matrix.*;
import cern.colt.matrix.linalg.*;
import cern.jet.math.*;
import java.io.*;
import java.util.*;

public class Isvm implements  Serializable{

        // private members

        private  DoubleMatrix2D myEE;
       private DoubleMatrix1D myED;
        private  DoubleMatrix1D Weight;
        protected  Algebra Alg;

        /**
         * return a deep copy og this Isvm
         * @return
         */
        public Isvm copy(){
          return new Isvm(this);
        }


  //moved to IsvmUtils - vered
/*
        private DoubleMatrix2D getE(DoubleMatrix2D A) {
                DoubleMatrix2D E = new SparseDoubleMatrix2D(A.rows(), A.columns()+1);
                int i, j;

                for (i=0; i < A.rows(); i++) {
                        for (j=0; j < A.columns(); j++) {
                                E.set(i, j, A.get(i,j));
                        }
                        E.set(i, j, -1.0);
                }
                return E;
        }

        private DoubleMatrix2D getI(int col) {
                DoubleMatrix2D I = new SparseDoubleMatrix2D(col, col);
                for (int i=0; i < col; i++)
                        I.set(i, i, 1);
                return I;
        }
*/
        // constructor

        public Isvm(int num_attr) {
                myEE = new SparseDoubleMatrix2D(num_attr+1, num_attr+1);
                myED = new SparseDoubleMatrix1D(num_attr+1);
                Weight = new SparseDoubleMatrix1D(num_attr+1);
                Alg = new Algebra();
        }


        /**
         * creates an Isvm object, identical to src.
         * @param src
         */
        public Isvm(Isvm src) {
               myEE = src.myEE.copy();
               myED = src.myED.copy();
               Weight = src.Weight.copy();
               Alg = new Algebra();
       }


        // public members

        public void init() {
                myEE.assign(0.0);
                myED.assign(0.0);
        }

        public DoubleMatrix1D train(DoubleMatrix2D A, DoubleMatrix1D D, double nu) {
                DoubleMatrix2D E = SVMUtils.getE(A);
                DoubleMatrix2D I = SVMUtils.getI(E.columns());

                // Part1 = E'E + I/nu

                DoubleMatrix2D Part1 = new SparseDoubleMatrix2D(E.columns(), E.columns());
                Part1 = Alg.mult(Alg.transpose(E), E);
                Part1.assign(this.myEE, Functions.plus);
                this.myEE.assign(Part1);

                I = I.assign(Functions.mult(1.0/nu));
                Part1.assign(I, Functions.plus);

                // Part2 = E'D e

                DoubleMatrix1D Part2 = new SparseDoubleMatrix1D(E.columns());
                Part2 = Alg.mult(Alg.transpose(E), D);
                Part2.assign(this.myED, Functions.plus);
                this.myED.assign(Part2);

                // Weight = Part1 \ Part2

                Weight.assign(Alg.mult(Alg.inverse(Part1), Part2));

                return Weight;
        }

        public double classify(DoubleMatrix1D data) {
                return Alg.mult(Weight, data);
        }

        // static functions

     /*   public static void printMatrix(Object A) {
                int i, j;

                if (A.getClass().getName().endsWith("1D")) {
                        for (i=0; i < ((DoubleMatrix1D)A).size(); i++) {
                                System.out.print(((DoubleMatrix1D)A).get(i)+" ");
                        }
                        System.out.println();
                        System.out.println();
                }
                else if (A.getClass().getName().endsWith("2D")) {
                        for (i=0; i < ((DoubleMatrix2D)A).rows(); i++) {
                                for (j=0; j < ((DoubleMatrix2D)A).columns(); j++) {
                                        System.out.print(((DoubleMatrix2D)A).get(i,j) + " ");
                                }
                                System.out.println();
                        }
                        System.out.println();
                }
        }
*/




        // main function

        public static void main(String[] args) throws IOException {
                int m, n = 2;
                Isvm isvm = new Isvm(n);
                DoubleMatrix1D weight;

                // train all the six data points at once

                m = 6;
                DoubleMatrix2D A = new SparseDoubleMatrix2D(m, n);
                A.set(0,0,1);
                A.set(0,1,0);
                A.set(1,0,0);
                A.set(1,1,-1);
                A.set(2,0,0);
                A.set(2,1,1);
                A.set(3,0,-1);
                A.set(3,1,-0);
                A.set(4,0,2);
                A.set(4,1,-1);
                A.set(5,0,-1);
                A.set(5,1,1);
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
                A1.set(0,0,1);
                A1.set(0,1,0);
                A1.set(1,0,0);
                A1.set(1,1,-1);
                A1.set(2,0,0);
                A1.set(2,1,1);
                A1.set(3,0,-1);
                A1.set(3,1,-0);
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
                A2.set(0,0,2);
                A2.set(0,1,-1);
                A2.set(1,0,-1);
                A2.set(1,1,1);
                DoubleMatrix1D D2 = new SparseDoubleMatrix1D(m);
                D2.set(0, 1);
                D2.set(1, -1);

                weight = isvm.train(A2, D2, 0.1);
                SVMUtils.printMatrix(weight);

                // test

                DoubleMatrix1D data = new SparseDoubleMatrix1D(n+1);
                data.set(0, 2);
                data.set(1, 3);
                data.set(2, 1);
                System.out.println(isvm.classify(data));
        }



//for debugging...
        public void printModel(){
          System.out.println("Printing ED matrix");
          SVMUtils.printMatrix(myED);
          System.out.println("Printing EE matrix");
          SVMUtils.printMatrix(myEE);
          System.out.println("Printing Weight matrix");
          SVMUtils.printMatrix(Weight);
        }



}
