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
/*
 * Created on Mar 11, 2003
 *
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code Template
 */
package ncsa.d2k.modules.core.transform.binning;

import ncsa.d2k.modules.core.datatype.table.Table;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Convenience factory to create BinDescriptors
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class BinDescriptorFactory {

   //~ Static fields/initializers **********************************************

   /** empty string */
   static protected final String EMPTY = "";
    /** colon */
   static protected final String COLON = " : ";
    /** comma */
   static protected final String COMMA = ",";
    /** dots */
   static protected final String DOTS = "...";
    /** open parenthesis */
   static protected final String OPEN_PAREN = "(";
    /** close parenthesis */
   static protected final String CLOSE_PAREN = ")";
    /** open bracket */
   static protected final String OPEN_BRACKET = "[";
    /** close bracket */
   static protected final String CLOSE_BRACKET = "]";

   /** the number of decimal places to use in rounding. */
   static protected int decimalPos = 3;

   //~ Methods *****************************************************************

   /**
    * Create a bin descriptor that goes from min to positive infinity.
    *
    * @param  col column index
    * @param  min min value
    * @param  nf  number formatter (not used)
    * @param  tbl table
    *
    * @return bin that goes from min to positive infinity
    */
   static public BinDescriptor createMaxNumericBinDescriptor(int col,
                                                             double min,
                                                             NumberFormat nf,
                                                             Table tbl) {
      StringBuffer nameBuffer = new StringBuffer();
      nameBuffer.append(OPEN_PAREN);

      // ANCA nameBuffer.append(nf.format(min));
      min = round(min, decimalPos);
      nameBuffer.append(min);
      nameBuffer.append(COLON);
      nameBuffer.append(DOTS);
      nameBuffer.append(CLOSE_BRACKET);

      BinDescriptor nb =
         new NumericBinDescriptor(col, nameBuffer.toString(),
                                  min, Double.POSITIVE_INFINITY,
                                  tbl.getColumnLabel(col));

      return nb;
   }

   /**
    * Create a numeric bin that goes from Double.NEGATIVE_INFINITY to
    * Double.POSITIVE_INFINITY.
    *
    * @param  col column index
    * @param  tbl table
    *
    * @return a numeric bin that goes from Double.NEGATIVE_INFINITY to
    *         Double.POSITIVE_INFINITY.
    */
   static public BinDescriptor createMinMaxBinDescriptor(int col, Table tbl) {
      StringBuffer nameBuffer = new StringBuffer();
      nameBuffer.append(OPEN_BRACKET);
      nameBuffer.append(DOTS);
      nameBuffer.append(COLON);
      nameBuffer.append(DOTS);
      nameBuffer.append(CLOSE_BRACKET);

      BinDescriptor nb =
         new NumericBinDescriptor(col, nameBuffer.toString(),
                                  Double.NEGATIVE_INFINITY,
                                  Double.POSITIVE_INFINITY,
                                  tbl.getColumnLabel(col));

      return nb;
   }

   /**
    * Create a numeric bin that goes from Double.NEGATIVE_INFINITY to max.
    *
    * @param  col column index
    * @param  max max value
    * @param  nf  number formatter (not used)
    * @param  tbl table
    *
    * @return create a numeric bin that goes from Double.NEGATIVE_INFINITY to
    *         max.
    */
   static public BinDescriptor createMinNumericBinDescriptor(int col,
                                                             double max,
                                                             NumberFormat nf,
                                                             Table tbl) {
      StringBuffer nameBuffer = new StringBuffer();
      nameBuffer.append(OPEN_BRACKET);
      nameBuffer.append(DOTS);
      nameBuffer.append(COLON);
      max = round(max, decimalPos);

      // ANCA nameBuffer.append(nf.format(max));
      nameBuffer.append(max);
      nameBuffer.append(CLOSE_BRACKET);

      BinDescriptor nb =
         new NumericBinDescriptor(col, nameBuffer.toString(),
                                  Double.NEGATIVE_INFINITY, max,
                                  tbl.getColumnLabel(col));

      return nb;
   }

   /**
    * Create a bin for missing values
    *
    * @param  idx column index
    * @param  tbl table
    *
    * @return Description of return value.
    */
   static public BinDescriptor createMissingValuesBin(int idx, Table tbl) {
      String[] vals = { tbl.getMissingString() };

      return new TextualBinDescriptor(idx, "Unknown", vals,
                                      tbl.getColumnLabel(idx));
   }

   /**
    * Create a numeric bin that goes from min to max.
    *
    * @param  col column index
    * @param  min minimum value
    * @param  max maximum value
    * @param  nf  number formatter (not used)
    * @param  tbl table
    *
    * @return create a numeric bin that goes from min to max.
    */
   static public BinDescriptor createNumericBinDescriptor(int col, double min,
                                                          double max,
                                                          NumberFormat nf,
                                                          Table tbl) {
      StringBuffer nameBuffer = new StringBuffer();
      min = round(min, decimalPos);
      max = round(max, decimalPos);
      nameBuffer.append(OPEN_PAREN);

      // ANCA nameBuffer.append(nf.format(min));
      nameBuffer.append(min);
      nameBuffer.append(COLON);

      // ANCA nameBuffer.append(nf.format(max));
      nameBuffer.append(max);
      nameBuffer.append(CLOSE_BRACKET);

      BinDescriptor nb =
         new NumericBinDescriptor(col, nameBuffer.toString(),
                                  min, max,
                                  tbl.getColumnLabel(col));

      return nb;
   }

   /**
    * Create a bin that holds the specified string values
    *
    * @param  idx  column index
    * @param  name bin name
    * @param  vals selected string values
    * @param  tbl  table
    *
    * @return put your documentation comment here.
    */
   static public BinDescriptor createTextualBin(int idx, String name,
                                                String[] vals, Table tbl) {
      return new TextualBinDescriptor(idx, name, vals, tbl.getColumnLabel(idx));
   }


   /**
    * Round a number
    *
    * @param  number           number
    * @param  decimalPositions (not used)
    *
    * @return rounded number
    */
   static public double round(double number, int decimalPositions) {

      NumberFormat nf = new DecimalFormat("0.##");
      // ANCA the code below will print 1873.02 like 1,873.02 nf =
      // NumberFormat.getInstance();
      // nf.setMaximumFractionDigits(decimalPositions);

      String rounded = nf.format(number).toString();
      // System.out.println("rounded " + rounded); // 0.67

      return (new Double(rounded)).doubleValue();

   }


} // end class BinDescriptorFactory
