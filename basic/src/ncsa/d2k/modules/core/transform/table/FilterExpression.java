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

import ncsa.d2k.modules.core.datatype.Expression;
import ncsa.d2k.modules.core.datatype.ExpressionException;
import ncsa.d2k.modules.core.datatype.MissingValueException;
import ncsa.d2k.modules.core.datatype.table.Table;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A <code>FilterExpression</code> object encapsulates a single expression of
 * arbitrary length that specifies a set of conditions under which rows should
 * (or should not) be filtered out of a <code>Table</code>.
 *
 * @author  gpape
 * @version $Revision$, $Date$
 */
public class FilterExpression implements Expression, java.io.Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 7484261285369521129L;


   /** constant for is null */
   static public final String IS_NULL = " *is +null";

   /** constant for is */
   static public final String IS = " *is ";

   /** constant for is not null. */
   static public final String IS_NOT_NULL = " *is +not +null";

   /******************************************************************************/
   /* The filter expression string is parsed into a tree in which each node is
    */
   /* either a subexpression or a terminal.
    */
   /*
    */
   /* subexpression:   <terminal> <boolean operator> <terminal>
    */
   /* terminal:        <element> <comparison operator> <element>
    */
   /******************************************************************************/

   /** equality */
   static private final int OP_EQ = 100;
    /** */
   static private final int OP_IS = 101;
    /** not equal to */
   static private final int OP_NEQ = 105;
    /** less than */
   static private final int OP_LT = 110;
    /** less than/equal to */
   static private final int OP_LTE = 115;
    /** greater than */
   static private final int OP_GT = 120;
    /** greater than equal to */
   static private final int OP_GTE = 125;
    /** boolean AND operator */
   static private final int BOOL_AND = 130;
    /**  boolean OR operator */
    static private final int BOOL_OR = 135;

   // NOTE: if you add any more boolean operators to
   // the following list, make       sure that their
   // int values remain sorted such that lesser int
   // values       correspond to greater operator
   // precedence. (This is how the parser
   // operates.)



   //~ Instance fields *********************************************************

   /** true if missing values should be included */
   private boolean includeMissingValues = true;

   /** column label to column index map */
   private HashMap labelToIndex;

   /** root of expression tree */
   private Node root;

   /** table */
   private Table table;

   //~ Constructors ************************************************************

   /**
    * Constructor for a <code>FilterExpression</code> object that should use the
    * given <code>Table</code> as its context.
    *
    * @param table          the <code>Table</code> that this <code>
    *                       FilterExpression</code> object should reference
    * @param includeMissing indicates if missing values should be included in
    *                       the filtered table or not.
    */
   public FilterExpression(Table table, boolean includeMissing) {
      this.table = table;
      labelToIndex = new HashMap();

      for (int i = 0; i < table.getNumColumns(); i++) {
         labelToIndex.put(table.getColumnLabel(i), new Integer(i));
      }

      includeMissingValues = includeMissing;
   }

   //~ Methods *****************************************************************

   /***************************************************************************
   * The expression string is broken down into subexpressions and parsed
   *
   * recursively.
   *  @param expression the expression
    * @return root of expression evaluation tree
   *
   **************************************************************************/
   protected Node parse(String expression) throws ExpressionException {

      if (expression.length() == 0) {
         return null;
      }

      char c;

      // we are interested in the shallowest *boolean* operator of least

      // precedence (i.e., we break ties by going to the right). if we don't

      // find one, we must be at a terminal.

      boolean booleanOperatorFound = false;

      int currentDepth = 0;
      int leastDepth = Integer.MAX_VALUE;
      int maximumDepth = 0;
      int leastPrecedenceType = BOOL_AND;
      int leastPrecedencePosition = -1;

      int leftParens = 0;
      int rightParens = 0;

      for (int i = 0; i < expression.length(); i++) {

         c = expression.charAt(i);

         switch (c) {

            case '(':
               currentDepth++;
               leftParens++;

               break;

            case ')':
               currentDepth--;
               rightParens++;

               break;

            case '&':

               if (expression.charAt(i + 1) != '&') {
                  throw new ExpressionException("FilterExpression: invalid single '&' at position " +
                                                i);
               }

               booleanOperatorFound = true;

               if (
                   currentDepth < leastDepth ||
                      currentDepth == leastDepth &&
                      BOOL_AND >= leastPrecedenceType) {

                  leastDepth = currentDepth;

                  leastPrecedenceType = BOOL_AND;

                  leastPrecedencePosition = i;

               }

               i++;

               break;

            case '|':

               if (expression.charAt(i + 1) != '|') {
                  throw new ExpressionException("FilterExpression: invalid single '|' at position " +
                                                i);
               }

               booleanOperatorFound = true;

               if (
                   currentDepth < leastDepth ||
                      currentDepth == leastDepth &&
                      BOOL_OR >= leastPrecedenceType) {

                  leastDepth = currentDepth;

                  leastPrecedenceType = BOOL_OR;

                  leastPrecedencePosition = i;

               }

               i++;

               break;

               // !: if we supported escape sequences, we'd handle them here...

         }

         if (currentDepth > maximumDepth) {
            maximumDepth = currentDepth;
         }

      } // end for

      if (leftParens != rightParens) {
         throw new ExpressionException("FilterExpression: parentheses do not match.");
      }

      if (leastDepth > maximumDepth) { // ...there were no parentheses

         leastDepth = 0;
      }

      if (booleanOperatorFound) { // ...we must recurse

         // remove extraneous parentheses first

         for (int i = 0; i < leastDepth; i++) {

            expression = expression.trim();

            expression = expression.substring(1, expression.length() - 1);

            leastPrecedencePosition--;

         }

         return new Subexpression(leastPrecedenceType,
                                  parse(expression.substring(0,
                                                             leastPrecedencePosition)
                                                  .trim()),
                                  parse(expression.substring(leastPrecedencePosition +
                                                             2,
                                                             expression
                                                                .length())
                                                  .trim()));

      } else { // ...this is a terminal

         // remove extraneous parentheses first (slightly different here)

         for (int i = 0; i < maximumDepth; i++) {

            expression = expression.trim();

            expression = expression.substring(1, expression.length() - 1);

         }

         return parseTerminal(expression);

      }

   } // end method parse

   /**
    * Parse an expression into an Element
    *
    * @param  expression expression
    *
    * @return an element, either NominalElement, ColumnElement, or ScalarElement
    *
    * @throws ExpressionException when something goes wrong
    */
   protected Element parseElement(String expression)
      throws ExpressionException {

      if (expression.length() == 0) {
         throw new ExpressionException("FilterExpression: encountered empty element");
      }

      double value = Double.NaN;

      try {

         value = Double.parseDouble(expression);

      } catch (Exception e) {

         if (expression.charAt(0) == '\'') {

            if (expression.indexOf('\'', 1) != expression.length() - 1) {
               throw new ExpressionException("invalid attribute value: " +
                                             expression);
            } else {
               return new NominalElement(expression.substring(1, expression
                                                                 .length() -
                                                              1));
            }

         }

         return new ColumnElement(expression);

      }

      return new ScalarElement(value);

   } // end method parseElement

   /**
    * Parse a terminal expression
    *
    * @param  expression expression
    *
    * @return Node Terminal node
    *
    * @throws ExpressionException when something goes wrong
    */
   protected Node parseTerminal(String expression) throws ExpressionException {

      char c;
      char d;

      boolean operatorFound = false;

      for (int i = 0; i < expression.length(); i++) {

         c = expression.charAt(i);

         switch (c) {

            case '=':

               if (expression.charAt(i + 1) != '=') {
                  throw new ExpressionException("FilterExpression: invalid single '=' in expression");
               }

               return new Terminal(OP_EQ,
                                   parseElement(expression.substring(0, i)
                                                          .trim()),
                                   parseElement(expression.substring(i + 2,
                                                                     expression
                                                                        .length())
                                                          .trim()));

            case '!':

               if (expression.charAt(i + 1) != '=') {
                  throw new ExpressionException("FilterExpression: invalid single '!' in expression");
               }

               return new Terminal(OP_NEQ,
                                   parseElement(expression.substring(0, i)
                                                          .trim()),
                                   parseElement(expression.substring(i + 2,
                                                                     expression
                                                                        .length())
                                                          .trim()));

            case '>':

               if (expression.charAt(i + 1) == '=') {
                  return new Terminal(OP_GTE,
                                      parseElement(expression.substring(0, i)
                                                             .trim()),
                                      parseElement(expression.substring(i + 2,
                                                                        expression
                                                                           .length())
                                                             .trim()));
               } else {
                  return new Terminal(OP_GT,
                                      parseElement(expression.substring(0, i)
                                                             .trim()),
                                      parseElement(expression.substring(i + 1,
                                                                        expression
                                                                           .length())
                                                             .trim()));
               }

            case '<':

               if (expression.charAt(i + 1) == '=') {
                  return new Terminal(OP_LTE,
                                      parseElement(expression.substring(0, i)
                                                             .trim()),
                                      parseElement(expression.substring(i + 2,
                                                                        expression
                                                                           .length())
                                                             .trim()));
               } else {
                  return new Terminal(OP_LT,
                                      parseElement(expression.substring(0, i)
                                                             .trim()),
                                      parseElement(expression.substring(i + 1,
                                                                        expression
                                                                           .length())
                                                             .trim()));
               }

            case ' ': // ANCA added this case to handle is NULL

// VERED - added here code to look for is null or is not null operator and
// literal terminals beforehand it was not allowing is nto null, was not
// allowing multiple consequitive spaces.
               String temp = expression.substring(i).toLowerCase();

               Pattern ptIs = Pattern.compile(IS);
               Matcher match = ptIs.matcher(temp);

               if (match.find() && match.start() == 0) {


                  // compiling an is null pattern
                  ptIs = Pattern.compile(IS_NULL);
                  match = ptIs.matcher(temp);

                  if (match.find() && match.start() == 0) {
                     return new Terminal(OP_IS,
                                         parseElement(expression.substring(0,
                                                                           i)
                                                                .trim()),
                                         new NominalElement("NULL"));

                  } else { // look for is not null

                     Pattern ptIsNot = Pattern.compile(this.IS_NOT_NULL);
                     match = ptIsNot.matcher(temp);

                     if (match.find() && match.start() == 0) {
                        return new Terminal(OP_IS,
                                            parseElement(expression.substring(0,
                                                                              i)
                                                                   .trim()),
                                            new NominalElement("NOT NULL"));

                     } else {
                        throw new ExpressionException("FilterExpression: malformed IS condition");
                     }
                  }
               } // if there is an "is" operator pattern


               break;

         }

      } // for

      // check to see if it's just empty parentheses
      String test = expression.trim();

      if (test.length() == 0) {
         return new TrueTerminal();
      }

      throw new ExpressionException("FilterExpression: apparently malformed expression.");

   } // end method parseTerminal

   /******************************************************************************/
   /* Expression interface
    */
   /******************************************************************************/

   /**
    * Returns a <code>boolean</code> array (cast to an <code>Object</code>) of
    * length equal to the number of rows in the <code>Table</code> that was
    * passed to the constructor. Each entry in the array is <code>true</code> if
    * and only if that row of the <code>Table</code> satisfies the last filter
    * expression string passed to <code>setExpression</code>.
    *
    * @return the <code>boolean</code> array
    *
    * @throws ExpressionException if there was any error in evaluation
    */
   public Object evaluate() throws ExpressionException {

      if (
          root == null ||
             table == null ||
             table.getNumRows() == 0 ||
             table.getObject(0, 0) == null) {
         return null;
      }

      boolean[] b = new boolean[table.getNumRows()];

      for (int i = 0; i < b.length; i++) {

         try {
            b[i] = root.evaluate(i);
         } catch (MissingValueException mve) {
            b[i] = includeMissingValues;
         }
      }

      return (Object) b;
   }

   /**
    * Sets this <code>FilterExpression</code>'s internal state to represent the
    * given filter expression string.
    *
    * <p>This filter expression string must be composed entirely of the
    * following elements (ignoring whitespace):</p>
    *
    * <ul>
    *   <li>valid column labels from <code>table</code>,</li>
    *   <li>valid nominal column elements from <code>table</code>,</li>
    *   <li>valid symbols for filter operations, namely:
    *
    *     <ul>
    *       <li><code>==</code> for equals,</li>
    *       <li><code>is</code> for for SQL is NULL,</li>
    *       <li><code>!=</code> for not equals,</li>
    *       <li><code>&lt;</code> for less than,</li>
    *       <li><code>&lt;=</code> for less than or equal to,</li>
    *       <li><code>&gt;</code> for greater than,</li>
    *       <li><code>&gt;=</code> for greater than or equal to,</li>
    *       <li><code>&&</code> for boolean AND, and</li>
    *       <li><code>||</code> for boolean OR,</li>
    *     </ul>
    *
    * and</li>
    *   <li>left and right parentheses: <code>(</code> and <code>)</code>.</li>
    * </ul>
    *
    * <p>In the absence of parentheses, AND takes precedence over OR. Column
    * labels <b>may not contain</b> spaces or the following symbols: <code>
    * =</code>, <code>!</code>, <code>&lt;</code>, <code>&gt;</code>, <code>
    * &</code>, <code>|</code>.</p>
    *
    * <p>Nominal values from columns must be enclosed by single-quote tick marks
    * (<code>'</code>) so as to distinguish them from column labels.</p>
    *
    * <p>An example of a sensible filter expression string would be:</p>
    *
    * <p><code>ColumnA &lt;= 5.0 && (ColumnB &gt; ColumnC || ColumnD ==
    * 'value')</code></p>
    *
    * @param  expression an expression which, if valid, will specify the
    *                    behavior of this <code>FilterExpression</code>
    *
    * @throws ExpressionException if the given expression is not valid with
    *                             regards to the given table
    */
   public void setExpression(String expression) throws ExpressionException {
      root = parse(expression);
   }

   /**
    * Get the expression as an SQL string
    *
    * @return get the expression as an SQL string
    */
   public String toSQLString() {

      if (root == null) {
         return "";
      }

      return root.toSQLString();
   }


    /**
     * Return a string representation of this expression.  It is built
     * recursively starting at the root of the expression tree.
     * @return string representation of this expression
     */
    public String toString() {

        if (root == null) {
            return "";
        }

        return root.toString();
    }

   /***************************************************************************
   * Elements -- the building blocks of a filter expression string -- can be
   *
   * labels of columns from the table, scalars, or nominal values taken from a
   *
   * particular column of the table.
   *
   ******************************************************************************/
   protected abstract class Element implements java.io.Serializable {

      /** Use serialVersionUID for interoperability. */
      static private final long serialVersionUID = -946996639011949735L;

       /**
        * Return SQL-valid string describing this element
        * @return SQL-valid string describing this element
        */
      public abstract String toSQLString();

       /**
        * Return string describing this element
        * @return string describing this element
        */
      public abstract String toString();
   }

    /**
     * A node is an element of the tree.  it can be evaluated.
     */
   protected abstract class Node implements java.io.Serializable {

      /** Use serialVersionUID for interoperability. */
      static private final long serialVersionUID = -5269921167973506810L;

        /**
         * Evaluate for the given row number
         * @param rowNumber row number
         * @return true if the evaluation is true
         * @throws ExpressionException when something goes wrong
         */
      abstract boolean evaluate(int rowNumber) throws ExpressionException;

       /**
        * Return SQL-valid string describing this Node
        * @return SQL-valid string describing this Node
        */
      public abstract String toSQLString();

       /**
        * Return string describing this Node
        * @return string describing this Node
        */
      public abstract String toString();
   }

    /**
     * An element that represents a column of the table
     */
   protected class ColumnElement extends Element {
        /** column label */
      private String columnLabel;
        /** column index in table */
      private int columnNumber;

        /**
         * Constructor.  Given the column label, look up its index.
         * @param columnLabel column label
         * @throws ExpressionException when something goes wrong
         */
      public ColumnElement(String columnLabel) throws ExpressionException {

         Integer I = (Integer) labelToIndex.get(columnLabel);

         if (I == null) {
            throw new ExpressionException("FilterExpression: invalid column label: " +
                                          columnLabel);
         }

         columnNumber = I.intValue();

         this.columnLabel = columnLabel;

      }

        /**
         * Return true if the value for this column at rowNumber is missing
         * @param rowNumber row number
         * @return true if the value for this column at rowNumber is missing
         */
      private boolean isMissing(int rowNumber) {
         return table.isValueMissing(rowNumber, columnNumber);
      }

        /**
         * Get the double at rowNumber for this column
         * @param rowNumber row number
         * @return the double at rowNumber for this column
         * @throws MissingValueException when the value was missing
         */
      public double evaluateDouble(int rowNumber) throws MissingValueException {

         if (this.isMissing(rowNumber)) {
            throw new MissingValueException(table.getColumnLabel(columnNumber),
                                            rowNumber);
         }

         return table.getDouble(rowNumber, columnNumber);
      }

        /**
         * Get the string at rowNumber for this column
         * @param rowNumber row number
         * @return the string at rowNumber for this column
         * @throws MissingValueException when the value was missing
         */
      public String evaluateString(int rowNumber) throws MissingValueException {

         if (this.isMissing(rowNumber)) {
            throw new MissingValueException(table.getColumnLabel(columnNumber),
                                            rowNumber);
         }

         return table.getString(rowNumber, columnNumber);
      }

        /**
         * Get the column number for this ColumnElement
         * @return column number
         */
      public int getColumnNumber() { return columnNumber; }


        /**
         * Return SQL-valid string describing this element
         *
         * @return SQL-valid string describing this element
         */
        public String toSQLString() {
            return columnLabel;
        }

        /**
         * Return string describing this element
         *
         * @return string describing this element
         */
        public String toString() {
            return columnLabel;
        }
   } // end class ColumnElement

    /**
     * A nominal element is a value from a nominal column
     */
   protected class NominalElement extends Element {
        /** the nominal value */
      private String value;

        /**
         * Constructor
         * @param value the nominal value
         */
      public NominalElement(String value) { this.value = value; }

        /**
         * Return the nominal value
         * @return nominal value
         */
      public String evaluate() { return value; }


        /**
         * Return SQL-valid string describing this element
         *
         * @return SQL-valid string describing this element
         */
        public String toSQLString() {

            if (value.equals("NULL")) {
                return "null";
            }

            return toString();
        }

        /**
         * Return string describing this element
         *
         * @return string describing this element
         */
        public String toString() {

            StringBuffer buffer = new StringBuffer();

            buffer.append('\'');

            buffer.append(value);

            buffer.append('\'');

            return buffer.toString();

        }

   } // end class NominalElement

    /**
     * An element that represents a scalar value
     */
   protected class ScalarElement extends Element {
        /** scalar value */
      private double value;

        /**
         * Constructor
         * @param value scalar value
         */
      public ScalarElement(double value) { this.value = value; }

        /**
         * Return the scalar value
         * @return scalar value
         */
      public double evaluate() { return value; }


        /**
         * Return SQL-valid string describing this element
         *
         * @return SQL-valid string describing this element
         */
        public String toSQLString() {
            return Double.toString(value);
        }

        /**
         * Return string describing this element
         *
         * @return string describing this element
         */
        public String toString() {
            return Double.toString(value);
        }

   }

    /**
     * A subexpression has a right hand side and left hand side.  They are
     * joined by an operator.
     */
   protected class Subexpression extends Node {

        /** left hand node */
      private Node left;
        /** right hand node */
      private Node right;

        /** code for operator */
      private int opcode;

        /**
         * Constructor
         * @param opcode operator code
         * @param left left hand side node
         * @param right right hand side node
         */
      Subexpression(int opcode, Node left, Node right) {

         this.opcode = opcode;

         this.left = left;

         this.right = right;

      }

        /**
         * Evaluate the given row of the table.  Either the AND or OR of the
         * left and right hand nodes, depending on the opcode.
         * @param rowNumber row of the table to evaluate
         * @return the evaluation for the given row number
         * @throws ExpressionException when the opcode is not recognized
         */
      boolean evaluate(int rowNumber) throws ExpressionException {

         switch (opcode) {

            case BOOL_AND:

               return left.evaluate(rowNumber) &&
                         right.evaluate(rowNumber);

            case BOOL_OR:

               return left.evaluate(rowNumber) ||
                         right.evaluate(rowNumber);

            default:

               throw new ExpressionException("FilterExpression: illegal opcode: " +
                                             opcode);

         }

      }



        /**
         * Return SQL-valid string describing this Node
         *
         * @return SQL-valid string describing this Node
         */
        public String toSQLString() {

            StringBuffer buffer = new StringBuffer();
            buffer.append('(');
            buffer.append(left.toSQLString());
            buffer.append(' ');

            switch (opcode) {

                case BOOL_AND:
                    buffer.append("and");

                    break;

                case BOOL_OR:
                    buffer.append("or");

                    break;

                default:
                    buffer.append("??");

                    break;
            }

            buffer.append(' ');
            buffer.append(right.toSQLString());
            buffer.append(')');

            return buffer.toString();
        } // end method toSQLString

        /**
         * Return string describing this Node
         *
         * @return string describing this Node
         */
        public String toString() {

            StringBuffer buffer = new StringBuffer();

            buffer.append('(');

            buffer.append(left.toString());

            buffer.append(' ');

            switch (opcode) {

                case BOOL_AND:
                    buffer.append("&&");

                    break;

                case BOOL_OR:
                    buffer.append("||");

                    break;

                default:
                    buffer.append("??");

                    break;

            }

            buffer.append(' ');

            buffer.append(right.toString());

            buffer.append(')');

            return buffer.toString();

        } // end method toString
   } // end class Subexpression

    /**
     * A Terminal node has left and right elements, joined by an operator.
     */
   protected class Terminal extends Node {
        /** left element */
      private Element left;
        /** right element */
      private Element right;
        /** operator */
      private int opcode;

        /**
         * Constructor
         */
      Terminal() { }

        /**
         * Constructor
         * @param opcode operator
         * @param left left element
         * @param right right element
         */
      Terminal(int opcode, Element left, Element right) {

         this.opcode = opcode;

         this.left = left;

         this.right = right;

      }

        /**
         * Evaluate for the given row number
         *
         * @param rowNumber row number
         * @return true if the evaluation is true
         * @throws ncsa.d2k.modules.core.datatype.ExpressionException
         *          when the opcode is not recognized
         */
        boolean evaluate(int rowNumber) throws ExpressionException {

            // Each element (left and right) may represent a column label, a

            // scalar value, or a column nominal value. All nine combinations,

            // unfortunately, must be handled differently...

            if (left instanceof ColumnElement) {

                if (right instanceof ColumnElement) {
                    ColumnElement cleft = (ColumnElement) left;
                    ColumnElement cright = (ColumnElement) right;

                    try {

                        // are both columns numeric?
                        cleft.evaluateDouble(rowNumber);
                        cright.evaluateDouble(rowNumber);

                        // if so, compare doubles:

                        switch (opcode) {

                            case OP_EQ:
                            case OP_IS: // ANCA added this case

                                return ((ColumnElement) left).evaluateDouble(rowNumber) ==
                                        ((ColumnElement) right).evaluateDouble(rowNumber);

                            case OP_NEQ:

                                return ((ColumnElement) left).evaluateDouble(rowNumber) !=
                                        ((ColumnElement) right).evaluateDouble(rowNumber);

                            case OP_LT:

                                return ((ColumnElement) left).evaluateDouble(rowNumber) <
                                        ((ColumnElement) right).evaluateDouble(rowNumber);

                            case OP_LTE:

                                return ((ColumnElement) left).evaluateDouble(rowNumber) <=
                                        ((ColumnElement) right).evaluateDouble(rowNumber);

                            case OP_GT:

                                return ((ColumnElement) left).evaluateDouble(rowNumber) >
                                        ((ColumnElement) right).evaluateDouble(rowNumber);

                            case OP_GTE:

                                return ((ColumnElement) left).evaluateDouble(rowNumber) >=
                                        ((ColumnElement) right).evaluateDouble(rowNumber);

                            default:

                                throw new ExpressionException("FilterExpression: illegal opcode: " +
                                        opcode);

                        }

                    } catch (NumberFormatException exc) {

                        // if not, compare strings:

                        switch (opcode) {

                            case OP_EQ:
                            case OP_IS: // ANCA added this case

                                return ((ColumnElement) left).evaluateString(rowNumber)
                                        .equals(((ColumnElement) right)
                                                .evaluateString(rowNumber));

                            case OP_NEQ:

                                return !((ColumnElement) left).evaluateString(rowNumber)
                                        .equals(((ColumnElement) right)
                                                .evaluateString(rowNumber));

                            default:

                                throw new ExpressionException("FilterExpression: cannot perform operation on nominal columns: " +
                                        opcode);

                        }

                    } // end try-catch

                } else if (right instanceof ScalarElement) {

                    switch (opcode) {

                        case OP_EQ:
                        case OP_IS: // ANCA added this case

                            return ((ColumnElement) left).evaluateDouble(rowNumber) ==
                                    ((ScalarElement) right).evaluate();

                        case OP_NEQ:

                            return ((ColumnElement) left).evaluateDouble(rowNumber) !=
                                    ((ScalarElement) right).evaluate();

                        case OP_LT:

                            return ((ColumnElement) left).evaluateDouble(rowNumber) <
                                    ((ScalarElement) right).evaluate();

                        case OP_LTE:

                            return ((ColumnElement) left).evaluateDouble(rowNumber) <=
                                    ((ScalarElement) right).evaluate();

                        case OP_GT:

                            return ((ColumnElement) left).evaluateDouble(rowNumber) >
                                    ((ScalarElement) right).evaluate();

                        case OP_GTE:

                            return ((ColumnElement) left).evaluateDouble(rowNumber) >=
                                    ((ScalarElement) right).evaluate();

                        default:

                            throw new ExpressionException("FilterExpression: illegal opcode: " +
                                    opcode);

                    }

                } else { // right instanceof NominalElement

                    switch (opcode) {

                        case OP_EQ:
                        case OP_IS: // ANCA added this case

                            return ((ColumnElement) left).evaluateString(rowNumber)
                                    .equals(((NominalElement) right).evaluate());

                        case OP_NEQ:

                            return !((ColumnElement) left).evaluateString(rowNumber)
                                    .equals(((NominalElement) right).evaluate());

                        default:

                            throw new ExpressionException("FilterExpression: illegal opcode on nominal: " +
                                    opcode);

                    }

                }

            } else if (left instanceof ScalarElement) {

                if (right instanceof ColumnElement) {

                    switch (opcode) {

                        case OP_EQ:
                        case OP_IS: // ANCA added this case

                            return ((ScalarElement) left).evaluate() ==
                                    ((ColumnElement) right).evaluateDouble(rowNumber);

                        case OP_NEQ:

                            return ((ScalarElement) left).evaluate() !=
                                    ((ColumnElement) right).evaluateDouble(rowNumber);

                        case OP_LT:

                            return ((ScalarElement) left).evaluate() <
                                    ((ColumnElement) right).evaluateDouble(rowNumber);

                        case OP_LTE:

                            return ((ScalarElement) left).evaluate() >=
                                    ((ColumnElement) right).evaluateDouble(rowNumber);

                        case OP_GT:

                            return ((ScalarElement) left).evaluate() >
                                    ((ColumnElement) right).evaluateDouble(rowNumber);

                        case OP_GTE:

                            return ((ScalarElement) left).evaluate() >=
                                    ((ColumnElement) right).evaluateDouble(rowNumber);

                        default:

                            throw new ExpressionException("FilterExpression: illegal opcode: " +
                                    opcode);

                    }

                } else if (right instanceof ScalarElement) {

                    switch (opcode) {

                        case OP_EQ:
                        case OP_IS: // ANCA added this case
                            return ((ScalarElement) left).evaluate() ==
                                    ((ScalarElement) right).evaluate();

                        case OP_NEQ:

                            return ((ScalarElement) left).evaluate() !=
                                    ((ScalarElement) right).evaluate();

                        case OP_LT:

                            return ((ScalarElement) left).evaluate() <
                                    ((ScalarElement) right).evaluate();

                        case OP_LTE:

                            return ((ScalarElement) left).evaluate() >=
                                    ((ScalarElement) right).evaluate();

                        case OP_GT:

                            return ((ScalarElement) left).evaluate() >
                                    ((ScalarElement) right).evaluate();

                        case OP_GTE:

                            return ((ScalarElement) left).evaluate() >=
                                    ((ScalarElement) right).evaluate();

                        default:

                            throw new ExpressionException("FilterExpression: illegal opcode: " +
                                    opcode);

                    }

                } else { // right instanceof NominalElement

                    throw new ExpressionException("FilterExpression: invalid operation: <scalar> <op> <nominal>");

                }

            } else { // left instanceof NominalElement

                if (right instanceof ColumnElement) {

                    switch (opcode) {

                        case OP_EQ:
                        case OP_IS: // ANCA added this case

                            return ((NominalElement) left).evaluate().equals(((ColumnElement)
                                    right)
                                    .evaluateString(rowNumber));

                        case OP_NEQ:

                            return !((NominalElement) left).evaluate().equals(((ColumnElement)
                                    right)
                                    .evaluateString(rowNumber));

                        default:

                            throw new ExpressionException("FilterExpression: illegal opcode on nominal: " +
                                    opcode);

                    }

                } else if (right instanceof ScalarElement) {

                    throw new ExpressionException("FilterExpression: invalid operation: <nominal> <op> <scalar>");

                } else { // right instanceof NominalElement

                    throw new ExpressionException("FilterExpression: invalid operation: <nominal> <op> <nominal>");

                }

            } // end if-else

        } // end method evaluate


        /**
         * Return SQL-valid string describing this Node
         *
         * @return SQL-valid string describing this Node
         */
        public String toSQLString() {

            StringBuffer buffer = new StringBuffer();
            buffer.append("( ");
            buffer.append(left.toSQLString());
            buffer.append(' ');

            switch (opcode) {

                case OP_EQ:
                    buffer.append("=");

                    break;

                case OP_IS:
                    buffer.append("is");

                    break;

                case OP_NEQ:
                    buffer.append("<>");

                    break;

                case OP_LT:
                    buffer.append("<");

                    break;

                case OP_LTE:
                    buffer.append("<=");

                    break;

                case OP_GT:
                    buffer.append(">");

                    break;

                case OP_GTE:
                    buffer.append(">=");

                    break;

                default:
                    buffer.append("??");

                    break;
            }

            buffer.append(' ');
            buffer.append(right.toSQLString());
            buffer.append(" )");

            return buffer.toString();
        } // end method toSQLString

        /**
         * Return string describing this Node
         *
         * @return string describing this Node
         */
        public String toString() {

            StringBuffer buffer = new StringBuffer();

            buffer.append('(');

            buffer.append(left.toString());

            buffer.append(' ');

            switch (opcode) {

                case OP_EQ:
                    buffer.append("==");

                    break;

                case OP_NEQ:
                    buffer.append("!=");

                    break;

                case OP_LT:
                    buffer.append("<");

                    break;

                case OP_LTE:
                    buffer.append("<=");

                    break;

                case OP_GT:
                    buffer.append(">");

                    break;

                case OP_GTE:
                    buffer.append(">=");

                    break;

                default:
                    buffer.append("??");

                    break;

            }

            buffer.append(' ');

            buffer.append(right.toString());

            buffer.append(')');

            return buffer.toString();

        } // end method toString

   } // end class Terminal


    /**
     * A true terminal node that always evaluates to true.
     */
   protected class TrueTerminal extends Terminal {

        /**
         * Constructor
         */
      TrueTerminal() { }


        /**
         * Evaluate for the given row number.  Always returns true!
         *
         * @param rowNumber row number
         * @return true if the evaluation is true
         */
        public boolean evaluate(int rowNumber) {
            return true;
        }

        /**
         * Return string describing this Node
         *
         * @return string describing this Node
         */
        public String toString() {
            return "true";
        }

   }

} // end class FilterExpression
