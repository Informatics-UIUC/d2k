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

import java.util.HashMap;
import java.util.StringTokenizer;


/**
 * <p>parses expressions with relations '==' or '=' (for sql) '!=' '<=' '>='
 * '<' '>', with operators '||' '&&' or 'or' and 'and' (sql) also operator 'is'
 * is a valid sql opertor NULL is a valid sql value values are to be inside
 * quotes: 'val' allows empty parentheses '()' allows no space before or after
 * parentheses.</p>
 *
 * @author  vered
 * @version 1.0
 */
public class ExpressionParser {

   //~ Instance fields *********************************************************

   /** this will hold the legal left hand operand. */
   private HashMap attMap;

   /** will hold the legal delimiters in the expression. */
   private String delimiter = " \n\t\f\r()<>=!&|";

   /** for each of the attributes in the attMap, indicates if it is scalar
    * (true) or nominal (false)*/
   private boolean[] isScalar;

   /** this will hold the legal operators. */
   private String[] operators;

   /** operations map */
   private HashMap opsMap;

   /** relations map */
   private HashMap relationMap;

   /** this will hold the legal relations. */
   private String[] relations;

   /** true if it is sql */
   private boolean sql;

   //~ Constructors ************************************************************

   /**
    * Creates a new ExpressionParser object.
    */
   public ExpressionParser() { }

   /**
    * Creates a new ExpressionParser object.
    *
    * @param map         attribute map
    * @param ops         operators
    * @param relate      relations
    * @param scalarFlags scalar flags
    * @param isSql       true if it is sql
    */
   public ExpressionParser(HashMap map, String[] ops, String[] relate,
                           boolean[] scalarFlags, boolean isSql) {
      attMap = map;
      operators = ops;
      sql = isSql;
      relations = relate;
      isScalar = scalarFlags;
      createOpsMap();
      createRelationMap();

   } // Ctor

   /**
    * Creates a new ExpressionParser object.
    *
    * @param map         attribute map
    * @param ops         operators
    * @param relate      relations
    * @param scalarFlags scalar flags
    * @param isSql       true if it is sql
    * @param delim       delimiter
    */
   public ExpressionParser(HashMap map, String[] ops, String[] relate,
                           boolean[] scalarFlags, boolean isSql, String delim) {
      this(map, ops, relate, scalarFlags, isSql);
      delimiter = delim;

   }

   //~ Methods *****************************************************************

