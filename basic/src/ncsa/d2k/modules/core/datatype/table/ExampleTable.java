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
package ncsa.d2k.modules.core.datatype.table;


/**
 * <p>This is a Table with some additional features designed to support the
 * model-building process in a standard and interchangable way. Encapsulated
 * here is some definition of a train and test set, in this case taking the form
 * of two integer arrays. These integer arrays contain the indices of the rows
 * containing the train data and the test data respectively.</p>
 *
 * <p>In two additional arrays the input features and the output features are
 * defined. The output features are the ones we would like to predict, the input
 * features are those that we can use to predict them.</p>
 *
 * <p>There are several additional methods defined in this class to provide
 * transparent access to the various sets. A TestTable and a TrainTable can be
 * created from the test and train sets, respectively.</p>
 *
 * <p>It is also typical that a dataset undergo some transformations before the
 * model-building algorithm can operate on it. These transformations need to be
 * reversed before the final result can actually be ascertained. Therefore, we
 * include a list of reversable transformations that have been performed on the
 * dataset in this Table.</p>
 *
 * @author  $author$
 * @version $Revision$, $Date$
 */
public interface ExampleTable extends MutableTable {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static final long serialVersionUID = 6037200533380375292L;

   //~ Methods *****************************************************************

   /**
    * Get the ith input as a boolean.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as a boolean
    */
   public boolean getInputBoolean(int e, int i);

   /**
    * Get the ith input as a byte.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as a byte
    */
   public byte getInputByte(int e, int i);

   /**
    * Get the ith input as bytes.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as bytes.
    */
   public byte[] getInputBytes(int e, int i);

   /**
    * Get the ith input as a char.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as a char
    */
   public char getInputChar(int e, int i);

   /**
    * Get the ith input as chars.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as chars
    */
   public char[] getInputChars(int e, int i);

   /**
    * Get the ith input as a double.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as a double
    */
   public double getInputDouble(int e, int i);

   /**
    * Returns an array of ints, the indices of the input columns.
    *
    * @return an array of ints, the indices of the input columns.
    */
   public int[] getInputFeatures();

   /**
    * Get the ith input as a float.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as a float
    */
   public float getInputFloat(int e, int i);

   /**
    * Get the ith input as an int.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as an int
    */
   public int getInputInt(int e, int i);

   /**
    * Get the ith input as a long.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as a long
    */
   public long getInputLong(int e, int i);

   /**
    * Get the name of an input.
    *
    * @param  i the input index
    *
    * @return the name of the ith input.
    */
   public String getInputName(int i);

   /**
    * Returns a list of names of the input columns.
    *
    * @return a list of names of the input columns.
    */
   public String[] getInputNames();

   /**
    * Get the ith input as an Object.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as an Object.
    */
   public Object getInputObject(int e, int i);

   /**
    * Get the ith input as a short.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as a short
    */
   public short getInputShort(int e, int i);

   /**
    * Get the ith input as a String.
    *
    * @param  e the example index
    * @param  i the input index
    *
    * @return the ith input as a String
    */
   public String getInputString(int e, int i);

   /**
    * Get the type of the ith input.
    *
    * @param  i the input index
    *
    * @return the type of the ith input
    *
    * @see    ncsa.d2k.modules.core.datatype.table.ColumnTypes
    */
   public int getInputType(int i);

   /**
    * Returns the number of input features.
    *
    * @return the number of input features.
    */
   public int getNumInputFeatures();

   /**
    * Get the number of output features.
    *
    * @return the number of output features.
    */
   public int getNumOutputFeatures();

   /**
    * Return the number of examples in the testing set.
    *
    * @return the number of examples in the testing set.
    */
   public int getNumTestExamples();

   /**
    * Return the number of examples in the training set.
    *
    * @return the number of examples in the training set.
    */
   public int getNumTrainExamples();

   /**
    * Get the oth output as a boolean.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as a boolean
    */
   public boolean getOutputBoolean(int e, int o);

   /**
    * Get the oth output as a byte.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as a byte
    */
   public byte getOutputByte(int e, int o);

   /**
    * Get the oth output as bytes.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as bytes.
    */
   public byte[] getOutputBytes(int e, int o);

