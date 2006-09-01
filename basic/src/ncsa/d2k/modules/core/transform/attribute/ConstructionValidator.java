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
package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.Table;

import java.util.HashMap;
import java.util.StringTokenizer;


/**
 * Validate that expressions can be executed on a table.
 *
 * @author  not attributable
 * @version 1.0
 */
class ConstructionValidator {

   //~ Static fields/initializers **********************************************

   /** what can we expect when parsing. */
   static public final int BEGIN = 0;

   /** constant for left, open, nothing */
   static public final int LEFT_OPEN_NOTHING = 0;

   /** constant for after open */
   static public final int AFTER_OPEN = 1;

   /** constant for left, open, close */
   static public final int LEFT_OPEN_CLOSE = 1;

   /** constant for after close */
   static public final int AFTER_CLOSE = 2;

   /** constant for after right */
   static public final int AFTER_RIGHT = 2;

   /** constant for operator, close, nothing */
   static public final int OPERATOR_CLOSE_NOTHING = 2;

   /** constant for after left */
   static public final int AFTER_LEFT = 3; // operator

   /** constant for operator. */
   static public final int OPERATOR = 3;

   /** constant for after operator */
   static public final int AFTER_OPERATOR = 4; // right

   /** constant for right */
   static public final int RIGHT = 4;

   //~ Instance fields *********************************************************

   /** the expressions */
   private String[] _expressions;

   /** the table */
   private Table _table;

   /** column label to index map. */
   private HashMap columns;

   //~ Constructors ************************************************************

   /**
    * Creates a new ConstructionValidator object.
    *
    * @param expressionss the expressions
    * @param table        the table
    */
   public ConstructionValidator(String[] expressionss, Table table) {
      _expressions = expressionss;
      _table = table;
   }

   //~ Methods *****************************************************************

   /**
    * Format a piece of the expressions for status printouts
    *
    * @param  small_exp expression
    *
    * @return formatted status printout
    */
   private String getMsg(String small_exp) {

      if (small_exp.length() == 0) {
         return " at the beginning of expression.";
      } else {
         return " after '" + small_exp + "'.";
      }

   }


   /**
    * Try to convert str to a number or a column label
    *
    * @param  str value to check
    * @param  exp (not used)
    *
    * @throws Exception when str is neither a number value or a column name
    */
   private void validate(String str, String exp) throws Exception {

      try {
         Double.parseDouble(str);
      } // try
      catch (Exception e) {

         if (!columns.containsKey(str.toUpperCase())) {
            throw new Exception("Discovered label '" + str +
                                "' .\nThe label could not be found in the input table. " +
                                "\nPlease reconfigure the module by running it with GUI.");
         }

      } // catch

   }

