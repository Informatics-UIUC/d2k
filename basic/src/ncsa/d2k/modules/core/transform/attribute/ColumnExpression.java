package ncsa.d2k.modules.core.transform.attribute;

import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.*;

/**
 * A <code>ColumnExpression</code> object encapsulates a single mathematical
 * expression of arbitrary length in which columns of a <code>Table</code> are
 * combined by various operators.
 *
 * @author gpape
 */
public class ColumnExpression
    implements Expression {

  private Node root;

  private HashMap labelToIndex,
      extraColumnIndexToType;

  private Table table;

  boolean lazy = false;

  /**
   * Constructor for a <code>ColumnExpression</code> object that should use
   * the given <code>Table</code> as its context.
   *
   * @param table           the <code>Table</code> that this
   *                        <code>ColumnExpression</code> object should
   *                        reference
   */
  public ColumnExpression(Table table) {

    this.table = table;

    labelToIndex = new HashMap();
    for (int i = 0; i < table.getNumColumns(); i++) {
      labelToIndex.put(table.getColumnLabel(i), new Integer(i));

    }
  }

  /*public MutableTable getTable() {
    return (MutableTable) table;
  }*/

  public void setTable(MutableTable mt) {
    table = mt;

    labelToIndex = new HashMap();
    for (int i = 0; i < table.getNumColumns(); i++) {
      labelToIndex.put(table.getColumnLabel(i), new Integer(i));

    }

  }

      /******************************************************************************/
      /* Expression interface                                                       */
      /******************************************************************************/

  /**
   * Evaluates the current expression and returns a new column in the form
   * of a primitive array cast to an <code>Object</code>.
   *
   * @return                the appropriate new column
   */
  public Object evaluate() throws ExpressionException {

    if (lazy || root == null || table == null) {
      return null;
    }

    return root.evaluate();

  }

  /**
   * Sets this <code>ColumnExpression</code>'s internal state to represent
   * the given column construction expression string.
   * <p>
   * The <code>expression</code> string must be composed entirely of the
   * following elements (ignoring whitespace):
   * <ul>
   *    <li>valid column labels from <code>table</code>,
   *    <li>valid symbols for column operations, namely:
   *       <ul>
   *          <li><code>+</code> for addition,
   *          <li><code>-</code> for subtraction,
   *          <li><code>*</code> for multiplication,
   *          <li><code>/</code> for division,
   *          <li><code>%</code> for modulus,
   *          <li><code>&&</code> for AND, and
   *          <li><code>||</code> for OR,
   *       </ul>
   *       and
   *    <li>left and right parentheses: <code>(</code> and <code>)</code>.
   * </ul>
   * <p>
   * In the absence of parentheses, the order of operations is as follows:
   * <ul>
   *    <li>AND,</li>
   *    <li>OR,</li>
   *    <li>multiplication and division,</li>
   *    <li>modulus,</li>
   *    <li>addition and subtraction.</li>
   * </ul>
   * Note that AND and OR can only be applied to boolean columns.
   * <p>
   * Should a column label contain whitespace and/or a symbol for a column
   * operator (such as a hyphen, which will be interpreted as a subtraction
   * operator), every such character must be preceded by a backslash in the
   * <code>expression</code> string. For example, a column label
   * <code>p-adic number</code> should appear in the <code>expression</code>
   * string as <code>p\-adic\ number</code>.
   *
   * @param expression      an expression which, if valid, will specify the
   *                        behavior of this <code>ColumnExpression</code>
   *                        object
   */
  public void setExpression(String expression) throws ExpressionException {
    root = parse(expression);
  }

  /**
   * Sets this <code>ColumnExpression</code>'s internal state to <b>lazily</b>
   * represent the given construction expression string, meaning that only
   * type evaluation is allowed. This is useful for constructing delayed
   * construction transformations using this <code>ColumnExpression</code>'s
   * <code>Table</code>.
   * <p>
   * Obviously, <code>newColumns</code> and <code>newColumnTypes</code>
   * must have the same length.
   *
   * @param expression      a construction expression string
   * @param newColumns      an array of potential new column labels
   * @param newColumnTypes  an array specifying those labels' types
   */
  public void setLazyExpression(String expression, String[] newColumns,
                                int[] newColumnTypes) throws
      ExpressionException {

    lazy = true;
    extraColumnIndexToType = new HashMap();

    if (newColumns == null || newColumnTypes == null) {
      root = parse(expression);
      return;
    }

    int numCols = table.getNumColumns();
    for (int i = 0; i < newColumns.length; i++) {
      // System.out.println("adding " + newColumns[i]);
      labelToIndex.put(newColumns[i], new Integer(i + numCols));
      extraColumnIndexToType.put(new Integer(i + numCols),
                                 new Integer(newColumnTypes[i]));
    }

    root = parse(expression);

  }

  public static void main(String[] args) {
    ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl ti =
        new ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl();
    double[] d1 = {
        4, 4};
    double[] d2 = {
        3, 3};
    ti.addColumn(d1);
    ti.addColumn(d2);

    ti.setColumnLabel("one", 0);
    ti.setColumnLabel("two", 1);
    ti.print();

    ColumnExpression ce = new ColumnExpression(ti);
    String exp = "one ^ two * exp(5)";
    try {
      ce.setExpression(exp);//, new String[] {"three"}
                           //, new int[] {ColumnTypes.DOUBLE});
      Object o = ce.evaluate();
      double[] ar = (double[])o;
      for(int i =0; i < ar.length; i++)
        System.out.println(ar[i]);
      System.out.println(o);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

      /******************************************************************************/
      /* The expression string is parsed recursively.                               */
      /******************************************************************************/

  private static final int
      ADDITION = 0,
      SUBTRACTION = 1,
      MULTIPLICATION = 2,
      DIVISION = 3,
      MODULUS = 4,
      AND = 5,
      OR = 6; // if you add any more with higher precedence than
  // AND, make sure you update best_type in parse()!

  private static final int
      POW = 7;
      //SQRT = 8,
      //LOG = 9,
      //LN = 10,
      //EXP = 11;

  private static final int[] order = {
      0, 0, 2, 2, 1, 4, 3, 2};

  private Node parse(String expression) throws ExpressionException {

    char c;

    // we're interested in the shallowest operator of least precedence
    // (meaning we break ties by going to the right). if we don't find an
    // operator then we're at a terminal subexpression.

    boolean operator_found = false;
    int depth = 0, max_depth = 0,
        best_depth = Integer.MAX_VALUE, best_type = AND, best_pos = -1;
    for (int i = 0; i < expression.length(); i++) {

      c = expression.charAt(i);

      switch (c) {

        case '(':
          depth++;
          break;
        case ')':
          depth--;
          break;

        case '+':
          operator_found = true;
          if (depth < best_depth ||
              depth == best_depth && order[ADDITION] <= order[best_type]) {
            best_depth = depth;
            best_type = ADDITION;
            best_pos = i;
          }
          break;

        case '-':
          operator_found = true;
          if (depth < best_depth ||
              depth == best_depth && order[SUBTRACTION] <= order[best_type]) {
            best_depth = depth;
            best_type = SUBTRACTION;
            best_pos = i;
          }
          break;

        case '*':
          operator_found = true;
          if (depth < best_depth ||
              depth == best_depth && order[MULTIPLICATION] <= order[best_type]) {
            best_depth = depth;
            best_type = MULTIPLICATION;
            best_pos = i;
          }
          break;

        case '/':
          operator_found = true;
          if (depth < best_depth ||
              depth == best_depth && order[DIVISION] <= order[best_type]) {
            best_depth = depth;
            best_type = DIVISION;
            best_pos = i;
          }
          break;

        case '%':
          operator_found = true;
          if (depth < best_depth ||
              depth == best_depth && order[MODULUS] <= order[best_type]) {
            best_depth = depth;
            best_type = MODULUS;
            best_pos = i;
          }
          break;

        case '&':
          operator_found = true;
          if (depth < best_depth ||
              depth == best_depth && order[AND] <= order[best_type]) {
            best_depth = depth;
            best_type = AND;
            best_pos = i;
          }
          i++;
          break;

        case '|':
          operator_found = true;
          if (depth < best_depth ||
              depth == best_depth && order[OR] <= order[best_type]) {
            best_depth = depth;
            best_type = OR;
            best_pos = i;
          }
          i++;
          break;

        case '\\':
          i++;
          break;

        // power
        case '^':
          operator_found = true;
          if (depth < best_depth ||
              depth == best_depth && order[POW] <= order[best_type]) {
            best_depth = depth;
            best_type = POW;
            best_pos = i;
          }
          break;

        // could be log/ln
        //case 'l':
        //  break;

        // could be sqrt
        //case 's':
        //    break;

        // could be exp
        //case 'e':
        //  break;
      }

      if (depth > max_depth) {
        max_depth = depth;
      }
    }

    if (best_depth > max_depth) { // if there were no parentheses (important!)
      best_depth = 0;

    }
    if (operator_found) { // we must recurse
//System.out.println("Operator found: "+expression);

      // first, remove extraneous parentheses, which are going to confuse
      // the parser.
      if (best_depth > 0) {

        for (int i = 0; i < best_depth; i++) {
          expression = expression.trim();
          expression = expression.substring(1, expression.length() - 1);
          best_pos--;
        }

      }

      int offset = 1;
      if (best_type == AND || best_type == OR) {
        offset = 2;

        // now we recurse
      }
      return new OperationNode(
          best_type,
          parse(expression.substring(0, best_pos).trim()),
          parse(expression.substring(best_pos + offset, expression.length()).
                trim())
          );

    }

    else if(expression.indexOf(LOG) != -1 || expression.indexOf(EXP) != -1 ||
            expression.indexOf(SIN) != -1 || expression.indexOf(COS) != -1 ||
           expression.indexOf(TAN) != -1 || expression.indexOf(SQRT) != -1 ||
           expression.indexOf(NAT_LOG) != -1 || expression.indexOf(ABS) != -1) {
      return new ComplexNode(expression);
    }

    else { // this is a terminal

//System.out.println("This is a terminal. "+expression);
      // we still have to remove extraneous parentheses, but it's a
      // little different this time
      if (max_depth > 0) {

        for (int i = 0; i < max_depth; i++) {
          expression = expression.trim();
          expression = expression.substring(1, expression.length() - 1);
        }

      }
//System.out.println("Now here: "+expression);
      try {
        return new TerminalNode(1, 1, Float.parseFloat(expression));
      }
      catch (Exception e) {
        float tempmyfloat = 0;
        return new TerminalNode(0, getIndex(expression), tempmyfloat);
      }

    }

  }

  private int getIndex(String label) throws ExpressionException {

    StringBuffer buffer = new StringBuffer(label.trim());

    for (int i = 0; i < buffer.length(); i++) {
      if (buffer.charAt(i) == '\\') {
        buffer.deleteCharAt(i);
        i--;
      }
    }

    Integer I = (Integer) labelToIndex.get(buffer.toString());

    if (I == null) {
      throw new ExpressionException("ColumnExpression: column " +
                                    buffer.toString() + " not found.");
    }

    return I.intValue();

  }

      /******************************************************************************/
      /* The traversal tree for the column expression is composed of subexpressions */
      /* that can represent either operations or terminals (columns).               */
      /******************************************************************************/

  public static final int
      TYPE_BOOLEAN = 0,
      TYPE_BYTE = 1,
      TYPE_DOUBLE = 2,
      TYPE_FLOAT = 3,
      TYPE_INTEGER = 4,
      TYPE_LONG = 5,
      TYPE_SHORT = 6;

  private abstract class Node {

    protected int returnType = 0;
    public abstract String toString();

    public abstract Object evaluate() throws ExpressionException;

  }

  private static final String LOG = "log";
  private static final String EXP = "exp";
  private static final String NAT_LOG = "ln";
  private static final String ABS = "abs";
  private static final String SIN = "sin";
  private static final String ASIN = "asin";
  private static final String COS = "cos";
  private static final String ACOS = "acos";
  private static final String TAN = "tan";
  private static final String ATAN = "atan";
  private static final String SQRT = "sqrt";

  private class ComplexNode extends Node {

    Node argument;
    String operation;

    ComplexNode(String expression) throws ExpressionException {
      returnType = TYPE_DOUBLE;

      expression = expression.trim();
      if(expression.startsWith(LOG)) {
        operation = LOG;
        String tmpExp = expression.substring(LOG.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else if(expression.startsWith(EXP)) {
        operation = EXP;
        String tmpExp = expression.substring(EXP.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else if(expression.startsWith(NAT_LOG)) {
        operation = NAT_LOG;
        String tmpExp = expression.substring(NAT_LOG.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else if(expression.startsWith(ABS)) {
        operation = ABS;
        String tmpExp = expression.substring(ABS.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else if(expression.startsWith(SIN)) {
        operation = SIN;
        String tmpExp = expression.substring(SIN.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else if(expression.startsWith(ASIN)) {
        operation = ASIN;
        String tmpExp = expression.substring(ASIN.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else if(expression.startsWith(COS)) {
        operation = COS;
        String tmpExp = expression.substring(COS.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else if(expression.startsWith(ACOS)) {
        operation = ACOS;
        String tmpExp = expression.substring(ACOS.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else if(expression.startsWith(TAN)) {
        operation = TAN;
        String tmpExp = expression.substring(TAN.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else if(expression.startsWith(ATAN)) {
        operation = ATAN;
        String tmpExp = expression.substring(ATAN.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else if(expression.startsWith(SQRT)) {
        operation = SQRT;
        String tmpExp = expression.substring(SQRT.length()+1, expression.length()-1);
        argument = parse(tmpExp);
      }
      else
        throw new ExpressionException("ComplexNode");
    }

    public String toString() {
      return "";
    }

    public Object evaluate() throws ExpressionException {
      double[] retVal = new double[table.getNumRows()];
      double[] arg;
      switch(argument.returnType) {
        case TYPE_BOOLEAN:
          throw new ExpressionException("NOPE");
        case TYPE_DOUBLE:
          arg = (double[])argument.evaluate();
          break;
        case TYPE_FLOAT:
          float[] ar = (float[])argument.evaluate();
          arg = new double[ar.length];
          for(int i = 0; i < ar.length; i++)
            arg[i] = ar[i];
          break;
        case TYPE_LONG:
          long[] l = (long[])argument.evaluate();
          arg = new double[l.length];
          for(int i = 0; i < l.length; i++)
            arg[i] = l[i];
          break;
        case TYPE_BYTE:
          byte[] b = (byte[])argument.evaluate();
          arg = new double[b.length];
          for(int i = 0; i < b.length; i++)
            arg[i] = b[i];
           break;
        case TYPE_INTEGER:
          int[] ia = (int[])argument.evaluate();
          arg = new double[ia.length];
          for(int i = 0; i < ia.length; i++)
            arg[i] = ia[i];
          break;
        case TYPE_SHORT:
          short[] s = (short[])argument.evaluate();
          arg = new double[s.length];
          for(int i = 0; i < s.length; i++)
            arg[i] = s[i];
          break;
        default:
          throw new ExpressionException("Nope2");
      }

      if(operation.equals(LOG)) {
        for(int i = 0; i < arg.length; i++) {
          retVal[i] = Math.log(arg[i])/Math.log(10);
        }
      }
      else if(operation.equals(SIN)) {
        for(int i = 0; i < arg.length; i++) {
          retVal[i] = Math.sin(arg[i]);
        }
      }
      else if(operation.equals(COS)) {
        for(int i = 0; i < arg.length; i++) {
          retVal[i] = Math.cos(arg[i]);
        }
      }
      else if(operation.equals(TAN)) {
        for(int i = 0; i < arg.length; i++) {
          retVal[i] = Math.tan(arg[i]);
        }
      }
      else if(operation.equals(SQRT)) {
        for(int i = 0; i < arg.length; i++) {
          retVal[i] = Math.sqrt(arg[i]);
        }
      }
      else if(operation.equals(ABS)) {
        for(int i = 0; i < arg.length; i++) {
          retVal[i] = Math.abs(arg[i]);
        }
      }
      else if(operation.equals(EXP)) {
        for(int i = 0; i < arg.length; i++) {
          retVal[i] = Math.exp(arg[i]);
        }
      }
      else if(operation.equals(ASIN)) {
        for(int i = 0; i < arg.length; i++) {
          retVal[i] = Math.asin(arg[i]);
        }
      }
      else if(operation.equals(ACOS)) {
        for(int i = 0; i < arg.length; i++) {
          retVal[i] = Math.acos(arg[i]);
        }
      }
      else if(operation.equals(ATAN)) {
        for(int i = 0; i < arg.length; i++) {
          retVal[i] = Math.atan(arg[i]);
        }
      }

      return retVal;
    }
  }

  private class OperationNode
      extends Node {

    protected int type;
    private Node left, right;

    public OperationNode(int type, Node left, Node right) throws
        ExpressionException {
      this.type = type;
      this.left = left;
      this.right = right;

      switch (left.returnType) {

        case TYPE_BOOLEAN:

          if (type != AND && type != OR) {
            throw new ExpressionException(
                "ColumnExpression: Illegal expression.");
          }

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              returnType = TYPE_BOOLEAN;
              break;
            default:
              throw new ExpressionException(
                  "No operator can combine a numeric and a boolean column.");

          }
          break;

        case TYPE_BYTE:

          if (type == AND || type == OR) {
            throw new ExpressionException(
                "ColumnExpression: illegal expression.");
          }

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "No operator can combine a numeric and a boolean column.");
            case TYPE_BYTE:
              returnType = TYPE_BYTE;
              break;
            case TYPE_DOUBLE:
              returnType = TYPE_DOUBLE;
              break;
            case TYPE_FLOAT:
              returnType = TYPE_FLOAT;
              break;
            case TYPE_INTEGER:
              returnType = TYPE_INTEGER;
              break;
            case TYPE_LONG:
              returnType = TYPE_LONG;
              break;
            case TYPE_SHORT:
              returnType = TYPE_SHORT;
              break;

          }
          break;

        case TYPE_DOUBLE:

          if (type == AND || type == OR) {
            throw new ExpressionException(
                "ColumnExpression: illegal expression.");
          }

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "No operator can combine a numeric and a boolean column.");
            case TYPE_BYTE:
            case TYPE_DOUBLE:
            case TYPE_FLOAT:
            case TYPE_INTEGER:
            case TYPE_LONG:
            case TYPE_SHORT:
              returnType = TYPE_DOUBLE;
              break;

          }
          break;

        case TYPE_FLOAT:

          if (type == AND || type == OR) {
            throw new ExpressionException(
                "ColumnExpression: illegal expression.");
          }

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "No operator can combine a numeric and a boolean column.");
            case TYPE_BYTE:
            case TYPE_FLOAT:
            case TYPE_INTEGER:
            case TYPE_SHORT:
              returnType = TYPE_FLOAT;
              break;
            case TYPE_DOUBLE:
            case TYPE_LONG:
              returnType = TYPE_DOUBLE;
              break;

          }
          break;

        case TYPE_INTEGER:

          if (type == AND || type == OR) {
            throw new ExpressionException(
                "ColumnExpression: illegal expression.");
          }

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "No operator can combine a numeric and a boolean column.");
            case TYPE_BYTE:
            case TYPE_INTEGER:
            case TYPE_SHORT:
              returnType = TYPE_INTEGER;
              break;
            case TYPE_FLOAT:
              returnType = TYPE_FLOAT;
              break;
            case TYPE_DOUBLE:
              returnType = TYPE_DOUBLE;
              break;
            case TYPE_LONG:
              returnType = TYPE_LONG;
              break;

          }
          break;

        case TYPE_LONG:

          if (type == AND || type == OR) {
            throw new ExpressionException(
                "ColumnExpression: illegal expression.");
          }

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "No operator can combine a numeric and a boolean column.");
            case TYPE_BYTE:
            case TYPE_INTEGER:
            case TYPE_LONG:
            case TYPE_SHORT:
              returnType = TYPE_LONG;
              break;
            case TYPE_FLOAT:
            case TYPE_DOUBLE:
              returnType = TYPE_DOUBLE;
              break;

          }
          break;

        case TYPE_SHORT:

          if (type == AND || type == OR) {
            throw new ExpressionException(
                "ColumnExpression: illegal expression.");
          }

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "No operator can combine a numeric and a boolean column.");
            case TYPE_BYTE:
            case TYPE_SHORT:
              returnType = TYPE_SHORT;
              break;
            case TYPE_INTEGER:
              returnType = TYPE_INTEGER;
              break;
            case TYPE_LONG:
              returnType = TYPE_LONG;
              break;
            case TYPE_FLOAT:
              returnType = TYPE_FLOAT;
              break;
            case TYPE_DOUBLE:
              returnType = TYPE_DOUBLE;
              break;

          }
          break;

      }

    }

    public String toString() {

      StringBuffer buffer = new StringBuffer();

      if (left instanceof OperationNode) {
        buffer.append('(');
        buffer.append(left);
        buffer.append(')');
      }
      else {
        buffer.append(left);
      }

      buffer.append(' ');
      switch (type) {
        case ADDITION:
          buffer.append('+');
          break;
        case SUBTRACTION:
          buffer.append('-');
          break;
        case MULTIPLICATION:
          buffer.append('*');
          break;
        case DIVISION:
          buffer.append('/');
          break;
        case MODULUS:
          buffer.append('%');
          break;
        case AND:
          buffer.append("&&");
          break;
        case OR:
          buffer.append("||");
          break;
        case POW:
          buffer.append("^");
          break;
      }
      buffer.append(' ');

      if (right instanceof OperationNode) {
        buffer.append('(');
        buffer.append(right);
        buffer.append(')');
      }
      else {
        buffer.append(right);
      }

      return buffer.toString();

    }

////////////////////////////////////////////////////////////////////////////////
// The big one. Operation evaluation is the main chunk of this class.         //
////////////////////////////////////////////////////////////////////////////////

    public Object evaluate() throws ExpressionException {

      int numRows = table.getNumRows();
      //Object[] output = new Object[numRows];

      switch (left.returnType) {

        case TYPE_BOOLEAN:

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              boolean[] b = new boolean[table.getNumRows()];
              boolean[] bL = (boolean[]) left.evaluate();
              boolean[] bR = (boolean[]) right.evaluate();
              switch (type) {
                case AND:
                  for (int i = 0; i < b.length; i++) {
                    b[i] = bL[i] && bR[i];
                  }
                  break;
                case OR:
                  for (int i = 0; i < b.length; i++) {
                    b[i] = bL[i] || bR[i];
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) b;
            default:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");

          }

        case TYPE_BYTE:

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");
            case TYPE_BYTE:
              byte[] b = new byte[table.getNumRows()];
              byte[] bL = (byte[]) left.evaluate();
              byte[] bR = (byte[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < b.length; i++) {
                    b[i] = (byte) (bL[i] + bR[i]);
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < b.length; i++) {
                    b[i] = (byte) (bL[i] - bR[i]);
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < b.length; i++) {
                    b[i] = (byte) (bL[i] * bR[i]);
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < b.length; i++) {
                    b[i] = (byte) (bL[i] / bR[i]);
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < b.length; i++) {
                    b[i] = (byte) (bL[i] % bR[i]);
                  }
                  break;
                case POW:
                  for(int i = 0; i < b.length; i++) {
                    b[i] = (byte) Math.pow(bL[i], bR[i]);
                  }
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) b;
            case TYPE_DOUBLE:
              double[] d = new double[table.getNumRows()];
              bL = (byte[]) left.evaluate();
              double[] dR = (double[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) bL[i] + dR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) bL[i] - dR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) bL[i] * dR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) bL[i] / dR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) bL[i] % dR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = Math.pow(bL[i], dR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) d;
            case TYPE_FLOAT:
              float[] f = new float[table.getNumRows()];
              bL = (byte[]) left.evaluate();
              float[] fR = (float[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) bL[i] + fR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) bL[i] - fR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) bL[i] * fR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) bL[i] / fR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) bL[i] % fR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float)Math.pow(bL[i], fR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) f;
            case TYPE_INTEGER:
              int[] I = new int[table.getNumRows()];
              bL = (byte[]) left.evaluate();
              int[] iR = (int[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) bL[i] + iR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) bL[i] - iR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) bL[i] * iR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) bL[i] / iR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) bL[i] % iR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int)Math.pow(bL[i], iR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) I;
            case TYPE_LONG:
              long[] l = new long[table.getNumRows()];
              bL = (byte[]) left.evaluate();
              long[] lR = (long[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) bL[i] + lR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) bL[i] - lR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) bL[i] * lR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) bL[i] / lR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) bL[i] % lR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long)Math.pow(bL[i], lR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) l;
            case TYPE_SHORT:
              short[] s = new short[table.getNumRows()];
              bL = (byte[]) left.evaluate();
              short[] sR = (short[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) ( (short) bL[i] + sR[i]);
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) ( (short) bL[i] - sR[i]);
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) ( (short) bL[i] * sR[i]);
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) ( (short) bL[i] / sR[i]);
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) ( (short) bL[i] % sR[i]);
                  }
                  break;
                case POW:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) Math.pow(bL[i], sR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) s;
            default:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");

          }

        case TYPE_DOUBLE:

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");
            case TYPE_BYTE:
              double[] d = new double[table.getNumRows()];
              double[] dL = (double[]) left.evaluate();
              byte[] bR = (byte[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] + (double) bR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] - (double) bR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] * (double) bR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] / (double) bR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] % (double) bR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = Math.pow(dL[i], bR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal Expression.");
              }
              return (Object) d;
            case TYPE_DOUBLE:
              d = new double[table.getNumRows()];
              dL = (double[]) left.evaluate();
              double[] dR = (double[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] + dR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] - dR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] * dR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] / dR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] % dR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = Math.pow(dL[i], dR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) d;
            case TYPE_FLOAT:
              d = new double[table.getNumRows()];
              dL = (double[]) left.evaluate();
              float[] fR = (float[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] + (double) fR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] - (double) fR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] * (double) fR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] / (double) fR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] % (double) fR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = Math.pow(dL[i], fR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) d;
            case TYPE_INTEGER:
              d = new double[table.getNumRows()];
              dL = (double[]) left.evaluate();
              int[] iR = (int[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] + (double) iR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] - (double) iR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] * (double) iR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] / (double) iR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] % (double) iR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = Math.pow(dL[i], iR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) d;
            case TYPE_LONG:
              d = new double[table.getNumRows()];
              dL = (double[]) left.evaluate();
              long[] lR = (long[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] + (double) lR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] - (double) lR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] * (double) lR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] / (double) lR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] % (double) lR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = Math.pow(dL[i], lR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) d;
            case TYPE_SHORT:
              d = new double[table.getNumRows()];
              dL = (double[]) left.evaluate();
              short[] sR = (short[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] + (double) sR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] - (double) sR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] * (double) sR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] / (double) sR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = dL[i] % (double) sR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = Math.pow(dL[i], sR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) d;
            default:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");

          }

        case TYPE_FLOAT:

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");
            case TYPE_BYTE:
              float[] f = new float[table.getNumRows()];
              float[] fL = (float[]) left.evaluate();
              byte[] bR = (byte[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] + (float) bR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] - (float) bR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] * (float) bR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] / (float) bR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] % (float) bR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float)Math.pow(fL[i], bR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal Expression.");
              }
              return (Object) f;
            case TYPE_DOUBLE:
              double[] d = new double[table.getNumRows()];
              fL = (float[]) left.evaluate();
              double[] dR = (double[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) fL[i] + dR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) fL[i] - dR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) fL[i] * dR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) fL[i] / dR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) fL[i] % dR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = Math.pow(fL[i], dR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) d;
            case TYPE_FLOAT:
              f = new float[table.getNumRows()];
              fL = (float[]) left.evaluate();
              float[] fR = (float[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] + fR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] - fR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] * fR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] / fR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] % fR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float)Math.pow(fL[i], fR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) f;
            case TYPE_INTEGER:
              f = new float[table.getNumRows()];
              fL = (float[]) left.evaluate();
              int[] iR = (int[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] + (float) iR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] - (float) iR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] * (float) iR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] / (float) iR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] % (float) iR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float)Math.pow(fL[i], iR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) f;
            case TYPE_LONG:
              d = new double[table.getNumRows()];
              fL = (float[]) left.evaluate();
              long[] lR = (long[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) fL[i] + (double) lR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) fL[i] - (double) lR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) fL[i] * (double) lR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) fL[i] / (double) lR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) fL[i] % (double) lR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = Math.pow(fL[i], lR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) d;
            case TYPE_SHORT:
              f = new float[table.getNumRows()];
              fL = (float[]) left.evaluate();
              short[] sR = (short[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] + (float) sR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] - (float) sR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] * (float) sR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] / (float) sR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = fL[i] % (float) sR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float)Math.pow(fL[i], sR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) f;
            default:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");

          }

        case TYPE_INTEGER:

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");
            case TYPE_BYTE:
              int[] I = new int[table.getNumRows()];
              int[] iL = (int[]) left.evaluate();
              byte[] bR = (byte[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] + (int) bR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] - (int) bR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] * (int) bR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] / (int) bR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] % (int) bR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int)Math.pow(iL[i], bR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal Expression.");
              }
              return (Object) I;
            case TYPE_DOUBLE:
              double[] d = new double[table.getNumRows()];
              iL = (int[]) left.evaluate();
              double[] dR = (double[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) iL[i] + dR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) iL[i] - dR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) iL[i] * dR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) iL[i] / dR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) iL[i] % dR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = Math.pow(iL[i], dR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) d;
            case TYPE_FLOAT:
              float[] f = new float[table.getNumRows()];
              iL = (int[]) left.evaluate();
              float[] fR = (float[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) iL[i] + fR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) iL[i] - fR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) iL[i] * fR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) iL[i] / fR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) iL[i] % fR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float)Math.pow(iL[i], fR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) f;
            case TYPE_INTEGER:
              I = new int[table.getNumRows()];
              iL = (int[]) left.evaluate();
              int[] iR = (int[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] + iR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] - iR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] * iR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] / iR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] % iR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int)Math.pow(iL[i], iR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) I;
            case TYPE_LONG:
              long[] l = new long[table.getNumRows()];
              iL = (int[]) left.evaluate();
              long[] lR = (long[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) iL[i] + lR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) iL[i] - lR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) iL[i] * lR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) iL[i] / lR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) iL[i] % lR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long)Math.pow(iL[i], lR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) l;
            case TYPE_SHORT:
              I = new int[table.getNumRows()];
              iL = (int[]) left.evaluate();
              short[] sR = (short[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] + (int) sR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] - (int) sR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] * (int) sR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] / (int) sR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = iL[i] % (int) sR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int)Math.pow(iL[i], sR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) I;
            default:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");

          }

        case TYPE_SHORT:

          switch (right.returnType) {

            case TYPE_BOOLEAN:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");
            case TYPE_BYTE:
              short[] s = new short[table.getNumRows()];
              short[] sL = (short[]) left.evaluate();
              byte[] bR = (byte[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) (sL[i] + (short) bR[i]);
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) (sL[i] - (short) bR[i]);
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) (sL[i] * (short) bR[i]);
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) (sL[i] / (short) bR[i]);
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) (sL[i] % (short) bR[i]);
                  }
                  break;
                case POW:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) Math.pow(sL[i], bR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) s;
            case TYPE_DOUBLE:
              double[] d = new double[table.getNumRows()];
              sL = (short[]) left.evaluate();
              double[] dR = (double[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) sL[i] + dR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) sL[i] - dR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) sL[i] * dR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) sL[i] / dR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) sL[i] % dR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < d.length; i++) {
                    d[i] = (double) Math.pow(sL[i], dR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) d;
            case TYPE_FLOAT:
              float[] f = new float[table.getNumRows()];
              sL = (short[]) left.evaluate();
              float[] fR = (float[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) sL[i] + fR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) sL[i] - fR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) sL[i] * fR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) sL[i] / fR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) sL[i] % fR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < f.length; i++) {
                    f[i] = (float) Math.pow(sL[i], fR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) f;
            case TYPE_INTEGER:
              int[] I = new int[table.getNumRows()];
              sL = (short[]) left.evaluate();
              int[] iR = (int[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) sL[i] + iR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) sL[i] - iR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) sL[i] * iR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) sL[i] / iR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) sL[i] % iR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < I.length; i++) {
                    I[i] = (int) Math.pow(sL[i], iR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) I;
            case TYPE_LONG:
              long[] l = new long[table.getNumRows()];
              sL = (short[]) left.evaluate();
              long[] lR = (long[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) sL[i] + lR[i];
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) sL[i] - lR[i];
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) sL[i] * lR[i];
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) sL[i] / lR[i];
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) sL[i] % lR[i];
                  }
                  break;
                case POW:
                  for (int i = 0; i < l.length; i++) {
                    l[i] = (long) Math.pow(sL[i], lR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) l;
            case TYPE_SHORT:
              s = new short[table.getNumRows()];
              sL = (short[]) left.evaluate();
              short[] sR = (short[]) right.evaluate();
              switch (type) {
                case ADDITION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) (sL[i] + sR[i]);
                  }
                  break;
                case SUBTRACTION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) (sL[i] - sR[i]);
                  }
                  break;
                case MULTIPLICATION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) (sL[i] * sR[i]);
                  }
                  break;
                case DIVISION:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) (sL[i] / sR[i]);
                  }
                  break;
                case MODULUS:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) (sL[i] % sR[i]);
                  }
                  break;
                case POW:
                  for (int i = 0; i < s.length; i++) {
                    s[i] = (short) Math.pow(sL[i], sR[i]);
                  }
                  break;
                default:
                  throw new ExpressionException(
                      "ColumnExpression: Illegal expression.");
              }
              return (Object) s;
            default:
              throw new ExpressionException(
                  "ColumnExpression: Illegal expression.");

          }

      }

      throw new ExpressionException(
          "ColumnExpression: apparently malformed expression.");
      // return null;

    }