   /**
    * Get the oth output as a char.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as a char
    */
   public char getOutputChar(int e, int o);

   /**
    * Get the oth output as chars.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as chars
    */
   public char[] getOutputChars(int e, int o);

   /**
    * Get the oth output as a double.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as a double
    */
   public double getOutputDouble(int e, int o);

   /**
    * Returns an array of ints, the indices of the output columns.
    *
    * @return an array of ints, the indices of the output columns.
    */
   public int[] getOutputFeatures();

   /**
    * Get the oth output as a float.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as a float
    */
   public float getOutputFloat(int e, int o);

   /**
    * Get the oth output as an int.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as an int
    */
   public int getOutputInt(int e, int o);

   /**
    * Get the oth output as a long.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the ith output as a long
    */
   public long getOutputLong(int e, int o);

   /**
    * Get the name of an output.
    *
    * @param  o the output index
    *
    * @return the name of the oth output
    */
   public String getOutputName(int o);

   /**
    * Returns a list of names of the input columns.
    *
    * @return a list of names of the input columns.
    */
   public String[] getOutputNames();

   /**
    * Get the oth output as an Object.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as an Object
    */
   public Object getOutputObject(int e, int o);

   /**
    * Get the oth output as a short.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as a short
    */
   public short getOutputShort(int e, int o);

   /**
    * Get the oth output as a String.
    *
    * @param  e the example index
    * @param  o the output index
    *
    * @return the oth output as a String
    */
   public String getOutputString(int e, int o);

   /**
    * Get the type of the oth output.
    *
    * @param  o the output index
    *
    * @return the type of the oth output
    *
    * @see    ncsa.d2k.modules.core.datatype.table.ColumnTypes
    */
   public int getOutputType(int o);

   /**
    * Get the testing set.
    *
    * @return the indices of the rows of the testing set.
    */
   public int[] getTestingSet();

   /**
    * Return a reference to a Table referencing only the testing data.
    *
    * @return a reference to a Table referencing only the testing data
    */
   public Table getTestTable();

   /**
    * Get the training set.
    *
    * @return the indices of the rows of the training set.
    */
   public int[] getTrainingSet();

   /**
    * Return a reference to a Table referencing only the training data.
    *
    * @return a reference to a Table referencing only the training data.
    */
   public Table getTrainTable();

   /**
    * Return true if the any of the input or output columns contains missing
    * values.
    *
    * @return true if the any of the input or output columns contains missing
    *         values.
    */
   public boolean hasMissingInputsOutputs();

   /**
    * Return true if the ith input is nominal, false otherwise.
    *
    * @param  i the input index
    *
    * @return true if the ith input is nominal, false otherwise.
    */
   public boolean isInputNominal(int i);

   /**
    * Return true if the ith input is scalar, false otherwise.
    *
    * @param  i the input index
    *
    * @return true if the ith input is scalar, false otherwise.
    */
   public boolean isInputScalar(int i);

   /**
    * Return true if the ith output is nominal, false otherwise.
    *
    * @param  o the output index
    *
    * @return true if the ith output is nominal, false otherwise.
    */
   public boolean isOutputNominal(int o);

   /**
    * Return true if the ith output is scalar, false otherwise.
    *
    * @param  o the output index
    *
    * @return true if the ith output is scalar, false otherwise.
    */
   public boolean isOutputScalar(int o);

   /**
    * Set the input features.
    *
    * @param inputs the indexes of the columns to be used as input features.
    */
   public void setInputFeatures(int[] inputs);

   /**
    * Set the output features.
    *
    * @param outs the indexes of the columns to be used as output features.
    */
   public void setOutputFeatures(int[] outs);

   /**
    * Set the indexes of the rows in the testing set.
    *
    * @param testingSet the indexes of the items to be used to test the model.
    */
   public void setTestingSet(int[] testingSet);

   /**
    * Set the indexes of the rows in the training set.
    *
    * @param trainingSet the indexes of the items to be used to train the model.
    */
   public void setTrainingSet(int[] trainingSet);

   /**
    * Return this ExampleTable as a PredictionTable.
    *
    * @return This object as a PredictionTable
    */
   public PredictionTable toPredictionTable();
} // end interface ExampleTable