   /**
    * Return true if the expressions can be executed on the table.  Iterate
    * through the expressions and check each one.
    *
    * @return true if the expressions are valid
    *
    * @throws Exception when something goes wrong
    */
   public boolean validate() throws Exception {

// building a column lookup map.
      columns = new HashMap();

      for (int i = 0; i < _table.getNumColumns(); i++) {

         if (
             _table.isColumnNumeric(i) ||
                (_table.getColumnType(i) == ColumnTypes.BOOLEAN)) {
            columns.put(_table.getColumnLabel(i).toUpperCase(), new Integer(i));
         }
      }

// for each expression
      for (int i = 0; i < _expressions.length; i++) {

         boolean end = true; // at beginning, after right hand operand and
                             // after closing parenthese (if parenthese are
                             // balanced)
         int paren_balance = 0;

// initializing a tokenizer.
         StringTokenizer tok =
            new StringTokenizer(_expressions[i], " \t()*-%/+|&\\", true);

         String curTok; // will hold current token
         String small_exp = ""; // will hold parsed expression so far
         int expected = this.BEGIN; // what tokens are expected now.
         String right = ""; // will hold current right hand operand
         String left = ""; // will hold current left hand operand


         // start parsing
         while (tok.hasMoreTokens()) {
            curTok = tok.nextToken();

            // inspecting the first char of the token
            switch (curTok.charAt(0)) {
               // cases of a delimiter first:

               // if it is a white space - skip
               case ' ':
               case '\t':
                  break;

               // if it is a calculus operator
               case '+':
               case '-':
               case '*':
               case '%':
               case '/':
// verifying that an operator is expected.
                  if (
                      expected == this.OPERATOR ||
                         expected == this.OPERATOR_CLOSE_NOTHING) {
// if this comes right after a left hand operand
                     if (expected == this.AFTER_LEFT) {

                        // the operand needs to be validated
                        validate(left, small_exp);

                        // and added to the parsed expression so far
                        small_exp += " " + left;
                     } // if after left

                     // if this comes right after a right hand operand
                     if (expected == this.AFTER_RIGHT) {

                        // the operand needs to be validated
                        validate(right, small_exp);

                        // and added to the parsed expression so far
                        small_exp += " " + right;
                     } // if after left


// now we are expecting for something after an operator
                     expected = this.AFTER_OPERATOR;
                     end = false;
// updating parsed expression
                     small_exp += " " + curTok;
                  } else {


                     throw new Exception("Discovered operator '" + curTok +
                                         "'" + getMsg(small_exp));
                  }

                  break;

// if it is a boolean operator - the validating is similar except for an
// additional validation of second half of the operator
               case '&':
               case '|':


                  if (
                      expected == this.OPERATOR ||
                         expected == this.OPERATOR_CLOSE_NOTHING) {

                     if (expected == this.AFTER_LEFT) {
                        validate(left, small_exp);
                        small_exp += " " + left;
                     }

                     // getting the second half of the operator
                     String endOp = tok.nextToken();

                     // if the second half is equal to the first one - all's
                     // well
                     if (endOp.equals(curTok)) {
                        expected = this.AFTER_OPERATOR;
                        end = false;
                        small_exp += " " + curTok + endOp;
                     } // if
                     else {


                        throw new Exception("Discovered operator '" + curTok +
                                            "'" + getMsg(small_exp) +
                                            " and a '" + endOp +
                                            "' after that.");
                     }
                  } else {


                     throw new Exception("Discovered operator '" + curTok +
                                         "'" + getMsg(small_exp));
                  }

                  break;


               // parentheses:
               // openning
               case '(':


                  if (
                      expected == this.LEFT_OPEN_CLOSE ||
                         expected == this.LEFT_OPEN_NOTHING) {
                     expected = this.AFTER_OPEN;
                     small_exp += " " + curTok;
                     paren_balance++;
                     end = false;
                  } // if
                  else {
                     throw new Exception("Discovered opening parentheses " +
                                         getMsg(small_exp));
                  }

                  break;
// if this is a closing one

               case ')':


                  if (
                      expected == this.LEFT_OPEN_CLOSE ||
                         expected == this.OPERATOR_CLOSE_NOTHING) {
// if the last token was a right hand operand - it needs to be validted and added
                     if (expected == this.AFTER_RIGHT) {
                        validate(right, small_exp);
                        small_exp += " " + right;
                     }

                     expected = this.AFTER_CLOSE;
                     paren_balance--;

                     if (paren_balance < 0) {
                        throw new Exception("Unbalanced parentheses!! Please reconfigure module via a GUI run.");
                     }

                     if (paren_balance == 0) {
                        end = true;
                     }

                     small_exp += " " + curTok;
                  } // if
                  else {
                     throw new Exception("Discovered closing parentheses " +
                                         getMsg(small_exp));
                  }

                  break;


// special case of back slash. this one comes always before a hyphen.
               case '\\':

                  // checking if last token was right hand or left hand. if not
                  // raising an exception.
                  if (
                      !(expected == this.AFTER_RIGHT ||
                           expected == this.AFTER_LEFT)) {
                     throw new Exception("Discovered '" + curTok +
                                         getMsg(small_exp));
                  }

// all's well - keeping on validating.
                  // getting the last operand
                  String prevTok;

                  if (expected == this.AFTER_RIGHT) {
                     prevTok = right;
                  } else {
                     prevTok = left;
                  }

                  // getting the hyphen

                  curTok = tok.nextToken();

                  // validating that it is indeed a hyphen
                  if (!curTok.equals("-")) {
                     throw new Exception("Discovered '" + curTok + "' after: " +
                                         small_exp + " " + prevTok);
                  }

                  // everything is ok - parsing the second half
                  curTok = tok.nextToken();

                  // adjusting right/left
                  if (expected == this.AFTER_RIGHT) {
                     right += "-" + curTok;
                  } else {
                     left += "-" + curTok;
                  }

                  break;


               default: // this is a left hand or a right hand.

                  if (
                      expected == this.LEFT_OPEN_CLOSE ||
                         expected == this.LEFT_OPEN_NOTHING) {
                     expected = this.AFTER_LEFT;
                     left = curTok;
                  } else if (expected == this.RIGHT) {
                     expected = this.AFTER_RIGHT;
                     right = curTok;
                  } else {
                     throw new Exception("Discovered '" + curTok + "'" +
                                         getMsg(small_exp));
                  }


                  break;

            } // switch char at 0

         } // while has more tokens

         // this is the end of the expression. if the last token was a right
         // hand operand it is needed to be validated
         if (expected == AFTER_RIGHT) {
            validate(right, small_exp);
            end = true;
         }

         if (!(paren_balance == 0)) {
            throw new Exception("Unbalanced parentheses!\nPlease reconfigure the module via a GUI run.");
         }

         if (!end) {
            throw new Exception("The expression '" + small_exp +
                                "' is invalid!\n" +
                                "Please reconfigure the moduel via g GUI run.");
         }


      } // for i


      return true;
   } // validate

} // ConstructionValidator