////////////////////////////////////////////////////////////////////////////////
// end operation evaluation                                                   //
////////////////////////////////////////////////////////////////////////////////

  }

  private class TerminalNode
      extends Node {

    private int column;
    int myownflag;
    float myownscalarvalue;
    public TerminalNode(int myownflag, int column, float myownscalarvalue) throws
        ExpressionException {

      this.myownflag = myownflag;
      if (myownflag == 0) {
        this.column = column;

        if (column < table.getNumColumns()) { // in table

          switch (table.getColumnType(column)) {
            case ColumnTypes.BOOLEAN:
              returnType = TYPE_BOOLEAN;
              break;
            case ColumnTypes.BYTE:
              returnType = TYPE_BYTE;
              break;
            case ColumnTypes.DOUBLE:
              returnType = TYPE_DOUBLE;
              break;
            case ColumnTypes.FLOAT:
              returnType = TYPE_FLOAT;
              break;
            case ColumnTypes.INTEGER:
              returnType = TYPE_INTEGER;
              break;
            case ColumnTypes.LONG:
              returnType = TYPE_LONG;
              break;
            case ColumnTypes.SHORT:
              returnType = TYPE_SHORT;
              break;
            default:
              throw new ExpressionException
                  (
                  "ColumnExpression supports only numeric and boolean columns.");
          }

        }
        else { // in array passed to setLazyExpression

          returnType = ( (Integer) extraColumnIndexToType.get(new Integer(
              column))).intValue();

        }
      }
      else {
        returnType = TYPE_FLOAT;
        this.myownscalarvalue = myownscalarvalue;
      }
    }

    public String toString() {
      if (myownflag == 0) {
        if (column >= table.getNumColumns()) {
          return "NEW";
        }

        return table.getColumnLabel(column);
      }
      else {
        return ( (String) Float.toString(myownscalarvalue));
      }
    }

    public Object evaluate() throws ExpressionException {
      if (myownflag == 0) {
        switch (returnType) {

          case TYPE_BOOLEAN:
            boolean[] b = new boolean[table.getNumRows()];
            for (int i = 0; i < b.length; i++) {
              b[i] = table.getBoolean(i, column);
            }
            return (Object) b;
          case TYPE_BYTE:
            byte[] bb = new byte[table.getNumRows()];
            for (int i = 0; i < bb.length; i++) {
              bb[i] = table.getByte(i, column);
            }
            return (Object) bb;
          case TYPE_DOUBLE:
            double[] d = new double[table.getNumRows()];
            for (int i = 0; i < d.length; i++) {
              d[i] = table.getDouble(i, column);
            }
            return (Object) d;
          case TYPE_FLOAT:
            float[] f = new float[table.getNumRows()];
            for (int i = 0; i < f.length; i++) {
              f[i] = table.getFloat(i, column);
            }
            return (Object) f;
          case TYPE_INTEGER:
            int[] I = new int[table.getNumRows()];
            for (int i = 0; i < I.length; i++) {
              I[i] = table.getInt(i, column);
            }
            return (Object) I;
          case TYPE_LONG:
            long[] l = new long[table.getNumRows()];
            for (int i = 0; i < l.length; i++) {
              l[i] = table.getLong(i, column);
            }
            return (Object) l;
          case TYPE_SHORT:
            short[] s = new short[table.getNumRows()];
            for (int i = 0; i < s.length; i++) {
              s[i] = table.getShort(i, column);
            }
            return (Object) s;
          default:
            throw new ExpressionException
                (
                "There has been an error in ColumnExpression. Double-check your expression.");

        }

      }
      else {
        float[] myf = new float[table.getNumRows()];
        for (int i = 0; i < myf.length; i++) {
          myf[i] = myownscalarvalue;
        }
        return (Object) myf;
      }
    }
  }

      /******************************************************************************/
      /* Evaluation traverses the tree recursively starting at the root.            */
      /******************************************************************************/

  /**
   * Returns a formatted representation of the expression represented by
   * this object.
   *
   * @return                the formatted expression, including column labels
   */
  public String toString() {
    return root.toString();
  }

  /**
   * Evaluates the type of column that should be returned by this expression.
   *
   * @return                column type of the form
   *                        <code>ColumnExpression.TYPE_???</code>
   */
  public int evaluateType() {
    return root.returnType;
  }

}