   /**
    * parses <code>expression</code> into sub expression, to verify that all of
    * them are legal and relevant to the attributes in <code>map</code>. allows
    * empty parentheses. checks for validity of parentheses as well. this method
    * is static becuase it supports also sql filter construction module's doit.
    *
    * @param  expression - an expression to parse. operators as && or ||
    * @param  map        - attributes map. each left hand operand in the
    *                    returned value must be in the map. (maps attribute name
    *                    <-> id
    * @param  sql        true if expression is sql
    *
    * @return - <code>expression</code> - pruned if needed (if sub expressions
    *         have attributes that are not in <code>map</code>
    *
    * @throws Exception - if finds an unexpected token or illegal prentheses.
    */
   static public String parseExpression(String expression, HashMap map,
                                        boolean sql) throws Exception {

      final String equal;
      final String and;
      final String or;
      final String is;

      if (sql) {
         equal = "=";
         and = "and";
         or = "or";
         is = "is";
      } else {
         equal = "==";
         and = "&&";
         or = "||";
         is = "";
      }


      String goodCondition = ""; // the returned value

      StringTokenizer tok =
         new StringTokenizer(expression, " \n\t\f\r()<>=!&|", true);


      int numOpen = 0; // number of opening parenthese without matching closing
                       // ones.
      int expected = Expected.BEGINNING; // what type of token we are expecting
                                         // to parse.
      int lastGoodExpected = expected;

      // String lastGoodToken = ""; //in cased we've parsed a non relative sub
      // expression this will let us know if before this expression was
      // parentheses or an operator. or nothing (the beginning)

      // whenparsing a sub expression - it will be added at once after the left
      // hand operand was parsed and the attribute was verifies.
      String rightHand = "";
      String leftHand = "";
      String relation = "";
      String operator = ""; // the operator will be added before the sub
                            // expression.


      // parsing the condition, each sub condition that holds a valid
      // attribute name will be copied into goodCondition
      // assuming the expression could be malformed.

      String secondChar = null;

      while (tok.hasMoreTokens()) {

         // parsing a token
         String currTok = tok.nextToken();

         // handling special case of sql operators.
         if (sql) {

            if (
                currTok.equals(and) ||
                   currTok.equals(or) ||
                   currTok.equals(is)) {

               // are we expecting an operator now?
               if (!(expected == Expected.CLOSE_OPERATOR_NOTHING)) {
                  throw new Exception("Did not expect to find operator " +
                                      currTok + " after " + goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               operator = currTok; // we are not adding the operator yet.

               // if the following sub expression is valid it will be added.
               // or if a '(' comes right after this currTok.
               expected = Expected.AFTER_OPERATOR;

               continue;
            } // if sql operator

         } // if sql

         // according to the first character of the token identification is
         // made.
         switch (currTok.charAt(0)) {

            case ' ':
            case '\t':
            case '\n':
            case '\r':
            case '\f':
               break;

            // open parentheses
            case '(':

               // a space is required after '('         if(currTok.length() > 1)
               // throw new Exception("there must be a space before and after
               // each parentheses!"); are we expecting this token?
               if (
                   !(expected == Expected.OPEN_CLOSE_LEFT ||
                        expected == Expected.OPEN_LEFT)) {
                  throw new Exception("Did not expect to find '(' after " +
                                      goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               // the token is legal at this point
               numOpen++;

               // checking if the last good token was an operator - if so,
               // adding it. actually no need to check, it will be empty if it
               // isn't if(!operator.equals("")){
               goodCondition += " " + operator;
               operator = ""; // initializing the operator...

               // }

               goodCondition += " " + currTok;
               expected = Expected.AFTER_OPEN;
               lastGoodExpected = expected;

               // lastGoodToken = currTok;
               break;

            // close parentheses
            case ')':

               // requires space after it.           if(currTok.length() > 1)
               // throw new Exception("there must be a space before and after
               // each parentheses!"); are we expecting this token?
               if (
                   !(expected == Expected.CLOSE_OPERATOR_NOTHING ||
                        expected == Expected.OPEN_CLOSE_LEFT)) {
                  throw new Exception("Did not expect to find ')' after " +
                                      goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               // so far it is legal
               numOpen--;

               // do we have too many closing parentheses?
               if (numOpen < 0) {
                  throw new Exception("Illegal expression, paretheses order is invalid!");
               }

               goodCondition += " " + currTok;
               expected = Expected.AFTER_CLOSE;
               lastGoodExpected = expected;

               // lastGoodToken = currTok;
               break;


            // this is an operator
            case '&':
            case '|':

               // are we expecting an operator now?
               if (!(expected == Expected.CLOSE_OPERATOR_NOTHING)) {
                  throw new Exception("Did not expect to find operator " +
                                      currTok + " after " + goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               // if this is not an sql parser - parsing the next token,
               // expecting to find a delimiter '&' or '|'
               secondChar = tok.nextToken();

               if (!currTok.equals(secondChar)) {

                  // is it a legal operator?
                  // if(!(currTok.equals(and) || currTok.equals(or)))
                  throw new Exception("Found illegal operator: " + currTok +
                                      secondChar.charAt(0));
               }

               if (
                   goodCondition.length() != 0 &&
                      goodCondition.charAt(goodCondition.length() - 1) != '(') {
                  operator = currTok + secondChar.charAt(0); // we are not adding the operator yet.
               }

               // if the following sub expression is valid it will be added.
               // or if a '(' comes right after this currTok.

               expected = Expected.AFTER_OPERATOR;

               // lastGoodToken = currTok;
               break;

            case '=':
            case '!':
            case '<':
            case '>':

               // are we expecting a relation?
               if (!(expected == Expected.RELATION)) {
                  throw new Exception("Did not expect to find relation " +
                                      currTok + " after " + goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               // parsing the rest of the relation. if there is:
               secondChar = tok.nextToken();

               if (secondChar.length() != 1) {
                  throw new Exception("Found illegal relation: " + currTok);
               }

               char c = secondChar.charAt(0);

               if (c == '=') {
                  currTok += secondChar;
               }
               // if it is not a white space - it is illegal.
               else if (
                        c != ' ' ||
                           c != '\n' ||
                           c != '\t' ||
                           c != '\f' ||
                           c != '\r') {
                  throw new Exception("Found illegal relation: ");
               }

               // verifying validity of the relation
               if (
                   !(currTok.equals(equal) || currTok.equals("!=") ||
                        currTok.equals("<") ||
                        currTok.equals(">") || currTok.equals("<=") ||
                        currTok.equals(">="))) {
                  throw new Exception("Found illegal relation: " + currTok);
               }

               // the relation will be added if the sub expression is relevant
               // and valid.
               relation = currTok;
               expected = Expected.AFTER_RELATION;

               break;


            default: // meaning this is either left or right hand operands

               // are we expecting an operand?
               if (
                   !(expected == Expected.RIGHT ||
                        expected == Expected.OPEN_CLOSE_LEFT ||
                        expected == Expected.OPEN_LEFT)) {
                  throw new Exception("Did not expect to find operand " +
                                      currTok + " after " + goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               if (expected == Expected.RIGHT) { // it is a right hand operand

                  // if it is in the map of attributes
                  rightHand = currTok;

                  if (map.containsKey(currTok)) {

                     // switching between right to left operands
                     rightHand = leftHand;
                     leftHand = currTok;
                  } // if contains currTok

                  // testing that the right hadn operand is really a value.
                  // trying first to parse a number...
                  try {
                     double d = Double.parseDouble(rightHand);
                  } catch (Exception e) {

                     // if it did not work looking for ' at the beginning and
                     // end.
                     if (
                         rightHand.charAt(0) != '\'' ||
                            rightHand.charAt(rightHand.length() - 1) != '\'') {
                        throw new Exception("Found illegal value - " +
                                            rightHand +
                                            ". Nominal values should be surounded with single quotes!");
                     }

                  }


                  // now verifying that the left hand is indeed an attribute
                  if (map.containsKey(leftHand)) {

                     // testing that value is between ' ' was done before
                     /*char first = rightHand.charAt(0);
                      * char last = rightHand.charAt(rightHand.length()-1);
                      *
                      * if(!(first == '\'' && last == '\'' )) throw new Exception
                      * ("\nValues have to be surounded by single quotes
                      * (\')\n");
                      */
                     // adding to good condition operator right hand relation
                     // and left hand
                     goodCondition +=
                        " " + operator + " " + leftHand + " " + relation + " " +
                        rightHand;
                     operator = "";
                     leftHand = "";
                     rightHand = "";
                     relation = "";

                     expected = Expected.AFTER_RIGHT;
                     lastGoodExpected = expected;
                     // lastGoodToken = currTok;
                  } // if contains leftHand
                  else { // the sub expression is not relevant

                     System.out.println("The attribute " + rightHand + "nor " +
                                        leftHand +
                                        " in the sub expression: " + leftHand +
                                        " " +
                                        relation + " " + rightHand +
                                        "could not be found in the attribute map.\n" +
                                        "The expression is being removed");

                     // restoring the last good expected
                     expected = Expected.AFTER_MAL_EXPRESSION;
                     lastGoodExpected = expected;
                     operator = "";
                     leftHand = "";
                     rightHand = "";
                     relation = "";


                  } // else contains leftHand

               } // if (right hand operand)
               else { // this is a left hand operand

                  leftHand = currTok;
                  expected = Expected.AFTER_LEFT;
               } // else right


               break;
         } // switch
      } // while

      if (numOpen > 0) {
         throw new Exception("\n\nThe expression " + goodCondition +
                             " has too many opening parentheses\n");
      }

      return goodCondition;

   } // parseExpression

   /**
    * Create operations map
    */
   private void createOpsMap() {
      opsMap = new HashMap(operators.length);

      for (int i = 0; i < operators.length; i++) {
         opsMap.put(operators[i], new Integer(i));
      }

   }

   /**
    * Create relations map
    */
   private void createRelationMap() {
      relationMap = new HashMap(relations.length);

      for (int i = 0; i < relations.length; i++) {
         relationMap.put(relations[i], new Integer(i));
      }

   }


   /**
    * Validate a small expression
    *
    * @param  leftHand  left hand side
    * @param  rightHand right hand side
    * @param  relation  relation between LHS and RHS
    *
    * @throws Exception when something goes wrong
    */
   private void validateSmallExpression(String leftHand, String rightHand,
                                        String relation) throws Exception {
      String exp = leftHand + " " + relation + " " + rightHand;

      if (!attMap.containsKey(leftHand)) {
         throw new Exception("Found illegal expression: " + exp +
                             " non of the operands " + leftHand + " or " +
                             rightHand +
                             " are in the input table");
      }

      // special case, this is an sql parser and the relation is "is"
      if (sql && relation.equals("is")) {

         if (!rightHand.equalsIgnoreCase("null")) {
            throw new Exception("Found illegal expression: " + exp +
                                " expected to find expression: " + leftHand +
                                " " +
                                relation + " NULL");
         } else {
            return;
         }
      }

      // so far all's well. if right and left are not both attributes
      if (!attMap.containsKey(rightHand)) {
         int index = ((Integer) attMap.get(leftHand)).intValue();

         // if left hand is a scalar attribute, making sure we can parse the
         // right hand value
         if (isScalar[index]) {

            try {
               Double.parseDouble(rightHand);
            } catch (NumberFormatException e) {
               throw new Exception("Found illegal expression: " + exp +
                                   "\n" + leftHand +
                                   " is a scalar attribute but " +
                                   rightHand + " is not a number");
            } // catch
         } // if sclar
         else {

            if (
                rightHand.charAt(0) != '\'' ||
                   rightHand.charAt(rightHand.length() - 1) != '\'') {
               throw new Exception("Found illegal expression: " + exp +
                                   "\n" + leftHand +
                                   " is a nominal attribute but " +
                                   rightHand +
                                   " is not surrounded by single quote marks.");
            }


         } // else - left hand is nominal

      } // if right hand is not an attribute

      // all's well
      return;


   } // validate small expression


   /**
    * parses <code>expression</code> into sub expression, to verify that all of
    * them are legal and relevant to the attributes in <code>attMap</code>.
    * allows empty parentheses. checks for validity of parentheses as well. this
    * method is static becuase it supports also sql filter construction module's
    * doit.
    *
    * @param  expression - an expression to parse.
    *
    * @return - <code>expression</code> - if is valid
    *
    * @throws Exception - if finds an unexpected token or illegal prentheses or
    *                   an attribute that is not in attMap.
    */
   public String parseExpression(String expression) throws Exception {
      /*   final String equal, and, or, is;
       *      if(sql){       equal = "=";       and = "and";       or = "or" ;
       *     is = "is";     }     else{      equal = "==";      and = "&&";
       * or = "||";      is = "";      }*/


      String goodCondition = ""; // the returned value

      StringTokenizer tok =
         new StringTokenizer(expression, delimiter /*" \n\t\f\r()<>=!&|"*/,
                             true);


      int numOpen = 0; // number of opening parenthese without matching closing
                       // ones.
      int expected = Expected.BEGINNING; // what type of token we are expecting
                                         // to parse.
      int lastGoodExpected = expected;

      // String lastGoodToken = ""; //in cased we've parsed a non relative sub
      // expression this will let us know if before this expression was
      // parentheses or an operator. or nothing (the beginning)

      // whenparsing a sub expression - it will be added at once after the left
      // hand operand was parsed and the attribute was verifies.
      String rightHand = "";
      String leftHand = "";
      String relation = "";
      String operator = ""; // the operator will be added before the sub
                            // expression.


      // parsing the condition, each sub condition that holds a valid
      // attribute name will be copied into goodCondition
      // assuming the expression could be malformed.

      String secondChar = null;
      boolean getNext = true;
      String currTok = "";

      while (tok.hasMoreTokens()) {

         // parsing a token
         if (getNext) {
            currTok = tok.nextToken();
         } else {
            getNext = true;
         }

         // handling special case of sql operators.
         if (sql && expected == Expected.CLOSE_OPERATOR_NOTHING) {

            if (this.opsMap.containsKey(currTok)) {

               operator = currTok; // we are not adding the operator yet.

               // if the following sub expression is valid it will be added.
               // or if a '(' comes right after this currTok.
               expected = Expected.AFTER_OPERATOR;

               continue;
            } // if sql operator

         } // if sql

         // according to the first character of the token identification is
         // made.
         switch (currTok.charAt(0)) {

            case ' ':
            case '\t':
            case '\n':
            case '\r':
            case '\f':
               break;

            // open parentheses
            case '(':

               // a space is required after '('         if(currTok.length() > 1)
               // throw new Exception("there must be a space before and after
               // each parentheses!"); are we expecting this token?
               if (
                   !(expected == Expected.OPEN_CLOSE_LEFT ||
                        expected == Expected.OPEN_LEFT)) {
                  throw new Exception("Did not expect to find '(' after " +
                                      goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               // the token is legal at this point
               numOpen++;

               // checking if the last good token was an operator - if so,
               // adding it. actually no need to check, it will be empty if it
               // isn't if(!operator.equals("")){
               goodCondition += " " + operator;
               operator = ""; // initializing the operator...

               // }

               goodCondition += " " + currTok;
               expected = Expected.AFTER_OPEN;
               lastGoodExpected = expected;

               // lastGoodToken = currTok;
               break;

            // close parentheses
            case ')':

               // requires space after it.           if(currTok.length() > 1)
               // throw new Exception("there must be a space before and after
               // each parentheses!"); are we expecting this token?
               if (
                   !(expected == Expected.CLOSE_OPERATOR_NOTHING ||
                        expected == Expected.OPEN_CLOSE_LEFT)) {
                  throw new Exception("Did not expect to find ')' after " +
                                      goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               // so far it is legal
               numOpen--;

               // do we have too many closing parentheses?
               if (numOpen < 0) {
                  throw new Exception("Illegal expression, paretheses order is invalid!");
               }

               goodCondition += " " + currTok;
               expected = Expected.AFTER_CLOSE;
               lastGoodExpected = expected;

               // lastGoodToken = currTok;
               break;


            // this is an operator
            case '&':
            case '|':

               // are we expecting an operator now?
               if (!(expected == Expected.CLOSE_OPERATOR_NOTHING)) {
                  throw new Exception("Did not expect to find operator " +
                                      currTok + " after " + goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               // if this is not an sql parser - parsing the next token,
               // expecting to find a delimiter '&' or '|'
               secondChar = tok.nextToken();

               if (!currTok.equals(secondChar)) {

                  // is it a legal operator?
                  // if(!(currTok.equals(and) || currTok.equals(or)))
                  throw new Exception("Found illegal operator: " + currTok +
                                      secondChar.charAt(0));
               }

               if (
                   goodCondition.length() != 0 &&
                      goodCondition.charAt(goodCondition.length() - 1) != '(') {
                  operator = currTok + secondChar.charAt(0); // we are not adding the operator yet.
               }

               // if the following sub expression is valid it will be added.
               // or if a '(' comes right after this currTok.

               expected = Expected.AFTER_OPERATOR;

               // lastGoodToken = currTok;
               break;

            case '=': // are we expecting a relation?

               if (!(expected == Expected.RELATION)) {
                  throw new Exception("Did not expect to find relation " +
                                      currTok + " after " + goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
// special case, we are expecting a relation and it is an sql relation. so equal
// is a sigle '='
               }

               if (sql) {

                  // the relation will be added if the sub expression is
                  // relevant and valid.
                  relation = currTok;
                  expected = Expected.AFTER_RELATION;

                  break;

               }
               // if not expecting sql relation - fall through to next cases.

            case '!':
            case '<':
            case '>':

               // are we expecting a relation?
               if (!(expected == Expected.RELATION)) {
                  throw new Exception("Did not expect to find relation " +
                                      currTok + " after " + goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               // parsing the rest of the relation. if there is:
               secondChar = tok.nextToken();

               if (secondChar.length() != 1) {
                  throw new Exception("Found illegal relation: " + currTok);
               }

               char c = secondChar.charAt(0);

               // expecting either '=' or white space or right hand operand.

               if (c == '=') {
                  currTok += secondChar;
               }
               // if it is not a white space - it is illegal.
               else if (
                        c != ' ' ||
                           c != '\n' ||
                           c != '\t' ||
                           c != '\f' ||
                           c != '\r') {

                  // secondChar holds the next token which is not white space,
                  // so it has to be right hand operand.
                  relation = currTok;
                  currTok = secondChar;
                  getNext = false;
                  expected = Expected.AFTER_RELATION;

                  break;
               }
               /*throw new Exception ("Found illegal relation: " );*/

               // verifying validity of the relation
               if (!relationMap.containsKey(currTok)) {
                  throw new Exception("Found illegal relation: " + currTok);
               }

               // the relation will be added if the sub expression is relevant
               // and valid.
               relation = currTok;
               expected = Expected.AFTER_RELATION;

               break;


            default: // meaning this is either left or right hand operands or
                     // the relation "is"

               // special case - expecting relation and this is an sql parser
               if (sql && Expected.RELATION == expected) {

                  if (currTok.equals("is") && relationMap.containsKey("is")) {
                     relation = currTok;
                     expected = Expected.AFTER_RELATION;

                     break;
                  } else {
                     throw new Exception("Found illegal relation: " + currTok);
                  }

               } // special case sql and relation

               // are we expecting an operand?
               if (
                   !(expected == Expected.RIGHT ||
                        expected == Expected.OPEN_CLOSE_LEFT ||
                        expected == Expected.OPEN_LEFT)) {
                  throw new Exception("Did not expect to find operand " +
                                      currTok + " after " + goodCondition +
                                      " " + leftHand + " " + relation + " " +
                                      rightHand);
               }

               if (expected == Expected.RIGHT) { // it is a right hand operand

                  // if it is in the map of attributes
                  rightHand = currTok;

                  if (attMap.containsKey(currTok)) {

                     // switching between right to left operands
                     rightHand = leftHand;
                     leftHand = currTok;
                  } // if contains currTok

                  validateSmallExpression(leftHand, rightHand, relation);

                  // testing that the right hadn operand is really a value.
                  // trying first to parse a number...
                  /*      try{
                   *      double d = Double.parseDouble(rightHand);   }
                   * catch(Exception e){     //if it did not work looking for '
                   * at the beginning and end.     if(rightHand.charAt(0) !=
                   * '\'' || rightHand.charAt(rightHand.length() -1) != '\'')
                   *    throw new Exception ("Found illegal value - " +
                   * rightHand + ". Nominal values should be surounded with
                   * single quotes!");
                   *
                   * }*/


                  // now verifying that the left hand is indeed an attribute
                  if (attMap.containsKey(leftHand)) {

                     // testing that value is between ' ' was done before
                     /*char first = rightHand.charAt(0);
                      * char last = rightHand.charAt(rightHand.length()-1);
                      *
                      * if(!(first == '\'' && last == '\'' )) throw new Exception
                      * ("\nValues have to be surounded by single quotes
                      * (\')\n");
                      */
                     // adding to good condition operator right hand relation
                     // and left hand
                     goodCondition +=
                        " " + operator + " " + leftHand + " " + relation + " " +
                        rightHand;
                     operator = "";
                     leftHand = "";
                     rightHand = "";
                     relation = "";

                     expected = Expected.AFTER_RIGHT;
                     lastGoodExpected = expected;
                     // lastGoodToken = currTok;
                  } // if contains leftHand
                  else { // the sub expression is not relevant

                     System.out.println("The attribute " + rightHand + "nor " +
                                        leftHand +
                                        " in the sub expression: " + leftHand +
                                        " " +
                                        relation + " " + rightHand +
                                        "could not be found in the attribute map.\n" +
                                        "The expression is being removed");

                     // restoring the last good expected
                     expected = Expected.AFTER_MAL_EXPRESSION;
                     lastGoodExpected = expected;
                     operator = "";
                     leftHand = "";
                     rightHand = "";
                     relation = "";


                  } // else contains leftHand

               } // if (right hand operand)
               else { // this is a left hand operand

                  leftHand = currTok;
                  expected = Expected.AFTER_LEFT;
               } // else right


               break;
         } // switch
      } // while

      if (numOpen > 0) {
         throw new Exception("\n\nThe expression " + goodCondition +
                             " has too many opening parentheses\n");
      }

      return goodCondition;

   } // parseExpression

   //~ Inner Classes ***********************************************************

    /**
     * Constants for the expected next token
     */
   public class Expected {
      static final int OPEN_CLOSE_LEFT = 0; // after '(' expecting '(' or ')'
                                            // or a left hand operand.
      static final int AFTER_OPEN = 0;

      static final int CLOSE_OPERATOR_NOTHING = 1; // after a right hand
                                                   // operand or a closing
                                                   // parentheses

      // expecting an operator (&&, ||) or ')' or nothing.
      static final int AFTER_CLOSE = 1;
      static final int AFTER_RIGHT = 1;
      static final int AFTER_MAL_EXPRESSION = 1;

      static final int RELATION = 2; // after a right hand operand expecting a
                                     // relation (<, > etc.)
      static final int AFTER_LEFT = 2;

      static final int OPEN_LEFT = 3; // at the beginning of the parsing
                                      // expecting open parentheses or right hand
                                      // operand.
      static final int BEGINNING = 3;
      static final int AFTER_OPERATOR = 3;

      static final int RIGHT = 4; // after a relation expecting a left hand
                                  // operand.
      static final int AFTER_RELATION = 4;


   } // Expected


} // ExpressionParser
