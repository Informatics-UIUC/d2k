package ncsa.d2k.modules.core.transform.table;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ExpressionParser {
  public ExpressionParser() {
  }


   public class Expected {
    static final int OPEN_CLOSE_RIGHT = 0; //after '(' expecting '(' or ')' or a right hand operand.
    static final int AFTER_OPEN = 0;

    static final int CLOSE_OPERATOR_NOTHING = 1; //after a left hand operand or a closing parentheses
    //expecting an operator (&&, ||) or ')' or nothing.
    static final int AFTER_CLOSE = 1;
    static final int AFTER_LEFT = 1;

    static final int RELATION = 2; //after a right hand operand expecting a relation (<, > etc.)
    static final int AFTER_RIGHT = 2;

    static final int OPEN_RIGHT = 3; //at the beginning of the parsing expecting open parentheses or right hand operand.
    static final int BEGINNING = 3;
    static final int AFTER_OPERATOR = 3;

    static final int LEFT = 4;   //after a relation expecting a left hand operand.
    static final int AFTER_RELATION = 4;


   }

   /**
    * parses <code>expression</code> into sub expression, to verify
    * that all of them are legal and relevant to the attribute in <code>map</code>.
    * allows empty parentheses. checks for validity of parentheses as well.
    * this method is static becuase it supports also sql filter construction module.
    * @param expression - an expression to parse. operators as && or ||
    * @param map - attributes map. each right hand operand in the returned value
    *              must be in the map. (maps attribute name <-> id
    * @return -    <code>expression</code> - pruned if needed (if sub expressions
    *              have attributes that are not in <code>map</code>
    * @throws Exception - if finds an unexpected token or illegal prentheses.
    */
   public static String parseExpression(String expression, HashMap map, boolean sql) throws Exception{

     final String equal, and, or;
     if(sql){
       equal = "=";
       and = "and";
       or = "or" ;
     }
     else{
      equal = "==";
      and = "&&";
      or = "||";
      }


     String goodCondition = ""; //the returned value

     StringTokenizer tok = new StringTokenizer(expression);


       int numOpen = 0;  //number of opening parenthese without matching closing ones.
       int expected = Expected.BEGINNING;  //what type of token we are expecting to parse.
       int lastGoodExpected = expected;

      // String lastGoodToken = ""; //in cased we've parsed a non relative sub expression
       //this will let us know if before this expression was parentheses or an operator.
       //or nothing (the beginning)

       //whenparsing a sub expression - it will be added at once after the left hand
       //operand was parsed and the attribute was verifies.
       String rightHand = null;
       String leftHand = null;
       String relation = null;
       String operator = ""; //the operator will be added before the sub expression.


       //parsing the condition, each sub condition that holds a valid
          //attribute name will be copied into goodCondition
          //assuming the expression could be malformed.

       while(tok.hasMoreTokens()){

         //parsing a token
         String currTok = tok.nextToken();

         //handling special case of sql operators.
         if(sql){
           if(currTok.equals(and) || currTok.equals(or)){
             //are we expecting an operator now?
              if(!(expected == Expected.CLOSE_OPERATOR_NOTHING))
                throw new Exception ("Did not expect to find operator " + currTok + " after " + goodCondition);

              operator = currTok; //we are not adding the operator yet.
              //if the following sub expression is valid it will be added.
              //or if a '(' comes right after this currTok.
              expected = Expected.AFTER_OPERATOR;
              continue;
           }//if sql operator

         }//if sql

         //according to the first character of the token identification is made.
        switch(currTok.charAt(0)){
          //open parentheses
          case '(':
            //a space is required after '('
            if(currTok.length() > 1) throw new Exception("there must be a space before and after each parentheses!");
            //are we expecting this token?
            if(!(expected == Expected.OPEN_CLOSE_RIGHT || expected == Expected.OPEN_RIGHT))
              throw new Exception("Did not expect to find '(' after " + goodCondition);

            //the token is legal at this point
            numOpen++;

            //checking if the last good token was an operator - if so, adding it.
            if(!operator.equals("")){
              goodCondition += " " + operator;
              operator = ""; //initializing the operator...
            }

            goodCondition += " " + currTok;
            expected = Expected.AFTER_OPEN;
            lastGoodExpected = expected;
           // lastGoodToken = currTok;
            break;

            //close parentheses
          case ')':
            //requires space after it.
            if(currTok.length() > 1) throw new Exception("there must be a space before and after each parentheses!");
             //are we expecting this token?
             if(!(expected == Expected.CLOSE_OPERATOR_NOTHING || expected == Expected.OPEN_CLOSE_RIGHT))
               throw new Exception("Did not expect to find ')' after " + goodCondition);
             //so far it is legal
             numOpen--;
             //do we have too many closing parentheses?
             if(numOpen < 0) throw new Exception ("Illegal expression, paretheses order is invalid!");

             goodCondition += " " + currTok;
             expected = Expected.AFTER_CLOSE;
             lastGoodExpected = expected;
          //   lastGoodToken = currTok;
             break;


             //this is an operator
          case '&':
         case '|':
                  //is it a legal operator?
                  if(!(currTok.equals(and) || currTok.equals(or)))
                     throw new Exception ("Found illegal operator: " + currTok);
                   //are we expecting an operator now?
                   if(!(expected == Expected.CLOSE_OPERATOR_NOTHING))
                     throw new Exception ("Did not expect to find operator " + currTok + " after " + goodCondition);

                   operator = currTok; //we are not adding the operator yet.
                   //if the following sub expression is valid it will be added.
                   //or if a '(' comes right after this currTok.
                   expected = Expected.AFTER_OPERATOR;
                 //  lastGoodToken = currTok;
                   break;
         case '=':
         case '!':
         case '<':
         case '>':

                   //verifying validity of the relation
                   if (!( currTok.equals(equal) || currTok.equals("!=") || currTok.equals("<") ||
                       currTok.equals(">") || currTok.equals("<=")  || currTok.equals(">=") ))
                     throw new Exception ("Found illegal relation: " + currTok);
                   //are we expecting a relation?
                   if(!(expected == Expected.RELATION))
                     throw new Exception ("Did not expect to find relation " + currTok + " after " + goodCondition);
                   //the relation will be added if the sub expression is relevant and valid.
                   relation = currTok;
                   expected = Expected.AFTER_RELATION;
                   break;




         default: //meaning this is either left or right hand operands





                  //are we expecting an operand?
                  if(!(expected == Expected.LEFT || expected == Expected.OPEN_CLOSE_RIGHT || expected == Expected.OPEN_RIGHT))
                     throw new Exception ("Did not expect to find operand " + currTok + " after " + goodCondition);

                   //if this is a left hand operand we are expecting
                  if(expected == Expected.LEFT){
                    //if it is in the map of attributes
                    leftHand = currTok;
                    if(map.containsKey(currTok)){
                      //switching between right to left operands
                      leftHand = rightHand;
                      rightHand = currTok;
                    }


                    //now verifying that the right hand is indeed an attribute
                    if(map.containsKey(rightHand)){
                      //adding to good condition operator right hand relation and left hand
                      goodCondition += " " + operator + " " + rightHand + " " + relation + " " + leftHand;
                      operator = "";
                      expected = Expected.AFTER_LEFT;
                      lastGoodExpected = expected;
                   //   lastGoodToken = currTok;
                    }
                    else { //the sub expression is not relevant

                      System.out.println("The attribute " + rightHand + "nor " + leftHand +
                      " in the sub expression: " + rightHand + " " +
                                         relation + " " + leftHand +
                                         "could not be found in the attribute map.\n" +
                                         "The expression is being removed");

                      //restoring the last good expected
                        expected = lastGoodExpected;

                    }//else contains key

                  }//if expected = left
                  else{
                    rightHand = currTok;
                    expected = Expected.AFTER_RIGHT;
                  }
                  break;
        }//switch
    }//while

    return goodCondition;

   }//parseExpression


}//ExpressionParser