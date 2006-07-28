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
package ncsa.d2k.modules.core.prediction.evaluators;

import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.util.Random;


/**
 * A bunch of utility methods.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class Utility {

   //~ Methods *****************************************************************


   /**
    * convert some bytes to an unsigned int
    *
    * @param  byte1 byte 1
    * @param  byte2 byte 2
    *
    * @return int value
    */
   static public int audioBytesToInt(byte byte1, byte byte2) {
      int unsignedInt =
         unsignedByteToInt(byte2) * 256 + unsignedByteToInt(byte1);

      if (unsignedInt >= 32768) {
         return unsignedInt - 65536;
      } else {
         return unsignedInt;
      }
   }


   /**
    * Given a two d array of doubles, create a table.
    *
    * @param  data        double values
    * @param  inputNames  names of input attributes
    * @param  outputNames names of output attributes
    * @param  inputs      the input attributes
    * @param  outputs     the output attributes
    * @param  count       number of examples
    *
    * @return given a two d array of doubles, create a table.
    */
   static public ExampleTable getTable(double[][] data, String[] inputNames,
                                       String[] outputNames, int[] inputs,
                                       int[] outputs, int count) {
      Column[] cols = new Column[data.length];
      int index = 0;

      for (int i = 0; i < inputs.length; i++, index++) {

         if (data.length != count) {
            double[] tmp = new double[count];
            System.arraycopy(data[index], 0, tmp, 0, count);
            data[index] = tmp;
         }

         cols[index] = new DoubleColumn(data[index]);
         cols[index].setLabel(inputNames[i]);
      }

      for (int i = 0; i < outputs.length; i++, index++) {

         if (data.length != count) {
            double[] tmp = new double[count];
            System.arraycopy(data[index], 0, tmp, 0, count);
            data[index] = tmp;
         }

         cols[index] = new DoubleColumn(data[index]);
         cols[index].setLabel(outputNames[i]);
      }

      MutableTable mt = new MutableTableImpl(cols);
      ExampleTable et = mt.toExampleTable();
      et.setInputFeatures(inputs);
      et.setOutputFeatures(outputs);

      return et;
   } // end method getTable

   /**
    * Given a comma-separated list of values, split it into its values
    *
    * @param  list comma-separated list of values
    *
    * @return the individual values in the list
    */
   static public String[] parseCSVList(String list) {

      int length = list.length();

      if (length == 0) {
         return new String[0];
      }

      // count number of commas
      int numCommas = 0;

      for (int i = 0; i < length; i++) {

         if (list.charAt(i) == ',') {
            numCommas++;
         }
      }

      int numStrings = numCommas + 1;
      String[] strings = new String[numStrings];

      // parse gene names into strings
      int stringIndex = 0;
      int lastCharIndex = 0;

      for (int i = 0; i < length; i++) {

         if (list.charAt(i) == ',') {
            strings[stringIndex++] = list.substring(lastCharIndex, i);
            lastCharIndex = i + 1;
         }
      }

      strings[stringIndex++] = list.substring(lastCharIndex, length);

      return strings;
   } // end method parseCSVList

   /*****************************************************************************/
   /* This function returns a random integer between min and max (both
    */
   /* inclusive).
    */
   /*****************************************************************************/
   static public int randomInt(Random generator, int min, int max) {
      return (int) ((generator.nextDouble() * (max - min + 1)) + min);
   }

   /**
    * Randomize an array of ints
    *
    * @param  generator   random number generator
    * @param  data        the int array
    * @param  numElements the number of elements
    *
    * @throws Exception Description of exception Exception.
    */
   static public void randomizeIntArray(Random generator, int[] data,
                                        int numElements) throws Exception {
      int temp;
      int rand_index;

      for (int i = 0; i < numElements - 1; i++) {
         rand_index = randomInt(generator, i + 1, numElements - 1);
         temp = data[i];
         data[i] = data[rand_index];
         data[rand_index] = temp;
      }
   }

   /**
    * Given an array of strings and a string, find the index of the string
    * in the array.
    *
    * @param  strings array of strings
    * @param  string  value to find in string array
    *
    * @return index of string in the array, or -1 if not found
    */
   static public int stringToIndex(String[] strings, String string) {

      int numStrings = strings.length;
      int index = -1;

      for (int i = 0; i < numStrings; i++) {

         if (strings[i].equals(string)) {
            index = i;

            break;
         }
      }

      return index;
   }

   /**
    * Convert an unsigned byte to an int
    *
    * @param  byte1 the byte
    *
    * @return int value
    */
   static public int unsignedByteToInt(byte byte1) {

      if (byte1 >= 0) {
         return (int) byte1;
      } else {
         return (int) 256 + (int) byte1;
      }
   }


} // end class Utility
